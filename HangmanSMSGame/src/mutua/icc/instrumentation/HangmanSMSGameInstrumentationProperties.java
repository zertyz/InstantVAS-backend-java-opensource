package mutua.icc.instrumentation;

import mutua.hangmansmsgame.smslogic.CommandDetails;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandAnswerDto;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandInvocationDto;

/** <pre>
 * InstrumentationProperties.java
 * ==============================
 * (created by luiz, Jan 21, 2015)
 *
 * Defines the available properties to participate on instrumentation events
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HangmanSMSGameInstrumentationProperties {
	
	public static IInstrumentableProperty<String>               IP_PHONE                 = new IInstrumentableProperty<String>("phone", String.class);
	
	public static IInstrumentableProperty<CommandInvocationDto> IP_COMMAND_INVOCATION    = new IInstrumentableProperty<CommandInvocationDto>("commandInvocationHandler", CommandInvocationDto.class) {
		@Override
		public void appendValueToLogLine(StringBuffer logLine, CommandInvocationDto commandInvocationHandler) {
			logLine.append(commandInvocationHandler.toString());
		}
		
	};
	
	public static IInstrumentableProperty<CommandDetails>       IP_COMMAND_DETAILS       = new IInstrumentableProperty<CommandDetails>("commandDetails", CommandDetails.class);
	
	public static IInstrumentableProperty<CommandAnswerDto>     IP_COMMAND_ANSWER        = new IInstrumentableProperty<CommandAnswerDto>("commandAnswer", CommandAnswerDto.class) {
		@Override
		public void appendValueToLogLine(StringBuffer logLine, CommandAnswerDto commandAnswer) {
		    logLine.append(commandAnswer.toString());
		}
	};


}
