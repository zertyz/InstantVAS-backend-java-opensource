package mutua.schedule;

import java.util.LinkedHashMap;
import java.util.Set;

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
	
	private final long                  scheduledMillis;
	private final EVENT_TYPE            scheduledEvent;
	private final String                key;
	private long                        executedMillis = -1;
	private EVENT_TYPE                  executedEvent  = null;
	private boolean                     hasTimedOut    = false;
	private LinkedHashMap<String, Long> milestones     = null;
	
	public ScheduleEntryInfo(String key, long scheduledMillis, EVENT_TYPE scheduledEvent, long executedMillis, EVENT_TYPE executedEvent) {
		this.key             = key;
		this.scheduledMillis = scheduledMillis;
		this.scheduledEvent  = scheduledEvent;
		this.executedMillis  = executedMillis;
		this.executedEvent   = executedEvent;
	}

	public ScheduleEntryInfo(String key, EVENT_TYPE scheduledEvent) {
		this.key             = key;
		this.scheduledMillis = System.currentTimeMillis();
		this.scheduledEvent  = scheduledEvent;
	}
	
	public void setExecuted(String key, EVENT_TYPE executedEvent) {
		this.executedMillis = System.currentTimeMillis();
		this.executedEvent  = executedEvent;
	}
	
	public void setTimedOut() {
		hasTimedOut    = true;
		executedMillis = System.currentTimeMillis();
	}
	
	public long getScheduledMillis() {
		return scheduledMillis;
	}

	public EVENT_TYPE getScheduledEvent() {
		return scheduledEvent;
	}

	public EVENT_TYPE getExecutedEvent() {
		return executedEvent;
	}
	
	public long getExecutedMillis() {
		return executedMillis;
	}

	public long getElapsedMillis() {
		return executedMillis - scheduledMillis;
	}
	
	public void setMilestone(String milestoneName) {
		// lazy structure creation
		if (milestones == null) {
			milestones = new LinkedHashMap<String, Long>(10);
		}
		milestones.put(milestoneName, System.currentTimeMillis());
	}
	
	/** returns the milestone hashmap as a 2D array, nx2, in the form {{milestone1Name, timeMillis1}, ...} */
	public Object[][] getMilestones() {
		if (milestones == null) {
			return null;
		}
		Set<String> keys = milestones.keySet();
		Object[][] milestonesArray = new Object[keys.size()][2];
		int i=0;
		for (String key : keys) {
			milestonesArray[i][0] = key;
			milestonesArray[i][1] = milestones.get(key);
			i++;
		}
		return milestonesArray;
	}
	
	/** builds a text with elapsed times between milestones, based on the output of {@link #getMilestones()} */
	public String milestonesToString(String lastOperationName, String eventDurationLabel) {
		if (milestones == null) {
			return null;
		}
		Set<String> milestoneNames = milestones.keySet();
		StringBuffer output = new StringBuffer(112);
		output.append(key).append(": ");
		long   lastMilestoneMillis = getScheduledMillis();
		for (String milestoneName : milestoneNames) {
			long milestoneMillis = milestones.get(milestoneName);
			long elapsed         = milestoneMillis - lastMilestoneMillis;
			lastMilestoneMillis  = milestoneMillis;
			output.append(milestoneName).append(" (+").append(elapsed).append("ms); ");
		}
		long lastMilestoneElapsed = getExecutedMillis() - lastMilestoneMillis;
		if (executedEvent != null) {
			output.append(lastOperationName).append(" (+").append(lastMilestoneElapsed).append("ms); ");
		} else {
			if (hasTimedOut) {
				output.append("Event completion track lost -- timedout after ").append(getElapsedMillis()).append("ms");
			} else {
				output.append("Event not yet completed.");
			}
			return output.toString();
		}
		output.append(eventDurationLabel).append(": ").append(getElapsedMillis()).append("ms");
		return output.toString();
	}

	@Override
	public String toString() {
		return new StringBuffer("{key='").append(key != null ? key : "NULL").
		       append("',scheduledMillis=").append(scheduledMillis).
		       append(",scheduledEvent=").append(scheduledEvent != null ? scheduledEvent.toString() : "NULL").
		       append(",executedMillis=").append(executedMillis).
		       append(",executedEvent=").append(executedEvent != null ? executedEvent.toString() : "NULL").
		       append(",hasTimedOut=").append(hasTimedOut).
		       append('}').toString();
	}

}
