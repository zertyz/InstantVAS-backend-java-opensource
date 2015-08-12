package mutua.smsappmodule.i18n;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;

/** <pre>
 * SMSAppModulePhrasingsSubscription.java
 * ======================================
 * (created by luiz, Jul 14, 2015)
 *
 * Enumerates and specifies the phrasing to be used by the "Subscription" 'MutuaSMSAppModule' implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Phrasing design pattern, as described in {@link Phrase}
 *
 * @see Phrase
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModulePhrasingsSubscription {

	phrDoubleOptinStart           ("Welcome to {{appName}}. Please confirm your subscription by texting YES to {{shortCode}}"),
	phrDisagreeToSubscribe        ("You didn't agree to subscribe and will not be allowed to use the system. When you're ready, text {{appName}} to {{shortCode}} to try again."),
	phrSuccessfullySubscribed     ("{{appName}}: Subscription succeeded. Send HELP to {{shortCode}} to know the commands; RULES to see the regulation."),
	phrCouldNotSubscribe          ("{{appName}}: You could not be registered at this time. Please try again later."),
	phrUserRequestedUnsubscription("You are now unsubscribed from the {{appName}} GAME and will no longer receive invitations to play nor lucky numbers. To join again, send {{appName}} to {{shortCode}}"),
	phrLifecycleUnsubscription    ("You could not be billed for 3 consecutive attempts, so you are now unsubscribed from the {{appName}} GAME and will no longer receive invitations to play nor lucky numbers. To join again, please make sure you have enough credits and send {{appName}} to {{shortCode}}"),
	
	;
	
	public final Phrase phrase;
	
	private SMSAppModulePhrasingsSubscription(String... phrases) {
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
	
	@ConfigurableElement("Phrase sent to inform the user he/she is about to subscribe -- the navigation will go to 'answering double opt-in', where the user must answer with YES to continue. Variables: {{shortCode}}, {{appName}}")
	public static String getDoubleOptinStart() {
		return phrDoubleOptinStart.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
		                                     "appName",   SMSAppModuleConfiguration.APPName);
	}
	
	@ConfigurableElement("Phrase sent when the user answers NO (or doesn't answer YES) to the double opt-in process -- informs he/she has to agree to use the system. Variables: {{shortCode}}, {{appName}}")
	public static String getDisagreeToSubscribe() {
		return phrDisagreeToSubscribe.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
		                                        "appName",   SMSAppModuleConfiguration.APPName);
	}

	@ConfigurableElement("Phrase sent in response to a successfull user subscription attempt -- an app 'welcome & you are ready to use it' message. Variables: {{shortCode}}, {{appName}}")
	public static String getSuccessfullySubscribed() {
		return phrSuccessfullySubscribed.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                                   "appName",   SMSAppModuleConfiguration.APPName);
	}
	
	@ConfigurableElement("Phrase sent in response to a unsucessfull user subscription attempt -- 'you cannot use it yet'. Variables: {{shortCode}}, {{appName}}")
	public static String getCouldNotSubscribe() {
		return phrCouldNotSubscribe.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                              "appName",   SMSAppModuleConfiguration.APPName);
	}
	
	@ConfigurableElement("Sent to inform the subscription was canceled on the platform due to user request. Variables: {{shortCode}}, {{appName}}")
	public static String getUserRequestedUnsubscriptionNotification() {
		return phrUserRequestedUnsubscription.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                                        "appName",   SMSAppModuleConfiguration.APPName);
	}

	@ConfigurableElement("Sent to inform the subscription was canceled on the platform due to lifecycle rules. Variables: {{shortCode}}, {{appName}}")
	public static String getLifecycleUnsubscriptionNotification() {
		return phrLifecycleUnsubscription.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                                    "appName",   SMSAppModuleConfiguration.APPName);
	}
	
}
