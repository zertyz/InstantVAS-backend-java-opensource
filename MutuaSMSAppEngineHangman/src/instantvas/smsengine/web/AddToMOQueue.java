package instantvas.smsengine.web;

import instantvas.smsengine.InstantVASHTTPInstrumentationRequestProperty;
import instantvas.smsengine.web.MOProducer.EInstantVASMOEvents;
import instantvas.smsengine.web.MOProducer.InstantVASMOEvent;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;

import config.InstantVASApplication;
import config.InstantVASApplicationConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.events.QueueEventLink;
import mutua.events.TestAdditionalEventServer.ETestAdditionalEventServices;
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
import static instantvas.smsengine.HangmanSMSGameServicesInstrumentationEvents.*;
import static instantvas.smsengine.HangmanSMSGameServicesInstrumentationProperties.*;
import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;
import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;


public class AddToMOQueue {
	
	private final Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log;
	private final InstantVASApplicationConfiguration ivac;
	
	// event consumers/producers
	private final MTProducer                      gameMTProducer;
	private final EventClient<EHangmanGameStates> gameMOConsumer;
	private final MOProducer                      gameMOProducer;
	
	// integration
	private final SMSInParser<Map, byte[]>  MOParser;
	private final SMSOutSender                  MTSender;
	
		
	/*******************************************************************************************************************************************

	getting an MO:           curl 'http://domlap:8080/HangmanSMSGameServices/AddToMOQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21998019167&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting a subscriber:    curl 'http://domlap:8080/HangmanSMSGameServices/AddToSubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting an unsubscriber: curl 'http://domlap:8080/HangmanSMSGameServices/AddToUnsubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=coconuts'

	Listen to MTs with:        while sleep 1; do echo "Expecting an MT"; /home/luiz/Projetos/scripts/share mt 15001; done
	Listen to subsc. api with: while sleep 1; do echo "Expecting user registration attempt"; /home/luiz/Projetos/scripts/share ss 8082; done

	*******************************************************************************************************************************************/
	
	public AddToMOQueue(Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log,
	                    InstantVASApplicationConfiguration ivac) {
		this.log  = log;
		this.ivac = ivac;
		gameMTProducer    = new MTProducer(ivac.MTpcLink, new MTConsumer());
		gameMOConsumer    = new MOConsumer(log,           gameMTProducer);
		gameMOProducer    = new MOProducer(ivac.MOpcLink, gameMOConsumer);
		MOParser = ivac.MOParser;
		MTSender = ivac.MTSender;
	}

	public byte[] process(HashMap<String, String> parameters, String queryString) {
		log.reportRequestStart(queryString);
		byte[] response;
		IncomingSMSDto mo = MOParser.parseIncomingSMS(parameters);
		if (mo == null) {
			response = MOParser.getReply(ESMSInParserSMSAcceptionStatus.REJECTED);
			log.reportEvent(IE_MESSAGE_REJECTED);
		} else {
			try {
				gameMOProducer.dispatchMOForProcessing(mo);
				log.reportEvent(IE_MESSAGE_ACCEPTED, IP_MO_MESSAGE, mo);
				response = MOParser.getReply(ESMSInParserSMSAcceptionStatus.ACCEPTED);
			} catch (Throwable t) {
				response = MOParser.getReply(ESMSInParserSMSAcceptionStatus.POSTPONED);
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
			InstantVASApplication.log, InstantVASApplication.APPID + " interaction", InstantVASApplication.SHORT_CODE, InstantVASApplication.MT_SERVICE_URL,
			InstantVASApplication.MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS, InstantVASApplication.MT_SERVICE_DELAY_BETWEEN_ATTEMPTS);
	
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


class MOProducer extends EventServer<EInstantVASMOEvents> {

	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) public @interface InstantVASMOEvent {
		EInstantVASMOEvents[] value();
	}
	
	public enum EInstantVASMOEvents {
		MO_ARRIVED,
	}
	
	public MOProducer(Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log,
	                  IEventLink<EInstantVASMOEvents> link,
	                  EventClient<EInstantVASMOEvents> moConsumer) {
		super(link);
		try {
			setConsumer(moConsumer);
		} catch (IndirectMethodNotFoundException e) {
			log.reportThrowable(e, "Error while setting moConsumer");
		}
	}
	
	public int dispatchMOForProcessing(IncomingSMSDto mo) {
		return dispatchConsumableEvent(EInstantVASMOEvents.MO_ARRIVED, mo);
	}
	
//	public boolean addToSubscribeUserQueue(String phone) {
//		return dispatchNeedToBeConsumedEvent(EHangmanGameStates.WON, phone);
//	}
}

class MOConsumer implements EventClient<EInstantVASMOEvents> {
	
	private SMSProcessor smsP;
	
	public MOConsumer(Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log, MTProducer gameMTProducer) {
		smsP = new SMSProcessor(log, gameMTProducer);
	}
	
	@InstantVASMOEvent(EInstantVASMOEvents.MO_ARRIVED)
	public void processMO(IncomingSMSDto MO) {
		try {
			smsP.process(MO);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}