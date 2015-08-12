package mutua.smsappmodule.config;

import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.*;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
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
	
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile.phrAskNickname")
	public static String PROFILEPphrAskNickname                      = phrAskNickname.toString();
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile.phrNicknameRegistrationNotification")
	public static String PROFILEPphrNicknameRegistrationNotification = phrNicknameRegistrationNotification.toString();
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile.phrUserProfilePresentation")
	public static String PROFILEPphrPresentation                     = phrUserProfilePresentation.toString();
	// show players?
	
	
	// command patterns
	///////////////////
	
	@ConfigurableElement("Global triggers (available to all navigation states) to execute the 'ask for a nickname' dialog")
	public static String[] PROFILEtrgGlobalStartAskForNicknameDialog    = trgGlobalStartAskForNicknameDialog;
	@ConfigurableElement("Local triggers (available only to the 'ask for a nickname' navigation state) to execute the 'register user nickname' command")
	public static String[] PROFILEtrgLocalRegisterNickname              = trgLocalRegisterNickname;
	@ConfigurableElement("Global triggers (available to all navigation states) to execute the 'register user nickname' command. Receives 1 parameter: the nickname")
	public static String[] PROFILEtrgGlobalRegisterNickname             = trgGlobalRegisterNickname;

	
	/************
	** METHODS **
	************/
	
	/** Apply on-the-fly phrasing changes */
	public static void applyPhrasingConfiguration() {
		phrAskNickname                     .setPhrases(PROFILEPphrAskNickname);
		phrNicknameRegistrationNotification.setPhrases(PROFILEPphrNicknameRegistrationNotification);
		phrUserProfilePresentation         .setPhrases(PROFILEPphrPresentation);
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
