package mutua.icc.instrumentation;


import static mutua.icc.instrumentation.SMSProcessorInstrumentationProperties.*;

/** <pre>
 * SMSProcessorInstrumentationEvents.java
 * ======================================
 * (created by luiz, Jan 21, 2015)
 *
 * Defines the available events that can participate on instrumentation logs
 *
 * @see SMSProcessorInstrumentationProperties
 * @version $Id$
 * @author luiz
 */

public enum SMSProcessorInstrumentationEvents implements IInstrumentableEvent {

	
	IE_REQUEST_FROM_NEW_USER       ("Request from new user",      IP_PHONE, IP_TEXT),
	IE_REQUEST_FROM_EXISTING_USER  ("Request from existing user", IP_PHONE, IP_STATE, IP_TEXT),
	IE_PROCESSING_COMMAND          ("Processing command",         IP_COMMAND_INVOCATION),
	IE_ANSWER_FROM_COMMAND         ("Answer from command",        IP_COMMAND_ANSWER),

	
	;
	
	
	private InstrumentableEvent instrumentableEvent;
	
	private SMSProcessorInstrumentationEvents(String name, InstrumentableProperty property) {
		instrumentableEvent = new InstrumentableEvent(name, property);
	}
	
	private SMSProcessorInstrumentationEvents(String name, InstrumentableProperty property1, InstrumentableProperty property2) {
		instrumentableEvent = new InstrumentableEvent(name, property1, property2);
	}
	
	private SMSProcessorInstrumentationEvents(String name, InstrumentableProperty property1, InstrumentableProperty property2, InstrumentableProperty property3) {
		instrumentableEvent = new InstrumentableEvent(name, property1, property2, property3);
	}
	
	private SMSProcessorInstrumentationEvents(String name) {
		instrumentableEvent = new InstrumentableEvent(name);
	}

	@Override
	public InstrumentableEvent getInstrumentableEvent() {
		return instrumentableEvent;
	}

}
