package mutua.hangmansmsgame.smslogic;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.IUserDB;
import mutua.hangmansmsgame.i18n.TestPhraseology;

import org.junit.Test;

/** <pre>
 * HangmanSMSGameProcessorTests.java
 * =================================
 * (created by luiz, Jan 19, 2011)
 *
 * Tests the application as described on the product manual -- with emphasis at the phraseologies,
 * and the usage mechanics
 */


public class HangmanSMSGameProcessorTests {

	
	private TestCommons tc = new TestCommons();
	TestPhraseology testPhraseology = new TestPhraseology();
	
	
	// databases
	////////////
	
	private static IUserDB userDB = DALFactory.getUserDB();

	
	/*********************
	** AUXILIAR METHODS **
	*********************/
	
	/** Reset the populate Users db. users := { {phone, nick}, ...} */
	public void setUserDB(String[][] users) {
		userDB.reset();
		for (String[] userRecord : users) {
			userDB.checkAvailabilityAndRecordNickname(userRecord[0], userRecord[1]);
		}
	}

	
	/**********
	** TESTS **
	**********/
	
	// SCENARIO: the first message being sent to the system
    ///////////////////////////////////////////////////////

//	@Test
//	public void testUnrecognizedCommand() {
//		tc.checkResponse("21991234899", "HJKS", "Welcome to the HANGMAN game. Join and compete for prizes. Send HELP for free to XXXX to know the rules.");
//	}
	
	@Test
	public void testExternalUserInvitationPlayingPath() {
		String playerNickName;
		String guestNickname;
		setUserDB(new String[][] {
			{"21991234899", playerNickName = "HardCodedNick"},
			{"21998019167", guestNickname  = "haole"},
		});
		
		tc.checkResponse("21991234899", "Forca", "Welcome to the HANGMAN game. Join and compete for prizes. Send HELP for free to XXXX to know the rules.");
		tc.checkResponse("21991234899", "AJUDA", new String[] {
			"1/3: You can play the HANGMAN game in 2 ways: guessing someone's word or inviting someone to play with your word",
			"2/3: You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number",
			"3/3: Every week, 1 lucky number is selected to win the prize. Send an option to XXXX: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help"});
		tc.checkResponse("21991234899", "nick HardCodedNick", "HANGMAN: Name registered: HardCodedNick. Send LIST to XXXX to see online players. NICK <NEW NICK> to change your name.");
		tc.checkResponse("21998019167", "nick haole", "HANGMAN: Name registered: haole. Send LIST to XXXX to see online players. NICK <NEW NICK> to change your name.");
		tc.checkResponse("21991234899", "C", "HANGMAN: Name registered: " + playerNickName + ". Send your friend's phone to XXXX or LIST to see online players. NICK <NEW NICK> to change your name.");
		tc.checkResponse("21991234899", "21998019167", "HANGMAN: Your friend's phone: 21998019167. Think of a word without special digits and send it now to XXXX. After the invitation, you'll get a lucky number");
		tc.checkResponse("21991234899", "coconuts", new String[] {
			guestNickname + " was invited to play with you. while you wait, you can provoke " + guestNickname + " by sending a message to XXXX (0.31+tax) or send SIGNUP to provoke for free how many times you want",
			"HANGMAN: " + playerNickName + " is inviting you for a hangman match. Do you accept? Send YES to XXXXX or PROFILE to see " + playerNickName + " information"});
		
		// opponent player wants to play and the game starts
		tc.checkResponse("21998019167", "YES", new String[] {"Game started with haole.\n" +
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
		                                                     "Send a letter, the complete word or END to cancel the game"});
		tc.checkResponse("21998019167", "o", new String[] {"haole guessed letter o\n" +
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
                "Send a letter, the complete word or END to cancel the game"});
		tc.checkResponse("21998019167", "a", new String[] {
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, false, false, false, false, false, "COCO---S", "a", "ACOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, false, false, false, false, false, "COCO---S", "ACOS")});
		tc.checkResponse("21998019167", "b", new String[] {
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, true, false, false, false, false, "COCO---S", "b", "ABCOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, true, false, false, false, false, "COCO---S", "ABCOS")});
		tc.checkResponse("21998019167", "c", new String[] {
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, true, false, false, false, false, "COCO---S", "c", "ABCOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, true, false, false, false, false, "COCO---S", "ABCOS")});
		tc.checkResponse("21998019167", "e", new String[] {
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, true, true, false, false, false, "COCO---S", "e", "ABCEOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, true, true, false, false, false, "COCO---S", "ABCEOS")});
		tc.checkResponse("21998019167", "f", new String[] {
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, true, true, true, false, false, "COCO---S", "f", "ABCEFOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, true, true, true, false, false, "COCO---S", "ABCEFOS")});
		tc.checkResponse("21998019167", "g", new String[] {
			testPhraseology.PLAYINGWordProvidingPlayerStatus(true, true, true, true, true, false, "COCO---S", "g", "ABCEFGOS", guestNickname),
			testPhraseology.PLAYINGWordGuessingPlayerStatus (true, true, true, true, true, false, "COCO---S", "ABCEFGOS")});
		tc.checkResponse("21998019167", "h", new String[] {
			"Good one! haole wasn't able to guessed your word! P haole MSG to provoke him/her or INVITE haole for a new match",
			testPhraseology.PLAYINGLosingMessageForWordGuessingPlayer("COCONUTS", playerNickName)});
		// TODO in the middle of the game, the word providing player might want to send a provocative message by just typing?
		
		String chatMessage = "now pick one for me!";
		tc.checkResponse("21991234899", "P " + guestNickname + " " + chatMessage, new String[] {
			testPhraseology.PROVOKINGDeliveryNotification(guestNickname),
			testPhraseology.PROVOKINGSendMessage(playerNickName, chatMessage)});
		
		tc.checkResponse("21998019167", "profile haole", "HANGMAN: DeiaGATA: Subscribed, Rio de Janeiro, 109 lucky numbers. Send SIGNUP to provoke for free or INVITE DeiaGATA for a match.");
		tc.checkResponse("21998019167", "nick pAtRiCiA", "HANGMAN: Name registered: pAtRiCiA. Send LIST to XXXX to see online players. NICK <NEW NICK> to change your name.");
		tc.checkResponse("21998019167", "list", "HardCodedNick(RJ/1), pAtRiCiA(RJ/2). To play, send INVITE NICK to XXXXX; MORE for more players or PROFILE NICK");
	}
	
}