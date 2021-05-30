package it.polimi.tiw.ria.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.ria.beans.Album;
import it.polimi.tiw.ria.beans.Song;
import it.polimi.tiw.ria.dao.MatchDAO;
import it.polimi.tiw.ria.utils.ConnectionHandler;


@WebServlet("/AddSongToPlaylist")
@MultipartConfig
public class AddSongToPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    
    public AddSongToPlaylist() {
        super();
    }
    
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionHandler.getConnection(servletContext);

	}
	

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int idPlaylist;
		int idSong;

		try {
			idPlaylist = Integer.parseInt(request.getParameter("idPlaylist"));
			idSong = Integer.parseInt(request.getParameter("songSelected"));
			


		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad params");
			return;
		}
		

		MatchDAO matchDAO = new MatchDAO(connection);
		HashMap<Song, Album> songAndAlbum = new HashMap<>();
		try {
			songAndAlbum = matchDAO.insertSongInPlaylist(idSong, idPlaylist);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		if (songAndAlbum == null || songAndAlbum.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal error");
			
		}else {
			response.setStatus(HttpServletResponse.SC_OK);
			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
			response.getWriter().println(gson.toJson(songAndAlbum));
			System.out.println(gson.toJson(songAndAlbum));
		}
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
