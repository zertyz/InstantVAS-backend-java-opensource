package mutua.smsappmodule.config;

import java.sql.SQLException;

import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** <pre>
 * InstantVASSMSAppModuleConfiguration.java
 * ========================================
 * (created by luiz, Feb 2, 2016)
 * 
 * Configure the classes' default values for new instances of the base sms application module.
 * 
 * The methods of this class are the ones external users should call in order to create the instances needed to use the module.
 * 
 * Follows the "Instant VAS SMS Modules" pattern described bellow:
 *
 * {@code
 * 	get it from the help module by now
 * }
 *
 * @version $Id$
 * @author luiz
 */

public class InstantVASSMSAppModuleConfiguration {

	/** Constructs the full version of 'InstantVASSMSAppModule', with all options set programmatically.
	 *  Before running these method, one might want to set the defaults for {@link PostgreSQLAdapter}:<pre>
	 *   {@link PostgreSQLAdapter#configureDefaultValuesForNewInstances}(
	 *       CONNECTION_PROPERTIES,
	 *       CONNECTION_POOL_SIZE);</pre> 
	 *  @param log
	 *  @param defaultModuleDAL 
	 *  @param postgreSQLAllowDataStructuresAssertion see {@link JDBCAdapter#allowDataStructuresAssertion}
	 *  @param postreSQLShouldDebugQueries            see {@link JDBCAdapter#shouldDebugQueries}
	 *  @param postreSQLHostname                      see {@link JDBCAdapter#hostname}
	 *  @param postreSQLPort                          see {@link JDBCAdapter#port}
	 *  @param postreSQLDatabase                      see {@link JDBCAdapter#database}
	 *  @param postreSQLUser                          see {@link JDBCAdapter#user}
	 *  @param postreSQLPassword                      see {@link JDBCAdapter#password}
	 *  @param availableCommands                      see {@link SMSAppModuleNavigationStates#SMSAppModuleNavigationStates(ICommandProcessor[], Object[][], Object[][])}
	 *  @param nstNewUserTriggers                     see {@link SMSAppModuleNavigationStates#SMSAppModuleNavigationStates(ICommandProcessor[], Object[][], Object[][])}
	 *  @param nstExistingUserTriggers                see {@link SMSAppModuleNavigationStates#SMSAppModuleNavigationStates(ICommandProcessor[], Object[][], Object[][])}
	 *  @returns {(SMSAppModuleNavigationStates)navigationStates, (SMSAppModuleCommandsHelp)commands, (SMSAppModulePhrasingsHelp)phrasings} */
	public static Object[] getBaseModuleInstances(Instrumentation<DefaultInstrumentationProperties, String> log, SMSAppModuleDALFactory defaultModuleDAL,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postreSQLShouldDebugQueries,
		String postreSQLHostname, int postreSQLPort, String postreSQLDatabase, String postreSQLUser, String postreSQLPassword,
		ICommandProcessor[] availableCommands,
        Object[][] nstNewUserTriggers,
        Object[][] nstExistingUserTriggers) throws SQLException {
		
		// Configure the DAL
		switch (defaultModuleDAL) {
			case POSTGRESQL:
				SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(log, postgreSQLAllowDataStructuresAssertion, postreSQLShouldDebugQueries,
					postreSQLHostname, postreSQLPort, postreSQLDatabase, postreSQLUser, postreSQLPassword);
				break;
			case RAM:
				break;
			default:
				throw new NotImplementedException();
		}

		SMSAppModuleNavigationStates navigationStates = new SMSAppModuleNavigationStates(availableCommands, nstNewUserTriggers, nstExistingUserTriggers);
		
		log.reportDebug(InstantVASSMSAppModuleConfiguration.class.getName() + ": new configuration loaded.");
		
		return new Object[] {navigationStates, null, null};
	}
}