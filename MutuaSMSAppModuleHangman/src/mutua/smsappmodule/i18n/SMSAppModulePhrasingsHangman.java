package mutua.smsappmodule.i18n;

/** <pre>
 * SMSAppModulePhrasingsHangman.java
 * =================================
 * (created by luiz, Sep 18, 2015)
 *
 * Declares and specifies the phrasings to be used by the "Hangman SMS Module" implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the "Instant VAS SMSApp Phrasing" design pattern, as described in {@link Phrase}
 *
 * @see Phrase
 * @version $Id$
 * @author luiz
 */

public class SMSAppModulePhrasingsHangman {
	
	/** The character to be used to draw the head on the gallows ASCII art */
	private final String headCharacter;
	/** The character to be used to draw the left arm on the gallows ASCII art */
	private final String leftArmCharacter;
	/** The character to be used to draw the chest on the gallows ASCII art */
	private final String chestCharacter;
	/** The character to be used to draw the right arm on the gallows ASCII art */
	private final String rightArmCharacter;
	/** The character to be used to draw the left leg on the gallows ASCII art */
	private final String leftLegCharacter;
	/** The character to be used to draw the right leg on the gallows ASCII art */
	private final String rightLegCharacter;
	/** Text ASCII art to present the gallows. Used in the next phrases to present the status of the hangman match */
	private final Phrase phr_gallowsArt;
	/** @see #getAskOpponentNicknameOrPhone */
	private final Phrase phrAskOpponentNicknameOrPhone;
	/** @see #getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation */
	private final Phrase phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation;
	/** @see #getAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation */
	private final Phrase phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation;
	/** @see #getInvitationResponseForInvitingPlayer */
	private final Phrase phrInvitationResponseForInvitingPlayer;
	/** @see #getInvitationNotificationForInvitedPlayer */
	private final Phrase phrInvitationNotificationForInvitedPlayer;
	/** @see #getTimeoutNotificationForInvitingPlayer */
	private final Phrase phrTimeoutNotificationForInvitingPlayer;
	/** @see #getInvitationRefusalResponseForInvitedPlayer */
	private final Phrase phrInvitationRefusalResponseForInvitedPlayer;
	/** @see #getInvitationRefusalNotificationForInvitingPlayer */
	private final Phrase phrInvitationRefusalNotificationForInvitingPlayer;
	/** @see #getNotAGoodWord */
	private final Phrase phrNotAGoodWord;
	/** @see #getWordProvidingPlayerMatchStart */
	private final Phrase phrWordProvidingPlayerMatchStart;
	/** @see #getWordGuessingPlayerMatchStart */
	private final Phrase phrWordGuessingPlayerMatchStart;
	/** @see #getWordProvidingPlayerStatus */
	private final Phrase phrWordProvidingPlayerStatus;
	/** @see #getWordGuessingPlayerStatus */
	private final Phrase phrWordGuessingPlayerStatus;
	/** @see #getWinningMessageForWordGuessingPlayer */
	private final Phrase phrWinningMessageForWordGuessingPlayer;
	/** @see #getWinningMessageForWordProvidingPlayer */
	private final Phrase phrWinningMessageForWordProvidingPlayer;
	/** @see #getLosingMessageForWordGuessingPlayer */
	private final Phrase phrLosingMessageForWordGuessingPlayer;
	/** @see #getLosingMessageForWordProvidingPlayer */
	private final Phrase phrLosingMessageForWordProvidingPlayer;
	/** @see #getMatchGiveupNotificationForWordGuessingPlayer */
	private final Phrase phrMatchGiveupNotificationForWordGuessingPlayer;
	/** @see #getMatchGiveupNotificationForWordProvidingPlayer */
	private final Phrase phrMatchGiveupNotificationForWordProvidingPlayer;
	/** @see #getGuessingWordHelp */
	private final Phrase phrGuessingWordHelp;
	
