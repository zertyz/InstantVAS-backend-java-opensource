package instantvas.smsengine;

import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;

import instantvas.smsengine.producersandconsumers.RequestProfilingTimes;
import mutua.icc.instrumentation.InstrumentableEvent;
import mutua.icc.instrumentation.InstrumentableProperty;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.dto.InstrumentationEventDto;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto;

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
	public static final InstrumentableEvent licenseInfringmentEvent;
	public static final InstrumentableEvent moQueueAdditionEvent;
	public static final InstrumentableEvent moQueueRejectionEvent;
	public static final InstrumentableEvent mtIsReadyEvent;
	public static final InstrumentableEvent mtQueueAdditionEvent;
	
	// 'InstrumentableProperty'ies
	public static final InstrumentableProperty addToMOQueueRequestProperty;
	public static final InstrumentableProperty batchAddToMOQueueRequestProperty;
	public static final InstrumentableProperty addToSubscribeUserQueueRequestProperty;
	public static final InstrumentableProperty addToUnsubscribeUserQueueRequestProperty;
	public static final InstrumentableProperty consumeSubscribeUserRequestProperty;
	public static final InstrumentableProperty consumeUnsubscribeUserRequestProperty;
	public static final InstrumentableProperty moIdProperty;
	public static final InstrumentableProperty moMessageProperty;
	public static final InstrumentableProperty mtMessageProperty;
	public static final InstrumentableProperty processMORequestProperty;
	public static final InstrumentableProperty deliverMTRequestProperty;
	public static final InstrumentableProperty profiledRequestProperty;
	
	static {
		addToMOQueueRequestProperty              = new InstrumentableProperty("AddToMOQueue queryString",              String.class);
		batchAddToMOQueueRequestProperty         = new InstrumentableProperty("AddToMOQueue (in batch) queryStrings",  String[].class);
		addToSubscribeUserQueueRequestProperty   = new InstrumentableProperty("AddToSubscribeUserQueue queryString",   String.class);
		addToUnsubscribeUserQueueRequestProperty = new InstrumentableProperty("AddToUnsubscribeUserQueue queryString", String.class);
		consumeSubscribeUserRequestProperty      = new InstrumentableProperty("consume Subscribe MSISDN",              String.class);
		consumeUnsubscribeUserRequestProperty    = new InstrumentableProperty("consume Unsubscribe MSISDN",            String.class);
		moIdProperty                             = new InstrumentableProperty("moId",                                  Integer.class);
		moMessageProperty                        = new InstrumentableProperty("MOMessage",                             IncomingSMSDto.class);
		mtMessageProperty                        = new InstrumentableProperty("MTMessage",                             OutgoingSMSDto.class);
		processMORequestProperty                 = new InstrumentableProperty("process MO",                            IncomingSMSDto.class);
		deliverMTRequestProperty                 = new InstrumentableProperty("Deliver MT",                            OutgoingSMSDto.class);
		profiledRequestProperty                  = new InstrumentableProperty("profileData",                           RequestProfilingTimes.class);
		
		licenseInfringmentEvent = new InstrumentableEvent("Not allowed MO message parameters",  ELogSeverity.CRITICAL);
		moQueueAdditionEvent    = new InstrumentableEvent("MO added to the queue",              ELogSeverity.CRITICAL);
		moQueueRejectionEvent   = new InstrumentableEvent("MO rejected by the queue",           ELogSeverity.ERROR);
		mtIsReadyEvent          = new InstrumentableEvent("Message ready for delivery",         ELogSeverity.DEBUG);
		mtQueueAdditionEvent    = new InstrumentableEvent("MT added to the queue",              ELogSeverity.DEBUG);

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

	public static void reportMOQueueAddition(int moId, IncomingSMSDto mo) {
		Instrumentation.logProfileAndCompute(moQueueAdditionEvent, moIdProperty, moId, moMessageProperty, mo);
	}

	/** overload to be used in batch addition. May defecate the profiling algorithm (since the moId isn't valid) */
	public static void reportMOQueueAddition(IncomingSMSDto mo) {
		Instrumentation.logProfileAndCompute(moQueueAdditionEvent, moIdProperty, -1, moMessageProperty, mo);
	}

	
	/** attempts to retrieve the first instrumented information by {@link #reportMOQueueAddition(int, IncomingSMSDto)}.
	 *  returns {(int)moId, (IncomingSMSDto)mo, (long)eventCurrentTimeMillis} */
	public static Object[] retrieveFirstMOQueueAdditionData(InstrumentationEventDto[] events) {
		for (int i=1; i<events.length; i++) {	// 'i=0' may be skipped because 'events[0]' is always 'REQUEST_START_EVENT'
			if (events[i].instrumentableEvent == moQueueAdditionEvent) {
				// the following hard-coded indexes comes from 'reportMOQueueAddition'
				int            moId = (Integer)        events[i].propertiesAndValues[1];
				IncomingSMSDto mo   = (IncomingSMSDto) events[i].propertiesAndValues[3];
				return new Object[] {moId, mo, events[i].currentTimeMillis};
			}
		}
		return null;
	}
	
	public static void reportMOQueueRejection(String queryString) {
		Instrumentation.logAndCompute(moQueueRejectionEvent, addToMOQueueRequestProperty, queryString);
	}
	
	public static void startMOProcessingRequest(IncomingSMSDto mo) {
		Instrumentation.startRequest(processMORequestProperty, mo);
	}
	
	public static void startMTDeliveryRequest(OutgoingSMSDto mt) {
		Instrumentation.startRequest(deliverMTRequestProperty, mt);
	}
	
	/** happens when the application makes a new mt available, after processing the mo */
	public static void reportMTIsReady(IncomingSMSDto mo, OutgoingSMSDto mt) {
		Instrumentation.logProfileAndCompute(mtIsReadyEvent, moMessageProperty, mo, mtMessageProperty, mt);
	}
	
	/** attempts to retrieve the first instrumented information by {@link #reportMTIsReady(IncomingSMSDto, OutgoingSMSDto)}.
	 *  returns {(IncomingSMSDto)mo, (OutgoingSMSDto)mt, (long)eventCurrentTimeMillis}  */
	public static Object[] retrieveFirstMTIsReadyData(InstrumentationEventDto[] events) {
		for (int i=1; i<events.length; i++) {	// 'i=0' may be skipped because 'events[0]' is always 'REQUEST_START_EVENT'
			if (events[i].instrumentableEvent == mtIsReadyEvent) {
				// the following hard-coded indexes comes from 'reportMTIsReady'
				IncomingSMSDto mo = (IncomingSMSDto) events[i].propertiesAndValues[1];
				OutgoingSMSDto mt = (OutgoingSMSDto) events[i].propertiesAndValues[3];
				return new Object[] {mo, mt, events[i].currentTimeMillis};
			}
		}
		return null;
	}
	
	public static void reportMTEnqueued(IncomingSMSDto mo, OutgoingSMSDto mt) {
		Instrumentation.logProfileAndCompute(mtQueueAdditionEvent, moMessageProperty, mo, mtMessageProperty, mt);
	}
	
	/** attempts to retrieve the last instrumented information by {@link #reportMTIsReady(IncomingSMSDto, OutgoingSMSDto)}.
	 *  returns {(IncomingSMSDto)mo, (OutgoingSMSDto)mt, (long)eventCurrentTimeMillis}  */
	public static Object[] retrieveLastMTEnqueuedData(InstrumentationEventDto[] events) {
		for (int i=events.length-2; i>1; i--) {	// 'i=0' and 'i=length-1' may be skipped because 'events[0]' is always 'REQUEST_START_EVENT' and events[length-1] is 'REQUEST_FINISH_EVENT'
			if (events[i].instrumentableEvent == mtQueueAdditionEvent) {
				// the following hard-coded indexes comes from 'reportMTEnqueued'
				IncomingSMSDto mo = (IncomingSMSDto) events[i].propertiesAndValues[1];
				OutgoingSMSDto mt = (OutgoingSMSDto) events[i].propertiesAndValues[3];
				return new Object[] {mo, mt, events[i].currentTimeMillis};
			}
		}
		return null;
	}
	
	public static void finishRequest() {
		Instrumentation.finishRequest();
	}
	
	public static void logProfiledRequest(RequestProfilingTimes rpt) {
		Instrumentation.justLog(PROFILED_REQUEST_EVENT, profiledRequestProperty, rpt);
	}
	
	public static void logTimedOutProfiledRequest(RequestProfilingTimes rpt) {
		Instrumentation.justLog(TIMEDOUT_REQUEST_EVENT, profiledRequestProperty, rpt);
	}
}