package instantvas.nativewebserver;

import static org.junit.Assert.*;
import instantvas.smsengine.web.AddToMOQueue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;


/** <pre>
 * NativeHTTPServerBehavioralTests.java
 * ====================================
 * (created by luiz, Jan 7, 2016)
 *
 * Tests both the common aspects of the native http server, as well as application specific behavior,
 * through configuration & log files 
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class NativeHTTPServerBehavioralTests {
	
	@Test
	public void testQueryStringParsing() throws UnsupportedEncodingException {
		
		HashMap<String, String> parameters = NativeHTTPServer.retrieveGetParameters("a=b&c=d&e=&");
		assertEquals("parameter 'a' was wrongly parsed", "b", parameters.get("a"));
		assertEquals("parameter 'c' was wrongly parsed", "d", parameters.get("c"));
		assertEquals("parameter 'e' was wrongly parsed", "",  parameters.get("e"));
		assertNull("non existing parameter should have a null value", parameters.get("nonexistingparametername"));
		
		parameters = NativeHTTPServer.retrieveGetParameters("a=bcde&");
		assertEquals("parameter 'a' was wrongly parsed", "bcde", parameters.get("a"));
		
		parameters = NativeHTTPServer.retrieveGetParameters("abcde");
		assertEquals("parameter 'abcde' was wrongly parsed", "", parameters.get("abcde"));
		
		parameters = NativeHTTPServer.retrieveGetParameters("=abcde");
		assertEquals("parameter '' was wrongly parsed", "abcde", parameters.get(""));
		
		parameters = NativeHTTPServer.retrieveGetParameters("sourceid=chrome-instant&ion=1&espv=2&es_th=1&ie=UTF-8&q=this+is+just+an+UTF-8+URLEncoded+test+which+may+contain+several+characters%2C+such+as+%C3%87%C2%A7%3D%26%3F%25+and%2C+possibly%2C+others...&es_th=1");
		assertEquals("parameter 'q' was wrongly parsed", "this is just an UTF-8 URLEncoded test which may contain several characters, such as Ç§=&?% and, possibly, others...", parameters.get("q"));
	}
	
	@Test
	public void testTheirsAndMine() throws IOException, InterruptedException {
		NativeHTTPServer.startServer(8080, 9999, NativeHTTPServer.InstantVASSMSWebHandlers.values(), TestHandlers.values());
		System.out.println("The server was started on 8080. Please, use it within 10 minutes.");
		Thread.sleep(600*1000);
	}
	
	public enum TestHandlers implements INativeHTTPServerHandler {

		krusty("/Krusty") {
			@Override
			public void handle(HttpExchange he) throws IOException {
				String queryString = he.getRequestURI().getRawQuery();
				byte[] response = "hey hey!! I just work, baby!!".getBytes();
				he.sendResponseHeaders(200, response.length);
				he.getResponseBody().write(response);
		        he.close();
			}
		}
		
		;
		
		private String contextPath;
		
		private TestHandlers(String contextPath) {
			this.contextPath = contextPath;
		}
		
		public String getContextPath() {
			return contextPath;
		}
	}

}
