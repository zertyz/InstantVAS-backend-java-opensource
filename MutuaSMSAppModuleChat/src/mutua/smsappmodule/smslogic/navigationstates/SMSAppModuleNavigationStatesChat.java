package mutua.smsappmodule.smslogic.navigationstates;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.CommandNamesChat.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.CommandTriggersChat.*;

import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;

/** <pre>
 * SMSAppModuleNavigationStatesChat.java
 * =====================================
 * (created by luiz, Aug 26, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Chat" SMSApp Module
 * 
 * Implements the "Instant VAS SMSApp Navigation States" design pattern, as described by
 * {@link NavigationState}.
 *
 * @see NavigationState
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleNavigationStatesChat {
	
	/** Class to be statically imported by the Commands Implementation to represent the navigation states */
	public static class NavigationStatesNamesChat {
		/** @see SMSAppModuleNavigationStatesChat#nstChattingWithSomeone */
		public final static String nstChattingWithSomeone = "ChattingWithSomeone";
	}
	
	// Navigation States Definitions
	////////////////////////////////
	
	/** The list of all navigation states -- for 'SMSProcessor' to be able to deserialize state names */
	public final NavigationState[] values;
	
	/** Navigation state used when privately chatting with someone -- 
	 *  allows the user to simply type the message (no need to provide the nickname) */
	public final NavigationState nstChattingWithSomeone;
	
	/** Provides the navigation states instance with the default test values */
	public SMSAppModuleNavigationStatesChat() { 
		this(new Object[][] {
			{cmdSendPrivateReply,       trgLocalSendPrivateReply}});
	}

	/** Provides the navigation states instance with custom triggers.
	 *  @param nstChattingWithSomeoneTriggers  The list of regular expression triggers and commands to execute when the user is on the {@link #nstChattingWithSomeone} navigation state. See {@link NavigationState#applyCommandTriggersData(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}*/
	public SMSAppModuleNavigationStatesChat(Object[][] nstChattingWithSomeoneTriggers) {		
		nstChattingWithSomeone = new NavigationState(NavigationStatesNamesChat.nstChattingWithSomeone, nstChattingWithSomeoneTriggers);
		
		// the list of values
		values = new NavigationState[] {
			nstChattingWithSomeone,	
		};
	}
}