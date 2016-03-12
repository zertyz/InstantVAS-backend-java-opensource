package mutua.smsappmodule.i18n;

import java.util.HashMap;

import mutua.smsappmodule.smslogic.navigationstates.NavigationState;

/** <pre>
 * SMSAppModulePhrasingsHelp.java
 * ==============================
 * (created by luiz, Jul 13, 2015)
 *
 * Declares and specifies the phrasings to be used by the "Help SMS Module" implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the "Instant VAS SMSApp Phrasing" design pattern, as described in {@link Phrase}
 *
 * @see Phrase
 * @version $Id$
 * @author luiz
 */

public class SMSAppModulePhrasingsHelp {
	
	/** @see #getNewUsersFallbackHelp() */
	private final Phrase                  phrNewUsersFallbackHelp;
	/** @see #getExistingUsersFallbackHelp() */
	private final Phrase                  phrExistingUsersFallbackHelp;
	/** @see #getStatelessHelpMessage() */
	private final Phrase                  phrStatelessHelp;
	/** @see #getStatefulHelpMessage(INavigationState) */
	private final HashMap<String, Phrase> phrStatefulHelpMessagesMap;	// contextualized helps based on the current navigation state
	/** @see #getCompositeHelpMessage(int) */
	private final Phrase                  phrCompositeHelps;
	
	/** Fulfill the 'Phrase' objects with the default test values */
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

	/** Fulfill the 'Phrase' objects with the given values.
	 *  @param shortCode                    The application's short code to be used on phrases with {{shortCode}}
	 *  @param appName                      The application name to be used on phrases with {{appName}}
	 *  @param phrNewUsersFallbackHelp      see {@link #phrNewUsersFallbackHelp} 
	 *  @param phrExistingUsersFallbackHelp see {@link #phrExistingUsersFallbackHelp}
	 *  @param phrStatelessHelp             see {@link #phrStatelessHelp}
	 *  @param phrStatefulHelpMessages      := {{(String)navigationStateName, (String)text}, ...} -- also, see {@link #phrStatefulHelpMessagesMap}
	 *  @param phrCompositeHelps            see {@link #phrCompositeHelps}*/
	public SMSAppModulePhrasingsHelp(String shortCode, String appName,
		String phrNewUsersFallbackHelp,
		String phrExistingUsersFallbackHelp,
		String phrStatelessHelp,
		String[][] phrStatefulHelpMessages,
		String[] phrCompositeHelps) {
		
		// constant parameters -- defines the common phrase parameters -- {{shortCode}} and {{appName}}
		String[] commonPhraseParameters = new String[] {
			"shortCode", shortCode,
			"appName",   appName};
		
		// phrases
		this.phrNewUsersFallbackHelp      = new Phrase(commonPhraseParameters, phrNewUsersFallbackHelp);
		this.phrExistingUsersFallbackHelp = new Phrase(commonPhraseParameters, phrExistingUsersFallbackHelp);
		this.phrStatelessHelp             = new Phrase(commonPhraseParameters, phrStatelessHelp);
		
		this.phrStatefulHelpMessagesMap   = new HashMap<String, Phrase>();
		for (String[] statefulHelpMessage : phrStatefulHelpMessages) {
			String navigationStateName = statefulHelpMessage[0];
			String stateHelpMessage    = statefulHelpMessage[1];
			this.phrStatefulHelpMessagesMap.put(navigationStateName, new Phrase(commonPhraseParameters, stateHelpMessage));
		}
		
		this.phrCompositeHelps            = new Phrase(phrCompositeHelps);
	}
	
	/*********************
	** PHRASING METHODS **
	*********************/
	
	/** Phrase sent when a new user sends an unrecognized keyword, possibly instructing him/her on how to register.
	 *  Variables: {{shortCode}}, {{appName}} */
	public String getNewUsersFallbackHelp() {
		return phrNewUsersFallbackHelp.getPhrase();
	}
	
	/** Phrase sent when an existing user attempts to send an unrecognized command, to give him/her a quick list of commands.
	 *  Variables: {{shortCode}}, {{appName}} */
	public String getExistingUsersFallbackHelp() {
		return phrExistingUsersFallbackHelp.getPhrase();
	}

	/** These are the general help messages, sent in response to the HELP command anywhere in the app navigation states.
	 *  This message will not interrupt the flow and the user may continue normally after receiving this message. Variables: {{shortCode}}, {{appName}} */
	public String getStatelessHelpMessage() {
		return phrStatelessHelp.getPhrase();
	}

	/** These are the detailed help messages, sent in response to the HELP/RULES command that will change the navigation state.
	 *  You can set a second, third and so on help messages, which will be sent in response to the MORE command. Variables: {{shortCode}}, {{appName}} */
	public String getCompositeHelpMessage(int helpMessageNumber) {
		String[] helps = phrCompositeHelps.getPhrases();
		if (helps.length > helpMessageNumber) {
			return helps[helpMessageNumber];
		} else {
			return null;
		}
	}

	/** Retrieve the navigation state specific help messages set on the configuration class */
	public String getStatefulHelpMessage(NavigationState navigationState) {
		Phrase phrase = phrStatefulHelpMessagesMap.get(navigationState.getNavigationStateName());
		return phrase != null ? phrase.getPhrase() : null;
	}
}
