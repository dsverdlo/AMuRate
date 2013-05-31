package com.dsverdlo.AMuRate.gui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.AMuRate;
import com.dsverdlo.AMuRate.objects.Album;
import com.dsverdlo.AMuRate.objects.Artist;
import com.dsverdlo.AMuRate.objects.Track;
import com.dsverdlo.AMuRate.services.DownloadImageTask;
import com.dsverdlo.AMuRate.services.DownloadLastFM;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This is the screen of one Artist.
 * It displays a picture, bio information, top tracks, top albums
 * 
 * TODO: Add bandmembers if they exist
 * (sometimes in a group they are supplied)
 * 
 * @author David Sverdlov
 *
 */
public class ArtistActivity extends BlankActivity {
	private AMuRate amr;

	private LinearLayout mainLayout;
	private LinearLayout artistStats;
	private ImageView artistBigPic;
	private TextView longScroll;
	private TextView tracksTitle;
	private LinearLayout tracksScroll;
	private TextView albumsTitle;
	private LinearLayout albumsScroll;
	
	private TextView tracksMessaging;
	private TextView albumsMessaging;
	
	private ArtistActivity thisActivity;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_activity);

		mainLayout = (LinearLayout)findViewById(R.id.artistMainLayout);
		mainLayout.setBackgroundResource(R.drawable.new62);
		
		this.amr = (AMuRate) getApplicationContext();
		thisActivity = this;
		
		artistStats = (LinearLayout)findViewById(R.id.artistStats);
		artistBigPic = (ImageView)findViewById(R.id.artistBigPic);
		longScroll = (TextView)findViewById(R.id.long_scroll);
		tracksTitle = (TextView)findViewById(R.id.row1col1);
		tracksScroll = (LinearLayout)findViewById(R.id.row2col1);
		albumsTitle = (TextView)findViewById(R.id.row1col2);
		albumsScroll = (LinearLayout)findViewById(R.id.row2col2);
		
		tracksMessaging = new TextView(amr);
		albumsMessaging = new TextView(amr);
		
		tracksTitle.setText(R.string.artist_TRACKS);
		albumsTitle.setText(R.string.artist_ALBUMS);
				
		// TODO: add animation
		tracksMessaging.setText(R.string.artist_loading_tracks);
		tracksScroll.addView(tracksMessaging);

		albumsMessaging.setText(R.string.artist_loading_albums);
		albumsScroll.addView(albumsMessaging);

		// Load the artist 
		final Artist artist = new Artist();
		artist.loadFromInfo(getIntent().getStringExtra("artist"));
		
		if(artist.getImage("xl").length() > 0) {
			System.out.println("Loading image for artist: " + artist.getImage("xl"));
			DownloadImageTask download = new DownloadImageTask(artistBigPic);
			download.execute(artist.getImage("xl"));
			
		} else {
			artistBigPic.setImageResource(R.drawable.not_available);
		}
		
		longScroll.setText(Html.fromHtml(artist.getSummary()));
		longScroll.setMovementMethod(LinkMovementMethod.getInstance());
		longScroll.setMaxHeight(160);
		
		Button buttonBack = new Button(amr);
		buttonBack.setBackgroundResource(R.layout.rounded_corners);
		buttonBack.setText(" X ");
		buttonBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
				Intent next = new Intent(amr,MainActivity.class);
				next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(next);
			}
		});
//		buttonBack.setMaxHeight(30);
//		buttonBack.setMaxWidth(30);
		RelativeLayout backLayout = new RelativeLayout(amr);
		RelativeLayout.LayoutParams backParams = 
                new RelativeLayout.LayoutParams(60, 50);
		backParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		buttonBack.setLayoutParams(backParams);
		backLayout.addView(buttonBack);
		buttonBack.setTextSize(13);
		
		TextView textViewArtistName = new TextView(this);
		textViewArtistName.setTextSize(25);
		textViewArtistName.setText(artist.getArtistName());
		
		TextView textViewUrlOut = new TextView(this);
		textViewUrlOut.setText(Html.fromHtml(artist.getUrlOut()));
		textViewUrlOut.setMovementMethod(LinkMovementMethod.getInstance());
		
		artistStats.addView(backLayout);
		artistStats.addView(textViewArtistName);
		artistStats.addView(textViewUrlOut);
		
		new DownloadLastFM(thisActivity).execute(""+DownloadLastFM.operations.dl_artist_load_tracks, artist.getMbid());
		new DownloadLastFM(thisActivity).execute(""+DownloadLastFM.operations.dl_artist_load_albums, artist.getMbid());
	}
	
	
	
	public void loadTracks(String loadedTracks) {
		try {
			JSONObject JSONloaded = new JSONObject(loadedTracks);
			if(!JSONloaded.has("toptracks")) {
				tracksMessaging.setText(R.string.artist_loading_tracks_failed); // TODO child
				return;
			}

			tracksScroll.removeAllViews();
			
			JSONArray JSONtracks = JSONloaded.getJSONObject("toptracks").getJSONArray("track");
		
		for(int i = 0; i < JSONtracks.length(); i++ ) {
			final JSONObject JSONtrack = JSONtracks.getJSONObject(i);
			final Track track = new Track();
			track.loadFromInfo(JSONtrack);
			
			final Button buttonTrack = new Button(amr);
			buttonTrack.setText(track.getTitle());
			buttonTrack.setBackgroundResource(R.layout.rounded_corners);
			buttonTrack.setTextSize(13);
 
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, 
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 8, 3, 4); // left, top, right, bottom
			buttonTrack.setLayoutParams(lp);
			
			buttonTrack.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					System.out.println("Someone clicked a track in ArtistActivity!");
					buttonTrack.setText(R.string.loading);

					new DownloadLastFM(thisActivity).execute(""+DownloadLastFM.operations.dl_artist_track_info, track.getMBID());
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
				albumsMessaging.setText(R.string.artist_loading_albums_failed); // TODO child
				return;
			}

			albumsScroll.removeAllViews();
			
			JSONArray JSONalbums = JSONloaded.getJSONObject("topalbums").getJSONArray("album");
		
		for(int i = 0; i < JSONalbums.length(); i++ ) {
			final JSONObject JSONalbum = JSONalbums.getJSONObject(i);
			final Album album = new Album(JSONalbum);
			
			final Button buttonAlbum = new Button(amr);
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
					buttonAlbum.setText(R.string.loading);
					new DownloadLastFM(thisActivity).execute(""+DownloadLastFM.operations.dl_artist_album_info, album.getMbid());
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
		Intent trackIntent = new Intent(amr, TrackActivity.class);
		trackIntent.putExtra("track", track);
		System.out.println("Starting TrackActivity intent");
		startActivity(trackIntent);
	}
	
	public void onAlbumLoaded(String album) {
		Intent albumIntent = new Intent(amr, AlbumActivity.class);
		albumIntent.putExtra("album", album);
		System.out.println("Starting AlbumActivity intent");
		startActivity(albumIntent);
	}
	
}




