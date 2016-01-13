package mutua.schedule;

/** <pre>
 * ScheduleEntryInfo.java
 * ======================
 * (created by luiz, Jan 12, 2016)
 *
 * Model each event on the 'ScheduleControl'
 *
 * @see ScheduleControl
 * @version $Id$
 * @author luiz
 */

public class ScheduleEntryInfo<EVENT_TYPE> {
	
	private String     key;
	private long       scheduledMillis;
	private EVENT_TYPE scheduledEvent;
	private long       executedMillis;
	private EVENT_TYPE executedEvent;
	
	public ScheduleEntryInfo(String key, long scheduledMillis, EVENT_TYPE scheduledEvent, long executedMillis, EVENT_TYPE executedEvent) {
		this.key             = key;
		this.scheduledMillis = scheduledMillis;
		this.scheduledEvent  = scheduledEvent;
		this.executedMillis  = executedMillis;
		this.executedEvent   = executedEvent;
	}

	public ScheduleEntryInfo(EVENT_TYPE scheduledEvent) {
		this.scheduledMillis = System.currentTimeMillis();
		this.scheduledEvent  = scheduledEvent;
	}
	
	public void setExecuted(String key, EVENT_TYPE executedEvent) {
		this.key            = key;
		this.executedMillis = System.currentTimeMillis();
		this.executedEvent  = executedEvent;
	}

	public EVENT_TYPE getScheduledEvent() {
		return scheduledEvent;
	}

	public EVENT_TYPE getExecutedEvent() {
		return executedEvent;
	}

	public long getElapsedMillis() {
		return executedMillis - scheduledMillis;
	}

	@Override
	public String toString() {
		return new StringBuffer("{key='").append(key).
		       append("',scheduledMillis=").append(scheduledMillis).
		       append(",scheduledEvent=").append(scheduledEvent.toString()).
		       append(",executedMillis=").append(executedMillis).
		       append(",executedEvent=").append(executedEvent.toString()).
		       append('}').toString();
	}

}
