package mutua.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

/** <pre>
 * ScheduleControl.java
 * ====================
 * (created by luiz, Jan 8, 2016)
 *
 * Manages a schedule of expected events, as well as controls its realization.
 * Originally made to be used by the automated MO/MT tests, but may suit any other
 * attribution which needs to keep track of the realization of expected events.
 * Timeout and correct realization should be controlled by the analysis of the data
 * returned by 'getUnnotifiedEventsCount' and 'consumeExecutedEvents'
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ScheduleControl<EVENT_TYPE> {
	
	private IScheduleIndexingFunction<EVENT_TYPE> scheduleIndexingFunction;
	private Hashtable<String, ScheduleEntryInfo<EVENT_TYPE>> scheduledEvents;	// may as well be a ConcurrentHashMap
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
			ScheduleEntryInfo<EVENT_TYPE> eventInfo = new ScheduleEntryInfo<EVENT_TYPE>(key, toScheduleEvent);
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

	public ScheduleEntryInfo<EVENT_TYPE> getPendingEventScheduleInfo(EVENT_TYPE registeredEvent) {
		String key = scheduleIndexingFunction.getKey(registeredEvent);
		ScheduleEntryInfo<EVENT_TYPE> scheduledEvent = scheduledEvents.get(key);
		return scheduledEvent;
	}

	/** monitoring function to tell, at any time, the number of events which didn't happen yet */
	public int getUnnotifiedEventsCount() {
		return scheduledEvents.size();
	}
	
	/** completes the cycle and frees the memory of executed events, returning auditable data about them */
	public ScheduleEntryInfo<EVENT_TYPE>[] consumeExecutedEvents() {
		ArrayList<ScheduleEntryInfo<EVENT_TYPE>> oldCompletedEvents = completedEvents;
		completedEvents = new ArrayList<ScheduleEntryInfo<EVENT_TYPE>>();
		return oldCompletedEvents.toArray((ScheduleEntryInfo<EVENT_TYPE>[])new ScheduleEntryInfo<?>[oldCompletedEvents.size()]);
	}

	private final Semaphore consumingPendingSemaphore = new Semaphore(1);	// results will only be given to one thread at a time, allowing concurrent threads not to wait, since that wouldn't make sense
	private final ScheduleEntryInfo<EVENT_TYPE>[] zeroLengthEvents  = (ScheduleEntryInfo<EVENT_TYPE>[]) new ScheduleEntryInfo<?>[0];
	/** removes events not notified within a certain amount of time, giving up waiting for them to happen */
	public ScheduleEntryInfo<EVENT_TYPE>[] consumePendingOldEvents(long timeoutMillis) {
		if (consumingPendingSemaphore.tryAcquire()) try {
			ArrayList<ScheduleEntryInfo<EVENT_TYPE>> timedoutEvents = null;
			Collection<ScheduleEntryInfo<EVENT_TYPE>> events = scheduledEvents.values();
			Iterator<ScheduleEntryInfo<EVENT_TYPE>> iterator = events.iterator();
			long currentMillis = System.currentTimeMillis();
			while (iterator.hasNext()) {
				ScheduleEntryInfo<EVENT_TYPE> event = iterator.next();
				if ((currentMillis - event.getScheduledMillis()) > timeoutMillis) {
					
					// lazy creation of 'timedoutEvents' list, because the relation between the number elements returned / number of expected calls
					// is likely to be very, very low -- this strategy avoids the creation of an ArrayList (costly) and a native array (cheap)
					if (timedoutEvents == null) {
						timedoutEvents = new ArrayList<ScheduleEntryInfo<EVENT_TYPE>>();
					}
					
					event.setTimedOut();
					timedoutEvents.add(event);
					iterator.remove();
				}
			}
			if (timedoutEvents == null) {
				return zeroLengthEvents;
			} else {
				return timedoutEvents.toArray((ScheduleEntryInfo<EVENT_TYPE>[])new ScheduleEntryInfo<?>[timedoutEvents.size()]);
			}
		} finally {
			consumingPendingSemaphore.release();
		} else {
			return zeroLengthEvents;
		}
	}

}
