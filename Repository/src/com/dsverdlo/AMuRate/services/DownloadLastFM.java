package com.dsverdlo.AMuRate.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.dsverdlo.AMuRate.gui.AlbumActivity;
import com.dsverdlo.AMuRate.gui.ArtistActivity;
import com.dsverdlo.AMuRate.gui.HistoryActivity;
import com.dsverdlo.AMuRate.gui.MainActivity;
import com.dsverdlo.AMuRate.gui.SearchArtistActivity;
import com.dsverdlo.AMuRate.gui.SearchResultsActivity;
import com.dsverdlo.AMuRate.gui.TrackActivity;
import android.os.AsyncTask;

/**
 * Handles all the internet/connection requests. 
 * These tasks must extend AsyncTask so they don't run
 * on the main thread.
 * 
 * Their structure is always more or less the same, so:
 * 
 * TODO: Split or merge all these aynctasks
 * (hence all the TODO:'s belowhand)
 * 
 * @author David Sverdlov
 *
 */

public class DownloadLastFM extends AsyncTask<String, Void, String> {
//	public static final int dl_main_search_title = 0;
//	public static final int dl_main_search_artist = 1;
//	public static final int dl_main_search_title_artist = 2;
//	public static final int dl_artist_load_tracks = 3;
//	public static final int dl_artist_load_albums = 4;
//	public static final int dl_track_album_info = 5;
//	public static final int dl_track_artist_info = 6;
//	public static final int dl_album_track_info = 7;
//	public static final int dl_history_track_info = 8;
//	public static final int dl_search_results_track = 9;
//	public static final int dl_search_artist_artist = 10;
	public enum operations { dl_main_search_title, dl_main_search_artist, dl_main_search_title_artist,
		dl_artist_load_tracks, dl_artist_load_albums, dl_artist_track_info, 
		dl_artist_album_info, dl_track_album_info,
		dl_track_artist_info, dl_album_track_info, dl_history_track_info,
		dl_search_results_track, dl_search_artist_artist };
	
	private String operation;
	
	private static String HOST = "http://ws.audioscrobbler.com/2.0/?method=";
	private static String FOOTER = "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json";

	private TrackActivity act_track;
	private MainActivity act_main;
	private AlbumActivity act_album;
	private ArtistActivity act_artist;
	private SearchArtistActivity act_search_artist;
	private SearchResultsActivity act_search_results;
	private HistoryActivity act_history;
	
	public DownloadLastFM(MainActivity main) { this.act_main = main; }
	public DownloadLastFM(SearchArtistActivity search) { this.act_search_artist = search; }
	public DownloadLastFM(TrackActivity ta) { this.act_track = ta; }
	public DownloadLastFM(SearchResultsActivity searchActivity) { this.act_search_results = searchActivity; }
	public DownloadLastFM( ArtistActivity activity) { this.act_artist = activity; }
	public DownloadLastFM(final AlbumActivity activity) { this.act_album =  activity; }
	public DownloadLastFM(HistoryActivity activity) { this.act_history = activity; }

