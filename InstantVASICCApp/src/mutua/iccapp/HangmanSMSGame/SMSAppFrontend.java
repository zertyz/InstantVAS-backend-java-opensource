package mutua.iccapp.HangmanSMSGame;

import java.sql.SQLException;

import config.InstantVASInstanceConfiguration;
import adapters.dto.PreparedProcedureInvocationDto;
import mutua.events.postgresql.QueuesPostgreSQLAdapter;
import mutua.hangmansmsgame.smslogic.SMSProcessor;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.eventclients.InstrumentationProfilingEventsClient;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterChat;
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

public class SMSAppFrontend {

	
	private static Instrumentation<ICCAppInstrumentationRequestProperty, String> log;
	
	private SMSProcessor                   smsP;
	private SimulationMessageReceiver      responseReceiver;
	private static TestableSubscriptionAPI subscriptionEngine;
	private static String                  subscriptionChannel = "InstantVASSimulator";
	
	// MO and MT databases (clones of the web application queue structures)
	private QueuesPostgreSQLAdapter moDB;
	private QueuesPostgreSQLAdapter mtDB;
	

    
    static {
    	log = new Instrumentation<ICCAppInstrumentationRequestProperty, String>("HangmanSMSGameICCApp", new ICCAppInstrumentationRequestProperty("phone"),
    			EInstrumentationDataPours.CONSOLE, null);
    	try {
        	InstrumentationProfilingEventsClient instrumentationProfilingEventsClient = new InstrumentationProfilingEventsClient(log, EInstrumentationDataPours.CONSOLE, null);
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}
    }

    
    public SMSAppFrontend() {
    	subscriptionEngine  = new TestableSubscriptionAPI(log);
    	
    	// attempt to use the same MO/MT registration as the main web application
    	try {
    		// TODO fix this hard coded info
    		// MutuaEventsAdditionalEventLinks configuration
    		QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(log, "venus", 5432, "hangman", "hangman", "hangman");
    		moDB = QueuesPostgreSQLAdapter.getQueuesDBAdapter(null, "MOQueue",
			                                                  "phone  TEXT NOT NULL, text   TEXT NOT NULL, ",
			                                                  "phone, text",
			                                                  "${PHONE}, ${TEXT}");
			mtDB = QueuesPostgreSQLAdapter.getQueuesDBAdapter(null, "MTQueue",
			                                                  "moId  INTEGER NOT NULL, phone  TEXT NOT NULL, text   TEXT NOT NULL, ",
			                                                  "moId, phone, text",
			                                                  "${MO_ID}, ${PHONE}, ${TEXT}");
	    	SMSAppModulePostgreSQLAdapterChat.configureChatDatabaseModule(log, "venus", 5432, "hangman", "hangman", "hangman",
	    	                                                              "MOQueue", "eventId", "text");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	InstantVASInstanceConfiguration.setDefaults(log, subscriptionEngine, subscriptionChannel);
    	responseReceiver = new SimulationMessageReceiver();
    	smsP = new SMSProcessor(log, responseReceiver, InstantVASInstanceConfiguration.navigationStates);
	}

    private static int nextMOId = 1;
    public String[][] process(String phone, String inputText, String carrier) {
    	log.reportRequestStart(phone);
    	int moId = nextMOId++;
        IncomingSMSDto mo = new IncomingSMSDto(moId, phone, inputText, ESMSInParserCarrier.valueOf(carrier), "1234");
        
        try {
            // insert into the MO database
            PreparedProcedureInvocationDto invocation = new PreparedProcedureInvocationDto("InsertNewQueueElement");
            invocation.addParameter("PHONE", phone);
            invocation.addParameter("TEXT", inputText);
            moDB.invokeScalarProcedure(invocation);
            
			smsP.process(mo);
		} catch (Throwable t) {
			log.reportThrowable(t, "Error processing simulated message");
			t.printStackTrace();
		}
		OutgoingSMSDto[] observedResponses = responseReceiver.getLastOutgoingSMSes();
		String[][] response = new String[observedResponses.length][3];
		for (int i=0; i<response.length; i++) {
			response[i][0] = observedResponses[i].getBillingType().toString();
			response[i][1] = observedResponses[i].getPhone();
			response[i][2] = observedResponses[i].getText().replaceAll("\n", "<br/>");
			try {
				// insert into the MT database
				PreparedProcedureInvocationDto invocation = new PreparedProcedureInvocationDto("InsertNewQueueElement");
	            invocation.addParameter("MO_ID", moId);
	            invocation.addParameter("PHONE", observedResponses[i].getPhone());
	            invocation.addParameter("TEXT", observedResponses[i].getText());
	            mtDB.invokeScalarProcedure(invocation);
			} catch (Throwable t) {
				log.reportThrowable(t, "Error processing simulated message");
				t.printStackTrace();
			}
		}
		log.reportRequestFinish();
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