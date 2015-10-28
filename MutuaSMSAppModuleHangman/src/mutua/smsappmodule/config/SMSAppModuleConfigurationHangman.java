package mutua.smsappmodule.config;

import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman.*;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman;

/** <pre>
 * SMSAppModuleConfigurationHangman.java
 * =====================================
 * (created by luiz, Sep 18, 2015)
 *
 * Defines the "Hangman" module configuration variables, implementing the Mutua SMSApp
 * Configuration design pattern, as described in 'SMSAppModuleConfiguration'
 *
 * @see SMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationHangman {

	
	/*************************************************
	** MutuaICConfiguration CONFIGURABLE PROPERTIES **
	*************************************************/
	
	@ConfigurableElement("Default prefix for invited & new users -- The suffix are the last 4 phone number digits")
	public static String DEFAULT_NICKNAME_PREFIX    = "Guest";

	
	// phrasing
	///////////
	
	@ConfigurableElement("The character to be used to draw the head on the gallows ASCII art")
	public static String HANGMANphr_headCharacter                                            = phr_headCharacter.toString();
	@ConfigurableElement("The character to be used to draw the left arm on the gallows ASCII art")
	public static String HANGMANphr_leftArmCharacter                                         = phr_leftArmCharacter.toString();
	@ConfigurableElement("The character to be used to draw the chest on the gallows ASCII art")
	public static String HANGMANphr_chestCharacter                                           = phr_chestCharacter.toString();
	@ConfigurableElement("The character to be used to draw the right arm on the gallows ASCII art")
	public static String HANGMANphr_rightArmCharacter                                        = phr_rightArmCharacter.toString();
	@ConfigurableElement("The character to be used to draw the left leg on the gallows ASCII art")
	public static String HANGMANphr_leftLegCharacter                                         = phr_leftLegCharacter.toString();
	@ConfigurableElement("The character to be used to draw the right leg on the gallows ASCII art")
	public static String HANGMANphr_rightLegCharacter                                        = phr_rightLegCharacter.toString();
	@ConfigurableElement("Text ASCII art to present the gallows. Used in the next phrases to present the status of the hangman match")
	public static String HANGMANphr_gallowsArt                                               = phr_gallowsArt.toString();
	@ConfigurableElement("Text ASCII art used in the next phrases to present the gallows for a winning match")
	public static String HANGMANphr_winningArt                                               = phr_winningArt.toString();
	@ConfigurableElement("Text ASCII art used in the next phrases to present the gallows for a losing match")
	public static String HANGMANphr_losingArt                                                = phr_losingArt.toString();
	@ConfigurableElement("Response to the request of inviting a human opponent to play -- Asks for his/her phone or nickname. Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}}")
	public static String HANGMANphrAskOpponentNicknameOrPhone                                = phrAskOpponentNicknameOrPhone.toString();
	@ConfigurableElement("Response to the request of inviting a human opponent to play -- Asks for the word he/she wants to be guessed. Variables: {{shortCode}}, {{appName}}, {{opponentNickname}}")
	public static String HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation = phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation.toString();
	@ConfigurableElement("Phrase to notify the inviting player that the notification has been sent to the opponent. Variables: {{shortCode}}, {{appName}}, {{invitedPlayerNickname}}")
	public static String HANGMANphrInvitationNotificationForInvitingPlayer                   = phrInvitationNotificationForInvitingPlayer.toString();
	@ConfigurableElement("Phrase to notify the invited player that he/she has been invited for a hangman match. Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}}")
	public static String HANGMANphrInvitationNotificationForInvitedPlayer                    = phrInvitationNotificationForInvitedPlayer.toString();
	@ConfigurableElement("Respose sent to the invited player when he/she accepts a match. Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{usedLetters}}")
	public static String HANGMANphrWordGuessingPlayerMatchStart                              = phrWordGuessingPlayerMatchStart.toString();
	@ConfigurableElement("Notification sent to the word providing player when the opponent accepts the match. Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrWordProvidingPlayerMatchStart                             = phrWordProvidingPlayerMatchStart.toString();
	@ConfigurableElement("Response sent to the word guessing player when he/she guesses (sends) a letter or word. Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{usedLetters}}")
	public static String HANGMANphrWordGuessingPlayerStatus                                  = phrWordGuessingPlayerStatus.toString();
	@ConfigurableElement("Notification sent to the word providing player when the opponent tries to guess his/her word. Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{guessedLetter}}, {{usedLetters}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrWordProvidingPlayerStatus                                 = phrWordProvidingPlayerStatus.toString();
	@ConfigurableElement("Response sent to the word guessing player when he/she wins the match. Variables: {{shortCode}}, {{appName}}, {{winningArt}}, {{word}}, {{wordProvidingPlayerNickname}}")
	public static String HANGMANphrWinningMessageForWordGuessingPlayer                       = phrWinningMessageForWordGuessingPlayer.toString();
	@ConfigurableElement("Notification sent to the word providing player when the opponent wins the match. Variables: {{shortCode}}, {{appName}}, {{winningArt}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrWinningMessageForWordProvidingPlayer                      = phrWinningMessageForWordProvidingPlayer.toString();
	@ConfigurableElement("Response sent to the word guessing player when he/she loses the match. Variables: {{shortCode}}, {{appName}}, {{losingArt}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrLosingMessageForWordGuessingPlayer                        = phrLosingMessageForWordGuessingPlayer.toString();
	@ConfigurableElement("Notification sent to the word providing player when the opponent loses the match. Variables: {{shortCode}}, {{appName}}, {{losingArt}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrLosingMessageForWordProvidingPlayer                       = phrLosingMessageForWordProvidingPlayer.toString();
	@ConfigurableElement("Notification sent to the word guessing player when the match has been canceled -- tipically due to an action from him/herself. Variables: {{shortCode}}, {{appName}}, {{wordProviderPlayerNickname}}")
	public static String HANGMANphrMatchGiveupNotificationForWordGuessingPlayer              = phrMatchGiveupNotificationForWordGuessingPlayer.toString();
	@ConfigurableElement("Notification sent to the word providing player when the match has been canceled -- tipically due to an action of the opponent. Variables: {{shortCode}}, {{appName}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrMatchGiveupNotificationForWordProvidingPlayer             = phrMatchGiveupNotificationForWordProvidingPlayer.toString();


	// command patterns
	///////////////////
	
	@ConfigurableElement("Global triggers (available to all navigation states) to invite a human player for a hangman match. Receives 1 parameter: the user identification string (phone number or nickname)")
	public static String[] HANGMANtrgGlobalInviteNicknameOrPhoneNumber    = trgGlobalInviteNicknameOrPhoneNumber;
	@ConfigurableElement("Local triggers (available only to the 'inviting user for a match' navigation state) to compute the word to be used on the hangman match")
	public static String[] HANGMANtrgLocalHoldMatchWord                   = trgLocalHoldMatchWord;
	@ConfigurableElement("Local triggers (available only to the 'answering invitation' navigation state) that should be interpreted as the invited user having accepted to play a hangman match")
	public static String[] HANGMANtrgLocalAcceptMatchInvitation           = trgLocalAcceptMatchInvitation;
	@ConfigurableElement("Local triggers (available only to the 'playing a hangman match to a bot or human' navigation state) that will recognize patterns to be interpreted as a letter or word of a hangman match")
	public static String[] HANGMANtrgLocalNewLetterOrWordSuggestion       = trgLocalNewLetterOrWordSuggestion;

	
	/************
	** METHODS **
	************/
	
	/** Apply on-the-fly phrasing changes */
	public static void applyPhrasingConfiguration() {
		phr_headCharacter                                            .setPhrases(HANGMANphr_headCharacter);
	    phr_leftArmCharacter                                         .setPhrases(HANGMANphr_leftArmCharacter);
	    phr_chestCharacter                                           .setPhrases(HANGMANphr_chestCharacter);
	    phr_rightArmCharacter                                        .setPhrases(HANGMANphr_rightArmCharacter);
	    phr_leftLegCharacter                                         .setPhrases(HANGMANphr_leftLegCharacter);
	    phr_rightLegCharacter                                        .setPhrases(HANGMANphr_rightLegCharacter);
	    phr_gallowsArt                                               .setPhrases(HANGMANphr_gallowsArt);
	    phr_winningArt                                               .setPhrases(HANGMANphr_winningArt);
	    phr_losingArt                                                .setPhrases(HANGMANphr_losingArt);
	    phrAskOpponentNicknameOrPhone                                .setPhrases(HANGMANphrAskOpponentNicknameOrPhone);
	    phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation .setPhrases(HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation);
	    phrInvitationNotificationForInvitingPlayer                   .setPhrases(HANGMANphrInvitationNotificationForInvitingPlayer);
	    phrInvitationNotificationForInvitedPlayer                    .setPhrases(HANGMANphrInvitationNotificationForInvitedPlayer);
	    phrWordGuessingPlayerMatchStart                              .setPhrases(HANGMANphrWordGuessingPlayerMatchStart);
	    phrWordProvidingPlayerMatchStart                             .setPhrases(HANGMANphrWordProvidingPlayerMatchStart);
	    phrWordGuessingPlayerStatus                                  .setPhrases(HANGMANphrWordGuessingPlayerStatus);
	    phrWordProvidingPlayerStatus                                 .setPhrases(HANGMANphrWordProvidingPlayerStatus);
	    phrWinningMessageForWordGuessingPlayer                       .setPhrases(HANGMANphrWinningMessageForWordGuessingPlayer);
	    phrWinningMessageForWordProvidingPlayer                      .setPhrases(HANGMANphrWinningMessageForWordProvidingPlayer);
	    phrLosingMessageForWordGuessingPlayer                        .setPhrases(HANGMANphrLosingMessageForWordGuessingPlayer);
	    phrLosingMessageForWordProvidingPlayer                       .setPhrases(HANGMANphrLosingMessageForWordProvidingPlayer);
	    phrMatchGiveupNotificationForWordGuessingPlayer              .setPhrases(HANGMANphrMatchGiveupNotificationForWordGuessingPlayer);
	    phrMatchGiveupNotificationForWordProvidingPlayer             .setPhrases(HANGMANphrMatchGiveupNotificationForWordProvidingPlayer);	}
	
	/** Apply on-the-fly command trigger changes */
	public static void applyTriggerConfiguration() {
		for (INavigationState navigationState : SMSAppModuleNavigationStatesHangman.values()) {
			navigationState.setCommandTriggersFromConfigurationValues();
		}
	}
	
	/** Apply the following on-the-fly configuration changes: phrasing, triggers */
	public static void applyConfiguration() {
		applyPhrasingConfiguration();
		applyTriggerConfiguration();
	}
	
	
	static {
		applyConfiguration();
	}
}