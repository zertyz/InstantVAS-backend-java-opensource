package mutua.hangmansmsgame.smslogic.commands.dto;

import java.util.Arrays;

import mutua.hangmansmsgame.smslogic.NavigationMap.ECOMMANDS;
import mutua.hangmansmsgame.smslogic.commands.ICommandProcessor;
	
/** <pre>
 * CommandInvocationDto.java
 * =========================
 * (created by luiz, Jan 24, 2011)
 *
 * Represents the information (parameters, etc) needed to call a 'ICommandProcessor'
 * in consequence of processing an incoming message
 */

public class CommandInvocationDto {

	private final ECOMMANDS command;
	private final ICommandProcessor commandProcessor;
	private final String[] parameters;

	public CommandInvocationDto(ECOMMANDS command, ICommandProcessor commandProcessor, String[] parameters) {
		this.command = command;
		this.commandProcessor = commandProcessor;
		this.parameters = parameters;
	}
	
	public ECOMMANDS getCommand() {
		return command;
	}
	
	public ICommandProcessor getCommandProcessor() {
		return commandProcessor;
	}
	
	public String[] getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return new StringBuffer().
			append("command='").append(command.name()).append("',").
			append("parameters={").append(Arrays.toString(parameters)).append("}").
			toString();
	}
	
}
