package com.dsverdlo.AMuRate;

import java.awt.Color;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable {
	// Private vars
	String title, artist, albumTitle, albumUrl, albumMBID;
	String imageS, imageM, imageL;
	String mbid, url;
	int duration, listeners, playcount;
	boolean streamable;
	

	private enum TrackKeys
	{
		streamable, listeners, image, artist, album, url, mbid, name; 
	}
	
	private enum AlbumKeys
	{
		title, mbid, image, url
	}
	
	public Track() {
		InitializeTrack();
	}
	
	public void InitializeTrack() {
		title = "";
		artist = "";
		mbid = "";
		albumTitle = "";
		albumUrl = "";
		albumMBID = "";
		imageS = "";
		imageM = "";
		imageL = "";
		duration = -1;
		listeners = -1;
		playcount = -1;
		streamable = false;
	}
	
	public Track(JSONObject json) {
		InitializeTrack();
		
		try {
			Iterator<?> it = json.keys();
			while(it.hasNext()) {
				String key = (String) it.next();
				//System.out.println("one:" + key);
			
				switch(TrackKeys.valueOf(key)) {
				case name: 
					title = json.getString("name");
					break;
				case mbid:
					mbid = json.getString("mbid");
					break;
				case album:
					JSONObject JSONalbum = json.getJSONObject("album");
					Iterator<?> ita = JSONalbum.keys();
					while(ita.hasNext()) {
						switch(AlbumKeys.valueOf((String) ita.next())) {
						case title:
							albumTitle = JSONalbum.getString("title");
							break;
						case mbid:
							albumMBID = JSONalbum.getString("mbid");
							break;
						case url:
							albumUrl = JSONalbum.getString("url");
							break;
						case image:
							JSONArray imageUrls = JSONalbum.getJSONArray("");
							for(int i = 0; i < imageUrls.length(); i++) {
								JSONObject imageUrl = imageUrls.getJSONObject(i);
								String size = imageUrl.getString("size");
								String url = imageUrl.getString("#text");
								if(size.equals("small")) { imageS = url; }
								else if(size.equals("medium")) { imageM = url; }
								else if(size.equals("large")) { imageL = url; }
							}
							break;
						
						}
					}
					break;
				case artist:
					artist = json.getString("artist");
					break;
				case streamable:
					JSONObject JSONstreamable = json.getJSONObject("streamable");
					int trackStreamable = JSONstreamable.getInt("#text");
					streamable = (trackStreamable == 1);
					break;
				default: break;
				}
			}
		} catch (JSONException je) {
			System.out.println("JSONException in Track(public constructor)");
			je.printStackTrace();
		}
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getTitle() {
		return title;
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

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
}
