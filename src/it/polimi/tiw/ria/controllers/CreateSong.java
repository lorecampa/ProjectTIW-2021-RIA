package it.polimi.tiw.ria.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import it.polimi.tiw.ria.beans.Album;
import it.polimi.tiw.ria.beans.Song;
import it.polimi.tiw.ria.beans.User;
import it.polimi.tiw.ria.dao.AlbumDAO;
import it.polimi.tiw.ria.dao.SongDAO;
import it.polimi.tiw.ria.utils.ConnectionHandler;


@WebServlet("/CreateSong")
@MultipartConfig
public class CreateSong extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private String audioPath = null;
    
    public CreateSong() {
        super();
    }
    
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	audioPath = getServletContext().getInitParameter("audioPath");
    	connection = ConnectionHandler.getConnection(servletContext);

	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		String title = request.getParameter("title");
		String albumIdString = request.getParameter("albums");


			
		if (title == null || title.isEmpty() || 
				albumIdString == null || albumIdString.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid params");
			return;
		}

		int albumId;
		try {
			albumId = Integer.parseInt(albumIdString);
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Error parsing albumId");
			return;
		}
		
		//find album bean by id
		AlbumDAO albumDAO = new AlbumDAO(connection);
		Album album;
		try {
			//return null if not present
			album = albumDAO.findAlumById(albumId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		//if idAlbum is doesn't belong to the user
		if (album == null || (album.getIdCreator() != user.getId())) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Information doesn't belongs to the user");
			return;
		}
		
	
		
		Part audioPart = request.getPart("audio");
		
		// We first check the parameter needed is present
		if (audioPart == null || audioPart.getSize() <= 0){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Audio file not present");
			return;
		}
		
		String contentType = audioPart.getContentType();
		//check if the file is an audio
		if (!contentType.startsWith("audio")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad audio format");
			return;
		}
		
		
		//find audio extension
		String audioFileName = Paths.get(audioPart.getSubmittedFileName()).getFileName().toString();
		int indexAudio = audioFileName.lastIndexOf('.');
		String audioExt = "";
		audioExt = audioFileName.substring(indexAudio);
		
				
		//create song (audioUrl is set by the database)
		Song song = new Song(title, audioExt, album.getId());
		SongDAO songDAO = new SongDAO(connection);
		int created;
		try {
			created = songDAO.createSong(song);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		if (created == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Song is already present");
			return;
		}
		
			
		
		//audioPath refers to the path initialized in the init part
		String audioOutputPath = audioPath + song.getSongUrl();
						
		File audioFile = new File(audioOutputPath);
		
		
		try (InputStream fileContent = audioPart.getInputStream()) {

			Files.copy(fileContent, audioFile.toPath());

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
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
