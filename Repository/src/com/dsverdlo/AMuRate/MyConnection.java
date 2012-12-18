package com.dsverdlo.AMuRate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.AsyncTask;

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
				System.out.println("Debug1");
				String mbid = params[0];
				System.out.println("Debug2");
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=track.getinfo&mbid=" + mbid + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");
				System.out.println("Debug3");
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
		System.out.println("Debug0.1 (" + mbid + ")");
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		System.out.println("Debug0.2");
		httpGetAsyncTask.execute(mbid); 
	}
}