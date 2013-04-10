package com.dsverdlo.AMuRate.objects;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

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
	private String imageL, imageXL;
	private String url;
	private String mbid;
	
	// more fields from getInfo
	private int playcount;
	private boolean streamable; // maybe for use later?
	private String urlOut;
	private String summary;
	private String content;
	
	private enum ArtistKeys {
		name, mbid, url, image,	listeners, streamable
	}
	
	private enum ArtistInfoKeys {
		name, mbid, url, image,	stats, streamable, bio,
		listeners, playcount, ontour, similar, tags,
		links, published, summary, content, placeformed,
		yearformed, formationlist
	}
	
	private void initialize() {
		artistName = "";
		listeners = 0;
		imageS = "";
		imageM = "";
		imageL = "";
		imageXL = "";
		url = "";
		setMbid("");
		
		playcount = 0;
		streamable = false;
		urlOut = "";
		summary = "";
		content = "";
		
		
		
	}
	
	public Artist() {
		initialize();
	}
	
	public void loadFromSearch(String extraArtist) {
		initialize();
		
		try {
			//JSONObject JSONobject = new JSONObject(extraArtist);
			//JSONObject JSONArtist = JSONobject.getJSONObject("artist");
			JSONObject JSONArtist = new JSONObject(extraArtist);
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
						
					case mbid:
						setMbid(JSONArtist.getString("mbid"));
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
		if(size.equals("s") || size.equals("S")) return imageS;
		if(size.equals("m") || size.equals("M")) return imageM;
		if(size.equals("l") || size.equals("L")) return imageL;
		if(size.equals("xl") || size.equals("XL")) return imageXL;
		return "";
	}

	public String getMbid() {
		return mbid;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
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

	public void loadFromInfo(String stringExtra) {
		initialize();
		try {
			JSONObject JSONArtist = new JSONObject(stringExtra);
			//JSONObject JSONArtist = JSONobject.getJSONObject("artist");
			Iterator<?> it = JSONArtist.keys();
			while(it.hasNext()) {
				try { 
					String key = (String) it.next();
					System.out.println("8008:" + key);
					switch(ArtistInfoKeys.valueOf(key)) {
					case name: 
						artistName = JSONArtist.getString("name");
						break;
						
					case stats:
						JSONObject JSONstats = JSONArtist.getJSONObject("stats");
						listeners = JSONstats.getInt("listeners");
						playcount = JSONstats.getInt("playcount");
						break;
					
					case url:
						url = JSONArtist.getString("url");
						break;
						
					case mbid:
						setMbid(JSONArtist.getString("mbid"));
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
							else if(size.equals("extralarge")) { imageXL = url; }
						}
						break;
					case bio:
						JSONObject JSONbio = JSONArtist.getJSONObject("bio");
						Iterator<?> itBio = JSONbio.keys();
						while(itBio.hasNext()) {
							String keyBio = (String) itBio.next();
							System.out.println("80085:" + keyBio);
							switch(ArtistInfoKeys.valueOf(keyBio)) {
							case links: 
								JSONObject links = JSONbio.getJSONObject("links");
								JSONObject link = links.getJSONObject("link");
								urlOut = link.getString("href");
								break;
							case summary:
								summary = JSONbio.getString("summary");
								break;
							case content:
								content = JSONbio.getString("content");
								break;
							default:
								break;
							}
						}
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

	public String getContent() {
		return this.content;
	}

	public String getUrlOut() {
		return this.urlOut;
	}

	public String getSummary() {
		return this.summary;
	}
}
