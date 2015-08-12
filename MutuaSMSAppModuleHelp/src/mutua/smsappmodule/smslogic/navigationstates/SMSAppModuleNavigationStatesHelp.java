package mutua.smsappmodule.smslogic.navigationstates;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationHelp.*;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.sessions.SessionModel;

/** <pre>
 * SMSAppModuleNavigationStatesHelp.java
 * =====================================
 * (created by luiz, Jul 17, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Help" SMS Application Module
 * 
 * Implements the Mutua SMSApp Navigation States design pattern, as described by
 * {@link INavigationState}
 *
 * @see INavigationState
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleNavigationStatesHelp implements INavigationState {
	
	/** Navigation state used to show the composite help messages.
	 *  SMS Applications must extend it adding their own command triggers. */
	nstPresentingCompositeHelp {
		@Override
		public void setCommandTriggersFromConfigurationValues() {
			setCommandTriggers(new Object[][] {
				{cmdStartCompositeHelpDialog,      HELPtrgGlobalStartCompositeHelpDialog},
				{cmdShowNextCompositeHelpMessage,  HELPtrgLocalShowNextCompositeHelpMessage},
				{cmdShowExistingUsersFallbackHelp, new String[] {".*"}},
			});
		}
	},
	
	;
	
	static {
		// any redefinition of other modules' navigation states goes here.
		// please see the "Subscription" module.
	}
	
	private NavigationStateCommons nsc;
	
	
	private SMSAppModuleNavigationStatesHelp() {
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