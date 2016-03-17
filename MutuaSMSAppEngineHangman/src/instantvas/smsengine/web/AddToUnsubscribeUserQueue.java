package instantvas.smsengine.web;

import java.util.HashMap;

import config.InstantVASApplicationConfiguration;
import instantvas.smsengine.InstantVASHTTPInstrumentationRequestProperty;
import instantvas.smsengine.producersandconsumers.SCConsumer;
import instantvas.smsengine.producersandconsumers.SCProducer;
import mutua.icc.instrumentation.Instrumentation;

public class AddToUnsubscribeUserQueue {

	// responses
	private static final byte[] ACCEPTED_ANSWER = "ACCEPTED".intern().getBytes();
	private static final byte[] FAILED_ANSWER   = "FAILED"  .intern().getBytes();
	
	private final Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log;
	private final InstantVASApplicationConfiguration ivac;
	
	// event consumers/producers
	private final SCProducer                     scProducer;

	public AddToUnsubscribeUserQueue(InstantVASApplicationConfiguration ivac) {
		this.ivac     = ivac;
		log           = ivac.log;
		scProducer    = new SCProducer(ivac, new SCConsumer(ivac));
	}
	
	public byte[] process(HashMap<String, String> parameters, String queryString) {
		log.reportRequestStart("AddToSubscribeUserQueue " + queryString);
		try {
			String msisdn = parameters.get("MSISDN");
			scProducer.dispatchAssureUserIsNotSubscribedEvent(msisdn);
			return ACCEPTED_ANSWER;
		} catch (Throwable t) {
			log.reportThrowable(t, "Error while subscribing user from the web");
			return FAILED_ANSWER;
		} finally {
			log.reportRequestFinish();
		}
	}
	
}
