package mutua.schedule;

/** <pre>
 * EventNotScheduledException.java
 * ===============================
 * (created by luiz, Jan 12, 2016)
 *
 * Represents an error state where two events with the same key were registered
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class EventNotScheduledException extends Exception {

	private static final long serialVersionUID = -1780380449863356473L;
	
	private Object key;
	private Object executedEvent;
	
	public EventNotScheduledException(Object key, Object executedEvent) {
		this.key           = key;
		this.executedEvent = executedEvent;
	}
	
	@Override
	public String getMessage() {
		return toString();
	}

	@Override
	public String toString() {
		return new StringBuffer("EventNotScheduledException={key='").append(key).
		       append("',executedEvent=").append(executedEvent.toString()).append('}').toString();
	}

	

}
