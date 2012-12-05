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
import org.json.JSONException;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyConnection extends Activity implements OnClickListener {

	private EditText artistEdit;
	private Button cancelButton;
	private Button sendGetReqButton;
	private TextView results;
	private TextView questionMark;
	private Button view;
	private JSONObject artist;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_test);

		artistEdit = (EditText) findViewById(R.id.tex);

		sendGetReqButton = (Button) findViewById(R.id.button_submit);
		sendGetReqButton.setOnClickListener(this);

		cancelButton = (Button) findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(this);

		questionMark = (Button) findViewById(R.id.questionmark);
		questionMark.setOnClickListener(this);
		questionMark.setTextColor(Color.LTGRAY);
		questionMark.setText(" ? ");

		view = (Button) findViewById(R.id.view);
		view.setOnClickListener(this);
		view.setVisibility(View.INVISIBLE);
		view.setText("View");

		results = (TextView) findViewById(R.id.results);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_submit : 

			// Get the values given in EditText fields
			String givenArtist = artistEdit.getText().toString();
			System.out.println("Given usernames is :" + givenArtist);

			if(givenArtist == "" || givenArtist == null || givenArtist.length() == 0 ) {
				Toast.makeText(getApplicationContext(), "Please enter an artist", Toast.LENGTH_LONG).show();
			} else {
				results.setText("Loading...");
				view.setVisibility(Button.INVISIBLE);
				connectWithHttpGet(givenArtist);	
			}
			break;

		case R.id.button_cancel :
			results.setText("");
			view.setVisibility(Button.INVISIBLE);
			artistEdit.setText("");

			break;

		case R.id.questionmark :
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Info");
			alertDialog.setMessage("Please enter an artist in the search field and then press SUBMIT.");
			// Setting OK Button
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Write your code here to execute after dialog closed
				}
			});
			alertDialog.show();
			break;

		case R.id.view : 

			Intent newpage = new Intent(this, ArtistActivity.class);
			newpage.putExtra("artist", artist.toString());
			startActivity(newpage);
			break;


		case R.id.tex : 
			// String get = artistEdit.getText().toString();
			break;
		}


	}


	private void connectWithHttpGet(final String givenArtist) {

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
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + givenArtist.replace(" ", "%20") + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json&autocorrect=1&user=dsverdlo");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");

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

				TextView results = (TextView) findViewById(R.id.results);
				try {
					JSONObject records = new JSONObject(result);

					if(result.substring(2, 7).equals("error")) {
						results.setText("Artist not found");
					} else {
						artist = records.getJSONObject("artist");
						JSONObject recor = artist.getJSONObject("bio");
						// Use fromHtml to handle htlm links in string (<a href="...">text</a>)
						String summary = android.text.Html.fromHtml(recor.getString("summary")).toString();

						System.out.println("From bio, pull: ");
						System.out.println(summary);
						results.setText(summary);

						view.setVisibility(View.VISIBLE);
					}
				} catch (JSONException e) {
					Log.d(VIBRATOR_SERVICE, "JSON Exception!");
					e.printStackTrace();
				}		
			}			
		}

		// Initialize the AsyncTask class
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		// Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
		// We are passing the connectWithHttpGet() method arguments to that
		httpGetAsyncTask.execute(givenArtist); 

	}
}