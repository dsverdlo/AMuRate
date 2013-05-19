package com.dsverdlo.AMuRate.services;

import com.dsverdlo.AMuRate.objects.HistoryAdapter;
import com.dsverdlo.AMuRate.objects.RatingAdapter;

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
public class DatabaseManager extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "AMuRate" + ".db";
	private static final int DATABASE_VERSION = 1;


	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(RatingAdapter.getSQLTableCreate());
		database.execSQL(HistoryAdapter.getSQLTableCreate());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseManager.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		onCreate(db);
	}


	
	
	
	
	


}

