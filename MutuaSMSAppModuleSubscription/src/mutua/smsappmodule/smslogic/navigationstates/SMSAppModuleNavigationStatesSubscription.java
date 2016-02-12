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
 * the "Subscription" SMS Application Module
 * 
 * Implements the "Instant VAS SMSApp Navigation States" design pattern, as described by
 * {@link NavigationStateCommons}.
 * 
 * @see NavigationStateCommons
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
	public final NavigationStateCommons[] values;
	
	/** Navigation state used to implement the double opt-in process.
	 * SMS Applications may extend it adding other commands. */
	public final NavigationStateCommons nstAnsweringDoubleOptin;
	
	/** Provides the navigation states instance with the default test values.
	 *  See @{link {@link #SMSAppModuleNavigationStatesSubscription(SMSAppModuleCommandsSubscription, Object[][])} */
	public SMSAppModuleNavigationStatesSubscription(final SMSAppModuleCommandsSubscription subscriptionCommands) { 
		this(subscriptionCommands, new Object[][] {
			{cmdSubscribe,             trgLocalAcceptDoubleOptin},
			{cmdDoNotAgreeToSubscribe, ".*"}});
	}

	/** Provides the navigation states instance with custom triggers.
	 *  Apart from configuring the states defined in this module, one should also configure {@link SMSAppModuleNavigationStates#nstNewUser} and {@link SMSAppModuleNavigationStates#nstExistingUser}:<pre>
	 *  - {@link SMSAppModuleCommandsSubscription#cmdStartDoubleOptinProcess} should be added to 'nstNewUser'      with triggers {@link SMSAppModuleCommandsSubscription.CommandTriggersSubscription#trgLocalStartDoubleOptin}
	 *  - {@link SMSAppModuleCommandsSubscription#cmdUnsubscribe}             should be added to 'nstExistingUser' with triggers {@link SMSAppModuleCommandsSubscription.CommandTriggersSubscription#trgGlobalUnsubscribe}</pre>
	 *  @param subscriptionCommands             The instance of commands for this module
	 *  @param nstAnsweringDoubleOptinTriggers  The list of regular expression triggers and commands to execute when the user is on the {@link #nstAnsweringDoubleOptin} navigation state. See {@link NavigationStateCommons#setCommandTriggers(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}*/
	public SMSAppModuleNavigationStatesSubscription(final SMSAppModuleCommandsSubscription subscriptionCommands,
	                                                final Object[][] nstAnsweringDoubleOptinTriggers) {

		nstAnsweringDoubleOptin = new NavigationStateCommons(NavigationStatesNamesSubscription.nstAnsweringDoubleOptin) {{
			setCommandTriggers(nstAnsweringDoubleOptinTriggers, subscriptionCommands.values);
		}};
		
		// the list of values
		values = new NavigationStateCommons[] {
			nstAnsweringDoubleOptin,	
		};
	}
}
