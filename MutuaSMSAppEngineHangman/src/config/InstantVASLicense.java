package config;

import java.util.regex.Pattern;

import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsin.parsers.SMSInCelltick;

/** <pre>
 * InstantVASLicense.java
 * ======================
 * (created by luiz, Mar 27, 2016)
 *
 * Class that regulates the scope of this Instant VAS Instance and sets it
 * to run the "HangmanForCelltickClaroBR".
 * 
 * This class was meant to be overwritten by every deployment.
 * 
 * All its fields are declared "public static final" in order for
 * several classes which make use of them will have their code changed/removed
 * by ProGuard, thus making this a more effective license infringment control than
 * encrypting a configuration file (which could be overcomed by debugging and recoding
 * a single file).
 * 
 * Some fields, that should otherwise be in 'InstantVASInstanceConfiguration', were moved
 * here to make any reverse-engineering attempt harder. The moved fields are, then, commented
 * out from that class, which makes the actual license a pair of this class and that class.
 * 
 * @see InstantVASInstancesDefinitions
 * @see InstantVASInstanceConfiguration
 * @version $Id$
 * @author luiz
*/

public class InstantVASLicense {

	// the following variable declarations function as an Enumeration, with the benefit of being able to "remove not used code" from the compiled byte codes,
	// making it harder for a reverse engineering attempt to get a (otherwise easy) fully unlocked version of our application.
	// Also, values are coded for the same reason: they are not placed as their plain string meaning so to remove clues for reverse engineering attempts.
	
	/** Includes hard-codes (on several classes) for the configurations defined here -- adding two layers of protection against reverse engineering: 
	 *  1) encryption-like configuration (since it will be proguarded);
	 *  2) need to reverse engineer several hard coded classes in order to unlock this version for other countries; and
	 *  3) code that would make this a fully unlocked Instant VAS application simply won't be on the binaries */
	public static final String ConfigurationSourceType_HARDCODED     = "LViJDl";
	/** Includes code for reading configurations from an encrypted jar file entry. The configuration is, therefore, read-only */
	public static final String ConfigurationSourceType_RESOURCE      = "KdfJeN";
	/** Includes code for reading configurations from an encrypted local file system entry. The configuration file is read-write and may be updated */
	public static final String ConfigurationSourceType_ENC_FS_FILE   = "LfNcxO";
	/** Includes code for reading configurations from a plain local file system entry. The configuration file is read-write and may be updated */
	public static final String ConfigurationSourceType_PLAIN_FS_FILE = "ZhlXkP";
	/** Includes code for fetching the configurations from the Instant VAS main website, via HTTP, returning an encrypted file which is cached locally */
	public static final String ConfigurationSourceType_HTTP          = "nvWkOk";
	/** Includes code for fetching the configurations from a PostgreSQL table and also exporting it via HTTP -- to be used on the InstantVAS.com deployed version */
	public static final String ConfigurationSourceType_POSTGRESQL    = "LvYoKl";

	// possibly we can do something like the above here -- remember to replace the switch cases for if-then-elses on the module loading
	public enum EInstantVASModules {// celltick integration modules, for production
	                                CELLTICK_BR_INTEGRATION,
	                                // celltick integration modules, for testing
	                                CELLTICK_JUNIT_TESTS_INTEGRATION,
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

	// Instant VAS main configuration
	/////////////////////////////////
	
	/** when true, use the hard-coded instance definition, bringing an extra layer of protection against reverse-engineering */
//	public static final boolean IFDEF_HARDCODED_INSTANTVAS_INSTANCE    = true;
	/** Defines the retrieve method for the Instant VAS instances definitions */
	public static final String INSTANTVAS_INSTANCES_SOURCE_TYPE        = /**/ ConfigurationSourceType_HARDCODED; //*/ ConfigurationSourceType_PLAIN_FS_FILE; 
	/** The location, in respect to the above definition, to retrieve the data from */
	public static /*final*/ String INSTANTVAS_INSTANCES_SOURCE_ACCESS_INFO = /** / null;                              //*/ "/InstantVASInstances.config";
	
	// Navite HTTP Server
	/////////////////////

