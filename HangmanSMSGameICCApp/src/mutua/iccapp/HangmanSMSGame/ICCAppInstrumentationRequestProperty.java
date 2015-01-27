package mutua.iccapp.HangmanSMSGame;

import mutua.icc.instrumentation.IInstrumentableProperty;

/** <pre>
 * ICCAppInstrumentationRequestProperty.java
 * =========================================
 * (created by luiz, Jan 25, 2015)
 *
 * Class that can log requests on the instrumentation framework
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ICCAppInstrumentationRequestProperty implements IInstrumentableProperty {

	private String propertyName;
	
	public ICCAppInstrumentationRequestProperty(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public String getInstrumentationPropertyName() {
		return propertyName;
	}

	@Override
	public Class<?> getType() {
		return propertyName.getClass();
	}

	@Override
	public void appendSerializedValue(StringBuffer buffer, Object value) {
		throw new RuntimeException("This serialization rule should not have been called since 'type' is a string");
	}

}
