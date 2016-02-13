package mutua.smsappmodule.smslogic.navigationstates;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.CommandNamesProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.CommandTriggersProfile.*;

import mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;

/** <pre>
 * SMSAppModuleNavigationStatesProfile.java
 * ========================================
 * (created by luiz, Aug 3, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Profile" SMSApp Module
 * 
 * Implements the "Instant VAS SMSApp Navigation States" design pattern, as described by
 * {@link NavigationStateCommons}.
 *
 * @see NavigationStateCommons
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleNavigationStatesProfile {
	
	/** Class to be statically imported by the Commands Implementation to represent the navigation states */
	public static class NavigationStatesNamesProfile {
		/** @see SMSAppModuleNavigationStatesProfile#nstRegisteringNickname */
		public final static String nstRegisteringNickname = "RegisteringNickname";
	}
	
	// Navigation States Definitions
	////////////////////////////////
	
	/** The list of all navigation states -- for 'SMSProcessor' to be able to deserialize state names */
	public final NavigationStateCommons[] values;
	
	/** Navigation state used to interact with the user when asking for a nickname */
	public final NavigationStateCommons nstRegisteringNickname;
	
	/** Provides the navigation states instance with the default test values */
	public SMSAppModuleNavigationStatesProfile(final SMSAppModuleCommandsProfile profileCommands) { 
		this(profileCommands, new Object[][] {
			{cmdRegisterNickname,                trgGlobalRegisterNickname},
			{cmdStartAskForNicknameDialog,       trgGlobalStartAskForNicknameDialog},
			{cmdAskForNicknameDialogCancelation, trgLocalNicknameDialogCancelation},
			{cmdShowUserProfile,                 trgGlobalShowUserProfile},
			{cmdRegisterNickname,                trgLocalRegisterNickname},
		});
	}

	/** Provides the navigation states instance with custom triggers.
	 *  @param profileCommands                 The instance of commands for this module
	 *  @param nstRegisteringNicknameTriggers  The list of regular expression triggers and commands to execute when the user is on the {@link #nstRegisteringNickname} navigation state. See {@link NavigationStateCommons#setCommandTriggers(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}*/
	public SMSAppModuleNavigationStatesProfile(final SMSAppModuleCommandsProfile profileCommands, final Object[][] nstRegisteringNicknameTriggers) {		
		nstRegisteringNickname = new NavigationStateCommons(NavigationStatesNamesProfile.nstRegisteringNickname) {{
			setCommandTriggers(nstRegisteringNicknameTriggers, profileCommands.values);
		}};
		
		// the list of values
		values = new NavigationStateCommons[] {
			nstRegisteringNickname,	
		};
	}
}
