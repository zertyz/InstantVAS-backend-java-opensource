package instantvas.tests;

import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationProfile;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.CommandNamesProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.CommandTriggersProfile.*;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;

/** <pre>
 * SMSAppModuleConfigurationProfileTests.java
 * ==========================================
 * (created by luiz, Aug 3, 2015)
 *
 * Configure the classes' default values for new instances of the "Profile" SMS Module test application.
 *
 * Typically, the configure* methods on this class must be invoked prior to its usage. 
 * 
 * Follows the "Mutua Configurable Module" pattern tuned for "Instant VAS SMS Modules", described on
 * the Base Modules Test Application Configuration -- {@link InstantVASSMSAppModuleConfiguration}
 *
 * @author luiz
 */

public class InstantVASSMSAppModuleProfileTestsConfiguration {

	public  static final String shortCode = "975";
	private static final String appName   = "ProfileTestApp";
	
	private static InstantVASSMSAppModuleProfileTestsConfiguration instance = null;

	public static Instrumentation<DefaultInstrumentationProperties, String> LOG;
	public static SMSAppModuleDALFactory                                    BASE_MODULE_DAL;
	public static SMSAppModuleDALFactoryProfile                             PROFILE_MODULE_DAL;
	public static int                                                       PERFORMANCE_TESTS_LOAD_FACTOR;
	
	public SMSAppModuleNavigationStates        baseModuleNavigationStates;
	public SMSAppModulePhrasingsProfile        profileModulePhrasings;
	public SMSAppModuleCommandsProfile         profileModuleCommands;
	public SMSAppModuleNavigationStatesProfile profileModuleNavigationStates;
	
	/**************************
	** CONFIGURATION METHODS **
	**************************/
	
	/** method to be called to configure all the modules needed to get instances of the test classes */
	public static void configureDefaultValuesForNewInstances(Instrumentation<DefaultInstrumentationProperties, String> log, 
		int performanceTestsLoadFactor, SMSAppModuleDALFactoryProfile profileModuleDAL,
		String postgreSQLconnectionProperties, int postgreSQLConnectionPoolSize,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postgreSQLShouldDebugQueries,
		String postgreSQLHostname, int postgreSQLPort, String postgreSQLDatabase, String postgreSQLUser, String postgreSQLPassword) throws SQLException {
		
		instance = null;
		
		LOG                           = log;
		PROFILE_MODULE_DAL            = profileModuleDAL;
		PERFORMANCE_TESTS_LOAD_FACTOR = performanceTestsLoadFactor;
		
		// database configuration
		switch (profileModuleDAL) {
			case POSTGRESQL:
				PostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLconnectionProperties, postgreSQLConnectionPoolSize);
				SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
				SMSAppModulePostgreSQLAdapterProfile.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
				// other databases
				BASE_MODULE_DAL = SMSAppModuleDALFactory.POSTGRESQL;
				break;
			case RAM:
				// other databases
				BASE_MODULE_DAL = SMSAppModuleDALFactory.RAM;
				break;
			default:
				throw new NotImplementedException();
		}
		
		System.err.println(InstantVASSMSAppModuleProfileTestsConfiguration.class.getCanonicalName() + ": test configuration loaded.");
	}
	
	public static InstantVASSMSAppModuleProfileTestsConfiguration getInstance() {
		if (instance == null) try {
			instance = new InstantVASSMSAppModuleProfileTestsConfiguration();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		return instance;
	}

	static {
		// configure with the default values
		try {
			configureDefaultValuesForNewInstances(
				// log
				new Instrumentation<DefaultInstrumentationProperties, String>(
					appName, DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null),
				// module DAL
				1, SMSAppModuleDALFactoryProfile.POSTGRESQL,
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
	
	private InstantVASSMSAppModuleProfileTestsConfiguration() throws SQLException {
		
		Object[] profileModule = SMSAppModuleConfigurationProfile.getProfileModuleInstances(shortCode, appName, PROFILE_MODULE_DAL);
		
		profileModuleNavigationStates = (SMSAppModuleNavigationStatesProfile) profileModule[0];
		profileModuleCommands         = (SMSAppModuleCommandsProfile)         profileModule[1];
		profileModulePhrasings        = (SMSAppModulePhrasingsProfile)        profileModule[2];
		
		// base module -- configured to interact with the Profile Module commands 
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(LOG, BASE_MODULE_DAL,
			/*nstNewUserTriggers*/
			new Object[][] {
				{cmdStartAskForNicknameDialog, trgGlobalStartAskForNicknameDialog},
			},
			/*nstExistingUserTriggers*/
			new Object[][] {
				{cmdRegisterNickname,                trgGlobalRegisterNickname},
				{cmdStartAskForNicknameDialog,       trgGlobalStartAskForNicknameDialog},
				{cmdShowUserProfile,                 trgGlobalShowUserProfile},
			});
		baseModuleNavigationStates = (SMSAppModuleNavigationStates)baseModule[0];
	}
}