package it.polimi.tiw.ria.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import it.polimi.tiw.ria.beans.User;
import it.polimi.tiw.ria.dao.UserDAO;
import it.polimi.tiw.ria.utils.ConnectionHandler;


@WebServlet("/CreateUser")
@MultipartConfig
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public CreateUser() {
    }

	
	public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionHandler.getConnection(servletContext);
	}
	
    

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		
		if(username == null || username.isEmpty() || email == null || email.isEmpty() ||
				password == null|| password.isEmpty() || name == null || name.isEmpty() ||
				surname == null || surname.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			return;
		}
		
		User user = new User(username, email, password, name, surname);
		UserDAO userDAO = new UserDAO(connection);
		int result;
		try {
			result = userDAO.createUser(user);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		
		if (result == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(user.getEmail() + " is already registred, please login");
		}else {
			try {
				user = userDAO.checkCredentials(email, password);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println(e.getMessage());
				return;
			}
			
			request.getSession().setAttribute("user", user);
			response.setStatus(HttpServletResponse.SC_OK);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(new Gson().toJson(user));
		}
		
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
