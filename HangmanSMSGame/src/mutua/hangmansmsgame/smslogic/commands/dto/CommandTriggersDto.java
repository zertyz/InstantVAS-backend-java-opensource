package mutua.hangmansmsgame.smslogic.commands.dto;

import java.util.Arrays;

import mutua.hangmansmsgame.smslogic.NavigationMap.ECOMMANDS;

/** <pre>
 * CommandTriggersDto.java
 * =======================
 * (created by luiz, Jan 24, 2011)
 *
 * Represents a command, the regexp patterns and the timeout used to validate it as the command that
 * should process a message or that should be activated after the elapsed time
 */

public class CommandTriggersDto {
	
	private final ECOMMANDS command;
	private final String[] patterns;
	private final long timeout;	// in milliseconds
	
	
	public CommandTriggersDto(ECOMMANDS command, String[] patterns, long timeout) {
		this.command  = command;
		this.patterns = patterns;
		this.timeout  = timeout;
	}
	
	public CommandTriggersDto(ECOMMANDS command, String[] patterns) {
		this(command, patterns, -1);
	}
	
	public CommandTriggersDto(ECOMMANDS command, long timeout) {
		this(command, new String[] {}, timeout);
	}
	
	public ECOMMANDS getCommand() {
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
		return "command='" + command + "', patterns=" +
		       Arrays.toString(patterns) + ", timeout=" + timeout;
	}
}
