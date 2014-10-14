package com.example.android.notepad;

public class ServiceReply {
	private boolean reply;
	
	public ServiceReply(){
		this.reply = false;
	}
	
	public void setReply(boolean b){
		this.reply = b;
	}
	
	public boolean getReply(){
		return this.reply;
	}
}
