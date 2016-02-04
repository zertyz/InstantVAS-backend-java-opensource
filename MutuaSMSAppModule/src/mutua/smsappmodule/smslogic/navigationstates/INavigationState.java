package mutua.smsappmodule.smslogic.navigationstates;

import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;

/** <pre>
 * INavigationState.java
 * ================
 * (created by luiz, Jul 16, 2015)
 *
 * Defines a navigation state, providing means for recognition of which command should process
 * which message.
 * 
 * Implementing classes must use the Mutua SMSApp Navigation States design pattern, described
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

public interface INavigationState {
	
	/** Returns the human readable navigation state name.
	 * Must be implemented as 'return this.name();' */
	String getNavigationStateName();

	/** Define the 'CommandTriggersDto' for each command supported by this navigation state,
	 * where 'commandTriggersData' := {{(String)commandName, (String[]|long)regular_expressions_array|timeout_millis, ...}, ...}
	 * where 'commandName' is a name of a command present in 'availableCommands' */
	void setCommandTriggers(Object[][] commandsTriggersData, ICommandProcessor[] availableCommands);
	
	/** Returns the 'CommandTriggersDto' for each command supported by the navigation state */
	CommandTriggersDto[] getCommandTriggers();
	
	/** Returns an array of strings describing the patterns & timeouts for the
	 * 'CommandTriggerDto' attending to 'command'.
	 * the implementation of this method must call 'return NavigationStateCommons.serializeCommandTrigger(commandTriggers, command);' */
	String[] serializeCommandTrigger(ICommandProcessor command);

	/** Builds a 'CommandTriggerDto' to attend to 'command' with the serialized array of strings
	 * describing the patterns & timeouts.
	 * the implementation of this method must call 'commandTriggers[command] = NavigationStateCommons.deserializeCommandTrigger(serializedData);' */
	void deserializeCommandTrigger(String[] serializedData);

}
