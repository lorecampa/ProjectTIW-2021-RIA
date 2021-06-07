package it.polimi.tiw.ria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import it.polimi.tiw.ria.beans.Album;
import it.polimi.tiw.ria.beans.Playlist;
import it.polimi.tiw.ria.beans.Song;

public class PlaylistDAO {
	private Connection con = null;
	
	public PlaylistDAO(Connection con) {
		this.con = con;
	}
	
	
	
	public ArrayList<Playlist> findUserPlaylists(int userId) throws SQLException{
		ArrayList<Playlist> playlists = new ArrayList<>();
		
		String query = "SELECT * FROM MusicPlaylistdb.Playlist WHERE idCreator = ? ORDER BY dateCreation";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Playlist playlist = new Playlist();
					playlist.setId(result.getInt(1));
					playlist.setTitle(result.getString(2));
					playlist.setIdCreator(result.getInt(3));
					playlist.setDate(result.getTimestamp(4));
					playlists.add(playlist);
				}
			}
		}
		
		return playlists;
	}
	
	
	public HashMap<Song, Album> findPlaylistSongs(int idPlaylist) throws SQLException{
		HashMap<Song, Album> songs = new HashMap<>();
		
		String query = "SELECT s.id, s.title, s.songUrl , m.idSongBefore, m.dateAdding, s.idAlbum, a.title, a.interpreter, a.year, a.genre, a.idCreator, a.imageUrl FROM MusicPlaylistdb.Playlist as p, MusicPlaylistdb.MatchOrder as m, MusicPlaylistdb.Song as s, MusicPlaylistdb.Album as a\n"
				+ "WHERE p.id = ? and p.id = m.idPlaylist and s.id = m.idSong and s.idAlbum = a.id and p.idCreator = a.idCreator";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idPlaylist);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Song song = new Song();
					song.setId(result.getInt(1));
					song.setTitle(result.getString(2));
					song.setSongUrl(result.getString(3));
					song.setIdSongBefore(result.getInt(4));
					song.setDateAdding(result.getTimestamp(5));
					int idAlbum = result.getInt(6);
					song.setIdAlbum(idAlbum);

					
					Album album = new Album();
					album.setId(idAlbum);
					album.setTitle(result.getString(7));
					album.setInterpreter(result.getString(8));
					album.setYear(result.getShort(9));
					album.setGenre(result.getString(10));
					album.setIdCreator(result.getInt(11));
					album.setImageUrl(result.getString(12));

				
					songs.put(song, album);
				}
			}
		}
		
		return songs;
	}
	
	
	public ArrayList<Song> findAllUserSongsNotInPlaylist(int idUser, int idPlaylist) throws SQLException{
		ArrayList<Song> songs = new ArrayList<>();
		
		String query = "SELECT s.id, s.title FROM MusicPlaylistdb.Song as s, MusicPlaylistdb.Album as a\n"
				+ "WHERE s.idAlbum = a.id and a.idCreator = ?\n"
				+ "and s.id NOT IN (\n"
				+ "SELECT m.idSong\n"
				+ "FROM MusicPlaylistdb.MatchOrder as m\n"
				+ "WHERE m.idPlaylist = ?)";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idUser);
			pstatement.setInt(2, idPlaylist);
			try (ResultSet result = pstatement.executeQuery();) {

				while(result.next()) {
					Song song = new Song();
					song.setId(result.getInt(1));
					song.setTitle(result.getString(2));

					songs.add(song);
				}
			}
		}
		return songs;
		
	}
	
	
	public int createPlaylist(String title, int idCreator) throws SQLException {
		int idPlaylist = -1;
		String query = "INSERT IGNORE INTO MusicPlaylistdb.Playlist (title, idCreator)   VALUES(?, ?)";
		PreparedStatement pstatement = null;
		ResultSet rs = null;
		try {
			pstatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pstatement.setString(1, title);
			pstatement.setInt(2, idCreator);
			pstatement.executeUpdate();
			
            rs = pstatement.getGeneratedKeys();
            if (rs.next()) {
            	idPlaylist = rs.getInt(1);
            }
            
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {

			}
		}
		
		return idPlaylist;
	}
	
	
	//return null if there is not a playlist with this id
	public Playlist findPlaylistById(int idPlaylist) throws SQLException {

		Playlist playlist = null;
		String query = "SELECT * FROM MusicPlaylistdb.Playlist WHERE id = ?";

		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idPlaylist);
			
			try (ResultSet result = pstatement.executeQuery();) {

				while(result.next()) {
					playlist = new Playlist();
					playlist.setId(idPlaylist);
					playlist.setTitle(result.getString("title"));
					playlist.setIdCreator(result.getInt("idCreator"));
					playlist.setDate(result.getTimestamp("dateCreation"));
				}
			}
		}
		return playlist;
		
	}	
	
		
	
}