package it.polimi.tiw.ria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import it.polimi.tiw.ria.beans.Album;
import it.polimi.tiw.ria.beans.Song;
import it.polimi.tiw.ria.beans.SongOrder;

public class MatchDAO {
private Connection con = null;
	
	public MatchDAO(Connection con) {
		this.con = con;
	}
	
	
	
	public void updateSongOrder(int idPlaylist, SongOrder update) throws SQLException {		
		String query = "UPDATE MusicPlaylistDb.MatchOrder\n"
				+ "SET idSongBefore = ?\n"
				+ "WHERE idPlaylist = ? and idSong = ? and idSongBefore != ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, update.getIdSongBefore());
			pstatement.setInt(2, idPlaylist);
			pstatement.setInt(3, update.getIdSong());
			pstatement.setInt(4, update.getIdSongBefore());

			pstatement.executeUpdate();
		}
		
	}
	
	
	public HashMap<Song, Album> insertSongInPlaylist(int idSong, int idPlaylist) throws SQLException {
		HashMap<Song, Album> songAndAlbum = new HashMap<>();
        // for find the order position to assign
        PreparedStatement pstmt1 = null;
        
        PreparedStatement pstmt2 = null;
        
        //for insertion
        PreparedStatement pstmt3 = null;
        
        //for gettin song
        PreparedStatement pstmt4 = null;
        ResultSet rs = null;
        
        // for getting order
        ResultSet rsFindOrder = null;

        
        int idSongBefore = 0;
        try {
            // set auto commit to false
            con.setAutoCommit(false);

            //find order
            String query1  = "SELECT m.idSong FROM MusicPlaylistDb.MatchOrder as m, MusicPlaylistDb.Song as s, MusicPlaylistDb.Album as a\n"
            		+ "WHERE m.idPlaylist = ? and s.id = m.idSong and s.idAlbum = a.id and  a.year =\n"
            		+ "(SELECT MAX(a1.year)\n"
            		+ "FROM MusicPlaylistDb.MatchOrder as m1, MusicPlaylistDb.Song as s1, MusicPlaylistDb.Album as a1\n"
            		+ "WHERE m1.idPlaylist = m.idPlaylist and m1.idSong = s1.id and s1.idAlbum = a1.id and a1.year <= \n"
            		+ "(SELECT a2.year\n"
            		+ "FROM MusicPlaylistDb.Song as s2, MusicPlaylistDb.Album as a2\n"
            		+ "WHERE s2.id = ? and s2.idAlbum = a2.id))\n"
            		+ "LIMIT 1";
            
            pstmt1 = con.prepareStatement(query1);           
            pstmt1.setInt(1, idPlaylist);
            pstmt1.setInt(2, idSong);
            rsFindOrder = pstmt1.executeQuery();
            


            
            //if null == idSongBefore = 0
            if (rsFindOrder.next()) {
            	idSongBefore = rsFindOrder.getInt(1);
            }else {
            	idSongBefore = 0;

            }
            	
            String query2 = "UPDATE MusicPlaylistDb.MatchOrder as m\n"
            		+ "SET m.idSongBefore = ?\n"
            		+ "WHERE m.idPlaylist = ? and m.idSongBefore = ?";
            
            pstmt2 = con.prepareStatement(query2);
            pstmt2.setInt(1, idSong);
            pstmt2.setInt(2, idPlaylist);
            pstmt2.setInt(3, idSongBefore);
            pstmt2.executeUpdate();

        	

            
            String query3 = "INSERT INTO MusicPlaylistDb.MatchOrder VALUES (?, ?, ?, current_timestamp())";
            pstmt3 = con.prepareStatement(query3);
            
            pstmt3.setInt(1, idSong);
            pstmt3.setInt(2, idPlaylist);
            pstmt3.setInt(3, idSongBefore);

     
            int rowAdded = pstmt3.executeUpdate();


            if(rowAdded == 1) {
            	String query4 = "SELECT s.id, s.title, s.songUrl, a.id, a.title, a.interpreter, a.year, a.genre, a.imageUrl FROM MusicPlaylistDb.Song as s, MusicPlaylistDb.Album as a\n"
            			+ "WHERE s.id = ? and s.idAlbum = a.id";
            	pstmt4 = con.prepareStatement(query4);
            	pstmt4.setInt(1, idSong);
            	rs = pstmt4.executeQuery();

            	if (rs.next()) {

            		Song song = new Song();
            		song.setId(rs.getInt(1));
            		song.setTitle(rs.getString(2));
            		song.setSongUrl(rs.getString(3));
            		song.setIdAlbum(rs.getInt(4));
            		
            		Album album = new Album();
            		album.setId(rs.getInt(4));
            		album.setTitle(rs.getString(5));
            		album.setInterpreter(rs.getString(6));
            		album.setYear(rs.getShort(7));
            		album.setGenre(rs.getString(8));
            		album.setImageUrl(rs.getString(9));
            		songAndAlbum.put(song, album);
            		


            		
            	}else {
            		con.rollback();
            		return null;
            	}

            	con.commit();
            }else {
            	con.rollback();
            	return null;

            }
          
        } catch (SQLException ex) {
            // roll back the transaction
            try{
                if(con != null)
                    con.rollback();
            }catch(SQLException e){
            	throw new SQLException(e);
            }

        } finally {
            try {
                if(rsFindOrder != null)  rsFindOrder.close();
                if(pstmt1 != null) pstmt1.close();
                if(pstmt2 != null) pstmt2.close();
                if(pstmt3 != null) pstmt3.close();
                
            } catch (SQLException e) {
            }
        }
        return songAndAlbum;
	}

}
