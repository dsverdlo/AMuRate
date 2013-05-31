package com.dsverdlo.AMuRate.objects;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Album abstract data type. Stores all kind of data from an album record.
 * The public constructor takes one string which is a JSON representation
 * of an album, returned by the api.
 * 
 * @author David Sverdlov
 */

public class Album {
	private String albumTitle;
	private String artistName;
	private int listeners;
	private int playcount;
	private String imageS, imageM, imageL;
	private JSONArray tracks;
	private String summary;
	
	private String mbid;

	private enum AlbumKeys {
		name, artist, id, mbid, url, releasedate, image,
		listeners, playcount, tracks, toptags, wiki
	}

	private void initialize() {
		albumTitle = "";
		artistName = "";
		listeners = 0;
		playcount = 0;
		imageS = "";
		imageM = "";
		imageL = "";
		summary = "";
		mbid = "";
		setTracks(new JSONArray());
	}

	private void switchAlbumInfo(JSONObject JSONAlbum) {
		try {
			Iterator<?> it = JSONAlbum.keys();
			String key = "";
			while(it.hasNext()) {
				try { 
					key = (String) it.next();
					switch(AlbumKeys.valueOf(key)) {
					case name: 
						albumTitle = JSONAlbum.getString("name");
						break;

					case artist:
						artistName = JSONAlbum.getString("artist");
						break;

					case listeners:
						listeners = JSONAlbum.getInt("listeners");
						break;

					case playcount:
						playcount = JSONAlbum.getInt("playcount");
						break;
					
					case mbid:
						mbid = JSONAlbum.getString("mbid");
						break;

					case wiki:
						JSONObject JSONwiki = JSONAlbum.getJSONObject("wiki");
						summary = JSONwiki.getString("summary");
						break;
					case tracks:
						setTracks(JSONAlbum.getJSONObject("tracks").getJSONArray("track"));
						break;

					case image:
						JSONArray imageUrls = JSONAlbum.getJSONArray("image");
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
					System.out.println("Illegal argument exception in Album(switchAlbumInfo):" + key);
				}
			}
		}
		catch (JSONException e) {
			System.out.println("JSONException in Album(public constructor)");
			e.printStackTrace();
		}		
	}

	public Album(String extraAlbum) {
		initialize();

		try {
			JSONObject JSONobject = new JSONObject(extraAlbum);
			JSONObject JSONAlbum = JSONobject.getJSONObject("album");
			switchAlbumInfo(JSONAlbum);

		} catch (JSONException e) {
			System.out.println("JSONException in Album(public constructor)");
			e.printStackTrace();
		}

	}

	public Album (JSONObject JSONAlbum) {
		initialize();
		switchAlbumInfo(JSONAlbum);
	}

	public String getAlbumTitle() {
		return albumTitle;
	}
	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}
	public String getArtistName() {
		return artistName;
	}
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	public String getImageL() {
		return imageL;
	}
	public void setImageL(String imageL) {
		this.imageL = imageL;
	}
	public int getPlaycount() {
		return playcount;
	}
	public void setPlaycount(int playcount) {
		this.playcount = playcount;
	}
	public int getListeners() {
		return listeners;
	}
	public void setListeners(int listeners) {
		this.listeners = listeners;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public JSONArray getTracks() {
		return tracks;
	}
	public void setTracks(JSONArray tracks) {
		this.tracks = tracks;
	}
	public String getMbid() {
		return mbid;
	}
}
