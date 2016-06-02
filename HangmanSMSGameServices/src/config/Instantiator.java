package config;

import java.io.IOException;
import java.sql.SQLException;

import instantvas.nativewebserver.InstantVASConfigurationLoader;
import instantvas.nativewebserver.NativeHTTPServer;

/** <pre>
 * Instantiator.java
 * =================
 * (created by luiz, Jun 1, 2016)
 *
 * Servlet Instantiator to instantiate this InstantVAS' instances
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class Instantiator {
	
	private static boolean preLoaded = false;
	
	/** Method that should be called by every Servlet's static constructor */
	public static void preloadConfiguration() {
		if (!preLoaded) try {
			forceConfigurationLoading();
			preLoaded = true;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Error Instantiating InstantVASServletFramework", t);
		}
	}
	
	/** Method to be called only when we want to forcibly reload / reinstantiate everything */
	public static void forceConfigurationLoading() throws Throwable {
		InstantVASLicense.INSTANTVAS_INSTANCE_CONFIG0_ACCESS_INFO = "/Celltick/app/etc/hangman.config";
		InstantVASConfigurationLoader.applyConfigurationFromLicenseClass();
		NativeHTTPServer.instantiate();
		preLoaded = true;
	}

}
