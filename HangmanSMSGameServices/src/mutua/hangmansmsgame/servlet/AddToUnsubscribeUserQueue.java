package mutua.hangmansmsgame.servlet;

import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;
import instantvas.nativewebserver.NativeHTTPServer;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.Instantiator;
import mutua.icc.instrumentation.Instrumentation;

public class AddToUnsubscribeUserQueue extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	static {
		Instantiator.preloadConfiguration();
	}

       
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Instrumentation.startRequest(MSG_PROPERTY, "AddToUnsubscribeUserQueue " + request.getQueryString());
		PrintWriter out = response.getWriter();
		String phone = request.getParameter("MSISDN");
		try {
			NativeHTTPServer.scProducer.dispatchAssureUserIsNotSubscribedEvent(phone);
			out.print("ACCEPTED");
		} catch (Throwable t) {
			out.print("FAILED");
			Instrumentation.reportThrowable(t, "AddToUnsubscribeUserQueue error while unsubscribing user from the web");
		}
		Instrumentation.finishRequest();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}
