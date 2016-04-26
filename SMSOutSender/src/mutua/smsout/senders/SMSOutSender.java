package mutua.smsout.senders;

import java.io.IOException;
import java.lang.reflect.Method;

import mutua.icc.instrumentation.IInstrumentableEvent;
import mutua.icc.instrumentation.InstrumentableProperty;
import mutua.icc.instrumentation.InstrumentableEvent;
import mutua.icc.instrumentation.Instrumentation;
import mutua.serialization.SerializationRepository;
import mutua.smsout.dto.OutgoingSMSDto;
import static mutua.smsout.senders.ESMSOutSenderInstrumentationProperties.*;

/** <pre>
 * SMSOutSender.java
 * =================
 * (created by luiz, Dec 9, 2008)
 *
 * This abstract class unifies how different message senders should work, in a way that eases
 * code reuse. Specific gateway senders ('SMSOutComperanTime', for instance) should always
 * implement this class and specific message routers ('EloSMSQueueService', for instance)
 * should always call the sending routines from this class
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public abstract class SMSOutSender {

		
	/** the unified reply codes given by the gateway when we attempt to send */
	public enum EOutgoingSMSAcceptionStatus {
		ACCEPTED,		// message was accepted and ready for processing
		REJECTED,		// message won't be processed now and never
		POSTPONED,		// message can't be processed now, please re-send later
	}
	

	protected final Instrumentation<?, ?> log;
	
	/** the name of the child class implementing this one -- for instrumentation */
	private String childClassName;
	
	/** the name application id using the child implementation -- for instrumentation */
	public final String smsAppId;
	
	/** number of times the default 'sendMessage' implementation will attempt to send the message through 'rawSendMessage' */
	public final int numberOfRetryAttempts;
	
	/** number of milliseconds that the default 'sendMessage' implementation will wait between 'rawSendMessage' retry attempts */ 
	public final long delayBetweenAttempts;
	
	
	/** constructor to be used by subclasses to provide instrumentation information */
	protected SMSOutSender(Instrumentation<?, ?> log, String childClassName, String smsAppId, int numberOfRetryAttempts, long delayBetweenAttempts) {
		this.childClassName        = childClassName;
		this.smsAppId              = smsAppId;
		this.numberOfRetryAttempts = numberOfRetryAttempts;
		this.delayBetweenAttempts  = delayBetweenAttempts;
		this.log = log;
		log.addInstrumentableEvents(ESMSOutSenderInstrumentationEvents.values());

	}

	/** the actual sending method extensions should provide */
	public abstract EOutgoingSMSAcceptionStatus rawSendMessage(OutgoingSMSDto smsOut) throws IOException;
	
	/** sends the message asynchronously and report the acceptance status. Try up to 'numberOfRetryAttempts' in case of an
	 *  exception, sleeping 'delayBetweenAttempts' before each one */
	public EOutgoingSMSAcceptionStatus sendMessage(OutgoingSMSDto smsOut) {
		for (int attempt=0; attempt<numberOfRetryAttempts; attempt++) try {
            return rawSendMessage(smsOut);
		} catch (IOException sendingException) {
            if (attempt < (numberOfRetryAttempts-1)) try {
            	Thread.sleep(delayBetweenAttempts);
            } catch (InterruptedException e) {
            	throw new RuntimeException(childClassName + " ("+smsAppId+"): Thread interrupted while sleeping between reattempts");
            } else {
                throw new RuntimeException(childClassName + " ("+smsAppId+"): Couldn't dispatch a message in "+numberOfRetryAttempts+" attempts", sendingException);
            }
		}
        throw new RuntimeException(childClassName + " ("+smsAppId+"): Couldn't dispatch a message in "+numberOfRetryAttempts+" attempts");
	}
}


//instrumentation events & properties
//////////////////////////////////////

enum ESMSOutSenderInstrumentationProperties implements InstrumentableProperty {

	BASE_URL ("baseURL",  String.class),
	REQUEST  ("request",  String[].class),
	RESPONSE ("response", String.class),
	
	
	;
	
	
	private String instrumentationPropertyName;
	private Class<?> instrumentationPropertyType;
	
	
	private ESMSOutSenderInstrumentationProperties(String instrumentationPropertyName, Class<?> instrumentationPropertyType) {
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

enum ESMSOutSenderInstrumentationEvents implements IInstrumentableEvent {


	SMSOUT_ACCEPTED  ("SMSOutSender.sending ACCEPTED",   REQUEST, RESPONSE),
	SMSOUT_REJECTED  ("SMSOutSender.sending REJECTED",   REQUEST, RESPONSE),
	SMSOUT_POSTPONED ("SMSOutSender.sending POSTPONED",  REQUEST, RESPONSE),
	
	
	;
	
	
	private InstrumentableEvent instrumentableEvent;
	
	private ESMSOutSenderInstrumentationEvents(String name, InstrumentableProperty property) {
		instrumentableEvent = new InstrumentableEvent(name, property);
	}
	
	private ESMSOutSenderInstrumentationEvents(String name, InstrumentableProperty property1, InstrumentableProperty property2) {
		instrumentableEvent = new InstrumentableEvent(name, property1, property2);
	}
	
	private ESMSOutSenderInstrumentationEvents(String name, InstrumentableProperty property1, InstrumentableProperty property2, InstrumentableProperty property3) {
		instrumentableEvent = new InstrumentableEvent(name, property1, property2, property3);
	}
	
	private ESMSOutSenderInstrumentationEvents(String name) {
		instrumentableEvent = new InstrumentableEvent(name);
	}
	
	@Override
	public InstrumentableEvent getInstrumentableEvent() {
		return instrumentableEvent;
	}
}
