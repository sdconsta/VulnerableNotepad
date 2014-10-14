package com.example.android.notepad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client{
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String outMessage;
	private String inMessage;
	public static String serverIP = "";
	
 	Client(String outMessage){
 		this.outMessage = outMessage;
 	}
 	 	
 	public void run(){
 		//Create socket connection
 		try{
 			socket = new Socket(serverIP, 4321);
 			out = new PrintWriter(socket.getOutputStream(), true);
 			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 		} catch(UnknownHostException e) {
 			System.out.println("Unknown host: ");
 			System.exit(1);
 		} catch(IOException e) {
 			System.out.println("No I/O");
 			System.exit(1);
 		}
 		
 		try{
 			out.println(outMessage);
 			inMessage = in.readLine();
 		    System.out.println("Message received: " + inMessage);
 			
 			
 			out.close();
            in.close();
            socket.close();
 		}catch(IOException e){
 			System.out.println("IOException");
 		}
 		
	}
 	
 	public String getInMessage(){
 		return inMessage;
 	}
 	
 	 	
	public static void main(String args[])
	{
		Client client = new Client("bye");
		client.run();
	}
}
