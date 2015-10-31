package mutua.smsappmodule.config;

import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.*;
import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription;
import mutua.subscriptionengine.SubscriptionEngine;
import mutua.subscriptionengine.TestableSubscriptionAPI;

/** <pre>
 * SMSAppModuleConfigurationSubscription.java
 * ==========================================
 * (created by luiz, Jul 14, 2015)
 *
 * Defines the "Subscription" module configuration variables, implementing the Mutua SMSApp
 * Configuration design pattern, as described in 'SMSAppModuleConfiguration'
 *
 * @see SMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationSubscription {


	// log
	public static Instrumentation<?, ?> log;
	
	// subscription engine
	public static SubscriptionEngine subscriptionEngine = null;
	public static String             subscriptionToken  = null;
	

	/*************************************************
	** MutuaICConfiguration CONFIGURABLE PROPERTIES **
	*************************************************/
	
	// phrasing
	///////////
	
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription.getDoubleOptinStart")
	public static String SUBSCRIPTIONphrDoubleOptinStart            = phrDoubleOptinStart.toString();
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription.getDisagreeToSubscribe")
	public static String SUBSCRIPTIONphrDisagreeToSubscribe         = phrDisagreeToSubscribe.toString();
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription.getSuccessfullySubscribed")
	public static String SUBSCRIPTIONphrSuccessfullySubscribed      = phrSuccessfullySubscribed.toString();
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription.getCouldNotSubscribe")
	public static String SUBSCRIPTIONphrCouldNotSubscribe           = phrCouldNotSubscribe.toString();
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription.getUserRequestedUnsubscriptionNotification")
	public static String SUBSCRIPTIONphrUserRequestedUnsubscription = phrUserRequestedUnsubscription.toString();
	@ConfigurableElement(sameAsMethod="mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription.getLifecycleUnsubscriptionNotification")
	public static String SUBSCRIPTIONphrLifecycleUnsubscription     = phrLifecycleUnsubscription.toString();


	// command patterns
	///////////////////
	
	@ConfigurableElement("Local triggers (available only to 'new users' and 'unsubscribed users') to execute the 'start double opt-in' process")
	public static String[] SUBSCRIPTIONtrgLocalStartDoubleOptin   = trgLocalStartDoubleOptin;
	@ConfigurableElement("Local triggers (available only to the 'answering double opt-in' navigation state) to execute the 'subscribe' process")
	public static String[] SUBSCRIPTIONtrgLocalAcceptDoubleOptin  = trgLocalAcceptDoubleOptin;
	@ConfigurableElement("Local triggers (available only to the 'answering double opt-in' navigation state) to quit the 'subscription' process")
	public static String[] SUBSCRIPTIONtrgLocalRefuseDoubleOptin  = trgLocalRefuseDoubleOptin;
	@ConfigurableElement("Global triggers (available to all navigation states) to execute the 'unsubscribe' command")
	public static String[] SUBSCRIPTIONtrgGlobalUnsubscribe = trgGlobalUnsubscribe;
		
	
	/************
	** METHODS **
	************/
	
	/** Apply on-the-fly phrasing changes */
	public static void applyPhrasingConfiguration() {
		phrDoubleOptinStart            .setPhrases(SUBSCRIPTIONphrDoubleOptinStart);
		phrDisagreeToSubscribe         .setPhrases(SUBSCRIPTIONphrDisagreeToSubscribe);
		phrSuccessfullySubscribed      .setPhrases(SUBSCRIPTIONphrSuccessfullySubscribed);
		phrCouldNotSubscribe           .setPhrases(SUBSCRIPTIONphrCouldNotSubscribe);
		phrUserRequestedUnsubscription .setPhrases(SUBSCRIPTIONphrUserRequestedUnsubscription);
		phrLifecycleUnsubscription     .setPhrases(SUBSCRIPTIONphrLifecycleUnsubscription);
	}
	
	/** Apply on-the-fly command trigger changes */
	public static void applyTriggerConfiguration() {
		for (INavigationState navigationState : SMSAppModuleNavigationStatesSubscription.values()) {
			navigationState.setCommandTriggersFromConfigurationValues();
		}
	}
	
	/** Apply the following on-the-fly configuration changes: phrasing, triggers */
	public static void applyConfiguration() {
		if ((subscriptionEngine == null) || (subscriptionToken == null)) {
			throw new RuntimeException("'subscriptionEngine' & 'subscriptionToken' cannot be left null");
		}
		applyPhrasingConfiguration();
		applyTriggerConfiguration();
	}

	
	static {
		applyTriggerConfiguration();
	}
}
