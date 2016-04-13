package instantvas.smsengine.producersandconsumers;

import static config.InstantVASLicense.*;

import mutua.icc.instrumentation.Instrumentation;
import mutua.schedule.EventAlreadyScheduledException;
import mutua.schedule.EventNotScheduledException;
import mutua.schedule.IScheduleIndexingFunction;
import mutua.schedule.ScheduleControl;
import mutua.schedule.ScheduleEntryInfo;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 * MOAndMTInstrumentation.java
 * ===========================
 * (created by luiz, Mar 31, 2016)
 *
 * Facility class to measures the times involved in the MOs and MTs processing
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
*/

// TODO 4/4/16: ScheduleControl pode ser unificado a Instrumentation e relatórios, enquanto resolve os problemas atuais. Pontos:
// 1) Devido a problemas de reentrância, dormimos até 10 segundos esperando pelo registro de um MO. Solução: registrar cada evento separadamente e só considerar o request como pronto quando todas as propriedades estiverem presentes. Deste modo, as instrumentações podem ser registradas em banco, logs, etc. Isto está de acordo com a visão inicial do Instrumentation
// 2) Cada uma das propriedades instrumentáveis contaria com propriedades do tipo: debug, log, measurement, average, count, method, etc.
// 3) Para eventos do tipo method, por exemplo, ao poder se relacionar propriedade A, B e C ao evento com ID=X, toma-se alguma providência: log, notificação, etc.
// 4) Juntando 1, 2 e 3, tem se a funcionalidade de ScheduleControl (aqui utilizada como um instrumentador) e pronta para substituí-lo aqui.
// 5) Pergunta: poderia ser substituído também nos testes web?

public class MOAndMTInstrumentation {

	private static final ScheduleControl<Object, Integer> schedule;
	
	static {
		schedule = new ScheduleControl<Object, Integer>(new IScheduleIndexingFunction<Object, Integer>() {
			@Override
			public Integer getKey(Object event) {
				if (event instanceof IncomingSMSDto) {
					IncomingSMSDto mo = (IncomingSMSDto)event;
					return mo.getMoId();
				} else if (event instanceof OutgoingSMSDto) {
					OutgoingSMSDto mt = (OutgoingSMSDto)event;
					return mt.getMoId();
				}
				throw new RuntimeException("ScheduleControl recceived an unkown event type: "+event.getClass());
			}
		});
	}
	
	/** Registers the timings of the MO arrival. Since, at that moment, the 'moId' isn't known, this method should called later and, thus, inform the 'arrivedMillis' */
	public static void registerLateMOArrival(Instrumentation<?, ?> log, IncomingSMSDto mo, int moId, long arrivedMillis) {
		try {
			schedule.registerEvent(mo, moId, arrivedMillis);
		} catch (EventAlreadyScheduledException e) {
			log.reportThrowable(e, "Unable to register MO event for MO/MT Instrumentation: " + mo);
		}
	}
	
	public static void reportMOEnqueuing(Instrumentation<?, ?> log, IncomingSMSDto mo, int moId) {
		ScheduleEntryInfo<Object, Integer> scheduledEntry = schedule.getPendingEventScheduleInfoByKey(moId);
		if (scheduledEntry == null) {
			log.reportThrowable(new RuntimeException(), "MO/MT instrumentation error: MO was not scheduled: " + mo);
		} else {
			scheduledEntry.setMilestone("enqueued MO");
		}
	}
	
	public static void reportMODequeuing(Instrumentation<?, ?> log, IncomingSMSDto mo) {
		for (int i=1000; i>0; i--) try {
			ScheduleEntryInfo<Object, Integer> scheduledEntry = schedule.getPendingEventScheduleInfoByKey(mo.getMoId());
			if (scheduledEntry == null) {
				// we have up to 10 seconds of 10ms sleepings for the reentrancy issue of an MO being consumed before it's registration
				// method had finished, which makes this function be called before the 'registerEvent'
				if (i == 2) {
					//log.reportThrowable(new RuntimeException(), "MO/MT instrumentation error after 10 seconds: MO was not scheduled: " + mo);
					registerLateMOArrival(log, mo, mo.getMoId(), System.currentTimeMillis());	// workarround for batch processing, where we are unable to register the MO (because we can't know the moId): register now, when i == 2, so that the milestone can be finally set when i == 1
				} else {
					Thread.sleep(10);
				}
			} else {
				scheduledEntry.setMilestone("dequeued MO");
				return;
			}
		} catch (InterruptedException e) {}
	}
	
	public static void reportMTIsReady(Instrumentation<?, ?> log, OutgoingSMSDto mt, IncomingSMSDto mo) {
		ScheduleEntryInfo<Object, Integer> scheduledEntry = schedule.getPendingEventScheduleInfoByKey(mt.getMoId());
		if (scheduledEntry == null) {
			log.reportThrowable(new RuntimeException(), "MO/MT instrumentation error: MO was not scheduled: " + mo + ". Response MT: " + mt);
		} else {
			scheduledEntry.setMilestone("response is ready");
		}
	}
	
	public static void reportMTEnqueuing(Instrumentation<?, ?> log, OutgoingSMSDto mt, IncomingSMSDto mo) {
		ScheduleEntryInfo<Object, Integer> scheduledEntry = schedule.getPendingEventScheduleInfoByKey(mt.getMoId());
		if (scheduledEntry == null) {
			log.reportThrowable(new RuntimeException(), "MO/MT instrumentation error: MO was not scheduled: " + mo + ". Response MT: " + mt);
		} else {
			scheduledEntry.setMilestone("enqueued MT");
		}
	}
	
	public static void reportMTDequeuing(Instrumentation<?, ?> log, OutgoingSMSDto mt) {
		ScheduleEntryInfo<Object, Integer> scheduledEntry = schedule.getPendingEventScheduleInfoByKey(mt.getMoId());
		if (scheduledEntry == null) {
			log.reportThrowable(new RuntimeException(), "MO/MT instrumentation error: MO was not scheduled: moId=" + mt.getMoId() + "; Response MT: " + mt);
		} else {
			scheduledEntry.setMilestone("dequeued MT");
		}
	}
	
	public static void notifyMTDelivery(Instrumentation<?, ?> log, OutgoingSMSDto mt) {
		try {
			schedule.notifyEvent(mt);
		} catch (EventNotScheduledException e) {
			// ignore, since some MOs might issue multiple MTs (and that's ok)
		}
		MOAndMTInstrumentation.logCompletedEvents(log);
		MOAndMTInstrumentation.logTimedOutEvents(log);
	}

	private static void logCompletedEvents(Instrumentation<?, ?> log) {
		ScheduleEntryInfo<Object, Integer>[] scheduledEntries = schedule.consumeExecutedEvents();
		for (ScheduleEntryInfo<Object, Integer> scheduledEntry : scheduledEntries) {
			log.reportDebug("MO Instrumentation " + scheduledEntry.milestonesToString("delivered MT", "Response time"));
		}
	}

	private static void logTimedOutEvents(Instrumentation<?, ?> log) {
		ScheduleEntryInfo<Object, Integer>[] timedOutEntries = schedule.consumePendingOldEvents(INSTRUMENT_MO_AND_MT_TIMEOUT);
		for (ScheduleEntryInfo<Object, Integer> scheduledEntry : timedOutEntries) {
			log.reportDebug("MO Instrumentation " + scheduledEntry.milestonesToString("never delivered MT", "Unknown response time"));
		}
	}
}
