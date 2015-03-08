package mutua.hangmansmsgame.i18n;

import java.util.Arrays;
import java.util.regex.Matcher;

import mutua.hangmansmsgame.config.Configuration;
import mutua.icc.configuration.annotations.ConfigurableElement;
import static mutua.hangmansmsgame.config.Configuration.SHORT_CODE;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * IPhraseology.java
 * =================
 * (created by luiz, Dec 19, 2014)
 *
 * Define the methods that will generate the phrasing for the SMS Application
 *
 * @see TestPhraseology
 * @version $Id$
 * @author luiz
 */

public abstract class IPhraseology {
	
	
	public enum EPhraseNames {
		
		shortHelp                                                     ("(J) Play online; (C) Invite a friend or user; (R)anking; (A)Help"),
		gallowsArt                                                    ("+-+\n| {{head}}\n|{{leftArm}}{{chest}}{{rightArm}}\n|{{leftLeg}} {{rightLeg}}\n|\n====\n"),
		winningArt                                                    ("\\0/\n |\n/ \\\n"),
		losingArt                                                     ("+-+\n| x\n|/|\\\n|/ \\\n====\n"),
		playersList                                                   ("{{nick}} ({{state}}/{{numberOfLuckyNumbers}})"),
		INFOWelcome                                                   ("HANGMAN: Registration succeeded. Send HELP to {{shortCode}} to know the rules and how to play, or simply send PLAY to {{shortCode}}"),
		INFOFallbackNewUsersHelp                                      ("You are at the HANGMAN game. To continue, you must subscribe. Send HANGMAN now to {{shortCode}} and compete for prizes. You will be charged at $ every week."),
		INFOFallbackExistingUsersHelp                                 ("HANGMAN: unknown command. Please send HELP to see the full list. Short list: LIST to see online users; P [NICK] [MSG] to send a private message; ",
		                                                               "INVITE [NICK] to invite a player; INVITE [PHONE] to invite a friend of yours; PLAY to play with a random user. Choose an option and send it to {{shortCode}}"),
		INFOFullHelp1                                                 ("You can play the HANGMAN game in 2 ways: guessing someone's word or inviting someone to play with your word " +
		                                                               "You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number " +
		                                                               "Every week, 1 lucky number is selected to win the prize. Send an option to {{shortCode}}: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help"),
		INFOFullHelp2                                                 ("This is the extended help message..."),
		INFOWelcomeMenu                                               ("Pick an option. Send to {{shortCode}}: {{shortHelp}}"),
		INFOCouldNotRegister                                          ("HANGMAN: You could not be registered at this time. Please try again later."),
		PROFILEView                                                   ("HANGMAN: {{nick}}: Subscribed, {{state}}, {{numberOfLuckyNumbers}} lucky numbers. Send SIGNUP to provoke for free or INVITE {{nick}} for a match."),
		PROFILEFullfillingAskNick                                     ("HANGMAN: To play with a friend, u need first to sign your name. Now send your name (8 letters or numbers max.) to {{shortCode}}"),
		PROFILENickRegisteredNotification                             ("HANGMAN: Name registered: {{newNickname}}. Send LIST to {{shortCode}} to see online players. NICK [NEW NICK] to change your name."),
		PLAYINGWordProvidingPlayerStart                               ("Game started with {{wordGuessingPlayerNick}}.\n{{gallowsArt}}Send P {{wordGuessingPlayerNick}} MSG to give him/her clues"),
		PLAYINGWordGuessingPlayerStart                                ("{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"),
		PLAYINGWordGuessingPlayerStatus                               ("{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"),
		PLAYINGWordProvidingPlayerStatus                              ("{{nick}} guessed letter {{guessedLetter}}\n{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend P {{nick}} MSG to provoke him/her"),
		PLAYINGWinningMessageForWordGuessingPlayer                    ("{{winningArt}}{{word}}! You got it! Here is your lucky number: {{luckyNumber}}. Send: J to play or A for help"),
		PLAYINGWinningMessageForWordProvidingPlayer                   ("{{wordGuessingPlayerNick}} guessed your word! P {{wordGuessingPlayerNick}} MSG to provoke him/her or INVITE {{wordGuessingPlayerNick}} for a new match"),
		PLAYINGLosingMessageForWordGuessingPlayer                     ("{{losingArt}}The word was {{word}}. Now challenge {{wordProvidingPlayerNick}}: send INVITE {{wordProvidingPlayerNick}} to {{shortCode}}"),
		PLAYINGLosingMessageForWordProvidingPlayer                    ("Good one! {{wordGuessingPlayerNick}} wasn't able to guessed your word! P {{wordGuessingPlayerNick}} MSG to provoke him/her or INVITE {{wordGuessingPlayerNick}} for a new match"),
		PLAYINGMatchGiveupNotificationForWordProvidingPlayer          ("{{wordGuessingPlayerNick}} cancelled the match. To find other users to play with, sent LIST to {{shortCode}}"),
		PLAYINGMatchGiveupNotificationForWordGuessingPlayer           ("Your match with {{wordProvidingPlayerNick}} has been canceled. Send P {{wordProvidingPlayerNick}} MSG to talk to him/her or LIST to play with someone else"),
		INVITINGAskOpponentNickOrPhone                                ("HANGMAN: Name registered: {{invitingPlayerNickname}}. Send your friend's phone to {{shortCode}} or LIST to see online players. NICK [NEW NICK] to change your name."),
		INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation ("HANGMAN: Inviting {{opponentNickname}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"),
		INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation("HANGMAN: Your friend's phone: {{opponentPhoneNumber}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"),
		INVITINGInvitationNotificationForInvitingPlayer               ("{{invitedPlayerNickname}} was invited to play with you. while you wait, you can provoke {{invitedPlayerNickname}} by sending a message to {{shortCode}} (0.31+tax) or send SIGNUP to provoke for free how many times you want"),
		INVITINGTimeoutNotificationForInvitingPlayer                  ("{{invitedPlayerNickname}} is taking too long to answer. However, a new player, {{suggestedNewPlayersNickname}}, is available. Play with {{suggestedNewPlayersNickname}}? Send YES to {{shortCode}}"),
		INVITINGInvitationNotificationForInvitedPlayer                ("HANGMAN: {{invitingPlayerNickname}} is inviting you for a hangman match. Do you accept? Send YES to {{shortCode}} or PROFILE to see {{invitingPlayerNickname}} information"),
		INVITINGInvitationRefusalNotificationForInvitingPlayer        ("{{invitedPlayerNickname}} refused your invitation to play. Send LIST to 9714 and pick someone else"),
		INVITINGInvitationRefusalNotificationForInvitedPlayer         ("The invitation to play the Hangman Game made by {{invitingPlayerNickname}} was refused. Send LIST to {{shortCode}} to see online users"),
		INVITINGNotAGoodWord                                          ("You selected '{{word}}'. This is possily not a good word. Please think of one only with A-Z letters, without accents, digits, ponctuation or any other special characters and send it to {{shortCode}}"),
		LISTINGShowPlayers                                            ("{{playersList}}. To play, send INVITE [NICK] to {{shortCode}}; MORE for more players or PROFILE [NICK]"),
		LISTINGNoMorePlayers                                          ("There are no more online players to show. Send P [NICK] [MSG] to provoke or INVITE [PHONE] to invite a friend of yours to play the Hangman Game."),
		PROVOKINGDeliveryNotification                                 ("Your message was sent to {{destinationNick}}. Wait for the answer or provoke other players sending P [NICK] [MSG] to {{shortCode}}. Send SIGNUP to provoke for free."),
		PROVOKINGSendMessage                                          ("{{sourceNick}}: {{message}} - Answer by sending P {{sourceNick}} [MSG] to {{shortCode}}"),
		PROVOKINGNickNotFound                                         ("No player with nickname '{{nickname}}' was found. Maybe he/she changed it? Send LIST to {{shortCode}} to see online players"),
		UNSUBSCRIBINGUnsubscriptionNotification                       ("You are now unsubscribed from the HANGMAN GAME and will no longer receive invitations to play nor lucky numbers. To join again, send HANGMAN to {{shortCode}}"),
		
