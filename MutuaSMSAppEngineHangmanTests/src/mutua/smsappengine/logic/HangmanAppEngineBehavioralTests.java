package mutua.smsappengine.logic;

import static mutua.smsappengine.config.HangmanSMSModulesConfiguration.*;
import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.events.postgresql.QueuesPostgreSQLAdapter;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappengine.config.HangmanSMSModulesConfiguration;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;
import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dal.IMatchDB;
import mutua.smsappmodule.dal.INextBotWordsDB;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.ISubscriptionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterChat;
import mutua.smsappmodule.hangmangame.HangmanGame;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsChat;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription;
import mutua.subscriptionengine.TestableSubscriptionAPI;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import adapters.dto.PreparedProcedureInvocationDto;

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
	
	private static QueuesPostgreSQLAdapter qdb;
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


	private static void checkResponse(String phone, String inputText, String... expectedResponsesText) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("InsertNewQueueElement");
		procedure.addParameter("PHONE", phone);
		procedure.addParameter("TEXT",  inputText);
		int moId = (Integer)qdb.invokeScalarProcedure(procedure);
		tc.checkResponse(moId, phone, inputText, expectedResponsesText);
	}
	
	@BeforeClass
	public static void setDefaultHangmanConfigurationParameters() throws SQLException {
		SMSAppModuleConfigurationTests.DEFAULT_MODULE_DAL = SMSAppModuleDALFactory.POSTGRESQL;
		SMSAppModuleConfigurationTests.applyConfiguration();
		HangmanSMSModulesConfiguration.setDefaults(log, subscriptionEngine, subscriptionChannel);
		tc = new SMSAppModuleTestCommons(log, HangmanSMSModulesConfiguration.navigationStates);
		
		// chat database configuration
		//////////////////////////////
		SMSAppModulePostgreSQLAdapterChat.configureChatDatabaseModule("ChatTestMOQueue", "eventId", "text");
		// MutuaEventsAdditionalEventLinks configuration
		QueuesPostgreSQLAdapter.log = log; 
		QueuesPostgreSQLAdapter.HOSTNAME = SMSAppModuleConfigurationTests.POSTGRESQL_CONNECTION_HOSTNAME;
		QueuesPostgreSQLAdapter.PORT     = SMSAppModuleConfigurationTests.POSTGRESQL_CONNECTION_PORT;
		QueuesPostgreSQLAdapter.DATABASE = SMSAppModuleConfigurationTests.POSTGRESQL_CONNECTION_DATABASE_NAME;
		QueuesPostgreSQLAdapter.USER     = SMSAppModuleConfigurationTests.POSTGRESQL_CONNECTION_USER;
		QueuesPostgreSQLAdapter.PASSWORD = SMSAppModuleConfigurationTests.POSTGRESQL_CONNECTION_PASSWORD;
		qdb = QueuesPostgreSQLAdapter.getQueuesDBAdapter(null, "ChatTestMOQueue",
		                                                       "phone  TEXT NOT NULL, text   TEXT NOT NULL, ",
		                                                       "phone, text",
		                                                       "${PHONE}, ${TEXT}");

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
		subscriptionEngine.reset();
	}

	
	/*********************
	** AUXILIAR METHODS **
	*********************/
    
    /** for NEW_USERs, register a new player's 'phone' and give it the provided 'nickname' */
    public void registerUser(String phone, String nickname) throws SQLException {
    	checkResponse(phone, "hangman",          SMSAppModulePhrasingsSubscription.getSuccessfullySubscribed());
		checkResponse(phone, "nick " + nickname, SMSAppModulePhrasingsProfile.getNicknameRegistrationNotification(nickname));
    }
    
