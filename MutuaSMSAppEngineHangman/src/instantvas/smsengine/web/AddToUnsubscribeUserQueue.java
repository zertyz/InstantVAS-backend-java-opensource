package instantvas.smsengine.web;

import java.util.HashMap;

import static instantvas.smsengine.SMSAppEngineInstrumentationMethods.*;

import config.InstantVASInstanceConfiguration;
import instantvas.smsengine.producersandconsumers.SCConsumer;
import instantvas.smsengine.producersandconsumers.SCProducer;
import mutua.icc.instrumentation.Instrumentation;

public class AddToUnsubscribeUserQueue {

	// responses
	private static final byte[] ACCEPTED_ANSWER = "ACCEPTED".intern().getBytes();
	private static final byte[] FAILED_ANSWER   = "FAILED"  .intern().getBytes();
	
	private final InstantVASInstanceConfiguration ivac;
	
	// event consumers/producers
	private final SCProducer                     scProducer;

	public AddToUnsubscribeUserQueue(InstantVASInstanceConfiguration ivac) {
		this.ivac     = ivac;
		scProducer    = new SCProducer(ivac, new SCConsumer(ivac));
	}
	
	public byte[] process(HashMap<String, String> parameters, String queryString) {
		startAddToSubscribeUserQueueRequest(queryString);
		try {
			String msisdn = parameters.get("MSISDN");
			scProducer.dispatchAssureUserIsNotSubscribedEvent(msisdn);
			return ACCEPTED_ANSWER;
		} catch (Throwable t) {
			Instrumentation.reportThrowable(t, "Error while subscribing user from the web");
			return FAILED_ANSWER;
		} finally {
			finishRequest();
		}
	}
	
}