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
import mutua.hangmansmsgame.dal.ISessionDB;
import mutua.hangmansmsgame.dal.IUserDB;
import mutua.hangmansmsgame.dal.dto.SessionDto;

public class AddToUnsubscribeUserQueue extends HttpServlet {

	private static final long serialVersionUID = 1L;
       
	private static IUserDB    userDB    = DALFactory.getUserDB();
	private static ISessionDB sessionDB = DALFactory.getSessionDB();

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			String phone = request.getParameter("MSISDN");
			if (!userDB.isUserSubscribed(phone)) {
				System.out.println("Hangman: received an api unregistration request for " + phone + ": not registered");
			} else {
				userDB.setSubscribed(phone, false);
				sessionDB.setSession(new SessionDto(phone, "NEW_USER"));
				System.out.println("Hangman: received an api unregistration request for " + phone + ": unregistration complete");
			}
			out.print("ACCEPTED");
		} catch (SQLException e) {
			Configuration.log.reportThrowable(e, "Error while unsubscribing user from the web");
			out.print("FAILED");
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}
