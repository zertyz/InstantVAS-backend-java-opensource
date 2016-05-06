package instantvas.tests;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.handlers.IInstrumentationHandler;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerLogConsole;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationHangman;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryHangman;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterHangman;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** <pre>
 * InstantVASSMSAppModuleHangmanTestsConfiguration.java
 * ====================================================
 * (created by luiz, Aug 13, 2015)
 *
 * Configure the classes' default values for new instances of the "Hangman" SMS Module test application.
 *
 * Typically, the configure* methods on this class must be invoked prior to its usage. 
 * 
 * Follows the "Mutua Configurable Module" pattern tuned for "Instant VAS SMS Modules", described on
 * the Base Modules Test Application Configuration -- {@link InstantVASSMSAppModuleConfiguration}
 *
 * @author luiz
 */

public class InstantVASSMSAppModuleHangmanTestsConfiguration {

	public  static final String shortCode = "975";
	private static final String appName   = "HangmanTestApp";
	
	private static InstantVASSMSAppModuleHangmanTestsConfiguration instance = null;

	public static SMSAppModuleDALFactory                                    BASE_MODULE_DAL;
	public static SMSAppModuleDALFactoryProfile                             PROFILE_MODULE_DAL;
	public static SMSAppModuleDALFactoryHangman                             HANGMAN_MODULE_DAL;
	public static int                                                       PERFORMANCE_TESTS_LOAD_FACTOR;
	
	public SMSAppModuleNavigationStates        baseModuleNavigationStates;
	public SMSAppModulePhrasingsHangman        hangmanModulePhrasings;
	public SMSAppModuleCommandsHangman         hangmanModuleCommands;
	public SMSAppModuleNavigationStatesHangman hangmanModuleNavigationStates;
	
	/**************************
	** CONFIGURATION METHODS **
	**************************/
	
	/** method to be called to configure all the modules needed to get instances of the test classes */
	public static void configureDefaultValuesForNewInstances(
		int performanceTestsLoadFactor, SMSAppModuleDALFactoryHangman hangmanModuleDAL,
		String postgreSQLconnectionProperties, int postgreSQLConnectionPoolSize,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postgreSQLShouldDebugQueries,
		String postgreSQLHostname, int postgreSQLPort, String postgreSQLDatabase, String postgreSQLUser, String postgreSQLPassword) throws SQLException {
		
		instance = null;
		
		HANGMAN_MODULE_DAL            = hangmanModuleDAL;
		PERFORMANCE_TESTS_LOAD_FACTOR = performanceTestsLoadFactor;
		
		// database configuration
		switch (hangmanModuleDAL) {
			case POSTGRESQL:
				PostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLconnectionProperties, postgreSQLConnectionPoolSize);
				SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
				SMSAppModulePostgreSQLAdapterProfile.configureDefaultValuesForNewInstances(postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
				SMSAppModulePostgreSQLAdapterHangman.configureDefaultValuesForNewInstances(postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
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
		
		System.err.println(InstantVASSMSAppModuleHangmanTestsConfiguration.class.getCanonicalName() + ": test configuration loaded.");
	}
	
	public static InstantVASSMSAppModuleHangmanTestsConfiguration getInstance() {
		if (instance == null) try {
			instance = new InstantVASSMSAppModuleHangmanTestsConfiguration();
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
				1, SMSAppModuleDALFactoryHangman.POSTGRESQL,
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
	
	private InstantVASSMSAppModuleHangmanTestsConfiguration() throws SQLException {
		
		Object[] hangmanModule = SMSAppModuleConfigurationHangman.getHangmanModuleInstances(shortCode, appName, null, BASE_MODULE_DAL, PROFILE_MODULE_DAL, HANGMAN_MODULE_DAL, "Guest");
		
		hangmanModuleNavigationStates = (SMSAppModuleNavigationStatesHangman) hangmanModule[0];
		hangmanModuleCommands         = (SMSAppModuleCommandsHangman)         hangmanModule[1];
		hangmanModulePhrasings        = (SMSAppModulePhrasingsHangman)        hangmanModule[2];
		
		// base module -- configured to interact with the Profile Module commands 
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(BASE_MODULE_DAL,
			/*nstNewUserTriggers*/
			new Object[][] {
//				{cmdStartAskForNicknameDialog, trgGlobalStartAskForNicknameDialog},
			},
			/*nstExistingUserTriggers*/
			new Object[][] {
//				{cmdRegisterNickname,                trgGlobalRegisterNickname},
//				{cmdStartAskForNicknameDialog,       trgGlobalStartAskForNicknameDialog},
//				{cmdShowUserProfile,                 trgGlobalShowUserProfile},
			});
		baseModuleNavigationStates = (SMSAppModuleNavigationStates)baseModule[0];
	}
}