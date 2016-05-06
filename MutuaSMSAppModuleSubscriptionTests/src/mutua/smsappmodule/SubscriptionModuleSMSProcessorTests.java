package mutua.smsappmodule;

import static instantvas.tests.InstantVASSMSAppModuleSubscriptionTestsConfiguration.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;

import java.sql.SQLException;

import org.junit.Test;

import instantvas.tests.InstantVASSMSAppModuleSubscriptionTestsConfiguration;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.NavigationState;

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

	
	// configuration
	InstantVASSMSAppModuleSubscriptionTestsConfiguration config = InstantVASSMSAppModuleSubscriptionTestsConfiguration.getInstance();
	

	// variables
	////////////
	
	private SMSAppModuleTestCommons tc = new SMSAppModuleTestCommons(
		BASE_MODULE_DAL,
		new NavigationState[][]   {config.baseModuleNavigationStates.values, config.subscriptionModuleNavigationStates.values},
		new ICommandProcessor[][] {config.subscriptionModuleCommands.values});
	
	
	// tests
	////////
	
	@Test
	public void testFullpathSubscriptionAndUnsubscription() throws SQLException {
		
		tc.resetBaseTables();
		
		// test double opt-in subscription refusal
		tc.checkResponse("991234899", "MyApp", config.subscriptionModulePhrasings.getDoubleOptinStart());
		tc.checkResponse("991234899", "no", config.subscriptionModulePhrasings.getDisagreeToSubscribe());
		tc.checkNavigationState("991234899", nstNewUser);

		// test double opt-in acceptance
		tc.checkResponse("991234899", "MyApp", config.subscriptionModulePhrasings.getDoubleOptinStart());
		tc.checkResponse("991234899", "yes", config.subscriptionModulePhrasings.getSuccessfullySubscribed());
		tc.checkNavigationState("991234899", nstExistingUser);
		
		// test unsubscription
		tc.checkResponse("991234899", "unsubscribe", config.subscriptionModulePhrasings.getUserRequestedUnsubscriptionNotification());
		tc.checkNavigationState("991234899", nstNewUser);
		
		tc.resetBaseTables();
		
		// test answering neither yes nor no to the double opt-in acceptance and, then, staring it over
		tc.checkResponse("991234899", "MyApp", config.subscriptionModulePhrasings.getDoubleOptinStart());
		tc.checkResponse("991234899", "dods",  config.subscriptionModulePhrasings.getDisagreeToSubscribe());
		tc.checkResponse("991234899", "MyApp", config.subscriptionModulePhrasings.getDoubleOptinStart());
		tc.checkResponse("991234899", "yes", config.subscriptionModulePhrasings.getSuccessfullySubscribed());
		tc.checkNavigationState("991234899", nstExistingUser);

	}

}
