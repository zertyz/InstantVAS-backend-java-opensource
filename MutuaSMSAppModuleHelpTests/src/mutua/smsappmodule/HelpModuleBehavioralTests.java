package mutua.smsappmodule;

import static mutua.smsappmodule.HelpModuleTestCommons.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationHelpTests.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;

import mutua.icc.configuration.ConfigurationManager;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.config.SMSAppModuleConfigurationHelp;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.sessions.SessionModel;

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

	@Test
	public void testConfigurationFile() throws IllegalArgumentException, IllegalAccessException {
		ConfigurationManager cm = new ConfigurationManager(log, SMSAppModuleConfigurationHelp.class);
		String serializedFields = cm.serializeConfigurableClasses();
		System.out.println(serializedFields);
		cm.deserializeConfigurableClasses(serializedFields);
	}

	@Test
	public void testStatefulHelpMessages() throws SQLException {
		String observedHelpMessage;

		HelpModuleTestCommons.setStatefulHelpMessages();
		SessionModel session = new SessionModel((UserDto)null);
		
		session.setNavigationState(ENavStates.STATE1);
		observedHelpMessage = cmdShowStatefulHelp.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("Stateful help didn't show the correctly contextualized message", expectedSTATE1HelpMessage, observedHelpMessage);

		session.setNavigationState(ENavStates.STATE2);
		observedHelpMessage = cmdShowStatefulHelp.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("Stateful help didn't show the correctly contextualized message", expectedSTATE2HelpMessage, observedHelpMessage);
		
		session.setNavigationState(ENavStates.STATE3);
		observedHelpMessage = cmdShowStatefulHelp.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertNull("If a contextualized help does not exist for a state and the command gets called, it should return null", observedHelpMessage);
	}
	
	@Test
	public void testCompositeHelpMessage() throws SQLException {
		String expectedCompositeHelpMessage1 = "composite help 1";
		String expectedCompositeHelpMessage2 = "composite help 2";
		String observedCompositeHelpMessage;
		
		SMSAppModuleConfigurationHelp.HELPphrComposite = new String[] {
			expectedCompositeHelpMessage1,
			expectedCompositeHelpMessage2,
		};
		SMSAppModuleConfigurationHelp.applyPhrasingConfiguration();
		SessionModel session = new SessionModel((UserDto)null);
		
		observedCompositeHelpMessage = cmdStartCompositeHelpDialog.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("First composite help message isn't correct", expectedCompositeHelpMessage1, observedCompositeHelpMessage);
		assertEquals("Navigation State wasn't correctly set", nstPresentingCompositeHelp, session.getNavigationState());
		
		observedCompositeHelpMessage = cmdShowNextCompositeHelpMessage.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("Second composite help message isn't correct", expectedCompositeHelpMessage2, observedCompositeHelpMessage);

		observedCompositeHelpMessage = cmdShowNextCompositeHelpMessage.processCommand(session, null, null).getResponseMessages()[0].getText();
		assertEquals("If a composite help dialog gets past the end, it must cycle again from the start", expectedCompositeHelpMessage1, observedCompositeHelpMessage);
	}
	
}