package mutua.smsin.parsers;

import java.util.Map;

import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.senders.SMSOutCelltick;

/** <pre>
 * SMSInCelltick.java
 * ==================
 * (created by luiz, Jan 8, 2015)
 *
 * This class is responsible for implementing the passive MO notification api for Celltick
 * gateways
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SMSInCelltick extends SMSInParser<Map<String, String>, byte[]> {
	
	public static byte[] ACCEPTED_RESPONSE  = ESMSInParserSMSAcceptionStatus.ACCEPTED.name().getBytes();
	public static byte[] POSTPONED_RESPONSE = ESMSInParserSMSAcceptionStatus.POSTPONED.name().getBytes();
	public static byte[] REJECTED_RESPONSE  = ESMSInParserSMSAcceptionStatus.REJECTED.name().getBytes();

	public SMSInCelltick(String smsAppId) {
		super("SMSInCelltick", smsAppId);
	}

	@Override
	public IncomingSMSDto parseIncomingSMS(Map<String, String> requestParameters) {
		// http://localhost:8080/HangmanSMSGameServices/AddToMOQueue?AUTHENTICATION_TOKEN=...&MSISDN=(+55)?DDN?NNNNNNNN&CARRIER_NAME=...&LA=...&MO_ID=...&TEXT=...
		String msisdn       = requestParameters.get("MSISDN");
		String carrierName  = requestParameters.get("CARRIER_NAME");
		String largeAccount = requestParameters.get("LA");
		String originalMoId = requestParameters.get("MO_ID");
		String text         = requestParameters.get("TEXT");
		
		if ((msisdn == null) || (carrierName == null) || (largeAccount == null) || (originalMoId == null) || (text == null)) {
			return null;
		}
		
		// extra parameters
		String account  = requestParameters.get("ACCOUNT");
		String validity = requestParameters.get("VALIDITY");
		String smsc     = requestParameters.get("SMSC");
		
		// TODO fix this workarround (if it is really necessary and if not, remove the dependency from SMSOutCelltick project)
		SMSOutCelltick.ACCOUNT  = account;
		SMSOutCelltick.VALIDITY = validity;
		SMSOutCelltick.SMSC     = smsc;
		
		
		ESMSInParserCarrier carrier = ESMSInParserCarrier.valueOf(carrierName.toUpperCase());
		if (carrier == null) {
			carrier = ESMSInParserCarrier.UNKNOWN;	// probably will make 'sendReply' return 'REJECTED'
		}
		
		return new IncomingSMSDto(originalMoId, msisdn, text, carrier, largeAccount);
	}

	@Override
	public byte[] getReply(ESMSInParserSMSAcceptionStatus status) {
		switch (status) {
			case ACCEPTED:
				return ACCEPTED_RESPONSE;
			case POSTPONED:
				return POSTPONED_RESPONSE;
			default:
				//Instrumentation.reportLocalopInconsistency("Unimplemented switch case statement '"+status.toString()+"'");
		        // now go on performing as rejected
			case REJECTED:
				return REJECTED_RESPONSE;
		}
	}

}
