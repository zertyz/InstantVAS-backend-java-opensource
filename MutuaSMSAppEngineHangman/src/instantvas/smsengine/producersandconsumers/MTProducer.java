package instantvas.smsengine.producersandconsumers;

import static config.InstantVASLicense.*;
import static instantvas.smsengine.SMSAppEngineInstrumentationMethods.*;

import config.InstantVASInstanceConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.icc.instrumentation.Instrumentation;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 * MTProducer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Registers the generation of MTs and make them available for delivering through an instance of {@link MTConsumer},
 * transmitted via an instance of {@link IEventLink}.
 *
 * @see IResponseReceiver
 * @version $Id$
 * @author luiz
*/

public class MTProducer extends EventServer<EInstantVASEvents> implements IResponseReceiver {

	public MTProducer(InstantVASInstanceConfiguration ivac,
	                  EventClient<EInstantVASEvents> mtConsumer) {
		super(ivac.MTpcLink);
		try {
			setConsumer(mtConsumer);
		} catch (IndirectMethodNotFoundException e) {
			Instrumentation.reportThrowable(e, "Error while adding mtConsumer");
		}
	}

	@Override
	public void onMessage(OutgoingSMSDto mt, IncomingSMSDto mo) {
		
		reportMTIsReady(mo, mt);
		
//		
//		// MO and MT instrumentation -- register a new milestone: MO just finish processing
//		if (IFDEF_INSTRUMENT_MO_AND_MT_TIMES) {
//			Instrumentation.reportDebug("MT is ready!!");
//			MOAndMTInstrumentation.reportMTIsReady(mt, mo);
//		}

		dispatchConsumableEvent(EInstantVASEvents.INTERACTIVE_MT, mt);
		
		reportMTEnqueued(mo, mt);
		
		// log, report and profile that the mt was enqueued

//		// MO and MT instrumentation -- register a new milestone: MT just added to the queue
//		if (IFDEF_INSTRUMENT_MO_AND_MT_TIMES) {
//			MOAndMTInstrumentation.reportMTEnqueuing(mt, mo);
//		}

	}
	
}
