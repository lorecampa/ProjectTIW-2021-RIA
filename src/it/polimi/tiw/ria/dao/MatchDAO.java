package it.polimi.tiw.ria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.ria.beans.Playlist;

public class MatchDAO {
private Connection con = null;
	
	public MatchDAO(Connection con) {
		this.con = con;
	}
	
	
	
	public int insertSongInPlaylist(int idSong, int idPlaylist) throws SQLException {
		int result = 0;
        // for find the order position to assign
        PreparedStatement pstmt1 = null;
        
        // for update all order number >=
        PreparedStatement pstmt2 = null;
        
        //for insertion
        PreparedStatement pstmt3 = null;
        
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
            	con.commit();
            	result = 1;

            }else {
            	con.rollback();

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
        return result;
	}

}
