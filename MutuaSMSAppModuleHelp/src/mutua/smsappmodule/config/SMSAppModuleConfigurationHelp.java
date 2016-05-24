package mutua.smsappmodule.config;

import java.util.Arrays;

import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;

/** <pre>
 * SMSAppModuleConfigurationHelp.java
 * ==================================
 * (created by luiz, Jul 14, 2015)
 *
 * Configure the classes' default values for new instances of the "Help SMS Module".
 * 
 * The methods of this class are the ones external users should call in order to create the instances needed to use the module.
 * 
 * Follows the "Instant VAS SMS Modules" pattern described bellow:
 *
 * @see InstantVASSMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationHelp {
	
	
	/** Constructs the simple version of this SMS Module, with default values, for testing purposes.<pre>
	 *  @param shortCode                            &
	 *  @param appName                              &
	 *  @param phrStatefulHelpMessages              see {@link SMSAppModulePhrasingsHelp#SMSAppModulePhrasingsHelp(String, String, String, String, String, Object[][], String[])}
	 *  @returns {(SMSAppModuleNavigationStatesHelp)navigationStates, (SMSAppModuleCommandsHelp)commands, (SMSAppModulePhrasingsHelp)phrasings} */
	public static Object[] getHelpModuleInstances(String shortCode, String appName, String[][] phrStatefulHelpMessages) {

		SMSAppModulePhrasingsHelp        phrasings        = new SMSAppModulePhrasingsHelp(shortCode, appName, phrStatefulHelpMessages);
		SMSAppModuleCommandsHelp         commands         = new SMSAppModuleCommandsHelp(phrasings);
		SMSAppModuleNavigationStatesHelp navigationStates = new SMSAppModuleNavigationStatesHelp();
		
		System.err.println(SMSAppModuleConfigurationHelp.class.getCanonicalName() + ": test configuration loaded.");
		
		return new Object[] {navigationStates, commands, phrasings};
	}
	
	/** Constructs the full version of this SMS Module, with all options set programmatically.<pre>
	 *  @param shortCode                                 &
	 *  @param appName                                   &
	 *  @param priceTag                                  see {@link SMSAppModulePhrasingsHelp#SMSAppModulePhrasingsHelp(String, String, String, String, String, String, String[][], String[])}
	 *  @param phrNewUsersFallbackHelp                   see {@link SMSAppModulePhrasingsHelp#getNewUsersFallbackHelp()}
	 *  @param phrExistingUsersFallbackHelp              see {@link SMSAppModulePhrasingsHelp#getExistingUsersFallbackHelp()}
	 *  @param phrStatelessHelp                          see {@link SMSAppModulePhrasingsHelp#getStatelessHelpMessage()}
	 *  @param phrStatefulHelpMessages                   see {@link SMSAppModulePhrasingsHelp#getStatefulHelpMessage(INavigationState)}
	 *  @param phrCompositeHelps                         see {@link SMSAppModulePhrasingsHelp#getCompositeHelpMessage(int)}
	 *  @param nstPresentingCompositeHelpCommandTriggers see {@link SMSAppModuleNavigationStatesHelp#SMSAppModuleNavigationStatesHelp(SMSAppModuleCommandsHelp, Object[][])}
	 *  @returns {(SMSAppModuleNavigationStatesHelp)navigationStates, (SMSAppModuleCommandsHelp)commands, (SMSAppModulePhrasingsHelp)phrasings} */
	public static Object[] getHelpModuleInstances(String shortCode, String appName, String priceTag,
		                                          String phrNewUsersFallbackHelp,
		                                          String phrExistingUsersFallbackHelp,
		                                          String phrStatelessHelp,
		                                          String[][] phrStatefulHelpMessages,
		                                          String[] phrCompositeHelps, Object[][] nstPresentingCompositeHelpCommandTriggers) {

		SMSAppModulePhrasingsHelp        phrasings        = new SMSAppModulePhrasingsHelp(shortCode, appName, priceTag, 
			phrNewUsersFallbackHelp, phrExistingUsersFallbackHelp, phrStatelessHelp, phrStatefulHelpMessages, phrCompositeHelps);
		SMSAppModuleCommandsHelp         commands         = new SMSAppModuleCommandsHelp(phrasings);
		SMSAppModuleNavigationStatesHelp navigationStates = new SMSAppModuleNavigationStatesHelp(nstPresentingCompositeHelpCommandTriggers);
		
		// log
		String logPrefix = "Help Module";
		Instrumentation.reportDebug(logPrefix + ": new instances:");
		Object[][] logPhrasings = {
			{"phrNewUsersFallbackHelp",      phrNewUsersFallbackHelp},
			{"phrExistingUsersFallbackHelp", phrExistingUsersFallbackHelp},
			{"phrStatelessHelp",             phrStatelessHelp},
			{"phrStatefulHelpMessages",      phrStatefulHelpMessages},
			{"phrCompositeHelps",            phrCompositeHelps},
		};
		Instrumentation.reportDebug(logPrefix + ": Phrasings        : " + Arrays.deepToString(logPhrasings));
		Object[][] logCommands = {};
		Instrumentation.reportDebug(logPrefix + ": Commands         : " + Arrays.deepToString(logCommands));
		Object[][] logCommandTriggers = {
			{"nstPresentingCompositeHelpCommandTriggers", nstPresentingCompositeHelpCommandTriggers},	
		};
		Instrumentation.reportDebug(logPrefix + ": Navigation States: " + Arrays.deepToString(logCommandTriggers));
		
		return new Object[] {navigationStates, commands, phrasings};
	}
	
}
