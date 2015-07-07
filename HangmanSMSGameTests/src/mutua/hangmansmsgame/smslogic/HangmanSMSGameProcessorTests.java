package mutua.hangmansmsgame.smslogic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Random;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.ISessionDB;
import mutua.hangmansmsgame.dal.IUserDB;
import mutua.hangmansmsgame.hangmangamelogic.HangmanGame;
import mutua.hangmansmsgame.i18n.IPhraseology;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

import org.junit.Test;

import config.Configuration;

/** <pre>
 * HangmanSMSGameProcessorTests.java
 * =================================
 * (created by luiz, Jan 19, 2011)
 *
 * Tests the application as described on the product manual -- with emphasis at the phrasing,
 * and the usage mechanics
 */


public class HangmanSMSGameProcessorTests {

	
	private TestCommons tc = new TestCommons();
	IPhraseology testPhraseology = IPhraseology.getCarrierSpecificPhraseology(ESMSInParserCarrier.TEST_CARRIER);
	
	// databases
	////////////
    
	private static IUserDB    userDB    = DALFactory.getUserDB(Configuration.DEFAULT_DAL);
	private static ISessionDB sessionDB = DALFactory.getSessionDB(Configuration.DEFAULT_DAL);

	
	/*********************
	** AUXILIAR METHODS **
	*********************/
    
    /** for NEW_USERs, register a new player's 'phone' and give it the provided 'nickname' */
    public void registerUser(String phone, String nickname) {
    	tc.checkResponse(phone, "forca",            testPhraseology.INFOWelcome());
		tc.checkResponse(phone, "nick " + nickname, testPhraseology.PROFILENickRegisteredNotification(nickname));
    }
    
