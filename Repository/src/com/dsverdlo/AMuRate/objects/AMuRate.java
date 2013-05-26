package com.dsverdlo.AMuRate.objects;

import com.dsverdlo.AMuRate.services.InternalDatabaseManager;

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
		private InternalDatabaseManager localConnection;
		private String ip;
		
		public void onCreate(){
			super.onCreate();
			//database setup: this takes some time, show splashscreen first
			System.out.println("AMR: get local connection");
			this.getLocalConnection();
			System.out.println("AMR: got local connection");
			
//			ip = "localhost"; // local
//			ip = "81.164.233.130"; // thuis
//			ip = "134.184.120.178"; // kot 
//			ip = "10.2.33.36"; // urbizone
//			ip = "134.184.108.145"; // edoroam
//			ip = "134.184.140.70"; // vubnet
			ip = "194.168.5.43"; // 3G
//			ip = "10.0.1.97"; // como
		}
		/**
		 * lazy loading of the local database on its first use
		 * @return a connection to the local database (SQLite)
		 */
		public synchronized InternalDatabaseManager getLocalConnection() {
			if (this.localConnection == null) {
				this.localConnection = new InternalDatabaseManager(this.getApplicationContext());
			}
			return this.localConnection;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getIp() {
			return ip;
		}
}
