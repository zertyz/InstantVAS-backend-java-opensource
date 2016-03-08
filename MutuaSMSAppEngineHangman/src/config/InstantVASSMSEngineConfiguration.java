package config;

import instantvas.smsengine.HangmanHTTPInstrumentationRequestProperty;
import instantvas.smsengine.HangmanSMSGameServicesInstrumentationEvents;
import instantvas.smsengine.MOSMSesQueueDataBureau;
import instantvas.smsengine.MTSMSesQueueDataBureau;
import instantvas.tests.InstantVASSMSAppModuleChatTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleHangmanTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleSubscriptionTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleTestsConfiguration;
import main.InstantVASTester;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import adapters.PostgreSQLAdapter;
import config.InstantVASSMSEngineConfiguration.EInstantVASModules;
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
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryHangman;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dal.SMSAppModuleDALFactorySubscription;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterChat;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterHangman;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterProfile;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterSubscription;
import mutua.smsappmodule.hangmangame.HangmanGame.EHangmanGameStates;
import mutua.smsin.parsers.SMSInCelltick;
import mutua.smsin.parsers.SMSInParser;
import mutua.subscriptionengine.CelltickLiveScreenSubscriptionAPI;
import mutua.tests.MutuaEventsAdditionalEventLinksTestsConfiguration;

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
		
	@ConfigurableElement("Specifies what queue driver should be used to buffer incoming SMSes (MOs) -- DIRECT means the messages will be processed directly, on the same request thread and without any buffer; RAM means the producers and consumers must be running on the same machine and on the same process; POSTGRESQL means a table will be used to keep those messages and serve as the queue at the same time")
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
	
	public enum EInstantVASModules {
		BASE,
		SUBSCRIPTION,
		SUBSCRIPTION_LIFECYCLE,
		DRAW,
		PROFILE,
		QUIZ,
		ALERTS,
		NOTIFICATIONS,
		DECISION_TREE,
		CELEBRITY_AI,
		REVERSE_AUCTION,
		SWEEPSTAKE,
		OFFER_VS_DEMAND,
		PROXIMITY_SEARCH,
		HANGMAN,
		TIC_TAC_TOE,
		XAVECO,
		TEXT4INFO,
		MASS_TEXT_MESSAGING,
		CHAT,
		DATING,
		MATCH_MAKING,
		SMS_TWITTER,
		SMS_QUORA,
		MPAYMENT,
		PIN_CODE,
		ZETA,
		SMS_ROUTER,
		
	};
	public enum EInstantVASDALs {
		RAM,
		POSTGRESQL,
	}
	
	private static void configureInstantVASModules(
		EInstantVASDALs      queuesDAL,
		EInstantVASDALs      modulesDAL,
		String               hostname,
		int                  port,
		String               database,
		String               user,
		String               password,
		boolean              allowDataStructuresAssertion,
		boolean              shouldDebugQueries,
		String               connectionProperties,
		int                  concurrentConnectionsNumber,
		int                  queuePoolingTime,
		int                  queueNumberOfWorkerThreads,
		String               moTableName, String moIdFieldName, String moTextFieldName,
		EInstantVASModules[] enabledModules) {
		
		List<EInstantVASModules> enabledModulesList = Arrays.asList(enabledModules);
		
		// DALs
		SMSAppModuleDALFactory             baseModuleDAL;
		SMSAppModuleDALFactorySubscription subscriptionDAL;
		SMSAppModuleDALFactoryProfile      profileModuleDAL;
		SMSAppModuleDALFactoryChat         chatModuleDAL;
		SMSAppModuleDALFactoryHangman      hangmanModuleDAL;
		
		// configure modules dal
		switch (modulesDAL) {
		case POSTGRESQL:
			System.out.println("\n### Configuring PostgreSQLAdapter...");
			PostgreSQLAdapter.configureDefaultValuesForNewInstances(connectionProperties, concurrentConnectionsNumber);
			boolean first = true;
			for (EInstantVASModules module : EInstantVASModules.values()) {
				if (!enabledModulesList.contains(module)) {
					continue;
				}
				if (!first) {
					System.out.print(", ");
				} else {
					first = false;
				}
				System.out.print(module.name().toLowerCase());
				switch (module) {
					case BASE:
						baseModuleDAL                            = SMSAppModuleDALFactory            .POSTGRESQL;
						SMSAppModulePostgreSQLAdapter            .configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
						break;
					case SUBSCRIPTION:
						subscriptionDAL                          = SMSAppModuleDALFactorySubscription.POSTGRESQL;
						SMSAppModulePostgreSQLAdapterSubscription.configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
						break;
					case PROFILE:
						profileModuleDAL                         = SMSAppModuleDALFactoryProfile     .POSTGRESQL;
						SMSAppModulePostgreSQLAdapterProfile     .configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
						break;
					case CHAT:
						chatModuleDAL                            = SMSAppModuleDALFactoryChat        .POSTGRESQL;
						SMSAppModulePostgreSQLAdapterChat        .configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password, moTableName, moIdFieldName, moTextFieldName);
						break;
					case HANGMAN:
						hangmanModuleDAL                         = SMSAppModuleDALFactoryHangman     .POSTGRESQL;
						SMSAppModulePostgreSQLAdapterHangman     .configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
						break;
					default:
						throw new RuntimeException("InstantVAS Module '"+module+"' isn't present");
				}
			}
			System.out.println(".");
			break;
		case RAM:
			baseModuleDAL    = SMSAppModuleDALFactory            .RAM;
			subscriptionDAL  = SMSAppModuleDALFactorySubscription.RAM;
			profileModuleDAL = SMSAppModuleDALFactoryProfile     .RAM;
			chatModuleDAL    = SMSAppModuleDALFactoryChat        .RAM;
			hangmanModuleDAL = SMSAppModuleDALFactoryHangman     .RAM;
			break;
		default:
			throw new RuntimeException("InstantVAS Modules DAL '"+modulesDAL+"' is not implemented");
		}
		
		// configure queues dal
		switch (queuesDAL) {
			case POSTGRESQL:
				System.out.println("\n### Configuring PostgreSQLQueues...");
				QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
				PostgreSQLQueueEventLink.configureDefaultValuesForNewInstances(log, queuePoolingTime, queueNumberOfWorkerThreads);
				break;
			case RAM:
				break;
			default:
				throw new RuntimeException("InstantVAS Queue DAL '"+queuesDAL+"' is not implemented");
		}
	}


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
					gameMOProducerAndConsumerLink = new PostgreSQLQueueEventLink<EHangmanGameStates>(EHangmanGameStates.class, annotationClasses, MOSMSesQueueDataBureau.MO_TABLE_NAME, new MOSMSesQueueDataBureau());
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
					gameMTProducerAndConsumerLink = new PostgreSQLQueueEventLink<EHangmanGameStates>(EHangmanGameStates.class, annotationClasses, "MTSMSes", new MTSMSesQueueDataBureau());
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
