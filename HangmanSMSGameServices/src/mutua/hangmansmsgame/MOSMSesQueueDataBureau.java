package mutua.hangmansmsgame;

import mutua.events.IDatabaseQueueDataBureau;
import mutua.hangmansmsgame.config.Configuration;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor.EHangmanSMSGameEvents;
import mutua.imi.IndirectMethodInvocationInfo;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import adapters.dto.PreparedProcedureInvocationDto;
import adapters.exceptions.PreparedProcedureException;

/** <pre>
 * MOSMSesQueueDataBureau.java
 * ===========================
 * (created by luiz, Feb 1, 2015)
 *
 * Responsible for serialization & deserialization of MO queue events to and from the database.
 * The MO queue has been overused to contemplate all EHangmanSMSGameEvents, to know:
 * 	- INTERACTIVE_REQUEST, a normal MO. Type 'IncomingSMSDto'
 * 	- TIMEOUT_EVENT, don't yet know if this really belong to an MO. If it does, then idem
 * 	- SUBSCRIBE_USER, an API call requesting user subscription. Type 'String'
 *  - UNSUBSCRIBE_USER, idem
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class MOSMSesQueueDataBureau extends IDatabaseQueueDataBureau<EHangmanSMSGameEvents> {

	@Override
	public void serializeQueueEntry(IndirectMethodInvocationInfo<EHangmanSMSGameEvents> entry, PreparedProcedureInvocationDto preparedProcedure) throws PreparedProcedureException {
		String carrier = "";
		String phone   = "";
		String text    = "";
		switch (entry.getMethodId()) {
			case INTERACTIVE_REQUEST:
				IncomingSMSDto mo = (IncomingSMSDto)entry.getParameters()[0];
				carrier = mo.getCarrier().toString();
				phone   = mo.getPhone();
				text    = mo.getText();
				break;
			case SUBSCRIBE_USER:
				phone = (String)entry.getParameters()[0];
				break;
			case UNSUBSCRIBE_USER:
				break;
			case TIMEOUT_EVENT:
				break;
			default:
				throw new RuntimeException("Don't know how to serialize EHangmanSMSGameEvents."+entry.getMethodId());
		}
		preparedProcedure.addParameter("CARRIER", carrier);
		preparedProcedure.addParameter("PHONE",   phone);
		preparedProcedure.addParameter("TEXT",    text);
	}

	@Override
	public IndirectMethodInvocationInfo<EHangmanSMSGameEvents> deserializeQueueEntry(int moId, Object[] databaseRow) {
		String methodId = (String)databaseRow[0];
		String carrier  = (String)databaseRow[1];
		String phone    = (String)databaseRow[2];
		String text     = (String)databaseRow[3];
		
		try {
			EHangmanSMSGameEvents gameEvent = EHangmanSMSGameEvents.valueOf(methodId);
			switch (gameEvent) {
				case INTERACTIVE_REQUEST:
					IncomingSMSDto mo = new IncomingSMSDto(moId, phone, text, ESMSInParserCarrier.valueOf(carrier), Configuration.SHORT_CODE);
					return new IndirectMethodInvocationInfo<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.INTERACTIVE_REQUEST, mo);
				case SUBSCRIBE_USER:
					return new IndirectMethodInvocationInfo<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.SUBSCRIBE_USER, phone);
				case UNSUBSCRIBE_USER:
					return null;
				case TIMEOUT_EVENT:
					return null;
				default:
					throw new RuntimeException("Don't know how to deserialize EHangmanSMSGameEvents."+gameEvent);
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Don't know how to deserialize 'EHangmanSMSGameEvents."+methodId+"'", e);
		}
		
	}

	@Override
	public String getValuesExpressionForInsertNewQueueElementQuery() {
		return "${METHOD_ID}, ${CARRIER}, ${PHONE}, ${TEXT}";
	}

	@Override
	public String getQueueElementFieldList() {
		return "methodId, carrier, phone, text";
	}

	@Override
	public String getFieldsCreationLine() {
		return 	"carrier   VARCHAR(15) NOT NULL, " +
                "phone     VARCHAR(15) NOT NULL, " +
				"text      VARCHAR(160) NOT NULL, ";
	}
}
