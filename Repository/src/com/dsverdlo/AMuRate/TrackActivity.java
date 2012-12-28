package com.dsverdlo.AMuRate;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.portable.Streamable;

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
		
		title = (TextView) findViewById(R.id.textView1);
		album = (TextView) findViewById(R.id.textView2);
		image = (ImageView) findViewById(R.id.track_image);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		ratingBar.setPadding(5, 10, 0, 0); 
		
		back = (Button) findViewById(R.id.track_back_button);
		back.setText("New search");
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
				Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(mainIntent);
			}
		});
		
		streamText = (TextView) findViewById(R.id.textView3);
		streamText.setPadding(0, 300, 0, 0);
		connection = new MyConnection();
		
			//JSONObject JSONobject = new JSONObject(getIntent().getStringExtra("track"));
			Track track = new Track();
			//track.loadFromInfo(JSONobject.getJSONObject("track"));
			track.loadFromInfo(getIntent().getStringExtra("track"));
			
			String trackName = track.getTitle();
			//int[] trackDuration = convertDuration(JSONtrack.getInt("duration"));
			
			/* START STREAMABLE SHIT 
			JSONObject JSONstreamable = JSONtrack.getJSONObject("streamable");
			Iterator<?> it = JSONstreamable.keys();
			while(it.hasNext()) {
				String kit = it.next().toString();
				System.out.println("Streamable: " + kit);
			}

			System.out.println(JSONstreamable.toString());
			int trackStreamable = JSONstreamable.getInt("#text");
			if (trackStreamable == 0) {
				streamText.setText("This song is not streamable");
			} else {
				streamText.setText("This song is streamable! Click here!");
				streamText.setOnClickListener( new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(getApplicationContext(), "Not yet implemented.", Toast.LENGTH_SHORT).show();
					}
					
				});
			} */
			
			//JSONObject JSONartist = JSONtrack.getJSONObject("artist");
			//String trackArtist = JSONartist.getString("name");
			
			
			//JSONObject JSONalbum = JSONtrack.getJSONObject("album");
			//String trackAlbum = JSONalbum.getString("title");
			

			title.setText(track.getTitle() + " - " + track.getArtist() + "  (" + track.getDuration() + ")");
			title.setPadding(0, 10, 0, 20);

			/*JSONArray JSONimage = JSONalbum.getJSONArray("image");
			JSONObject JSONimageLarge = JSONimage.getJSONObject(2);
			String url = JSONimageLarge.getString("#text");*/
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
			
			// TODO: add clickable to album songs 
			album.setText("From album: " + track.getAlbum());
			
			ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					Toast.makeText(getApplicationContext(), "Sending score: " + rating + " to database!", Toast.LENGTH_SHORT).show();
					ratingBar.setClickable(false);
					
				}
			});
			
			
		
		
	}
	

}
