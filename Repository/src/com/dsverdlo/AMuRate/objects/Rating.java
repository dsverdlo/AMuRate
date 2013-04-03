package com.dsverdlo.AMuRate.objects;

/**
 * Abstract Data Type for a rating.
 * A Rating consists of the following members:
 * - MBID of the song
 * - Title of the song
 * - Artist of the song
 * - Rating (float 0-5, step 0.5)
 * - Date (int unix timestamp)
 * - User id of user
 * 
 * @author David Sverdlov
 * @version 1.0
 *
 */
public class Rating {
	String mbid, title, artist, user;
	int date;
	float rating;
	
	public Rating() {
		mbid = "";
		title = "";
		artist = "";
		user = "";
		date = (int) (System.currentTimeMillis() / 1000L);
		rating = 0;
	}

	public String getMbid() {
		return mbid;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}
	
	public int getDate() {
		return date;
	}

}
