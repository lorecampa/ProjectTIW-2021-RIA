package it.polimi.tiw.ria.beans;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Playlist {
	private int id;
	private String title;
	private int idCreator;
	private String date;
	
	public Playlist() {
		super();
	}
	
	public Playlist(String title, int idCreator) {
		this.title = title;
		this.idCreator = idCreator;
		setCurrentDate();
	}
	
	
	

	public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}




	public void setDate(String date) {
		this.date = date;
	}
	
	private void setCurrentDate() {
		this.date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	}




	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIdCreator() {
		return idCreator;
	}
	public void setIdCreator(int idCreator) {
		this.idCreator = idCreator;
	}
	
	
	public String getDate() {
		return date;
	}

	public void setDate(Timestamp timestamp) {
		String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(timestamp);
		this.date = formattedDate;
	}

	
	

}
