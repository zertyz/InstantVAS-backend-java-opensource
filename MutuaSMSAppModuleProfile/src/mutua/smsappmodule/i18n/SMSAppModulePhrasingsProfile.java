package mutua.smsappmodule.i18n;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationProfile;

/** <pre>
 * SMSAppModulePhrasingsProfile.java
 * =================================
 * (created by luiz, Aug 3, 2015)
 *
 * Enumerates and specifies the phrasing to be used by the "User Profile" 'MutuaSMSAppModule' implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Phrasing design pattern, as described in {@link Phrase}
 *
 * @see Phrase
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModulePhrasingsProfile {
	
	phrAskForFirstNickname             ("{{appName}}: To be properly registered, whould you mind providing a nickname? Please text it now to {{shortCode}} -- 8 letters or numbers max."),
	phrAskForNewNickname               ("{{appName}}: Your current nickname is {{currentNickname}}. To change it, text the new desired nickname to {{shortCode}} -- 8 letters or numbers max. If you're OK with it, text CANCEL"),
	phrAskForNicknameCancelation       ("{{appName}}: Nickname changing canceled. Your nickname is still {{currentNickname}}. Now edit this phrase to present the user some command options."),
	phrNicknameRegistrationNotification("{{appName}}: Nickname registered: {{registeredNickname}}. Thanks. Now, who is going to tell you what commands may go next? Someone must customize this message."),
	phrUserProfilePresentation         ("{{appName}}: {{nickname}}: {{subscriptionState}}, from {{geoUserLocation}}, {{numberOfLuckyNumbers}} lucky numbers. Whatever more you want to show through customizations: {{whatever}} -- these new variables may be set as function calls on the phrasing facility, this way we can plug & play new features on all modules, for instance, lucky numbers, which might introduce {{numberOfLuckyNumbers}} and {{generateAndGetNewLuckyNumber}}"),
	// TODO geoUserLocation should be implemented as a new module, as described on the phrase above
	
	;
	
	public final Phrase phrase;
	
	private SMSAppModulePhrasingsProfile(String... phrases) {
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
	
	/** See {@link SMSAppModuleConfigurationProfile#PROFILEphrAskForFirstNickname} */
	public static String getAskForFirstNickname() {
		return phrAskForFirstNickname.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                                "appName",   SMSAppModuleConfiguration.APPName);
	}
	
	/** See {@link SMSAppModuleConfigurationProfile#PROFILEphrAskForNewNickname} */
	public static String getAskForNewNickname(String currentNickname) {
		return phrAskForNicknameCancelation.getPhrase("shortCode",       SMSAppModuleConfiguration.APPShortCode,
                                                      "appName",         SMSAppModuleConfiguration.APPName,
                                                      "currentNickname", currentNickname);
	}

	/** See {@link SMSAppModuleConfigurationProfile#PROFILEphrAskForNicknameCancelation} */
	public static String getAskForNicknameCancelation(String currentNickname) {
		return phrAskForNicknameCancelation.getPhrase("shortCode",       SMSAppModuleConfiguration.APPShortCode,
                                                      "appName",         SMSAppModuleConfiguration.APPName,
                                                      "currentNickname", currentNickname);
	}

	/** see {@link SMSAppModuleConfigurationProfile#PROFILEphrNicknameRegistrationNotification} */
	public static String getNicknameRegistrationNotification(String registeredNickname) {
		return phrNicknameRegistrationNotification.getPhrase("shortCode",          SMSAppModuleConfiguration.APPShortCode,
                                                             "appName",            SMSAppModuleConfiguration.APPName,
                                                             "registeredNickname", registeredNickname);
	}
	
	/** see {@link SMSAppModuleConfigurationProfile#PROFILEphrPresentation} */
	public static String getUserProfilePresentation(String nickname) {
		return phrUserProfilePresentation.getPhrase("shortCode",  SMSAppModuleConfiguration.APPShortCode,
                                                    "appName",    SMSAppModuleConfiguration.APPName,
                                                    "nickname",   nickname);
	}

}
