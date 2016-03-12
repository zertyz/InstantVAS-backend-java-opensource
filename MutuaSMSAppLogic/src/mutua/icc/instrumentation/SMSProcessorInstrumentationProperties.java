package mutua.icc.instrumentation;

import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandInvocationDto;
import mutua.smsappmodule.smslogic.navigationstates.NavigationState;

/** <pre>
 * SMSProcessorInstrumentationProperties.java
 * ==========================================
 * (created by luiz, Jan 21, 2015)
 *
 * Defines the available properties to participate on instrumentation events
 *
 * @see SMSProcessorInstrumentationEvents
 * @version $Id$
 * @author luiz
 */

public enum SMSProcessorInstrumentationProperties implements IInstrumentableProperty {


	IP_PHONE                 ("phone", String.class),
	IP_TEXT                  ("text",  String.class),
	
	IP_STATE                 ("state", NavigationState.class) {
		@Override
		public void appendSerializedValue(StringBuffer logLine, Object value) {
			// this method will be called only if 'value' isn't member of any enumeration -- the enumeration serialization will be called otherwise
			NavigationState navigationState = (NavigationState)value;
			logLine.append("state='").
			        append(navigationState.getNavigationStateName()).
			        append("', ");
		}
	},
	
	IP_COMMAND_INVOCATION    ("commandInvocationHandler", CommandInvocationDto.class) {
		@Override
		public void appendSerializedValue(StringBuffer logLine, Object value) {
			CommandInvocationDto commandInvocationHandler = (CommandInvocationDto)value;
			logLine.append(commandInvocationHandler.toString());
		}
	},
	
	IP_COMMAND_ANSWER        ("commandAnswer", CommandAnswerDto.class) {
		@Override
		public void appendSerializedValue(StringBuffer logLine, Object value) {
			CommandAnswerDto commandAnswer = (CommandAnswerDto)value;
			logLine.append(commandAnswer.toString());
		}
	},

	
	;

	
	private String instrumentationPropertyName;
	private Class<?> instrumentationPropertyType;
	
	
	private SMSProcessorInstrumentationProperties(String instrumentationPropertyName, Class<?> instrumentationPropertyType) {
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