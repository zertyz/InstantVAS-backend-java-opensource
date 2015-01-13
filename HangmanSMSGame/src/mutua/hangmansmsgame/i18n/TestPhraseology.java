package mutua.hangmansmsgame.i18n;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.regex.Matcher;

/** <pre>
 * TestPhraseology.java
 * ====================
 * (created by luiz, Dec 19, 2014)
 *
 * Phrasing used for testing purposes
 *
 * @see IPhraseology
 * @version $Id$
 * @author luiz
 */

public class TestPhraseology extends IPhraseology {
	
	
	private String shortCode;

	
	/** @see IPhraseology#IPhraseology */
	private static String[][] phrases = {
		{"shortHelp",                                                      "(J) Play online; (C) Invite a friend or user; (R)anking; (A)Help"},
		{"gallowsArt",                                                     "+-+\n| {{head}}\n|{{leftArm}}{{chest}}{{rightArm}}\n|{{leftLeg}} {{rightLeg}}\n|\n====\n"},
		{"winningArt",                                                     "\\0/\n |\n/ \\\n"},
		{"losingArt",                                                      "+-+\n| x\n|/|\\\n|/ \\\n====\n"},
		{"playersList",                                                    "{{nick}} ({{state}}/{{numberOfLuckyNumbers}})"},
		{"INFOWelcome",                                                    "Welcome to the HANGMAN game. Join and compete for prizes. Send HELP for free to {{shortCode}} to know the rules."},
		{"INFOFullHelp",                                                   "1/3: You can play the HANGMAN game in 2 ways: guessing someone's word or inviting someone to play with your word",
		                                                                   "2/3: You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number",
		                                                                   "3/3: Every week, 1 lucky number is selected to win the prize. Send an option to {{shortCode}}: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help"},
		{"INFOWelcomeMenu",                                                "Pick an option. Send to {{shortCode}}: {{shortHelp}}"},
		{"INFOCouldNotRegister",                                           "HANGMAN: You could not be registered at this time. Please try again later."},
		{"PROFILEView",                                                    "HANGMAN: {{nick}}: Subscribed, {{state}}, {{numberOfLuckyNumbers}} lucky numbers. Send SIGNUP to provoke for free or INVITE {{nick}} for a match."},
		{"PROFILEFullfillingAskNick",                                      "HANGMAN: To play with a friend, u need first to sign your name. Now send your name (8 letters or numbers max.) to {{shortCode}}"},
		{"PROFILENickRegisteredNotification",                              "HANGMAN: Name registered: {{newNickname}}. Send LIST to {{shortCode}} to see online players. NICK [NEW NICK] to change your name."},
		{"PLAYINGWordProvidingPlayerStart",                                "Game started with {{wordGuessingPlayerNick}}.\n{{gallowsArt}}Send P {{wordGuessingPlayerNick}} MSG to give him/her clues"},
		{"PLAYINGWordGuessingPlayerStart",                                 "{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"},
		{"PLAYINGWordGuessingPlayerStatus",                                "{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"},
		{"PLAYINGWordProvidingPlayerStatus",                               "{{nick}} guessed letter {{guessedLetter}}\n{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend P {{nick}} MSG to provoke him/her"},
		{"PLAYINGWinningMessageForWordGuessingPlayer",                     "{{winningArt}}{{word}}! You got it! Here is your lucky number: {{luckyNumber}}. Send: J to play or A for help"},
		{"PLAYINGWinningMessageForWordProvidingPlayer",                    "{{wordGuessingPlayerNick}} guessed your word! P {{wordGuessingPlayerNick}} MSG to provoke him/her or INVITE {{wordGuessingPlayerNick}} for a new match"},
		{"PLAYINGLosingMessageForWordGuessingPlayer",                      "{{losingArt}}The word was {{word}}. Now challenge {{wordProvidingPlayerNick}}: send INVITE {{wordProvidingPlayerNick}} to {{shortCode}}"},
		{"PLAYINGLosingMessageForWordProvidingPlayer",                     "Good one! {{wordGuessingPlayerNick}} wasn't able to guessed your word! P {{wordGuessingPlayerNick}} MSG to provoke him/her or INVITE {{wordGuessingPlayerNick}} for a new match"},
		{"PLAYINGMatchGiveupNotificationForWordProvidingPlayer",           "{{wordGuessingPlayerNick}} cancelled the match. To find other users to play with, sent LIST to {{shortCode}}"},
		{"PLAYINGMatchGiveupNotificationForWordGuessingPlayer",            "Your match with {{wordProvidingPlayerNick}} has been canceled. Send P {{wordProvidingPlayerNick}} MSG to talk to him/her or LIST to play with someone else"},
		{"INVITINGAskOpponentNickOrPhone",                                 "HANGMAN: Name registered: {{invitingPlayerNickname}}. Send your friend's phone to {{shortCode}} or LIST to see online players. NICK [NEW NICK] to change your name."},
		{"INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation",  "HANGMAN: Inviting {{opponentNickname}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"},
		{"INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation", "HANGMAN: Your friend's phone: {{opponentPhoneNumber}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"},
		{"INVITINGInvitationNotificationForInvitingPlayer",                "{{invitedPlayerNickName}} was invited to play with you. while you wait, you can provoke {{invitedPlayerNickName}} by sending a message to {{shortCode}} (0.31+tax) or send SIGNUP to provoke for free how many times you want"},
		{"INVITINGTimeoutNotificationForInvitingPlayer",                   "{{invitedPlayerNickName}} is taking too long to answer. However, a new player, {{suggestedNewPlayersNickName}}, is available. Play with {{suggestedNewPlayersNickName}}? Send YES to {{shortCode}}"},
		{"INVITINGInvitationNotificationForInvitedPlayer",                 "HANGMAN: {{invitingPlayerNickName}} is inviting you for a hangman match. Do you accept? Send YES to {{shortCode}} or PROFILE to see {{invitingPlayerNickName}} information"},
		{"LISTINGShowPlayers",                                             "{{playersList}}. To play, send INVITE [NICK] to {{shortCode}}; MORE for more players or PROFILE [NICK]"},
		{"LISTINGNoMorePlayers",                                           "There is no more online players to show. Send P [NICK] [MSG] to provoke or INVITE [PHONE] to invite a friend of yours to play the Hangman Game."},
		{"PROVOKINGDeliveryNotification",                                  "Your message was sent to {{destinationNick}}. Wait for the answer or provoke other players sending P [NICK] [MSG] to {{shortCode}}. Send SIGNUP to provoke for free."},
		{"PROVOKINGSendMessage",                                           "{{sourceNick}}: {{message}} - Answer by sending P {{sourceNick}} [MSG] to {{shortCode}}"},
		{"PROVOKINGNickNotFound",                                          "No player with nickname '{{nickname}}' was found. Maybe he/she changed it? Send LIST to {{shortCode}} to see online players"},
		{"UNSUBSCRIBINGUnsubscriptionNotification",                        "You are now unsubscribed from the HANGMAN GAME and will no longer receive invitations to play nor lucky numbers. To join again, send HANGMAN to {{shortCode}}"},
	};
	
