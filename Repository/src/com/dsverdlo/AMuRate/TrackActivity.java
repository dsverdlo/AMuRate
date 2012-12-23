package com.dsverdlo.AMuRate;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.portable.Streamable;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
	 
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_activity);
		
		title = (TextView) findViewById(R.id.textView1);
		album = (TextView) findViewById(R.id.textView2);
		image = (ImageView) findViewById(R.id.track_image);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		ratingBar.setPadding(5, 10, 0, 0); 
		
		streamText = (TextView) findViewById(R.id.textView3);
		streamText.setPadding(0, 300, 0, 0);
		connection = new MyConnection();
		
		try {
			JSONObject JSONobject = new JSONObject(getIntent().getStringExtra("track"));
			JSONObject JSONtrack = JSONobject.getJSONObject("track");
			Iterator<?> keys = JSONtrack.keys();
			while(keys.hasNext()) {
				String key = (String) keys.next();
				System.out.println("*-* " + key);
			}
			
			String trackName = JSONtrack.getString("name");
			int[] trackDuration = convertDuration(JSONtrack.getInt("duration"));
			

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
			}
			
			JSONObject JSONartist = JSONtrack.getJSONObject("artist");
			String trackArtist = JSONartist.getString("name");
			
			JSONObject JSONalbum = JSONtrack.getJSONObject("album");
			String trackAlbum = JSONalbum.getString("title");
			

			title.setText(trackName + " - " + trackArtist + "  (" + convertDurationToString(trackDuration) + ")");
			title.setPadding(0, 10, 0, 20);

			JSONArray JSONimage = JSONalbum.getJSONArray("image");
			JSONObject JSONimageLarge = JSONimage.getJSONObject(2);
			String url = JSONimageLarge.getString("#text");
			connection.loadImage(url, image);
			album.setText("From album: " + trackAlbum);
			
			ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					Toast.makeText(getApplicationContext(), "Sending score: " + rating + " to database!", Toast.LENGTH_SHORT).show();
					ratingBar.setClickable(false);
					
				}
			});
			
			
		} catch (JSONException e) {
			System.out.println("JSON Exception in TrackActivity(onCreate)");
			e.printStackTrace();
		}
		
		
	}
	
	private int[] convertDuration(double milliseconds) {
		int time[] = new int[3];
		int seconds = (int) (milliseconds / 1000) % 60 ;
		int minutes = (int) ((milliseconds / (1000*60)) % 60);
		int hours = (int) ((milliseconds / (1000*60*60)) % 24);
		
		time[0] = seconds;
		time[1] = minutes;
		time[2] = hours;
		return time;
	}
	
	private String convertDurationToString(int[] time) {
		int hours = time[2];
		int minutes = time[1];
		int seconds = time[0];
		boolean hoursExtra = hours < 9;
		boolean minsExtra = minutes < 9;
		boolean secsExtra = seconds < 9;
		if(hours == 0) return "" + (minsExtra ? "0": "") + minutes + ":" + (secsExtra ? "0" : "") + seconds; 
		return "" + (hoursExtra ? "0" : "") + hours + ":" + (minsExtra ? "0" : "") + minutes + ":" + (secsExtra ? "0" : "") + seconds;
	}
}
