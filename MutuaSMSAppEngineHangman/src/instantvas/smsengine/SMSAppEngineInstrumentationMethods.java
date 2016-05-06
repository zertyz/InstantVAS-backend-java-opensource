package instantvas.smsengine;

import mutua.icc.instrumentation.InstrumentableEvent;
import mutua.icc.instrumentation.InstrumentableProperty;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.smsin.dto.IncomingSMSDto;

/** <pre>
 * SMSAppEngineInstrumentationMethods.java
 * =======================================
 * (created by luiz, May 5, 2016)
 *
 * Helper Instrumentation class concentrating definitions & calls to all
 * instrumentation events used by this project
 *
 * @version $Id$
 * @author luiz
 */

public class SMSAppEngineInstrumentationMethods {

	// 'InstrumentableEvent's
	private static final InstrumentableEvent licenseInfringmentEvent;
	private static final InstrumentableEvent moQueueAdditionEvent;
	private static final InstrumentableEvent moQueueRejectionEvent;
	
	// 'InstrumentableProperty'ies
	private static final InstrumentableProperty addToMOQueueRequestProperty;
	private static final InstrumentableProperty batchAddToMOQueueRequestProperty;
	private static final InstrumentableProperty addToSubscribeUserQueueRequestProperty;
	private static final InstrumentableProperty addToUnsubscribeUserQueueRequestProperty;
	private static final InstrumentableProperty consumeSubscribeUserRequestProperty;
	private static final InstrumentableProperty consumeUnsubscribeUserRequestProperty;
	private static final InstrumentableProperty moMessageProperty;
	
	static {
		addToMOQueueRequestProperty              = new InstrumentableProperty("AddToMOQueue queryString",              String.class);
		batchAddToMOQueueRequestProperty         = new InstrumentableProperty("AddToMOQueue (in batch) queryStrings",  String[].class);
		addToSubscribeUserQueueRequestProperty   = new InstrumentableProperty("AddToSubscribeUserQueue queryString",   String.class);
		addToUnsubscribeUserQueueRequestProperty = new InstrumentableProperty("AddToUnsubscribeUserQueue queryString", String.class);
		consumeSubscribeUserRequestProperty      = new InstrumentableProperty("consume Subscribe MSISDN",              String.class);
		consumeUnsubscribeUserRequestProperty    = new InstrumentableProperty("consume Unsubscribe MSISDN",            String.class);
		moMessageProperty                        = new InstrumentableProperty("MOMessage",                             IncomingSMSDto.class);
		
		licenseInfringmentEvent = new InstrumentableEvent("Not allowed MO message parameters",  ELogSeverity.CRITICAL);
		moQueueAdditionEvent    = new InstrumentableEvent("MO message accepted on the queue",   ELogSeverity.CRITICAL);
		moQueueRejectionEvent   = new InstrumentableEvent("MO message rejected by the queue",   ELogSeverity.ERROR);
	}
	
	public static void startAddToMOQueueRequest(String queryString) {
		Instrumentation.startRequest(addToMOQueueRequestProperty, queryString);
	}
	
	public static void startBatchAddToMOQueueRequest(String[] queryStringSet) {
		Instrumentation.startRequest(batchAddToMOQueueRequestProperty, queryStringSet);
	}
	
	public static void startAddToSubscribeUserQueueRequest(String queryString) {
		Instrumentation.startRequest(addToSubscribeUserQueueRequestProperty, queryString);
	}
	
	public static void startConsumeSubscribeUserRequest(String msisdn) {
		Instrumentation.startRequest(consumeSubscribeUserRequestProperty, msisdn);
	}
	
	public static void startAddToUnsubscribeUserQueueRequest(String queryString) {
		Instrumentation.startRequest(addToUnsubscribeUserQueueRequestProperty, queryString);
	}
	
	public static void startConsumeUnsubscribeUserRequest(String msisdn) {
		Instrumentation.startRequest(consumeUnsubscribeUserRequestProperty, msisdn);
	}
	
	public static void reportLicenseInfringment(IncomingSMSDto mo) {
		Instrumentation.logAndCompute(licenseInfringmentEvent, moMessageProperty, mo);
	}
	
	public static void reportMOQueueAddition(IncomingSMSDto mo) {
		Instrumentation.logAndCompute(moQueueAdditionEvent, moMessageProperty, mo);
	}
	
	public static void reportMOQueueRejection(String queryString) {
		Instrumentation.logAndCompute(moQueueRejectionEvent, addToMOQueueRequestProperty, queryString);
	}
	
	public static void finishRequest() {
		Instrumentation.finishRequest();
	}
}
