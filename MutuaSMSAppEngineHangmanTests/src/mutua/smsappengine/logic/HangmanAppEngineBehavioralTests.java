package mutua.smsappengine.logic;

import static org.junit.Assert.*;

import java.sql.SQLException;

import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp.NavigationStatesNamesHelp.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription.NavigationStatesNamesSubscription.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.NavigationStatesNamesProfile.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat.NavigationStatesNamesChat.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman.NavigationStatesNamesHangman.*;
import mutua.icc.configuration.ConfigurationManager;
import mutua.icc.instrumentation.DefaultInstrumentationEvents;
import mutua.icc.instrumentation.InstrumentableEvent;
import mutua.icc.instrumentation.Instrumentation;
import mutua.imi.IndirectMethodInvocationInfo;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dal.IMatchDB;
import mutua.smsappmodule.dal.INextBotWordsDB;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.ISubscriptionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.hangmangame.HangmanGame;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.subscriptionengine.TestableSubscriptionAPI;

import org.junit.Before;
import org.junit.Test;

import config.InstantVASInstanceConfiguration;
import instantvas.smsengine.producersandconsumers.EInstantVASEvents;

/** <pre>
 * HangmanAppEngineBehavioralTests.java
 * ====================================
 * (created by luiz, Sep 15, 2015)
 *
 * Tests the integration of the "Hangman" SMS App Module (and the modules it depends on) with the SMS Processor logic,
 * to produce an unified and consistent SMS Application
 *
 * @version $Id$
 * @author luiz
 */

public class HangmanAppEngineBehavioralTests {
	
	private static SMSAppModuleTestCommons tc;
	
	private static InstantVASInstanceConfiguration ivac;
	
	// db
	private static IUserDB         userDB;
	private static ISessionDB      sessionDB;
	private static ISubscriptionDB subscriptionDB;
	private static IProfileDB      profileDB;
	private static IChatDB         chatDB;
	private static IMatchDB        matchDB;
	private static INextBotWordsDB nextBotWordsDB;
	
	private static TestableSubscriptionAPI subscriptionEngine;
	
	public HangmanAppEngineBehavioralTests() throws IllegalArgumentException, SecurityException, SQLException, IllegalAccessException, NoSuchFieldException {
		
		InstantVASInstanceConfiguration.setHangmanTestDefaults();
		ivac = new InstantVASInstanceConfiguration();
		
		subscriptionEngine  = (TestableSubscriptionAPI)ivac.subscriptionEngine;
		
		userDB         = ivac.baseModuleDAL.getUserDB();
		sessionDB      = ivac.baseModuleDAL.getSessionDB();
		subscriptionDB = ivac.subscriptionDAL.getSubscriptionDB();
		profileDB      = ivac.profileModuleDAL.getProfileDB();
		chatDB         = ivac.chatModuleDAL.getChatDB();
		matchDB        = ivac.hangmanModuleDAL.getMatchDB();
		nextBotWordsDB = ivac.hangmanModuleDAL.getNextBotWordsDB();
		
		tc = new SMSAppModuleTestCommons(ivac.baseModuleDAL, ivac.modulesNavigationStates, ivac.modulesCommandProcessors);
	}

	/** @see SMSAppModuleTestCommons#checkResponse(String, String, String...) */
	private void checkResponse(String phone, String inputText, String... expectedResponsesText) throws SQLException {
		IncomingSMSDto mo = new IncomingSMSDto(-1, phone, inputText, ESMSInParserCarrier.TEST_CARRIER, "1234");
		int moId = ivac.MOpcLink.reportConsumableEvent(new IndirectMethodInvocationInfo<EInstantVASEvents>(EInstantVASEvents.MO_ARRIVED, mo));
		tc.checkResponse(moId, phone, inputText, expectedResponsesText);
	}
	
	private long navigationNewUserPhone = 21997559595L;
	/** @see SMSAppModuleTestCommons#navigateNewUserTo(String, String, Object[][]) */
	private String navigateNewUserTo(String targetNavigationState, String... moTexts) throws SQLException {
		String phone = Long.toString(navigationNewUserPhone++);
		// put the MOs on the queue
		Object[][] moIdsAndTexts = new Object[moTexts.length][2];
		for (int i=0; i<moTexts.length; i++) {
			IncomingSMSDto mo = new IncomingSMSDto(-1, phone, moTexts[i], ESMSInParserCarrier.TEST_CARRIER, "1234");
			moIdsAndTexts[i][0] = ivac.MOpcLink.reportConsumableEvent(new IndirectMethodInvocationInfo<EInstantVASEvents>(EInstantVASEvents.MO_ARRIVED, mo));
			moIdsAndTexts[i][1] = moTexts[i];
		}
		// check
		tc.navigateNewUserTo(phone, targetNavigationState, moIdsAndTexts);
		return phone;
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
    	checkResponse(phone, "hangman",          ivac.subscriptionPhrasings.getSuccessfullySubscribed());
		checkResponse(phone, "nick " + nickname, ivac.profilePhrasings.getNicknameRegistrationNotification(nickname));
    }
    