		;
		
		private String[] texts;
		
		private EPhraseNames(String... texts) {
			this.texts = texts;
		}
		
		public void setTexts(String[] texts) {
			this.texts = texts;
		}
		
		public String[] getTexts() {
			return texts;
		}
	}

	
	/** retrieves the messages for the given 'phrase', fulfilling all parameters, where
	 *  'parameters' := { {"name", "value"}, ... }, or 'null' if 'phraseName' wasn't found */
	protected String[] getPhrases(EPhraseNames phrase, String[][] parameters) {
		String[] messages = phrase.getTexts();
		if (messages == null) {
			return null;
		}
		messages = Arrays.copyOf(messages, messages.length);
		if (parameters != null) {
			for (int i=0; i<messages.length; i++) {
				String originalMessage = messages[i];
				String fulfilledMessage = originalMessage;
				// fulfill the parameters
				for (String[] parameter : parameters) {
					String parameterName  = parameter[0];
					String parameterValue = parameter[1];
					if (parameterValue != null) {
						fulfilledMessage = fulfilledMessage.replaceAll("\\{\\{" + parameterName + "\\}\\}", Matcher.quoteReplacement(parameterValue));
					}
				}
				messages[i] = fulfilledMessage;
			}
		}
		return messages;
	}
	
	/** @see IPhraseology#getPhrases(EPhraseNames, String[][]) */
	protected String getPhrase(EPhraseNames phrase, String[][] parameters) {
		String[] messages = getPhrases(phrase, parameters);
		if (messages == null) {
			return null;
		} else {
			return messages[0];
		}
	}
	
