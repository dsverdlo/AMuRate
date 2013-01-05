package com.dsverdlo.AMuRate.gui;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.RatingAdapter;
import com.dsverdlo.AMuRate.objects.Track;
import com.dsverdlo.AMuRate.services.MyConnection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

	private TextView title;
	private TextView album;
	private ImageView image;
	private MyConnection connection;
	private TextView streamText;
	private RatingBar ratingBar;
	private TextView ratingBarInfo;
	private Button back;
	
	private RatingAdapter ra;

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
		ra.open();

		title = (TextView) findViewById(R.id.track_title);
		album = (TextView) findViewById(R.id.track_album);
		image = (ImageView) findViewById(R.id.track_image);
		
		back = (Button) findViewById(R.id.track_back_button);
		back.setText("New search"); 
		
		ratingBar = (RatingBar) findViewById(R.id.track_ratingBar);
		ratingBar.setPadding(5, 10, 0, 0);  
		ratingBar.setNumStars(5); // TODO: remove here
		ratingBar.setStepSize((float) 0.5);

		final float ratingBarRating = ra.getRatingAvg(track.getMBID());
		final int ratingBarAmount = ra.getRatingAmount(track.getMBID());
		ratingBarInfo = (TextView) findViewById(R.id.track_ratingBar_info);
		final String ratingBarInfoString = "Average: %.1f (based on %d reviews)";
		ratingBarInfo.setText(String.format(ratingBarInfoString, Math.ceil(ratingBarRating * 2) / 2.0, ratingBarAmount));
 

		streamText = (TextView) findViewById(R.id.track_stream);
		//streamText.setPadding(0, 50, 0, 0);

		back.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				ra.close();
				finish();
				Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(mainIntent);
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
				long addResult = ra.addRating(track.getMBID(), rating);
				if(addResult < 0) {
					Toast.makeText(getApplicationContext(), "Error sending score.", Toast.LENGTH_SHORT).show();
				} else {
					float avg = ra.getRatingAvg(track.getMBID());
					
					int amt = ra.getRatingAmount(track.getMBID());
					ratingBarInfo.setText(String.format(ratingBarInfoString, Math.ceil(avg * 2) / 2.0, amt));
					Toast.makeText(getApplicationContext(), "Thank you for your rating!", Toast.LENGTH_SHORT).show();
					ratingBar.setClickable(false);
					ratingBar.setEnabled(false);
				}

			}
		});
	}

	public void onPostExecute(String album) {
		Intent albumIntent = new Intent(getApplicationContext(), AlbumActivity.class);
		albumIntent.putExtra("album", album);
		System.out.println("Starting AlbumActivity intent");
		ra.close();
		startActivity(albumIntent);
	}




}
