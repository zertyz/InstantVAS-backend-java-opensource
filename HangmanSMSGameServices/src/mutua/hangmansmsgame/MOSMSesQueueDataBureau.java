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
 * Responsible for serialization & deserialization of MO queue events to and from the database
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class MOSMSesQueueDataBureau extends IDatabaseQueueDataBureau<EHangmanSMSGameEvents> {

	@Override
	public void serializeQueueEntry(IndirectMethodInvocationInfo<EHangmanSMSGameEvents> entry, PreparedProcedureInvocationDto preparedProcedure) throws PreparedProcedureException {
		IncomingSMSDto mo = (IncomingSMSDto)entry.getParameters()[0];
		preparedProcedure.addParameter("CARRIER", mo.getCarrier().toString());
		preparedProcedure.addParameter("PHONE",   mo.getPhone());
		preparedProcedure.addParameter("TEXT",    mo.getText());
	}

	@Override
	public IndirectMethodInvocationInfo<EHangmanSMSGameEvents> desserializeQueueEntry(int moId, Object[] databaseRow) {
		String carrier = (String)databaseRow[1];
		String phone   = (String)databaseRow[2];
		String text    = (String)databaseRow[3];
		IncomingSMSDto mo = new IncomingSMSDto(moId, phone, text, ESMSInParserCarrier.valueOf(carrier), Configuration.SHORT_CODE);
		IndirectMethodInvocationInfo<EHangmanSMSGameEvents> entry = new IndirectMethodInvocationInfo<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.INTERACTIVE_REQUEST, mo);
		return entry;
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