	/** The port the Native HTTP server should listen to, on all interfaces */
	public static final int NATIVE_HTTP_SERVER_PORT                = 8080;
	/** The number of accepted connections put on wait while one of the request processing threads become available to process them */
	public static final int NATIVE_HTTP_SOCKET_BACKLOG_QUEUE_SLOTS = 9999;
	/** The maximum number of requests to be processed in parallel by the native web server */
	public static final int NATIVE_HTTP_NUMBER_OF_THREADS          = 5;
	/** For POST methods, the native web server reads chunks at the most this number of bytes */
	public static final int NATIVE_HTTP_INPUT_BUFFER_SIZE          = 1024;
	/** While reading the chunks above, wait at most this number of milliseconds before considering the connection stale */
	public static final int NATIVE_HTTP_READ_TIMEOUT               = 30000;
	/** The maximum number of parameters, among all request, for optimum hash configuration */
	public static final int NATIVE_HTTP_PARAMETERS_HASH_CAPACITY   = 20;


	// TODO 18/5/2016 -- corrigir a arquitetura de Instancias: O ConnectionPoolPolicy (uma nova variável a ser criada em InstanceConfiguration, ou até mesmo em InstantVASMainConfiguration)
	//      pode ser 'private' ou shared. Também há o campo 'DatabaseSchema', que pode ser deixado em branco ou definido. Todas as tabelas
	//      (exceto as filas) respeitarão estas definições. Caso seja 'private', o schema é setado no momento da criação da conexão; se
	//      'shared', as informações de conexão não mais poderão ser definidas em cada instância e o schema deve ser setado antes de cada
	//      comando -- e o JDBCAdapter guarda o 'ThreadSchema', que deve ser setado pelos MO/MT Producers & Consumers, e incluídos ou não
	//      via IFDEV, lógico
	
	// TODO 18/5/2016 -- corrigir a arquitetura de Instancias: Logs, Relatórios, Profiles... podem ser UNIQUE ou PerInstance. Caso sejam perInstance,
	//      códigos IFDEF na recepção e processamento de MOs, MTs, etc. devem setar o LogOutput, ReportOutput, ProfileOutput... para cada thread.
	//      Parece ser uma boa solução isso?? Não teria sido melhor continuar passando as instâncias de log para todos? Certamente isto fere a
	//      determinação "nada deve ser estático", porém esta é uma boa excessão pela simplificação dos construtores.
	
	// Instant VAS instances
	////////////////////////
	// The following values may be repeated n times -- one for each SMS Application Instance served by this Instant VAS server.
	
	// these constants are exclusively used for the hard-coded version (when IFDEF_HARDCODED_INSTANTVAS_INSTANCE is true)...
	
	/** @see #INSTANTVAS_INSTANCE_CONFIG_TYPE */
	public static final String INSTANTVAS_INSTANCE_CONFIG0_TYPE         = /**/ ConfigurationSourceType_PLAIN_FS_FILE;	//*/ null; 
	/** @see #INSTANTVAS_INSTANCE_CONFIG_ACCESS_INFO */
	public static /*final*/ String INSTANTVAS_INSTANCE_CONFIG0_ACCESS_INFO  = /**/ "/tmp/InstantVASHangman.config";			//*/ null;
	/** @see #INSTANTVAS_INSTANCE_CONFIG_TOKEN */
	public static final String INSTANTVAS_INSTANCE_CONFIG0_TOKEN        = /**/ "AiHfidSIfSmMd84ISi4";					//*/ null;
	/** Tells how many hard-coded instance definitions the hard-coded version of the "instance recognition code" should consider.
	 *  Set to 0 to disable the hard-coding -- if hard-coding is enabled, the non hard-coded version bellow won't be used. */
	public static final int    INSTANTVAS_INSTANCE_CONFIGn_LENGTH       = /**/ 1;										//*/ 0;
	
	// Instance License Infringement Control
	////////////////////////////////////////
	// The following definitions will cause a hard code in 'InstantVASInstanceConfiguration'

//	/** The 'CelltickLiveScreenSubscriptionAPI's 'package name' for this service -- the value to be sent when attempting to subscribe / unsubscribe using the provided 'LIFECYCLE_SERVICE_BASE_URL' */
//	public static final String LIFECYCLE_CHANNEL_NAME = "HangMan";
	
	// Passive HTTPD Service; Active HTTP Queue Client
	public static final String MOAcquisitionMethods_PASSIVE_ONLY             = "kBc8A";
	public static final String MOAcquisitionMethods_ACTIVE_HTTP_QUEUE_CLIENT = "lC9Jz";
	
