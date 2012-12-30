package com.dsverdlo.AMuRate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class TrackActivity extends Activity {
	private TrackActivity trackActivity;

	private TextView title;
	private TextView album;
	private ImageView image;
	private MyConnection connection;
	private TextView streamText;
	private RatingBar ratingBar;
	private Button back;


	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_activity);
		trackActivity = this;

		title = (TextView) findViewById(R.id.textView1);
		album = (TextView) findViewById(R.id.textView2);
		image = (ImageView) findViewById(R.id.track_image);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		ratingBar.setPadding(5, 10, 0, 0); 

		back = (Button) findViewById(R.id.track_back_button);
		back.setText("New search");

		streamText = (TextView) findViewById(R.id.textView3);
		streamText.setPadding(0, 300, 0, 0);
		connection = new MyConnection();

		final Track track = new Track();
		track.loadFromInfo(getIntent().getStringExtra("track"));

		back.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
				Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(mainIntent);
			}
		});

		title.setText(track.getTitle() + " - " + track.getArtist() + "  (" + track.getDuration() + ")");
		title.setPadding(0, 10, 0, 20);

		String imageUrl = track.getImage("l");
		if(imageUrl.length() > 0) {
			connection.loadImage(imageUrl, image);
		} else {
			// Else try a medium picture?
			String OtherImageUrl = track.getImage("m");
			if(OtherImageUrl.length() > 0) {
				connection.loadImage(OtherImageUrl, image);
			} else image.setImageResource(R.drawable.not_available);
		}

		album.setText("From album: " + track.getAlbum());
		if(track.albumMBID.length() > 0) {
			album.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					System.out.println("You click on the album");
					MyConnection conn = new MyConnection();
					conn.getFromAlbum(trackActivity, track.albumMBID);
				}

			});
		album.setClickable(true);
		}

		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				Toast.makeText(getApplicationContext(), "Sending score: " + rating + " to database!", Toast.LENGTH_SHORT).show();
				ratingBar.setClickable(false);

			}
		});
	}

	public void onPostExecute(String album) {
		Intent mainIntent = new Intent(getApplicationContext(), AlbumActivity.class);
		mainIntent.putExtra("album", album);
		System.out.println("Starting AlbumActivity intent");
		startActivity(mainIntent);
	}




}
