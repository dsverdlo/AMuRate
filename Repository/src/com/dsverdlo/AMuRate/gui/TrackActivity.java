package com.dsverdlo.AMuRate.gui;

import org.json.JSONException;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.HistoryAdapter;
import com.dsverdlo.AMuRate.objects.RatingAdapter;
import com.dsverdlo.AMuRate.objects.Track;
import com.dsverdlo.AMuRate.services.AnimationView;
import com.dsverdlo.AMuRate.services.Client;
import com.dsverdlo.AMuRate.services.MyConnection;
import com.mysql.jdbc.Connection;

import android.app.Activity;
import android.content.Intent;
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
	private TextView streamText;
	private TextView title;
	private TextView ratingBarInfo;

	private RatingAdapter ra;
	private HistoryAdapter ha;
	private Client client;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_activity);

		trackActivity = this;

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
		ra = new RatingAdapter(this);

		// External DB communication
		isExternalDatabaseAvailable = -1; // unchecked
		client = new Client(this);
		//Toast.makeText(getApplicationContext(), "I can do things meanwhile", Toast.LENGTH_SHORT).show();

		// Save in history
		ha = new HistoryAdapter(this);
		ha.addHistoryTrack(track.getMBID(), track.getArtist(), track.getTitle());

		title = (TextView) findViewById(R.id.track_title);
		artist = (Button) findViewById(R.id.track_button_artist);	
		image = (ImageView) findViewById(R.id.track_albumimage);
		image.setVisibility(View.GONE);
		System.out.println("\nAreyounotentertained?\n"+findViewById(R.id.track_butt_on_album).toString());
		album = (Button) findViewById(R.id.track_butt_on_album);	

		back = (Button) findViewById(R.id.track_newsearch);
		back.setText("New search"); 

		ratingBar = (RatingBar) findViewById(R.id.track_ratingBar);
		//ratingBar.setPadding(5, 10, 0, 0);  
		ratingBar.setNumStars(5); // TODO: remove here
		ratingBar.setStepSize((float) 0.5);

		/*System.out.println("Starting reading rating avg");
		final float ratingBarRating = ra.readRatingAvg(track.getMBID());
		System.out.println("Finished reading rating avg: " + ratingBarRating);
		final int ratingBarAmount = ra.readRatingAmount(track.getMBID());
		System.out.println("Finished reading rating amount");
		*/ ratingBarInfo = (TextView) findViewById(R.id.track_ratingbarinfo); /*
		ratingBarInfo.setText(String.format(ratingBarInfoString, Math.ceil(ratingBarRating * 2) / 2.0, ratingBarAmount));
*/
		// Youtube link
		youtube = (ImageButton) findViewById(R.id.track_youtube);
		//LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//youtube.setLayoutParams(params);
		youtube.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
			 Intent intent = new Intent(Intent.ACTION_SEARCH);
			 intent.setPackage("com.google.android.youtube");
			 intent.putExtra("query", track.getTitle() +" - "+track.getArtist());
			 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 startActivity(intent);
			}
		});

		//streamText = (TextView) findViewById(R.id.track_stream);
		//streamText.setPadding(0, 50, 0, 0);

		back.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
				Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
				mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainIntent);
			}
		});

		// Set the title as: Title (mm:ss)
		String titleString = track.getTitle();
		if(!track.getDuration().equals(":")) titleString = titleString + "  (" + track.getDuration() + ")";
		
		title.setText(titleString);
		//title.setPadding(0, 10, 0, 20);

		artist.setText(track.getArtist());
		artist.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
