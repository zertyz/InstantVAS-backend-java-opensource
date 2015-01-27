package mutua.hangmansmsgame.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mutua.hangmansmsgame.config.Configuration;
import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.IUserDB;
import mutua.hangmansmsgame.smslogic.CommandDetails;

/**
 * Servlet implementation class AddToSubscriberUserQueue
 */
public class AddToSubscribeUserQueue extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			String phone = request.getParameter("MSISDN");
			CommandDetails.registerUserNickname(phone, "Webby");
			if (CommandDetails.assureUserIsRegistered(phone)) {
				System.out.println("Hangman: received an api registration request for " + phone + ": registration complete");
			} else {
				System.out.println("Hangman: received an api registration request for " + phone + ": already registered");
			}
		} catch (SQLException e) {
			Configuration.log.reportThrowable(e, "Error while subscribing user from the web");
			out.print("FAILED");
		}
		out.print("ACCEPTED");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}
