package com.dsverdlo.AMuRate.gui;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.AMuRate;
import com.dsverdlo.AMuRate.objects.Track;
import com.dsverdlo.AMuRate.services.DownloadImageTask;
import com.dsverdlo.AMuRate.services.DownloadLastFM;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Display the search results on the screen.
 * 
 * @author David Sverdlov
 */
public class SearchResultsActivity extends BlankActivity {
	private SearchResultsActivity thisActivity;
	// Save the clickedLayout so we can alter it when the connection returns
	private RelativeLayout clickedLayout;
	private AMuRate amr;
	private AsyncTask[] tasks;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		thisActivity = this;
		final SearchResultsActivity thisActivity = this;
		amr = (AMuRate)getApplicationContext();
		tasks = new AsyncTask[30];
		
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
				//
				
				//System.out.println("Try to get array[" + i + "]");
				final JSONObject oneResult = tracks.getJSONObject(i);
				Track track = new Track();
				track.loadFromSearch(oneResult);

				// If the track does not come with a mbid, skip it
				if(track.getMBID().length() == 0) {
					continue;
				}
				
				// Create a layout for the track
				final RelativeLayout horizontalLayout = new RelativeLayout(amr);
				horizontalLayout.setGravity(Gravity.LEFT); // TODO; center verical
				//Because i can
				horizontalLayout.setPadding(0, 0, 0, 0);
				horizontalLayout.setBackgroundResource(R.drawable.track_background_wider_small);
				horizontalLayout.setPadding(5, 5, 5, 5);

				ImageView picture = new ImageView(amr);
				AnimationView load_pic = new AnimationView(amr, null);
				load_pic.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				load_pic.setMinimumHeight(200);
				load_pic.setMinimumWidth(120);
				load_pic.setVisibility(View.GONE);
				load_pic.setId(100);
				int k = load_pic.getWidth();
				int j = load_pic.getHeight();
				picture.setVisibility(View.GONE); // TODO:
				picture.setId(105);
				
				LinearLayout titleLayout = new LinearLayout(amr);
				titleLayout.setOrientation(LinearLayout.VERTICAL); 
				titleLayout.setPadding(20, 0, 20, 0);

				TextView artist = new TextView(amr);

				//if(getKey.equals("track")) {
				TextView title = new TextView(amr);
				title.setTextColor(Color.BLACK);
				title.setText(track.getTitle());
				titleLayout.addView(title);
					//
				
				TextView next = new TextView(amr);
				next.setGravity(Gravity.RIGHT);

				artist.setTextColor(Color.BLACK);
				artist.setText(track.getArtist());
				
				// if possible set image in pictureview
				String imageUrl = track.getImage("l");
				if(imageUrl.length() != 0) { 
					DownloadImageTask down = new DownloadImageTask(picture, load_pic);
					tasks[i] = down.execute(imageUrl);
				} else {
					picture.setImageResource(R.drawable.not_available);
				}

				// Set the onClick function
				final String mbid = track.getMBID(); 
				horizontalLayout.setOnClickListener( new OnClickListener() {
					public void onClick(View v) {
						horizontalLayout.setBackgroundResource(R.drawable.track_background_2);
						clickedLayout = horizontalLayout;
						System.out.println("Someone clicked a track! Starting connection for: " + mbid);
						
						// before we download the info, cancel other downloads
						for(int idx = 0; idx < tasks.length; idx++) {
							// if task is not null and not finished: cancel
							if(tasks[idx] != null && !tasks[idx].isCancelled()) {
								tasks[idx].cancel(true);
								tasks[idx] = null;
							}
						}
						new DownloadLastFM(thisActivity).execute(""+DownloadLastFM.operations.dl_search_results_track, mbid);
					}
				});
				horizontalLayout.setClickable(true);
				
				RelativeLayout.LayoutParams loadParams = 
		                new RelativeLayout.LayoutParams(
		                    RelativeLayout.LayoutParams.WRAP_CONTENT, 
		                    RelativeLayout.LayoutParams.WRAP_CONTENT);
				loadParams.addRule(RelativeLayout.ALIGN_LEFT);
				loadParams.addRule(RelativeLayout.LEFT_OF, 105);
				
				RelativeLayout.LayoutParams imageParams = 
		                new RelativeLayout.LayoutParams(
		                    RelativeLayout.LayoutParams.WRAP_CONTENT, 
		                    RelativeLayout.LayoutParams.WRAP_CONTENT);
				imageParams.addRule(RelativeLayout.ALIGN_LEFT);
				
				RelativeLayout.LayoutParams titleParams = 
		                new RelativeLayout.LayoutParams(
		                    RelativeLayout.LayoutParams.WRAP_CONTENT, 
		                    RelativeLayout.LayoutParams.WRAP_CONTENT);

		      titleParams.addRule(RelativeLayout.CENTER_VERTICAL);
		      titleParams.addRule(RelativeLayout.RIGHT_OF, 105);
				
				
				// Add single items to horizontal layout
				horizontalLayout.addView(load_pic,loadParams);
				horizontalLayout.addView(picture, imageParams);
				titleLayout.addView(artist);
				horizontalLayout.addView(titleLayout, titleParams);
				horizontalLayout.addView(next);

				//load_pic.bringToFront();
				System.out.println("Loadpic width="+k+ " and height="+j);
				// Add finished product to vertical linear layout
				ll.addView(horizontalLayout, horizontalLayoutParams);
			}


		} catch (Exception e) {
			System.out.println("Exception in SearchResultsActivity (onCreate):");
			e.printStackTrace();
		}
	}

	public void onPostExecute(String results) {
		clickedLayout.setBackgroundResource(R.drawable.track_background_wider_small);
		
		Intent nextPage = new Intent(amr, TrackActivity.class);
		nextPage.putExtra("track", results);
		startActivity(nextPage);
	}

}
