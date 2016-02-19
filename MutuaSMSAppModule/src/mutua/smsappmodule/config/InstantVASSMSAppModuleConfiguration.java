package mutua.smsappmodule.config;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;

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
	 *  This should be the last module to be configured if you will set the 'nstNewUserTriggers' &
	 *  'nstExistingUserTriggers' to point to commands from all the other modules.
	 *  Before running these method, one might want to set the defaults for {@link PostgreSQLAdapter}:<pre>
	 *   {@link PostgreSQLAdapter#configureDefaultValuesForNewInstances}(
	 *       CONNECTION_PROPERTIES,
	 *       CONNECTION_POOL_SIZE);</pre> 
	 *  @param log
	 * @param baseModuleDAL                          one of the members of {@link SMSAppModuleDALFactory}
	 * @param availableCommands                      &
	 * @param nstNewUserTriggers                     &
	 * @param nstExistingUserTriggers                see {@link SMSAppModuleNavigationStates#SMSAppModuleNavigationStates(ICommandProcessor[], Object[][], Object[][])}
	 *  @returns {(SMSAppModuleNavigationStates)navigationStates, (SMSAppModuleCommandsHelp)commands, (SMSAppModulePhrasingsHelp)phrasings} */
	public static Object[] getBaseModuleInstances(Instrumentation<?, ?> log, SMSAppModuleDALFactory baseModuleDAL,
		ICommandProcessor[] availableCommands, Object[][] nstNewUserTriggers,
		Object[][] nstExistingUserTriggers) throws SQLException {
		
		SMSAppModuleNavigationStates navigationStates = new SMSAppModuleNavigationStates(availableCommands, nstNewUserTriggers, nstExistingUserTriggers);
		
		log.reportDebug(InstantVASSMSAppModuleConfiguration.class.getCanonicalName() + ": new configuration loaded.");
		
		return new Object[] {navigationStates, null, null};
	}
}