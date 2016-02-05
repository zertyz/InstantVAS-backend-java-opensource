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
	
	public static Instrumentation<DefaultInstrumentationProperties, String> LOG;
	public static SMSAppModuleDALFactory                                    BASE_MODULE_DAL;
	public static SMSAppModuleDALFactorySubscription                        SUBSCRIPTION_DAL;
	public static SMSAppModuleNavigationStates             baseModuleNavigationStates;
	public static SMSAppModulePhrasingsSubscription        subscriptionModulePhrasings;
	public static SMSAppModuleCommandsSubscription         subscriptionModuleCommands;
	public static SMSAppModuleNavigationStatesSubscription subscriptionModuleNavigationStates;
	
	/************
	** METHODS **
	************/
	
	/** method to be called to configure all the modules needed to get the desired instance of 'SMSAppModuleHelp' */
	public static void configureDefaultValuesForNewInstances(Instrumentation<DefaultInstrumentationProperties, String> log, 
		SMSAppModuleDALFactory baseModuleDAL, SMSAppModuleDALFactorySubscription subscriptionDAL,
		String postgreSQLconnectionProperties, int postgreSQLConnectionPoolSize,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postreSQLShouldDebugQueries,
		String postreSQLHostname, int postreSQLPort, String postreSQLDatabase, String postreSQLUser, String postreSQLPassword) throws SQLException {
		
		LOG              = log;
		BASE_MODULE_DAL  = baseModuleDAL;
		SUBSCRIPTION_DAL = subscriptionDAL;
		
		// database configuration
		switch (baseModuleDAL) {
			case POSTGRESQL:
				PostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLconnectionProperties, postgreSQLConnectionPoolSize);
				SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postreSQLShouldDebugQueries,
					postreSQLHostname, postreSQLPort, postreSQLDatabase, postreSQLUser, postreSQLPassword);
				SMSAppModulePostgreSQLAdapterSubscription.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postreSQLShouldDebugQueries,
					postreSQLHostname, postreSQLPort, postreSQLDatabase, postreSQLUser, postreSQLPassword);
				break;
			case RAM:
				break;
			default:
				throw new NotImplementedException();
		}
		
		// subscription module
		Object[] subscriptionModule = SMSAppModuleConfigurationSubscription.getSubscriptionModuleInstances(shortCode, appName, priceTag,
		                                                                                                   baseModuleDAL, subscriptionDAL,
		                                                                                                   new TestableSubscriptionAPI(log),
		                                                                                                   "BillingCenterFor_"+appName);
		subscriptionModuleNavigationStates = (SMSAppModuleNavigationStatesSubscription) subscriptionModule[0];
		subscriptionModuleCommands         = (SMSAppModuleCommandsSubscription)         subscriptionModule[1];
		subscriptionModulePhrasings        = (SMSAppModulePhrasingsSubscription)        subscriptionModule[2];
		
		// base module -- configured to interact with the Subscription Module commands 
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(log, baseModuleDAL, subscriptionModuleCommands.values,
			/*nstNewUserTriggers*/
			new Object[][] {
				{cmdStartDoubleOptinProcess, trgLocalStartDoubleOptin},
			},
			/*nstExistingUserTriggers*/
			new Object[][] {
				{cmdUnsubscribe, trgGlobalUnsubscribe},
			});
		baseModuleNavigationStates = (SMSAppModuleNavigationStates) baseModule[0];
		
		System.err.println(InstantVASSMSAppModuleSubscriptionTestsConfiguration.class.getName() + ": test configuration loaded.");
	}

	
	static {
		// configure with the default values
		try {
			configureDefaultValuesForNewInstances(
				// log
				new Instrumentation<DefaultInstrumentationProperties, String>(
					appName, DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null),
				// modules DAL
				SMSAppModuleDALFactory            .POSTGRESQL,
				SMSAppModuleDALFactorySubscription.POSTGRESQL,
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
}