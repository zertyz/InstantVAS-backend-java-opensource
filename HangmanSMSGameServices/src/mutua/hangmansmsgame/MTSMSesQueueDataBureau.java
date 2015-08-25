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
 * Responsible for serialization & deserialization of MT queue events to and from the database
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class MTSMSesQueueDataBureau extends	IDatabaseQueueDataBureau<EHangmanSMSGameEvents> {

	@Override
	public void serializeQueueEntry(IndirectMethodInvocationInfo<EHangmanSMSGameEvents> entry, PreparedProcedureInvocationDto preparedProcedure) throws PreparedProcedureException {
		OutgoingSMSDto mt = (OutgoingSMSDto)entry.getParameters()[0];
		preparedProcedure.addParameter("METHOD_ID", entry.getMethodId().toString());
		preparedProcedure.addParameter("MO_ID",     mt.getMoId());
		preparedProcedure.addParameter("PHONE",     mt.getPhone());
		preparedProcedure.addParameter("TEXT",      mt.getText());
	}

	@Override
	public IndirectMethodInvocationInfo<EHangmanSMSGameEvents> deserializeQueueEntry(int eventId, Object[] databaseRow) {
		int    moId    = (Integer)databaseRow[1];
		String phone   = (String)databaseRow[2];
		String text    = (String)databaseRow[3];
		OutgoingSMSDto mt = new OutgoingSMSDto(moId, phone, text, EBillingType.FREE);
		IndirectMethodInvocationInfo<EHangmanSMSGameEvents> entry = new IndirectMethodInvocationInfo<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.INTERACTIVE_REQUEST, mt);
		return entry;
	}

	@Override
	public String getValuesExpressionForInsertNewQueueElementQuery() {
		return "${METHOD_ID}, ${MO_ID}, ${PHONE}, ${TEXT}";
	}

	@Override
	public String getQueueElementFieldList() {
		return "methodId, moId, phone, text";
	}

	@Override
	public String getFieldsCreationLine() {
		return "methodId  TEXT         NOT NULL, " +
		       "moId      INTEGER      NOT NULL, " +
		       "phone     TEXT         NOT NULL, " +
		       "text      TEXT         NOT NULL, ";
	}
}
