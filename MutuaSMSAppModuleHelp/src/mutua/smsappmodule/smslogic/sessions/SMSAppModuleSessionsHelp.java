package mutua.smsappmodule.smslogic.sessions;

import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;

/** <pre>
 * SMSAppModuleSessionsHelp.java
 * =============================
 * (created by luiz, Jul 17, 2015)
 *
 * Declares the session properties needed by the "Help" SMS Application Module command processors,
 * implementing the "Instant VAS SMSApp Session Properties" design pattern, as described in {@link ISessionProperty}
 *
 * @see ISessionProperty
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleSessionsHelp implements ISessionProperty {
	
	/** Session property used to store the last shown composite help message, when on the
	 * {@link SMSAppModuleNavigationStatesHelp#nstPresentingCompositeHelp} state */
	sprLastCompositeHelpMsgNumberShown,
	
	;

	@Override
	public String getPropertyName() {
		return this.name().substring(3);
	}

}
