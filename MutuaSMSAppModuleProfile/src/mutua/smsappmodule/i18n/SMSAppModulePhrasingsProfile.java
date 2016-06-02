package mutua.smsappmodule.i18n;

import mutua.smsappmodule.dto.ProfileDto;
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
	/** @see #getShortProfilePresentation */
	private final Phrase phrShortProfilePresentation;
	/** @see #getProfileList() */
	private final Phrase phrProfileList;
	/** @see #getNoMoreProfiles() */
	private final Phrase phrNoMoreProfiles;
	
	private final IGeoLocatorPlaceHolder userGeoLocatorPlugin;
	
	/** Fulfill the 'Phrase' objects with the default test values */
	public SMSAppModulePhrasingsProfile(String shortCode, String appName) {
		this(shortCode, appName,
			"{{appName}}: To be properly registered, whould you mind providing a nickname? Please text it now to {{shortCode}} -- 8 letters or numbers max.",
			"{{appName}}: Your current nickname is {{currentNickname}}. To change it, text the new desired nickname to {{shortCode}} -- 8 letters or numbers max. If you're OK with it, text CANCEL",
			"{{appName}}: Nickname changing canceled. Your nickname is still {{currentNickname}}. Now edit this phrase to present the user some command options.",
			"{{appName}}: Nickname registered: {{registeredNickname}}. Thanks. Now, who is going to tell you what commands may go next? Someone must customize this message.",
			"{{appName}}: {{nickname}}: {{subscriptionState}}, from {{geoUserLocation}}, {{numberOfLuckyNumbers}} lucky numbers. Whatever more you want to show through customizations: {{whatever}} -- these new variables may be set as function calls on the phrasing facility, this way we can plug & play new features on all modules, for instance, lucky numbers, which might introduce {{numberOfLuckyNumbers}} and {{generateAndGetNewLuckyNumber}}",
			"{{appName}}: There is no one like '{{nickname}}'. Maybe he/she changed nickname? Send LIST to {{shortCode}} to see who is online",
			"{{nickname}}-{{countryStateByMSISDN}} ",
			"{{profilesList}}. To play, send INVITE [NICK] to {{shortCode}}; MORE for more players or PROFILE [NICK]",
			"There are no more online players to show. Send P [NICK] [MSG] to provoke or INVITE [PHONE] to invite a friend of yours to play the Hangman Game",
			new IGeoLocatorPlaceHolder() {
				public String getPlaceHolderName() { return "CountryStateByMSISDNResolver"; }
				public String getPlaceHolderValue(String msisdn) { return "JR"; }
			});
	}
	
	/** Fulfill the 'Phrase' objects with the given values.
	 *  @param shortCode                            The application's short code to be used on phrases with {{shortCode}}
	 *  @param appName                              The application name to be used on phrases with {{appName}}
	 *  @param phrAskForFirstNickname               see {@link #phrAskForFirstNickname}
	 *  @param phrAskForNewNickname                 see {@link #phrAskForNewNickname}
	 *  @param phrAskForNicknameCancelation         see {@link #phrAskForNicknameCancelation} 
	 *  @param phrNicknameRegistrationNotification  see {@link #phrNicknameRegistrationNotification}
	 *  @param phrUserProfilePresentation           see {@link #phrUserProfilePresentation}
	 *  @param phrNicknameNotFound                  see {@link #phrNicknameNotFound}
	 *  @param phrShortProfilePresentation          see {@link #phrShortProfilePresentation}
	 *  @param phrProfileList                       see {@link #phrProfileList}
	 *  @param phrNoMoreProfiles                    see {@link #phrNoMoreProfiles}
	 *  @param userGeoLocatorPlugin                 one of the instances from {@link UserGeoLocator} */
	public SMSAppModulePhrasingsProfile(String shortCode, String appName,
		String phrAskForFirstNickname,
		String phrAskForNewNickname,
		String phrAskForNicknameCancelation,
		String phrNicknameRegistrationNotification,
		String phrUserProfilePresentation,
		String phrNicknameNotFound,
		String phrShortProfilePresentation,
		String phrProfileList,
		String phrNoMoreProfiles,
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
		this.phrShortProfilePresentation         = new Phrase(commonPhraseParameters, phrShortProfilePresentation);
		this.phrProfileList                      = new Phrase(commonPhraseParameters, phrProfileList);
		this.phrNoMoreProfiles                   = new Phrase(commonPhraseParameters, phrNoMoreProfiles);
		
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
	
	/** Text sent to present the details of a user profile. Variables: {{shortCode}}, {{appName}}, {{nickname}} and others, from extensions, such as {{subscriptionState}}, {{countryStateByMSISDN}} and {{numberOfLuckyNumbers}} */
	public String getUserProfilePresentation(String nickname, String msisdn) {
		return phrUserProfilePresentation.getPhrase("nickname",                                nickname,
		                                            userGeoLocatorPlugin.getPlaceHolderName(), userGeoLocatorPlugin.getPlaceHolderValue(msisdn));
	}

	/** Phrase sent to the sender user, who referenced a user by it's nickname, to inform that the command wasn't executed for the informed nickname was not found. Variables: {{shortCode}}, {{appName}}, {{targetNickname}} */
	public String getNicknameNotFound(String targetNickname) {
		return phrNicknameNotFound.getPhrase("targetNickname", targetNickname);
	}
	
	/** Phrase excerpt used when composing each of the profiles in the profiles list of {@link #getProfileList()}. Variables: {{shortCode}}, {{appName}}, {{nickname}} and others, from extensions, such as {{subscriptionState}}, {{countryStateByMSISDN}} and {{numberOfLuckyNumbers}} */
	private String getShortProfilePresentation(String nickname, String msisdn) {
		return phrShortProfilePresentation.getPhrase("nickname",                                nickname,
		                                             userGeoLocatorPlugin.getPlaceHolderName(), userGeoLocatorPlugin.getPlaceHolderValue(msisdn));
		
	}
	
	/** Builds a profiles list from 'profiles' using the phrase {@link #getShortProfilePresentation()} for each element, which will be placed in substitution for {{profilesList}}. Variables: {{shortCode}}, {{appName}} and, of course, {{profilesList}} */
	public String getProfileList(ProfileDto[] profiles) {
		StringBuffer profilesList = new StringBuffer(profiles.length*phrShortProfilePresentation.getPhrase().length());
		for (int i=0; i<profiles.length; i++) {
			profilesList.append(getShortProfilePresentation(profiles[i].getNickname(), profiles[i].getUser().getPhoneNumber()));
		}
		return phrProfileList.getPhrase("profilesList", profilesList.toString());
	}

	/** Phrase to show when, in the attempt to list available profiles, there are none left to show. Variables: {{shortCode}}, {{appName}} */
	public String getNoMoreProfiles() {
		return phrNoMoreProfiles.getPhrase();
	}
}