	public static final String MO_ACQUISITION_METHOD                  = /**/ MOAcquisitionMethods_PASSIVE_ONLY; //*/ MOAcquisitionMethods_ACTIVE_HTTP_QUEUE_CLIENT;
	public static final String MO_ACTIVE_HTTP_QUEUE_BASE_URL          = "http://test.InstantVAS.com/CelltickMOs.php";
	public static final String MO_ACTIVE_HTTP_QUEUE_LOCAL_OFFSET_FILE = "/tmp/lastFetchedMO";
	public static final String MO_ACTIVE_HTTP_QUEUE_BATCH_SIZE        = "50";
	public static final long   MO_ACTIVE_HTTP_QUEUE_POOLING_DELAY     = 1000;
	
//	/** The short code of the Hangman Game -- phrases can refer to this value using {{shortCode}} */
//	public static final String SHORT_CODE  = "993";
	
//	/** 'SMSOutCelltick' parameter to allow sending MTs */
//	public static final String KANNEL_MT_SMSC = "C1";	// using a single SMSC might fuck it up for Celltick's Claro BR... but they need to provide guidance for a solution
	
	// Additional MO verification parameters
	// The following variables provokes a hardcode check of the SMSC MO parameter on 'NativeHTTPServer.ADD_TO_MO_QUEUE' and 'AddToMOQueue' servlet -- Known values as of december, 2015:  C1, C1b, C1c, C1d, C1e, C1f, C1g, C1h
	public static final String   HardCodeCheckMethodOfAdditionalMOParameterValues_EQUALS       = "lNvsO";
	public static final String   HardCodeCheckMethodOfAdditionalMOParameterValues_STARTS_WITH  = "CxZs3";	// plain text only
	public static final String   HardCodeCheckMethodOfAdditionalMOParameterValues_REGEX        = "dkP4W";	// uses Matcher.matches to decide
	public static final String   IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES = HardCodeCheckMethodOfAdditionalMOParameterValues_REGEX;
	public static final String   MO_ADDITIONAL_RULE0_FIELD_NAME  = null; // SMSInCelltick.SMSCParameterName;
	public static final int      MO_ADDITIONAL_RULE0_FIELD_INDEX = -1;   // SMSInCelltick.SMSCParameterIndex;
	public static final Pattern  MO_ADDITIONAL_RULE0_REGEX       = null; // Pattern.compile("C1[abcdefg]?");
	public static final String   MO_ADDITIONAL_RULE0_VALUE       = null; // "C1";
	public static final int      MO_ADDITIONAL_RULE0_MIN_LEN     = -1;
	public static final int      MO_ADDITIONAL_RULE0_MAX_LEN     = -1; // 3;
	public static final int      MO_ADDITIONAL_RULEn_LENGTH      = 0;  // 1;

	/** Specifies the available Instant VAS modules to this instance and the order they should be loaded */
	public static final EInstantVASModules[] ENABLED_MODULES = {
		EInstantVASModules.CELLTICK_BR_INTEGRATION,
		EInstantVASModules.BASE,
		EInstantVASModules.HELP,
		EInstantVASModules.SUBSCRIPTION,
		EInstantVASModules.PROFILE,
		EInstantVASModules.CHAT,
		EInstantVASModules.HANGMAN,
	};

	// AddToMOQueue, AddToSubscribeUserQueue and other services (license infringement control)
	//////////////////////////////////////////////////////////////////////////////////////////
	
