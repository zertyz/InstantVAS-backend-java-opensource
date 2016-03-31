package instantvas.smsengine;

import mutua.icc.instrumentation.IInstrumentableProperty;
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
	
	IP_MO_MESSAGE ("incomingMOMessage", IncomingSMSDto.class) {
		@Override
		public void appendSerializedValue(StringBuffer buffer, Object value) {
			IncomingSMSDto mo = (IncomingSMSDto)value;
			buffer.append(mo.toString());
		}
	},
	
	IP_REQUEST_DATA("queryString", String.class),

	
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