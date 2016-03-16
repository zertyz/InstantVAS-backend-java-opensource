package instantvas.smsengine.producersandconsumers;

import config.InstantVASApplicationConfiguration;
import instantvas.smsengine.producersandconsumers.MTProducer.InstantVASMTEvent;
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

public class MTConsumer implements EventClient<EInstantVASMTEvents> {
	
	private Instrumentation<?, ?> log;
	private SMSOutSender mtSender;
	
	public MTConsumer(InstantVASApplicationConfiguration ivac) {
		this.log      = ivac.log;
		this.mtSender = ivac.mtSender;
	}
	
	@InstantVASMTEvent(EInstantVASMTEvents.INTERACTIVE_MT)
	public void sendMT(OutgoingSMSDto mt) {
		log.reportDebug("Sending Interactive MT -- " + mt.toString());
		try {
			mtSender.sendMessage(mt);
		} catch (Throwable t) {
			log.reportThrowable(t, "Error while sending MT -- " + mt.toString());
		}
	}
	
}