package config;

import static mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationHelp.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationSubscription.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationProfile.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationChat.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationHangman.*;
import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription.*;

import java.util.Arrays;
import java.util.List;

import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman.*;
import instantvas.smsengine.HangmanHTTPInstrumentationRequestProperty;
import instantvas.smsengine.MOSMSesQueueDataBureau;
import mutua.events.IEventLink;
import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.postgresql.QueuesPostgreSQLAdapter;
import mutua.hangmansmsgame.dal.DALFactory.EDataAccessLayers;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationChat;
import mutua.smsappmodule.config.SMSAppModuleConfigurationHangman;
import mutua.smsappmodule.config.SMSAppModuleConfigurationHelp;
import mutua.smsappmodule.config.SMSAppModuleConfigurationProfile;
import mutua.smsappmodule.config.SMSAppModuleConfigurationSubscription;
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
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsChat;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription;
import mutua.smsappmodule.smslogic.SMSAppModuleEventsSubscription;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription;
import mutua.subscriptionengine.SubscriptionEngine;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;
import config.InstantVASSMSEngineConfiguration.EInstantVASDALs;
import config.InstantVASSMSEngineConfiguration.EInstantVASModules;
import config.InstantVASSMSEngineConfiguration.EQueueStrategy;

