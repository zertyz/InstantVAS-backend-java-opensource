package mutua.smsappmodule.config;

import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactorySubscription;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription;
import mutua.subscriptionengine.SubscriptionEngine;

/** <pre>
 * SMSAppModuleConfigurationSubscription.java
 * ==========================================
 * (created by luiz, Jul 14, 2015)
 *
 * Configure the classes' default values for new instances of the "Subscription SMS Module".
 * 
 * The methods of this class are the ones external users should call in order to create the instances needed to use the module.
 * 
 * Follows the "Instant VAS SMS Modules" pattern described bellow:
 *
 * @see InstantVASSMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationSubscription {
	
	/** Constructs the simple version of this SMS Module, with default values, for testing purposes.
	 *  @param shortCode          &
	 *  @param appName            &
	 *  @param priceTag           see {@link SMSAppModulePhrasingsSubscription#SMSAppModulePhrasingsSubscription(String, String, String, String, String, String, String, String, String)}
	 *  @param baseModulesDAL     &
	 *  @param subscriptionDAL    &
	 *  @param subscriptionEngine & 
	 *  @param subscriptionToken  see {@link SMSAppModuleCommandsSubscription#SMSAppModuleCommandsSubscription(SMSAppModulePhrasingsSubscription, SMSAppModuleDALFactory, SMSAppModuleDALFactorySubscription, SubscriptionEngine, String)}
	 *  @returns {(SMSAppModuleNavigationStatesSubscription)navigationStates, (SMSAppModuleCommandsSubscription)commands, (SMSAppModulePhrasingsSubscription)phrasings} */
	public static Object[] getSubscriptionModuleInstances(String shortCode, String appName, String priceTag,
	                                                      SMSAppModuleDALFactory             baseModulesDAL,
                                                          SMSAppModuleDALFactorySubscription subscriptionDAL,
                                                          SubscriptionEngine                 subscriptionEngine,
                                                          String                             subscriptionToken) {
		SMSAppModulePhrasingsSubscription        phrasings        = new SMSAppModulePhrasingsSubscription(shortCode, appName, priceTag);
		SMSAppModuleCommandsSubscription         commands         = new SMSAppModuleCommandsSubscription(phrasings, baseModulesDAL, subscriptionDAL,
		                                                                                                 subscriptionEngine, subscriptionToken);
		SMSAppModuleNavigationStatesSubscription navigationStates = new SMSAppModuleNavigationStatesSubscription(commands);
		
		System.err.println(SMSAppModuleConfigurationSubscription.class.getName() + ": test configuration loaded.");
		
		return new Object[] {navigationStates, commands, phrasings};
	}
}
