package mutua.smsout.senders;

import mutua.icc.instrumentation.InstrumentableEvent;
import mutua.icc.instrumentation.InstrumentableProperty;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 * SMSOutSenderInstrumentationMethods.java
 * =======================================
 * (created by luiz, May 4, 2016)
 *
 * Helper Instrumentation class concentrating definitions & calls to all
 * instrumentation events used by this project
 *
 * @version $Id$
 * @author luiz
 */

public class SMSOutSenderInstrumentationMethods {
	
	// 'InstrumentableEvent's
	private static final InstrumentableEvent smsOutAcceptedEvent;
	private static final InstrumentableEvent smsOutRejectedEvent;
	private static final InstrumentableEvent smsOutPostponedEvent;
	private static final InstrumentableEvent smsOutRetryAttemptEvent;
	
	// 'InstrumentableProperty'ies
	private static final InstrumentableProperty integrationClassProperty;
	private static final InstrumentableProperty baseURLProperty;
	private static final InstrumentableProperty requestProperty;
	private static final InstrumentableProperty responseProperty;
	private static final InstrumentableProperty attemptNumberProperty;
	private static final InstrumentableProperty mtProperty;
	
	static {
		integrationClassProperty = new InstrumentableProperty("integrationClass", String.class);
		baseURLProperty          = new InstrumentableProperty("baseURL",          String.class);
		requestProperty          = new InstrumentableProperty("request",          String[].class);
		responseProperty         = new InstrumentableProperty("response",         String.class);
		attemptNumberProperty    = new InstrumentableProperty("retry attempt",    int.class);
		mtProperty               = new InstrumentableProperty("mt",               OutgoingSMSDto.class);

		smsOutAcceptedEvent     = new InstrumentableEvent("SMSOutSender.sending ACCEPTED",  ELogSeverity.CRITICAL, integrationClassProperty);
		smsOutRejectedEvent     = new InstrumentableEvent("SMSOutSender.sending REJECTED",  ELogSeverity.CRITICAL, integrationClassProperty);
		smsOutPostponedEvent    = new InstrumentableEvent("SMSOutSender.sending POSTPONED", ELogSeverity.CRITICAL, integrationClassProperty);
		smsOutRetryAttemptEvent = new InstrumentableEvent("SMSOutSender.sending RETRYING",  ELogSeverity.ERROR,    integrationClassProperty);
	}
	
	public static void reportMTDeliveryAccepted(String integrationClass, String baseURL, String[] request, String response) {
		Instrumentation.logProfileAndCompute(smsOutAcceptedEvent,
			integrationClassProperty, integrationClass,
			baseURLProperty,          baseURL,
			requestProperty,          request,
			responseProperty,         response);
	}
	
	public static void reportMTDeliveryRejected(String integrationClass, String baseURL, String[] request, String response) {
		Instrumentation.logAndCompute(smsOutRejectedEvent,
			integrationClassProperty, integrationClass,
			baseURLProperty,          baseURL,
			requestProperty,          request,
			responseProperty,         response);
	}

	public static void reportMTDeliveryPostponed(String integrationClass, String baseURL, String[] request, String response) {
		Instrumentation.logAndCompute(smsOutPostponedEvent,
			integrationClassProperty, integrationClass,
			baseURLProperty,          baseURL,
			requestProperty,          request,
			responseProperty,         response);
	}
	
	public static void reportMTDeliveryRetryAttempt(String integrationClass, int attemptNumber, OutgoingSMSDto mt) {
		Instrumentation.logAndCompute(smsOutRetryAttemptEvent,
			integrationClassProperty, integrationClass,
			attemptNumberProperty,    attemptNumber,
			mtProperty,               mt);
	}

}
