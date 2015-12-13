package mutua.smsappmodule.smslogic.navigationstates;

import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;

/** <pre>
 * SMSAppModuleNavigationStates.java
 * =================================
 * (created by luiz, Jul 23, 2015)
 *
 * Declares the navigation states required by all SMS Application Modules.
 * 
 * Implements the Mutua SMSApp Navigation States design pattern, as described by 'INavigationState'
 *
 * @see INavigationState
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleNavigationStates implements INavigationState {
	
	/** Navigation state used to initiate the first interaction with the application.
	 * SMS Applications must extend it adding their own commands & triggers. */
	nstNewUser {
		@Override
		public void setCommandTriggersFromConfigurationValues() {
			// applications must add their own commands & triggers with nstNewUser.setCommandTriggers
		}
	},
	
	/** Navigation state used by registered users.
	 * SMS Applications must extend it adding their own commands & triggers, keeping in mind that
	 * the usage of the application happens from this state on. */
	nstExistingUser {
		@Override
		public void setCommandTriggersFromConfigurationValues() {
			// applications must add their own commands & triggers with nstExistingUser.setCommandTriggers
		}
	}
	
	;
	
	private NavigationStateCommons nsc;
	
	private SMSAppModuleNavigationStates() {
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