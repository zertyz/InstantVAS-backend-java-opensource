package mutua.smsappmodule.config;

import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsChat.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.*;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat;

/** <pre>
 * SMSAppModuleConfigurationChat.java
 * ==================================
 * (created by luiz, Aug 26, 2015)
 *
 * Defines the "Chat" module configuration variables, implementing the Mutua SMSApp
 * Configuration design pattern, as described in 'SMSAppModuleConfiguration'
 *
 * @see SMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationChat {
	
	
	/*************************************************
	** MutuaICConfiguration CONFIGURABLE PROPERTIES **
	*************************************************/
	
	// phrasing
	///////////
	
	@ConfigurableElement("Phrase sent to the target user when the sender user wants to send a chat private message. Variables: {{shortCode}}, {{appName}}, {{senderNickname}}, {{senderMessage}}")
	public static String CHATphrPrivateMessage                     = phrPrivateMessage.toString();
	@ConfigurableElement("Phrase sent to the sender user, who sent a private message, to inform of the correct delivery. Variables: {{shortCode}}, {{appName}}, {{targetNickname}}")
	public static String CHATphrPrivateMessageDeliveryNotification = phrPrivateMessageDeliveryNotification.toString();
	@ConfigurableElement("Phrase used to inform the sender that the statefull private conversation can no longer be conducted (because we don't know who is the target user), therefore, he/she must try the stateles command. Variables: {{shortCode}}, {{appName}}")
	public static String CHATphrDoNotKnowWhoYouAreChattingTo       = phrDoNotKnowWhoYouAreChattingTo.toString();
	
	
	// command patterns
	///////////////////
	
	@ConfigurableElement("Global triggers (available to all navigation states) to send a private message to a chosen user. Receives 2 parameter: the destination nickname and the message")
	public static String[] CHATtrgGlobalSendPrivateMessage    = trgGlobalSendPrivateMessage;
	@ConfigurableElement("Local triggers (available only to the 'privately chatting with an user' navigation state) send a private message to that user. Receives 1 parameter: the message")
	public static String[] CHATtrgLocalSendPrivateReply       = trgLocalSendPrivateReply;

	
	/************
	** METHODS **
	************/
	
	/** Apply on-the-fly phrasing changes */
	public static void applyPhrasingConfiguration() {
		phrPrivateMessage                    .setPhrases(CHATphrPrivateMessage);
		phrPrivateMessageDeliveryNotification.setPhrases(CHATphrPrivateMessageDeliveryNotification);
		phrDoNotKnowWhoYouAreChattingTo      .setPhrases(CHATphrDoNotKnowWhoYouAreChattingTo);
	}
	
	/** Apply on-the-fly command trigger changes */
	public static void applyTriggerConfiguration() {
		for (INavigationState navigationState : SMSAppModuleNavigationStatesChat.values()) {
			navigationState.setCommandTriggersFromConfigurationValues();
		}
	}
	
	/** Apply the following on-the-fly configuration changes: phrasing, triggers */
	public static void applyConfiguration() {
		applyPhrasingConfiguration();
		applyTriggerConfiguration();
	}
	
	
	static {
		applyConfiguration();
	}
}