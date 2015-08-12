package mutua.smsappmodule;

import static org.junit.Assert.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationSubscriptionTests.log;
import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.*;

import java.sql.SQLException;

import mutua.smsappmodule.config.SMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription;
import mutua.subscriptionengine.TestableSubscriptionAPI;

import org.junit.Test;

/** <pre>
 * SubscriptionModuleSMSProcessorTests.java
 * ========================================
 * (created by luiz, Jul 24, 2015)
 *
 * Tests the integration of the "Subscription" SMS App Module with the SMS Processor logic
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SubscriptionModuleSMSProcessorTests {


	// variables
	////////////
	
	private SMSAppModuleTestCommons tc = new SMSAppModuleTestCommons(log,
		SMSAppModuleNavigationStates.values(),
		SMSAppModuleNavigationStatesSubscription.values());
	
	
	// tests
	////////
	
	@Test
	public void testFullpathSubscriptionAndUnsubscription() throws SQLException {
		
		tc.resetTables();
		
		// test double opt-in subscription refusal
		tc.checkResponse("991234899", "SubscriptionTest", getDoubleOptinStart());
		tc.checkResponse("991234899", "no", getDisagreeToSubscribe());
		tc.checkNavigationState("991234899", nstNewUser);

		// test double opt-in acceptance
		tc.checkResponse("991234899", "SubscriptionTest", getDoubleOptinStart());
		tc.checkResponse("991234899", "yes", getSuccessfullySubscribed());
		tc.checkNavigationState("991234899", nstExistingUser);
		
		// test unsubscription
		tc.checkResponse("991234899", "unsubscribe", getUserRequestedUnsubscriptionNotification());
		tc.checkNavigationState("991234899", nstNewUser);
	}

}
