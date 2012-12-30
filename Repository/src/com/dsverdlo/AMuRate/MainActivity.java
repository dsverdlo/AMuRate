package com.dsverdlo.AMuRate;

import org.json.JSONException;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private EditText searchArtist;
	private EditText searchTitle;
	private Button cancelButton;
	private Button sendGetReqButton;
	private TextView results;
	private TextView questionMark;
	private Button view;
	private JSONObject artist;
	private MyConnection connection; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_test);	

		connection = new MyConnection();

		searchArtist = (EditText) findViewById(R.id.enterArtist);
		searchTitle = (EditText) findViewById(R.id.enterTitle);

		// TODO: remove hack
		searchTitle.setText("hunter");
		searchArtist.setText("Dido");

		sendGetReqButton = (Button) findViewById(R.id.button_submit);
		sendGetReqButton.setOnClickListener(this);
		sendGetReqButton.setTextColor(Color.WHITE);
		sendGetReqButton.setText(R.string.submit);

		cancelButton = (Button) findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(this);
		cancelButton.setTextColor(Color.WHITE);
		cancelButton.setText(R.string.cancel);

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
			String givenArtist = searchArtist.getText().toString();
			String givenTitle = searchTitle.getText().toString();
			int nArtist = givenArtist.length();
			int nTitle = givenTitle.length();

			System.out.println("Given artist[" + givenArtist + "], given title[" + givenTitle + "]");

			if(nArtist == 0 && nTitle == 0 ) {
				Toast.makeText(getApplicationContext(), "Please enter an artist, song or both", Toast.LENGTH_LONG).show();
			} else {
				results.setText("Loading...");
				view.setVisibility(Button.INVISIBLE);

				if(nArtist > 0 && nTitle == 0) connection.getFromArtist(this, givenArtist);	
				if(nArtist == 0 && nTitle > 0) connection.getFromTitle(this, givenTitle);	
				if(nArtist > 0 && nTitle > 0) connection.getFromTitleAndArtist(this, givenTitle, givenArtist);	
			}
			break;

		case R.id.button_cancel :
			results.setText("");
			view.setVisibility(Button.INVISIBLE);
			searchArtist.setText("");
			searchTitle.setText(""); 

			break;

		case R.id.questionmark :
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Info");
			alertDialog.setMessage("Please enter an artist, a song title or both in the search field and then press SUBMIT.");
			// Setting OK Button
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Write your code here to execute after dialog closed
				}
			});
			alertDialog.show();
			break;

		case R.id.view : 

			Intent newpage = new Intent(this, SearchResultsActivity.class);
			newpage.putExtra("searchResults", artist.toString());
			startActivity(newpage);
			break;


			//case R.id.tex : 
			// String get = artistEdit.getText().toString();
			//break;
		}
	}

	public void searchResultsTitle(String resultString) {
		if(resultString == null) {
			Toast.makeText(getApplicationContext(), "No internet connection... Please try again later", Toast.LENGTH_SHORT).show();
			results.setText("Results...");
		} else {
			try {
				if(!resultString.substring(2, 7).equals("error")) {
					JSONObject JSONobject = new JSONObject(resultString);
					JSONObject JSONresults = JSONobject.getJSONObject("results");
					int nResults = JSONresults.getInt("opensearch:totalResults");
					if(nResults>0) {
						results.setText("" + nResults + " results found...");
						artist = JSONresults.getJSONObject("trackmatches");
						view.setVisibility(Button.VISIBLE);
					}

				}
			} catch (JSONException je) {
				System.out.println("JSON Exception in MainAcitivty(searchResultsTitle):");
				je.printStackTrace();
			}
		}
	}

	public void searchResultsArtist(String resultString) {
		if(resultString == null) {
			Toast.makeText(getApplicationContext(), "No internet connection... Please connect and try again", Toast.LENGTH_SHORT).show();
			results.setText("Results...");
		} else {
			try {
				if(!resultString.substring(2, 7).equals("error")) {
					JSONObject JSONobject = new JSONObject(resultString);
					JSONObject JSONresults = JSONobject.getJSONObject("results");
					int nResults = JSONresults.getInt("opensearch:totalResults");
					if(nResults>0) {
						results.setText("" + nResults + " results found...");
						artist = JSONresults.getJSONObject("artistmatches");
						view.setVisibility(Button.VISIBLE);
					}	
				}
			} catch (JSONException je) {
				System.out.println("JSON Exception in MainAcitivty(searchResultsArtist):");
				je.printStackTrace();
			}
		}
	}

	public void searchResultsTitleAndArtist(String resultString) {
		if(resultString == null || resultString.length() < 7) {
			Toast.makeText(getApplicationContext(), "No internet connection... Please ensure your connection", Toast.LENGTH_SHORT).show();
			results.setText("Results...");
		} else {
			try {
				if(!resultString.substring(2, 7).equals("error")) {
					JSONObject JSONobject = new JSONObject(resultString);
					JSONObject JSONresults = JSONobject.getJSONObject("results");
					int nResults = JSONresults.getInt("opensearch:totalResults");
					if(nResults>0) {
						results.setText("" + nResults + " results found...");
						artist = JSONresults.getJSONObject("trackmatches");
						view.setVisibility(Button.VISIBLE);
					}
				}
			} catch (JSONException je) {
				System.out.println("JSON Exception in MainAcitivty(searchResultsTitleAndArtist):");
				je.printStackTrace();
			}
		}
	}

}