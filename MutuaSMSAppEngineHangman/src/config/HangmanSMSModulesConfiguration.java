package config;

import static mutua.smsappmodule.config.SMSAppModuleConfiguration.*;
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
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman.*;
import instantvas.smsengine.HangmanHTTPInstrumentationRequestProperty;
import instantvas.smsengine.MOSMSesQueueDataBureau;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;
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
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription;
import mutua.subscriptionengine.SubscriptionEngine;
import adapters.JDBCAdapter;
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

public class HangmanSMSModulesConfiguration {
	
	public static final INavigationState[][] navigationStates = {
		SMSAppModuleNavigationStates.values(),
		SMSAppModuleNavigationStatesHelp.values(),
		SMSAppModuleNavigationStatesSubscription.values(),
		SMSAppModuleNavigationStatesProfile.values(),
		SMSAppModuleNavigationStatesHangman.values()//,
		//SMSAppModuleNavigationStatesHangman.values(),
	};
	
	public static Instrumentation<HangmanHTTPInstrumentationRequestProperty, String> log;
	
	/** to be defined by the request receivers (servlet, console, tests, icc app, ...) */
	public static SubscriptionEngine SUBSCRIPTION_ENGINE;
	
	public static String SUBSCRIBE_SERVICE_URL               = "http://localhost:8082/celltick/wapAPI?action=subpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
	public static String UNSUBSCRIBE_SERVICE_URL             = "http://localhost:8082/celltick/wapAPI?action=unsubpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
	public static String SUBSCRIPTION_CHANNEL_NAME           = "HangMan";
	public static String MT_SERVICE_URL                      = "http://localhost:15001/cgi-bin/sendsms";
	public static int    MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS = 5;
	public static long   MT_SERVICE_DELAY_BETWEEN_ATTEMPTS   = 5000;

	
	public static SMSAppModuleDALFactory                  DEFAULT_MODULE_DAL  = SMSAppModuleDALFactory.            DEFAULT_DAL;
	public static SMSAppModuleDALFactorySubscription SUBSCRIPTION_MODULE_DAL  = SMSAppModuleDALFactorySubscription.DEFAULT_DAL;
	public static SMSAppModuleDALFactoryProfile           PROFILE_MODULE_DAL  = SMSAppModuleDALFactoryProfile.     DEFAULT_DAL;
	public static SMSAppModuleDALFactoryChat                 CHAT_MODULE_DAL  = SMSAppModuleDALFactoryChat.        DEFAULT_DAL;
	public static SMSAppModuleDALFactoryHangman           HANGMAN_MODULE_DAL  = SMSAppModuleDALFactoryHangman.     DEFAULT_DAL;
	
	public static String  POSTGRESQL_CONNECTION_HOSTNAME           = "venus";
	public static int     POSTGRESQL_CONNECTION_PORT               = 5432;
	public static String  POSTGRESQL_CONNECTION_DATABASE_NAME      = "hangman";
	public static String  POSTGRESQL_CONNECTION_USER               = "hangman";
	public static String  POSTGRESQL_CONNECTION_PASSWORD           = "hangman";

	public static EInstrumentationDataPours LOG_STRATEGY                   = EInstrumentationDataPours.CONSOLE;
	public static String LOG_HANGMAN_FILE_PATH                             = "";
	public static String LOG_WEBAPP_FILE_PATH                              = "";
	public static String APPID                                             = "HANGMAN";
	public static String SHORT_CODE                                        = "9714";

	
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
		SMSAppModuleConfiguration.applyConfiguration();
		
		
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
