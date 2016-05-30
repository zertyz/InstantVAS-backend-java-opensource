package instantvas.tests;

import java.sql.SQLException;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.CommandNamesChat.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.CommandTriggersChat.*;
import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.SpecializedMOQueueDataBureau;
import mutua.events.TestAdditionalEventServer;
import mutua.events.TestAdditionalEventServer.ETestAdditionalEventServices;
import mutua.events.postgresql.QueuesPostgreSQLAdapter;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.handlers.IInstrumentationHandler;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerLogConsole;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterChat;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsChat;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import adapters.PostgreSQLAdapter;

/** <pre>
 * InstantVASSMSAppModuleChatTestsConfiguration.java
 * =================================================
 * (created by luiz, Sep 8, 2015)
 *
 * Configure the classes' default values for new instances of the "Chat" SMS Module test application.
 *
 * Typically, the configure* methods on this class must be invoked prior to its usage. 
 * 
 * Follows the "Mutua Configurable Module" pattern tuned for "Instant VAS SMS Modules", described on
 * the Base Modules Test Application Configuration -- {@link InstantVASSMSAppModuleConfiguration}
 * 
 * @author luiz
 */

public class InstantVASSMSAppModuleChatTestsConfiguration {
	
	public  static final String shortCode = "975";
	private static final String appName   = "ChatTestApp";
	
	private static InstantVASSMSAppModuleChatTestsConfiguration instance = null;

	public static SMSAppModuleDALFactory                                    BASE_MODULE_DAL;
	public static SMSAppModuleDALFactoryProfile                             PROFILE_MODULE_DAL;
	public static SMSAppModuleDALFactoryChat                                CHAT_MODULE_DAL;
	public static int                                                       PERFORMANCE_TESTS_LOAD_FACTOR;
	public static String                                                    MO_TABLE_NAME;
	
	public PostgreSQLQueueEventLink<ETestAdditionalEventServices>  MO_QUEUE_LINK;
	public TestAdditionalEventServer                               MO_QUEUE_PRODUCER;
	public SMSAppModuleNavigationStates                            baseModuleNavigationStates;
	public SMSAppModulePhrasingsProfile                            profileModulePhrasings;
	public SMSAppModulePhrasingsChat                               chatModulePhrasings;
	public SMSAppModuleCommandsChat                                chatModuleCommands;
	public SMSAppModuleNavigationStatesChat                        chatModuleNavigationStates;
	
	/**************************
	** CONFIGURATION METHODS **
	**************************/
	
	/** method to be called to configure all the modules needed to get instances of the test classes */
	public static void configureDefaultValuesForNewInstances(
		int performanceTestsLoadFactor, SMSAppModuleDALFactoryChat chatModuleDAL,
		String postgreSQLconnectionProperties, int postgreSQLConnectionPoolSize,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postgreSQLShouldDebugQueries,
		String postgreSQLHostname, int postgreSQLPort, String postgreSQLDatabase, String postgreSQLUser, String postgreSQLPassword) throws SQLException {
		
		instance = null;
		
		PERFORMANCE_TESTS_LOAD_FACTOR = performanceTestsLoadFactor;
		CHAT_MODULE_DAL               = chatModuleDAL;
		MO_TABLE_NAME                 = "ChatTestMOQueue";
		
		// database configuration
		switch (chatModuleDAL) {
			case POSTGRESQL:
				PostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLconnectionProperties, postgreSQLConnectionPoolSize);
				SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
				SMSAppModulePostgreSQLAdapterProfile.configureDefaultValuesForNewInstances(postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword, MO_TABLE_NAME, "#eventId", "#phone");
				// MO simulation
				QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries, postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
				PostgreSQLQueueEventLink.configureDefaultValuesForNewInstances(-1, -1);
				// chat db
				SMSAppModulePostgreSQLAdapterChat.configureDefaultValuesForNewInstances(postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword,
					MO_TABLE_NAME, "eventId", "text");
				// other databases
				BASE_MODULE_DAL    = SMSAppModuleDALFactory       .POSTGRESQL;
				PROFILE_MODULE_DAL = SMSAppModuleDALFactoryProfile.POSTGRESQL;
				break;
			case RAM:
				// other databases
				BASE_MODULE_DAL    = SMSAppModuleDALFactory       .RAM;
				PROFILE_MODULE_DAL = SMSAppModuleDALFactoryProfile.RAM;
				break;
			default:
				throw new NotImplementedException();
		}

		System.err.println(InstantVASSMSAppModuleChatTestsConfiguration.class.getCanonicalName() + ": test configuration loaded.");
	}
	
	public static InstantVASSMSAppModuleChatTestsConfiguration getInstance() {
		if (instance == null) try {
			instance = new InstantVASSMSAppModuleChatTestsConfiguration();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		return instance;
	}

	static {
		
		// Instrumentation
		IInstrumentationHandler log = new InstrumentationHandlerLogConsole(appName, ELogSeverity.DEBUG);
		Instrumentation.configureDefaultValuesForNewInstances(log, log, log);

		// configure with the default values
		try {
			configureDefaultValuesForNewInstances(
				// module DAL
				1, SMSAppModuleDALFactoryChat   .POSTGRESQL,
				// PostgreSQL properties
				null,	// connection properties
				-1,		// connection pool size
				true,	// assert structures
				false,	// debug queries
				"venus", 5432, "hangman", "hangman", "hangman");
		} catch (SQLException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/*****************
	** CONSTRUCTORS **
	*****************/
	
	private InstantVASSMSAppModuleChatTestsConfiguration() throws SQLException {

		// mo simulation queue configuration
		switch (CHAT_MODULE_DAL) {
			case POSTGRESQL:
				MO_QUEUE_LINK     = new PostgreSQLQueueEventLink<ETestAdditionalEventServices>(ETestAdditionalEventServices.class, null, MO_TABLE_NAME, new SpecializedMOQueueDataBureau());
				MO_QUEUE_PRODUCER = new TestAdditionalEventServer(MO_QUEUE_LINK);
				break;
			case RAM:
				MO_QUEUE_LINK     = null;
				MO_QUEUE_PRODUCER = null;
				break;
			default:
		}

		Object[] chatModule = SMSAppModuleConfigurationChat.getChatModuleInstances(shortCode, appName,
		                                                                           PROFILE_MODULE_DAL, CHAT_MODULE_DAL);

		chatModuleNavigationStates = (SMSAppModuleNavigationStatesChat) chatModule[0];
		chatModuleCommands         = (SMSAppModuleCommandsChat)         chatModule[1];
		chatModulePhrasings        = (SMSAppModulePhrasingsChat)        chatModule[2];
		profileModulePhrasings     = (SMSAppModulePhrasingsProfile)     chatModule[3];
		
		// base module -- configured to interact with the Chat Module commands 
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(BASE_MODULE_DAL,
			/*nstNewUserTriggers*/
			new Object[][] {
				{cmdSendPrivateMessage, trgGlobalSendPrivateMessage},
			},
			/*nstExistingUserTriggers*/
			new Object[][] {
				{cmdSendPrivateMessage, trgGlobalSendPrivateMessage},
		});
		baseModuleNavigationStates = (SMSAppModuleNavigationStates) baseModule[0];
		
	}
}
