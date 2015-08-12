package mutua.smsappmodule.smslogic.commands;

import java.util.Arrays;
	
/** <pre>
 * CommandInvocationDto.java
 * =========================
 * (created by luiz, Jan 24, 2011)
 *
 * Represents the information (parameters, etc) needed to call a 'ICommandProcessor'
 * in consequence of processing an incoming message
 */

public class CommandInvocationDto {

	private final ICommandProcessor command;
	private final String[] parameters;

	public CommandInvocationDto(ICommandProcessor command, String[] parameters) {
		this.command = command;
		this.parameters = parameters;
	}
	
	public ICommandProcessor getCommand() {
		return command;
	}
	
	public String[] getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return new StringBuffer().
			append("command='").append(command.getCommandName()).append("',").
			append("parameters={").append(Arrays.toString(parameters)).append("}").
			toString();
	}
	
}
