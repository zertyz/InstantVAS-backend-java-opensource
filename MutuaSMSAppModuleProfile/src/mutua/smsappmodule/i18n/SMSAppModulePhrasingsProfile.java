package mutua.smsappmodule.i18n;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;

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
	
	phrAskNickname                     ("{{appName}}: To be properly registered, whould you mind providing a nickname? Please text it now to {{shortCode}} -- 8 letters or numbers max."),
	phrNicknameRegistrationNotification("{{appName}}: Nickname registered: {{registeredNickname}}. Thanks. Now, who is going to tell you what commands may go next? Someone must customize this message."),
	phrUserProfilePresentation         ("{{appName}}: {{nickname}}: {{subscriptionState}}, from {{state}}, {{numberOfLuckyNumbers}} lucky numbers. Whatever more you want to show through customizations: {{whatever}}"),
	
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
	
	@ConfigurableElement("Phrase sent when the system wants the user to inform his/her nickname. Variables: {{shortCode}}, {{appName}}")
	public static String getAskNickname() {
		return phrAskNickname.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                        "appName",   SMSAppModuleConfiguration.APPName);
	}
	
	@ConfigurableElement("Phrase sent in response to the request of change the nickname -- the text should confirm the nickname registered on the system. Variables: {{shortCode}}, {{appName}}, {{registeredNickname}}")
	public static String getNicknameRegistrationNotification(String registeredNickname) {
		return phrNicknameRegistrationNotification.getPhrase("shortCode",          SMSAppModuleConfiguration.APPShortCode,
                                                             "appName",            SMSAppModuleConfiguration.APPName,
                                                             "registeredNickname", registeredNickname);
	}

	@ConfigurableElement("Text sent to present the details of a user profile. Variables: {{shortCode}}, {{appName}}")
	public static String getUserProfilePresentation() {
		return phrUserProfilePresentation.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
                                                    "appName",   SMSAppModuleConfiguration.APPName);
	}

}
