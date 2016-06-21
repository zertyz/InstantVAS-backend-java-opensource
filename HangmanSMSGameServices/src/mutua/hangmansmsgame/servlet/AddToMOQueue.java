package mutua.hangmansmsgame.servlet;

import static config.InstantVASLicense.*;
import static config.MutuaHardCodedConfiguration.IFDEF_WEB_DEBUG;
import instantvas.nativewebserver.NativeHTTPServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.Instantiator;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsin.dto.IncomingSMSDto;


public class AddToMOQueue extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	
	/*******************************************************************************************************************************************

	getting an MO:           curl 'http://domlap:8080/HangmanSMSGameServices/AddToMOQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21998019167&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting a subscriber:    curl 'http://domlap:8080/HangmanSMSGameServices/AddToSubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting an unsubscriber: curl 'http://domlap:8080/HangmanSMSGameServices/AddToUnsubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=coconuts'

	Listen to MTs with:        while sleep 1; do echo "Expecting an MT"; /home/luiz/Projetos/scripts/share mt 15001; done
	Listen to subsc. api with: while sleep 1; do echo "Expecting user registration attempt"; /home/luiz/Projetos/scripts/share ss 8082; done

	*******************************************************************************************************************************************/
	
	static {
		Instantiator.preloadConfiguration();
	}
	
	private static String[] parameterNames = NativeHTTPServer.moParser.getRequestParameterNames("AUTHENTICATION_TOKEN");
	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		// Override the Servlet's default ISO-8859-1 character encoding, for scenarios where the customer cannot edit
		// tomcat's 'server.xml' and specify URIEncoding="UTF-8", as documented in https://struts.apache.org/docs/how-to-support-utf-8-uriencoding-with-tomcat.html
		HashMap<String, String> utf8Parameters = NativeHTTPServer.retrieveGetParameters(request.getQueryString());
		
		// get parameter names
		String[] parameterValues = new String[parameterNames.length];
		for (int i=0; i<parameterNames.length; i++) {
			parameterValues[i] = utf8Parameters.get(parameterNames[i]);
		}
		
		byte[] contents;
		
		// Authenticate
		// TODO 20160601 Refactor with 'AddToMOQueue.processRequest(String queryString)'
		/* debug */ if (IFDEF_WEB_DEBUG) {Instrumentation.reportDebug("/AddToMOQueue: " + Arrays.deepToString(parameterValues));}
		if (parameterValues == null) {
			/* debug */ if (IFDEF_WEB_DEBUG) {Instrumentation.reportDebug("/AddToMOQueue BAD_REQUEST: " + request.getQueryString());}
			contents = "BAD_REQUEST".getBytes();
		} else if (!NativeHTTPServer.addToMOQueue.attemptToAuthenticateFromStrictGetParameters(parameterValues)) {
			/* debug */ if (IFDEF_WEB_DEBUG) {
				Instrumentation.reportDebug("/AddToMOQueue BAD_AUTHENTICATION: " + Arrays.toString(parameterValues));
				Instrumentation.reportDebug("IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES=" + IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES);
				Instrumentation.reportDebug("MO_ADDITIONAL_RULEn_LENGTH=" + MO_ADDITIONAL_RULEn_LENGTH);
				Instrumentation.reportDebug("parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]=" + parameterValues[1+MO_ADDITIONAL_RULE0_FIELD_INDEX]);
				Instrumentation.reportDebug("MO_ADDITIONAL_RULE0_REGEX.matcher(parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]).matches(): " + MO_ADDITIONAL_RULE0_REGEX.matcher(parameterValues[1+MO_ADDITIONAL_RULE0_FIELD_INDEX]).matches());
			}
			contents = "BAD_AUTHENTICATION".getBytes();
		}
		
		// the request is allowed. Proceed.
		IncomingSMSDto mo = NativeHTTPServer.moParser.parseIncomingSMS(parameterValues);
		contents = NativeHTTPServer.addToMOQueue.processRequest(mo, request.getQueryString());
		response.setContentType("text/plain");
		response.setContentLength(contents.length);
		response.getOutputStream().write(contents);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}