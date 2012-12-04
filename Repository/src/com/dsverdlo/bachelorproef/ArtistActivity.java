package com.dsverdlo.bachelorproef;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

public class ArtistActivity extends Activity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.artist_activity);
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
	
	
}

