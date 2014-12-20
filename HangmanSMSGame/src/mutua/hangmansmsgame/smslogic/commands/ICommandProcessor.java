package mutua.hangmansmsgame.smslogic.commands;

import mutua.hangmansmsgame.dto.UserSessionDto;
import mutua.hangmansmsgame.i18n.IPhraseology;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandAnswerDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * ICommandProcessor.java  --  $Id: ICommandProcessor.java 847 2011-02-23 21:36:49Z asantos $
 * ======================
 * (created by luiz, Jan 19, 2011)
 *
 * Defines a command processor able to transform an incoming into an outgoing message
 * 
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public interface ICommandProcessor {

	/**
	 * method to get the response message.
	 * by convention when -- in the 'CommandAnswerInfo' -- the 'phone' is null, the messages are
	 * addressed to the same person that just sent the incoming message
	 */
	CommandAnswerDto processCommand(UserSessionDto userState, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases);

}
