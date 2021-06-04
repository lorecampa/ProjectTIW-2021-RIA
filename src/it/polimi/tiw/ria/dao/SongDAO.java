package it.polimi.tiw.ria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import it.polimi.tiw.ria.beans.Song;

public class SongDAO {
private Connection con;
	
	public SongDAO (Connection con) {
		this.con = con;
	}
	
	
	public int createSong(Song song) throws SQLException {
		int result = 0;
		PreparedStatement pstm1 = null;
		PreparedStatement pstm2 = null;
		
		// for the new songId
        ResultSet rs = null;
		try {
			// set auto commit to false
            con.setAutoCommit(false);
            
            String query1 = "INSERT IGNORE INTO `MusicPlaylistDb`.`Song` (`title`, `idAlbum`) VALUES (?, ?);";
			pstm1 = con.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
			pstm1.setString(1, song.getTitle());
			pstm1.setInt(2, song.getIdAlbum());
			int rowAffected = pstm1.executeUpdate();
			
			// get song id
            rs = pstm1.getGeneratedKeys();
            int songId = 0;
            if (rs.next()) {
            	songId = rs.getInt(1);
            	song.setId(songId);
            }
            
			if (rowAffected == 1) {
				//update songUrl
				String query2 = "UPDATE MusicPlaylistDb.Song as s\n"
						+ "SET s.songUrl = ?\n"
						+ "WHERE s.id = ?;";
				
				pstm2 = con.prepareStatement(query2);
				String songUrl = "songAudio_" + songId + "_" + song.getIdAlbum()+""+song.getSongUrl();
				song.setSongUrl(songUrl);
				pstm2.setString(1, songUrl);
				pstm2.setInt(2, songId);
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
	
	//return null if song is not present
	public Song findSongById(int songId) throws SQLException {
		Song song = null;
		String query = "SELECT * FROM MusicPlaylistdb.Song WHERE id = ?";

		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, songId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				
				if (result.next()) {
					song = new Song();
					song.setId(songId);
					song.setTitle(result.getString("title"));
					song.setSongUrl(result.getString("songUrl"));
					song.setIdAlbum(result.getInt("idAlbum"));
				}
			}
		}
		
		
		return song;
	}
	
	
	
}
