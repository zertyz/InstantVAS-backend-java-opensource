package mutua.smsappmodule.smslogic.commands;

import java.lang.reflect.Method;

import mutua.serialization.SerializationRepository;
import mutua.serialization.SerializationRepository.EfficientTextualSerializationMethod;
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
	
	private static final Method responseMessagesSerializationMethod = SerializationRepository.getSerializationMethod(CommandMessageDto.class);
	@EfficientTextualSerializationMethod
	public void toString(StringBuffer buffer) {
		String userSession = (sessionAfterCommandExecution != null) ? sessionAfterCommandExecution.toString() : "NULL";
		buffer.append("{userSession=").append(userSession).append(", ").append("responseMessages=");
		SerializationRepository.serialize(buffer, responseMessagesSerializationMethod, responseMessages);
		buffer.append('}');
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(buffer);
		return buffer.toString();
	}
	
	
}
