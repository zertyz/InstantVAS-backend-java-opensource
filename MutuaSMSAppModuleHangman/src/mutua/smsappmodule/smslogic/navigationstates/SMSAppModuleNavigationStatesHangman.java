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
 * {@link NavigationState}.
 *
 * @see NavigationState
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
	public final NavigationState[] values;
	
	/** Navigation state part of the invitation process of a human to play a hangman match -- on this state, the user must enter the desired word to be guessed,
	 *  which will be processed by {@link SMSAppModuleCommandsHangman#cmdHoldMatchWord} */
	public final NavigationState nstEnteringMatchWord;
	
	/** State an invited user gets into after he/she is invited for a match, which is set by {@link SMSAppModuleCommandsHangman#cmdHoldMatchWord}.
	 *  The invited user answer will, then, be processed by {@link SMSAppModuleCommandsHangman#cmdAnswerToInvitation} */
	public final NavigationState nstAnsweringToHangmanMatchInvitation;
	
	/** Navigation state that indicates the user is playing a hangman match with a human (as the invited opponent), and his/her role is to guess the word */
	public final NavigationState nstGuessingWordFromHangmanHumanOpponent;
	
	/** Navigation state that indicates the user is playing a hangman match with the robot, and his/her hole is to guess the word */
	public final NavigationState nstGuessingWordFromHangmanBotOpponent;


	/** Provides the navigation states instance with the default test values */
	public SMSAppModuleNavigationStatesHangman() { 
		this(// nstEnteringMatchWordTriggers
		     new Object[][] {{cmdHoldMatchWord,               trgLocalHoldMatchWord}},
		     // nstAnsweringToHangmanMatchInvitationTriggers
		     new Object[][] {{cmdHoldMatchWord,               trgLocalHoldMatchWord}},
		     // nstGuessingWordFromHangmanHumanOpponentTriggers
		     new Object[][] {{cmdSuggestLetterOrWordForHuman, trgLocalSingleLetterSuggestion, trgLocalWordSuggestionFallback}},
		     // nstGuessingWordFromHangmanBotOpponentTriggers
		     new Object[][] {{cmdSuggestLetterOrWordForBot,   trgLocalSingleLetterSuggestion, trgLocalWordSuggestionFallback}});
	}

	/** Provides the navigation states instance with custom triggers.
	 *  @param nstEnteringMatchWordTriggers                     The list of regular expression triggers and commands to execute when the user is on the {@link #nstEnteringMatchWord} navigation state. See {@link NavigationState#applyCommandTriggersData(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}
	 *  @param nstAnsweringToHangmanMatchInvitationTriggers     idem for {@link #nstAnsweringToHangmanMatchInvitation}
	 *  @param nstGuessingWordFromHangmanHumanOpponentTriggers  idem for {@link #nstGuessingWordFromHangmanHumanOpponent}
	 *  @param nstGuessingWordFromHangmanBotOpponentTriggers    idem for {@link #nstGuessingWordFromHangmanBotOpponent} */
	public SMSAppModuleNavigationStatesHangman(Object[][] nstEnteringMatchWordTriggers,
	                                           Object[][] nstAnsweringToHangmanMatchInvitationTriggers,
	                                           Object[][] nstGuessingWordFromHangmanHumanOpponentTriggers,
	                                           Object[][] nstGuessingWordFromHangmanBotOpponentTriggers) {		
		nstEnteringMatchWord = new NavigationState(NavigationStatesNamesHangman.nstEnteringMatchWord, nstEnteringMatchWordTriggers);
		nstAnsweringToHangmanMatchInvitation = new NavigationState(NavigationStatesNamesHangman.nstAnsweringToHangmanMatchInvitation, nstAnsweringToHangmanMatchInvitationTriggers);
		nstGuessingWordFromHangmanHumanOpponent = new NavigationState(NavigationStatesNamesHangman.nstGuessingWordFromHangmanHumanOpponent, nstGuessingWordFromHangmanHumanOpponentTriggers, cmdGiveUpCurrentHumanMatch);
		nstGuessingWordFromHangmanBotOpponent   = new NavigationState(NavigationStatesNamesHangman.nstGuessingWordFromHangmanBotOpponent,   nstGuessingWordFromHangmanBotOpponentTriggers,   cmdGiveUpCurrentBotMatch);
		
		// the list of values
		values = new NavigationState[] {
			nstEnteringMatchWord,
			nstAnsweringToHangmanMatchInvitation,
			nstGuessingWordFromHangmanHumanOpponent,
			nstGuessingWordFromHangmanBotOpponent,
		};
	}
}