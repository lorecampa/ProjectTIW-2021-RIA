package it.polimi.tiw.ria.beans;

public class Song{
	private int idSong;
	private String titleSong;
	private String songUrl;
	private String titleAlbum;
	private String interpreterAlbum;
	private short yearAlbum;
	private String genreAlbum;
	private Integer idCreator;
	private String imageUrl;
	
	
	public Song() {
		super();
	}
	
	
	
	public int getIdSong() {
		return idSong;
	}



	public void setIdSong(int idSong) {
		this.idSong = idSong;
	}



	public String getTitleSong() {
		return titleSong;
	}
	public void setTitleSong(String titleSong) {
		this.titleSong = titleSong;
	}
	public String getSongUrl() {
		return songUrl;
	}
	public void setSongUrl(String songUrl) {
		this.songUrl = songUrl;
	}
	public String getTitleAlbum() {
		return titleAlbum;
	}
	public void setTitleAlbum(String titleAlbum) {
		this.titleAlbum = titleAlbum;
	}
	public String getInterpreterAlbum() {
		return interpreterAlbum;
	}
	public void setInterpreterAlbum(String interpreterAlbum) {
		this.interpreterAlbum = interpreterAlbum;
	}
	public short getYearAlbum() {
		return yearAlbum;
	}
	public void setYearAlbum(short yearAlbum) {
		this.yearAlbum = yearAlbum;
	}
	public String getGenreAlbum() {
		return genreAlbum;
	}
	public void setGenreAlbum(String genreAlbum) {
		this.genreAlbum = genreAlbum;
	}
	public Integer getIdCreator() {
		return idCreator;
	}
	public void setIdCreator(Integer idCreator) {
		this.idCreator = idCreator;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	

	
	
	
	
	
}
