package com.dsverdlo.AMuRate;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable {
	// Private vars
	String title, artist, album;
	String mbid, url;
	int duration, listeners, playcount;
	
	
	public Track() {
		
	}
	
	public Track(JSONObject json) {
		try {
			json.getInt("duration");
			
		} catch (JSONException je) {
			System.out.println("JSONException in Track(public constructor)");
			je.printStackTrace();
		}
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
}
