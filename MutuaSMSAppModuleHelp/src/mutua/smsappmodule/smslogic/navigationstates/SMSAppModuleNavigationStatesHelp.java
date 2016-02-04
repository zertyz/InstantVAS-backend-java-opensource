package mutua.smsappmodule.smslogic.navigationstates;

import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.CommandTriggersHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.CommandNamesHelp.*;

/** <pre>
 * SMSAppModuleNavigationStatesHelp.java
 * =====================================
 * (created by luiz, Jul 17, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Help" SMS Application Module.
 * 
 * Implements the Mutua SMSApp Navigation States design pattern, as described by
 * {@link NavigationStateCommons} and, additionally, follows "Mutua Configurable Module" pattern.
 *
 * @see INavigationState
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
	public final NavigationStateCommons[] values;
	
	/** Navigation state used to show the composite help messages,
	 *  containing command triggers to navigate from here on. */
	public final NavigationStateCommons nstPresentingCompositeHelp;
	
	/** Provides the navigation states instance with custom triggers.
	 *  @param helpCommands                       The instance of commands for this module
	 *  @param nstPresentingCompositeHelpTriggers The list of commands to execute when on {@link #nstExistingUser} navigation state based on MO matches with the provided regular expressions. See {@link NavigationStateCommons#setCommandTriggers(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}*/
	public SMSAppModuleNavigationStatesHelp(final SMSAppModuleCommandsHelp helpCommands, final Object[][] nstPresentingCompositeHelpTriggers) {		
		nstPresentingCompositeHelp = new NavigationStateCommons(NavigationStatesNamesHelp.nstPresentingCompositeHelp) {{
			setCommandTriggers(nstPresentingCompositeHelpTriggers, helpCommands.values);
		}};
		
		// the list of values
		values = new NavigationStateCommons[] {
			nstPresentingCompositeHelp,	
		};

	}

	/** Provides the navigation states instance with the default test values */
	public SMSAppModuleNavigationStatesHelp(final SMSAppModuleCommandsHelp helpCommands) { 
		this(helpCommands, new Object[][] {
			{cmdStartCompositeHelpDialog,      trgGlobalStartCompositeHelpDialog},
			{cmdShowNextCompositeHelpMessage,  trgLocalShowNextCompositeHelpMessage},
			{cmdShowExistingUsersFallbackHelp, trgGlobalShowExistingUsersFallbackHelp}});
	}
}