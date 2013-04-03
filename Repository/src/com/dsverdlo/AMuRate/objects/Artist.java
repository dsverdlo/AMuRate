package com.dsverdlo.AMuRate.objects;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Artist abstract data type. Stores all kind of data from an artist.
 * The public constructor takes one string which is a JSON representation
 * of an album, returned by the api.
 * 
 * @author David Sverdlov
 */

public class Artist implements Parcelable {
	private String artistName;
	private int listeners;
	private String imageS, imageM;
	private String imageL;
	private String url;
	
	private enum ArtistKeys {
		name, mbid, url, image,	listeners, streamable
	}
	
	private void initialize() {
		artistName = "";
		listeners = 0;
		imageS = "";
		imageM = "";
		imageL = "";
		url = "";
	}
	
	public Artist() {
		initialize();
	}
	
	public void loadFromSearch(String extraArtist) {
		initialize();
		
		try {
			JSONObject JSONobject = new JSONObject(extraArtist);
			JSONObject JSONArtist = JSONobject.getJSONObject("artist");
			Iterator<?> it = JSONArtist.keys();
			while(it.hasNext()) {
				try { 
					String key = (String) it.next();
					System.out.println("777:" + key);
					switch(ArtistKeys.valueOf(key)) {
					case name: 
						artistName = JSONArtist.getString("name");
						break;
						
					case listeners:
						listeners = JSONArtist.getInt("listeners");
						break;
					
					case url:
						url = JSONArtist.getString("url");
						break;
												
					case image:
						JSONArray imageUrls = JSONArtist.getJSONArray("image");
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
					System.out.println("Error: illegal argument exception in Album");
				}
			}

		} catch (JSONException e) {
			System.out.println("JSONException in Album(public constructor)");
			e.printStackTrace();
		}
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public int getListeners() {
		return listeners;
	}

	public void setListeners(int listeners) {
		this.listeners = listeners;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void loadFromSearch(JSONObject oneResult) {
		// TODO Auto-generated method stub
		
	}
}
