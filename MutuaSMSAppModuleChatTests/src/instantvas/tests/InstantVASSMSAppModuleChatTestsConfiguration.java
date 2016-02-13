package instantvas.tests;

import java.sql.SQLException;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.CommandNamesChat.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.CommandTriggersChat.*;

import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.SpecializedMOQueueDataBureau;
import mutua.events.TestEventServer;
import mutua.events.TestEventServer.ETestEventServices;
import mutua.events.postgresql.QueuesPostgreSQLAdapter;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
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

	public static Instrumentation<DefaultInstrumentationProperties, String> LOG;
	public static SMSAppModuleDALFactory                                    BASE_MODULE_DAL;
	public static SMSAppModuleDALFactoryProfile                             PROFILE_MODULE_DAL;
	public static SMSAppModuleDALFactoryChat                                CHAT_MODULE_DAL;
	public static int                                                       PERFORMANCE_TESTS_LOAD_FACTOR;
	public static PostgreSQLQueueEventLink<ETestEventServices>              MO_QUEUE_LINK;
	public static TestEventServer                                           MO_QUEUE_PRODUCER;
	public static SMSAppModuleNavigationStates     baseModuleNavigationStates;
	public static SMSAppModulePhrasingsProfile     profileModulePhrasings;
	public static SMSAppModulePhrasingsChat        chatModulePhrasings;
	public static SMSAppModuleCommandsChat         chatModuleCommands;
	public static SMSAppModuleNavigationStatesChat chatModuleNavigationStates;
	
	/************
	** METHODS **
	************/
	
	/** method to be called to configure all the modules needed to get instances of the test classes */
	public static void configureDefaultValuesForNewInstances(Instrumentation<DefaultInstrumentationProperties, String> log, 
		int performanceTestsLoadFactor, SMSAppModuleDALFactoryChat chatModuleDAL,
		String postgreSQLconnectionProperties, int postgreSQLConnectionPoolSize,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postgreSQLShouldDebugQueries,
		String postgreSQLHostname, int postgreSQLPort, String postgreSQLDatabase, String postgreSQLUser, String postgreSQLPassword,
		String moTableName, String moIdFieldName, String moTextFieldName) throws SQLException {
		
		LOG                           = log;
		PERFORMANCE_TESTS_LOAD_FACTOR = performanceTestsLoadFactor;
		CHAT_MODULE_DAL               = chatModuleDAL;
		
		// database configuration
		switch (chatModuleDAL) {
			case POSTGRESQL:
				PostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLconnectionProperties, postgreSQLConnectionPoolSize);
				SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
				SMSAppModulePostgreSQLAdapterProfile.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
				// MO simulation
				QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries, postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
				PostgreSQLQueueEventLink.configureDefaultValuesForNewInstances(log, -1, -1);
				MO_QUEUE_LINK     = new PostgreSQLQueueEventLink<ETestEventServices>(ETestEventServices.class, moTableName, new SpecializedMOQueueDataBureau());
				MO_QUEUE_PRODUCER = new TestEventServer(MO_QUEUE_LINK);
				// chat db
				SMSAppModulePostgreSQLAdapterChat.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword,
					moTableName, moIdFieldName, moTextFieldName);
				// other databases
				BASE_MODULE_DAL    = SMSAppModuleDALFactory       .POSTGRESQL;
				PROFILE_MODULE_DAL = SMSAppModuleDALFactoryProfile.POSTGRESQL;
				break;
			case RAM:
				// other databases
				BASE_MODULE_DAL    = SMSAppModuleDALFactory       .RAM;
				PROFILE_MODULE_DAL = SMSAppModuleDALFactoryProfile.RAM;
				// MO simulation
				MO_QUEUE_LINK     = null;
				MO_QUEUE_PRODUCER = null;
				break;
			default:
				throw new NotImplementedException();
		}
		
		// chat module
		Object[] chatModule = SMSAppModuleConfigurationChat.getChatModuleInstances(shortCode, appName,
		                                                                           PROFILE_MODULE_DAL, chatModuleDAL);
		
		chatModuleNavigationStates = (SMSAppModuleNavigationStatesChat) chatModule[0];
		chatModuleCommands         = (SMSAppModuleCommandsChat)         chatModule[1];
		chatModulePhrasings        = (SMSAppModulePhrasingsChat)        chatModule[2];
		profileModulePhrasings     = (SMSAppModulePhrasingsProfile)     chatModule[3];
		
		// base module -- configured to interact with the Chat Module commands 
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(log, BASE_MODULE_DAL, chatModuleCommands.values,
			/*nstNewUserTriggers*/
			new Object[][] {
				{cmdSendPrivateMessage, trgGlobalSendPrivateMessage},
			},
			/*nstExistingUserTriggers*/
			new Object[][] {
				{cmdSendPrivateMessage, trgGlobalSendPrivateMessage},
			});
		baseModuleNavigationStates = (SMSAppModuleNavigationStates) baseModule[0];
		
		System.err.println(InstantVASSMSAppModuleChatTestsConfiguration.class.getName() + ": test configuration loaded.");
	}

	static {
		// configure with the default values
		try {
			configureDefaultValuesForNewInstances(
				// log
				new Instrumentation<DefaultInstrumentationProperties, String>(
					appName, DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null),
				// module DAL
				1, SMSAppModuleDALFactoryChat   .POSTGRESQL,
				// PostgreSQL properties
				null,	// connection properties
				-1,		// connection pool size
				true,	// assert structures
				false,	// debug queries
				"venus", 5432, "hangman", "hangman", "hangman",
				// Chat DAL properties
				"ChatTestMOQueue", "eventId", "text");
		} catch (SQLException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
}
