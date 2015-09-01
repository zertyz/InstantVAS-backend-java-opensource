package mutua.smsappmodule.smslogic.navigationstates;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationChat.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.*;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;

/** <pre>
 * SMSAppModuleNavigationStatesChat.java
 * =====================================
 * (created by luiz, Aug 26, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Chat" SMS Application Module
 * 
 * Implements the Mutua SMSApp Navigation States design pattern, as described by
 * {@link INavigationState}
 *
 * @see INavigationState
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleNavigationStatesChat implements INavigationState {
	
	/** Navigation state used when privately chatting with someone -- allows the user to simply type the message (no need to provide the nickname).
	 *  SMS Applications must extend it, adding their own command triggers. */
	nstChattingWithSomeone {
		@Override
		public void setCommandTriggersFromConfigurationValues() {
			setCommandTriggers(new Object[][] {
				{cmdSendPrivateReply,                           CHATtrgLocalSendPrivateReply},
//				{SMSAppModuleCommandsHelp.cmdShowStatelessHelp, new String[] {"HELP"}},
//				{SMSAppModuleCommandsHelp.cmdShowStatefulHelp,  new String[] {".*"}},
			});
		}
	},
	
	;
	
	static {
		// defines stateful help messages for the here defined navigation states
//		SMSAppModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
//			{cmdStartDoubleOptinProcess, SUBSCRIPTIONtrgLocalStartDoubleOptin},
//			//{/*fall back help*/,         ".*"},
//		});
//		SMSAppModuleNavigationStates.nstExistingUser.setCommandTriggers(new Object[][] {
//			{cmdUnsubscribe, SUBSCRIPTIONtrgGlobalUnsubscribe},
//			//{/*fall back help*/,         ".*"},
//		});
	}
	
	private NavigationStateCommons nsc;
	
	private SMSAppModuleNavigationStatesChat() {
		nsc = new NavigationStateCommons(this);
	}

	@Override
	public String getNavigationStateName() {
		return this.name();
	}

	@Override
	public void setCommandTriggers(Object[][] commandsTriggersData) {
		nsc.setCommandTriggers(commandsTriggersData);
	}

	@Override
	public CommandTriggersDto[] getCommandTriggers() {
		return nsc.getCommandTriggers();
	}

	@Override
	public String[] serializeCommandTrigger(ICommandProcessor command) {
		return nsc.serializeCommandTrigger(command);
	}

	@Override
	public void deserializeCommandTrigger(String[] serializedData) {
		nsc.deserializeCommandTrigger(serializedData);
	}
}