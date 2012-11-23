package com.dsverdlo.bachelorproef;

import android.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		System.out.println("0.5");
		Toast t = Toast.makeText(getApplicationContext(), "Main created!", Toast.LENGTH_LONG);
		t.show();
		  

		MyConnection myc = new MyConnection();
		myc.getTest(this.getApplicationContext());
		
		TextView tex = new TextView(this.getApplicationContext());
		
		tex.setText("Enter an artist name");
		
		Button button_submit = new Button(this.getApplicationContext());
		button_submit.setText("Submit");
		button_submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast later = Toast.makeText(getApplicationContext(), "I'll do it later", Toast.LENGTH_LONG);
				later.show();
				//System.out.println(tex.getText());
			}
		});
		
		Button button_cancel = new Button(this.getApplicationContext());
		button_cancel.setText("Cancel");
		button_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast later = Toast.makeText(getApplicationContext(), "I'll cancel it later", Toast.LENGTH_LONG);
				later.show();
				//System.out.println(tex.getText());
			}
		});
		
	}
}