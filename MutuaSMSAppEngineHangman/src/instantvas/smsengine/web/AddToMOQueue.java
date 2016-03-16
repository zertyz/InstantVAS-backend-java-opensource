package instantvas.smsengine.web;

import instantvas.smsengine.InstantVASHTTPInstrumentationRequestProperty;
import instantvas.smsengine.producersandconsumers.EInstantVASMOEvents;
import instantvas.smsengine.producersandconsumers.MOConsumer;
import instantvas.smsengine.producersandconsumers.MOProducer;
import instantvas.smsengine.producersandconsumers.MTConsumer;
import instantvas.smsengine.producersandconsumers.MTProducer;

import java.util.HashMap;
import java.util.Map;

import config.InstantVASApplicationConfiguration;
import mutua.events.EventClient;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.parsers.SMSInParser;
import mutua.smsin.parsers.SMSInParser.ESMSInParserSMSAcceptionStatus;
import static instantvas.smsengine.HangmanSMSGameServicesInstrumentationEvents.*;
import static instantvas.smsengine.HangmanSMSGameServicesInstrumentationProperties.*;


public class AddToMOQueue {
	
	private final Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log;
	private final InstantVASApplicationConfiguration ivac;
	
	// event consumers/producers
	private final MTProducer                       mtProducer;
	private final EventClient<EInstantVASMOEvents> moConsumer;
	private final MOProducer                       moProducer;
	
	// SMS Integration
	private final SMSInParser<Map<String, String>, byte[]>  moParser;		
		
	/*******************************************************************************************************************************************

	getting an MO:           curl 'http://domlap:8080/HangmanSMSGameServices/AddToMOQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21998019167&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting a subscriber:    curl 'http://domlap:8080/HangmanSMSGameServices/AddToSubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting an unsubscriber: curl 'http://domlap:8080/HangmanSMSGameServices/AddToUnsubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=coconuts'

	Listen to MTs with:        while sleep 1; do echo "Expecting an MT"; /home/luiz/Projetos/scripts/share mt 15001; done
	Listen to subsc. api with: while sleep 1; do echo "Expecting user registration attempt"; /home/luiz/Projetos/scripts/share ss 8082; done

	*******************************************************************************************************************************************/
	
	public AddToMOQueue(InstantVASApplicationConfiguration ivac) {
		this.ivac     = ivac;
		log           = ivac.log;
		mtProducer    = new MTProducer(ivac, new MTConsumer(ivac));
		moConsumer    = new MOConsumer(ivac, mtProducer);
		moProducer    = new MOProducer(ivac, moConsumer);
		moParser      = ivac.moParser;
	}

	public byte[] process(HashMap<String, String> parameters, String queryString) {
		log.reportRequestStart(queryString);
		byte[] response;
		IncomingSMSDto mo = moParser.parseIncomingSMS(parameters);
		if (mo == null) {
			response = moParser.getReply(ESMSInParserSMSAcceptionStatus.REJECTED);
			log.reportEvent(IE_MESSAGE_REJECTED);
		} else {
			try {
				moProducer.dispatchMOForProcessing(mo);
				log.reportEvent(IE_MESSAGE_ACCEPTED, IP_MO_MESSAGE, mo);
				response = moParser.getReply(ESMSInParserSMSAcceptionStatus.ACCEPTED);
			} catch (Throwable t) {
				response = moParser.getReply(ESMSInParserSMSAcceptionStatus.POSTPONED);
				log.reportThrowable(t, "Error detected while attempting to add an MO to the queue");
			}
				
		}
		log.reportRequestFinish();
		
		return response;
	}

}