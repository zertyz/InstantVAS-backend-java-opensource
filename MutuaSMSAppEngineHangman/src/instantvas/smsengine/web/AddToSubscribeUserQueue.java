package instantvas.smsengine.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;

import static config.InstantVASSMSEngineConfiguration.*;
import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;
import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;
/**
 * Servlet implementation class AddToSubscriberUserQueue
 */
public class AddToSubscribeUserQueue {
	
	public static byte[] process(HashMap<String, String> parameters, String queryString) {
		return null;
//		log.reportRequestStart("AddToSubscribeUserQueue " + request.getQueryString());
//		PrintWriter out = response.getWriter();
//		try {
//			String phone = request.getParameter("MSISDN");
//			if (AddToMOQueue.gameMOProducer.addToSubscribeUserQueue(phone)) {
//				out.print("ACCEPTED");
//			} else {
//				throw new RuntimeException("Adding entry to 'SubscribeUserQueue' was not possible");
//			}
//		} catch (Throwable t) {
//			out.print("FAILED");
//			log.reportThrowable(t, "Error while subscribing user from the web");
//		}
//		log.reportRequestFinish();
	}
	
}