	public static final boolean IFDEF_HARCODED_INSTANCE_RESTRICTION = true;	// if true, the hard-coded information bellow will be used -- please, refactor this name & behavior
	/** If set, instructs /AddToMOQueue and other services to require received MSISDNs to have a minimum length */
	public static final int      ALLOWABLE_MSISDN_MIN_LENGTH = /**/ -1; //*/ 12;	// if set to -1, restriction code for min length won't be used
	/** Same as above, but for a maximum length */
	public static final int      ALLOWABLE_MSISDN_MAX_LENGTH = /**/ -1; //*/ 13;	// idem for max length
	/** If set, MSISDNs used on any service will be required to have one of the listed prefixes */
	public static final boolean  IFDEF_ALLOWABLE_MSISDN_PREFIXES  = /** / true; //*/ false;	// if false, restriction code won't be present
	public static final String[] ALLOWABLE_MSISDN_PREFIXES        = /**/ null; //*/ {"55"};
	public static final String   ALLOWABLE_MSISDN_PREFIX0         = /** / "55"; //*/ null;
	public static final int      ALLOWABLE_MSISDN_PREFIXn_LENGTH  = /** / 1;    //*/ 0;		// if set to 0 and IFDEF is true, flexible code for 'ALLOWABLE_MSISDN_PREFIXES' will be used instead of the hard-code for '*PREFIXn' values
	/** If set, /AddToMOQueue will only process MOs from the listed carriers */
	public static final boolean               IFDEF_ALLOWABLE_CARRIERS  = /** / true;                     //*/ false;	// if false, restriction code won't be present
	public static final ESMSInParserCarrier[] ALLOWABLE_CARRIERS        = /**/ null;                     //*/ {ESMSInParserCarrier.VIVO};
	public static final ESMSInParserCarrier   ALLOWABLE_CARRIER0        = /** / ESMSInParserCarrier.VIVO; //*/ null;
	public static final int                   ALLOWABLE_CARRIERn_LENGTH = /** / 1;                        //*/ 0;		// if set to 0 and IFDEF is true, flexible code for 'ALLOWABLE_CARRIERS' will be used instead of the hard-code for '*CARRIERn' values
	/** If set, /AddToMOQueue (and other services) will only process MOs and send MTs to the listed short codes -- which may be long codes as well */
	public static final boolean  IFDEF_ALLOWABLE_SHORT_CODES  = /** / true;  //*/ false;	// if false, restriction code won't be present
	public static final String[] ALLOWABLE_SHORT_CODES        = /**/ null;  //*/ {"993", "990"};
	public static final String   ALLOWABLE_SHORT_CODE0        = /** / "993"; //*/ null;
	public static final String   ALLOWABLE_SHORT_CODE1        = /** / "990"; //*/ null;
	public static final int      ALLOWABLE_SHORT_CODEn_LENGTH = /** / 2;     //*/ 0;		// if set to 0 and IFDEF is true, flexible code for 'ALLOWABLE_SHORT_CODES' will be used instead of the hard-code for '*SHORT_CODEn' values
	
	// navigation states
	////////////////////
	
