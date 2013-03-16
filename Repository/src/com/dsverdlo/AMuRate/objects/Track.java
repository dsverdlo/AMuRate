package com.dsverdlo.AMuRate.objects;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Track abstract data type. Stores all kind of information of tracks.
 * Since the JSON representation of a track returned by the api differs,
 * the appropriate loadFromX method must be used when loading in data.
 * The class implements Parcelable so it can be passed from one Activity
 * to another.  
 * 
 * @author david
 *
 */
public class Track implements Parcelable {
	// Private vars
	int id;
	String trackTitle, trackUrl; 
	private String albumTitle;
	String albumUrl;
	private String albumMBID;
	String artistName, artistUrl, artistMBID;
	String imageS, imageM, imageL;
	String mbid, url;
	int duration, listeners, playcount;
	boolean streamable;
	

	private enum TrackKeys
	{
		streamable, listeners, image, artist, album, url, mbid, name,
		id, duration, playcount, toptags;
	}
	
	private enum AlbumKeys
	{
		title, mbid, image, url, artist
	}
	
	private enum ArtistKeys
	{
		name, mbid, url
	}
	
	public Track() {
		InitializeTrack();
	}
	
	public void InitializeTrack() {
		id = 0;
		trackTitle = "";
		mbid = "";
		trackUrl = "";
		albumTitle = "";
		albumUrl = "";
		albumMBID = "";
		artistName = "";
		artistUrl = "";
		artistMBID = "";
		imageS = "";
		imageM = "";
		imageL = "";
		duration = -1;
		listeners = -1;
		playcount = -1;
		streamable = false;
	}
	
	public void loadFromSearch(JSONObject json) {
		try {
			Iterator<?> it = json.keys();
			while(it.hasNext()) {
				String key = (String) it.next();
				try { 			
					switch(TrackKeys.valueOf(key)) {
					case name: 
						trackTitle = json.getString("name");
						break;

					case mbid:
						mbid = json.getString("mbid");
						break;

					case url:
						trackUrl = json.getString("url");
						break;

					case image:
						JSONArray imgUrls = json.getJSONArray("image");
						for(int i = 0; i < imgUrls.length(); i++) {
							JSONObject imageUrl = imgUrls.getJSONObject(i);
							String size = imageUrl.getString("size");
							String url = imageUrl.getString("#text");
							if(size.equals("small")) { imageS = url; }
							else if(size.equals("medium")) { imageM = url; }
							else if(size.equals("large")) { imageL = url; }
						}
						break;

					case artist:
						artistName = json.getString("artist");
						break;

					case streamable:
						JSONObject JSONstreamable = json.getJSONObject("streamable");
						int trackStreamable = JSONstreamable.getInt("#text");
						streamable = (trackStreamable == 1);
						break;

					case listeners:
						listeners = json.getInt("listeners");
						break;

					default: break;
					}
				}
				catch (IllegalArgumentException iae) {
					System.out.println("Error: IllegalArgumentException in Track(onLoadSearch)");
				}
			}
		} catch (JSONException je) {
			System.out.println("JSONException in Track(loadFromSearch)");
			je.printStackTrace();
		}
	}

