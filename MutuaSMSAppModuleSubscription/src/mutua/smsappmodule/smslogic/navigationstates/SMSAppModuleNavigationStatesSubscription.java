package mutua.smsappmodule.smslogic.navigationstates;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.CommandNamesSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.CommandTriggersSubscription.*;

import mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;

/** <pre>
 * SMSAppModuleNavigationStatesSubscription.java
 * =============================================
 * (created by luiz, Jul 22, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Subscription" SMSApp Module
 * 
 * Implements the "Instant VAS SMSApp Navigation States" design pattern, as described by
 * {@link NavigationState}.
 * 
 * @see NavigationState
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleNavigationStatesSubscription {
	
	/** Class to be statically imported by the Commands Implementation to represent the navigation states */
	public static class NavigationStatesNamesSubscription {
		/** @see SMSAppModuleNavigationStatesSubscription#nstAnsweringDoubleOptin */
		public final static String nstAnsweringDoubleOptin = "AnsweringDoubleOptin";
	}
	
	// Navigation States Definitions
	////////////////////////////////
	
	/** The list of all navigation states -- for 'SMSProcessor' to be able to deserialize state names */
	public final NavigationState[] values;
	
	/** Navigation state used to implement the double opt-in process */
	public final NavigationState nstAnsweringDoubleOptin;
	
	/** Provides the navigation states instance with the default test values.
	 *  See @{link {@link #SMSAppModuleNavigationStatesSubscription(Object[][])} */
	public SMSAppModuleNavigationStatesSubscription() { 
		this(new Object[][] {
			{cmdSubscribe,             trgLocalAcceptDoubleOptin},
			{cmdDoNotAgreeToSubscribe, trgLocalRefuseDoubleOptin}});
	}

	/** Provides the navigation states instance with custom triggers.
	 *  Apart from configuring the states defined in this module, one should also configure {@link SMSAppModuleNavigationStates#nstNewUser} and {@link SMSAppModuleNavigationStates#nstExistingUser}:<pre>
	 *  - {@link SMSAppModuleCommandsSubscription#cmdStartDoubleOptinProcess} should be added to 'nstNewUser'      with triggers {@link SMSAppModuleCommandsSubscription.CommandTriggersSubscription#trgLocalStartDoubleOptin}
	 *  - {@link SMSAppModuleCommandsSubscription#cmdUnsubscribe}             should be added to 'nstExistingUser' with triggers {@link SMSAppModuleCommandsSubscription.CommandTriggersSubscription#trgGlobalUnsubscribe}</pre>
	 *  @param nstAnsweringDoubleOptinTriggers  The list of regular expression triggers and commands to execute when the user is on the {@link #nstAnsweringDoubleOptin} navigation state. See {@link NavigationState#applyCommandTriggersData(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}*/
	public SMSAppModuleNavigationStatesSubscription(Object[][] nstAnsweringDoubleOptinTriggers) {

		nstAnsweringDoubleOptin = new NavigationState(NavigationStatesNamesSubscription.nstAnsweringDoubleOptin, nstAnsweringDoubleOptinTriggers);
		
		// the list of values
		values = new NavigationState[] {
			nstAnsweringDoubleOptin,	
		};
	}
}
