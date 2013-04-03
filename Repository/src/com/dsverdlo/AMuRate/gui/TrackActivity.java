package com.dsverdlo.AMuRate.gui;

import java.io.FileDescriptor;
import java.io.IOException;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.HistoryAdapter;
import com.dsverdlo.AMuRate.objects.RatingAdapter;
import com.dsverdlo.AMuRate.objects.Track;
import com.dsverdlo.AMuRate.services.MyConnection;
import com.dsverdlo.AMuRate.services.ServerManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TableRow;
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

	private TextView title;
	private TextView album;
	private ImageView image;
	private MyConnection connection;
	private TextView streamText;
	private RatingBar ratingBar;
	private TextView ratingBarInfo;
	private Button back;
	
	private Button togglePlayer;
	private MediaPlayer mPlayer;
	private boolean streaming;
	
	private RatingAdapter ra;
	private HistoryAdapter ha;
	private ServerManager sm; //TODO

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_activity);
		
		trackActivity = this;
		connection = new MyConnection();
		
		// Load the track 
		final Track track = new Track();
		track.loadFromInfo(getIntent().getStringExtra("track"));
		
		// DB communication
		ra = new RatingAdapter(this);
		
		// Server communication TODO: remove here?
		sm = new ServerManager();
		
		// Save in history
		ha = new HistoryAdapter(this);
		ha.addHistoryTrack(track.getMBID(), track.getArtist(), track.getTitle());

		title = (TextView) findViewById(R.id.track_title);
		album = (TextView) findViewById(R.id.track_album);
		image = (ImageView) findViewById(R.id.track_image);
		
		back = (Button) findViewById(R.id.track_back_button);
		back.setText("New search"); 
		
		ratingBar = (RatingBar) findViewById(R.id.track_ratingBar);
		ratingBar.setPadding(5, 10, 0, 0);  
		ratingBar.setNumStars(5); // TODO: remove here
		ratingBar.setStepSize((float) 0.5);

		System.out.println("Starting reading rating avg");
		final float ratingBarRating = ra.readRatingAvg(track.getMBID());
		System.out.println("Finished reading rating avg");
		final int ratingBarAmount = ra.readRatingAmount(track.getMBID());
		System.out.println("Finished reading rating amount");
		ratingBarInfo = (TextView) findViewById(R.id.track_ratingBar_info);
		final String ratingBarInfoString = "Average: %.1f (based on %d reviews)";
		ratingBarInfo.setText(String.format(ratingBarInfoString, Math.ceil(ratingBarRating * 2) / 2.0, ratingBarAmount));
 

		streamText = (TextView) findViewById(R.id.track_stream);
		//streamText.setPadding(0, 50, 0, 0);

		back.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				/*finish();
				Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(mainIntent);*/
				
				/*String all = ha.getSearchHistory(HistoryAdapter.SQL_GET_ALL);
				System.out.println(all);*/
				int amt = sm.getRatingAmt("23");
				float avg = sm.getRatingAvg("23");
				System.out.println("\nThe server avg is: " + avg + " on " + amt + " reviews");
			}
		});

		// Set the title as: Title - Artist (mm:ss)
		title.setText(track.getTitle() + " - " + track.getArtist() + "  (" + track.getDuration() + ")");
		title.setPadding(0, 10, 0, 20);

		// Check if an image can be displayed. If there is no large, try a medium.
		String imageUrl = track.getImage("l");
		if(imageUrl.length() > 0) {
			connection.loadImage(imageUrl, image);
		} else {
			// Else try a medium picture
			String OtherImageUrl = track.getImage("m");
			if(OtherImageUrl.length() > 0) {
				connection.loadImage(OtherImageUrl, image);
			} else image.setImageResource(R.drawable.not_available);
		}

		// Set the text From which album. If there is an album mbid, 
		// then an album activity can be created with a click.
		album.setText("From album: ");
		if(track.getAlbumMBID().length() > 0) {
			album.setTextColor(Color.CYAN);
			String combined = album.getText() + track.getAlbumTitle();
			SpannableString content = new SpannableString(combined);
			content.setSpan(new UnderlineSpan(), 0, combined.length(), 0);
			album.setText(content);
			
			album.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					System.out.println("You click on the album");
					MyConnection conn = new MyConnection();
					conn.getFromAlbum(trackActivity, track.getAlbumMBID());
				}
			});
		album.setClickable(true);
		} else {
			album.setText(album.getText() + track.getAlbumTitle());
		}

		// Initialize the ratingbar with the averaged result and an onClickListener
		// TODO: Add info text about how many votes
		ratingBar.setRating((float) (Math.ceil(ratingBarRating * 2) / 2.0));
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				Toast.makeText(getApplicationContext(), "Sending score: " + rating + " to database!", Toast.LENGTH_SHORT).show();
				long addResult = ra.addRating(track.getMBID(), track.getArtist(), track.getTitle(), rating);
				if(addResult < 0) {
					Toast.makeText(getApplicationContext(), "Error sending score.", Toast.LENGTH_SHORT).show();
				} else {
					float avg = ra.readRatingAvg(track.getMBID());
					
					int amt = ra.readRatingAmount(track.getMBID());
					ratingBarInfo.setText(String.format(ratingBarInfoString, Math.ceil(avg * 2) / 2.0, amt));
					Toast.makeText(getApplicationContext(), "Thank you for your rating!", Toast.LENGTH_SHORT).show();
					ratingBar.setClickable(false);
					ratingBar.setEnabled(false);
				}

			}
		});
		
		streaming = false;
		if(track.getStreamable()) { // TODO: why negated?
			TableRow tr = (TableRow) findViewById(R.id.tableRow2_675);
			tr.setVisibility(TableRow.VISIBLE);
			// Hide until stream ready?
			//togglePlayer = new Button(trackActivity);
			//togglePlayer.setText("        .");
			/*LinearLayout.LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			togglePlayer.setLayoutParams(horizontalLayoutParams);*/
			//togglePlayer.setBackgroundResource(R.drawable.mplayer_start);
			//togglePlayer.setOnClickListener(new OnClickListener() {
				//public void onClick(View v) {
					//togglePlayer();
				//}
			//});
			//tr.addView(togglePlayer);
			//String fileName = "preview_" + track.getArtist() + "_" + track.getTitle();
			//System.out.println("MP: download");
			//connection.getPreviewFromId(trackActivity, track.getId(), fileName);
			//previewAvailable("http://play.last.fm/preview/" + track.getId() + ".mp3");
		}
	}

	public void onPostExecute(String album) {
		Intent albumIntent = new Intent(getApplicationContext(), AlbumActivity.class);
		albumIntent.putExtra("album", album);
		System.out.println("Starting AlbumActivity intent");
		//mPlayer.release();
		//mPlayer = null;
		startActivity(albumIntent);
	}
	
	public void previewAvailable(String outputFile) {
		System.out.println("MP: available");
		
		try {
			// works
		//mPlayer.setDataSource("mnt/sdcard/Music/Mix/13 Asleep.mp3");//outputFile);
			
			//FileDescriptor fd = new FileDescriptor();

			//mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			//mPlayer.setDataSource(outputFile);
			System.out.println("MP source set to: " + outputFile);
			
			
			//mPlayer.setOnPreparedListener(new OnPreparedListener() {
				//public void onPrepared(MediaPlayer mp) {
					//trackActivity.togglePlayer.setBackgroundResource(R.drawable.mplayer_stop);
					//mp.start();
				//}
			//});
			/*mPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					trackActivity.togglePlayer.setBackgroundResource(R.drawable.mplayer_play);
				} 
			});
			*/

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	private void togglePlayer() {
		System.out.println("MP: TOGGLE");
		if(mPlayer.isPlaying()) {
			mPlayer.stop();
			togglePlayer.setBackgroundResource(R.drawable.mplayer_start);
		} else {
			//Button  = (Button) findViewById(R.id.tr)
			try {
				togglePlayer.setBackgroundResource(R.drawable.mplayer_loading);
				System.out.println("MP: (re)starting!");
				 mPlayer.prepareAsync();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		streaming = !streaming;
	}


}
