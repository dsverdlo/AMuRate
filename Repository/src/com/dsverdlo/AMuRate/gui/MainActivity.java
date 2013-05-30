package com.dsverdlo.AMuRate.gui;

import org.json.JSONException;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.AMuRate;
import com.dsverdlo.AMuRate.services.DatabaseSyncer;
import com.dsverdlo.AMuRate.services.InternalDatabaseHistoryAdapter;
import com.dsverdlo.AMuRate.services.HttpConnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

	private AMuRate amr;
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
	private HttpConnect connection; 
	private InternalDatabaseHistoryAdapter ha;

	private int search_option = 0; // 
	private static final int SEARCH_ARTIST = 1;
	private static final int SEARCH_TITLE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	

		// Get global vars
		amr = (AMuRate)getApplicationContext();
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		amr.setSCREENHEIGHT(displaymetrics.heightPixels);
		amr.setSCREENWIDTH(displaymetrics.widthPixels);
		System.out.println("Screen w="+amr.getSCREENWIDTH()+" h="+amr.getSCREENHEIGHT());
		connection = new HttpConnect();
		ha = new InternalDatabaseHistoryAdapter(this);

		searchArtist = (EditText) findViewById(R.id.enterArtist);
		searchTitle = (EditText) findViewById(R.id.enterTitle);

		// TODO: remove hack
		//searchTitle.setText("hunter");
		//searchArtist.setText("Dido");
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
		questionMark.setText(R.string.questionmark);
		questionMark.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					String ip = searchTitle.getText().toString();
					if(ip != null) {
						amr.setIp(ip);
						Toast.makeText(amr, "Ip set!", Toast.LENGTH_SHORT).show();
					}
					return false;
		}});
		
		view = (Button) findViewById(R.id.view);
		view.setOnClickListener(this);
		view.setVisibility(View.INVISIBLE);
		view.setText(R.string.view);
		
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

		new DatabaseSyncer(amr, amr.getIp(), amr.getUser()).execute();

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
				Toast.makeText(amr, R.string.main_msg_empty, Toast.LENGTH_LONG).show();
			} else {
				// The following four lines hdie the soft keyboard
				InputMethodManager inputManager = 
				        (InputMethodManager) amr.getSystemService(Context.INPUT_METHOD_SERVICE); 
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
			alertDialog.setTitle(R.string.main_instructions);
			alertDialog.setMessage(amr.getString(R.string.main_instruction_text));
			// Setting OK Button
			alertDialog.setButton(amr.getString(R.string.OK), new DialogInterface.OnClickListener() {
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
			Toast.makeText(amr, amr.getString(R.string.main_no_internet_conn), Toast.LENGTH_SHORT).show();
			loading.setVisibility(View.GONE);
			results.setText(amr.getString(R.string.results) + "...");
			results.setVisibility(View.VISIBLE);
		} else {
			try {
				if(!resultString.substring(2, 7).equals("error")) {
					JSONObject JSONobject = new JSONObject(resultString);
					JSONObject JSONresults = JSONobject.getJSONObject("results");
					int nResults = JSONresults.getInt("opensearch:totalResults");
					if(nResults>0) {
						search_option = SEARCH_TITLE;
						String resultsMessage = amr.getString(R.string.main_results_found);
						if(nResults>30) {
							results.setText(amr.getString(R.string.main_many) + resultsMessage); }
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
			Toast.makeText(amr, R.string.main_no_internet_conn, Toast.LENGTH_SHORT).show();
			loading.setVisibility(View.GONE);
			results.setText(amr.getString(R.string.results) + "...");
			results.setVisibility(View.VISIBLE);
		} else {
			try {
				if(!resultString.substring(2, 7).equals("error")) {
					JSONObject JSONobject = new JSONObject(resultString);
					JSONObject JSONresults = JSONobject.getJSONObject("results");
					int nResults = JSONresults.getInt("opensearch:totalResults");
					if(nResults>0) {
						search_option = SEARCH_ARTIST;
						String resultsMessage = amr.getString(R.string.main_results_found);
						if(nResults>30) {
							results.setText(amr.getString(R.string.main_many) + resultsMessage); }
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
			Toast.makeText(amr, R.string.main_no_internet_conn, Toast.LENGTH_SHORT).show();
			loading.setVisibility(View.GONE);
			results.setText(amr.getString(R.string.results) + "...");
			results.setVisibility(View.VISIBLE);
		} else {
			try {
				if(!resultString.substring(2, 7).equals("error")) {
					JSONObject JSONobject = new JSONObject(resultString);
					JSONObject JSONresults = JSONobject.getJSONObject("results");
					int nResults = JSONresults.getInt("opensearch:totalResults");
					if(nResults>0) {
						search_option = SEARCH_TITLE;
						String resultsMessage = amr.getString(R.string.main_results_found);
						if(nResults>30) {
							results.setText(R.string.main_many + resultsMessage); }
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