	private String getShortHelp() {
		return getPhrase("shortHelp");
	}
	
	
	public TestPhraseology(String shortCode) {
		super(phrases);
		this.shortCode = shortCode;
	}
	
	@Override
	public String INFOWelcome() {
		return getPhrase("INFOWelcome", new String[][] {{"shortCode", shortCode}});
	}

	@Override
	public String[] INFOFullHelp() {
		return getPhrases("INFOFullHelp", new String[][] {{"shortCode", shortCode}});
	}

	@Override
	public String INFOWelcomeMenu() {
		return getPhrase("INFOWelcomeMenu", new String[][] {
			{"shortCode", shortCode},
			{"shortHelp", getShortHelp()},
		});
	}

	@Override
	public String INFOCouldNotRegister() {
		return getPhrase("INFOCouldNotRegister");
	}

	@Override
	public String PROFILEView(String nick, String state, int numberOfLuckyNumbers) {
		return getPhrase("PROFILEView", new String[][] {
			{"shortCode",            shortCode},
			{"nick",                 nick},
			{"state",                state},
			{"numberOfLuckyNumbers", Integer.toString(numberOfLuckyNumbers)},
		});
	}
	
	@Override
	public String PROFILEFullfillingAskNick() {
		return getPhrase("PROFILEFullfillingAskNick", new String[][] {
			{"shortCode", shortCode},
		});
	}
	
