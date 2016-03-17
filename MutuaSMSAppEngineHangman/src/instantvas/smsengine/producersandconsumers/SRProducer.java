package instantvas.smsengine.producersandconsumers;

import config.InstantVASApplicationConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.imi.IndirectMethodNotFoundException;

/** <pre>
 * SRProducer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Registers the request that an user gets its Subscription status set as 'Renewed', which will be
 * processed by an instance of {@link SRConsumer}, transmitted via an instance of {@link IEventLink}.
 *
 * @version $Id$
 * @author luiz
*/

public class SRProducer extends EventServer<EInstantVASEvents> {

	public SRProducer(InstantVASApplicationConfiguration ivac,
                      EventClient<EInstantVASEvents> srConsumer) {
		super(ivac.SRpcLink);
		try {
			setConsumer(srConsumer);
		} catch (IndirectMethodNotFoundException e) {
			ivac.log.reportThrowable(e, "Error while setting srConsumer");
		}
	}

	public int dispatchAssureUserIsSubscribedEvent(String msisdn) {
		return dispatchConsumableEvent(EInstantVASEvents.SUBSCRIPTION_RENEWED, msisdn);
	}
}
