package instantvas.smsengine.producersandconsumers;

import static config.InstantVASLicense.*;

import config.InstantVASInstanceConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.icc.instrumentation.Instrumentation;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.schedule.EventAlreadyScheduledException;
import mutua.schedule.ScheduleEntryInfo;
import mutua.smsin.dto.IncomingSMSDto;

/** <pre>
 * MOProducer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Registers the reception of MOs and make them available for processing through an instance of {@link MOConsumer},
 * transmitted via an instance of {@link IEventLink}.
 *
 * @version $Id$
 * @author luiz
*/

public class MOProducer extends EventServer<EInstantVASEvents> implements IMOProducer {
	
	private Instrumentation<?, ?> log;
	
	public MOProducer(InstantVASInstanceConfiguration ivac,
	                  EventClient<EInstantVASEvents> moConsumer) {
		super(ivac.MOpcLink);
		this.log = ivac.log;
		try {
			setConsumer(moConsumer);
		} catch (IndirectMethodNotFoundException e) {
			log.reportThrowable(e, "Error while setting moConsumer");
		}
	}
	
	@Override
	public int dispatchMOForProcessing(IncomingSMSDto mo) {
		
		int moId = dispatchConsumableEvent(EInstantVASEvents.MO_ARRIVED, mo);
		
		// MO and MT instrumentation -- create the event
		if (IFDEF_INSTRUMENT_MO_AND_MT_TIMES) try {
			MOAndMTInstrumentation.schedule.registerEvent(moId);
		} catch (EventAlreadyScheduledException e) {
			// two MOs for the same MSISDN. Marks the first MO as timed out and register the new event
			ScheduleEntryInfo<Integer> scheduledEntry = MOAndMTInstrumentation.schedule.getPendingEventScheduleInfo(moId);
			scheduledEntry.setTimedOut();
			MOAndMTInstrumentation.logTimedOutEvents(log);
			try {
				MOAndMTInstrumentation.schedule.registerEvent(moId);
			} catch (EventAlreadyScheduledException e2) {
				log.reportThrowable(e2, "Unable to register MO event for MO/MT Instrumentation: " + mo);
			}
		}
		
		return moId;
	}

}
