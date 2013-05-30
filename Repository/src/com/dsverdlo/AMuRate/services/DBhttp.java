package com.dsverdlo.AMuRate.services;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import com.dsverdlo.AMuRate.objects.MyCallback;




public class DBhttp extends Thread {
	public static final int METHOD_POST = 0;
	public static final int METHOD_GET = 3;
	public static final int METHOD_GET_AVG = 1;
	public static final int METHOD_GET_AMT = 2;
	
	private String mbid, artist, title, user;
	private int method, date;
	private float rating;
	private MyCallback callback;
	
	public DBhttp(int method, String mbid, String artist, String title, float rating, int date, String user) {
		this.method = method;
		this.mbid = mbid;
		this.artist = artist;
		this.title = title;
		this.date = date;
		this.rating = rating;
		this.user = user;
	};
	
	public DBhttp(int method, String mbid) {
		this.method = method;
		this.mbid = mbid;
	}

	public void run() {
		switch(method) {
		case METHOD_POST: runPost(); break;
		case METHOD_GET: runGet(); break;
		case METHOD_GET_AVG: break;
		case METHOD_GET_AMT: break;
		default: break;
		}
	}
	public void runPost() {
		try {
			String urlParameters = "title="+title+"&artist="+artist+"&date="+date+"&rating="+rating+"&user="+user+"&mbid="+mbid+"&apptoken=4444MuR4444t333";
			URL url = new URL("http://dsverdlo.net23.net/testpost.php");
			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			writer.write(urlParameters);
			writer.flush();

			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((line = reader.readLine()) != null) {
				if(!line.equals("")) callback.setResult(Double.parseDouble(line));
			}
			writer.close();
			reader.close();
		} catch (Exception e) {
			System.out.println("Exceptions happen in DBhttp.java");
			e.printStackTrace();
		}
	} 
	
	public void runGet() {
		try {
			double result = -1;
			System.out.println("DBhttp GETTING RESULTS");
			URL url_avg = new URL("http://dsverdlo.net23.net/testget.php?mbid="+mbid+"&method=avg");
			URL url_amt = new URL("http://dsverdlo.net23.net/testget.php?mbid="+mbid+"&method=amt");
			
			URLConnection conn = url_avg.openConnection();
			conn.setDoOutput(true);
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = reader.readLine()) != null) {
				if(!line.equals("")) result = Double.parseDouble(line);
			}
			reader.close();
			
			conn = url_amt.openConnection();
			conn.setDoOutput(true);
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while((line = reader.readLine()) != null) {
				if(!line.equals("")) result = result + 10 * Integer.parseInt(line);
			}
			reader.close();
			
			if(result > -1) callback.setResult(result);
			
		} catch (Exception e) {
			System.out.println("Exceptions happen in DBhttp.java");
			e.printStackTrace();
		}
	}

	public void setCallback(MyCallback cb) {
		this.callback = cb;
		
	}
	
}
