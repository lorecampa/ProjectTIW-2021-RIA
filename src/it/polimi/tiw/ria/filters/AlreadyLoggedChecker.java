package it.polimi.tiw.ria.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.ria.beans.User;


@WebFilter("/LoginChecker")
public class AlreadyLoggedChecker implements Filter {

    
    public AlreadyLoggedChecker() {
    }

	
	public void destroy() {
	}

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("AlreadyLoggedChecker filter executing ...");
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		HttpSession session = req.getSession(false);
		
		if (session != null && session.getAttribute("user") != null) {
			res.setStatus(HttpServletResponse.SC_OK);
			User user = (User) session.getAttribute("user");
			res.getWriter().println(new Gson().toJson(user));
			return;
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
