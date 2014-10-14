package com.example.android.notepad;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class Codec {
	
	public static String encode(LinkedHashMap<Integer, NoteEntry> entries){
		String encoded = "";
		Set<Integer> keySet = entries.keySet();
		Iterator<Integer> keyIter = keySet.iterator();
		while(keyIter.hasNext()){
			Integer key = keyIter.next();
			NoteEntry entry = entries.get(key);
			
			encoded += entry.getId();
			encoded += "*";
			encoded += entry.getTitle();
			encoded += "*";
			encoded += entry.getNote();
			encoded += "|";
		}
		
		return encoded;
	}
	
	public static LinkedHashMap<Integer, NoteEntry> decode(String data){
		LinkedHashMap<Integer, NoteEntry> entries = new LinkedHashMap<Integer, NoteEntry>();
		String[] tokens = data.split("[|]");
		for(String token : tokens){
			String[] subTokens = token.split("[*]");
			NoteEntry entry;
			if(subTokens.length==3){
				entry = new NoteEntry(Integer.parseInt(subTokens[0]),
						subTokens[1], subTokens[2]);
			}else{
				entry = new NoteEntry(Integer.parseInt(subTokens[0]),
						subTokens[1]);
			}
			entries.put(new Integer(subTokens[0]), entry);
		}
		
		return entries;
	}
	
	public static String encode2(List<NoteEntry> entries){
		String encoded = "";
		for(NoteEntry entry : entries){
			encoded += entry.getId();
			encoded += "*";
			encoded += entry.getTitle();
			encoded += "*";
			encoded += entry.getNote();
			encoded += "|";
		}
		
		return encoded;
	}
	
	public static List<NoteEntry> decode2(String data){
		List<NoteEntry> entries = new ArrayList<NoteEntry>();
		
		String[] tokens = data.split("[|]");
		for(String token : tokens){
			String[] subTokens = token.split("[*]");
			NoteEntry entry;
			if(subTokens.length==3){
				entry = new NoteEntry(Integer.parseInt(subTokens[0]),
						subTokens[1], subTokens[2]);
			}else{
				entry = new NoteEntry(Integer.parseInt(subTokens[0]),
						subTokens[1]);
			}
			entries.add(entry);
		}
		
		return entries;
	}
	
	public static String encodePacket(int command, String message){
		String packet = "";
		packet += command;
		packet += "%" + message;
		return packet;
	}

}
