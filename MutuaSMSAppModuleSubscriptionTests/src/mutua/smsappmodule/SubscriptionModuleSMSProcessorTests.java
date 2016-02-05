package mutua.smsappmodule;

import static instantvas.tests.InstantVASSMSAppModuleSubscriptionTestsConfiguration.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;

import java.sql.SQLException;

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
	
	private SMSAppModuleTestCommons tc = new SMSAppModuleTestCommons(LOG,
		BASE_MODULE_DAL,
		baseModuleNavigationStates.values, subscriptionModuleNavigationStates.values);
	
	
	// tests
	////////
	
	@Test
	public void testFullpathSubscriptionAndUnsubscription() throws SQLException {
		
		tc.resetBaseTables();
		
		// test double opt-in subscription refusal
		tc.checkResponse("991234899", "SubscriptionTest", subscriptionModulePhrasings.getDoubleOptinStart());
		tc.checkResponse("991234899", "no", subscriptionModulePhrasings.getDisagreeToSubscribe());
		tc.checkNavigationState("991234899", nstNewUser);

		// test double opt-in acceptance
		tc.checkResponse("991234899", "SubscriptionTest", subscriptionModulePhrasings.getDoubleOptinStart());
		tc.checkResponse("991234899", "yes", subscriptionModulePhrasings.getSuccessfullySubscribed());
		tc.checkNavigationState("991234899", nstExistingUser);
		
		// test unsubscription
		tc.checkResponse("991234899", "unsubscribe", subscriptionModulePhrasings.getUserRequestedUnsubscriptionNotification());
		tc.checkNavigationState("991234899", nstNewUser);
		
		tc.resetBaseTables();
		
		// test answering neither yes nor no to the double opt-in acceptance and, then, staring it over
		tc.checkResponse("991234899", "SubscriptionTest", subscriptionModulePhrasings.getDoubleOptinStart());
		tc.checkResponse("991234899", "dods", subscriptionModulePhrasings.getDisagreeToSubscribe());
		tc.checkResponse("991234899", "SubscriptionTest", subscriptionModulePhrasings.getDoubleOptinStart());
		tc.checkResponse("991234899", "yes", subscriptionModulePhrasings.getSuccessfullySubscribed());
		tc.checkNavigationState("991234899", nstExistingUser);

	}

}