	/** Fulfill the 'Phrase' objects with the default test values */
	public SMSAppModulePhrasingsHangman(String shortCode, String appName) {
		this(shortCode, appName,
			"\\0/\n |\n/ \\\n",
			"+-+\n| x\n|/|\\\n|/ \\\n====\n",
			"O",
			"/",
			"|",
			"\\",
			"/",
			"\\",
			"+-+\n| {{head}}\n|{{leftArm}}{{chest}}{{rightArm}}\n|{{leftLeg}} {{rightLeg}}\n|\n====\n",
			"{{appName}}: Name registered: {{invitingPlayerNickname}}. Send your friend's phone to {{shortCode}} or LIST to see online players. NICK [NEW NICK] to change your name.",
			"{{appName}}: Inviting {{opponentNickname}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number",
			"{{appName}}: Your friend's phone: {{opponentPhoneNumber}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number",
			"{{invitedPlayerNickname}} was invited to play with you. while you wait, you can provoke {{invitedPlayerNickname}} by sending a message to {{shortCode}} (0.31+tax) or send SIGNUP to provoke for free how many times you want",
			"{{appName}}: {{invitingPlayerNickname}} is inviting you for a hangman match. Do you accept? Send YES or NO to {{shortCode}} or P {{invitingPlayerNickname}} [MSG] to send him/her a message",
			"{{appName}}: {{invitedPlayerNickname}} is taking too long to answer. However, a new player, {{suggestedNewPlayersNickname}}, is available. Play with {{suggestedNewPlayersNickname}}? Send YES to {{shortCode}}",
			"The invitation to play the Hangman Game made by {{invitingPlayerNickname}} was refused. Send LIST to {{shortCode}} to see online users",
			"{{invitedPlayerNickname}} refused your invitation to play. Send LIST to {{shortCode}} and pick someone else",
			"You selected '{{word}}'. This is possily not a good word. Please think of one only with A-Z letters, without accents, digits, ponctuation or any other special characters and send it to {{shortCode}}",
			"Game started with {{wordGuessingPlayerNickname}}.\n{{gallowsArt}}Send P {{wordGuessingPlayerNickname}} [MSG] to give him/her clues",
			"{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game",
			"{{wordGuessingPlayerNickname}} guessed letter {{guessedLetter}}\n{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend P {{wordGuessingPlayerNickname}} [MSG] to provoke him/her",
			"{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game",
			"{{winningArt}}{{word}}! You got it! Here is your lucky number: xxx.xx.xx.xxx. Send: J to play or A for help",
			"{{wordGuessingPlayerNickname}} guessed your word! P {{wordGuessingPlayerNickname}} [MSG] to provoke him/her or INVITE {{wordGuessingPlayerNickname}} for a new match",
			"{{losingArt}}The word was {{word}}. Now challenge {{wordProvidingPlayerNickname}}: send INVITE {{wordProvidingPlayerNickname}} to {{shortCode}}",
			"Good one! {{wordGuessingPlayerNickname}} wasn't able to guessed your word! P {{wordGuessingPlayerNickname}} [MSG] to provoke him/her or INVITE {{wordGuessingPlayerNickname}} for a new match",
			"Your match with {{wordProvidingPlayerNickname}} has been canceled. Send P {{wordProvidingPlayerNickname}} [MSG] to talk to him/her or LIST to play with someone else",
			"{{wordGuessingPlayerNickname}} cancelled the match. To find other users to play with, sent LIST to {{shortCode}}",
			"You are guessing a word on a {{appName}} match. Please text a letter or: END to quit the match; P [nick] [MSG] to ask for clues; LIST to see other online users");
	}
		