	/** @see IPhraseology#getPhrases(EPhraseNames, String[][]) */
	protected String getPhrase(EPhraseNames phrase) {
		return getPhrase(phrase, null);
	}

	
	/**************************
	** INSTANTIATION METHODS **
	**************************/
	
	public static IPhraseology getCarrierSpecificPhraseology(ESMSInParserCarrier carrier) {
		return new TestPhraseology(SHORT_CODE);
	}

	protected String getList(String[] elements, String pairSeparator, String lastPairSeparator) {
        String list = "";
        for (int i=0; i<elements.length; i++) {
                list += elements[i];
                if (i == (elements.length-2)) {
                        list += lastPairSeparator;
                } else if (i != (elements.length-1)) {
                        list += pairSeparator;
                }
        }
        return list;
}
	
	/**********************
	** INTERFACE METHODS **
	**********************/
	
	// CUPONS
	/////////
	
//	/** shown when the user deserves another lucky number in order to participate in the next draw(s) */
//	public abstract String CUPOMNewLuckyNumber(String cuponsList, String series, String daysList, String monthAndYear);
//	
//	/** shown when the user wants to get informed on which draws him/her participation is assured */
//	public abstract String CUPOMDrawParticipation(String daysList, String monthAndYear, String cuponsList);
//	
//	/** shown when the user wants to be informed about draws on which he/she is a participant, but he/she isn't participating on any */
//	public abstract String CUPOMNoLuckyNumbers();
	
	
	// SUBSCRIPTION
	///////////////
	
//	/** shown when the user requested to be a subscriber, but he/she is already one */
//	public abstract String SUBSCRIPTIONAlreadySubscribed();
//	
//	/** shown when we could not bill the user and, therefore, could not complete the subscription or renewal process */
//	public abstract String SUBSCRIPTIONCouldNotBillNorCompleteTheSubscriptionOrRenewal();
//	
//	/** show when the time to renew the subscription has come and it succeeded */
//	public abstract String SUBSCRIPTIONRenewalSuccess();
	
	
	// INFO
	////////
	
	@ConfigurableElement("shown in response to the first interaction the user has with the game, after registration")
	public abstract String INFOWelcome();
	
	@ConfigurableElement("shown in response to the first interaction the user has with the game, to instruct him/her on how to register")
	public abstract String INFOFallbackNewUsersHelp();
	
	@ConfigurableElement("shown when an existing user attempts to send an unrecognized command, to give him/her a quick list of commands")
	public abstract String INFOFallbackExistingUsersHelp();
	
	@ConfigurableElement("shown when the user request the help / instructions")
	public abstract String[] INFOFullHelp1();
	
	@ConfigurableElement("shown when the user requests to see more of the help / instructions")
	public abstract String[] INFOFullHelp2();
	
	@ConfigurableElement("menu shown to new users")
	public abstract String INFOWelcomeMenu();

	@ConfigurableElement("shown when it is not possible to register the user on the registration APIs")
	public abstract String INFOCouldNotRegister();


	
	
	// PROFILE
	//////////
	
	@ConfigurableElement("shown when a user wants to view the profile of another user")
	public abstract String PROFILEView(String nick, String state, int numberOfLuckyNumbers);

	@ConfigurableElement("shown when the game asks the player to choose a nickname")
	public abstract String PROFILEFullfillingAskNick();

	@ConfigurableElement("present the user information regarding the confirmation of his nickname registration")
	public abstract String PROFILENickRegisteredNotification(String newNickname);

	
	// PLAYING
	//////////
	
	protected String getGallowsArt(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                                boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg) {
		return getPhrase(EPhraseNames.gallowsArt, new String[][] {
			{"head",     (drawHead?"O":"")},
			{"leftArm",  (drawLeftArm?"/":" ")},
			{"chest",    (drawChest?"|":" ")},
			{"rightArm", (drawRightArm?"\\":"")},
			{"leftLeg",  (drawLeftLeg?"/":" ")},
			{"rightLeg", (drawRightLeg?"\\":"")},
		});
	}
	
	protected String getWinningArt() {
		return getPhrase(EPhraseNames.winningArt);
	}
	
	protected String getLosingArt() {
		return getPhrase(EPhraseNames.losingArt);
	}

	
	@ConfigurableElement("sent to the player who provided the word when the match starts -- when it is accepted by the opponent player")
	public abstract String PLAYINGWordProvidingPlayerStart(String guessedWordSoFar, String wordGuessingPlayerNick);

