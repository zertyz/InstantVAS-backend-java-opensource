package mutua.hangmansmsgame.i18n;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.regex.Matcher;

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
	
	
	public static String SHORT_CODE = "9714";

	
	/** phrases_map := { ["phrase name"] = {message1, message2, ...}, ... } */
	private Hashtable<String, String[]> phrasesMap;
	
	/** phrases := { {"phrase name", "phrase text"}, ... } */
	protected IPhraseology(String[][] phrases) {
		phrasesMap = new Hashtable<String, String[]>();
		for (String[] phrase : phrases) {
			String phraseName = phrase[0];
			String[] messagesArray = Arrays.copyOfRange(phrase, 1, phrase.length);
			phrasesMap.put(phraseName, messagesArray);
		}
	}
	
	/** retrieves the messages for the given 'phraseName', fulfilling all parameters, where
	 *  'parameters' := { {"name", "value"}, ... }, or 'null' if 'phraseName' wasn't found */
	protected String[] getPhrases(String phraseName, String[][] parameters) {
		String[] messages = phrasesMap.get(phraseName);
		if (messages == null) {
			return null;
		}
		messages = Arrays.copyOf(messages, messages.length);
		if (parameters != null) {
			for (int i=0; i<messages.length; i++) {
				String originalMessage = messages[i];
				String fulfilledMessage = originalMessage;
				// fulfill the parameters
				for (String[] parameter : parameters) {
					String parameterName  = parameter[0];
					String parameterValue = parameter[1];
					if (parameterValue != null) {
						fulfilledMessage = fulfilledMessage.replaceAll("\\{\\{" + parameterName + "\\}\\}", Matcher.quoteReplacement(parameterValue));
					}
				}
				messages[i] = fulfilledMessage;
			}
		}
		return messages;
	}
	
	/** @see TestPhraseology#getPhrases(String, String[][]) */
	protected String getPhrase(String phraseName, String[][] parameters) {
		String[] messages = getPhrases(phraseName, parameters);
		if (messages == null) {
			return null;
		} else {
			return messages[0];
		}
	}
	
	/** @see TestPhraseology#getPhrases(String, String[][]) */
	protected String getPhrase(String phraseName) {
		return getPhrase(phraseName, null);
	}

	
	/**************************
	** INSTANTIATION METHODS **
	**************************/
	
	public static IPhraseology getCarrierSpecificPhraseology(ESMSInParserCarrier carrier) {
		return new TestPhraseology(SHORT_CODE);
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
	
	/** shown when it is not possible to register the user on the registration APIs */
	public abstract String INFOCouldNotRegister();


	
	
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
	
	protected String getGallowsArt(boolean drawHead, boolean drawLeftArm, boolean drawRightArm,
	                                boolean drawChest, boolean drawLeftLeg, boolean drawRightLeg) {
		return getPhrase("gallowsArt", new String[][] {
			{"head",     (drawHead?"O":"")},
			{"leftArm",  (drawLeftArm?"/":" ")},
			{"chest",    (drawChest?"|":" ")},
			{"rightArm", (drawRightArm?"\\":"")},
			{"leftLeg",  (drawLeftLeg?"/":" ")},
			{"rightLeg", (drawRightLeg?"\\":"")},
		});
	}
	
	protected String getWinningArt() {
		return getPhrase("winningArt");
	}
	
	protected String getLosingArt() {
		return getPhrase("losingArt");
	}

	
	/** sent to the player who provided the word when the match starts -- when it is accepted by the opponent player */
	public abstract String PLAYINGWordProvidingPlayerStart(String guessedWordSoFar, String wordGuessingPlayerNick);

	/** sent back to the player who is attempting to guess the word when the match starts */
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
	
	/** shown to the losing player, who tried to guess the word */
	public abstract String PLAYINGLosingMessageForWordGuessingPlayer(String word, String wordProvidingPlayerNick);
	
	/** show to the word providing player when the word guessing player lost the game */
	public abstract String PLAYINGLosingMessageForWordProvidingPlayer(String wordGuessingPlayerNick);

	/** sent to inform the word providing player that the opponent has taken actions that lead to a match give up */
	public abstract String PLAYINGMatchGiveupNotificationForWordProvidingPlayer(String wordGuessingPlayerNick);

	/** sent to inform the word guessing player that his actions lead to a match give up */
	public abstract String PLAYINGMatchGiveupNotificationForWordGuessingPlayer(String wordProvidingPlayerNick);

	
	// INVITING
	///////////
	
	/** shown when the game wants to ask the player for an opponent to be invited for a match */
	public abstract String INVITINGAskOpponentNickOrPhone(String invitingPlayerNickname);

	/** shown when the game asks the user for a word to start a match with an invited player, for which the nick name was provided */
	public abstract String INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation(String opponentNickname);

	/** shown when the game asks the user for a word to start a match with an invited player, for which the phone number was provided */
	public abstract String INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(String opponentPhoneNumber);

	/** message to notify the inviting player that the invitation has been sent */
	public abstract String INVITINGInvitationNotificationForInvitingPlayer(String invitedPlayerNickname);
	
	/** message to notify the inviting player that the opponent took too long to reply and the match is cancelled */
	public abstract String INVITINGTimeoutNotificationForInvitingPlayer(String invitedPlayerNickname, String suggestedNewPlayersNickname);
	
	/** message to notify the invited player that someone wants to play a hangman match */
	public abstract String INVITINGInvitationNotificationForInvitedPlayer(String invitingPlayerNickname);
	
	/** message to notify the inviting player that the opponent refused the match */
	public abstract String INVITINGInvitationRefusalNotificationForInvitingPlayer(String invitedPlayerNickname);

	/** response to the invited player after he/she refused to play */
	public abstract String INVITINGInvitationRefusalNotificationForInvitedPlayer(String invitingPlayerNickname);
	

	// LISTING PLAYERS
	//////////////////
	
	/** shows a list of players. 'playersInfo' := { {nick, estate, number of lucky numbers received}, ... } */
	public abstract String LISTINGShowPlayers(String[][] playersInfo);

	/** used to notify that we were through when showing online players */
	public abstract String LISTINGNoMorePlayers();

	
	// PROVOKING
	////////////
	
	/** presented to the user after he/she attempted to provoke someone, indicating the message was delivered */
	public abstract String PROVOKINGDeliveryNotification(String destinationNick);

	/** the message sent to the destination user when he is being provoked by someone */
	public abstract String PROVOKINGSendMessage(String sourceNick, String message);

	/** presented to inform that the desired nickname was not found */
	public abstract String PROVOKINGNickNotFound(String nickname);

	
	// UNSUBSCRIPTION
	/////////////////
	
	/** presented to inform the subscription was canceled on the game platform */
	public abstract String UNSUBSCRIBINGUnsubscriptionNotification();

}
