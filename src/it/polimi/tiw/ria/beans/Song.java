package it.polimi.tiw.ria.beans;

public class Song{
	
	private int id;
	private String title;
	private String songUrl;
	private int idAlbum;
	
	
	
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
	
	
	
	
}
