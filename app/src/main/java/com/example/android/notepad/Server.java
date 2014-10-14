package com.example.android.notepad;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import android.util.Log;

public class Server implements Command{

	//linux
	private static final String filename = "/tmp/notepad";
	
	//windows
	//private static final String filename = "C:\\tmp\\notepad";
	
	private LinkedHashMap<Integer, NoteEntry> entries;
	private File database;
	
	private ServerSocket server;
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	
	public Server(){
		
		if(initDatabase()){
			
			String data = readFile();
			if(data == "" || data == null){
				entries = new LinkedHashMap<Integer, NoteEntry>();
			}else{
				entries = Codec.decode(data);
			}
			
			//start server
			startServer();
		}
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Server server = new Server();

	}
	
	private void startServer(){
		try{
			server = new ServerSocket(4321); 
		} catch (IOException e) {
			System.out.println("Could not listen on port 4321");
		    System.exit(-1);
		}

		while(true){
			try{
			    client = server.accept();
			} catch (IOException e) {
			    System.out.println("Accept failed: 4321");
			    System.exit(-1);
			}
	
			try{
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out = new PrintWriter(client.getOutputStream(), true);
			} catch (IOException e) {
			    System.out.println("Read failed");
			    System.exit(-1);
			}
	
		
			try{
				String line;
				while((line = in.readLine())!=null){				
					System.out.println("Received: " + line);
					//Send data back to client
					out.println(respond(line));
				}
			} catch (IOException e) {
				System.out.println("Read failed");				
		        System.exit(-1);
			}
		}
	}
	
	private boolean initDatabase(){
		try{
			database = new File(filename);
			if(!database.exists()){
				database.createNewFile();
				System.out.println("Create new database file " + filename);
			}
			return true;
		}catch(IOException e){
			System.out.println("IOException. Fail to initialize database file.");
		}
		
		return false;
	}
	
	public String loadDB(){
		String encoded = readFile();
		return encoded;
	}
	
	public void saveDB(String encoded){
		writeFile(encoded);
	}
	
	public String readFile(){
		String data = "";
	    try{
	    	//File inFile = new File(filename);
	    	File inFile = database;
	    	BufferedReader br = new BufferedReader(new InputStreamReader(
	    			new FileInputStream(inFile)));
	    	String dataLine;
	    	
	    	if((dataLine = br.readLine()) != null){
	    		data += dataLine;
	    		
	    		while((dataLine = br.readLine()) != null){		    		
		    		data += "\n" + dataLine;
		    	}
	    	}	    	
	    	
	    	br.close();
	    }catch (FileNotFoundException ex) {
	    	return (null);
	    }catch (IOException ex) {
	    	return (null);
	    }
	    return (data);
	}
	
	public boolean writeFile(String dataLine) {
		try{
			//File outFile = new File(filename);
			File outFile = database;
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(outFile));
			dos.writeBytes(dataLine);
			dos.close();
		}catch(FileNotFoundException ex) {
			return (false);
		}catch(IOException ex) {
			return (false);
		}
		return (true);
	}
	
	public void addNewNoteEntry(NoteEntry entry){	
		
		String data = readFile();
		if(data == "" || data == null){
			entries = new LinkedHashMap<Integer, NoteEntry>();
		}else{
			entries = Codec.decode(data);
		}
		
		entries.put(new Integer(entry.getId()), entry);
		String encoded = Codec.encode(entries);
		writeFile(encoded);		
	}
	
	public NoteEntry queryNoteEntryById(int id){
		
		String data = readFile();
		if(data == "" || data == null){
			entries = new LinkedHashMap<Integer, NoteEntry>();
		}else{
			entries = Codec.decode(data);
		}
		
		NoteEntry entry = entries.get(new Integer(id));
		return entry;
	}
	
	public void updateNoteEntry(NoteEntry entry){
		Integer key = new Integer(entry.getId());
		
		String data = readFile();
		if(data == "" || data == null){
			entries = new LinkedHashMap<Integer, NoteEntry>();
		}else{
			entries = Codec.decode(data);
		}
		
		if(entries.containsKey(key)){
			NoteEntry oldEntry = entries.get(key);
			oldEntry.update(entry);
		}else{
			entries.put(key, entry);
		}
		
		String encoded = Codec.encode(entries);
		writeFile(encoded);
	}
	
	public void removeNoteEntryById(int id){
		Integer key = new Integer(id);
		
		String data = readFile();
		if(data == "" || data == null){
			entries = new LinkedHashMap<Integer, NoteEntry>();
		}else{
			entries = Codec.decode(data);
		}
		
		entries.remove(key);
		
		List<Integer> toRemove = new ArrayList<Integer>();
		List<NoteEntry> toAdd = new ArrayList<NoteEntry>();
		
		Set<Integer> keySet = entries.keySet();
		Iterator<Integer> keyIter = keySet.iterator();
		
		
		while(keyIter.hasNext()){
			key = keyIter.next();
			if(key.intValue()>id){
				toRemove.add(key);
				NoteEntry entry = entries.get(key);
				toAdd.add(new NoteEntry((key.intValue()-1), entry.getTitle(), entry.getNote()));
			}
		}
		for(Integer i : toRemove){
			entries.remove(i);
		}
		for(NoteEntry e : toAdd){
			entries.put(new Integer(e.getId()), e);
		}
		
		String encoded = Codec.encode(entries);
		writeFile(encoded);
	}
	
	public String respond(String packet){
		String response = "";
		String[] tokens = packet.split("[%]");
		switch(Integer.parseInt(tokens[0]))
		{
			case SAVE:
			{
				String encoded = tokens[1];
				writeFile(encoded);
				response = "update succeeded";
				break;
			}
			
			case LOAD:
			{
				response = loadDB();
				break;
			}
			
			case UPDATE:
			{
				String encoded = tokens[1];
				System.out.println("update entry: encoded-" + encoded);
				List<NoteEntry> updateEntries = Codec.decode2(encoded);
				for(NoteEntry entry : updateEntries){
					System.out.println("update entry:" + entry.getId() + "*" + entry.getTitle() + "*" + entry.getNote());
					updateNoteEntry(entry);
				}
				break;
			}
			
			case INSERT:
			{
				String encoded = tokens[1];
				System.out.println("insert entry: encoded-" + encoded);
				List<NoteEntry> insertEntries = Codec.decode2(encoded);
				for(NoteEntry entry : insertEntries){
					System.out.println("insert entry:" + entry.getId() + "*" + entry.getTitle() + "*" + entry.getNote());
					addNewNoteEntry(entry);
				}
				break;
			}
			
			case DELETE:
			{
				String encoded = tokens[1];
				System.out.println("insert entry: encoded-" + encoded);
				List<NoteEntry> deleteEntries = Codec.decode2(encoded);
				for(NoteEntry entry : deleteEntries){
					System.out.println("delete entry:" + entry.getId() + "*" + entry.getTitle() + "*" + entry.getNote());
					removeNoteEntryById(entry.getId());
				}
				break;
			}
			
			default:
			{
				
			}
					
		}
		return response;
	}

}
