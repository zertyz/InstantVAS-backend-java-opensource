package mutua.hangmansmsgame.config;

import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents.IE_REQUEST_FROM_EXISTING_USER;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents.IE_REQUEST_FROM_NEW_USER;

import java.lang.reflect.Field;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.DALFactory.EDataAccessLayers;
import mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters;
import mutua.hangmansmsgame.i18n.IPhraseology.EPhraseNames;
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
	public static String[] shortHelp                                                      = EPhraseNames.shortHelp.getTexts();
	@ConfigurableElement("")
	public static String[] gallowsArt                                                     = EPhraseNames.gallowsArt.getTexts();
	@ConfigurableElement("")
	public static String[] winningArt                                                     = EPhraseNames.winningArt.getTexts();
	@ConfigurableElement("")
	public static String[] losingArt                                                      = EPhraseNames.losingArt.getTexts();
	@ConfigurableElement("")
	public static String[] playersList                                                    = EPhraseNames.playersList.getTexts();
	@ConfigurableElement("")
	public static String[] INFOWelcome                                                    = EPhraseNames.INFOWelcome.getTexts();
	@ConfigurableElement("")
	public static String[] INFOFullHelp                                                   = EPhraseNames.INFOFullHelp.getTexts();
	@ConfigurableElement("")
	public static String[] INFOWelcomeMenu                                                = EPhraseNames.INFOWelcomeMenu.getTexts();
	@ConfigurableElement("")
	public static String[] INFOCouldNotRegister                                           = EPhraseNames.INFOCouldNotRegister.getTexts();
	@ConfigurableElement("")
	public static String[] PROFILEView                                                    = EPhraseNames.PROFILEView.getTexts();
	@ConfigurableElement("")
	public static String[] PROFILEFullfillingAskNick                                      = EPhraseNames.PROFILEFullfillingAskNick.getTexts();
	@ConfigurableElement("")
	public static String[] PROFILENickRegisteredNotification                              = EPhraseNames.PROFILENickRegisteredNotification.getTexts();
	@ConfigurableElement("")
	public static String[] PLAYINGWordProvidingPlayerStart                                = EPhraseNames.PLAYINGWordProvidingPlayerStart.getTexts();
	@ConfigurableElement("")
	public static String[] PLAYINGWordGuessingPlayerStart                                 = EPhraseNames.PLAYINGWordGuessingPlayerStart.getTexts();
	@ConfigurableElement("")
	public static String[] PLAYINGWordGuessingPlayerStatus                                = EPhraseNames.PLAYINGWordGuessingPlayerStatus.getTexts();
	@ConfigurableElement("")
	public static String[] PLAYINGWordProvidingPlayerStatus                               = EPhraseNames.PLAYINGWordProvidingPlayerStatus.getTexts();
	@ConfigurableElement("")
	public static String[] PLAYINGWinningMessageForWordGuessingPlayer                     = EPhraseNames.PLAYINGWinningMessageForWordGuessingPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] PLAYINGWinningMessageForWordProvidingPlayer                    = EPhraseNames.PLAYINGWinningMessageForWordProvidingPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] PLAYINGLosingMessageForWordGuessingPlayer                      = EPhraseNames.PLAYINGLosingMessageForWordGuessingPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] PLAYINGLosingMessageForWordProvidingPlayer                     = EPhraseNames.PLAYINGLosingMessageForWordProvidingPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] PLAYINGMatchGiveupNotificationForWordProvidingPlayer           = EPhraseNames.PLAYINGMatchGiveupNotificationForWordProvidingPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] PLAYINGMatchGiveupNotificationForWordGuessingPlayer            = EPhraseNames.PLAYINGMatchGiveupNotificationForWordGuessingPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] INVITINGAskOpponentNickOrPhone                                 = EPhraseNames.INVITINGAskOpponentNickOrPhone.getTexts();
	@ConfigurableElement("")
	public static String[] INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation  = EPhraseNames.INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation.getTexts();
	@ConfigurableElement("")
	public static String[] INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation = EPhraseNames.INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation.getTexts();
	@ConfigurableElement("")
	public static String[] INVITINGInvitationNotificationForInvitingPlayer                = EPhraseNames.INVITINGInvitationNotificationForInvitingPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] INVITINGTimeoutNotificationForInvitingPlayer                   = EPhraseNames.INVITINGTimeoutNotificationForInvitingPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] INVITINGInvitationNotificationForInvitedPlayer                 = EPhraseNames.INVITINGInvitationNotificationForInvitedPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] INVITINGInvitationRefusalNotificationForInvitingPlayer         = EPhraseNames.INVITINGInvitationRefusalNotificationForInvitingPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] INVITINGInvitationRefusalNotificationForInvitedPlayer          = EPhraseNames.INVITINGInvitationRefusalNotificationForInvitedPlayer.getTexts();
	@ConfigurableElement("")
	public static String[] LISTINGShowPlayers                                             = EPhraseNames.LISTINGShowPlayers.getTexts();
	@ConfigurableElement("")
	public static String[] LISTINGNoMorePlayers                                           = EPhraseNames.LISTINGNoMorePlayers.getTexts();
	@ConfigurableElement("")
	public static String[] PROVOKINGDeliveryNotification                                  = EPhraseNames.PROVOKINGDeliveryNotification.getTexts();
	@ConfigurableElement("")
	public static String[] PROVOKINGSendMessage                                           = EPhraseNames.PROVOKINGSendMessage.getTexts();
	@ConfigurableElement("")
	public static String[] PROVOKINGNickNotFound                                          = EPhraseNames.PROVOKINGNickNotFound.getTexts();
	@ConfigurableElement("")
	public static String[] UNSUBSCRIBINGUnsubscriptionNotification                        = EPhraseNames.UNSUBSCRIBINGUnsubscriptionNotification.getTexts();

	
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
		
		// set phrases
		for (EPhraseNames i18nPhrase : EPhraseNames.values()) {
			Field configurationPhrase = Configuration.class.getField(i18nPhrase.name());
			String[] texts = (String[])configurationPhrase.get(null);
			i18nPhrase.setTexts(texts);
		}
		
	}

}