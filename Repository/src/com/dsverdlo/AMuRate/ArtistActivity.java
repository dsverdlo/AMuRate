package com.dsverdlo.AMuRate;

import org.json.JSONException;
import org.json.JSONObject;

import com.dsverdlo.AMuRate.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ArtistActivity extends Activity implements OnClickListener {
	private Button back;
	private TextView title;
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_activity);

		try {
			new JSONObject();
			JSONObject.numberToString(500);

			back = (Button) findViewById(R.id.back);
			back.setText(" Back ");
			back.setOnClickListener(this);

			title = (TextView) findViewById(R.id.title);
			title.setText("Surprise!");

			TextView info = (TextView) findViewById(R.id.info);
			info.setText("Congradulations motherfucker");

			ImageView picture = (ImageView) findViewById(R.id.picture);
			picture.setImageResource(R.drawable.cher_large);

		} catch (JSONException e) {
			System.out.println("JSON Exception in ArtistActivity (onCreate):");
			e.printStackTrace();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}
}




