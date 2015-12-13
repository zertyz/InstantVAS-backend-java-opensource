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

	/** Reconfigure the 'CommandTriggersDto' for each command supported by the navigation state,
	 * where 'commandTriggersData' := {{(ICommandProcessor)command, (String[]|long)regular_expressions_array|timeout_millis, ...}, ...} */
	void setCommandTriggers(Object[][] commandsTriggersData);
	
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

	/** invokes 'setCommandTriggers' on instantiation and reconfiguration times, with the configured values --
	 * tipically called from the configuration class' 'applyTriggerConfiguration' method.
	 * the implementation must call 'setCommandTriggers(new Object[][] {{cmd, trigger1, ...}, ...}' */
	void setCommandTriggersFromConfigurationValues();
	
}
