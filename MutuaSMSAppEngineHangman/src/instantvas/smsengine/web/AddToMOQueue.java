package instantvas.smsengine.web;

import static config.InstantVASLicense.*;
import static config.MutuaHardCodedConfiguration.IFDEF_USE_STRICT_GET_PARSER;
import static config.MutuaHardCodedConfiguration.IFDEF_WEB_DEBUG;

import instantvas.nativewebserver.NativeHTTPServer;
import instantvas.smsengine.HangmanSMSGameServicesInstrumentationEvents;
import instantvas.smsengine.InstantVASHTTPInstrumentationRequestProperty;
import instantvas.smsengine.producersandconsumers.IMOProducer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import mutua.icc.instrumentation.Instrumentation;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsin.parsers.SMSInParser;
import mutua.smsin.parsers.SMSInParser.ESMSInParserSMSAcceptionStatus;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static instantvas.smsengine.HangmanSMSGameServicesInstrumentationEvents.*;
import static instantvas.smsengine.HangmanSMSGameServicesInstrumentationProperties.*;


public class AddToMOQueue {
	
	private final Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log;
	
	// license infringment control
    private final int                   allowableMSISDNMinLength;
    private final int                   allowableMSISDNMaxLength;
    private final String[]              allowableMSISDNPrefixes;
    private final ESMSInParserCarrier[] allowableCarriers;
    private final String[]              allowableShortCodes;
	
	
	// event producer for new MOs
	private final IMOProducer moProducer;
	
	// SMS Integration
	private final SMSInParser<?, byte[]>  moParser;

	// native web server fields
	static final int    AUTHENTICATION_TOKENParameterIndex       = 0;
	static final String AUTHENTICATION_TOKENParameterName        = "AUTHENTICATION_TOKEN";
	static final int    PRECEDING_REQUEST_PARAMETERS_LENGTH      = 1;
	final String[] parameterNames;

	// additional request responses
	static final byte[] BAD_REQUEST        = "BAD REQUEST".getBytes();
	static final byte[] BAD_AUTHENTICATION = "BAD AUTHENTICATION".getBytes();	
	

		
	/*******************************************************************************************************************************************

	getting an MO:           curl 'http://domlap:8080/HangmanSMSGameServices/AddToMOQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21998019167&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting a subscriber:    curl 'http://domlap:8080/HangmanSMSGameServices/AddToSubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting an unsubscriber: curl 'http://domlap:8080/HangmanSMSGameServices/AddToUnsubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=coconuts'

	Listen to MTs with:        while sleep 1; do echo "Expecting an MT"; /home/luiz/Projetos/scripts/share mt 15001; done
	Listen to subsc. api with: while sleep 1; do echo "Expecting user registration attempt"; /home/luiz/Projetos/scripts/share ss 8082; done

	*******************************************************************************************************************************************/
	
	public AddToMOQueue(Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log,
	                    IMOProducer            moProducer,
	                    SMSInParser<?, byte[]> moParser,
	                    int                    allowableMSISDNMinLength,
	                    int                    allowableMSISDNMaxLength,
	                    String[]               allowableMSISDNPrefixes,
	                    ESMSInParserCarrier[]  allowableCarriers,
	                    String[]               allowableShortCodes) {

		log.addInstrumentableEvents(HangmanSMSGameServicesInstrumentationEvents.values());

		this.log        = log;		
		this.moProducer = moProducer;
		this.moParser   = moParser;
		this.allowableMSISDNMinLength = allowableMSISDNMinLength;
		this.allowableMSISDNMaxLength = allowableMSISDNMaxLength;
		this.allowableMSISDNPrefixes  = allowableMSISDNPrefixes;		
		this.allowableCarriers        = allowableCarriers;
		this.allowableShortCodes      = allowableShortCodes;
		
		parameterNames = moParser.getRequestParameterNames(AUTHENTICATION_TOKENParameterName);
		/* debug */ if (IFDEF_WEB_DEBUG) {log.reportDebug("/AddToMOQueue: Expected Parameters: " + Arrays.deepToString(parameterNames));}
	}
	
