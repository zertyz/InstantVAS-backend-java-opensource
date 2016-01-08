package instantvas.smsengine.web;

import static config.InstantVASSMSEngineConfiguration.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;

public class AddToUnsubscribeUserQueue {

//	private static IUserDB    userDB    = DALFactory.getUserDB(Configuration.DATA_ACCESS_LAYER);
//	private static ISessionDB sessionDB = DALFactory.getSessionDB(Configuration.SESSIONS_DATA_ACCESS_LAYER);

	public static byte[] process(HashMap<String, String> parameters, String queryString) {
//		log.reportRequestStart(queryString);
//		try {
//			String phone = request.getParameter("MSISDN");
//			if (!userDB.isUserSubscribed(phone)) {
//				log.reportDebug("Hangman: received an api unregistration request for " + phone + ": not registered");
//			} else {
//				userDB.setSubscribed(phone, false);
//				sessionDB.setSession(new SessionDto(phone, "NEW_USER"));
//				log.reportDebug("Hangman: received an api unregistration request for " + phone + ": unregistration complete");
//			}
//			out.print("ACCEPTED");
//		} catch (SQLException e) {
//			out.print("FAILED");
//			log.reportThrowable(e, "Error while unsubscribing user from the web");
//		}
//		log.reportRequestFinish();
		return null;
	}
	
}
