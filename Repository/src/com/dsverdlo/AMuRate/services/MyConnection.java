package com.dsverdlo.AMuRate.services;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.dsverdlo.AMuRate.gui.AlbumActivity;
import com.dsverdlo.AMuRate.gui.ArtistActivity;
import com.dsverdlo.AMuRate.gui.MainActivity;
import com.dsverdlo.AMuRate.gui.SearchArtistActivity;
import com.dsverdlo.AMuRate.gui.SearchResultsActivity;
import com.dsverdlo.AMuRate.gui.TrackActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

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
public class MyConnection  {

	public void getFromTitle(final MainActivity main, final String givenTitle) {

		// Connect with a server is a time consuming process.
		//Therefore we use AsyncTask to handle it
		// From the three generic types;
		//First type relate with the argument send in execute()
		//Second type relate with onProgressUpdate method which I haven't use in this code
		//Third type relate with the return type of the doInBackground method, which also the input type of the onPostExecute method
		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{

			@Override
			protected String doInBackground(String... params) {

				// As you can see, doInBackground has taken an Array of Strings as the argument
				//We need to specifically get the givenArtist and givenPassword
				String paramArtist = params[0];
				System.out.println("paramArtist is :" + paramArtist);

				// Create an intermediate to connect with the Internet
				HttpClient httpClient = new DefaultHttpClient();

				// Sending a GET request to the web page that we want
				// Because of we are sending a GET request, we have to pass the values through the URL
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=track.search&track=" + givenTitle.replace(" ", "%20") + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");

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
				main.searchResultsTitle(result);

			}			
		}

		// Initialize the AsyncTask class
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		// Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
		// We are passing the connectWithHttpGet() method arguments to that
		httpGetAsyncTask.execute(givenTitle); 
	}


	//// COPPPYYYYYYYY TODO: lol


	public void getFromArtist(final MainActivity main, final String givenArtist) {

		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String... params) {
				String paramArtist = params[0];
				System.out.println("paramArtist is :" + paramArtist);

				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=artist.search&artist=" + givenArtist.replace(" ", "%20") + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json&autocorrect=1&user=dsverdlo");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");

				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null){
						stringBuilder.append(bufferedStrChunk);
					}
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
				main.searchResultsArtist(result);

			}			
		}

		// Initialize the AsyncTask class
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		// Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
		// We are passing the connectWithHttpGet() method arguments to that
		httpGetAsyncTask.execute(givenArtist); 
	}

	//// COPY TODO: trol


	public void getFromTitleAndArtist(final MainActivity main, final String givenTitle, final String givenArtist) {

		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String... params) {
				String paramArtist = params[0];
				System.out.println("paramArtist is :" + paramArtist);

				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=track.search&track=" + givenTitle.replace(" ", "%20") + "&artist=" + givenArtist.replace(" ",  "%20") + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");

				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null){
						stringBuilder.append(bufferedStrChunk);
					}
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

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result); 
				System.out.println("Sending httpget result back :-)");
				main.searchResultsTitleAndArtist(result);
			}			
		}

		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(givenArtist); 
	}


	// TODO: copy lolno!

	public void getFromMBID(final SearchResultsActivity searchActivity, final String mbid) {

		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String... params) {
				String mbid = params[0];
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=track.getinfo&mbid=" + mbid + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");


				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null){
						stringBuilder.append(bufferedStrChunk);
					}
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

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result); 
				System.out.println("Connection sending back!");
				searchActivity.onPostExecute(result);
			}			
		}
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(mbid); 
	}
	
	// TODO yeah yeah
	
	public void loadImage(final String url, final ImageView iv) {

		class HttpGetAsyncTask extends AsyncTask<String, Void, Bitmap>{
			@Override
			protected Bitmap doInBackground(String... params) {
				try {
					URL url = new URL(params[0]);
					Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					return bmp;
				} catch (Exception e) {
					System.out.println("Exception in MyConnection(loadImage):");
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap bmp) {
				super.onPostExecute(bmp); 
				iv.setImageBitmap(bmp);
				System.out.println("Done loading image!");
			}			
		}
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(url); 
	}
	
	// TODO yeah yeah
	
	public void loadImage(final String url, final ImageView iv, final AnimationView load) {

		class HttpGetAsyncTask extends AsyncTask<String, Void, Bitmap>{
			@Override
			protected Bitmap doInBackground(String... params) {
				try {
					URL url = new URL(params[0]);
					Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					return bmp;
				} catch (Exception e) {
					System.out.println("Exception in MyConnection(loadImage):");
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap bmp) {
				super.onPostExecute(bmp);
				load.setVisibility(View.GONE);
				iv.setVisibility(View.VISIBLE);
				iv.setImageBitmap(bmp);
				System.out.println("Done loading image!");
			}			
		}
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(url); 
	}
	// TODO: LOLOLOL
	
	public void getFromAlbum(final TrackActivity ta, final String mbid) {

		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String... params) {

				String mbid = params[0];
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=album.getinfo&mbid=" + mbid + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");
				
				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null){
						stringBuilder.append(bufferedStrChunk);
					}
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

			@Override
			protected void onPostExecute(String album) {
				super.onPostExecute(album); 
				ta.onDoneLoadingAlbum(album);
			}			
		}
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(mbid); 
	}
	
	
	
	// TODO: no
	// TODO: no
	public void getFromTitleAndArtist(final AlbumActivity main, final String givenTitle, final String givenArtist) {

		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String... params) {
				String paramArtist = params[0];
				System.out.println("paramArtist is :" + givenArtist);

				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=track.getinfo&track=" + givenTitle.replace(" ", "%20") + "&artist=" + givenArtist.replace(" ",  "%20") + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");

				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null){
						stringBuilder.append(bufferedStrChunk);
					}
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

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result); 
				main.searchResultsTitleAndArtist(result);
			}			
		}

		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(givenArtist); 
	}
	
