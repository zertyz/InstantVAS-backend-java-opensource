package mutua.hangmansmsgame.i18n;

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

	
	private String getShortHelp() {
		return "(J) Play online; (C) Invite a friend or user; (R)anking; (A)Help";
	}
	
/*	@Override
	public String CUPOMNewLuckyNumber(String cuponsList, String series, String daysList, String monthAndYear) {
		return null;
	}

	@Override
	public String CUPOMDrawParticipation(String daysList, String monthAndYear, String cuponsList) {
		return null;
	}

	@Override
	public String CUPOMNoLuckyNumbers() {
		return null;
	}

	@Override
	public String SUBSCRIPTIONAlreadySubscribed() {
		return "HANGMAN: you're already a subscriber. Next renewal in DD/MM/YYYY. Send LIST to XXXX to see online players; P NICK MSG to provoke them! To cancel the service, send EXIT";
	}

	@Override
	public String SUBSCRIPTIONCouldNotBillNorCompleteTheSubscriptionOrRenewal() {
		return "HANGMAN: it was not possible to confirm or renew your subscription. Please, check your credits and try again. Send SIGNUP to XXXX ($2+taxes/week)";
	}

	@Override
	public String SUBSCRIPTIONRenewalSuccess() {
		return "HANGMAN: your subscription has been renewed and you can send provocation messages for free for 1 more week. Send LIST to XXXX to see online players; P NICK MSG to provoke";
	}
*/
	@Override
	public String INFOWelcome() {
		return "Welcome to the HANGMAN game. Join and compete for prizes. Send HELP for free to XXXX to know the rules.";
	}

	@Override
	public String[] INFOFullHelp() {
		return new String[] {
			"1/3: You can play the HANGMAN game in 2 ways: guessing someone's word or inviting someone to play with your word",
			"2/3: You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number",
			"3/3: Every week, 1 lucky number is selected to win the prize. Send an option to XXXX: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help",
		};
	}

	@Override
	public String INFOWelcomeMenu() {
		return "Pick an option. Send to XXXX: " + getShortHelp();
	}

	@Override
	public String PROFILEView(String nick, String state, int numberOfLuckyNumbers) {
		return "HANGMAN: DeiaGATA: Subscribed, Rio de Janeiro, 109 lucky numbers. Send SIGNUP to provoke for free or INVITE DeiaGATA for a match.";
	}
	
	@Override
	public String PROFILEFullfillingAskNick() {
		return "HANGMAN: To play with a friend, u need first to sign your name. Now send your name (8 letters or numbers max.) to XXXXX";
	}
	
	@Override
	public String PROFILENickRegisteredNotification(String newNickname) {
		return "HANGMAN: Name registered: " + newNickname + ". Send LIST to XXXX to see online players. NICK <NEW NICK> to change your name.";
	}


	@Override
	public String PLAYINGWordProvidingPlayerStart(String guessedWordSoFar, String wordGuessingPlayerNick) {
		return "Game started with " + wordGuessingPlayerNick + ".\n" +
		       getHangDrawing(false, false, false, false, false, false) +
		       "Send P " + wordGuessingPlayerNick + " MSG to give him/her clues";
	}

	@Override
	public String PLAYINGWordGuessingPlayerStart(String guessedWordSoFar, String usedLetters) {
		return PLAYINGWordGuessingPlayerStatus(false, false, false, false, false, false, guessedWordSoFar, usedLetters);
	}
	
	@Override
	public String PLAYINGWordGuessingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                                              boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
	                                              String guessedWordSoFar, String usedLetters) {
		return getHangDrawing(drawHead, drawLeftArm, drawRightArm, drawChest, drawLeftLeg, drawRightLeg) +
		       "Word: " + guessedWordSoFar + "\n" +
		       "Used: " + usedLetters + "\n" +
		       "Send a letter, the complete word or END to cancel the game";
	}

	@Override
	public String PLAYINGWordProvidingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                                               boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
	                                               String guessedWordSoFar, String guessedLetter, String usedLetters,
	                                               String nick) {
		return nick + " guessed letter " + guessedLetter + "\n" +
		       getHangDrawing(drawHead, drawLeftArm, drawRightArm, drawChest, drawLeftLeg, drawRightLeg) +
		       "Word: " + guessedWordSoFar + "\n" +
		       "Used: " + usedLetters + "\n" +
		       "Send P " + nick + " MSG to provoke him/her";
	}

	@Override
	public String PLAYINGWinningMessageForWordGuessingPlayer(String word, String luckyNumber) {
		return getWinningDrawing() + word +
		       "! You got it! Here is your lucky number: " + luckyNumber + ". Send: J to play or A for help";
	}

	@Override
	public String PLAYINGWinningMessageForWordProvidingPlayer(String nick) {
		return nick + " guessed your word! P " + nick + " MSG to provoke him/her or INVITE " + nick + " for a new match";
	}

	@Override
	public String PLAYINGLoosingMessageForWordGuessingPlayer(String word, String nick) {
		return getLoosingDrawing() + "The word was " + word + ". " + getShortHelp();
		// TODO: the message may encourage the loosing player to try and suggest a word back to the opponent.
	}

	@Override
	public String PLAYINGLoosingMessageForWordProvidingPlayer(String nick) {
		return "Good one! " + nick + " wasn't able to guessed your word! P " + nick + " MSG to provoke him/her or INVITE " + nick + " for a new match";
	}

	@Override
	public String INVITINGAskOpponentNickOrPhone(String invitingPlayerNickname) {
		return "HANGMAN: Name registered: " + invitingPlayerNickname + ". Send your friend's phone to XXXX or LIST to see online players. NICK <NEW NICK> to change your name.";
	}

	@Override
	public String INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation(String opponentNickname) {
		return "HANGMAN: Inviting " + opponentNickname + ". Think of a word without special digits and send it now to XXXX. After the invitation, you'll get a lucky number";
	}

	@Override
	public String INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(String opponentPhoneNumber) {
		return "HANGMAN: Your friend's phone: " + opponentPhoneNumber + ". Think of a word without special digits and send it now to XXXX. After the invitation, you'll get a lucky number";
	}

	@Override
	public String INVITINGInvitationNotificationForInvitingPlayer(String invitedPlayerNickName) {
		return invitedPlayerNickName + " was invited to play with you. while you wait, you can provoke " + invitedPlayerNickName +  " by sending a message to XXXX (0.31+tax) or send SIGNUP to provoke for free how many times you want";
	}

	@Override
	public String INVITINGTimeoutNotificationForInvitingPlayer(String invitedPlayerNickName, String suggestedNewPlayersNickName) {
		return invitedPlayerNickName + " is taking too long to answer. However, a new player, " +
		       suggestedNewPlayersNickName + ", is available. Play with " + suggestedNewPlayersNickName + "? Send YES to XXXX";
	}

	@Override
	public String INVITINGInvitationNotificationForInvitedPlayer(String invitingPlayerNickName) {
		return "HANGMAN: " + invitingPlayerNickName + " is inviting you for a hangman match. Do you accept? Send YES to XXXXX or PROFILE to see " + invitingPlayerNickName + " information";
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
		return playersList + ". To play, send INVITE NICK to XXXXX; MORE for more players or PROFILE NICK";
	}

	@Override
	public String PROVOKINGDeliveryNotification(String destinationNick) {
		return "Your message was sent to " + destinationNick + ". Wait for the answer or provoke other players sending P NICK MSG to XXXXX. Send SIGNUP to provoke for free.";
	}

	@Override
	public String PROVOKINGSendMessage(String sourceNick, String message) {
		return sourceNick + ": " + message + " - Answer by sending P " + sourceNick + " MSG to XXXX";
	}

}
