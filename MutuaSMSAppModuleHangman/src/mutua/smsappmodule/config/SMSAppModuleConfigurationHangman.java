package mutua.smsappmodule.config;

import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryHangman;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman;
import mutua.smsappmodule.smslogic.SMSAppModuleEventsSubscription;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman;

/** <pre>
 * SMSAppModuleConfigurationHangman.java
 * =====================================
 * (created by luiz, Sep 18, 2015)
 *
 * Configure the classes' default values for new instances of the "Hangman SMS Module".
 * 
 * The methods of this class are the ones external users should call in order to create the instances needed to use the module.
 * 
 * Follows the "Instant VAS SMS Modules" pattern described bellow:
 *
 * @see InstantVASSMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationHangman {

	/** Constructs the simple version of this SMS Module, with default values, for testing purposes.
	 *  @param shortCode                &
	 *  @param appName                  see {@link SMSAppModulePhrasingsHangman#SMSAppModulePhrasingsHangman(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)}
	 *  @param subscriptionEventsServer one of the values returned by {@link SMSAppModuleConfigurationSubscription#getSubscriptionModuleInstances}
	 *  @param baseModuleDAL            &
	 *  @param profileModuleDAL         &
	 *  @param hangmanModuleDAL         &
	 *  @param defaultNicknamePrefix    see {@link SMSAppModuleCommandsHangman#SMSAppModuleCommandsHangman(SMSAppModulePhrasingsHangman, SMSAppModuleDALFactory, SMSAppModuleDALFactoryProfile, SMSAppModuleDALFactoryHangman, String)}
	 *  @returns {(SMSAppModuleNavigationStatesHangman)navigationStates, (SMSAppModuleCommandsHangman)commands, (SMSAppModulePhrasingsHangman)phrasings} */
	public static Object[] getHangmanModuleInstances(String shortCode, String appName,
	                                                 SMSAppModuleEventsSubscription subscriptionEventsServer,
	                                                 SMSAppModuleDALFactory         baseModuleDAL,
	          	                                     SMSAppModuleDALFactoryProfile  profileModuleDAL,
	        	                                     SMSAppModuleDALFactoryHangman  hangmanModuleDAL,
	        	                                     String defaultNicknamePrefix) {
		SMSAppModulePhrasingsHangman        phrasings        = new SMSAppModulePhrasingsHangman(shortCode, appName);
		SMSAppModuleCommandsHangman         commands         = new SMSAppModuleCommandsHangman(phrasings, subscriptionEventsServer, baseModuleDAL, profileModuleDAL, hangmanModuleDAL, defaultNicknamePrefix);
		SMSAppModuleNavigationStatesHangman navigationStates = new SMSAppModuleNavigationStatesHangman(commands);
		
		System.err.println(SMSAppModuleConfigurationHangman.class.getCanonicalName() + ": test configuration loaded.");
		
		return new Object[] {navigationStates, commands, phrasings};
	}
}