package instantvas.smsengine;

import mutua.events.IDatabaseQueueDataBureau;
import mutua.imi.IndirectMethodInvocationInfo;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import adapters.IJDBCAdapterParameterDefinition;
import instantvas.smsengine.producersandconsumers.EInstantVASEvents;

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

public class MOSMSesQueueDataBureau extends IDatabaseQueueDataBureau<EInstantVASEvents> {
	
	public static final String MO_TABLE_NAME      = "MOSMSes";
	public static final String MO_ID_FIELD_NAME   = "eventId";
	public static final String MO_TEXT_FIELD_NAME = "text";
	
	enum EMOQueueQueryParameters implements IJDBCAdapterParameterDefinition {

		PHONE,
		TEXT;

		@Override
		public String getParameterName() {
			return name();
		}
	}
	
	private final String              shortCode;
	private final ESMSInParserCarrier defaultCarrier;
	
	public MOSMSesQueueDataBureau(String shortCode) {
		this.shortCode = shortCode;
		defaultCarrier = ESMSInParserCarrier.CLARO;
	}


	@Override
	public Object[] serializeQueueEntry(IndirectMethodInvocationInfo<EInstantVASEvents> entry) {
		IncomingSMSDto mo = (IncomingSMSDto)entry.getParameters()[0];
		return new Object[] {
			EMOQueueQueryParameters.PHONE, mo.getPhone(),
			EMOQueueQueryParameters.TEXT,  mo.getText()};
	}
	
	@Override
	public IndirectMethodInvocationInfo<EInstantVASEvents> deserializeQueueEntry(int eventId, Object[] databaseRow) {
		String phone   = (String)databaseRow[0];
		String text    = (String)databaseRow[1];
		IncomingSMSDto mo = new IncomingSMSDto(eventId, phone, text, defaultCarrier, shortCode);
		IndirectMethodInvocationInfo<EInstantVASEvents> entry = new IndirectMethodInvocationInfo<EInstantVASEvents>(EInstantVASEvents.MO_ARRIVED, mo);
		return entry;
	}
	
	@Override
	public IJDBCAdapterParameterDefinition[] getParametersListForInsertNewQueueElementQuery() {
		return EMOQueueQueryParameters.values();
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