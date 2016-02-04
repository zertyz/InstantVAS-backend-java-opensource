package instantvas.tests;

import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationHelp;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;

/** <pre>
 * InstantVASSMSAppModuleHelpTestsConfiguration.java
 * =================================================
 * (created by luiz, Aug 10, 2015)
 *
 * Configure the classes' default values for new instances of the "Help" module test application
 *
 * Typically, the configure* methods on this class must be invoked prior to its usage. 
 * 
 * Follows the "Mutua Configurable Module" pattern tuned for Instant VAS Modules, described bellow:
 *
 * {@code
 * 	get it from the help module by now
 * }
 *
 *
 * @see InstantVASSMSAppModuleConfiguration
 * @author luiz
 */

public class InstantVASSMSAppModuleHelpTestsConfiguration {

	public static Instrumentation<DefaultInstrumentationProperties, String> log;
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
	
	/** method to be called to configure all the modules needed to get the desired instance of 'SMSAppModuleHelp' */
	public static void configureDefaultValuesForNewInstances(Instrumentation<DefaultInstrumentationProperties, String> log, 
		SMSAppModuleDALFactory defaultModuleDAL, String postgreSQLconnectionProperties, int postgreSQLConnectionPoolSize,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postreSQLShouldDebugQueries,
		String postreSQLHostname, int postreSQLPort, String postreSQLDatabase, String postreSQLUser, String postreSQLPassword) throws SQLException {
		
		InstantVASSMSAppModuleHelpTestsConfiguration.log = log;
		// Suggested by 'InstantVASSMSAppModuleConfiguration.configureSMSAppModule()' */
		PostgreSQLAdapter.configureDefaultValuesForNewInstances(postgreSQLconnectionProperties, postgreSQLConnectionPoolSize);
		Object[] helpModule = SMSAppModuleConfigurationHelp.getHelpModuleInstances("1234", "HelpApp", new String[][] {
			{SMSAppModuleNavigationStates.NavigationStatesNames.nstExistingUser,                    expectedNstExistingUserStatefulHelpMessage},
			{SMSAppModuleNavigationStates.NavigationStatesNames.nstNewUser,                         expectedNstNewUserStatefulHelpMessage}});
		helpModuleNavigationStates = (SMSAppModuleNavigationStatesHelp) helpModule[0];
		helpModuleCommands         = (SMSAppModuleCommandsHelp)         helpModule[1];
		helpModulePhrasings        = (SMSAppModulePhrasingsHelp)        helpModule[2];
		// Suggested by 'SMSAppModuleConfigurationHelp.getHelpModuleNavigationStates' 
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(log, defaultModuleDAL, postgreSQLAllowDataStructuresAssertion,
			postreSQLShouldDebugQueries, postreSQLHostname, postreSQLPort, postreSQLDatabase, postreSQLUser, postreSQLPassword,
			helpModuleCommands.values,
			new Object[0][] /*nstNewUserTriggers*/,
			new Object[0][] /*nstExistingUserTriggers*/);
		baseModuleNavigationStates = (SMSAppModuleNavigationStates) baseModule[0];
		
		System.err.println(InstantVASSMSAppModuleHelpTestsConfiguration.class.getName() + ": test configuration loaded.");
	}

	
	static {
		// configure with the default values
		try {
			configureDefaultValuesForNewInstances(
				// log
				new Instrumentation<DefaultInstrumentationProperties, String>(
					"SMSHelpModuleTests", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null),
				// default base module DAL
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
