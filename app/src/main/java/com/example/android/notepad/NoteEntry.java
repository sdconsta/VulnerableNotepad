package com.example.android.notepad;

public class NoteEntry {

	private int id;
	private String title;
	private String note;
	
	public NoteEntry(int id, String title){
		this.id = id;
		this.title = title;
		this.note = "";
	}
	
	public NoteEntry(int id, String title, String note){
		this.id = id;
		this.title = title;
		this.note = note;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getNote(){
		return this.note;
	}
	
	public void updateNote(String note){
		this.note = note;
	}
	
	public void updateTitle(String title){
		this.title = title;
	}
	
	public void update(NoteEntry entry){
		this.title = entry.getTitle();
		this.note = entry.getNote();
	}
	
	public void decrementId(){
		this.id--;
	}
}
