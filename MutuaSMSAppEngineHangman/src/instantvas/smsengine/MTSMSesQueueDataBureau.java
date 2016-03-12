package instantvas.smsengine;

import adapters.IJDBCAdapterParameterDefinition;
import adapters.exceptions.PreparedProcedureException;
import mutua.events.IDatabaseQueueDataBureau;
import mutua.imi.IndirectMethodInvocationInfo;
import mutua.smsappmodule.hangmangame.HangmanGame.EHangmanGameStates;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto.EBillingType;

/** <pre>
 * MTSMSesQueueDataBureau.java
 * ===========================
 * (created by luiz, Jan 6, 2016)
 *
 * Responsible for serialization & deserialization of MT queue events to and from the database
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class MTSMSesQueueDataBureau extends IDatabaseQueueDataBureau<EHangmanGameStates> {
	
	enum EMTQueueQueryParameters implements IJDBCAdapterParameterDefinition {
		MO_ID,
		PHONE,
		TEXT;
		
		@Override
		public String getParameterName() {
			return name();
		}
	}
	
	@Override
	public Object[] serializeQueueEntry(IndirectMethodInvocationInfo<EHangmanGameStates> entry) throws PreparedProcedureException {
		OutgoingSMSDto mt = (OutgoingSMSDto)entry.getParameters()[0];
		return new Object[] {
			EMTQueueQueryParameters.MO_ID, mt.getMoId(),
			EMTQueueQueryParameters.PHONE, mt.getPhone(),
			EMTQueueQueryParameters.TEXT,  mt.getText()};
	}
	
	@Override
	public IndirectMethodInvocationInfo<EHangmanGameStates> deserializeQueueEntry(int eventId, Object[] databaseRow) {
		Integer moId   = (Integer)databaseRow[0];
		String  phone  = (String)databaseRow[1];
		String  text   = (String)databaseRow[2];
		OutgoingSMSDto mt = new OutgoingSMSDto(moId, phone, text, EBillingType.FREE);
		IndirectMethodInvocationInfo<EHangmanGameStates> entry = new IndirectMethodInvocationInfo<EHangmanGameStates>(EHangmanGameStates.WON, mt);
		return entry;
	}
	
	@Override
	public IJDBCAdapterParameterDefinition[] getParametersListForInsertNewQueueElementQuery() {
		return EMTQueueQueryParameters.values();
	}
	
	@Override
	public String getQueueElementFieldList() {
		return "moId, phone, text";
	}
	
	@Override
	public String getFieldsCreationLine() {
		return "moId   INTEGER NOT NULL, " + 
		       "phone  TEXT    NOT NULL, " +
		       "text   TEXT    NOT NULL, ";
	}
}