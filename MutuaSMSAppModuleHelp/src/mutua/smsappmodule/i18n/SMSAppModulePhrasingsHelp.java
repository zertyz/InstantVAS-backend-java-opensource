package mutua.smsappmodule.i18n;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationHelp;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;

/** <pre>
 * SMSAppModulePhrasingsHelp.java
 * ==============================
 * (created by luiz, Jul 13, 2015)
 *
 * Enumerates and specifies the phrasing to be used by the "Help" 'MutuaSMSAppModule' implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Phrasing design pattern, as described in {@link Phrase}
 *
 * @see Phrase
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModulePhrasingsHelp {

	phrNewUsersFallbackHelp     ("Welcome to {{appName}}, the service that can provide you whatever this service provides. Text {{appName}} to {{shortCode}} to subscribe"),
	phrExistingUsersFallbackHelp("{{appName}}: unknown command. Please send HELP to see the full list. Short list: HELP for the list of commands; RULES for the regulation. "),
	phrStatelessHelp            ("This is the help you can access from anywhere, without changing the usage flow of the game or app -- it will not change the navigation state"),
//	STATEFUL_HELPS              ("those come from the configuration class"),
	phrCompositeHelps           ("You can play the {{appName}} game in 2 ways: guessing someone's word or inviting someone to play with your word " +
	                             "You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number " +
	                             "Every week, 1 lucky number is selected to win the prize. Send an option to {{shortCode}}: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help",
	                             "This is the extended help message... You can place as many as you want."),
	
	;
	
	public final Phrase phrase;
	
	private SMSAppModulePhrasingsHelp(String... phrases) {
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
	
	@ConfigurableElement("Phrase sent when a new user sends an unrecognized keyword, possibly instructing him/her on how to register. Variables: {{shortCode}}, {{appName}}")
	public static String getNewUsersFallbackHelp() {
		return phrNewUsersFallbackHelp.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                                 "appName",   SMSAppModuleConfiguration.APPName);
	}
	
	@ConfigurableElement("Phrase sent when an existing user attempts to send an unrecognized command, to give him/her a quick list of commands. Variables: {{shortCode}}, {{appName}}")
	public static String getExistingUsersFallbackHelp() {
		return phrExistingUsersFallbackHelp.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                                      "appName",   SMSAppModuleConfiguration.APPName);
	}

	@ConfigurableElement("These are the general help messages, sent in response to the HELP command anywhere in the app navigation states. This message will not interrupt the flow and the user may continue normally after receiving this message. Variables: {{shortCode}}, {{appName}}")
	public static String getStatelessHelpMessage() {
		return phrStatelessHelp.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                          "appName",   SMSAppModuleConfiguration.APPName);
	}

	@ConfigurableElement("These are the detailed help messages, sent in response to the HELP/RULES command that will change the navigation state. You can set a second, third and so on help messages, which will be sent in response to the MORE command. Variables: {{shortCode}}, {{appName}}")
	public static String getCompositeHelpMessage(int helpMessageNumber) {
		String[] helps = phrCompositeHelps.getPhrases("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                                      "appName",   SMSAppModuleConfiguration.APPName);
		if (helps.length > helpMessageNumber) {
			return helps[helpMessageNumber];
		} else {
			return null;
		}
	}

	/** Retrieve the navigation state specific help messages set on the configuration class */
	public static String getStatefulHelpMessage(INavigationState navigationState) {
		return SMSAppModuleConfigurationHelp.getStatefulHelpMessage(navigationState);
	}
}
