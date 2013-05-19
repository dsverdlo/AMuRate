package com.dsverdlo.AMuRate.gui;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.History;
import com.dsverdlo.AMuRate.objects.HistoryAdapter;
import com.dsverdlo.AMuRate.services.MyConnection;

import android.R.color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class HistoryActivity extends Activity {
	private HistoryAdapter ha;
	private HistoryActivity activity;
	private Context context;
	private String textBeforeLoading;
	private Button buttonLoading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		activity = this;
		context = getApplicationContext();
		ha = new HistoryAdapter(context);
		
		// Load the layout 
		LinearLayout lv = (LinearLayout)findViewById(R.id.history_linlay);
		Button buttonBack = (Button)findViewById(R.id.history_back);
		TextView textTitle = (TextView)findViewById(R.id.history_title);
		Button buttonDelete = (Button)findViewById(R.id.history_button_remove);
		
		buttonDelete.setText(" Delete history ");
		buttonDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(context, "todo", Toast.LENGTH_SHORT).show();
			}
		});
		
		
		buttonBack.setText(" Back ");
		buttonBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
				Intent next = new Intent(context, MainActivity.class);
				startActivity(next);
			}
		});
		textTitle.setText("~History~");
		
		// Start loading all the histories
		History[] histories = ha.getSearchHistory(HistoryAdapter.SQL_GET_ALL);
		
		// If there is no history yet, display a textview saying that
		if(histories == null) {
			TextView tv = new TextView(context);
			tv.setText("No history yet.");
			lv.addView(tv);
			return;
		}
		
		System.out.println("HistoryActivity says: " + histories.length);
		// Otherwise, for every history we make a button in the listview
		for(int i = 0; i < histories.length; i++) {
			Button b = new Button(context);
			b.setBackgroundResource(R.layout.rounded_corners);
			b.setTextColor(color.white);
			
			History h = histories[i];
			
			if(h.getKey() == HistoryAdapter.KEY_SEARCH) { 
				lv.addView(makeButtonSearch(b, h));
				continue;
			}
			
			if(h.getKey() == HistoryAdapter.KEY_TRACK) {
				lv.addView(makeButtonTrack(b, h));
				continue;
			}		
		}
	}

	private Button makeButtonSearch(Button b, History h) {
		final String artist = h.getArtist();
		final String title = h.getTitle();
		int date = h.getDate();
		
		String format = "Search: %s - %s (%s)";
		b.setText(String.format(format, artist, title, dateToString(date)));
		
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent next = new Intent(context, MainActivity.class);
				next.putExtra("title", title);
				next.putExtra("artist", artist);
				finish();
				startActivity(next);
			}
		});
		return b;
	}
	
	private Button makeButtonTrack(final Button b, History h) {
		String artist = h.getArtist();
		final String mbid = h.getMbid();
		String title = h.getTitle();
		int date = h.getDate();

		String format = "Search: %s - %s (%s)";
		b.setText(String.format(format, artist, title, dateToString(date)));
		
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				MyConnection conn = new MyConnection();
				textBeforeLoading = (String) b.getText();
				buttonLoading = b;
				b.setText("Search: loading...");
				conn.loadTrackActivity(activity, mbid);
			}
		});
		
		return b;
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

	private String dateToString(int seconds) {
		int milliseconds = seconds * 1000;
		Date d = new Date(milliseconds);
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm dd/mm/yyyy");
		return sdf.format(d);
	}

	public void onDoneLoadingTrackinfo(String trackInfo) {
		buttonLoading.setText(textBeforeLoading);
		
		Intent nextPage = new Intent(context, TrackActivity.class);
		nextPage.putExtra("track", trackInfo);
		startActivity(nextPage);
	}
}
