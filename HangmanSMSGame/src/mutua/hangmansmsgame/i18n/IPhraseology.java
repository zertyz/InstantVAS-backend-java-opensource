package mutua.hangmansmsgame.i18n;

import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * IPhraseology.java
 * =================
 * (created by luiz, Dec 19, 2014)
 *
 * Define the methods that will generate the phrasing for the SMS Application
 *
 * @see RelatedClass(es)
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

	
	/**********************
	** INTERFACE METHODS **
	**********************/
	
	// CUPONS
	/////////
	
	/** shown when the user deserves another lucky number in order to participate in the next draw(s) */
	public abstract String CUPOMNewLuckyNumber(String cuponsList, String series, String daysList, String monthAndYear);
	
	/** shown when the user wants to get informed on which draws him/her participation is assured */
	public abstract String CUPOMDrawParticipation(String daysList, String monthAndYear, String cuponsList);
	
	/** shown when the user wants to be informed about draws on which he/she is a participant, but he/she isn't participating on any */
	public abstract String CUPOMNoLuckyNumbers();
	
	
	// SUBSCRIPTION
	///////////////
	
	/** shown when the user requested to be a subscriber, but he/she is already one */
	public abstract String SUBSCRIPTIONAlreadySubscribed();
	
	/** shown when we could not bill the user and, therefore, could not complete the subscription or renewal process */
	public abstract String SUBSCRIPTIONCouldNotBillNorCompleteTheSubscriptionOrRenewal();
	
	/** show when the time to renew the subscription has come and it succeeded */
	public abstract String SUBSCRIPTIONRenewalSuccess();
	
	
	// INFO
	////////
	
	/** shown in response to the first interaction the user has with the game */
	public abstract String INFOWelcome();
	
	/** menu shown to new users */
	public abstract String INFOWelcomeMenu();

	/** shown when the user request the help / instructions */
	public abstract String[] INFOFullHelp();

}
