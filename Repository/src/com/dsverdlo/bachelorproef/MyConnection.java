package com.dsverdlo.bachelorproef;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class MyConnection {

	MyConnection() {
	}

	public void getTest(android.content.Context Context)  {
		Log.d(android.content.Context.VIBRATOR_SERVICE, "start get test");
		InputStream is = getInputStreamFromUrl("http://ws.audioscrobbler.com/2.0/?method=artist.search&artist=cher&api_key=46d561a6de9e5daa380db343d40ffbab", 30000);
		
		Log.d(android.content.Context.VIBRATOR_SERVICE, "done");
		System.out.println("donee");
	}

	private InputStream getInputStreamFromUrl(final String string, int i) {
		new Thread(new Runnable() {
			public void run() {
				try {
					URL urlurl = new URL(string);
					URLConnection conn = urlurl.openConnection();
					InputStream is = conn.getInputStream() ;
					
					Log.d(android.content.Context.VIBRATOR_SERVICE, "Got the input stream");
					String yeah = convertStreamToString(is);
					//String yeah = is.toString();  
					Log.d(android.content.Context.VIBRATOR_SERVICE, "Converted it to string");
					System.out.println("with length:");
					System.out.println(yeah);
					Log.d(android.content.Context.VIBRATOR_SERVICE, "Done");
				} catch (Exception e) {
					Log.d(android.content.Context.VIBRATOR_SERVICE, "Exception!");
					e.printStackTrace();
				}
			} 

		}).start();
		return null;
	}

	
	public static String convertStreamToString(InputStream is) throws IOException {
		//
		// To convert the InputStream to String we use the
		// Reader.read(char[] buffer) method. We iterate until the
		// Reader return -1 which means there's no more data to
		// read. We use the StringWriter class to produce the string.
		//
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
}
