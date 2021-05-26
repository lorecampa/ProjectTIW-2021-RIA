package it.polimi.tiw.ria.messages;
import it.polimi.tiw.ria.beans.User;

public class UserInfo {
	private final String username;
	private final int id;
	
	public UserInfo(User user) {
		this.username = user.getUsername();
		this.id = user.getId();
	}

	public String getUsername() {
		return username;
	}

	public int getId() {
		return id;
	}
	
	

}
