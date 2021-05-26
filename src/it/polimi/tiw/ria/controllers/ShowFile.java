package it.polimi.tiw.ria.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/ShowFile/*")
public class ShowFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	   
	String imagePath = "";
	String audioPath = "";

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		// get folder path from webapp init parameters inside web.xml
		imagePath = getServletContext().getInitParameter("imagePath");
		audioPath = getServletContext().getInitParameter("audioPath");

		
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String pathInfo = request.getPathInfo();
		
		if (pathInfo == null || pathInfo.equals("/")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad File parameter");
			return;
		}
		
		int index = pathInfo.lastIndexOf("_");
		String fileType = pathInfo.substring(1, index);
		String fileId = pathInfo.substring(index + 1);
		
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
		
		
		URLDecoder.decode(fileId, "UTF-8");
		File file = new File(filePath + fileId); 

		if (!file.exists() || file.isDirectory()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("File not found");
			return;
		}

		// set headers for browser
		response.setHeader("Content-Type", getServletContext().getMimeType(fileId));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
																									
		// copy file to output stream
		Files.copy(file.toPath(), response.getOutputStream());

	}
	
}
