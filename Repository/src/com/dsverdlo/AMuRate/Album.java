package com.dsverdlo.AMuRate;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Album {
	String albumTitle, artistName;
	int listeners, playcount;
	String imageS, imageM, imageL;
	JSONArray tracks;
	String summary;
	
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
		tracks = new JSONArray();
	}
	public Album(String extraAlbum) {
		initialize();
		
		try {
			JSONObject JSONobject = new JSONObject(extraAlbum);
			JSONObject JSONAlbum = JSONobject.getJSONObject("album");
			Iterator<?> it = JSONAlbum.keys();
			while(it.hasNext()) {
				try { 
					String key = (String) it.next();
					System.out.println("888:" + key);
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
						
					case wiki:
						JSONObject JSONwiki = JSONAlbum.getJSONObject("wiki");
						summary = JSONwiki.getString("summary");
						break;
					case tracks:
						tracks = JSONAlbum.getJSONObject("tracks").getJSONArray("track");
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
					System.out.println("Error: illegal argument exception in Album");
				}
			}

		} catch (JSONException e) {
			System.out.println("JSONException in Album(public constructor)");
			e.printStackTrace();
		}
		
	}
}
