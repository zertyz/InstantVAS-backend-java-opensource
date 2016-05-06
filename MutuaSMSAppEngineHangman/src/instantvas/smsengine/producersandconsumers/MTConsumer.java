package instantvas.smsengine.producersandconsumers;

import static config.InstantVASLicense.IFDEF_INSTRUMENT_MO_AND_MT_TIMES;

import config.InstantVASInstanceConfiguration;
import mutua.events.EventClient;
import mutua.events.IEventLink;
import mutua.icc.instrumentation.Instrumentation;
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
	
	private SMSOutSender mtSender;
	
	public MTConsumer(InstantVASInstanceConfiguration ivac) {
		this.mtSender = ivac.mtSender;
	}
	
	@InstantVASEvent(EInstantVASEvents.INTERACTIVE_MT)
	public void sendMT(OutgoingSMSDto mt) {
		
		// MO and MT instrumentation -- register a new milestone: MT just retrieved from the queue
		if (IFDEF_INSTRUMENT_MO_AND_MT_TIMES) {
			MOAndMTInstrumentation.reportMTDequeuing(mt);
		}

		// deliver the MT
		try {
			mtSender.sendMessage(mt);
		} catch (Throwable t) {
			Instrumentation.reportThrowable(t, "Error while sending MT -- " + mt.toString());
		}
		
		// MO and MT instrumentation -- event finished: MT sent
		if (IFDEF_INSTRUMENT_MO_AND_MT_TIMES) {
			MOAndMTInstrumentation.notifyMTDelivery(mt);
		}

	}
	
}