	@Override
	public String PROFILENickRegisteredNotification(String newNickname) {
		return getPhrase("PROFILENickRegisteredNotification", new String[][] {
			{"shortCode",   shortCode},
			{"newNickname", newNickname},
		});
	}


	@Override
	public String PLAYINGWordProvidingPlayerStart(String guessedWordSoFar, String wordGuessingPlayerNick) {
		return getPhrase("PLAYINGWordProvidingPlayerStart", new String[][] {
			{"shortCode",              shortCode},
			{"wordGuessingPlayerNick", wordGuessingPlayerNick},
			{"gallowsArt",             getGallowsArt(false, false, false, false, false, false)},
		});
	}

	@Override
	public String PLAYINGWordGuessingPlayerStart(String guessedWordSoFar, String usedLetters) {
		return getPhrase("PLAYINGWordGuessingPlayerStart", new String[][] {
			{"shortCode",              shortCode},
			{"guessedWordSoFar",       guessedWordSoFar},
			{"usedLetters",            usedLetters},
			{"gallowsArt",             getGallowsArt(false, false, false, false, false, false)},
		});
	}
	
	@Override
	public String PLAYINGWordGuessingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                                              boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
	                                              String guessedWordSoFar, String usedLetters) {
		return getPhrase("PLAYINGWordGuessingPlayerStatus", new String[][] {
			{"shortCode",              shortCode},
			{"guessedWordSoFar",       guessedWordSoFar},
			{"usedLetters",            usedLetters},
			{"gallowsArt",             getGallowsArt(drawHead, drawLeftArm, drawRightArm, drawChest, drawLeftLeg, drawRightLeg)},
		});
	}

	@Override
	public String PLAYINGWordProvidingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                                               boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
	                                               String guessedWordSoFar, String guessedLetter, String usedLetters,
	                                               String nick) {
		return getPhrase("PLAYINGWordProvidingPlayerStatus", new String[][] {
			{"shortCode",              shortCode},
			{"guessedWordSoFar",       guessedWordSoFar},
			{"guessedLetter",          guessedLetter},
			{"usedLetters",            usedLetters},
			{"nick",                   nick},
			{"gallowsArt",             getGallowsArt(drawHead, drawLeftArm, drawRightArm, drawChest, drawLeftLeg, drawRightLeg)},
		});
	}

	@Override
	public String PLAYINGWinningMessageForWordGuessingPlayer(String word, String luckyNumber) {
		return getPhrase("PLAYINGWinningMessageForWordGuessingPlayer", new String[][] {
			{"shortCode",   shortCode},
			{"word",        word},
			{"luckyNumber", luckyNumber},
		});
	}

	@Override
	public String PLAYINGWinningMessageForWordProvidingPlayer(String wordGuessingPlayerNick) {
		return getPhrase("PLAYINGWinningMessageForWordProvidingPlayer", new String[][] {
			{"shortCode",              shortCode},
			{"wordGuessingPlayerNick", wordGuessingPlayerNick},
		});
	}

	@Override
	public String PLAYINGLosingMessageForWordGuessingPlayer(String word, String wordProvidingPlayerNick) {
		return getPhrase("PLAYINGLosingMessageForWordGuessingPlayer", new String[][] {
			{"shortCode",               shortCode},
			{"word",                    word},
			{"wordProvidingPlayerNick", wordProvidingPlayerNick},
			{"losingArt",               getLosingArt()},
		});
	}

	@Override
	public String PLAYINGLosingMessageForWordProvidingPlayer(String wordGuessingPlayerNick) {
		return getPhrase("PLAYINGLosingMessageForWordProvidingPlayer", new String[][] {
			{"shortCode",              shortCode},
			{"wordGuessingPlayerNick", wordGuessingPlayerNick},
		});
	}
	
	@Override
	public String PLAYINGMatchGiveupNotificationForWordProvidingPlayer(String wordGuessingPlayerNick) {
		return getPhrase("PLAYINGLosingMessageForWordProvidingPlayer", new String[][] {
			{"shortCode",              shortCode},
			{"wordGuessingPlayerNick", wordGuessingPlayerNick},
		});
	}

	@Override
	public String PLAYINGMatchGiveupNotificationForWordGuessingPlayer(String wordProvidingPlayerNick) {
		return getPhrase("PLAYINGMatchGiveupNotificationForWordGuessingPlayer", new String[][] {
			{"shortCode",               shortCode},
			{"wordProvidingPlayerNick", wordProvidingPlayerNick},
		});
	}

	@Override
	public String INVITINGAskOpponentNickOrPhone(String invitingPlayerNickname) {
		return getPhrase("INVITINGAskOpponentNickOrPhone", new String[][] {
			{"shortCode",              shortCode},
			{"invitingPlayerNickname", invitingPlayerNickname},
		});
	}

	@Override
	public String INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation(String opponentNickname) {
		return getPhrase("INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation", new String[][] {
			{"shortCode",        shortCode},
			{"opponentNickname", opponentNickname},
		});
	}

	@Override
	public String INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(String opponentPhoneNumber) {
		return getPhrase("INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation", new String[][] {
			{"shortCode",           shortCode},
			{"opponentPhoneNumber", opponentPhoneNumber},
		});
	}

	@Override
	public String INVITINGInvitationNotificationForInvitingPlayer(String invitedPlayerNickName) {
		return getPhrase("INVITINGInvitationNotificationForInvitingPlayer", new String[][] {
			{"shortCode",             shortCode},
			{"invitedPlayerNickName", invitedPlayerNickName},
		});
	}

	@Override
	public String INVITINGTimeoutNotificationForInvitingPlayer(String invitedPlayerNickName, String suggestedNewPlayersNickName) {
		return getPhrase("INVITINGTimeoutNotificationForInvitingPlayer", new String[][] {
			{"shortCode",                   shortCode},
			{"invitedPlayerNickName",       invitedPlayerNickName},
			{"suggestedNewPlayersNickName", suggestedNewPlayersNickName},
		});
	}

	@Override
	public String INVITINGInvitationNotificationForInvitedPlayer(String invitingPlayerNickName) {
		return getPhrase("INVITINGInvitationNotificationForInvitedPlayer", new String[][] {
			{"shortCode",                   shortCode},
			{"invitingPlayerNickName",      invitingPlayerNickName},
		});
	}

	@Override
	public String LISTINGShowPlayers(String[][] playersInfo) {
		String[] players = new String[playersInfo.length];
		for (int i=0; i<playersInfo.length; i++) {
			String[] playerInfo = playersInfo[i];
			String nick                 = playerInfo[0];
			String estate               = playerInfo[1];
			String numberOfLuckyNumbers = playerInfo[2];
			players[i] = nick + "(" + estate + "/" + numberOfLuckyNumbers + ")";
		}
		String playersList = getList(players, ", ", ", ");
		return getPhrase("LISTINGShowPlayers", new String[][] {
			{"shortCode",   shortCode},
			{"playersList", playersList},
		});
	}

	@Override
	public String LISTINGNoMorePlayers() {
		return getPhrase("LISTINGNoMorePlayers", new String[][] {
			{"shortCode",   shortCode},
		});
	}

	@Override
	public String PROVOKINGDeliveryNotification(String destinationNick) {
		return getPhrase("PROVOKINGDeliveryNotification", new String[][] {
			{"shortCode",       shortCode},
			{"destinationNick", destinationNick},
		});
	}

	@Override
	public String PROVOKINGSendMessage(String sourceNick, String message) {
		return getPhrase("PROVOKINGSendMessage", new String[][] {
			{"shortCode",  shortCode},
			{"sourceNick", sourceNick},
			{"message",    message},
		});
	}

	@Override
	public String PROVOKINGNickNotFound(String nickname) {
		return getPhrase("PROVOKINGNickNotFound", new String[][] {
			{"shortCode", shortCode},
			{"nickname",  nickname},
		});
	}

	@Override
	public String UNSUBSCRIBINGUnsubscriptionNotification() {
		return getPhrase("UNSUBSCRIBINGUnsubscriptionNotification", new String[][] {
			{"shortCode", shortCode},
		});
	}

}
