package mutua.smsappmodule.config;

import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile;

/** <pre>
 * SMSAppModuleConfigurationProfile.java
 * =====================================
 * (created by luiz, Aug 3, 2015)
 *
 * Configure the classes' default values for new instances of the "Profile SMS Module".
 * 
 * The methods of this class are the ones external users should call in order to create the instances needed to use the module.
 * 
 * Follows the "Instant VAS SMS Modules" pattern described bellow:
 *
 * @see InstantVASSMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationProfile {

	/** Constructs the simple version of this SMS Module, with default values, for testing purposes.
	 *  @param shortCode         &
	 *  @param appName           see {@link SMSAppModulePhrasingsChat#SMSAppModulePhrasingsChat(String, String, String, String, String)}
	 *  @param profileModuleDAL  see {@link SMSAppModuleCommandsChat#SMSAppModuleCommandsChat}
	 *  @returns {(SMSAppModuleNavigationStatesProfile)navigationStates, (SMSAppModuleCommandsProfile)commands, (SMSAppModulePhrasingsProfile)phrasings} */
	public static Object[] getChatModuleInstances(String shortCode, String appName,
	                                              SMSAppModuleDALFactoryProfile profileModuleDAL) {
		SMSAppModulePhrasingsProfile        phrasings        = new SMSAppModulePhrasingsProfile(shortCode, appName);
		SMSAppModuleCommandsProfile         commands         = new SMSAppModuleCommandsProfile(phrasings, profileModuleDAL);
		SMSAppModuleNavigationStatesProfile navigationStates = new SMSAppModuleNavigationStatesProfile(commands);
		
		System.err.println(SMSAppModuleConfigurationProfile.class.getName() + ": test configuration loaded.");
		
		return new Object[] {navigationStates, commands, phrasings};
	}
}
