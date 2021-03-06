package instantvas.tests;

import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.handlers.IInstrumentationHandler;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerLogConsole;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationHelp;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;

/** <pre>
 * InstantVASSMSAppModuleHelpTestsConfiguration.java
 * =================================================
 * (created by luiz, Aug 10, 2015)
 *
 * Configure the classes' default values for new instances of the "Help" SMS Module test application
 *
 * Typically, the configure* methods on this class must be invoked prior to its usage. 
 * 
 * Follows the "Mutua Configurable Module" pattern tuned for "Instant VAS SMS Modules", described on
 * the Base Modules Test Application Configuration -- {@link InstantVASSMSAppModuleConfiguration}
 *
 * @author luiz
 */

public class InstantVASSMSAppModuleHelpTestsConfiguration {
	
	private static final String shortCode = "975";
	private static final String appName   = "HelpTestApp";
	
	private static InstantVASSMSAppModuleHelpTestsConfiguration instance = null;

	public static SMSAppModuleDALFactory           BASE_MODULE_DAL;
	public static SMSAppModuleNavigationStates     baseModuleNavigationStates;
	public static SMSAppModulePhrasingsHelp        helpModulePhrasings;
	public static SMSAppModuleCommandsHelp         helpModuleCommands;
	public static SMSAppModuleNavigationStatesHelp helpModuleNavigationStates;
	
	// expected stateful help messages
	public final static String expectedNstExistingUserStatefulHelpMessage = "Here is the stateful help for 'nstExistingUser's";
	public final static String expectedNstNewUserStatefulHelpMessage      = "Here is the stateful help for 'nstNewUser's --  -- it doesn't make sense for neither the above, but we should test with what we have...";
	
	/************
	** METHODS **
	************/
	
	/** method to be called to configure all the modules needed to get instances of the test classes */
	public static void configureDefaultValuesForNewInstances(
		SMSAppModuleDALFactory baseModuleDAL, String postgreSQLconnectionProperties, int postgreSQLConnectionPoolSize,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postreSQLShouldDebugQueries,
		String postreSQLHostname, int postreSQLPort, String postreSQLDatabase, String postreSQLUser, String postreSQLPassword) throws SQLException {
		
		BASE_MODULE_DAL = baseModuleDAL;
		
		instance = null;
		
		// database configuration
		switch (baseModuleDAL) {
			case POSTGRESQL:
				PostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLconnectionProperties, postgreSQLConnectionPoolSize);
				SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLAllowDataStructuresAssertion, postreSQLShouldDebugQueries,
					postreSQLHostname, postreSQLPort, postreSQLDatabase, postreSQLUser, postreSQLPassword);
				break;
			case MVSTORE:
				break;
			case RAM:
				break;
			default:
				throw new NotImplementedException();
		}

		// help module
		Object[] helpModule = SMSAppModuleConfigurationHelp.getHelpModuleInstances(shortCode, appName, new String[][] {
			{SMSAppModuleNavigationStates.NavigationStatesNames.nstExistingUser, expectedNstExistingUserStatefulHelpMessage},
			{SMSAppModuleNavigationStates.NavigationStatesNames.nstNewUser,      expectedNstNewUserStatefulHelpMessage}});
		helpModuleNavigationStates = (SMSAppModuleNavigationStatesHelp) helpModule[0];
		helpModuleCommands         = (SMSAppModuleCommandsHelp)         helpModule[1];
		helpModulePhrasings        = (SMSAppModulePhrasingsHelp)        helpModule[2];
		
		// base module -- configured to interact with the Help Module commands
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(baseModuleDAL,
			/*nstNewUserTriggers*/
			new Object[0][],	// zeroed since we are not testing the help modules through the command processor
			/*nstExistingUserTriggers*/
			new Object[0][]);	// idem
		baseModuleNavigationStates = (SMSAppModuleNavigationStates) baseModule[0];
		
		System.err.println(InstantVASSMSAppModuleHelpTestsConfiguration.class.getCanonicalName() + ": test configuration loaded.");
	}

	public static InstantVASSMSAppModuleHelpTestsConfiguration getInstance() {
		if (instance == null) try {
			instance = new InstantVASSMSAppModuleHelpTestsConfiguration();
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
				// modules DAL
				SMSAppModuleDALFactory.POSTGRESQL,
				// PostgreSQL properties
				null,	// connection properties
				-1,		// connection pool size
				true,	// assert structures
				true,	// debug queries
				"venus", 5432, "hangman", "hangman", "hangman");
		} catch (SQLException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
}