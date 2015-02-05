package mutua.hangmansmsgame.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.WebAppConfiguration;

public class ReloadConfiguration extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		WebAppConfiguration.loadConfiguration();
		PrintWriter out = response.getWriter();
		out.print("RELOADED");
	}
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}
