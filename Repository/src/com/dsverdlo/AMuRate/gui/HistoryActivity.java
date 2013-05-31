package com.dsverdlo.AMuRate.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.AMuRate;
import com.dsverdlo.AMuRate.objects.History;
import com.dsverdlo.AMuRate.objects.Rating;
import com.dsverdlo.AMuRate.services.DownloadLastFM;
import com.dsverdlo.AMuRate.services.InternalDatabaseHistoryAdapter;
import com.dsverdlo.AMuRate.services.InternalDatabaseRatingAdapter;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends BlankActivity {
	private AMuRate amr;
	
	private InternalDatabaseHistoryAdapter ha;
	private InternalDatabaseRatingAdapter ra;
	private HistoryActivity activity;
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
		amr = (AMuRate)getApplicationContext();
		ha = new InternalDatabaseHistoryAdapter(amr);
		ra = new InternalDatabaseRatingAdapter(amr);

		// Load the layout 
		lv = (LinearLayout)findViewById(R.id.history_linlay);

		
		buttonBack = (Button)findViewById(R.id.history_back);
		textTitle = (TextView)findViewById(R.id.history_title);
		buttonDelete = (Button)findViewById(R.id.history_button_remove);
		buttonOptionRating = (Button)findViewById(R.id.history_button_ratings);
		buttonOptionSearch = (Button)findViewById(R.id.history_button_search);
		buttonOptionTracks = (Button) findViewById(R.id.history_button_track);

		buttonDelete.setText(R.string.history_delete);
		buttonDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch(currentOption) {
				case optionSearch:
					ha.deleteHistory(InternalDatabaseHistoryAdapter.SQL_DELETE_SEARCH);
					Toast.makeText(amr, R.string.history_deleted_search, Toast.LENGTH_SHORT).show();
					lv.removeAllViews();
					break;
				case optionTracks:
					ha.deleteHistory(InternalDatabaseHistoryAdapter.SQL_DELETE_TRACK);
					Toast.makeText(amr, R.string.history_deleted_tracks, Toast.LENGTH_SHORT).show();
					lv.removeAllViews();
					break;
				case optionRating:
					ra.deleteRatings();
					Toast.makeText(amr, R.string.history_deleted_synced_ratings, Toast.LENGTH_SHORT).show();
					lv.removeAllViews();
					break;
				}
			}
		});

		buttonBack.setText(R.string.back);
		buttonBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		textTitle.setText(R.string.history_history);

		// Load the options' onclicklisteners
		
		buttonOptionRating.setBackgroundResource(R.layout.rounded_corners);
		buttonOptionRating.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(currentOption != optionRating) {
					currentOption = optionRating;
					lv.removeAllViews();
					loadRatings();
				} else {
					Toast.makeText(amr, R.string.history_showing_ratings, Toast.LENGTH_SHORT).show();
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
					Toast.makeText(amr, R.string.history_showing_tracks, Toast.LENGTH_SHORT).show();
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
					Toast.makeText(amr, R.string.history_showing_search, Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Start loading all the search histories
		currentOption = optionSearch;
		loadSearch();

	}





	@SuppressLint("SimpleDateFormat")
	private String dateToString(int seconds) {
		Date d = new Date((long) seconds * 1000); // takes milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/M/yyyy");
		return sdf.format(d);
	}

	public void onDoneLoadingTrackinfo(String trackInfo) {
		buttonLoading.setText(textBeforeLoading);

		Intent nextPage = new Intent(amr, TrackActivity.class);
		nextPage.putExtra("track", trackInfo);
		startActivity(nextPage);
	}

	private void loadRatings() {
		Rating[] ratings = ra.getRatings(InternalDatabaseRatingAdapter.SQL_GET_ALL_RATINGS);
		if(ratings == null) {
			TextView tv = new TextView(amr);
			tv.setText(R.string.history_no_ratings_yet);
			lv.addView(tv);
			return;
		}
		
		
		// Otherwise, for every rating we make a view in the listview
		for(int i = ratings.length; i > 0; i--) {
			Button b = new Button(amr);
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

			String format = "%s - %s (%.1f " + amr.getString(R.string.stars) + ")";
			b.setText(String.format(format, artist, title, rating));

			b.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Toast.makeText(amr, amr.getString(R.string.history_on)+dateToString(date), Toast.LENGTH_SHORT).show();
				}
			});
			lv.addView(b);
			continue;
		}
	}


	private void loadSearch() {
		History[] histories = ha.getSearchHistory(InternalDatabaseHistoryAdapter.SQL_GET_ALL_SEARCH);

		// If there is no search history yet, display a textview saying that
		if(histories == null) {
			TextView tv = new TextView(amr);
			tv.setText(R.string.history_no_search_yet);
			lv.addView(tv);
			return;
		}

	
		// Otherwise, for every history we make a button in the listview
		for(int i = histories.length; i > 0; i--) {
			Button b = new Button(amr);
			b.setBackgroundResource(R.layout.rounded_corners);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(8, 10, 8, 4); // left, top, right, bottom
			b.setLayoutParams(lp);

			History h = histories[i-1];
			final String artist = h.getArtist();
			final String title = h.getTitle();
			final int date = h.getDate();

			String format =  amr.getString(R.string.history_one_search) + " %s - %s";
			b.setText(String.format(format, artist, title));

			b.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					Toast.makeText(amr, amr.getString(R.string.history_on)+dateToString(date), Toast.LENGTH_SHORT).show();
					return true;
				}
			});
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent next = new Intent(amr, MainActivity.class);
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
		History[] histories = ha.getSearchHistory(InternalDatabaseHistoryAdapter.SQL_GET_ALL_TRACKS);

		// If there is no track history yet, display a textview saying that
		if(histories == null) {
			TextView tv = new TextView(amr);
			tv.setText(R.string.history_no_tracks_yet);
			lv.addView(tv);
			return;
		}

		
		// Otherwise, for every history we make a button in the listview
		for(int i = histories.length; i > 0; i--) {
			final Button b = new Button(amr);
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

			String format = amr.getString(R.string.history_one_track) + " %s - %s";
			b.setText(String.format(format, artist, title));
			
			b.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					Toast.makeText(amr, amr.getString(R.string.history_on)+dateToString(date), Toast.LENGTH_SHORT).show();
					return true;
				}
			});
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					textBeforeLoading = (String) b.getText();
					buttonLoading = b;
					b.setText(amr.getString(R.string.history_one_track) + amr.getString(R.string.loading));
					new DownloadLastFM(activity).execute(""+DownloadLastFM.operations.dl_history_track_info, mbid);
				}
			});
			lv.addView(b);
			continue;
		}
	}
}
