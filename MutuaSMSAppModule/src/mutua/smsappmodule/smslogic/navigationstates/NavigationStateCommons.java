package mutua.smsappmodule.smslogic.navigationstates;

import java.util.ArrayList;

import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.sessions.SessionModel;

/** <pre>
 * NavigationStateCommons.java
 * ===========================
 * (created by luiz, Jul 23, 2015)
 *
 * Contains entries common to {@link INavigationState} implementing classes
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class NavigationStateCommons implements INavigationState {
	
	
	private CommandTriggersDto[] commandTriggers;


	// forces the instantiation from the other constructor
	private NavigationStateCommons() {}
	
	/** Prepares the environment for a new navigation state. 1) Register the state on the {@link SessionModel};
	 * 2) run the command triggers configuration method */
	public NavigationStateCommons(INavigationState enumItem) {
		this();
		SessionModel.registerNewNavigationState(enumItem);
	}
	
	@Override
	public String getNavigationStateName() {
		throw new RuntimeException("This method (from the 'NavigationStateCommons' class) cannot be used");
	}

	@Override
	public void setCommandTriggers(Object[][] commandsTriggersData) {
		int commandTriggersIndex = 0;
		commandTriggers = new CommandTriggersDto[commandsTriggersData.length];
		for (Object[] commandTriggersData : commandsTriggersData) {
			ICommandProcessor commandProcessor = (ICommandProcessor) commandTriggersData[0];
			String[] regularExpressions = null;
			long timeout = -1l;
			for (int i=1; i<commandTriggersData.length; i++) {
				if (commandTriggersData[i] instanceof Long) {
					timeout = (Long)commandTriggersData[i];
				} else if (commandTriggersData[i] instanceof String[]) {
					regularExpressions = (String[])commandTriggersData[i];
				} else {
					throw new RuntimeException("Don't know how to create a command trigger from type '"+commandTriggersData[i].getClass().getName()+"'");
				}
			}
			if (timeout == -1l) {
				commandTriggers[commandTriggersIndex++] = new CommandTriggersDto(commandProcessor, regularExpressions);
			} else {
				commandTriggers[commandTriggersIndex++] = new CommandTriggersDto(commandProcessor, regularExpressions, timeout);
			}
		}
	}

	@Override
	public CommandTriggersDto[] getCommandTriggers() {
		return commandTriggers;
	}

	@Override
	public String[] serializeCommandTrigger(ICommandProcessor command) {
		String[] serializedCommandTriggers = new String[commandTriggers.length];
		for (int i=0; i<commandTriggers.length; i++) {
			serializedCommandTriggers[i] = commandTriggers[i].toString();
		}
		return serializedCommandTriggers;
	}

	@Override
	public void deserializeCommandTrigger(String[] serializedData) {
	}

	@Override
	public void setCommandTriggersFromConfigurationValues() {
		throw new RuntimeException("This method (from the 'NavigationStateCommons' class) cannot be used");
	}

}
