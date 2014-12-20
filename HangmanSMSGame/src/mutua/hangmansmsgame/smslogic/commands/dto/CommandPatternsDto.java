package mutua.hangmansmsgame.smslogic.commands.dto;

import java.util.Arrays;

import mutua.hangmansmsgame.smslogic.NavigationMap.ECOMMANDS;

/** <pre>
 * CommandPatternsDto.java
 * =======================
 * (created by luiz, Jan 24, 2011)
 *
 * Represents a command and the regexp patterns used to validate it as the command that
 * should process a message
 */

public class CommandPatternsDto {
	
	private final ECOMMANDS command;
	private final String[] patterns;
	
	public CommandPatternsDto(ECOMMANDS command, String[] patterns) {
		this.command  = command;
		this.patterns = patterns;
	}
	
	public ECOMMANDS getCommand() {
		return command;
	}
	
	public String[] getPatterns() {
		return patterns;
	}

	@Override
	public String toString() {
		return "CommandPatternsDto [command=" + command + ", patterns="	+ Arrays.toString(patterns) + "]";
	}
}
