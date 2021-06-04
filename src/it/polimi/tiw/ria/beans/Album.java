package it.polimi.tiw.ria.beans;

public class Album {
	private int id;
	private String title;
	private String interpreter;
	private Short year;
	private String genre;
	private int idCreator;
	private String imageUrl;
	
	public Album() {
		super();
	}
	public Album(String title, String interpreter, short year, String genre, int idCreator, String imageUrl) {
		this.title = title;
		this.interpreter = interpreter;
		this.year = year;
		this.genre = genre;
		this.idCreator = idCreator;
		this.imageUrl = imageUrl;
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
	
	public String getInterpreter() {
		return interpreter;
	}
	public void setInterpreter(String interpreter) {
		this.interpreter = interpreter;
	}
	public Short getYear() {
		return year;
	}
	public void setYear(Short year) {
		this.year = year;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public int getIdCreator() {
		return idCreator;
	}
	public void setIdCreator(int idCreator) {
		this.idCreator = idCreator;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	
	
	
	

}
