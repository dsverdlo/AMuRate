package com.dsverdlo.bachelorproef;

import com.dsverdlo.bachelorproef.R;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_test);
		System.out.println("0.5");
		final Toast t = Toast.makeText(getApplicationContext(), "Main created!", Toast.LENGTH_LONG);
		t.show();
		  

		//MyConnection myc = new MyConnection();
		//myc.getTest(this.getApplicationContext());
		
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
		Button button_submit = (Button) findViewById(R.id.button_submit);
		button_submit.setBackgroundColor(Color.LTGRAY);
		button_submit.setText("Submit");
		button_submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast later = Toast.makeText(getApplicationContext(), "I'll do it later", Toast.LENGTH_LONG);
				later.show();
				//System.out.println(tex.getText());
			}
		});

		System.out.println("Submit button created");
		Button button_cancel = (Button) findViewById(R.id.button_cancel);
		button_cancel.setBackgroundColor(Color.LTGRAY);
		button_cancel.setText("Cancel");
		button_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast later = Toast.makeText(getApplicationContext(), "I'll cancel it later", Toast.LENGTH_LONG);
				later.show();
				//System.out.println(tex.getText());
			}
		});

		System.out.println("Cancel button created");
	}
}