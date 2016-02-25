package instantvas.smsengine.web;

import instantvas.smsengine.HangmanHTTPInstrumentationRequestProperty;

import java.io.IOException;
import java.util.HashMap;

import config.HangmanSMSModulesConfiguration;
import config.InstantVASSMSEngineConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.events.QueueEventLink;
import mutua.events.annotations.EventConsumer;
import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.hangmansmsgame.smslogic.SMSProcessor;
import mutua.hangmansmsgame.smslogic.SMSProcessorException;
import mutua.icc.configuration.ConfigurationManager;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.eventclients.InstrumentationProfilingEventsClient;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsappmodule.hangmangame.HangmanGame.EHangmanGameStates;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.parsers.SMSInCelltick;
import mutua.smsin.parsers.SMSInParser;
import mutua.smsin.parsers.SMSInParser.ESMSInParserSMSAcceptionStatus;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.smsout.senders.SMSOutCelltick;
import mutua.smsout.senders.SMSOutSender;
import mutua.subscriptionengine.CelltickLiveScreenSubscriptionAPI;
import mutua.subscriptionengine.TestableSubscriptionAPI;
import static config.InstantVASSMSEngineConfiguration.*;
import static instantvas.smsengine.HangmanSMSGameServicesInstrumentationEvents.*;
import static instantvas.smsengine.HangmanSMSGameServicesInstrumentationProperties.*;
import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;
import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;


public class AddToMOQueue {
		
	/*******************************************************************************************************************************************

	getting an MO:           curl 'http://domlap:8080/HangmanSMSGameServices/AddToMOQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21998019167&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting a subscriber:    curl 'http://domlap:8080/HangmanSMSGameServices/AddToSubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting an unsubscriber: curl 'http://domlap:8080/HangmanSMSGameServices/AddToUnsubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=coconuts'

	Listen to MTs with:        while sleep 1; do echo "Expecting an MT"; /home/luiz/Projetos/scripts/share mt 15001; done
	Listen to subsc. api with: while sleep 1; do echo "Expecting user registration attempt"; /home/luiz/Projetos/scripts/share ss 8082; done

	*******************************************************************************************************************************************/
	
	static {
		new InstantVASSMSEngineConfiguration();
	}

	// SMS APP
	//////////
	
	private static EventClient<EHangmanGameStates> gameMTConsumer    = new MTConsumer();
	private static MTProducer                      gameMTProducer    = new MTProducer(gameMTProducerAndConsumerLink, gameMTConsumer);

	protected static EventClient<EHangmanGameStates> gameMOConsumer    = new MOConsumer(HangmanSMSModulesConfiguration.log, gameMTProducer);
	protected static MOProducer                      gameMOProducer    = new MOProducer(gameMOProducerAndConsumerLink, gameMOConsumer);
	

	public static byte[] process(HashMap<String, String> parameters, String queryString) {
		log.reportRequestStart(queryString);
		byte[] response;
		IncomingSMSDto mo = smsParser.parseIncomingSMS(parameters);
		if (mo == null) {
			response = smsParser.getReply(ESMSInParserSMSAcceptionStatus.REJECTED);
			log.reportEvent(IE_MESSAGE_REJECTED);
		} else {
			try {
				gameMOProducer.addToMOQueue(mo);
				log.reportEvent(IE_MESSAGE_ACCEPTED, IP_MO_MESSAGE, mo);
				response = smsParser.getReply(ESMSInParserSMSAcceptionStatus.ACCEPTED);
			} catch (Throwable t) {
				response = smsParser.getReply(ESMSInParserSMSAcceptionStatus.POSTPONED);
				log.reportThrowable(t, "Error detected while attempting to add an MO to the queue");
			}
				
		}
		log.reportRequestFinish();
		
		return response;
	}

}


/******************************
** IResponseReceiver CLASSES **
******************************/

class MTProducer extends EventServer<EHangmanGameStates> implements IResponseReceiver {

	protected MTProducer(IEventLink<EHangmanGameStates> link, EventClient<EHangmanGameStates> mtConsumer) {
		super(link);
		try {
			addListener(mtConsumer);
		} catch (IndirectMethodNotFoundException e) {
			log.reportThrowable(e, "Error while adding mtConsumer");
		}
	}

	@Override
	public void onMessage(OutgoingSMSDto outgoingMessage, IncomingSMSDto incomingMessage) {
		dispatchConsumableEvent(EHangmanGameStates.WON, outgoingMessage);
	}
	
}

class MTConsumer implements EventClient<EHangmanGameStates> {
	
	private static SMSOutSender smsSender = new SMSOutCelltick(
			HangmanSMSModulesConfiguration.log, HangmanSMSModulesConfiguration.APPID + " interaction", HangmanSMSModulesConfiguration.SHORT_CODE, HangmanSMSModulesConfiguration.MT_SERVICE_URL,
			HangmanSMSModulesConfiguration.MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS, HangmanSMSModulesConfiguration.MT_SERVICE_DELAY_BETWEEN_ATTEMPTS);
	
	@EventConsumer("INTERACTIVE_REQUEST")
	public void sendMT(OutgoingSMSDto mt) {
		log.reportDebug("sending interactive SMS -- " + mt);
		try {
			smsSender.sendMessage(mt);
		} catch (Throwable t) {
			log.reportThrowable(t, "Error while sending mt -- " + mt);
		}
	}
	
}


class MOProducer extends EventServer<EHangmanGameStates> {

	public MOProducer(IEventLink<EHangmanGameStates> link, EventClient<EHangmanGameStates> moConsumer) {
		super(link);
		try {
			addListener(moConsumer);
		} catch (IndirectMethodNotFoundException e) {
			log.reportThrowable(e, "Error while adding moConsumer");
		}
	}
	
	public boolean addToMOQueue(IncomingSMSDto mo) {
		return dispatchNeedToBeConsumedEvent(EHangmanGameStates.WON, mo);
	}
	
	public boolean addToSubscribeUserQueue(String phone) {
		return dispatchNeedToBeConsumedEvent(EHangmanGameStates.WON, phone);
	}
}

class MOConsumer implements EventClient<EHangmanGameStates> {
	
	private SMSProcessor smsP;
	
	public MOConsumer(Instrumentation<HangmanHTTPInstrumentationRequestProperty, String> log, MTProducer gameMTProducer) {
		smsP = new SMSProcessor(log, gameMTProducer);
	}
	
	@EventConsumer("WON")
	public void processMO(IncomingSMSDto MO) {
		try {
			smsP.process(MO);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}