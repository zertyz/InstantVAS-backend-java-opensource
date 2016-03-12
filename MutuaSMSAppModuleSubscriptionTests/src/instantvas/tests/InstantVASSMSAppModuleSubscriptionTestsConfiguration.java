package instantvas.tests;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationSubscription;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactorySubscription;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterSubscription;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription;
import mutua.subscriptionengine.TestableSubscriptionAPI;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.CommandNamesSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.CommandTriggersSubscription.*;

/** <pre>
 * InstantVASSMSAppModuleSubscriptionTestsConfiguration.java
 * =========================================================
 * (created by luiz, Jul 27, 2015)
 *
 * Configure the classes' default values for new instances of the "Subscription" SMS Module test application.
 *
 * Typically, the configure* methods on this class must be invoked prior to its usage. 
 * 
 * Follows the "Mutua Configurable Module" pattern tuned for "Instant VAS SMS Modules", described on
 * the Base Modules Test Application Configuration -- {@link InstantVASSMSAppModuleConfiguration}
 * 
 * @author luiz
 */

public class InstantVASSMSAppModuleSubscriptionTestsConfiguration {

	private static final String shortCode = "975";
	private static final String appName   = "SubscriptionTestApp";
	private static final String priceTag  = "0.99";
	
	private static InstantVASSMSAppModuleSubscriptionTestsConfiguration instance = null;

	public static Instrumentation<DefaultInstrumentationProperties, String> LOG;
	public static SMSAppModuleDALFactory                                    BASE_MODULE_DAL;
	public static SMSAppModuleDALFactorySubscription                        SUBSCRIPTION_DAL;
	public static int                                                       PERFORMANCE_TESTS_LOAD_FACTOR;
		
	// instance variables
	public final SMSAppModuleNavigationStates             baseModuleNavigationStates;
	public final SMSAppModulePhrasingsSubscription        subscriptionModulePhrasings;
	public final SMSAppModuleCommandsSubscription         subscriptionModuleCommands;
	public final SMSAppModuleNavigationStatesSubscription subscriptionModuleNavigationStates;
	
	/**************************
	** CONFIGURATION METHODS **
	**************************/
	
	/** method to be called to configure all the modules needed to get instances of the test classes */
	public static void configureDefaultValuesForNewInstances(Instrumentation<DefaultInstrumentationProperties, String> log, 
		int performanceTestsLoadFactor, SMSAppModuleDALFactorySubscription subscriptionDAL,
		String postgreSQLconnectionProperties, int postgreSQLConnectionPoolSize,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postgreSQLShouldDebugQueries,
		String postgreSQLHostname, int postgreSQLPort, String postgreSQLDatabase, String postgreSQLUser, String postgreSQLPassword) throws SQLException {

		instance = null;

		LOG                           = log;
		SUBSCRIPTION_DAL              = subscriptionDAL;
		PERFORMANCE_TESTS_LOAD_FACTOR = performanceTestsLoadFactor;
		
		// database configuration
		switch (subscriptionDAL) {
			case POSTGRESQL:
				PostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLconnectionProperties, postgreSQLConnectionPoolSize);
				SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
					postgreSQLHostname, postgreSQLPort, postgreSQLDatabase, postgreSQLUser, postgreSQLPassword);
				SMSAppModulePostgreSQLAdapterSubscription.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postgreSQLShouldDebugQueries,
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
		
		System.err.println(InstantVASSMSAppModuleSubscriptionTestsConfiguration.class.getCanonicalName() + ": test configuration loaded.");
	}

	public static InstantVASSMSAppModuleSubscriptionTestsConfiguration getInstance() {
		if (instance == null) try {
			instance = new InstantVASSMSAppModuleSubscriptionTestsConfiguration();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		return instance;
	}
	
	// preconfigure with default values
	static {
		// configure with the default values
		try {
			configureDefaultValuesForNewInstances(
				// log
				new Instrumentation<DefaultInstrumentationProperties, String>(
					appName, DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null),
				// modules DAL
				1, SMSAppModuleDALFactorySubscription.POSTGRESQL,
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
	
	private InstantVASSMSAppModuleSubscriptionTestsConfiguration() throws SQLException {
		Object[] subscriptionModule = SMSAppModuleConfigurationSubscription.getSubscriptionModuleInstances(shortCode, appName, priceTag,
		                                                                                                   BASE_MODULE_DAL, SUBSCRIPTION_DAL,
		                                                                                                   new TestableSubscriptionAPI(LOG),
		                                                                                                   "BillingCenterFor_"+appName);
		subscriptionModuleNavigationStates = (SMSAppModuleNavigationStatesSubscription) subscriptionModule[0];
		subscriptionModuleCommands         = (SMSAppModuleCommandsSubscription)         subscriptionModule[1];
		subscriptionModulePhrasings        = (SMSAppModulePhrasingsSubscription)        subscriptionModule[2];
		
		// base module -- configured to interact with the Subscription Module commands 
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(LOG, BASE_MODULE_DAL,
			/*nstNewUserTriggers*/
			new Object[][] {
				{cmdStartDoubleOptinProcess, trgLocalStartDoubleOptin},
			},
			/*nstExistingUserTriggers*/
			new Object[][] {
				{cmdUnsubscribe, trgGlobalUnsubscribe},
			});
		baseModuleNavigationStates = (SMSAppModuleNavigationStates) baseModule[0];
	}
}