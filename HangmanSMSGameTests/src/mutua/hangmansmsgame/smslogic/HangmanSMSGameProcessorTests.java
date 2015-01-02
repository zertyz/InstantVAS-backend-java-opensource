package mutua.hangmansmsgame.smslogic;

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
	
	
	/*********************
	** AUXILIAR METHODS **
	*********************/

	
	/**********
	** TESTS **
	**********/
	
	// SCENARIO: the first message being sent to the system
    ///////////////////////////////////////////////////////

	@Test
	public void testUnrecognizedCommand() {
		tc.checkResponse("21991234899", "HJKS", "Welcome to the HANGMAN game. Join and compete for prizes. Send HELP for free to XXXX to know the rules.");
	}
	
	@Test
	public void testExternalUserInvitationPlayingPath() {
		String expectedNickName = "HardCodedNick";
		tc.checkResponse("21991234899", "Forca", "Welcome to the HANGMAN game. Join and compete for prizes. Send HELP for free to XXXX to know the rules.");
		tc.checkResponse("21991234899", "AJUDA", new String[] {
			"1/3: You can play the HANGMAN game in 2 ways: guessing someone's word or inviting someone to play with your word",
			"2/3: You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number",
			"3/3: Every week, 1 lucky number is selected to win the prize. Send an option to XXXX: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help"});
		tc.checkResponse("21991234899", "C", "HANGMAN: Name registered: " + expectedNickName + ". Send your friend's phone to XXXX or LIST to see online players. NICK <NEW NICK> to change your name.");
		tc.checkResponse("21991234899", "21998019167", "HANGMAN: Your friend's phone: 21998019167. Think of a word without special digits and send it now to XXXX. After the invitation, you'll get a lucky number");
		tc.checkResponse("21991234899", "coco", new String[] {
			"Howlie was invited to play with you. while you wait, you can provoke Howlie by sending a message to XXXX (0.31+tax) or send SIGNUP to provoke for free how many times you want",
			"HANGMAN: HardCodedNick is inviting you for a hangman match. Do you accept? Send YES to XXXXX or PROFILE to see HardCodedNick information"});
	}
	
}