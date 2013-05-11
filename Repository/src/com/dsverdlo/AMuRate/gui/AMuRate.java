package com.dsverdlo.AMuRate.gui;

import com.dsverdlo.AMuRate.services.DatabaseManager;

import android.app.Application;
/**
 * Not sure if this is even used...
 * TODO: find out and delete
 * 
 * @author David Sverdlov
 *
 */
public class AMuRate extends Application {
	 //will be lazily loaded...
		private DatabaseManager localConnection;

		public void onCreate(){
			super.onCreate();
			//database setup: this takes some time, show splashscreen first
			System.out.println("AMR: get local connection");
			this.getLocalConnection();
			System.out.println("AMR: got local connection");
		}
		/**
		 * lazy loading of the local database on its first use
		 * @return a connection to the local database (SQLite)
		 */
		public synchronized DatabaseManager getLocalConnection() {
			if (this.localConnection == null) {
				this.localConnection = new DatabaseManager(this.getApplicationContext());
			}
			return this.localConnection;
		}
}
