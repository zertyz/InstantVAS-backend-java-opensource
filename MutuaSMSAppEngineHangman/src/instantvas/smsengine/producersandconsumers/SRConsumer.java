package instantvas.smsengine.producersandconsumers;

import static instantvas.smsengine.SMSAppEngineInstrumentationMethods.*;

import config.InstantVASInstanceConfiguration;
import mutua.events.EventClient;
import mutua.events.IEventLink;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription;

/** <pre>
 * SRConsumer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Processes "Subscription Renewal" events generated by {@link SRProducer}, transmitted via an instance of {@link IEventLink}. 
 *
 * @version $Id$
 * @author luiz
*/

public class SRConsumer implements EventClient<EInstantVASEvents> {
	
	private SMSAppModuleCommandsSubscription subscriptionCommands;
	
	public SRConsumer(InstantVASInstanceConfiguration ivac) {
		subscriptionCommands = ivac.subscriptionCommands;
	}
	
	@InstantVASEvent(EInstantVASEvents.SUBSCRIPTION_RENEWED)
	public void assureMSISDNIsSubscribed(String msisdn) {
		try {
			startConsumeSubscribeUserRequest(msisdn);
			subscriptionCommands.subscribeUser(msisdn);
		} catch (Throwable t) {
			Instrumentation.reportThrowable(t, "Error while assuring MSISDN '"+msisdn+"' is subscribed");
		} finally {
			finishRequest();
		}
	}
}