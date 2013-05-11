package com.dsverdlo.AMuRate.objects;

import java.util.Date;

import com.dsverdlo.AMuRate.services.DatabaseManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	
	// SQL Statements
	private static final String TABLE_RATINGS = "ratings";
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_MBID = "mbid";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_RATING = "rating";
	private static final String COLUMN_DATE = "date";
	

	private static final String TABLE_CREATE = "create table IF NOT EXISTS " + 
			TABLE_RATINGS + " ( " + 
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_MBID + " text not null, " + 
			COLUMN_NAME + " text not null, " +
			COLUMN_TITLE + " text not null, " +
			COLUMN_RATING + " real not null, " + 
			COLUMN_DATE + " date not null " +
			")";

	// SQL Statements
	private static final String SQL_READ_RATING_AVG = "SELECT AVG(rating) FROM ratings WHERE mbid='%s';";
	private static final String SQL_READ_RATING_AMOUNT = "SELECT COUNT(*) FROM ratings WHERE mbid='%s';";
	
	
	/**
	 * RatingAdapter Public constructor for the class.
	 * @param context Context of the application
	 */
	public RatingAdapter(Context context) {
		dbm = new DatabaseManager(context);
	}

	/**
	 * readRatingAvg Reads the average rating of a song from the local database.
	 * @param mbid Unique identifier of the song.
	 * @return float Rating Average. Value is -1 in case something went wrong.
	 */
	public float readRatingAvg(String mbid) {
		database = dbm.getWritableDatabase();
		System.out.println("internal DB: reading avg rating");
		Cursor results = database.rawQuery(String.format(SQL_READ_RATING_AVG, mbid), null);
		if(results != null) {
			if(results.moveToFirst()) {
				database.close();
				return results.getFloat(0);
			} else {
				database.close();
				return -1; 
			}
		} else {
			// Sql error
			database.close();
			System.out.println("SQL error in readRating");
			return -1;
		}
	}
	
	/**
	 * readRatingAmount Get the amount of ratings for a particular song
	 * @param mbid The unique identifier of the song.
	 * @return int The total amount of ratings for the song. If not found -1.
	 */
	public int readRatingAmount(String mbid) {
		database = dbm.getWritableDatabase();
		System.out.println("internal DB: reading amount ratings");
		Cursor results = database.rawQuery(String.format(SQL_READ_RATING_AMOUNT, mbid), null);
		if(results != null && results.moveToFirst()) {
			int ret = results.getInt(0);
			database.close();
			return ret;
		}
		database.close();
		return -1;
	}

	/**
	 * addRating Add a rating to the local database.
	 * @param mbid The unique identifier of the song.
	 * @param name The name of the artist of the song
	 * @param title The title of the song to be added
	 * @param rating This float is the given rating 
	 * @return insertID The id of the filled row.
	 */
	public long addRating(String mbid, String name, String title, float rating) {
		database = dbm.getWritableDatabase();
		ContentValues values = new ContentValues();
	    values.put(COLUMN_RATING, rating);
	    values.put(COLUMN_MBID, mbid);
	    values.put(COLUMN_NAME, name);
	    values.put(COLUMN_TITLE, title);
	    Date curr_date = new Date();
	    values.put(COLUMN_DATE, curr_date.toString());
	    long insertId = database.insert(TABLE_RATINGS, null, values);
	    database.close();
	    return insertId;
	}

	/**
	 * getSQLTableCreate Retrieve the SQL script to create the table.
	 * @return SQL_TABLE_CREATE
	 */
	public static String getSQLTableCreate() {
		return TABLE_CREATE;
	}

} 