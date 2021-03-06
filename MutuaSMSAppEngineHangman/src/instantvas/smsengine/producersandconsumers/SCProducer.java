package instantvas.smsengine.producersandconsumers;

import config.InstantVASInstanceConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.icc.instrumentation.Instrumentation;
import mutua.imi.IndirectMethodNotFoundException;

/** <pre>
 * SCProducer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Registers the request that an user gets its Subscription status set as 'Cancelled', which will be
 * processed by an instance of {@link SCConsumer}, transmitted ia an instance of {@link IEventLink}.
 *
 * @version $Id$
 * @author luiz
*/

public class SCProducer extends EventServer<EInstantVASEvents> {

	public SCProducer(InstantVASInstanceConfiguration ivac,
	                  EventClient<EInstantVASEvents> scConsumer) {
		super(ivac.SCpcLink);
		try {
			setConsumer(scConsumer);
		} catch (IndirectMethodNotFoundException e) {
			Instrumentation.reportThrowable(e, "Error while setting srConsumer");
		}
	}

	public int dispatchAssureUserIsNotSubscribedEvent(String msisdn) {
		return dispatchConsumableEvent(EInstantVASEvents.SUBSCRIPTION_CANCELLED, msisdn);
	}

}
