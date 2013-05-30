package com.dsverdlo.AMuRate.services;


import java.io.*;
import java.net.*;

import android.os.AsyncTask;

import com.dsverdlo.AMuRate.gui.TrackActivity;




/**
 * This is the client program which will communicate with the external server/database.
 * On a different thread, a socket is attempted to connect with the server
 * on port 2005 (for no reason, could be some other number also)
 * 
 * Code based on http://stackoverflow.com/questions/1776457/java-client-server-application-with-sockets
 * 
 * @author David Sverdlov
 *
 */

public class ExternalDatabaseConnect extends AsyncTask<String, Void, Double> {
	
	// private members
	private TrackActivity activity;
	private DatabaseSyncer syncer;
	private Socket requestSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String message;
	private int method;

	// request methods
	public static final int ISCONNECTED = 0;
	public static final int SENDRATING = 1;
	public static final int GETRATING = 2;
	public static final int GETAMOUNT = 3;
	public static final int HASRATED = 4;

	private int timeOut = 3000; // 3 seconds as i am inpatient
	private int portNo = 2005; 


	private String ipAddress;

	public ExternalDatabaseConnect(TrackActivity activity, String ip){ 
		this.activity = activity;
		this.ipAddress = ip;
	}


	public ExternalDatabaseConnect(DatabaseSyncer databaseSyncer, String ip) {
		this.syncer = databaseSyncer;
		this.ipAddress = ip;
	}


	private void setUpConnection() {
		try{
			//1. creating a socket to connect to the server

			System.out.println("[c]Connecting to "+ipAddress+" in port "+portNo);
			//here you must put your computer's IP address.
			InetAddress serverAddr = InetAddress.getByName(ipAddress);
			System.out.println("[c]Connectingg to "+serverAddr.toString()+" in port "+portNo);
			//			requestSocket = new Socket(serverAddr, portNo);

			Socket requestSocket = new Socket();
			requestSocket.connect(new InetSocketAddress(serverAddr, portNo), timeOut);

			System.out.println("[c]Connected to ^ in port " + portNo);
			//2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush(); 
			in = new ObjectInputStream(requestSocket.getInputStream());
			//3: Communicating with the server
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioe){
			//			ioException.printStackTrace();
			//Toast.makeText(activity.getApplicationContext(), "Exception in Client.java [setUpConnection]", Toast.LENGTH_SHORT).show();
			System.out.println("Exception in Client.java [setUpConnection]\n"+ioe);
		} catch(Exception e) {
			System.out.println("Exception in Client.java [setUpConnection]\n"+e);
		}
	}

