package mutua.smsappmodule.smslogic.navigationstates;

import java.util.ArrayList;
import java.util.Arrays;

import mutua.serialization.SerializationRepository.EfficientTextualSerializationMethod;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;

/** <pre>
 * NavigationState.java
 * ====================
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

public class NavigationState {
	
	private final String navigationStateName;
	private CommandTriggersDto[] commandTriggers;
	
	// temporary navigation state data (until 'applyCommandTriggersData' is called)
	/** see {@link #applyCommandTriggersData}*/
	private Object[][] commandTriggersData;


	public NavigationState(String navigationStateName, Object[][] commandsTriggersData) {
		this.navigationStateName  = navigationStateName;
		this.commandTriggersData = commandsTriggersData;
	}
	
	/** Returns the human readable navigation state name.
	 * Must be implemented as 'return this.name();' */
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

	
	/** Called after all commands are instantiated, to map the navigation state triggers to the real {@link ICommandProcessor} instances,
	 *  transforming the regular expression patterns into their respective {@link CommandTriggersDto} for each command supported by this navigation state,
	 *  where 'commandTriggersData' := {{(String)commandName, (String...|long)regular_expressions_array|timeout_millis, ...}, ...}
	 *  and 'commandName' is a name of a command present in 'availableCommands' */
	public void applyCommandTriggersData(ICommandProcessor[] availableCommands) {
		if (commandTriggersData != null) {
			setCommandTriggers(commandTriggersData, availableCommands);
			commandTriggersData = null;
		}
	}
	
	/** @see #applyCommandTriggersData */
	public void setCommandTriggers(Object[][] commandsAndTriggersData, ICommandProcessor[] availableCommands) {
		int commandTriggersIndex = 0;
		commandTriggers = new CommandTriggersDto[commandsAndTriggersData.length];
		for (Object[] commandTriggersData : commandsAndTriggersData) {
			String commandName = (String)commandTriggersData[0];
			ICommandProcessor commandProcessor = getCommandProcessor(commandName, availableCommands);
			ArrayList<String> regularExpressions = new ArrayList<String>();
			long timeout = -1l;
			for (int i=1; i<commandTriggersData.length; i++) {
				if (commandTriggersData[i] instanceof Long) {
					timeout = (Long)commandTriggersData[i];
				} else if (commandTriggersData[i] instanceof String[]) {
					regularExpressions.addAll(Arrays.asList((String[])commandTriggersData[i]));
				} else if (commandTriggersData[i] instanceof String) {
					regularExpressions.add((String)commandTriggersData[i]);
				} else {
					throw new RuntimeException("Don't know how to create a command trigger from type '"+commandsAndTriggersData[i].getClass().getName()+"'");
				}
			}
			String[] regularExpressionsArray = regularExpressions.toArray(new String[regularExpressions.size()]);
			if (timeout == -1l) {
				commandTriggers[commandTriggersIndex++] = new CommandTriggersDto(commandProcessor, regularExpressionsArray);
			} else {
				commandTriggers[commandTriggersIndex++] = new CommandTriggersDto(commandProcessor, regularExpressionsArray, timeout);
			}
		}
	}

	/** Returns the 'CommandTriggersDto' for each command supported by the navigation state */
	public CommandTriggersDto[] getCommandTriggers() {
		return commandTriggers;
	}

	/** Returns an array of strings describing the patterns & timeouts for the
	 * 'CommandTriggerDto' attending to 'command' */
	public String[] serializeCommandTrigger(ICommandProcessor command) {
		String[] serializedCommandTriggers = new String[commandTriggers.length];
		for (int i=0; i<commandTriggers.length; i++) {
			serializedCommandTriggers[i] = commandTriggers[i].toString();
		}
		return serializedCommandTriggers;
	}

	/** Builds a 'CommandTriggerDto' to attend to 'command' with the serialized array of strings
	 * describing the patterns & timeouts */
	public void deserializeCommandTrigger(String[] serializedData) {
	}
	
	@EfficientTextualSerializationMethod
	public void toString(StringBuffer buffer) {
		buffer.append(navigationStateName);
	}

}
