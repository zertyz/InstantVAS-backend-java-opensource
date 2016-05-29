package mutua.smsappmodule.i18n;

import mutua.smsappmodule.i18n.plugins.IGeoLocatorPlaceHolder;
import mutua.smsappmodule.i18n.plugins.UserGeoLocator;

/** <pre>
 * SMSAppModulePhrasingsProfile.java
 * =================================
 * (created by luiz, Aug 3, 2015)
 *
 * Declares and specifies the phrasings to be used by the "Profile SMS Module" implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the "Instant VAS SMSApp Phrasing" design pattern, as described in {@link Phrase}
 *
 * @see Phrase
 * @version $Id$
 * @author luiz
 */

public class SMSAppModulePhrasingsProfile {
	
	/** @see #getAskForFirstNickname() */
	private final Phrase phrAskForFirstNickname;
	/** @see #getAskForNewNickname() */
	private final Phrase phrAskForNewNickname;
	/** @see #getAskForNicknameCancelation() */
	private final Phrase phrAskForNicknameCancelation;
	/** @see #getNicknameRegistrationNotification() */
	private final Phrase phrNicknameRegistrationNotification;
	/** @see #getUserProfilePresentation() */
	private final Phrase phrUserProfilePresentation;
	/** @see #getNicknameNotFound() */
	private final Phrase phrNicknameNotFound;
	
	private final IGeoLocatorPlaceHolder userGeoLocatorPlugin;
	
	/** Fulfill the 'Phrase' objects with the default test values */
	public SMSAppModulePhrasingsProfile(String shortCode, String appName) {
		this(shortCode, appName,
			"{{appName}}: To be properly registered, whould you mind providing a nickname? Please text it now to {{shortCode}} -- 8 letters or numbers max.",
			"{{appName}}: Your current nickname is {{currentNickname}}. To change it, text the new desired nickname to {{shortCode}} -- 8 letters or numbers max. If you're OK with it, text CANCEL",
			"{{appName}}: Nickname changing canceled. Your nickname is still {{currentNickname}}. Now edit this phrase to present the user some command options.",
			"{{appName}}: Nickname registered: {{registeredNickname}}. Thanks. Now, who is going to tell you what commands may go next? Someone must customize this message.",
			"{{appName}}: {{nickname}}: {{subscriptionState}}, from {{geoUserLocation}}, {{numberOfLuckyNumbers}} lucky numbers. Whatever more you want to show through customizations: {{whatever}} -- these new variables may be set as function calls on the phrasing facility, this way we can plug & play new features on all modules, for instance, lucky numbers, which might introduce {{numberOfLuckyNumbers}} and {{generateAndGetNewLuckyNumber}}",
			// TODO geoUserLocation should be implemented as a new module, as described on the phrase above
			"{{appName}}: There is no one like '{{nickname}}'. Maybe he/she changed nickname? Send LIST to {{shortCode}} to see who is online",
			new IGeoLocatorPlaceHolder() {
				public String getPlaceHolderName() { return "nothing"; }
				public String getPlaceHolderValue(String msisdn) { return "__RJ__"; }
			});
	}
	
	/** Fulfill the 'Phrase' objects with the given values.
	 *  @param shortCode                            The application's short code to be used on phrases with {{shortCode}}
	 * @param appName                              The application name to be used on phrases with {{appName}}
	 * @param phrAskForFirstNickname               see {@link #phrAskForFirstNickname}
	 * @param phrAskForNewNickname                 see {@link #phrAskForNewNickname}
	 * @param phrAskForNicknameCancelation         see {@link #phrAskForNicknameCancelation} 
	 * @param phrNicknameRegistrationNotification  see {@link #phrNicknameRegistrationNotification}
	 * @param phrUserProfilePresentation           see {@link #phrUserProfilePresentation}
	 * @param phrNicknameNotFound                  see {@link #phrNicknameNotFound} 
	 * @param userGeoLocatorPlugin                 one of the instances from {@link UserGeoLocator} */
	public SMSAppModulePhrasingsProfile(String shortCode, String appName,
		String phrAskForFirstNickname,
		String phrAskForNewNickname,
		String phrAskForNicknameCancelation,
		String phrNicknameRegistrationNotification,
		String phrUserProfilePresentation,
		String phrNicknameNotFound,
		IGeoLocatorPlaceHolder userGeoLocatorPlugin) {
		
		// constant parameters -- defines the common phrase parameters -- {{shortCode}} and {{appName}}
		String[] commonPhraseParameters = new String[] {
			"shortCode", shortCode,
			"appName",   appName};

		this.phrAskForFirstNickname              = new Phrase(commonPhraseParameters, phrAskForFirstNickname);
		this.phrAskForNewNickname                = new Phrase(commonPhraseParameters, phrAskForNewNickname);
		this.phrAskForNicknameCancelation        = new Phrase(commonPhraseParameters, phrAskForNicknameCancelation);
		this.phrNicknameRegistrationNotification = new Phrase(commonPhraseParameters, phrNicknameRegistrationNotification);
		this.phrUserProfilePresentation          = new Phrase(commonPhraseParameters, phrUserProfilePresentation);
		this.phrNicknameNotFound                 = new Phrase(commonPhraseParameters, phrNicknameNotFound);
		
		this.userGeoLocatorPlugin = userGeoLocatorPlugin;
	}
	
	/*********************
	** PHRASING METHODS **
	*********************/
	
	/** Phrase sent when the system wants the user to inform his/her nickname for the first time. Variables: {{shortCode}}, {{appName}} */
	public String getAskForFirstNickname() {
		return phrAskForFirstNickname.getPhrase();
	}
	
	/** Phrase sent when the system asks the user to change his/her nickname (for the case when he/she already has a valid nickname). Variables: {{shortCode}}, {{appName}}, {{currentNickname}} */
	public String getAskForNewNickname(String currentNickname) {
		return phrAskForNewNickname.getPhrase("currentNickname", currentNickname);
	}

	/** Phrase sent when the 'change nickname dialog' has been deliberately cancelled by the user. Consider the opportunity to present some possible next commands. Variables: {{shortCode}}, {{appName}}, {{currentNickname}} */
	public String getAskForNicknameCancelation(String currentNickname) {
		return phrAskForNicknameCancelation.getPhrase("currentNickname", currentNickname);
	}

	/** Phrase sent in response to the request of changing the nickname -- the text should confirm the nickname registered on the system. Variables: {{shortCode}}, {{appName}}, {{registeredNickname}} */
	public String getNicknameRegistrationNotification(String registeredNickname) {
		return phrNicknameRegistrationNotification.getPhrase("registeredNickname", registeredNickname);
	}
	
	/** Text sent to present the details of a user profile. Variables: {{shortCode}}, {{appName}}, {{nickname}} and others, from extensions, such as {{subscriptionState}}, {{geoUserLocation}} and {{numberOfLuckyNumbers}} */
	public String getUserProfilePresentation(String nickname, String msisdn) {
		return phrUserProfilePresentation.getPhrase("nickname",                                nickname,
		                                            userGeoLocatorPlugin.getPlaceHolderName(), userGeoLocatorPlugin.getPlaceHolderValue(msisdn));
	}

	/** Phrase sent to the sender user, who referenced a user by it's nickname, to inform that the command wasn't executed for the informed nickname was not found. Variables: {{shortCode}}, {{appName}}, {{targetNickname}} */
	public String getNicknameNotFound(String targetNickname) {
		return phrNicknameNotFound.getPhrase("targetNickname", targetNickname);
	}
}
