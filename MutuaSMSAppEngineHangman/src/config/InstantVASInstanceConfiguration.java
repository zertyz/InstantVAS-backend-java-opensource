package config;

import static config.InstantVASLicense.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.CommandNamesHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.CommandTriggersHelp.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp.NavigationStatesNamesHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.CommandNamesSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.CommandTriggersSubscription.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription.NavigationStatesNamesSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.CommandNamesProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.CommandTriggersProfile.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.NavigationStatesNamesProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.CommandNamesChat.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.CommandTriggersChat.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat.NavigationStatesNamesChat.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman.CommandNamesHangman.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman.CommandTriggersHangman.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman.NavigationStatesNamesHangman.*;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import instantvas.nativewebserver.InstantVASConfigurationLoader;
import instantvas.smsengine.MOSMSesQueueDataBureau;
import instantvas.smsengine.MTSMSesQueueDataBureau;
import instantvas.smsengine.producersandconsumers.EInstantVASEvents;
import instantvas.smsengine.producersandconsumers.InstantVASEvent;
import instantvas.smsengine.producersandconsumers.MOAndMTProfileInstrumentationHandler;
import mutua.events.DirectEventLink;
import mutua.events.IEventLink;
import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.QueueEventLink;
import mutua.events.postgresql.QueuesPostgreSQLAdapter;
import mutua.hangmansmsgame.smslogic.SMSProcessor;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.handlers.IInstrumentationHandler;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerLogConsole;
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
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsChat;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription;
import mutua.smsappmodule.i18n.plugins.IGeoLocatorPlaceHolder;
import mutua.smsappmodule.i18n.plugins.UserGeoLocator;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription;
import mutua.smsappmodule.smslogic.SMSAppModuleEventsSubscription;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.NavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription;
import mutua.smsin.parsers.SMSInCelltick;
import mutua.smsin.parsers.SMSInParser;
import mutua.smsout.senders.SMSOutCelltick;
import mutua.smsout.senders.SMSOutSender;
import mutua.subscriptionengine.CelltickLiveScreenSubscriptionAPI;
import mutua.subscriptionengine.SubscriptionEngine;
import mutua.subscriptionengine.TestableSubscriptionAPI;
import adapters.HTTPClientAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * InstantVASInstanceConfiguration.java
 * ====================================
 * (created by luiz, Sep 15, 2015)
 *
 * This class defines configurations that m
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class InstantVASInstanceConfiguration {

//	// Instant VAS main configuration
//	/////////////////////////////////
//	
//	public enum EConfigurationSourceType {JAR_FILE/*read-only*/, FS_FILE/*rw*/, HTTP/*rw*/, POSTGRESQL/*rw*/};
//	
//	@ConfigurableElement("Defines the retrieve method for the Instant VAS instances definitions")
//	public static EConfigurationSourceType INSTANTVAS_INSTANCES_SOURCE_TYPE;
//	@ConfigurableElement("The location, in respect to the above definition, to retrieve the data from")
//	public static String                   INSTANTVAS_INSTANCES_SOURCE_ACCESS_INFO;
//	
//	// Instant VAS instances
//	////////////////////////
//	// The following values may be repeated n times -- one for each SMS Application Instance served by this Instant VAS server
//		
//	@ConfigurableElement("Defines the retrieve method for the Instant VAS application configuration")
//	public static EConfigurationSourceType[] INSTANTVAS_INSTANCE_CONFIG_TYPE;
//	@ConfigurableElement("The location, in respect to the above definition, to retrieve the data from")
//	public static String[]                   INSTANTVAS_INSTANCE_CONFIG_ACCESS_INFO;
//	@ConfigurableElement("The token this instance's requests are required to provide to have their authorization granted")
//	public static String[]                   INSTANTVAS_INSTANCE_CONFIG_TOKEN;
//	
//	// Navite HTTP Server
//	/////////////////////
//
//	@ConfigurableElement("The port the Native HTTP server should listen to, on all interfaces")
//	public static int NATIVE_HTTP_SERVER_PORT                = 8080;
//	@ConfigurableElement("The number of accepted connections put on wait while one of the request processing threads become available to process them")
//	public static int NATIVE_HTTP_SOCKET_BACKLOG_QUEUE_SLOTS = 9999;
//	@ConfigurableElement("The maximum number of requests to be processed in parallel by the native web server")
//	public static int NATIVE_HTTP_NUMBER_OF_THREADS          = 5;
//	@ConfigurableElement("For POST methods, the native web server reads chunks at the most this number of bytes")
//	public static int NATIVE_HTTP_INPUT_BUFFER_SIZE          = 1024;
//	@ConfigurableElement("While reading the chunks above, wait at most this number of milliseconds before considering the connection stale")
//	public static int NATIVE_HTTP_READ_TIMEOUT               = 30000;
	
	// SMS Application
	//////////////////
	
	public enum ELogEventHandler    {CONSOLE, PLAIN_FILE, ROTATORY_PLAIN_FILE, ROTATORY_COMPRESSED_FILE};
	public enum EReportEventHandler {CONSOLE, PLAIN_FILE, ROTATORY_PLAIN_FILE, ROTATORY_COMPRESSED_FILE, POSTGRESQL};

	@ConfigurableElement({"Reports", "#######", "", "Where to store report data"})
	public static EReportEventHandler REPORT_DATA_COLLECTOR_STRATEGY;
	
	@ConfigurableElement({"Logs", "####", "", "Where to store log data"})
	public static ELogEventHandler LOG_STRATEGY;
	@ConfigurableElement("What severity of log events should be persisted?")
	public static ELogSeverity MINIMUM_LOG_SEVERITY;
	// TODO review those file name variables -- one should be for the reports and the other for the logs. There should be a prefix and suffix
	@ConfigurableElement("File name to log Hangman Logic logs")
	public static String LOG_HANGMAN_FILE_PATH;
	@ConfigurableElement("File name to log Hangman Web / Integration logs")
	public static String LOG_WEBAPP_FILE_PATH;
	
	@ConfigurableElement({"Game Options", "############", "", "The name of the Hangman Game -- phrases can refer to this value using {{appName}}"})
	public static String APP_NAME;
	@ConfigurableElement("The short code of the Hangman Game -- phrases can refer to this value using {{shortCode}}")
	public static String SHORT_CODE;
	@ConfigurableElement("The subscription cost to the end user -- phrases can refer to this value using {{priceTag}}")
	public static String PRICE_TAG;
	@ConfigurableElement("Default prefix for invited & new users -- The suffix are the last 4 phone number digits")
	public static String DEFAULT_NICKNAME_PREFIX;	

	@ConfigurableElement({"Bot users should be specifyed via real phone numbers if bots are to be handled transparently, being able to participate in chats, etc.",
	                      "Anyway, the bot user nicknames must be set via normal SMS interaction with NICK <new nickname> command or via direct database intervention on the table 'PROFILE'.",
	                      "You may set as many bots as you want, but must set at least one. Matches will be shared among them via a round-robin algorithm.",
	                      "By the way, bot words must be defined directly on the database by appending them to the 'NextBotWords' table."})
	public static String[] BOT_PHONE_NUMBERS = {
		"5521991234899",
	};
	@ConfigurableElement("Words to cycle through when playing with the computer")
	public static String[] BOT_WORDS;

	
	// MO QUEUE (but also SubscribeUser & UnsubscribeUser queues)
	/////////////////////////////////////////////////////////////
	
	public enum EEventProcessingStrategy {DIRECT, RAM, LOG_FILE, POSTGRESQL};
	public enum EInstantVASDAL           {RAM, POSTGRESQL}
		
	// HTTPClientAdapter
	////////////////////
	
	@ConfigurableElement("General HTTP/HTTPD client behavior, in milliseconds")
	public static int HTTP_CONNECTION_TIMEOUT_MILLIS;
	@ConfigurableElement()
	public static int HTTP_READ_TIMEOUT_MILLIS;
	
	// Integration with 'SMSOutCelltick' and 'SubscriptionEngineCelltick'
	/////////////////////////////////////////////////////////////////////

	@ConfigurableElement({"Integration Parameters", "######################", "", "Lifecycle Client service base URLs for Subscription & Unsubscription using 'CelltickLiveScreenSubscriptionAPI'"})
	public static String LIFECYCLE_SERVICE_BASE_URL;
	@ConfigurableElement("The 'CelltickLiveScreenSubscriptionAPI's 'package name' for this service -- the value to be sent when attempting to subscribe / unsubscribe using the provided 'LIFECYCLE_SERVICE_BASE_URL'")
	public static String LIFECYCLE_CHANNEL_NAME;
	@ConfigurableElement("MT service URLs & data for Celltick's Kannel APIs")
	public static String MT_SERVICE_URL;
	@ConfigurableElement("'SMSC' parameter when delivering MTs on the Celltick's Kannel API")
	public static String KANNEL_MT_SMSC = "C1";
	@ConfigurableElement("the number of times 'sendMessage' will attempt to send the message before reporting it as unsendable")
	public static int    MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS;
	@ConfigurableElement("the number of milliseconds 'sendMessage' will wait between retry attempts")
	public static long   MT_SERVICE_DELAY_BETWEEN_ATTEMPTS;
	
	// JDBCAdapter
	//////////////
	
	@ConfigurableElement({"Database", "########", "", "The desired data access handler for all hangman databases"})
	public static EInstantVASDAL  DATA_ACCESS_LAYER;
	@ConfigurableElement("Hostname (or IP) of the PostgreSQL server. For localhost, try '::1' first")
	public static String  POSTGRESQL_HOSTNAME;
	@ConfigurableElement("Connection port for the PostgreSQL server")
	public static int     POSTGRESQL_PORT;
	@ConfigurableElement("The PostgreSQL database with the application's data scope")
	public static String  POSTGRESQL_DATABASE;
	@ConfigurableElement("The PostgreSQL user name to access 'DATABASE' -- note: administrative rights, such as the creation of tables, are necessary for the model auto-generation feature")
	public static String  POSTGRESQL_USER;
	@ConfigurableElement("The PostgreSQL plain text password for the above user")
	public static String  POSTGRESQL_PASSWORD;
	@ConfigurableElement("Additional URL parameters for PostgreSQL JDBC driver connection properties")
	public static String  POSTGRESQL_CONNECTION_PROPERTIES;
	@ConfigurableElement("The number of concurrent connections allowed to each database server. Suggestion: fine tune to get the optimum number for this particular app/database, paying attention to the fact that a pool smaller than the sum of all consumer threads may be suboptimal, and that a greater than it can be a waste. As an initial value, set this to 2 * nDbCPUs * nDbHDs and adjust the 'number of consumer threads for the MO queue' accordingly")
	public static int     NUMBER_OF_CONCURRENT_CONNECTIONS;
	@ConfigurableElement("Indicates whether or not to perform any needed administrative tasks, such as table & stored procedure creation")
	public static boolean POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS;
	@ConfigurableElement("Set to true to have all database queries logged")
	public static boolean POSTGRESQL_SHOULD_DEBUG_QUERIES;
		
	@ConfigurableElement({"MO Processing", "#############", "", "Specifies which event processing strategy should be used on incoming SMSes (MOs) -- DIRECT means the messages will be processed directly, on the same request thread and without any buffer; RAM means the producers and consumers must be running on the same machine and on the same process; POSTGRESQL means a table will be used to keep those messages and serve as the queue at the same time"})
	public static EEventProcessingStrategy MO_PROCESSING_STRATEGY;
	@ConfigurableElement("The maximum number of entries when using 'RAM' for 'MO_PROCESSING_STRATEGY'")
	public static int    MO_RAM_QUEUE_CAPACITY;
	@ConfigurableElement("The directory were to store log files when using 'LOG_FILE' for 'MO_PROCESSING_STRATEGY'")
	public static String MO_FILE_QUEUE_LOG_DIRECTORY;
	@ConfigurableElement("The maximum milliseconds the 'LOG_FILE' consumer manager should wait between queries for new queue entries to process. Set to 0 to rely on the internal notification mechanisms and only when queue producers and consumers are running on the same machine and on the same process.")
	public static long   MO_FILE_QUEUE_POOLING_TIME;
	@ConfigurableElement("Same as above, but applyed when using the 'POSTGRESQL' queue driver")
	public static long   MO_POSTGRESQL_QUEUE_POOLING_TIME;
	@ConfigurableElement("The number of consumer threads. Applyable to all queues (not applyable to the 'DIRECT' event processing strategy). This number should not be greater than 'NUMBER_OF_CONCURRENT_CONNECTIONS'. The value here should be determined through experiments which aim to achieve the greater number of MOs processed per second")
	public static int    MO_QUEUE_NUMBER_OF_WORKER_THREADS;

	@ConfigurableElement({"MT Processing", "#############", "", "The same as described on 'MO_PROCESSING_STRATEGY', but for MTs (outgoing SMSes)"})
	public static EEventProcessingStrategy MT_PROCESSING_STRATEGY;
	@ConfigurableElement("The maximum number of entries when using 'RAM' for 'MT_PROCESSING_STRATEGY'")
	public static int    MT_RAM_QUEUE_CAPACITY;
	@ConfigurableElement("The directory were to store log files when using 'LOG_FILE' for 'MT_PROCESSING_STRATEGY'")
	public static String MT_FILE_QUEUE_LOG_DIRECTORY;
	@ConfigurableElement("The maximum milliseconds the 'LOG_FILE' consumer manager should wait between queries for new queue entries to process. Set to 0 to rely on the internal notification mechanisms and only when queue producers and consumers are running on the same machine and on the same process.")
	public static long   MT_FILE_QUEUE_POOLING_TIME;
	@ConfigurableElement("Same as above, but applyed when using the 'POSTGRESQL' queue driver")
	public static long   MT_POSTGRESQL_QUEUE_POOLING_TIME;
	@ConfigurableElement("The number of consumer threads. Applyable to all queues (not applyable to the 'DIRECT' event processing strategy). This number may be greater than 'NUMBER_OF_CONCURRENT_CONNECTIONS' and should get bigger as the latency between the two involved servers increase, as the processing time on the MT server increase, as the number of MT retries increase and so on...")
	public static int    MT_QUEUE_NUMBER_OF_WORKER_THREADS;
	
	@ConfigurableElement({"Subscription Renewal Processing", "###############################", "", "The same as described on 'MO_PROCESSING_STRATEGY', but for 'Subscription Renewal' events, generated by the subscription lifecycle engine"})
	public static EEventProcessingStrategy SUBSCRIPTION_RENEWAL_PROCESSING_STRATEGY;
	@ConfigurableElement("The maximum number of entries when using 'RAM' for 'SUBSCRIPTION_RENEWAL_PROCESSING_STRATEGY'")
	public static int    SR_RAM_QUEUE_CAPACITY;
	@ConfigurableElement("The directory were to store log files when using 'LOG_FILE' for 'SUBSCRIPTION_RENEWAL_PROCESSING_STRATEGY'")
	public static String SR_FILE_QUEUE_LOG_DIRECTORY;
	@ConfigurableElement("The maximum milliseconds the 'LOG_FILE' consumer manager should wait between queries for new queue entries to process. Set to 0 to rely on the internal notification mechanisms and only when queue producers and consumers are running on the same machine and on the same process.")
	public static long   SR_FILE_QUEUE_POOLING_TIME;
	@ConfigurableElement("Same as above, but applyed when using the 'POSTGRESQL' queue driver")
	public static long   SR_POSTGRESQL_QUEUE_POOLING_TIME;
	@ConfigurableElement("The number of consumer threads. Applyable to all queues (not applyable to the 'DIRECT' event processing strategy). This number may be greater than 'NUMBER_OF_CONCURRENT_CONNECTIONS' and should get bigger as the latency between the two involved servers increase, as the processing time on the MT server increase, as the number of MT retries increase and so on...")
	public static int    SR_QUEUE_NUMBER_OF_WORKER_THREADS;

	@ConfigurableElement({"Subscription Cancellation Processing", "####################################", "", "The same as described on 'SUBSCRIPTION_RENEWAL_PROCESSING_STRATEGY', but for 'Subscription Cancellation' events"})
	public static EEventProcessingStrategy SUBSCRIPTION_CANCELLATION_PROCESSING_STRATEGY;
	@ConfigurableElement("The maximum number of entries when using 'RAM' for 'SUBSCRIPTION_CANCELLATION_PROCESSING_STRATEGY'")
	public static int    SC_RAM_QUEUE_CAPACITY;
	@ConfigurableElement("The directory were to store log files when using 'LOG_FILE' for 'SUBSCRIPTION_CANCELLATION_PROCESSING_STRATEGY'")
	public static String SC_FILE_QUEUE_LOG_DIRECTORY;
	@ConfigurableElement("The maximum milliseconds the 'LOG_FILE' consumer manager should wait between queries for new queue entries to process. Set to 0 to rely on the internal notification mechanisms and only when queue producers and consumers are running on the same machine and on the same process.")
	public static long   SC_FILE_QUEUE_POOLING_TIME;
	@ConfigurableElement("Same as above, but applyed when using the 'POSTGRESQL' queue driver")
	public static long   SC_POSTGRESQL_QUEUE_POOLING_TIME;
	@ConfigurableElement("The number of consumer threads. Applyable to all queues (not applyable to the 'DIRECT' event processing strategy). This number may be greater than 'NUMBER_OF_CONCURRENT_CONNECTIONS' and should get bigger as the latency between the two involved servers increase, as the processing time on the MT server increase, as the number of MT retries increase and so on...")
	public static int    SC_QUEUE_NUMBER_OF_WORKER_THREADS;
	
//	public enum EInstantVASModules {// celltick integration modules, for production
//	                                CELLTICK_BR_INTEGRATION,
//	                                // celltick integration modules, for testing
//	                                CELLTICK_JUNIT_TESTS_INTEGRATION,
//	                                // infrastructure modules
//	                                BASE, HELP, SUBSCRIPTION, SUBSCRIPTION_LIFECYCLE, DRAW, PROFILE,
//	                                // entretainment modules
//	                                QUIZ, CELEBRITY_AI, REVERSE_AUCTION,
//	                                // entretainment / mobile learning modules
//	                                DECISION_TREE,
//	                                // game modules
//	                                HANGMAN, TIC_TAC_TOE, XAVECO,
//	                                // bet modules
//	                                SWEEPSTAKE,	OFFER_VS_DEMAND,
//	                                // mobile marketing modules
//	                                ALERTS, NOTIFICATIONS, PROXIMITY_SEARCH, TEXT4INFO, PIN_CODE, MASS_TEXT_MESSAGING,
//	                                // social network modules
//	                                CHAT, DATING, MATCH_MAKING, SMS_TWITTER, SMS_QUORA,
//	                                // mobile banking modules
//	                                MPAYMENT,
//	                                // mobile aggregator modules
//	                                ZETA, SMS_ROUTER,
//	};
//
//
//	@ConfigurableElement("Not a good idea to mess with these values")
//	public static EInstantVASModules[] ENABLED_MODULES;
//	
//	// AddToMOQueue, AddToSubscribeUserQueue and other services (license infringment control)
//	/////////////////////////////////////////////////////////////////////////////////////////
//	
//	@ConfigurableElement("If set, instructs /AddToMOQueue and other services to require received MSISDNs to have a minimum length")
//	public static int                   ALLOWABLE_MSISDN_MIN_LENGTH = -1;
//	@ConfigurableElement("Same as above, but for a maximum length")
//	public static int                   ALLOWABLE_MSISDN_MAX_LENGTH = -1;
//	@ConfigurableElement("If set, MSISDNs used on any service will be required to have one of the listed prefixes")
//	public static String[]              ALLOWABLE_MSISDN_PREFIXES;
//	@ConfigurableElement("If set, /AddToMOQueue will only process MOs from the listed carriers")
//	public static ESMSInParserCarrier[] ALLOWABLE_CARRIERS;
//	@ConfigurableElement("If set, /AddToMOQueue (and other services) will only process MOs and send MTs to the listed short codes -- which may be long codes as well")
//	public static String[]              ALLOWABLE_SHORT_CODES;


	
	// Phrasings
	////////////
	
	// help
	@ConfigurableElement({"Phrases", "#######", "", "Phrase sent when a new user sends an unrecognized keyword, possibly instructing him/her on how to register. Variables: {{shortCode}}, {{appName}}"})
	public static String HELPphrNewUsersFallback;
	@ConfigurableElement("Phrase sent when an existing user attempts to send an unrecognized command, to give him/her a quick list of commands. Variables: {{shortCode}}, {{appName}}")
	public static String HELPphrExistingUsersFallback;
	@ConfigurableElement("These are the general help messages, sent in response to the HELP command anywhere in the app navigation states. This message will not interrupt the flow and the user may continue normally after receiving this message. Variables: {{shortCode}}, {{appName}}")
	public static String HELPphrStateless;
	@ConfigurableElement("These are the detailed help messages, sent in response to the HELP/RULES command that will change the navigation state. You can set a second, third and so on help messages, which will be sent in response to the MORE command. Variables: {{shortCode}}, {{appName}}")
	public static String[] HELPphrComposite;
	@ConfigurableElement("Used on a temporary fix. Search for this variable and fix. For this to be used, a new variable should be created for every state. Example: HELPphrPlayingWithAHuman, ...")
	public static String[][] HELPphrStatefulHelpMessages;
	
	// subscription
	@ConfigurableElement("Phrase sent to inform the user he/she is about to subscribe -- the navigation will go to 'nstAnsweringDoubleOptin', where the user must answer with YES to continue. Variables: {{shortCode}}, {{appName}}, {{priceTag}}")
	public static String SUBSCRIPTIONphrDoubleOptinStart;
	@ConfigurableElement("Phrase sent when the user answers NO (or doesn't answer YES) to the double opt-in process -- informs he/she has to agree to use the service. Variables: {{shortCode}}, {{appName}}, {{priceTag}}")
	public static String SUBSCRIPTIONphrDisagreeToSubscribe;
	@ConfigurableElement("Phrase sent in response to a successful user subscription attempt -- an app 'welcome & you are ready to use it' message. Variables: {{shortCode}}, {{appName}}, {{priceTag}}")
	public static String SUBSCRIPTIONphrSuccessfullySubscribed;
	@ConfigurableElement("Phrase sent in response to a unsuccessful user subscription attempt -- something like 'you cannot use it yet' or 'we couldn't bill you'. Variables: {{shortCode}}, {{appName}}, {{priceTag}}")
	public static String SUBSCRIPTIONphrCouldNotSubscribe;
	@ConfigurableElement("Sent to inform the subscription was canceled on the platform due to user request. Variables: {{shortCode}}, {{appName}}, {{priceTag}}")
	public static String SUBSCRIPTIONphrUserRequestedUnsubscription;
	@ConfigurableElement("Sent to inform the subscription was canceled on the platform due to carrier's lifecycle rules. Variables: {{shortCode}}, {{appName}}, {{priceTag}}")
	public static String SUBSCRIPTIONphrLifecycleUnsubscription;
	
	// profile
	@ConfigurableElement("Phrase sent when the system wants the user to inform his/her nickname for the first time. Variables: {{shortCode}}, {{appName}}")
	public static String PROFILEphrAskForFirstNickname;
	@ConfigurableElement("Phrase sent when the system asks the user to change his/her nickname (for the case when he/she already has a valid nickname). Variables: {{shortCode}}, {{appName}}, {{currentNickname}}")
	public static String PROFILEphrAskForNewNickname;
	@ConfigurableElement("Phrase sent when the 'change nickname dialog' has been deliberately cancelled by the user. Consider the opportunity to present some possible next commands. Variables: {{shortCode}}, {{appName}}, {{currentNickname}}")
	public static String PROFILEphrAskForNicknameCancelation;
	@ConfigurableElement("Phrase sent in response to the request of changing the nickname -- the text should confirm the nickname registered on the system. Variables: {{shortCode}}, {{appName}}, {{registeredNickname}}")
	public static String PROFILEphrNicknameRegistrationNotification;
	@ConfigurableElement("Text sent to present the details of a user profile. Variables: {{shortCode}}, {{appName}}, {{nickname}} and others, from extensions, such as {{countryStateByMSISDN}} -- see 'PROFILEGeoLocatorMSISDNCountryStatePatterns' bellow")
	public static String PROFILEphrUserProfilePresentation;
	@ConfigurableElement("Phrase sent to the sender user, who referenced a user by it's nickname, to inform that the command wasn't executed for the informed nickname was not found. Variables: {{shortCode}}, {{appName}}, {{targetNickname}}")
	public static String PROFILEphrNicknameNotFound;
	@ConfigurableElement("Phrase excerpt used when composing each of the profiles in the profiles list command. Variables: {{shortCode}}, {{appName}}, {{nickname}} and others, from extensions, such as {{countryStateByMSISDN}}")
	public static String PROFILEphrShortProfilePresentation;
	@ConfigurableElement("Builds a profiles list from 'profiles' using the phrase 'PROFILEphrShortProfilePresentation' for each element, which will be placed in substitution for {{profilesList}}. Variables: {{shortCode}}, {{appName}} and, of course, {{profilesList}}")
	public static String PROFILEphrProfileList;
	@ConfigurableElement("Phrase to show when, in the attempt to list available profiles, there are none left to show. Variables: {{shortCode}}, {{appName}}")
	public static String PROFILEphrNoMoreProfiles;
	@ConfigurableElement("Phrase sent as a help when the provided nickname, on the 'change nickname dialog', is not valid. Variables: {{shortCode}}, {{appName}}, {{MOText}}")
	public static String PROFILENicknameRegistrationFallbackHelp;
	
	// chat
	@ConfigurableElement("Phrase sent to the target user when the sender user wants to send a chat private message. Variables: {{shortCode}}, {{appName}}, {{senderNickname}}, {{senderMessage}}")
	public static String CHATphrPrivateMessage;
	@ConfigurableElement("Phrase sent to the sender user, who sent a private message, to inform of the correct delivery. Variables: {{shortCode}}, {{appName}}, {{targetNickname}}")
	public static String CHATphrPrivateMessageDeliveryNotification;
	@ConfigurableElement("Phrase used to inform the sender that the stateful private conversation can no longer be conducted (because we don't know who is the target user), therefore, he/she must try the stateles command. Variables: {{shortCode}}, {{appName}}")
	public static String CHATphrDoNotKnowWhoYouAreChattingTo;
	
	// hangman
	@ConfigurableElement("The gallows ascii art, after winning a hangman match")
	public static String HANGMANwinningArt;
	@ConfigurableElement("the gallows ascii art, after losing")
	public static String HANGMANlosingArt;
	@ConfigurableElement("The character to be used to draw the head on the gallows ASCII art")
	public static String HANGMANheadCharacter;
	@ConfigurableElement("The character to be used to draw the left arm on the gallows ASCII art")
	public static String HANGMANleftArmCharacter;
	@ConfigurableElement("The character to be used to draw the chest on the gallows ASCII art")
	public static String HANGMANchestCharacter;
	@ConfigurableElement("The character to be used to draw the right arm on the gallows ASCII art")
	public static String HANGMANrightArmCharacter;
	@ConfigurableElement("The character to be used to draw the left leg on the gallows ASCII art")
	public static String HANGMANleftLegCharacter;
	@ConfigurableElement("The character to be used to draw the right leg on the gallows ASCII art")
	public static String HANGMANrightLegCharacter;
	@ConfigurableElement("Text ASCII art to present the gallows. Used in the next phrases to present the status of the hangman match")
	public static String HANGMANphr_gallowsArt;
	@ConfigurableElement("Response to the request of inviting a human opponent to play -- Asks for his/her phone or nickname. Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}}")
	public static String HANGMANphrAskOpponentNicknameOrPhone;
	@ConfigurableElement("Response to the request of inviting an internal human opponent to play -- Asks for the word he/she wants to be guessed. Variables: {{shortCode}}, {{appName}}, {{opponentNickname}}")
	public static String HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation;
	@ConfigurableElement("Response to the request of inviting an external human opponent to play (by giving his/her phone number) -- Asks for the word he/she wants to be guessed. Variables: {{shortCode}}, {{appName}}, {{opponentPhoneNumber}}")
	public static String HANGMANphrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation;
	@ConfigurableElement("Phrase to notify the inviting player that the notification has been sent to the opponent. Variables: {{shortCode}}, {{appName}}, {{invitedPlayerNickname}}")
	public static String HANGMANphrInvitationResponseForInvitingPlayer;
	@ConfigurableElement("Phrase to notify the invited player that he/she has been invited for a hangman match. Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}}")
	public static String HANGMANphrInvitationNotificationForInvitedPlayer;
	@ConfigurableElement("Notification sent to the inviting player when the invited player doesn't answer to the invitation request within a certain ammount of time. Variables: {{shortCode}}, {{appName}}, {{invitedPlayerNickname}}, {{suggestedNewPlayersNickname}}")
	public static String HANGMANphrTimeoutNotificationForInvitingPlayer;
	@ConfigurableElement("Respose sent to the invited player when he/she refuses a match. Variables: {{shortCode}}, {{appName}}, {{invitingPlayerNickname}}")
	public static String HANGMANphrInvitationRefusalResponseForInvitedPlayer;
	@ConfigurableElement("Notification sent to the word providing player when the opponent refuses the match. Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrInvitationRefusalNotificationForInvitingPlayer;
	@ConfigurableElement("Error Respose sent to the invited player when he/she accepts a match. Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{usedLetters}}")
	public static String HANGMANphrNotAGoodWord;
	@ConfigurableElement("Notification sent to the word providing player when the opponent accepts the match. Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrWordProvidingPlayerMatchStart;
	@ConfigurableElement("Respose sent to the invited player when he/she accepts a match. Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{usedLetters}}")
	public static String HANGMANphrWordGuessingPlayerMatchStart;
	@ConfigurableElement("Notification sent to the word providing player when the opponent tries to guess his/her word. Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{guessedLetter}}, {{usedLetters}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrWordProvidingPlayerStatus;
	@ConfigurableElement("Response sent to the word guessing player when he/she guesses (sends) a letter or word. Variables: {{shortCode}}, {{appName}}, {{gallowsArt}}, {{guessedWordSoFar}}, {{usedLetters}}")
	public static String HANGMANphrWordGuessingPlayerStatus;
	@ConfigurableElement("Response sent to the word guessing player when he/she wins the match. Variables: {{shortCode}}, {{appName}}, {{winningArt}}, {{word}}, {{wordProvidingPlayerNickname}}")
	public static String HANGMANphrWinningMessageForWordGuessingPlayer;
	@ConfigurableElement("Notification sent to the word providing player when the opponent wins the match. Variables: {{shortCode}}, {{appName}}, {{winningArt}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrWinningMessageForWordProvidingPlayer;
	@ConfigurableElement("Response sent to the word guessing player when he/she loses the match. Variables: {{shortCode}}, {{appName}}, {{losingArt}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrLosingMessageForWordGuessingPlayer;
	@ConfigurableElement("Notification sent to the word providing player when the opponent loses the match. Variables: {{shortCode}}, {{appName}}, {{losingArt}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrLosingMessageForWordProvidingPlayer;
	@ConfigurableElement("Notification sent to the word guessing player when his/her actions lead to the cancel of the match. Variables: {{shortCode}}, {{appName}}, {{wordProviderPlayerNickname}}")
	public static String HANGMANphrMatchGiveupNotificationForWordGuessingPlayer;
	@ConfigurableElement("Notification sent to the word providing player when the match has been canceled -- tipically due to an action of the opponent. Variables: {{shortCode}}, {{appName}}, {{wordGuessingPlayerNickname}}")
	public static String HANGMANphrMatchGiveupNotificationForWordProvidingPlayer;
	@ConfigurableElement("Response sent to the word guessing player if he/she fails to send a valid command while on a hangman match. Variables: {{shortCode}}, {{appName}}")
	public static String HANGMANphrGuessingWordHelp;

	// Command Triggers (Regular Expression Patterns)
	/////////////////////////////////////////////////
	
	// help
	@ConfigurableElement({
		"################",
		"Command Triggers",
		"################",
		"The following definitions specifies commands and the regular expressions that activates them.",
		"Command names should appear as the first element, and the possible values are, for each module:",
		"HELP:         ShowStatelessHelp, ShowStatefulHelp, StartCompositeHelpDialog, ShowNextCompositeHelpMessage, ShowNewUsersFallbackHelp, ShowExistingUsersFallbackHelp",
		"SUBSCRIPTION: StartDoubleOptinProcess, Subscribe, Unsubscribe, DoNotAgreeToSubscribe",
		"PROFILE:      StartAskForNicknameDialog, AskForNicknameDialogCancelation, RegisterNickname, ShowUserProfile, ListProfiles, ListMoreProfiles",
		"CHAT:         SendPrivateReply, SendPrivateMessage",
		"HANGMAN:      InviteNicknameOrPhoneNumber, StartHangmanMatchInvitationProcess, HoldOpponentPhoneNumber, HoldOpponentNickname, HoldMatchWord, AcceptMatchInvitation, RefuseMatchInvitation, SuggestLetterOrWordForHuman, SuggestLetterOrWordForBot",
        "",
		"HELP module",
		"###########",
		"",
		"If matched, shows a very long help message which -- requires pagination to show it's full contents -- and puts the user in the 'nstPresentingCompositeHelp' navigation state"})
	public static String[] HELPtrgGlobalStartCompositeHelpDialog;
	@ConfigurableElement("For users in 'nstPresentingCompositeHelp', if matched, shows the next page of a very long help")
	public static String[] HELPtrgLocalShowNextCompositeHelpMessage;
	@ConfigurableElement("If the MO for a new user isn't matched by no other trigger, this one will present the 'HELPphrNewUsersFallback' message. For no reason it should be different than .*")
	public static String[] HELPtrgGlobalShowNewUsersFallbackHelp;
	@ConfigurableElement("If the MO isn't matched by no other trigger, this one will present the 'HELPphrExistingUsersFallback' message. For no reason it should be different than .*")
	public static String[] HELPtrgGlobalShowExistingUsersFallbackHelp;
	@ConfigurableElement("If matched, shows a single message help")
	public static String[] HELPtrgGlobalShowStatelessHelpMessage;
	@ConfigurableElement("Fallback pattern (should always be matched) to get a help when no other command was recognized")
	public static String[] HELPtrgGlobalShowStatefulHelpMessage;
	@ConfigurableElement("Fallback pattern (should always be matched) to get a help when no other command was recognized")
	public static String[] HELPtrgGlobalShowStatefulHelpMessageFallback;
	
	// subscription
	@ConfigurableElement({"SUBSCRIPTION module", "###################", "", "For users in 'nstNewUser' or 'nstUnsubscribedUser', if matched, starts the double opt-in process"})
	public static String[] SUBSCRIPTIONtrgLocalStartDoubleOptin;
	@ConfigurableElement("For users in 'nstAnsweringDoubleOptin', if matched, considers the subscription as being accepted")
	public static String[] SUBSCRIPTIONtrgLocalAcceptDoubleOptin;
	@ConfigurableElement("Same as above, but considers the subscription as being denied")
	public static String[] SUBSCRIPTIONtrgLocalRefuseDoubleOptin;
	@ConfigurableElement("If matched, attempts to cancel the user subscription, returning him/her to the 'nstNewUser' navigation state")
	public static String[] SUBSCRIPTIONtrgGlobalUnsubscribe;
	
	// profile
	@ConfigurableElement({"PROFILE module", "##############", "", "If matched, asks for a (new) nickname, starting the 'register a nickname' dialog and puts the user in 'nstRegisteringNickname' navigation state"})
	public static String[] PROFILEtrgGlobalStartAskForNicknameDialog;
	@ConfigurableElement("For users in 'nstRegisteringNickname', if matched, cancels the 'register a nickname' dialog")
	public static String[] PROFILEtrgLocalNicknameDialogCancelation;
	@ConfigurableElement("For users in 'nstRegisteringNickname', if matched, sets the new nickname. Should match 1 parameter: the new nickname")
	public static String[] PROFILEtrgLocalRegisterNickname;
	@ConfigurableElement("If matched, registers the user's new nickname. Should capture 1 parameter: the new nickname")
	public static String[] PROFILEtrgGlobalRegisterNickname;
	@ConfigurableElement("If matched, presents the desired user's profile. May capture 1 parameter: the desired user's nickname. If matched with no parameters, the current user profile will be presented")
	public static String[] PROFILEtrgGlobalShowUserProfile;
	@ConfigurableElement("If matched, starts the presentation of the list of available profiles for interaction, starting with the one which most recently sent an MO. Should capture no parameters.")
	public static String[] PROFILEtrgGlobalListProfiles;
	@ConfigurableElement("For users in 'nstListingProfiles', if matched, keeps on presenting the list of available profiles ordered by their last sent MO. Should capture no parameters.")
	public static String[] PROFILEtrgLocalListMoreProfiles;

	
	// chat
	@ConfigurableElement({"CHAT module", "###########", "", "If matched, sends a private message to a user. Should capture 2 parameters: the nickname and the message"})
	public static String[] CHATtrgGlobalSendPrivateMessage;
	@ConfigurableElement("For users in 'nstChattingWithSomeone', if matched, sends a private reply to the active chat partner. Should match 1 parameter: the message")
	public static String[] CHATtrgLocalSendPrivateReply;
	
	// hangman
	@ConfigurableElement({"HANGMAN module", "##############", "", "If matched, immediatly start playing with a bot or with a human who timed out waiting for a response to play with another human opponent"})
	public static String[] HANGMANtrgPlayWithRandomUserOrBot;
	@ConfigurableElement("If matched, starts the invitation for a hangman match process. Should capture 1 parameter: the phone number or nickname of the desired opponent")
	public static String[] HANGMANtrgGlobalInviteNicknameOrPhoneNumber;
	@ConfigurableElement("When on 'nstEnteringMatchWord', if matched, advances on the 'invite for a hangman match' process by computing the desired word'. Should capture 1 parameter: the desired match word")
	public static String[] HANGMANtrgLocalHoldMatchWord;
	@ConfigurableElement("When on 'nstAnsweringToHangmanMatchInvitation', if matched, accepts the invitation for a hangman match")
	public static String[] HANGMANtrgLocalAcceptMatchInvitation;
	@ConfigurableElement("Same as above, but refuses the match")
	public static String[] HANGMANtrgLocalRefuseMatchInvitation;
	@ConfigurableElement("When on 'nstGuessingWordFromHangmanHumanOpponent', if matched, should capture a letter to be used as a suggestion, in order to advance on the current hangman match")
	public static String[] HANGMANtrgLocalSingleLetterSuggestionForHuman;
	@ConfigurableElement("The same as above, but must capture a word -- if that word wasn't matched by any other game command")
	public static String[] HANGMANtrgLocalWordSuggestionFallbackForHuman;
	@ConfigurableElement("The Same as the two above, but for 'nstGuessingWordFromHangmanBotOpponent' navigation state")
	public static String[] HANGMANtrgLocalSingleLetterSuggestionForBot;
	@ConfigurableElement("...")
	public static String[] HANGMANtrgLocalWordSuggestionFallbackForBot;
	@ConfigurableElement("Recognized patterns, when playing, to end a match with another HUMAN user")
	public static String[] HANGMANtrgLocalEndCurrentHumanMatch;
	@ConfigurableElement("Recognized patterns, when playing, to end a match with a BOT")
	public static String[] HANGMANtrgLocalEndCurrentBotMatch;

	
	// profile geo locator
	
	public enum EGeoLocators {FIXED, MSISDNRegex, LBSPassiveAPI, LBSActiveAPI};
	
	@ConfigurableElement({
		"############",
		"GEO Locators",
		"############",
		"The following fields are used to determine user locations when presenting their profile.",
		"",
		"Specifies which geo location algorithm to use:",
		"  FIXED         -- same as none. No place holder will be added and any location must be hard coded on the phrases by hand;",
		"  MSISDNRegex   -- adds the {{countryStateByMSISDN}} place holder to PROFILE phrases and requires specifying 'PROFILEGeoLocatorCountryStateByMSISDNPatterns';",
		"  LBSPassiveAPI -- adds the {{LBSLocation}} place holder to PROFILE phrases and instructs the system to serve LBS informative notifications from the network;",
		"  LBSActiveAPI  -- adds the {{LBSLocation}} place holder to PROFILE phrases and instructs the system to query, when needed, the LBS information from the network.",
		""})
	public static EGeoLocators PROFILEGeoLocator;
	
	@ConfigurableElement({"Country State MSISDN Patterns", "#############################",
	                      "Those patterns are used to replace {{countryStateByMSISDN}} when building 'PROFILEphrUserProfilePresentation' --",
	                      "Based on the MSISDN, the state or region within the country will be presented for that user, so the profile may present a",
	                      "location information without showing the MSISDN. The format for these patterns is:",
	                      "PROFILEGeoLocatorCountryStateByMSISDNPatterns+=StateName1",
	                      "PROFILEGeoLocatorCountryStateByMSISDNPatterns+=MSISDNPattern1",
	                      "PROFILEGeoLocatorCountryStateByMSISDNPatterns+=...",
	                      "PROFILEGeoLocatorCountryStateByMSISDNPatterns+=UnmatchedMSISDNStateName",})
	public static String[] PROFILEGeoLocatorCountryStateByMSISDNPatterns;
	

//	// navigation states
//	////////////////////
//	
//	@ConfigurableElement("Navigation state used to initiate the first interaction with the application and, also, the state after users subscriptions cancellation")
//	public static EInstantVASCommandTriggers[] BASEnstNewUser;
//	@ConfigurableElement("Navigation state used by registered users. Also the 'main loop' navigation state, to which all other states revert to when they finish their businesses")
//	public static EInstantVASCommandTriggers[] BASEnstExistingUser;
//	@ConfigurableElement("Navigation state used to show the composite help messages, containing command triggers to navigate from here on")
//	public static EInstantVASCommandTriggers[] HELPnstPresentingCompositeHelp;
//	@ConfigurableElement("Navigation state used to implement the double opt-in process")
//	public static EInstantVASCommandTriggers[] SUBSCRIPTIONnstAnsweringDoubleOptin;
//	@ConfigurableElement("Navigation state used to interact with the user when asking for a nickname")
//	public static EInstantVASCommandTriggers[] PROFILEnstRegisteringNickname;
//	@ConfigurableElement("Navigation state used when privately chatting with someone -- allows the user to simply type the message (no need to provide the nickname)")
//	public static EInstantVASCommandTriggers[] CHATnstChattingWithSomeone;
//	@ConfigurableElement("Navigation state part of the invitation process of a human to play a hangman match -- on this state, the user must enter the desired word to be guessed, which will be processed by 'cmdHoldMatchWord'")
//	public static EInstantVASCommandTriggers[] HANGMANnstEnteringMatchWord;
//	@ConfigurableElement("State an invited user gets into after he/she is invited for a match, which is set by 'cmdHoldMatchWord'. The invited user answer will, then, be processed by 'cmdAnswerToInvitation'")
//	public static EInstantVASCommandTriggers[] HANGMANnstAnsweringToHangmanMatchInvitation;
//	@ConfigurableElement("Navigation state that indicates the user is playing a hangman match with a human (as the invited opponent), and his/her role is to guess the word")
//	public static EInstantVASCommandTriggers[] HANGMANnstGuessingWordFromHangmanHumanOpponent;
//	@ConfigurableElement("Navigation state that indicates the user is playing a hangman match with the robot, and his/her hole is to guess the word")
//	public static EInstantVASCommandTriggers[] HANGMANnstGuessingWordFromHangmanBotOpponent;
//	
//	public enum EInstantVASCommandTriggers {
//		// help
//		HELPtrgGlobalStartCompositeHelpDialog,
//		HELPtrgLocalShowNextCompositeHelpMessage,
//		HELPtrgGlobalShowNewUsersFallbackHelp,
//		HELPtrgGlobalShowExistingUsersFallbackHelp,
//		HELPtrgGlobalShowStatelessHelpMessage,
//		HELPtrgGlobalShowStatefulHelpMessage,
//		HELPtrgGlobalShowStatefulHelpMessageFallback
//		// subscription
//		SUBSCRIPTIONtrgLocalStartDoubleOptin,
//		SUBSCRIPTIONtrgLocalAcceptDoubleOptin,
//		SUBSCRIPTIONtrgLocalRefuseDoubleOptin,
//		SUBSCRIPTIONtrgGlobalUnsubscribe,
//		// profile
//		PROFILEtrgGlobalStartAskForNicknameDialog,
//		PROFILEtrgLocalNicknameDialogCancelation,
//		PROFILEtrgLocalRegisterNickname,
//		PROFILEtrgGlobalRegisterNickname,
//		PROFILEtrgGlobalShowUserProfile,
//		// chat
//		CHATtrgGlobalSendPrivateMessage,
//		CHATtrgLocalSendPrivateReply,
//		// hangman
//		HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
//		HANGMANtrgLocalHoldMatchWord,
//		HANGMANtrgLocalAcceptMatchInvitation,
//		HANGMANtrgLocalRefuseMatchInvitation,
//		HANGMANtrgLocalNewLetterOrWordSuggestionForHuman***REFACTORED*PLEASE*COPY*THE*CODE*FROM*LICENSE*CLASS,
//		HANGMANtrgLocalNewLetterOrWordSuggestionForBot;
//		
//		public String[] getCommandTriggerPatterns() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
//			return (String[])InstantVASInstanceConfiguration.class.getField(name()).get(null);
//		}
//		
//		/** Converts a navigation state's command & triggers set based on an 'EInstantVASCommandTriggers' array to a string based representation */
//		public static String[][] get2DStringArrayFromEInstantVASCommandTriggersArray(EInstantVASCommandTriggers[] navigationStateCommandTriggers) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
//			String[][] stringArrayNavigationStateTriggers = new String[navigationStateCommandTriggers.length][];
//			for (int i=0; i<stringArrayNavigationStateTriggers.length; i++) {
//				stringArrayNavigationStateTriggers[i] = navigationStateCommandTriggers[i].getCommandTriggerPatterns();
//			}
//			return stringArrayNavigationStateTriggers;
//		}
//	};
	
	// INSTANCE VARIABLES
	/////////////////////
	
	// integration
	public SubscriptionEngine subscriptionEngine;
	public SMSInParser<Map<String, String>, byte[]>  moParser;
	public SMSOutSender                              mtSender;
	
	// event links
	public IEventLink<EInstantVASEvents>  MOpcLink;	// "MO received"      producer/consumer event link
	public IEventLink<EInstantVASEvents>  MTpcLink;	// "MT ready to send" ...
	public IEventLink<EInstantVASEvents>  SRpcLink;	// "Subscription Renewal" producer/consumer event link 
	public IEventLink<EInstantVASEvents>  SCpcLink;	// "Subscription Cancellation" producer/consumer event link 
	
	// DALs
	public SMSAppModuleDALFactory             baseModuleDAL    = null;
	public SMSAppModuleDALFactorySubscription subscriptionDAL  = null;
	public SMSAppModuleDALFactoryProfile      profileModuleDAL = null;
	public SMSAppModuleDALFactoryChat         chatModuleDAL    = null;
	public SMSAppModuleDALFactoryHangman      hangmanModuleDAL = null;
	
	// module instances
	public SMSAppModuleNavigationStates             baseStates               = null;
	public SMSAppModuleNavigationStatesHelp         helpStates               = null;
	public SMSAppModuleCommandsHelp                 helpCommands             = null;
	public SMSAppModulePhrasingsHelp                helpPhrasings            = null;
	public SMSAppModuleNavigationStatesSubscription subscriptionStates       = null;
	public SMSAppModuleCommandsSubscription         subscriptionCommands     = null;
	public SMSAppModulePhrasingsSubscription        subscriptionPhrasings    = null;
	public SMSAppModuleEventsSubscription           subscriptionEventsServer = null;
	public SMSAppModuleNavigationStatesProfile      profileStates            = null; 
	public SMSAppModuleCommandsProfile              profileCommands          = null;
	public SMSAppModulePhrasingsProfile             profilePhrasings         = null;
	public SMSAppModuleNavigationStatesChat         chatStates               = null;
	public SMSAppModuleCommandsChat                 chatCommands             = null;
	public SMSAppModulePhrasingsChat                chatPhrasings            = null;
	public SMSAppModuleNavigationStatesHangman      hangmanStates            = null;
	public SMSAppModuleCommandsHangman              hangmanCommands          = null;
	public SMSAppModulePhrasingsHangman             hangmanPhrasings         = null;
	
	public final NavigationState[][]    modulesNavigationStates;
	public final ICommandProcessor[][]  modulesCommandProcessors;

	
	public InstantVASInstanceConfiguration() throws SQLException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		
		IInstrumentationHandler logHandler;
		IInstrumentationHandler reportHandler;
		IInstrumentationHandler profileHandler;
		
		// pre-instrumentation instantiation error messages
		///////////////////////////////////////////////////
		if (APP_NAME == null) {
			System.out.println("Configuration ERROR: 'APP_NAME' seems not to be defined! Configuration file is likely to be invalid");
		}
		if (MINIMUM_LOG_SEVERITY == null) {
			System.out.println("Configuration ERROR: 'MINIMUM_LOG_SEVERITY' seems not to be defined! Configuration is likely to be invalid");
		}
		
		switch (LOG_STRATEGY) {
		case CONSOLE:
			logHandler = new InstrumentationHandlerLogConsole(APP_NAME, MINIMUM_LOG_SEVERITY);
			break;
		default:
			throw new RuntimeException("LOG_STRATEGY '"+LOG_STRATEGY+"' isn't implemented yet");
		}
		
		reportHandler = logHandler;
		profileHandler = new MOAndMTProfileInstrumentationHandler();
		
		Instrumentation.configureDefaultValuesForNewInstances(logHandler, reportHandler, profileHandler);

		// log to the right destination any previously instrumented events (mainly consisting of the configuration loading process)
		InstantVASConfigurationLoader.purgeTemporaryLog(logHandler);
				
		Instrumentation.reportDebug("### Configuring HTTPClientAdapter...");
		HTTPClientAdapter.configureDefaultValuesForNewInstances(HTTP_CONNECTION_TIMEOUT_MILLIS, HTTP_READ_TIMEOUT_MILLIS, false, "User-Agent", "InstantVAS.com integration client");
		
		List<EInstantVASModules> enabledModulesList = Arrays.asList(ENABLED_MODULES);
		
		// configure modules dal
		switch (DATA_ACCESS_LAYER) {
		case POSTGRESQL:
			Instrumentation.reportDebug("### Configuring PostgreSQLAdapter...");
			PostgreSQLAdapter.configureDefaultValuesForNewInstances(POSTGRESQL_CONNECTION_PROPERTIES, NUMBER_OF_CONCURRENT_CONNECTIONS);
			Instrumentation.reportDebug("### Configuring modules DALs: ");
			for (EInstantVASModules module : EInstantVASModules.values()) {
				if (!enabledModulesList.contains(module)) {
					continue;
				}
				Instrumentation.reportDebug("  "+module.name().toLowerCase()+",");
				switch (module) {
				case CELLTICK_BR_INTEGRATION:
				case CELLTICK_JUNIT_TESTS_INTEGRATION:
					break;
				case BASE:
					baseModuleDAL = SMSAppModuleDALFactory.POSTGRESQL;
					SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
					baseModuleDAL.checkDataAccessLayers();
					break;
				case HELP:
					break;
				case SUBSCRIPTION:
					subscriptionDAL = SMSAppModuleDALFactorySubscription.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterSubscription.configureDefaultValuesForNewInstances(POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
					subscriptionDAL.checkDataAccessLayers();
					break;
				case PROFILE:
					profileModuleDAL = SMSAppModuleDALFactoryProfile.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterProfile.configureDefaultValuesForNewInstances(POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD,
					                                                                           MOSMSesQueueDataBureau.MO_TABLE_NAME,
					                                                                           MOSMSesQueueDataBureau.MO_ID_FIELD_NAME,
					                                                                           /*MOSMSesQueueDataBureau.MO_PHONE_FIELD_NAME*/ "phone");
					profileModuleDAL.checkDataAccessLayers();
					break;
				case CHAT:
					chatModuleDAL = SMSAppModuleDALFactoryChat.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterChat.configureDefaultValuesForNewInstances(POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD,
					                                                                        MOSMSesQueueDataBureau.MO_TABLE_NAME,
					                                                                        MOSMSesQueueDataBureau.MO_ID_FIELD_NAME,
					                                                                        MOSMSesQueueDataBureau.MO_TEXT_FIELD_NAME);
					chatModuleDAL.checkDataAccessLayers();
					break;
				case HANGMAN:
					hangmanModuleDAL = SMSAppModuleDALFactoryHangman.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterHangman.configureDefaultValuesForNewInstances(POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
					hangmanModuleDAL.checkDataAccessLayers();
					break;
				default:
					throw new RuntimeException("InstantVAS Module '"+module+"' isn't present");
				}
			}
			break;
		case RAM:
			baseModuleDAL    = SMSAppModuleDALFactory            .RAM;
			subscriptionDAL  = SMSAppModuleDALFactorySubscription.RAM;
			profileModuleDAL = SMSAppModuleDALFactoryProfile     .RAM;
			chatModuleDAL    = SMSAppModuleDALFactoryChat        .RAM;
			hangmanModuleDAL = SMSAppModuleDALFactoryHangman     .RAM;
			break;
		default:
			throw new RuntimeException("InstantVAS Modules DAL '"+DATA_ACCESS_LAYER+"' is not implemented");
		}
		
		// configure event processing strategies
		Class<? extends Annotation>[] eventProcessingAnnotationClasses = (Class<? extends Annotation>[]) new Class<?>[] {InstantVASEvent.class};
		Instrumentation.reportDebug("### Configuring 'MO arrived' event processing strategy: ");
		switch (MO_PROCESSING_STRATEGY) {
		case POSTGRESQL:
			Instrumentation.reportDebug("  PostgreSQL Queue...");
			QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
			PostgreSQLQueueEventLink.configureDefaultValuesForNewInstances(MO_POSTGRESQL_QUEUE_POOLING_TIME, MO_QUEUE_NUMBER_OF_WORKER_THREADS);
			MOpcLink = new PostgreSQLQueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, MOSMSesQueueDataBureau.MO_TABLE_NAME, new MOSMSesQueueDataBureau(SHORT_CODE));
			break;
		case RAM:
			Instrumentation.reportDebug("  RAM Queue...");
			MOpcLink = new QueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, MO_RAM_QUEUE_CAPACITY, MO_QUEUE_NUMBER_OF_WORKER_THREADS);
			break;
		case DIRECT:
			Instrumentation.reportDebug("  Direct...");
			MOpcLink = new DirectEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses);
			break;
		default:
			throw new RuntimeException("InstantVAS 'MO arrived' Event Processing Strategy '"+MO_PROCESSING_STRATEGY+"' is not implemented");
		}
		Instrumentation.reportDebug("### Configuring 'MT ready for delivert' event processing strategy: ");
		switch (MO_PROCESSING_STRATEGY) {
		case POSTGRESQL:
			Instrumentation.reportDebug("  PostgreSQL Queue...");
			QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
			PostgreSQLQueueEventLink.configureDefaultValuesForNewInstances(MT_POSTGRESQL_QUEUE_POOLING_TIME, MT_QUEUE_NUMBER_OF_WORKER_THREADS);
			MTpcLink = new PostgreSQLQueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, MTSMSesQueueDataBureau.MT_TABLE_NAME, new MTSMSesQueueDataBureau());
			break;
		case RAM:
			Instrumentation.reportDebug("  RAM Queue...");
			MTpcLink = new QueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, MT_RAM_QUEUE_CAPACITY, MT_QUEUE_NUMBER_OF_WORKER_THREADS);
			break;
		case DIRECT:
			Instrumentation.reportDebug("  Direct...");
			MTpcLink = new DirectEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses);
			break;
		default:
			throw new RuntimeException("InstantVAS 'MT ready for delivery' Event Processing Strategy '"+MT_PROCESSING_STRATEGY+"' is not implemented");
		}
		Instrumentation.reportDebug("### Configuring 'Subscription Renewal' event processing strategy:");
		switch (SUBSCRIPTION_RENEWAL_PROCESSING_STRATEGY) {
		case POSTGRESQL:
//			System.out.println("PostgreSQL Queue...");
//			QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(log, POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
//			PostgreSQLQueueEventLink.configureDefaultValuesForNewInstances(log, MT_POSTGRESQL_QUEUE_POOLING_TIME, MT_QUEUE_NUMBER_OF_WORKER_THREADS);
//			SRpcLink = new PostgreSQLQueueEventLink<EInstantVASEvents>(
//				EInstantVASEvents.class, eventProcessingAnnotationClasses, MTSMSesQueueDataBureau.MT_TABLE_NAME, new MTSMSesQueueDataBureau());
//			break;
		default:
			throw new RuntimeException("InstantVAS 'Subscription Renewal' Event Processing Strategy '"+SUBSCRIPTION_RENEWAL_PROCESSING_STRATEGY+"' is not implemented");
		case RAM:
			Instrumentation.reportDebug("  RAM Queue...");
			SRpcLink = new QueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, SR_RAM_QUEUE_CAPACITY, SR_QUEUE_NUMBER_OF_WORKER_THREADS);
			break;
		case DIRECT:
			Instrumentation.reportDebug("  Direct...");
			SRpcLink = new DirectEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses);
			break;
		}
		Instrumentation.reportDebug("### Configuring 'Subscription Cancellation' event processing strategy:");
		switch (SUBSCRIPTION_CANCELLATION_PROCESSING_STRATEGY) {
		case POSTGRESQL:
//			System.out.println("PostgreSQL Queue...");
//			QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(log, POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
//			PostgreSQLQueueEventLink.configureDefaultValuesForNewInstances(log, MT_POSTGRESQL_QUEUE_POOLING_TIME, MT_QUEUE_NUMBER_OF_WORKER_THREADS);
//			MTpcLink = new PostgreSQLQueueEventLink<EInstantVASEvents>(
//				EInstantVASEvents.class, eventProcessingAnnotationClasses, MTSMSesQueueDataBureau.MT_TABLE_NAME, new MTSMSesQueueDataBureau());
//			break;
		default:
			throw new RuntimeException("InstantVAS 'Subscription Cancellation' Event Processing Strategy '"+SUBSCRIPTION_CANCELLATION_PROCESSING_STRATEGY+"' is not implemented");
		case RAM:
			Instrumentation.reportDebug("  RAM Queue...");
			SCpcLink = new QueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, SC_RAM_QUEUE_CAPACITY, SC_QUEUE_NUMBER_OF_WORKER_THREADS);
			break;
		case DIRECT:
			Instrumentation.reportDebug("  Direct...");
			SCpcLink = new DirectEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses);
			break;
		}
		
		// configure the SMSProcessor
		Instrumentation.reportDebug("### Configuring the SMS Processor...");
		SMSProcessor.configureDefaultValuesForNewInstances(baseModuleDAL);
		
		Instrumentation.reportDebug("### Instantiating modules: ");
		for (EInstantVASModules module : EInstantVASModules.values()) {
			if (!enabledModulesList.contains(module)) {
				continue;
			}
			Instrumentation.reportDebug("  " + module.name().toLowerCase() + ",");
			switch (module) {
			case CELLTICK_BR_INTEGRATION:
				configureCelltickBRIntegration();
				break;
			case CELLTICK_JUNIT_TESTS_INTEGRATION:
				configureCelltickJUnitTestsIntegration();
				break;
			case BASE:
				Object[] baseModuleInstances = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(baseModuleDAL,
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(BASEnstNewUser),
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(BASEnstExistingUser));
				baseStates = (SMSAppModuleNavigationStates) baseModuleInstances[0];
				break;
			case HELP:
				// apply this temporary fix -- to solve it, some specific named configurations must be created
				HELPphrStatefulHelpMessages  = new String[][] {
					{nstRegisteringNickname,                  PROFILENicknameRegistrationFallbackHelp},
					{nstGuessingWordFromHangmanHumanOpponent, HANGMANphrGuessingWordHelp},
				};
				Object[] helpModuleInstances = SMSAppModuleConfigurationHelp.getHelpModuleInstances(
					SHORT_CODE, APP_NAME, PRICE_TAG,
					HELPphrNewUsersFallback, HELPphrExistingUsersFallback, HELPphrStateless, HELPphrStatefulHelpMessages,
					HELPphrComposite, EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(HELPnstPresentingCompositeHelp));
				helpStates    = (SMSAppModuleNavigationStatesHelp) helpModuleInstances[0];
				helpCommands  = (SMSAppModuleCommandsHelp)         helpModuleInstances[1];
				helpPhrasings = (SMSAppModulePhrasingsHelp)        helpModuleInstances[2];
				break;
			case SUBSCRIPTION:
				Object[] subscriptionModuleInstances = SMSAppModuleConfigurationSubscription.getSubscriptionModuleInstances(SHORT_CODE, APP_NAME, PRICE_TAG,
					SUBSCRIPTIONphrDoubleOptinStart, SUBSCRIPTIONphrDisagreeToSubscribe, SUBSCRIPTIONphrSuccessfullySubscribed, SUBSCRIPTIONphrCouldNotSubscribe,
					SUBSCRIPTIONphrUserRequestedUnsubscription, SUBSCRIPTIONphrLifecycleUnsubscription,
					baseModuleDAL, subscriptionDAL, subscriptionEngine,
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(SUBSCRIPTIONnstAnsweringDoubleOptin));
				subscriptionStates       = (SMSAppModuleNavigationStatesSubscription) subscriptionModuleInstances[0];
				subscriptionCommands     = (SMSAppModuleCommandsSubscription)         subscriptionModuleInstances[1];
				subscriptionPhrasings    = (SMSAppModulePhrasingsSubscription)        subscriptionModuleInstances[2];
				subscriptionEventsServer = (SMSAppModuleEventsSubscription)           subscriptionModuleInstances[3];
				break;
			case PROFILE:
				// determines which geo locator to use
				IGeoLocatorPlaceHolder userGeoLocatorPlugin;
				switch (PROFILEGeoLocator) {
				case MSISDNRegex:
					if ((PROFILEGeoLocatorCountryStateByMSISDNPatterns == null) || (PROFILEGeoLocatorCountryStateByMSISDNPatterns.length == 0)) {
						Instrumentation.reportDebug("WARNING: when setting 'PROFILEGeoLocator' to 'MSISDNRegex', 'PROFILEGeoLocatorCountryStateByMSISDNPatterns' must also be defined");
					}
					userGeoLocatorPlugin = new UserGeoLocator.CountryStateByMSISDNResolver(PROFILEGeoLocatorCountryStateByMSISDNPatterns);
					break;
				default:
					throw new RuntimeException("GeoLocator '"+PROFILEGeoLocator+"' isn't implemented yet");
				}
				Object[] profileModuleInstances = SMSAppModuleConfigurationProfile.getProfileModuleInstances(SHORT_CODE, APP_NAME,
					PROFILEphrAskForFirstNickname, PROFILEphrAskForNewNickname, PROFILEphrAskForNicknameCancelation,
					PROFILEphrNicknameRegistrationNotification, PROFILEphrUserProfilePresentation, PROFILEphrNicknameNotFound,
					PROFILEphrShortProfilePresentation, PROFILEphrProfileList, PROFILEphrNoMoreProfiles,
					userGeoLocatorPlugin, profileModuleDAL,
					new String[] {nstNewUser, nstAnsweringToHangmanMatchInvitation, nstAnsweringDoubleOptin, nstEnteringMatchWord,
						          nstGuessingWordFromHangmanBotOpponent, nstGuessingWordFromHangmanHumanOpponent, nstRegisteringNickname},
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(PROFILEnstRegisteringNickname),
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(PROFILEnstListingProfilesTriggers));
				profileStates    = (SMSAppModuleNavigationStatesProfile) profileModuleInstances[0]; 
				profileCommands  = (SMSAppModuleCommandsProfile)         profileModuleInstances[1];
				profilePhrasings = (SMSAppModulePhrasingsProfile)        profileModuleInstances[2];
				break;
			case CHAT:
				Object[] chatModuleInstances = SMSAppModuleConfigurationChat.getChatModuleInstances(SHORT_CODE, APP_NAME,
					profilePhrasings,
					CHATphrPrivateMessage, CHATphrPrivateMessageDeliveryNotification, CHATphrDoNotKnowWhoYouAreChattingTo,
					profileModuleDAL, chatModuleDAL,
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(CHATnstChattingWithSomeone));
				chatStates    = (SMSAppModuleNavigationStatesChat) chatModuleInstances[0];
				chatCommands  = (SMSAppModuleCommandsChat)         chatModuleInstances[1];
				chatPhrasings = (SMSAppModulePhrasingsChat)        chatModuleInstances[2];
				break;
			case HANGMAN:
				Object[] hangmanModuleInstances = SMSAppModuleConfigurationHangman.getHangmanModuleInstances(SHORT_CODE, APP_NAME,
					HANGMANwinningArt, HANGMANlosingArt, HANGMANheadCharacter, HANGMANleftArmCharacter, HANGMANchestCharacter,
					HANGMANrightArmCharacter, HANGMANleftLegCharacter, HANGMANrightLegCharacter, 
					HANGMANphr_gallowsArt, HANGMANphrAskOpponentNicknameOrPhone, HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation,
					HANGMANphrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation, HANGMANphrInvitationResponseForInvitingPlayer,                      
					HANGMANphrInvitationNotificationForInvitedPlayer, HANGMANphrTimeoutNotificationForInvitingPlayer,                     
					HANGMANphrInvitationRefusalResponseForInvitedPlayer, HANGMANphrInvitationRefusalNotificationForInvitingPlayer,           
					HANGMANphrNotAGoodWord, HANGMANphrWordProvidingPlayerMatchStart, HANGMANphrWordGuessingPlayerMatchStart, HANGMANphrWordProvidingPlayerStatus,                                
					HANGMANphrWordGuessingPlayerStatus, HANGMANphrWinningMessageForWordGuessingPlayer, HANGMANphrWinningMessageForWordProvidingPlayer,                     
					HANGMANphrLosingMessageForWordGuessingPlayer, HANGMANphrLosingMessageForWordProvidingPlayer, HANGMANphrMatchGiveupNotificationForWordGuessingPlayer,             
					HANGMANphrMatchGiveupNotificationForWordProvidingPlayer,
					subscriptionEventsServer, baseModuleDAL, profileModuleDAL, hangmanModuleDAL,
					DEFAULT_NICKNAME_PREFIX, BOT_PHONE_NUMBERS, BOT_WORDS,
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(HANGMANnstEnteringMatchWord),
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(HANGMANnstAnsweringToHangmanMatchInvitation),
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(HANGMANnstGuessingWordFromHangmanHumanOpponent),
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(HANGMANnstGuessingWordFromHangmanBotOpponent));
				hangmanStates    = (SMSAppModuleNavigationStatesHangman) hangmanModuleInstances[0];
				hangmanCommands  = (SMSAppModuleCommandsHangman)         hangmanModuleInstances[1];
				hangmanPhrasings = (SMSAppModulePhrasingsHangman)        hangmanModuleInstances[2];
				break;
			default:
				throw new RuntimeException("InstantVAS Module '"+module+"' isn't present");
			}
		}
		
		// keep track of the loaded module's navigation states...
		modulesNavigationStates = new NavigationState[][] {
			baseStates.values,
			helpStates.values,
			subscriptionStates.values,
			profileStates.values,
			chatStates.values,
			hangmanStates.values,
		};
		// and commands
		modulesCommandProcessors = new ICommandProcessor[][] {
			helpCommands.values,
			subscriptionCommands.values,
			profileCommands.values,
			chatCommands.values,
			hangmanCommands.values,
		};
	}
	
	/** Gets an array containing the elements {a1, a2[0], ..., a2[n]} */
	private static String[] getConcatenationOf(String a1, String... a2) {
		String[] combinedElements = new String[1 + a2.length];
		combinedElements[0] = a1;
		System.arraycopy(a2, 0, combinedElements, 1, a2.length);
		return combinedElements;
	}
	
	private static String[] getConcatenationOf(String[] a1, String... a2) {
		String[] combinedElements = new String[a1.length + a2.length];
		System.arraycopy(a1, 0, combinedElements, 0, a1.length);
		System.arraycopy(a2, 0, combinedElements, a1.length, a2.length);
		return combinedElements;
	}
	
	/** This one might be used for piracy control if APP_NAME, SHORT_CODE, etc becomes hardcoded, read from an encypted file, read from InstantVAS.com or something like that */
	private void configureCelltickBRIntegration() {
		moParser           = new SMSInCelltick(APP_NAME);
		mtSender           = new SMSOutCelltick(APP_NAME, SHORT_CODE, KANNEL_MT_SMSC, MT_SERVICE_URL,
		                                        MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS, MT_SERVICE_DELAY_BETWEEN_ATTEMPTS);
		subscriptionEngine = new CelltickLiveScreenSubscriptionAPI(LIFECYCLE_SERVICE_BASE_URL, LIFECYCLE_CHANNEL_NAME);
	}
	
	private void configureCelltickJUnitTestsIntegration() {
		moParser = null;
		mtSender = null;
		subscriptionEngine = new TestableSubscriptionAPI("HangmanTests");
	}
	
	public static void setHangmanTestDefaults() {
		setHangmanProductionDefaults();
		ENABLED_MODULES[0] = EInstantVASModules.CELLTICK_JUNIT_TESTS_INTEGRATION;
		POSTGRESQL_SHOULD_DEBUG_QUERIES     = false;
		LIFECYCLE_SERVICE_BASE_URL          = "http://test.InstantVAS.com/CelltickSubscriptions.php";
		MT_SERVICE_URL                      = "http://test.InstantVAS.com/CelltickMTs.php";
//		PROFILEGeoLocatorCountryStateByMSISDNPatterns = new String[] {
//			"JR",
//			".*",
//			"?JR?",
//		};
	}
	

	/** Set the default configuration for the Hangman SMS Application.
	 *  This function might be used to control piracy if it receives a parameter like "client" or "environment" --
	 *  which would fill in piracy protection variables for CELLTICK_BR or CELLTICK_TEST */
	public static void setHangmanProductionDefaults() {
		
		REPORT_DATA_COLLECTOR_STRATEGY = EReportEventHandler.CONSOLE;
		LOG_STRATEGY                   = ELogEventHandler.CONSOLE;
		MINIMUM_LOG_SEVERITY           = ELogSeverity.DEBUG;
		LOG_HANGMAN_FILE_PATH          = "";
		LOG_WEBAPP_FILE_PATH           = "";
		APP_NAME                       = "HANGMAN";
		SHORT_CODE                     = "993";
		PRICE_TAG                      = "$0.99";
		DEFAULT_NICKNAME_PREFIX        = "Guest";
		BOT_WORDS                      = new String[] {"CHIMPANZEE", "AGREGATE", "TWEEZERS"};
		
		HTTP_CONNECTION_TIMEOUT_MILLIS = 30000;
		HTTP_READ_TIMEOUT_MILLIS       = 30000;
		
		LIFECYCLE_SERVICE_BASE_URL          = "http://test.InstantVAS.com/CelltickSubscriptions.php"; //"http://localhost:8082/celltick/wapAPI";
		LIFECYCLE_CHANNEL_NAME              = "HangMan";
		MT_SERVICE_URL                      = "http://test.InstantVAS.com/CelltickMTs.php"; //"http://localhost:15001/cgi-bin/sendsms";
		MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS = 5;
		MT_SERVICE_DELAY_BETWEEN_ATTEMPTS   = 5000;

		DATA_ACCESS_LAYER                           = EInstantVASDAL.POSTGRESQL;
		POSTGRESQL_HOSTNAME                         = "::1";
		POSTGRESQL_PORT                             = 5432;
		POSTGRESQL_DATABASE                         = "hangman";
		POSTGRESQL_USER                             = "hangman";
		POSTGRESQL_PASSWORD                         = "hangman";
		POSTGRESQL_CONNECTION_PROPERTIES            = PostgreSQLAdapter.CONNECTION_PROPERTIES;
		NUMBER_OF_CONCURRENT_CONNECTIONS            = PostgreSQLAdapter.CONNECTION_POOL_SIZE;
		POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS = true;
		POSTGRESQL_SHOULD_DEBUG_QUERIES             = false;
		
		MO_PROCESSING_STRATEGY            = EEventProcessingStrategy.POSTGRESQL;
		MO_RAM_QUEUE_CAPACITY             = 1000;
		MO_FILE_QUEUE_LOG_DIRECTORY       = "";
		MO_FILE_QUEUE_POOLING_TIME        = 0;
		MO_POSTGRESQL_QUEUE_POOLING_TIME  = 0;
		MO_QUEUE_NUMBER_OF_WORKER_THREADS = 5;
		
		MT_PROCESSING_STRATEGY            = EEventProcessingStrategy.POSTGRESQL;
		MT_RAM_QUEUE_CAPACITY             = 1000;
		MT_FILE_QUEUE_LOG_DIRECTORY       = "";
		MT_FILE_QUEUE_POOLING_TIME        = 0;
		MT_POSTGRESQL_QUEUE_POOLING_TIME  = 0;
		MT_QUEUE_NUMBER_OF_WORKER_THREADS = 10;
		
		SUBSCRIPTION_RENEWAL_PROCESSING_STRATEGY = EEventProcessingStrategy.RAM;
		SR_RAM_QUEUE_CAPACITY                    = 1000;
		SR_FILE_QUEUE_LOG_DIRECTORY              = "";
		SR_FILE_QUEUE_POOLING_TIME               = 0;
		SR_POSTGRESQL_QUEUE_POOLING_TIME         = 0;
		SR_QUEUE_NUMBER_OF_WORKER_THREADS        = 2;
		
		SUBSCRIPTION_CANCELLATION_PROCESSING_STRATEGY = EEventProcessingStrategy.RAM;
		SC_RAM_QUEUE_CAPACITY                         = 1000;
		SC_FILE_QUEUE_LOG_DIRECTORY                   = "";
		SC_FILE_QUEUE_POOLING_TIME                    = 0;
		SC_POSTGRESQL_QUEUE_POOLING_TIME              = 0;
		SC_QUEUE_NUMBER_OF_WORKER_THREADS             = 2;
		
//		ENABLED_MODULES = new EInstantVASModules[] {
//			EInstantVASModules.CELLTICK_BR_INTEGRATION,
//			EInstantVASModules.BASE,
//			EInstantVASModules.HELP,
//			EInstantVASModules.SUBSCRIPTION,
//			EInstantVASModules.PROFILE,
//			EInstantVASModules.CHAT,
//			EInstantVASModules.HANGMAN
//		};
//		
//		ALLOWABLE_MSISDN_MIN_LENGTH = -1;
//		ALLOWABLE_MSISDN_MAX_LENGTH = -1;
//		ALLOWABLE_MSISDN_PREFIXES   = null;
//		ALLOWABLE_CARRIERS          = null;
//		ALLOWABLE_SHORT_CODES       = null;


		// Help
		///////
		
		// phrasing
		HELPphrNewUsersFallback      = "### HELPphrNewUsersFallback ###";
		HELPphrExistingUsersFallback = "{{appName}}: Invalid command. Text HELP to see all available commands! a tip: Send PLAY to start a match with a random player.";
		HELPphrStateless             = "You can play the {{appName}} game in 2 ways: guessing someone's word or inviting someone to play with your word " +
		                               "You'll get 1 lucky number each time your subscription is renewed. Whenever you invite a friend or user to play, you win another lucky number " +
		                               "Every week, 1 lucky number is selected to win the prize. Send an option to {{shortCode}}: (P)lay online; (I)nvite a friend or user; (R)anking; (H)elp";
		HELPphrComposite             = new String[] {"Guess the right words using the correct letters. The ones who don't, go straight to the gallows. Now, the commands: PLAY - start a match with the robot; LIST - find online palyers to chat or invite for a match; " +
		                                               "INVITE <nickname> - invite someone to play; NICK <your name> - create your nick. M <nick> <message> to chat. There is More... text HELP again...",
		                                             "This game is for subscribers of the {{appName}} game. {{priceTag}} every week. All messages are free. On every subscription renewal, you'll get a lucky number to compete for prizes! " +
		                                               "See + http://www.canaispremiados.com.  Got tired of all of it? Send LEAVE. When you want to come back, just text HANGMAN to {{shortCode}}."};
		// HELPphrStatefulHelpMessages  is defined at the end of all phrases
		
		// command patterns
		HELPtrgGlobalStartCompositeHelpDialog        = getConcatenationOf(cmdStartCompositeHelpDialog,      /*trgGlobalStartCompositeHelpDialog*/      "[^A-Z0-9]*HE?L?P?[^A-Z0-9]*");
		HELPtrgLocalShowNextCompositeHelpMessage     = getConcatenationOf(cmdShowNextCompositeHelpMessage,  /*trgLocalShowNextCompositeHelpMessage*/   "[^A-Z0-9]*HE?L?P?[^A-Z0-9]*|[^A-Z0-9]*MO?R?E?[^A-Z0-9]*", "[^A-Z0-9]*\\+.*");
		HELPtrgGlobalShowNewUsersFallbackHelp        = getConcatenationOf(cmdShowNewUsersFallbackHelp,                                                 ".*");
		HELPtrgGlobalShowExistingUsersFallbackHelp   = getConcatenationOf(cmdShowExistingUsersFallbackHelp, /*trgGlobalShowExistingUsersFallbackHelp*/ ".*");
		HELPtrgGlobalShowStatelessHelpMessage        = getConcatenationOf(cmdShowStatelessHelp,             /*trgGlobalShowStatelessHelpMessage*/      "[^A-Z0-9]*TIP[^A-Z0-9]*");
		HELPtrgGlobalShowStatefulHelpMessage         = getConcatenationOf(cmdShowStatefulHelp,                                                         "[^A-Z0-9]*HE?L?P?[^A-Z0-9]*");
		HELPtrgGlobalShowStatefulHelpMessageFallback = getConcatenationOf(cmdShowStatefulHelp,                                                         "(.*)");
		
//		// stateful help messages
//		setStatefulHelpMessages(new Object[][] {
//			{nstNewUser,                              "fallback help message for new users"},
//			{nstExistingUser,                         "fallback help message for existing users"},
//			{nstAnsweringDoubleOptin,                 "fallback help message when answering double opt-in"},
//			{nstRegisteringNickname,                  "fallback help message when registering a nickname"},
//			{nstChattingWithSomeone,                  "help message when statefully chatting with someone"},
//			{nstGuessingWordFromHangmanHumanOpponent, SMSAppModulePhrasingsHangman.getGuessingWordHelp()},
//		});
				
		// Subscription
		///////////////
		
		// phrasing
		SUBSCRIPTIONphrDoubleOptinStart            = "Hi! You are at the {{appName}} game!! To play and meet new friends, you need to be a subscriber. Please answer to this message with the word HANGMAN to start. Only {{priceTag}} per week. Have fun!"; 
		SUBSCRIPTIONphrDisagreeToSubscribe         = "{{appName}}: All right! You asked not to be subscribed. Whenever you change your mind, just text HANGMAN to {{shortCode}}. In the mean time, you may consult, completely free, the list of players - Text LIST to {{shortCode}}. Send HELP to learn how it works.";
		SUBSCRIPTIONphrSuccessfullySubscribed      = "Hi! You are at the {{appName}} game!! To play with a random player, text PLAY; To register a nickname and be able to chat, text NICK <your name>; To play or chat with a specific member, text LIST to see the list of online players. You can always send HELP to see the rules and other commands.";
		SUBSCRIPTIONphrCouldNotSubscribe           = "{{appName}}: Oops, we were not able to subscribe you at the moment. Please, try again later. In the mean time, you may text LIST to see the online players or HELP to learn about the game.";
		SUBSCRIPTIONphrUserRequestedUnsubscription = "OK, you asked to leave, so you're out - you won't be able to play the {{appName}} anymore, nor receive chat or game messages, nor lucky numbers to win prizes... Changed your mind? Nice! Text HANGMAN to {{shortCode}} - {{priceTag}} a week.";
		SUBSCRIPTIONphrLifecycleUnsubscription     = "### SUBSCRIPTIONphrLifecycleUnsubscription ###";
		
		// command patterns
		SUBSCRIPTIONtrgLocalStartDoubleOptin   = getConcatenationOf(cmdStartDoubleOptinProcess,                               ".*");
		SUBSCRIPTIONtrgLocalAcceptDoubleOptin  = getConcatenationOf(cmdSubscribe,                                             ".*HA[NM]GM?A?[NM]?.*");
		SUBSCRIPTIONtrgLocalRefuseDoubleOptin  = getConcatenationOf(cmdDoNotAgreeToSubscribe,   /*trgLocalRefuseDoubleOptin*/ "[^A-Z0-9]*NO.*");
		SUBSCRIPTIONtrgGlobalUnsubscribe       = getConcatenationOf(cmdUnsubscribe,             /*trgGlobalUnsubscribe*/      "[^A-Z0-9]*CANCEL[^A-Z0-9]*|[^A-Z0-9]*UNSUBSCRIBE[^A-Z0-9]*|[^A-Z0-9]*LEAVE[^A-Z0-9]*|[^A-Z0-9]*QUIT[^A-Z0-9]*|[^A-Z0-9]*EXIT[^A-Z0-9]*");
		
		// Profile
		//////////
		
		// phrasing
		PROFILEphrAskForFirstNickname              = "{{appName}}: Think of a nickname to play and chat with others!! Text now a name with up to 8 letters or numbers, but with no special characters.";
		PROFILEphrAskForNewNickname                = "### PROFILEphrAskForNewNickname ###";
		PROFILEphrAskForNicknameCancelation        = "### PROFILEphrAskForNicknameCancelation ###";
		PROFILEphrNicknameRegistrationNotification = "{{appName}}: Your nickname: {{registeredNickname}}. Text LIST to see online players; NICK [NEW NICK] to change your name again.";
		PROFILEphrUserProfilePresentation          = "{{appName}}: {{nickname}}: Subscribed; Online; {{countryStateByMSISDN}}. Text INVITE {{nickname}} to play a hangman match; M {{nickname}} [MSG] to chat; LIST to see online players; P to play with a random user.";
		PROFILEphrNicknameNotFound                 = "{{appName}}: No player with nickname '{{targetNickname}}' was found. Maybe he/she changed it? Text LIST to see online players";
		PROFILEphrShortProfilePresentation         = "{{nickname}}-{{countryStateByMSISDN}} ";
		PROFILEphrProfileList                      = "{{profilesList}}... You may text MORE if you didn't choose your opponent yet. Then, text INVITE and a nickname. For instance: INVITE Guest1234";
		PROFILEphrNoMoreProfiles                   = "There are no more online players available. Text PLAY to start a game or LIST to query online users again. Text M [nickname] and a message to chat with someone";
		PROFILENicknameRegistrationFallbackHelp    = "Ops! That is not a good nickname. You texted '{{MOText}}'. Please, text now your desired nickname with up to 8 letters or numbers, but with no special characters or space -- or text HELP";

		
		// command patterns
		PROFILEtrgGlobalStartAskForNicknameDialog = getConcatenationOf(cmdStartAskForNicknameDialog,                      "[^A-Z0-9]*NIC?K?[^A-Z0-9]*");
		PROFILEtrgLocalNicknameDialogCancelation  = getConcatenationOf(cmdAskForNicknameDialogCancelation/*,              "---"*/);
		PROFILEtrgLocalRegisterNickname           = getConcatenationOf(cmdRegisterNickname,                               "[^A-Z0-9]*([A-Z0-9]+).*");
		PROFILEtrgGlobalRegisterNickname          = getConcatenationOf(cmdRegisterNickname, /*trgGlobalRegisterNickname*/ "[^A-Z0-9]*NIC?K?N?A?M?E?[^A-Z0-9]+([A-Z0-9]+).*");
		PROFILEtrgGlobalShowUserProfile           = getConcatenationOf(cmdShowUserProfile,  /*trgGlobalShowUserProfile*/  "[^A-Z0-9]*PRO?FILE?[^A-Z0-9]*|[^A-Z0-9]*PRO?FILE?[^A-Z0-9]+([A-Z0-9]+).*");
		PROFILEtrgGlobalListProfiles              = getConcatenationOf(cmdListProfiles,  /*trgGlobalListProfiles*/        "[^A-Z0-9]*LIST[^A-Z0-9]*|[^A-Z0-9]*RA[NM]KI?N?G?[^A-Z0-9]*");
		PROFILEtrgLocalListMoreProfiles           = getConcatenationOf(cmdListMoreProfiles,  /*trgLocalListMoreProfiles*/ "[^A-Z0-9]*MORE?[^A-Z0-9]*|[^A-Z0-9]*LIST[^A-Z0-9]*|[^A-Z0-9]*LIST[^A-Z0-9]*|[^A-Z0-9]*RA[NM]KI?N?G?[^A-Z0-9]*");
		
		// Chat
		///////
		
		// phrasing
		CHATphrPrivateMessage                     = "{{senderNickname}}: {{senderMessage}} - Answer with M {{senderNickname}} [MSG]";
		CHATphrPrivateMessageDeliveryNotification = "{{appName}}: your message has been delivered to {{targetNickname}}. While you wait for the answer, you may LIST online players";
		CHATphrDoNotKnowWhoYouAreChattingTo       = "### CHATphrDoNotKnowWhoYouAreChattingTo ###";
		
		// command patterns
		CHATtrgGlobalSendPrivateMessage = getConcatenationOf(cmdSendPrivateMessage, /*trgGlobalSendPrivateMessage*/ "[^A-Z0-9]*[MP][^A-Z0-9]+([A-Z0-9]+)[^A-Z0-9]+(.*)");
		CHATtrgLocalSendPrivateReply    = getConcatenationOf(cmdSendPrivateReply/*,                                 "---"*/);

		// Hangman
		//////////
		
		// phrasing
		HANGMANwinningArt                                                   = "\\0/\n |\n/ \\\n";
		HANGMANlosingArt                                                    = "+-+\n| x\n|/|\\\n|/ \\\n====\n";
		HANGMANheadCharacter                                                = "O";
		HANGMANleftArmCharacter                                             = "/";
		HANGMANchestCharacter                                               = "|";
		HANGMANrightArmCharacter                                            = "\\";
		HANGMANleftLegCharacter                                             = "/";
		HANGMANrightLegCharacter                                            = "\\";
		HANGMANphr_gallowsArt                                               = "+-+\n| {{head}}\n|{{leftArm}}{{chest}}{{rightArm}}\n|{{leftLeg}} {{rightLeg}}\n|\n====\n";
		HANGMANphrAskOpponentNicknameOrPhone                                = "### HANGMANphrAskOpponentNicknameOrPhone ###{{appName}}: Name registered: {{invitingPlayerNickname}}. Send your friend's phone to {{shortCode}} or LIST to see online players. NICK [NEW NICK] to change your name.";
		HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation = "{{appName}}: Inviting {{opponentNickname}}. Think of a word without special digits and send it now to {{shortCode}}. The most rare words work better for you to win!";
		HANGMANphrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation    = "### HANGMANphrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation ###{{appName}}: Your friend's phone: {{opponentPhoneNumber}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number";
		HANGMANphrInvitationResponseForInvitingPlayer                       = "{{invitedPlayerNickname}} was invited to play with you. Wait for the answer and good luck!";
		HANGMANphrInvitationNotificationForInvitedPlayer                    = "{{appName}}: {{invitingPlayerNickname}} is inviting you for a match. Do you accept? Text YES or NO. You may also text M {{invitingPlayerNickname}} [MSG] to send him/her a message.";
		HANGMANphrTimeoutNotificationForInvitingPlayer                      = "### HANGMANphrTimeoutNotificationForInvitingPlayer ###{{appName}}: {{invitedPlayerNickname}} is taking too long to answer. However, a new player, {{suggestedNewPlayersNickname}}, is available. Play with {{suggestedNewPlayersNickname}}? Send YES to {{shortCode}}";
		HANGMANphrInvitationRefusalResponseForInvitedPlayer                 = "The invitation to play the Hangman Game made by {{invitingPlayerNickname}} was refused. Text LIST to {{shortCode}} to see online users or send him/her a message: text M {{invitingPlayerNickname}} [MSG]";
		HANGMANphrInvitationRefusalNotificationForInvitingPlayer            = "{{invitedPlayerNickname}} refused your invitation to play. Send LIST to pick someone else or send him/her a message: text M {{invitedPlayerNickname}} [MSG]";
		HANGMANphrNotAGoodWord                                              = "You chose '{{word}}'. Humm... this is possily not a good word. Please think of a single word using only A-Z letters, without accents, digits, ponctuation or any other special characters and send it again.";
		HANGMANphrWordProvidingPlayerMatchStart                             = "Game started with {{wordGuessingPlayerNickname}}.\n{{gallowsArt}}Is your word really a hard one? We'll see... While you wait for {{wordGuessingPlayerNickname}} to make his/her first guess, you may text M {{wordGuessingPlayerNickname}} [MSG] to give him/her cues";
		HANGMANphrWordGuessingPlayerMatchStart                              = "{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nAnswer with your first letter, the complete word or ask for cues with M {{wordProvidingPlayerNickname}} [MSG]";
		HANGMANphrWordProvidingPlayerStatus                                 = "Match going on! {{wordGuessingPlayerNickname}} guessed letter {{guessedLetter}}\n{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nWant to chat with him/her? Text M {{wordGuessingPlayerNickname}} [MSG] to provoke him/her";
		HANGMANphrWordGuessingPlayerStatus                                  = "{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nText a letter, the complete word or M {{wordProvidingPlayerNickname}} [MSG]";
		HANGMANphrWinningMessageForWordGuessingPlayer                       = "{{winningArt}}{{word}}! You got it! Keep on playing! Text INVITE {{wordProvidingPlayerNickname}} for a new match with this player or text LIST to see other online players. You may also text P to play with a random user.";
		HANGMANphrWinningMessageForWordProvidingPlayer                      = "{{wordGuessingPlayerNickname}} guessed your word! Want revenge? Text INVITE {{wordGuessingPlayerNickname}}; Want to tease him/her? Text M {{wordGuessingPlayerNickname}} [MSG]";
		HANGMANphrLosingMessageForWordGuessingPlayer                        = "{{losingArt}}Oh, my... you were hanged! The word was {{word}}. Now challenge {{wordProvidingPlayerNickname}} for a revenge: text INVITE {{wordProvidingPlayerNickname}} or tease him/her with M {{wordGuessingPlayerNickname}} [MSG]";
		HANGMANphrLosingMessageForWordProvidingPlayer                       = "Good one! {{wordGuessingPlayerNickname}} wasn't able to guess your word! Say something about it with M {{wordGuessingPlayerNickname}} [MSG] or INVITE {{wordGuessingPlayerNickname}} for a new match, and make it hard when you choose the word!";
		HANGMANphrMatchGiveupNotificationForWordGuessingPlayer              = "Your match with {{wordProvidingPlayerNickname}} has been canceled. Send P {{wordProvidingPlayerNickname}} [MSG] to talk to him/her or LIST to pick another opponent. You may also text PLAY to play with a random user.";
		HANGMANphrMatchGiveupNotificationForWordProvidingPlayer             = "{{wordGuessingPlayerNickname}} cancelled the match. To find other users to play with, text LIST; to play with a random user, text PLAY";
		HANGMANphrGuessingWordHelp                                          = "Ops!! This is not a letter! You are guessing a word on a {{appName}} and you texted '{{MOText}}'. Please text a letter or: END to quit the match; M [nick] [MSG] to ask for cues; LIST to quit this match and see other online players";
		
		// command patterns
		HANGMANtrgPlayWithRandomUserOrBot                = getConcatenationOf(cmdPlayWithRandomUserOrBot,     /*trgPlayWithRandomUserOrBot*/           "[^A-Z0-9]*[HR]A[NM]GM?A?N?[^A-Z0-9]*|[^A-Z0-9]*I[NM]VITE?[^A-Z0-9]*|[^A-Z0-9]*PL[AE][YI][^A-Z0-9]*");
		HANGMANtrgGlobalInviteNicknameOrPhoneNumber      = getConcatenationOf(cmdInviteNicknameOrPhoneNumber, /*trgGlobalInviteNicknameOrPhoneNumber*/ "[^A-Z0-9]*[HR]A[NM]GM?A?N?[^A-Z0-9]+([A-Z0-9]+).*|[^A-Z0-9]*I[NM]VITE?[^A-Z0-9]+([A-Z0-9]+).*|[^A-Z0-9]*PL[AE][YI][^A-Z0-9]+([A-Z0-9]+).*");
		HANGMANtrgLocalHoldMatchWord                     = getConcatenationOf(cmdHoldMatchWord,               /*trgLocalHoldMatchWord*/                "[^A-Z0-9]*([A-Z0-9]+).*");
		HANGMANtrgLocalAcceptMatchInvitation             = getConcatenationOf(cmdAcceptMatchInvitation,       /*trgLocalAcceptMatchInvitation*/        "[^A-Z0-9]*Y[EA]?[SPA]?P?[^A-Z0-9]*|[^A-Z0-9]*SURE?[^A-Z0-9]*|[^A-Z0-9]*OK[^A-Z0-9]*|[^A-Z0-9]*FINE[^A-Z0-9]*|.*GO.*");
		HANGMANtrgLocalRefuseMatchInvitation             = getConcatenationOf(cmdRefuseMatchInvitation,       /*trgLocalRefuseMatchInvitation*/        "[^A-Z0-9]*NO?P?E?[^A-Z0-9]*");
		HANGMANtrgLocalSingleLetterSuggestionForHuman    = getConcatenationOf(cmdSuggestLetterOrWordForHuman, /*trgLocalSingleLetterSuggestion*/       "[^A-Z0-9]*([A-Z])");
		HANGMANtrgLocalWordSuggestionFallbackForHuman    = getConcatenationOf(cmdSuggestLetterOrWordForHuman, /*trgLocalWordSuggestionFallback*/       "[^A-Z0-9]*([A-Z]+)");
		HANGMANtrgLocalSingleLetterSuggestionForBot      = getConcatenationOf(cmdSuggestLetterOrWordForBot,   /*trgLocalSingleLetterSuggestion*/       "[^A-Z0-9]*([A-Z])");
		HANGMANtrgLocalWordSuggestionFallbackForBot      = getConcatenationOf(cmdSuggestLetterOrWordForBot,   /*trgLocalWordSuggestionFallback*/       "[^A-Z0-9]*([A-Z]+)");
		HANGMANtrgLocalEndCurrentHumanMatch              = getConcatenationOf(cmdGiveUpCurrentHumanMatch,     /*trgLocalEndCurrentHumanMatch*/         "[^A-Z0-9]*END[^A-Z0-9]*|[^A-Z0-9]*CANCEL[^A-Z0-9]*|[^A-Z0-9]*QUIT[^A-Z0-9]*");
		HANGMANtrgLocalEndCurrentBotMatch                = getConcatenationOf(cmdGiveUpCurrentBotMatch,       /*trgLocalEndCurrentBotMatch*/           "[^A-Z0-9]*END[^A-Z0-9]*|[^A-Z0-9]*CANCEL[^A-Z0-9]*|[^A-Z0-9]*QUIT[^A-Z0-9]*");

		
		// geo locator
		
		PROFILEGeoLocator = EGeoLocators.MSISDNRegex;		
		// according to 'UserGeoLocator.CountryStateByMSISDNResolver(...)'
		PROFILEGeoLocatorCountryStateByMSISDNPatterns = new String[] {
			"Alabama", "\\+1205.*|\\+125[16].*|\\+1334.*",
			"Alaska",  "\\+1907.*",
			"Arizona", "\\+1480.*|\\+1520.*|\\+6021.*|\\+1623.*|\\+1928.*",
			"(unknown state)",
		};
		
		// stateful help
		////////////////

		// this temporary fix was moved to the constructor while awaiting the real refactor.
//		HELPphrStatefulHelpMessages  = new String[][] {
//			{"GuessingWordFromHangmanHumanOpponent", HANGMANphrGuessingWordHelp},
//		};

		
//		// navigation
//		/////////////
//		
//		/* Hoje em dia já é possível que os cmds sejam chamados de forma a imitar o serviço africano 'iText'. Por exemplo, para o envio de emails,
//		 * o comando cmdSendEmail (que recebe dois parâmetros) pode ser usado, mesmo que se esteja enviando sempre email para uma única pessoa,
//		 * situação na qual a regular expression deve ser trocada de algo como "M (%w+) (.*)" para "M (.*)" e a chamada do comando seria trocada de
//		 * cmdSendEmail("$1", "$2") para cmdSendEmail("luiz@InstantVAS.com", "$1") -- esta forma de chamar ainda tem que ser implementada, na verdade. */
//		
//		// base
//		BASEnstNewUser = new EInstantVASCommandTriggers[] {
//			EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalAcceptDoubleOptin,	// the double opt-in process starts with a broadcast message, outside the scope of this application
//			EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
//			EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalStartDoubleOptin,
//			EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,			// let the user answer to chats
//			EInstantVASCommandTriggers.HELPtrgGlobalShowNewUsersFallbackHelp,	// fallback help
//		};
//		BASEnstExistingUser = new EInstantVASCommandTriggers[] {
//			EInstantVASCommandTriggers.HELPtrgGlobalShowStatelessHelpMessage,
//			EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
//			EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
//			EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
//			EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
//			EInstantVASCommandTriggers.HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
//			EInstantVASCommandTriggers.HELPtrgGlobalShowNewUsersFallbackHelp,
//		};
//
//		// help
//		HELPnstPresentingCompositeHelp = new EInstantVASCommandTriggers[] {
//			EInstantVASCommandTriggers.HELPtrgGlobalStartCompositeHelpDialog,
//			EInstantVASCommandTriggers.HELPtrgLocalShowNextCompositeHelpMessage,
//			EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
//		};
//		
//		// subscription
//		SUBSCRIPTIONnstAnsweringDoubleOptin = new EInstantVASCommandTriggers[] {
//			EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalAcceptDoubleOptin,
//			EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalRefuseDoubleOptin,
//			EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
//			EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalStartDoubleOptin,	// for some of known wrong commands, start the double opt-in process again
//		};
//		
//		// profile
//		PROFILEnstRegisteringNickname = new EInstantVASCommandTriggers[] {
//			EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
//			EInstantVASCommandTriggers.PROFILEtrgGlobalStartAskForNicknameDialog,
//			EInstantVASCommandTriggers.PROFILEtrgLocalNicknameDialogCancelation,
//			EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
//			EInstantVASCommandTriggers.PROFILEtrgLocalRegisterNickname,
//		};
//		
//		// chat
//		CHATnstChattingWithSomeone = new EInstantVASCommandTriggers[] {
//			EInstantVASCommandTriggers.CHATtrgLocalSendPrivateReply,
//		};
//		
//		// hangman
//		HANGMANnstEnteringMatchWord = new EInstantVASCommandTriggers[] {
//			EInstantVASCommandTriggers.HANGMANtrgLocalHoldMatchWord,
//			EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
//		};
//		HANGMANnstAnsweringToHangmanMatchInvitation = new EInstantVASCommandTriggers[] {
//			EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
//			EInstantVASCommandTriggers.HANGMANtrgLocalAcceptMatchInvitation,
//			EInstantVASCommandTriggers.HANGMANtrgLocalRefuseMatchInvitation,
//			EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
//			EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
//		};
//		HANGMANnstGuessingWordFromHangmanHumanOpponent = new EInstantVASCommandTriggers[] {
//			EInstantVASCommandTriggers.HANGMANtrgLocalNewLetterOrWordSuggestionForHuman***REFACTORED*PLEASE*COPY*THE*CODE*FROM*LICENSE*CLASS,
//			EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
//			EInstantVASCommandTriggers.HELPtrgGlobalShowStatefulHelpMessage,
//		};
//		HANGMANnstGuessingWordFromHangmanBotOpponent = new EInstantVASCommandTriggers[] {
//			EInstantVASCommandTriggers.HANGMANtrgLocalNewLetterOrWordSuggestionForBot,
//			EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
//			EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
//		};
	}
	
	/** The same as {@link #setHangmanProductionDefaults()}, but reconfigure phrases and commands for the CLARO BR account */
	public static void setHangmanProductionDefaultsCLARO_BR() {
		
		setHangmanProductionDefaults();

		APP_NAME                = "FORCA PREMIADA";
		PRICE_TAG               = "Rs1,99";
		SHORT_CODE              = "993";
		DEFAULT_NICKNAME_PREFIX = "JOGADOR";
		BOT_WORDS               = new String[] {
			"JARDIM",		"MANTEIGA",		"RELATO",	"GANSO",	"PERRENGUE",	"MANEIRA",	"DRIBLAR",		"JOGATINA", 
			"FORNO", 		"FONTE",		"GRUDE",	"BISCOITO",	"PERFUME",		"GORRO",	"ANDAIME",		"VUVUZELA", 
			"PESQUISA", 	"PARAFUSO",		"ILHOTA",	"CHAVE",	"VIDA",			"TELEFONE",	"MANGA",		"MARGARINA", 
			"EXTREMISTA", 	"SENTIMENTO",	"NORMAL",	"PALAVRA",	"CELULAR",		"FRANGO",	"PASSAPORTE",	"GRAMPO", 
			"PANQUECA", 	"TOMADA",		"PIPOCA",	"PASSE",	"TITUBEAR",		"MORANGO",	"CHACOTA",		"XALE", 
			
		};
		
		// note on the command patterns bellow:
		// the original english commands & patterns are used, having the pt-BR patters added to them.
		
		// Help
		///////
		
		// phrasing
		HELPphrNewUsersFallback      = "Ola! Voce esta no {{appName}}!! Eh preciso ser assinante para jogar. Responda essa msg com a palavra FORCA para comecar. Apenas {{priceTag}} por semana, divirta-se!";
		HELPphrExistingUsersFallback = "{{appName}}: Comando invalido. Envie AJUDA para consultar todos os comandos disponiveis! Uma dica: Envie J para desafiar o robo e treinar bastante antes de entrar nos desafios.";
		HELPphrStateless             = "### Este jogo não usa a ajuda sem estados (somente a com estados). Defina as mensagens em HELPphrComposite ###";
		HELPphrComposite             = new String[] {"Adivinhe a palavra secreta usando as letras certas. Quem nao consegue eh enforcado. Veja aqui os COMANDOS UTEIS: J - jogar contra o robo; LISTA - encontrar jogadores; " +
		                                               "CHAMAR <apelido>, convidar alguem da lista; NICK <seu nome>, criar seu apelido. P <nick>  <mensagem> conversar. Continua... envie AJUDA",
		                                             "Jogo exclusivo para assinantes do {{appName}}. {{priceTag}} por semana, todas as mensagens e jogos sao gratis. A cada renovacao, um numero da sorte para muitos premios! " +
		                                               "Veja + em http://www.canaispremiados.com.  Cansou de tudo e quer sair? Envie FORCAOUT. Se quiser voltar, envie FORCA para {{shortCode}}."};
		// HELPphrStatefulHelpMessages  is defined at the end of all phrases
		
		// command patterns
		HELPtrgGlobalStartCompositeHelpDialog        = getConcatenationOf(HELPtrgGlobalStartCompositeHelpDialog,         "[^A-Z0-9]*AJ?U?D?A?[^A-Z0-9]*");
		HELPtrgLocalShowNextCompositeHelpMessage     = getConcatenationOf(HELPtrgLocalShowNextCompositeHelpMessage,      "[^A-Z0-9]*AJ?U?D?A?[^A-Z0-9]*|[^A-Z0-9]*MA?I?S?[^A-Z0-9]*");
		HELPtrgGlobalShowNewUsersFallbackHelp        = getConcatenationOf(HELPtrgGlobalShowNewUsersFallbackHelp/*,          ".*"*/);
		HELPtrgGlobalShowExistingUsersFallbackHelp   = getConcatenationOf(HELPtrgGlobalShowExistingUsersFallbackHelp/*,     ".*"*/);
		HELPtrgGlobalShowStatelessHelpMessage        = getConcatenationOf(HELPtrgGlobalShowStatelessHelpMessage,         "[^A-Z0-9]*DICA[^A-Z0-9]*");
		HELPtrgGlobalShowStatefulHelpMessage         = getConcatenationOf(HELPtrgGlobalShowStatefulHelpMessage,          "[^A-Z0-9]*AJ?U?D?A?[^A-Z0-9]*");
		HELPtrgGlobalShowStatefulHelpMessageFallback = getConcatenationOf(HELPtrgGlobalShowStatefulHelpMessageFallback/*,   ".*"*/);
		
//		// stateful help messages
//		setStatefulHelpMessages(new Object[][] {
//			{nstNewUser,                              "fallback help message for new users"},
//			{nstExistingUser,                         "fallback help message for existing users"},
//			{nstAnsweringDoubleOptin,                 "fallback help message when answering double opt-in"},
//			{nstRegisteringNickname,                  "fallback help message when registering a nickname"},
//			{nstChattingWithSomeone,                  "help message when statefully chatting with someone"},
//			{nstGuessingWordFromHangmanHumanOpponent, SMSAppModulePhrasingsHangman.getGuessingWordHelp()},
//		});
				
		// Subscription
		///////////////
		
		// phrasing
		SUBSCRIPTIONphrDoubleOptinStart            = "Ola! Voce esta no {{appName}}!! Eh preciso ser assinante para jogar. Responda essa msg com a palavra FORCA para comecar. Apenas {{priceTag}} por semana, divirta-se!"; 
		SUBSCRIPTIONphrDisagreeToSubscribe         = "### A mensagem enviada quando o usuário se recusa a assinar não está definida. Defina-a através de SUBSCRIPTIONphrDisagreeToSubscribe ###";
		SUBSCRIPTIONphrSuccessfullySubscribed      = "Ola! Voce esta no {{appName}}!! Pra desafiar o robo, envie J. Pra criar seu apelido, envie NICK e o apelido que deseja, com espaco. Exemplo: NICK PEDRO. Para ver todos os comandos, envie AJUDA.";
		SUBSCRIPTIONphrCouldNotSubscribe           = "{{appName}}: Oops, nao conseguimos fazer seu registro. Tente novamente mais tarde. Enquanto isso, você pode enviar AJUDA para ver as regras do jogo ou LISTA para consultar os usuários online.";
		SUBSCRIPTIONphrUserRequestedUnsubscription = "OK, voce pediu pra sair, entao esta fora - voce nao podera mais jogar FORCA, receber mensagens do jogo ou jogadores nem recebera numeros da sorte para os premios. Mudou de ideia? Oba!! Envie FORCA pra {{shortCode}}. {{priceTag}} por semana.";
		SUBSCRIPTIONphrLifecycleUnsubscription     = "### A mensagem enviada ao usuário quando ele é desassinado por regras do ciclo de vida não está definida. Defina-a através de SUBSCRIPTIONphrLifecycleUnsubscription ###";
		
		// command patterns
		SUBSCRIPTIONtrgLocalStartDoubleOptin   = getConcatenationOf(SUBSCRIPTIONtrgLocalStartDoubleOptin/*,  ".*"*/);
		SUBSCRIPTIONtrgLocalAcceptDoubleOptin  = getConcatenationOf(SUBSCRIPTIONtrgLocalAcceptDoubleOptin, ".*FOR[CÇç]A.*");
		SUBSCRIPTIONtrgLocalRefuseDoubleOptin  = getConcatenationOf(SUBSCRIPTIONtrgLocalRefuseDoubleOptin/*, ".*N.*"*/);
		SUBSCRIPTIONtrgGlobalUnsubscribe       = getConcatenationOf(SUBSCRIPTIONtrgGlobalUnsubscribe,      "[^A-Z0-9]*SAIR?[^A-Z0-9]*|[^A-Z0-9]*CANCELAR?[^A-Z0-9]*|[^A-Z0-9]*FORCA ?OUT[^A-Z0-9]*");
		
		// Profile
		//////////
		
		// phrasing
		PROFILEphrAskForFirstNickname              = "{{appName}}: Crie um apelido pra jogar e papear com outras pessoas!! Envie agora um nome com ate oito letras, sem acento nem caracteres especiais.";
		PROFILEphrAskForNewNickname                = "### PROFILEphrAskForNewNickname ###";
		PROFILEphrAskForNicknameCancelation        = "### PROFILEphrAskForNicknameCancelation ###";
		PROFILEphrNicknameRegistrationNotification = "{{appName}}: Seu apelido: {{registeredNickname}}. Envie LISTA para para ver os apelidos de outros. Se quiser mudar o seu, apenas envie NICK e seu novo apelido, com espaco entre ambos.";
		PROFILEphrUserProfilePresentation          = "{{appName}}: {{nickname}}: Jogador de {{countryStateByMSISDN}}. Quer desafiar? Envie C {{nickname}} para comecar uma partida. P {{nickname}} [MSG] para mandar uma mensagem. Para ver todos os jogadores disponiveis, envie LISTA.";
		PROFILEphrNicknameNotFound                 = "{{appName}}: Nao encontramos nenhum jogador com o apelido '{{targetNickname}}'. Sera que mudou, ou esta mal digitado? Envie LISTA e veja a lista completa dos jogadores ativos. Caso ainda tenha duvidas, envie AJUDA.";
		PROFILEphrShortProfilePresentation         = "{{nickname}}-{{countryStateByMSISDN}} ";
		PROFILEphrProfileList                      = "{{profilesList}}. Escolheu seu adversario? Envie CHAMAR seguido do apelido. Exemplo: CHAMAR JOGADOR2398. Para ver mais jogadores, envie MAIS";
		PROFILEphrNoMoreProfiles                   = "Nao ha mais jogadores disponiveis. Envie JOGAR p/ iniciar um jogo ou LISTA para rever a lista de jogadores online. Envie P seguido do apelido e a mensagem que quer enviar, sempre com um espaco no meio, para papear com alguem. Exemplo: P JOGADOR2398 Bom dia! Rola uma jogada?";
		PROFILENicknameRegistrationFallbackHelp    = "###traduzir###";

		
		// command patterns
		PROFILEtrgGlobalStartAskForNicknameDialog = getConcatenationOf(PROFILEtrgGlobalStartAskForNicknameDialog, "[^A-Z0-9]*AP?[EI]?L?I?D?[OU]?[^A-Z0-9]*|[^A-Z0-9]*NOME[^A-Z0-9]*");
		PROFILEtrgLocalNicknameDialogCancelation  = getConcatenationOf(PROFILEtrgLocalNicknameDialogCancelation/*,  "---"*/);
		PROFILEtrgLocalRegisterNickname           = getConcatenationOf(PROFILEtrgLocalRegisterNickname/*,           "[^A-Z0-9]*([A-Z0-9]+).*"*/);
		PROFILEtrgGlobalRegisterNickname          = getConcatenationOf(PROFILEtrgGlobalRegisterNickname,          "[^A-Z0-9]*AP?[EI]?L?I?D?[OU]?[^A-Z0-9]+([A-Z0-9]+).*|[^A-Z0-9]*NOME[^A-Z0-9]+([A-Z0-9]+).*");
		PROFILEtrgGlobalShowUserProfile           = getConcatenationOf(PROFILEtrgGlobalShowUserProfile,           "[^A-Z0-9]*PER?FI[LU]?[^A-Z0-9]*|[^A-Z0-9]*PER?FI[LU]?[^A-Z0-9]+([A-Z0-9]+).*");
		PROFILEtrgGlobalListProfiles              = getConcatenationOf(PROFILEtrgGlobalListProfiles,              "[^A-Z0-9]*LIST[AE]R?[^A-Z0-9]*|[^A-Z0-9]*RANK[IE]?N?G?[^A-Z0-9]*|[^A-Z0-9]*RANQ?U?E?[^A-Z0-9]*");
		PROFILEtrgLocalListMoreProfiles           = getConcatenationOf(PROFILEtrgLocalListMoreProfiles,           "[^A-Z0-9]*MA?I?S?[^A-Z0-9]*|[^A-Z0-9]*LIST[AE][^A-Z0-9]*|[^A-Z0-9]*RANK[IE]?N?G?[^A-Z0-9]*|[^A-Z0-9]*RANQ?U?E?[^A-Z0-9]*");
		
		// Chat
		///////
		
		// phrasing
		CHATphrPrivateMessage                     = "{{senderNickname}}: {{senderMessage}} - Responda enviando P {{senderNickname}} e a sua mensagem, sempre com espaco no meio. Exemplo: P {{senderNickname}} tudo bem, e voce?";
		CHATphrPrivateMessageDeliveryNotification = "Sua mensagem foi enviada para {{targetNickname}}. Aguarde a resposta ou envie LISTA e veja a lista completa dos jogadores ativos.";
		CHATphrDoNotKnowWhoYouAreChattingTo       = "### CHATphrDoNotKnowWhoYouAreChattingTo ###";
		
		// command patterns
		CHATtrgGlobalSendPrivateMessage = getConcatenationOf(CHATtrgGlobalSendPrivateMessage/*, "[^A-Z0-9]*[MP][^A-Z0-9]+([A-Z0-9]+).*[^A-Z0-9]+(.*)"*/);
		CHATtrgLocalSendPrivateReply    = getConcatenationOf(CHATtrgLocalSendPrivateReply/*, "---"*/);

		// Hangman
		//////////
		
		// phrasing
		HANGMANwinningArt                                                   = "\\0/\n l\n/ \\\n";
		HANGMANlosingArt                                                    = "+-+\nl x\nl/l\\\nl/ \\\n====\n";
		HANGMANheadCharacter                                                = "O";
		HANGMANleftArmCharacter                                             = "<";
		HANGMANchestCharacter                                               = "l";
		HANGMANrightArmCharacter                                            = ">";
		HANGMANleftLegCharacter                                             = "/";
		HANGMANrightLegCharacter                                            = "\\";
		HANGMANphr_gallowsArt                                               = "+-+\nl {{head}}\nl{{leftArm}}{{chest}}{{rightArm}}\nl{{leftLeg}} {{rightLeg}}\nl\n====\n";
		HANGMANphrAskOpponentNicknameOrPhone                                = "### HANGMANphrAskOpponentNicknameOrPhone ###{{appName}}: Name registered: {{invitingPlayerNickname}}. Send your friend's nick name or phone number; Or send LIST to see the available online players. NICK [NEW NICK] to change your name.";
		HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation = "{{appName}}: Chamando {{opponentNickname}} pro jogo! Envie uma palavra sem acentos ou simbolos. Quanto mais dificil, maiores as chances dele(a) nao conseguir! Capriche :)";
		HANGMANphrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation    = "### HANGMANphrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation ###{{appName}}: Your friend's phone: {{opponentPhoneNumber}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number";
		HANGMANphrInvitationResponseForInvitingPlayer                       = "{{invitedPlayerNickname}} ja vai receber o convite. Aguarde pela resposta e boa sorte!";
		HANGMANphrInvitationNotificationForInvitedPlayer                    = "{{appName}}: {{invitingPlayerNickname}} esta te desafiando pra uma partida. Vai encarar? Envie SIM caso queira aceitar ou NAO pra recusar. Vc tambem pode enviar M {{invitingPlayerNickname}} [MSG] para falar com ele(a).";
		HANGMANphrTimeoutNotificationForInvitingPlayer                      = "### HANGMANphrTimeoutNotificationForInvitingPlayer ###{{appName}}: {{invitedPlayerNickname}} is taking too long to answer. However, a new player, {{suggestedNewPlayersNickname}}, is available. Play with {{suggestedNewPlayersNickname}}? Send YES to {{shortCode}}";
		HANGMANphrInvitationRefusalResponseForInvitedPlayer                 = "O convite de jogo de {{invitingPlayerNickname}} foi recusado. Envie LISTA para ver outros jogadores ativos ou mande uma mensagem para ele(a). Envie P {{invitingPlayerNickname}} [MSG]";
		HANGMANphrInvitationRefusalNotificationForInvitingPlayer            = "{{invitedPlayerNickname}} recusou o jogo. Envie LISTA para escolher outro adversario ou envie uma mensagem para ele(a). Envie P {{invitingPlayerNickname}} [MSG]";
		HANGMANphrNotAGoodWord                                              = "Voce escolheu '{{word}}'. Hmmm... Esta palavra nao foi aceita. Escolha uma palavra apenas com letras de A a Z, sem acentos, numeros ou simbolos, e envie novamente!";
		HANGMANphrWordProvidingPlayerMatchStart                             = "Pronto! O jogo contra {{wordGuessingPlayerNickname}}.\n{{gallowsArt}}. Agora eh so esperar ele(a) enviar a primeira letra! Sera que sua palavra eh dificil mesmo? Veremos... Enquanto esperamos pelo primeiro palpite de {{wordGuessingPlayerNickname}}, vc pode dar uma dica enviando P {{wordGuessingPlayerNickname}} [MSG]";
		HANGMANphrWordGuessingPlayerMatchStart                              = "{{gallowsArt}}Palavra: {{guessedWordSoFar}}\nLetras: {{usedLetters}}\nVamos ver se a palavra vai ser desvendada! Qual é a sua primeira letra? Envie-a p/ {{shortCode}} ou peca dicas a {{wordProvidingPlayerNickname}} enviando P {{wordProvidingPlayerNickname}} [MSG]";
		HANGMANphrWordProvidingPlayerStatus                                 = "Jogo rolando! {{wordGuessingPlayerNickname}} enviou uma letra! {{guessedLetter}}\n{{gallowsArt}}Palavra: {{guessedWordSoFar}}\nLetras: {{usedLetters}}\nQuer falar com ele(a)? Envie P {{wordGuessingPlayerNickname}} [MSG]";
		HANGMANphrWordGuessingPlayerStatus                                  = "{{gallowsArt}}Palavra: {{guessedWordSoFar}}\nLetras: {{usedLetters}}\nEnvie uma letra para formar a palavra. Capriche para ganhar esse jogo!";
		HANGMANphrWinningMessageForWordGuessingPlayer                       = "{{winningArt}}{{word}}! Parabens!! Voce completou a palavra! Continue jogando! Desafie esse mesmo jogador enviando CHAMAR {{wordProvidingPlayerNickname}} com espaco no meio, ou escolha outro adversario enviando LISTA.";
		HANGMANphrWinningMessageForWordProvidingPlayer                      = "{{wordGuessingPlayerNickname}} completou a palavra! Quer revanche? Envie CHAMAR {{wordGuessingPlayerNickname}} sempre com espaco entre as palavras, e boa sorte.";
		HANGMANphrLosingMessageForWordGuessingPlayer                        = "{{losingArt}}Xi, enforcou... A palavra era {{word}}. Tente agora voce! Peca uma revanche! Envie CHAMAR {{wordProvidingPlayerNickname}}, sempre com espaco entre as palavras, e boa sorte!";
		HANGMANphrLosingMessageForWordProvidingPlayer                       = "Boa!! {{wordGuessingPlayerNickname}} nao desvendou a palavra! Quer comentar o jogo com ele/a? Envie P {{wordGuessingPlayerNickname}} e sua mensagem, com espaco no meio. Quer comecar um novo duelo? Envie CHAMAR {{wordGuessingPlayerNickname}}, com espaco no meio, e caprich na escolha da palavra secreta!!";
		HANGMANphrMatchGiveupNotificationForWordGuessingPlayer              = "Sua partida contra {{wordProvidingPlayerNickname}} foi cancelada. Envie P {{wordProvidingPlayerNickname}} [MSG] para falar com ele(a) ou LISTA para escolher outro adversario. Para treinar com o Robo, envie J.";
		HANGMANphrMatchGiveupNotificationForWordProvidingPlayer             = "{{wordGuessingPlayerNickname}} cancelou a partida. Escolha outro adversario enviando LISTA, ou treine com o robo enviando J.";
		HANGMANphrGuessingWordHelp                                          = "### HANGMANphrGuessingWordHelp ###You are guessing a word on a {{appName}} match. Please text a letter or: END to quit the match; P [nick] [MSG] to ask for cues; LIST to see other online users";
		
		// command patterns
		HANGMANtrgPlayWithRandomUserOrBot                = getConcatenationOf(HANGMANtrgPlayWithRandomUserOrBot,                "[^A-Z0-9]*E?[NM]?FOR[CÇç]AR?[^A-Z0-9]*|[^A-Z0-9]*CO?N?V?I?[DT]?[AEO]?R?[^A-Z0-9]*|[^A-Z0-9]*CH?A?M?[AEO]?R?[^A-Z0-9]*|[^A-Z0-9]*JO?G?[AO]?R?[^A-Z0-9]*|[^A-Z0-9]*PARTIDA[^A-Z0-9]*|[^A-Z0-9]*NOV[OU][^A-Z0-9]*|[^A-Z0-9]*D[EI] ?NOV[OU][^A-Z0-9]*|[^A-Z0-9]*OU?TR[OA][^A-Z0-9]*");
		HANGMANtrgGlobalInviteNicknameOrPhoneNumber      = getConcatenationOf(HANGMANtrgGlobalInviteNicknameOrPhoneNumber,      "[^A-Z0-9]*E?[NM]?FOR[CÇç]AR?[^A-Z0-9]+([A-Z0-9]+).*|[^A-Z0-9]*CO?N?V?I?[DT]?[AEO]?R?[^A-Z0-9]+([A-Z0-9]+).*|[^A-Z0-9]*CH?A?M?[AEO]?R?[^A-Z0-9]+([A-Z0-9]+).*|[^A-Z0-9]*JO?G?[AO]?R?[^A-Z0-9]+([A-Z0-9]+).*|[^A-Z0-9]*PARTIDA[^A-Z0-9]+([A-Z0-9]+).*|[^A-Z0-9]*NOV[OU][^A-Z0-9]+([A-Z0-9]+).*|[^A-Z0-9]*D[EI] ?NOV[OU][^A-Z0-9]+([A-Z0-9]+).*|[^A-Z0-9]*OU?TR[OA][^A-Z0-9]+([A-Z0-9]+).*");
		HANGMANtrgLocalHoldMatchWord                     = getConcatenationOf(HANGMANtrgLocalHoldMatchWord/*,                     "[^A-Z0-9]*([A-Z0-9]+).*"*/);
		HANGMANtrgLocalAcceptMatchInvitation             = getConcatenationOf(HANGMANtrgLocalAcceptMatchInvitation,             "[^A-Z0-9]*SI?M?[^A-Z0-9]*|[^A-Z0-9]*TA[^A-Z0-9]*|[^A-Z0-9]*OK[^A-Z0-9]*|[^A-Z0-9]*VAI[^A-Z0-9]*|[^A-Z0-9]*ACEIT[OA]R?[^A-Z0-9]*|.*AGORA.*");
		HANGMANtrgLocalRefuseMatchInvitation             = getConcatenationOf(HANGMANtrgLocalRefuseMatchInvitation,             "[^A-Z0-9]*N[ÃãA]?O?[^A-Z0-9]*|[^A-Z0-9]*NEI?[MN][^A-Z0-9]*");
		HANGMANtrgLocalSingleLetterSuggestionForHuman    = getConcatenationOf(HANGMANtrgLocalSingleLetterSuggestionForHuman/*,    "[^A-Z0-9]*([A-Z])"*/);
		HANGMANtrgLocalWordSuggestionFallbackForHuman    = getConcatenationOf(HANGMANtrgLocalWordSuggestionFallbackForHuman/*,    "[^A-Z0-9]*([A-Z]+)"*/);
		HANGMANtrgLocalSingleLetterSuggestionForBot      = getConcatenationOf(HANGMANtrgLocalSingleLetterSuggestionForBot/*,      "[^A-Z0-9]*([A-Z])"*/);
		HANGMANtrgLocalWordSuggestionFallbackForBot      = getConcatenationOf(HANGMANtrgLocalWordSuggestionFallbackForBot/*,      "[^A-Z0-9]*([A-Z]+)"*/);
		HANGMANtrgLocalEndCurrentHumanMatch              = getConcatenationOf(HANGMANtrgLocalEndCurrentHumanMatch,              "[^A-Z0-9]*SAIR?[^A-Z0-9]*|[^A-Z0-9]*CANCELA?R?[^A-Z0-9]*|[^A-Z0-9]*FI[MN][^A-Z0-9]*");
		HANGMANtrgLocalEndCurrentBotMatch                = getConcatenationOf(HANGMANtrgLocalEndCurrentBotMatch,                "[^A-Z0-9]*SAIR?[^A-Z0-9]*|[^A-Z0-9]*CANCELA?R?[^A-Z0-9]*|[^A-Z0-9]*FI[MN][^A-Z0-9]*");
		
		// other settings
		// according to 'UserGeoLocator.CountryStateByMSISDNResolver(...)'
		PROFILEGeoLocatorCountryStateByMSISDNPatterns = new String[] {
			"SP", "551[123456789].*",
			"RJ", "552[124].*",
			"ES", "552[78].*",
			"MG", "553[1234578].*",
			"PR", "554[123456].*",
			"SC", "554[789].*",
			"RS", "555[1345].*",
			"DF", "5561.*",
			"GO", "556[24].*",
			"TO", "5563.*",
			"MT", "556[56].*",
			"MS", "5567.*",
			"AC", "5568.*",
			"RO", "5569.*",
			"BA", "557[13457].*",
			"SE", "5579.*",
			"PE", "5581.*",
			"AL", "5582.*",
			"PB", "5583.*",
			"RN", "5584.*",
			"CE", "558[58].*",
			"PI", "5589.*",
			"PA", "559[134].*",
			"AM", "559[27].*",
			"RR", "5595.*",
			"AP", "5596.*",
			"MA", "559[89].*",
			"--",
		};

	}

}