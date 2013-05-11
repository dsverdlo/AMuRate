package com.dsverdlo.AMuRate.gui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.Album;
import com.dsverdlo.AMuRate.objects.Artist;
import com.dsverdlo.AMuRate.objects.Track;
import com.dsverdlo.AMuRate.services.MyConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ArtistActivity extends Activity {
	private LinearLayout artistStats;
	private ImageView artistBigPic;
	private TextView longScroll;
	private TextView tracksTitle;
	private LinearLayout tracksScroll;
	private TextView albumsTitle;
	private LinearLayout albumsScroll;
	
	private TextView tracksMessaging;
	private TextView albumsMessaging;
	
	private Context context;
	private MyConnection conn;
	private ArtistActivity thisActivity;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_activity);

		conn = new MyConnection();		
		this.context = getApplicationContext();
		thisActivity = this;
		
		artistStats = (LinearLayout)findViewById(R.id.artistStats);
		artistBigPic = (ImageView)findViewById(R.id.artistBigPic);
		longScroll = (TextView)findViewById(R.id.long_scroll);
		tracksTitle = (TextView)findViewById(R.id.row1col1);
		tracksScroll = (LinearLayout)findViewById(R.id.row2col1);
		albumsTitle = (TextView)findViewById(R.id.row1col2);
		albumsScroll = (LinearLayout)findViewById(R.id.row2col2);
		
		tracksMessaging = new TextView(getApplicationContext());
		albumsMessaging = new TextView(getApplicationContext());
		
		tracksTitle.setText("TRACKS");
		albumsTitle.setText("ALBUMS");
				
		tracksMessaging.setText("Loading tracks...");
		tracksScroll.addView(tracksMessaging);

		albumsMessaging.setText("Loading albums...");
		albumsScroll.addView(albumsMessaging);

		// Load the artist 
		final Artist artist = new Artist();
		artist.loadFromInfo(getIntent().getStringExtra("artist"));
		System.out.println("### ArtistActivity verb 1/3");
		//albumsLoading.setText("Heren, met de zomer in aantocht wordt het tijd om wat aan de gezondheid te doen. Dus zoek alvast je best voorziene vrouwelijke collega op en vraag haar om tien minuutjes voor jouw te komen zitten.Volgens de New England Journal of Medecine is er effectief een verbetering van de gezondheid bij mannen die dagelijks tien minuten naar de borsten van goed voorziene dames kijken. Die tien minuten geven hetzelfde resultaat als dertig minuten aerobics, dat zegt tenminste Karen Weatherby, een experte op het vlak van veroudering. Daarvoor voerde ze een onderzoek van maar liefst vijf jaar met een groep mannen die elke dag moesten kijken naar vrouwenborsten. Alles voor de wetenschap! " + artist.getListeners());

		System.out.println("### ArtistActivity verb 2/3");
		
		if(artist.getImage("xl").length() > 0) {
			System.out.println("Loading image for artist: " + artist.getImage("xl"));
			conn.loadImage(artist.getImage("xl"), artistBigPic); 
		} else {
			artistBigPic.setImageResource(R.drawable.not_available);
		}

		System.out.println("### ArtistActivity verb 3/3: length=" + artist.getSummary().length());
		
		
		longScroll.setText(Html.fromHtml(artist.getSummary()));
		longScroll.setBackgroundColor(Color.DKGRAY);
		
		TextView textViewArtistName = new TextView(this);
		textViewArtistName.setTextSize(25);
		textViewArtistName.setText(artist.getArtistName());
		
		TextView textViewUrlOut = new TextView(this);
		textViewUrlOut.setText(Html.fromHtml(artist.getUrlOut()));
		
		artistStats.addView(textViewArtistName);
		artistStats.addView(textViewUrlOut);
		
		conn.loadTracks(artist.getMbid(), thisActivity);
		conn.loadAlbums(artist.getMbid(), thisActivity);
	}
	
	
	
	public void loadTracks(String loadedTracks) {
		try {
			JSONObject JSONloaded = new JSONObject(loadedTracks);
			if(!JSONloaded.has("toptracks")) {
				tracksMessaging.setText("Unable to fetch tracks..."); // TODO child
				return;
			}

			tracksScroll.removeAllViews();
			
			JSONArray JSONtracks = JSONloaded.getJSONObject("toptracks").getJSONArray("track");
		
		for(int i = 0; i < JSONtracks.length(); i++ ) {
			final JSONObject JSONtrack = JSONtracks.getJSONObject(i);
			final Track track = new Track();
			track.loadFromInfo(JSONtrack);
			
			final Button buttonTrack = new Button(getApplicationContext());
			buttonTrack.setText(track.getTitle());
			buttonTrack.setBackgroundResource(R.layout.rounded_corners);
			buttonTrack.setTextSize(13);
			//buttonTrack.setPadding(10, 0, 10, 0);

 
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, 
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 8, 3, 4); // left, top, right, bottom
			buttonTrack.setLayoutParams(lp);
			
			buttonTrack.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					System.out.println("Someone clicked a track in ArtistActivity!");
					buttonTrack.setText("Loading...");
					/*Intent nextPage = new Intent(getApplicationContext(), TrackActivity.class);
					nextPage.putExtra("jsontrack", JSONtrack.toString());
					startActivity(nextPage); */

					conn.loadFromArtistActivity("loadTrack", track.getMBID(), thisActivity);
				}
			});
			
			// Only if MBID exists we add it to the list
			if(track.getMBID().length() > 0) tracksScroll.addView(buttonTrack);
			
		}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadAlbums(String loadedAlbums) {
		try {
			JSONObject JSONloaded = new JSONObject(loadedAlbums);
			if(!JSONloaded.has("topalbums")) {
				albumsMessaging.setText("Unable to fetch albums..."); // TODO child
				return;
			}

			albumsScroll.removeAllViews();
			
			JSONArray JSONalbums = JSONloaded.getJSONObject("topalbums").getJSONArray("album");
		
		for(int i = 0; i < JSONalbums.length(); i++ ) {
			final JSONObject JSONalbum = JSONalbums.getJSONObject(i);
			final Album album = new Album(JSONalbum);
			
			final Button buttonAlbum = new Button(getApplicationContext());
			buttonAlbum.setText(album.getAlbumTitle());
			buttonAlbum.setBackgroundResource(R.layout.rounded_corners);
			buttonAlbum.setTextSize(13);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, 
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(3, 8, 0, 4); // left, top, right, bottom
			buttonAlbum.setLayoutParams(lp);
			
			buttonAlbum.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					System.out.println("Someone clicked an album in ArtistActivity!");
					buttonAlbum.setText("Loading...");
					/*Intent nextPage = new Intent(getApplicationContext(), AlbumActivity.class);
					nextPage.putExtra("jsonalbum", JSONalbum.toString());
					startActivity(nextPage);*/
					conn.loadFromArtistActivity("loadAlbum", album.getMbid(), thisActivity);
				}
			});
			
			// Only if album has an MBID we show it
			if(album.getMbid().length() > 0) albumsScroll.addView(buttonAlbum);
			
		}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void onTrackLoaded(String track) {
		Intent trackIntent = new Intent(getApplicationContext(), TrackActivity.class);
		trackIntent.putExtra("track", track);
		System.out.println("Starting TrackActivity intent");
		startActivity(trackIntent);
	}
	
	public void onAlbumLoaded(String album) {
		Intent albumIntent = new Intent(getApplicationContext(), AlbumActivity.class);
		albumIntent.putExtra("album", album);
		System.out.println("Starting AlbumActivity intent");
		startActivity(albumIntent);
	}
	
}




