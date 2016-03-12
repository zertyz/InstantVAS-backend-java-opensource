package mutua.smsappmodule.smslogic.navigationstates;

/** <pre>
 * SMSAppModuleNavigationStates.java
 * =================================
 * (created by luiz, Jul 23, 2015)
 *
 * Declares the navigation states required by all SMS Application Modules.
 * 
 * Implements the Mutua SMSApp Navigation States design pattern, as described by
 * {@link NavigationState} and, additionally, follows "Mutua Configurable Module" pattern.
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
	public final NavigationState[] values;
	
	
	/** Navigation state used to initiate the first interaction with the application and, also, the state after users subscriptions cancellation*/
	public final NavigationState nstNewUser;
	
	/** Navigation state used by registered users. Also the 'main loop' navigation state, to which all other states revert to when they finish
	 *  their businesses */
	public final NavigationState nstExistingUser;
	
	
	/** Provides the navigation states instance with custom triggers.
	 *  @param nstNewUserTriggers      The list of commands to execute when on {@link #nstNewUser} navigation state based on MO matches with the provided regular expressions. See {@link NavigationState#setCommandTriggers(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}
	 *  @param nstExistingUserTriggers The list of commands to execute when on {@link #nstExistingUser} navigation state based on MO matches with the provided regular expressions. See {@link NavigationState#setCommandTriggers(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}*/
	public SMSAppModuleNavigationStates(Object[][] nstNewUserTriggers,
	                                    Object[][] nstExistingUserTriggers) {		
		nstNewUser      = new NavigationState(NavigationStatesNames.nstNewUser,      nstNewUserTriggers);
		nstExistingUser = new NavigationState(NavigationStatesNames.nstExistingUser, nstExistingUserTriggers);
		
		// the list
		values = new NavigationState[] {
			nstNewUser,
			nstExistingUser,
		};

	}
}