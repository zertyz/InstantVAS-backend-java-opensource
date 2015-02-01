package mutua.hangmansmsgame.config;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.DALFactory.EDataAccessLayers;
import mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents;
import mutua.icc.instrumentation.HangmanSMSGameInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.eventclients.InstrumentationProfilingEventsClient;
import mutua.icc.instrumentation.eventclients.InstrumentationReportDataCollectorEventClient;
import mutua.icc.instrumentation.pour.PourFactory;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.subscriptionengine.SubscriptionEngine;
import adapters.HTTPClientAdapter;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents.*;

/** <pre>
 * Configuration.java
 * ==================
 * (created by luiz, Jan 21, 2015)
 *
 * Defines common configuration variables for the framework
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class Configuration {
	
	/** to be defined by the request receivers (servlet, console, tests, icc app, ...) */
	public static Instrumentation<HangmanSMSGameInstrumentationProperties, String> log = null;
	
	/** also to be defined by the request receivers (servlet, console, tests, icc app, ...) */
	public static SubscriptionEngine SUBSCRIPTION_ENGINE;

	// Integration services
	///////////////////////

	@ConfigurableElement("Subscription service URL for 'CelltickLiveScreenSubscriptionAPI'")
	public static String SUBSCRIBE_SERVICE_URL               = "http://localhost:8082/celltick/wapAPI?action=subpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
	@ConfigurableElement("")
	public static String UNSUBSCRIBE_SERVICE_URL             = "http://localhost:8082/celltick/wapAPI?action=unsubpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
	@ConfigurableElement("")
	public static String SUBSCRIPTION_CHANNEL_NAME           = "HangMan";
	@ConfigurableElement("")
	public static String MT_SERVICE_URL                      = "http://localhost:15001/cgi-bin/sendsms";
	@ConfigurableElement("This is the original text")
	public static int    MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS = 5;
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.config.Configuration.MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS")
	public static long   MT_SERVICE_DELAY_BETWEEN_ATTEMPTS   = 5000;

	
	// TODO criar propriedades p/ QUEUE_STRATEGY=RAM|DATABASE|LOCAL_FILE
	// TODO SubscriptionEngine, SMSInParser e SMSOutParser devem receber parâmetros p/ registrar as SMSes em database
	// TODO DataAccessLayer específico p/ sessions
	
	// TODO criar includes p/ permitir a inclusão de i18n.international e i18n.brazil com frases, comandos, bots e palavras
	
	// HTTPClientAdapter
	////////////////////
	
	@ConfigurableElement(sameAs="adapters.HTTPClientAdapter.CONNECTION_TIMEOUT")
	public static int HTTP_CONNECTION_TIMEOUT_MILLIS = 30000;
	@ConfigurableElement(sameAs="adapters.HTTPClientAdapter.CONNECTION_TIMEOUT")
	public static int HTTP_READ_TIMEOUT_MILLIS       = 30000;
	
	
	// JDBCAdapter
	//////////////
	
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.dal.DALFactory.DEFAULT_DAL")
	public static EDataAccessLayers  DATA_ACCESS_LAYER          = EDataAccessLayers.RAM;
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.dal.DALFactory.DEFAULT_SESSIONS_DAL")
	public static EDataAccessLayers  SESSIONS_DATA_ACCESS_LAYER = DATA_ACCESS_LAYER;
	@ConfigurableElement("")
	public static String  POSTGRESQL_CONNECTION_HOSTNAME      = "zertyz.heliohost.org";
	@ConfigurableElement("")
	public static int     POSTGRESQL_CONNECTION_PORT          = 5432;
	@ConfigurableElement("")
	public static String  POSTGRESQL_CONNECTION_DATABASE_NAME = "zertyz_spikes";
	@ConfigurableElement("")
	public static String  POSTGRESQL_CONNECTION_USER          = "zertyz_user";
	@ConfigurableElement("")
	public static String  POSTGRESQL_CONNECTION_PASSWORD      = "spikes";
	@ConfigurableElement(sameAs="adapters.PostgreSQLAdapter.CONNECTION_PROPERTIES")
	public static String  POSTGRES_CONNECTION_PROPERTIES    = PostgreSQLAdapter.CONNECTION_PROPERTIES;
	@ConfigurableElement(sameAs="adapters.JDBCAdapter.SHOULD_DEBUG_QUERIES")
	public static Boolean POSTGRESQL_SHOULD_DEBUG_QUERIES   = JDBCAdapter.SHOULD_DEBUG_QUERIES;

	
	// SMS Framework parameters
	///////////////////////////
	
	/** empty string means console. They may share the same file */
	@ConfigurableElement("Where to store report data")
	public static EInstrumentationDataPours REPORT_DATA_COLLECTOR_STRATEGY = EInstrumentationDataPours.POSTGRESQL_DATABASE;
	@ConfigurableElement("Where to store log data")
	public static EInstrumentationDataPours LOG_STRATEGY                      = EInstrumentationDataPours.CONSOLE;
	@ConfigurableElement("File name to log Hangman Logic logs")
	public static String LOG_HANGMAN_FILE_PATH             = "";
	@ConfigurableElement("File name to log Hangman Web / Integration logs")
	public static String LOG_WEBAPP_FILE_PATH              = "";
	@ConfigurableElement("The name of the Hangman Game, as shown in the logs")
	public static String APPID                             = "HANGMAN";
	@ConfigurableElement("The short code of the Hangman Game")
	public static String SHORT_CODE                        = "9714";

	
	// SMS Application parameters
	/////////////////////////////
	
	@ConfigurableElement("")
	public static String DEFAULT_NICKNAME_PREFIX    = "Guest";
	@ConfigurableElement("")
	public static long   INVITATION_TIMEOUT_MILLIS  = (1000*60)*20;
	
	@ConfigurableElement("")
	public static String[] BOT_WORDS = {
		"CHIMPANZEE", "AGREGATE", "TWEEZERS",
	};


	// phrasing
	///////////
	
	@ConfigurableElement("")
	public static String[] shortHelp                                                      = {"(J) Play online; (C) Invite a friend or user; (R)anking; (A)Help"};
	@ConfigurableElement("")
	public static String[] gallowsArt                                                     = {"+-+\n| {{head}}\n|{{leftArm}}{{chest}}{{rightArm}}\n|{{leftLeg}} {{rightLeg}}\n|\n====\n"};
	@ConfigurableElement("")
	public static String[] winningArt                                                     = {"\\0/\n |\n/ \\\n"};
	@ConfigurableElement("")
	public static String[] losingArt                                                      = {"+-+\n| x\n|/|\\\n|/ \\\n====\n"};
	@ConfigurableElement("")
	public static String[] playersList                                                    = {"{{nick}} ({{state}}/{{numberOfLuckyNumbers}})"};
	@ConfigurableElement("")
	public static String[] INFOWelcome                                                    = {"Welcome to the HANGMAN game. Join and compete for prizes. Send HELP for free to {{shortCode}} to know the rules."};
	@ConfigurableElement("")
	public static String[] INFOFullHelp                                                   = {"1/3: You can play the HANGMAN game in 2 ways: guessing someone's word or inviting someone to play with your word",
	                                                                                         "2/3: You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number",
	                                                                                         "3/3: Every week, 1 lucky number is selected to win the prize. Send an option to {{shortCode}}: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help"};
	@ConfigurableElement("")
	public static String[] INFOWelcomeMenu                                                = {"Pick an option. Send to {{shortCode}}: {{shortHelp}}"};
	@ConfigurableElement("")
	public static String[] INFOCouldNotRegister                                           = {"HANGMAN: You could not be registered at this time. Please try again later."};
	@ConfigurableElement("")
	public static String[] PROFILEView                                                    = {"HANGMAN: {{nick}}: Subscribed, {{state}}, {{numberOfLuckyNumbers}} lucky numbers. Send SIGNUP to provoke for free or INVITE {{nick}} for a match."};
	@ConfigurableElement("")
	public static String[] PROFILEFullfillingAskNick                                      = {"HANGMAN: To play with a friend, u need first to sign your name. Now send your name (8 letters or numbers max.) to {{shortCode}}"};
	@ConfigurableElement("")
	public static String[] PROFILENickRegisteredNotification                              = {"HANGMAN: Name registered: {{newNickname}}. Send LIST to {{shortCode}} to see online players. NICK [NEW NICK] to change your name."};
	@ConfigurableElement("")
	public static String[] PLAYINGWordProvidingPlayerStart                                = {"Game started with {{wordGuessingPlayerNick}}.\n{{gallowsArt}}Send P {{wordGuessingPlayerNick}} MSG to give him/her clues"};
	@ConfigurableElement("")
	public static String[] PLAYINGWordGuessingPlayerStart                                 = {"{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"};
	@ConfigurableElement("")
	public static String[] PLAYINGWordGuessingPlayerStatus                                = {"{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"};
	@ConfigurableElement("")
	public static String[] PLAYINGWordProvidingPlayerStatus                               = {"{{nick}} guessed letter {{guessedLetter}}\n{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend P {{nick}} MSG to provoke him/her"};
	@ConfigurableElement("")
	public static String[] PLAYINGWinningMessageForWordGuessingPlayer                     = {"{{winningArt}}{{word}}! You got it! Here is your lucky number: {{luckyNumber}}. Send: J to play or A for help"};
	@ConfigurableElement("")
	public static String[] PLAYINGWinningMessageForWordProvidingPlayer                    = {"{{wordGuessingPlayerNick}} guessed your word! P {{wordGuessingPlayerNick}} MSG to provoke him/her or INVITE {{wordGuessingPlayerNick}} for a new match"};
	@ConfigurableElement("")
	public static String[] PLAYINGLosingMessageForWordGuessingPlayer                      = {"{{losingArt}}The word was {{word}}. Now challenge {{wordProvidingPlayerNick}}: send INVITE {{wordProvidingPlayerNick}} to {{shortCode}}"};
	@ConfigurableElement("")
	public static String[] PLAYINGLosingMessageForWordProvidingPlayer                     = {"Good one! {{wordGuessingPlayerNick}} wasn't able to guessed your word! P {{wordGuessingPlayerNick}} MSG to provoke him/her or INVITE {{wordGuessingPlayerNick}} for a new match"};
	@ConfigurableElement("")
	public static String[] PLAYINGMatchGiveupNotificationForWordProvidingPlayer           = {"{{wordGuessingPlayerNick}} cancelled the match. To find other users to play with, sent LIST to {{shortCode}}"};
	@ConfigurableElement("")
	public static String[] PLAYINGMatchGiveupNotificationForWordGuessingPlayer            = {"Your match with {{wordProvidingPlayerNick}} has been canceled. Send P {{wordProvidingPlayerNick}} MSG to talk to him/her or LIST to play with someone else"};
	@ConfigurableElement("")
	public static String[] INVITINGAskOpponentNickOrPhone                                 = {"HANGMAN: Name registered: {{invitingPlayerNickname}}. Send your friend's phone to {{shortCode}} or LIST to see online players. NICK [NEW NICK] to change your name."};
	@ConfigurableElement("")
	public static String[] INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation  = {"HANGMAN: Inviting {{opponentNickname}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"};
	@ConfigurableElement("")
	public static String[] INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation = {"HANGMAN: Your friend's phone: {{opponentPhoneNumber}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"};
	@ConfigurableElement("")
	public static String[] INVITINGInvitationNotificationForInvitingPlayer                = {"{{invitedPlayerNickname}} was invited to play with you. while you wait, you can provoke {{invitedPlayerNickname}} by sending a message to {{shortCode}} (0.31+tax) or send SIGNUP to provoke for free how many times you want"};
	@ConfigurableElement("")
	public static String[] INVITINGTimeoutNotificationForInvitingPlayer                   = {"{{invitedPlayerNickname}} is taking too long to answer. However, a new player, {{suggestedNewPlayersNickname}}, is available. Play with {{suggestedNewPlayersNickname}}? Send YES to {{shortCode}}"};
	@ConfigurableElement("")
	public static String[] INVITINGInvitationNotificationForInvitedPlayer                 = {"HANGMAN: {{invitingPlayerNickname}} is inviting you for a hangman match. Do you accept? Send YES to {{shortCode}} or PROFILE to see {{invitingPlayerNickname}} information"};
	@ConfigurableElement("")
	public static String[] INVITINGInvitationRefusalNotificationForInvitingPlayer         = {"{{invitedPlayerNickname}} refused your invitation to play. Send LIST to 9714 and pick someone else"};
	@ConfigurableElement("")
	public static String[] INVITINGInvitationRefusalNotificationForInvitedPlayer          = {"The invitation to play the Hangman Game made by {{invitingPlayerNickname}} was refused. Send LIST to {{shortCode}} to see online users"};
	@ConfigurableElement("")
	public static String[] LISTINGShowPlayers                                             = {"{{playersList}}. To play, send INVITE [NICK] to {{shortCode}}; MORE for more players or PROFILE [NICK]"};
	@ConfigurableElement("")
	public static String[] LISTINGNoMorePlayers                                           = {"There is no more online players to show. Send P [NICK] [MSG] to provoke or INVITE [PHONE] to invite a friend of yours to play the Hangman Game."};
	@ConfigurableElement("")
	public static String[] PROVOKINGDeliveryNotification                                  = {"Your message was sent to {{destinationNick}}. Wait for the answer or provoke other players sending P [NICK] [MSG] to {{shortCode}}. Send SIGNUP to provoke for free."};
	@ConfigurableElement("")
	public static String[] PROVOKINGSendMessage                                           = {"{{sourceNick}}: {{message}} - Answer by sending P {{sourceNick}} [MSG] to {{shortCode}}"};
	@ConfigurableElement("")
	public static String[] PROVOKINGNickNotFound                                          = {"No player with nickname '{{nickname}}' was found. Maybe he/she changed it? Send LIST to {{shortCode}} to see online players"};
	@ConfigurableElement("")
	public static String[] UNSUBSCRIBINGUnsubscriptionNotification                        = {"You are now unsubscribed from the HANGMAN GAME and will no longer receive invitations to play nor lucky numbers. To join again, send HANGMAN to {{shortCode}}"};

	
	///////
	
	static {
		applyConfiguration();
	}
	
	public static void applyConfiguration() {
		
		if (log == null) try {
			log = new Instrumentation<HangmanSMSGameInstrumentationProperties, String>(APPID, HangmanSMSGameInstrumentationProperties.IP_PHONE, HangmanSMSGameInstrumentationEvents.values());
        	InstrumentationProfilingEventsClient          instrumentationProfilingEventsClient           = new InstrumentationProfilingEventsClient(log, EInstrumentationDataPours.CONSOLE);
        	InstrumentationReportDataCollectorEventClient instrumentationReportDataCollectorEventsClient = new InstrumentationReportDataCollectorEventClient(log, EInstrumentationDataPours.CONSOLE,
					IE_REQUEST_FROM_NEW_USER.getInstrumentableEvent(), IE_REQUEST_FROM_EXISTING_USER.getInstrumentableEvent());
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
			log.addInstrumentationPropagableEventsClient(instrumentationReportDataCollectorEventsClient);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}
		
		HTTPClientAdapter.CONNECTION_TIMEOUT = HTTP_CONNECTION_TIMEOUT_MILLIS;
		HTTPClientAdapter.READ_TIMEOUT       = HTTP_READ_TIMEOUT_MILLIS;

		JDBCAdapter.SHOULD_DEBUG_QUERIES        = POSTGRESQL_SHOULD_DEBUG_QUERIES;
		PostgreSQLAdapter.CONNECTION_PROPERTIES = POSTGRES_CONNECTION_PROPERTIES;

		DALFactory.DEFAULT_DAL          = DATA_ACCESS_LAYER;
		DALFactory.DEFAULT_SESSIONS_DAL = SESSIONS_DATA_ACCESS_LAYER;
		HangmanSMSGamePostgreSQLAdapters.log = log;
		HangmanSMSGamePostgreSQLAdapters.HOSTNAME = POSTGRESQL_CONNECTION_HOSTNAME;
		HangmanSMSGamePostgreSQLAdapters.PORT     = POSTGRESQL_CONNECTION_PORT;
		HangmanSMSGamePostgreSQLAdapters.DATABASE = POSTGRESQL_CONNECTION_DATABASE_NAME;
		HangmanSMSGamePostgreSQLAdapters.USER     = POSTGRESQL_CONNECTION_USER;
		HangmanSMSGamePostgreSQLAdapters.PASSWORD = POSTGRESQL_CONNECTION_PASSWORD;
		
		switch (REPORT_DATA_COLLECTOR_STRATEGY) {
			case NETWORK:
				log.reportDebug("Creating a NETWORK for REPORT_DATA_COLLECTOR_STRATEGY -- not implemented. Falling back...");
			case COMPRESSED_ROLLING_FILE:
				log.reportDebug("Creating a COMPRESSED_ROLLING_FILE for REPORT_DATA_COLLECTOR_STRATEGY -- not implemented. Falling back...");
			case ROLLING_FILE:
				log.reportDebug("Creating a ROLLING_FILE for REPORT_DATA_COLLECTOR_STRATEGY -- not implemented. Falling back...");
			case POSTGRESQL_DATABASE:
				log.reportDebug("Error creating a POSTGRESQL_DATABASE for REPORT_DATA_COLLECTOR_STRATEGY -- Falling back...");
			case CONSOLE:
				log.reportDebug("Creating a CONSOLE for REPORT_DATA_COLLECTOR_STRATEGY");
				break;
			default:
				throw new RuntimeException("Don't know '"+REPORT_DATA_COLLECTOR_STRATEGY.name()+"' REPORT_DATA_COLLECTOR_STRATEGY");
		}
		
		switch (LOG_STRATEGY) {
		case NETWORK:
			log.reportDebug("Creating a NETWORK for LOG_STRATEGY -- not implemented. Falling back...");
		case COMPRESSED_ROLLING_FILE:
			log.reportDebug("Creating a COMPRESSED_ROLLING_FILE for LOG_STRATEGY -- not implemented. Falling back...");
		case ROLLING_FILE:
			log.reportDebug("Creating a ROLLING_FILE for LOG_STRATEGY -- not implemented. Falling back...");
		case POSTGRESQL_DATABASE:
			log.reportDebug("Error creating a POSTGRESQL_DATABASE for LOG_STRATEGY -- Falling back...");
		case CONSOLE:
			log.reportDebug("Creating a CONSOLE for LOG_STRATEGY");
			break;
		default:
			throw new RuntimeException("Don't know '"+LOG_STRATEGY.name()+"' LOG_STRATEGY");
	}

	}

}

