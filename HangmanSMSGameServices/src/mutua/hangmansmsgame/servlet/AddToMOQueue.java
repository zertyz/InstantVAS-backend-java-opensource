package mutua.hangmansmsgame.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.events.QueueEventLink;
import mutua.hangmansmsgame.HangmanHTTPInstrumentationRequestProperty;
import mutua.hangmansmsgame.config.Configuration;
import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor.EHangmanSMSGameEvents;
import mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.eventclients.InstrumentationProfilingEventsClient;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.parsers.SMSInCelltick;
import mutua.smsin.parsers.SMSInParser;
import mutua.smsin.parsers.SMSInParser.ESMSInParserSMSAcceptionStatus;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.smsout.senders.SMSOutCelltick;
import mutua.smsout.senders.SMSOutSender;
import mutua.subscriptionengine.CelltickLiveScreenSubscriptionAPI;
import mutua.subscriptionengine.TestableSubscriptionAPI;
import static mutua.hangmansmsgame.config.Configuration.log;
import static mutua.icc.instrumentation.DefaultInstrumentationEvents.DIE_DEBUG;
import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;


/************
** SERVLET **
************/

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
    	Instrumentation<HangmanHTTPInstrumentationRequestProperty, String> log;
    	log = new Instrumentation<HangmanHTTPInstrumentationRequestProperty, String>(Configuration.APPID, new HangmanHTTPInstrumentationRequestProperty(), HangmanSMSGameInstrumentationEvents.values());
    	try {
        	InstrumentationProfilingEventsClient instrumentationProfilingEventsClient = new InstrumentationProfilingEventsClient(log, EInstrumentationDataPours.CONSOLE);
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}
    	Configuration.log = log;
    	Configuration.SUBSCRIPTION_ENGINE = new CelltickLiveScreenSubscriptionAPI(log, Configuration.SUBSCRIBE_SERVICE_URL, Configuration.UNSUBSCRIBE_SERVICE_URL);
    	try {
			Configuration.loadFromFile("/tmp/hangman.config");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	
	// MO
	/////
	
	private static SMSInParser<HttpServletRequest, HttpServletResponse>  smsParser = new SMSInCelltick(Configuration.APPID);
	
	
	// SMS APP
	//////////
	
	private static IEventLink<EHangmanSMSGameEvents>  producerAndConsumerLink = new QueueEventLink<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.class, Configuration.QUEUE_CAPACITY, Configuration.QUEUE_NUMBER_OF_WORKER_THREADS);
	private static EventClient<EHangmanSMSGameEvents> gameMessagesConsumer    = new HangmanSMSGameProcessor(new InteractiveReceiver());
	private static MOProducer                         gameMessagesProducer    = new MOProducer(producerAndConsumerLink, gameMessagesConsumer);

	private void process(HttpServletRequest request, HttpServletResponse response) {
		try {
			IncomingSMSDto mo = smsParser.parseIncomingSMS(request);
			if (mo == null) {
				log.reportDebug("received an incorrect MO request -- " + request.getQueryString());
				smsParser.sendReply(ESMSInParserSMSAcceptionStatus.REJECTED, response);
			} else {
				log.reportDebug("adding MO to the queue -- " + mo.toString());
				gameMessagesProducer.addToMOQueue(mo);
				smsParser.sendReply(ESMSInParserSMSAcceptionStatus.ACCEPTED, response);
			}
		} catch (Throwable e) {
			log.reportUncoughtThrowable(e, "Error detected while attempting to add an MO to the queue");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}


/******************************
** IResponseReceiver CLASSES **
******************************/

class InteractiveReceiver implements IResponseReceiver {

	private static SMSOutSender smsSender = new SMSOutCelltick(
			Configuration.log, Configuration.APPID + " interaction", Configuration.SHORT_CODE, Configuration.MT_SERVICE_URL,
			Configuration.MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS, Configuration.MT_SERVICE_DELAY_BETWEEN_ATTEMPTS);
	
	@Override
	public void onMessage(OutgoingSMSDto outgoingMessage, IncomingSMSDto incomingMessage) {
		log.reportDebug("sending interactive SMS -- " + outgoingMessage);
		smsSender.sendMessage(outgoingMessage);
	}
	
}


class MOProducer extends EventServer<EHangmanSMSGameEvents> {

	public MOProducer(IEventLink<EHangmanSMSGameEvents> link, EventClient<EHangmanSMSGameEvents> consumerClient) {
		super(link);
		try {
			addClient(consumerClient);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void addToMOQueue(IncomingSMSDto mo) {
		dispatchNeedToBeConsumedEvent(EHangmanSMSGameEvents.PROCESS_INCOMING_SMS, mo);
	}
}