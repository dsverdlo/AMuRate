package com.dsverdlo.AMuRate.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.History;
import com.dsverdlo.AMuRate.objects.HistoryAdapter;
import com.dsverdlo.AMuRate.objects.Rating;
import com.dsverdlo.AMuRate.objects.RatingAdapter;
import com.dsverdlo.AMuRate.services.MyConnection;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class HistoryActivity extends Activity {
	private HistoryAdapter ha;
	private RatingAdapter ra;
	private HistoryActivity activity;
	private Context context;
	private String textBeforeLoading;
	private Button buttonLoading;

	private LinearLayout lv;
	private Button buttonBack;
	private TextView textTitle;
	private Button buttonDelete;
	private Button buttonOptionSearch;
	private Button buttonOptionTracks;
	private Button buttonOptionRating;

	// option switching system
	private int currentOption;
	private final static int optionSearch = 0;
	private final static int optionTracks = 1;
	private final static int optionRating = 2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		activity = this;
		context = getApplicationContext();
		ha = new HistoryAdapter(context);
		ra = new RatingAdapter(context);

		// Load the layout 
		lv = (LinearLayout)findViewById(R.id.history_linlay);
		buttonBack = (Button)findViewById(R.id.history_back);
		textTitle = (TextView)findViewById(R.id.history_title);
		buttonDelete = (Button)findViewById(R.id.history_button_remove);
		buttonOptionRating = (Button)findViewById(R.id.history_button_ratings);
		buttonOptionSearch = (Button)findViewById(R.id.history_button_search);
		buttonOptionTracks = (Button) findViewById(R.id.history_button_track);

		buttonDelete.setText(" Delete history ");
		buttonDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch(currentOption) {
				case optionSearch:
					ha.deleteHistory(HistoryAdapter.SQL_DELETE_SEARCH);
					lv.removeAllViews();
					break;
				case optionTracks:
					ha.deleteHistory(HistoryAdapter.SQL_DELETE_TRACK);
					lv.removeAllViews();
					break;
				case optionRating:
					ra.deleteRatings();
					lv.removeAllViews();
					break;
				}
			}
		});

		buttonBack.setText("Back");
		buttonBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		textTitle.setText("~History~");

		// Load the options' onclicklisteners
		
		buttonOptionRating.setBackgroundResource(R.layout.rounded_corners);
		buttonOptionRating.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(currentOption != optionRating) {
					currentOption = optionRating;
					lv.removeAllViews();
					loadRatings();
				} else {
					Toast.makeText(context, "Already showing ratings...", Toast.LENGTH_SHORT).show();
				} 
			}
		} );

		buttonOptionTracks.setBackgroundResource(R.layout.rounded_corners);
		buttonOptionTracks.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(currentOption != optionTracks) {
					currentOption = optionTracks;
					lv.removeAllViews();
					loadTracks();
				}else {
					Toast.makeText(context, "Already showing tracks...", Toast.LENGTH_SHORT).show();
				}
			}
		});

		buttonOptionSearch.setBackgroundResource(R.layout.rounded_corners);
		buttonOptionSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(currentOption != optionSearch) {
					currentOption = optionSearch;
					lv.removeAllViews();
					loadSearch();
				} else {
					Toast.makeText(context, "Already showing search...", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Start loading all the search histories
		currentOption = optionSearch;
		loadSearch();

	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SimpleDateFormat")
	private String dateToString(int seconds) {
		Date d = new Date((long) seconds * 1000); // takes milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/M/yyyy");
		return sdf.format(d);
	}

	public void onDoneLoadingTrackinfo(String trackInfo) {
		buttonLoading.setText(textBeforeLoading);

		Intent nextPage = new Intent(context, TrackActivity.class);
		nextPage.putExtra("track", trackInfo);
		startActivity(nextPage);
	}

	private void loadRatings() {
		Rating[] ratings = ra.getRatings(RatingAdapter.SQL_GET_ALL_RATINGS);
		if(ratings == null) {
			TextView tv = new TextView(context);
			tv.setText("No history yet.");
			lv.addView(tv);
			return;
		}
		
		System.out.println("HistoryActivity says: " + ratings.length);
		// Otherwise, for every rating we make a view in the listview
		for(int i = ratings.length; i > 0; i--) {
			Button b = new Button(context);
			b.setBackgroundResource(R.layout.rounded_corners);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(8, 10, 8, 4); // left, top, right, bottom
			b.setLayoutParams(lp);

			Rating r = ratings[i-1];
			final String artist = r.getArtist();
			final String title = r.getTitle();
			final int date = r.getDate();
			float rating = r.getRating();

			String format = "Rating: %s - %s (%.1f stars)";
			b.setText(String.format(format, artist, title, rating));

			b.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Toast.makeText(context, "On: "+dateToString(date), Toast.LENGTH_SHORT).show();
				}
			});
			lv.addView(b);
			continue;
		}
	}


	private void loadSearch() {
		History[] histories = ha.getSearchHistory(HistoryAdapter.SQL_GET_ALL_SEARCH);

		// If there is no search history yet, display a textview saying that
		if(histories == null) {
			TextView tv = new TextView(context);
			tv.setText("No searches performed yet.");
			lv.addView(tv);
			return;
		}

		System.out.println("HistoryActivity says: " + histories.length);
		// Otherwise, for every history we make a button in the listview
		for(int i = histories.length; i > 0; i--) {
			Button b = new Button(context);
			b.setBackgroundResource(R.layout.rounded_corners);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(8, 10, 8, 4); // left, top, right, bottom
			b.setLayoutParams(lp);

			History h = histories[i-1];
			final String artist = h.getArtist();
			final String title = h.getTitle();
			final int date = h.getDate();

			String format = "Search: %s - %s";
			b.setText(String.format(format, artist, title));

			b.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					Toast.makeText(context, "On: "+dateToString(date), Toast.LENGTH_SHORT).show();
					return true;
				}
			});
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent next = new Intent(context, MainActivity.class);
					next.putExtra("title", title);
					next.putExtra("artist", artist);
					finish();
					next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(next);
				}
			});
			lv.addView(b);
			continue;
		}
	}

	private void loadTracks() {
		History[] histories = ha.getSearchHistory(HistoryAdapter.SQL_GET_ALL_TRACKS);

		// If there is no track history yet, display a textview saying that
		if(histories == null) {
			TextView tv = new TextView(context);
			tv.setText("No tracks viewed yet.");
			lv.addView(tv);
			return;
		}

		System.out.println("HistoryActivity says: " + histories.length);
		// Otherwise, for every history we make a button in the listview
		for(int i = histories.length; i > 0; i--) {
			final Button b = new Button(context);
			b.setBackgroundResource(R.layout.rounded_corners);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(8, 10, 8, 4); // left, top, right, bottom
			b.setLayoutParams(lp);

			History h = histories[i-1];
			final String artist = h.getArtist();
			final String title = h.getTitle();
			final String mbid = h.getMbid();
			final int date = h.getDate();

			String format = "Track: %s - %s";
			b.setText(String.format(format, artist, title));
			
			b.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					Toast.makeText(context, "On: "+dateToString(date), Toast.LENGTH_SHORT).show();
					return true;
				}
			});
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					MyConnection conn = new MyConnection();
					textBeforeLoading = (String) b.getText();
					buttonLoading = b;
					b.setText("Track: loading...");
					conn.loadTrackActivity(activity, mbid);
				}
			});
			lv.addView(b);
			continue;
		}
	}
}