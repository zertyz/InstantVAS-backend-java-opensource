package mutua.iccapp.HangmanSMSGame;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mutua.events.DirectEventLink;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.hangmansmsgame.config.Configuration;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor.EHangmanSMSGameEvents;
import mutua.hangmansmsgame.smslogic.SMSProcessorException;
import mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.eventclients.InstrumentationProfilingEventsClient;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.subscriptionengine.TestableSubscriptionAPI;

/** <pre>
 * SMSAppFrontend.java  --  $Id$
 * ===================
 * (created by luiz, Feb 4, 2011)
 *
 * Do the interfacing between the graphical application and the SMS service engine
 * 
 */

public class SMSAppFrontend extends EventServer<EHangmanSMSGameEvents>{

	
	private static IEventLink<EHangmanSMSGameEvents> link = new DirectEventLink<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.class);

	private static SimulationMessageReceiver simulationMessageReceiver = new SimulationMessageReceiver();
    private static HangmanSMSGameProcessor processor = new HangmanSMSGameProcessor(simulationMessageReceiver);
	private static Instrumentation<ICCAppInstrumentationRequestProperty, String> log;
    
    
    static {
    	log = new Instrumentation<ICCAppInstrumentationRequestProperty, String>("HangmanSMSGameICCApp", new ICCAppInstrumentationRequestProperty("phone"), HangmanSMSGameInstrumentationEvents.values());
    	try {
        	InstrumentationProfilingEventsClient instrumentationProfilingEventsClient = new InstrumentationProfilingEventsClient(log, EInstrumentationDataPours.CONSOLE);
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}
    	Configuration.SUBSCRIPTION_ENGINE = new TestableSubscriptionAPI(log);
    	Configuration.log = log;
    	try {
			Configuration.loadFromFile("/tmp/hangman.config");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    
    public SMSAppFrontend() {
    	super(link);
    	try {
			addClient(processor);
		} catch (IndirectMethodNotFoundException e) {
			log.reportThrowable(e, "Error while adding EventClient to process SMSes");
		}
	}

    public String[][] process(String phone, String inputText, String carrier) {
        IncomingSMSDto mo = new IncomingSMSDto(phone, inputText, ESMSInParserCarrier.valueOf(carrier), "1234", "null");
//        try {
        	log.reportRequestStart(phone);
        	dispatchNeedToBeConsumedEvent(EHangmanSMSGameEvents.PROCESS_INCOMING_SMS, mo);
			//processor.process(mo);
			log.reportRequestFinish();
//		} catch (SMSProcessorException e) {
//			Logger.getLogger(HangmanSMSGameICCAppView.class.getName()).log(Level.SEVERE, null, e);
//			e.printStackTrace();
//			System.exit(1);
//		}
		OutgoingSMSDto[] observedResponses = simulationMessageReceiver.getLastOutgoingSMSes();
		String[][] response = new String[observedResponses.length][3];
		for (int i=0; i<response.length; i++) {
			response[i][0] = observedResponses[i].getBillingType().toString();
			response[i][1] = observedResponses[i].getPhone();
			response[i][2] = observedResponses[i].getText().replaceAll("\n", "<br/>");
		}
		return response;
	}

    public static int getAppVersionsLength() {
//    	return AppVersionInfo.APP_VERSIONS.length;
    	return -1;
    }
    
    public static String getAppVersionModuleName(int id) {
//    	return AppVersionInfo.APP_VERSIONS[id][0];
    	return "---";
    }
    
    private static String[][] cycleNameMapping = {
    	{"DEV",     "Desenvolvimento"},	
    	{"HOMOLOG", "Homologação"},	
    	{"PROD",    "Produção"},
    	{"NCMT",    "Não Comitada"},
    };
    public static String getAppVersionCycle(int id) {
//    	String cycle = AppVersionInfo.APP_VERSIONS[id][1].replaceAll("\\-.*", "");
//    	for (int i=0; i<cycleNameMapping.length; i++) {
//    		cycle = cycle.replace(cycleNameMapping[i][0], cycleNameMapping[i][1]);
//    	}
//    	return cycle;
    	return "---";
    }
    
    public static String getAppVersionTag(int id) {
//    	return AppVersionInfo.APP_VERSIONS[id][1];
    	return "---";
    }

}
