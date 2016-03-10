package instantvas.smsengine;

import mutua.events.IDatabaseQueueDataBureau;
import mutua.imi.IndirectMethodInvocationInfo;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.hangmangame.HangmanGame.EHangmanGameStates;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import adapters.exceptions.PreparedProcedureException;

/** <pre>
 * MOSMSesQueueDataBureau.java
 * ===========================
 * (created by luiz, Aug 20, 2015)
 *
 * Responsible for serialization & deserialization of MO queue events to and from the database.
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class MOSMSesQueueDataBureau extends IDatabaseQueueDataBureau<EHangmanGameStates> {
	
	public static final String MO_TABLE_NAME      = "MOSMSes";
	public static final String MO_ID_FIELD_NAME   = "eventId";
	public static final String MO_TEXT_FIELD_NAME = "text";

	@Override
	public void serializeQueueEntry(IndirectMethodInvocationInfo<EHangmanGameStates> entry, PreparedProcedureInvocationDto preparedProcedure) throws PreparedProcedureException {
		IncomingSMSDto mo = (IncomingSMSDto)entry.getParameters()[0];
		preparedProcedure.addParameter("PHONE", mo.getPhone());
		preparedProcedure.addParameter("TEXT",  mo.getText());
	}
	
	@Override
	public IndirectMethodInvocationInfo<EHangmanGameStates> deserializeQueueEntry(int eventId, Object[] databaseRow) {
		String phone   = (String)databaseRow[0];
		String text    = (String)databaseRow[1];
		IncomingSMSDto mo = new IncomingSMSDto(eventId, phone, text, ESMSInParserCarrier.CLARO, InstantVASSMSAppModuleConfiguration.APPShortCode);
		IndirectMethodInvocationInfo<EHangmanGameStates> entry = new IndirectMethodInvocationInfo<EHangmanGameStates>(EHangmanGameStates.WON, mo);
		return entry;
	}
	
	@Override
	public String getParametersListForInsertNewQueueElementQuery() {
		return "${PHONE}, ${TEXT}";
	}
	
	@Override
	public String getQueueElementFieldList() {
		return "phone, text";
	}
	
	@Override
	public String getFieldsCreationLine() {
		return "phone  TEXT NOT NULL, " +
		       "text   TEXT NOT NULL, ";
	}
}