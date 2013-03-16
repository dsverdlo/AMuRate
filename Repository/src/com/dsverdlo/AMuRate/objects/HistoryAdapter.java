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
 * This adapter creates, reads and updates the history data in the 
 * History table
 * 
 * So far 2 kinds of history items exist:
 * 1. Search history (_id, name, title, date)
 * 2. Track history (_id, name, title, mbid, date)
 * Since these three have a lot of fields in common, they will be put into one table
 * 
 * @author David Sverdlov
 *
 */
public class HistoryAdapter {

	private SQLiteDatabase database;
	private DatabaseManager dbm;
	
	private final static int KEY_SEARCH = 1;
	private final static int KEY_TRACK = 2;

	private final static String TABLE_HISTORY = "history";
	
	private final static String COLUMN_ID = "_id";
	private final static String COLUMN_HISTORY_KEY = "history_key";
	private final static String COLUMN_DATE = "date";
	private final static String COLUMN_NAME = "name";
	private final static String COLUMN_TITLE = "title";
	private final static String COLUMN_MBID = "mbid";
	private final static String COLUMN_RATING = "rating";
	
	private final static String SQL_TABLE_CREATE = "create table " + TABLE_HISTORY + " (" +
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_HISTORY_KEY + " integer not null, " + 
			COLUMN_DATE + " date not null," +
			COLUMN_NAME + " text not null," +
			COLUMN_TITLE + " text not null," +
			COLUMN_MBID + " text not null" +
			");";
	
	public HistoryAdapter(Context context) {
		dbm = new DatabaseManager(context);
	}

	/**
	 * addHistorySearch adds a value to the local search history table.
	 * 
	 * @param name Given name
	 * @param title Given title
	 * @return id of the row in the database where the entry was placed.
	 */
	public long addHistorySearch(String name, String title) {
		database = dbm.getWritableDatabase();
		ContentValues values = new ContentValues();
	    values.put(COLUMN_HISTORY_KEY, KEY_SEARCH);
	    values.put(COLUMN_MBID, "");
	    values.put(COLUMN_NAME, name);
	    values.put(COLUMN_TITLE, title);
	    values.put(COLUMN_RATING, 0);
	    Date curr_date = new Date();
	    values.put(COLUMN_DATE, curr_date.toString());
	    long insertId = database.insert(TABLE_HISTORY, null, values);
		database.close();
		return insertId;
	}
	
	/**
	 * This function is called when a user views a track page.
	 * The details are kept in a table.
	 * 
	 * @param mbid
	 * @param name
	 * @param title
	 * @return row of inserted entry.
	 */
	public long addHistoryTrack(String mbid, String name, String title) {
		database = dbm.getWritableDatabase();
		ContentValues values = new ContentValues();
	    values.put(COLUMN_HISTORY_KEY, KEY_TRACK);
	    values.put(COLUMN_MBID, mbid);
	    values.put(COLUMN_NAME, name);
	    values.put(COLUMN_TITLE, title);
	    values.put(COLUMN_RATING, 0);
	    Date curr_date = new Date();
	    values.put(COLUMN_DATE, curr_date.toString());
	    long insertId = database.insert(TABLE_HISTORY, null, values);
		database.close();
		return insertId;
	}
	
	public Cursor getSearchHistory(String query) {
		database = dbm.getReadableDatabase();
		// NYI: TODO:
		return database.rawQuery(query, null);

	}
	
	public static String getSQLTableCreate() {
		return SQL_TABLE_CREATE;
	}

} 