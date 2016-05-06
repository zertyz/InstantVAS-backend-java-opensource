package mutua.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
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
 * @version $Id$
 * @author luiz
 */

public class ScheduleControl<EVENT_TYPE, KEY_TYPE> {
	
	private IScheduleIndexingFunction<EVENT_TYPE, KEY_TYPE>                      scheduleIndexingFunction;
	private ConcurrentHashMap<KEY_TYPE, ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>> scheduledEvents;
	private ArrayList<ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>>                   completedEvents;

	/** creates an instance to control its own set of events */
	public ScheduleControl(IScheduleIndexingFunction<EVENT_TYPE, KEY_TYPE> scheduleIndexingFunction) {
		this.scheduleIndexingFunction = scheduleIndexingFunction;
		this.scheduledEvents            = new ConcurrentHashMap<KEY_TYPE, ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>>(100, 0.75f, 3);
		this.completedEvents            = new ArrayList<ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>>();
	}

	/** The same as {@link #registerEvent(Object, Object)}, but allowing a late event registration, which requires the
	 *  'scheduledTimeMillis' to provide the correct time */
	public void registerEvent(EVENT_TYPE toScheduleEvent, KEY_TYPE eventKey, long scheduledTimeMillis) throws EventAlreadyScheduledException {
		ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE> eventInfo = new ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>(eventKey, scheduledTimeMillis, toScheduleEvent, -1, null);
		synchronized (scheduledEvents) {
			ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE> previouslyScheduledEvent = scheduledEvents.putIfAbsent(eventKey, eventInfo);
			if (previouslyScheduledEvent != null) {
				throw new EventAlreadyScheduledException(previouslyScheduledEvent.getScheduledEvent(), toScheduleEvent);
			}
		}
	}
	
	/** The same as {@link #registerEvent(Object)}, but using the provided 'eventKey' */
	public void registerEvent(EVENT_TYPE toScheduleEvent, KEY_TYPE eventKey) throws EventAlreadyScheduledException {
		registerEvent(toScheduleEvent, eventKey, System.currentTimeMillis());
	}
	/** Registers an event which is expected to happen in the future. Events are qualified by their 'key', returned by
	 *  the 'scheduleIndexingFunction' and if an event with the same key is already registered but not reported as
	 *  completed with 'notifyEvent', an exception will be thrown */
	public void registerEvent(EVENT_TYPE toScheduleEvent) throws EventAlreadyScheduledException {
		KEY_TYPE eventKey = scheduleIndexingFunction.getKey(toScheduleEvent);
		registerEvent(toScheduleEvent, eventKey);
	}

	/** The same as {@link #notifyEvent(Object)}, but using the provided 'eventKey' */
	public void notifyEvent(EVENT_TYPE executedEvent, KEY_TYPE eventKey) throws EventNotScheduledException {
		ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE> scheduledEventInfo;
		synchronized (scheduledEvents) {
			scheduledEventInfo = scheduledEvents.remove(eventKey);
		}
		if (scheduledEventInfo == null) {
			throw new EventNotScheduledException(eventKey, executedEvent);
		} else {
			scheduledEventInfo.setExecuted(eventKey, executedEvent);
			synchronized (completedEvents) {
				completedEvents.add(scheduledEventInfo);
			}
		}
	}

	/** notifies that an event previously registered with 'registerEvent' has been completed, making it available
	 *  to be inquired by 'consumeExecutedEvents' */
	public void notifyEvent(EVENT_TYPE executedEvent) throws EventNotScheduledException {
		KEY_TYPE key = scheduleIndexingFunction.getKey(executedEvent);
		notifyEvent(executedEvent, key);
	}
	
	public boolean isKeyPending(KEY_TYPE key) {
		synchronized (scheduledEvents) {
			return scheduledEvents.containsKey(key);
		}
	}
	
	public boolean isEventPending(EVENT_TYPE event) {
		return isKeyPending(scheduleIndexingFunction.getKey(event));
	}

	public ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE> getPendingEventScheduleInfoByKey(KEY_TYPE eventKey) {
		synchronized (scheduledEvents) {
			return scheduledEvents.get(eventKey);
		}
	}

	public ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE> getPendingEventScheduleInfo(EVENT_TYPE registeredEvent) {
		KEY_TYPE eventKey = scheduleIndexingFunction.getKey(registeredEvent);
		return getPendingEventScheduleInfoByKey(eventKey);
	}
	
	/** monitoring function to tell, at any time, the number of events which didn't happen yet */
	public int getUnnotifiedEventsCount() {
		synchronized (scheduledEvents) {
			return scheduledEvents.size();
		}
	}
	
	/** completes the cycle and frees the memory of executed events, returning auditable data about them */
	public ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>[] consumeExecutedEvents() {
		ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>[] completedEventsArray;
		synchronized (completedEvents) {
			completedEventsArray = completedEvents.toArray(zeroLengthEvents);
			completedEvents.clear();
		}
		return completedEventsArray;
	}

	private final Semaphore consumingPendingSemaphore = new Semaphore(1);	// results will only be given to one thread at a time, allowing concurrent threads not to wait, since that wouldn't make sense
	private final ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>[] zeroLengthEvents  = (ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>[]) new ScheduleEntryInfo<?, ?>[0];
	/** removes events not notified within a certain amount of time, giving up waiting for them to happen */
	public ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>[] consumePendingOldEvents(long timeoutMillis) {
		if (consumingPendingSemaphore.tryAcquire()) try {
			synchronized (scheduledEvents) {
				ArrayList<ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>> timedOutEvents = null;
				Collection<ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>> events = scheduledEvents.values();
				Iterator<ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>> iterator = events.iterator();
				long currentMillis = System.currentTimeMillis();
				while (iterator.hasNext()) {
					ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE> event = iterator.next();
					if ((currentMillis - event.getScheduledMillis()) > timeoutMillis) {
						
						// lazy creation of 'timedoutEvents' list, because the relation between the number elements returned / number of expected calls
						// is likely to be very, very low -- this strategy avoids the creation of an ArrayList (costly) and a native array (cheap)
						if (timedOutEvents == null) {
							timedOutEvents = new ArrayList<ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>>();
						}
						
						event.setTimedOut();
						timedOutEvents.add(event);
					}
				}
				if (timedOutEvents == null) {
					return zeroLengthEvents;
				} else {
					ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE>[] timedOutEventsArray = timedOutEvents.toArray(zeroLengthEvents);
					// remove the elements
					for (ScheduleEntryInfo<EVENT_TYPE, KEY_TYPE> timedOutEvent : timedOutEventsArray) {
						scheduledEvents.remove(timedOutEvent.getKey(), timedOutEvent);
					}
					return timedOutEventsArray;
				}
			}
		} finally {
			consumingPendingSemaphore.release();
		} else {
			return zeroLengthEvents;
		}
	}

}
