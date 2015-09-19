package mutua.smsappmodule.smslogic.sessions;

import mutua.smsappmodule.dal.IMatchDB;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman;

/** <pre>
 * SMSAppModuleSessionsHangman.java
 * ================================
 * (created by luiz, Sep 18, 2015)
 *
 * Declares the session properties needed by the "Chat" SMS Application Module command processors,
 * implementing the Mutua SMSApp Session Properties design pattern, as described in {@link ISessionProperty}
 *
 * @see ISessionProperty
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleSessionsHangman implements ISessionProperty {

	/** Session property used to keep track of the opponent phone number, when inviting a human
	 *  opponent for a match */
	sprOpponentPhoneNumber,
	
	/** Session property used to keep track of {@link IMatchDB} matchId, when on the
	 *  {@link SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanHumanOpponent} state. */
	sprHangmanMatchId,
	
	;
	
	@Override
	public String getPropertyName() {
		return this.name().substring(3);
	}

}