	@Override
	protected String doInBackground(String... params) {

		// As you can see, doInBackground has taken an Array of Strings as the argument
		//We need to specifically get the givenArtist and givenPassword
		operation = params[0];
		System.out.println("Operating:" + operation);

		// Create an intermediate to connect with the Internet
		HttpClient httpClient = new DefaultHttpClient();

		String operation_url = "";
		// Sending a GET request to the web page that we want
		// Because of we are sending a GET request, we have to pass the values through the URL
		switch(operations.valueOf(operation)) {
		case dl_main_search_title: 
			operation_url += "track.search" + "&track=" + params[1].replace(" ", "%20");
			break;
		case dl_main_search_artist: 
			operation_url += "artist.search" + "&artist=" + params[1].replace(" ", "%20");
			break;
		case dl_main_search_title_artist: 
			operation_url += "track.search" + "&track=" + params[1].replace(" ", "%20") + "&artist=" + params[2].replace(" ",  "%20");
			break;
		case dl_artist_load_tracks:
			operation_url += "artist.gettoptracks" + "&mbid=" + params[1];
			break;
		case dl_artist_load_albums: 
			operation_url += "artist.gettopalbums" + "&mbid=" + params[1];
			break;
		case dl_artist_album_info:
			operation_url += "album.getinfo" + "&mbid=" + params[1];
			break;
		case dl_artist_track_info:
			operation_url += "track.getinfo" + "&mbid=" + params[1];
			break;
		case dl_track_album_info:
			operation_url += "album.getinfo" + "&mbid=" + params[1];
			break;
		case dl_track_artist_info: 
			operation_url += "artist.getinfo" + "&mbid=" + params[1];
			break;
		case dl_album_track_info: 
			operation_url += "track.getinfo" + "&track=" + params[1].replace(" ", "%20") + "&artist=" + params[2].replace(" ",  "%20");
			break;
		case dl_history_track_info: 
			operation_url += "track.getinfo" + "&mbid=" + params[1];
			break;
		case dl_search_results_track: 
			operation_url += "track.getinfo" + "&mbid=" + params[1]; 
			break;
		case dl_search_artist_artist : 
			operation_url += "artist.getinfo" + "&mbid=" + params[1];
			break;
		default: break; 
		}
		
		HttpGet httpGet = new HttpGet(HOST + operation_url + FOOTER); 

		try {
			// execute(); executes a request using the default context.
			// Then we assign the execution result to HttpResponse
			HttpResponse httpResponse = httpClient.execute(httpGet);
			System.out.println("httpResponse");

			// getEntity() ; obtains the message entity of this response
			// getContent() ; creates a new InputStream object of the entity.
			// Now we need a readable source to read the byte stream that comes as the httpResponse
			InputStream inputStream = httpResponse.getEntity().getContent();

			// We have a byte stream. Next step is to convert it to a Character stream
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

			// Then we have to wraps the existing reader (InputStreamReader) and buffer the input
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			// InputStreamReader contains a buffer of bytes read from the source stream and converts these into characters as needed.
			//The buffer size is 8K
			//Therefore we need a mechanism to append the separately coming chunks in to one String element
			// We have to use a class that can handle modifiable sequence of characters for use in creating String
			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			// There may be so many buffered chunks. We have to go through each and every chunk of characters
			//and assign a each chunk to bufferedStrChunk String variable
			//and append that value one by one to the stringBuilder
			while((bufferedStrChunk = bufferedReader.readLine()) != null){
				stringBuilder.append(bufferedStrChunk);
			}

			// Now we have the whole response as a String value.
			//We return that value then the onPostExecute() can handle the content
			//	System.out.println("Returning value of doInBackground :" + stringBuilder.toString());

			return stringBuilder.toString();

		} catch (ClientProtocolException cpe) {
			System.out.println("Exception generates caz of httpResponse :" + cpe);
			cpe.printStackTrace();
		} catch (IOException ioe) {
			System.out.println("Second exception generates caz of httpResponse :" + ioe);
			ioe.printStackTrace();
		}

		return null;
	}

	// Argument comes for this method according to the return type of the doInBackground() and
	//it is the third generic type of the AsyncTask
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result); 
		switch(operations.valueOf(operation)) {
		case dl_main_search_title: act_main.searchResultsTitle(result);	break;
		case dl_main_search_artist: act_main.searchResultsArtist(result); break;
		case dl_main_search_title_artist: act_main.searchResultsTitleAndArtist(result);	break;
		case dl_artist_load_tracks: act_artist.loadTracks(result); break;
		case dl_artist_load_albums: act_artist.loadAlbums(result); break;
		case dl_artist_track_info: act_artist.onTrackLoaded(result); break;
		case dl_artist_album_info: act_artist.onAlbumLoaded(result); break;
		case dl_track_album_info: act_track.onDoneLoadingAlbum(result);	break;
		case dl_track_artist_info: act_track.onDoneLoadingArtist(result); break;
		case dl_album_track_info: act_album.searchResultsTitleAndArtist(result); break;
		case dl_history_track_info: act_history.onDoneLoadingTrackinfo(result);	break;
		case dl_search_results_track: act_search_results.onPostExecute(result);	break;
		case dl_search_artist_artist: act_search_artist.onRetrievedArtistInfo(result); break;
		default: break; 
		}

	}			


}
