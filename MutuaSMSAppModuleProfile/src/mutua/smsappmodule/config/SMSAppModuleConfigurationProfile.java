package mutua.smsappmodule.config;

import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.*;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile;

/** <pre>
 * SMSAppModuleConfigurationProfile.java
 * =====================================
 * (created by luiz, Aug 3, 2015)
 *
 * Defines the "User Profile" module configuration variables, implementing the Mutua SMSApp
 * Configuration design pattern, as described in 'SMSAppModuleConfiguration'
 *
 * @see SMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationProfile {
	
	
	/*************************************************
	** MutuaICConfiguration CONFIGURABLE PROPERTIES **
	*************************************************/
	
	// phrasing
	///////////
	
	@ConfigurableElement("Phrase sent when the system wants the user to inform his/her nickname for the first time. Variables: {{shortCode}}, {{appName}}")
	public static String PROFILEphrAskForFirstNickname              = phrAskForFirstNickname.toString();
	@ConfigurableElement("Phrase sent when the system wants the user to change his/her nickname (for the case when he/she already has a valid nickname). Variables: {{shortCode}}, {{appName}}, {{currentNickname}}")
	public static String PROFILEphrAskForNewNickname                = phrAskForNewNickname.toString();
	@ConfigurableElement("Phrase sent when the change nickname dialog has been deliberately cancelled by the user. Consider the opportunity to present some possible next commands. Variables: {{shortCode}}, {{appName}}, {{currentNickname}}")
	public static String PROFILEphrAskForNicknameCancelation        = phrAskForNicknameCancelation.toString();
	@ConfigurableElement("Phrase sent in response to the request of changing the nickname -- the text should confirm the nickname registered on the system. Variables: {{shortCode}}, {{appName}}, {{registeredNickname}}")
	public static String PROFILEphrNicknameRegistrationNotification = phrNicknameRegistrationNotification.toString();
	@ConfigurableElement("Text sent to present the details of a user profile. Variables: {{shortCode}}, {{appName}}, {{nickname}} and others, from extensions, such as {{subscriptionState}}, {{geoUserLocation}} and {{numberOfLuckyNumbers}}")
	public static String PROFILEphrPresentation                     = phrUserProfilePresentation.toString();
	@ConfigurableElement("Phrase sent to the sender user, who referenced a user by it's nickname, to inform that the command wasn't executed for the informed nickname was not found. Variables: {{shortCode}}, {{appName}}, {{targetNickname}}")
	public static String PROFILEphrNicknameNotFound                 = phrNicknameNotFound.toString();
	// show players? YESS!! When done, delete this comment
	
	
	// command patterns
	///////////////////
	
	@ConfigurableElement("Global triggers (available to all navigation states) to execute the 'ask for a nickname' dialog")
	public static String[] PROFILEtrgGlobalStartAskForNicknameDialog    = trgGlobalStartAskForNicknameDialog;
	@ConfigurableElement("Local triggers (available only to the 'ask for a nickname' navigation state) to cancel the 'ask for a nickname' dialog")
	public static String[] PROFILEtrgLocalNicknameDialogCancelation     = trgLocalNicknameDialogCancelation;
	@ConfigurableElement("Local triggers (available only to the 'ask for a nickname' navigation state) to execute the 'register user nickname' command")
	public static String[] PROFILEtrgLocalRegisterNickname              = trgLocalRegisterNickname;
	@ConfigurableElement("Global triggers (available to all navigation states) to execute the 'register user nickname' command. Receives 1 parameter: the nickname")
	public static String[] PROFILEtrgGlobalRegisterNickname             = trgGlobalRegisterNickname;
	@ConfigurableElement("Global triggers (available to all navigation states) to execute the 'show user profile' command. Receives 1 optional parameter: the nickname. If called without a parameter, the user gets his/her own information instead")
	public static String[] PROFILEtrgGlobalShowUserProfile              = trgGlobalShowUserProfile;

	
	/************
	** METHODS **
	************/
	
	/** Apply on-the-fly phrasing changes */
	public static void applyPhrasingConfiguration() {
		phrAskForFirstNickname             .setPhrases(PROFILEphrAskForFirstNickname);
		phrAskForNewNickname               .setPhrases(PROFILEphrAskForNewNickname);
		phrAskForNicknameCancelation       .setPhrases(PROFILEphrAskForNicknameCancelation);
		phrNicknameRegistrationNotification.setPhrases(PROFILEphrNicknameRegistrationNotification);
		phrUserProfilePresentation         .setPhrases(PROFILEphrPresentation);
		phrNicknameNotFound                .setPhrases(PROFILEphrNicknameNotFound);
	}
	
	/** Apply on-the-fly command trigger changes */
	public static void applyTriggerConfiguration() {
		for (INavigationState navigationState : SMSAppModuleNavigationStatesProfile.values()) {
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
