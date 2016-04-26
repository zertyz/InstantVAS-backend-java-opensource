package mutua.hangmansmsgame;

import javax.servlet.http.HttpServletRequest;

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

public class HangmanHTTPInstrumentationRequestProperty implements InstrumentableProperty {

	@Override
	public String getInstrumentationPropertyName() {
		return "httpRequest";
	}

	@Override
	public Class<?> getType() {
		return org.apache.catalina.connector.RequestFacade.class;
	}

	@Override
	public void appendSerializedValue(StringBuffer buffer, Object value) {
		HttpServletRequest request = (HttpServletRequest)value;
		buffer.append("queryString='").append(request.getQueryString()).append("'");
	}

}
