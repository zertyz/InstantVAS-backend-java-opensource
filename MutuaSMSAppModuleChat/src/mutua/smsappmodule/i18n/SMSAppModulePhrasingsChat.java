package mutua.smsappmodule.i18n;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;

/** <pre>
 * SMSAppModulePhrasingsChat.java
 * ==============================
 * (created by luiz, Aug 31, 2015)
 *
 * Enumerates and specifies the phrasing to be used by the "Chat" 'MutuaSMSAppModule' implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Phrasing design pattern, as described in {@link Phrase}
 *
 * @see Phrase
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModulePhrasingsChat {
	
	phrPrivateMessage                    ("{{appName}}: "),
	phrPrivateMessageDeliveryNotification("{{appName}}: "),
	phrNicknameNotFound                  ("{{appName}}: "),
	phrDoNotKnowWhoYouAreChattingTo      ("{{appName}}: To chat privately, please text M <nickname> <msg> to {{shortCode}}"),
	
	;
	
	public final Phrase phrase;
	
	private SMSAppModulePhrasingsChat(String... phrases) {
		phrase = new Phrase(phrases);
	}
	
	public String[] toStrings() {
		return phrase.getPhrases();
	}

	public String toString() {
		return toStrings()[0];
	}
	
	public void setPhrases(String... phrases) {
		phrase.setPhrases(phrases);
	}
	
	public String getPhrase(String... parameters) {
		return phrase.getPhrase(parameters);
	}

	public String[] getPhrases(String... parameters) {
		return phrase.getPhrases(parameters);
	}


	
	/*********************
	** PHRASING METHODS **
	*********************/
	
	@ConfigurableElement("Phrase sent to the target user when the sender user wants to send a chat private message. Variables: {{shortCode}}, {{appName}}, {{senderNickname}}")
	public static String getPrivateMessage(String senderNickname) {
		return phrPrivateMessage.getPhrase("shortCode",      SMSAppModuleConfiguration.APPShortCode,
                                           "appName",        SMSAppModuleConfiguration.APPName,
                                           "senderNickname", senderNickname);
	}
	
	@ConfigurableElement("Phrase sent to the sender user, who sent a private message, to inform of the correct delivery. Variables: {{shortCode}}, {{appName}}, {{targetNickname}}")
	public static String getPrivateMessageDeliveryNotification(String targetNickname) {
		return phrPrivateMessageDeliveryNotification.getPhrase("shortCode",      SMSAppModuleConfiguration.APPShortCode,
                                                               "appName",        SMSAppModuleConfiguration.APPName,
                                                               "targetNickname", targetNickname);
	}

	@ConfigurableElement("Phrase sent to the sender user, who sent a private message, to inform that the delivery wasn't possible due to the target nickname currently doesn't exist. Variables: {{shortCode}}, {{appName}}, {{targetNickname}}")
	public static String getNicknameNotFound(String targetNickname) {
		return phrNicknameNotFound.getPhrase("shortCode",      SMSAppModuleConfiguration.APPShortCode,
                                             "appName",        SMSAppModuleConfiguration.APPName,
                                             "targetNickname", targetNickname);
	}
	
	@ConfigurableElement("Phrase used to inform the sender that the statefull private conversation can no longer be conducted (because we don't know who is the target user), therefore, he/she must try the stateles command. Variables: {{shortCode}}, {{appName}}")
	public static String getDoNotKnowWhoYouAreChattingTo() {
		return phrDoNotKnowWhoYouAreChattingTo.getPhrase("shortCode",      SMSAppModuleConfiguration.APPShortCode,
                                                         "appName",        SMSAppModuleConfiguration.APPName);
	}

}
