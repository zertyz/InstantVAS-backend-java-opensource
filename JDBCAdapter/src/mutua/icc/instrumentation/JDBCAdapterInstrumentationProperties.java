package mutua.icc.instrumentation;

import java.util.ArrayList;

import adapters.IJDBCAdapterParameterDefinition;


/** <pre>
 * JDBCAdapterInstrumentationProperties.java
 * =========================================
 * (created by luiz, Jan 26, 2015)
 *
 * Defines the available properties that can participate on instrumentation events
 *
 * @see JDBCAdapterInstrumentationEvents
 * @version $Id$
 * @author luiz
 */

public enum JDBCAdapterInstrumentationProperties implements	IInstrumentableProperty {


	IP_PREPARED_SQL            ("preparedSQL", String.class),
	IP_SQL_TEMPLATE_PARAMETERS ("parameters",  Object[].class) {
		@Override
		// code based on 'AbstractPreparedProcedure#buildPreparedStatement'
		public void appendSerializedValue(StringBuffer buffer, Object value) {
			Object[] parametersAndValuesPairs = (Object[])value;
			for (int i=0; i<parametersAndValuesPairs.length; i+=2) {
				if (i>0) {
					buffer.append(',');
				}
				String parameterName  = ((IJDBCAdapterParameterDefinition)parametersAndValuesPairs[i]).getParameterName();
				buffer.append(parameterName).append('=');
				Object parameterValue = parametersAndValuesPairs[i+1];
				if (parameterValue instanceof String) {
					buffer.append('"').append(parameterValue).append('"');
				} else {
					buffer.append(parameterValue.toString());
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