package com.dsverdlo.AMuRate;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class TrackActivity extends Activity {
	private TextView title;
	private TextView album;
	private ImageView image;
	private RatingBar ratingBar;
	 
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_activity);
		
		title = (TextView) findViewById(R.id.textView1);
		album = (TextView) findViewById(R.id.textView2);
		image = (ImageView) findViewById(R.id.track_image);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
		
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
			
			JSONObject JSONartist = JSONtrack.getJSONObject("artist");
			String trackArtist = JSONartist.getString("name");
			
			JSONObject JSONalbum = JSONtrack.getJSONObject("album");
			String trackAlbum = JSONalbum.getString("title");
			
			

			title.setText(trackName + " - " + trackArtist + "(" + convertDurationToString(trackDuration) + ")");
			image.setImageResource(R.drawable.cher_large);
			album.setText("From album: " + trackAlbum);
			
			//ratingBar.setRating(3);
			
			
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

		if(hours == 0) return "" + minutes + ":" + seconds; 
		return "" + hours + ":" + minutes + ":" + seconds;
	}
}
