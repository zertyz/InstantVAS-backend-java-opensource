package config;

import instantvas.smsengine.HangmanHTTPInstrumentationRequestProperty;
import instantvas.smsengine.HangmanSMSGameServicesInstrumentationEvents;
import instantvas.smsengine.MOSMSesQueueDataBureau;
import instantvas.smsengine.MTSMSesQueueDataBureau;

import java.sql.SQLException;
import java.util.Map;

import mutua.events.DirectEventLink;
import mutua.events.IEventLink;
import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.QueueEventLink;
import mutua.events.postgresql.QueuesPostgreSQLAdapter;
import mutua.icc.configuration.ConfigurationManager;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.eventclients.InstrumentationProfilingEventsClient;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.hangmangame.HangmanGame.EHangmanGameStates;
import mutua.smsin.parsers.SMSInCelltick;
import mutua.smsin.parsers.SMSInParser;
import mutua.subscriptionengine.CelltickLiveScreenSubscriptionAPI;

/** <pre>
 * WebAppConfiguration.java
 * ========================
 * (created by luiz, Jan 29, 2015)
 *
 * Defines common configuration variables for the application
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class InstantVASSMSEngineConfiguration {


	// LOGGING
	//////////

	public static Instrumentation<DefaultInstrumentationProperties, String> configurationLog;
	public static Instrumentation<HangmanHTTPInstrumentationRequestProperty, String> log;
	
	
	// MO
	/////
	
	public static SMSInParser<Map<String, String>, byte[]>  smsParser = new SMSInCelltick(InstantVASSMSAppModuleConfiguration.APPName);

	
	// MO QUEUE (but also SubscribeUser & UnsubscribeUser queues)
	/////////////////////////////////////////////////////////////
	
	public enum EQueueStrategy {DIRECT, RAM, LOG_FILE, POSTGRESQL};
	
	public static IEventLink<EHangmanGameStates>  gameMOProducerAndConsumerLink;
		
	@ConfigurableElement("Specifies what queue driver should be used to buffer incoming SMS (MOs) -- DIRECT means the messages will be processed directly, on the same request thread and without any buffer; RAM means the producers and consumers must be running on the same machine and on the same process")
	public static EQueueStrategy MO_QUEUE_STRATEGY = EQueueStrategy.POSTGRESQL;
	@ConfigurableElement("The maximum number of entries when using 'RAM' for 'MO_QUEUE_STRATEGY'")
	public static int    MO_RAM_QUEUE_CAPACITY             = 1000;
	@ConfigurableElement("The directory were to store log files when using 'LOG_FILE' for 'MO_QUEUE_STRATEGY'")
	public static String MO_RAM_QUEUE_LOG_FILES_DIRECTORY  = "";
	@ConfigurableElement("The amount of milliseconds the consumer manager should wait between queries for new queue entries to process. Set to 0 to rely on the internal notification mechanisms and only when queue producers and consumers are running on the same machine and on the same process.")
	public static long   MO_FILE_QUEUE_POOLING_TIME  = 0;
	@ConfigurableElement(sameAs="mutua.events.PostgreSQLQueueEventLink.QUEUE_POOLING_TIME")
	public static long   MO_POSTGRESQL_QUEUE_POOLING_TIME  = 0;
	@ConfigurableElement(sameAs="mutua.events.PostgreSQLQueueEventLink.QUEUE_NUMBER_OF_WORKER_THREADS")
	public static int    MO_QUEUE_NUMBER_OF_WORKER_THREADS = 10;

	
	public static IEventLink<EHangmanGameStates>  gameMTProducerAndConsumerLink;

	@ConfigurableElement("Specifies what queue driver should be used to buffer outgoing SMS (MTs) -- DIRECT means the messages will be processed directly, on the same request thread and without any buffer; RAM means the producers and consumers must be running on the same machine and on the same process")
	public static EQueueStrategy MT_QUEUE_STRATEGY = EQueueStrategy.DIRECT;
	@ConfigurableElement("The maximum number of entries when using 'RAM' for 'MT_QUEUE_STRATEGY'")
	public static int    MT_RAM_QUEUE_CAPACITY             = 1000;
	@ConfigurableElement("The directory were to store log files when using 'LOG_FILE' for 'MT_QUEUE_STRATEGY'")
	public static String MT_RAM_QUEUE_LOG_FILES_DIRECTORY  = "";
	@ConfigurableElement("The amount of milliseconds the consumer manager should wait between queries for new queue entries to process. Set to 0 to rely on the internal notification mechanisms and only when queue producers and consumers are running on the same machine and on the same process.")
	public static long   MT_FILE_QUEUE_POOLING_TIME  = 0;
	@ConfigurableElement(sameAs="mutua.events.PostgreSQLQueueEventLink.QUEUE_POOLING_TIME")
	public static long   MT_POSTGRESQL_QUEUE_POOLING_TIME  = 0;
	@ConfigurableElement(sameAs="mutua.events.PostgreSQLQueueEventLink.QUEUE_NUMBER_OF_WORKER_THREADS")
	public static int    MT_QUEUE_NUMBER_OF_WORKER_THREADS = 10;


	public static void loadConfiguration() {
		
		try {
//    		// Obtain our environment naming context
//    		Context initCtx = new InitialContext();
//    		Context envCtx = (Context) initCtx.lookup("java:comp/env");
//
//    		// Look up our data source
//    		String configFileName = (String)envCtx.lookup("hangmanSMSGameConfigFileName");
    		String configFileName = "/Celltick/app/etc/hangman.config";
    		configurationLog.reportRequestStart("Attempting to read configuration file from '"+configFileName+"'");
    		ConfigurationManager cm = new ConfigurationManager(configurationLog, InstantVASSMSEngineConfiguration.class, HangmanSMSModulesConfiguration.class);
//    		cm.saveToFile(configFileName);		// to activate file write, please go to 'AddToMOQueue#doGet', since the class loading for 'CommandDetails' will fail at this point, due to 'log' being null at this point -- and this will leave some comments blank
    		cm.loadFromFile(configFileName);
    		configurationLog.reportRequestFinish();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		
    	// LOGS
    	///////
    	
    	try {
        	log = new Instrumentation<HangmanHTTPInstrumentationRequestProperty, String>(InstantVASSMSAppModuleConfiguration.APPName + " WEB", new HangmanHTTPInstrumentationRequestProperty(),
        			HangmanSMSModulesConfiguration.LOG_STRATEGY, HangmanSMSModulesConfiguration.LOG_WEBAPP_FILE_PATH, HangmanSMSGameServicesInstrumentationEvents.values());
        	InstrumentationProfilingEventsClient instrumentationProfilingEventsClient = new InstrumentationProfilingEventsClient(log,
        			HangmanSMSModulesConfiguration.LOG_STRATEGY, HangmanSMSModulesConfiguration.LOG_WEBAPP_FILE_PATH);
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
			HangmanSMSModulesConfiguration.SUBSCRIPTION_ENGINE = new CelltickLiveScreenSubscriptionAPI(log, HangmanSMSModulesConfiguration.SUBSCRIBE_SERVICE_URL, HangmanSMSModulesConfiguration.UNSUBSCRIBE_SERVICE_URL);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}

    	
    	applyConfiguration();
    	try {
    		//HangmanSMSModulesConfiguration.applyConfiguration();
		} catch (Throwable t) {
			t.printStackTrace();
			configurationLog.reportThrowable(t, "Error applying Configuration");
		}


    	// QUEUES
    	/////////

    	// MO
    	switch (MO_QUEUE_STRATEGY) {
			case DIRECT:
				log.reportDebug("Creating a 'DirectEventLink' MO event handling mechanism");
				gameMOProducerAndConsumerLink = new DirectEventLink<EHangmanGameStates>(EHangmanGameStates.class);
				break;
    		case POSTGRESQL:
				try {
					log.reportDebug("Creating a 'PostgreSQLQueueEventLink' MO event handling mechanism");
					gameMOProducerAndConsumerLink = new PostgreSQLQueueEventLink<EHangmanGameStates>(EHangmanGameStates.class, MOSMSesQueueDataBureau.MO_TABLE_NAME, new MOSMSesQueueDataBureau());
	    			break;
				} catch (SQLException e) {
					log.reportThrowable(e, "Error creating the 'PostgreSQLQueueEventLink' MO queue. Falling back to 'RAM' queue strategy");
				}
    		case RAM:
				log.reportDebug("Creating a 'QueueEventLink' MO event handling mechanism");
    			gameMOProducerAndConsumerLink = new QueueEventLink<EHangmanGameStates>(EHangmanGameStates.class, MO_RAM_QUEUE_CAPACITY, MO_QUEUE_NUMBER_OF_WORKER_THREADS);
    			break;
    		default:
    			throw new RuntimeException("Don't know nothing about '"+MO_QUEUE_STRATEGY+"' MO queue strategy");
    	}

    	// MT
    	switch (MT_QUEUE_STRATEGY) {
			case DIRECT:
				log.reportDebug("Creating a 'DirectEventLink' MT event handling mechanism");
				gameMTProducerAndConsumerLink = new DirectEventLink<EHangmanGameStates>(EHangmanGameStates.class);
				break;
    		case POSTGRESQL:
				try {
					log.reportDebug("Creating a 'PostgreSQLQueueEventLink' MT event handling mechanism");
					gameMTProducerAndConsumerLink = new PostgreSQLQueueEventLink<EHangmanGameStates>(EHangmanGameStates.class, "MTSMSes", new MTSMSesQueueDataBureau());
	    			break;
				} catch (SQLException e) {
					log.reportThrowable(e, "Error creating the 'PostgreSQLQueueEventLink' MT queue. Falling back to 'RAM' queue strategy");
				}
    		case RAM:
				log.reportDebug("Creating a 'QueueEventLink' MT event handling mechanism");
    			gameMTProducerAndConsumerLink = new QueueEventLink<EHangmanGameStates>(EHangmanGameStates.class, MT_RAM_QUEUE_CAPACITY, MT_QUEUE_NUMBER_OF_WORKER_THREADS);
    			break;
    		default:
    			throw new RuntimeException("Don't know nothing about '"+MT_QUEUE_STRATEGY+"' MT queue strategy");
    	}

    }
	
	private static void applyConfiguration() {
		
		// PostgreSQL queues
		////////////////////
		
		PostgreSQLQueueEventLink.QUEUE_POOLING_TIME             = MO_POSTGRESQL_QUEUE_POOLING_TIME;
		PostgreSQLQueueEventLink.QUEUE_NUMBER_OF_WORKER_THREADS = MO_QUEUE_NUMBER_OF_WORKER_THREADS;
		QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(log,
			HangmanSMSModulesConfiguration.POSTGRESQL_CONNECTION_HOSTNAME,
			HangmanSMSModulesConfiguration.POSTGRESQL_CONNECTION_PORT,
			HangmanSMSModulesConfiguration.POSTGRESQL_CONNECTION_DATABASE_NAME,
			HangmanSMSModulesConfiguration.POSTGRESQL_CONNECTION_USER,
			HangmanSMSModulesConfiguration.POSTGRESQL_CONNECTION_PASSWORD);
	}


	static {

		// by this time, before reading the configuration, we can only log to console
		configurationLog = new Instrumentation<DefaultInstrumentationProperties, String>(InstantVASSMSAppModuleConfiguration.APPName+"Configuration", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null);
		
		loadConfiguration();
	}

}
