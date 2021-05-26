package it.polimi.tiw.ria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
	
	
	public ArrayList<Song> findPlaylistSongs(int idPlaylist) throws SQLException{
		ArrayList<Song> songs = new ArrayList<>();
		
		String query = "SELECT s.title, s.songUrl, a.title, a.interpreter, a.year, a.genre, a.idCreator, a.imageUrl FROM MusicPlaylistdb.Playlist as p, MusicPlaylistdb.MatchOrder as m, MusicPlaylistdb.Song as s, MusicPlaylistdb.Album as a\n"
				+ "WHERE p.id = ? and p.id = m.idPlaylist and s.id = m.idSong and s.idAlbum = a.id and p.idCreator = a.idCreator";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idPlaylist);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Song song = new Song();
					song.setTitleSong(result.getString(1));
					song.setSongUrl(result.getString(2));
					song.setTitleAlbum(result.getString(3));
					song.setInterpreterAlbum(result.getString(4));
					song.setYearAlbum(result.getShort(5));
					song.setGenreAlbum(result.getString(6));
					song.setIdCreator(result.getInt(7));
					song.setImageUrl(result.getString(8));
					songs.add(song);
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
					song.setIdSong(result.getInt(1));
					song.setTitleSong(result.getString(2));
					songs.add(song);
				}
			}
		}
		return songs;
		
	}
	
}