	/** Fulfill the 'Phrase' objects with the given values.
	 * @param shortCode                                                    The application's short code to be used on phrases with {{shortCode}}
	 * @param appName                                                      The application name to be used on phrases with {{appName}}
	 * @param winningArt                                                   Text ASCII art used in the next phrases to present the gallows for a winning match
	 * @param losingArt                                                    Text ASCII art used in the next phrases to present the gallows for a losing match
	 * @param headCharacter                                                see {@link #headCharacter}
	 * @param leftArmCharacter                                             see {@link #leftArmCharacter}
	 * @param chestCharacter                                               see {@link #chestCharacter}
	 * @param rightArmCharacter                                            see {@link #rightArmCharacter}
	 * @param leftLegCharacter                                             see {@link #leftLegCharacter}
	 * @param rightLegCharacter                                            see {@link #rightLegCharacter}
	 * @param phr_gallowsArt                                               see {@link #phr_gallowsArt}
	 * @param phrAskOpponentNicknameOrPhone                                see {@link #phrAskOpponentNicknameOrPhone}
	 * @param phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation see {@link #phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation}
	 * @param phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation    see {@link #phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation}
	 * @param phrInvitationResponseForInvitingPlayer                       see {@link #phrInvitationResponseForInvitingPlayer}
	 * @param phrInvitationNotificationForInvitedPlayer                    see {@link #phrInvitationNotificationForInvitedPlayer}
	 * @param phrTimeoutNotificationForInvitingPlayer                      see {@link #phrTimeoutNotificationForInvitingPlayer}
	 * @param phrInvitationRefusalResponseForInvitedPlayer                 see {@link #phrInvitationRefusalResponseForInvitedPlayer}
	 * @param phrInvitationRefusalNotificationForInvitingPlayer            see {@link #phrInvitationRefusalNotificationForInvitingPlayer}
	 * @param phrNotAGoodWord                                              see {@link #phrNotAGoodWord}
	 * @param phrWordProvidingPlayerMatchStart                             see {@link #phrWordProvidingPlayerMatchStart}
	 * @param phrWordGuessingPlayerMatchStart                              see {@link #phrWordGuessingPlayerMatchStart}
	 * @param phrWordProvidingPlayerStatus                                 see {@link #phrWordProvidingPlayerStatus}
	 * @param phrWordGuessingPlayerStatus                                  see {@link #phrWordGuessingPlayerStatus}
	 * @param phrWinningMessageForWordGuessingPlayer                       see {@link #phrWinningMessageForWordGuessingPlayer}
	 * @param phrWinningMessageForWordProvidingPlayer                      see {@link #phrWinningMessageForWordProvidingPlayer}
	 * @param phrLosingMessageForWordGuessingPlayer                        see {@link #phrLosingMessageForWordGuessingPlayer}
	 * @param phrLosingMessageForWordProvidingPlayer                       see {@link #phrLosingMessageForWordProvidingPlayer}
	 * @param phrMatchGiveupNotificationForWordGuessingPlayer              see {@link #phrMatchGiveupNotificationForWordGuessingPlayer}
	 * @param phrMatchGiveupNotificationForWordProvidingPlayer             see {@link #phrMatchGiveupNotificationForWordProvidingPlayer}
	 * @param phrGuessingWordHelp                                          see {@link #phrGuessingWordHelp} */
	public SMSAppModulePhrasingsHangman(String shortCode, String appName,
		String winningArt,
		String losingArt,
		String headCharacter,
		String leftArmCharacter,
		String chestCharacter,
		String rightArmCharacter,
		String leftLegCharacter,
		String rightLegCharacter,
		String phr_gallowsArt,
		String phrAskOpponentNicknameOrPhone,
		String phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation,
		String phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation,
		String phrInvitationResponseForInvitingPlayer,
		String phrInvitationNotificationForInvitedPlayer,
		String phrTimeoutNotificationForInvitingPlayer,
		String phrInvitationRefusalResponseForInvitedPlayer,
		String phrInvitationRefusalNotificationForInvitingPlayer,
		String phrNotAGoodWord,
		String phrWordProvidingPlayerMatchStart,
		String phrWordGuessingPlayerMatchStart,
		String phrWordProvidingPlayerStatus,
		String phrWordGuessingPlayerStatus,
		String phrWinningMessageForWordGuessingPlayer,
		String phrWinningMessageForWordProvidingPlayer,
		String phrLosingMessageForWordGuessingPlayer,
		String phrLosingMessageForWordProvidingPlayer,
		String phrMatchGiveupNotificationForWordGuessingPlayer,
		String phrMatchGiveupNotificationForWordProvidingPlayer,
		String phrGuessingWordHelp) {
		
		// constant parameters -- defines the common phrase parameters -- {{shortCode}}, {{appName}}, {{winningArt}} and {{losingArt}}
		String[] commonPhraseParameters = new String[] {
			"shortCode",  shortCode,
			"appName",    appName,
			"winningArt", winningArt,
			"losingArt",  losingArt};

		this.headCharacter     = headCharacter;
		this.leftArmCharacter  = leftArmCharacter;
		this.chestCharacter    = chestCharacter;
		this.rightArmCharacter = rightArmCharacter;
		this.leftLegCharacter  = leftLegCharacter;
		this.rightLegCharacter = rightLegCharacter;
		this.phr_gallowsArt                                               = new Phrase(commonPhraseParameters, phr_gallowsArt);
		this.phrAskOpponentNicknameOrPhone                                = new Phrase(commonPhraseParameters, phrAskOpponentNicknameOrPhone);
		this.phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation = new Phrase(commonPhraseParameters, phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation);
		this.phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation    = new Phrase(commonPhraseParameters, phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation);
		this.phrInvitationResponseForInvitingPlayer                       = new Phrase(commonPhraseParameters, phrInvitationResponseForInvitingPlayer);
		this.phrInvitationNotificationForInvitedPlayer                    = new Phrase(commonPhraseParameters, phrInvitationNotificationForInvitedPlayer);
		this.phrTimeoutNotificationForInvitingPlayer                      = new Phrase(commonPhraseParameters, phrTimeoutNotificationForInvitingPlayer);
		this.phrInvitationRefusalResponseForInvitedPlayer                 = new Phrase(commonPhraseParameters, phrInvitationRefusalResponseForInvitedPlayer);
		this.phrInvitationRefusalNotificationForInvitingPlayer            = new Phrase(commonPhraseParameters, phrInvitationRefusalNotificationForInvitingPlayer);
		this.phrNotAGoodWord                                              = new Phrase(commonPhraseParameters, phrNotAGoodWord);
		this.phrWordProvidingPlayerMatchStart                             = new Phrase(commonPhraseParameters, phrWordProvidingPlayerMatchStart);
		this.phrWordGuessingPlayerMatchStart                              = new Phrase(commonPhraseParameters, phrWordGuessingPlayerMatchStart);
		this.phrWordProvidingPlayerStatus                                 = new Phrase(commonPhraseParameters, phrWordProvidingPlayerStatus);
		this.phrWordGuessingPlayerStatus                                  = new Phrase(commonPhraseParameters, phrWordGuessingPlayerStatus);
		this.phrWinningMessageForWordGuessingPlayer                       = new Phrase(commonPhraseParameters, phrWinningMessageForWordGuessingPlayer);
		this.phrWinningMessageForWordProvidingPlayer                      = new Phrase(commonPhraseParameters, phrWinningMessageForWordProvidingPlayer);
		this.phrLosingMessageForWordGuessingPlayer                        = new Phrase(commonPhraseParameters, phrLosingMessageForWordGuessingPlayer);
		this.phrLosingMessageForWordProvidingPlayer                       = new Phrase(commonPhraseParameters, phrLosingMessageForWordProvidingPlayer);
		this.phrMatchGiveupNotificationForWordGuessingPlayer              = new Phrase(commonPhraseParameters, phrMatchGiveupNotificationForWordGuessingPlayer);
		this.phrMatchGiveupNotificationForWordProvidingPlayer             = new Phrase(commonPhraseParameters, phrMatchGiveupNotificationForWordProvidingPlayer);
		this.phrGuessingWordHelp                                          = new Phrase(commonPhraseParameters, phrGuessingWordHelp);
	}

