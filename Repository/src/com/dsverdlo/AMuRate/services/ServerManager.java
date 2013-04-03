package com.dsverdlo.AMuRate.services;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Enumeration;

import com.dsverdlo.AMuRate.objects.Rating;


/**
 * ServerManager is the service that offers the application ways of 
 * communicating with a/the server to send and retrieve data.
 * 
 * The ratings of all users are collected on the server.
 * 
 * @author David Sverdlov
 * @version 1.0
 */

public class ServerManager {
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	public ServerManager() {
		// are you gonna do something? maybe a set up?
		
	}
	
	public boolean isConnected() {
		// Test to check whether the server is connected
		connectServer();
		disconnectServer();
		return true;
	}

	public int sendRating(Rating rat) {
		connectServer();
		int result = -1;
		try {
			stmt = conn.createStatement();
			result = stmt.executeUpdate("INSERT INTO amurate.Ratings " +
					"(MBID, Artist, Title, Rating, Date, User )" +
					" VALUES (" + 					
					rat.getMbid() + ", " + 
					rat.getArtist() + ", " +
					rat.getTitle() + ", " +
					rat.getRating() + ", " + 
					rat.getDate() + ", " +
					rat.getUser() 
					+ ")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		disconnectServer();
		return result;
	}
	
	public float getRatingAvg(String mbid) {
		if(!connectServer()) {
			System.out.println("Cannot connect...");
			return 0;
		}
		float result = -1;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT AVG(Rating) FROM amurate.ratings WHERE MBID = " + mbid + ";");
			result = rs.getFloat(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: close rs and stmt?
		disconnectServer();
		return result;
	}


	public int getRatingAmt(String mbid) {	
		System.out.println("Drivers: " + DriverManager.getDrivers().toString());
		Enumeration<Driver> en = DriverManager.getDrivers();
		while(en.hasMoreElements()) {
			Driver d = en.nextElement();
			try {
				System.out.println("\nD: " + d.acceptsURL("jdbc:mysql://localhost/amurate?user=amurate&password=AMuRate"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!connectServer()) {
		System.out.println("Cannot connect...");
		return 0;
	}
		int result = -1;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) FROM amurate.ratings WHERE MBID = " + mbid + ";");
			result = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: close rs and stmt?
		disconnectServer();
		return result;
	}
	
	private boolean connectServer() {
		try {
			conn =
					DriverManager.getConnection("jdbc:mysql://localhost/amurate" + "?" +
							"user=root&password=AMuRate");
			return true;
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}
	}
	
	private void disconnectServer() {
		if(conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				conn = null;
			}
		}
	}

}
