package com.dsverdlo.AMuRate.gui;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.R.layout;
import com.dsverdlo.AMuRate.R.menu;
import com.dsverdlo.AMuRate.objects.Artist;
import com.dsverdlo.AMuRate.services.MyConnection;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchArtistActivity extends Activity {
	MyConnection connection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_artist);


		final SearchArtistActivity thisActivity = this;

		// Grab the vertical layout so we can add objects to it
		LinearLayout ll = (LinearLayout) findViewById(R.id.searchArtistLayout);

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

			JSONArray artists = searchResults.getJSONArray(getKey);
			System.out.println("" + artists.length() + " objects in JSONArray");
			for(int i = 0; i < artists.length(); i++ ) {

				//System.out.println("Try to get array[" + i + "]");
				final JSONObject oneResult = artists.getJSONObject(i);
				Artist artist = new Artist();
				artist.loadFromSearch(oneResult.toString()); // tostringed

				// Create a layout for the artist
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

				TextView artistView = new TextView(getApplicationContext());

				TextView artistName = new TextView(getApplicationContext());
				artistName.setTextColor(Color.BLACK);
				artistName.setText(artist.getArtistName());
				titleLayout.addView(artistName);
				//

				TextView next = new TextView(getApplicationContext());
				next.setGravity(Gravity.RIGHT);

				artistView.setTextColor(Color.BLACK);
				artistView.setText(artist.getArtistName());

				// if possible set image in pictureview
				String imageUrl = artist.getImage("l");
				if(imageUrl.length() != 0) { 
					connection.loadImage(imageUrl, picture);
				} else {
					picture.setImageResource(R.drawable.not_available);
				}

				// Set the onClick function
				final String mbid = "23"; //todo: 
				horizontalLayout.setOnClickListener( new OnClickListener() {
					public void onClick(View v) {
						horizontalLayout.setBackgroundResource(R.drawable.track_background_2);
						System.out.println("Someone clicked an artist! Starting connection for: " + mbid);
						//connection.getFromMBID(thisActivity, mbid);//todo
					}
				});
				horizontalLayout.setClickable(true);

				// Add single items to horizontal layout
				horizontalLayout.addView(picture);
				//titleLayout.addView(artist);
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




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search_for_artist, menu);
		return true;
	}

}
