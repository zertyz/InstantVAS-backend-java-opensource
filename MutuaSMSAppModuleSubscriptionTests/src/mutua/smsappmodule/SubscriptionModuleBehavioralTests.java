package mutua.smsappmodule;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationTests.log;
import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription.getDoubleOptinStart;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsSubscription.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription.*;
import static org.junit.Assert.assertEquals;
import java.sql.SQLException;
import mutua.icc.configuration.ConfigurationManager;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;
import mutua.smsappmodule.dto.UserDto;
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
	public void testConfigurationFile() throws IllegalArgumentException, IllegalAccessException {
		ConfigurationManager cm = new ConfigurationManager(log, SMSAppModuleConfigurationTests.class);
		String serializedFields = cm.serializeConfigurableClasses();
		System.out.println(serializedFields);
		cm.deserializeConfigurableClasses(serializedFields);
	}
	
	@Test
	public void testDoubleOptin() throws SQLException {
		String expectedMessage = getDoubleOptinStart();
		SessionModel session = new SessionModel((UserDto)null, null, null);
		
		// first (and normal) interaction
		String observedMessage = cmdStartDoubleOptinProcess.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("Command didn't answer the correct message for starting the double opt-in process", expectedMessage, observedMessage);
		assertEquals("Navigation State wasn't correctly set", nstAnsweringDoubleOptin, session.getNavigationState());
	}


}
