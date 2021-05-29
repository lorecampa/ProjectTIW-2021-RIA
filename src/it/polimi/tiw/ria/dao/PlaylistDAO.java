package it.polimi.tiw.ria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import it.polimi.tiw.ria.beans.Album;
import it.polimi.tiw.ria.beans.Playlist;
import it.polimi.tiw.ria.beans.Song;
import it.polimi.tiw.ria.beans.User;

public class PlaylistDAO {
	private Connection con = null;
	
	public PlaylistDAO(Connection con) {
		this.con = con;
	}
	
	
	
	public ArrayList<Playlist> findUserPlaylists(int userId) throws SQLException{
		ArrayList<Playlist> playlists = new ArrayList<>();
		
		String query = "SELECT * FROM MusicPlaylistdb.Playlist WHERE idCreator = ? ORDER BY dateCreation DESC";
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
		
		String query = "SELECT s.title, s.songUrl, a.title, a.interpreter, a.year, a.genre, a.idCreator, a.imageUrl, a.id, s.id FROM MusicPlaylistdb.Playlist as p, MusicPlaylistdb.MatchOrder as m, MusicPlaylistdb.Song as s, MusicPlaylistdb.Album as a\n"
				+ "WHERE p.id = ? and p.id = m.idPlaylist and s.id = m.idSong and s.idAlbum = a.id and p.idCreator = a.idCreator";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idPlaylist);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Song song = new Song();
					Album album = new Album();
					
					
					song.setTitle(result.getString(1));
					song.setSongUrl(result.getString(2));
					album.setTitle(result.getString(3));
					album.setInterpreter(result.getString(4));
					album.setYear(result.getShort(5));
					album.setGenre(result.getString(6));
					album.setIdCreator(result.getInt(7));
					album.setImageUrl(result.getString(8));
					album.setId(result.getInt(9));
					song.setIdAlbum(result.getInt(9));
					song.setId(result.getInt(10));
					songs.put(song, album);
				}
			}
		}
		
		return songs;
	}
	
	
	public ArrayList<Song> findAllUserSongsNotInPlaylist(int idUser, int idPlaylist) throws SQLException{
		ArrayList<Song> songs = new ArrayList<>();
		
		String query = "SELECT s.id, s.title FROM MusicPlaylistdb.Playlist as p, MusicPlaylistdb.Song as s, MusicPlaylistdb.Album as a\n"
				+ "WHERE p.id = ? and p.idCreator = ? and s.idAlbum = a.id and a.idCreator = ?\n"
				+ "and not exists(\n"
				+ "SELECT *\n"
				+ "FROM MusicPlaylistdb.MatchOrder as m\n"
				+ "WHERE m.idSong = s.id and m.idPlaylist = p.id)";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idPlaylist);
			pstatement.setInt(2, idUser);
			pstatement.setInt(3, idUser);
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
		int code = 0;
		String query = "INSERT IGNORE INTO MusicPlaylistdb.Playlist (title, idCreator)   VALUES(?, ?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, title);
			pstatement.setInt(2, idCreator);
			code = pstatement.executeUpdate();

		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {

			}
		}
		
		return code;
	}
	
	
		
	
}