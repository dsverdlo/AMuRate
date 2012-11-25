package com.dsverdlo.bachelorproef;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyConnection extends Activity implements OnClickListener {

	private EditText usernameEditText;
	private Button cancelButton;
	private Button sendGetReqButton;
	private TextView results;
	private TextView questionMark;
	private RatingBar rating;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_test);

        usernameEditText = (EditText) findViewById(R.id.tex);

        sendGetReqButton = (Button) findViewById(R.id.button_submit);
        sendGetReqButton.setOnClickListener(this);

        cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(this);
        
		rating = (RatingBar) findViewById(R.id.ratingbar);
		rating.setVisibility(RatingBar.INVISIBLE);

		questionMark = (TextView) findViewById(R.id.questionmark);
		questionMark.setText(" ? ");

		results = (TextView) findViewById(R.id.results);
    }

	public void onClick(View v) {
		System.out.println("Clicked: " + v.getId() + "/" + R.id.button_cancel);
		if(v.getId() == R.id.button_submit){

			// Get the values given in EditText fields
			String givenUsername = usernameEditText.getText().toString();
			System.out.println("Given usernames is :" + givenUsername);

			//if (givenUsername=="auth") {
				//Lets auth this bitch
				//authWithHttpsPost();
		//	} else {
			// Pass those values to connectWithHttpGet() method
			connectWithHttpGet(givenUsername);
		//	}
		} else if (v.getId() == R.id.button_cancel) {
			results.setText("");
			usernameEditText.setText("");
			rating.setVisibility(RatingBar.INVISIBLE);
			
		}
	}

	/*private void authWithHttpsPost() {
		class HttpsPostAsyncTask extends AsyncTask<Void, Void, String>{

			@Override
			protected String doInBackground() {
				HttpsURLConnection https;
				String url = ""
				
			}
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result); 
			}
		}
			
	}*/
	
	private void connectWithHttpGet(final String givenUsername) {

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
				//We need to specifically get the givenUsername and givenPassword
				String paramUsername = params[0];
				System.out.println("paramUsername is :" + paramUsername);

				// Create an intermediate to connect with the Internet
				HttpClient httpClient = new DefaultHttpClient();

				// Sending a GET request to the web page that we want
				// Because of we are sending a GET request, we have to pass the values through the URL
				//HttpGet httpGet = new HttpGet("http://wilma.vub.ac.be/~dsverdlo/bachproef/getJSON.php?paramUsername=" + paramUsername);
				//HttpGet httpGet = new HttpGet("http://twitter.com/statuses/user_timeline/franklakatos.json");
				HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + givenUsername + "&api_key=46d561a6de9e5daa380db343d40ffbab&format=json&user=dsverdlo");//&mbid=b406e15c-0e89-40b7-99c1-39a250310b84");
					
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

					// If the Username and Password match, it will return "working" as response
					// If the Username or Password wrong, it will return "invalid" as response					
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
				//if(result.charAt(0) == '1'){

				TextView results = (TextView) findViewById(R.id.results);
				try {
			//		JSONArray records = new JSONArray(result);
					//Toast.makeText(getApplicationContext(), "HTTP GET JSON is working...", Toast.LENGTH_LONG).show();
					//System.out.println("onPostExecute length: " + result.length());
					
					final RatingBar rating = (RatingBar) findViewById(R.id.ratingbar);
					rating.setVisibility(RatingBar.VISIBLE);
					rating.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							float getRating = rating.getRating();
							Toast.makeText(getApplicationContext(), "Sending score " + getRating + " to the database", Toast.LENGTH_LONG).show();
						}
					});
					JSONObject records = new JSONObject(result);
					JSONObject record = records.getJSONObject("artist");
					JSONObject recor = record.getJSONObject("bio");
					//JSONObject reco = recor.getJSONObject("summary");
					
					//results.setText("Error!" + "\n" + "err: " + record.getString("errors"));	
					//	results.setText(record.getString("status"));
					//	System.out.println("JSON Object length:");
						//System.out.println(record.length());
					System.out.println("From bio, pull: ");
					System.out.println(recor.get("summary"));
					results.setText(recor.getString("summary"));
					} catch (JSONException e) {
						Log.d(VIBRATOR_SERVICE, "JSON Exception motherfucker!");
						e.printStackTrace();
					}
			//	}else{
			//		Toast.makeText(getApplicationContext(), "Invalid...", Toast.LENGTH_LONG).show();
			//	}			
			}			
		}

		// Initialize the AsyncTask class
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		// Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
		// We are passing the connectWithHttpGet() method arguments to that
		httpGetAsyncTask.execute(givenUsername); 

	}
}