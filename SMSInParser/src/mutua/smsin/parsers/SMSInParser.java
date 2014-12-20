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
    
    // these are the reply codes, when responding the asynchronous request back to the gateway
    public enum ESMSInParserSMSAcceptionStatus {
        ACCEPTED,       // message was accepted and ready for processing
        REJECTED,       // message won't be processed now nor ever
        POSTPONED,      // message can't be processed now, please re-send later
    }


    /*****************************
    ** INCOMING MESSAGES PARSER **
    *****************************/
    
    /**
     * Responsible for acquiring information from the message and building an 'IncomingSMSDto' object
     */
    public abstract IncomingSMSDto parseIncomingSMS(REQUEST_OBJECT request);
    
    
    /***************************
    ** ACKNOWLEDGE THE SENDER **
    ***************************/
    
    /**
     * Responsible for acknowledging the originating gateway how the message was accepted
     */
    public abstract void sendReply(ESMSInParserSMSAcceptionStatus status, RESPONSE_OBJECT response);

}