    /** for EXISTING_USERs, send 'play' to start playing with a bot */
    public void playWithBot(String phone, String expectedWord) {
    	HangmanGame game = new HangmanGame(expectedWord, 6);
    	tc.checkResponse(phone, "play", testPhraseology.PLAYINGWordGuessingPlayerStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()));
    }
    
    /** for EXISTING_USERs, send the 'invite' by nickname command from the word providing to the word guessing already registered players */
    public void invitePlayerByNick(String wordProvidingPlayerPhone, String wordGuessingPlayerNick) {
		tc.checkResponse(wordProvidingPlayerPhone, "invite " + wordGuessingPlayerNick, testPhraseology.INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation(wordGuessingPlayerNick));		
    }
    
    /** for EXISTING_USERs, send the 'invite' by phone command from the word providing to the word guessing already registered players */
    public void invitePlayerByPhone(String wordProvidingPlayerPhone, String wordGuessingPlayerPhone) {
		tc.checkResponse(wordProvidingPlayerPhone, "invite " + wordGuessingPlayerPhone, testPhraseology.INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(wordGuessingPlayerPhone));		
    }
    
    /** for an ENTERING_MATCH_WORD word providing player, send the word for a match */
    public void sendWordToBeGuessed(String wordProvidingPlayerPhone, String wordProvidingPlayerNick,
                                    String word, String wordGuessingPlayerNick) {
		// provide the word
    	tc.checkResponse(wordProvidingPlayerPhone, word,
			testPhraseology.INVITINGInvitationNotificationForInvitingPlayer(wordGuessingPlayerNick),
			testPhraseology.INVITINGInvitationNotificationForInvitedPlayer(wordProvidingPlayerNick));
    }
    
    /** for an ANSWERING_TO_INVITATION word guessing player, send YES to accept the match */
    public void acceptInvitation(String wordGuessingPlayerPhone, String word, String wordGuessingPlayerNick) {
    	HangmanGame game = new HangmanGame(word, 6);
    	String guessedWordSoFar = game.getGuessedWordSoFar();
    	String usedLetters = game.getAttemptedLettersSoFar();
		tc.checkResponse(wordGuessingPlayerPhone, "yes",
			testPhraseology.PLAYINGWordProvidingPlayerStart(guessedWordSoFar, wordGuessingPlayerNick),
			testPhraseology.PLAYINGWordGuessingPlayerStart(guessedWordSoFar, usedLetters));
    }
    
    /** for REGISTERED_USERs, send chat messages */
    public void sendPrivateMessage(String fromPhone, String toNickname, String message) throws SQLException {
    	String fromNickname = userDB.getUserNickname(fromPhone);
		tc.checkResponse(fromPhone, "P " + toNickname + " " + message,
				testPhraseology.PROVOKINGDeliveryNotification(toNickname),
				testPhraseology.PROVOKINGSendMessage(fromNickname, message));
    }
    
    /** for NEW_USERS, register the two players and invite "word guessing player" to play with the provided 'word' */
    public void invitePlayerForAMatch(String wordProvidingPlayerPhone, String wordProvidingPlayerNick, String word,
                                      String wordGuessingPlayerPhone,  String wordGuessingPlayerNick) {

    	// register word providing player
    	registerUser(wordProvidingPlayerPhone, wordProvidingPlayerNick);

    	// register word guessing player
    	registerUser(wordGuessingPlayerPhone, wordGuessingPlayerNick);
    	
    	// invite
    	invitePlayerByNick(wordProvidingPlayerPhone, wordGuessingPlayerNick);

		// provide the word
    	sendWordToBeGuessed(wordProvidingPlayerPhone, wordProvidingPlayerNick, word, wordGuessingPlayerNick);
    }
    
    /** for NEW_USERS, start a match between them */
    public void startAPlayerMatch(String wordProvidingPlayerPhone, String wordProvidingPlayerNick, String word,
                                  String wordGuessingPlayerPhone, String wordGuessingPlayerNick) {
		
    	// invite
    	invitePlayerForAMatch(wordProvidingPlayerPhone, wordProvidingPlayerNick, word, wordGuessingPlayerPhone, wordGuessingPlayerNick);

    	// accept
    	acceptInvitation(wordGuessingPlayerPhone, word, wordGuessingPlayerNick);		
    }
    
    /** for a NEW_USER, start a match with a bot */
    public void startABotMatch(String phone, String nick, String expectedWord) {
		registerUser(phone, nick);
		playWithBot(phone, expectedWord);
    }
	
	
	/*********************
	** USUAL PATH TESTS **
	*********************/
	
	@Test
	public void testUnrecognizedCommandSubtleties() throws SQLException {
		tc.resetDatabases();
		
		// send 'NEW_USER's help on an unknown command, help or any other
		tc.checkResponse("21991234899", "HJKS",        testPhraseology.INFOFallbackNewUsersHelp());
		tc.checkResponse("21991234899", "help",        testPhraseology.INFOFallbackNewUsersHelp());
		tc.checkResponse("21991234899", "list",        testPhraseology.INFOFallbackNewUsersHelp());
		tc.checkResponse("21991234899", "invite",      testPhraseology.INFOFallbackNewUsersHelp());
		tc.checkResponse("21991234899", "p dombot hi", testPhraseology.INFOFallbackNewUsersHelp());
		
		// send 'EXISTING_USER's help likewise
		registerUser("21991234899", "Dom");
		tc.checkResponse("21991234899", "HJKS", testPhraseology.INFOFallbackExistingUsersHelp());

	}
	
	@Test
	public void testGameRestartSubtleties() throws SQLException {
		tc.resetDatabases();
		
		// register some users
		registerUser("21991234899", "Dom");
		registerUser("21998019166", "Paty");
		
		// simulate an application server restart
		// (temporary test, only valid for POSTGRESQL DAL)
		sessionDB.reset();
		
		// test if the users still can play without problems
		invitePlayerByNick("21991234899", "Paty");
	}
	
	@Test
	public void testHelpCommandSubtleties() throws SQLException {
		tc.resetDatabases();
		
		// Asking for help at NEW_USER state should not show the full help
		tc.checkResponse("21991234899", "help", testPhraseology.INFOFallbackNewUsersHelp());
		
		// now, the full help must be issued, then the second help message, and then back again to the first... even if they share the same keyword
		registerUser("21991234899", "DOM");
		tc.checkResponse("21991234899", "help", testPhraseology.INFOFullHelp1());
		tc.checkResponse("21991234899", "help", testPhraseology.INFOFullHelp2());
		tc.checkResponse("21991234899", "help", testPhraseology.INFOFullHelp1());
		tc.checkResponse("21991234899", "help", testPhraseology.INFOFullHelp2());
		
		// já criadas as frases e os comandos, com atualização no arquivo de configurações.
		// falta fazer o comando help2 ser reconhecido por algum estado mas, antes,
		// temos que criar um estado para "mostrando help", onde o help2 será reconhecido
		
		// but, again, an unknown command should issue a special help
		tc.checkResponse("21991234899", "helpame!!", testPhraseology.INFOFallbackExistingUsersHelp());

	}
	
	@Test
	public void testUserRegistrationSubtleties() throws SQLException {
		tc.resetDatabases();
		
		tc.checkResponse("21998019166", "unsubscribe", testPhraseology.UNSUBSCRIBINGUnsubscriptionNotification());
		assertFalse("Even new users should be able to send unsubscribe", userDB.isUserSubscribed("21998019166"));
		
		tc.checkResponse("21998019166", "hangman", testPhraseology.INFOWelcome());
		assertTrue("Just saying hello to the game should register the player", userDB.isUserSubscribed("21998019166"));
		
		tc.checkResponse("21998019167", "help", testPhraseology.INFOFallbackNewUsersHelp());
		assertFalse("Asking for help should not register the player", userDB.isUserSubscribed("21998019167"));

// this is now a useless test since we register the user right on the "NEW_USER" state. Can be deleted after some air time validates the rule
//		tc.checkResponse("21998019167", "play", testPhraseology.PLAYINGWordGuessingPlayerStart("C-------EE", "CE"));
//		assertTrue("When starting a match, the registration should be done", userDB.isUserSubscribed("21998019167"));

		registerUser("21991234899", "dOM");
		assertTrue("After setting a custom the nickname, the player should be registered", userDB.isUserSubscribed("21991234899"));
		
		registerUser("111111", "user");
		invitePlayerByPhone("111111", "222222");
		sendWordToBeGuessed("111111", "user", "guesswhat", "Guest2222");
		acceptInvitation("222222", "guesswhat", "Guest2222");
		assertTrue("After accepting the invitation, the invited player should be registered", userDB.isUserSubscribed("222222"));
		
		String wordProvidingPlayerPhone = "333333";
		String wordProvidingPlayerNick  = "inviter";
		String word                     = "guessagain";
		String wordGuessingPlayerPhone  = "444444";
		String wordGuessingPlayerNick   = "Guest4444";
    	registerUser(wordProvidingPlayerPhone, wordProvidingPlayerNick);
    	invitePlayerByPhone(wordProvidingPlayerPhone, wordGuessingPlayerPhone);
    	sendWordToBeGuessed(wordProvidingPlayerPhone, wordProvidingPlayerNick, word, wordGuessingPlayerNick);
		assertFalse("The invited user must not be registered until he/she accepts the match", userDB.isUserSubscribed("444444"));
		tc.checkResponse(wordGuessingPlayerPhone, "no",
				testPhraseology.INVITINGInvitationRefusalNotificationForInvitingPlayer(wordGuessingPlayerNick),
				testPhraseology.INVITINGInvitationRefusalNotificationForInvitedPlayer(wordProvidingPlayerNick));
		assertFalse("The invited user must definitly not be registered after he/she refused invitation to the game", userDB.isUserSubscribed("444444"));
		
	}
	
	@Test
	public void testEndGameSubtleties() throws SQLException {
		tc.resetDatabases();
		
		// test ending a match with another player
		startAPlayerMatch("111111", "AllOne", "OneMotherFucker", "22222", "AllTwo");
		tc.checkResponse("22222", "end",
			testPhraseology.PLAYINGMatchGiveupNotificationForWordProvidingPlayer("AllTwo"),
			testPhraseology.PLAYINGMatchGiveupNotificationForWordGuessingPlayer("AllOne"));
		// test fall back to 'EXISTING_USER' state, where unknown commands issues the specialized help
		tc.checkResponse("22222", "x", testPhraseology.INFOFallbackExistingUsersHelp());
		
		// test ending a match with a bot
		startABotMatch("21991234899", "DOM", "CHIMPANZEE");
		tc.checkResponse("21991234899", "end", testPhraseology.PLAYINGMatchGiveupNotificationForWordGuessingPlayer("DomBot"));
		// test the fall back to 'EXISTING_USER' state, where only known commands are answered
		tc.checkResponse("21991234899", "x", testPhraseology.INFOFallbackExistingUsersHelp());
	}
	
	@Test
	public void testWordSelectionSubtleties() throws SQLException {
		tc.resetDatabases();
		
		registerUser("21991234899", "donna");
		invitePlayerByPhone("21991234899", "21998019167");
		tc.checkResponse("21991234899", "caca123", testPhraseology.INVITINGNotAGoodWord("CACA123"));	// non letter characters
		tc.checkResponse("21991234899", "coco", testPhraseology.INVITINGNotAGoodWord("COCO"));			// unplayable word
	}
	
	@Test
	public void testExternalUserInvitationPlayingPath() throws SQLException {
		String playerNickName = "HardCodedNick";
		String guestNickname  = "haole";
		
		tc.resetDatabases();
		
		tc.checkResponse("21991234899", "Forca", "HANGMAN: Registration succeeded. Send HELP to 9714 to know the rules and how to play, or simply send PLAY to 9714");
		tc.checkResponse("21991234899", "AJUDA",
			"You can play the HANGMAN game in 2 ways: guessing someone's word or inviting someone to play with your word " +
			"You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number " +
			"Every week, 1 lucky number is selected to win the prize. Send an option to 9714: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help");
		tc.checkResponse("21991234899", "nick HardCodedNick", "HANGMAN: Name registered: HardCodedNick. Send LIST to 9714 to see online players. NICK [NEW NICK] to change your name.");
		tc.checkResponse("21998019167", "Forca", "HANGMAN: Registration succeeded. Send HELP to 9714 to know the rules and how to play, or simply send PLAY to 9714");
		tc.checkResponse("21998019167", "nick haole", "HANGMAN: Name registered: haole. Send LIST to 9714 to see online players. NICK [NEW NICK] to change your name.");
		tc.checkResponse("21991234899", "C", "HANGMAN: Name registered: " + playerNickName + ". Send your friend's phone to 9714 or LIST to see online players. NICK [NEW NICK] to change your name.");
		tc.checkResponse("21991234899", "21998019167", "HANGMAN: Your friend's phone: 21998019167. Think of a word without special digits and send it now to 9714. After the invitation, you'll get a lucky number");
		tc.checkResponse("21991234899", "coconuts",
			guestNickname + " was invited to play with you. while you wait, you can provoke " + guestNickname + " by sending a message to 9714 (0.31+tax) or send SIGNUP to provoke for free how many times you want",
			"HANGMAN: " + playerNickName + " is inviting you for a hangman match. Do you accept? Send YES to 9714 or PROFILE to see " + playerNickName + " information");
		
		// opponent player wants to play and the game starts
		tc.checkResponse("21998019167", "YES", "Game started with haole.\n" +
		                                       "+-+\n" +
		                                       "| \n" +
		                                       "|  \n" +
		                                       "|  \n" +
		                                       "|\n" +
		                                       "====\n" +
		                                       "Send P haole MSG to give him/her clues",
		                                       "+-+\n" +
		                                       "| \n" +
		                                       "|  \n" +
		                                       "|  \n" +
		                                       "|\n" +
		                                       "====\n" +
		                                       "Word: C-C----S\n" +
		                                       "Used: CS\n" +
		                                       "Send a letter, the complete word or END to cancel the game");
		tc.checkResponse("21998019167", "o", "haole guessed letter o\n" +
		                                     "+-+\n" +
		                                     "| \n" +
		                                     "|  \n" +
		                                     "|  \n" +
		                                     "|\n" +
		                                     "====\n" +
		                                     "Word: COCO---S\n" +
		                                     "Used: COS\n" +
		                                     "Send P haole MSG to provoke him/her",
		                                     "+-+\n" +
		                                     "| \n" +
		                                     "|  \n" +
		                                     "|  \n" +
		                                     "|\n" +
		                                     "====\n" +
		                                     "Word: COCO---S\n" +
		                                     "Used: COS\n" +
		                                     "Send a letter, the complete word or END to cancel the game");
		tc.checkResponse("21998019167", "a",
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, false, false, false, false, false, "COCO---S", "a", "ACOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, false, false, false, false, false, "COCO---S", "ACOS"));
		tc.checkResponse("21998019167", "b",
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, true, false, false, false, false, "COCO---S", "b", "ABCOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, true, false, false, false, false, "COCO---S", "ABCOS"));
		tc.checkResponse("21998019167", "c",
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, true, false, false, false, false, "COCO---S", "c", "ABCOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, true, false, false, false, false, "COCO---S", "ABCOS"));
		tc.checkResponse("21998019167", "e",
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, true, true, false, false, false, "COCO---S", "e", "ABCEOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, true, true, false, false, false, "COCO---S", "ABCEOS"));
		tc.checkResponse("21998019167", "f",
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, true, true, true, false, false, "COCO---S", "f", "ABCEFOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, true, true, true, false, false, "COCO---S", "ABCEFOS"));
		tc.checkResponse("21998019167", "g",
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, true, true, true, true, false, "COCO---S", "g", "ABCEFGOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, true, true, true, true, false, "COCO---S", "ABCEFGOS"));
		tc.checkResponse("21998019167", "h",
			"Good one! haole wasn't able to guessed your word! P haole MSG to provoke him/her or INVITE haole for a new match",
			testPhraseology.PLAYINGLosingMessageForWordGuessingPlayer("COCONUTS", playerNickName));
		// TODO in the middle of the game, the word providing player might want to send a provocative message by just typing?
		
		String chatMessage = "now pick one for me!";
		tc.checkResponse("21991234899", "P " + guestNickname + " " + chatMessage,
			testPhraseology.PROVOKINGDeliveryNotification(guestNickname),
			testPhraseology.PROVOKINGSendMessage(playerNickName, chatMessage));
		
		tc.checkResponse("21998019167", "profile haole", "HANGMAN: haole: Subscribed, RJ, 0 lucky numbers. Send SIGNUP to provoke for free or INVITE haole for a match.");
		tc.checkResponse("21998019167", "nick pAtRiCiA", "HANGMAN: Name registered: pAtRiCiA. Send LIST to 9714 to see online players. NICK [NEW NICK] to change your name.");
		// TODO testar extensivamente o comando list em cenário de muitos usuários registrados pois eu recebi uma exception de index out of bounds que ainda não consegui reproduzir
		tc.checkResponse("21998019167", "list", "HardCodedNick(RJ/10). To play, send INVITE [NICK] to 9714; MORE for more players or PROFILE [NICK]");
		
		tc.checkResponse("21998019167", "ranking", "HardCodedNick(RJ/10). To play, send INVITE [NICK] to 9714; MORE for more players or PROFILE [NICK]");
		
		tc.checkResponse("21998019167", "invite HardCodedNick", "HANGMAN: Inviting HardCodedNick. Think of a word without special digits and send it now to 9714. After the invitation, you'll get a lucky number");
		tc.checkResponse("21998019167", "Scriptogram",
			"HardCodedNick was invited to play with you. while you wait, you can provoke HardCodedNick by sending a message to 9714 (0.31+tax) or send SIGNUP to provoke for free how many times you want",
			"HANGMAN: pAtRiCiA is inviting you for a hangman match. Do you accept? Send YES to 9714 or PROFILE to see pAtRiCiA information");
		
		tc.checkResponse("21991234899", "unsubscribe", "You are now unsubscribed from the HANGMAN GAME and will no longer receive invitations to play nor lucky numbers. To join again, send HANGMAN to 9714");
		
		tc.checkResponse("21991234800", "forca", testPhraseology.INFOWelcome());
		tc.checkResponse("21991234800", "forca", testPhraseology.PLAYINGWordGuessingPlayerStart("C-------EE", "CE"));
		tc.checkResponse("21991234800", "a", testPhraseology.PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, "C----A--EE", "ACE"));
		tc.checkResponse("21991234800", "h", testPhraseology.PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, "CH---A--EE", "ACEH"));
		tc.checkResponse("21991234800", "i", testPhraseology.PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, "CHI--A--EE", "ACEHI"));
		tc.checkResponse("21991234800", "m", testPhraseology.PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, "CHIM-A--EE", "ACEHIM"));
		tc.checkResponse("21991234800", "p", testPhraseology.PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, "CHIMPA--EE", "ACEHIMP"));
		tc.checkResponse("21991234800", "n", testPhraseology.PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, "CHIMPAN-EE", "ACEHIMNP"));
		tc.checkResponse("21991234800", "z", testPhraseology.PLAYINGWinningMessageForWordGuessingPlayer("CHIMPANZEE", "xx.xx.xx.xx"));
	}
	
	@Test
	public void testAnswersToInvitation() throws SQLException {
		tc.resetDatabases();
		invitePlayerForAMatch("21991234899", "Dom", "cacatua", "21998019167", "pAtY");
		tc.checkResponse("21998019167", "profile DOM", "HANGMAN: Dom: Subscribed, RJ, 0 lucky numbers. Send SIGNUP to provoke for free or INVITE Dom for a match.");
		tc.checkResponse("21998019167", "no",
			"pAtY refused your invitation to play. Send LIST to 9714 and pick someone else",
			"The invitation to play the Hangman Game made by Dom was refused. Send LIST to 9714 to see online users");
	}
	
	@Test
	public void testProfileCommand() throws SQLException {
		tc.resetDatabases();
		
		// check profile for players
		invitePlayerForAMatch("21991234899", "dOM", "abracadabra", "11998019167", "spATY");
		tc.checkResponse("11998019167", "profile",       "HANGMAN: dOM: Subscribed, RJ, 0 lucky numbers. Send SIGNUP to provoke for free or INVITE dOM for a match.");
		tc.checkResponse("21991234899", "profile SPaty", "HANGMAN: spATY: Subscribed, SP, 0 lucky numbers. Send SIGNUP to provoke for free or INVITE spATY for a match.");
		
		// check bot profile
		tc.checkResponse("11998019167", "profile dombot", "No player with nickname 'dombot' was found. Maybe he/she changed it? Send LIST to 9714 to see online players");
	}
	
	@Test
	public void testCycleThroughBotWords() throws SQLException {
		tc.resetDatabases();

		registerUser("21991234899", "DOM");

		// test cycle through words
		for (String botWord : mutua.hangmansmsgame.config.Configuration.BOT_WORDS) {
			playWithBot("21991234899", botWord);
		}

		// test the recycle
		for (String botWord : mutua.hangmansmsgame.config.Configuration.BOT_WORDS) {
			playWithBot("21991234899", botWord);
		}

	}
	
	@Test
	public void testAllLettersWhenGuessingWords() throws SQLException {
		char[] attemptedLetters = {
			'A', 'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f', 'G', 'g', 'H', 'h', 'I', 'i', 'J', 'j', 'K',
			'k', 'L', 'l', 'M', 'm', 'N', 'n', 'O', 'o', 'P', 'p', 'Q', 'q', 'R', 'r', 'S', 's', 'T', 't', 'U', 'u',
			'V', 'v', 'W', 'w', 'X', 'x', 'Y', 'y', 'Z', 'z',	
		};
		String hardWord = "AZCDEFGHIJKLMNOPQRSTUVWXYB";
		String usedLetters;
		
		tc.resetDatabases();
		
		// test playing with a human letters recognition
		startAPlayerMatch("21991234899", "dom", hardWord, "21998019167", "paty");
		usedLetters = "AB";
		// first, test sending an empty message
		tc.checkResponse("21998019167", "",
		                 testPhraseology.PLAYINGWordProvidingPlayerStatus(false, false, false, false, false, false, "A------------------------B", "", usedLetters, "paty"),
		                 testPhraseology.PLAYINGWordGuessingPlayerStatus (false, false, false, false, false, false, "A------------------------B", usedLetters));
		// now loop through all messages
		for (char letter : attemptedLetters) {
			String sLetter = Character.toString(letter);
			if (usedLetters.indexOf(sLetter.toUpperCase()) == -1) {
				usedLetters += sLetter.toUpperCase();
			}
			String hardWordSoFar = hardWord.replaceAll("[^"+usedLetters+"]", "-");
			if (hardWordSoFar.equals(hardWord)) {
				tc.checkResponse("21998019167", sLetter,
		                         testPhraseology.PLAYINGWinningMessageForWordProvidingPlayer("paty"),
		                         testPhraseology.PLAYINGWinningMessageForWordGuessingPlayer (hardWord, "xx.xx.xx.xx"));
				break;
			} else {
				tc.checkResponse("21998019167", sLetter,
				                 testPhraseology.PLAYINGWordProvidingPlayerStatus(false, false, false, false, false, false, hardWordSoFar, sLetter, usedLetters, "paty"),
				                 testPhraseology.PLAYINGWordGuessingPlayerStatus (false, false, false, false, false, false, hardWordSoFar, usedLetters));
			}
		}
		
		// test playing with a bot letters recognition
		// TODO do the same as for humans... should allow setting bot words on the fly
	}
	
	@Test
	public void testNicknameSubtleties() throws SQLException {

		tc.resetDatabases();
		
		// test default nickname
		registerUser("21991234899", "dOm");
		invitePlayerByPhone("21991234899", "21998019167");
		sendWordToBeGuessed("21991234899", "dOm", "cacatua", "Guest9167");
		
		// test invitation by phone of a registered user
		tc.checkResponse("21998019167", "nick PatY", testPhraseology.PROFILENickRegisteredNotification("PatY"));
		invitePlayerByPhone("21991234899", "21998019167");
		acceptInvitation("21998019167", "cacatua", "PatY");		
	}
	
	@Test
	public void testInviteRegisteredUserByPhone() throws SQLException {
		tc.resetDatabases();
		registerUser("21991234899", "dOm");
		registerUser("21998019167", "PatY");		
		invitePlayerByPhone("21991234899", "21998019167");
		sendWordToBeGuessed("21991234899", "dOm", "cacatua", "PatY");
	}
	
	@Test
	public void testInvitedUserGoesOutOfAnsweringInvitationState() throws SQLException, ClassNotFoundException, SecurityException, NoSuchFieldException {
	
		String commentReferenceField = "mutua.hangmansmsgame.smslogic.CommandDetails.NO_ANSWER";
		
		String className = commentReferenceField.replaceAll("(.*)\\.(.*)", "$1");
		String fieldName = commentReferenceField.replaceAll("(.*)\\.(.*)", "$2");
		
		System.out.println("className='"+className+"'");
		System.out.println("fieldName='"+fieldName+"'");

		Class<?> c = getClass().forName(className);
		Field f = c.getDeclaredField(fieldName);
//		for (Field f : c.getDeclaredFields()) {
			System.out.println("Field '"+f.getName()+"'");
//		}
		
		tc.resetDatabases();
		registerUser("21991234899", "DOm");
		invitePlayerByPhone("21991234899", "21998019167");

		// future feature
//		tc.checkResponse("998019167", "list", "Guest9167 has become busy. However, a new player, DomBot, is available. Play with DomBot? Send YES to 9714",
//		                                      "DOm(RJ/10). To play, send INVITE [NICK] to 9714; MORE for more players or PROFILE [NICK]");
	}
	
	@Test
	public void testPlayingWithMyselfBehavior() throws SQLException {
		String wordProvidingPlayerPhone = "21991234899";
		String wordProvidingPlayerNick  = "Dom";
		String word = "ICannotPlayWithMyself";
		String wordGuessingPlayerPhone  = wordProvidingPlayerPhone;
		String wordGuessingPlayerNick   = wordProvidingPlayerNick;

		// attempting to play with myself by nickname must be like attempting to play with a non existing user
		tc.resetDatabases();
    	registerUser(wordProvidingPlayerPhone, wordProvidingPlayerNick);
    	tc.checkResponse(wordProvidingPlayerPhone, "invite "+wordProvidingPlayerNick, testPhraseology.PROVOKINGNickNotFound(wordProvidingPlayerNick));
		
		// but attempting to play with myself via phone number must go well
		tc.resetDatabases();
    	registerUser(wordProvidingPlayerPhone, wordProvidingPlayerNick);
    	invitePlayerByPhone(wordProvidingPlayerPhone, wordGuessingPlayerPhone);
    	sendWordToBeGuessed(wordProvidingPlayerPhone, wordProvidingPlayerNick, word, wordGuessingPlayerNick);
		acceptInvitation(wordGuessingPlayerPhone, word, wordGuessingPlayerNick);
	}
	
	@Test
	public void testPlayingSubtleties() throws SQLException {
		tc.resetDatabases();
		
		// verify that the user may send the same letter again without loosing chances
		startABotMatch("21991234899", "dom", "CHIMPANZEE");
		tc.checkResponse("21991234899", "c", testPhraseology.PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, "C-------EE", "CE"));
		tc.checkResponse("21991234899", "e", testPhraseology.PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, "C-------EE", "CE"));
		tc.checkResponse("21991234899", "p", testPhraseology.PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, "C---P---EE", "CEP"));
		tc.checkResponse("21991234899", "p", testPhraseology.PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, "C---P---EE", "CEP"));
		tc.checkResponse("21991234899", "k", testPhraseology.PLAYINGWordGuessingPlayerStatus(true, false, false, false, false, false, "C---P---EE", "CEKP"));
		tc.checkResponse("21991234899", "k", testPhraseology.PLAYINGWordGuessingPlayerStatus(true, false, false, false, false, false, "C---P---EE", "CEKP"));
	}
	
	
	/*************************
	** HIGH INTENSITY TESTS **
	*************************/
	
	@Test
	public void testNicknameStress() throws SQLException {
		int numberOfUsers = 104;
		String basePhone  = "11111";
		String baseNickname = "Dom";
		String expectedNick = baseNickname + numberOfUsers;
		String expectedPatyNick = "pAtY";
		
		tc.resetDatabases();

		for (int i=0; i<numberOfUsers; i++) {
	    	tc.checkResponse(basePhone+i, "forca",                testPhraseology.INFOWelcome());
			tc.checkResponse(basePhone+i, "nick " + baseNickname, testPhraseology.PROFILENickRegisteredNotification((i>0)?(baseNickname+i):(baseNickname)));
		}
    	tc.checkResponse("21991234899", "forca",                testPhraseology.INFOWelcome());
		tc.checkResponse("21991234899", "nick " + baseNickname, testPhraseology.PROFILENickRegisteredNotification(expectedNick));
		
		
		registerUser    ("21998019167", "CaCaTuA");
		tc.checkResponse("21998019167", "nick " + expectedPatyNick, testPhraseology.PROFILENickRegisteredNotification(expectedPatyNick));
	}
	
	@Test
	/** This test takes multiple users gradually and randomly through some navigation states */
	public void testMultipleStates() throws SQLException {
		long startWordProvidingPlayerPhone       = 21991223999L;
		String wordProvidingPlayerNicknameSuffix = "TestingDom";
		long startWordGuessingPlayerPhone        = 21998018999L;
		String wordGuessingPlayerNicknameSuffix  = "TestinTricia";
		String lastState = "6";
		String wordProvidingPlayerPhone;
		String wordProvidingPlayerNick;
		String word;
		String wordGuessingPlayerPhone;
		String wordGuessingPlayerNick;
		
		// stateTrackingData := { {wordProvidingPlayerPhone, wordProvidingPlayerNick, word, wordGuessingPlayerPhone, wordGuessingPlayerNick, state}, ... }
		String[][] stateTrackingData = new String[100][6];
		
		// build the state tracking structure
		for (int i=0; i<stateTrackingData.length; i++) {
			wordProvidingPlayerPhone = Long.toString(startWordProvidingPlayerPhone + i);
			wordProvidingPlayerNick  = i + wordProvidingPlayerNicknameSuffix;
			word = new String(new byte[] {(byte)(65+(10+i)%26), (byte)(65+(20+i)%26), (byte)(65+(12+i)%26),
			                              (byte)(65+(11+i)%26), (byte)(65+(17+i)%26), (byte)(65+(5+i)%26),
			                              (byte)(65+(1+i)%26),  (byte)(65+(9+i)%26)});
			wordGuessingPlayerPhone = Long.toString(startWordGuessingPlayerPhone + i);
			wordGuessingPlayerNick  = i + wordGuessingPlayerNicknameSuffix;

			stateTrackingData[i][0] = wordProvidingPlayerPhone;
			stateTrackingData[i][1] = wordProvidingPlayerNick;
			stateTrackingData[i][2] = word;
			stateTrackingData[i][3] = wordGuessingPlayerPhone;
			stateTrackingData[i][4] = wordGuessingPlayerNick;
			stateTrackingData[i][5] = "1";
		}
		
		tc.resetDatabases();

		// keep track of the evolution through states of 'stateTrackingData' and use that to
		// play the game and check that the messages match
		Random random = new Random(System.currentTimeMillis());
		DONE: while (true) {
			int count = stateTrackingData.length;
			int i     = random.nextInt(count);
			while (stateTrackingData[i][5].equals(lastState)) {
				if (count == 0) {
					break DONE;
				}
				i = (i+1) % stateTrackingData.length;
				count--;
			}
			wordProvidingPlayerPhone = stateTrackingData[i][0];
			wordProvidingPlayerNick  = stateTrackingData[i][1];
			word                     = stateTrackingData[i][2];
			wordGuessingPlayerPhone  = stateTrackingData[i][3];
			wordGuessingPlayerNick   = stateTrackingData[i][4];
			int state = Integer.parseInt(stateTrackingData[i][5]);
			switch (state) {
				case 1:
					registerUser(wordProvidingPlayerPhone, wordProvidingPlayerNick);
					registerUser(wordGuessingPlayerPhone,  wordGuessingPlayerNick);
					state = 2;
					break;
				case 2:
					invitePlayerByNick(wordProvidingPlayerPhone, wordGuessingPlayerNick);
					state = 3;
					break;
				case 3:
					sendWordToBeGuessed(wordProvidingPlayerPhone, wordProvidingPlayerNick, word, wordGuessingPlayerNick);
					state = 4;
					break;
				case 4:
					acceptInvitation(wordGuessingPlayerPhone, word, wordGuessingPlayerNick);
					state = 5;
					break;
				case 5:
					sendPrivateMessage(wordProvidingPlayerPhone, wordGuessingPlayerNick, "Did you get that as well?");
					sendPrivateMessage(wordGuessingPlayerPhone, wordProvidingPlayerNick, "Surely I did!");
					state = 6;
					break;
				default:
					throw new RuntimeException("Unknown state " + state);
			}
			stateTrackingData[i][5] = Integer.toString(state);
		}
		
		for (int i=0; i<stateTrackingData.length; i++) {
			assertEquals("Testing algorithm failed", lastState, stateTrackingData[i][5]);
		}
	}
	
	
	/**********************
	** USAGE ERROR TESTS **
	**********************/
	
	@Test
	public void testSpecialCharacterMessages() throws SQLException {
		tc.resetDatabases();
		// TODO forca\n is not being recognized as forca. Update command patterns
		tc.checkResponse("21991234899", "forca\n", testPhraseology.INFOFallbackNewUsersHelp());
		tc.checkResponse("21991234898", "forca\r", testPhraseology.INFOFallbackNewUsersHelp());
		tc.checkResponse("21991234897", "\r\n\r\n", testPhraseology.INFOFallbackNewUsersHelp());
	}
	
}