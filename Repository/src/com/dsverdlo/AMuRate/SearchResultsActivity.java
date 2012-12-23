package com.dsverdlo.AMuRate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SearchResultsActivity extends Activity {
	private TextView tv;
	private String mbids[];
	private MyConnection connection;

	private enum TrackKeys
	{
		streamable, listeners, image, artist, url, mbid, name; 
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		//tv = (TextView) findViewById(R.id.searchTextView);

		connection = new MyConnection();


		final SearchResultsActivity thisActivity = this;

		mbids = new String[30];
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
				// if( jObject.get(key) instanceof JSONObject ){
				getKey = key;
				System.out.println("---" + key);
				//}
			}

			JSONArray track = searchResults.getJSONArray(getKey);
			System.out.println("" + track.length() + " objects in JSONArray");
			for(int i = 0; i < track.length(); i++ ) {
				System.out.println("Try to get array[" + i + "]");
				final JSONObject oneResult = track.getJSONObject(i);
				//System.out.println("Object"+(i+1)+"= "+oneResult.toString());

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

				if(getKey.equals("track")) {
					TextView title = new TextView(getApplicationContext());
					title.setTextColor(Color.BLACK);
					title.setText(oneResult.getString("name"));
					titleLayout.addView(title);
					//
				}
				TextView next = new TextView(getApplicationContext());
				next.setGravity(Gravity.RIGHT);

				// Dispatch all the keys!
				Iterator<?> it = oneResult.keys();
				while(it.hasNext()) {
					String key = (String) it.next();
					System.out.println("one:" + key);
					switch(TrackKeys.valueOf(key)) {
					case name: 
						break;
					case image:
						JSONArray JSONimage = oneResult.getJSONArray("image");
						JSONObject JSONimageMedium = JSONimage.getJSONObject(1);
						String url = JSONimageMedium.getString("#text");
						connection.loadImage(url, picture);
						break;
					case artist:
						artist.setTextColor(Color.BLACK);
						artist.setText(oneResult.getString("artist"));
						break;
					case streamable:
						break;
					default: break;
					}
				}
				//Track track(oneResults);

				// Set the onClick function
				final String mbid = oneResult.getString("mbid"); 
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
