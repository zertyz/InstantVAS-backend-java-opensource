package mutua.schedule;

/** <pre>
 * EventAlreadyScheduledException.java
 * ===================================
 * (created by luiz, Jan 12, 2016)
 *
 * Represents an error state where two events with the same key were registered
 *
 * @see ScheduleControl
 * @version $Id$
 * @author luiz
 */

public class EventAlreadyScheduledException extends Exception {

	private static final long serialVersionUID = -1012011343293659314L;
	
	private Object registeredEvent;
	private Object attemptedEvent;
	
	public EventAlreadyScheduledException(Object registeredEvent, Object attemptedEvent) {
		this.registeredEvent = registeredEvent;
		this.attemptedEvent  = attemptedEvent;
	}

	public Object getRegisteredEvent() {
		return registeredEvent;
	}

	public Object getAttemptedEvent() {
		return attemptedEvent;
	}

	@Override
	public String getMessage() {
		return "EventAlreadyScheduledException: {scheduledEvent=" + registeredEvent.toString() + ", attemptedEvent=" + attemptedEvent.toString() + "}";
	}
	
	

}
