package config;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.CommandNamesHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.CommandTriggersHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.CommandNamesSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.CommandTriggersSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.CommandNamesProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.CommandTriggersProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.CommandNamesChat.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.CommandTriggersChat.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman.CommandNamesHangman.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman.CommandTriggersHangman.*;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import instantvas.smsengine.InstantVASHTTPInstrumentationRequestProperty;
import instantvas.smsengine.MOSMSesQueueDataBureau;
import instantvas.smsengine.MTSMSesQueueDataBureau;
import instantvas.smsengine.producersandconsumers.EInstantVASEvents;
import instantvas.smsengine.producersandconsumers.InstantVASEvent;
import mutua.events.DirectEventLink;
import mutua.events.IEventLink;
import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.QueueEventLink;
import mutua.events.postgresql.QueuesPostgreSQLAdapter;
import mutua.hangmansmsgame.smslogic.SMSProcessor;
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
import adapters.PostgreSQLAdapter;

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

public class InstantVASApplicationConfiguration {

	// MUTUA ICC CONFIGURABLE CONSTANTS
	///////////////////////////////////

	@ConfigurableElement("Where to store report data")
	public static EInstrumentationDataPours REPORT_DATA_COLLECTOR_STRATEGY;
	@ConfigurableElement("Where to store log data")
	public static EInstrumentationDataPours LOG_STRATEGY;
	@ConfigurableElement("File name to log Hangman Logic logs")
	public static String LOG_HANGMAN_FILE_PATH;
	@ConfigurableElement("File name to log Hangman Web / Integration logs")
	public static String LOG_WEBAPP_FILE_PATH;
	@ConfigurableElement("The name of the Hangman Game, as shown in the logs")
	public static String APP_NAME;
	@ConfigurableElement("The short code of the Hangman Game")
	public static String SHORT_CODE;
	@ConfigurableElement("The subscription cost to the end user")
	public static String PRICE_TAG;
	@ConfigurableElement("Default prefix for invited & new users -- The suffix are the last 4 phone number digits")
	public static String DEFAULT_NICKNAME_PREFIX;
	
	// MO QUEUE (but also SubscribeUser & UnsubscribeUser queues)
	/////////////////////////////////////////////////////////////
	
	public enum EEventProcessingStrategy {DIRECT, RAM, LOG_FILE, POSTGRESQL};
	public enum EInstantVASDALs     {RAM, POSTGRESQL}
		
	// HTTPClientAdapter
	////////////////////
	
	@ConfigurableElement("General HTTP/HTTPD client behavior, in milliseconds")
	public static int HTTP_CONNECTION_TIMEOUT_MILLIS;
	public static int HTTP_READ_TIMEOUT_MILLIS;
	
	// Integration with 'SMSOutCelltick' and 'SubscriptionEngineCelltick'
	/////////////////////////////////////////////////////////////////////

	@ConfigurableElement("Subscription service URLs & data for 'CelltickLiveScreenSubscriptionAPI'")
	public static String CELLTICK_SUBSCRIBE_SERVICE_URL;
	public static String CELLTICK_UNSUBSCRIBE_SERVICE_URL;
	public static String CELLTICK_SUBSCRIPTION_CHANNEL_NAME;
	@ConfigurableElement("MT service URLs & data for Celltick's Kannel APIs")
	public static String CELLTICK_MT_SERVICE_URL;
	@ConfigurableElement("the number of times 'sendMessage' will attempt to send the message before reporting it as unsendable")
	public static int    MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS;
	@ConfigurableElement("the number of milliseconds 'sendMessage' will wait between retry attempts")
	public static long   MT_SERVICE_DELAY_BETWEEN_ATTEMPTS;
	
	// JDBCAdapter
	//////////////
	
	@ConfigurableElement("The desired data access handler for all hangman databases")
	public static EInstantVASDALs  DATA_ACCESS_LAYER;
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
		
	@ConfigurableElement("Specifies which event processing strategy should be used on incoming SMSes (MOs) -- DIRECT means the messages will be processed directly, on the same request thread and without any buffer; RAM means the producers and consumers must be running on the same machine and on the same process; POSTGRESQL means a table will be used to keep those messages and serve as the queue at the same time")
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

	@ConfigurableElement("The same as described on 'MO_PROCESSING_STRATEGY', but for MTs (outgoing SMSes)")
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
	
	@ConfigurableElement("The same as described on 'MO_PROCESSING_STRATEGY', but for 'Subscription Renewal' events, generated by the subscription lifecycle engine")
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

	@ConfigurableElement("The same as described on 'SUBSCRIPTION_RENEWAL_PROCESSING_STRATEGY', but for 'Subscription Cancellation' events")
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
	
	@ConfigurableElement("Not a good idea to mess with these values")
	public static EInstantVASModules[] ENABLED_MODULES;

	
	// Phrasings
	////////////
	
	// help
	@ConfigurableElement("Phrase sent when a new user sends an unrecognized keyword, possibly instructing him/her on how to register. Variables: {{shortCode}}, {{appName}}")
	public static String HELPphrNewUsersFallback;
	@ConfigurableElement("Phrase sent when an existing user attempts to send an unrecognized command, to give him/her a quick list of commands. Variables: {{shortCode}}, {{appName}}")
	public static String HELPphrExistingUsersFallback;
	@ConfigurableElement("These are the general help messages, sent in response to the HELP command anywhere in the app navigation states. This message will not interrupt the flow and the user may continue normally after receiving this message. Variables: {{shortCode}}, {{appName}}")
	public static String HELPphrStateless;
	@ConfigurableElement("These are the detailed help messages, sent in response to the HELP/RULES command that will change the navigation state. You can set a second, third and so on help messages, which will be sent in response to the MORE command. Variables: {{shortCode}}, {{appName}}")
	public static String[] HELPphrComposite;
	@ConfigurableElement("Not used. For this to be used, a new variable should be created for every state. Example: HELPphrPlayingWithAHuman, ...")
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
	@ConfigurableElement("Text sent to present the details of a user profile. Variables: {{shortCode}}, {{appName}}, {{nickname}} and others, from extensions, such as {{subscriptionState}}, {{geoUserLocation}} and {{numberOfLuckyNumbers}}")
	public static String PROFILEphrUserProfilePresentation;
	@ConfigurableElement("Phrase sent to the sender user, who referenced a user by it's nickname, to inform that the command wasn't executed for the informed nickname was not found. Variables: {{shortCode}}, {{appName}}, {{targetNickname}}")
	public static String PROFILEphrNicknameNotFound;
	
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

	
	// Command Triggers to Command Mappings
	///////////////////////////////////////
	
