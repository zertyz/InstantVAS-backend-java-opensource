package instantvas.smsengine;


import java.lang.reflect.Method;

import mutua.icc.instrumentation.InstrumentableProperty;

/** <pre>
 * HangmanHTTPInstrumentationRequestProperty.java
 * ==============================================
 * (created by luiz, Jan 26, 2015)
 *
 * Class that can log requests on the instrumentation framework
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class InstantVASHTTPInstrumentationRequestProperty implements InstrumentableProperty {

	@Override
	public String getInstrumentationPropertyName() {
		return "httpRequest";
	}

	@Override
	public Class<?> getInstrumentationPropertyType() {
		return String[].class;
	}

	@Override
	public Method getTextualSerializationMethod() {
		return null;
	}

}
