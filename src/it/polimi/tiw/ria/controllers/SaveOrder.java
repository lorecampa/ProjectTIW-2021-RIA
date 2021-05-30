package it.polimi.tiw.ria.controllers;

import java.io.BufferedReader;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import it.polimi.tiw.ria.beans.SongOrder;
import it.polimi.tiw.ria.dao.MatchDAO;
import it.polimi.tiw.ria.utils.ConnectionHandler;


@WebServlet("/SaveOrder")
public class SaveOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    
    public SaveOrder() {
        super();
    }
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionHandler.getConnection(servletContext);

	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reader =  request.getReader();
		ArrayList<SongOrder> playlistOrder = new Gson().fromJson(reader, new TypeToken<ArrayList<SongOrder>>(){}.getType());
		int idPlaylist;
		try {
			idPlaylist = Integer.parseInt((String) request.getParameter("idPlaylist"));
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Error parsing idPlaylist");
			return;
		}
		
		MatchDAO matchDAO = new MatchDAO(connection);
		for (SongOrder update: playlistOrder) {
			try {
				matchDAO.updateSongOrder(idPlaylist, update);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println(e.getMessage());
				return;
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);

		
		
		
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
