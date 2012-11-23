package com.dsverdlo.bachelorproef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Text;

import android.R;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;


public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		System.out.println("0.5");
		setContentView(R.layout.activity_list_item);
		Toast t = Toast.makeText(getApplicationContext(), "Main created!", Toast.LENGTH_LONG);
		t.show();
		  

		MyConnection myc = new MyConnection();
		myc.getTest(this.getApplicationContext());
		
		
		
		/*
		try {

			String yeah = convertStreamToString(getInputStreamFromUrl("http://wilma.vub.ac.be/~dsverdlo/bachproef/api.php", 30000));
			Log.d(VIBRATOR_SERVICE, yeah);
			System.out.println(yeah);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(VIBRATOR_SERVICE, "failed");
			e.printStackTrace();
		}
		Log.d(VIBRATOR_SERVICE, "done");
		System.out.println("donee");
	}

	public static InputStream getInputStreamFromUrl(String url, int timeoutInMilliseconds) {
		InputStream content = null;
		try {
			// set up GET request
			HttpGet httpGet = new HttpGet(url);

			// set connection timeout + create client
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, timeoutInMilliseconds);
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			Log.d(VIBRATOR_SERVICE, "a1");
			// execute GET request
			HttpResponse httpResponse = httpClient.execute(httpGet);
			Log.d(VIBRATOR_SERVICE, "a2");
			content = httpResponse.getEntity().getContent();
			Log.d(VIBRATOR_SERVICE, "a3");
		} catch (ConnectTimeoutException e) {
			// handle the timeout exception !
		} catch (Exception e) {
			// other exceptions
		}
		return content;
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
	} */

	}
}