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
	private TextView info;
	private ImageView picture;
	//private ratings
	private JSONObject artist;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_activity);

		try {
			//artist = new JSONObject(getIntent().getStringExtra("artist"));
			JSONObject l = new JSONObject();
			JSONObject.numberToString(500);

			back = (Button) findViewById(R.id.back);
			back.setText(" Back ");
			back.setOnClickListener(this);

			title = (TextView) findViewById(R.id.title);
			//title.setText(artist.getString("name"));
			title.setText("Surprise!");

			TextView info = (TextView) findViewById(R.id.info);
			//info.setText("Cher (born Cherilyn Sarkisian; May 20, 1946) is an American singer and actress. A major figure for over five decades in the world of popular culture, she is often referred to as the Goddess of Pop for having first brought the sense of female autonomy and self-actualization into entertainment industry. She is known for her distinctive contralto and for having worked extensively across media, as well as for continuously reinventing both her music and image, the latter of which has been known to induce controversy.");
			//info.setText(android.text.Html.fromHtml(artist.getJSONObject("bio").getString("content")));
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
	/*
	    Context mContext = this;
	    SpannableStringBuilder builder = new SpannableStringBuilder();
	    builder.append(mContext.getText(R.string.part1));
	    int lengthOfPart1 = builder.length();
	    builder.append(" ");
	    builder.append(mContext.getText(R.string.part2));
	    Drawable d = mContext.getResources().getDrawable(R.drawable.picasaIcon);
	    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()); // <---- Very important otherwise your image won't appear
	    ImageSpan myImage = new ImageSpan(d);
	    builder.setSpan(myImage, lengthOfPart1, lengthOfPart1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    myTextView.setText(builder);
	 */

}




