package mutua.smsappmodule.smslogic.navigationstates;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.*;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;

/** <pre>
 * SMSAppModuleNavigationStatesProfile.java
 * ========================================
 * (created by luiz, Aug 3, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Profile" SMS Application Module
 * 
 * Implements the Mutua SMSApp Navigation States design pattern, as described by
 * {@link INavigationState}
 *
 * @see INavigationState
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleNavigationStatesProfile implements INavigationState {
	
	/** Navigation state used to interact with the user when asking for a nickname.
	 *  SMS Applications must extend it adding their own command triggers. */
	nstRegisteringNickname {
		@Override
		public void setCommandTriggersFromConfigurationValues() {
			setCommandTriggers(new Object[][] {
				{cmdRegisterNickname,                           PROFILEtrgLocalRegisterNickname},
//				{SMSAppModuleCommandsHelp.cmdShowStatelessHelp, new String[] {"HELP"}},
//				{SMSAppModuleCommandsHelp.cmdShowStatefulHelp,  new String[] {".*"}},
			});
		}
	},
	
	//nstFulfillingProfileWizard,
	
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
	
	private SMSAppModuleNavigationStatesProfile() {
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
