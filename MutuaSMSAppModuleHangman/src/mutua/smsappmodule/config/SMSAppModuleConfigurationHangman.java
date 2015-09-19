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
	
	@ConfigurableElement("Response to the request of inviting a human opponent to play -- Asks for his/her phone or nickname. Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}}")
	public static String HANGMANphrAskOpponentNicknameOrPhone                                = phrAskOpponentNicknameOrPhone.toString();
	@ConfigurableElement("Response to the request of inviting a human opponent to play -- Asks for the word he/she wants to be guessed. Variables: {{shortCode}}, {{appName}}, {{opponentNickname}}")
	public static String HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation = phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation.toString();
	@ConfigurableElement("Phrase to notify the inviting player that the notification has been sent to the opponent. Variables: {{shortCode}}, {{appName}}, {{invitedPlayerNickname}}")
	public static String HANGMANphrInvitationNotificationForInvitingPlayer                   = phrInvitationNotificationForInvitingPlayer.toString();
	@ConfigurableElement("Phrase to notify the invited player that he/she has been invited for a hangman match. Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}}")
	public static String HANGMANphrInvitationNotificationForInvitedPlayer                    = phrInvitationNotificationForInvitedPlayer.toString();


	// command patterns
	///////////////////
	
	@ConfigurableElement("Global triggers (available to all navigation states) to invite a human player for a hangman match. Receives 1 parameter: the user identification string (phone number or nickname)")
	public static String[] HANGMANtrgGlobalInviteNicknameOrPhoneNumber    = trgGlobalInviteNicknameOrPhoneNumber;
//	@ConfigurableElement("Local triggers (available only to the 'privately chatting with an user' navigation state) send a private message to that user. Receives 1 parameter: the message")
//	public static String[] CHATtrgLocalSendPrivateReply       = trgLocalSendPrivateReply;

	
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