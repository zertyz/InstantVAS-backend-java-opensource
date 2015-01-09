package mutua.hangmansmsgame.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.ISessionDB;
import mutua.hangmansmsgame.dal.IUserDB;

public class AddToUnsubscribeUserQueue extends HttpServlet {

	private static final long serialVersionUID = 1L;
       
	private static IUserDB    userDB    = DALFactory.getUserDB();
	private static ISessionDB sessionDB = DALFactory.getSessionDB();

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String phone = request.getParameter("MSISDN");
		if (!userDB.isUserOnRecord(phone)) {
			System.out.println("Hangman: received an api unregistration request for " + phone + ": not registered");
		} else {
			userDB.reset();
			sessionDB.reset();
			System.out.println("Hangman: received an api unregistration request for " + phone + ": unregistration complete");
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
