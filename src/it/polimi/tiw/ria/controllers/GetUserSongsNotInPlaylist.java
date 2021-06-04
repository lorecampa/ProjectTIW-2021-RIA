package it.polimi.tiw.ria.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.ria.beans.Playlist;
import it.polimi.tiw.ria.beans.Song;
import it.polimi.tiw.ria.beans.User;
import it.polimi.tiw.ria.dao.PlaylistDAO;
import it.polimi.tiw.ria.utils.ConnectionHandler;


@WebServlet("/GetUserSongsNotInPlaylist")
public class GetUserSongsNotInPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public GetUserSongsNotInPlaylist() {
        super();
    }
    
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionHandler.getConnection(servletContext);

	}

    
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int idUser = user.getId();
		
		int idPlaylist;
		try {
			idPlaylist = Integer.parseInt((String) request.getParameter("idPlaylist"));
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		//finding playlist bean
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		Playlist playlist = null;
		try {
			playlist = playlistDAO.findPlaylistById(idPlaylist);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		//control that the playlist belongs to the user session
		if(playlist == null || (playlist.getIdCreator() != user.getId())) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("You are trying to access wrong information");
			return;
		}			
		
		ArrayList<Song> userSongsNotInPlaylist;
		try {
			userSongsNotInPlaylist = playlistDAO.findAllUserSongsNotInPlaylist(idUser, idPlaylist);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(new Gson().toJson(userSongsNotInPlaylist));
		
		
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
