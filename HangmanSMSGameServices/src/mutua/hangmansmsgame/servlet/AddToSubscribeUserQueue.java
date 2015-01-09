package mutua.hangmansmsgame.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.IUserDB;

/**
 * Servlet implementation class AddToSubscriberUserQueue
 */
public class AddToSubscribeUserQueue extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static IUserDB userDB = DALFactory.getUserDB();

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String phone = request.getParameter("MSISDN");
		if (!userDB.isUserOnRecord(phone)) {
			userDB.checkAvailabilityAndRecordNickname(phone, "Webby");
			System.out.println("Hangman: received an api registration request for " + phone + ": registration complete");
		} else {
			System.out.println("Hangman: received an api registration request for " + phone + ": already registered");
		}
		PrintWriter out = response.getWriter();
		out.print("ACCEPTED");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}
