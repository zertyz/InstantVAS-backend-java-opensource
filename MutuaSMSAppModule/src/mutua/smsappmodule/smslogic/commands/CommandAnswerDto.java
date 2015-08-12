package mutua.smsappmodule.smslogic.commands;

import java.util.Arrays;

import mutua.smsappmodule.smslogic.sessions.SessionModel;

/** <pre>
 * CommandAnswerDto.java
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
	
	private final CommandMessageDto[] responseMessages;
	private final SessionModel sessionAfterCommandExecution;	// now that sessionDto is mutable, consider eliminating this
	
	public CommandAnswerDto(CommandMessageDto[] responseMessages, SessionModel sessionAfterCommandExecution) {
		this.responseMessages = responseMessages;
		this.sessionAfterCommandExecution = sessionAfterCommandExecution;
	}
	
	public CommandAnswerDto(CommandMessageDto responseMessage, SessionModel sessionAfterCommandExecution) {
		this(new CommandMessageDto[] {responseMessage}, sessionAfterCommandExecution);
	}

	public CommandMessageDto[] getResponseMessages() {
		return responseMessages;
	}

	public SessionModel getUserSession() {
		return sessionAfterCommandExecution;
	}

	@Override
	public String toString() {
		String userSession = (sessionAfterCommandExecution != null) ? sessionAfterCommandExecution.toString():"null";
		return new StringBuffer().
			append("userSession={").append(userSession).append("}, ").
		    append("responseMessages=").append(Arrays.toString(responseMessages)).toString();
	}
	
	
}
