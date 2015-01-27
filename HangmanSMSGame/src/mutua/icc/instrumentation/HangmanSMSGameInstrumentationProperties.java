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
 * @see HangmanSMSGameInstrumentationEvents
 * @version $Id$
 * @author luiz
 */

public enum HangmanSMSGameInstrumentationProperties implements IInstrumentableProperty {


	// configuration
	////////////////
	
	IP_CONFIGURATION_FIELD_NAME               ("fieldName", String.class),
	IP_CONFIGURATION_STRING_FIELD_VALUE       ("value",     String.class),
	IP_CONFIGURATION_NUMBER_FIELD_VALUE       ("value",     long.class),
	IP_CONFIGURATION_STRING_ARRAY_FIELD_VALUE ("values",    String[].class),
	
	
	// sms application
	//////////////////
	
	IP_PHONE                 ("phone", String.class),
	
	IP_COMMAND_INVOCATION    ("commandInvocationHandler", CommandInvocationDto.class) {
		@Override
		public void appendSerializedValue(StringBuffer logLine, Object value) {
			CommandInvocationDto commandInvocationHandler = (CommandInvocationDto)value;
			logLine.append(commandInvocationHandler.toString());
		}
	},
	
	IP_COMMAND_DETAILS       ("commandDetails", CommandDetails.class),
	
	IP_COMMAND_ANSWER        ("commandAnswer", CommandAnswerDto.class) {
		@Override
		public void appendSerializedValue(StringBuffer logLine, Object value) {
			CommandAnswerDto commandAnswer = (CommandAnswerDto)value;
			logLine.append(commandAnswer.toString());
		}
	};

	
	;

	
	private String instrumentationPropertyName;
	private Class<?> instrumentationPropertyType;
	
	
	private HangmanSMSGameInstrumentationProperties(String instrumentationPropertyName, Class<?> instrumentationPropertyType) {
		this.instrumentationPropertyName = instrumentationPropertyName;
		this.instrumentationPropertyType = instrumentationPropertyType;
	}

	
	// IInstrumentableProperty implementation
	/////////////////////////////////////////
	
	@Override
	public String getInstrumentationPropertyName() {
		return instrumentationPropertyName;
	}

	
	// ISerializationRule implementation
	////////////////////////////////////
	
	@Override
	public Class<?> getType() {
		return instrumentationPropertyType;
	}

	@Override
	public void appendSerializedValue(StringBuffer buffer, Object value) {
		throw new RuntimeException("Serialization Rule '" + this.getClass().getName() +
                                   "' didn't overrode 'appendSerializedValue' from " +
                                   "'ISerializationRule' for type '" + instrumentationPropertyType);
	}

}