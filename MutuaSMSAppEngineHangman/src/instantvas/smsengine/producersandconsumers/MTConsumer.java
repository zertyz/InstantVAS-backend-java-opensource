package instantvas.smsengine.producersandconsumers;

import static config.InstantVASLicense.IFDEF_INSTRUMENT_MO_AND_MT_TIMES;

import config.InstantVASInstanceConfiguration;
import mutua.events.EventClient;
import mutua.events.IEventLink;
import mutua.icc.instrumentation.Instrumentation;
import mutua.schedule.EventNotScheduledException;
import mutua.schedule.ScheduleEntryInfo;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.smsout.senders.SMSOutSender;

/** <pre>
 * MTConsumer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Processes an MT generated via an instance of {@link MTProducer}, transmitted via an instance of {@link IEventLink}
 *
 * @version $Id$
 * @author luiz
*/

public class MTConsumer implements EventClient<EInstantVASEvents> {
	
	private Instrumentation<?, ?> log;
	private SMSOutSender mtSender;
	
	public MTConsumer(InstantVASInstanceConfiguration ivac) {
		this.log      = ivac.log;
		this.mtSender = ivac.mtSender;
	}
	
	@InstantVASEvent(EInstantVASEvents.INTERACTIVE_MT)
	public void sendMT(OutgoingSMSDto mt) {
		
		// MO and MT instrumentation -- register a new milestone: MT just retrieved from the queue
		if (IFDEF_INSTRUMENT_MO_AND_MT_TIMES) {
			ScheduleEntryInfo<Integer> scheduledEntry = MOAndMTInstrumentation.schedule.getPendingEventScheduleInfo(mt.getMoId());
			if (scheduledEntry == null) {
				log.reportThrowable(new RuntimeException(), "MO/MT instrumentation error: MO was not scheduled: moId=" + mt.getMoId() + "; Response MT: " + mt);
			} else {
				scheduledEntry.setMilestone("dequeued MT");
			}
		}

		// deliver the MT
		try {
			mtSender.sendMessage(mt);
		} catch (Throwable t) {
			log.reportThrowable(t, "Error while sending MT -- " + mt.toString());
		}
		
		// MO and MT instrumentation -- event finished: MT sent
		if (IFDEF_INSTRUMENT_MO_AND_MT_TIMES) try {
			MOAndMTInstrumentation.schedule.notifyEvent(mt.getMoId());
			MOAndMTInstrumentation.logCompletedEvents(log);
			MOAndMTInstrumentation.logTimedOutEvents(log);
		} catch (EventNotScheduledException e) {
			// ignore, since some MOs might issue multiple MTs and that's ok
		}

	}
	
}