package mutua.hangmansmsgame.dispatcher;

import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 * IResponseReceiver.java
 * ======================
 * (created by luiz, Sep 14, 2009)
 *
 * Implementers of this interface should be able to deliver MT's (SMS messages that where generated by the system)
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public interface IResponseReceiver {

	/*
	 *  on the event that a message is available for sending, this method
	 *  gets invoked -- with the originating message
	 */
	void onMessage(OutgoingSMSDto outgoingMessage, IncomingSMSDto incomingMessage);

}
