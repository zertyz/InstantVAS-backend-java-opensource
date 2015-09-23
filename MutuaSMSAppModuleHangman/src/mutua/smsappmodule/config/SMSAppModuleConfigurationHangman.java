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
	@ConfigurableElement("Text ASCII art to present the gallows. Used by the next phrases to present the status of the hangman match")
	public static String HANGMANphr_gallowsArt                                               = phr_gallowsArt.toString();
	
	@ConfigurableElement("Response to the request of inviting a human opponent to play -- Asks for his/her phone or nickname. Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}}")
	public static String HANGMANphrAskOpponentNicknameOrPhone                                = phrAskOpponentNicknameOrPhone.toString();
	@ConfigurableElement("Response to the request of inviting a human opponent to play -- Asks for the word he/she wants to be guessed. Variables: {{shortCode}}, {{appName}}, {{opponentNickname}}")
	public static String HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation = phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation.toString();
	@ConfigurableElement("Phrase to notify the inviting player that the notification has been sent to the opponent. Variables: {{shortCode}}, {{appName}}, {{invitedPlayerNickname}}")
	public static String HANGMANphrInvitationNotificationForInvitingPlayer                   = phrInvitationNotificationForInvitingPlayer.toString();
	@ConfigurableElement("Phrase to notify the invited player that he/she has been invited for a hangman match. Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}}")
	public static String HANGMANphrInvitationNotificationForInvitedPlayer                    = phrInvitationNotificationForInvitedPlayer.toString();
	@ConfigurableElement("Respose sent to the invited player when he/she accepts a match. Variables: {{shortCode}}, {{appName}}, {{guessedWordSoFar}}, {{usedLetters}}")
	public static String HANGMANphrWordGuessingPlayerMatchStart                              = phrWordGuessingPlayerMatchStart.toString();
	@ConfigurableElement("Notification sent to the word providing player when the opponent accepts the match. Variables: {{shortCode}}, {{appName}}, {{guessedWordSoFar}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrWordProvidingPlayerMatchStart                             = phrWordProvidingPlayerMatchStart.toString();


	// command patterns
	///////////////////
	
	@ConfigurableElement("Global triggers (available to all navigation states) to invite a human player for a hangman match. Receives 1 parameter: the user identification string (phone number or nickname)")
	public static String[] HANGMANtrgGlobalInviteNicknameOrPhoneNumber    = trgGlobalInviteNicknameOrPhoneNumber;
	@ConfigurableElement("Local triggers (available only to the 'inviting user for a match' navigation state) to compute the word to be used on the hangman match")
	public static String[] HANGMANtrgLocalHoldMatchWord                   = trgLocalHoldMatchWord;
	@ConfigurableElement("Local triggers (available only to the 'answering invitation' navigation state) that will should be interpreted as the invited user having accepted to play a hangman match")
	public static String[] HANGMANtrgLocalAcceptMatchInvitation           = trgLocalAcceptMatchInvitation;

	
	/************
	** METHODS **
	************/
	
	/** Apply on-the-fly phrasing changes */
	public static void applyPhrasingConfiguration() {
		phrAskOpponentNicknameOrPhone                               .setPhrases(HANGMANphrAskOpponentNicknameOrPhone);
		phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation.setPhrases(HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation);
		phrInvitationNotificationForInvitingPlayer                  .setPhrases(HANGMANphrInvitationNotificationForInvitingPlayer);
		phrInvitationNotificationForInvitedPlayer                   .setPhrases(HANGMANphrInvitationNotificationForInvitedPlayer);
	}
	
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