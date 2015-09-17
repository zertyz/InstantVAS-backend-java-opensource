package mutua.smsappengine.config;

import static mutua.smsappmodule.config.SMSAppModuleConfiguration.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationChat.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationHelp.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationProfile.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription.*;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationChat;
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
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription;
import mutua.subscriptionengine.SubscriptionEngine;
import adapters.JDBCAdapter;

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
		SMSAppModuleNavigationStatesChat.values()//,
		//SMSAppModuleNavigationStatesHangman.values(),
	};
	
	public static SMSAppModuleDALFactory                  DEFAULT_MODULE_DAL  = SMSAppModuleDALFactory.            DEFAULT_DAL;
	public static SMSAppModuleDALFactorySubscription SUBSCRIPTION_MODULE_DAL  = SMSAppModuleDALFactorySubscription.DEFAULT_DAL;
	public static SMSAppModuleDALFactoryProfile           PROFILE_MODULE_DAL  = SMSAppModuleDALFactoryProfile.     DEFAULT_DAL;
	public static SMSAppModuleDALFactoryChat                 CHAT_MODULE_DAL  = SMSAppModuleDALFactoryChat.        DEFAULT_DAL;
	public static SMSAppModuleDALFactoryHangman           HANGMAN_MODULE_DAL  = SMSAppModuleDALFactoryHangman.     DEFAULT_DAL;

	
	private static void setDefaultDBToPOSTGRESQL(Instrumentation<?, ?> log, String hostname, int port, String database, String user, String password) {

		SMSAppModuleDALFactory.            DEFAULT_DAL = SMSAppModuleDALFactory.            POSTGRESQL;
		SMSAppModuleDALFactorySubscription.DEFAULT_DAL = SMSAppModuleDALFactorySubscription.POSTGRESQL;
		SMSAppModuleDALFactoryProfile.     DEFAULT_DAL = SMSAppModuleDALFactoryProfile.     POSTGRESQL;
		SMSAppModuleDALFactoryChat.        DEFAULT_DAL = SMSAppModuleDALFactoryChat.        POSTGRESQL;
		SMSAppModuleDALFactoryHangman.     DEFAULT_DAL = SMSAppModuleDALFactoryHangman.     POSTGRESQL;

		     DEFAULT_MODULE_DAL  = SMSAppModuleDALFactory.            DEFAULT_DAL;
		SUBSCRIPTION_MODULE_DAL  = SMSAppModuleDALFactorySubscription.DEFAULT_DAL;
		     PROFILE_MODULE_DAL  = SMSAppModuleDALFactoryProfile.     DEFAULT_DAL;
		        CHAT_MODULE_DAL  = SMSAppModuleDALFactoryChat.        DEFAULT_DAL;
		     HANGMAN_MODULE_DAL  = SMSAppModuleDALFactoryHangman.     DEFAULT_DAL;


		// JDBC & PostgreSQL parameter configuration
		////////////////////////////////////////////
		     
		JDBCAdapter.SHOULD_DEBUG_QUERIES = true;
		JDBCAdapter.CONNECTION_POOL_SIZE = 4;

		// default module
		SMSAppModulePostgreSQLAdapter.log = log;
		SMSAppModulePostgreSQLAdapter.ALLOW_DATABASE_ADMINISTRATION = true;
		SMSAppModulePostgreSQLAdapter.HOSTNAME       = hostname;
		SMSAppModulePostgreSQLAdapter.PORT           = port;
		SMSAppModulePostgreSQLAdapter.DATABASE       = database;
		SMSAppModulePostgreSQLAdapter.USER           = user;
		SMSAppModulePostgreSQLAdapter.PASSWORD       = password;

		// subscription module
		SMSAppModulePostgreSQLAdapterSubscription.log = log;
		SMSAppModulePostgreSQLAdapterSubscription.ALLOW_DATABASE_ADMINISTRATION = true;
		SMSAppModulePostgreSQLAdapterSubscription.HOSTNAME       = hostname;
		SMSAppModulePostgreSQLAdapterSubscription.PORT           = port;
		SMSAppModulePostgreSQLAdapterSubscription.DATABASE       = database;
		SMSAppModulePostgreSQLAdapterSubscription.USER           = user;
		SMSAppModulePostgreSQLAdapterSubscription.PASSWORD       = password;

		// profile module
		SMSAppModulePostgreSQLAdapterProfile.log = log;
		SMSAppModulePostgreSQLAdapterProfile.ALLOW_DATABASE_ADMINISTRATION = true;
		SMSAppModulePostgreSQLAdapterProfile.HOSTNAME       = hostname;
		SMSAppModulePostgreSQLAdapterProfile.PORT           = port;
		SMSAppModulePostgreSQLAdapterProfile.DATABASE       = database;
		SMSAppModulePostgreSQLAdapterProfile.USER           = user;
		SMSAppModulePostgreSQLAdapterProfile.PASSWORD       = password;

		// chat module
		SMSAppModulePostgreSQLAdapterChat.log = log;
		SMSAppModulePostgreSQLAdapterChat.ALLOW_DATABASE_ADMINISTRATION = true;
		SMSAppModulePostgreSQLAdapterChat.HOSTNAME       = hostname;
		SMSAppModulePostgreSQLAdapterChat.PORT           = port;
		SMSAppModulePostgreSQLAdapterChat.DATABASE       = database;
		SMSAppModulePostgreSQLAdapterChat.USER           = user;
		SMSAppModulePostgreSQLAdapterChat.PASSWORD       = password;

		// hangman module
		SMSAppModulePostgreSQLAdapterHangman.log = log;
		SMSAppModulePostgreSQLAdapterHangman.ALLOW_DATABASE_ADMINISTRATION = true;
		SMSAppModulePostgreSQLAdapterHangman.HOSTNAME       = hostname;
		SMSAppModulePostgreSQLAdapterHangman.PORT           = port;
		SMSAppModulePostgreSQLAdapterHangman.DATABASE       = database;
		SMSAppModulePostgreSQLAdapterHangman.USER           = user;
		SMSAppModulePostgreSQLAdapterHangman.PASSWORD       = password;


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
		HELPphrFallbackExistingUsers = "neither here... not a shit to show";
		HELPphrStateless             = "You can play the {{appName}} game in 2 ways: guessing someone's word or inviting someone to play with your word " +
		                               "You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number " +
		                               "Every week, 1 lucky number is selected to win the prize. Send an option to 9714: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help";
		HELPphrComposite             = new String[] {
			"",
		};
		
		// command patterns
		HELPtrgGlobalStartCompositeHelpDialog = new String[] {
			"RULES",	
		};
		HELPtrgLocalShowNextCompositeHelpMessage = new String[] {
			"+",
		};
		HELPtrgGlobalShowStatelessHelpMessage = new String[] {
			"HELP",
		};
		
		// stateful help messages
		setStatefulHelpMessages(new Object[][] {
			{nstNewUser,              "fallback help message for new users"},
			{nstExistingUser,         "fallback help message for existing users"},
			{nstAnsweringDoubleOptin, "fallback help message when answering double opt-in"},
			{nstRegisteringNickname,  "fallback help message when registering a nickname"},
			{nstChattingWithSomeone,  "help message when statefully chatting with someone"},
		});

		// SMSAppModuleConfigurationHelp.log = null;  // to come from a parameter. BTW, where is the log for this module?
		SMSAppModuleConfigurationHelp.applyConfiguration();
		
		
		// SMSAppModuleConfigurationSubscription
		////////////////////////////////////////
		
		// phrasing
		SUBSCRIPTIONphrDoubleOptinStart            = "You are at the {{appName}} game. To continue, you must subscribe. Send {{appName}} now to {{shortCode}} and compete for prizes. You will be charged at $ every week."; 
		SUBSCRIPTIONphrDisagreeToSubscribe         = "";
		SUBSCRIPTIONphrSuccessfullySubscribed      = "{{appName}}: Registration succeeded. Send HELP to {{shortCode}} to know the rules and how to play, or simply send PLAY to {{shortCode}}";
		SUBSCRIPTIONphrCouldNotSubscribe           = "";
		SUBSCRIPTIONphrUserRequestedUnsubscription = "You are now unsubscribed from the {{appName}} GAME and will no longer receive invitations to play nor lucky numbers. To join again, send {{appName}} to {{shortCode}}";
		SUBSCRIPTIONphrLifecycleUnsubscription     = "";
		
		// command patterns
		SUBSCRIPTIONtrgLocalStartDoubleOptin   = new String[] {
			".*",
		};
		SUBSCRIPTIONtrgLocalAcceptDoubleOptin  = new String[] {
			"HANGMAN",
		};
		SUBSCRIPTIONtrgGlobalUnsubscribe       = new String[] {
			"UNSUBSCRIBE",
		};
		
		// navigation states
		nstNewUser.setCommandTriggers(new Object[][] {
			{cmdSubscribe,                SUBSCRIPTIONtrgLocalAcceptDoubleOptin},	// the double opt-in process starts with a broadcast message
			{cmdUnsubscribe,              SUBSCRIPTIONtrgGlobalUnsubscribe},
			{cmdStartDoubleOptinProcess,  SUBSCRIPTIONtrgLocalStartDoubleOptin},	// for some of known wrong commands, start the double opt-in process again
			{cmdShowNewUsersFallbackHelp, ".*"},									// fallback help
		});
		nstAnsweringDoubleOptin.setCommandTriggers(new Object[][] {
			{cmdSubscribe,                SUBSCRIPTIONtrgLocalAcceptDoubleOptin},
			{cmdDoNotAgreeToSubscribe,    SUBSCRIPTIONtrgGlobalUnsubscribe},
			{cmdShowNewUsersFallbackHelp, ".*"},
		});
		nstExistingUser.setCommandTriggers(new Object[][] {
			{cmdUnsubscribe,                   SUBSCRIPTIONtrgGlobalUnsubscribe},
			{cmdShowStatelessHelp,             HELPtrgGlobalShowStatelessHelpMessage},
			{cmdRegisterNickname,              PROFILEtrgGlobalRegisterNickname},
			{cmdShowExistingUsersFallbackHelp, ".*"},
		});
		
		SMSAppModuleConfigurationSubscription.subscriptionEngine = subscriptionEngine;
		SMSAppModuleConfigurationSubscription.subscriptionToken  = subscriptionToken;
		SMSAppModuleConfigurationSubscription.log = log;
		SMSAppModuleConfigurationSubscription.applyConfiguration();
		
		
		// SMSAppModuleConfigurationProfile
		///////////////////////////////////
		
		// phrasing
		PROFILEphrAskNickname                      = "";
		PROFILEphrNicknameRegistrationNotification = "{{appName}}: Name registered: {{registeredNickname}}. Send LIST to {{shortCode}} to see online players. NICK [NEW NICK] to change your name.";
		PROFILEphrPresentation                     = "";
		
		// command patterns
		PROFILEtrgGlobalStartAskForNicknameDialog = new String[] {
			"",
		};
		PROFILEtrgLocalRegisterNickname           = new String[] {
			"",
		};
		PROFILEtrgGlobalRegisterNickname          = new String[] {
			"",
		};
		
		//SMSAppModuleConfigurationProfile.log = null; // to come from a parameter. BTW, where is the log for this module?
		SMSAppModuleConfigurationProfile.applyConfiguration();
		
		
		// SMSAppModuleConfigurationChat
		////////////////////////////////
		
		// phrasing
		CHATphrPrivateMessage                     = "";
		CHATphrPrivateMessageDeliveryNotification = "";
		CHATphrNicknameNotFound                   = "";
		CHATphrDoNotKnowWhoYouAreChattingTo       = "";
		
		// command patterns
		CHATtrgGlobalSendPrivateMessage = new String[] {
			"",
		};
		CHATtrgLocalSendPrivateReply    = new String[] {
			"",
		};
		
		//SMSAppModuleConfigurationChat.log = null;	// to come from a parameter. BTW, where is the log for this module?
		SMSAppModuleConfigurationChat.applyConfiguration();

		
		// SMSAppModuleConfigurationHangman
		///////////////////////////////////
		
		// phrasing
		
		// command patterns

		//SMSAppModuleConfigurationHangman.log = null;	// to come from a parameter. BTW, where is the log for this module?
		//SMSAppModuleConfigurationHangman.applyConfiguration();

	}

}
