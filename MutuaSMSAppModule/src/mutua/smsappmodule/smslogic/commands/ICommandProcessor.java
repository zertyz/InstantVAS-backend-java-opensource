package mutua.smsappmodule.smslogic.commands;

import java.sql.SQLException;

import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * ICommandProcessor.java
 * ======================
 * (created by luiz, Jan 19, 2011)
 *
 * Represents a command processor, roughly speaking, the entity responsible for the
 * transformation of an incoming into an outgoing message.
 * 
 * Implementing classes must use the "Instant VAS SMSApp Command Processors" design pattern, described
 * bellow:
 * 
 * {@code
 * 	get it from the help module by now
 * }
 * 
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public abstract class ICommandProcessor {
	
	private String commandName;
	
	private ICommandProcessor() {}
	
	public ICommandProcessor(String commandName) {
		this.commandName = commandName;
	}

	/** Returns the human readable command name used to reference the command processor instance */
	public String getCommandName() {
		return commandName;
	}
	
	/** Method to execute the command and get the response message(s) and persistent information.
	 *  By convention when -- in the 'CommandAnswerInfo' -- the 'phone' is null, the MTs are
	 *  addressed to the same phone who sent the MO */
	public abstract CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException;

}
