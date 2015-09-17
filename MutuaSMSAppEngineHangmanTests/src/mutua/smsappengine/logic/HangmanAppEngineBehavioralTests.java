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
		tc.checkResponse("21998019167", "Scriptogram",
			"HardCodedNick was invited to play with you. while you wait, you can provoke HardCodedNick by sending a message to 9714 (0.31+tax) or send SIGNUP to provoke for free how many times you want",
			"HANGMAN: pAtRiCiA is inviting you for a hangman match. Do you accept? Send YES to 9714 or PROFILE to see pAtRiCiA information");
	}

}
