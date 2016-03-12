package mutua.smsappmodule;

import static instantvas.tests.InstantVASSMSAppModuleHelpTestsConfiguration.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp.NavigationStatesNamesHelp.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;

import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.navigationstates.NavigationState;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import org.junit.Test;

/** <pre>
 * HelpModuleBehavioralTests.java
 * ==============================
 * (created by luiz, Jul 13, 2015)
 *
 * Tests the normal-circumstance usage of the help module features
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HelpModuleBehavioralTests {

/*	@Test
	public void testConfigurationFile() throws IllegalArgumentException, IllegalAccessException {
		ConfigurationManager cm = new ConfigurationManager(log, SMSAppModuleConfigurationHelp.class);
		String serializedFields = cm.serializeConfigurableClasses();
		System.out.println(serializedFields);
		cm.deserializeConfigurableClasses(serializedFields);
	}
*/
	@Test
	public void testStatefulHelpMessages() throws SQLException {
		String observedHelpMessage;

		SessionModel session = new SessionModel((UserDto)null, null, null) {
			public NavigationState getNavigationStateFromStateName(String navigationStateName) {
				if (navigationStateName.equals(nstNewUser)) {
					return baseModuleNavigationStates.nstNewUser;
				} else if (navigationStateName.equals(nstExistingUser)) {
					return baseModuleNavigationStates.nstExistingUser;
				} else if (navigationStateName.equals(nstPresentingCompositeHelp)) {
					return helpModuleNavigationStates.nstPresentingCompositeHelp;
				} else {
					throw new NotImplementedException();
				}
			}
			
		};
		
		session.setNavigationState(nstNewUser);
		observedHelpMessage = helpModuleCommands.cmdShowStatefulHelp.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("Stateful help didn't show the correctly contextualized message", expectedNstNewUserStatefulHelpMessage, observedHelpMessage);

		session.setNavigationState(nstExistingUser);
		observedHelpMessage = helpModuleCommands.cmdShowStatefulHelp.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("Stateful help didn't show the correctly contextualized message", expectedNstExistingUserStatefulHelpMessage, observedHelpMessage);
		
		session.setNavigationState(nstPresentingCompositeHelp);
		observedHelpMessage = helpModuleCommands.cmdShowStatefulHelp.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertNull("If a contextualized help does not exist for a state and the command gets called, it should return null", observedHelpMessage);
	}
	
	@Test
	public void testCompositeHelpMessage() throws SQLException {
		String expectedCompositeHelpMessage1 = helpModulePhrasings.getCompositeHelpMessage(0);
		String expectedCompositeHelpMessage2 = "This is the extended help message... You can place as many as you want."; // compositeHelp[1]
		String observedCompositeHelpMessage;
		
		SessionModel session = new SessionModel((UserDto)null, null, null) {
			public NavigationState getNavigationStateFromStateName(String navigationStateName) {
				if (nstPresentingCompositeHelp.equals(navigationStateName)) {
					return helpModuleNavigationStates.nstPresentingCompositeHelp;
				}
				return null;
			}
		};
		
		observedCompositeHelpMessage = helpModuleCommands.cmdStartCompositeHelpDialog.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("First composite help message isn't correct", expectedCompositeHelpMessage1, observedCompositeHelpMessage);
		assertEquals("Navigation State wasn't correctly set", helpModuleNavigationStates.nstPresentingCompositeHelp, session.getNavigationState());
		
		observedCompositeHelpMessage = helpModuleCommands.cmdShowNextCompositeHelpMessage.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("Second composite help message isn't correct", expectedCompositeHelpMessage2, observedCompositeHelpMessage);

		observedCompositeHelpMessage = helpModuleCommands.cmdShowNextCompositeHelpMessage.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("If a composite help dialog gets past the end, it must cycle again from the start", expectedCompositeHelpMessage1, observedCompositeHelpMessage);
	}
	
} 