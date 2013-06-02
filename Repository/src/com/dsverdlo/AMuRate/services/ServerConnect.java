package com.dsverdlo.AMuRate.services;

import com.dsverdlo.AMuRate.gui.TrackActivity;

import android.os.AsyncTask;

public class ServerConnect {
	// request methods
	public static final int ISCONNECTED = 0;
	public static final int SENDRATING = 1;
	public static final int GETRATING = 2;
	public static final int GETAMOUNT = 3;
	public static final int HASRATED = 4;
	
	// Connection to be used
	private static final int USEMYSQL = 0;
	private static final int USEPHP = 1;
	
	// CHANGE THIS TO SWITCH CONNECTION TYPE
	private static final int USE = 0;
	// CHANGE THIS TO SWITCH CONNECTION TYPE
	
	AsyncTask<String, Void, Double> test = null;
	
	public ServerConnect(DatabaseSyncer databaseSyncer, String ip, int port, int method){
		switch(USE) {
		case USEMYSQL: test = new ServerConnectMySQL(databaseSyncer, ip, port, method); break;
		case USEPHP: test = new ServerConnectPHP(databaseSyncer, ip, method); break;
		default: test = new ServerConnectPHP(databaseSyncer, ip, method); break;
		} 
	}
	
	public ServerConnect(TrackActivity trackActivity, String ip, int port, int method){
		switch(USE) {
		case USEMYSQL: test = new ServerConnectMySQL(trackActivity, ip, port, method); break;
		case USEPHP: test = new ServerConnectPHP(trackActivity, ip, method); break;
		default: test = new ServerConnectPHP(trackActivity, ip, method); break;
		}
	}

	
	public void execute(String...strings) {

		// We cannot call the execute function with the strings
		// in given variable strings, so we append the strings 
		// into one using a delimiter which  we will send along
		// as the first parameter
		
		String delimiter = ";~:,.";
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < strings.length; i++) {
			str.append(strings[i]);
			str.append(delimiter); 
		}
		test.execute(delimiter, str.toString());
	}

	

}
