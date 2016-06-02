package mutua.hangmansmsgame.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.Instantiator;

public class ReloadConfiguration extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	static {
		Instantiator.preloadConfiguration();
	}

	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		try {
			Instantiator.forceConfigurationLoading();
			out.print("RELOADED");
		} catch (Throwable t) {
			t.printStackTrace();
			out.print("ERROR - " + t.getMessage());
			throw new RuntimeException("Error Instantiating InstantVASServletFramework from 'ReloadConfiguration'", t);
		}
	}
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}
