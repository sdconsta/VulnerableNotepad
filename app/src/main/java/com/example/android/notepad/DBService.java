package com.example.android.notepad;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class DBService extends Service implements Command, ServerAddress{
	
	public static String serverIP = "";
		
	final Messenger myMessenger = new Messenger(new IncomingHandler());
	
	@Override
	public IBinder onBind(Intent intent) {
	      return myMessenger.getBinder();
	}
	
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {			
			
		}
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d("DBService", "onStartCommand");
		String command = intent.getStringExtra("command");
		
		if(command.equals("serverip")){
			
			serverIP = intent.getStringExtra("server");
			
		}else if(command.equals("delete")){			
			
			Uri mUri = intent.getData();					
			getContentResolver().delete(mUri, null, null);
			
			synchronized (NoteEditor.reply) {
				NoteEditor.reply.notify();	        		
        	}
		}
		
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	
}

