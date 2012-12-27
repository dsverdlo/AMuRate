package com.dsverdlo.AMuRate;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SearchResultsActivity extends Activity {
	private MyConnection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);

		connection = new MyConnection();
		final SearchResultsActivity thisActivity = this;
		
		// Grab the vertical layout so we can add objects to it
		LinearLayout ll = (LinearLayout) findViewById(R.id.searchResultsLayout);

		// Add small margin
		LinearLayout.LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		horizontalLayoutParams.setMargins(5, 20, 5, 0); // left, top, right, bottom


		try {
			String getSearchResults = getIntent().getStringExtra("searchResults");
			System.out.println("Getting: " + getSearchResults.length());

			JSONObject searchResults = new JSONObject(getSearchResults);
			String getKey = "";
			Iterator<?> keys = searchResults.keys();

			while( keys.hasNext() ){
				String key = (String)keys.next();
				getKey = key;
				System.out.println("---" + key);
			}

			JSONArray tracks = searchResults.getJSONArray(getKey);
			System.out.println("" + tracks.length() + " objects in JSONArray");
			for(int i = 0; i < tracks.length(); i++ ) {
				
				System.out.println("Try to get array[" + i + "]");
				final JSONObject oneResult = tracks.getJSONObject(i);
				Track track = new Track(oneResult);

				// Create a layout for the track
				final LinearLayout horizontalLayout = new LinearLayout(getApplicationContext());
				horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
				horizontalLayout.setGravity(Gravity.CENTER_VERTICAL);
				//Because i can
				horizontalLayout.setPadding(0, 0, 0, 0);
				horizontalLayout.setBackgroundResource(R.drawable.track_background_wider_small);
				horizontalLayout.setPadding(5, 5, 5, 5);

				ImageView picture = new ImageView(getApplicationContext());

				LinearLayout titleLayout = new LinearLayout(getApplicationContext());
				titleLayout.setOrientation(LinearLayout.VERTICAL); 
				titleLayout.setPadding(20, 0, 20, 0);

				TextView artist = new TextView(getApplicationContext());

				//if(getKey.equals("track")) {
				TextView title = new TextView(getApplicationContext());
				title.setTextColor(Color.BLACK);
				title.setText(track.getTitle());
				titleLayout.addView(title);
					//
				
				TextView next = new TextView(getApplicationContext());
				next.setGravity(Gravity.RIGHT);

				artist.setText(track.getArtist());
				
				// if possible set image in pictureview
				String imageUrl = track.getImage("s");
				if(imageUrl.length() != 0) { 
					connection.loadImage(imageUrl, picture);
				} else {
					picture.setImageResource(R.drawable.not_available);
				}

				// Set the onClick function
				final String mbid = track.getMBID(); 
				horizontalLayout.setOnClickListener( new OnClickListener() {
					public void onClick(View v) {
						horizontalLayout.setBackgroundResource(R.drawable.track_background_2);
						System.out.println("Someone clicked a track! Starting connection for: " + mbid);
						connection.getFromMBID(thisActivity, mbid);
					}
				});
				horizontalLayout.setClickable(true);
				
				// Add single items to horizontal layout
				horizontalLayout.addView(picture);
				titleLayout.addView(artist);
				horizontalLayout.addView(titleLayout);
				horizontalLayout.addView(next);
				
				// Add finished product to vertical linear layout
				ll.addView(horizontalLayout, horizontalLayoutParams);
			}


		} catch (Exception e) {
			System.out.println("Exception in SearchResultsActivity (onCreate):");
			e.printStackTrace();
		}
	}

	public void onPostExecute(String results) {
		Intent nextPage = new Intent(getApplicationContext(), TrackActivity.class);
		nextPage.putExtra("track", results);
		startActivity(nextPage);
	}

}