	public void loadFromInfo(JSONObject json) {		
		try {
			Iterator<?> it = json.keys();
			while(it.hasNext()) {
				String key = (String) it.next();
				//System.out.println("one:" + key);

				switch(TrackKeys.valueOf(key)) {
				case id:
					id = json.getInt("id");
					break;
				case name:
					trackTitle = json.getString("name");
					break;
				case mbid:
					mbid = json.getString("mbid");
					break;
				case url:
					trackUrl = json.getString("url");
					break;
				case duration:
					duration = json.getInt("duration");
					break;
				case listeners:
					listeners = json.getInt("listeners");
					break;
				case playcount:
					playcount = json.getInt("playcount");
					break;
				case streamable:
					JSONObject JSONstreamable = json.getJSONObject("streamable");
					int trackStreamable = JSONstreamable.getInt("#text");
					streamable = (trackStreamable == 1);
					break;
				case artist:
					JSONObject JSONartist = json.getJSONObject("artist");
					Iterator<?> itArtist = JSONartist.keys();
					while(itArtist.hasNext()) {
						switch(ArtistKeys.valueOf((String) itArtist.next())) {
							case name:
								artistName = JSONartist.getString("name");
								break;
							case mbid: 
								artistMBID = JSONartist.getString("mbid");
								break;
							case url:
								artistUrl = JSONartist.getString("url");
								break;
							default: break;
						}
					}
					break;					
					
				case album:
					JSONObject JSONalbum = json.getJSONObject("album");
					Iterator<?> itAlbum = JSONalbum.keys();
					while(itAlbum.hasNext()) {
						try {
						switch(AlbumKeys.valueOf((String) itAlbum.next())) {
						case title:
							albumTitle = JSONalbum.getString("title");
							break;
						case mbid:
							albumMBID =  JSONalbum.getString("mbid");
							break;
						case url:
							albumUrl = JSONalbum.getString("url");
							break;
						case image:
							JSONArray imageUrls = JSONalbum.getJSONArray("image");
							for(int i = 0; i < imageUrls.length(); i++) {
								JSONObject imageUrl = imageUrls.getJSONObject(i);
								String size = imageUrl.getString("size");
								String url = imageUrl.getString("#text");
								if(size.equals("small")) { imageS = url; }
								else if(size.equals("medium")) { imageM = url; }
								else if(size.equals("large")) { imageL = url; }
							}
							break;
						default: break;
						}
						} catch (IllegalArgumentException iae) {
							System.out.println("Error: illegal argument in Track(loadFromInfo)");
						}
					}
					break;
				default: break;
				}
			}
		} catch (Exception e) { 
			System.out.println("Exception in Track(loadFromInfo)");
			e.printStackTrace();
		}
	}
	
	public void loadFromInfo(String stringExtra) {
		try {
			JSONObject JSONextra = new JSONObject(stringExtra);
			loadFromInfo(JSONextra.getJSONObject("track"));
		} catch (JSONException je) {
			System.out.println("JSONException in Track(loadFromInfo(String))");
			je.printStackTrace();
		}
		
	}

	public String getArtist() {
		return artistName;
	}
	
	public String getTitle() {
		return trackTitle;
	}
	
	public String getMBID() {
		return mbid;
	}
	
	public String getImage(String size) {
		if(size.equals("s")) return imageS;
		if(size.equals("m")) return imageM;
		if(size.equals("l")) return imageL;
		return "";
	}
	
	public String getDuration() {
		return convertDurationToString(convertDuration(duration));
		}

	public String getAlbumMBID() {
		return albumMBID;
	}

	public String getAlbumTitle() {
		return albumTitle;
	}
	
	public String getAlbum() {
		return getAlbumTitle();
	}


	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	private int[] convertDuration(double milliseconds) {
		int time[] = new int[3];
		int seconds = (int) (milliseconds / 1000) % 60 ;
		int minutes = (int) ((milliseconds / (1000*60)) % 60);
		int hours = (int) ((milliseconds / (1000*60*60)) % 24);
		
		time[0] = seconds;
		time[1] = minutes;
		time[2] = hours;
		return time;
	}
	
	private String convertDurationToString(int[] time) {
		int hours = time[2];
		int minutes = time[1];
		int seconds = time[0];
		boolean hoursExtra = hours < 9;
		boolean minsExtra = minutes < 9;
		boolean secsExtra = seconds < 9;
		if(hours == 0) return "" + (minsExtra ? "0": "") + minutes + ":" + (secsExtra ? "0" : "") + seconds; 
		return "" + (hoursExtra ? "0" : "") + hours + ":" + (minsExtra ? "0" : "") + minutes + ":" + (secsExtra ? "0" : "") + seconds;
	}

	public boolean getStreamable() {
		return streamable;
	}

	public int getId() {
		return id;
	}




	
}
