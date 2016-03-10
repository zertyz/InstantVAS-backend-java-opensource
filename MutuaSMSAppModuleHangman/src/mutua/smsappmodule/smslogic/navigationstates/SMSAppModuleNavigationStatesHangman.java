package mutua.smsappmodule.smslogic.navigationstates;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman.CommandNamesHangman.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman.CommandTriggersHangman.*;

import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;

/** <pre>
 * SMSAppModuleNavigationStatesHangman.java
 * ========================================
 * (created by luiz, Sep 18, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Hangman" SMSApp Module
 * 
 * Implements the "Instant VAS SMSApp Navigation States" design pattern, as described by
 * {@link NavigationStateCommons}.
 *
 * @see NavigationStateCommons
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleNavigationStatesHangman {
	
	/** Class to be statically imported by the Commands Implementation to represent the navigation states */
	public static class NavigationStatesNamesHangman {
		/** @see SMSAppModuleNavigationStatesHangman#nstEnteringMatchWord... */
		public final static String nstEnteringMatchWord                    = "EnteringMatchWord";
		/** @see SMSAppModuleNavigationStatesHangman#nstAnsweringToHangmanMatchInvitation */
		public final static String nstAnsweringToHangmanMatchInvitation    = "AnsweringToHangmanMatchInvitation";
		/** @see SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanHumanOpponent */
		public final static String nstGuessingWordFromHangmanHumanOpponent = "GuessingWordFromHangmanHumanOpponent";
		/** @see SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanBotOpponent */
		public final static String nstGuessingWordFromHangmanBotOpponent   = "GuessingWordFromHangmanBotOpponent";
	}
	
	// Navigation States Definitions
	////////////////////////////////
	
	/** The list of all navigation states -- for 'SMSProcessor' to be able to deserialize state names */
	public final NavigationStateCommons[] values;
	
	/** Navigation state part of the invitation process of a human to play a hangman match -- on this state, the user must enter the desired word to be guessed,
	 *  which will be processed by {@link SMSAppModuleCommandsHangman#cmdHoldMatchWord} */
	public final NavigationStateCommons nstEnteringMatchWord;
	
	/** State an invited user gets into after he/she is invited for a match, which is set by {@link SMSAppModuleCommandsHangman#cmdHoldMatchWord}.
	 *  The invited user answer will, then, be processed by {@link SMSAppModuleCommandsHangman#cmdAnswerToInvitation} */
	public final NavigationStateCommons nstAnsweringToHangmanMatchInvitation;
	
	/** Navigation state that indicates the user is playing a hangman match with a human (as the invited opponent), and his/her role is to guess the word */
	public final NavigationStateCommons nstGuessingWordFromHangmanHumanOpponent;
	
	/** Navigation state that indicates the user is playing a hangman match with the robot, and his/her hole is to guess the word */
	public final NavigationStateCommons nstGuessingWordFromHangmanBotOpponent;


	/** Provides the navigation states instance with the default test values */
	public SMSAppModuleNavigationStatesHangman(final SMSAppModuleCommandsHangman hangmanCommands) { 
		this(hangmanCommands,
		     // nstEnteringMatchWordTriggers
		     new Object[][] {{cmdHoldMatchWord,               trgLocalHoldMatchWord}},
		     // nstAnsweringToHangmanMatchInvitationTriggers
		     new Object[][] {{cmdHoldMatchWord,               trgLocalHoldMatchWord}},
		     // nstGuessingWordFromHangmanHumanOpponentTriggers
		     new Object[][] {{cmdSuggestLetterOrWordForHuman, trgLocalNewLetterOrWordSuggestion}},
		     // nstGuessingWordFromHangmanBotOpponentTriggers
		     new Object[][] {{cmdSuggestLetterOrWordForBot,   trgLocalNewLetterOrWordSuggestion}});
	}

	/** Provides the navigation states instance with custom triggers.
	 *  @param hangmanCommands                                  The instance of commands for this module
	 *  @param nstEnteringMatchWordTriggers                     The list of regular expression triggers and commands to execute when the user is on the {@link #nstEnteringMatchWord} navigation state. See {@link NavigationStateCommons#setCommandTriggers(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}
	 *  @param nstAnsweringToHangmanMatchInvitationTriggers     idem for {@link #nstAnsweringToHangmanMatchInvitation}
	 *  @param nstGuessingWordFromHangmanHumanOpponentTriggers  idem for {@link #nstGuessingWordFromHangmanHumanOpponent}
	 *  @param nstGuessingWordFromHangmanBotOpponentTriggers    idem for {@link #nstGuessingWordFromHangmanBotOpponent} */
	public SMSAppModuleNavigationStatesHangman(final SMSAppModuleCommandsHangman hangmanCommands,
	                                           final Object[][] nstEnteringMatchWordTriggers,
	                                           final Object[][] nstAnsweringToHangmanMatchInvitationTriggers,
	                                           final Object[][] nstGuessingWordFromHangmanHumanOpponentTriggers,
	                                           final Object[][] nstGuessingWordFromHangmanBotOpponentTriggers) {		
		nstEnteringMatchWord = new NavigationStateCommons(NavigationStatesNamesHangman.nstEnteringMatchWord) {{
			setCommandTriggers(nstEnteringMatchWordTriggers, hangmanCommands.values);
		}};
		nstAnsweringToHangmanMatchInvitation = new NavigationStateCommons(NavigationStatesNamesHangman.nstAnsweringToHangmanMatchInvitation) {{
			setCommandTriggers(nstAnsweringToHangmanMatchInvitationTriggers, hangmanCommands.values);
		}};
		nstGuessingWordFromHangmanHumanOpponent = new NavigationStateCommons(NavigationStatesNamesHangman.nstGuessingWordFromHangmanHumanOpponent) {{
			setCommandTriggers(nstGuessingWordFromHangmanHumanOpponentTriggers, hangmanCommands.values);
		}};
		nstGuessingWordFromHangmanBotOpponent = new NavigationStateCommons(NavigationStatesNamesHangman.nstGuessingWordFromHangmanBotOpponent) {{
			setCommandTriggers(nstGuessingWordFromHangmanBotOpponentTriggers, hangmanCommands.values);
		}};
		
		// the list of values
		values = new NavigationStateCommons[] {
			nstEnteringMatchWord,
			nstAnsweringToHangmanMatchInvitation,
			nstGuessingWordFromHangmanHumanOpponent,
			nstGuessingWordFromHangmanBotOpponent,
		};
	}
}