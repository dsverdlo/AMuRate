package com.dsverdlo.AMuRate.objects;

import com.dsverdlo.AMuRate.services.DatabaseManager;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class is an adapter separating the GUI from the database code.
 * It allows to open, close and read/write from the database.
 * We can add ratings or retrieve average ratings from unique song id's 
 * (mbid).
 * 
 * @author David Sverdlov
 *
 */
public class RatingAdapter {

	private SQLiteDatabase database;
	private DatabaseManager dbm;

	public RatingAdapter(Context context) {
		dbm = new DatabaseManager(context);
	}

	public void open() throws SQLException {
		database = dbm.getWritableDatabase();
	}

	public void close() {
		dbm.close();
	}

	public long addRating(String mbid, float rating) {
		return dbm.addRating(database, mbid, rating);
	}

	public int getRatingAmount(String mbid) {
		return dbm.readRatingAmount(database, mbid);
	}

	public float getRatingAvg(String mbid) {
		return dbm.readRatingAvg(database, mbid);
	}



} 