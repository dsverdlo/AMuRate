package com.dsverdlo.AMuRate.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DatabaseManager handles the database portion of the project.
 * 
 * @author David Sverdlov
 *
 */
public class DatabaseManager extends SQLiteOpenHelper {

	public static final String TABLE_RATINGS = "ratings";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MBID = "mbid";
	public static final String COLUMN_RATING = "rating";

	private static final String DATABASE_NAME = TABLE_RATINGS + ".db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + 
			TABLE_RATINGS + "(" + 
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_MBID + " text not null, " + 
			COLUMN_RATING + " real not null" + 
			")";

	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseManager.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATINGS);
		onCreate(db);
	}

	public float readRatingAvg(SQLiteDatabase db, String mbid) {
		System.out.println("DB: reading avg rating");
		String sqlReadRating = "SELECT AVG(rating) FROM ratings WHERE mbid='%s';";
		Cursor results = db.rawQuery(String.format(sqlReadRating, mbid), null);
		if(results != null) {
			if(results.moveToFirst()) {
				return results.getFloat(0);
			} else return -1;
		} else {
			// Sql error
			System.out.println("SQL error in readRating");
			return -1;
		}
	}
	public int readRatingAmount(SQLiteDatabase db, String mbid) {
		System.out.println("DB: reading amount ratings");
		String sqlReadRatingAmount = "SELECT COUNT(*) FROM ratings WHERE mbid='%s';";
		Cursor results = db.rawQuery(String.format(sqlReadRatingAmount, mbid), null);
		if(results != null && results.moveToFirst()) return results.getInt(0);
		return -1;
	}

	public long addRating(SQLiteDatabase db, String mbid, float rating) {
		ContentValues values = new ContentValues();
	    values.put(DatabaseManager.COLUMN_RATING, rating);
	    values.put(DatabaseManager.COLUMN_MBID, mbid);
	    long insertId = db.insert(DatabaseManager.TABLE_RATINGS, null, values);
	    return insertId;
	}



}

