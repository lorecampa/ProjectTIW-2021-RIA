package it.polimi.tiw.ria.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
import it.polimi.tiw.ria.beans.Playlist;
import it.polimi.tiw.ria.beans.User;
import it.polimi.tiw.ria.dao.PlaylistDAO;
import it.polimi.tiw.ria.utils.ConnectionHandler;



@WebServlet("/CreatePlaylist")
@MultipartConfig
public class CreatePlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
    public CreatePlaylist() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		String playlistName = request.getParameter("name");
		
		if (playlistName == null || playlistName.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad playlist name");
			return;
		}
		
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		int idPlaylist;
		try {
			//return -1 if playlist is already present
			idPlaylist = playlistDAO.createPlaylist(playlistName, user.getId());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		if (idPlaylist == -1) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Playlist was already present");

		}else {
			
			response.setStatus(HttpServletResponse.SC_OK);
			Playlist playlist = new Playlist(idPlaylist, playlistName, user.getId());
			response.getWriter().println(new Gson().toJson(playlist));
			System.out.println("Playlist created: idCreator ->" + playlist.getIdCreator());

		}
		
		
		
		
	}

}
