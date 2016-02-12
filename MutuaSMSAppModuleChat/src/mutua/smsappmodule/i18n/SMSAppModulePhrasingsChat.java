package mutua.smsappmodule.i18n;

/** <pre>
 * SMSAppModulePhrasingsChat.java
 * ==============================
 * (created by luiz, Aug 31, 2015)
 *
 * Declares and specifies the phrasings to be used by the "Chat SMS Module" implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the "Instant VAS SMSApp Phrasing" design pattern, as described in {@link Phrase}
 *
 * @see Phrase
 * @version $Id$
 * @author luiz
 */

public class SMSAppModulePhrasingsChat {
	
	/** @see #getPrivateMessage() */
	private final Phrase phrPrivateMessage;
	/** @see #getPrivateMessageDeliveryNotification() */
	private final Phrase phrPrivateMessageDeliveryNotification;
	/** @see #getDoNotKnowWhoYouAreChattingTo() */
	private final Phrase phrDoNotKnowWhoYouAreChattingTo;
	
	/** Fulfill the 'Phrase' objects with the default test values */
	public SMSAppModulePhrasingsChat(String shortCode, String appName) {
		this(shortCode, appName,
			"{{senderNickname}}: {{senderMessage}} - To answer, text P {{senderNickname}} [MSG] to {{shortCode}}",
			"{{appName}}: Your message has been sent to {{targetNickname}}",
			"{{appName}}: To chat privately, please text M <nickname> <msg> to {{shortCode}}");
	}
	
	/** Fulfill the 'Phrase' objects with the given values.
	 *  @param shortCode                              The application's short code to be used on phrases with {{shortCode}}
	 *  @param appName                                The application name to be used on phrases with {{appName}}
	 *  @param phrPrivateMessage                      see {@link #phrPrivateMessage}
	 *  @param phrPrivateMessageDeliveryNotification  see {@link #phrPrivateMessageDeliveryNotification}
	 *  @param phrDoNotKnowWhoYouAreChattingTo        see {@link #phrDoNotKnowWhoYouAreChattingTo} */
	public SMSAppModulePhrasingsChat(String shortCode, String appName,
		String phrPrivateMessage,
		String phrPrivateMessageDeliveryNotification,
		String phrDoNotKnowWhoYouAreChattingTo) {
		
		// constant parameters -- defines the common phrase parameters -- {{shortCode}} and {{appName}}
		String[] commonPhraseParameters = new String[] {
			"shortCode", shortCode,
			"appName",   appName};

		this.phrPrivateMessage                     = new Phrase(commonPhraseParameters, phrPrivateMessage);
		this.phrPrivateMessageDeliveryNotification = new Phrase(commonPhraseParameters, phrPrivateMessageDeliveryNotification);
		this.phrDoNotKnowWhoYouAreChattingTo       = new Phrase(commonPhraseParameters, phrDoNotKnowWhoYouAreChattingTo);
	}
	
	/*********************
	** PHRASING METHODS **
	*********************/
	
	/** Phrase sent to the target user when the sender user wants to send a chat private message.
	 *  Variables: {{shortCode}}, {{appName}}, {{senderNickname}}, {{senderMessage}} */
	public String getPrivateMessage(String senderNickname, String senderMessage) {
		return phrPrivateMessage.getPhrase("senderNickname", senderNickname,
                                           "senderMessage",  senderMessage);
	}
	
	/** Phrase sent to the sender user, who sent a private message, to inform of the correct delivery.
	 *  Variables: {{shortCode}}, {{appName}}, {{targetNickname}} */
	public String getPrivateMessageDeliveryNotification(String targetNickname) {
		return phrPrivateMessageDeliveryNotification.getPhrase("targetNickname", targetNickname);
	}

	/** Phrase used to inform the sender that the statefull private conversation can no longer be conducted (because we
	 *  don't know who is the target user), therefore, he/she must try the stateles command. Variables: {{shortCode}}, {{appName}} */
	public String getDoNotKnowWhoYouAreChattingTo() {
		return phrDoNotKnowWhoYouAreChattingTo.getPhrase();
	}
}