	// global commands:
	//                  help,
	//                  unsubscribe,
	//                  nick(PROFILEtrgGlobalStartAskForNicknameDialog, PROFILEtrgGlobalRegisterNickname),
	//                  chat(CHATtrgGlobalSendPrivateMessage),
	//                  list(PROFILEtrgGlobalListProfiles),
	//                  profile(PROFILEtrgGlobalShowUserProfile),
	//					play
	//                  invite

	
	/** Navigation state used to initiate the first interaction with the application and, also, the state after users subscriptions cancellation */
	public static final EInstantVASCommandTriggers[] BASEnstNewUser = {
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalAcceptDoubleOptin,	// the double opt-in process starts with an externally generated broadcast message -- so we may proceed from that point on
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
		EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
		EInstantVASCommandTriggers.PROFILEtrgGlobalListProfiles,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalStartDoubleOptin,
	};
	/** Navigation state used by registered users. Also the 'main loop' navigation state, to which all other states revert to when they finish their businesses */
	public static final EInstantVASCommandTriggers[] BASEnstExistingUser = {
		EInstantVASCommandTriggers.HELPtrgGlobalStartCompositeHelpDialog,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatelessHelpMessage,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
		EInstantVASCommandTriggers.PROFILEtrgGlobalStartAskForNicknameDialog,
		EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
		EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
		EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
		EInstantVASCommandTriggers.PROFILEtrgGlobalListProfiles,
		EInstantVASCommandTriggers.HANGMANtrgPlayWithRandomUserOrBot,
		EInstantVASCommandTriggers.HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
		EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
	};
	/** Navigation state used to show the composite help messages, containing command triggers to navigate from here on */
	public static final EInstantVASCommandTriggers[] HELPnstPresentingCompositeHelp = {
		EInstantVASCommandTriggers.HELPtrgLocalShowNextCompositeHelpMessage,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
		EInstantVASCommandTriggers.PROFILEtrgGlobalStartAskForNicknameDialog,
		EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
		EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
		EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
		EInstantVASCommandTriggers.PROFILEtrgGlobalListProfiles,
		EInstantVASCommandTriggers.HANGMANtrgPlayWithRandomUserOrBot,
		EInstantVASCommandTriggers.HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
		EInstantVASCommandTriggers.HELPtrgGlobalStartCompositeHelpDialog,
		EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
	};
	/** Navigation state used to implement the double opt-in process */
	public static final EInstantVASCommandTriggers[] SUBSCRIPTIONnstAnsweringDoubleOptin = {
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalAcceptDoubleOptin,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
		EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalRefuseDoubleOptin,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgLocalStartDoubleOptin,	// for some of known wrong commands, start the double opt-in process again
	};
	/** Navigation state used to interact with the user when asking for a nickname */
	public static final EInstantVASCommandTriggers[] PROFILEnstRegisteringNickname = {
		EInstantVASCommandTriggers.HELPtrgGlobalStartCompositeHelpDialog,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatelessHelpMessage,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
		EInstantVASCommandTriggers.PROFILEtrgGlobalStartAskForNicknameDialog,
		EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
		EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
		EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
		EInstantVASCommandTriggers.PROFILEtrgGlobalListProfiles,
		EInstantVASCommandTriggers.HANGMANtrgPlayWithRandomUserOrBot,
		EInstantVASCommandTriggers.HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
		EInstantVASCommandTriggers.PROFILEtrgLocalNicknameDialogCancelation,	// makes sense?
		EInstantVASCommandTriggers.PROFILEtrgLocalRegisterNickname,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatefulHelpMessageFallback,
	};
	/** Navigation state used to list online profiles available for interaction */
	public static final EInstantVASCommandTriggers[] PROFILEnstListingProfilesTriggers = {
		EInstantVASCommandTriggers.HELPtrgGlobalStartCompositeHelpDialog,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatelessHelpMessage,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
		EInstantVASCommandTriggers.PROFILEtrgGlobalStartAskForNicknameDialog,
		EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
		EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
		EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
		EInstantVASCommandTriggers.PROFILEtrgLocalListMoreProfiles,
		EInstantVASCommandTriggers.HANGMANtrgPlayWithRandomUserOrBot,
		EInstantVASCommandTriggers.HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
		EInstantVASCommandTriggers.PROFILEtrgLocalRegisterNickname,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatefulHelpMessageFallback,
	};
	/** Navigation state used when privately chatting with someone -- allows the user to simply type the message (no need to provide the nickname) */
	public static final EInstantVASCommandTriggers[] CHATnstChattingWithSomeone = {
		EInstantVASCommandTriggers.CHATtrgLocalSendPrivateReply,
	};
	/** Navigation state part of the invitation process of a human to play a hangman match -- on this state, the user must enter the desired word to be guessed, which will be processed by 'cmdHoldMatchWord' */
	public static final EInstantVASCommandTriggers[] HANGMANnstEnteringMatchWord = {
		EInstantVASCommandTriggers.HELPtrgGlobalStartCompositeHelpDialog,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatelessHelpMessage,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
		EInstantVASCommandTriggers.PROFILEtrgGlobalStartAskForNicknameDialog,
		EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
		EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
		EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
		EInstantVASCommandTriggers.PROFILEtrgGlobalListProfiles,
		EInstantVASCommandTriggers.HANGMANtrgPlayWithRandomUserOrBot,
		EInstantVASCommandTriggers.HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
		EInstantVASCommandTriggers.HANGMANtrgLocalHoldMatchWord,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatefulHelpMessageFallback,
	};
	/** State an invited user gets into after he/she is invited for a match, which is set by 'cmdHoldMatchWord'. The invited user answer will, then, be processed by 'cmdAnswerToInvitation' */
	public static final EInstantVASCommandTriggers[] HANGMANnstAnsweringToHangmanMatchInvitation = {
		EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
		EInstantVASCommandTriggers.PROFILEtrgGlobalListProfiles,
		EInstantVASCommandTriggers.HANGMANtrgLocalAcceptMatchInvitation,
		EInstantVASCommandTriggers.HANGMANtrgLocalRefuseMatchInvitation,
		EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
		EInstantVASCommandTriggers.HELPtrgGlobalShowExistingUsersFallbackHelp,
	};
	/** Navigation state that indicates the user is playing a hangman match with a human (as the invited opponent), and his/her role is to guess the word */
	public static final EInstantVASCommandTriggers[] HANGMANnstGuessingWordFromHangmanHumanOpponent = {
		EInstantVASCommandTriggers.HANGMANtrgLocalSingleLetterSuggestionForHuman,
		EInstantVASCommandTriggers.HELPtrgGlobalStartCompositeHelpDialog,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatelessHelpMessage,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
		EInstantVASCommandTriggers.PROFILEtrgGlobalStartAskForNicknameDialog,
		EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
		EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
		EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
		EInstantVASCommandTriggers.PROFILEtrgGlobalListProfiles,
		EInstantVASCommandTriggers.HANGMANtrgPlayWithRandomUserOrBot,
		EInstantVASCommandTriggers.HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
		EInstantVASCommandTriggers.HANGMANtrgLocalEndCurrentHumanMatch,
		EInstantVASCommandTriggers.HANGMANtrgLocalWordSuggestionFallbackForHuman,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatefulHelpMessageFallback,
	};
	/** Navigation state that indicates the user is playing a hangman match with the robot, and his/her hole is to guess the word */
	public static final EInstantVASCommandTriggers[] HANGMANnstGuessingWordFromHangmanBotOpponent = {
		EInstantVASCommandTriggers.HANGMANtrgLocalSingleLetterSuggestionForBot,
		EInstantVASCommandTriggers.HELPtrgGlobalStartCompositeHelpDialog,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatelessHelpMessage,
		EInstantVASCommandTriggers.SUBSCRIPTIONtrgGlobalUnsubscribe,
		EInstantVASCommandTriggers.PROFILEtrgGlobalStartAskForNicknameDialog,
		EInstantVASCommandTriggers.PROFILEtrgGlobalRegisterNickname,
		EInstantVASCommandTriggers.PROFILEtrgGlobalShowUserProfile,
		EInstantVASCommandTriggers.CHATtrgGlobalSendPrivateMessage,
		EInstantVASCommandTriggers.PROFILEtrgGlobalListProfiles,
		EInstantVASCommandTriggers.HANGMANtrgPlayWithRandomUserOrBot,
		EInstantVASCommandTriggers.HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
		EInstantVASCommandTriggers.HANGMANtrgLocalEndCurrentBotMatch,
		EInstantVASCommandTriggers.HANGMANtrgLocalWordSuggestionFallbackForBot,
		EInstantVASCommandTriggers.HELPtrgGlobalShowStatefulHelpMessageFallback,
	};
	
