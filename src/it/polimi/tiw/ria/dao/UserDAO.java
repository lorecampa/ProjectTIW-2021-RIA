package it.polimi.tiw.ria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.ria.beans.User;

public class UserDAO {
	private Connection con;
	
	public UserDAO(Connection con) {
		this.con = con;
	}
	
	public User checkCredentials(String email, String pwd) throws SQLException {
		String query = "SELECT * FROM MusicPlaylistdb.user WHERE email = ? and password = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, email);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					user.setEmail(email);
					user.setPassword(pwd);
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}
	
	
	public int createUser(User user) throws SQLException {
		int code = 0;
		String query = "INSERT IGNORE INTO MusicPlaylistdb.user (username, email, password, name, surname)   VALUES(?, ?, ?, ?, ?);";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, user.getUsername());
			pstatement.setString(2, user.getEmail());
			pstatement.setString(3, user.getPassword());
			pstatement.setString(4, user.getName());
			pstatement.setString(5, user.getSurname());
			code = pstatement.executeUpdate();
		}
		
		return code;
	}

}
