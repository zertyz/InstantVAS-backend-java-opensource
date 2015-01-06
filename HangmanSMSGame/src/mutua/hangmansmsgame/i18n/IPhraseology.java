package mutua.hangmansmsgame.i18n;

import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * IPhraseology.java
 * =================
 * (created by luiz, Dec 19, 2014)
 *
 * Define the methods that will generate the phrasing for the SMS Application
 *
 * @see TestPhraseology
 * @version $Id$
 * @author luiz
 */

public abstract class IPhraseology {

	
	/**************************
	** INSTANTIATION METHODS **
	**************************/
	
	public static IPhraseology getCarrierSpecificPhraseology(ESMSInParserCarrier carrier) {
		return new TestPhraseology();
	}

	protected String getList(String[] elements, String pairSeparator, String lastPairSeparator) {
        String list = "";
        for (int i=0; i<elements.length; i++) {
                list += elements[i];
                if (i == (elements.length-2)) {
                        list += lastPairSeparator;
                } else if (i != (elements.length-1)) {
                        list += pairSeparator;
                }
        }
        return list;
}
	
	/**********************
	** INTERFACE METHODS **
	**********************/
	
	// CUPONS
	/////////
	
//	/** shown when the user deserves another lucky number in order to participate in the next draw(s) */
//	public abstract String CUPOMNewLuckyNumber(String cuponsList, String series, String daysList, String monthAndYear);
//	
//	/** shown when the user wants to get informed on which draws him/her participation is assured */
//	public abstract String CUPOMDrawParticipation(String daysList, String monthAndYear, String cuponsList);
//	
//	/** shown when the user wants to be informed about draws on which he/she is a participant, but he/she isn't participating on any */
//	public abstract String CUPOMNoLuckyNumbers();
	
	
	// SUBSCRIPTION
	///////////////
	
//	/** shown when the user requested to be a subscriber, but he/she is already one */
//	public abstract String SUBSCRIPTIONAlreadySubscribed();
//	
//	/** shown when we could not bill the user and, therefore, could not complete the subscription or renewal process */
//	public abstract String SUBSCRIPTIONCouldNotBillNorCompleteTheSubscriptionOrRenewal();
//	
//	/** show when the time to renew the subscription has come and it succeeded */
//	public abstract String SUBSCRIPTIONRenewalSuccess();
	
	
	// INFO
	////////
	
	/** shown in response to the first interaction the user has with the game */
	public abstract String INFOWelcome();
	
	/** menu shown to new users */
	public abstract String INFOWelcomeMenu();

	/** shown when the user request the help / instructions */
	public abstract String[] INFOFullHelp();
	
	
	// PROFILE
	//////////
	
	/** shown when a user wants to view the profile of another user */
	public abstract String PROFILEView(String nick, String state, int numberOfLuckyNumbers);

	/** shown when the game asks the player to choose a nickname */
	public abstract String PROFILEFullfillingAskNick();

	/** present the user information regarding the confirmation of his nickname registration */
	public abstract String PROFILENickRegisteredNotification(String newNickname);

	
	// PLAYING
	//////////
	
	protected String getHangDrawing(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                                boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg) {
		return "+-+\n" +
		       "| " + (drawHead?"O":"") + "\n" +
		       "|" + (drawLeftArm?"/":" ") + (drawChest?"|":" ") + (drawRightArm?"\\":"") + "\n" +
		       "|" + (drawLeftLeg?"/":" ") + " " + (drawRightLeg?"\\":"") + "\n" +
		       "|\n" +
		       "====\n";
	}
	
	protected String getWinningDrawing() {
		return "\\0/\n" +
		       " |\n" +
		       "/ \\\n";
	}
	
	protected String getLoosingDrawing() {
		return "+-+\n" +
		       "| x\n" +
		       "|/|\\\n" +
		       "|/ \\\n" +
		       "====\n";
	}

	
	/** sent to the player who provided the word when the match starts -- when it is accepted by the opponent player */
	public abstract String PLAYINGWordProvidingPlayerStart(String guessedWordSoFar, String wordGuessingPlayerNick);

	/** sent back to the player who is attempting to guess the word when the match starts 
	 * @param usedLetters TODO*/
	public abstract String PLAYINGWordGuessingPlayerStart(String guessedWordSoFar, String usedLetters);
	
	/** shows the state of the game play to the player attempting to guess the word */
	public abstract String PLAYINGWordGuessingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
                                                           boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
                                                           String guessedWordSoFar, String usedLetters);
	
	/** shows the state of the game play to the player who provided the word */
	public abstract String PLAYINGWordProvidingPlayerStatus(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                                                        boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg,
	                                                        String guessedWordSoFar, String guessedLetter, String usedLetters, String nick);
	
	/** shown to the winning player, who tried to guess the word */
	public abstract String PLAYINGWinningMessageForWordGuessingPlayer(String word, String luckyNumber);
	
	/** show to the word providing player when the word guessing player won the game */
	public abstract String PLAYINGWinningMessageForWordProvidingPlayer(String wordGuessingPlayerNick);
	
	/** shown to the losing player, who tried to guess the word 
	 * @param wordProvidingPlayerNick TODO*/
	public abstract String PLAYINGLosingMessageForWordGuessingPlayer(String word, String wordProvidingPlayerNick);
	
	/** show to the word providing player when the word guessing player lost the game */
	public abstract String PLAYINGLosingMessageForWordProvidingPlayer(String wordGuessingPlayerNick);
	
	
	// INVITING
	///////////
	
	// TODO what if the user provide an unknown nickname or a wrong phone number?
	/** shown when the game wants to ask the player for an opponent to be invited for a match */
	public abstract String INVITINGAskOpponentNickOrPhone(String invitingPlayerNickname);

	/** shown when the game asks the user for a word to start a match with an invited player, for which the nick name was provided */
	public abstract String INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation(String opponentNickname);

	/** shown when the game asks the user for a word to start a match with an invited player, for which the phone number was provided */
	public abstract String INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(String opponentPhoneNumber);

	/** message to notify the inviting player that the invitation has been sent */
	public abstract String INVITINGInvitationNotificationForInvitingPlayer(String invitedPlayerNickName);
	
	/** message to notify the inviting player that the opponent took too long to reply and the match is cancelled */
	public abstract String INVITINGTimeoutNotificationForInvitingPlayer(String invitedPlayerNickName, String suggestedNewPlayersNickName);
	
	/** message to notify the invited player that someone wants to play a hangman match */
	public abstract String INVITINGInvitationNotificationForInvitedPlayer(String invitingPlayerNickName);
	

	// LISTING PLAYERS
	//////////////////
	
	/** shows a list of players. 'playersInfo' := { {nick, estate, number of lucky numbers received}, ... } */
	public abstract String LISTINGShowPlayers(String[][] playersInfo);
	
	
	// PROVOKING
	////////////
	
	// TODO what if 'destinationNick' is invalid?
	/** presented to the user after he/she attempted to provoke someone, indicating the message was delivered */
	public abstract String PROVOKINGDeliveryNotification(String destinationNick);

	/** the message sent to the destination user when he is being provoked by someone */
	public abstract String PROVOKINGSendMessage(String sourceNick, String message);

}
