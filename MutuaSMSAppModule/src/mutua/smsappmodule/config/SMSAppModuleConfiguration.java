package mutua.smsappmodule.config;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;

/** <pre>
 * SMSAppModuleConfiguration.java
 * ==============================
 * (created by luiz, Jul 13, 2015)
 *
 * Defines common configuration variables for the 'MutuaSMSAppModule' base project and sub projects.
 * 
 * Module specific configuration classes should use the Mutua SMSApp Configuration design pattern, described
 * bellow:
 * 
 * {@code
 * 	get it from the help module by now
 * }
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfiguration {

	
	/*************************************************
	** MutuaICConfiguration CONFIGURABLE PROPERTIES **
	*************************************************/

	// application
	//////////////

	@ConfigurableElement("The name of this SMS Game / Application, for phrasing & logging purposes")
	public static String APPName      = "GenericApp";
	@ConfigurableElement("The short code of this SMS Game / Application, for phrasing / routing purposes")
	public static String APPShortCode = "991";
	

	// databases
	////////////
	
	@ConfigurableElement(sameAs="mutua.smsappmodule.dal.SMSAppModuleDALFactory.DEFAULT_DAL")
	public static SMSAppModuleDALFactory DALModules = SMSAppModuleDALFactory.DEFAULT_DAL;
	
	
	/************
	** METHODS **
	************/
	
	/** Apply the following on-the-fly configuration changes: application */
	public static void applyConfiguration() {
		//SMSAppModuleDALFactory.DEFAULT_DAL = DALModules;
	}

	
	static {
		applyConfiguration();
	}
}
