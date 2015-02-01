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

import static config.WebAppConfiguration.*;
import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;
import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;
/**
 * Servlet implementation class AddToSubscriberUserQueue
 */
public class AddToSubscribeUserQueue extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.reportRequestStart(request.getQueryString());
		PrintWriter out = response.getWriter();
		try {
			String phone = request.getParameter("MSISDN");
			CommandDetails.registerUserNickname(phone, "Webby");
			if (CommandDetails.assureUserIsRegistered(phone)) {
				log.reportDebug("Hangman: received an api registration request for " + phone + ": registration complete");
			} else {
				log.reportDebug("Hangman: received an api registration request for " + phone + ": already registered");
			}
			out.print("ACCEPTED");
		} catch (SQLException e) {
			log.reportThrowable(e, "Error while subscribing user from the web");
			out.print("FAILED");
		}
		log.reportRequestFinish();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}
