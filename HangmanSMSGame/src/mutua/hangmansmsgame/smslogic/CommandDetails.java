package mutua.hangmansmsgame.smslogic;

import mutua.hangmansmsgame.dto.UserSessionDto;
import mutua.hangmansmsgame.i18n.IPhraseology;
import mutua.hangmansmsgame.smslogic.commands.ICommandProcessor;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandAnswerDto;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandMessageDto;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandMessageDto.EResponseMessageType;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * CommandDetails.java
 * ===================
 * (created by luiz, Dec 19, 2014)
 *
 * Defines how to process each of the commands defined in 'ECOMMANDS'
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class CommandDetails {

	public static final ICommandProcessor SHOW_WELCOME_MESSAGE = new ICommandProcessor() {

		@Override
		public CommandAnswerDto processCommand(UserSessionDto userState, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INFOWelcome(),
			                                                          EResponseMessageType.HELP);
			return new CommandAnswerDto(commandResponse, null);
		}
		
	};

}
