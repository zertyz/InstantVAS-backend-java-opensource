package config;

import java.sql.SQLException;
import java.util.Map;

import mutua.events.DirectEventLink;
import mutua.events.IEventLink;
import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.QueueEventLink;
import mutua.events.postgresql.QueuesPostgreSQLAdapter;
import mutua.hangmansmsgame.HangmanHTTPInstrumentationRequestProperty;
import mutua.hangmansmsgame.HangmanSMSGameServicesInstrumentationEvents;
import mutua.hangmansmsgame.MOSMSesQueueDataBureau;
import mutua.hangmansmsgame.MTSMSesQueueDataBureau;
import mutua.hangmansmsgame.config.Configuration;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor.EHangmanSMSGameEvents;
import mutua.icc.configuration.ConfigurationManager;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.eventclients.InstrumentationProfilingEventsClient;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;
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

public class WebAppConfiguration {


	// LOGGING
	//////////

	public static Instrumentation<DefaultInstrumentationProperties, String> configurationLog;
	public static Instrumentation<HangmanHTTPInstrumentationRequestProperty, String> log;
	
	
	// MO
	/////
	
	public static SMSInParser<Map<String, String>, byte[]>  smsParser = new SMSInCelltick(Configuration.APPID);

	
	// MO QUEUE (but also SubscribeUser & UnsubscribeUser queues)
	/////////////////////////////////////////////////////////////
	
	public enum EQueueStrategy {DIRECT, RAM, LOG_FILE, POSTGRESQL};
	
	public static IEventLink<EHangmanSMSGameEvents>  gameMOProducerAndConsumerLink;
		
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

	
	public static IEventLink<EHangmanSMSGameEvents>  gameMTProducerAndConsumerLink;

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
    		ConfigurationManager cm = new ConfigurationManager(configurationLog, WebAppConfiguration.class, Configuration.class);
//    		cm.saveToFile(configFileName);		// to activate file write, please go to 'AddToMOQueue#doGet', since the class loading for 'CommandDetails' will fail at this point, due to 'log' being null at this point -- and this will leave some comments blank
    		cm.loadFromFile(configFileName);
    		configurationLog.reportRequestFinish();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		
    	// LOGS
    	///////
    	
    	try {
        	log = new Instrumentation<HangmanHTTPInstrumentationRequestProperty, String>(Configuration.APPID + " WEB", new HangmanHTTPInstrumentationRequestProperty(),
        			Configuration.LOG_STRATEGY, Configuration.LOG_WEBAPP_FILE_PATH, HangmanSMSGameServicesInstrumentationEvents.values());
        	InstrumentationProfilingEventsClient instrumentationProfilingEventsClient = new InstrumentationProfilingEventsClient(log,
        			Configuration.LOG_STRATEGY, Configuration.LOG_WEBAPP_FILE_PATH);
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
	    	Configuration.SUBSCRIPTION_ENGINE = new CelltickLiveScreenSubscriptionAPI(log, Configuration.SUBSCRIBE_SERVICE_URL, Configuration.UNSUBSCRIBE_SERVICE_URL);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}

    	
    	applyConfiguration();
    	try {
			Configuration.applyConfiguration();
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
				gameMOProducerAndConsumerLink = new DirectEventLink<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.class);
				break;
    		case POSTGRESQL:
				try {
					log.reportDebug("Creating a 'PostgreSQLQueueEventLink' MO event handling mechanism");
					gameMOProducerAndConsumerLink = new PostgreSQLQueueEventLink<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.class, annotationClasses, "MOSMSes", new MOSMSesQueueDataBureau());
	    			break;
				} catch (SQLException e) {
					log.reportThrowable(e, "Error creating the 'PostgreSQLQueueEventLink' MO queue. Falling back to 'RAM' queue strategy");
				}
    		case RAM:
				log.reportDebug("Creating a 'QueueEventLink' MO event handling mechanism");
    			gameMOProducerAndConsumerLink = new QueueEventLink<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.class, MO_RAM_QUEUE_CAPACITY, MO_QUEUE_NUMBER_OF_WORKER_THREADS);
    			break;
    		default:
    			throw new RuntimeException("Don't know nothing about '"+MO_QUEUE_STRATEGY+"' MO queue strategy");
    	}

    	// MT
    	switch (MT_QUEUE_STRATEGY) {
			case DIRECT:
				log.reportDebug("Creating a 'DirectEventLink' MT event handling mechanism");
				gameMTProducerAndConsumerLink = new DirectEventLink<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.class);
				break;
    		case POSTGRESQL:
				try {
					log.reportDebug("Creating a 'PostgreSQLQueueEventLink' MT event handling mechanism");
					gameMTProducerAndConsumerLink = new PostgreSQLQueueEventLink<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.class, annotationClasses, "MTSMSes", new MTSMSesQueueDataBureau());
	    			break;
				} catch (SQLException e) {
					log.reportThrowable(e, "Error creating the 'PostgreSQLQueueEventLink' MT queue. Falling back to 'RAM' queue strategy");
				}
    		case RAM:
				log.reportDebug("Creating a 'QueueEventLink' MT event handling mechanism");
    			gameMTProducerAndConsumerLink = new QueueEventLink<EHangmanSMSGameEvents>(EHangmanSMSGameEvents.class, MT_RAM_QUEUE_CAPACITY, MT_QUEUE_NUMBER_OF_WORKER_THREADS);
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
			Configuration.POSTGRESQL_CONNECTION_HOSTNAME,
			Configuration.POSTGRESQL_CONNECTION_PORT,
			Configuration.POSTGRESQL_CONNECTION_DATABASE_NAME,
			Configuration.POSTGRESQL_CONNECTION_USER,
			Configuration.POSTGRESQL_CONNECTION_PASSWORD);
	}


	static {

		// by this time, before reading the configuration, we can only log to console
		configurationLog = new Instrumentation<DefaultInstrumentationProperties, String>(Configuration.APPID+"Configuration", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null);
		
		loadConfiguration();
	}

}
