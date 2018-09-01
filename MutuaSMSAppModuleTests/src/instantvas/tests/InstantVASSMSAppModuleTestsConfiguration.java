package instantvas.tests;

import java.sql.SQLException;

import adapters.MVStoreAdapter;
import adapters.PostgreSQLAdapter;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.handlers.IInstrumentationHandler;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerLogConsole;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerNull;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** <pre>
 * InstantVASSMSAppModuleTestsConfiguration.java
 * =============================================
 * (created by luiz, Jul 28, 2015)
 *
 * Configures the classes' static options for the "Base" SMS Module test application.
 * 
 * Follows the "Mutua Configurable Module" pattern tuned for "Instant VAS SMS Modules", described bellow:
 * 
 * {@code
 * 	get it from the help module by now
 * }
 *
 * @version $Id$
 * @author luiz
 */

public class InstantVASSMSAppModuleTestsConfiguration {
	
	private static InstantVASSMSAppModuleTestsConfiguration instance = null;

	public  static SMSAppModuleDALFactory  BASE_MODULE_DAL;
	public  static int                     PERFORMANCE_TESTS_LOAD_FACTOR;

	public SMSAppModuleNavigationStates baseModuleNavigationStates;
		
	/**************************
	** CONFIGURATION METHODS **
	**************************/
	
	/** method to be called to configure all the modules needed to get the desired instance of 'InstantVASSMSAppModule' base modules */
	public static void configureDefaultValuesForNewInstances(
		int performanceTestsLoadFactor, SMSAppModuleDALFactory baseModuleDAL,
		String postgreSQLConnectionProperties, int postgreSQLConnectionPoolSize,
		String mvStoreDatabaseFileName,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postreSQLShouldDebugQueries, String postreSQLHostname, int postreSQLPort, String postreSQLDatabase,
		String postreSQLUser, String postreSQLPassword) throws SQLException {
		
		instance = null;

		BASE_MODULE_DAL               = baseModuleDAL;
		PERFORMANCE_TESTS_LOAD_FACTOR = performanceTestsLoadFactor;
		
		// database configuration
		switch (baseModuleDAL) {
			case POSTGRESQL:
				PostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLConnectionProperties, postgreSQLConnectionPoolSize);
				SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLAllowDataStructuresAssertion, postreSQLShouldDebugQueries,
					postreSQLHostname, postreSQLPort, postreSQLDatabase, postreSQLUser, postreSQLPassword);
				break;
			case MVSTORE:
				MVStoreAdapter.configureDefaultValuesForNewInstances(mvStoreDatabaseFileName);
				break;
			case RAM:
				break;
			default:
				throw new NotImplementedException();
		}
		
		System.err.println(InstantVASSMSAppModuleTestsConfiguration.class.getCanonicalName() + ": test configuration loaded.");
	}
	
	public static InstantVASSMSAppModuleTestsConfiguration getInstance() {
		if (instance == null) try {
			instance = new InstantVASSMSAppModuleTestsConfiguration();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		return instance;
	}
	
	static {
		
		// Instrumentation
		IInstrumentationHandler log = new InstrumentationHandlerLogConsole("SMSModuleTests", ELogSeverity.DEBUG);
		Instrumentation.configureDefaultValuesForNewInstances(log, InstrumentationHandlerNull.instance, InstrumentationHandlerNull.instance);

		try {
			configureDefaultValuesForNewInstances(
				10, SMSAppModuleDALFactory.MVSTORE,
				// PostgreSQL properties (don't touch default connection properties & pool size)
				null, 0,
				// MVStore
				"/tmp/InstantVASSMSAppModuleTests.mvstoredb",
				true, false, "venus", 5432, "hangman", "hangman", "hangman");
		} catch (SQLException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	/*****************
	** CONSTRUCTORS **
	*****************/
	
	private InstantVASSMSAppModuleTestsConfiguration() throws SQLException {
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(BASE_MODULE_DAL,
			new Object[0][], new Object[0][]);
		baseModuleNavigationStates = (SMSAppModuleNavigationStates) baseModule[0];

	}
}
