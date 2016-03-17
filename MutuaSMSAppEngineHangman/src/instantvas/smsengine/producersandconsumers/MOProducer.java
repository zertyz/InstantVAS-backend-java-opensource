package instantvas.smsengine.producersandconsumers;

import config.InstantVASApplicationConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.imi.IndirectMethodNotFoundException;
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

public class MOProducer extends EventServer<EInstantVASEvents> {
	
	public MOProducer(InstantVASApplicationConfiguration ivac,
	                  EventClient<EInstantVASEvents> moConsumer) {
		super(ivac.MOpcLink);
		try {
			setConsumer(moConsumer);
		} catch (IndirectMethodNotFoundException e) {
			ivac.log.reportThrowable(e, "Error while setting moConsumer");
		}
	}
	
	public int dispatchMOForProcessing(IncomingSMSDto mo) {
		return dispatchConsumableEvent(EInstantVASEvents.MO_ARRIVED, mo);
	}

}
