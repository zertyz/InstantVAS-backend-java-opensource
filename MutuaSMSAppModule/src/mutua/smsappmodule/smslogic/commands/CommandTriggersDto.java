package mutua.smsappmodule.smslogic.commands;

import java.util.Arrays;

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

	@Override
	public String toString() {
		return "command='" + command.getCommandName() + "', patterns=" +
		       Arrays.toString(patterns) + ", timeout=" + timeout;
	}
}
