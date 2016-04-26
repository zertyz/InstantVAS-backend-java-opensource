package mutua.icc.instrumentation;

import java.lang.reflect.Method;

import adapters.IJDBCAdapterParameterDefinition;
import mutua.serialization.SerializationRepository;
import mutua.serialization.SerializationRepository.EfficientTextualSerializationMethod;


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

public enum JDBCAdapterInstrumentationProperties implements	InstrumentableProperty {


	IP_PREPARED_SQL            ("preparedSQL", String.class),
	IP_SQL_TEMPLATE_PARAMETERS ("parameters",  null) {
		// code based on 'AbstractPreparedProcedure#buildPreparedStatement'
		@EfficientTextualSerializationMethod
		public void toString(StringBuffer buffer) {
			Object[] parametersAndValuesPairs = (Object[])(Object)this;
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
		if (instrumentationPropertyType != null) {
			this.instrumentationPropertyType = instrumentationPropertyType;
		} else {
			this.instrumentationPropertyType = this.getClass();
		}
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