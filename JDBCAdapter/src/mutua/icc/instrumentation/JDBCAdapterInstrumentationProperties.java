package mutua.icc.instrumentation;

import java.util.ArrayList;


/** <pre>
 * JDBCAdapterInstrumentationProperties.java
 * =========================================
 * (created by luiz, Jan 26, 2015)
 *
 * Defines the available properties to participate on instrumentation events
 *
 * @see JDBCAdapterInstrumentationEvents
 * @version $Id$
 * @author luiz
 */

public enum JDBCAdapterInstrumentationProperties implements	IInstrumentableProperty {


	IP_SQL_TEMPLATE            ("sqlTemplate", String.class),
	IP_SQL_TEMPLATE_PARAMETERS ("parameters",  ArrayList.class) {
		@Override
		public void appendSerializedValue(StringBuffer buffer, Object value) {
			ArrayList<?> parameters = (ArrayList<?>)value;
			boolean first = true;
			for (Object parameter : parameters) {
				if (first) {
					first = false;
				} else {
					buffer.append(',');
				}
				if (parameter instanceof String) {
					buffer.append('"').append(parameter).append('"');
				} else {
					buffer.append(parameter);
				}
			}
		}
	},
	
		
	;

	
	private String instrumentationPropertyName;
	private Class<?> instrumentationPropertyType;
	
	
	private JDBCAdapterInstrumentationProperties(String instrumentationPropertyName, Class<?> instrumentationPropertyType) {
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