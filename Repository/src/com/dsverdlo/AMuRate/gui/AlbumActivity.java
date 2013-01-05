package com.dsverdlo.AMuRate.gui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.Album;
import com.dsverdlo.AMuRate.services.MyConnection;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class AlbumActivity extends Activity {
	private AlbumActivity albumActivity;
	private TextView albumTitle, albumArtist;
	private TextView playcount, listeners;
	private ImageView albumImage;
	private LinearLayout tracksLayout;
	private TextView summary;
	
	private MyConnection connection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		
		albumActivity = this;
		
		albumTitle = (TextView) findViewById(R.id.album_title);
		albumArtist = (TextView) findViewById(R.id.album_artist);
		albumImage = (ImageView) findViewById(R.id.album_image);
		playcount = (TextView) findViewById(R.id.album_playcount);
		listeners = (TextView) findViewById(R.id.album_listeners);
		tracksLayout = (LinearLayout) findViewById(R.id.album_tracks);
		summary = (TextView) findViewById(R.id.album_summary);	
		
		connection = new MyConnection();
				
		final Album album = new Album(getIntent().getStringExtra("album"));
		
		albumTitle.setText(album.getAlbumTitle());
		albumArtist.setText(album.getArtistName());
		
		if(album.getImageL().length() > 0) {
			connection.loadImage(album.getImageL(), albumImage);
		} else albumImage.setImageResource(R.drawable.not_available);
		
		playcount.setText("P:" + album.getPlaycount());
		listeners.setText("L:" + album.getListeners());
		
		summary.setText(Html.fromHtml(album.getSummary()));
		
		// Set the tracks in the view TODO: abstract
		try {
			JSONArray tracks = album.getTracks();
			for (int i = 0; i < tracks.length(); i++) {
				JSONObject oneTrack = tracks.getJSONObject(i);
				final String tit = oneTrack.getString("name");
				String mbid = oneTrack.getString("mbid");
				Button bt = new Button(getApplicationContext());
				bt.setBackgroundResource(R.layout.rounded_corners);

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(8, 10, 8, 4); // left, top, right, bottom
				bt.setLayoutParams(lp);
				
				bt.setText("" + (i+1) + ":" + tit);
				bt.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						connection.getFromTitleAndArtist(albumActivity, tit, album.getArtistName());
					}
				});
				tracksLayout.addView(bt);
				
			}
			
		} catch (JSONException je) {
			System.out.println("JSONException in AlbumActivity");
		}
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_album, menu);
		return true;
	}
	
	public void searchResultsTitleAndArtist(String extraString) {
		Intent trackActivity = new Intent(getApplicationContext(), TrackActivity.class);
		trackActivity.putExtra("track", extraString);
		startActivity(trackActivity);
	}

}