//				Toast.makeText(getApplicationContext(), "You clicked on artist", Toast.LENGTH_SHORT).show();
				String artistMBID = track.getArtistMBID();
				if(artistMBID.length()>0) {
					MyConnection conn = new MyConnection();
					conn.getFromArtistMBID(trackActivity, artistMBID);
				} else {
					Toast.makeText(getApplicationContext(), "This artist did not have an ID.", Toast.LENGTH_SHORT).show();
					
				
				}
			}
		});
		
		// Check if an image can be displayed. If there is no large, try a medium.
		String imageUrl = track.getImage("l");
		if(imageUrl.length() > 0) {
			MyConnection conn = new MyConnection();
			conn.loadImage(imageUrl, image, loading_image);
		} else {
			// Else try a medium picture
			String OtherImageUrl = track.getImage("m");
			if(OtherImageUrl.length() > 0) {
				MyConnection conn = new MyConnection();
				conn.loadImage(OtherImageUrl, image, loading_image);
			} else image.setImageResource(R.drawable.not_available);
		}

		// Set the text From which album. If there is an album mbid, 
		// then an album activity can be created with a click.
		//album.setText("From album: ");
		if(track.getAlbumMBID().length() > 0) {
			album.setText(track.getAlbumTitle());

			album.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					//album.setTextColor(Color.DKGRAY);
					System.out.println("You click on the album");
					MyConnection conn = new MyConnection();
					conn.getFromAlbum(trackActivity, track.getAlbumMBID());
					//album.setTextColor(Color.CYAN);
				}
			});
			//album.setClickable(true);
		} else {
			//album.setText(album.getText() + track.getAlbumTitle());
			
		}

		ratingBarInfo.setText("Loading ratings...");
		// Initialize the ratingbar with the averaged result and an onClickListener
		// TODO: Add info text about how many votes
		//Client clien = new Client(Toast.makeText(getApplicationContext(), "Gotten MySQL rating!", Toast.LENGTH_SHORT));
		//client.execute("get", track.getMBID());
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				switch(isExternalDatabaseAvailable) {
				case -1: //check hasnt registered yet, stall time until Asynctask is done
					Toast.makeText(getApplicationContext(), "Testing connection, please try again", Toast.LENGTH_SHORT).show();
					break;
				case 1: sendToExternalDatabase(); 
				Toast.makeText(getApplicationContext(), "Sending to ext DB", Toast.LENGTH_SHORT).show();
				ratingBar.setClickable(false);
				ratingBar.setEnabled(false);
				break;
				case 0: sendToInternalDatabase(); 
				Toast.makeText(getApplicationContext(), "Sending to int DB", Toast.LENGTH_SHORT).show();		
				ratingBar.setClickable(false);
				ratingBar.setEnabled(false);
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
					Toast.makeText(getApplicationContext(), "This track does not have an mbid associated with it", Toast.LENGTH_SHORT).show();					
				}
			});
		}
		

		// Check if ext database is connectable
		client.execute(""+Client.ISCONNECTED);
		

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
 
	public void onDoneSendingExternal() {
		System.out.println("DBDB__" + "onDoneSendingExternal");
		// TODO Auto-generated method stub

		//Toast.makeText(getApplicationContext(), "Sending score: " + rating + " to database!", Toast.LENGTH_SHORT).show();

		// try sending it to remote database first
		Toast.makeText(getApplicationContext(), "Rating succesfully sent to remote db.", Toast.LENGTH_SHORT).show();

		// get new avg
		new Client(trackActivity).execute(""+Client.GETRATING, track.getMBID());
	}

	public void onDoneTestingExternalConnection(Double result) {
		System.out.println("DBDB__" + "onDoneTestingExternalConnection");
		
		isExternalDatabaseAvailable = (result > 0) ? 1 : 0; 

		// and now we know this, we can get appropriate readings
		if(isExternalDatabaseAvailable > 0){
			// if external available, read from it
			new Client(trackActivity).execute(""+Client.GETRATING, track.getMBID());
		} else {
			// manually set it with info from local database
//			onDoneGettingExternal((double) ra.readRatingAvg(track.getMBID()));
			// TODO: unsplit

			Double avg = (double) ra.readRatingAvg(track.getMBID());
			int amt = ra.readRatingAmount(track.getMBID());
			onDoneGettingExternal(avg + (amt*10));
		}
	}

	private void sendToExternalDatabase() {
		System.out.println("DBDB__" + "sendToExternalDB");
		// Prepare params for AsyncTask
		int method = Client.SENDRATING;
		String mbid = track.getMBID();
		String artist = track.getArtist();
		String title = track.getTitle();
		float rating = ratingBar.getRating();
		int date = (int) (System.currentTimeMillis() / 1000L);
		//TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		//TelephonyMgr.getSimSerialNumber(); // Requires READ_PHONE_STATE
		String user = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		System.out.println("\nuserID; " + user);

		new Client(trackActivity).execute(""+method, mbid, artist, title, ""+rating, ""+date, user);

	}

	private void sendToInternalDatabase() {

		System.out.println("DBDB__" + "sending unsynced ToInternalDatabase");
		//if(client.)
		// try to store it local in rating adapter
		float rating = ratingBar.getRating();
		long addResult = ra.addRating(track.getMBID(), track.getArtist(), track.getTitle(), rating, false);
		if(addResult < 0) {
			Toast.makeText(getApplicationContext(), "Error sending score.", Toast.LENGTH_SHORT).show();
		} else {
			float avg = ra.readRatingAvg(track.getMBID());
			int amt = ra.readRatingAmount(track.getMBID());
			onDoneGettingExternal((double) avg + (amt * 10) );
			//int amt = ra.readRatingAmount(track.getMBID());
			//ratingBarInfo.setText(String.format(ratingBarInfoString, Math.ceil(avg * 2) / 2.0, amt));
			//Toast.makeText(getApplicationContext(), "Thank you for your rating!", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(getApplicationContext(), "No info could be obtained..", Toast.LENGTH_LONG).show();
				return;
			}
			JSONObject JSONartist = JSONartistInfo.getJSONObject("artist");
			
			
		Intent artistIntent = new Intent(getApplicationContext(), ArtistActivity.class);
		artistIntent.putExtra("artist", JSONartist.toString());
		System.out.println("Starting ArtistActivity intent");
		startActivity(artistIntent);
		
		} catch (JSONException jsone) {
			System.err.println("JSONException in TrackActivity: onDoneLoadingArtist:\n"+jsone);
		}
	}

	public void onDoneLoadingAlbum(String album) {
		Intent albumIntent = new Intent(getApplicationContext(), AlbumActivity.class);
		albumIntent.putExtra("album", album);
		System.out.println("Starting AlbumActivity intent");
		//mPlayer.release();
		//mPlayer = null;
		startActivity(albumIntent);
	}

	
	
	}