//	// LOAD PREVIEW
//	public void getPreviewFromId(final TrackActivity trackActivity, final int id, final String fileName) {		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
//		@Override
//		protected String doInBackground(String...params) {
//			String out = Environment.getExternalStorageDirectory().getAbsolutePath() + "/h_" + fileName + ".mp3";
//			try { 
//				System.out.println("URLTest: init");
//				URL url = new URL("http://play.last.fm/preview/" + id + ".mp3"); //you can write here any link
//				File file = new File(out);
//
//				/* Open a connection to that URL. */
//				URLConnection ucon = url.openConnection();
//
//				System.out.println("URLTest: opened connection");
//				/*
//				 * Define InputStreams to read from the URLConnection.
//				 */
//				InputStream is = ucon.getInputStream();
//				BufferedInputStream bis = new BufferedInputStream(is);
//
//				System.out.println("URLTest: bis created");
//				/*
//				 * Read bytes to the Buffer until there is nothing more to read(-1).
//				 */
//				ByteArrayBuffer baf = new ByteArrayBuffer(50);
//				int current = 0;
//				while ((current = bis.read()) != -1) {
//					baf.write((byte) current);
//				}
//
//				System.out.println("URLTest: loop done");
//
//				/* Convert the Bytes read to a String. */
//				FileOutputStream fos = new FileOutputStream(file);
//				fos.write(baf.toByteArray());
//				fos.close();
//				bis.close();
//				baf.close();
//
//				System.out.println("URLTest: finish up");
//				trackActivity.previewAvailable(out);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return out;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result); 
//			trackActivity.previewAvailable(result);
//		}
//	}
//	HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
//	httpGetAsyncTask.execute(); 
//	}


	public void getArtistInfo(String mbid, final SearchArtistActivity destination) {
		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String... params) {
				String mbid = params[0];
				System.out.println("Downloading artist info on:" + mbid);

				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&mbid=" + mbid + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");

				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null){
						stringBuilder.append(bufferedStrChunk);
					}
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

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				destination.onRetrievedArtistInfo(result);
			}			
		}

		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(mbid); 
	
	}


	public void loadTracks(String mbid, final ArtistActivity activity) {
		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String... params) {
				String mbid = params[0];
				System.out.println("Downloading tracks from:" + mbid);

				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&mbid=" + mbid + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");

				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null){
						stringBuilder.append(bufferedStrChunk);
					}
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

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				activity.loadTracks(result);
			}			
		}

		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(mbid); 
	
	}
	

	public void loadAlbums(String mbid, final ArtistActivity activity) {
		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String... params) {
				String mbid = params[0];
				System.out.println("Downloading albums from:" + mbid);

				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&mbid=" + mbid + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");

				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null){
						stringBuilder.append(bufferedStrChunk);
					}
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

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				activity.loadAlbums(result);
			}			
		}

		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(mbid); 
	
	}


	public void loadFromArtistActivity(final String load, String mbid, final ArtistActivity activity) {
		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String... params) {
				String mbid = params[0];
				System.out.println("Downloading: " + load + " for ArtistActivity:" + mbid);

				String httpGetUrl = "";
				if(load.equals("loadAlbum")) {
					httpGetUrl = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&mbid=" + mbid + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json";
				} else if(load.equals("loadTrack")) {
					httpGetUrl = "http://ws.audioscrobbler.com/2.0/?method=track.getinfo&mbid=" + mbid + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json";
				}
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(httpGetUrl);//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");

				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null){
						stringBuilder.append(bufferedStrChunk);
					}
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

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if(load.equals("loadAlbum")) {
					activity.onAlbumLoaded(result);
				} else if(load.equals("loadTrack")) {
					activity.onTrackLoaded(result);
				}
			}			
		}

		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(mbid); 
	}

	// TODO Auto-generated method stub
	public void getFromArtistMBID(final TrackActivity trackActivity, final String artistMBID) {


		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String... params) {

				String mbid = params[0];
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&mbid=" + artistMBID + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");
				
				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null){
						stringBuilder.append(bufferedStrChunk);
					}
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

			@Override
			protected void onPostExecute(String artistInfo) {
				super.onPostExecute(artistInfo); 
				trackActivity.onDoneLoadingArtist(artistInfo);
			}			
		}
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(artistMBID); 
	}

}
/*
		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			@Override
			protected String doInBackground(String...params) {
				String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/g_" + fileName + ".mp3";
				System.out.println("paramId is :" + id + "\nlocation: " + outputFile);
				try { 
					//Get inputstream
					URLConnection conn = new URL("http://play.last.fm/preview/" + id + ".mp3").openConnection();
					InputStream is = conn.getInputStream();
					
					//Get outputstream
					System.out.println("LOAD: 1");
					File myFile = new File(outputFile);
					System.out.println("LOAD: 2");
					if(myFile.exists()) return outputFile; 
					myFile.createNewFile();
					System.out.println("LOAD: 3");
					FileOutputStream outstream = new FileOutputStream(myFile);
					System.out.println("LOAD: 4");
					
					//Context context = trackActivity.getApplicationContext();
					//FileOutputStream outstream = 
					//		context.openFileOutput(outputFile, context.MODE_WORLD_WRITEABLE);
					OutputStreamWriter myOutWriter = new OutputStreamWriter(outstream);
					
					byte[] buffer = new byte[2];
					int len;
					while ((len = is.read(buffer)) > 0) {
						//myOutWriter.append(buffer, 0, len);
						String s = new String(buffer);
						myOutWriter.append(s); 
						System.out.println("Downloaded and written " + len + " bytes");
					}
					is.close();
					myOutWriter.close();
					outstream.close(); 

				} catch (MalformedURLException mURLe) {
					System.out.println("Malformed URL Exception in MyConnection(getPreviewFromId)");
					mURLe.printStackTrace();
				} catch (IOException e) {
					System.out.println("IOException in MyConnection(getPReviewFromId)");
					e.printStackTrace();
				}
				return outputFile;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result); 
				trackActivity.previewAvailable(result);
			}
		}
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(); 
	}*/

