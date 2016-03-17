package instantvas.smsengine.producersandconsumers;

import config.InstantVASApplicationConfiguration;
import instantvas.smsengine.InstantVASHTTPInstrumentationRequestProperty;
import mutua.events.EventClient;
import mutua.events.IEventLink;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription;

/** <pre>
 * SCConsumer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Processes "Subscription Cancellation" events generated by {@link SCProducer}, transmitted via an instance of {@link IEventLink}.
 *
 * @version $Id$
 * @author luiz
*/

public class SCConsumer implements EventClient<EInstantVASEvents> {

	private Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log;
	private SMSAppModuleCommandsSubscription subscriptionCommands;
	
	public SCConsumer(InstantVASApplicationConfiguration ivac) {
		log  = ivac.log;
		subscriptionCommands = ivac.subscriptionCommands;
	}
	
	@InstantVASEvent(EInstantVASEvents.SUBSCRIPTION_CANCELLED)
	public void assureMSISDNIsNotSubscribed(String msisdn) {
		try {
			log.reportRequestStart("Assuring MSISDN '"+msisdn+"' is not a Subscriber");
			subscriptionCommands.unsubscribeUser(msisdn);
		} catch (Throwable t) {
			log.reportThrowable(t, "Error while assuring MSISDN '"+msisdn+"' is not subscribed");
		} finally {
			log.reportRequestFinish();
		}
	}
}
