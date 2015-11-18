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

	phr_headCharacter                                           ("O"),
	phr_leftArmCharacter                                        ("/"),
	phr_chestCharacter                                          ("|"),
	phr_rightArmCharacter                                       ("\\"),
	phr_leftLegCharacter                                        ("/"),
	phr_rightLegCharacter                                       ("\\"),
	phr_gallowsArt                                              ("+-+\n| {{head}}\n|{{leftArm}}{{chest}}{{rightArm}}\n|{{leftLeg}} {{rightLeg}}\n|\n====\n"),
	phr_winningArt                                              ("\\0/\n |\n/ \\\n"),
	phr_losingArt                                               ("+-+\n| x\n|/|\\\n|/ \\\n====\n"),
	phrAskOpponentNicknameOrPhone                               ("{{appName}}: Name registered: {{invitingPlayerNickname}}. Send your friend's phone to {{shortCode}} or LIST to see online players. NICK [NEW NICK] to change your name."),
	phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation("{{appName}}: Inviting {{opponentNickname}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"),
	phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation   ("{{appName}}: Your friend's phone: {{opponentPhoneNumber}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"),
	phrInvitationResponseForInvitingPlayer                      ("{{invitedPlayerNickname}} was invited to play with you. while you wait, you can provoke {{invitedPlayerNickname}} by sending a message to {{shortCode}} (0.31+tax) or send SIGNUP to provoke for free how many times you want"),
	phrInvitationNotificationForInvitedPlayer                   ("{{appName}}: {{invitingPlayerNickname}} is inviting you for a hangman match. Do you accept? Send YES or NO to {{shortCode}} or P {{invitingPlayerNickname}} [MSG] to send him/her a message"),
	phrTimeoutNotificationForInvitingPlayer                     ("{{appName}}: {{invitedPlayerNickname}} is taking too long to answer. However, a new player, {{suggestedNewPlayersNickname}}, is available. Play with {{suggestedNewPlayersNickname}}? Send YES to {{shortCode}}"),
	phrInvitationRefusalResponseForInvitedPlayer                ("The invitation to play the Hangman Game made by {{invitingPlayerNickname}} was refused. Send LIST to {{shortCode}} to see online users"),
	phrInvitationRefusalNotificationForInvitingPlayer           ("{{invitedPlayerNickname}} refused your invitation to play. Send LIST to 9714 and pick someone else"),
	phrNotAGoodWord                                             ("You selected '{{word}}'. This is possily not a good word. Please think of one only with A-Z letters, without accents, digits, ponctuation or any other special characters and send it to {{shortCode}}"),
	phrWordProvidingPlayerMatchStart                            ("Game started with {{wordGuessingPlayerNickname}}.\n{{gallowsArt}}Send P {{wordGuessingPlayerNickname}} [MSG] to give him/her clues"),
	phrWordGuessingPlayerMatchStart                             ("{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"),
	phrWordProvidingPlayerStatus                                ("{{wordGuessingPlayerNickname}} guessed letter {{guessedLetter}}\n{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend P {{wordGuessingPlayerNickname}} [MSG] to provoke him/her"),
	phrWordGuessingPlayerStatus                                 ("{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"),
	phrWinningMessageForWordGuessingPlayer                      ("{{winningArt}}{{word}}! You got it! Here is your lucky number: xxx.xx.xx.xxx. Send: J to play or A for help"),
	phrWinningMessageForWordProvidingPlayer                     ("{{wordGuessingPlayerNickname}} guessed your word! P {{wordGuessingPlayerNickname}} [MSG] to provoke him/her or INVITE {{wordGuessingPlayerNickname}} for a new match"),
	phrLosingMessageForWordGuessingPlayer                       ("{{losingArt}}The word was {{word}}. Now challenge {{wordProvidingPlayerNickname}}: send INVITE {{wordProvidingPlayerNickname}} to {{shortCode}}"),
	phrLosingMessageForWordProvidingPlayer                      ("Good one! {{wordGuessingPlayerNickname}} wasn't able to guessed your word! P {{wordGuessingPlayerNickname}} [MSG] to provoke him/her or INVITE {{wordGuessingPlayerNickname}} for a new match"),
	phrMatchGiveupNotificationForWordGuessingPlayer             ("Your match with {{wordProvidingPlayerNickname}} has been canceled. Send P {{wordProvidingPlayerNickname}} [MSG] to talk to him/her or LIST to play with someone else"),
	phrMatchGiveupNotificationForWordProvidingPlayer            ("{{wordGuessingPlayerNickname}} cancelled the match. To find other users to play with, sent LIST to {{shortCode}}"),
	phrGuessingWordHelp                                         ("You are guessing a word on a {{appName}} match. Please text a letter or: END to quit the match; P [nick] [MSG] to ask for clues; LIST to see other online users"),
	
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
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphr_gallowsArt */
	private static String getGallowsArt(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                                   boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg) {
		return phr_gallowsArt.getPhrase("head",     (drawHead     ? phr_headCharacter    .getPhrase():""),
		                                "leftArm",  (drawLeftArm  ? phr_leftArmCharacter .getPhrase():" "),
		                                "chest",    (drawChest    ? phr_chestCharacter   .getPhrase():" "),
		                                "rightArm", (drawRightArm ? phr_rightArmCharacter.getPhrase():""),
		                                "leftLeg",  (drawLeftLeg  ? phr_leftLegCharacter .getPhrase():" "),
		                                "rightLeg", (drawRightLeg ? phr_rightLegCharacter.getPhrase():""));
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphr_winningArt */
	private static String getWinningArt() {
		return phr_winningArt.getPhrase();
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphr_losingArt */
	private static String getLosingArt() {
		return phr_losingArt.getPhrase();
	}

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
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation */
	public static String getAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(String opponentPhoneNumber) {
		return phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation.getPhrase("shortCode",           SMSAppModuleConfiguration.APPShortCode,
		                                                                           "appName",             SMSAppModuleConfiguration.APPName,
		                                                                           "opponentPhoneNumber", opponentPhoneNumber);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrInvitationResponseForInvitingPlayer */
	public static String getInvitationResponseForInvitingPlayer(String invitedPlayerNickname) {
		return phrInvitationResponseForInvitingPlayer.getPhrase("shortCode",             SMSAppModuleConfiguration.APPShortCode,
		                                                            "appName",               SMSAppModuleConfiguration.APPName,
		                                                            "invitedPlayerNickname", invitedPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrInvitationNotificationForInvitedPlayer */
	public static String getInvitationNotificationForInvitedPlayer(String invitingPlayerNickname) {
		return phrInvitationNotificationForInvitedPlayer.getPhrase("shortCode",              SMSAppModuleConfiguration.APPShortCode,
		                                                           "appName",                SMSAppModuleConfiguration.APPName,
		                                                           "invitingPlayerNickname", invitingPlayerNickname);
	}
	
	public static String getTimeoutNotificationForInvitingPlayer(String invitedPlayerNickname, String suggestedNewPlayersNickname) {
		return phrTimeoutNotificationForInvitingPlayer.getPhrase("shortCode",                   SMSAppModuleConfiguration.APPShortCode,
		                                                         "appName",                     SMSAppModuleConfiguration.APPName,
		                                                         "invitedPlayerNickname",       invitedPlayerNickname,
		                                                         "suggestedNewPlayersNickname", suggestedNewPlayersNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrInvitationRefusalNotificationForInvitingPlayer */
	public static String getInvitationRefusalNotificationForInvitingPlayer(String invitedPlayerNickname) {
		return phrInvitationRefusalNotificationForInvitingPlayer.getPhrase("shortCode",             SMSAppModuleConfiguration.APPShortCode,
                                                                           "appName",               SMSAppModuleConfiguration.APPName,
                                                                           "invitedPlayerNickname", invitedPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrInvitationRefusalResponseForInvitedPlayer */
	public static String getInvitationRefusalResponseForInvitedPlayer(String invitingPlayerNickname) {
		return phrInvitationRefusalResponseForInvitedPlayer.getPhrase("shortCode",              SMSAppModuleConfiguration.APPShortCode,
		                                                              "appName",                SMSAppModuleConfiguration.APPName,
		                                                              "invitingPlayerNickname", invitingPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrNotAGoodWord */
	public static String getNotAGoodWord(String word) {
		return phrNotAGoodWord.getPhrase("shortCode", SMSAppModuleConfiguration.APPShortCode,
		                                 "appName",   SMSAppModuleConfiguration.APPName,
		                                 "word",      word);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrWordGuessingPlayerMatchStart */
	public static String getWordGuessingPlayerMatchStart(String guessedWordSoFar, String usedLetters) {
		return phrWordGuessingPlayerMatchStart.getPhrase("shortCode",              SMSAppModuleConfiguration.APPShortCode,
		                                                 "appName",                SMSAppModuleConfiguration.APPName,
		                                                 "gallowsArt",             getGallowsArt(false, false, false, false, false, false),
		                                                 "guessedWordSoFar",       guessedWordSoFar,
		                                                 "usedLetters",            usedLetters);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrWordProvidingPlayerMatchStart */
	public static String getWordProvidingPlayerMatchStart(String guessedWordSoFar, String wordGuessingPlayerNickname) {
		return phrWordProvidingPlayerMatchStart.getPhrase("shortCode",                  SMSAppModuleConfiguration.APPShortCode,
		                                                  "appName",                    SMSAppModuleConfiguration.APPName,
		                                                  "gallowsArt",                 getGallowsArt(false, false, false, false, false, false),
		                                                  "guessedWordSoFar",           guessedWordSoFar,
		                                                  "wordGuessingPlayerNickname", wordGuessingPlayerNickname);
	}

	/** @see SMSAppModuleConfigurationHangman#HANGMANphrWordGuessingPlayerStatus */
	public static String getWordGuessingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
                                                     boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
                                                     String guessedWordSoFar, String usedLetters) {
		return phrWordGuessingPlayerStatus.getPhrase("shortCode",        SMSAppModuleConfiguration.APPShortCode,
                                                     "appName",          SMSAppModuleConfiguration.APPName,
                                                     "gallowsArt",       getGallowsArt(drawHead, drawLeftArm, drawRightArm, drawChest, drawLeftLeg, drawRightLeg),
                                                     "guessedWordSoFar", guessedWordSoFar,
                                                     "usedLetters",      usedLetters);
		
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrWordProvidingPlayerStatus */
	public static String getWordProvidingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
                                                      boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
                                                      String guessedWordSoFar, String guessedLetter, String usedLetters, String wordGuessingPlayerNickname) {
		return phrWordProvidingPlayerStatus.getPhrase("shortCode",                  SMSAppModuleConfiguration.APPShortCode,
                                                      "appName",                    SMSAppModuleConfiguration.APPName,
                                                      "gallowsArt",                 getGallowsArt(drawHead, drawLeftArm, drawRightArm, drawChest, drawLeftLeg, drawRightLeg),
                                                      "guessedWordSoFar",           guessedWordSoFar,
                                                      "guessedLetter",              guessedLetter,
                                                      "usedLetters",                usedLetters,
                                                      "wordGuessingPlayerNickname", wordGuessingPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrWinningMessageForWordGuessingPlayer */
	public static String getWinningMessageForWordGuessingPlayer(String word, String wordProvidingPlayerNickname) {
		return phrWinningMessageForWordGuessingPlayer.getPhrase("shortCode",                   SMSAppModuleConfiguration.APPShortCode,
		                                                        "appName",                     SMSAppModuleConfiguration.APPName,
		                                                        "winningArt",                  getWinningArt(),
		                                                        "word",                        word,
		                                                        "wordProvidingPlayerNickname", wordProvidingPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrWinningMessageForWordProvidingPlayer */
	public static String getWinningMessageForWordProvidingPlayer(String wordGuessingPlayerNickname) {
		return phrWinningMessageForWordProvidingPlayer.getPhrase("shortCode",                  SMSAppModuleConfiguration.APPShortCode,
		                                                         "appName",                    SMSAppModuleConfiguration.APPName,
		                                                         "winningArt",                 getWinningArt(),
		                                                         "wordGuessingPlayerNickname", wordGuessingPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrLosingMessageForWordGuessingPlayer */
	public static String getLosingMessageForWordGuessingPlayer(String word, String wordProvidingPlayerNickname) {
		return phrLosingMessageForWordGuessingPlayer.getPhrase("shortCode",                   SMSAppModuleConfiguration.APPShortCode,
		                                                       "appName",                     SMSAppModuleConfiguration.APPName,
		                                                       "losingArt",                   getLosingArt(),
		                                                       "word",                        word,
		                                                       "wordProvidingPlayerNickname", wordProvidingPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrLosingMessageForWordProvidingPlayer */
	public static String getLosingMessageForWordProvidingPlayer(String wordGuessingPlayerNickname) {
		return phrLosingMessageForWordProvidingPlayer.getPhrase("shortCode",                  SMSAppModuleConfiguration.APPShortCode,
		                                                        "appName",                    SMSAppModuleConfiguration.APPName,
		                                                        "losingArt",                  getLosingArt(), 
		                                                        "wordGuessingPlayerNickname", wordGuessingPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrMatchGiveupNotificationForWordGuessingPlayer */
	public static String getMatchGiveupNotificationForWordGuessingPlayer(String wordProvidingPlayerNickname) {
		return phrMatchGiveupNotificationForWordGuessingPlayer.getPhrase("shortCode",                   SMSAppModuleConfiguration.APPShortCode,
		                                                                 "appName",                     SMSAppModuleConfiguration.APPName,
		                                                                 "wordProvidingPlayerNickname", wordProvidingPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrMatchGiveupNotificationForWordProvidingPlayer */
	public static String getMatchGiveupNotificationForWordProvidingPlayer(String wordGuessingPlayerNickname) {
		return phrMatchGiveupNotificationForWordProvidingPlayer.getPhrase("shortCode",                  SMSAppModuleConfiguration.APPShortCode,
		                                                                  "appName",                    SMSAppModuleConfiguration.APPName,
		                                                                  "wordGuessingPlayerNickname", wordGuessingPlayerNickname);
	}
	
	/** @see SMSAppModuleConfigurationHangman#HANGMANphrGuessingWordHelp */
	public static String getGuessingWordHelp() {
		return phrGuessingWordHelp.getPhrase("shortCode",                   SMSAppModuleConfiguration.APPShortCode,
                                             "appName",                     SMSAppModuleConfiguration.APPName);
	}

}