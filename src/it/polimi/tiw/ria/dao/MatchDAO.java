package it.polimi.tiw.ria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import it.polimi.tiw.ria.beans.Album;
import it.polimi.tiw.ria.beans.Playlist;
import it.polimi.tiw.ria.beans.Song;

public class MatchDAO {
private Connection con = null;
	
	public MatchDAO(Connection con) {
		this.con = con;
	}
	
	
	
	public HashMap<Song, Album> insertSongInPlaylist(int idSong, int idPlaylist) throws SQLException {
		HashMap<Song, Album> songAndAlbum = new HashMap<>();
        // for find the order position to assign
        PreparedStatement pstmt1 = null;
        
        // for update all order number >=
        PreparedStatement pstmt2 = null;
        
        //for insertion
        PreparedStatement pstmt3 = null;
        
        //for gettin song
        PreparedStatement pstmt4 = null;
        ResultSet rs = null;
        
        // for getting order
        ResultSet rsFindOrder = null;

        
        int orderToInsert = 0;
        try {
            // set auto commit to false
            con.setAutoCommit(false);
            
            //find order
            String query1  = "SELECT IFNULL(max(m.order), 1) FROM MusicPlaylistDb.MatchOrder as m\n"
            		+ "WHERE m.idPlaylist = ? and m.dateAdding < current_timestamp()\n"
            		+ "ORDER BY m.dateAdding DESC\n"
            		+ "LIMIT 1";
            
            pstmt1 = con.prepareStatement(query1);           
            pstmt1.setInt(1, idPlaylist);
            
            rsFindOrder = pstmt1.executeQuery();
            
            //if null == 1
            rsFindOrder.next();
        	orderToInsert = rsFindOrder.getInt(1);

            
            //update
            String query2 = "UPDATE MusicPlaylistDb.MatchOrder as m\n"
            		+ "SET m.order = (m.order + 1)\n"
            		+ "WHERE m.order >= ?;";
            
            pstmt2 = con.prepareStatement(query2);
            pstmt2.setInt(1, orderToInsert);
            pstmt2.executeUpdate();
            
            
            String query3 = "INSERT INTO MusicPlaylistDb.MatchOrder VALUES (?, ?, ?, current_timestamp())";
            pstmt3 = con.prepareStatement(query3);
            
            pstmt3.setInt(1, idSong);
            pstmt3.setInt(2, idPlaylist);
            pstmt3.setInt(3, orderToInsert);
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
