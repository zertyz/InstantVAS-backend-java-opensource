package mutua.hangmansmsgame.smslogic.commands.dto;

import java.util.Arrays;

import mutua.hangmansmsgame.dto.UserSessionDto;

/* CommandAnswerDto.java  --  $Id: CommandAnswerDto.java 847 2011-02-23 21:36:49Z asantos $
 * =====================
 * (created by luiz, Jan 19, 2011)
 *
 * Represents the set of messages and new user session that were produced after
 * the execution of a command, so actions (like sending the messages and setting
 * the new navigation state) can be taken.
 * 
 * As a convention, if 'sessionAfterCommandExecution' is null, no change
 * should be performed.
 */

public class CommandAnswerDto {
	
	private final CommandMessageDto[] responseMesages;
	private final UserSessionDto sessionAfterCommandExecution;
	
	public CommandAnswerDto(CommandMessageDto[] responseMessages, UserSessionDto sessionAfterCommandExecution) {
		this.responseMesages = responseMessages;
		this.sessionAfterCommandExecution = sessionAfterCommandExecution;
	}
	
	public CommandAnswerDto(CommandMessageDto responseMessage, UserSessionDto sessionAfterCommandExecution) {
		this(new CommandMessageDto[] {responseMessage}, sessionAfterCommandExecution);
	}

	public CommandMessageDto[] getResponseMessages() {
		return responseMesages;
	}

	public CommandMessageDto[] getResponseMesages() {
		return responseMesages;
	}

	public UserSessionDto getUserSession() {
		return sessionAfterCommandExecution;
	}

	@Override
	public String toString() {
		return "CommandAnswerDto [responseMesages=" + Arrays.toString(responseMesages) +
		       ", userSession=" + sessionAfterCommandExecution + "]";
	}
	
	
}
