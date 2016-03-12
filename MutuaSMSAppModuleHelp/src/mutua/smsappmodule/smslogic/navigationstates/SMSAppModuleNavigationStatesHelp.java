package mutua.smsappmodule.smslogic.navigationstates;

import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.CommandTriggersHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.CommandNamesHelp.*;

/** <pre>
 * SMSAppModuleNavigationStatesHelp.java
 * =====================================
 * (created by luiz, Jul 17, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Help" SMSApp Module.
 * 
 * Implements the "Instant VAS SMSApp Navigation States" design pattern, as described by
 * {@link NavigationState}.
 *
 * @see NavigationState
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleNavigationStatesHelp {

	
	/** Class to be statically imported by the Commands Implementation to represent the navigation states */
	public static class NavigationStatesNamesHelp {
		/** @see SMSAppModuleNavigationStatesHelp#nstPresentingCompositeHelp */
		public final static String nstPresentingCompositeHelp = "PresentingCompositeHelp";
	}
	
	// Navigation States Definitions
	////////////////////////////////
	
	/** The list of all navigation states -- for 'SMSProcessor' to be able to deserialize state names */
	public final NavigationState[] values;
	
	/** Navigation state used to show the composite help messages,
	 *  containing command triggers to navigate from here on. */
	public final NavigationState nstPresentingCompositeHelp;
	
	/** Provides the navigation states instance with the default test values */
	public SMSAppModuleNavigationStatesHelp() { 
		this(new Object[][] {
			{cmdStartCompositeHelpDialog,      trgGlobalStartCompositeHelpDialog},
			{cmdShowNextCompositeHelpMessage,  trgLocalShowNextCompositeHelpMessage},
			{cmdShowExistingUsersFallbackHelp, trgGlobalShowExistingUsersFallbackHelp}});
	}

	/** Provides the navigation states instance with custom triggers.
	 *  @param nstPresentingCompositeHelpTriggers The list of regular expression triggers and commands to execute when the user is on the {@link #nstPresentingCompositeHelp} navigation state. See {@link NavigationState#applyCommandTriggersData(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}*/
	public SMSAppModuleNavigationStatesHelp(Object[][] nstPresentingCompositeHelpTriggers) {		
		nstPresentingCompositeHelp = new NavigationState(NavigationStatesNamesHelp.nstPresentingCompositeHelp, nstPresentingCompositeHelpTriggers);
		
		// the list of values
		values = new NavigationState[] {
			nstPresentingCompositeHelp,	
		};
	}
}