package instantvas.smsengine.producersandconsumers;

import static config.InstantVASLicense.*;

import mutua.icc.instrumentation.Instrumentation;
import mutua.schedule.IScheduleIndexingFunction;
import mutua.schedule.ScheduleControl;
import mutua.schedule.ScheduleEntryInfo;

/** <pre>
 * MOAndMTInstrumentation.java
 * ===========================
 * (created by luiz, Mar 31, 2016)
 *
 * Measures the times involded in the MOs and MTs processing
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
*/

public class MOAndMTInstrumentation {

	static final ScheduleControl<Integer> schedule;
	
	static {
		schedule = new ScheduleControl<Integer>(new IScheduleIndexingFunction<Integer>() {
			@Override
			public String getKey(Integer event) {
				return event.toString();
			}
		});
	}

	public synchronized static void logCompletedEvents(Instrumentation<?, ?> log) {
		ScheduleEntryInfo<Integer>[] scheduledEntries = schedule.consumeExecutedEvents();
		for (ScheduleEntryInfo<Integer> scheduledEntry : scheduledEntries) {
			log.reportDebug("MO Instrumentation " + scheduledEntry.milestonesToString("delivered MT", "Response time"));
		}
	}

	public synchronized static void logTimedOutEvents(Instrumentation<?, ?> log) {
		ScheduleEntryInfo<Integer>[] timedOutEntries = schedule.consumePendingOldEvents(INSTRUMENT_MO_AND_MT_TIMEOUT);
		for (ScheduleEntryInfo<Integer> scheduledEntry : timedOutEntries) {
			log.reportDebug("MO Instrumentation " + scheduledEntry.milestonesToString("never delivered MT", "Unknown response time"));
		}
	}
}
