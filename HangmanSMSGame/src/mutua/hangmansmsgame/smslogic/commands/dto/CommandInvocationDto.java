package mutua.hangmansmsgame.smslogic.commands.dto;

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

}
