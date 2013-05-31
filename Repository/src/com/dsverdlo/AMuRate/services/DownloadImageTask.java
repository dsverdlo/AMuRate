package com.dsverdlo.AMuRate.services;

import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.dsverdlo.AMuRate.gui.AnimationView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	private ImageView iv;
	private AnimationView load;

	
	public DownloadImageTask(final ImageView iv, final AnimationView load) { 
		this.iv = iv;
		this.load = load;
	}
	
	public DownloadImageTask(final ImageView iv) { 
		this.iv = iv;
	}

		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
				return bmp;
			} catch (Exception e) {
				System.out.println("Exception in MyConnection(loadImage):");
				e.printStackTrace();
				return null;
			}
		}
		
		
		@Override
		protected void onPostExecute(Bitmap bmp) {
			super.onPostExecute(bmp);
			if(load != null) load.setVisibility(View.GONE);
			iv.setVisibility(View.VISIBLE);
			iv.setImageBitmap(bmp);
			System.out.println("Done loading image!");
		}			
	}