	// help
	// HELPtrgGlobalStartCompositeHelpDialog     +=StartCompositeHelpDialog
	// HELPtrgLocalShowNextCompositeHelpMessage  +=ShowNextCompositeHelpMessage
	// HELPtrgGlobalShowNewUsersFallbackHelp     +=ShowNewUsersFallbackHelp
	// HELPtrgGlobalShowExistingUsersFallbackHelp+=ShowExistingUsersFallbackHelp
	// HELPtrgGlobalShowStatelessHelpMessage     +=ShowStatelessHelp
	
	// subscription
	// SUBSCRIPTIONtrgLocalStartDoubleOptin +=StartDoubleOptinProcess
	// SUBSCRIPTIONtrgLocalAcceptDoubleOptin+=Subscribe
	// SUBSCRIPTIONtrgLocalRefuseDoubleOptin+=DoNotAgreeToSubscribe
	// SUBSCRIPTIONtrgGlobalUnsubscribe     +=Unsubscribe
	
	// profile
	// PROFILEtrgGlobalRegisterNickname         +=RegisterNickname
	// PROFILEtrgGlobalStartAskForNicknameDialog+=StartAskForNicknameDialog
	// PROFILEtrgLocalNicknameDialogCancelation +=AskForNicknameDialogCancelation
	// PROFILEtrgGlobalShowUserProfile          +=ShowUserProfile
	// PROFILEtrgLocalRegisterNickname          +=RegisterNickname
	// PROFILE
	
	// chat
	// CHATtrgGlobalSendPrivateMessage+=SendPrivateMessage
	// CHATtrgLocalSendPrivateReply   +=SendPrivateReply
	
	// hangman
	// HANGMANtrgLocalHoldMatchWord+=cmdHoldMatchWord
	// HANGMANtrgLocalHoldMatchWord+=cmdHoldMatchWord
	// HANGMANtrgLocalNewLetterOrWordSuggestionForHuman+=cmdSuggestLetterOrWordForHuman
	// HANGMANtrgLocalNewLetterOrWordSuggestionForBot+=cmdSuggestLetterOrWordForBot

	// Command Triggers (Regular Expression Patterns)
	/////////////////////////////////////////////////
	
	// help
	@ConfigurableElement("If matched, shows a very long help message which -- requires pagination to show it's full contents -- and puts the user in the 'nstPresentingCompositeHelp' navigation state")
	public static String[] HELPtrgGlobalStartCompositeHelpDialog;
	@ConfigurableElement("For users in 'nstPresentingCompositeHelp', if matched, shows the next page of a very long help")
	public static String[] HELPtrgLocalShowNextCompositeHelpMessage;
	@ConfigurableElement("If the MO for a new user isn't matched by no other trigger, this one will present the 'HELPphrNewUsersFallback' message. For no reason it should be different than .*")
	public static String[] HELPtrgGlobalShowNewUsersFallbackHelp;
	@ConfigurableElement("If the MO isn't matched by no other trigger, this one will present the 'HELPphrExistingUsersFallback' message. For no reason it should be different than .*")
	public static String[] HELPtrgGlobalShowExistingUsersFallbackHelp;
	@ConfigurableElement("If matched, shows a single message help")
	public static String[] HELPtrgGlobalShowStatelessHelpMessage;
	
	// subscription
	@ConfigurableElement("For users in 'nstNewUser' or 'nstUnsubscribedUser', if matched, starts the double opt-in process")
	public static String[] SUBSCRIPTIONtrgLocalStartDoubleOptin;
	@ConfigurableElement("For users in 'nstAnsweringDoubleOptin', if matched, considers the subscription as being accepted")
	public static String[] SUBSCRIPTIONtrgLocalAcceptDoubleOptin;
	@ConfigurableElement("Same as above, but considers the subscription as being denied")
	public static String[] SUBSCRIPTIONtrgLocalRefuseDoubleOptin;
	@ConfigurableElement("If matched, attempts to cancel the user subscription, returning him/her to the 'nstNewUser' navigation state")
	public static String[] SUBSCRIPTIONtrgGlobalUnsubscribe;
	
	// profile
	@ConfigurableElement("If matched, asks for a (new) nickname, starting the 'register a nickname' dialog and puts the user in 'nstRegisteringNickname' navigation state")
	public static String[] PROFILEtrgGlobalStartAskForNicknameDialog;
	@ConfigurableElement("For users in 'nstRegisteringNickname', if matched, cancels the 'register a nickname' dialog")
	public static String[] PROFILEtrgLocalNicknameDialogCancelation;
	@ConfigurableElement("For users in 'nstRegisteringNickname', if matched, sets the new nickname. Should match 1 parameter: the new nickname")
	public static String[] PROFILEtrgLocalRegisterNickname;
	@ConfigurableElement("If matched, registers the user's new nickname. Should capture 1 parameter: the new nickname")
	public static String[] PROFILEtrgGlobalRegisterNickname;
	@ConfigurableElement("If matched, presents the desired user's profile. Should capture 1 parameter: the desired user's nickname")
	public static String[] PROFILEtrgGlobalShowUserProfile;
	
	// chat
	@ConfigurableElement("If matched, sends a private message to a user. Should capture 2 parameters: the nickname and the message")
	public static String[] CHATtrgGlobalSendPrivateMessage;
	@ConfigurableElement("For users in 'nstChattingWithSomeone', if matched, sends a private reply to the active chat partner. Should match 1 parameter: the message")
	public static String[] CHATtrgLocalSendPrivateReply;
	