//    /** for EXISTING_USERs, send 'play' to start playing with a bot */
//    public void playWithBot(String phone, String expectedWord) {
//    	HangmanGame game = new HangmanGame(expectedWord, 6);
//    	checkResponse(phone, "play", testPhraseology.PLAYINGWordGuessingPlayerStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()));
//    }
    
    /** for EXISTING_USERs, send the 'invite' by nickname command from the word providing to the word guessing already registered players */
    public void invitePlayerByNick(String wordProvidingPlayerPhone, String wordGuessingPlayerNickname) throws SQLException {
		checkResponse(wordProvidingPlayerPhone, "invite " + wordGuessingPlayerNickname, SMSAppModulePhrasingsHangman.getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation(wordGuessingPlayerNickname));		
    }
    
//    /** for EXISTING_USERs, send the 'invite' by phone command from the word providing to the word guessing already registered players */
//    public void invitePlayerByPhone(String wordProvidingPlayerPhone, String wordGuessingPlayerPhone) {
//		checkResponse(wordProvidingPlayerPhone, "invite " + wordGuessingPlayerPhone, SMSAppModulePhrasingsHangman.getAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(wordGuessingPlayerPhone));		
//    }
    
    /** for an ENTERING_MATCH_WORD word providing player, send the word for a match */
    public void sendWordToBeGuessed(String wordProvidingPlayerPhone, String wordProvidingPlayerNickname,
                                    String word, String wordGuessingPlayerNickname) throws SQLException {
		// provide the word
    	checkResponse(wordProvidingPlayerPhone, word,
			SMSAppModulePhrasingsHangman.getInvitationResponseForInvitingPlayer(wordGuessingPlayerNickname),
			SMSAppModulePhrasingsHangman.getInvitationNotificationForInvitedPlayer(wordProvidingPlayerNickname));
    }
    
    /** for an ANSWERING_TO_INVITATION word guessing player, send YES to accept the match */
    public void acceptInvitation(String wordGuessingPlayerPhone, String word, String wordGuessingPlayerNick) throws SQLException {
    	HangmanGame game = new HangmanGame(word, 6);
    	String guessedWordSoFar = game.getGuessedWordSoFar();
    	String usedLetters = game.getAttemptedLettersSoFar();
		checkResponse(wordGuessingPlayerPhone, "yes",
			SMSAppModulePhrasingsHangman.getWordGuessingPlayerMatchStart(guessedWordSoFar, usedLetters),
			SMSAppModulePhrasingsHangman.getWordProvidingPlayerMatchStart(guessedWordSoFar, wordGuessingPlayerNick));
    }
    
    /** for REGISTERED_USERs, send chat messages */
    public void sendPrivateMessage(String fromPhone, String toNickname, String message) throws SQLException {
    	String fromNickname = profileDB.getProfileRecord(userDB.assureUserIsRegistered(fromPhone)).getNickname();
		checkResponse(fromPhone, "P " + toNickname + " " + message,
			SMSAppModulePhrasingsChat.getPrivateMessageDeliveryNotification(toNickname),
			SMSAppModulePhrasingsChat.getPrivateMessage(fromNickname, message));
    }
    
    /** for NEW_USERS, register the two players and invite "word guessing player" to play with the provided 'word' */
    public void invitePlayerForAMatch(String wordProvidingPlayerPhone, String wordProvidingPlayerNick, String word,
                                      String wordGuessingPlayerPhone,  String wordGuessingPlayerNick) throws SQLException {

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
                                  String wordGuessingPlayerPhone, String wordGuessingPlayerNick) throws SQLException {
		
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
		checkResponse("21998019167", "help", "You are at the HANGMAN game. To continue, you must subscribe. Send HANGMAN now to 9714 and compete for prizes. You will be charged at $ every week.");
		assertFalse("User should not have been subscribed on the backend", subscriptionEngine._isUserSubscribed("21998019167", subscriptionChannel));
		
		// user's first message is the double opt-in agreement: register him/her (it is assumed a broadcast message was sent instructing him/her what to reply to subscribe)
		checkResponse("21991234899", "Hangman", "HANGMAN: Registration succeeded. Send HELP to 9714 to know the rules and how to play, or simply send PLAY to 9714");
		assertTrue("User was not subscribed on the backend", subscriptionEngine._isUserSubscribed("21991234899", subscriptionChannel));
		
		// user's first message is unsubscribe -- we must assure he/she is unsubscribed...
		// ... for the user, for some reason, might be subscribed
		subscriptionEngine.subscribeUser("21998019166", subscriptionChannel);
		checkResponse("21998019166", "unsubscribe", "You are now unsubscribed from the HANGMAN GAME and will no longer receive invitations to play nor lucky numbers. To join again, send HANGMAN to 9714");
		assertFalse("User should not be still subscribed on the backend", subscriptionEngine._isUserSubscribed("21998019166", subscriptionChannel));

		// desperate help
		checkResponse("21991234899", "how can I use this stuff??",
		                                "HANGMAN: unknown command. Please send HELP to see the full command set. Some examples: LIST to see online users; P [NICK] [MSG] to send a private message; " +
                                        "INVITE [NICK] to invite a listed player; INVITE [PHONE] to invite a friend of yours; PLAY to play with a random user. Choose an option and send it to 9714");

		// desired help
		checkResponse("21991234899", "help",
			"You can play the HANGMAN game in 2 ways: guessing someone's word or inviting someone to play with your word " +
			"You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number " +
			"Every week, 1 lucky number is selected to win the prize. Send an option to 9714: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help");

		// nickname registration
		String expectedNickname = "HardCodedNick";
		checkResponse("21991234899", "nick " + expectedNickname, "HANGMAN: Name registered: " + expectedNickname + ". Send LIST to 9714 to see online players. NICK [NEW NICK] to change your name.");
		String observedNickname = profileDB.getProfileRecord(userDB.assureUserIsRegistered("21991234899")).getNickname();
		assertEquals("Nickname registration failed", expectedNickname, observedNickname);
		
		// opponent registration
		checkResponse("21998019167", "hangman", "HANGMAN: Registration succeeded. Send HELP to 9714 to know the rules and how to play, or simply send PLAY to 9714");
		checkResponse("21998019167", "nick haole", "HANGMAN: Name registered: haole. Send LIST to 9714 to see online players. NICK [NEW NICK] to change your name.");
		checkResponse("21998019167", "nick pAtRiCiA", "HANGMAN: Name registered: pAtRiCiA. Send LIST to 9714 to see online players. NICK [NEW NICK] to change your name.");
		
		// user listing
//		tc.checkResponse("21998019167", "list", "i want to see the list of users i can play with...");
		
		// invitation
		checkResponse("21998019167", "invite HardCodedNick", "HANGMAN: Inviting HardCodedNick. Think of a word without special digits and send it now to 9714. After the invitation, you'll get a lucky number");
		checkResponse("21998019167", "coconuts",
			"HardCodedNick was invited to play with you. while you wait, you can provoke HardCodedNick by sending a message to 9714 (0.31+tax) or send SIGNUP to provoke for free how many times you want",
			"HANGMAN: pAtRiCiA is inviting you for a hangman match. Do you accept? Send YES or NO to 9714 or P pAtRiCiA [MSG] to send him/her a message");

		// chat
		String expectedChatMessage = "c'mon, man! Lets go for a match!!";
		checkResponse("21998019167", "P HardCodedNick " + expectedChatMessage, 
			"HANGMAN: your message has been delivered to HardCodedNick. What can be the command that I'll suggest now?",
			"pAtRiCiA: " + expectedChatMessage + " - To answer, text P pAtRiCiA [MSG] to 9714"
		);
		expectedChatMessage = "I don't know what is this yet. But, OK... lets try... For me it is easy because I'm already subscribed.";
		checkResponse("21991234899", "P pAtRiCiA " + expectedChatMessage,
			"HANGMAN: your message has been delivered to pAtRiCiA. What can be the command that I'll suggest now?",
			"HardCodedNick: " + expectedChatMessage + " - To answer, text P HardCodedNick [MSG] to 9714"
		);
		
		// back to the invitation... lets play the match!
		checkResponse("21991234899", "YES", "+-+\n" +
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
		checkResponse("21991234899", "o", "+-+\n" +
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
		checkResponse("21991234899", "a",
			SMSAppModulePhrasingsHangman.getWordGuessingPlayerStatus (true, false, false, false, false, false, "COCO---S", "ACOS"),
			SMSAppModulePhrasingsHangman.getWordProvidingPlayerStatus(true, false, false, false, false, false, "COCO---S", "a", "ACOS", expectedNickname));
		checkResponse("21991234899", "nu",
			SMSAppModulePhrasingsHangman.getWordGuessingPlayerStatus (true, false, false, false, false, false, "COCONU-S", "ACNOSU"),
			SMSAppModulePhrasingsHangman.getWordProvidingPlayerStatus(true, false, false, false, false, false, "COCONU-S", "nu", "ACNOSU", expectedNickname));
		checkResponse("21991234899", "xyz",
			SMSAppModulePhrasingsHangman.getWordGuessingPlayerStatus (true, true, true, true, false, false, "COCONU-S", "ACNOSUXYZ"),
			SMSAppModulePhrasingsHangman.getWordProvidingPlayerStatus(true, true, true, true, false, false, "COCONU-S", "xyz", "ACNOSUXYZ", expectedNickname));
		
		// test the winning phrase
		checkResponse("21991234899", "t", "\\0/\n" +
                                             " |\n" +
                                             "/ \\\n" +
                                             "COCONUTS! You got it! Here is your lucky number: xxx.xx.xx.xxx. Send: J to play or A for help",
                                             "HardCodedNick guessed your word! P HardCodedNick [MSG] to provoke him/her or INVITE HardCodedNick for a new match");
		
		// start a new game to test the losing phrase
		invitePlayerByNick("21991234899", "pAtRiCiA");
		sendWordToBeGuessed("21991234899", "HardCodedNick", "Muggles", "pAtRiCiA");
		acceptInvitation("21998019167", "Muggles", "pAtRiCiA");

		// lose the game
		checkResponse("21998019167", "muskratramblesong", "+-+\n" +
                                                             "| x\n" +
                                                             "|/|\\\n" +
                                                             "|/ \\\n" +
                                                             "====\n" +
                                                             "The word was MUGGLES. Now challenge HardCodedNick: send INVITE HardCodedNick to 9714",
                                                             "Good one! pAtRiCiA wasn't able to guessed your word! P pAtRiCiA [MSG] to provoke him/her or INVITE pAtRiCiA for a new match");

		// unsubscribe
		checkResponse("21998019167", "unsubscribe", "You are now unsubscribed from the HANGMAN GAME and will no longer receive invitations to play nor lucky numbers. To join again, send HANGMAN to 9714");
		checkResponse("21991234899", "unsubscribe", "You are now unsubscribed from the HANGMAN GAME and will no longer receive invitations to play nor lucky numbers. To join again, send HANGMAN to 9714");
		assertFalse("User should not be still subscribed on the backend", subscriptionEngine._isUserSubscribed("21998019167", subscriptionChannel));
		assertFalse("User should not be still subscribed on the backend", subscriptionEngine._isUserSubscribed("21991234899", subscriptionChannel));

	}
	
	@Test
	public void testAnswersToInvitation() throws SQLException {
		String invitingPlayerPhone    = "21991234899";
		String invitingPlayerNickname = "Dom";
		String invitedPlayerPhone     = "21998019167";
		String invitedPlayerNickname  = "pAtY";
		String word                   = "cacatua";
		String guessedWordSoFar       = "CACA--A";
		String usedLetters            = "AC";
		
		// usual yes
		resetStates();
		invitePlayerForAMatch(invitingPlayerPhone, invitingPlayerNickname, word, invitedPlayerPhone, invitedPlayerNickname);
		checkResponse(invitedPlayerPhone, "yes",
		              SMSAppModulePhrasingsHangman.getWordGuessingPlayerMatchStart(guessedWordSoFar, usedLetters),
		              SMSAppModulePhrasingsHangman.getWordProvidingPlayerMatchStart(guessedWordSoFar, invitedPlayerNickname));
		
		
		// usual no
		resetStates();
		invitePlayerForAMatch(invitingPlayerPhone, invitingPlayerNickname, "cacatua", invitedPlayerPhone, invitedPlayerNickname);
		checkResponse(invitedPlayerPhone, "no",
		              SMSAppModulePhrasingsHangman.getInvitationRefusalResponseForInvitedPlayer(invitingPlayerNickname),
		              SMSAppModulePhrasingsHangman.getInvitationRefusalNotificationForInvitingPlayer(invitedPlayerNickname));
		
		// some other commands before the no
		resetStates();
		invitePlayerForAMatch(invitingPlayerPhone, invitingPlayerNickname, "cacatua", invitedPlayerPhone, invitedPlayerNickname);
		// send and receive a chat
		sendPrivateMessage(invitedPlayerPhone, invitingPlayerNickname, "Why do you want to play with me?");
		sendPrivateMessage(invitingPlayerPhone, invitedPlayerNickname, "'Cause you're the only one on my test list 8-)");
		// also, see the profile...
		tc.checkResponse("21998019167", "profile DOM", SMSAppModulePhrasingsProfile.getUserProfilePresentation("Dom"));
		// the no
		checkResponse(invitedPlayerPhone, "no",
		              SMSAppModulePhrasingsHangman.getInvitationRefusalResponseForInvitedPlayer(invitingPlayerNickname),
		              SMSAppModulePhrasingsHangman.getInvitationRefusalNotificationForInvitingPlayer(invitedPlayerNickname));

	}
	
	@Test
	public void testProfileCommand() throws SQLException {
		invitePlayerForAMatch("21991234899", "Dom", "cacatua", "21998019167", "pAtY");
		
		// profile on the "playing" state
		checkResponse("21998019167", "profile DOM", "HANGMAN: Dom: Subscribed; Online; RJ. Text INVITE Dom to play a hangman match; P Dom [MSG] to chat; LIST to see online players; P to play with a random user.");
		
		checkResponse("21998019167", "no",
			"The invitation to play the Hangman Game made by Dom was refused. Send LIST to 9714 to see online users",
			"pAtY refused your invitation to play. Send LIST to 9714 and pick someone else");
		
		// profile on the "existing player" state
		// also, profile on the "new user" state? what other commands there?
	}

	
	/**************************************
	** COMMANDS & STATES EXAUSTIVE TESTS **
	**************************************/
	
	@Test
	public void testSubscriptionSubtleties() throws SQLException {

		// unregistered users should not be able to run some commands...
		checkResponse("21991234899", "nick Mario", SMSAppModulePhrasingsSubscription.getDoubleOptinStart());
		
		// even new users should be able to unsubscribe
		checkResponse("21998019167", "unsubscribe", SMSAppModulePhrasingsSubscription.getUserRequestedUnsubscriptionNotification());
		
		// struggle to subscribe
		checkResponse("21991234899", "help",                 SMSAppModulePhrasingsSubscription.getDoubleOptinStart());
		checkResponse("21991234899", "no",                   SMSAppModulePhrasingsSubscription.getDisagreeToSubscribe());
		checkResponse("21991234899", "come again?",          SMSAppModulePhrasingsSubscription.getDoubleOptinStart());
		checkResponse("21991234899", "well, not yet...",     SMSAppModulePhrasingsSubscription.getDoubleOptinStart());
		checkResponse("21991234899", "what is this, again?", SMSAppModulePhrasingsSubscription.getDoubleOptinStart());
		checkResponse("21991234899", "hangman",              SMSAppModulePhrasingsSubscription.getSuccessfullySubscribed());
		// struggle to find one's first steps after subscription
		checkResponse("21991234899", "HJKS", SMSAppModulePhrasingsHelp.getExistingUsersFallbackHelp());
		
		// now, when a user that was registered attempts to play (but he/she has been secretly unsubscribed due to life cycle rules), what happens?
		// he/she must use the game as if their subscription was still valid...
		subscriptionEngine.unsubscribeUser("21991234899", subscriptionChannel);
		checkResponse("21991234899", "nick domJon", SMSAppModulePhrasingsProfile.getNicknameRegistrationNotification("domJon"));
		sendPrivateMessage("21991234899", "domJon", "I believe I would receive this, but... should I?");
		
		// ... only when the unsubscription is formally reported, the game notes the change in the state
		SMSAppModuleCommandsSubscription.unsubscribeUser("21991234899");
		checkResponse("21991234899", "nick ItsMeMario", SMSAppModulePhrasingsSubscription.getDoubleOptinStart());
		
		// test the default nick for newly registered users
		checkResponse("21991234900", "hangman", SMSAppModulePhrasingsSubscription.getSuccessfullySubscribed());
		checkResponse("21991234900", "profile", SMSAppModulePhrasingsProfile.getUserProfilePresentation("Guest4900"));
	}
	
	@Test
	public void testWordSelectionSubtleties() throws SQLException {
		registerUser("21991234899", "dom");
		registerUser("21998019167", "donna");
		invitePlayerByNick("21991234899", "donna");
		checkResponse("21991234899", "caca123", SMSAppModulePhrasingsHangman.getNotAGoodWord("CACA123"));	// non letter characters
		checkResponse("21991234899", "coco", SMSAppModulePhrasingsHangman.getNotAGoodWord("COCO"));			// unplayable word
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
		
		// test playing with a human letters recognition
		startAPlayerMatch("21991234899", "dom", hardWord, "21998019167", "paty");
		usedLetters = "AB";
		// first, test sending an empty message
		checkResponse("21998019167", "", SMSAppModulePhrasingsHangman.getGuessingWordHelp());
		// now loop through all messages
		for (char letter : attemptedLetters) {
			String sLetter = Character.toString(letter);
			if (usedLetters.indexOf(sLetter.toUpperCase()) == -1) {
				usedLetters += sLetter.toUpperCase();
			}
			String hardWordSoFar = hardWord.replaceAll("[^"+usedLetters+"]", "-");
			if (hardWordSoFar.equals(hardWord)) {
				checkResponse("21998019167", sLetter,				                 
		                      SMSAppModulePhrasingsHangman.getWinningMessageForWordGuessingPlayer (hardWord, "xx.xx.xx.xx"),
		                      SMSAppModulePhrasingsHangman.getWinningMessageForWordProvidingPlayer("paty"));
				break;
			} else {
				checkResponse("21998019167", sLetter,
				              SMSAppModulePhrasingsHangman.getWordGuessingPlayerStatus (false, false, false, false, false, false, hardWordSoFar, usedLetters),
				              SMSAppModulePhrasingsHangman.getWordProvidingPlayerStatus(false, false, false, false, false, false, hardWordSoFar, sLetter, usedLetters, "paty"));
			}
		}
		
		// test playing with a bot letters recognition
		// TODO do the same as for humans... should allow setting bot words on the fly
	}


}

/* test mappings:
 * =============
 * leggend: --> -- new test name
 *          x   -- test doesn't make sense on the new platform
 *          NA  -- functionality (and test) not not configured on the new hangman
 *          OK  -- test implemented with same name
 * 
 * testUnrecognizedCommandSubtleties --> testSubscriptionSubtleties
 * testGameRestartSubtleties         --> x
 * testHelpCommandSubtleties         --> NA
 * testUserRegistrationSubtleties    --> testSubscriptionSubtleties * falta fazer testes para convite por telefone
 * testWordSelectionSubtleties       --> testWordSelectionSubtleties
 * testAllLettersWhenGuessingWords   --> testAllLettersWhenGuessingWords
 * 
 */
