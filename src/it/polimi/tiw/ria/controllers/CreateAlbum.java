package it.polimi.tiw.ria.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.google.gson.Gson;

import it.polimi.tiw.ria.beans.Album;
import it.polimi.tiw.ria.beans.Genre;
import it.polimi.tiw.ria.beans.User;
import it.polimi.tiw.ria.dao.AlbumDAO;
import it.polimi.tiw.ria.utils.ConnectionHandler;






@WebServlet("/CreateAlbum")
@MultipartConfig
public class CreateAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private String imagePath = null;
    
    public CreateAlbum() {
        super();
    }
    
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	imagePath = getServletContext().getInitParameter("imagePath");
    	connection = ConnectionHandler.getConnection(servletContext);

	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		String title = request.getParameter("title");
		String interpreter = request.getParameter("interpreter");
		String yearString = request.getParameter("year");
		String genreString = request.getParameter("genre");

		
		if (title == null || title.isEmpty() || 
				interpreter  == null || interpreter.isEmpty() ||
				yearString == null || yearString.isEmpty() ||
				genreString == null || genreString.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid album params");
			return;
			
		}
		
		Short year;
		try {
			Integer yearInteger = Integer.parseInt(yearString);
			// year limit [0 - currentYear + 1]
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			if (yearInteger < 0 || yearInteger > currentYear + 1) {
				String msg = "Year must be beetween [0 - " + (currentYear + 1) + "]";
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(msg);
				return;
			}
			year = yearInteger.shortValue();
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		Genre genre = Genre.fromString(genreString);
		if (genre.getDisplayName() == null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Genre is not in list");
			return;
		}
		
		
		Part imagePart = request.getPart("picture");
		// We first check the parameter needed is present
		if (imagePart == null || imagePart.getSize() <= 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Image file not permitted");
			return;
		}
		
		String contentType = imagePart.getContentType();
		//check if the file is an image
		if (!contentType.startsWith("image")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Image file not permitted");
			return;
		}
		
		//get image file extension
		String imageFileName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
		int indexImage = imageFileName.lastIndexOf('.');
		String imageExt = imageFileName.substring(indexImage);

		
		Album album = new Album(title, interpreter, year, genreString, user.getId(), imageExt);
		AlbumDAO albumDAO = new AlbumDAO(connection);
		
		int created;
		try {
			//return 0 if already present in our database (title, interpreter, year, genre, idCreator) is a unique constraint
			created = albumDAO.createAlbum(album);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		if(created == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Album already present");
			return;
		}
				
		
		//imagePath refers to the path initialized in the init part
		String imageOutputPath = imagePath + album.getImageUrl();
		
		//save image
		File imageFile = new File(imageOutputPath);
		try (InputStream fileContent = imagePart.getInputStream()) {
			
			Files.copy(fileContent, imageFile.toPath());

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(new Gson().toJson(album));
		
		
		
		
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	

}
