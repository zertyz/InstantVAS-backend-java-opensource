package mutua.smsappmodule.config;

import java.util.Arrays;

import mutua.icc.instrumentation.Instrumentation;
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
	
	/** Constructs the simple version of this SMS Module, with default values, for testing purposes.<pre>
	 *  @param shortCode          &
	 *  @param appName            &
	 *  @param priceTag           see {@link SMSAppModulePhrasingsSubscription#SMSAppModulePhrasingsSubscription(String, String, String, String, String, String, String, String, String)}
	 *  @param baseModuleDAL      &
	 *  @param subscriptionDAL    &
	 *  @param subscriptionEngine & 
	 *  @param subscriptionToken  see {@link SMSAppModuleCommandsSubscription#SMSAppModuleCommandsSubscription(SMSAppModulePhrasingsSubscription, SMSAppModuleDALFactory, SMSAppModuleDALFactorySubscription, SubscriptionEngine, String)}
	 *  @returns {(SMSAppModuleNavigationStatesSubscription)navigationStates, (SMSAppModuleCommandsSubscription)commands, (SMSAppModulePhrasingsSubscription)phrasings} */
	public static Object[] getSubscriptionModuleInstances(String shortCode, String appName, String priceTag,
	                                                      SMSAppModuleDALFactory             baseModuleDAL,
                                                          SMSAppModuleDALFactorySubscription subscriptionDAL,
                                                          SubscriptionEngine                 subscriptionEngine,
                                                          String                             subscriptionToken) {
		SMSAppModulePhrasingsSubscription        phrasings        = new SMSAppModulePhrasingsSubscription(shortCode, appName, priceTag);
		SMSAppModuleCommandsSubscription         commands         = new SMSAppModuleCommandsSubscription(phrasings, baseModuleDAL, subscriptionDAL,
		                                                                                                 subscriptionEngine, subscriptionToken);
		SMSAppModuleNavigationStatesSubscription navigationStates = new SMSAppModuleNavigationStatesSubscription();
		
		System.err.println(SMSAppModuleConfigurationSubscription.class.getCanonicalName() + ": test configuration loaded.");
		
		return new Object[] {navigationStates, commands, phrasings};
	}
	
	/** Constructs the full version of this SMS Module, with all options set programmatically.<pre>
	 *  @param log
	 *  @param shortCode                      &
	 *  @param appName                        &
	 *  @param priceTag                       &
	 *  @param phrDoubleOptinStart            &
	 *  @param phrDisagreeToSubscribe         &
	 *  @param phrSuccessfullySubscribed      &
	 *  @param phrCouldNotSubscribe           &
	 *  @param phrUserRequestedUnsubscription &
	 *  @param phrLifecycleUnsubscription     see {@link SMSAppModulePhrasingsSubscription#SMSAppModulePhrasingsSubscription(String, String, String, String, String, String, String, String, String)}
	 *  @param baseModuleDAL      &
	 *  @param subscriptionDAL    &
	 *  @param subscriptionEngine & 
	 *  @param subscriptionToken  see {@link SMSAppModuleCommandsSubscription#SMSAppModuleCommandsSubscription(SMSAppModulePhrasingsSubscription, SMSAppModuleDALFactory, SMSAppModuleDALFactorySubscription, SubscriptionEngine, String)}
	 *  @param nstAnsweringDoubleOptinTriggers see {@link SMSAppModuleNavigationStatesSubscription#SMSAppModuleNavigationStatesSubscription(SMSAppModuleCommandsSubscription, Object[][])}
	 *  @returns {(SMSAppModuleNavigationStatesSubscription)navigationStates, (SMSAppModuleCommandsSubscription)commands, (SMSAppModulePhrasingsSubscription)phrasings} */
	public static Object[] getSubscriptionModuleInstances(Instrumentation<?, ?> log, String shortCode, String appName, String priceTag,
		                                                  String phrDoubleOptinStart,
		                                                  String phrDisagreeToSubscribe,
		                                                  String phrSuccessfullySubscribed,
		                                                  String phrCouldNotSubscribe,
		                                                  String phrUserRequestedUnsubscription,
		                                                  String phrLifecycleUnsubscription,
                                                          SMSAppModuleDALFactory             baseModuleDAL,
                                                          SMSAppModuleDALFactorySubscription subscriptionDAL,
                                                          SubscriptionEngine                 subscriptionEngine,
                                                          String                             subscriptionToken,
                                                          Object[][] nstAnsweringDoubleOptinTriggers) {
		
		SMSAppModulePhrasingsSubscription        phrasings        = new SMSAppModulePhrasingsSubscription(shortCode, appName, priceTag,
			phrDoubleOptinStart, phrDisagreeToSubscribe, phrSuccessfullySubscribed, phrCouldNotSubscribe, phrUserRequestedUnsubscription, phrLifecycleUnsubscription);
		SMSAppModuleCommandsSubscription         commands         = new SMSAppModuleCommandsSubscription(phrasings, baseModuleDAL, subscriptionDAL,
			subscriptionEngine, subscriptionToken);
		SMSAppModuleNavigationStatesSubscription navigationStates = new SMSAppModuleNavigationStatesSubscription(nstAnsweringDoubleOptinTriggers);
		
		// log
		String logPrefix = "Subscription Module";
		log.reportDebug(logPrefix + ": new instances:");
		Object[][] logPhrasings = {
			{"phrDoubleOptinStart",            phrDoubleOptinStart},
			{"phrDisagreeToSubscribe",         phrDisagreeToSubscribe},
			{"phrSuccessfullySubscribed",      phrSuccessfullySubscribed},
			{"phrCouldNotSubscribe",           phrCouldNotSubscribe},
			{"phrUserRequestedUnsubscription", phrUserRequestedUnsubscription},
			{"phrLifecycleUnsubscription",     phrLifecycleUnsubscription},
		};
		log.reportDebug(logPrefix + ": Phrasings        : " + Arrays.deepToString(logPhrasings));
		Object[][] logCommands = {
			{"baseModuleDAL",      baseModuleDAL},
	        {"subscriptionDAL",    subscriptionDAL},
	        {"subscriptionEngine", subscriptionEngine},
	        {"subscriptionToken",  subscriptionToken},
		};
		log.reportDebug(logPrefix + ": Commands         : " + Arrays.deepToString(logCommands));
		Object[][] logCommandTriggers = {
			{"nstAnsweringDoubleOptinTriggers", nstAnsweringDoubleOptinTriggers},	
		};
		log.reportDebug(logPrefix + ": Navigation States: " + Arrays.deepToString(logCommandTriggers));

		return new Object[] {navigationStates, commands, phrasings};
	}
}
