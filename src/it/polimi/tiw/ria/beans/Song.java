package it.polimi.tiw.ria.beans;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Song{
	
	private int id;
	private String title;
	private String songUrl;
	private int idAlbum;
	private int idSongBefore;
	private String dateAdding;
	
	
	
	public Song() {
		super();
	}
	
	public Song(String title, String songUrl, int idAlbum) {
		this.title = title;
		this.songUrl = songUrl;
		this.idAlbum = idAlbum;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getSongUrl() {
		return songUrl;
	}



	public void setSongUrl(String songUrl) {
		this.songUrl = songUrl;
	}



	public int getIdAlbum() {
		return idAlbum;
	}



	public void setIdAlbum(int idAlbum) {
		this.idAlbum = idAlbum;
	}

	public int getIdSongBefore() {
		return idSongBefore;
	}

	public void setIdSongBefore(int idSongBefore) {
		this.idSongBefore = idSongBefore;
	}

	public String getDateAdding() {
		return dateAdding;
	}

	public void setDateAdding(Timestamp dateAdding) {
		this.dateAdding = new SimpleDateFormat("dd-MM-yyyy").format(dateAdding);
	}
	
	
	
	
	
	
	
}
