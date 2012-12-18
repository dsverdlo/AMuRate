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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SearchResultsActivity extends Activity {
	private TextView tv;
	private String mbids[];
	private MyConnection connection;
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
		 horizontalLayoutParams.setMargins(5, 20, 0, 0); // left, top, right, bottom
		
		
		try {
			String getSearchResults = getIntent().getStringExtra("searchResults");

			System.out.println("Getting: " + getSearchResults.length());
			//Toast.makeText(getApplicationContext(), "Getting: " + getSearchResults.length(), Toast.LENGTH_LONG).show();
			System.out.println("I go a jar of...");
			System.out.println(getSearchResults);
			//JSONObject searchResults = new JSONObject(getSearchResults);
			// TODO: chop String in pieces for intent.extra
			//if(getSearchResults.length() > 0) tv.setText(getSearchResults);
			
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
				 
				 LinearLayout horizontalLayout = new LinearLayout(getApplicationContext());
				 horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
				 horizontalLayout.setGravity(Gravity.CENTER_VERTICAL);
				 horizontalLayout.setPadding(0, 5, 0, 5);
				 horizontalLayout.setBackgroundColor(Color.DKGRAY);
				 
				 System.out.println("..1");
				 ImageView picture = new ImageView(getApplicationContext());
				 //Uri pictureUri = new Uri("");
				 Iterator<?> k = oneResult.keys();
				 while(k.hasNext()) {
					 System.out.println("one:" + k.next());
				 }

				 //try {
				//	  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL("http://userserve-ak.last.fm/serve/34/13203603.jpg").getContent());
				//	  picture.setImageBitmap(bitmap);
				//	} catch (IOException e) {
				//	  e.printStackTrace();
				//	}
				//Bitmap bitmap = BitmapFactory.decodeFile("http://userserve-ak.last.fm/serve/34/13203603.jpg");
				 //URI uri = new URI("http://userserve-ak.last.fm/serve/34/13203603.jpg");//oneResult.getString("image"));
				 //picture.setImageBitmap(bitmap);
				 picture.setImageResource(R.drawable.cher_med);
				 System.out.println("..2");
				 
				 LinearLayout titleLayout = new LinearLayout(getApplicationContext());
				 titleLayout.setOrientation(LinearLayout.VERTICAL); 
				 //titleLayout.
				 System.out.println("..3");

				 TextView artist = new TextView(getApplicationContext());
				 artist.setText(oneResult.getString("name"));

				 if(getKey.equals("track")) {
					 TextView title = new TextView(getApplicationContext());
					 title.setText(oneResult.getString("name"));
					 titleLayout.addView(title);
					 //
					 artist.setText(oneResult.getString("artist"));
				 }
				 TextView next = new TextView(getApplicationContext());
				 next.setText(">");
				 System.out.println("..4"); 
				 next.setGravity(Gravity.RIGHT);

				 final String mbid = oneResult.getString("mbid");
				 horizontalLayout.setOnClickListener( new OnClickListener() {
					 public void onClick(View v) {
						 System.out.println("Someone clicked a track! Starting connection for: " + mbid);
						 connection.getFromMBID(thisActivity, mbid);
					 }
				 });
				 horizontalLayout.setClickable(true);
				 horizontalLayout.setPadding(5, 5, 5, 0);
				 System.out.println("..5");
				 
				 horizontalLayout.addView(picture);
				 titleLayout.addView(artist);
				 horizontalLayout.addView(titleLayout);
				 horizontalLayout.addView(next);
				 ll.addView(horizontalLayout, horizontalLayoutParams);
				 
				 System.out.println("Added the search result to the gui.Continue Looping!");
			 }


		} catch (Exception e) {
			System.out.println("Exception in SearchResultsActivity (onCreate):");
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search_results, menu);
		return true;
	}
	
	public void onPostExecute(String results) {
		 Intent nextPage = new Intent(getApplicationContext(), TrackActivity.class);
		 nextPage.putExtra("track", results);
		 startActivity(nextPage);
	}

}
