package mutua.smsappmodule.i18n;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationChat;

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
	
	phrPrivateMessage                    ("{{senderNickname}}: {{senderMessage}} - To answer, text P {{senderNickname}} [MSG] to {{shortCode}}"),
	phrPrivateMessageDeliveryNotification("{{appName}}: Your message has been sent to {{targetNickname}}"),
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
	
	/** @see SMSAppModuleConfigurationChat#CHATphrPrivateMessage */
	public static String getPrivateMessage(String senderNickname, String senderMessage) {
		return phrPrivateMessage.getPhrase("shortCode",      SMSAppModuleConfiguration.APPShortCode,
                                           "appName",        SMSAppModuleConfiguration.APPName,
                                           "senderNickname", senderNickname,
                                           "senderMessage",  senderMessage);
	}
	
	/** @see SMSAppModuleConfigurationChat#CHATphrPrivateMessageDeliveryNotification */
	public static String getPrivateMessageDeliveryNotification(String targetNickname) {
		return phrPrivateMessageDeliveryNotification.getPhrase("shortCode",      SMSAppModuleConfiguration.APPShortCode,
                                                               "appName",        SMSAppModuleConfiguration.APPName,
                                                               "targetNickname", targetNickname);
	}

	/** @see SMSAppModuleConfigurationChat#CHATphrDoNotKnowWhoYouAreChattingTo */
	public static String getDoNotKnowWhoYouAreChattingTo() {
		return phrDoNotKnowWhoYouAreChattingTo.getPhrase("shortCode",      SMSAppModuleConfiguration.APPShortCode,
                                                         "appName",        SMSAppModuleConfiguration.APPName);
	}

}
