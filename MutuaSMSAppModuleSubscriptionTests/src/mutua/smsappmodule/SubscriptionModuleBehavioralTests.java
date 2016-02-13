package mutua.smsappmodule;

import static instantvas.tests.InstantVASSMSAppModuleSubscriptionTestsConfiguration.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription.NavigationStatesNamesSubscription.*;
import static org.junit.Assert.assertEquals;
import java.sql.SQLException;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.sessions.SessionModel;

import org.junit.Test;

/** <pre>
 * SubscriptionModuleBehavioralTests.java
 * ======================================
 * (created by luiz, Jul 22, 2015)
 *
 * Tests the normal-circumstance usage of the subscription module features
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SubscriptionModuleBehavioralTests {

	@Test
	public void testDoubleOptin() throws SQLException {
		String expectedMessage = subscriptionModulePhrasings.getDoubleOptinStart();
		SessionModel session = new SessionModel((UserDto)null, null, null) {
			@Override
			public INavigationState getNavigationStateFromStateName(String navigationStateName) {
				if (navigationStateName.equals(nstAnsweringDoubleOptin)) {
					return subscriptionModuleNavigationStates.nstAnsweringDoubleOptin;
				}
				return null;
			}
		};
		
		// first (and normal) interaction
		String observedMessage = subscriptionModuleCommands.cmdStartDoubleOptinProcess.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("Command didn't answer the correct message for starting the double opt-in process", expectedMessage, observedMessage);
		assertEquals("Navigation State wasn't correctly set", subscriptionModuleNavigationStates.nstAnsweringDoubleOptin, session.getNavigationState());
	}


}
