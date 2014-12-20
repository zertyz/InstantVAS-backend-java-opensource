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

	@Override
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
		return null;
	}

	@Override
	public String SUBSCRIPTIONCouldNotBillNorCompleteTheSubscriptionOrRenewal() {
		return null;
	}

	@Override
	public String SUBSCRIPTIONRenewalSuccess() {
		return null;
	}

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
		return "Pick an option. Send to XXXX: (J) Play online; (C) Invite a friend or user; (R)anking; (A)Help";
	}
	
}
