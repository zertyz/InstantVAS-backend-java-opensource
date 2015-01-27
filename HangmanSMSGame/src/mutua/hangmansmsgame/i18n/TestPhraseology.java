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

	
	private String getShortHelp() {
		return getPhrase(EPhraseNames.shortHelp);
	}
	
	
	public TestPhraseology(String shortCode) {
		this.shortCode = shortCode;
	}
	
	@Override
	public String INFOWelcome() {
		return getPhrase(EPhraseNames.INFOWelcome, new String[][] {{"shortCode", shortCode}});
	}

	@Override
	public String[] INFOFullHelp() {
		return getPhrases(EPhraseNames.INFOFullHelp, new String[][] {{"shortCode", shortCode}});
	}

	@Override
	public String INFOWelcomeMenu() {
		return getPhrase(EPhraseNames.INFOWelcomeMenu, new String[][] {
			{"shortCode", shortCode},
			{"shortHelp", getShortHelp()},
		});
	}

	@Override
	public String INFOCouldNotRegister() {
		return getPhrase(EPhraseNames.INFOCouldNotRegister);
	}

	@Override
	public String PROFILEView(String nick, String state, int numberOfLuckyNumbers) {
		return getPhrase(EPhraseNames.PROFILEView, new String[][] {
			{"shortCode",            shortCode},
			{"nick",                 nick},
			{"state",                state},
			{"numberOfLuckyNumbers", Integer.toString(numberOfLuckyNumbers)},
		});
	}
	
	@Override
	public String PROFILEFullfillingAskNick() {
		return getPhrase(EPhraseNames.PROFILEFullfillingAskNick, new String[][] {
			{"shortCode", shortCode},
		});
	}
	
	@Override
	public String PROFILENickRegisteredNotification(String newNickname) {
		return getPhrase(EPhraseNames.PROFILENickRegisteredNotification, new String[][] {
			{"shortCode",   shortCode},
			{"newNickname", newNickname},
		});
	}


	@Override
	public String PLAYINGWordProvidingPlayerStart(String guessedWordSoFar, String wordGuessingPlayerNick) {
		return getPhrase(EPhraseNames.PLAYINGWordProvidingPlayerStart, new String[][] {
			{"shortCode",              shortCode},
			{"wordGuessingPlayerNick", wordGuessingPlayerNick},
			{"gallowsArt",             getGallowsArt(false, false, false, false, false, false)},
		});
	}

	@Override
	public String PLAYINGWordGuessingPlayerStart(String guessedWordSoFar, String usedLetters) {
		return getPhrase(EPhraseNames.PLAYINGWordGuessingPlayerStart, new String[][] {
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
		return getPhrase(EPhraseNames.PLAYINGWordGuessingPlayerStatus, new String[][] {
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
		return getPhrase(EPhraseNames.PLAYINGWordProvidingPlayerStatus, new String[][] {
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
		return getPhrase(EPhraseNames.PLAYINGWinningMessageForWordGuessingPlayer, new String[][] {
			{"shortCode",   shortCode},
			{"word",        word},
			{"luckyNumber", luckyNumber},
			{"winningArt",  getWinningArt()},
		});
	}

	@Override
	public String PLAYINGWinningMessageForWordProvidingPlayer(String wordGuessingPlayerNick) {
		return getPhrase(EPhraseNames.PLAYINGWinningMessageForWordProvidingPlayer, new String[][] {
			{"shortCode",              shortCode},
			{"wordGuessingPlayerNick", wordGuessingPlayerNick},
		});
	}

	@Override
	public String PLAYINGLosingMessageForWordGuessingPlayer(String word, String wordProvidingPlayerNick) {
		return getPhrase(EPhraseNames.PLAYINGLosingMessageForWordGuessingPlayer, new String[][] {
			{"shortCode",               shortCode},
			{"word",                    word},
			{"wordProvidingPlayerNick", wordProvidingPlayerNick},
			{"losingArt",               getLosingArt()},
		});
	}

	@Override
	public String PLAYINGLosingMessageForWordProvidingPlayer(String wordGuessingPlayerNick) {
		return getPhrase(EPhraseNames.PLAYINGLosingMessageForWordProvidingPlayer, new String[][] {
			{"shortCode",              shortCode},
			{"wordGuessingPlayerNick", wordGuessingPlayerNick},
		});
	}
	
	@Override
	public String PLAYINGMatchGiveupNotificationForWordProvidingPlayer(String wordGuessingPlayerNick) {
		return getPhrase(EPhraseNames.PLAYINGLosingMessageForWordProvidingPlayer, new String[][] {
			{"shortCode",              shortCode},
			{"wordGuessingPlayerNick", wordGuessingPlayerNick},
		});
	}

	@Override
	public String PLAYINGMatchGiveupNotificationForWordGuessingPlayer(String wordProvidingPlayerNick) {
		return getPhrase(EPhraseNames.PLAYINGMatchGiveupNotificationForWordGuessingPlayer, new String[][] {
			{"shortCode",               shortCode},
			{"wordProvidingPlayerNick", wordProvidingPlayerNick},
		});
	}

	@Override
	public String INVITINGAskOpponentNickOrPhone(String invitingPlayerNickname) {
		return getPhrase(EPhraseNames.INVITINGAskOpponentNickOrPhone, new String[][] {
			{"shortCode",              shortCode},
			{"invitingPlayerNickname", invitingPlayerNickname},
		});
	}

	@Override
	public String INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation(String opponentNickname) {
		return getPhrase(EPhraseNames.INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation, new String[][] {
			{"shortCode",        shortCode},
			{"opponentNickname", opponentNickname},
		});
	}

	@Override
	public String INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(String opponentPhoneNumber) {
		return getPhrase(EPhraseNames.INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation, new String[][] {
			{"shortCode",           shortCode},
			{"opponentPhoneNumber", opponentPhoneNumber},
		});
	}

	@Override
	public String INVITINGInvitationNotificationForInvitingPlayer(String invitedPlayerNickname) {
		return getPhrase(EPhraseNames.INVITINGInvitationNotificationForInvitingPlayer, new String[][] {
			{"shortCode",             shortCode},
			{"invitedPlayerNickname", invitedPlayerNickname},
		});
	}

	@Override
	public String INVITINGTimeoutNotificationForInvitingPlayer(String invitedPlayerNickname, String suggestedNewPlayersNickname) {
		return getPhrase(EPhraseNames.INVITINGTimeoutNotificationForInvitingPlayer, new String[][] {
			{"shortCode",                   shortCode},
			{"invitedPlayerNickname",       invitedPlayerNickname},
			{"suggestedNewPlayersNickname", suggestedNewPlayersNickname},
		});
	}

	@Override
	public String INVITINGInvitationNotificationForInvitedPlayer(String invitingPlayerNickname) {
		return getPhrase(EPhraseNames.INVITINGInvitationNotificationForInvitedPlayer, new String[][] {
			{"shortCode",                   shortCode},
			{"invitingPlayerNickname",      invitingPlayerNickname},
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
		return getPhrase(EPhraseNames.LISTINGShowPlayers, new String[][] {
			{"shortCode",   shortCode},
			{"playersList", playersList},
		});
	}

	@Override
	public String LISTINGNoMorePlayers() {
		return getPhrase(EPhraseNames.LISTINGNoMorePlayers, new String[][] {
			{"shortCode",   shortCode},
		});
	}

	@Override
	public String PROVOKINGDeliveryNotification(String destinationNick) {
		return getPhrase(EPhraseNames.PROVOKINGDeliveryNotification, new String[][] {
			{"shortCode",       shortCode},
			{"destinationNick", destinationNick},
		});
	}

	@Override
	public String PROVOKINGSendMessage(String sourceNick, String message) {
		return getPhrase(EPhraseNames.PROVOKINGSendMessage, new String[][] {
			{"shortCode",  shortCode},
			{"sourceNick", sourceNick},
			{"message",    message},
		});
	}

	@Override
	public String PROVOKINGNickNotFound(String nickname) {
		return getPhrase(EPhraseNames.PROVOKINGNickNotFound, new String[][] {
			{"shortCode", shortCode},
			{"nickname",  nickname},
		});
	}

	@Override
	public String UNSUBSCRIBINGUnsubscriptionNotification() {
		return getPhrase(EPhraseNames.UNSUBSCRIBINGUnsubscriptionNotification, new String[][] {
			{"shortCode", shortCode},
		});
	}

	@Override
	public String INVITINGInvitationRefusalNotificationForInvitingPlayer(String invitedPlayerNickname) {
		return getPhrase(EPhraseNames.INVITINGInvitationRefusalNotificationForInvitingPlayer, new String[][] {
			{"shortCode",             shortCode},
			{"invitedPlayerNickname", invitedPlayerNickname},
		});
	}

	@Override
	public String INVITINGInvitationRefusalNotificationForInvitedPlayer(String invitingPlayerNickname) {
		return getPhrase(EPhraseNames.INVITINGInvitationRefusalNotificationForInvitedPlayer, new String[][] {
			{"shortCode",              shortCode},
			{"invitingPlayerNickname", invitingPlayerNickname},
		});
	}

}
