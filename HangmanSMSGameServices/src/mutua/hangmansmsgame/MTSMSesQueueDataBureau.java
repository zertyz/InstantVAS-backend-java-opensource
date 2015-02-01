package mutua.hangmansmsgame;

import adapters.dto.PreparedProcedureInvocationDto;
import adapters.exceptions.PreparedProcedureException;
import mutua.events.IDatabaseQueueDataBureau;
import mutua.hangmansmsgame.config.Configuration;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor.EHangmanSMSGameEvents;
import mutua.imi.IndirectMethodInvocationInfo;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto.EBillingType;

/** <pre>
 * MTSMSesQueueDataBureau.java
 * ===========================
 * (created by luiz, Feb 1, 2015)
 *
 * Responsible for serialization & deserialization of MO queue events to and from the database
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class MTSMSesQueueDataBureau extends	IDatabaseQueueDataBureau<EHangmanSMSGameEvents> {

	@Override
	public void serializeQueueEntry(IndirectMethodInvocationInfo<EHangmanSMSGameEvents> entry, PreparedProcedureInvocationDto preparedProcedure) throws PreparedProcedureException {
		OutgoingSMSDto mt = (OutgoingSMSDto)entry.getParameters()[0];
		preparedProcedure.addParameter("PHONE",   mt.getPhone());
		preparedProcedure.addParameter("TEXT",    mt.getText());
	}

	@Override
	public IndirectMethodInvocationInfo<EHangmanSMSGameEvents> desserializeQueueEntry(Object[] databaseRow) {
		String phone   = (String)databaseRow[1];
		String text    = (String)databaseRow[2];
		OutgoingSMSDto mt = new OutgoingSMSDto(phone, text, EBillingType.FREE);
		IndirectMethodInvocationInfo<EHangmanSMSGameEvents> entry = new IndirectMethodInvocationInfo<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.PROCESS_INCOMING_SMS, mt);
		return entry;
	}

	@Override
	public String getValuesExpressionForInsertNewQueueElementQuery() {
		return "${METHOD_ID}, ${PHONE}, ${TEXT}";
	}

	@Override
	public String getFieldListForFetchQueueElementById() {
		return "methodId, phone, text";
	}

	@Override
	public String getFieldsCreationLine() {
		return 	"phone     VARCHAR(15) NOT NULL, " +
				"text      VARCHAR(511) NOT NULL, ";
	}
}