	/*********************
	** PHRASING METHODS **
	*********************/
	
	/** @see #phr_gallowsArt */
	private String getGallowsArt(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                             boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg) {
		return phr_gallowsArt.getPhrase("head",     (drawHead     ? headCharacter     : ""),
		                                "leftArm",  (drawLeftArm  ? leftArmCharacter  : " "),
		                                "chest",    (drawChest    ? chestCharacter    : " "),
		                                "rightArm", (drawRightArm ? rightArmCharacter : ""),
		                                "leftLeg",  (drawLeftLeg  ? leftLegCharacter  : " "),
		                                "rightLeg", (drawRightLeg ? rightLegCharacter : ""));
	}
	
	/** Response to the request of inviting a human opponent to play -- Asks for his/her phone or nickname.
	 *  Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}} */
	public String getAskOpponentNicknameOrPhone(String invitingPlayerNickname) {
		return phrAskOpponentNicknameOrPhone.getPhrase("invitingPlayerNickname", invitingPlayerNickname);
	}
	
	/** Response to the request of inviting an internal human opponent to play -- Asks for the word he/she wants to be guessed.
	 *  Variables: {{shortCode}}, {{appName}}, {{opponentNickname}} */
	public String getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation(String opponentNickname) {
		return phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation.getPhrase("opponentNickname", opponentNickname);
	}
	
