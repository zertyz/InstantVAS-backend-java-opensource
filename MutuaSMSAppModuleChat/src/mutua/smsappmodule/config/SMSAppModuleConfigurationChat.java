package mutua.smsappmodule.config;

import mutua.smsappmodule.i18n.SMSAppModulePhrasingsChat;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat;

/** <pre>
 * SMSAppModuleConfigurationChat.java
 * ==================================
 * (created by luiz, Aug 26, 2015)
 *
 * Configure the classes' default values for new instances of the "Chat SMS Module".
 * 
 * The methods of this class are the ones external users should call in order to create the instances needed to use the module.
 * 
 * Follows the "Instant VAS SMS Modules" pattern described bellow:
 *
 * @see InstantVASSMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationChat {

	/** Constructs the simple version of this SMS Module, with default values, for testing purposes.
	 *  @param shortCode         &
	 *  @param appName           see {@link SMSAppModulePhrasingsChat#SMSAppModulePhrasingsChat(String, String, String, String, String)}
	 *  @param profileModuleDAL  &
	 *  @param chatModuleDAL     see {@link SMSAppModuleCommandsChat#SMSAppModuleCommandsChat}
	 *  @returns {(SMSAppModuleNavigationStatesSubscription)navigationStates, (SMSAppModuleCommandsSubscription)commands, (SMSAppModulePhrasingsSubscription)phrasings} */
	public static Object[] getChatModuleInstances(String shortCode, String appName,
	                                              SMSAppModuleDALFactoryProfile profileModuleDAL,
	                                              SMSAppModuleDALFactoryChat    chatModuleDAL) {
		SMSAppModulePhrasingsChat        phrasings        = new SMSAppModulePhrasingsChat(shortCode, appName);
		SMSAppModuleCommandsChat         commands         = new SMSAppModuleCommandsChat(phrasings, profileModuleDAL, chatModuleDAL);
		SMSAppModuleNavigationStatesChat navigationStates = new SMSAppModuleNavigationStatesChat(commands);
		
		System.err.println(SMSAppModuleConfigurationChat.class.getName() + ": test configuration loaded.");
		
		return new Object[] {navigationStates, commands, phrasings};
	}
}