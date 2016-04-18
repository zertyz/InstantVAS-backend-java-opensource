package mutua.subscriptionengine;

import mutua.icc.instrumentation.IInstrumentableEvent;
import mutua.icc.instrumentation.IInstrumentableProperty;
import mutua.icc.instrumentation.InstrumentableEvent;
import mutua.icc.instrumentation.Instrumentation;
import mutua.serialization.SerializationRepository;

import static mutua.subscriptionengine.ESubscriptionEngineInstrumentationProperties.*;

import java.lang.reflect.Method;

import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;

/** <pre>
 * SubscriptionEngine.java
 * =======================
 * (created by luiz, Jan 25, 2015)
 *
 * Common API for SMS platforms to request carriers to sign up users to services
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public abstract class SubscriptionEngine {

	
	protected final Instrumentation<?, ?> log;
	
	public SubscriptionEngine(Instrumentation<?, ?> log) {
		this.log = log;
		log.addInstrumentableEvents(ESubscriptionEngineInstrumentationEvents.values());
	}
	
	/** Return results for 'subscribeUser' operation */
    public enum ESubscriptionOperationStatus {
		/** Indicates a successful subscription */             OK,
		/** Server reported authentication failure */          AUTHENTICATION_ERROR,
		/** Indicates that the server could not be reached */  COMMUNICATION_ERROR,
		/** Server reported the user was already subscribed */ ALREADY_SUBSCRIBED,
    }

    /** Attempts to subscribe the user specified by 'userPhone' into the service */
    public abstract ESubscriptionOperationStatus subscribeUser(String userPhone);
    
    
    /** Return results for 'unsubscribeUser' operation */
    public enum EUnsubscriptionOperationStatus {
		/** Indicates a successful unsubscription */          OK,
		/** Server reported authentication failure */         AUTHENTICATION_ERROR,
		/** Indicates that the server could not be reached */ COMMUNICATION_ERROR,
		/** Server reported the user was not subscribed */    NOT_SUBSCRIBED,
    };

    /** Attempts to unsubscribe the user specified by 'userPhone' from the service */
    public abstract EUnsubscriptionOperationStatus unsubscribeUser(String userPhone);



}


// instrumentation events & properties
//////////////////////////////////////

enum ESubscriptionEngineInstrumentationProperties implements IInstrumentableProperty {

	BASE_URL ("baseURL",    String.class),
	REQUEST  ("parameters", String[].class),
	RESPONSE ("response",   String.class),
	
	
	;

	
	private String instrumentationPropertyName;
	private Class<?> instrumentationPropertyType;
	
	
	private ESubscriptionEngineInstrumentationProperties(String instrumentationPropertyName, Class<?> instrumentationPropertyType) {
		this.instrumentationPropertyName = instrumentationPropertyName;
		this.instrumentationPropertyType = instrumentationPropertyType;
	}

	
	// IInstrumentableProperty implementation
	/////////////////////////////////////////
	
	@Override
	public String getInstrumentationPropertyName() {
		return instrumentationPropertyName;
	}

	
	// ISerializationRule implementation
	////////////////////////////////////
	
	@Override
	public Class<?> getInstrumentationPropertyType() {
		return instrumentationPropertyType;
	}

	@Override
	public Method getTextualSerializationMethod() {
		return SerializationRepository.getSerializationMethod(instrumentationPropertyType);
	}

}

enum ESubscriptionEngineInstrumentationEvents implements IInstrumentableEvent {

	
	SUBSCRIPTION_OK                   ("SubscriptionEngine.subscription OK",                   BASE_URL, REQUEST, RESPONSE),
	SUBSCRIPTION_ALREADY_SUBSCRIBED   ("SubscriptionEngine.subscription ALREADY_SUBSCRIBED",   BASE_URL, REQUEST, RESPONSE),
	SUBSCRIPTION_AUTHENTICATION_ERROR ("SubscriptionEngine.subscription AUTHENTICATION_ERROR", BASE_URL, REQUEST, RESPONSE),
	SUBSCRIPTION_COMMUNICATION_ERROR  ("SubscriptionEngine.subscription COMMUNICATION_ERROR",  BASE_URL, REQUEST, DIP_THROWABLE),

	UNSUBSCRIPTION_OK                   ("SubscriptionEngine.unsubscription OK",                   BASE_URL, REQUEST, RESPONSE),
	UNSUBSCRIPTION_NOT_SUBSCRIBED       ("SubscriptionEngine.unsubscription NOT_SUBSCRIBED",       BASE_URL, REQUEST, RESPONSE),
	UNSUBSCRIPTION_AUTHENTICATION_ERROR ("SubscriptionEngine.unsubscription AUTHENTICATION_ERROR", BASE_URL, REQUEST, RESPONSE),
	UNSUBSCRIPTION_COMMUNICATION_ERROR  ("SubscriptionEngine.unsubscription COMMUNICATION_ERROR",  BASE_URL, REQUEST, DIP_THROWABLE),
	
	
	;
	
	
	private InstrumentableEvent instrumentableEvent;
	
	private ESubscriptionEngineInstrumentationEvents(String name, IInstrumentableProperty property) {
		instrumentableEvent = new InstrumentableEvent(name, property);
	}
	
	private ESubscriptionEngineInstrumentationEvents(String name, IInstrumentableProperty property1, IInstrumentableProperty property2) {
		instrumentableEvent = new InstrumentableEvent(name, property1, property2);
	}
	
	private ESubscriptionEngineInstrumentationEvents(String name, IInstrumentableProperty property1, IInstrumentableProperty property2, IInstrumentableProperty property3) {
		instrumentableEvent = new InstrumentableEvent(name, property1, property2, property3);
	}
	
	private ESubscriptionEngineInstrumentationEvents(String name) {
		instrumentableEvent = new InstrumentableEvent(name);
	}

	@Override
	public InstrumentableEvent getInstrumentableEvent() {
		return instrumentableEvent;
	}
}
