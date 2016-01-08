package mutua.hangmansmsgame.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.WebAppConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.events.annotations.EventConsumer;
import mutua.hangmansmsgame.config.Configuration;
import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor.EHangmanSMSGameEvents;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.parsers.SMSInParser.ESMSInParserSMSAcceptionStatus;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.smsout.senders.SMSOutCelltick;
import mutua.smsout.senders.SMSOutSender;
import static config.WebAppConfiguration.*;
import static mutua.hangmansmsgame.HangmanSMSGameServicesInstrumentationProperties.*;
import static mutua.hangmansmsgame.HangmanSMSGameServicesInstrumentationEvents.*;


public class AddToMOQueue extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	
	/*******************************************************************************************************************************************

	getting an MO:           curl 'http://domlap:8080/HangmanSMSGameServices/AddToMOQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21998019167&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting a subscriber:    curl 'http://domlap:8080/HangmanSMSGameServices/AddToSubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=forca'
	getting an unsubscriber: curl 'http://domlap:8080/HangmanSMSGameServices/AddToUnsubscribeUserQueue?AUTHENTICATION_TOKEN=caosjufsojjfidsjf&MSISDN=21991234899&CARRIER_NAME=Vivo&LA=48925&MO_ID=1&TEXT=coconuts'

	Listen to MTs with:        while sleep 1; do echo "Expecting an MT"; /home/luiz/Projetos/scripts/share mt 15001; done
	Listen to subsc. api with: while sleep 1; do echo "Expecting user registration attempt"; /home/luiz/Projetos/scripts/share ss 8082; done

	*******************************************************************************************************************************************/
	
	static {
		new WebAppConfiguration();
	}

	// SMS APP
	//////////
	
	private static EventClient<EHangmanSMSGameEvents> gameMTConsumer    = new MTConsumer();
	private static MTProducer                         gameMTProducer    = new MTProducer(gameMTProducerAndConsumerLink, gameMTConsumer);

	protected static EventClient<EHangmanSMSGameEvents> gameMOConsumer    = new HangmanSMSGameProcessor(gameMTProducer);
	protected static MOProducer                         gameMOProducer    = new MOProducer(gameMOProducerAndConsumerLink, gameMOConsumer);
	

	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.reportRequestStart(request.getQueryString());
		IncomingSMSDto mo = smsParser.parseIncomingSMS(request.getParameterMap());
		byte[] contents;
		if (mo == null) {
			contents = smsParser.getReply(ESMSInParserSMSAcceptionStatus.REJECTED);
			log.reportEvent(IE_MESSAGE_REJECTED);
		} else {
			try {
				gameMOProducer.addToMOQueue(mo);
				log.reportEvent(IE_MESSAGE_ACCEPTED, IP_MO_MESSAGE, mo);
				contents = smsParser.getReply(ESMSInParserSMSAcceptionStatus.ACCEPTED);
			} catch (Throwable t) {
				contents = smsParser.getReply(ESMSInParserSMSAcceptionStatus.POSTPONED);
				log.reportThrowable(t, "Error detected while attempting to add an MO to the queue");
			}
		}
		log.reportRequestFinish();
		response.setContentType("text/plain");
		response.setContentLength(contents.length);
		response.getOutputStream().write(contents);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);

		// uncomment to write a brand new configuration file
//		try {
//			ConfigurationManager cm = new ConfigurationManager(configurationLog, WebAppConfiguration.class, Configuration.class);
//			cm.saveToFile("/tmp/hangman.config");
//		} catch (Throwable t) {
//			t.printStackTrace();
//		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}


/******************************
** IResponseReceiver CLASSES **
******************************/

class MTProducer extends EventServer<EHangmanSMSGameEvents> implements IResponseReceiver {

	protected MTProducer(IEventLink<EHangmanSMSGameEvents> link, EventClient<EHangmanSMSGameEvents> mtConsumer) {
		super(link);
		try {
			addClient(mtConsumer);
		} catch (IndirectMethodNotFoundException e) {
			log.reportThrowable(e, "Error while adding mtConsumer");
		}
	}

	@Override
	public void onMessage(OutgoingSMSDto outgoingMessage, IncomingSMSDto incomingMessage) {
		dispatchConsumableEvent(EHangmanSMSGameEvents.INTERACTIVE_REQUEST, outgoingMessage);
	}
	
}

class MTConsumer implements EventClient<EHangmanSMSGameEvents> {
	
	private static SMSOutSender smsSender = new SMSOutCelltick(
			Configuration.log, Configuration.APPID + " interaction", Configuration.SHORT_CODE, Configuration.MT_SERVICE_URL,
			Configuration.MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS, Configuration.MT_SERVICE_DELAY_BETWEEN_ATTEMPTS);
	
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


class MOProducer extends EventServer<EHangmanSMSGameEvents> {

	public MOProducer(IEventLink<EHangmanSMSGameEvents> link, EventClient<EHangmanSMSGameEvents> moConsumer) {
		super(link);
		try {
			addClient(moConsumer);
		} catch (IndirectMethodNotFoundException e) {
			log.reportThrowable(e, "Error while adding moConsumer");
		}
	}
	
	public boolean addToMOQueue(IncomingSMSDto mo) {
		return dispatchNeedToBeConsumedEvent(EHangmanSMSGameEvents.INTERACTIVE_REQUEST, mo);
	}
	
	public boolean addToSubscribeUserQueue(String phone) {
		return dispatchNeedToBeConsumedEvent(EHangmanSMSGameEvents.SUBSCRIBE_USER, phone);
	}
}