	@ConfigurableElement("sent back to the player who is attempting to guess the word when the match starts")
	public abstract String PLAYINGWordGuessingPlayerStart(String guessedWordSoFar, String usedLetters);
	
	@ConfigurableElement("shows the state of the game play to the player attempting to guess the word")
	public abstract String PLAYINGWordGuessingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
                                                           boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
                                                           String guessedWordSoFar, String usedLetters);
	
	@ConfigurableElement("shows the state of the game play to the player who provided the word")
	public abstract String PLAYINGWordProvidingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                                                        boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
	                                                        String guessedWordSoFar, String guessedLetter, String usedLetters, String nick);
	
	@ConfigurableElement("shown to the winning player, who tried to guess the word")
	public abstract String PLAYINGWinningMessageForWordGuessingPlayer(String word, String luckyNumber);
	
	@ConfigurableElement("show to the word providing player when the word guessing player won the game")
	public abstract String PLAYINGWinningMessageForWordProvidingPlayer(String wordGuessingPlayerNick);
	
	@ConfigurableElement("shown to the losing player, who tried to guess the word")
	public abstract String PLAYINGLosingMessageForWordGuessingPlayer(String word, String wordProvidingPlayerNick);
	
	@ConfigurableElement("show to the word providing player when the word guessing player lost the game")
	public abstract String PLAYINGLosingMessageForWordProvidingPlayer(String wordGuessingPlayerNick);

	@ConfigurableElement("sent to inform the word providing player that the opponent has taken actions that lead to a match give up")
	public abstract String PLAYINGMatchGiveupNotificationForWordProvidingPlayer(String wordGuessingPlayerNick);

	@ConfigurableElement("sent to inform the word guessing player that his actions lead to a match give up")
	public abstract String PLAYINGMatchGiveupNotificationForWordGuessingPlayer(String wordProvidingPlayerNick);

	
	// INVITING
	///////////
	
	@ConfigurableElement("shown when the game wants to ask the player for an opponent to be invited for a match")
	public abstract String INVITINGAskOpponentNickOrPhone(String invitingPlayerNickname);

	@ConfigurableElement("shown when the game asks the user for a word to start a match with an invited player, for which the nick name was provided")
	public abstract String INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation(String opponentNickname);

	@ConfigurableElement("shown when the game asks the user for a word to start a match with an invited player, for which the phone number was provided")
	public abstract String INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(String opponentPhoneNumber);

	@ConfigurableElement("message to notify the inviting player that the invitation has been sent")
	public abstract String INVITINGInvitationNotificationForInvitingPlayer(String invitedPlayerNickname);
	
	@ConfigurableElement("message to notify the inviting player that the opponent took too long to reply and the match is cancelled")
	public abstract String INVITINGTimeoutNotificationForInvitingPlayer(String invitedPlayerNickname, String suggestedNewPlayersNickname);
	
	@ConfigurableElement("message to notify the invited player that someone wants to play a hangman match")
	public abstract String INVITINGInvitationNotificationForInvitedPlayer(String invitingPlayerNickname);
	
	@ConfigurableElement("message to notify the inviting player that the opponent refused the match")
	public abstract String INVITINGInvitationRefusalNotificationForInvitingPlayer(String invitedPlayerNickname);

	@ConfigurableElement("response to the invited player after he/she refused to play")
	public abstract String INVITINGInvitationRefusalNotificationForInvitedPlayer(String invitingPlayerNickname);
	
	@ConfigurableElement("response to the inviting player when he/she picks a word refused by the game")
	public abstract String INVITINGNotAGoodWord(String word);
	

	// LISTING PLAYERS
	//////////////////
	
	@ConfigurableElement("shows a list of players. 'playersInfo' := { {nick, estate, number of lucky numbers received}, ... }")
	public abstract String LISTINGShowPlayers(String[][] playersInfo);

	@ConfigurableElement("used to notify that we were through when showing online players")
	public abstract String LISTINGNoMorePlayers();

	
	// PROVOKING
	////////////
	
	@ConfigurableElement("presented to the user after he/she attempted to provoke someone, indicating the message was delivered")
	public abstract String PROVOKINGDeliveryNotification(String destinationNick);

	@ConfigurableElement("the message sent to the destination user when he is being provoked by someone")
	public abstract String PROVOKINGSendMessage(String sourceNick, String message);

	@ConfigurableElement("presented to inform that the desired nickname was not found")
	public abstract String PROVOKINGNickNotFound(String nickname);

	
	// UNSUBSCRIPTION
	/////////////////
	
	@ConfigurableElement("presented to inform the subscription was canceled on the game platform")
	public abstract String UNSUBSCRIBINGUnsubscriptionNotification();

}
