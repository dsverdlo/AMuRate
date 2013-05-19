package com.dsverdlo.AMuRate.services;
import java.io.*;
import java.net.*;

import com.dsverdlo.AMuRate.gui.TrackActivity;

import android.os.AsyncTask;

/**
 * This is the client program which will communicate with the external server/database.
 * On a different thread, a socket is attempted to connect with the server
 * on port 2005 (for no reason, could be some other number also)
 * 
 * Code copied from http://stackoverflow.com/questions/1776457/java-client-server-application-with-sockets
 * 
 * @author David Sverdlov
 *
 */

public class ClientOld extends AsyncTask<String, Void, Double> {
	private TrackActivity activity;
	private Socket requestSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String message;
	private int method;

	// Requests that the client supports
	public static final int ISCONNECTED = 0;
	public static final int SENDRATING = 1;
	public static final int GETRATING = 2;
	public static final int GETAMOUNT = 3;
	
	private int timeOut = 7000; // 7 seconds
	private int portNo = 2005; 
	
	private String ipAddress = "localhost"; // local
	//private String ipAddress = "134.184.120.178"; // kot 
//	private String ipAddress = "10.2.33.36"; // urbizone
	//private String ipAddress = "134.184.108.145"; // edoroam
	//private String ipAddress = "134.184.140.70"; // vubnet
	//private String ipAddress = "194.168.5.43"; // 3G
	//private String ipAddress = "10.0.1.97"; // como
	
	public ClientOld(TrackActivity activity){ this.activity = activity; }


	private void setUpConnection() {
		try{
			
			System.out.println("[c] Creating a socket to connect to "+ipAddress+" in port "+portNo);
			
			InetAddress serverAddr = InetAddress.getByName(ipAddress);
			System.out.println("[c] Connecting to "+serverAddr.toString()+" in port "+portNo);
			
			requestSocket = new Socket();
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

	private boolean sendRating(String mbid, String artist, String title, double rating, int date, String user)
	{
		boolean result = false;
		setUpConnection();
		try{
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
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			tearDownConnection();
		}
		return result;
	}

	private boolean testConnection() {
		boolean isOnline = false;
		setUpConnection();
		
		try {
			// Read connection status
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
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			tearDownConnection();
		}
		return result;
	}
	

	protected void onPostExecute(Double result) {
		System.out.println("++*++*++*++ Finished asynctask!");
		
		switch(method) {
		case SENDRATING: activity.onDoneSendingExternal(); break;
		case GETRATING: activity.onDoneGettingExternal(result); break;
		case ISCONNECTED: 
			System.out.println("? Test connection resulted in: " + result);
			activity.onDoneTestingExternalConnection(result); break;
		//case GETAMOUNT: activity.onDoneGettingExternalAmount(result); break;
		default: return;
		}
		
	}

	@Override
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
			if(sendRating(mbid, artist, title, rating, date, user)) return (double) 1;
			return (double) -1;
		case GETRATING:
			return getRatingAvg(params[1]);

		case ISCONNECTED:
			return (testConnection()) ? (double) 1 : (double) -1 ;
		default:
			return (double) -1;

		}
	}
}