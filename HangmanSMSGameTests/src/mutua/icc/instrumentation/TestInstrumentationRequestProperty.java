package mutua.icc.instrumentation;

/** <pre>
 * TestInstrumentationRequestProperty.java
 * =======================================
 * (created by luiz, Jan 21, 2015)
 *
 * Class that can log requests on the instrumentation framework
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class TestInstrumentationRequestProperty implements IInstrumentableProperty {

	private String testName;
	
	public TestInstrumentationRequestProperty(String testName) {
		this.testName = testName;
	}

	@Override
	public String getInstrumentationPropertyName() {
		return testName;
	}

	@Override
	public Class<?> getType() {
		return testName.getClass();
	}

	@Override
	public void appendSerializedValue(StringBuffer buffer, Object value) {
		throw new RuntimeException("This serialization rule should not have been called since 'type' is a string");
	}

}
