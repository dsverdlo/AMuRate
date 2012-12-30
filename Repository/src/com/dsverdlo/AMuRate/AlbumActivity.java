package com.dsverdlo.AMuRate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		
		albumTitle.setText(album.albumTitle);
		albumArtist.setText(album.artistName);
		
		if(album.imageL.length() > 0) {
			connection.loadImage(album.imageL, albumImage);
		} else albumImage.setImageResource(R.drawable.not_available);
		
		playcount.setText("P:" + album.playcount);
		listeners.setText("L:" + album.listeners);
		
		summary.setText(album.summary);
		
		// Set the tracks in the view TODO: abstract
		try {
			JSONArray tracks = album.tracks;
			for (int i = 0; i < tracks.length(); i++) {
				JSONObject oneTrack = tracks.getJSONObject(i);
				final String tit = oneTrack.getString("name");
				String mbid = oneTrack.getString("mbid");
				Button bt = new Button(getApplicationContext());
				bt.setText("" + (i+1) + ":" + tit);
				bt.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						connection.getFromTitleAndArtist(albumActivity, tit, album.artistName);
					}
				});
				tracksLayout.addView(bt);
				
			}
			
		} catch (JSONException je) {
			System.out.println("JSONException in AlbumActivity");
		}
		
		TextView tv = new TextView(getApplicationContext());
		tv.setTextSize(20);
		tv.setText("Demonstraties, degustaties, sell-out?Bij Think and Go organiseren we elke week demonstraties/animaties voor merken van zowel elektronische producten voor de gewone consument als voor FMCG producten. Tijdens het seizoen zijn we eveneens aanwezig in de speelgoed - of de doe-het-zelf afdelingen.Opdat de demonstraties in volmaakte harmonie zouden verlopen met de verdeling hebben we controleprocedures en procedures ter evaluatie van de demonstrateurs op poten gezet in samenwerking met de winkels waar wij het vaakst aanwezig zijn. Wij informeren op voorhand elke winkel omtrent de aanwezigheid van een demonstrateur en wij nemen, na de animatie, telefonisch contact op met de afdelingsverantwoordelijke om de resultaten van de actie te kunnen evalueren. Deze stap maakt het mogelijk zelf te anticiperen op eventuele problemen (zonder voorraad zitten...) en de tevredenheid van de retail naar de klanten toe en naar onszelf toe te verhogen");
		//tracks.addView(tv);
		
		
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
