package instantvas.tests;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
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

	private static Instrumentation<?, ?>   LOG;
	public  static SMSAppModuleDALFactory  BASE_MODULE_DAL;
	public  static int                     PERFORMANCE_TESTS_LOAD_FACTOR;

	public SMSAppModuleNavigationStates baseModuleNavigationStates;
		
	/**************************
	** CONFIGURATION METHODS **
	**************************/
	
	/** method to be called to configure all the modules needed to get the desired instance of 'InstantVASSMSAppModule' base modules */
	public static void configureDefaultValuesForNewInstances(Instrumentation<?, ?> log, 
		int performanceTestsLoadFactor, SMSAppModuleDALFactory baseModuleDAL,
		String postgreSQLConnectionProperties, int postgreSQLConnectionPoolSize,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postreSQLShouldDebugQueries, String postreSQLHostname, int postreSQLPort, String postreSQLDatabase,
		String postreSQLUser, String postreSQLPassword) throws SQLException {
		
		instance = null;

		LOG                           = log;
		BASE_MODULE_DAL               = baseModuleDAL;
		PERFORMANCE_TESTS_LOAD_FACTOR = performanceTestsLoadFactor;
		
		// database configuration
		switch (baseModuleDAL) {
			case POSTGRESQL:
				PostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLConnectionProperties, postgreSQLConnectionPoolSize);
				SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postreSQLShouldDebugQueries,
					postreSQLHostname, postreSQLPort, postreSQLDatabase, postreSQLUser, postreSQLPassword);
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
		try {
			configureDefaultValuesForNewInstances(
				// log
				new Instrumentation<DefaultInstrumentationProperties, String>(
					"SMSModuleTests", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null),
				1, SMSAppModuleDALFactory.POSTGRESQL,
				// PostgreSQL properties (don't touch default connection properties & pool size)
				null, 0,
				true, false, "venus", 5432, "hangman", "hangman", "hangman");
		} catch (SQLException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	/*****************
	** CONSTRUCTORS **
	*****************/
	
	private InstantVASSMSAppModuleTestsConfiguration() throws SQLException {
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(LOG, BASE_MODULE_DAL, new ICommandProcessor[0],
			new Object[0][], new Object[0][]);
		baseModuleNavigationStates = (SMSAppModuleNavigationStates) baseModule[0];

	}
}
