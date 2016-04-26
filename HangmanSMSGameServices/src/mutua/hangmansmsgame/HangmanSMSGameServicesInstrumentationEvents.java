package mutua.hangmansmsgame;

import static mutua.hangmansmsgame.HangmanSMSGameServicesInstrumentationProperties.*;
import mutua.icc.instrumentation.IInstrumentableEvent;
import mutua.icc.instrumentation.InstrumentableProperty;
import mutua.icc.instrumentation.InstrumentableEvent;

/** <pre>
 * HangmanSMSGameServicesInstrumentationEvents.java
 * ================================================
 * (created by luiz, Feb 1, 2015)
 *
 * Defines the available events that can participate on instrumentation logs
 *
 * @see HangmanSMSGameServicesInstrumentationEvents
 * @version $Id$
 * @author luiz
 */

public enum HangmanSMSGameServicesInstrumentationEvents implements IInstrumentableEvent {

	
	// queue events
	///////////////
	
	IE_MESSAGE_ACCEPTED  ("MO message accepted on the queue", IP_MO_MESSAGE),
	IE_MESSAGE_REJECTED  ("MO message rejected by the queue"),
	
	
	// errors
	/////////
	
	
	;
	
	
	private InstrumentableEvent instrumentableEvent;
	
	private HangmanSMSGameServicesInstrumentationEvents(String name, InstrumentableProperty property) {
		instrumentableEvent = new InstrumentableEvent(name, property);
	}
	
	private HangmanSMSGameServicesInstrumentationEvents(String name, InstrumentableProperty property1, InstrumentableProperty property2) {
		instrumentableEvent = new InstrumentableEvent(name, property1, property2);
	}
	
	private HangmanSMSGameServicesInstrumentationEvents(String name) {
		instrumentableEvent = new InstrumentableEvent(name);
	}

	@Override
	public InstrumentableEvent getInstrumentableEvent() {
		return instrumentableEvent;
	}

}