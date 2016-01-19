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
 * Originally made to be used by the automated MO/MT tests, but may suit any other
 * attribution which needs to keep track of the realization of expected events.
 * Timeout and correct realization should be controlled by the analisys of the data
 * returned by 'getUnnotifiedEventsCount' and 'consumeExecutedEvents'
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ScheduleControl<EVENT_TYPE> {
	
	private IScheduleIndexingFunction<EVENT_TYPE> scheduleIndexingFunction;
	private Hashtable<String, ScheduleEntryInfo<EVENT_TYPE>> scheduledEvents;
	private ArrayList<ScheduleEntryInfo<EVENT_TYPE>> completedEvents;

	/** creates an instance to control its own set of events */
	public ScheduleControl(IScheduleIndexingFunction<EVENT_TYPE> scheduleIndexingFunction) {
		this.scheduleIndexingFunction = scheduleIndexingFunction;
		this.scheduledEvents            = new Hashtable<String, ScheduleEntryInfo<EVENT_TYPE>>();
		this.completedEvents            = new ArrayList<ScheduleEntryInfo<EVENT_TYPE>>();
	}

	/** Registers an event which is expected to happen in the future. Events are qualified by their 'key', returned by
	 *  the 'scheduleIndexingFunction' and if an event with the same key is already registered but not reported as
	 *  completed with 'notifyEvent', an exception will be thrown */
	public void registerEvent(EVENT_TYPE toScheduleEvent) throws EventAlreadyScheduledException {
		String key = scheduleIndexingFunction.getKey(toScheduleEvent);
		ScheduleEntryInfo<EVENT_TYPE> alreadyScheduledEvent = scheduledEvents.get(key);
		if (alreadyScheduledEvent != null) {
			throw new EventAlreadyScheduledException(scheduledEvents.get(key).getScheduledEvent(), toScheduleEvent);
		} else {
			ScheduleEntryInfo<EVENT_TYPE> eventInfo = new ScheduleEntryInfo<EVENT_TYPE>(toScheduleEvent);
			scheduledEvents.put(key, eventInfo);
		}
	}

	/** notifies that an event previously registered with 'registerEvent' has been completed, making it available
	 *  to be inquired by 'consumeExecutedEvents' */
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

	/** monitoring function to tell, at any time, the number of events which didn't happen yet */
	public int getUnnotifiedEventsCount() {
		return scheduledEvents.size();
	}
	
	/** completes the cycle and frees the memory of executed events, returning auditable data about them */
	public ScheduleEntryInfo<EVENT_TYPE>[] consumeExecutedEvents() {
		ArrayList<ScheduleEntryInfo<EVENT_TYPE>> oldCompletedEvents = completedEvents;
		completedEvents = new ArrayList<ScheduleEntryInfo<EVENT_TYPE>>();
		return (ScheduleEntryInfo<EVENT_TYPE>[]) Arrays.copyOf(oldCompletedEvents.toArray(), oldCompletedEvents.size(), ((ScheduleEntryInfo<EVENT_TYPE>[]) new ScheduleEntryInfo[0]).getClass());
	}

}
