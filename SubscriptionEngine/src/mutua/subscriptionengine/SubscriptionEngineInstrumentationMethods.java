package mutua.subscriptionengine;

import mutua.icc.instrumentation.DefaultInstrumentationEvents;
import mutua.icc.instrumentation.InstrumentableEvent;
import mutua.icc.instrumentation.InstrumentableProperty;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;

/** <pre>
 * SubscriptionEngineInstrumentationMethods.java
 * =============================================
 * (created by luiz, May 4, 2016)
 *
 * Helper Instrumentation class concentrating definitions & calls to all
 * instrumentation events used by this project
 *
 * @version $Id$
 * @author luiz
 */

public class SubscriptionEngineInstrumentationMethods {

	// 'InstrumentableEvent's
	private static final InstrumentableEvent subscriptionOKEvent;
	private static final InstrumentableEvent subscriptionAlreadySubscribedEvent;
	private static final InstrumentableEvent subscriptionAuthenticationErrorEvent;
	private static final InstrumentableEvent subscriptionCommunicationErrorEvent;
	
	private static final InstrumentableEvent unsubscriptionOKEvent;
	private static final InstrumentableEvent unsubscriptionAlreadySubscribedEvent;
	private static final InstrumentableEvent unsubscriptionAuthenticationErrorEvent;
	private static final InstrumentableEvent unsubscriptionCommunicationErrorEvent;
	
	// 'InstrumentableProperty'ies
	private static final InstrumentableProperty channelProperty;
	private static final InstrumentableProperty baseURLProperty;
	private static final InstrumentableProperty requestProperty;
	private static final InstrumentableProperty responseProperty;

	static {
		channelProperty   = new InstrumentableProperty("channel",  String.class);
		baseURLProperty   = new InstrumentableProperty("baseURL",  String.class);
		requestProperty   = new InstrumentableProperty("request",  String[].class);
		responseProperty  = new InstrumentableProperty("response", String.class);

		subscriptionOKEvent                  = new InstrumentableEvent("SubscriptionEngine.subscription OK",                   ELogSeverity.CRITICAL, channelProperty);
		subscriptionAlreadySubscribedEvent   = new InstrumentableEvent("SubscriptionEngine.subscription ALREADY_SUBSCRIBED",   ELogSeverity.CRITICAL, channelProperty);
		subscriptionAuthenticationErrorEvent = new InstrumentableEvent("SubscriptionEngine.subscription AUTHENTICATION_ERROR", ELogSeverity.ERROR,    channelProperty);
		subscriptionCommunicationErrorEvent  = new InstrumentableEvent("SubscriptionEngine.subscription COMMUNICATION_ERROR",  ELogSeverity.ERROR,    channelProperty);

		unsubscriptionOKEvent                  = new InstrumentableEvent("SubscriptionEngine.unsubscription OK",                   ELogSeverity.CRITICAL, channelProperty);
		unsubscriptionAlreadySubscribedEvent   = new InstrumentableEvent("SubscriptionEngine.unsubscription ALREADY_SUBSCRIBED",   ELogSeverity.CRITICAL, channelProperty);
		unsubscriptionAuthenticationErrorEvent = new InstrumentableEvent("SubscriptionEngine.unsubscription AUTHENTICATION_ERROR", ELogSeverity.ERROR,    channelProperty);
		unsubscriptionCommunicationErrorEvent  = new InstrumentableEvent("SubscriptionEngine.unsubscription COMMUNICATION_ERROR",  ELogSeverity.ERROR,    channelProperty);
	}

	public static void reportSubscriptionOK(String channel, String baseURL, String[] request, String response) {
		Instrumentation.logProfileAndCompute(subscriptionOKEvent,
			channelProperty,  channel,
			baseURLProperty,  baseURL,
			requestProperty,  request,
			responseProperty, response);
	}
		
	public static void reportSubscriptionAlreadySubscribed(String channel, String baseURL, String[] request, String response) {
		Instrumentation.logProfileAndCompute(subscriptionAlreadySubscribedEvent,
			channelProperty,  channel,
			baseURLProperty,  baseURL,
			requestProperty,  request,
			responseProperty, response);
	}

	public static void reportSubscriptionAuthenticationError(String channel, String baseURL, String[] request, String response) {
		Instrumentation.logProfileAndCompute(subscriptionAuthenticationErrorEvent,
			channelProperty,  channel,
			baseURLProperty,  baseURL,
			requestProperty,  request,
			responseProperty, response);
	}

	public static void reportSubscriptionCommunicationError(String channel, String baseURL, String[] request, Throwable t) {
		Instrumentation.logProfileAndCompute(subscriptionCommunicationErrorEvent,
			channelProperty,                                 channel,
			baseURLProperty,                                 baseURL,
			requestProperty,                                 request,
			DefaultInstrumentationEvents.THROWABLE_PROPERTY, t);
	}

	public static void reportUnsubscriptionOK(String channel, String baseURL, String[] request, String response) {
		Instrumentation.logProfileAndCompute(unsubscriptionOKEvent,
			channelProperty,  channel,
			baseURLProperty,  baseURL,
			requestProperty,  request,
			responseProperty, response);
	}
		
	public static void reportUnsubscriptionNotSubscribed(String channel, String baseURL, String[] request, String response) {
		Instrumentation.logProfileAndCompute(unsubscriptionAlreadySubscribedEvent,
			channelProperty,  channel,
			baseURLProperty,  baseURL,
			requestProperty,  request,
			responseProperty, response);
	}

	public static void reportUnsubscriptionAuthenticationError(String channel, String baseURL, String[] request, String response) {
		Instrumentation.logProfileAndCompute(unsubscriptionAuthenticationErrorEvent,
			channelProperty,  channel,
			baseURLProperty,  baseURL,
			requestProperty,  request,
			responseProperty, response);
	}

	public static void reportUnsubscriptionCommunicationError(String channel, String baseURL, String[] request, Throwable t) {
		Instrumentation.logProfileAndCompute(unsubscriptionCommunicationErrorEvent,
			channelProperty,                                 channel,
			baseURLProperty,                                 baseURL,
			requestProperty,                                 request,
			DefaultInstrumentationEvents.THROWABLE_PROPERTY, t);
	}

}
