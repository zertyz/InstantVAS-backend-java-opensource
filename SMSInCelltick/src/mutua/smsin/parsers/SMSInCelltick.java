package mutua.smsin.parsers;

import java.util.Map;

import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

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
	
	public static final byte[] ACCEPTED_RESPONSE  = ESMSInParserSMSAcceptionStatus.ACCEPTED.name().getBytes();
	public static final byte[] POSTPONED_RESPONSE = ESMSInParserSMSAcceptionStatus.POSTPONED.name().getBytes();
	public static final byte[] REJECTED_RESPONSE  = ESMSInParserSMSAcceptionStatus.REJECTED.name().getBytes();
	
	public static final int    MSISDNParameterIndex        = 0;
	public static final String MSISDNParameterName         = "MSISDN";
	public static final int    CARRIER_NAMEParameterIndex  = 1;
	public static final String CARRIER_NAMEParameterName   = "CARRIER_NAME";
	public static final int    LAParameterIndex            = 2;
	public static final String LAParameterName             = "LA";
	public static final int    MO_IDParameterIndex         = 3;
	public static final String MO_IDParameterName          = "MO_ID";
	public static final int    TEXTParameterIndex          = 4;
	public static final String TEXTParameterName           = "TEXT";
	public static final int    KANNEL_UNIQUEParameterIndex = 5;
	public static final String KANNEL_UNIQUEParameterName  = "KANNEL_UNIQUE";
	public static final int    SMSCParameterIndex          = 6;
	public static final String SMSCParameterName           = "SMSC";
	public static final int    parametersLength            = 7;

	public SMSInCelltick(String smsAppId) {
		super("SMSInCelltick", smsAppId);
	}

	@Override
	public String[] getRequestParameterNames(String... precedingParameterNames) {
		int offset = precedingParameterNames.length;
		String[] parameterNames = new String[offset+parametersLength];
		for (int i=0; i<precedingParameterNames.length; i++) {
			parameterNames[i] = precedingParameterNames[i];
		}
		parameterNames[offset + MSISDNParameterIndex]        = MSISDNParameterName;
		parameterNames[offset + CARRIER_NAMEParameterIndex]  = CARRIER_NAMEParameterName;
		parameterNames[offset + LAParameterIndex]            = LAParameterName;
		parameterNames[offset + MO_IDParameterIndex]         = MO_IDParameterName;
		parameterNames[offset + TEXTParameterIndex]          = TEXTParameterName;
		parameterNames[offset + KANNEL_UNIQUEParameterIndex] = KANNEL_UNIQUEParameterName;
		parameterNames[offset + SMSCParameterIndex]          = SMSCParameterName;
		return parameterNames;
	}

	@Override
	public IncomingSMSDto parseIncomingSMS(String... parameterValues) {
		
		int offset = parameterValues.length - parametersLength;
		
		String msisdn       = parameterValues[offset + MSISDNParameterIndex];
		String carrierName  = parameterValues[offset + CARRIER_NAMEParameterIndex];
		String largeAccount = parameterValues[offset + LAParameterIndex];
		String originalMoId = parameterValues[offset + MO_IDParameterIndex];
		String text         = parameterValues[offset + TEXTParameterIndex];
		
		if ((msisdn == null) || (carrierName == null) || (largeAccount == null) || (originalMoId == null) || (text == null)) {
			return null;
		}
		
		ESMSInParserCarrier carrier = ESMSInParserCarrier.valueOf(carrierName.toUpperCase());
		if (carrier == null) {
			carrier = ESMSInParserCarrier.UNKNOWN;	// probably will make 'sendReply' return 'REJECTED'
		}
		
		return new IncomingSMSDto(originalMoId, msisdn, text, carrier, largeAccount);
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
