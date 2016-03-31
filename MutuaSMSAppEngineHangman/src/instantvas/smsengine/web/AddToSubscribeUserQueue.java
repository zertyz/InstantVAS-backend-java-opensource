package instantvas.smsengine.web;

import java.util.HashMap;

import config.InstantVASInstanceConfiguration;
import instantvas.smsengine.InstantVASHTTPInstrumentationRequestProperty;
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
	
	private final Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log;
	private final InstantVASInstanceConfiguration ivac;
	
	// event consumers/producers
	private final SRProducer                     srProducer;

	public AddToSubscribeUserQueue(InstantVASInstanceConfiguration ivac) {
		this.ivac     = ivac;
		log           = ivac.log;
		srProducer    = new SRProducer(ivac, new SRConsumer(ivac));
	}
	
	public byte[] process(HashMap<String, String> parameters, String queryString) {
		log.reportRequestStart("AddToSubscribeUserQueue " + queryString);
		try {
			String msisdn = parameters.get("MSISDN");
			srProducer.dispatchAssureUserIsSubscribedEvent(msisdn);
			return ACCEPTED_ANSWER;
		} catch (Throwable t) {
			log.reportThrowable(t, "Error while subscribing user from the web");
			return FAILED_ANSWER;
		} finally {
			log.reportRequestFinish();
		}
	}
	
}