/** <pre>
 * HangmanSMSModulesConfiguration.java
 * ===================================
 * (created by luiz, Sep 15, 2015)
 *
 * This class sets all 'MutuaSMSAppModule*' projects to behave as an unified Hangman SMS Game
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HangmanSMSModulesConfiguration {
	
	public static Instrumentation<HangmanHTTPInstrumentationRequestProperty, String> log;
	
	/** to be defined by the request receivers (servlet, console, tests, icc app, ...) */
	public static SubscriptionEngine SUBSCRIPTION_ENGINE;
	
	public static EInstrumentationDataPours LOG_STRATEGY                   = EInstrumentationDataPours.CONSOLE;
	public static String LOG_HANGMAN_FILE_PATH                             = "";
	public static String LOG_WEBAPP_FILE_PATH                              = "";
	public static String APPID                                             = "HANGMAN";
	public static String SHORT_CODE                                        = "9714";
	
	// MO QUEUE (but also SubscribeUser & UnsubscribeUser queues)
	/////////////////////////////////////////////////////////////
	
	public enum EProcessingStrategy {DIRECT, RAM, LOG_FILE, POSTGRESQL};
	public enum EInstantVASDALs     {RAM, POSTGRESQL}
	
	
	public static IEventLink<EHangmanGameStates>  gameMOProducerAndConsumerLink;
	
	// HTTPClientAdapter
	////////////////////
	
	@ConfigurableElement("General HTTP/HTTPD client behavior, in milliseconds")
	public static int HTTP_CONNECTION_TIMEOUT_MILLIS = 30000;
	public static int HTTP_READ_TIMEOUT_MILLIS       = 30000;
	
	// Integration with 'SMSOutCelltick' and 'SubscriptionEngineCelltick'
	/////////////////////////////////////////////////////////////////////

	@ConfigurableElement("Subscription service URLs & data for 'CelltickLiveScreenSubscriptionAPI'")
	public static String CELLTICK_SUBSCRIBE_SERVICE_URL               = "http://localhost:8082/celltick/wapAPI?action=subpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
	public static String CELLTICK_UNSUBSCRIBE_SERVICE_URL             = "http://localhost:8082/celltick/wapAPI?action=unsubpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
	public static String CELLTICK_SUBSCRIPTION_CHANNEL_NAME           = "HangMan";
	@ConfigurableElement("MT service URLs & data for Celltick's Kannel APIs")
	public static String CELLTICK_MT_SERVICE_URL                      = "http://localhost:15001/cgi-bin/sendsms";
	@ConfigurableElement("the number of times 'sendMessage' will attempt to send the message before reporting it as unsendable")
	public static int    CELLTICK_MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS = 5;
	@ConfigurableElement("the number of milliseconds 'sendMessage' will wait between retry attempts")
	public static long   CELLTICK_MT_SERVICE_DELAY_BETWEEN_ATTEMPTS   = 5000;
	
	// JDBCAdapter
	//////////////
	
	@ConfigurableElement("The desired data access handler for all hangman databases")
	public static EDataAccessLayers  DATA_ACCESS_LAYER             = EInstantVASDALs.POSTGRESQL;
	@ConfigurableElement("Hostname (or IP) of the PostgreSQL server. For localhost, try '::1' first")
	public static String  POSTGRESQL_CONNECTION_HOSTNAME           = "venus";
	@ConfigurableElement("Connection port for the PostgreSQL server")
	public static int     POSTGRESQL_CONNECTION_PORT               = 5432;
	@ConfigurableElement("The PostgreSQL database with the application's data scope")
	public static String  POSTGRESQL_CONNECTION_DATABASE_NAME      = "hangman";
	@ConfigurableElement("The PostgreSQL user name to access 'DATABASE' -- note: administrative rights, such as the creation of tables, are necessary for the model auto-generation feature")
	public static String  POSTGRESQL_CONNECTION_USER               = "hangman";
	@ConfigurableElement("The PostgreSQL plain text password for the above user")
	public static String  POSTGRESQL_CONNECTION_PASSWORD           = "hangman";
	@ConfigurableElement("Additional URL parameters for PostgreSQL JDBC driver connection properties")
	public static String  POSTGRESQL_CONNECTION_PROPERTIES         = PostgreSQLAdapter.CONNECTION_PROPERTIES;
	@ConfigurableElement("The number of concurrent connections allowed to each database server. Suggestion: fine tune to get the optimum number for this particular app/database, paying attention to the fact that a pool smaller than the sum of all consumer threads may be suboptimal, and that a greater than it can be a waste. As an initial value, set this to 2 * nDbCPUs * nDbHDs and adjust the 'number of consumer threads for the MO queue' accordingly")
	public static int     NUMBER_OF_CONCURRENT_CONNECTIONS        = PostgreSQLAdapter.CONNECTION_POOL_SIZE;
	@ConfigurableElement("Indicates whether or not to perform any needed administrative tasks, such as table & stored procedure creation")
	public static boolean POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION = true;
	@ConfigurableElement("Set to true to have all database queries logged")
	public static boolean POSTGRESQL_SHOULD_DEBUG_QUERIES          = true;
		
	@ConfigurableElement("Specifies which queue driver should be used to buffer incoming SMSes (MOs) -- DIRECT means the messages will be processed directly, on the same request thread and without any buffer; RAM means the producers and consumers must be running on the same machine and on the same process; POSTGRESQL means a table will be used to keep those messages and serve as the queue at the same time")
	public static EProcessingStrategy MO_PROCESSING_STRATEGY = EProcessingStrategy.POSTGRESQL;
	@ConfigurableElement("The maximum number of entries when using 'RAM' for 'MO_PROCESSING_STRATEGY'")
	public static int    MO_RAM_QUEUE_CAPACITY             = 1000;
	@ConfigurableElement("The directory were to store log files when using 'LOG_FILE' for 'MO_PROCESSING_STRATEGY'")
	public static String MO_RAM_QUEUE_LOG_FILES_DIRECTORY  = "";
	@ConfigurableElement("The maximum milliseconds the 'LOG_FILE' consumer manager should wait between queries for new queue entries to process. Set to 0 to rely on the internal notification mechanisms and only when queue producers and consumers are running on the same machine and on the same process.")
	public static long   MO_FILE_QUEUE_POOLING_TIME  = 0;
	@ConfigurableElement("Same as above, but applyed when using the 'POSTGRESQL' queue driver")
	public static long   MO_POSTGRESQL_QUEUE_POOLING_TIME  = 0;
	@ConfigurableElement("The number of consumer threads. Applyable to all queues (not applyable to the 'DIRECT' processing strategy). This number should not be greater than 'NUMBER_OF_CONCURRENT_CONNECTIONS'. The value here should be determined through experiments which aim to achieve the greater number of MOs processed per second")
	public static int    MO_QUEUE_NUMBER_OF_WORKER_THREADS = 10;

	@ConfigurableElement("The same as described on 'MO_PROCESSING_STRATEGY', but for MTs (outgoing SMSes)")
	public static EProcessingStrategy MT_PROCESSING_STRATEGY = EProcessingStrategy.POSTGRESQL;
	@ConfigurableElement("The maximum number pf entries when using 'RAM' for 'MT_PROCESSING_STRATEGY'")
	public static int    MT_RAM_QUEUE_CAPACITY             = 1000;
	@ConfigurableElement("The directory were to store log files when using 'LOG_FILE' for 'MT_PROCESSING_STRATEGY'")
	public static String MT_RAM_QUEUE_LOG_FILES_DIRECTORY  = "";
	@ConfigurableElement("The maximum milliseconds the 'LOG_FILE' consumer manager should wait between queries for new queue entries to process. Set to 0 to rely on the internal notification mechanisms and only when queue producers and consumers are running on the same machine and on the same process.")
	public static long   MT_FILE_QUEUE_POOLING_TIME  = 0;
	@ConfigurableElement("Same as above, but applyed when using the 'POSTGRESQL' queue driver")
	public static long   MT_POSTGRESQL_QUEUE_POOLING_TIME  = 0;
	@ConfigurableElement("The number of consumer threads. Applyable to all queues (not applyable to the 'DIRECT' processing strategy). This number may be greater than 'NUMBER_OF_CONCURRENT_CONNECTIONS' and should get bigger as the latency between the two involved servers increase, as the processing time on the MT server increase, as the number of MT retries increase and so on...")
	public static int    MT_QUEUE_NUMBER_OF_WORKER_THREADS = 2;

	
	public enum EInstantVASModules {CELLTICK_MO_RECEPTION, CELLTICK_MT_DELIVERY, CELLTICK_SUBSCRIPTION_LIFECYCLE,	// integration modules
	                                // infrastructure modules
	                                BASE, HELP, SUBSCRIPTION, SUBSCRIPTION_LIFECYCLE, DRAW, PROFILE, 	
	                                // entretainment modules
	                                QUIZ, CELEBRITY_AI, REVERSE_AUCTION,
	                                // entretainment / mobile learning modules
	                                DECISION_TREE,
	                                // game modules
	                                HANGMAN, TIC_TAC_TOE, XAVECO,
	                                // bet modules
	                                SWEEPSTAKE,	OFFER_VS_DEMAND,
	                                // mobile marketing modules
	                                ALERTS, NOTIFICATIONS, PROXIMITY_SEARCH, TEXT4INFO, PIN_CODE, MASS_TEXT_MESSAGING,
	                                // social network modules
	                                CHAT, DATING, MATCH_MAKING, SMS_TWITTER, SMS_QUORA,
	                                // mobile banking modules
	                                MPAYMENT,
	                                // mobile aggregator modules
	                                ZETA, SMS_ROUTER,
		
	};

	private static void configureInstantVASModules(
		// app
		String shortCode, String appName, String priceTag,
		// modules
		EInstantVASDALs      modulesDAL,
		EInstantVASModules[] enabledModules,
		// queues
		EInstantVASDALs      queuesDAL,
		int                  queuePoolingTime,
		int                  queueNumberOfWorkerThreads,
		// database
		String               hostname,
		int                  port,
		String               database,
		String               user,
		String               password,
		boolean              allowDataStructuresAssertion,
		boolean              shouldDebugQueries,
		String               connectionProperties,
		int                  concurrentConnectionsNumber,
		// help module
		String phrNewUsersFallbackHelp,	String phrExistingUsersFallbackHelp, String phrStatelessHelp,
		String[][] phrStatefulHelpMessages, String[] phrCompositeHelps,
		Object[][] nstPresentingCompositeHelpCommandTriggers,
		// subscription module
		String phrDoubleOptinStart, String phrDisagreeToSubscribe, String phrSuccessfullySubscribed, String phrCouldNotSubscribe,
		String phrUserRequestedUnsubscription, String phrLifecycleUnsubscription,
		SubscriptionEngine subscriptionEngine, String subscriptionToken,
        Object[][] nstAnsweringDoubleOptinTriggers,
        // profile module
        String phrAskForFirstNickname, String phrAskForNewNickname, String phrAskForNicknameCancelation, String phrNicknameRegistrationNotification,
        String phrUserProfilePresentation, String phrNicknameNotFound,
        Object[][] nstRegisteringNicknameTriggers,
		// chat module DAL
        String phrPrivateMessage, String phrPrivateMessageDeliveryNotification, String phrDoNotKnowWhoYouAreChattingTo,
        Object[][] nstChattingWithSomeoneTriggers,
		String moTableName, String moIdFieldName, String moTextFieldName,
		// hangman module DAL
        String winningArt, String losingArt, String headCharacter, String leftArmCharacter, String chestCharacter,
        String rightArmCharacter, String leftLegCharacter, String rightLegCharacter,
        String phr_gallowsArt, String phrAskOpponentNicknameOrPhone, String phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation,
        String phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation, String phrInvitationResponseForInvitingPlayer,
        String phrInvitationNotificationForInvitedPlayer, String phrTimeoutNotificationForInvitingPlayer, String phrInvitationRefusalResponseForInvitedPlayer,
        String phrInvitationRefusalNotificationForInvitingPlayer, String phrNotAGoodWord, String phrWordProvidingPlayerMatchStart, String phrWordGuessingPlayerMatchStart,
        String phrWordProvidingPlayerStatus, String phrWordGuessingPlayerStatus, String phrWinningMessageForWordGuessingPlayer, String phrWinningMessageForWordProvidingPlayer,
        String phrLosingMessageForWordGuessingPlayer, String phrLosingMessageForWordProvidingPlayer, String phrMatchGiveupNotificationForWordGuessingPlayer,
        String phrMatchGiveupNotificationForWordProvidingPlayer, String phrGuessingWordHelp,
        String defaultNicknamePrefix,
        Object[][] nstEnteringMatchWordTriggers, Object[][] nstAnsweringToHangmanMatchInvitationTriggers,
        Object[][] nstGuessingWordFromHangmanHumanOpponentTriggers, Object[][] nstGuessingWordFromHangmanBotOpponentTriggers) {
		
		List<EInstantVASModules> enabledModulesList = Arrays.asList(enabledModules);
		
		// DALs
		SMSAppModuleDALFactory             baseModuleDAL;
		SMSAppModuleDALFactorySubscription subscriptionDAL;
		SMSAppModuleDALFactoryProfile      profileModuleDAL;
		SMSAppModuleDALFactoryChat         chatModuleDAL;
		SMSAppModuleDALFactoryHangman      hangmanModuleDAL;
		
		// module instances
		SMSAppModuleNavigationStatesHelp         helpStates;
		SMSAppModuleCommandsHelp                 helpCommands;
		SMSAppModulePhrasingsHelp                helpPhrasings;
		SMSAppModuleNavigationStatesSubscription subscriptionStates;
		SMSAppModuleCommandsSubscription         subscriptionCommands;
		SMSAppModulePhrasingsSubscription        subscriptionPhrasings;
		SMSAppModuleEventsSubscription           subscriptionEventsServer;
		SMSAppModuleNavigationStatesProfile      profileStates; 
		SMSAppModuleCommandsProfile              profilecommands;
		SMSAppModulePhrasingsProfile             profilePhrasings;
		SMSAppModuleNavigationStatesChat         chatStates;
		SMSAppModuleCommandsChat                 chatCommands;
		SMSAppModulePhrasingsChat                chatPhrasings;
		SMSAppModuleNavigationStatesHangman      hangmanStates;
		SMSAppModuleCommandsHangman              hangmanCommands;
		SMSAppModulePhrasingsHangman             hangmanPhrasings;

		
		// configure modules dal
		switch (modulesDAL) {
		case POSTGRESQL:
			System.out.println("\n### Configuring PostgreSQLAdapter...");
			PostgreSQLAdapter.configureDefaultValuesForNewInstances(connectionProperties, concurrentConnectionsNumber);
			System.out.print("\n### Configuring modules DALs: ");
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
					baseModuleDAL = SMSAppModuleDALFactory.POSTGRESQL;
					SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
					break;
				case HELP:
					break;
				case SUBSCRIPTION:
					subscriptionDAL = SMSAppModuleDALFactorySubscription.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterSubscription.configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
					break;
				case PROFILE:
					profileModuleDAL = SMSAppModuleDALFactoryProfile.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterProfile.configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
					break;
				case CHAT:
					chatModuleDAL = SMSAppModuleDALFactoryChat.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterChat.configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password, moTableName, moIdFieldName, moTextFieldName);
					break;
				case HANGMAN:
					hangmanModuleDAL = SMSAppModuleDALFactoryHangman.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterHangman.configureDefaultValuesForNewInstances(log, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
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
		
		System.out.print("\n### Instantiating modules: ");
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
				break;
			case HELP:
				Object[] helpModuleInstances = SMSAppModuleConfigurationHelp.getHelpModuleInstances(
					log, shortCode, appName,
					phrNewUsersFallbackHelp, phrExistingUsersFallbackHelp, phrStatelessHelp, phrStatefulHelpMessages, phrCompositeHelps,
					nstPresentingCompositeHelpCommandTriggers);
				helpStates    = (SMSAppModuleNavigationStatesHelp) helpModuleInstances[0];
				helpCommands  = (SMSAppModuleCommandsHelp)         helpModuleInstances[1];
				helpPhrasings = (SMSAppModulePhrasingsHelp)        helpModuleInstances[2];
				break;
			case SUBSCRIPTION:
				Object[] subscriptionModuleInstances = SMSAppModuleConfigurationSubscription.getSubscriptionModuleInstances(log, shortCode, appName, priceTag,
					phrDoubleOptinStart, phrDisagreeToSubscribe, phrSuccessfullySubscribed, phrCouldNotSubscribe, phrUserRequestedUnsubscription, phrLifecycleUnsubscription,
					baseModuleDAL, subscriptionDAL, subscriptionEngine, subscriptionToken,
					nstAnsweringDoubleOptinTriggers);
				subscriptionStates       = (SMSAppModuleNavigationStatesSubscription) subscriptionModuleInstances[0];
				subscriptionCommands     = (SMSAppModuleCommandsSubscription)         subscriptionModuleInstances[1];
				subscriptionPhrasings    = (SMSAppModulePhrasingsSubscription)        subscriptionModuleInstances[2];
				subscriptionEventsServer = (SMSAppModuleEventsSubscription)           subscriptionModuleInstances[3];
				break;
			case PROFILE:
				Object[] profileModuleInstances = SMSAppModuleConfigurationProfile.getProfileModuleInstances(log, shortCode, appName,
					phrAskForFirstNickname, phrAskForNewNickname, phrAskForNicknameCancelation, phrNicknameRegistrationNotification,
					phrUserProfilePresentation, phrNicknameNotFound,
					profileModuleDAL,
					nstRegisteringNicknameTriggers);
				profileStates    = (SMSAppModuleNavigationStatesProfile) profileModuleInstances[0]; 
				profilecommands  = (SMSAppModuleCommandsProfile)         profileModuleInstances[1];
				profilePhrasings = (SMSAppModulePhrasingsProfile)        profileModuleInstances[2];
				break;
			case CHAT:
				Object[] chatModuleInstances = SMSAppModuleConfigurationChat.getChatModuleInstances(log, shortCode, appName,
					profilePhrasings,
					phrPrivateMessage, phrPrivateMessageDeliveryNotification, phrDoNotKnowWhoYouAreChattingTo,
					profileModuleDAL, chatModuleDAL,
					nstChattingWithSomeoneTriggers);
				chatStates    = (SMSAppModuleNavigationStatesChat) chatModuleInstances[0];
				chatCommands  = (SMSAppModuleCommandsChat)         chatModuleInstances[1];
				chatPhrasings = (SMSAppModulePhrasingsChat)        chatModuleInstances[2];
				break;
			case HANGMAN:
				Object[] hangmanModuleInstances = SMSAppModuleConfigurationHangman.getHangmanModuleInstances(log, shortCode, appName,
					winningArt, losingArt, headCharacter, leftArmCharacter, chestCharacter, rightArmCharacter, leftLegCharacter, rightLegCharacter, 
					phr_gallowsArt, phrAskOpponentNicknameOrPhone, phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation,
					phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation, phrInvitationResponseForInvitingPlayer,                      
					phrInvitationNotificationForInvitedPlayer, phrTimeoutNotificationForInvitingPlayer,                     
					phrInvitationRefusalResponseForInvitedPlayer, phrInvitationRefusalNotificationForInvitingPlayer,           
					phrNotAGoodWord, phrWordProvidingPlayerMatchStart, phrWordGuessingPlayerMatchStart, phrWordProvidingPlayerStatus,                                
					phrWordGuessingPlayerStatus, phrWinningMessageForWordGuessingPlayer, phrWinningMessageForWordProvidingPlayer,                     
					phrLosingMessageForWordGuessingPlayer, phrLosingMessageForWordProvidingPlayer, phrMatchGiveupNotificationForWordGuessingPlayer,             
					phrMatchGiveupNotificationForWordProvidingPlayer, phrGuessingWordHelp,
					subscriptionEventsServer, baseModuleDAL, profileModuleDAL, hangmanModuleDAL, defaultNicknamePrefix,
					nstEnteringMatchWordTriggers,nstAnsweringToHangmanMatchInvitationTriggers,
					nstGuessingWordFromHangmanHumanOpponentTriggers, nstGuessingWordFromHangmanBotOpponentTriggers);
				hangmanStates    = (SMSAppModuleNavigationStatesHangman) hangmanModuleInstances[0];
				hangmanCommands  = (SMSAppModuleCommandsHangman)         hangmanModuleInstances[1];
				hangmanPhrasings = (SMSAppModulePhrasingsHangman)        hangmanModuleInstances[2];
				break;
			default:
				throw new RuntimeException("InstantVAS Module '"+module+"' isn't present");
			}
		}
		System.out.println(".");
	}


	
	private static void setDefaultDBToPOSTGRESQL(Instrumentation<?, ?> log, String hostname, int port, String database, String user, String password) {

		SMSAppModuleDALFactory.            DEFAULT_DAL = SMSAppModuleDALFactory.            POSTGRESQL;
		SMSAppModuleDALFactorySubscription.DEFAULT_DAL = SMSAppModuleDALFactorySubscription.POSTGRESQL;
		SMSAppModuleDALFactoryProfile.     DEFAULT_DAL = SMSAppModuleDALFactoryProfile.     POSTGRESQL;
		SMSAppModuleDALFactoryChat.        DEFAULT_DAL = SMSAppModuleDALFactoryChat.        POSTGRESQL;
		SMSAppModuleDALFactoryHangman.     DEFAULT_DAL = SMSAppModuleDALFactoryHangman.     POSTGRESQL;

//		SMSAppModuleDALFactory.            DEFAULT_DAL = SMSAppModuleDALFactory.            RAM;
//		SMSAppModuleDALFactorySubscription.DEFAULT_DAL = SMSAppModuleDALFactorySubscription.RAM;
//		SMSAppModuleDALFactoryProfile.     DEFAULT_DAL = SMSAppModuleDALFactoryProfile.     RAM;
//		SMSAppModuleDALFactoryChat.        DEFAULT_DAL = SMSAppModuleDALFactoryChat.        RAM;
//		SMSAppModuleDALFactoryHangman.     DEFAULT_DAL = SMSAppModuleDALFactoryHangman.     RAM;
		
		     DEFAULT_MODULE_DAL  = SMSAppModuleDALFactory.            DEFAULT_DAL;
		SUBSCRIPTION_MODULE_DAL  = SMSAppModuleDALFactorySubscription.DEFAULT_DAL;
		     PROFILE_MODULE_DAL  = SMSAppModuleDALFactoryProfile.     DEFAULT_DAL;
		        CHAT_MODULE_DAL  = SMSAppModuleDALFactoryChat.        DEFAULT_DAL;
		     HANGMAN_MODULE_DAL  = SMSAppModuleDALFactoryHangman.     DEFAULT_DAL;


		// JDBC & PostgreSQL parameter configuration
		////////////////////////////////////////////
		     
		JDBCAdapter.SHOULD_DEBUG_QUERIES                = true;
		JDBCAdapter.CONNECTION_POOL_SIZE                = 8;
		PostgreSQLAdapter.ALLOW_DATABASE_ADMINISTRATION = true;

		// default module
		SMSAppModulePostgreSQLAdapter.configureSMSDatabaseModule(log, hostname, port, database, user, password);

		// subscription module
		SMSAppModulePostgreSQLAdapterSubscription.configureSubscriptionDatabaseModule(log, hostname, port, database, user, password);

		// profile module
		SMSAppModulePostgreSQLAdapterProfile.configureProfileDatabaseModule(log, hostname, port, database, user, password);

		// chat module
		SMSAppModulePostgreSQLAdapterChat.configureChatDatabaseModule(log, hostname, port, database, user, password,
		                                                              MOSMSesQueueDataBureau.MO_TABLE_NAME,
		                                                              MOSMSesQueueDataBureau.MO_ID_FIELD_NAME,
		                                                              MOSMSesQueueDataBureau.MO_TEXT_FIELD_NAME);

		// hangman module
		SMSAppModulePostgreSQLAdapterHangman.configureHangmanDatabaseModule(log, hostname, port, database, user, password);

	}
	
	public static void setDefaults(Instrumentation<?, ?> log, SubscriptionEngine subscriptionEngine, String subscriptionToken) {
		
		// DAL configuration
		////////////////////
		
		setDefaultDBToPOSTGRESQL(log, "venus", 5432, "hangman", "hangman", "hangman");
		
		// SMSAppModuleConfiguration
		////////////////////////////
		
		APPName      = "HANGMAN";
		APPShortCode = "9714";
		
		// SMSAppModuleConfiguration.log = null;  // to come from a parameter. BTW, where is the log for this module?
		InstantVASSMSAppModuleConfiguration.applyConfiguration();
		
		
		// SMSAppModuleConfigurationHelp
		////////////////////////////////
		
		// phrasing
		HELPphrNewUsersFallback      = "no shits currently here";
		HELPphrFallbackExistingUsers = "{{appName}}: unknown command. Please send HELP to see the full command set. Some examples: LIST to see online users; P [NICK] [MSG] to send a private message; " +
                                       "INVITE [NICK] to invite a listed player; INVITE [PHONE] to invite a friend of yours; PLAY to play with a random user. Choose an option and send it to {{shortCode}}";
		HELPphrStateless             = "You can play the {{appName}} game in 2 ways: guessing someone's word or inviting someone to play with your word " +
		                               "You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number " +
		                               "Every week, 1 lucky number is selected to win the prize. Send an option to 9714: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help";
		HELPphrComposite             = new String[] {
			"",
		};
		
		// command patterns
		HELPtrgGlobalStartCompositeHelpDialog    = new String[] {"RULES"};
		HELPtrgLocalShowNextCompositeHelpMessage = new String[] {"+"};
		HELPtrgGlobalShowStatelessHelpMessage    = new String[] {"HELP"};
		
		// stateful help messages
		setStatefulHelpMessages(new Object[][] {
			{nstNewUser,                              "fallback help message for new users"},
			{nstExistingUser,                         "fallback help message for existing users"},
			{nstAnsweringDoubleOptin,                 "fallback help message when answering double opt-in"},
			{nstRegisteringNickname,                  "fallback help message when registering a nickname"},
			{nstChattingWithSomeone,                  "help message when statefully chatting with someone"},
			{nstGuessingWordFromHangmanHumanOpponent, SMSAppModulePhrasingsHangman.getGuessingWordHelp()},
		});

		// SMSAppModuleConfigurationHelp.log = null;  // to come from a parameter. BTW, where is the log for this module?
		SMSAppModuleConfigurationHelp.applyConfiguration();
		
		
		// SMSAppModuleConfigurationSubscription
		////////////////////////////////////////
		
		// phrasing
		SUBSCRIPTIONphrDoubleOptinStart            = "You are at the {{appName}} game. To continue, you must subscribe. Send {{appName}} now to {{shortCode}} and compete for prizes. You will be charged at $ every week."; 
		SUBSCRIPTIONphrDisagreeToSubscribe         = "To fully use the {{appName}} game you need to subscribe. Please text {{appName}} to do so. In the mean time, you may consult the list of players. Text LIST to {{shortCode}}. Send RULES to learn how it worls.";
		SUBSCRIPTIONphrSuccessfullySubscribed      = "{{appName}}: Registration succeeded. Send HELP to {{shortCode}} to know the rules and how to play, or simply send PLAY to {{shortCode}}";
		SUBSCRIPTIONphrCouldNotSubscribe           = "";
		SUBSCRIPTIONphrUserRequestedUnsubscription = "You are now unsubscribed from the {{appName}} GAME and will no longer receive invitations to play nor lucky numbers. To join again, send {{appName}} to {{shortCode}}";
		SUBSCRIPTIONphrLifecycleUnsubscription     = "";
		
		// command patterns
		SUBSCRIPTIONtrgLocalStartDoubleOptin   = new String[] {".*"};
		SUBSCRIPTIONtrgLocalAcceptDoubleOptin  = new String[] {"HANGMAN"};
		SUBSCRIPTIONtrgGlobalUnsubscribe       = new String[] {"UNSUBSCRIBE"};
		
		SMSAppModuleConfigurationSubscription.subscriptionEngine = subscriptionEngine;
		SMSAppModuleConfigurationSubscription.subscriptionToken  = subscriptionToken;
		SMSAppModuleConfigurationSubscription.log = log;
		SMSAppModuleConfigurationSubscription.applyConfiguration();
		
		
		// SMSAppModuleConfigurationProfile
		///////////////////////////////////
		
		// phrasing
		PROFILEphrAskForFirstNickname              = "todo";
		PROFILEphrNicknameRegistrationNotification = "{{appName}}: Name registered: {{registeredNickname}}. Send LIST to {{shortCode}} to see online players. NICK [NEW NICK] to change your name.";
		PROFILEphrPresentation                     = "{{appName}}: {{nickname}}: Subscribed; Online; RJ. Text INVITE {{nickname}} to play a hangman match; P {{nickname}} [MSG] to chat; LIST to see online players; P to play with a random user.";
		PROFILEphrNicknameNotFound                 = "{{appName}}: No player with nickname '{{nickname}}' was found. Maybe he/she changed it? Send LIST to {{shortCode}} to see online players";

		
		// command patterns
		PROFILEtrgGlobalStartAskForNicknameDialog = new String[] {"todo"};
		PROFILEtrgLocalRegisterNickname           = new String[] {"todo"};
		//PROFILEtrgGlobalRegisterNickname          = new String[] {""};
		
		//SMSAppModuleConfigurationProfile.log = null; // to come from a parameter. BTW, where is the log for this module?
		SMSAppModuleConfigurationProfile.applyConfiguration();
		
		
		// SMSAppModuleConfigurationChat
		////////////////////////////////
		
		// phrasing
		CHATphrPrivateMessage                     = "{{senderNickname}}: {{senderMessage}} - To answer, text P {{senderNickname}} [MSG] to {{shortCode}}";
		CHATphrPrivateMessageDeliveryNotification = "{{appName}}: your message has been delivered to {{targetNickname}}. What can be the command that I'll suggest now?";
		CHATphrDoNotKnowWhoYouAreChattingTo       = "";
		
		// command patterns
		CHATtrgGlobalSendPrivateMessage = new String[] {"[MP] ([^ ]+) (.*)"};
		CHATtrgLocalSendPrivateReply    = new String[] {""};

		//SMSAppModuleConfigurationChat.log = null;	// to come from a parameter. BTW, where is the log for this module?
		SMSAppModuleConfigurationChat.applyConfiguration();

		
		// SMSAppModuleConfigurationHangman
		///////////////////////////////////
		
		// phrasing
		// (default phrases won't need to be change for testing purposes. Only when it comes the time this file will allow them to be mapped by the configuration file
		
		// command patterns
		HANGMANtrgGlobalInviteNicknameOrPhoneNumber = new String[] {"INVITE +(.*)"};
		HANGMANtrgLocalHoldMatchWord                = new String[] {"([^ ]+)"};
		HANGMANtrgLocalAcceptMatchInvitation        = new String[] {"YES"};
		HANGMANtrgLocalNewLetterOrWordSuggestion    = new String[] {"([A-Z]+)"};

		DEFAULT_NICKNAME_PREFIX = "Guest";
		//SMSAppModuleConfigurationHangman.log = null;	// to come from a parameter. BTW, where is the log for this module?
		SMSAppModuleConfigurationHangman.applyConfiguration();
		
		
		// navigation
		/////////////
		
		/* estes estados são o que define a aplicação. Estivessem em um arquivo de configurações, uma só aplicação, com todos os módulos compilados juntos,
		 * atenderia a todos os serviços possíveis. Uma forma interessante de conseguir isso é carregar estes dados através da rede, toda vez que o servidor
		 * de aplicações der boot -- desta maneira nós garantimos que o serviço "instant vas" será sempre necessário para executar o software e evitam-se
		 * possíveis problemas com pirataria.
		 * Hoje em dia já é possível que os cmds sejam chamados de forma a imitar o serviço africano 'iText'. Por exemplo, para o envio de emails,
		 * o comando cmdSendEmail (que recebe dois parâmetros) pode ser usado, mesmo que se esteja enviando sempre email para uma única pessoa,
		 * situação na qual a regular expression deve ser trocada de algo como "M (%w+) (.*)" para "M (.*)" e a chamada do comando seria trocada de
		 * cmdSendEmail("$1", "$2") para cmdSendEmail("luiz@InstantVAS.com", "$1") -- esta forma de chamar ainda tem que ser implementada, na verdade. */
		
		// TODO aqui é preciso ter um array compartilhado com todos os global triggers pra gente não ter que ficar repetindo em cada estado
		
		nstNewUser.setCommandTriggers(new Object[][] {
			{cmdSubscribe,                SUBSCRIPTIONtrgLocalAcceptDoubleOptin},	// the double opt-in process starts with a broadcast message
			{cmdUnsubscribe,              SUBSCRIPTIONtrgGlobalUnsubscribe},
			{cmdStartDoubleOptinProcess,  SUBSCRIPTIONtrgLocalStartDoubleOptin},	// for some of known wrong commands, start the double opt-in process again
			{cmdShowNewUsersFallbackHelp, ".*"},									// fallback help
		});
		nstAnsweringDoubleOptin.setCommandTriggers(new Object[][] {
			{cmdSubscribe,                SUBSCRIPTIONtrgLocalAcceptDoubleOptin},
			{cmdDoNotAgreeToSubscribe,    SUBSCRIPTIONtrgLocalRefuseDoubleOptin},
			{cmdUnsubscribe,              SUBSCRIPTIONtrgGlobalUnsubscribe},
			{cmdStartDoubleOptinProcess,  ".*"},
		});
		nstExistingUser.setCommandTriggers(new Object[][] {
			{cmdUnsubscribe,                   SUBSCRIPTIONtrgGlobalUnsubscribe},
			{cmdShowStatelessHelp,             HELPtrgGlobalShowStatelessHelpMessage},
			{cmdRegisterNickname,              PROFILEtrgGlobalRegisterNickname},
			{cmdInviteNicknameOrPhoneNumber,   HANGMANtrgGlobalInviteNicknameOrPhoneNumber},
			{cmdSendPrivateMessage,            CHATtrgGlobalSendPrivateMessage},
			{cmdShowUserProfile,               PROFILEtrgGlobalShowUserProfile},
			{cmdShowExistingUsersFallbackHelp, ".*"},
		});
		nstEnteringMatchWord.setCommandTriggers(new Object[][] {
			{cmdHoldMatchWord,                 HANGMANtrgLocalHoldMatchWord},
			{cmdShowExistingUsersFallbackHelp, ".*"},
		});
		nstAnsweringToHangmanMatchInvitation.setCommandTriggers(new Object[][] {
			{cmdSendPrivateMessage,            CHATtrgGlobalSendPrivateMessage},
			{cmdAcceptMatchInvitation,         HANGMANtrgLocalAcceptMatchInvitation},
			{cmdRefuseMatchInvitation,         HANGMANtrgLocalRefuseMatchInvitation},
			{cmdShowUserProfile,               PROFILEtrgGlobalShowUserProfile},
			{cmdShowExistingUsersFallbackHelp, ".*"},
		});
		nstGuessingWordFromHangmanHumanOpponent.setCommandTriggers(new Object[][] {
			{cmdSuggestLetterOrWordForHuman,   HANGMANtrgLocalNewLetterOrWordSuggestion},
			{cmdSendPrivateMessage,            CHATtrgGlobalSendPrivateMessage},
			{cmdShowStatefulHelp,              ".*"},
		});
		


	}

}
