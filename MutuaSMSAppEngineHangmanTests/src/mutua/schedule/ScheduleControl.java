package mutua.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

/** <pre>
 * ScheduleControl.java
 * ====================
 * (created by luiz, Jan 8, 2016)
 *
 * Manages a schedule of expected events, as well as controls its realization.
 * Made to be used by the automated MO/MT tests. 
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ScheduleControl<EVENT_TYPE> {
	
	private int defaultTimeout;
	private IScheduleIndexingFunction<EVENT_TYPE> scheduleIndexingFunction;
	private Hashtable<String, ScheduleEntryInfo<EVENT_TYPE>> scheduledEvents;
	private ArrayList<ScheduleEntryInfo<EVENT_TYPE>> completedEvents;

	public ScheduleControl(int defaultTimeout, IScheduleIndexingFunction<EVENT_TYPE> scheduleIndexingFunction) {
		this.defaultTimeout           = defaultTimeout;
		this.scheduleIndexingFunction = scheduleIndexingFunction;
		this.scheduledEvents            = new Hashtable<String, ScheduleEntryInfo<EVENT_TYPE>>();
		this.completedEvents            = new ArrayList<ScheduleEntryInfo<EVENT_TYPE>>();
	}

	public void registerEvent(EVENT_TYPE toScheduleEvent) throws EventAlreadyScheduledException {
		String key = scheduleIndexingFunction.getKey(toScheduleEvent);
		ScheduleEntryInfo<EVENT_TYPE> alreadyScheduledEvent = scheduledEvents.get(key);
		if (alreadyScheduledEvent != null) {
			throw new EventAlreadyScheduledException(scheduledEvents.get(key).getScheduledEvent(), toScheduleEvent);
		} else {
			ScheduleEntryInfo<EVENT_TYPE> eventInfo = new ScheduleEntryInfo<EVENT_TYPE>(toScheduleEvent);
			scheduledEvents.put(key, eventInfo);
			//System.out.println("registered event "+toScheduleEvent);
		}
	}

	public void notifyEvent(EVENT_TYPE executedEvent) throws EventNotScheduledException {
		String key = scheduleIndexingFunction.getKey(executedEvent);
		ScheduleEntryInfo<EVENT_TYPE> scheduledEventInfo = scheduledEvents.remove(key);
		if (scheduledEventInfo == null) {
			throw new EventNotScheduledException(key, executedEvent);
		} else {
			scheduledEventInfo.setExecuted(key, executedEvent);
			completedEvents.add(scheduledEventInfo);
			//System.out.println("executed event "+executedEvent);
		}
	}

	public boolean isKeyPending(String key) {
		return scheduledEvents.containsKey(key);
	}
	
	public boolean isEventPending(EVENT_TYPE event) {
		return isKeyPending(scheduleIndexingFunction.getKey(event));
	}

	public int getScheduledEventsCount() {
		return scheduledEvents.size();
	}
	
	public ScheduleEntryInfo<EVENT_TYPE>[] consumeExecutedEvents() {
		ArrayList<ScheduleEntryInfo<EVENT_TYPE>> oldCompletedEvents = completedEvents;
		completedEvents = new ArrayList<ScheduleEntryInfo<EVENT_TYPE>>();
		return (ScheduleEntryInfo<EVENT_TYPE>[]) Arrays.copyOf(oldCompletedEvents.toArray(), oldCompletedEvents.size(), ((ScheduleEntryInfo<EVENT_TYPE>[]) new ScheduleEntryInfo[0]).getClass());
	}

}
