package com.dsverdlo.AMuRate.gui;

import org.json.JSONException;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.HistoryAdapter;
import com.dsverdlo.AMuRate.services.DatabaseSyncer;
import com.dsverdlo.AMuRate.services.MyConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * MainActivity is the first screen users see when launching the app.
 * It provides search fields and a help button with instructions.
 * 
 * 
 * @author David Sverdlov
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	private EditText searchArtist;
	private EditText searchTitle;
	private Button cancelButton;
	private Button sendGetReqButton;
	private Button historyButton;
	private TextView results;
	private TextView questionMark;
	private Button view;
	private AnimationView loading;
	private JSONObject artist;
	private MyConnection connection; 
	private HistoryAdapter ha;

	private int search_option = 0; // 
	private static final int SEARCH_ARTIST = 1;
	private static final int SEARCH_TITLE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_test);	

		connection = new MyConnection();
		ha = new HistoryAdapter(this);

		searchArtist = (EditText) findViewById(R.id.enterArtist);
		searchTitle = (EditText) findViewById(R.id.enterTitle);

		// TODO: remove hack
		searchTitle.setText("hunter");
		searchArtist.setText("Dido");
		if(getIntent().hasExtra("title") && getIntent().hasExtra("artist")) {
			searchTitle.setText(getIntent().getStringExtra("title"));
			searchArtist.setText(getIntent().getStringExtra("artist"));
		}

		loading = (AnimationView) findViewById(R.id.gif_loading);
		loading.setVisibility(View.GONE);

		sendGetReqButton = (Button) findViewById(R.id.button_search);
		sendGetReqButton.setOnClickListener(this);
		sendGetReqButton.setTextColor(Color.WHITE);
		sendGetReqButton.setText(R.string.search);

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
		
		historyButton = (Button)findViewById(R.id.main_button_history);
		historyButton.setOnClickListener(this);
		historyButton.setTextColor(Color.WHITE);

		results = (TextView) findViewById(R.id.results);

		OnLongClickListener quitAction = new OnLongClickListener() {
			public boolean onLongClick(View v) {
				finish();
				return false;
			}
		};
		cancelButton.setOnLongClickListener(quitAction);

		new DatabaseSyncer(getApplicationContext()).execute();

	}


	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_search : 

			// Get the values given in EditText fields
			String givenArtist = searchArtist.getText().toString();
			String givenTitle = searchTitle.getText().toString();
			int nArtist = givenArtist.length();
			int nTitle = givenTitle.length();

			System.out.println("Given artist[" + givenArtist + "], given title[" + givenTitle + "]");

			if(nArtist == 0 && nTitle == 0 ) {
				Toast.makeText(getApplicationContext(), "Please enter an artist, song or both\nTo quit, hold down on 'Cancel'", Toast.LENGTH_LONG).show();
			} else {
				// The following four lines hdie the soft keyboard
				InputMethodManager inputManager = 
				        (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(
				        this.getCurrentFocus().getWindowToken(),
				        InputMethodManager.HIDE_NOT_ALWAYS);
				
				loading.setVisibility(View.VISIBLE);
				results.setVisibility(View.INVISIBLE);
				view.setVisibility(Button.INVISIBLE);

				ha.addHistorySearch(givenArtist, givenTitle);

				if(nArtist > 0 && nTitle == 0) connection.getFromArtist(this, givenArtist);	
				if(nArtist == 0 && nTitle > 0) connection.getFromTitle(this, givenTitle);	
				if(nArtist > 0 && nTitle > 0) connection.getFromTitleAndArtist(this, givenTitle, givenArtist);	
			}
			break;

		case R.id.button_cancel :
			results.setText("");
			loading.setVisibility(View.GONE);
			view.setVisibility(Button.INVISIBLE);
			searchArtist.setText("");
			searchTitle.setText(""); 

			break;

		case R.id.questionmark :
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Instructions");
			alertDialog.setMessage("Please enter an artist, a song title or both in the search field and then press search.");
			// Setting OK Button
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Write your code here to execute after dialog closed
				}
			});
			alertDialog.show();
			break;

		case R.id.view : 
			if (search_option == SEARCH_ARTIST) {
				Intent newpage = new Intent(this, SearchArtistActivity.class);
				newpage.putExtra("searchResults", artist.toString());
				startActivity(newpage);				
			} else {
				Intent newpage = new Intent(this, SearchResultsActivity.class);
				newpage.putExtra("searchResults", artist.toString());
				startActivity(newpage); 
			}
			break;

		case R.id.main_button_history :
			Intent newpage = new Intent(this, HistoryActivity.class);
			startActivity(newpage);
		}
	}

	public void searchResultsTitle(String resultString) {
		if(resultString == null) {
			Toast.makeText(getApplicationContext(), "No internet connection... Please try again later", Toast.LENGTH_SHORT).show();
			loading.setVisibility(View.GONE);
			results.setText("Results...");
			results.setVisibility(View.VISIBLE);
		} else {
			try {
				if(!resultString.substring(2, 7).equals("error")) {
					JSONObject JSONobject = new JSONObject(resultString);
					JSONObject JSONresults = JSONobject.getJSONObject("results");
					int nResults = JSONresults.getInt("opensearch:totalResults");
					if(nResults>0) {
						search_option = SEARCH_TITLE;
						String resultsMessage = " results found!";
						if(nResults>30) {
							results.setText("Lots of" + resultsMessage); }
						else {
							results.setText("" + nResults + resultsMessage);
						}
						artist = JSONresults.getJSONObject("trackmatches");
						loading.setVisibility(View.GONE);
						results.setVisibility(View.VISIBLE);
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
			loading.setVisibility(View.GONE);
			results.setText("Results...");
			results.setVisibility(View.VISIBLE);
		} else {
			try {
				if(!resultString.substring(2, 7).equals("error")) {
					JSONObject JSONobject = new JSONObject(resultString);
					JSONObject JSONresults = JSONobject.getJSONObject("results");
					int nResults = JSONresults.getInt("opensearch:totalResults");
					if(nResults>0) {
						search_option = SEARCH_ARTIST;
						String resultsMessage = " results found!";
						if(nResults>30) {
							results.setText("Lots of" + resultsMessage); }
						else {
							results.setText("" + nResults + resultsMessage);
						}
						artist = JSONresults.getJSONObject("artistmatches");
						loading.setVisibility(View.GONE);
						results.setVisibility(View.VISIBLE);
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
			loading.setVisibility(View.GONE);
			results.setText("Results...");
			results.setVisibility(View.VISIBLE);
		} else {
			try {
				if(!resultString.substring(2, 7).equals("error")) {
					JSONObject JSONobject = new JSONObject(resultString);
					JSONObject JSONresults = JSONobject.getJSONObject("results");
					int nResults = JSONresults.getInt("opensearch:totalResults");
					if(nResults>0) {
						search_option = SEARCH_TITLE;
						String resultsMessage = " results found!";
						if(nResults>30) {
							results.setText("Lots of" + resultsMessage); }
						else {
							results.setText("" + nResults + resultsMessage);
						}
						artist = JSONresults.getJSONObject("trackmatches");
						loading.setVisibility(View.GONE);
						view.setVisibility(Button.VISIBLE);
						results.setVisibility(View.VISIBLE);
					}
				}
			} catch (JSONException je) {
				System.out.println("JSON Exception in MainAcitivty(searchResultsTitleAndArtist):");
				je.printStackTrace();
			}
		}
	}
	
	
	
	
	

}