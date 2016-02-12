package mutua.smsappmodule.smslogic.navigationstates;

import java.util.Arrays;

import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;

/** <pre>
 * NavigationStateCommons.java
 * ===========================
 * (created by luiz, Jul 23, 2015)
 *
 * Represents a navigation state, used by SMS Applications and Games to retain a per user context to help
 * decide what to do with the next input.
 * 
 * Implementing classes must use the "Instant VAS SMSApp Navigation State" design pattern, described
 * bellow:
 * 
 * {@code
 * 	get it from the help module by now
 * }
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class NavigationStateCommons implements INavigationState {
	
	private final String navigationStateName;
	private CommandTriggersDto[] commandTriggers;


	public NavigationStateCommons(String navigationStateName) {
		this.navigationStateName = navigationStateName;
	}
	
	@Override
	public String getNavigationStateName() {
		return navigationStateName;
	}
	
	private static ICommandProcessor getCommandProcessor(String commandName, ICommandProcessor[] availableCommands) {
		for (ICommandProcessor cp : availableCommands) {
			if (commandName.equals(cp.getCommandName())) {
				return cp;
			}
		}
		throw new RuntimeException("Wanted command named '"+commandName+"' is not present on the availableCommands " + Arrays.toString(availableCommands));
	}

	@Override
	/** */
	public void setCommandTriggers(Object[][] commandsTriggersData, ICommandProcessor[] availableCommands) {
		int commandTriggersIndex = 0;
		commandTriggers = new CommandTriggersDto[commandsTriggersData.length];
		for (Object[] commandTriggersData : commandsTriggersData) {
			String commandName = (String)commandTriggersData[0];
			ICommandProcessor commandProcessor = getCommandProcessor(commandName, availableCommands);
			String[] regularExpressions = null;
			long timeout = -1l;
			for (int i=1; i<commandTriggersData.length; i++) {
				if (commandTriggersData[i] instanceof Long) {
					timeout = (Long)commandTriggersData[i];
				} else if (commandTriggersData[i] instanceof String[]) {
					regularExpressions = (String[])commandTriggersData[i];
				} else if (commandTriggersData[i] instanceof String) {
					regularExpressions = new String[] {(String)commandTriggersData[i]};
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

}
