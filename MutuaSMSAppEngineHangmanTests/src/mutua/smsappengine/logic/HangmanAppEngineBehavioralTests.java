package mutua.smsappengine.logic;

import static mutua.smsappengine.config.HangmanSMSModulesConfiguration.*;
import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappengine.config.HangmanSMSModulesConfiguration;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dal.IMatchDB;
import mutua.smsappmodule.dal.INextBotWordsDB;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.ISubscriptionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.hangmangame.HangmanGame;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsChat;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription;
import mutua.subscriptionengine.TestableSubscriptionAPI;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** <pre>
 * HangmanAppEngineBehavioralTests.java
 * ====================================
 * (created by luiz, Sep 15, 2015)
 *
 * Tests the integration of the "Hangman" SMS App Module (and the modules it depends on) with the SMS Processor logic,
 * to produce an unified and consistent SMS Application
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HangmanAppEngineBehavioralTests {
	
	// log
	private static Instrumentation<DefaultInstrumentationProperties, String> log = new Instrumentation<DefaultInstrumentationProperties, String>(
		"HangmanAppEngineBehavioralTests", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null);

	private static SMSAppModuleTestCommons tc;
	
	private IUserDB         userDB         = DEFAULT_MODULE_DAL.getUserDB();
	private ISessionDB      sessionDB      = DEFAULT_MODULE_DAL.getSessionDB();
	private ISubscriptionDB subscriptionDB = SUBSCRIPTION_MODULE_DAL.getSubscriptionDB();
	private IProfileDB      profileDB      = PROFILE_MODULE_DAL.getProfileDB();
	private IChatDB         chatDB         = CHAT_MODULE_DAL.getChatDB();
	private IMatchDB        matchDB        = HANGMAN_MODULE_DAL.getMatchDB();
	private INextBotWordsDB nextBotWordsDB = HANGMAN_MODULE_DAL.getNextBotWordsDB();
	
	private static TestableSubscriptionAPI subscriptionEngine  = new TestableSubscriptionAPI(log);
	private static String                  subscriptionChannel = "behavioralTests";

	
	@BeforeClass
	public static void setDefaultHangmanConfigurationParameters() {
		tc = new SMSAppModuleTestCommons(log, HangmanSMSModulesConfiguration.navigationStates);
		HangmanSMSModulesConfiguration.setDefaults(log, subscriptionEngine, subscriptionChannel);
	}
	
	@Before
	public void resetStates() throws SQLException {
		userDB.reset();
		sessionDB.reset();
		subscriptionDB.reset();
		profileDB.reset();
		chatDB.reset();
		matchDB.reset();
		nextBotWordsDB.reset();
	}

	
	/*********************
	** AUXILIAR METHODS **
	*********************/
    
    /** for NEW_USERs, register a new player's 'phone' and give it the provided 'nickname' */
    public void registerUser(String phone, String nickname) {
    	tc.checkResponse(phone, "forca",            SMSAppModulePhrasingsSubscription.getSuccessfullySubscribed());
		tc.checkResponse(phone, "nick " + nickname, SMSAppModulePhrasingsProfile.getNicknameRegistrationNotification(nickname));
    }
    
//    /** for EXISTING_USERs, send 'play' to start playing with a bot */
//    public void playWithBot(String phone, String expectedWord) {
//    	HangmanGame game = new HangmanGame(expectedWord, 6);
//    	tc.checkResponse(phone, "play", testPhraseology.PLAYINGWordGuessingPlayerStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()));
//    }
    
    /** for EXISTING_USERs, send the 'invite' by nickname command from the word providing to the word guessing already registered players */
    public void invitePlayerByNick(String wordProvidingPlayerPhone, String wordGuessingPlayerNickname) {
		tc.checkResponse(wordProvidingPlayerPhone, "invite " + wordGuessingPlayerNickname, SMSAppModulePhrasingsHangman.getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation(wordGuessingPlayerNickname));		
    }
    
//    /** for EXISTING_USERs, send the 'invite' by phone command from the word providing to the word guessing already registered players */
//    public void invitePlayerByPhone(String wordProvidingPlayerPhone, String wordGuessingPlayerPhone) {
//		tc.checkResponse(wordProvidingPlayerPhone, "invite " + wordGuessingPlayerPhone, SMSAppModulePhrasingsHangman.getAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(wordGuessingPlayerPhone));		
//    }
    
    /** for an ENTERING_MATCH_WORD word providing player, send the word for a match */
    public void sendWordToBeGuessed(String wordProvidingPlayerPhone, String wordProvidingPlayerNickname,
                                    String word, String wordGuessingPlayerNickname) {
		// provide the word
    	tc.checkResponse(wordProvidingPlayerPhone, word,
			SMSAppModulePhrasingsHangman.getInvitationNotificationForInvitingPlayer(wordGuessingPlayerNickname),
			SMSAppModulePhrasingsHangman.getInvitationNotificationForInvitedPlayer(wordProvidingPlayerNickname));
    }
    
    /** for an ANSWERING_TO_INVITATION word guessing player, send YES to accept the match */
    public void acceptInvitation(String wordGuessingPlayerPhone, String word, String wordGuessingPlayerNick) {
    	HangmanGame game = new HangmanGame(word, 6);
    	String guessedWordSoFar = game.getGuessedWordSoFar();
    	String usedLetters = game.getAttemptedLettersSoFar();
		tc.checkResponse(wordGuessingPlayerPhone, "yes",
			SMSAppModulePhrasingsHangman.getWordGuessingPlayerMatchStart(guessedWordSoFar, usedLetters),
			SMSAppModulePhrasingsHangman.getWordProvidingPlayerMatchStart(guessedWordSoFar, wordGuessingPlayerNick));
    }
    
    /** for REGISTERED_USERs, send chat messages */
    public void sendPrivateMessage(String fromPhone, String toNickname, String message) throws SQLException {
    	String fromNickname = profileDB.getProfileRecord(userDB.assureUserIsRegistered(fromPhone)).getNickname();
		tc.checkResponse(fromPhone, "P " + toNickname + " " + message,
			SMSAppModulePhrasingsChat.getPrivateMessage(fromNickname, message),
			SMSAppModulePhrasingsChat.getPrivateMessageDeliveryNotification(toNickname));
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
    
//    /** for a NEW_USER, start a match with a bot */
//    public void startABotMatch(String phone, String nick, String expectedWord) {
//		registerUser(phone, nick);
//		playWithBot(phone, expectedWord);
//    }
	
	
	/*******************************
	** EXPECTED USAGE PATHS TESTS **
	*******************************/
    
	@Test
	public void testDefaultPhrasings() throws SQLException {

		// user's first message is an invalid command -- restart the double opt-in from scratch
		tc.checkResponse("21998019167", "help", "You are at the HANGMAN game. To continue, you must subscribe. Send HANGMAN now to 9714 and compete for prizes. You will be charged at $ every week.");
		assertFalse("User should not have been subscribed on the backend", subscriptionEngine._isUserSubscribed("21998019167", subscriptionChannel));
		
		// user's first message is the double opt-in agreement: register him/her (it is assumed a broadcast message was sent instructing him/her what to reply to subscribe)
		tc.checkResponse("21991234899", "Hangman", "HANGMAN: Registration succeeded. Send HELP to 9714 to know the rules and how to play, or simply send PLAY to 9714");
		assertTrue("User was not subscribed on the backend", subscriptionEngine._isUserSubscribed("21991234899", subscriptionChannel));
		
		// user's first message is unsubscribe -- we must assure he/she is unsubscribed...
		// ... for the user, for some reason, might be subscribed
		subscriptionEngine.subscribeUser("21998019166", subscriptionChannel);
		tc.checkResponse("21998019166", "unsubscribe", "You are now unsubscribed from the HANGMAN GAME and will no longer receive invitations to play nor lucky numbers. To join again, send HANGMAN to 9714");
		assertFalse("User should not be still subscribed on the backend", subscriptionEngine._isUserSubscribed("21998019166", subscriptionChannel));
		
		// help
		tc.checkResponse("21991234899", "help",
			"You can play the HANGMAN game in 2 ways: guessing someone's word or inviting someone to play with your word " +
			"You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number " +
			"Every week, 1 lucky number is selected to win the prize. Send an option to 9714: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help");

		// nickname registration
		String expectedNickname = "HardCodedNick";
		tc.checkResponse("21991234899", "nick " + expectedNickname, "HANGMAN: Name registered: " + expectedNickname + ". Send LIST to 9714 to see online players. NICK [NEW NICK] to change your name.");
		String observedNickname = profileDB.getProfileRecord(userDB.assureUserIsRegistered("21991234899")).getNickname();
		assertEquals("Nickname registration failed", expectedNickname, observedNickname);
		
		// opponent registration
		tc.checkResponse("21998019167", "hangman", "HANGMAN: Registration succeeded. Send HELP to 9714 to know the rules and how to play, or simply send PLAY to 9714");
		tc.checkResponse("21998019167", "nick haole", "HANGMAN: Name registered: haole. Send LIST to 9714 to see online players. NICK [NEW NICK] to change your name.");
		tc.checkResponse("21998019167", "nick pAtRiCiA", "HANGMAN: Name registered: pAtRiCiA. Send LIST to 9714 to see online players. NICK [NEW NICK] to change your name.");
		
		// user listing
//		tc.checkResponse("21998019167", "list", "i want to see the list of users i can play with...");
		
		// invitation
		tc.checkResponse("21998019167", "invite HardCodedNick", "HANGMAN: Inviting HardCodedNick. Think of a word without special digits and send it now to 9714. After the invitation, you'll get a lucky number");
		tc.checkResponse("21998019167", "coconuts",
			"HardCodedNick was invited to play with you. while you wait, you can provoke HardCodedNick by sending a message to 9714 (0.31+tax) or send SIGNUP to provoke for free how many times you want",
			"HANGMAN: pAtRiCiA is inviting you for a hangman match. Do you accept? Send YES to 9714 or PROFILE to see pAtRiCiA information");

		// chat
		String expectedChatMessage = "c'mon, man! Lets go for a match!!";
		tc.checkResponse("21998019167", "P HardCodedNick " + expectedChatMessage, 
			"HANGMAN: your message has been delivered to HardCodedNick. What can be the command that I'll suggest now?",
			"pAtRiCiA: " + expectedChatMessage + " - To answer, text P pAtRiCiA [MSG] to 9714"
		);
		expectedChatMessage = "I don't know what is this yet. But, OK... lets try... For me it is easy because I'm already subscribed.";
		tc.checkResponse("21991234899", "P pAtRiCiA " + expectedChatMessage,
			"HANGMAN: your message has been delivered to pAtRiCiA. What can be the command that I'll suggest now?",
			"HardCodedNick: " + expectedChatMessage + " - To answer, text P HardCodedNick [MSG] to 9714"
		);
		
		// back to the invitation... lets play the match!
		tc.checkResponse("21991234899", "YES", "+-+\n" +
		                                       "| \n" +
		                                       "|  \n" +
		                                       "|  \n" +
		                                       "|\n" +
		                                       "====\n" +
		                                       "Word: C-C----S\n" +
		                                       "Used: CS\n" +
		                                       "Send a letter, the complete word or END to cancel the game",
		                                       "Game started with HardCodedNick.\n" +
		                                       "+-+\n" +
		                                       "| \n" +
		                                       "|  \n" +
		                                       "|  \n" +
		                                       "|\n" +
		                                       "====\n" +
		                                       "Send P HardCodedNick [MSG] to give him/her clues");
		tc.checkResponse("21991234899", "o", "+-+\n" +
		                                     "| \n" +
		                                     "|  \n" +
		                                     "|  \n" +
		                                     "|\n" +
		                                     "====\n" +
		                                     "Word: COCO---S\n" +
		                                     "Used: COS\n" +
		                                     "Send a letter, the complete word or END to cancel the game",
		                                     "HardCodedNick guessed letter o\n" +
		                                     "+-+\n" +
		                                     "| \n" +
		                                     "|  \n" +
		                                     "|  \n" +
		                                     "|\n" +
		                                     "====\n" +
		                                     "Word: COCO---S\n" +
		                                     "Used: COS\n" +
		                                     "Send P HardCodedNick [MSG] to provoke him/her");
		
		// continue playing, with eventually some wrong letters, until HardCodedNick wins
		tc.checkResponse("21991234899", "a",
			SMSAppModulePhrasingsHangman.getWordGuessingPlayerStatus (true, false, false, false, false, false, "COCO---S", "ACOS"),
			SMSAppModulePhrasingsHangman.getWordProvidingPlayerStatus(true, false, false, false, false, false, "COCO---S", "a", "ACOS", expectedNickname));
		tc.checkResponse("21991234899", "nu",
			SMSAppModulePhrasingsHangman.getWordGuessingPlayerStatus (true, false, false, false, false, false, "COCONU-S", "ACNOSU"),
			SMSAppModulePhrasingsHangman.getWordProvidingPlayerStatus(true, false, false, false, false, false, "COCONU-S", "nu", "ACNOSU", expectedNickname));
		tc.checkResponse("21991234899", "xyz",
			SMSAppModulePhrasingsHangman.getWordGuessingPlayerStatus (true, true, true, true, false, false, "COCONU-S", "ACNOSUXYZ"),
			SMSAppModulePhrasingsHangman.getWordProvidingPlayerStatus(true, true, true, true, false, false, "COCONU-S", "xyz", "ACNOSUXYZ", expectedNickname));
		
		// test the winning phrase
		tc.checkResponse("21991234899", "t", "\\0/\n" +
                                             " |\n" +
                                             "/ \\\n" +
                                             "COCONUTS! You got it! Here is your lucky number: xxx.xx.xx.xxx. Send: J to play or A for help",
                                             "HardCodedNick guessed your word! P HardCodedNick [MSG] to provoke him/her or INVITE HardCodedNick for a new match");
		
		// start a new game to test the losing phrase
		invitePlayerByNick("21991234899", "pAtRiCiA");
		sendWordToBeGuessed("21991234899", "HardCodedNick", "Muggles", "pAtRiCiA");
		acceptInvitation("21998019167", "Muggles", "pAtRiCiA");

		// lose the game
		tc.checkResponse("21998019167", "muskratramblesong", "+-+\n" +
                                                             "| x\n" +
                                                             "|/|\\\n" +
                                                             "|/ \\\n" +
                                                             "====\n" +
                                                             "The word was MUGGLES. Now challenge HardCodedNick: send INVITE HardCodedNick to 9714",
                                                             "Good one! pAtRiCiA wasn't able to guessed your word! P pAtRiCiA [MSG] to provoke him/her or INVITE pAtRiCiA for a new match");

	}

}
