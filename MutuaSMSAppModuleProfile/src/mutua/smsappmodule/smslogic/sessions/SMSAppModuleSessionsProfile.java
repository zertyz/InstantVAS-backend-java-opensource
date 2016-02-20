package mutua.smsappmodule.smslogic.sessions;

import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile;

/** <pre>
 * SMSAppModuleSessionsProfile.java
 * ================================
 * (created by luiz, Aug 3, 2015)
 *
 * Declares the session properties needed by the "Profile" SMS Application Module command processors,
 * implementing the "Instant VAS SMSApp Session Properties design pattern", as described in {@link ISessionProperty}
 *
 * @see ISessionProperty
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleSessionsProfile implements ISessionProperty {
	
	/** Session property used to store the sequence on which the "profile wizard" should run the "cmd*Dialog" commands,
	 * when on the {@link SMSAppModuleNavigationStatesProfile#nstFulfillingProfileWizard} state */
	sprProfileWizardDialogSequence,
	
	;
	
	@Override
	public String getPropertyName() {
		return this.name().substring(3);
	}

}
