package com.dsverdlo.bachelorproef;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyConnection extends Activity implements OnClickListener {

	private EditText usernameEditText;
	private Button sendGetReqButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_test);

        usernameEditText = (EditText) findViewById(R.id.tex);

        sendGetReqButton = (Button) findViewById(R.id.button_submit);
        sendGetReqButton.setOnClickListener(this);

		final RatingBar rating = (RatingBar) findViewById(R.id.ratingbar);
		rating.setVisibility(RatingBar.INVISIBLE);
		
		TextView questionMark = (TextView) findViewById(R.id.questionmark);
		questionMark.setText(" ? ");
    }

	public void onClick(View v) {

		if(v.getId() == R.id.button_submit){

			// Get the values given in EditText fields
			String givenUsername = usernameEditText.getText().toString();
			System.out.println("Given usernames is :" + givenUsername);

			// Pass those values to connectWithHttpGet() method
			connectWithHttpGet(givenUsername);
		}		
	}

	private void connectWithHttpGet(String givenUsername) {

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
				HttpGet httpGet = new HttpGet("http://wilma.vub.ac.be/~dsverdlo/bachproef/command.php?paramUsername=" + paramUsername);

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
					System.out.println("Returning value of doInBackground :" + stringBuilder.toString());

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
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				TextView results = (TextView) findViewById(R.id.results);
				if(result.charAt(0) == '1'){
					Toast.makeText(getApplicationContext(), "HTTP GET is working...", Toast.LENGTH_LONG).show();
					System.out.println("onPostExecute length: " + result.length());
					
					final RatingBar rating = (RatingBar) findViewById(R.id.ratingbar);
					rating.setVisibility(RatingBar.VISIBLE);
					rating.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							float getRating = rating.getRating();
							Toast.makeText(getApplicationContext(), "Sending score " + getRating + " to the database", Toast.LENGTH_LONG).show();
						}
					});
				}else{
					Toast.makeText(getApplicationContext(), "Invalid...", Toast.LENGTH_LONG).show();
				}	
				results.setText(result.substring(1));			
			}			
		}

		// Initialize the AsyncTask class
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		// Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
		// We are passing the connectWithHttpGet() method arguments to that
		httpGetAsyncTask.execute(givenUsername); 

	}
}