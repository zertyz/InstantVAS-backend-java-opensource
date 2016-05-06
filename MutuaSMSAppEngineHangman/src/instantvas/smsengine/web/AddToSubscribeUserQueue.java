package instantvas.smsengine.web;

import java.util.HashMap;

import static instantvas.smsengine.SMSAppEngineInstrumentationMethods.*;

import config.InstantVASInstanceConfiguration;
import instantvas.smsengine.producersandconsumers.SRConsumer;
import instantvas.smsengine.producersandconsumers.SRProducer;
import mutua.icc.instrumentation.Instrumentation;

/**
 * Servlet implementation class AddToSubscriberUserQueue
 */
public class AddToSubscribeUserQueue {
	
	// responses
	private static final byte[] ACCEPTED_ANSWER = "ACCEPTED".intern().getBytes();
	private static final byte[] FAILED_ANSWER   = "FAILED"  .intern().getBytes();
	
	private final InstantVASInstanceConfiguration ivac;
	
	// event consumers/producers
	private final SRProducer                     srProducer;

	public AddToSubscribeUserQueue(InstantVASInstanceConfiguration ivac) {
		this.ivac     = ivac;
		srProducer    = new SRProducer(ivac, new SRConsumer(ivac));
	}
	
	public byte[] process(HashMap<String, String> parameters, String queryString) {
		startAddToSubscribeUserQueueRequest(queryString);
		try {
			String msisdn = parameters.get("MSISDN");
			srProducer.dispatchAssureUserIsSubscribedEvent(msisdn);
			return ACCEPTED_ANSWER;
		} catch (Throwable t) {
			Instrumentation.reportThrowable(t, "Error while subscribing user from the web");
			return FAILED_ANSWER;
		} finally {
			finishRequest();
		}
	}
	
}