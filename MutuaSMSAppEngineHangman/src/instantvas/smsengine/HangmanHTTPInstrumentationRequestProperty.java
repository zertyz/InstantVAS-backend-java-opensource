package instantvas.smsengine;


import mutua.icc.instrumentation.IInstrumentableProperty;

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

public class HangmanHTTPInstrumentationRequestProperty implements IInstrumentableProperty {

	@Override
	public String getInstrumentationPropertyName() {
		return "httpRequest";
	}

	@Override
	public Class<?> getType() {
		return String[].class;
	}

	@Override
	public void appendSerializedValue(StringBuffer buffer, Object value) {
		String[] parameters = (String[])value;
		buffer.append("parameters={param1='").append(parameters[0]).append("'}");
	}

}