	public boolean attemptToAuthenticateFromStrictGetParameters(String[] parameterValues) {
		// code made in 'InstantVASLicenseTests' and shared between 'NavitaHTTPServer.ADD_TO_MO_QUEUE' and 'AddToMOQueue' servlet.
		if (// test the authentication token
		    ((INSTANTVAS_INSTANCE_CONFIGn_LENGTH > 0) && (!INSTANTVAS_INSTANCE_CONFIG0_TOKEN.equals(parameterValues[AUTHENTICATION_TOKENParameterIndex]))) ||
			// test additional MO parameter values -- EQUALS check method
		    ((IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES == HardCodeCheckMethodOfAdditionalMOParameterValues_EQUALS) && (
		     ((MO_ADDITIONAL_RULEn_LENGTH > 0) && (!MO_ADDITIONAL_RULE0_VALUE.equals(parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX])))
		    )) ||
			// test additional MO parameter values -- STARTS_WITH with MAX_LEN check method
		    ((IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES == HardCodeCheckMethodOfAdditionalMOParameterValues_STARTS_WITH) && (
		     ((MO_ADDITIONAL_RULEn_LENGTH > 0) && ( (parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX] == null) || (parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX].length() > MO_ADDITIONAL_RULE0_MAX_LEN) || (!parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX].startsWith(MO_ADDITIONAL_RULE0_VALUE))) )
		    )) ||
		    // test additional MO parameter values -- REGEX
		    ((IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES == HardCodeCheckMethodOfAdditionalMOParameterValues_REGEX) && (
		     ((MO_ADDITIONAL_RULEn_LENGTH > 0) && ( (parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX] == null) || (!MO_ADDITIONAL_RULE0_REGEX.matcher(parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]).matches()) ) )
		   )) ) {
			return false;
		} else {
			return true;
		}
	}
	
	/** Method to be called from the native HTTP handler (passive HTTPD server service) -- receives only a query String */
	public byte[] processRequest(String queryString) throws UnsupportedEncodingException {
		byte[] response;
		String[] parameterValues;
		
		if (IFDEF_USE_STRICT_GET_PARSER) {
			if (queryString != null) {
				parameterValues = NativeHTTPServer.retrieveStrictGetParameters(parameterNames, queryString);
			} else {
				parameterValues = null;
			}
		} else {
			throw new NotImplementedException();
		}
		
		/* debug */ if (IFDEF_WEB_DEBUG) {log.reportDebug("/AddToMOQueue: " + Arrays.deepToString(parameterValues));}
		if (parameterValues == null) {
			/* debug */ if (IFDEF_WEB_DEBUG) {log.reportDebug("/AddToMOQueue " + new String(BAD_REQUEST) + ": " + queryString);}
			response = BAD_REQUEST;
		} else if (attemptToAuthenticateFromStrictGetParameters(parameterValues)) {
			/* debug */ if (IFDEF_WEB_DEBUG) {
				log.reportDebug("/AddToMOQueue " + new String(BAD_AUTHENTICATION) + ": " + queryString);
				log.reportDebug("IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES=" + IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES);
				log.reportDebug("MO_ADDITIONAL_RULEn_LENGTH=" + MO_ADDITIONAL_RULEn_LENGTH);
				log.reportDebug("parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]=" + parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]);
				log.reportDebug("MO_ADDITIONAL_RULE0_REGEX.matcher(parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]).matches()" + MO_ADDITIONAL_RULE0_REGEX.matcher(parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]).matches());
			}
			response = BAD_AUTHENTICATION;
		} else {
			// the request is allowed. Proceed.
			IncomingSMSDto mo = moParser.parseIncomingSMS(parameterValues);
			response = processRequest(mo, queryString);
		}
		return response;
	}
	
	/** Method to be called from the active HTTP queue client -- receives a baunch of query strings */
	public void processRequests(String[] queryStringSet) throws UnsupportedEncodingException {
		String[] parameterValues;
		IncomingSMSDto[] moSet = new IncomingSMSDto[queryStringSet.length];
		
		// batch authenticate
		for (int i=0; i<queryStringSet.length; i++) {
			
			String queryString = queryStringSet[i];
		
			if (IFDEF_USE_STRICT_GET_PARSER) {
				if (queryString != null) {
					parameterValues = NativeHTTPServer.retrieveStrictGetParameters(parameterNames, queryString);
				} else {
					parameterValues = null;
				}
			} else {
				throw new NotImplementedException();
			}
			
			/* debug */ if (IFDEF_WEB_DEBUG) {log.reportDebug("/AddToMOQueue: " + Arrays.deepToString(parameterValues));}
			if (parameterValues == null) {
				/* debug */ if (IFDEF_WEB_DEBUG) {log.reportDebug("/AddToMOQueue " + new String(BAD_REQUEST) + ": " + queryString);}
				moSet[i] = null;
			} else if (attemptToAuthenticateFromStrictGetParameters(parameterValues)) {
				/* debug */ if (IFDEF_WEB_DEBUG) {
					log.reportDebug("/AddToMOQueue " + new String(BAD_AUTHENTICATION) + ": " + queryString);
					log.reportDebug("IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES=" + IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES);
					log.reportDebug("MO_ADDITIONAL_RULEn_LENGTH=" + MO_ADDITIONAL_RULEn_LENGTH);
					log.reportDebug("parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]=" + parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]);
					log.reportDebug("MO_ADDITIONAL_RULE0_REGEX.matcher(parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]).matches()" + MO_ADDITIONAL_RULE0_REGEX.matcher(parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]).matches());
				}
				moSet[i] = null;
			} else {
				// the request is allowed. Proceed.
				moSet[i] = moParser.parseIncomingSMS(parameterValues);
			}
		}
		
		// batch process
		processRequests(moSet, queryStringSet);
	}

	private boolean isPrefixAllowed(String msisdn) {
		if (allowableMSISDNPrefixes == null) {
			return true;
		}
		for (String allowedPrefix : allowableMSISDNPrefixes) {
			if (msisdn.indexOf(allowedPrefix) != -1) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isCarrierAllowed(ESMSInParserCarrier carrier) {
		for (ESMSInParserCarrier allowedCarrier : allowableCarriers) {
			if (allowedCarrier == carrier) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isShortCodeAllowed(String shortCode) {
		for (String allowedShortCode : allowableShortCodes) {
			if (shortCode.equals(allowedShortCode)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean attemptToValidateMO(IncomingSMSDto mo) {
		if ((IFDEF_HARCODED_INSTANCE_RESTRICTION && (
		    // msisdn length validation
		    ( ((ALLOWABLE_MSISDN_MIN_LENGTH == -1) || (mo.getPhone().length() >= ALLOWABLE_MSISDN_MIN_LENGTH)) && 
		      ((ALLOWABLE_MSISDN_MAX_LENGTH == -1) || (mo.getPhone().length() <= ALLOWABLE_MSISDN_MAX_LENGTH)) ) &&
		    // msisdn prefixes validation
		    ( ((ALLOWABLE_MSISDN_PREFIXn_LENGTH > 0) && (mo.getPhone().startsWith(ALLOWABLE_MSISDN_PREFIX0)))/* ||
		      ((ALLOWABLE_MSISDN_PREFIXn_LENGTH > 1) && (mo.getPhone().startsWith(ALLOWABLE_MSISDN_PREFIX1))) ||*/
		    ) &&
		    // carrier validation
		    ( ((ALLOWABLE_CARRIERn_LENGTH > 0) && (mo.getCarrier() == ALLOWABLE_CARRIER0))/* ||
		      ((ALLOWABLE_CARRIERn_LENGTH > 1) && (mo.getCarrier() == ALLOWABLE_CARRIER1)) ||*/
		    ) &&
		    // allowable short codes validation
		    ( ((ALLOWABLE_SHORT_CODEn_LENGTH > 0) && (ALLOWABLE_SHORT_CODE0.equals(mo.getLargeAccount()))) ||
		      ((ALLOWABLE_SHORT_CODEn_LENGTH > 1) && (ALLOWABLE_SHORT_CODE1.equals(mo.getLargeAccount())))
		   ) )) ||
		   // use the flexible (non-hard coded) version of the validation
		   ((!IFDEF_HARCODED_INSTANCE_RESTRICTION) && (
		    ( ((allowableMSISDNMinLength == -1)   || (mo.getPhone().length() >= allowableMSISDNMinLength)) &&
		      ((allowableMSISDNMaxLength == -1)   || (mo.getPhone().length() <= allowableMSISDNMaxLength)) ) && 
		    ((allowableMSISDNPrefixes  == null) || (isPrefixAllowed(mo.getPhone())))                         &&
		    ((allowableCarriers        == null) || (isCarrierAllowed(mo.getCarrier())))                      &&
		    ((allowableShortCodes      == null) || (isShortCodeAllowed(mo.getLargeAccount())))
		   )) ) {
			return true;
		} else {
			return false;
		}
	}

	public void processRequests(IncomingSMSDto[] moSet, String[] requestDataSet) {
		
		log.reportRequestStart("AddToMOQueue in batch: " + Arrays.deepToString(requestDataSet));
		
		ArrayList<IncomingSMSDto> moSetAcceptedForProcessing          = new ArrayList<IncomingSMSDto>(moSet.length+1);
		ArrayList<String>         requestDataSetAcceptedForProcessing = new ArrayList<String>(moSet.length+1);
		
		for (int i=0; i<moSet.length; i++) {
			
			IncomingSMSDto mo          = moSet[i];
			String         requestData = requestDataSet[i];
			
			if (mo == null) {
				log.reportEvent(IE_MESSAGE_REJECTED, IP_REQUEST_DATA, requestData);
			} else if (attemptToValidateMO(mo)) {
				
				log.reportEvent(IE_MESSAGE_ACCEPTED, IP_MO_MESSAGE, mo);
				moSetAcceptedForProcessing         .add(mo);
				requestDataSetAcceptedForProcessing.add(requestData);
				
			} else {
				log.reportEvent(IE_LICENSE_INFRINGMENT, IP_MO_MESSAGE, mo);
			}
		}
		
		moProducer.dispatchMOsForProcessing(moSetAcceptedForProcessing.toArray(new IncomingSMSDto[0]));

		log.reportRequestFinish();
	}
	
	/** Method to be called from web handlers, such as Tomcat Servlets */
	public byte[] processRequest(IncomingSMSDto mo, String requestData) {
		byte[] response;
		log.reportRequestStart("AddToMOQueue " + requestData);
		if (mo == null) {
			log.reportEvent(IE_MESSAGE_REJECTED, IP_REQUEST_DATA, requestData);
			response = moParser.getReply(ESMSInParserSMSAcceptionStatus.REJECTED);
		} else if (attemptToValidateMO(mo)) {
			
			try {
				log.reportEvent(IE_MESSAGE_ACCEPTED, IP_MO_MESSAGE, mo);
				moProducer.dispatchMOForProcessing(mo);
				response = moParser.getReply(ESMSInParserSMSAcceptionStatus.ACCEPTED);
			} catch (Throwable t) {
				response = moParser.getReply(ESMSInParserSMSAcceptionStatus.POSTPONED);
				log.reportThrowable(t, "Error detected while attempting to add an MO to the queue");
			}
			
		} else {
			log.reportEvent(IE_LICENSE_INFRINGMENT, IP_MO_MESSAGE, mo);
			response = moParser.getReply(ESMSInParserSMSAcceptionStatus.REJECTED);
		}

		log.reportRequestFinish();
		return response;
	}

}