package instantvas.smsengine.producersandconsumers;

import config.InstantVASApplicationConfiguration;
import instantvas.smsengine.producersandconsumers.MOProducer.InstantVASMOEvent;
import mutua.events.EventClient;
import mutua.events.IEventLink;
import mutua.hangmansmsgame.smslogic.SMSProcessor;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsin.dto.IncomingSMSDto;

/** <pre>
 * MOConsumer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Processes an MO generated via an instance of {@link MOProducer}, transmitted via an instance of {@link IEventLink}
 *
 * @version $Id$
 * @author luiz
*/

public class MOConsumer implements EventClient<EInstantVASMOEvents> {

	private Instrumentation<?, ?> log;
	private SMSProcessor smsP;
	
	public MOConsumer(InstantVASApplicationConfiguration ivac,
	                  MTProducer mtProducer) {
		log  = ivac.log;
		smsP = new SMSProcessor(mtProducer, ivac.modulesNavigationStates, ivac.modulesCommandProcessors);
	}
	
	@InstantVASMOEvent(EInstantVASMOEvents.MO_ARRIVED)
	public void processMO(IncomingSMSDto mo) {
		try {
			smsP.process(mo);
		} catch (Throwable t) {
			log.reportThrowable(t, "Error while processing MO -- "+mo.toString());
		}
	}
}
