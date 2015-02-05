package mutua.hangmansmsgame.dispatcher;

import java.sql.SQLException;

import mutua.hangmansmsgame.dal.dto.SessionDto;
import mutua.hangmansmsgame.smslogic.BillingRules;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandMessageDto;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto.EBillingType;

/**
 * <pre>
 * MessageDispatcher.java
 * ======================
 * (created by luiz, Sep 14, 2009)
 * 
 * Dispatches a message generated in an SMS Application to an 'IMessageReceiver' instance
 * 
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class MessageDispatcher {

	
	/**************
	 ** DATABASES **
	 **************/

//	private static IStateDAO stateDAO;
//
//	static {
//		try {
//			stateDAO = DALFactory.getInstance().getStateDAO();
//		} catch (Exception e) {
//			throw new RuntimeException("Error instantiating database", e);
//		}
//	}

	
	/***********
	 ** FIELDS **
	 ***********/

	/** where to route generated messages */
	private IResponseReceiver receiver;

	
	/*********************
	 ** AUXILIAR METHODS **
	 *********************/

	/**
	 * Set the state respecting the convention: null cause the state not to
	 * change. Note: the corresponding 'getState' function is implemented in the
	 * 'BlocosDeCarnavalProcessor' class
	 */
	private static void setState(String phone, SessionDto userSessionDto) {
		if (userSessionDto != null) {
//			stateDAO.update(phone, userSessionDto.getState().name());
//			stateDAO.updateStateParameters(phone, userSessionDto.getParameters());
		}
	}
	 
	/** Translates an internal message object into an external message object
	 *  applying the billing rules and respecting the convention:
	 *  null phones in destination messages mean they are addressed
	 *  to the originating user */
	private OutgoingSMSDto translateResponseMessage(IncomingSMSDto incomingSMS,	CommandMessageDto internalResponseMessage) {
		String phone = internalResponseMessage.getPhone();
		String text  = internalResponseMessage.getText();
		EBillingType billingType = BillingRules.getBillingTypeForResponseMessageType(incomingSMS.getCarrier(),
		                                                                             internalResponseMessage.getType());
		if (phone == null) {
			phone = incomingSMS.getPhone();
		}
		OutgoingSMSDto externalMessage = new OutgoingSMSDto(incomingSMS.getMoId(), phone, text, billingType);

		return externalMessage;
	}

	/*********************
	 ** EXTERNAL METHODS **
	 *********************/

	public MessageDispatcher(IResponseReceiver receiver) {
		this.receiver = receiver;
	}

	/** Dispatches messages generated in the system to a receiver able to forward it to their destination */
	public void dispatchMessage(CommandMessageDto[] internalMessages, IncomingSMSDto incomingSms) throws SQLException {
		OutgoingSMSDto externalMessage;
		for (int i=0; i<internalMessages.length; i++) {
			externalMessage = translateResponseMessage(incomingSms, internalMessages[i]);
			receiver.onMessage(externalMessage, incomingSms);
		}
	}
}