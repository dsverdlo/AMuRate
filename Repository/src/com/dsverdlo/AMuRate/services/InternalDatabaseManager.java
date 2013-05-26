package com.dsverdlo.AMuRate.services;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DatabaseManager handles the internal database of the project.
 * It is an extension of the class SQLiteOpenHelper.
 * 
 * @author David Sverdlov
 *
 */
public class InternalDatabaseManager extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "AMuRate" + ".db";
	private static final int DATABASE_VERSION = 1;


	public InternalDatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(InternalDatabaseRatingAdapter.getSQLTableCreate());
		database.execSQL(InternalDatabaseHistoryAdapter.getSQLTableCreate());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(InternalDatabaseManager.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		onCreate(db);
	}


	
	
	
	
	


}