	/** Response to the request of inviting an external human opponent to play (by giving his/her phone number) -- Asks for the word he/she wants to be guessed.
	 *  Variables: {{shortCode}}, {{appName}}, {{opponentPhoneNumber}} */
	public String getAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(String opponentPhoneNumber) {
		return phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation.getPhrase("opponentPhoneNumber", opponentPhoneNumber);
	}
	
	/** Phrase to notify the inviting player that the notification has been sent to the opponent.
	 *  Variables: {{shortCode}}, {{appName}}, {{invitedPlayerNickname}} */
	public String getInvitationResponseForInvitingPlayer(String invitedPlayerNickname) {
		return phrInvitationResponseForInvitingPlayer.getPhrase("invitedPlayerNickname", invitedPlayerNickname);
	}
	
	/** Phrase to notify the invited player that he/she has been invited for a hangman match.
	 *  Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}} */
	public String getInvitationNotificationForInvitedPlayer(String invitingPlayerNickname) {
		return phrInvitationNotificationForInvitedPlayer.getPhrase("invitingPlayerNickname", invitingPlayerNickname);
	}
	
	/** Notification sent to the inviting player when the invited player doesn't answer to the invitation request within a certain ammount of time.
	 *  Variables: {{shortCode}}, {{appName}}, {{invitedPlayerNickname}}, {{suggestedNewPlayersNickname}} */
	public String getTimeoutNotificationForInvitingPlayer(String invitedPlayerNickname, String suggestedNewPlayersNickname) {
		return phrTimeoutNotificationForInvitingPlayer.getPhrase("invitedPlayerNickname",       invitedPlayerNickname,
		                                                         "suggestedNewPlayersNickname", suggestedNewPlayersNickname);
	}
	
	/** Respose sent to the invited player when he/she refuses a match.
	 *  Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}} */
	public String getInvitationRefusalResponseForInvitedPlayer(String invitingPlayerNickname) {
		return phrInvitationRefusalResponseForInvitedPlayer.getPhrase("invitingPlayerNickname", invitingPlayerNickname);
	}
	
	/** Notification sent to the word providing player when the opponent refuses the match.
	 *  Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{wordGuessingPlayerNickname}} */
	public String getInvitationRefusalNotificationForInvitingPlayer(String invitedPlayerNickname) {
		return phrInvitationRefusalNotificationForInvitingPlayer.getPhrase("invitedPlayerNickname", invitedPlayerNickname);
	}
	
	/** Error Respose sent to the invited player when he/she accepts a match.
	 *  Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{usedLetters}} */
	public String getNotAGoodWord(String word) {
		return phrNotAGoodWord.getPhrase("word", word);
	}
	
	/** Notification sent to the word providing player when the opponent accepts the match.
	 *  Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{wordGuessingPlayerNickname}} */
	public String getWordProvidingPlayerMatchStart(String guessedWordSoFar, String wordGuessingPlayerNickname) {
		return phrWordProvidingPlayerMatchStart.getPhrase("gallowsArt",                 getGallowsArt(false, false, false, false, false, false),
		                                                  "guessedWordSoFar",           guessedWordSoFar,
		                                                  "wordGuessingPlayerNickname", wordGuessingPlayerNickname);
	}

	/** Respose sent to the invited player when he/she accepts a match.
	 *  Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{usedLetters}} */
	public String getWordGuessingPlayerMatchStart(String guessedWordSoFar, String usedLetters) {
		return phrWordGuessingPlayerMatchStart.getPhrase("gallowsArt",             getGallowsArt(false, false, false, false, false, false),
		                                                 "guessedWordSoFar",       guessedWordSoFar,
		                                                 "usedLetters",            usedLetters);
	}
	