	public enum EInstantVASCommandTriggers {
		// help
		HELPtrgGlobalStartCompositeHelpDialog,
		HELPtrgLocalShowNextCompositeHelpMessage,
		HELPtrgGlobalShowNewUsersFallbackHelp,
		HELPtrgGlobalShowExistingUsersFallbackHelp,
		HELPtrgGlobalShowStatelessHelpMessage,
		HELPtrgGlobalShowStatefulHelpMessage,
		HELPtrgGlobalShowStatefulHelpMessageFallback,
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
		PROFILEtrgGlobalListProfiles,
		PROFILEtrgLocalListMoreProfiles,
		// chat
		CHATtrgGlobalSendPrivateMessage,
		CHATtrgLocalSendPrivateReply,
		// hangman
		HANGMANtrgPlayWithRandomUserOrBot,
		HANGMANtrgGlobalInviteNicknameOrPhoneNumber,
		HANGMANtrgLocalHoldMatchWord,
		HANGMANtrgLocalAcceptMatchInvitation,
		HANGMANtrgLocalRefuseMatchInvitation,
		HANGMANtrgLocalSingleLetterSuggestionForHuman,
		HANGMANtrgLocalWordSuggestionFallbackForHuman,
		HANGMANtrgLocalSingleLetterSuggestionForBot,
		HANGMANtrgLocalWordSuggestionFallbackForBot,
		HANGMANtrgLocalEndCurrentHumanMatch,
		HANGMANtrgLocalEndCurrentBotMatch;
		
		public String[] getCommandTriggerPatterns() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
			return (String[])InstantVASInstanceConfiguration.class.getField(name()).get(null);
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
	
	// Instrumentation
	//////////////////
	
	/** if true, includes code that will instrument MOs and MTs processing, issuing log lines informing about processing times.
	 *  Affected classes are: MOProducer, MOConsumer, MTProducer, MTConsumer and MOAndMTInstrumentation */
	public static final boolean IFDEF_INSTRUMENT_MO_AND_MT_TIMES = true;
	/** keeps track of MOs and MTs processing for up to this amount of milliseconds */
	public static final long    INSTRUMENT_MO_AND_MT_TIMEOUT     = 60*1000;	// 10 seconds

}
