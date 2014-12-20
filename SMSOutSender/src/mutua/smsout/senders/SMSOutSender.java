package mutua.smsout.senders;

import java.io.IOException;

import mutua.smsout.dto.OutgoingSMSDto;

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

	// these are the reply codes given by the gateway when we attempt to send
	public enum EOutgoingSMSAcceptionStatus {
		ACCEPTED,		// message was accepted and ready for processing
		REJECTED,		// message won't be processed now and never
		POSTPONED,		// message can't be processed now, please re-send later
	}
	
	// send the message synchronously and report the acception status
	public abstract EOutgoingSMSAcceptionStatus sendMessage(OutgoingSMSDto smsOut) throws IOException;
}
