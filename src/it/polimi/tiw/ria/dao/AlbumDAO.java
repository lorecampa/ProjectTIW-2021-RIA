package it.polimi.tiw.ria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import it.polimi.tiw.ria.beans.Album;
import it.polimi.tiw.ria.beans.User;

public class AlbumDAO {
private Connection con;
	
	public AlbumDAO(Connection con) {
		this.con = con;
	}
	
	
	
	
	public int createAlbum(Album album) throws SQLException {
		int result = 0;
		PreparedStatement pstm1 = null;
		PreparedStatement pstm2 = null;
		
		// for the new songId
        ResultSet rs = null;
		try {
			// set auto commit to false
            con.setAutoCommit(false);
            
            String query1 = "INSERT IGNORE INTO `MusicPlaylistDb`.`Album` (`title`, `interpreter`, `year`, `genre`, `idCreator`) VALUES(?, ?, ?, ?, ?);";
			pstm1 = con.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
			pstm1.setString(1, album.getTitle());
			pstm1.setString(2, album.getInterpreter());
			pstm1.setShort(3, album.getYear());
			pstm1.setString(4, album.getGenre());
			pstm1.setInt(5, album.getIdCreator());
			int rowAffected = pstm1.executeUpdate();
			
			// get song id
            rs = pstm1.getGeneratedKeys();
            int albumId = 0;
            if (rs.next()) {
            	albumId = rs.getInt(1);
            	album.setId(albumId);
            }
            
			if (rowAffected == 1) {
				//update songUrl
				String query2 = "UPDATE MusicPlaylistDb.Album as a\n"
						+ "SET a.imageUrl = ?\n"
						+ "WHERE a.id = ?;";
				
				pstm2 = con.prepareStatement(query2);
				String imageUrl = "albumImage_" + albumId + "" + album.getImageUrl();
				album.setImageUrl(imageUrl);
				pstm2.setString(1, imageUrl);
				pstm2.setInt(2, albumId);
				int rowUpdated = pstm2.executeUpdate();
				
				if (rowUpdated == 1) {
					con.commit();
					result = 1;
					
				}else {
					con.rollback();
				}
				
			}else {
				con.rollback();
			}
			
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if(rs != null)  rs.close();
				if(pstm1 != null) pstm1.close();
                if(pstm2 != null) pstm2.close();
			} catch (Exception e1) {

			}
		}
		return result;
		
	}
	
	
	
	//return null if there is not album
	public Album findAlumById(int albumId) throws SQLException {
		Album album = null;
		String query = "SELECT * FROM MusicPlaylistdb.Album WHERE id = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, albumId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					album = new Album();
					album.setId(albumId);
					album.setTitle(result.getString("title"));
					album.setInterpreter(result.getString("interpreter"));
					album.setYear(result.getShort("year"));
					album.setGenre(result.getString("genre"));
					album.setIdCreator(result.getInt("idCreator"));
					album.setImageUrl(result.getString("imageUrl"));
				}
			}
		}
		return album;

	}
	
	public ArrayList<Album> getAllUserAlbums(int userId) throws SQLException {
		ArrayList<Album> albums = new ArrayList<>();
		
		String query = "SELECT * FROM MusicPlaylistdb.Album WHERE idCreator = ?";
		
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Album album = new Album();
					album.setId(result.getInt("id"));
					album.setTitle(result.getString("title"));
					album.setInterpreter(result.getString("interpreter"));
					album.setYear(result.getShort("year"));
					album.setGenre(result.getString("genre"));
					album.setIdCreator(result.getInt("idCreator"));
					album.setImageUrl(result.getString("imageUrl"));
					
					albums.add(album);
				}
			}
		}
		
		return albums;
		
	}
	
	
	

}
	
