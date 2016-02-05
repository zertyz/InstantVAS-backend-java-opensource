package mutua.smsappmodule.i18n;

/** <pre>
 * SMSAppModulePhrasingsSubscription.java
 * ======================================
 * (created by luiz, Jul 14, 2015)
 *
 * Declares and specifies the phrasings to be used by the "Subscription SMS Module" implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the "Instant VAS SMSApp Phrasing" design pattern, as described in {@link Phrase}
 *
 * @see Phrase
 * @version $Id$
 * @author luiz
 */

public class SMSAppModulePhrasingsSubscription {
	
	/** @see #getDoubleOptinStart() */
	private final Phrase phrDoubleOptinStart;
	/** @see #getDisagreeToSubscribe() */
	private final Phrase phrDisagreeToSubscribe;
	/** @see #getSuccessfullySubscribed() */
	private final Phrase phrSuccessfullySubscribed;
	/** @see #getCouldNotSubscribe() */
	private final Phrase phrCouldNotSubscribe;
	/** @see #getUserRequestedUnsubscriptionNotification() */
	private final Phrase phrUserRequestedUnsubscription;
	/** @see #getLifecycleUnsubscriptionNotification() */
	private final Phrase phrLifecycleUnsubscription;
	
	/** Fulfill the Phrase objects with the default test values */
	public SMSAppModulePhrasingsSubscription(String shortCode, String appName, String priceTag) {
		this(shortCode, appName, priceTag,
			"Welcome to {{appName}}. Please confirm your subscription by texting YES to {{shortCode}} -- You'll be able to play & chat unlimitedly and will be billed in ${{priceTag}} every week. For more info about the service, please text HELP.",
			"You didn't agree to subscribe and will not be allowed to use the system. When you're ready, text {{appName}} to {{shortCode}} to try again.",
			"{{appName}}: Subscription succeeded. Send HELP to {{shortCode}} to know the commands; RULES to see the regulation.",
			"{{appName}}: You could not be registered at this time. Please try again later.",
			"{{appName}}: Your subscription has been terminated and you will no longer receive invitations to play nor lucky numbers. To join again, send {{appName}} to {{shortCode}} -- ${{priceTag}}/week.",
			"{{appName}}: You could not be billed for 3 consecutive attempts, so your subscription has been revoked -- you will no longer receive invitations to play nor lucky numbers. To join again, please make sure you have enough credits and send {{appName}} to {{shortCode}} -- ${{priceTag}}/week.");
	}

	/** Fulfill the Phrase objects with the given values.
	 *  @param shortCode                       The application's short code to be used on phrases with {{shortCode}}
	 *  @param appName                         The application name to be used on phrases with {{appName}}
	 *  @param priceTag                        The application's price to be used on phrases with {{priceTag}}
	 *  @param phrDoubleOptinStart             see {@link #phrDoubleOptinStart} 
	 *  @param phrDisagreeToSubscribe          see {@link #phrDisagreeToSubscribe} 
	 *  @param phrSuccessfullySubscribed       see {@link #phrSuccessfullySubscribed} 
	 *  @param phrCouldNotSubscribe            see {@link #phrCouldNotSubscribe} 
	 *  @param phrUserRequestedUnsubscription  see {@link #phrUserRequestedUnsubscription} 
	 *  @param phrLifecycleUnsubscription      see {@link #phrLifecycleUnsubscription} */
	public SMSAppModulePhrasingsSubscription(String shortCode, String appName, String priceTag,
		String phrDoubleOptinStart,
		String phrDisagreeToSubscribe,
		String phrSuccessfullySubscribed,
		String phrCouldNotSubscribe,
		String phrUserRequestedUnsubscription,
		String phrLifecycleUnsubscription) {
		
		// constant parameters -- defines the common phrase parameters -- {{shortCode}} and {{appName}}
		String[] commonPhraseParameters = new String[] {
			"shortCode", shortCode,
			"appName",   appName,
			"priceTag",  priceTag};

		this.phrDoubleOptinStart            = new Phrase(commonPhraseParameters, phrDoubleOptinStart);
		this.phrDisagreeToSubscribe         = new Phrase(commonPhraseParameters, phrDisagreeToSubscribe);
		this.phrSuccessfullySubscribed      = new Phrase(commonPhraseParameters, phrSuccessfullySubscribed);
		this.phrCouldNotSubscribe           = new Phrase(commonPhraseParameters, phrCouldNotSubscribe);
		this.phrUserRequestedUnsubscription = new Phrase(commonPhraseParameters, phrUserRequestedUnsubscription);
		this.phrLifecycleUnsubscription     = new Phrase(commonPhraseParameters, phrLifecycleUnsubscription);
	}
	
	/*********************
	** PHRASING METHODS **
	*********************/
	
	/** Phrase sent to inform the user he/she is about to subscribe -- the navigation will go to 'answering double opt-in',
	 *  where the user must answer with YES to continue. Variables: {{shortCode}}, {{appName}}, {{priceTag}} */
	public String getDoubleOptinStart() {
		return phrDoubleOptinStart.getPhrase();
	}
	
	/** Phrase sent when the user answers NO (or doesn't answer YES) to the double opt-in
	 *  process -- informs he/she has to agree to use the system. Variables: {{shortCode}}, {{appName}}, {{priceTag}} */
	public String getDisagreeToSubscribe() {
		return phrDisagreeToSubscribe.getPhrase();
	}

	/** Phrase sent in response to a successful user subscription attempt -- an app 'welcome & you are ready
	 *  to use it' message. Variables: {{shortCode}}, {{appName}}, {{priceTag}} */
	public String getSuccessfullySubscribed() {
		return phrSuccessfullySubscribed.getPhrase();
	}
	
	/** Phrase sent in response to a unsuccessful user subscription attempt -- 'you cannot use it yet'.
	 *  Variables: {{shortCode}}, {{appName}}, {{priceTag}} */
	public String getCouldNotSubscribe() {
		return phrCouldNotSubscribe.getPhrase();
	}
	
	/** Sent to inform the subscription was canceled on the platform due to user request. Variables: {{shortCode}}, {{appName}}, {{priceTag}} */
	public String getUserRequestedUnsubscriptionNotification() {
		return phrUserRequestedUnsubscription.getPhrase();
	}

	/** Sent to inform the subscription was canceled on the platform due to lifecycle rules. Variables: {{shortCode}}, {{appName}}, {{priceTag}} */
	public String getLifecycleUnsubscriptionNotification() {
		return phrLifecycleUnsubscription.getPhrase();
	}
	
}