	private void tearDownConnection() {
		//4: Closing connection
		try{
			if(in != null) in.close();
			if(out != null) out.close();
			if(requestSocket != null) requestSocket.close();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	private boolean sendRating(String mbid, String artist, String title, double rating, int date, String user) {
		boolean result = false;
		try{
		     
			setUpConnection();
			// Read connection status
			message = (String)in.readObject();
			System.out.println("[c]server>" + message);

			// Send request method
			sendMessage("POST");

			// Send parameters
			sendMessage(mbid);
			sendMessage(artist);
			sendMessage(title);
			sendMessage("" + rating);
			sendMessage("" + date);
			sendMessage(user);

			// Read result					
			result = ((String)in.readObject()).equals("true");
			System.out.println("[c]server>" + result);

		}
		catch(ClassNotFoundException classNot){
			System.err.println("data received in unknown format");
		}  catch (Exception e) {
			e.printStackTrace();
		} finally{
			tearDownConnection();
		}
		return result;
	}

	private double hasRated(String mbid, String user) {
		double result = -1;
		try{
			// Try setting up a connection
			setUpConnection();

			// Read connection status
			message = (String)in.readObject();
			System.out.println("[c]server>" + message);

			// Send request method
			sendMessage("HASRATED");

			// Send parameters
			sendMessage(mbid);
			sendMessage(user);

			// Read result					
			result = Float.parseFloat((String)in.readObject());
			System.out.println("[c]server>" + result);

		}
		catch (Exception e) {	e.printStackTrace();} 
		finally{ tearDownConnection(); }
		
		return result;
	}

	private boolean testConnection() {
		boolean isOnline = false;
		//
		try{
			//1. creating a socket to connect to the server

			System.out.println("[c]Connecting to "+ipAddress+" in port "+portNo);

			InetAddress serverAddr = InetAddress.getByName(ipAddress);
			System.out.println("[c]Connectingg to "+serverAddr.toString()+" in port "+portNo);

			Socket requestSocket = new Socket();
			requestSocket.connect(new InetSocketAddress(serverAddr, portNo), timeOut);

			/** */ //requestSocket = new Socket(serverAddr, 2005);
			System.out.println("[c]Connected to ... in port "+portNo);
			//2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush(); 
			in = new ObjectInputStream(requestSocket.getInputStream());
			//3: Communicating with the server
		}
		catch(IOException e){
			System.out.println("Exception in Client.java [setUpConnection]\n"+e);
		}
		//
		// Read connection status
		try {
			message = (String)in.readObject();
			// So far so good, we got a connection with local server
			// Now we test if the server can reach the database

			// POST ACTION TEST
			sendMessage("TEST");

			// Read server response
			String result = (String)in.readObject();

			// if result == "true", ext. db is online
			isOnline = result.equals("true");

		} catch (Exception e) {
			System.out.println("Exception in Client.java[testConnection]");
			return isOnline; 
		}
		System.out.println("[c]server>" + message);

		tearDownConnection();
		return isOnline;
	}

	private void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("[c]client>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	private double getRatingAvg(String mbid) {
		double result = -1;
		setUpConnection();
		try{
			// read connection status
			message = (String)in.readObject();
			System.out.println("[c]server>" + message);

			// send request method
			sendMessage("GET");
			sendMessage("average");

			// send parameters
			sendMessage(mbid);

			// read results
			result = Double.parseDouble((String)in.readObject());
			System.out.println("[c]server>" + result);

			// read second results TODO: remove here!!
			int amt = Integer.parseInt((String)in.readObject());
			// Since an avg rating is < 10, we can multiply the amount by 10
			// before we add it with the average to transport 2 numbers in 1
			// E.g; 512 ratings average to 3.5 --> 512*10 + 3.5 = 5123.5
			// to extract numbers: avg= mod( X, 10 )
			// amount = (X - avg) / 10

			result = result + (amt * 10) ;

		}
		catch(ClassNotFoundException classNot){
			System.err.println("data received in unknown format");
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(Exception xception){
			xception.printStackTrace();
		}
		finally{
			tearDownConnection();
		}
		return result;
	}


	protected void onPostExecute(Double result) {
		System.out.println("++*++*++*++ Finished asynctask! "+result);

		switch(method) {
		case SENDRATING: 
			// Send rating could alse be done by the DatabaseSyncer
			if(activity != null) { activity.onDoneSendingExternal(result); break; }
			if(syncer != null) { syncer.onDoneSendingSynced(result); break; }

		case GETRATING: activity.onDoneGettingExternal(result); break;
		case ISCONNECTED: 
			if(activity != null) {activity.onDoneTestingExternalConnection(result); break; }
			if(syncer != null) { syncer.onDoneTestingExternalConnection(result); break; }
			//case GETAMOUNT: activity.onDoneGettingExternalAmount(result); break;
		case HASRATED: { activity.onDoneCheckingHasRated(result); break; }
		default: return;
		}

	}

	protected Double doInBackground(String... params) {
		method = Integer.parseInt(params[0]);
		System.out.println("++*++*++*++ Starting a AsyncTask method=" + method);
		switch(method) {
		case SENDRATING:
			String mbid = params[1];
			String artist = params[2];
			String title = params[3];
			float rating = Float.parseFloat(params[4]);
			int date = Integer.parseInt(params[5]);
			String user = params[6];
			if(sendRating(mbid, artist, title, rating, date, user)) {
				return (double) 1;
			} else {
				return (double) -1;
			}
		case GETRATING:
			return getRatingAvg(params[1]);

		case ISCONNECTED:
			return (testConnection()) ? (double) 1 : (double) -1 ;

		case HASRATED:
			return hasRated(params[1], params[2]);
			
		default:
			return (double) -1;


		}
	}





}