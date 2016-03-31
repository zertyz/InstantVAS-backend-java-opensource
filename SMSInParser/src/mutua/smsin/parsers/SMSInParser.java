package mutua.smsin.parsers;

import mutua.smsin.dto.IncomingSMSDto;

/** <pre>
 * SMSInParser.java
 * ================
 * (created by luiz, Dec 8, 2008)
 *
 * This abstract class unifies how different incoming message parsers should work, in a way which eases
 * code reuse. Specific gateway parsers ('SMSInComperanTime', for instance) should always implement
 * this class and specific message routers ('EloSMSQueueService', for instance) should always
 * call the parser from this class
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public abstract class SMSInParser<REQUEST_OBJECT, RESPONSE_OBJECT> {

	
    /** the unified reply codes, signaling our acceptance of the asynchronous requests */
    public enum ESMSInParserSMSAcceptionStatus {
        ACCEPTED,       // message was accepted and ready for processing
        REJECTED,       // message won't be processed now nor ever
        POSTPONED,      // message can't be processed now, please re-send later
    }

    
	/** the name of the child class implementing this one -- for instrumentation */
	public final String childClassName;
	
	/** the name application id using the child implementation -- for instrumentation */
	public final String smsAppId;
	
	
	/** constructor to be used by subclasses to provide instrumentation information */
	protected SMSInParser(String childClassName, String smsAppId) {
		this.childClassName = childClassName;
		this.smsAppId       = smsAppId;
	}
	
	/** Returns the list of parameters that should be present on 'REQUEST_OBJECT' in order for an MO to be parsed -- prefixed by 'precedingParameterNames'.
	 *  On high performance HTTP Servers (like InstantVAS NativeHTTPServer), the requests should define parameters
	 *  on this exact order and without any other parameters between them -- in this case, the correct method to
	 *  parse MOs is {@link #parseIncomingSMS(String...)} */
	public abstract String[] getRequestParameterNames(String... precedingParameterNames);
	
	/** High Efficiency parser for an MO, where 'parameterValues' elements should be in the order defined by
	 *  {@link #getRequestParametersList} */
	public abstract IncomingSMSDto parseIncomingSMS(String... parameterValues);

    /** Responsible for acquiring information from the message and building an 'IncomingSMSDto' object */
    public abstract IncomingSMSDto parseIncomingSMS(REQUEST_OBJECT request);
        
    /** Responsible for acknowledging the originating gateway how the message was accepted */
    public abstract RESPONSE_OBJECT getReply(ESMSInParserSMSAcceptionStatus status);

}