	/** Notification sent to the word providing player when the opponent tries to guess his/her word.
	 *  Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{guessedLetter}}, {{usedLetters}}, {{wordGuessingPlayerNickname}} */
	public String getWordProvidingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
                                               boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
                                               String guessedWordSoFar, String guessedLetter, String usedLetters, String wordGuessingPlayerNickname) {
		return phrWordProvidingPlayerStatus.getPhrase("gallowsArt",                 getGallowsArt(drawHead, drawLeftArm, drawRightArm, drawChest, drawLeftLeg, drawRightLeg),
                                                      "guessedWordSoFar",           guessedWordSoFar,
                                                      "guessedLetter",              guessedLetter,
                                                      "usedLetters",                usedLetters,
                                                      "wordGuessingPlayerNickname", wordGuessingPlayerNickname);
	}
	
	/** Response sent to the word guessing player when he/she guesses (sends) a letter or word.
	 *  Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{usedLetters}} */
	public String getWordGuessingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
                                              boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
                                              String guessedWordSoFar, String usedLetters) {
		return phrWordGuessingPlayerStatus.getPhrase("gallowsArt",       getGallowsArt(drawHead, drawLeftArm, drawRightArm, drawChest, drawLeftLeg, drawRightLeg),
                                                     "guessedWordSoFar", guessedWordSoFar,
                                                     "usedLetters",      usedLetters);
		
	}
	
	/** Response sent to the word guessing player when he/she wins the match.
	 *  Variables: {{shortCode}}, {{appName}}, {{winningArt}}, {{word}}, {{wordProvidingPlayerNickname}} */
	public String getWinningMessageForWordGuessingPlayer(String word, String wordProvidingPlayerNickname) {
		return phrWinningMessageForWordGuessingPlayer.getPhrase("word",                        word,
		                                                        "wordProvidingPlayerNickname", wordProvidingPlayerNickname);
	}
	
	/** Notification sent to the word providing player when the opponent wins the match.
	 *  Variables: {{shortCode}}, {{appName}}, {{winningArt}}, {{wordGuessingPlayerNickname}} */
	public String getWinningMessageForWordProvidingPlayer(String wordGuessingPlayerNickname) {
		return phrWinningMessageForWordProvidingPlayer.getPhrase("wordGuessingPlayerNickname", wordGuessingPlayerNickname);
	}
	
	/** Response sent to the word guessing player when he/she loses the match.
	 *  Variables: {{shortCode}}, {{appName}}, {{losingArt}}, {{wordGuessingPlayerNickname}} */
	public String getLosingMessageForWordGuessingPlayer(String word, String wordProvidingPlayerNickname) {
		return phrLosingMessageForWordGuessingPlayer.getPhrase("word",                        word,
		                                                       "wordProvidingPlayerNickname", wordProvidingPlayerNickname);
	}
	
	/** Notification sent to the word providing player when the opponent loses the match.
	 *  Variables: {{shortCode}}, {{appName}}, {{losingArt}}, {{wordGuessingPlayerNickname}} */
	public String getLosingMessageForWordProvidingPlayer(String wordGuessingPlayerNickname) {
		return phrLosingMessageForWordProvidingPlayer.getPhrase("wordGuessingPlayerNickname", wordGuessingPlayerNickname);
	}
	
	/** Notification sent to the word guessing player when his/her actions lead to the cancel of the match.
	 *  Variables: {{shortCode}}, {{appName}}, {{wordProviderPlayerNickname}} */
	public String getMatchGiveupNotificationForWordGuessingPlayer(String wordProvidingPlayerNickname) {
		return phrMatchGiveupNotificationForWordGuessingPlayer.getPhrase("wordProvidingPlayerNickname", wordProvidingPlayerNickname);
	}
	
	/** Notification sent to the word providing player when the match has been canceled -- tipically due to an action of the opponent.
	 *  Variables: {{shortCode}}, {{appName}}, {{wordGuessingPlayerNickname}} */
	public String getMatchGiveupNotificationForWordProvidingPlayer(String wordGuessingPlayerNickname) {
		return phrMatchGiveupNotificationForWordProvidingPlayer.getPhrase("wordGuessingPlayerNickname", wordGuessingPlayerNickname);
	}
	
	/** Response sent to the word guessing player if he/she fails to send a valid command while on a hangman match.
	 *  Variables: {{shortCode}}, {{appName}} */
	public String getGuessingWordHelp() {
		return phrGuessingWordHelp.getPhrase();
	}

}