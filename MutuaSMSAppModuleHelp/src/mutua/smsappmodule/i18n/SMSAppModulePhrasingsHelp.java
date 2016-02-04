package mutua.smsappmodule.i18n;

import java.util.HashMap;

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

public class SMSAppModulePhrasingsHelp {
	
	/** phrase parameters -- {{shortCode}} and {{appName}} */
	private final String[] phraseParameters;
	
	// Phrase objects
	private final Phrase                  newUsersFallbackHelp;
	private final Phrase                  existingUsersFallbackHelp;
	private final Phrase                  statelessHelp;
	private final HashMap<String, Phrase> statefulHelpMessagesMap;	// contextualized helps based on the current navigation state
	private final Phrase                  compositeHelps;

	/** Fulfill the Phrase objects with the given values.
	 *  @param shortCode                 The application's short code to be used on phrases as {{shortCode}}
	 *  @param appName                   The application name to be used on phrases as {{appName}}
	 *  @param newUsersFallbackHelp      see {@link SMSAppModulePhrasingsHelp#getNewUsersFallbackHelp()} 
	 *  @param existingUsersFallbackHelp see {@link SMSAppModulePhrasingsHelp#getExistingUsersFallbackHelp()}
	 *  @param statelessHelp             see {@link SMSAppModulePhrasingsHelp#getStatelessHelpMessage()}
	 *  @param statefulHelpMessages      := {{(String)navigationStateName, (String)text}, ...} -- also, see {@link SMSAppModulePhrasingsHelp#getStatefulHelpMessage(INavigationState)}
	 *  @param compositeHelps            see {@link SMSAppModulePhrasingsHelp#getCompositeHelpMessage(int)}*/
	public SMSAppModulePhrasingsHelp(String shortCode, String appName,
		String newUsersFallbackHelp,
		String existingUsersFallbackHelp,
		String statelessHelp,
		String[][] statefulHelpMessages,
		String[] compositeHelps) {
		
		// parameters
		this.phraseParameters = new String[] {
			"shortCode", shortCode,
			"appName",   appName};
		
		// phrases
		this.newUsersFallbackHelp      = new Phrase(newUsersFallbackHelp);
		this.existingUsersFallbackHelp = new Phrase(existingUsersFallbackHelp);
		this.statelessHelp             = new Phrase(statelessHelp);
		
		this.statefulHelpMessagesMap   = new HashMap<String, Phrase>();
		for (String[] statefulHelpMessage : statefulHelpMessages) {
			String navigationStateName = statefulHelpMessage[0];
			String stateHelpMessage    = statefulHelpMessage[1];
			this.statefulHelpMessagesMap.put(navigationStateName, new Phrase(stateHelpMessage));
		}
		
		this.compositeHelps            = new Phrase(compositeHelps);
	}

	/** Fulfill the Phrase objects with the default test values */
	public SMSAppModulePhrasingsHelp(String shortCode, String appName, String[][] phrStatefulHelpMessages) {
		this(shortCode, appName,
			"Welcome to {{appName}}, the service that can provide you whatever this service provides. Text {{appName}} to {{shortCode}} to subscribe",
			"{{appName}}: unknown command. Please send HELP to see the full list. Short list: HELP for the list of commands; RULES for the regulation. ",
			"This is the help you can access from anywhere, without changing the usage flow of the game or app -- it will not change the navigation state",
			phrStatefulHelpMessages,
			new String[] {"You can play the {{appName}} game in 2 ways: guessing someone's word or inviting someone to play with your word " +
		                  "You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number " +
		                  "Every week, 1 lucky number is selected to win the prize. Send an option to {{shortCode}}: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help",
		                  "This is the extended help message... You can place as many as you want."});
	}

	
	/*********************
	** PHRASING METHODS **
	*********************/
	
	/** Phrase sent when a new user sends an unrecognized keyword, possibly instructing him/her on how to register. Variables: {{shortCode}}, {{appName}} */
	public String getNewUsersFallbackHelp() {
		return newUsersFallbackHelp.getPhrase(phraseParameters);
	}
	
	/** Phrase sent when an existing user attempts to send an unrecognized command, to give him/her a quick list of commands. Variables: {{shortCode}}, {{appName}} */
	public String getExistingUsersFallbackHelp() {
		return existingUsersFallbackHelp.getPhrase(phraseParameters);
	}

	/** These are the general help messages, sent in response to the HELP command anywhere in the app navigation states. This message will not interrupt the flow and the user may continue normally after receiving this message. Variables: {{shortCode}}, {{appName}} */
	public String getStatelessHelpMessage() {
		return statelessHelp.getPhrase(phraseParameters);
	}

	/** These are the detailed help messages, sent in response to the HELP/RULES command that will change the navigation state. You can set a second, third and so on help messages, which will be sent in response to the MORE command. Variables: {{shortCode}}, {{appName}} */
	public String getCompositeHelpMessage(int helpMessageNumber) {
		String[] helps = compositeHelps.getPhrases(phraseParameters);
		if (helps.length > helpMessageNumber) {
			return helps[helpMessageNumber];
		} else {
			return null;
		}
	}

	/** Retrieve the navigation state specific help messages set on the configuration class */
	public String getStatefulHelpMessage(INavigationState navigationState) {
		Phrase phrase = statefulHelpMessagesMap.get(navigationState.getNavigationStateName());
		return phrase != null ? phrase.getPhrase(phraseParameters) : null;
	}
}
