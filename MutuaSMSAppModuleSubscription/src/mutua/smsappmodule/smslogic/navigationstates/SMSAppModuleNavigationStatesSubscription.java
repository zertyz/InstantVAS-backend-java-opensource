package mutua.smsappmodule.smslogic.navigationstates;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.*;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;

/** <pre>
 * SMSAppModuleNavigationStatesSubscription.java
 * =============================================
 * (created by luiz, Jul 22, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Subscription" SMS Application Module
 * 
 * Implements the Mutua SMSApp Navigation States design pattern, as described by
 * {@link INavigationState}
 * 
 * Please note that {@link SMSAppModuleNavigationStates#nstNewUser} triggers are redefined here for reference
 * -- they should be extended by the application.
 *
 * @see INavigationState
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleNavigationStatesSubscription implements INavigationState {

	/** Navigation state used to implement the double opt-in process.
	 * SMS Applications may extend it adding other commands. */
	nstAnsweringDoubleOptin() {
		@Override
		public void setCommandTriggersFromConfigurationValues() {
			setCommandTriggers(new Object[][] {
				{cmdSubscribe,             SUBSCRIPTIONtrgLocalAcceptDoubleOptin},
				{cmdDoNotAgreeToSubscribe, new String[] {".*"}},
			});
		}
	},
	
	;

	// TODO this should be deleted from here and placed into the test class. The same should be done for all navigation state definition classes 
	static {
		// set triggers for the "new" and "existing" user navigation states. Applications must extend them even further
		SMSAppModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
			{cmdStartDoubleOptinProcess, SUBSCRIPTIONtrgLocalStartDoubleOptin},
			//{/*fall back help*/,         ".*"},
		});
		SMSAppModuleNavigationStates.nstExistingUser.setCommandTriggers(new Object[][] {
			{cmdUnsubscribe, SUBSCRIPTIONtrgGlobalUnsubscribe},
			//{/*fall back help*/,         ".*"},
		});
	}
	
	private NavigationStateCommons nsc;
	
	
	private SMSAppModuleNavigationStatesSubscription() {
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
