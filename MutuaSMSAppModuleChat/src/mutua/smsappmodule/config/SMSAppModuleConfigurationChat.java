package mutua.smsappmodule.config;

import mutua.smsappmodule.i18n.SMSAppModulePhrasingsChat;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat;

import java.util.Arrays;

import mutua.icc.instrumentation.Instrumentation;
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

	/** Constructs the simple version of this SMS Module, with default values, for testing purposes.<pre>
	 *  @param shortCode         &
	 *  @param appName           see {@link SMSAppModulePhrasingsChat#SMSAppModulePhrasingsChat(String, String, String, String, String)}
	 *  @param profileModuleDAL  &
	 *  @param chatModuleDAL     see {@link SMSAppModuleCommandsChat#SMSAppModuleCommandsChat}
	 *  @returns {(SMSAppModuleNavigationStatesChat)navigationStates, (SMSAppModuleCommandsChat)commands, (SMSAppModulePhrasingsChat)chatPhrasings, (SMSAppModulePhrasingsProfile)profilePhrasings} */
	public static Object[] getChatModuleInstances(String shortCode, String appName,
	                                              SMSAppModuleDALFactoryProfile profileModuleDAL,
	                                              SMSAppModuleDALFactoryChat    chatModuleDAL) {
		SMSAppModulePhrasingsProfile     profilePhrasings = new SMSAppModulePhrasingsProfile(shortCode, appName);
		SMSAppModulePhrasingsChat        chatPhrasings    = new SMSAppModulePhrasingsChat(shortCode, appName);
		SMSAppModuleCommandsChat         commands         = new SMSAppModuleCommandsChat(profilePhrasings, chatPhrasings, profileModuleDAL, chatModuleDAL);
		SMSAppModuleNavigationStatesChat navigationStates = new SMSAppModuleNavigationStatesChat(commands);
		
		System.err.println(SMSAppModuleConfigurationChat.class.getCanonicalName() + ": test configuration loaded.");
		
		return new Object[] {navigationStates, commands, chatPhrasings, profilePhrasings};
	}

	/** Constructs the simple version of this SMS Module, with all options set programmatically.<pre>
	 *  @param log
	 *  @param profilePhrasings                    an instance returned by {@link SMSAppModuleConfigurationProfile#getProfileModuleInstances}
	 *  @param shortCode                             &
	 *  @param appName                               &
	 *  @param phrPrivateMessage                     &
	 *  @param phrPrivateMessageDeliveryNotification &
	 *  @param phrDoNotKnowWhoYouAreChattingTo       see {@link SMSAppModulePhrasingsChat#SMSAppModulePhrasingsChat(String, String, String, String, String)}
	 *  @param profileModuleDAL  &
	 *  @param chatModuleDAL     see {@link SMSAppModuleCommandsChat#SMSAppModuleCommandsChat}
	 *  @returns {(SMSAppModuleNavigationStatesChat)navigationStates, (SMSAppModuleCommandsChat)commands, (SMSAppModulePhrasingsChat)chatPhrasings} */
	public static Object[] getChatModuleInstances(Instrumentation<?, ?> log, String shortCode, String appName,
	                                              SMSAppModulePhrasingsProfile profilePhrasings,
	                                              String phrPrivateMessage,
	                                              String phrPrivateMessageDeliveryNotification,
	                                              String phrDoNotKnowWhoYouAreChattingTo,
	                                              SMSAppModuleDALFactoryProfile profileModuleDAL,
	                                              SMSAppModuleDALFactoryChat    chatModuleDAL,
	                                              Object[][] nstChattingWithSomeoneTriggers) {

		SMSAppModulePhrasingsChat        chatPhrasings    = new SMSAppModulePhrasingsChat(shortCode, appName, phrPrivateMessage, phrPrivateMessageDeliveryNotification, phrDoNotKnowWhoYouAreChattingTo);
		SMSAppModuleCommandsChat         commands         = new SMSAppModuleCommandsChat(profilePhrasings, chatPhrasings, profileModuleDAL, chatModuleDAL);
		SMSAppModuleNavigationStatesChat navigationStates = new SMSAppModuleNavigationStatesChat(commands, nstChattingWithSomeoneTriggers);
		
		// log
		String logPrefix = "Chat Module";
		log.reportDebug(logPrefix + ": new instances:");
		Object[][] logPhrasings = {
			{"phrPrivateMessage",                     phrPrivateMessage},
			{"phrPrivateMessageDeliveryNotification", phrPrivateMessageDeliveryNotification},
			{"phrDoNotKnowWhoYouAreChattingTo",       phrDoNotKnowWhoYouAreChattingTo},
		};
		log.reportDebug(logPrefix + ": Phrasings        : " + Arrays.deepToString(logPhrasings));
		Object[][] logCommands = {
			{"profileModuleDAL",      profileModuleDAL},
			{"chatModuleDAL",         chatModuleDAL},
		};
		log.reportDebug(logPrefix + ": Commands         : " + Arrays.deepToString(logCommands));
		Object[][] logCommandTriggers = {
			{"nstChattingWithSomeoneTriggers", nstChattingWithSomeoneTriggers},	
		};
		log.reportDebug(logPrefix + ": Navigation States: " + Arrays.deepToString(logCommandTriggers));
		
		return new Object[] {navigationStates, commands, chatPhrasings};
	}
}