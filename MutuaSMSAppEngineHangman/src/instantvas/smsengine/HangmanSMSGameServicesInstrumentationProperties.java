package instantvas.smsengine;

import java.lang.reflect.Method;

import mutua.icc.instrumentation.IInstrumentableProperty;
import mutua.serialization.SerializationRepository;
import mutua.smsin.dto.IncomingSMSDto;

/** <pre>
 * HangmanSMSGameServicesInstrumentationProperties.java
 * ====================================================
 * (created by luiz, Feb 1, 2015)
 *
 * Defines the available properties that can participate on instrumentation events
 *
 * @see HangmanSMSGameServicesInstrumentationEvents
 * @version $Id$
 * @author luiz
 */

public enum HangmanSMSGameServicesInstrumentationProperties implements IInstrumentableProperty {

	
	// queue events
	///////////////
	
	IP_MO_MESSAGE  ("incomingMOMessage", IncomingSMSDto.class),
	IP_REQUEST_DATA("queryString",       String.class),

	
	// errors
	/////////
	
	
		
	;

	
	private String instrumentationPropertyName;
	private Class<?> instrumentationPropertyType;
	
	
	private HangmanSMSGameServicesInstrumentationProperties(String instrumentationPropertyName, Class<?> instrumentationPropertyType) {
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