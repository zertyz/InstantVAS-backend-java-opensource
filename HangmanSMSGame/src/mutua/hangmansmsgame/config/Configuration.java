package mutua.hangmansmsgame.config;

import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents.IE_REQUEST_FROM_EXISTING_USER;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents.IE_REQUEST_FROM_NEW_USER;

import java.lang.reflect.Field;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.DALFactory.EDataAccessLayers;
import mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters;
import mutua.hangmansmsgame.i18n.IPhraseology.EPhraseNames;
import mutua.hangmansmsgame.smslogic.StateDetails;
import mutua.hangmansmsgame.smslogic.StateDetails.ECommandPatterns;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents;
import mutua.icc.instrumentation.HangmanSMSGameInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.eventclients.InstrumentationProfilingEventsClient;
import mutua.icc.instrumentation.eventclients.InstrumentationReportDataCollectorEventClient;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.subscriptionengine.SubscriptionEngine;
import adapters.HTTPClientAdapter;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;

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
	@ConfigurableElement("the number of times 'sendMessage' will attempt to send the message before reporting it as unsendable")
	public static int    MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS = 5;
	@ConfigurableElement("the number of milliseconds 'sendMessage' will wait between retry attempts")
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
	public static EDataAccessLayers  DATA_ACCESS_LAYER             = EDataAccessLayers.RAM;
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.dal.DALFactory.DEFAULT_SESSIONS_DAL")
	public static EDataAccessLayers  SESSIONS_DATA_ACCESS_LAYER    = DATA_ACCESS_LAYER;
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters.HOSTNAME")
	public static String  POSTGRESQL_CONNECTION_HOSTNAME           = "zertyz.heliohost.org";
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters.PORT")
	public static int     POSTGRESQL_CONNECTION_PORT               = 5432;
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters.DATABASE")
	public static String  POSTGRESQL_CONNECTION_DATABASE_NAME      = "zertyz_spikes";
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters.USER")
	public static String  POSTGRESQL_CONNECTION_USER               = "zertyz_user";
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters.PASSWORD")
	public static String  POSTGRESQL_CONNECTION_PASSWORD           = "spikes";
	@ConfigurableElement(sameAs="adapters.PostgreSQLAdapter.CONNECTION_PROPERTIES")
	public static String  POSTGRESQL_CONNECTION_PROPERTIES         = PostgreSQLAdapter.CONNECTION_PROPERTIES;
	@ConfigurableElement(sameAs="adapters.PostgreSQLAdapter.ALLOW_DATABASE_ADMINISTRATION")
	public static boolean POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION = PostgreSQLAdapter.ALLOW_DATABASE_ADMINISTRATION;
	@ConfigurableElement(sameAs="adapters.JDBCAdapter.SHOULD_DEBUG_QUERIES")
	public static boolean POSTGRESQL_SHOULD_DEBUG_QUERIES          = JDBCAdapter.SHOULD_DEBUG_QUERIES;

	
	// SMS Framework parameters
	///////////////////////////
	
	@ConfigurableElement("Where to store report data")
	public static EInstrumentationDataPours REPORT_DATA_COLLECTOR_STRATEGY = EInstrumentationDataPours.POSTGRESQL_DATABASE;
	@ConfigurableElement("Where to store log data")
	public static EInstrumentationDataPours LOG_STRATEGY                   = EInstrumentationDataPours.CONSOLE;
	@ConfigurableElement("File name to log Hangman Logic logs")
	public static String LOG_HANGMAN_FILE_PATH                             = "";
	@ConfigurableElement("File name to log Hangman Web / Integration logs")
	public static String LOG_WEBAPP_FILE_PATH                              = "";
	@ConfigurableElement("The name of the Hangman Game, as shown in the logs")
	public static String APPID                                             = "HANGMAN";
	@ConfigurableElement("The short code of the Hangman Game")
	public static String SHORT_CODE                                        = "9714";

	
	// SMS Application parameters
	/////////////////////////////
	
	@ConfigurableElement("Default prefix for invited & new users -- The suffix are the last 4 phone number digits")
	public static String DEFAULT_NICKNAME_PREFIX    = "Guest";
	
	@ConfigurableElement("Words to cycle through when playing with the computer")
	public static String[] BOT_WORDS = {
		"CHIMPANZEE", "AGREGATE", "TWEEZERS",
	};
	
	@ConfigurableElement("Bot names -- should be backed by real user names in order for chat & invitation to work properly")
	public static String[] BOT_USERS = {
		"DomBot",
	};


	// phrasing
	///////////
	
	@ConfigurableElement("")
	public static String[] shortHelp                                                      = EPhraseNames.shortHelp.getTexts();
	@ConfigurableElement("")
	public static String[] gallowsArt                                                     = EPhraseNames.gallowsArt.getTexts();
	@ConfigurableElement("")
	public static String[] winningArt                                                     = EPhraseNames.winningArt.getTexts();
	@ConfigurableElement("")
	public static String[] losingArt                                                      = EPhraseNames.losingArt.getTexts();
	@ConfigurableElement("")
	public static String[] playersList                                                    = EPhraseNames.playersList.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INFOWelcome")
	public static String[] INFOWelcome                                                    = EPhraseNames.INFOWelcome.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INFOFallbackNewUsersHelp")
	public static String[] INFOFallbackNewUsersHelp                                       = EPhraseNames.INFOFallbackNewUsersHelp.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INFOFallbackExistingUsersHelp")
	public static String[] INFOFallbackExistingUsersHelp                                  = EPhraseNames.INFOFallbackExistingUsersHelp.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INFOFullHelp")
	public static String[] INFOFullHelp                                                   = EPhraseNames.INFOFullHelp.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INFOWelcomeMenu")
	public static String[] INFOWelcomeMenu                                                = EPhraseNames.INFOWelcomeMenu.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INFOCouldNotRegister")
	public static String[] INFOCouldNotRegister                                           = EPhraseNames.INFOCouldNotRegister.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PROFILEView")
	public static String[] PROFILEView                                                    = EPhraseNames.PROFILEView.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PROFILEFullfillingAskNick")
	public static String[] PROFILEFullfillingAskNick                                      = EPhraseNames.PROFILEFullfillingAskNick.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PROFILENickRegisteredNotification")
	public static String[] PROFILENickRegisteredNotification                              = EPhraseNames.PROFILENickRegisteredNotification.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PLAYINGWordProvidingPlayerStart")
	public static String[] PLAYINGWordProvidingPlayerStart                                = EPhraseNames.PLAYINGWordProvidingPlayerStart.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PLAYINGWordGuessingPlayerStart")
	public static String[] PLAYINGWordGuessingPlayerStart                                 = EPhraseNames.PLAYINGWordGuessingPlayerStart.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PLAYINGWordGuessingPlayerStatus")
	public static String[] PLAYINGWordGuessingPlayerStatus                                = EPhraseNames.PLAYINGWordGuessingPlayerStatus.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PLAYINGWordProvidingPlayerStatus")
	public static String[] PLAYINGWordProvidingPlayerStatus                               = EPhraseNames.PLAYINGWordProvidingPlayerStatus.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PLAYINGWinningMessageForWordGuessingPlayer")
	public static String[] PLAYINGWinningMessageForWordGuessingPlayer                     = EPhraseNames.PLAYINGWinningMessageForWordGuessingPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PLAYINGWinningMessageForWordProvidingPlayer")
	public static String[] PLAYINGWinningMessageForWordProvidingPlayer                    = EPhraseNames.PLAYINGWinningMessageForWordProvidingPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PLAYINGLosingMessageForWordGuessingPlayer")
	public static String[] PLAYINGLosingMessageForWordGuessingPlayer                      = EPhraseNames.PLAYINGLosingMessageForWordGuessingPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PLAYINGLosingMessageForWordProvidingPlayer")
	public static String[] PLAYINGLosingMessageForWordProvidingPlayer                     = EPhraseNames.PLAYINGLosingMessageForWordProvidingPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PLAYINGMatchGiveupNotificationForWordProvidingPlayer")
	public static String[] PLAYINGMatchGiveupNotificationForWordProvidingPlayer           = EPhraseNames.PLAYINGMatchGiveupNotificationForWordProvidingPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PLAYINGMatchGiveupNotificationForWordGuessingPlayer")
	public static String[] PLAYINGMatchGiveupNotificationForWordGuessingPlayer            = EPhraseNames.PLAYINGMatchGiveupNotificationForWordGuessingPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INVITINGAskOpponentNickOrPhone")
	public static String[] INVITINGAskOpponentNickOrPhone                                 = EPhraseNames.INVITINGAskOpponentNickOrPhone.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation")
	public static String[] INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation  = EPhraseNames.INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation")
	public static String[] INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation = EPhraseNames.INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INVITINGInvitationNotificationForInvitingPlayer")
	public static String[] INVITINGInvitationNotificationForInvitingPlayer                = EPhraseNames.INVITINGInvitationNotificationForInvitingPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INVITINGTimeoutNotificationForInvitingPlayer")
	public static String[] INVITINGTimeoutNotificationForInvitingPlayer                   = EPhraseNames.INVITINGTimeoutNotificationForInvitingPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INVITINGInvitationNotificationForInvitedPlayer")
	public static String[] INVITINGInvitationNotificationForInvitedPlayer                 = EPhraseNames.INVITINGInvitationNotificationForInvitedPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INVITINGInvitationRefusalNotificationForInvitingPlayer")
	public static String[] INVITINGInvitationRefusalNotificationForInvitingPlayer         = EPhraseNames.INVITINGInvitationRefusalNotificationForInvitingPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INVITINGInvitationRefusalNotificationForInvitedPlayer")
	public static String[] INVITINGInvitationRefusalNotificationForInvitedPlayer          = EPhraseNames.INVITINGInvitationRefusalNotificationForInvitedPlayer.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.INVITINGNotAGoodWord")
	public static String[] INVITINGNotAGoodWord                                           = EPhraseNames.INVITINGNotAGoodWord.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.LISTINGShowPlayers")
	public static String[] LISTINGShowPlayers                                             = EPhraseNames.LISTINGShowPlayers.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.LISTINGNoMorePlayers")
	public static String[] LISTINGNoMorePlayers                                           = EPhraseNames.LISTINGNoMorePlayers.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PROVOKINGDeliveryNotification")
	public static String[] PROVOKINGDeliveryNotification                                  = EPhraseNames.PROVOKINGDeliveryNotification.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PROVOKINGSendMessage")
	public static String[] PROVOKINGSendMessage                                           = EPhraseNames.PROVOKINGSendMessage.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.PROVOKINGNickNotFound")
	public static String[] PROVOKINGNickNotFound                                          = EPhraseNames.PROVOKINGNickNotFound.getTexts();
	@ConfigurableElement(sameAsMethod="mutua.hangmansmsgame.i18n.IPhraseology.UNSUBSCRIBINGUnsubscriptionNotification")
	public static String[] UNSUBSCRIBINGUnsubscriptionNotification                        = EPhraseNames.UNSUBSCRIBINGUnsubscriptionNotification.getTexts();
	
	
	// command patterns
	///////////////////
	
	// a nice tool to test the patterns: http://www.regexplanet.com/advanced/java/index.html
	
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.SHOW_FALLBACK_NEW_USERS_HELP")
	public static String[] SHOW_FALLBACK_NEW_USERS_HELP      = ECommandPatterns.SHOW_FALLBACK_NEW_USERS_HELP     .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.SHOW_FALLBACK_EXISTING_USERS_HELP")
	public static String[] SHOW_FALLBACK_EXISTING_USERS_HELP = ECommandPatterns.SHOW_FALLBACK_EXISTING_USERS_HELP.getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.SHOW_FULL_HELP_MESSAGE")
	public static String[] SHOW_FULL_HELP_MESSAGE            = ECommandPatterns.SHOW_FULL_HELP_MESSAGE           .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.SHOW_PROFILE")
	public static String[] SHOW_PROFILE                      = ECommandPatterns.SHOW_PROFILE                     .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.DEFINE_NICK")
	public static String[] DEFINE_NICK                       = ECommandPatterns.DEFINE_NICK                      .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.LIST_USERS")
	public static String[] LIST_USERS                        = ECommandPatterns.LIST_USERS                       .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.PROVOKE")
	public static String[] PROVOKE                           = ECommandPatterns.PROVOKE                          .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.START_INVITATION_PROCESS")
	public static String[] START_INVITATION_PROCESS          = ECommandPatterns.START_INVITATION_PROCESS         .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.INVITE_NICK_OR_PHONE")
	public static String[] INVITE_NICK_OR_PHONE              = ECommandPatterns.INVITE_NICK_OR_PHONE             .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.PLAY_WITH_RANDOM_USER_OR_BOT")
	public static String[] PLAY_WITH_RANDOM_USER_OR_BOT      = ECommandPatterns.PLAY_WITH_RANDOM_USER_OR_BOT     .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.UNSUBSCRIBE")
	public static String[] UNSUBSCRIBE                       = ECommandPatterns.UNSUBSCRIBE                      .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.SHOW_WELCOME_MESSAGE")
	public static String[] SHOW_WELCOME_MESSAGE              = ECommandPatterns.SHOW_WELCOME_MESSAGE             .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.NO_ANSWER")
	public static String[] NO_ANSWER                         = ECommandPatterns.NO_ANSWER                        .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.HOLD_OPPONENT_PHONE")
	public static String[] HOLD_OPPONENT_PHONE               = ECommandPatterns.HOLD_OPPONENT_PHONE              .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.HOLD_OPPONENT_NICK")
	public static String[] HOLD_OPPONENT_NICK                = ECommandPatterns.HOLD_OPPONENT_NICK               .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.HOLD_MATCH_WORD")
	public static String[] HOLD_MATCH_WORD                   = ECommandPatterns.HOLD_MATCH_WORD                  .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.ACCEPT_INVITATION")
	public static String[] ACCEPT_INVITATION                 = ECommandPatterns.ACCEPT_INVITATION                .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.REFUSE_INVITATION")
	public static String[] REFUSE_INVITATION                 = ECommandPatterns.REFUSE_INVITATION                .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.INVITATION_TIMEOUT")
	public static long     INVITATION_TIMEOUT                = ECommandPatterns.INVITATION_TIMEOUT               .getTimeout(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.SUGGEST_LETTER_OR_WORD_FOR_HUMAN")
	public static String[] SUGGEST_LETTER_OR_WORD_FOR_HUMAN  = ECommandPatterns.SUGGEST_LETTER_OR_WORD_FOR_HUMAN .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.CANCEL_HUMAN_GAME")
	public static String[] CANCEL_HUMAN_GAME                 = ECommandPatterns.CANCEL_HUMAN_GAME                .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.SUGGEST_LETTER_OR_WORD_FOR_BOT")
	public static String[] SUGGEST_LETTER_OR_WORD_FOR_BOT    = ECommandPatterns.SUGGEST_LETTER_OR_WORD_FOR_BOT   .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.CANCEL_BOT_GAME")
	public static String[] CANCEL_BOT_GAME                   = ECommandPatterns.CANCEL_BOT_GAME                  .getRegularExpressions(); 
	@ConfigurableElement(sameAs="mutua.hangmansmsgame.smslogic.CommandDetails.LIST_MORE_USERS")
	public static String[] LIST_MORE_USERS                   = ECommandPatterns.LIST_MORE_USERS                  .getRegularExpressions();

	
	public static void applyConfiguration() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		if (log == null) try {
			log = new Instrumentation<HangmanSMSGameInstrumentationProperties, String>(APPID + " Logic", HangmanSMSGameInstrumentationProperties.IP_PHONE,
					LOG_STRATEGY, LOG_HANGMAN_FILE_PATH, HangmanSMSGameInstrumentationEvents.values());
        	InstrumentationProfilingEventsClient          instrumentationProfilingEventsClient           = new InstrumentationProfilingEventsClient(log, LOG_STRATEGY, LOG_HANGMAN_FILE_PATH);
        	InstrumentationReportDataCollectorEventClient instrumentationReportDataCollectorEventsClient = new InstrumentationReportDataCollectorEventClient(log,
        			REPORT_DATA_COLLECTOR_STRATEGY, POSTGRESQL_CONNECTION_HOSTNAME+";"+POSTGRESQL_CONNECTION_PORT+";"+POSTGRESQL_CONNECTION_DATABASE_NAME+";"+POSTGRESQL_CONNECTION_USER+";"+POSTGRESQL_CONNECTION_PASSWORD,
					IE_REQUEST_FROM_NEW_USER.getInstrumentableEvent(), IE_REQUEST_FROM_EXISTING_USER.getInstrumentableEvent());
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
			log.addInstrumentationPropagableEventsClient(instrumentationReportDataCollectorEventsClient);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}
		
		HTTPClientAdapter.CONNECTION_TIMEOUT = HTTP_CONNECTION_TIMEOUT_MILLIS;
		HTTPClientAdapter.READ_TIMEOUT       = HTTP_READ_TIMEOUT_MILLIS;

		JDBCAdapter.SHOULD_DEBUG_QUERIES                = POSTGRESQL_SHOULD_DEBUG_QUERIES;
		PostgreSQLAdapter.CONNECTION_PROPERTIES         = POSTGRESQL_CONNECTION_PROPERTIES;
		PostgreSQLAdapter.ALLOW_DATABASE_ADMINISTRATION = POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION;

		DALFactory.DEFAULT_DAL          = DATA_ACCESS_LAYER;
		DALFactory.DEFAULT_SESSIONS_DAL = SESSIONS_DATA_ACCESS_LAYER;
		HangmanSMSGamePostgreSQLAdapters.log = log;
		HangmanSMSGamePostgreSQLAdapters.HOSTNAME = POSTGRESQL_CONNECTION_HOSTNAME;
		HangmanSMSGamePostgreSQLAdapters.PORT     = POSTGRESQL_CONNECTION_PORT;
		HangmanSMSGamePostgreSQLAdapters.DATABASE = POSTGRESQL_CONNECTION_DATABASE_NAME;
		HangmanSMSGamePostgreSQLAdapters.USER     = POSTGRESQL_CONNECTION_USER;
		HangmanSMSGamePostgreSQLAdapters.PASSWORD = POSTGRESQL_CONNECTION_PASSWORD;
		
		// set phrases
		for (EPhraseNames i18nPhrase : EPhraseNames.values()) {
			Field configurationPhrase = Configuration.class.getField(i18nPhrase.name());
			String[] texts = (String[])configurationPhrase.get(null);
			i18nPhrase.setTexts(texts);
		}
		
		// set command patterns
		for (ECommandPatterns commandPatterns : ECommandPatterns.values()) {
			Field configurationPattern = Configuration.class.getField(commandPatterns.name());
			// command patterns can be from two types: long, if specifying a timeout; String[] when specifying a regular expression list
			if (configurationPattern.getType() == Long.TYPE) {
				long timeout = (Long)configurationPattern.get(null);
				commandPatterns.setTimeout(timeout);
			} else {
				String[] regularExpressions = (String[])configurationPattern.get(null);
				commandPatterns.setRegularExpressions(regularExpressions);
			}
		}
		// apply new command patterns
		try {
			StateDetails.defineFields();
		} catch (Throwable t) {
			System.out.println("ERROR -- could not run 'StateDetails.defineFields'. Command Patterns reloading didn't work and probably left it in an inconsistent state. Please restart the Hangman Game");
			t.printStackTrace();
		}
		
	}

}