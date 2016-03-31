package mutua.schedule;

import static org.junit.Assert.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import mutua.tests.SplitRun;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** <pre>
 * ScheduleControlBehavioralTests.java
 * ===================================
 * (created by luiz, Jan 8, 2016)
 *
 * Tests the scheduling control subsystem
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ScheduleControlBehavioralTests {

	@Test
	public void testEventRegistrationAndNotification() throws EventAlreadyScheduledException, EventNotScheduledException {
		
		IScheduleIndexingFunction<String> sif = new IScheduleIndexingFunction<String>() {
			
			@Override
			public String getKey(String event) {
				return event.replaceAll("From ([^\\.]*)\\. Message: .*", "letterFrom$1");
			}
		};
		
		ScheduleControl<String> schedule = new ScheduleControl<String>(sif);
		
		assertFalse("Events that were not registered yet should be considered as not pending", schedule.isEventPending("A"));
		schedule.registerEvent("A");		
		assertTrue("Events that were registered and not yet notified should be considered as pending", schedule.isEventPending("A"));
		schedule.notifyEvent("A");
		assertFalse("Events that were notified should be considered as not pending", schedule.isEventPending("A"));
		ScheduleEntryInfo<String>[] consumedEvents = schedule.consumeExecutedEvents();
		assertEquals("Wrong number of consumed events", 1, consumedEvents.length);
		assertEquals("Wrong consumed event reported", "A", consumedEvents[0].getScheduledEvent());
		

		String expectedLetterFromA = "From A. Message: Any shit. Really...";
		String actualLetterFromA   = "From A. Message: I'm, hereby, writting as promissed";
		
		schedule.registerEvent(expectedLetterFromA);	// instead of schedule.registerEvent("letterFromA");
		schedule.notifyEvent(actualLetterFromA);
		assertFalse("Usual is pending query, supplying the original event",               schedule.isEventPending(expectedLetterFromA));
		assertFalse("Usual is pending query, supplying a similar (to be executed) event", schedule.isEventPending(actualLetterFromA));
		assertFalse("Registered events may be queried by key",                            schedule.isKeyPending("letterFromA"));
		consumedEvents = schedule.consumeExecutedEvents();		
		assertEquals("Wrong number of consumed events", 1, consumedEvents.length);
		assertEquals("Consumed event reported wrong scheduled event", expectedLetterFromA, consumedEvents[0].getScheduledEvent());
		assertEquals("Consumed event reported wrong executed event",  actualLetterFromA,   consumedEvents[0].getExecutedEvent());
	}
	
	@Test
	public void testTimeMeasurement() throws EventAlreadyScheduledException, EventNotScheduledException, InterruptedException {
		ScheduleControl<String> schedule = new ScheduleControl<String>(new IScheduleIndexingFunction<String>() {
			@Override
			public String getKey(String event) {
				return event.toLowerCase();
			}
		});
		
		schedule.registerEvent("On time A");
		schedule.registerEvent("Late B");
		
		Thread.sleep(500);
		assertEquals("Wrong number of events awaiting to be executed (before any timeout)", 2, schedule.getUnnotifiedEventsCount());
		schedule.notifyEvent("on time a");
		assertEquals("Wrong number of events awaiting to be executed (after one execution)", 1, schedule.getUnnotifiedEventsCount());
		Thread.sleep(1000);
		assertEquals("Wrong number of events awaiting to be executed (after one execution and one timeout)", 1, schedule.getUnnotifiedEventsCount());
		schedule.notifyEvent("late b");
		assertEquals("Wrong number of events awaiting to be executed (after one on time and another timed out execution)", 0, schedule.getUnnotifiedEventsCount());
		
		ScheduleEntryInfo<String>[] executedEvents = schedule.consumeExecutedEvents();
		assertEquals("Wrong first executed event",              "On time A", executedEvents[0].getScheduledEvent());
		assertTrue("Wrong first executed event elapsed time -- "+executedEvents[0].getElapsedMillis()+"ms", Math.abs(500-executedEvents[0].getElapsedMillis()) < 10);
		assertEquals("Wrong first executed event",              "late b", executedEvents[1].getExecutedEvent());
		assertTrue("Wrong first executed event elapsed time -- "+executedEvents[1].getElapsedMillis()+"ms", Math.abs(1500-executedEvents[1].getElapsedMillis()) < 10);
	}
	
	@Test
	public void testNeverNotifiedEvents() throws EventAlreadyScheduledException, InterruptedException {
		ScheduleControl<String> schedule = new ScheduleControl<String>(new IScheduleIndexingFunction<String>() {
			@Override
			public String getKey(String event) {
				return event;
			}
		});
		
		final int  eventsPerPass = 100000;
		final long timeoutMillis = 1000;

		// first pass
		for (int i=0; i<eventsPerPass; i++) {
			String event = "FirstPassEvent#" + Integer.toString(i);
			schedule.registerEvent(event);
		}
		
		assertEquals("An event was consumed", eventsPerPass, schedule.getUnnotifiedEventsCount());
		Thread.sleep(timeoutMillis+1);

		// second pass
		for (int i=0; i<eventsPerPass; i++) {
			String event = "SecondPassEvent#" + Integer.toString(i);
			schedule.registerEvent(event);
		}
		
		assertEquals("Wrong number of pending events", 2*eventsPerPass, schedule.getUnnotifiedEventsCount());

		ScheduleEntryInfo<String>[] timedoutEvents = schedule.consumePendingOldEvents(timeoutMillis);
		assertTrue("Not just the first pass, but also the second pass' events were conosumed", schedule.getUnnotifiedEventsCount() > 0);
		assertEquals("First pass events didn't leave the pending list", eventsPerPass, schedule.getUnnotifiedEventsCount());
		
		// first pass check (events that just left the pending events list)
		for (int i=0; i<eventsPerPass; i++) {
			String expectedPrefix = "FirstPassEvent#"; // + Integer.toString(i); not used because order cannot be guaranteed
			String observedEvent  = timedoutEvents[i].getScheduledEvent();
			
			assertTrue("Events do not match", observedEvent.startsWith(expectedPrefix));	// order cannot be guaranteed
			assertNull("Events that timeout are events that were never notified as executed, so executedEvent must be null", timedoutEvents[i].getExecutedEvent());
			assertTrue("Event if not executed, getElapsed must return a value -- in this case, the time elapsed before considering the event as having timed out", timedoutEvents[i].getElapsedMillis() > timeoutMillis);
		}

	}
	
	@Test
	public void testMilestones() throws EventAlreadyScheduledException, InterruptedException, EventNotScheduledException {
		ScheduleControl<String> schedule = new ScheduleControl<String>(new IScheduleIndexingFunction<String>() {
			@Override
			public String getKey(String event) {
				return event;
			}
		});
		
		String eventId = "testEvent";
		
		// wait to start -- to improve sleep precision
		long start = System.currentTimeMillis();
		while (start == System.currentTimeMillis()) ;

		// milestone 1 -- event start (mo added to the queue)
		schedule.registerEvent(eventId);
		ScheduleEntryInfo<String> scheduledEntry = schedule.getPendingEventScheduleInfo(eventId);
		Thread.sleep(1);
		
		// milestone 2 -- mo consumption process started
		scheduledEntry.setMilestone("mo consumed");
		Thread.sleep(2);
		
		// milestone 3 -- mt added to the queue
		scheduledEntry.setMilestone("mt produced");
		Thread.sleep(3);
		
		// milestone 4 -- event concluded (mt sent)
		schedule.notifyEvent(eventId);
		
		// check
		ScheduleEntryInfo<String>[] executedScheduleEntries = schedule.consumeExecutedEvents();
		for (ScheduleEntryInfo<String> executedScheduleEntry : executedScheduleEntries) {
			String output = executedScheduleEntry.milestonesToString("mt consumed", "MT response time");
			assertEquals(output, "testEvent: mo consumed (+1ms); mt produced (+2ms); mt consumed (+3ms); MT response time: 6ms");
		}
		
		// again. Test error paths
		schedule.registerEvent(eventId);
		Thread.sleep(1);
		scheduledEntry = schedule.getPendingEventScheduleInfo(eventId);
		
		// no milestones
		String output = scheduledEntry.milestonesToString("mt consumed", "MT response time");
		assertNull(output);
		
		// unfinished event
		scheduledEntry.setMilestone("mo consumed");
		output = scheduledEntry.milestonesToString("mt consumed", "MT response time");
		assertEquals(output, "testEvent: mo consumed (+1ms); Event not yet completed.");
		
		// timed out event
		scheduledEntry.setTimedOut();
		output = scheduledEntry.milestonesToString("mt consumed", "MT response time");
		assertEquals(output, "testEvent: mo consumed (+1ms); Event completion track lost -- timedout after 1ms");
	}
	
	@Test
	public void testErrorConditions() throws EventAlreadyScheduledException, EventNotScheduledException {
		ScheduleControl<String> schedule = new ScheduleControl<String>(new IScheduleIndexingFunction<String>() {
			@Override
			public String getKey(String event) {
				return event;
			}
		});
		
		// register the same event twice
		schedule.registerEvent("A");
		try {
			schedule.registerEvent("A");
			fail("An exception should have been raised");
		} catch (EventAlreadyScheduledException e) {
			String registeredEvent = (String) e.getRegisteredEvent();
			String attemptedEvent  = (String) e.getAttemptedEvent();
			assertEquals("The two events should be equal", "A", registeredEvent);
			assertEquals("The two events should be equal", "A", attemptedEvent);
		}
		
		// notify the one registered and try to notify again
		schedule.notifyEvent("A");
		try {
			schedule.notifyEvent("A");
			fail("An exception should have been raised");
		} catch (EventNotScheduledException e) {}
		

		// notify the execution of an event which was never registered
		try {
			schedule.notifyEvent("This one does not exist");
			fail("An exception should have been raised");
		} catch (EventNotScheduledException e) {}
		
	}
	
	@Test
	public void testSMSEvents() throws InterruptedException {
		
		// the SMS generator's data & structures
		////////////////////////////////////////
		
		final String[] phoneNumbers = {
			"11111111",	
			"22222222",	
			"33333333",	
			"44444444",	
			"55555555",	
			"66666666",	
			"77777777",	
			"88888888",	
			"99999999",
			"00000000"
		};
		
		final String[] MOs = {
			"ping",			// return a single message to the sender
			"chat",			// send something to another user and return something to sender
			"super ping",	// return a double message to sender
		};
		
		final String[] expectedMTKeys = {
			"expected MT in response to ping MO",
			"expected MT in response to chat MO",
			"first expected MT in response to super ping MO",
			"second expected MT in response to super ping MO",
		};
		
		final ArrayBlockingQueue<SMS> MOQueue = new ArrayBlockingQueue<SMS>(phoneNumbers.length, false);
		final ArrayBlockingQueue<SMS> MTQueue = new ArrayBlockingQueue<SMS>(phoneNumbers.length, false);
		
		final int testLoopCount = 400000;
		
		// the events controller
		////////////////////////
		
		IScheduleIndexingFunction<SMS> mtKeyGenerator = new IScheduleIndexingFunction<SMS>() {
			@Override
			public String getKey(SMS mt) {
				String condensedText;
				// the following code relates the 'expectedMTKeys' to the actual MT phraseology
				if (mt.text.equals("ping back") || mt.text.equals(expectedMTKeys[0])) {
					condensedText = "PING MT";
				} else if (mt.text.equals("message for you") || mt.text.equals("message to another user") || mt.text.equals(expectedMTKeys[1])) {
					condensedText = "CHAT MT";
				} else if (mt.text.equals("return message 1") || mt.text.equals(expectedMTKeys[2])) {
					condensedText = "SUPER PING MT 1";
				} else if (mt.text.equals("return message 2") || mt.text.equals(expectedMTKeys[3])) {
					condensedText = "SUPER PING MT 2";
				} else {
					throw new NotImplementedException();
				}
				return mt.phone + "§" + condensedText;
			}
		};
		
		final ScheduleControl<SMS> schedule = new ScheduleControl<SMS>(mtKeyGenerator);
		
		
		// SMS & Events generators code
		
		final SplitRun generateMOsAndRegisterEvents = new SplitRun(-1) {			
			public void splitRun(int arg) throws Throwable {
				SMS expectedMT1;
				SMS expectedMT2;
				for (int i=0; i<testLoopCount; i++) {
					int n = i%phoneNumbers.length;
					int m = i%MOs.length;
					if (m == 0) {			// ping
						expectedMT1 = new SMS(phoneNumbers[n], expectedMTKeys[m]);
						schedule.registerEvent(expectedMT1);
					} else if (m == 1) {	// chat
						expectedMT1 = new SMS(phoneNumbers[n],     expectedMTKeys[m]);
						expectedMT2 = new SMS(phoneNumbers[n]+"_", expectedMTKeys[m]);
						schedule.registerEvent(expectedMT1);
						schedule.registerEvent(expectedMT2);
					} else if (m == 2) {	// super ping
						expectedMT1 = new SMS(phoneNumbers[n], expectedMTKeys[m]);
						expectedMT2 = new SMS(phoneNumbers[n], expectedMTKeys[m+1]);
						schedule.registerEvent(expectedMT1);
						schedule.registerEvent(expectedMT2);
					} else {
						throw new NotImplementedException();
					}
					MOQueue.put(new SMS(phoneNumbers[n], MOs[m]));
				}
//				System.out.println("generateMOsAndRegisterEvents is done");
			}
		};
		
		final long[] scheduledEventsCount = {-1};
		final SplitRun consumeMOsAndGenerateMTs = new SplitRun(-1) {
			public void splitRun(int arg) throws Throwable {
				scheduledEventsCount[0] = 0;
				for (int i=0; i<testLoopCount; i++) {
					SMS mo = MOQueue.take();
					SMS mt1 = null;
					SMS mt2 = null;
					// the following strings represent the sms application phraseology. They are, most likely, not exactly the same
					// as the ones on 'expectedMTKeys'. The 'IScheduleIndexingFunction' implementation is the responsible for
					// guaranteeing the relation between them
					if (mo.text == MOs[0]) {
						mt1 = new SMS(mo.phone, "ping back");
					} else if (mo.text == MOs[1]) {
						mt1 = new SMS(mo.phone, "message for you");
						mt2 = new SMS(mo.phone+"_", "message to another user");
					} else if (mo.text == MOs[2]) {
						mt1 = new SMS(mo.phone, "return message 1");
						mt2 = new SMS(mo.phone, "return message 2");
					} else {
						throw new NotImplementedException();
					}
					// add to the queue and register the events
					MTQueue.put(mt1);
					scheduledEventsCount[0]++;
					if (mt2 != null) {
						MTQueue.put(mt2);
						scheduledEventsCount[0]++;
					}
				}
//				System.out.println("consumeMOsAndGenerateMTs is done");
			}
		};
		
		final long[] notifiedEventsCount = {-12};
		final SplitRun consumeMTsAndNotifyEvents = new SplitRun(-1) {
			public void splitRun(int arg) throws Throwable {
				notifiedEventsCount[0] = 0;
				while (consumeMOsAndGenerateMTs.running || (MTQueue.isEmpty() == false)) {
					SMS mt = MTQueue.poll(1000, TimeUnit.MILLISECONDS);
					if (mt != null) {
						schedule.notifyEvent(mt);
						notifiedEventsCount[0]++;
					}
				}
//				System.out.println("consumeMTsAndNotifyEvents is done");
			}
		};
		
		final long[] consumedEventsCount = {-123};
		final SplitRun consolidateStatistics = new SplitRun(-1) {
			public void splitRun(int arg) throws Throwable {
				double elapsedAverage = 0;
				long n = 0;
				while (consumeMTsAndNotifyEvents.running) {
					Thread.sleep(1000);
					for (ScheduleEntryInfo<SMS> event : schedule.consumeExecutedEvents()) {
						long elapsedMillis = event.getElapsedMillis();
						n++;
						elapsedAverage += ((((double)elapsedMillis) - elapsedAverage) / ((double)n));
					}
//					System.err.println("elapsedAverage("+n+") = "+elapsedAverage);
				}
//				System.out.println("consolidateStatistics is done");
				consumedEventsCount[0] = n;
			}
		};
		
		// run the simulation
		
		SplitRun.add(generateMOsAndRegisterEvents);
		SplitRun.add(consumeMOsAndGenerateMTs);
		SplitRun.add(consumeMTsAndNotifyEvents);
		SplitRun.add(consolidateStatistics);
		
		SplitRun.runAndWaitForAll();
		
		assertEquals("Scheduled and Notified events should be the same", scheduledEventsCount[0], notifiedEventsCount[0]);
		assertEquals("All notified events should have been consumed",    notifiedEventsCount[0], consumedEventsCount[0]);
		assertEquals("No events should have been left unnotified after the test", 0, schedule.getUnnotifiedEventsCount());
	}

}

class SMS {
	String phone;
	String text;
	
	SMS(String phone, String text) {
		this.phone = phone;
		this.text  = text;
	}

	@Override
	public String toString() {
		return new StringBuffer("{phone='").append(phone).
		       append("',text='").append(text).append("'}").toString();
	}
	
	
}