	// hangman
	@ConfigurableElement("If matched, starts the invitation for a hangman match process. Should capture 1 parameter: the phone number or nickname of the desired opponent")
	public static String[] HANGMANtrgGlobalInviteNicknameOrPhoneNumber;
	@ConfigurableElement("When on 'nstEnteringMatchWord', if matched, advances on the 'invite for a hangman match' process by computing the desired word'. Should capture 1 parameter: the desired match word")
	public static String[] HANGMANtrgLocalHoldMatchWord;
	@ConfigurableElement("When on 'nstAnsweringToHangmanMatchInvitation', if matched, accepts the invitation for a hangman match")
	public static String[] HANGMANtrgLocalAcceptMatchInvitation;
	@ConfigurableElement("Same as above, but refuses the match")
	public static String[] HANGMANtrgLocalRefuseMatchInvitation;
	@ConfigurableElement("When on 'nstGuessingWordFromHangmanHumanOpponent', if matched, should capture a letter or word to be used as a suggestion, in order to advance on the current hangman match")
	public static String[] HANGMANtrgLocalNewLetterOrWordSuggestionForHuman;
	@ConfigurableElement("Same as above, but for 'nstGuessingWordFromHangmanBotOpponent' navigation state")
	public static String[] HANGMANtrgLocalNewLetterOrWordSuggestionForBot;
	

	// navigation states
	////////////////////
	
	@ConfigurableElement("Navigation state used to initiate the first interaction with the application and, also, the state after users subscriptions cancellation")
	public static EInstantVASCommandTriggers[] BASEnstNewUser;
	@ConfigurableElement("Navigation state used by registered users. Also the 'main loop' navigation state, to which all other states revert to when they finish their businesses")
	public static EInstantVASCommandTriggers[] BASEnstExistingUser;
	@ConfigurableElement("Navigation state used to show the composite help messages, containing command triggers to navigate from here on")
	public static EInstantVASCommandTriggers[] HELPnstPresentingCompositeHelp;
	@ConfigurableElement("Navigation state used to implement the double opt-in process")
	public static EInstantVASCommandTriggers[] SUBSCRIPTIONnstAnsweringDoubleOptin;
	@ConfigurableElement("Navigation state used to interact with the user when asking for a nickname")
	public static EInstantVASCommandTriggers[] PROFILEnstRegisteringNickname;
	@ConfigurableElement("Navigation state used when privately chatting with someone -- allows the user to simply type the message (no need to provide the nickname)")
	public static EInstantVASCommandTriggers[] CHATnstChattingWithSomeone;
	@ConfigurableElement("Navigation state part of the invitation process of a human to play a hangman match -- on this state, the user must enter the desired word to be guessed, which will be processed by 'cmdHoldMatchWord'")
	public static EInstantVASCommandTriggers[] 	HANGMANnstEnteringMatchWord;
	@ConfigurableElement("State an invited user gets into after he/she is invited for a match, which is set by 'cmdHoldMatchWord'. The invited user answer will, then, be processed by 'cmdAnswerToInvitation'")
	public static EInstantVASCommandTriggers[] HANGMANnstAnsweringToHangmanMatchInvitation;
	@ConfigurableElement("Navigation state that indicates the user is playing a hangman match with a human (as the invited opponent), and his/her role is to guess the word")
	public static EInstantVASCommandTriggers[] HANGMANnstGuessingWordFromHangmanHumanOpponent;
	@ConfigurableElement("Navigation state that indicates the user is playing a hangman match with the robot, and his/her hole is to guess the word")
	public static EInstantVASCommandTriggers[] HANGMANnstGuessingWordFromHangmanBotOpponent;
	
	public enum EInstantVASCommandTriggers {
		// help
		HELPtrgGlobalStartCompositeHelpDialog,
		HELPtrgLocalShowNextCompositeHelpMessage,
		HELPtrgGlobalShowNewUsersFallbackHelp,
		HELPtrgGlobalShowExistingUsersFallbackHelp,
		HELPtrgGlobalShowStatelessHelpMessage,
		// subscription
		SUBSCRIPTIONtrgLocalStartDoubleOptin,
		SUBSCRIPTIONtrgLocalAcceptDoubleOptin,
		SUBSCRIPTIONtrgLocalRefuseDoubleOptin,
		SUBSCRIPTIONtrgGlobalUnsubscribe,
		// profile
		PROFILEtrgGlobalStartAskForNicknameDialog,
		PROFILEtrgLocalNicknameDialogCancelation,
		PROFILEtrgLocalRegisterNickname,
		PROFILEtrgGlobalRegisterNickname,
		PROFILEtrgGlobalShowUserProfile,
		// chat
		CHATtrgGlobalSendPrivateMessage,
		CHATtrgLocalSendPrivateReply,
		// hangman
		HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
		HANGMANtrgLocalHoldMatchWord,
		HANGMANtrgLocalAcceptMatchInvitation,
		HANGMANtrgLocalRefuseMatchInvitation,
		HANGMANtrgLocalNewLetterOrWordSuggestionForHuman,
		HANGMANtrgLocalNewLetterOrWordSuggestionForBot;
		
		public String[] getCommandTriggerPatterns() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
			return (String[])InstantVASApplicationConfiguration.class.getField(name()).get(null);
		}
		
