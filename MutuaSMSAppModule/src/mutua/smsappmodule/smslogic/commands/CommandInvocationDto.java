package mutua.smsappmodule.smslogic.commands;

import mutua.serialization.SerializationRepository;
import mutua.serialization.SerializationRepository.EfficientTextualSerializationMethod;
	
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
	
	@EfficientTextualSerializationMethod
	public void toString(StringBuffer buffer) {
		buffer.append("command='").append(command.getCommandName()).append("',").
		       append("parameters={");
		SerializationRepository.serialize(buffer, parameters);
		buffer.append("}");
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(buffer);
		return buffer.toString();
	}
	
}
