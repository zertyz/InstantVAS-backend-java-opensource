package mutua.smsappmodule.smslogic.sessions;

import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat;

/** <pre>
 * SMSAppModuleSessionsChat.java
 * =============================
 * (created by luiz, Aug 26, 2015)
 *
 * Declares the session properties needed by the "Chat" SMS Application Module command processors,
 * implementing the Mutua SMSApp Session Properties design pattern, as described in {@link ISessionProperty}
 *
 * @see ISessionProperty
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleSessionsChat implements ISessionProperty {

	/** Session property used to keep track of the last sender, to be used on stateful replies (replies without the
	 *  nickname), when on the {@link SMSAppModuleNavigationStatesChat#nstChattingWithSomeone} state.
	 *  Note: this variable is only set for target users -- the senders should never have this variable set. Actually,
	 *  the sender must have this variable deleted if he/she attempts to send a message to a different user */
	sprLastPrivateMessageSender,
	
	;
	
	@Override
	public String getPropertyName() {
		return this.name().substring(3);
	}

}