		/** Converts a navigation state's command & triggers set based on an 'EInstantVASCommandTriggers' array to a string based representation */
		public static String[][] get2DStringArrayFromEInstantVASCommandTriggersArray(EInstantVASCommandTriggers[] navigationStateCommandTriggers) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
			String[][] stringArrayNavigationStateTriggers = new String[navigationStateCommandTriggers.length][];
			for (int i=0; i<stringArrayNavigationStateTriggers.length; i++) {
				stringArrayNavigationStateTriggers[i] = navigationStateCommandTriggers[i].getCommandTriggerPatterns();
			}
			return stringArrayNavigationStateTriggers;
		}
	};
	
	public enum EInstantVASModules {// integration modules
	                                CELLTICK_BR_INTEGRATION,
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
	
	// INSTANCE VARIABLES
	/////////////////////
	
	// generic
	public final Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log;

	// integration
	public SubscriptionEngine subscriptionEngine;
	public String subscriptionToken;
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
	public SMSAppModuleCommandsProfile              profilecommands          = null;
	public SMSAppModulePhrasingsProfile             profilePhrasings         = null;
	public SMSAppModuleNavigationStatesChat         chatStates               = null;
	public SMSAppModuleCommandsChat                 chatCommands             = null;
	public SMSAppModulePhrasingsChat                chatPhrasings            = null;
	public SMSAppModuleNavigationStatesHangman      hangmanStates            = null;
	public SMSAppModuleCommandsHangman              hangmanCommands          = null;
	public SMSAppModulePhrasingsHangman             hangmanPhrasings         = null;
	
	public final NavigationState[][]    modulesNavigationStates;
	public final ICommandProcessor[][]  modulesCommandProcessors;

	
	public InstantVASApplicationConfiguration() throws SQLException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		
		log = new Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String>(
			APP_NAME, new InstantVASHTTPInstrumentationRequestProperty(), LOG_STRATEGY, LOG_HANGMAN_FILE_PATH);
		
		List<EInstantVASModules> enabledModulesList = Arrays.asList(ENABLED_MODULES);
		
		// configure modules dal
		switch (DATA_ACCESS_LAYER) {
		case POSTGRESQL:
			System.out.println("\n### Configuring PostgreSQLAdapter...");
			PostgreSQLAdapter.configureDefaultValuesForNewInstances(POSTGRESQL_CONNECTION_PROPERTIES, NUMBER_OF_CONCURRENT_CONNECTIONS);
			System.out.print("### Configuring modules DALs: ");
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
				case CELLTICK_BR_INTEGRATION:
					break;
				case BASE:
					baseModuleDAL = SMSAppModuleDALFactory.POSTGRESQL;
					SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(log, POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
					break;
				case HELP:
					break;
				case SUBSCRIPTION:
					subscriptionDAL = SMSAppModuleDALFactorySubscription.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterSubscription.configureDefaultValuesForNewInstances(log, POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
					break;
				case PROFILE:
					profileModuleDAL = SMSAppModuleDALFactoryProfile.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterProfile.configureDefaultValuesForNewInstances(log, POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
					break;
				case CHAT:
					chatModuleDAL = SMSAppModuleDALFactoryChat.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterChat.configureDefaultValuesForNewInstances(log, POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD, MOSMSesQueueDataBureau.MO_TABLE_NAME, MOSMSesQueueDataBureau.MO_ID_FIELD_NAME, MOSMSesQueueDataBureau.MO_TEXT_FIELD_NAME);
					break;
				case HANGMAN:
					hangmanModuleDAL = SMSAppModuleDALFactoryHangman.POSTGRESQL;
					SMSAppModulePostgreSQLAdapterHangman.configureDefaultValuesForNewInstances(log, POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
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
			throw new RuntimeException("InstantVAS Modules DAL '"+DATA_ACCESS_LAYER+"' is not implemented");
		}
		
		// configure event processing strategies
		Class<? extends Annotation>[] eventProcessingAnnotationClasses = (Class<? extends Annotation>[]) new Class<?>[] {InstantVASEvent.class};
		System.out.print("\n### Configuring 'MO arrived' event processing strategy: ");
		switch (MO_PROCESSING_STRATEGY) {
		case POSTGRESQL:
			System.out.println("PostgreSQL Queue...");
			QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(log, POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
			PostgreSQLQueueEventLink.configureDefaultValuesForNewInstances(log, MO_POSTGRESQL_QUEUE_POOLING_TIME, MO_QUEUE_NUMBER_OF_WORKER_THREADS);
			MOpcLink = new PostgreSQLQueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, MOSMSesQueueDataBureau.MO_TABLE_NAME, new MOSMSesQueueDataBureau(SHORT_CODE));
			break;
		case RAM:
			System.out.println("RAM Queue...");
			MOpcLink = new QueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, MO_RAM_QUEUE_CAPACITY, MO_QUEUE_NUMBER_OF_WORKER_THREADS);
			break;
		case DIRECT:
			System.out.println("Direct...");
			MOpcLink = new DirectEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses);
			break;
		default:
			throw new RuntimeException("InstantVAS 'MO arrived' Event Processing Strategy '"+MO_PROCESSING_STRATEGY+"' is not implemented");
		}
		System.out.print("\n### Configuring 'MT ready for delivert' event processing strategy: ");
		switch (MO_PROCESSING_STRATEGY) {
		case POSTGRESQL:
			System.out.println("PostgreSQL Queue...");
			QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(log, POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS, POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
			PostgreSQLQueueEventLink.configureDefaultValuesForNewInstances(log, MT_POSTGRESQL_QUEUE_POOLING_TIME, MT_QUEUE_NUMBER_OF_WORKER_THREADS);
			MTpcLink = new PostgreSQLQueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, MTSMSesQueueDataBureau.MT_TABLE_NAME, new MTSMSesQueueDataBureau());
			break;
		case RAM:
			System.out.println("RAM Queue...");
			MTpcLink = new QueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, MT_RAM_QUEUE_CAPACITY, MT_QUEUE_NUMBER_OF_WORKER_THREADS);
			break;
		case DIRECT:
			System.out.println("Direct...");
			MTpcLink = new DirectEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses);
			break;
		default:
			throw new RuntimeException("InstantVAS 'MT ready for delivery' Event Processing Strategy '"+MT_PROCESSING_STRATEGY+"' is not implemented");
		}
		System.out.println("\n### Configuring 'Subscription Renewal' event processing strategy:");
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
			System.out.println("RAM Queue...");
			SRpcLink = new QueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, SR_RAM_QUEUE_CAPACITY, SR_QUEUE_NUMBER_OF_WORKER_THREADS);
			break;
		case DIRECT:
			System.out.println("Direct...");
			SRpcLink = new DirectEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses);
			break;
		}
		System.out.println("\n### Configuring 'Subscription Cancellation' event processing strategy:");
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
			System.out.println("RAM Queue...");
			SCpcLink = new QueueEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses, SC_RAM_QUEUE_CAPACITY, SC_QUEUE_NUMBER_OF_WORKER_THREADS);
			break;
		case DIRECT:
			System.out.println("Direct...");
			SCpcLink = new DirectEventLink<EInstantVASEvents>(
				EInstantVASEvents.class, eventProcessingAnnotationClasses);
			break;
		}
		
		// configure the SMSProcessor
		System.out.print("\n### Configuring the SMS Processor...");
		SMSProcessor.configureDefaultValuesForNewInstances(log, baseModuleDAL);
		System.out.println(" OK");
		
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
			case CELLTICK_BR_INTEGRATION:
				configureCelltickBRIntegration();
				break;
			case BASE:
				Object[] baseModuleInstances = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(log, baseModuleDAL,
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(BASEnstNewUser),
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(BASEnstExistingUser));
				baseStates = (SMSAppModuleNavigationStates) baseModuleInstances[0];
				break;
			case HELP:
				Object[] helpModuleInstances = SMSAppModuleConfigurationHelp.getHelpModuleInstances(
					log, SHORT_CODE, APP_NAME,
					HELPphrNewUsersFallback, HELPphrExistingUsersFallback, HELPphrStateless, HELPphrStatefulHelpMessages, HELPphrComposite,
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(HELPnstPresentingCompositeHelp));
				helpStates    = (SMSAppModuleNavigationStatesHelp) helpModuleInstances[0];
				helpCommands  = (SMSAppModuleCommandsHelp)         helpModuleInstances[1];
				helpPhrasings = (SMSAppModulePhrasingsHelp)        helpModuleInstances[2];
				break;
			case SUBSCRIPTION:
				Object[] subscriptionModuleInstances = SMSAppModuleConfigurationSubscription.getSubscriptionModuleInstances(log, SHORT_CODE, APP_NAME, PRICE_TAG,
					SUBSCRIPTIONphrDoubleOptinStart, SUBSCRIPTIONphrDisagreeToSubscribe, SUBSCRIPTIONphrSuccessfullySubscribed, SUBSCRIPTIONphrCouldNotSubscribe,
					SUBSCRIPTIONphrUserRequestedUnsubscription, SUBSCRIPTIONphrLifecycleUnsubscription,
					baseModuleDAL, subscriptionDAL, subscriptionEngine, subscriptionToken,
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(SUBSCRIPTIONnstAnsweringDoubleOptin));
				subscriptionStates       = (SMSAppModuleNavigationStatesSubscription) subscriptionModuleInstances[0];
				subscriptionCommands     = (SMSAppModuleCommandsSubscription)         subscriptionModuleInstances[1];
				subscriptionPhrasings    = (SMSAppModulePhrasingsSubscription)        subscriptionModuleInstances[2];
				subscriptionEventsServer = (SMSAppModuleEventsSubscription)           subscriptionModuleInstances[3];
				break;
			case PROFILE:
				Object[] profileModuleInstances = SMSAppModuleConfigurationProfile.getProfileModuleInstances(log, SHORT_CODE, APP_NAME,
					PROFILEphrAskForFirstNickname, PROFILEphrAskForNewNickname, PROFILEphrAskForNicknameCancelation,
					PROFILEphrNicknameRegistrationNotification, PROFILEphrUserProfilePresentation, PROFILEphrNicknameNotFound,
					profileModuleDAL,
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(PROFILEnstRegisteringNickname));
				profileStates    = (SMSAppModuleNavigationStatesProfile) profileModuleInstances[0]; 
				profilecommands  = (SMSAppModuleCommandsProfile)         profileModuleInstances[1];
				profilePhrasings = (SMSAppModulePhrasingsProfile)        profileModuleInstances[2];
				break;
			case CHAT:
				Object[] chatModuleInstances = SMSAppModuleConfigurationChat.getChatModuleInstances(log, SHORT_CODE, APP_NAME,
					profilePhrasings,
					CHATphrPrivateMessage, CHATphrPrivateMessageDeliveryNotification, CHATphrDoNotKnowWhoYouAreChattingTo,
					profileModuleDAL, chatModuleDAL,
					EInstantVASCommandTriggers.get2DStringArrayFromEInstantVASCommandTriggersArray(CHATnstChattingWithSomeone));
				chatStates    = (SMSAppModuleNavigationStatesChat) chatModuleInstances[0];
				chatCommands  = (SMSAppModuleCommandsChat)         chatModuleInstances[1];
				chatPhrasings = (SMSAppModulePhrasingsChat)        chatModuleInstances[2];
				break;
			case HANGMAN:
				Object[] hangmanModuleInstances = SMSAppModuleConfigurationHangman.getHangmanModuleInstances(log, SHORT_CODE, APP_NAME,
					HANGMANwinningArt, HANGMANlosingArt, HANGMANheadCharacter, HANGMANleftArmCharacter, HANGMANchestCharacter,
					HANGMANrightArmCharacter, HANGMANleftLegCharacter, HANGMANrightLegCharacter, 
					HANGMANphr_gallowsArt, HANGMANphrAskOpponentNicknameOrPhone, HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation,
					HANGMANphrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation, HANGMANphrInvitationResponseForInvitingPlayer,                      
					HANGMANphrInvitationNotificationForInvitedPlayer, HANGMANphrTimeoutNotificationForInvitingPlayer,                     
					HANGMANphrInvitationRefusalResponseForInvitedPlayer, HANGMANphrInvitationRefusalNotificationForInvitingPlayer,           
					HANGMANphrNotAGoodWord, HANGMANphrWordProvidingPlayerMatchStart, HANGMANphrWordGuessingPlayerMatchStart, HANGMANphrWordProvidingPlayerStatus,                                
					HANGMANphrWordGuessingPlayerStatus, HANGMANphrWinningMessageForWordGuessingPlayer, HANGMANphrWinningMessageForWordProvidingPlayer,                     
					HANGMANphrLosingMessageForWordGuessingPlayer, HANGMANphrLosingMessageForWordProvidingPlayer, HANGMANphrMatchGiveupNotificationForWordGuessingPlayer,             
					HANGMANphrMatchGiveupNotificationForWordProvidingPlayer, HANGMANphrGuessingWordHelp,
					subscriptionEventsServer, baseModuleDAL, profileModuleDAL, hangmanModuleDAL, DEFAULT_NICKNAME_PREFIX,
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
		System.out.println(".");
		
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
			profilecommands.values,
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
	
	/** This one might be used for piracy control if APP_NAME, SHORT_CODE, etc becomes hardcoded, read from an encypted file, read from InstantVAS.com or something like that */
	private void configureCelltickBRIntegration() {
		moParser           = new SMSInCelltick(APP_NAME);
		mtSender           = new SMSOutCelltick(log, APP_NAME, SHORT_CODE, CELLTICK_MT_SERVICE_URL, MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS, MT_SERVICE_DELAY_BETWEEN_ATTEMPTS);
		subscriptionEngine = new CelltickLiveScreenSubscriptionAPI(log, CELLTICK_SUBSCRIBE_SERVICE_URL, CELLTICK_UNSUBSCRIBE_SERVICE_URL);
		subscriptionToken  = CELLTICK_SUBSCRIPTION_CHANNEL_NAME;
	}

	/** Set the default configuration for the Hangman SMS Application.
	 *  This function might be used to control piracy if it receives a parameter like "client" or "environment" --
	 *  which would fill in piracy protection variables for CELLTICK_BR or CELLTICK_TEST */
	public static void setHangmanDefaults() {
		
		REPORT_DATA_COLLECTOR_STRATEGY = EInstrumentationDataPours.POSTGRESQL_DATABASE;
		LOG_STRATEGY                   = EInstrumentationDataPours.CONSOLE;
		LOG_HANGMAN_FILE_PATH          = "";
		LOG_WEBAPP_FILE_PATH           = "";
		APP_NAME                       = "HANGMAN";
		SHORT_CODE                     = "993";
		PRICE_TAG                      = "$0.99";
		DEFAULT_NICKNAME_PREFIX        = "Guest";
		
		HTTP_CONNECTION_TIMEOUT_MILLIS = 30000;
		HTTP_READ_TIMEOUT_MILLIS       = 30000;
		
		CELLTICK_SUBSCRIBE_SERVICE_URL               = "http://localhost:8082/celltick/wapAPI?action=subpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
		CELLTICK_UNSUBSCRIBE_SERVICE_URL             = "http://localhost:8082/celltick/wapAPI?action=unsubpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
		CELLTICK_SUBSCRIPTION_CHANNEL_NAME           = "HangMan";
		CELLTICK_MT_SERVICE_URL                      = "http://localhost:15001/cgi-bin/sendsms";
		MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS = 5;
		MT_SERVICE_DELAY_BETWEEN_ATTEMPTS   = 5000;

		DATA_ACCESS_LAYER                           = EInstantVASDALs.POSTGRESQL;
		POSTGRESQL_HOSTNAME                         = "venus";
		POSTGRESQL_PORT                             = 5432;
		POSTGRESQL_DATABASE                         = "hangman";
		POSTGRESQL_USER                             = "hangman";
		POSTGRESQL_PASSWORD                         = "hangman";
		POSTGRESQL_CONNECTION_PROPERTIES            = PostgreSQLAdapter.CONNECTION_PROPERTIES;
		NUMBER_OF_CONCURRENT_CONNECTIONS            = PostgreSQLAdapter.CONNECTION_POOL_SIZE;
		POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTIONS = true;
		POSTGRESQL_SHOULD_DEBUG_QUERIES             = true;
		
		MO_PROCESSING_STRATEGY            = EEventProcessingStrategy.POSTGRESQL;
		MO_RAM_QUEUE_CAPACITY             = 1000;
		MO_FILE_QUEUE_LOG_DIRECTORY       = "";
		MO_FILE_QUEUE_POOLING_TIME        = 0;
		MO_POSTGRESQL_QUEUE_POOLING_TIME  = 0;
		MO_QUEUE_NUMBER_OF_WORKER_THREADS = 10;
		
		MT_PROCESSING_STRATEGY            = EEventProcessingStrategy.POSTGRESQL;
		MT_RAM_QUEUE_CAPACITY             = 1000;
		MT_FILE_QUEUE_LOG_DIRECTORY       = "";
		MT_FILE_QUEUE_POOLING_TIME        = 0;
		MT_POSTGRESQL_QUEUE_POOLING_TIME  = 0;
		MT_QUEUE_NUMBER_OF_WORKER_THREADS = 3;
		
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
		
		ENABLED_MODULES = new EInstantVASModules[] {
			EInstantVASModules.CELLTICK_BR_INTEGRATION,
			EInstantVASModules.BASE,
			EInstantVASModules.HELP,
			EInstantVASModules.SUBSCRIPTION,
			EInstantVASModules.PROFILE,
			EInstantVASModules.CHAT,
			EInstantVASModules.HANGMAN
		};

		// Help
		///////
		
		// phrasing
		HELPphrNewUsersFallback      = "no shits currently here";
		HELPphrExistingUsersFallback = "{{appName}}: unknown command. Please send HELP to see the full command set. Some examples: LIST to see online users; P [NICK] [MSG] to send a private message; " +
                                       "INVITE [NICK] to invite a listed player; INVITE [PHONE] to invite a friend of yours; PLAY to play with a random user. Choose an option and send it to {{shortCode}}";
		HELPphrStateless             = "You can play the {{appName}} game in 2 ways: guessing someone's word or inviting someone to play with your word " +
		                               "You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number " +
		                               "Every week, 1 lucky number is selected to win the prize. Send an option to 9714: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help";
		HELPphrComposite             = new String[] {""};
		HELPphrStatefulHelpMessages  = new String[0][0];
		
		// command patterns
		HELPtrgGlobalStartCompositeHelpDialog      = getConcatenationOf(cmdStartCompositeHelpDialog,      trgGlobalStartCompositeHelpDialog);
		HELPtrgLocalShowNextCompositeHelpMessage   = getConcatenationOf(cmdShowNextCompositeHelpMessage,  trgLocalShowNextCompositeHelpMessage);
		HELPtrgGlobalShowNewUsersFallbackHelp      = getConcatenationOf(cmdShowNewUsersFallbackHelp,      ".*");
		HELPtrgGlobalShowExistingUsersFallbackHelp = getConcatenationOf(cmdShowExistingUsersFallbackHelp, trgGlobalShowExistingUsersFallbackHelp);
		HELPtrgGlobalShowStatelessHelpMessage      = getConcatenationOf(cmdShowStatelessHelp,             trgGlobalShowStatelessHelpMessage);
		
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
		SUBSCRIPTIONphrDoubleOptinStart            = "You are at the {{appName}} game. To continue, you must subscribe. Send {{appName}} now to {{shortCode}} and compete for prizes. You will be charged at {{priceTag}} every week."; 
		SUBSCRIPTIONphrDisagreeToSubscribe         = "To fully use the {{appName}} game you need to subscribe. Please text {{appName}} to do so. In the mean time, you may consult the list of players. Text LIST to {{shortCode}}. Send RULES to learn how it worls.";
		SUBSCRIPTIONphrSuccessfullySubscribed      = "{{appName}}: Registration succeeded. Send HELP to {{shortCode}} to know the rules and how to play, or simply send PLAY to {{shortCode}}";
		SUBSCRIPTIONphrCouldNotSubscribe           = "";
		SUBSCRIPTIONphrUserRequestedUnsubscription = "You are now unsubscribed from the {{appName}} GAME and will no longer receive invitations to play nor lucky numbers. To join again, send {{appName}} to {{shortCode}}";
		SUBSCRIPTIONphrLifecycleUnsubscription     = "";
		
		// command patterns
		SUBSCRIPTIONtrgLocalStartDoubleOptin   = getConcatenationOf(cmdStartDoubleOptinProcess, ".*");
		SUBSCRIPTIONtrgLocalAcceptDoubleOptin  = getConcatenationOf(cmdSubscribe,               APP_NAME);
		SUBSCRIPTIONtrgLocalRefuseDoubleOptin  = getConcatenationOf(cmdDoNotAgreeToSubscribe,   trgLocalRefuseDoubleOptin);
		SUBSCRIPTIONtrgGlobalUnsubscribe       = getConcatenationOf(cmdUnsubscribe,             trgGlobalUnsubscribe);
		
		// Profile
		//////////
		
		// phrasing
		PROFILEphrAskForFirstNickname              = "---";
		PROFILEphrAskForNewNickname                = "---";
		PROFILEphrAskForNicknameCancelation        = "---";
		PROFILEphrNicknameRegistrationNotification = "{{appName}}: Name registered: {{registeredNickname}}. Send LIST to {{shortCode}} to see online players. NICK [NEW NICK] to change your name.";
		PROFILEphrUserProfilePresentation          = "{{appName}}: {{nickname}}: Subscribed; Online; RJ. Text INVITE {{nickname}} to play a hangman match; P {{nickname}} [MSG] to chat; LIST to see online players; P to play with a random user.";
		PROFILEphrNicknameNotFound                 = "{{appName}}: No player with nickname '{{nickname}}' was found. Maybe he/she changed it? Send LIST to {{shortCode}} to see online players";

		
		// command patterns
		PROFILEtrgGlobalStartAskForNicknameDialog = new String[] {"---"};
		PROFILEtrgLocalNicknameDialogCancelation  = new String[] {"---"};
		PROFILEtrgLocalRegisterNickname           = new String[] {"---"};
		PROFILEtrgGlobalRegisterNickname          = getConcatenationOf(cmdRegisterNickname, trgGlobalRegisterNickname);
		PROFILEtrgGlobalShowUserProfile           = getConcatenationOf(cmdShowUserProfile,  trgGlobalShowUserProfile);
		
		// Chat
		///////
		
		// phrasing
		CHATphrPrivateMessage                     = "{{senderNickname}}: {{senderMessage}} - To answer, text P {{senderNickname}} [MSG] to {{shortCode}}";
		CHATphrPrivateMessageDeliveryNotification = "{{appName}}: your message has been delivered to {{targetNickname}}. What can be the command that I'll suggest now?";
		CHATphrDoNotKnowWhoYouAreChattingTo       = "---";
		
		// command patterns
		CHATtrgGlobalSendPrivateMessage = getConcatenationOf(cmdSendPrivateMessage, trgGlobalSendPrivateMessage);
		CHATtrgLocalSendPrivateReply    = new String[] {"---"};

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
		HANGMANphrAskOpponentNicknameOrPhone                                = "{{appName}}: Name registered: {{invitingPlayerNickname}}. Send your friend's phone to {{shortCode}} or LIST to see online players. NICK [NEW NICK] to change your name.";
		HANGMANphrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation = "{{appName}}: Inviting {{opponentNickname}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number";
		HANGMANphrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation    = "{{appName}}: Your friend's phone: {{opponentPhoneNumber}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number";
		HANGMANphrInvitationResponseForInvitingPlayer                       = "{{invitedPlayerNickname}} was invited to play with you. while you wait, you can provoke {{invitedPlayerNickname}} by sending a message to {{shortCode}} (0.31+tax) or send SIGNUP to provoke for free how many times you want";
		HANGMANphrInvitationNotificationForInvitedPlayer                    = "{{appName}}: {{invitingPlayerNickname}} is inviting you for a hangman match. Do you accept? Send YES or NO to {{shortCode}} or P {{invitingPlayerNickname}} [MSG] to send him/her a message";
		HANGMANphrTimeoutNotificationForInvitingPlayer                      = "{{appName}}: {{invitedPlayerNickname}} is taking too long to answer. However, a new player, {{suggestedNewPlayersNickname}}, is available. Play with {{suggestedNewPlayersNickname}}? Send YES to {{shortCode}}";
		HANGMANphrInvitationRefusalResponseForInvitedPlayer                 = "The invitation to play the Hangman Game made by {{invitingPlayerNickname}} was refused. Send LIST to {{shortCode}} to see online users";
		HANGMANphrInvitationRefusalNotificationForInvitingPlayer            = "{{invitedPlayerNickname}} refused your invitation to play. Send LIST to 9714 and pick someone else";
		HANGMANphrNotAGoodWord                                              = "You selected '{{word}}'. This is possily not a good word. Please think of one only with A-Z letters, without accents, digits, ponctuation or any other special characters and send it to {{shortCode}}";
		HANGMANphrWordProvidingPlayerMatchStart                             = "Game started with {{wordGuessingPlayerNickname}}.\n{{gallowsArt}}Send P {{wordGuessingPlayerNickname}} [MSG] to give him/her clues";
		HANGMANphrWordGuessingPlayerMatchStart                              = "{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game";
		HANGMANphrWordProvidingPlayerStatus                                 = "{{wordGuessingPlayerNickname}} guessed letter {{guessedLetter}}\n{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend P {{wordGuessingPlayerNickname}} [MSG] to provoke him/her";
		HANGMANphrWordGuessingPlayerStatus                                  = "{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game";
		HANGMANphrWinningMessageForWordGuessingPlayer                       = "{{winningArt}}{{word}}! You got it! Here is your lucky number: xxx.xx.xx.xxx. Send: J to play or A for help";
		HANGMANphrWinningMessageForWordProvidingPlayer                      = "{{wordGuessingPlayerNickname}} guessed your word! P {{wordGuessingPlayerNickname}} [MSG] to provoke him/her or INVITE {{wordGuessingPlayerNickname}} for a new match";
		HANGMANphrLosingMessageForWordGuessingPlayer                        = "{{losingArt}}The word was {{word}}. Now challenge {{wordProvidingPlayerNickname}}: send INVITE {{wordProvidingPlayerNickname}} to {{shortCode}}";
		HANGMANphrLosingMessageForWordProvidingPlayer                       = "Good one! {{wordGuessingPlayerNickname}} wasn't able to guessed your word! P {{wordGuessingPlayerNickname}} [MSG] to provoke him/her or INVITE {{wordGuessingPlayerNickname}} for a new match";
		HANGMANphrMatchGiveupNotificationForWordGuessingPlayer              = "Your match with {{wordProvidingPlayerNickname}} has been canceled. Send P {{wordProvidingPlayerNickname}} [MSG] to talk to him/her or LIST to play with someone else";
		HANGMANphrMatchGiveupNotificationForWordProvidingPlayer             = "{{wordGuessingPlayerNickname}} cancelled the match. To find other users to play with, sent LIST to {{shortCode}}";
		HANGMANphrGuessingWordHelp                                          = "You are guessing a word on a {{appName}} match. Please text a letter or: END to quit the match; P [nick] [MSG] to ask for clues; LIST to see other online users";
		
		// command patterns
		HANGMANtrgGlobalInviteNicknameOrPhoneNumber      = getConcatenationOf(cmdInviteNicknameOrPhoneNumber, trgGlobalInviteNicknameOrPhoneNumber);
		HANGMANtrgLocalHoldMatchWord                     = getConcatenationOf(cmdHoldMatchWord,               trgLocalHoldMatchWord);
		HANGMANtrgLocalAcceptMatchInvitation             = getConcatenationOf(cmdAcceptMatchInvitation,       trgLocalAcceptMatchInvitation);
		HANGMANtrgLocalRefuseMatchInvitation             = getConcatenationOf(cmdRefuseMatchInvitation,       trgLocalRefuseMatchInvitation);
		HANGMANtrgLocalNewLetterOrWordSuggestionForHuman = getConcatenationOf(cmdSuggestLetterOrWordForHuman, trgLocalNewLetterOrWordSuggestion);
		HANGMANtrgLocalNewLetterOrWordSuggestionForBot   = getConcatenationOf(cmdSuggestLetterOrWordForBot,   trgLocalNewLetterOrWordSuggestion);
		
		// navigation
		/////////////
		
		/* Hoje em dia já é possível que os cmds sejam chamados de forma a imitar o serviço africano 'iText'. Por exemplo, para o envio de emails,
		 * o comando cmdSendEmail (que recebe dois parâmetros) pode ser usado, mesmo que se esteja enviando sempre email para uma única pessoa,
		 * situação na qual a regular expression deve ser trocada de algo como "M (%w+) (.*)" para "M (.*)" e a chamada do comando seria trocada de
		 * cmdSendEmail("$1", "$2") para cmdSendEmail("luiz@InstantVAS.com", "$1") -- esta forma de chamar ainda tem que ser implementada, na verdade. */
		
		// TODO aqui é preciso ter um array compartilhado com todos os global triggers pra gente não ter que ficar repetindo em cada estado -- pelo visto isso caducou...
		
		// base
		BASEnstNewUser = new EInstantVASCommandTriggers[] {
			EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalAcceptDoubleOptin,	// the double opt-in process starts with a broadcast message, outside the scope of this application
			EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
			EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalStartDoubleOptin,	// for some of known wrong commands, start the double opt-in process again
			EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,			// let the user answer to chats
			EInstantVASCommandTriggers.HELPtrgGlobalShowNewUsersFallbackHelp,	// fallback help
		};
		BASEnstExistingUser = new EInstantVASCommandTriggers[] {
			EInstantVASCommandTriggers.HELPtrgGlobalShowStatelessHelpMessage,
			EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
			EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
			EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
			EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
			EInstantVASCommandTriggers.HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
			EInstantVASCommandTriggers.HELPtrgGlobalShowNewUsersFallbackHelp,
		};

		// help
		HELPnstPresentingCompositeHelp = new EInstantVASCommandTriggers[] {
			EInstantVASCommandTriggers.HELPtrgGlobalStartCompositeHelpDialog,
			EInstantVASCommandTriggers.HELPtrgLocalShowNextCompositeHelpMessage,
			EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
		};
		
		// subscription
		SUBSCRIPTIONnstAnsweringDoubleOptin = new EInstantVASCommandTriggers[] {
			EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalAcceptDoubleOptin,
			EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalRefuseDoubleOptin,
			EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
			EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalStartDoubleOptin,
		};
		
		// profile
		PROFILEnstRegisteringNickname = new EInstantVASCommandTriggers[] {
			EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
			EInstantVASCommandTriggers.PROFILEtrgGlobalStartAskForNicknameDialog,
			EInstantVASCommandTriggers.PROFILEtrgLocalNicknameDialogCancelation,
			EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
			EInstantVASCommandTriggers.PROFILEtrgLocalRegisterNickname,
		};
		
		// chat
		CHATnstChattingWithSomeone = new EInstantVASCommandTriggers[] {
			EInstantVASCommandTriggers.CHATtrgLocalSendPrivateReply,
		};
		
		// hangman
		HANGMANnstEnteringMatchWord = new EInstantVASCommandTriggers[] {
			EInstantVASCommandTriggers.HANGMANtrgLocalHoldMatchWord,
			EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
		};
		HANGMANnstAnsweringToHangmanMatchInvitation = new EInstantVASCommandTriggers[] {
			EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
			EInstantVASCommandTriggers.HANGMANtrgLocalAcceptMatchInvitation,
			EInstantVASCommandTriggers.HANGMANtrgLocalRefuseMatchInvitation,
			EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
			EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
		};
		HANGMANnstGuessingWordFromHangmanHumanOpponent = new EInstantVASCommandTriggers[] {
			EInstantVASCommandTriggers.HANGMANtrgLocalNewLetterOrWordSuggestionForHuman,
			EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
			EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
		};
		HANGMANnstGuessingWordFromHangmanBotOpponent = new EInstantVASCommandTriggers[] {
			EInstantVASCommandTriggers.HANGMANtrgLocalNewLetterOrWordSuggestionForBot,
			EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
			EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
		};
	}

}