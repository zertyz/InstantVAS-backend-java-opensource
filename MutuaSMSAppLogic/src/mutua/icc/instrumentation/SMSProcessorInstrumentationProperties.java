package mutua.icc.instrumentation;

import java.lang.reflect.Method;

import mutua.serialization.SerializationRepository;
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
	
	IP_STATE                 ("state", NavigationState.class),
	
	IP_COMMAND_INVOCATION    ("commandInvocationHandler", CommandInvocationDto.class),
	IP_COMMAND_ANSWER        ("commandAnswer",            CommandAnswerDto.class),

	
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

	@Override
	public Class<?> getInstrumentationPropertyType() {
		return instrumentationPropertyType;
	}

	@Override
	public Method getTextualSerializationMethod() {
		return SerializationRepository.getSerializationMethod(instrumentationPropertyType);
	}

}