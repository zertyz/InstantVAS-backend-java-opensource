package mutua.smsappmodule.smslogic.navigationstates;

import mutua.smsappmodule.smslogic.commands.ICommandProcessor;

/** <pre>
 * SMSAppModuleNavigationStates.java
 * =================================
 * (created by luiz, Jul 23, 2015)
 *
 * Declares the navigation states required by all SMS Application Modules.
 * 
 * Implements the Mutua SMSApp Navigation States design pattern, as described by
 * {@link NavigationStateCommons} and, additionally, follows "Mutua Configurable Module" pattern.
 *
 * @see INavigationState
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleNavigationStates {

	
	/** Class to be statically imported by the Commands Implementation to represent the navigation states */
	public static class NavigationStatesNames {
		public final static String nstNewUser      = "NewUser";
		public final static String nstExistingUser = "ExistingUser";
	}

	// Navigation States Definitions
	////////////////////////////////
	
	/** The list of all navigation states -- for 'SMSProcessor' to be able to deserialize state names */
	public final NavigationStateCommons[] values;
	
	
	/** Navigation state used to initiate the first interaction with the application and, also, the state after users subscriptions cancellation*/
	public final NavigationStateCommons nstNewUser;
	
	/** Navigation state used by registered users. Also the 'main loop' navigation state, to which all other states revert to when they finish
	 *  their businesses */
	public final NavigationStateCommons nstExistingUser;
	
	
	/** Provides the navigation states instance with custom triggers.
	 *  @param availableCommands       The instances of commands to be used on the following parameters
	 *  @param nstNewUserTriggers      The list of commands to execute when on {@link #nstNewUser} navigation state based on MO matches with the provided regular expressions. See {@link NavigationStateCommons#setCommandTriggers(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}
	 *  @param nstExistingUserTriggers The list of commands to execute when on {@link #nstExistingUser} navigation state based on MO matches with the provided regular expressions. See {@link NavigationStateCommons#setCommandTriggers(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}*/
	public SMSAppModuleNavigationStates(final ICommandProcessor[] availableCommands,
	                                    final Object[][] nstNewUserTriggers,
	                                    final Object[][] nstExistingUserTriggers) {		
		nstNewUser = new NavigationStateCommons(NavigationStatesNames.nstNewUser) {{
			setCommandTriggers(nstNewUserTriggers, availableCommands);
		}};
		nstExistingUser = new NavigationStateCommons(NavigationStatesNames.nstExistingUser) {{
			setCommandTriggers(nstExistingUserTriggers, availableCommands);
		}};
		
		// the list
		values = new NavigationStateCommons[] {
			nstNewUser,
			nstExistingUser,
		};

	}
}