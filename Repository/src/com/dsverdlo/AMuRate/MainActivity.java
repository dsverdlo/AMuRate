package com.dsverdlo.AMuRate;

import com.dsverdlo.AMuRate.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_test);
		System.out.println("0.5");
		
		context = this.getApplicationContext();
		final Toast t = Toast.makeText(context, "Main created!", Toast.LENGTH_LONG);
		t.show();
		  

		
		System.out.println("Test succeeded");
		final EditText tex = (EditText) findViewById(R.id.tex);
		tex.setBackgroundColor(Color.LTGRAY);
		final String init_tex = "Enter an artist name";
		tex.setText(init_tex);
		tex.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String get = tex.getText().toString();
				if (get == init_tex) {
					t.setText("Erasing");
					t.show();
					tex.setText("");
				} else {
					t.setText("Selecting all");
					t.show();
					tex.selectAll();
				}
			}
		});

		System.out.println("EditText completed");
		

	/*	Button button_submit = (Button) findViewById(R.id.button_submit);
		button_submit.setBackgroundColor(Color.LTGRAY);
		button_submit.setText("Submit");
		button_submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast later = Toast.makeText(context, "I'll do it now!", Toast.LENGTH_LONG);
				later.show();

				TextView results = (TextView) findViewById(R.id.results);
				
				//myc.getTest(results, tex.getEditableText().toString());
				//System.out.println("Returned from server with:");
				//System.out.println(get);
				//System.out.println(tex.getText());
				results.setBackgroundColor(Color.WHITE);
				results.setText("Loading...");
			}
		});*/

		System.out.println("Submit button created");
		Button button_cancel = (Button) findViewById(R.id.button_cancel);
		button_cancel.setBackgroundColor(Color.LTGRAY);
		button_cancel.setText("Cancel");
		button_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast later = Toast.makeText(context, "I'll cancel it later", Toast.LENGTH_LONG);
				later.show();
				//System.out.println(tex.getText());
			}
		});

		System.out.println("Cancel button created");
		Intent inten = new Intent(getBaseContext(), MyConnection.class);
		startActivity(inten);
	}
}