    /** for EXISTING_USERs, send 'play' to start playing with a bot */
    public void playWithBot(String phone, String expectedWord) throws SQLException {
    	registerUser(ivac.BOT_PHONE_NUMBERS[0], "DomBot");		// bots don't have a default nickname, so we now set it
    	HangmanGame game = new HangmanGame(expectedWord, 6);
    	checkResponse(phone, "play", ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar(), "DomBot"));
    }
    
    /** for EXISTING_USERs, send the 'invite' by nickname command from the word providing to the word guessing already registered players */
    public void invitePlayerByNick(String wordProvidingPlayerPhone, String wordGuessingPlayerNickname) throws SQLException {
		checkResponse(wordProvidingPlayerPhone, "invite " + wordGuessingPlayerNickname, ivac.hangmanPhrasings.getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation(wordGuessingPlayerNickname));		
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
			ivac.hangmanPhrasings.getInvitationResponseForInvitingPlayer(wordGuessingPlayerNickname),
			ivac.hangmanPhrasings.getInvitationNotificationForInvitedPlayer(wordProvidingPlayerNickname));
    }
    
    /** for an ANSWERING_TO_INVITATION word guessing player, send YES to accept the match */
    public void acceptInvitation(String wordGuessingPlayerPhone, String word, String wordGuessingPlayerNick, String wordProvidingPlayerNickname) throws SQLException {
    	HangmanGame game = new HangmanGame(word, 6);
    	String guessedWordSoFar = game.getGuessedWordSoFar();
    	String usedLetters = game.getAttemptedLettersSoFar();
		checkResponse(wordGuessingPlayerPhone, "yes",
			ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart(guessedWordSoFar, usedLetters, wordProvidingPlayerNickname),
			ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart(guessedWordSoFar, wordGuessingPlayerNick));
    }
    
    /** for REGISTERED_USERs, send chat messages */
    public void sendPrivateMessage(String fromPhone, String toNickname, String message) throws SQLException {
    	String fromNickname = profileDB.getProfileRecord(userDB.assureUserIsRegistered(fromPhone)).getNickname();
		checkResponse(fromPhone, "P " + toNickname + " " + message,
			ivac.chatPhrasings.getPrivateMessageDeliveryNotification(toNickname),
			ivac.chatPhrasings.getPrivateMessage(fromNickname, message));
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
    public void startAPlayerMatch(String wordProvidingPlayerPhone, String wordProvidingPlayerNickname, String word,
                                  String wordGuessingPlayerPhone, String wordGuessingPlayerNickname) throws SQLException {
		
    	// invite
    	invitePlayerForAMatch(wordProvidingPlayerPhone, wordProvidingPlayerNickname, word, wordGuessingPlayerPhone, wordGuessingPlayerNickname);

    	// accept
    	acceptInvitation(wordGuessingPlayerPhone, word, wordGuessingPlayerNickname, wordProvidingPlayerNickname);		
    }
    
    /** for a NEW_USER, start a match with a bot */
    public void startABotMatch(String phone, String nick, String expectedWord) throws SQLException {
		registerUser(phone, nick);
		playWithBot(phone, expectedWord);
    }
    
	/*******************************
	** EXPECTED USAGE PATHS TESTS **
	*******************************/
    
    @Test
    public void testConfiguration() throws IllegalArgumentException, IllegalAccessException {
		ConfigurationManager cm = new ConfigurationManager(InstantVASInstanceConfiguration.class);
		String serializedFields = cm.serializeConfigurableClasses();
		System.out.println(serializedFields);
		cm.deserializeConfigurableClasses(serializedFields);
    }
    
	@Test
	public void testDefaultPhrasings() throws SQLException {

		// user's first message is an invalid command -- restart the double opt-in from scratch
		checkResponse("21998019167", "help", "Hi! You are at the HANGMAN game!! To play and meet new friends, you need to be a subscriber. Please answer to this message with the word HANGMAN to start. Only $0.99 per week. Have fun!");
		assertFalse("User should not have been subscribed on the backend", subscriptionEngine._isUserSubscribed("21998019167"));
		
		// user's first message is the double opt-in agreement: register him/her (it is assumed a broadcast message was sent instructing him/her what to reply to subscribe)
		checkResponse("21991234899", "Hangman", "Hi! You are at the HANGMAN game!! To play with a random player, text PLAY; To register a nickname and be able to chat, text NICK <your name>; To play or chat with a specific member, text LIST to see the list of online players. You can always send HELP to see the rules and other commands.");
		assertTrue("User was not subscribed on the backend", subscriptionEngine._isUserSubscribed("21991234899"));
		
		// user's first message is unsubscribe -- we must assure he/she is unsubscribed...
		// ... for the user, for some reason, might be subscribed
		subscriptionEngine.subscribeUser("21998019166");
		checkResponse("21998019166", "unsubscribe", "OK, you asked to leave, so you're out - you won't be able to play the HANGMAN anymore, nor receive chat or game messages, nor lucky numbers to win prizes... Changed your mind? Nice! Text HANGMAN to 993 - $0.99 a week.");
		assertFalse("User should not be still subscribed on the backend", subscriptionEngine._isUserSubscribed("21998019166"));

		// desperate help
		checkResponse("21991234899", "how can I use this stuff??",
		                             "HANGMAN: Invalid command. Text HELP to see all available commands! a tip: Send PLAY to start a match with a random player.");

		// desired help
		checkResponse("21991234899", "help",
			"Guess the right words using the correct letters. The ones who don't, go straight to the gallows. Now, the commands: PLAY - start " +
			"a match with the robot; LIST - find online palyers to chat or invite for a match; INVITE <nickname> - invite someone to play; " +
			"NICK <your name> - create your nick. M <nick> <message> to chat. There is More... text HELP again...");

		// additional help
		checkResponse("21991234899", "help",
			"This game is for subscribers of the HANGMAN game. $0.99 every week. All messages are free. On every subscription renewal, you'll get a lucky " +
			"number to compete for prizes! See + http://www.canaispremiados.com.  Got tired of all of it? Send LEAVE. When you want to come back, just " +
			"text HANGMAN to 993.");

		// nickname registration
		String expectedNickname = "HardCodedNick";
		checkResponse("21991234899", "nick " + expectedNickname, "HANGMAN: Your nickname: " + expectedNickname + ". Text LIST to see online players; NICK [NEW NICK] to change your name again.");
		String observedNickname = profileDB.getProfileRecord(userDB.assureUserIsRegistered("21991234899")).getNickname();
		assertEquals("Nickname registration failed", expectedNickname, observedNickname);
		
		// opponent registration
		checkResponse("21998019167", "hangman",       "Hi! You are at the HANGMAN game!! To play with a random player, text PLAY; To register a nickname and be able to chat, text NICK <your name>; To play or chat with a specific member, text LIST to see the list of online players. You can always send HELP to see the rules and other commands.");
		checkResponse("21998019167", "nick haole",    "HANGMAN: Your nickname: haole. Text LIST to see online players; NICK [NEW NICK] to change your name again.");
		checkResponse("21998019167", "nick pAtRiCiA", "HANGMAN: Your nickname: pAtRiCiA. Text LIST to see online players; NICK [NEW NICK] to change your name again.");
		
		checkResponse("21991234899", "play",          "+-+\n| \n|  \n|  \n|\n====\nWord: C-------EE\nUsed: CE\nAnswer with your first letter, the complete word or ask for cues with M Guest4899 [MSG]");
		
		// user listing -- no user is in a listable state...
		tc.checkResponse("21998019167", "list", "There are no more online players available. Text PLAY to start a game or LIST to query online users again. Text M [nickname] and a message to chat with someone");
		
		// invitation
		checkResponse("21998019167", "invite HardCodedNick", "HANGMAN: Inviting HardCodedNick. Think of a word without special digits and send it now to 993. The most rare words work better for you to win!");
		checkResponse("21998019167", "coconuts",
			"HardCodedNick was invited to play with you. Wait for the answer and good luck!",
			"HANGMAN: pAtRiCiA is inviting you for a match. Do you accept? Text YES or NO. You may also text M pAtRiCiA [MSG] to send him/her a message.");

		// chat
		String expectedChatMessage = "c'mon, man! Lets go for a match!!";
		checkResponse("21998019167", "P HardCodedNick " + expectedChatMessage, 
			"HANGMAN: your message has been delivered to HardCodedNick. While you wait for the answer, you may LIST online players",
			"pAtRiCiA: " + expectedChatMessage + " - Answer with M pAtRiCiA [MSG]");
		
		expectedChatMessage = "I don't know what is this yet. But, OK... lets try... For me it is easy because I'm already subscribed.";
		checkResponse("21991234899", "M pAtRiCiA " + expectedChatMessage,
			"HANGMAN: your message has been delivered to pAtRiCiA. While you wait for the answer, you may LIST online players",
			"HardCodedNick: " + expectedChatMessage + " - Answer with M HardCodedNick [MSG]");

		// back to the invitation... lets play the match!
		checkResponse("21991234899", "YES", "+-+\n" +
		                                       "| \n" +
		                                       "|  \n" +
		                                       "|  \n" +
		                                       "|\n" +
		                                       "====\n" +
		                                       "Word: C-C----S\n" +
		                                       "Used: CS\n" +
		                                       "Answer with your first letter, the complete word or ask for cues with M pAtRiCiA [MSG]",
		                                       "Game started with HardCodedNick.\n" +
		                                       "+-+\n" +
		                                       "| \n" +
		                                       "|  \n" +
		                                       "|  \n" +
		                                       "|\n" +
		                                       "====\n" +
		                                       "Is your word really a hard one? We'll see... While you wait for HardCodedNick to make his/her first guess, " +
		                                       "you may text M HardCodedNick [MSG] to give him/her cues");
		checkResponse("21991234899", "o", "+-+\n" +
		                                     "| \n" +
		                                     "|  \n" +
		                                     "|  \n" +
		                                     "|\n" +
		                                     "====\n" +
		                                     "Word: COCO---S\n" +
		                                     "Used: COS\n" +
		                                     "Text a letter, the complete word or M pAtRiCiA [MSG]",
		                                     "Match going on! HardCodedNick guessed letter O\n" +
		                                     "+-+\n" +
		                                     "| \n" +
		                                     "|  \n" +
		                                     "|  \n" +
		                                     "|\n" +
		                                     "====\n" +
		                                     "Word: COCO---S\n" +
		                                     "Used: COS\n" +
		                                     "Want to chat with him/her? Text M HardCodedNick [MSG] to provoke him/her");
		
		// continue playing, with eventually some wrong letters, until HardCodedNick wins
		checkResponse("21991234899", "a",
			ivac.hangmanPhrasings.getWordGuessingPlayerStatus (true, false, false, false, false, false, "COCO---S", "ACOS", "pAtRiCiA"),
			ivac.hangmanPhrasings.getWordProvidingPlayerStatus(true, false, false, false, false, false, "COCO---S", "A", "ACOS", expectedNickname));
		checkResponse("21991234899", "nu",
			ivac.hangmanPhrasings.getWordGuessingPlayerStatus (true, false, false, false, false, false, "COCONU-S", "ACNOSU", "pAtRiCiA"),
			ivac.hangmanPhrasings.getWordProvidingPlayerStatus(true, false, false, false, false, false, "COCONU-S", "NU", "ACNOSU", expectedNickname));
		checkResponse("21991234899", "xyz",
			ivac.hangmanPhrasings.getWordGuessingPlayerStatus (true, true, true, true, false, false, "COCONU-S", "ACNOSUXYZ", "pAtRiCiA"),
			ivac.hangmanPhrasings.getWordProvidingPlayerStatus(true, true, true, true, false, false, "COCONU-S", "XYZ", "ACNOSUXYZ", expectedNickname));
		
		// test the winning phrase
		checkResponse("21991234899", "t", "\\0/\n" +
                                          " |\n" +
                                          "/ \\\n" +
                                          "COCONUTS! You got it! Keep on playing! Text INVITE pAtRiCiA for a new match with this player or text LIST to see " +
                                          "other online players. You may also text P to play with a random user.",
                                          "HardCodedNick guessed your word! Want revenge? Text INVITE HardCodedNick; Want to tease him/her? Text M HardCodedNick [MSG]");
		
		// start a new game to test the losing phrase
		invitePlayerByNick("21991234899", "pAtRiCiA");
		sendWordToBeGuessed("21991234899", "HardCodedNick", "Muggles", "pAtRiCiA");
		acceptInvitation("21998019167", "Muggles", "pAtRiCiA", "HardCodedNick");

		// lose the game
		checkResponse("21998019167", "muskratramblesong", "+-+\n" +
                                                            "| x\n" +
                                                            "|/|\\\n" +
                                                            "|/ \\\n" +
                                                            "====\n" +
                                                            "Oh, my... you were hanged! The word was MUGGLES. Now challenge HardCodedNick for a revenge: text INVITE HardCodedNick " +
                                                            "or tease him/her with M {{wordGuessingPlayerNickname}} [MSG]",
                                                            "Good one! pAtRiCiA wasn't able to guess your word! Say something about it with M pAtRiCiA [MSG] or INVITE pAtRiCiA " +
                                                            "for a new match, and make it hard when you choose the word!");

		// unsubscribe
		checkResponse("21998019167", "unsubscribe", "OK, you asked to leave, so you're out - you won't be able to play the HANGMAN anymore, nor receive chat or game messages, nor lucky numbers to win prizes... Changed your mind? Nice! Text HANGMAN to 993 - $0.99 a week.");
		checkResponse("21991234899", "unsubscribe", "OK, you asked to leave, so you're out - you won't be able to play the HANGMAN anymore, nor receive chat or game messages, nor lucky numbers to win prizes... Changed your mind? Nice! Text HANGMAN to 993 - $0.99 a week.");
		assertFalse("User should not be still subscribed on the backend", subscriptionEngine._isUserSubscribed("21998019167"));
		assertFalse("User should not be still subscribed on the backend", subscriptionEngine._isUserSubscribed("21991234899"));

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
		              ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart(guessedWordSoFar, usedLetters, "Dom"),
		              ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart(guessedWordSoFar, invitedPlayerNickname));
		
		
		// usual no
		resetStates();
		invitePlayerForAMatch(invitingPlayerPhone, invitingPlayerNickname, "cacatua", invitedPlayerPhone, invitedPlayerNickname);
		checkResponse(invitedPlayerPhone, "no",
		              ivac.hangmanPhrasings.getInvitationRefusalResponseForInvitedPlayer(invitingPlayerNickname),
		              ivac.hangmanPhrasings.getInvitationRefusalNotificationForInvitingPlayer(invitedPlayerNickname));
		
		// some other commands before the no
		resetStates();
		invitePlayerForAMatch(invitingPlayerPhone, invitingPlayerNickname, "cacatua", invitedPlayerPhone, invitedPlayerNickname);
		// send and receive a chat
		sendPrivateMessage(invitedPlayerPhone, invitingPlayerNickname, "Why do you want to play with me?");
		sendPrivateMessage(invitingPlayerPhone, invitedPlayerNickname, "Cause you're the only one on my test list 8-)");
		// also, see the profile...
		tc.checkResponse("21998019167", "profile DOM", ivac.profilePhrasings.getUserProfilePresentation("Dom", "21991234899"));
		// the no
		checkResponse(invitedPlayerPhone, "no",
		              ivac.hangmanPhrasings.getInvitationRefusalResponseForInvitedPlayer(invitingPlayerNickname),
		              ivac.hangmanPhrasings.getInvitationRefusalNotificationForInvitingPlayer(invitedPlayerNickname));

	}
	
	@Test
	public void testProfileCommand() throws SQLException {
		invitePlayerForAMatch("+1907991234899", "Dom", "cacatua", "21998019167", "pAtY");

		checkResponse("21998019167", "profile DOM", "HANGMAN: Dom: Subscribed; Online; Alaska. Text INVITE Dom to play a hangman match; M Dom [MSG] to chat; LIST to see online players; P to play with a random user.");
		
		checkResponse("21998019167", "no",
			"The invitation to play the Hangman Game made by Dom was refused. Text LIST to 993 to see online users or send him/her a message: text M Dom [MSG]",
			"pAtY refused your invitation to play. Send LIST to pick someone else or send him/her a message: text M pAtY [MSG]");
		
		// new user
		checkResponse("2199999999", "profile dom", "HANGMAN: Dom: Subscribed; Online; Alaska. Text INVITE Dom to play a hangman match; M Dom [MSG] to chat; LIST to see online players; P to play with a random user.");
		
	}

	
	/**************************************
	** COMMANDS & STATES EXAUSTIVE TESTS **
	**************************************/
	
	@Test
	public void testSubscriptionSubtleties() throws SQLException {

		// unregistered users should not be able to run some commands...
		checkResponse("21991234899", "nick Mario", ivac.subscriptionPhrasings.getDoubleOptinStart());
		
		// even new users should be able to unsubscribe
		checkResponse("21998019167", "unsubscribe", ivac.subscriptionPhrasings.getUserRequestedUnsubscriptionNotification());
		
		// struggle to subscribe
		checkResponse("21991234899", "help",                 ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse("21991234899", "no",                   ivac.subscriptionPhrasings.getDisagreeToSubscribe());
		checkResponse("21991234899", "come again?",          ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse("21991234899", "what is this, again?", ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse("21991234899", "hangman",              ivac.subscriptionPhrasings.getSuccessfullySubscribed());
		// struggle to find one's first steps after subscription
		checkResponse("21991234899", "HJKS", ivac.helpPhrasings.getExistingUsersFallbackHelp());
		
		// now, when a user that was registered attempts to play (but he/she has been secretly unsubscribed due to life cycle rules), what happens?
		// he/she must use the game as if their subscription was still valid...
		subscriptionEngine.unsubscribeUser("21991234899");
		checkResponse("21991234899", "nick domJon", ivac.profilePhrasings.getNicknameRegistrationNotification("domJon"));
		sendPrivateMessage("21991234899", "domJon", "I believe I would receive this, but... should I?");
		
		// ... only when the unsubscription is formally reported, the game notes the change in the state
		ivac.subscriptionCommands.unsubscribeUser("21991234899");
		checkResponse("21991234899", "nick ItsMeMario", ivac.subscriptionPhrasings.getDoubleOptinStart());
		
		// test the default nick for newly registered users
		checkResponse("21991234900", "hangman", ivac.subscriptionPhrasings.getSuccessfullySubscribed());
		checkResponse("21991234900", "profile", ivac.profilePhrasings.getUserProfilePresentation("Guest4900", "21991234899"));
	}
	
	@Test
	public void testWordSelectionSubtleties() throws SQLException {
		registerUser("21991234899", "dom");
		registerUser("21998019167", "donna");
		invitePlayerByNick("21991234899", "donna");
		checkResponse("21991234899", "caca123", ivac.hangmanPhrasings.getNotAGoodWord("CACA123"));	// non letter characters
		checkResponse("21991234899", "coco",    ivac.hangmanPhrasings.getNotAGoodWord("COCO"));		// unplayable word
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
		// first, test sending an empty ...
		checkResponse("21998019167", "", ivac.helpPhrasings.getStatefulHelpMessage(nstGuessingWordFromHangmanHumanOpponent, ""));
		// ... and an invalid message
		checkResponse("21998019167", "???", ivac.helpPhrasings.getStatefulHelpMessage(nstGuessingWordFromHangmanHumanOpponent, "???"));
		// now loop through all messages
		for (char letter : attemptedLetters) {
			String sLetter = Character.toString(letter);
			if (usedLetters.indexOf(sLetter.toUpperCase()) == -1) {
				usedLetters += sLetter.toUpperCase();
			}
			String hardWordSoFar = hardWord.replaceAll("[^"+usedLetters+"]", "-");
			if (hardWordSoFar.equals(hardWord)) {
				checkResponse("21998019167", sLetter,				                 
		                      ivac.hangmanPhrasings.getWinningMessageForWordGuessingPlayer (hardWord, "dom"),
		                      ivac.hangmanPhrasings.getWinningMessageForWordProvidingPlayer("paty"));
				break;
			} else {
				checkResponse("21998019167", sLetter,
				              ivac.hangmanPhrasings.getWordGuessingPlayerStatus (false, false, false, false, false, false, hardWordSoFar, usedLetters, "dom"),
				              ivac.hangmanPhrasings.getWordProvidingPlayerStatus(false, false, false, false, false, false, hardWordSoFar, sLetter.toUpperCase(), usedLetters, "paty"));
			}
		}
		
		// test playing with a bot letters recognition
		// TODO do the same as for humans... should allow setting bot words on the fly
	}
	
	@Test
	public void testEndGameSubtleties() throws SQLException {
		
		// test ending a match with another player by issuing a state changing command
		startAPlayerMatch("11111", "AllOne", "OneMotherFucker", "22222", "AllTwo");
		tc.checkResponse("22222", "unsubscribe",
			ivac.hangmanPhrasings.getMatchGiveupNotificationForWordGuessingPlayer("AllOne"),
			ivac.hangmanPhrasings.getMatchGiveupNotificationForWordProvidingPlayer("AllTwo"),
			ivac.subscriptionPhrasings.getUserRequestedUnsubscriptionNotification());
		tc.checkResponse("22222", "x", ivac.subscriptionPhrasings.getDoubleOptinStart());
		
		// test ending a match with another player through the "END MATCH" command
		startAPlayerMatch("111111", "AllOnePlus1", "TwoMotherFucker", "222222", "AllTwoPlus2");
		tc.checkResponse("222222", "end",
			ivac.hangmanPhrasings.getMatchGiveupNotificationForWordGuessingPlayer("AllOnePlus1"),
			ivac.hangmanPhrasings.getMatchGiveupNotificationForWordProvidingPlayer("AllTwoPlus2"));
		// test fall back to 'EXISTING_USER' state, where unknown commands issues the specialized help
		tc.checkResponse("222222", "x", ivac.helpPhrasings.getExistingUsersFallbackHelp());
		
		// test ending a match with a bot
		startABotMatch("21991234898", "DOM", "CHIMPANZEE");
		tc.checkResponse("21991234898", "end", ivac.hangmanPhrasings.getMatchGiveupNotificationForWordGuessingPlayer("DomBot"));
		// test the fall back to 'EXISTING_USER' state, where only known commands are answered
		tc.checkResponse("21991234898", "x", ivac.helpPhrasings.getExistingUsersFallbackHelp());
	}
	
	@Test
	// assures we can reach the correct game commands on each of the states
	public void testNavigationStates() throws SQLException {
		// nstNewUser state command tests
		String wordProvidingPhone;
		String wordGuessingPhone;
		
		// define the test environment
		registerUser("21999999999", "SomeOne");
		
		// nstNewUser
		checkResponse(navigateNewUserTo(nstNewUser), "help",               ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstNewUser), "what??",             ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstNewUser), "unsubscribe",        ivac.subscriptionPhrasings.getUserRequestedUnsubscriptionNotification());
		checkResponse(navigateNewUserTo(nstNewUser), "nick",               ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstNewUser), "nick WhoAmI",        ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstNewUser), "m SomeOne hi there", ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstNewUser), "list",               ivac.profilePhrasings.getProfileList(new ProfileDto[] {profileDB.getProfileRecord("SomeOne")}));
		checkResponse(navigateNewUserTo(nstNewUser), "profile SomeOne",    ivac.profilePhrasings.getUserProfilePresentation("SomeOne", "21999999999"));
		checkResponse(navigateNewUserTo(nstNewUser), "invite SomeOne",     ivac.subscriptionPhrasings.getDoubleOptinStart());
		
		// nstAnsweringDoubleOptin
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "hangman",            ivac.subscriptionPhrasings.getSuccessfullySubscribed());
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "no",                 ivac.subscriptionPhrasings.getDisagreeToSubscribe());
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "help",               ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "unsubscribe",        ivac.subscriptionPhrasings.getUserRequestedUnsubscriptionNotification());
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "nick",               ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "nick WhoAmI",        ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "m SomeOne hi there", ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "list",               ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "profile SomeOne",    ivac.profilePhrasings.getUserProfilePresentation("SomeOne", "21999999999"));
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "invite SomeOne",     ivac.subscriptionPhrasings.getDoubleOptinStart());
		checkResponse(navigateNewUserTo(nstAnsweringDoubleOptin, "help"), "wtf???",             ivac.subscriptionPhrasings.getDoubleOptinStart());

		// nstExistingUser
		//checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "hangman",            ivac.subscriptionPhrasings.getSuccessfullySubscribed());
		checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "help",               ivac.helpPhrasings.getCompositeHelpMessage(0));
		checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "unsubscribe",        ivac.subscriptionPhrasings.getUserRequestedUnsubscriptionNotification());
		checkResponse(navigateNewUserTo(nstExistingUser, "hangman",
		                                "nick WhoAmI877"),           "nick",               ivac.profilePhrasings.getAskForNewNickname("WhoAmI877"));
		checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "nick FromExisting",  ivac.profilePhrasings.getNicknameRegistrationNotification("FromExisting"));
		checkResponse(navigateNewUserTo(nstExistingUser, "hangman",
		                                "nick FromExisting2"),       "m SomeOne hi there", ivac.chatPhrasings.getPrivateMessageDeliveryNotification("SomeOne"),
		                                                                                   ivac.chatPhrasings.getPrivateMessage("FromExisting2", "hi there"));
		//checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "list",               ivac.profilePhrasings....);
		checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "profile SomeOne",    ivac.profilePhrasings.getUserProfilePresentation("SomeOne", "21999999999"));
		checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "invite SomeOne",     ivac.hangmanPhrasings.getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation("SomeOne"));
		checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "wtf???",             ivac.helpPhrasings.getExistingUsersFallbackHelp());
		
		// nstPresentingCompositeHelp
		//checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman"), "hangman",            ivac.subscriptionPhrasings.getSuccessfullySubscribed());
		checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman", "help"), "help",               ivac.helpPhrasings.getCompositeHelpMessage(1));
		checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman", "help"), "unsubscribe",        ivac.subscriptionPhrasings.getUserRequestedUnsubscriptionNotification());
		checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman", "help",
		                                "nick WhoAmI966"),                              "nick",               ivac.profilePhrasings.getAskForNewNickname("WhoAmI966"));
		checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman", "help"), "nick FromHelp",      ivac.profilePhrasings.getNicknameRegistrationNotification("FromHelp"));
		checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman", "help",
		                                "nick FromHelp2"),                              "m SomeOne hi there", ivac.chatPhrasings.getPrivateMessageDeliveryNotification("SomeOne"),
		                                                                                                      ivac.chatPhrasings.getPrivateMessage("FromHelp2", "hi there"));
		//checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman"), "list",               ivac.profilePhrasings....);
		checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman", "help"), "profile SomeOne",    ivac.profilePhrasings.getUserProfilePresentation("SomeOne", "21999999999"));
		checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman", "help"), "invite SomeOne",     ivac.hangmanPhrasings.getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation("SomeOne"));
		checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman", "help"), "wtf???",             ivac.helpPhrasings.getExistingUsersFallbackHelp());
		
		// nstRegisteringNickname
		checkResponse(navigateNewUserTo(nstRegisteringNickname, "hangman", "nick"), "help",               ivac.helpPhrasings.getCompositeHelpMessage(0));
		checkResponse(navigateNewUserTo(nstRegisteringNickname, "hangman", "nick"), "unsubscribe",        ivac.subscriptionPhrasings.getUserRequestedUnsubscriptionNotification());
		checkResponse(navigateNewUserTo(nstRegisteringNickname, "hangman",
		                                "nick WhoAmIS28", "nick"),                  "nick",               ivac.profilePhrasings.getAskForNewNickname("WhoAmIS28"));
		checkResponse(navigateNewUserTo(nstRegisteringNickname, "hangman", "nick"), "nick FromNick",      ivac.profilePhrasings.getNicknameRegistrationNotification("FromNick"));
		checkResponse(navigateNewUserTo(nstRegisteringNickname, "hangman",
		                                "nick FromNick2", "nick"),                  "m SomeOne hi there", ivac.chatPhrasings.getPrivateMessageDeliveryNotification("SomeOne"),
		                                                                                                  ivac.chatPhrasings.getPrivateMessage("FromNick2", "hi there"));
		//checkResponse(navigateNewUserTo(nstPresentingCompositeHelp, "hangman"), "list",               ivac.profilePhrasings....);
		checkResponse(navigateNewUserTo(nstRegisteringNickname, "hangman", "nick"), "profile SomeOne",    ivac.profilePhrasings.getUserProfilePresentation("SomeOne", "21999999999"));
		checkResponse(navigateNewUserTo(nstRegisteringNickname, "hangman", "nick"), "invite SomeOne",     ivac.hangmanPhrasings.getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation("SomeOne"));
		checkResponse(navigateNewUserTo(nstRegisteringNickname, "hangman", "nick"), "wtf is this??",      ivac.helpPhrasings.getStatefulHelpMessage(nstRegisteringNickname, "wtf is this??"));
		checkResponse(navigateNewUserTo(nstRegisteringNickname, "hangman", "nick"), "FromNick3",          ivac.profilePhrasings.getNicknameRegistrationNotification("FromNick3"));
		
		// nstEnteringMatchWord
		//checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "hangman",            ivac.subscriptionPhrasings.getSuccessfullySubscribed());
		checkResponse(navigateNewUserTo(nstEnteringMatchWord, "hangman", "invite SomeOne"), "help",               ivac.helpPhrasings.getCompositeHelpMessage(0));
		checkResponse(navigateNewUserTo(nstEnteringMatchWord, "hangman", "invite SomeOne"), "unsubscribe",        ivac.subscriptionPhrasings.getUserRequestedUnsubscriptionNotification());
		checkResponse(navigateNewUserTo(nstEnteringMatchWord, "hangman", "invite SomeOne",
		                                "nick WhoAmI485"),                                  "nick",               ivac.profilePhrasings.getAskForNewNickname("WhoAmI485"));
		checkResponse(navigateNewUserTo(nstEnteringMatchWord, "hangman", "invite SomeOne"), "nick FromWord",      ivac.profilePhrasings.getNicknameRegistrationNotification("FromWord"));
		checkResponse(navigateNewUserTo(nstEnteringMatchWord, "hangman", "invite SomeOne",
		                                "nick FromWord2"),                                  "m SomeOne hi there", ivac.chatPhrasings.getPrivateMessageDeliveryNotification("SomeOne"),
		                                                                                                          ivac.chatPhrasings.getPrivateMessage("FromWord2", "hi there"));
		//checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "list",               ivac.profilePhrasings....);
		checkResponse(navigateNewUserTo(nstEnteringMatchWord, "hangman", "invite SomeOne"), "profile SomeOne",    ivac.profilePhrasings.getUserProfilePresentation("SomeOne", "21999999999"));
		checkResponse(navigateNewUserTo(nstEnteringMatchWord, "hangman", "invite SomeOne"), "invite SomeOne",     ivac.hangmanPhrasings.getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation("SomeOne"));
		checkResponse(navigateNewUserTo(nstEnteringMatchWord, "hangman", "invite SomeOne"), "wtf???",             ivac.hangmanPhrasings.getNotAGoodWord("WTF???"));
		checkResponse(navigateNewUserTo(nstEnteringMatchWord, "hangman",
		                                "nick FromWord3", "invite SomeOne"),                "MyWord",             ivac.hangmanPhrasings.getInvitationResponseForInvitingPlayer("SomeOne"),
		                                                                                                          ivac.hangmanPhrasings.getInvitationNotificationForInvitedPlayer("FromWord3"));

		// nstAnsweringToHangmanMatchInvitation
		
		// nstGuessingWordFromHangmanHumanOpponent
		//checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "hangman",            ivac.subscriptionPhrasings.getSuccessfullySubscribed());
		
		wordGuessingPhone  = navigateNewUserTo(nstExistingUser, "hangman", "nick Guessing1");
		wordProvidingPhone = navigateNewUserTo(nstExistingUser, "hangman", "nick Providing1", "invite Guessing1", "Shoes");
		checkResponse(wordGuessingPhone, "yes", ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart("S---S", "S", "Providing1"),
		                                        ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart("S--S", "Guessing1"));
		checkResponse(wordGuessingPhone, "help", ivac.hangmanPhrasings.getMatchGiveupNotificationForWordGuessingPlayer("Providing1"),
		                                         ivac.hangmanPhrasings.getMatchGiveupNotificationForWordProvidingPlayer("Guessing1"),
		                                         ivac.helpPhrasings.getCompositeHelpMessage(0));
		
		wordGuessingPhone  = navigateNewUserTo(nstExistingUser, "hangman", "nick Guessing2");
		wordProvidingPhone = navigateNewUserTo(nstExistingUser, "hangman", "nick Providing2", "invite Guessing2", "Shoes");
		checkResponse(wordGuessingPhone, "yes", ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart("S---S", "S", "Providing2"),
		                                        ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart("S--S", "Guessing2"));
		checkResponse(wordGuessingPhone, "unsubscribe", ivac.hangmanPhrasings.getMatchGiveupNotificationForWordGuessingPlayer("Providing2"),
		                                                ivac.hangmanPhrasings.getMatchGiveupNotificationForWordProvidingPlayer("Guessing2"),
		                                                ivac.subscriptionPhrasings.getUserRequestedUnsubscriptionNotification());

		wordGuessingPhone  = navigateNewUserTo(nstExistingUser, "hangman", "nick Guessing3");
		wordProvidingPhone = navigateNewUserTo(nstExistingUser, "hangman", "nick Providing3", "invite Guessing3", "Shoes");
		checkResponse(wordGuessingPhone, "yes", ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart("S---S", "S", "Providing3"),
		                                        ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart("S--S", "Guessing3"));
		checkResponse(wordGuessingPhone, "nick", ivac.hangmanPhrasings.getMatchGiveupNotificationForWordGuessingPlayer("Providing3"),
		                                         ivac.hangmanPhrasings.getMatchGiveupNotificationForWordProvidingPlayer("Guessing3"),
		                                         ivac.profilePhrasings.getAskForNewNickname("Guessing3"));

		wordGuessingPhone  = navigateNewUserTo(nstExistingUser, "hangman", "nick Guessing4");
		wordProvidingPhone = navigateNewUserTo(nstExistingUser, "hangman", "nick Providing4", "invite Guessing4", "Shoes");
		checkResponse(wordGuessingPhone, "yes", ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart("S---S", "S", "Providing4"),
		                                        ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart("S--S", "Guessing4"));
		checkResponse(wordGuessingPhone, "nick FromHuman", ivac.profilePhrasings.getNicknameRegistrationNotification("FromHuman"));

		wordGuessingPhone  = navigateNewUserTo(nstExistingUser, "hangman", "nick Guessing5");
		wordProvidingPhone = navigateNewUserTo(nstExistingUser, "hangman", "nick Providing5", "invite Guessing5", "Shoes");
		checkResponse(wordGuessingPhone, "yes", ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart("S---S", "S", "Providing5"),
		                                        ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart("S--S", "Guessing5"));
		checkResponse(wordGuessingPhone, "m Providing5 hi there", ivac.chatPhrasings.getPrivateMessageDeliveryNotification("Providing5"),
                                                                  ivac.chatPhrasings.getPrivateMessage("Guessing5", "hi there"));

		//checkResponse(navigateNewUserTo(nstExistingUser, "hangman"), "list",               ivac.profilePhrasings....);
		
		wordGuessingPhone  = navigateNewUserTo(nstExistingUser, "hangman", "nick Guessing6");
		wordProvidingPhone = navigateNewUserTo(nstExistingUser, "hangman", "nick Providing6", "invite Guessing6", "Shoes");
		checkResponse(wordGuessingPhone, "yes", ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart("S---S", "S", "Providing6"),
		                                        ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart("S--S", "Guessing6"));
		checkResponse(wordGuessingPhone, "profile Guessing6", ivac.profilePhrasings.getUserProfilePresentation("Guessing6", wordGuessingPhone));
		
		wordGuessingPhone  = navigateNewUserTo(nstExistingUser, "hangman", "nick Guessing7");
		wordProvidingPhone = navigateNewUserTo(nstExistingUser, "hangman", "nick Providing7", "invite Guessing7", "Shoes");
		checkResponse(wordGuessingPhone, "yes", ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart("S---S", "S", "Providing7"),
		                                        ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart("S--S", "Guessing7"));
		checkResponse(wordGuessingPhone, "invite SomeOne", ivac.hangmanPhrasings.getMatchGiveupNotificationForWordGuessingPlayer("Providing7"),
                                                           ivac.hangmanPhrasings.getMatchGiveupNotificationForWordProvidingPlayer("Guessing7"),
                                                           ivac.hangmanPhrasings.getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation("SomeOne"));
		
		wordGuessingPhone  = navigateNewUserTo(nstExistingUser, "hangman", "nick Guessing8");
		wordProvidingPhone = navigateNewUserTo(nstExistingUser, "hangman", "nick Providing8", "invite Guessing8", "Shoes");
		checkResponse(wordGuessingPhone, "yes", ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart("S---S", "S", "Providing8"),
		                                        ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart("S--S", "Guessing8"));
		checkResponse(wordGuessingPhone, "?", ivac.helpPhrasings.getStatefulHelpMessage(nstGuessingWordFromHangmanHumanOpponent, "?"));

		wordGuessingPhone  = navigateNewUserTo(nstExistingUser, "hangman", "nick Guessing9");
		wordProvidingPhone = navigateNewUserTo(nstExistingUser, "hangman", "nick Providing9", "invite Guessing9", "Shoes");
		checkResponse(wordGuessingPhone, "yes", ivac.hangmanPhrasings.getWordGuessingPlayerMatchStart("S---S", "S", "Providing9"),
		                                        ivac.hangmanPhrasings.getWordProvidingPlayerMatchStart("S--S", "Guessing9"));
		checkResponse(wordGuessingPhone, "a", ivac.hangmanPhrasings.getWordGuessingPlayerStatus(true, false, false, false, false, false, "S---S", "AS", "Providing9"),
                                              ivac.hangmanPhrasings.getWordProvidingPlayerStatus(true, false, false, false, false, false, "S---S", "A", "AS", "Guessing9"));
		
		// nstGuessingWordFromHangmanBotOpponent, idem
		
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
