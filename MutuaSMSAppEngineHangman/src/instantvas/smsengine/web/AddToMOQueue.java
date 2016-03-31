package instantvas.smsengine.web;

import static config.InstantVASLicense.*;
import instantvas.smsengine.HangmanSMSGameServicesInstrumentationEvents;
import instantvas.smsengine.InstantVASHTTPInstrumentationRequestProperty;
import instantvas.smsengine.producersandconsumers.EInstantVASEvents;
import instantvas.smsengine.producersandconsumers.IMOProducer;
import instantvas.smsengine.producersandconsumers.MOProducer;

import java.util.Map;

import mutua.events.EventServer;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsin.parsers.SMSInParser;
import mutua.smsin.parsers.SMSInParser.ESMSInParserSMSAcceptionStatus;
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

	public byte[] processRequest(IncomingSMSDto mo, String requestData) {
		byte[] response;
		log.reportRequestStart("AddToMOQueue " + requestData);
		if (mo == null) {
			log.reportEvent(IE_MESSAGE_REJECTED, IP_REQUEST_DATA, requestData);
			response = moParser.getReply(ESMSInParserSMSAcceptionStatus.REJECTED);
		} else if ((IFDEF_HARCODED_INSTANCE_RESTRICTION && (
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