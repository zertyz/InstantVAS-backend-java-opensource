package mutua.icc.instrumentation;


import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationProperties.*;

/** <pre>
 * InstrumentationEvents.java
 * ==========================
 * (created by luiz, Jan 21, 2015)
 *
 * Defines the available events that can participate on instrumentation logs
 *
 * @see HangmanSMSGameInstrumentationProperties
 * @version $Id$
 * @author luiz
 */

public enum HangmanSMSGameInstrumentationEvents implements IInstrumentableEvent {

	
	IE_REQUEST_FROM_NEW_USER       ("Request from new user",      IP_PHONE),
	IE_REQUEST_FROM_EXISTING_USER  ("Request from existing user", IP_PHONE),
	IE_PROCESSING_COMMAND          ("Processing command",         IP_COMMAND_INVOCATION),
	IE_ANSWER_FROM_COMMAND         ("Answer from command",        IP_COMMAND_ANSWER),

	
	;
	
	
	private InstrumentableEvent instrumentableEvent;
	
	private HangmanSMSGameInstrumentationEvents(String name, InstrumentableProperty property) {
		instrumentableEvent = new InstrumentableEvent(name, property);
	}
	
	private HangmanSMSGameInstrumentationEvents(String name, InstrumentableProperty property1, InstrumentableProperty property2) {
		instrumentableEvent = new InstrumentableEvent(name, property1, property2);
	}
	
	private HangmanSMSGameInstrumentationEvents(String name) {
		instrumentableEvent = new InstrumentableEvent(name);
	}

	@Override
	public InstrumentableEvent getInstrumentableEvent() {
		return instrumentableEvent;
	}

}
