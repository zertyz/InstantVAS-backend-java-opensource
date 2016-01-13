package instantvas.nativewebserver;

import com.sun.net.httpserver.HttpHandler;

/** <pre>
 * INativeHTTPServerHandler.java
 * =============================
 * (created by luiz, Jan 7, 2016)
 *
 * Defines serviceable Contexts on a 'NativeHTTPServer' instance 
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public interface INativeHTTPServerHandler extends HttpHandler {
	
	/** Returns the context this instance services -- for instance, "/AddToMOQueue" */
	String getContextPath();

}
