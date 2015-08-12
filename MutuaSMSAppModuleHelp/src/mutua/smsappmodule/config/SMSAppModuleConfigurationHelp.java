package mutua.smsappmodule.config;

import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.trgGlobalStartCompositeHelpDialog;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.trgLocalShowNextCompositeHelpMessage;

import java.util.HashMap;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;

/** <pre>
 * SMSAppModuleConfigurationHelp.java
 * ==================================
 * (created by luiz, Jul 14, 2015)
 *
 * Defines the "Help" module configuration variables, implementing the Mutua SMSApp
 * Configuration design pattern, as described in 'SMSAppModuleConfiguration'
 *
 * @see SMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationHelp {

	
	// help module specific functionality -- contextualized helps based on the current navigation state
	private static HashMap<INavigationState, String> statefulHelpMessagesMap = new HashMap<INavigationState, String>();

	
	/*************************************************
	** MutuaICConfiguration CONFIGURABLE PROPERTIES **
	*************************************************/
	
	// phrasing
	///////////
	
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp.getFallbackNewUsersHelp")
	public static String HELPphrNewUsersFallback      = phrNewUsersFallbackHelp.toString();
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp.getFallbackExistingUsersHelp")
	public static String HELPphrFallbackExistingUsers = phrExistingUsersFallbackHelp.toString();
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp.getStatelessHelpMessage")
	public static String HELPphrStateless             = phrStatelessHelp.toString();
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp.getStatefulHelpMessage")
	public static String[] HELPphrComposite           = phrCompositeHelps.toStrings();
	
	
	// command patterns
	///////////////////
	
	@ConfigurableElement("Global triggers (available to all navigation states) to execute the 'composite help dialog', the multiple help/rules/terms of service messages which accepts the MORE keyword (described at 'triggersHELPcmdShowNextCompositeHelpDialog') to present the next one")
	public static String[] HELPtrgGlobalStartCompositeHelpDialog    = trgGlobalStartCompositeHelpDialog;
	@ConfigurableElement("Local triggers (available only to the 'composite help dialog' navigation state) to execute the 'show next composite help message'")
	public static String[] HELPtrgLocalShowNextCompositeHelpMessage = trgLocalShowNextCompositeHelpMessage;

	
	/************
	** METHODS **
	************/
	
	/** Method to configure & apply state specific help messages, where
	 *  statefulHelpMessages := {{(INavigationState)navigationState, (String)helpMessage}, ...} */
	public static void setStatefulHelpMessages(Object[][] statefulHelpMessages) {
		statefulHelpMessagesMap.clear();
		for (Object[] statefulHelpMessage : statefulHelpMessages) {
			INavigationState navigationState = (INavigationState)(statefulHelpMessage[0]);
			String           helpMessage     = (String)          (statefulHelpMessage[1]);
			statefulHelpMessagesMap.put(navigationState, helpMessage);
		}
	}
	
	/** Method to add & apply navigation state command triggers, where
	 *  statefulHelpMessages := {{(INavigationState)navigationState, (String)helpMessage}, ...} */
	public static void addNavigationStateCommandTriggers(SMSAppModuleNavigationStates navigationState) {
		//navigationState.setCommandTriggers(commandTriggersData);
	}
	
	/** returns the help message for a particular state */
	public static String getStatefulHelpMessage(INavigationState navigationState) {
		return statefulHelpMessagesMap.get(navigationState);
	}
	
	/** Apply on-the-fly phrasing changes */
	public static void applyPhrasingConfiguration() {
		phrNewUsersFallbackHelp     .setPhrases(HELPphrNewUsersFallback);
		phrExistingUsersFallbackHelp.setPhrases(HELPphrFallbackExistingUsers);
		phrStatelessHelp            .setPhrases(HELPphrStateless);
		phrCompositeHelps           .setPhrases(HELPphrComposite);
	}
	
	/** Apply on-the-fly command trigger changes */
	public static void applyTriggerConfiguration() {
		for (INavigationState navigationState : SMSAppModuleNavigationStatesHelp.values()) {
			navigationState.setCommandTriggersFromConfigurationValues();
		}
	}
	
	/** Apply the following on-the-fly configuration changes: phrasing, triggers */
	public static void applyConfiguration() {
		applyPhrasingConfiguration();
		applyTriggerConfiguration();
	}
	
	
	static {
		applyConfiguration();
	}
}
