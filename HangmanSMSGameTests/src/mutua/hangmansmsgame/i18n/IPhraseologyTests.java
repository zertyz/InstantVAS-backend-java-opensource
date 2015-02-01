package mutua.hangmansmsgame.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import mutua.hangmansmsgame.config.Configuration;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessorTests;

import org.junit.Test;

/** <pre>
 * IPhraseologyTests.java
 * ======================
 * (created by luiz, Jan 12, 2015)
 *
 * Verifies the behavior of 'i18n' classes
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

class TPhrases extends IPhraseology {
	public String INFOWelcome() {return null;}
	public String INFOWelcomeMenu() {return null;}
	public String[] INFOFullHelp() {return null;}
	public String INFOCouldNotRegister() {return null;}
	public String PROFILEView(String nick, String state, int numberOfLuckyNumbers) {return null;}
	public String PROFILEFullfillingAskNick() {return null;}
	public String PROFILENickRegisteredNotification(String newNickname) {return null;}
	public String PLAYINGWordProvidingPlayerStart(String guessedWordSoFar, String wordGuessingPlayerNick) {return null;}
	public String PLAYINGWordGuessingPlayerStart(String guessedWordSoFar, String usedLetters) {return null;}
	public String PLAYINGWordGuessingPlayerStatus(boolean drawHead,	boolean drawLeftArm, boolean drawRightArm, boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg, String guessedWordSoFar, String usedLetters) {return null;}
	public String PLAYINGWordProvidingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm, boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg, String guessedWordSoFar, String guessedLetter, String usedLetters, String nick) {return null;}
	public String PLAYINGWinningMessageForWordGuessingPlayer(String word, String luckyNumber) {return null;}
	public String PLAYINGWinningMessageForWordProvidingPlayer(String wordGuessingPlayerNick) {return null;}
	public String PLAYINGLosingMessageForWordGuessingPlayer(String word, String wordProvidingPlayerNick) {return null;}
	public String PLAYINGLosingMessageForWordProvidingPlayer(String wordGuessingPlayerNick) {return null;}
	public String PLAYINGMatchGiveupNotificationForWordProvidingPlayer(String wordGuessingPlayerNick) {return null;}
	public String PLAYINGMatchGiveupNotificationForWordGuessingPlayer(String wordProvidingPlayerNick) {return null;}
	public String INVITINGAskOpponentNickOrPhone(String invitingPlayerNickname) {return null;}
	public String INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation(String opponentNickname) {return null;}
	public String INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(String opponentPhoneNumber) {return null;}
	public String INVITINGInvitationNotificationForInvitingPlayer(String invitedPlayerNickName) {return null;}
	public String INVITINGTimeoutNotificationForInvitingPlayer(String invitedPlayerNickName, String suggestedNewPlayersNickName) {return null;}
	public String INVITINGInvitationNotificationForInvitedPlayer(String invitingPlayerNickName) {return null;}
	public String LISTINGShowPlayers(String[][] playersInfo) {return null;}
	public String LISTINGNoMorePlayers() {return null;}
	public String PROVOKINGDeliveryNotification(String destinationNick) {return null;}
	public String PROVOKINGSendMessage(String sourceNick, String message) {return null;}
	public String PROVOKINGNickNotFound(String nickname) {return null;}
	public String UNSUBSCRIBINGUnsubscriptionNotification() {return null;}
	public String INVITINGInvitationRefusalNotificationForInvitingPlayer(String invitedPlayerNickname) {return null;}
	public String INVITINGInvitationRefusalNotificationForInvitedPlayer(String invitingPlayerNickname) {return null;}
}

public class IPhraseologyTests {

	@Test
	public void testReadingMessagesFromAConfiguration() {
		fail("fix these tests");
//		IPhraseology phrases = new TPhrases(new String[][] {
//			{"playersList", "{{nick}} ({{state}}/{{numberOfLuckyNumbers}})"},
//		});
//		String message = phrases.getPhrase("playersList", new String[][] {
//			{"nick",                 "Dom"},
//			{"state",                "RJ"},
//			{"numberOfLuckyNumbers", "97"},
//		});
//		assertEquals("Parameter substitution failed", "Dom (RJ/97)", message);
//		message = phrases.getPhrase("playersList", new String[][] {
//			{"nick",                 "\\"},
//			{"state",                "$"},
//			{"numberOfLuckyNumbers", "{}"},
//		});
//		assertEquals("Spacial symbols substitution failed", "\\ ($/{})", message);
//		message = phrases.getPhrase("playersList", new String[][] {
//			{"nick",                 null},
//			{"state",                null},
//			{"numberOfLuckyNumbers", null},
//		});
//		assertEquals("Null parameter values substitution failed", "{{nick}} ({{state}}/{{numberOfLuckyNumbers}})", message);
	}

}
