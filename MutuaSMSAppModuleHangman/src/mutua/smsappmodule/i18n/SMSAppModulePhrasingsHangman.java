package mutua.smsappmodule.i18n;

import mutua.smsappmodule.config.SMSAppModuleConfigurationHangman;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;

/** <pre>
 * SMSAppModulePhrasingsHangman.java
 * =================================
 * (created by luiz, Sep 18, 2015)
 *
 * Enumerates and specifies the phrasing to be used by the "Hangman" 'MutuaSMSAppModule' implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Phrasing design pattern, as described in {@link Phrase}
 *
 * @see Phrase
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModulePhrasingsHangman {

	phrgallowsArt                                               ("+-+\n| {{head}}\n|{{leftArm}}{{chest}}{{rightArm}}\n|{{leftLeg}} {{rightLeg}}\n|\n====\n"),
	phrwinningArt                                               ("\\0/\n |\n/ \\\n"),
	phrlosingArt                                                ("+-+\n| x\n|/|\\\n|/ \\\n====\n"),
	phrAskOpponentNicknameOrPhone                               ("{{appName}}: Name registered: {{invitingPlayerNickname}}. Send your friend's phone to {{shortCode}} or LIST to see online players. NICK [NEW NICK] to change your name."),
	phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation("{{appName}}: Inviting {{opponentNickname}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"),
	phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation   ("{{appName}}: Your friend's phone: {{opponentPhoneNumber}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"),
	phrInvitationNotificationForInvitingPlayer                  ("{{invitedPlayerNickname}} was invited to play with you. while you wait, you can provoke {{invitedPlayerNickname}} by sending a message to {{shortCode}} (0.31+tax) or send SIGNUP to provoke for free how many times you want"),
	phrTimeoutNotificationForInvitingPlayer                     ("{{appName}}: {{invitedPlayerNickname}} is taking too long to answer. However, a new player, {{suggestedNewPlayersNickname}}, is available. Play with {{suggestedNewPlayersNickname}}? Send YES to {{shortCode}}"),
	phrInvitationNotificationForInvitedPlayer                   ("{{appName}}: {{invitingPlayerNickname}} is inviting you for a hangman match. Do you accept? Send YES to {{shortCode}} or PROFILE to see {{invitingPlayerNickname}} information"),
	phrInvitationRefusalNotificationForInvitingPlayer           ("{{invitedPlayerNickname}} refused your invitation to play. Send LIST to 9714 and pick someone else"),
	phrInvitationRefusalNotificationForInvitedPlayer            ("The invitation to play the Hangman Game made by {{invitingPlayerNickname}} was refused. Send LIST to {{shortCode}} to see online users"),
	phrNotAGoodWord                                             ("You selected '{{word}}'. This is possily not a good word. Please think of one only with A-Z letters, without accents, digits, ponctuation or any other special characters and send it to {{shortCode}}"),
	phrWordProvidingPlayerStart                                 ("Game started with {{wordGuessingPlayerNick}}.\n{{gallowsArt}}Send P {{wordGuessingPlayerNick}} MSG to give him/her clues"),
	phrWordGuessingPlayerStart                                  ("{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"),
	phrWordGuessingPlayerStatus                                 ("{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"),
	phrWordProvidingPlayerStatus                                ("{{nickname}} guessed letter {{guessedLetter}}\n{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend P {{nickname}} MSG to provoke him/her"),
	phrWinningMessageForWordGuessingPlayer                      ("{{winningArt}}{{word}}! You got it! Here is your lucky number: {{luckyNumber}}. Send: J to play or A for help"),
	phrWinningMessageForWordProvidingPlayer                     ("{{wordGuessingPlayerNick}} guessed your word! P {{wordGuessingPlayerNick}} MSG to provoke him/her or INVITE {{wordGuessingPlayerNick}} for a new match"),
	phrLosingMessageForWordGuessingPlayer                       ("{{losingArt}}The word was {{word}}. Now challenge {{wordProvidingPlayerNick}}: send INVITE {{wordProvidingPlayerNick}} to {{shortCode}}"),
	phrLosingMessageForWordProvidingPlayer                      ("Good one! {{wordGuessingPlayerNick}} wasn't able to guessed your word! P {{wordGuessingPlayerNick}} MSG to provoke him/her or INVITE {{wordGuessingPlayerNick}} for a new match"),
	phrMatchGiveupNotificationForWordProvidingPlayer            ("{{wordGuessingPlayerNick}} cancelled the match. To find other users to play with, sent LIST to {{shortCode}}"),
	phrMatchGiveupNotificationForWordGuessingPlayer             ("Your match with {{wordProvidingPlayerNick}} has been canceled. Send P {{wordProvidingPlayerNick}} MSG to talk to him/her or LIST to play with someone else"),
	
	;
	
	public final Phrase phrase;
	
	private SMSAppModulePhrasingsHangman(String... phrases) {
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
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrAskOpponentNicknameOrPhone */
	public static String getAskOpponentNicknameOrPhone(String invitingPlayerNickname) {
		return phrAskOpponentNicknameOrPhone.getPhrase("shortCode",              SMSAppModuleConfiguration.APPShortCode,
                                                       "appName",                SMSAppModuleConfiguration.APPName,
                                                       "invitingPlayerNickname", invitingPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation */
	public static String getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation(String opponentNickname) {
		return phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation.getPhrase("shortCode",        SMSAppModuleConfiguration.APPShortCode,
                                                                                      "appName",          SMSAppModuleConfiguration.APPName,
                                                                                      "opponentNickname", opponentNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrInvitationNotificationForInvitingPlayer */
	public static String getInvitationNotificationForInvitingPlayer(String invitedPlayerNickname) {
		return phrInvitationNotificationForInvitingPlayer.getPhrase("shortCode",             SMSAppModuleConfiguration.APPShortCode,
		                                                            "appName",               SMSAppModuleConfiguration.APPName,
		                                                            "invitedPlayerNickname", invitedPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrInvitationNotificationForInvitedPlayer */
	public static String getInvitationNotificationForInvitedPlayer(String invitingPlayerNickname) {
		return phrInvitationNotificationForInvitedPlayer.getPhrase("shortCode",             SMSAppModuleConfiguration.APPShortCode,
		                                                           "appName",               SMSAppModuleConfiguration.APPName,
		                                                           "invitedPlayerNickname", invitingPlayerNickname);
	}
	

}