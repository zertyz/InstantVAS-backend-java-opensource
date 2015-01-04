package mutua.hangmansmsgame.smslogic;

import mutua.hangmansmsgame.smslogic.commands.dto.CommandMessageDto.EResponseMessageType;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.dto.OutgoingSMSDto.EBillingType;

/**
 * <pre>
 * BillingRules.java
 * =================
 * (created by luiz, Sep 10, 2009)
 * 
 * This class defines how each type of message must be billed.
 * 
 * The information contained here is used to transform an internal 'CommandMessageDto'
 * object into an external 'OutgoingSMSDto'...
 * 
 * ... where the later is suitable for being processed by the sending SMS and billing
 * mechanisms
 * 
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class BillingRules {
	
	public static EBillingType getBillingTypeForResponseMessageType(ESMSInParserCarrier incomingCarrier, EResponseMessageType responseMessageType) {

		switch (responseMessageType) {
			case HELP:
				return EBillingType.FREE;
			case ACQUIRE_MATCH_INFORMATION:
				return EBillingType.FREE;
			case MATCH:
				return EBillingType.SMS;
			case PLAYING:
				return EBillingType.SMS;
			case ERROR:
				return EBillingType.FREE;
			case INCENTIVE:
				return EBillingType.FREE;
			case INVITATION_MESSAGE:
				return EBillingType.FREE;
			default:
				throw new RuntimeException("Billing rule not defined for response message type '" +
				                           responseMessageType + "'");
		}
	}

}
