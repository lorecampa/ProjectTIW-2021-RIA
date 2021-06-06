package it.polimi.tiw.ria.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.ria.beans.Album;
import it.polimi.tiw.ria.beans.Song;
import it.polimi.tiw.ria.dao.AlbumDAO;
import it.polimi.tiw.ria.dao.SongDAO;
import it.polimi.tiw.ria.utils.ConnectionHandler;
import it.polimi.tiw.ria.beans.User;


@WebServlet("/ShowFile/*")
public class ShowFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	   
	String imagePath = "";
	String audioPath = "";

	public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
		// get folder path from webapp init parameters inside web.xml
		imagePath = getServletContext().getInitParameter("imagePath");
		audioPath = getServletContext().getInitParameter("audioPath");

		
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		String pathInfo = request.getPathInfo();
		
		if (pathInfo == null || pathInfo.equals("/")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad File parameter");
			return;
		}
		
		int indexExt = pathInfo.indexOf(".");
		String ext = pathInfo.substring(indexExt);
		String[] fileNameSplitted = pathInfo.substring(1, indexExt).split("_");
				
		String fileType = fileNameSplitted[0];
		String fileName = fileNameSplitted[1];
		
		int idAlbum;
		int idSong;
		try {
			if (fileNameSplitted.length == 3) {
				idSong = -1;
				idAlbum = Integer.parseInt(fileNameSplitted[2]);
				fileName +="_"+idAlbum;
			}else if(fileNameSplitted.length == 4){
				idSong = Integer.parseInt(fileNameSplitted[2]);
				idAlbum = Integer.parseInt(fileNameSplitted[3]);
				fileName += "_" + idSong + "_" + idAlbum;
			}else {
				throw new Exception();
			}
			fileName += ext;
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad File parameter");
			return;
		}
		
				
		//finding song bean
		SongDAO songDAO = new SongDAO(connection);
		Song song = null;
		try {
			if (idSong != -1) {
				song = songDAO.findSongById(idSong);
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}		

		//finding album bean
		AlbumDAO albumDAO = new AlbumDAO(connection);
		Album album = null;
		try {
			album = albumDAO.findAlumById(idAlbum);			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		//control show file authenticity
		
		if ((song == null && idSong != -1) || album == null ||
				(idSong != -1 && song.getIdAlbum() != idAlbum) ||
				(album.getIdCreator() != user.getId())) {
			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad File parameter");
			return;
		}
		
		
		//control file type
		String filePath;
		if (fileType.equals("image")) {
			filePath = imagePath;
		}else if(fileType.equals("audio")) {
			filePath = audioPath;
		}
		else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid type");
			return;
		}
		
		
		URLDecoder.decode(fileName, "UTF-8");
		File file = new File(filePath + fileName); 

		if (!file.exists() || file.isDirectory()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("File not found");
			return;
		}

		// set headers for browser
		response.setHeader("Content-Type", getServletContext().getMimeType(fileName));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
																									
		// copy file to output stream
		Files.copy(file.toPath(), response.getOutputStream());

	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
