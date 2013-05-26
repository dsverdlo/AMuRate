package com.dsverdlo.AMuRate.gui;

import org.json.JSONException;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.AMuRate;
import com.dsverdlo.AMuRate.objects.Track;
import com.dsverdlo.AMuRate.services.ExternalDatabaseConnect;
import com.dsverdlo.AMuRate.services.DatabaseSyncer;
import com.dsverdlo.AMuRate.services.InternalDatabaseHistoryAdapter;
import com.dsverdlo.AMuRate.services.HttpConnect;
import com.dsverdlo.AMuRate.services.InternalDatabaseRatingAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Display a selected song/track on the screen. 
 * 
 * @author David Sverdlov
 *
 */

public class TrackActivity extends Activity {
	private AMuRate amr;
	
	private TrackActivity trackActivity;
	private final Track track = new Track();
	final String ratingBarInfoString = "Average: %.1f (based on %d reviews)";

	private int isExternalDatabaseAvailable; // unchecked, no, yes
	private AnimationView loading_image;
	private Button back;
	private Button album;
	private Button artist;
	private ImageButton youtube;
	private ImageView image;
	private RatingBar ratingBar;
	private TextView ratingBarInfo2;
	private TextView title;
	private TextView ratingBarInfo;

	private InternalDatabaseRatingAdapter ra;
	private InternalDatabaseHistoryAdapter ha;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_activity);

		trackActivity = this;
		amr = (AMuRate) getApplicationContext();

		// Load the track 
		if(getIntent().hasExtra("track")) {
			track.loadFromInfo(getIntent().getStringExtra("track"));
		} else if(getIntent().hasExtra("jsontrack")) {
			JSONObject JSONtrack;
			try {
				JSONtrack = new JSONObject(getIntent().getStringExtra("jsontrack"));
				track.loadFromInfo(JSONtrack);
			} catch (JSONException e) {
				System.out.println("JSON Exception in TrackActivity(constructor)");
				e.printStackTrace();
			}
		}
		
		loading_image = (AnimationView) findViewById(R.id.track_loading_image);
		loading_image.setVisibility(View.VISIBLE);
		
		// DB communication
		ra = new InternalDatabaseRatingAdapter(this);

		// External DB communication
		isExternalDatabaseAvailable = -1; // unchecked

		// Save in history
		ha = new InternalDatabaseHistoryAdapter(this);
		ha.addHistoryTrack(track.getMBID(), track.getArtist(), track.getTitle());

		title = (TextView) findViewById(R.id.track_title);
		artist = (Button) findViewById(R.id.track_button_artist);	
		image = (ImageView) findViewById(R.id.track_albumimage);
		image.setVisibility(View.GONE);
		album = (Button) findViewById(R.id.track_butt_on_album);	

		back = (Button) findViewById(R.id.track_newsearch);
		back.setText("New search"); 

		ratingBar = (RatingBar) findViewById(R.id.track_ratingBar);
		ratingBar.setNumStars(5); // TODO: remove here
		ratingBar.setStepSize((float) 0.5);

		ratingBarInfo = (TextView) findViewById(R.id.track_ratingbarinfo); 		
		ratingBarInfo2 = (TextView) findViewById(R.id.track_ratingbarinfo2);

		youtube = (ImageButton) findViewById(R.id.track_youtube);

		youtube.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				PackageManager pm = amr.getPackageManager();
				try {
					// Semi check to see if youtube app is installed:
					pm.getPackageInfo("com.google.android.youtube", PackageManager.GET_ACTIVITIES);

					Intent intent = new Intent(Intent.ACTION_SEARCH);
					intent.setPackage("com.google.android.youtube");
					intent.putExtra("query", track.getTitle() +" - "+track.getArtist());
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				catch (Exception ex){
					// if not installed it will raise exception so we show a Toast
					Toast.makeText(amr, "Youtube app not found...", Toast.LENGTH_SHORT).show();
				}
			}
		});


		back.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
				Intent mainIntent = new Intent(amr, MainActivity.class);
				mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainIntent);
			}
		});

		// Set the title as: Title (mm:ss)
		String titleString = track.getTitle();
		if(!track.getDuration().equals(":")) titleString = titleString + "  (" + track.getDuration() + ")";
		
		title.setText(titleString);

		artist.setText(track.getArtist());
		artist.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String artistMBID = track.getArtistMBID();
				if(artistMBID.length()>0) {
					HttpConnect conn = new HttpConnect();
					conn.getFromArtistMBID(trackActivity, artistMBID);
				} else {
					Toast.makeText(amr, "This artist did not have an ID.", Toast.LENGTH_SHORT).show();
					
				
				}
			}
		});
		
		// Check if an image can be displayed. If there is no large, try a medium.
		String imageUrl = track.getImage("l");
		if(imageUrl.length() > 0) {
			HttpConnect conn = new HttpConnect();
			conn.loadImage(imageUrl, image, loading_image);
		} else {
			// Else try a medium picture
			String OtherImageUrl = track.getImage("m");
			if(OtherImageUrl.length() > 0) {
				HttpConnect conn = new HttpConnect();
				conn.loadImage(OtherImageUrl, image, loading_image);
			} else image.setImageResource(R.drawable.not_available);
		}

		// Set the text From which album. If there is an album mbid, 
		// then an album activity can be created with a click.
		if(track.getAlbumMBID().length() > 0) {
			album.setText(track.getAlbumTitle());

			album.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					//album.setTextColor(Color.DKGRAY);
					System.out.println("You click on the album");
					HttpConnect conn = new HttpConnect();
					conn.getFromAlbum(trackActivity, track.getAlbumMBID());
					//album.setTextColor(Color.CYAN);
				}
			});
		} 

		ratingBarInfo.setText("Loading ratings...");
		ratingBarInfo2.setText("Checking if rated before");

		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				switch(isExternalDatabaseAvailable) {
				case -1: //check hasnt registered yet, stall time until Asynctask is done
					Toast.makeText(amr, "Testing connection, please try again", Toast.LENGTH_SHORT).show();
					break;
				case 1: sendToExternalDatabase(); 
				Toast.makeText(amr, "Sending to ext DB", Toast.LENGTH_SHORT).show();
				ratingBar.setClickable(false);
				ratingBar.setEnabled(false);
				ratingBarInfo2.setText("You rated this track: "+rating);
				ratingBarInfo2.setVisibility(View.VISIBLE);
				break;
				case 0: sendToInternalDatabase(); 
				Toast.makeText(amr, "Sending to int DB", Toast.LENGTH_SHORT).show();		
				ratingBar.setClickable(false);
				ratingBar.setEnabled(false);
				ratingBarInfo2.setText("You rated this track: "+rating);
				ratingBarInfo2.setVisibility(View.VISIBLE);
				ratingBarInfo.setVisibility(View.GONE);
				break;
				default: // shouldnt get here
					System.out.println("Something went wrong! isAvailable?" + isExternalDatabaseAvailable);
					
				}
			}
		});
		
		if(track.getMBID().length() == 0) {
			ratingBar.setEnabled(false);
			ratingBar.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Toast.makeText(amr, "This track does not have an mbid associated with it", Toast.LENGTH_SHORT).show();					
				}
			});
		}
		
		title.setSelected(true);
		
		// Check if ext database is connectable
		new ExternalDatabaseConnect(trackActivity, amr.getIp()).execute(""+ExternalDatabaseConnect.ISCONNECTED);
		
		// Try syncing aswell
		new DatabaseSyncer(amr, amr.getIp()).execute();
	}


	

	public void onDoneGettingExternal(Double result) {
		// result is still two values in one TODO: split
		Double avg = result % 10;
		int amt = (int) ((result - avg) / 10);
		
		System.out.println("DBDB__" + "onDoneGettingExternal");
		
		// Store listener, then nullify it because we are going to change
		// the value of the bar, but don't want it to go post it
		OnRatingBarChangeListener orbcl = ratingBar.getOnRatingBarChangeListener();
		ratingBar.setOnRatingBarChangeListener(null);
		ratingBar.setRating((float) (Math.ceil(avg * 2) / 2.0));
		ratingBar.setOnRatingBarChangeListener(orbcl);

		ratingBarInfo.setText(String.format(ratingBarInfoString, avg, amt));

	}
 
	public void onDoneSendingExternal(Double result) {
		System.out.println("DBDB__" + "onDoneSendingExternal");

		if(result > 0) { // Sent succesfully
			
			Toast.makeText(amr, "Rating succesfully sent to remote db.", Toast.LENGTH_SHORT).show();

			// get new avg
			new ExternalDatabaseConnect(trackActivity, amr.getIp()).execute(""+ExternalDatabaseConnect.GETRATING, track.getMBID());
		} else {
			// Sending did not succeed so send it unsynced to int database
			Toast.makeText(amr, "Rating sending to remote db failed.", Toast.LENGTH_SHORT).show();
			
			sendToInternalDatabase(); 
			
			// Also retest connection
			Toast.makeText(amr, "Retesting connection...", Toast.LENGTH_SHORT).show();
			new ExternalDatabaseConnect(trackActivity, amr.getIp()).execute(""+ExternalDatabaseConnect.ISCONNECTED);
		}
	}

	public void onDoneTestingExternalConnection(Double result) {
		System.out.println("DBDB__" + "onDoneTestingExternalConnection");
		
		isExternalDatabaseAvailable = (result > 0) ? 1 : 0; 

		// and now we know this, we can get appropriate readings
		if(isExternalDatabaseAvailable > 0){
			// if external available, read from it
			new ExternalDatabaseConnect(trackActivity, amr.getIp()).execute(""+ExternalDatabaseConnect.GETRATING, track.getMBID());
			
			// and check if user has already rated this
			String user = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
			new ExternalDatabaseConnect(trackActivity, amr.getIp()).execute(""+ExternalDatabaseConnect.HASRATED, track.getMBID(), user);
			
			
		} else {
			// manually set it with info from local database
			// TODO: unsplit
			if(ra.hasRatedBefore(track.getMBID())){
				float rating = ra.readRating(track.getMBID());
				ratingBarInfo.setText("No connection with server for getting average rating.");
				ratingBarInfo2.setText("You have rated this track "+rating);
				// change listener swap trick
				OnRatingBarChangeListener orbcl = ratingBar.getOnRatingBarChangeListener();
				ratingBar.setOnRatingBarChangeListener(null);
				ratingBar.setRating(rating); 
				ratingBar.setOnRatingBarChangeListener(orbcl);
				
				ratingBar.setEnabled(false);
				ratingBar.setClickable(true);
			} else {
				ratingBarInfo.setText("No connection with server. If you rate now, the rating will be sent when a connection could be made.");
				ratingBarInfo2.setVisibility(View.GONE);
				ratingBar.setEnabled(true);
				ratingBar.setClickable(true);
			}
		}
	}

	private void sendToExternalDatabase() {
		System.out.println("DBDB__" + "sendToExternalDB");
		// Prepare params for AsyncTask
		int method = ExternalDatabaseConnect.SENDRATING;
		String mbid = track.getMBID();
		String artist = track.getArtist();
		String title = track.getTitle();
		float rating = ratingBar.getRating();
		int date = (int) (System.currentTimeMillis() / 1000L);

		String user = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		System.out.println("\nuserID; " + user);
		
		// Also keep a copy in the local database (synced)
		ra.addRating(track.getMBID(), track.getArtist(), track.getTitle(), rating, true);
		
		// Finally send to ext db
		new ExternalDatabaseConnect(trackActivity, amr.getIp()).execute(""+method, mbid, artist, title, ""+rating, ""+date, user);
		
		
	}

	private void sendToInternalDatabase() {

		System.out.println("DBDB__" + "sending unsynced ToInternalDatabase");
		
		// try to store it local in rating adapter
		float rating = ratingBar.getRating();
		long addResult = ra.addRating(track.getMBID(), track.getArtist(), track.getTitle(), rating, false);
		if(addResult < 0) {
			Toast.makeText(amr, "You already rated this", Toast.LENGTH_SHORT).show();
		} else {
			float avg = ra.readRating(track.getMBID());
			int amt = 1;//ra.readRating(track.getMBID());
			onDoneGettingExternal((double) avg + (amt * 10) );
			//int amt = ra.readRatingAmount(track.getMBID());
			//ratingBarInfo.setText(String.format(ratingBarInfoString, Math.ceil(avg * 2) / 2.0, amt));
			//Toast.makeText(amr, "Thank you for your rating!", Toast.LENGTH_SHORT).show();
			ratingBar.setClickable(false);
			ratingBar.setEnabled(false);
		}
	}


	
	public void onBackPressed() {
		   Log.d("CDA", "onBackPressed Called");
		   finish();
		}


	public void onDoneLoadingArtist(String artistInfo) {
		//TODO: get the try out of here
		try {
			JSONObject JSONartistInfo = new JSONObject(artistInfo);
			if(!JSONartistInfo.has("artist")) {
				// something went wrong.
				Toast.makeText(amr, "No info could be obtained..", Toast.LENGTH_LONG).show();
				return;
			}
			JSONObject JSONartist = JSONartistInfo.getJSONObject("artist");
			
			
		Intent artistIntent = new Intent(amr, ArtistActivity.class);
		artistIntent.putExtra("artist", JSONartist.toString());
		System.out.println("Starting ArtistActivity intent");
		startActivity(artistIntent);
		
		} catch (JSONException jsone) {
			System.err.println("JSONException in TrackActivity: onDoneLoadingArtist:\n"+jsone);
		}
	}

	public void onDoneLoadingAlbum(String album) {
		Intent albumIntent = new Intent(amr, AlbumActivity.class);
		albumIntent.putExtra("album", album);
		System.out.println("Starting AlbumActivity intent");

		startActivity(albumIntent);
	}

	public void onDoneCheckingHasRated(Double result) {
		if(result > 0) {
			ratingBarInfo2.setText("You rated this track: "+result);
			ratingBar.setEnabled(false);
			ratingBar.setClickable(false);
		} else {
			ratingBarInfo2.setText("You have not rated this track yet");
		}
	}
}
