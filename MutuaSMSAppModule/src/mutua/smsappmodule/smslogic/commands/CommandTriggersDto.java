package mutua.smsappmodule.smslogic.commands;

import mutua.serialization.SerializationRepository;
import mutua.serialization.SerializationRepository.EfficientTextualSerializationMethod;

/** <pre>
 * CommandTriggersDto.java
 * =======================
 * (created by luiz, Jan 24, 2011)
 *
 * Represents a command, the regexp patterns and the timeout used to validate it as the command that
 * should process a message or that should be activated after the elapsed time
 */

public class CommandTriggersDto {
	
	private final ICommandProcessor command;
	private final String[] patterns;
	private final long timeout;	// in milliseconds
	
	
	public CommandTriggersDto(ICommandProcessor command, String[] patterns, long timeout) {
		this.command  = command;
		this.patterns = patterns;
		this.timeout  = timeout;
	}
	
	public CommandTriggersDto(ICommandProcessor command, String[] patterns) {
		this(command, patterns, -1);
	}
	
	public CommandTriggersDto(ICommandProcessor command, long timeout) {
		this(command, new String[] {}, timeout);
	}
	
	public ICommandProcessor getCommand() {
		return command;
	}
	
	public String[] getPatterns() {
		return patterns;
	}
	
	public long getTimeout() {
		return timeout;
	}
	
	@EfficientTextualSerializationMethod
	public void toString(StringBuffer buffer) {
		buffer.
			append("{command='").
			append(command).
			append("', patterns=").
			append(SerializationRepository.serialize(buffer, patterns)).
			append(", timeout=").
			append(timeout).
			append("}");
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(buffer);
		return buffer.toString();
	}

}
