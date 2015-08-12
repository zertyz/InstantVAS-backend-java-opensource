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
 * Implementing classes must use the Mutua SMSApp Command Processors design pattern, described
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

public interface ICommandProcessor {

	/** Returns the human readable command name used to reference the command processor instance.
	 * Must be implemented as 'return this.name();' */
	String getCommandName();
	
	/** Method to execute the command and get the response message(s) and persistent information.
	 *  by convention when -- in the 'CommandAnswerInfo' -- the 'phone' is null, the MTs are
	 *  addressed to the same phone who sent the MO */
	CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException;

}
