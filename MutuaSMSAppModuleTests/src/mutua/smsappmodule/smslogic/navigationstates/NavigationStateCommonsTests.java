package mutua.smsappmodule.smslogic.navigationstates;

import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

import org.junit.Test;

import instantvas.tests.InstantVASSMSAppModuleTestsConfiguration;

/** <pre>
 * NavigationStateCommonsTests.java
 * ================================
 * (created by luiz, Jul 23, 2015)
 *
 * Test the {@link #NavigationStateCommons} class
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class NavigationStateCommonsTests {
	
	// configuration
	InstantVASSMSAppModuleTestsConfiguration config = InstantVASSMSAppModuleTestsConfiguration.getInstance();
	
	final static String availableCommandName      = "TestCommandProcessor";
	private ICommandProcessor[] availableCommands = {new TestCommandProcessor()};

	@Test
	public void testSetCommandTriggers() {
		NavigationStateCommons nsc = new NavigationStateCommons("my state");
		nsc.setCommandTriggers(new Object[][] {
			{availableCommandName, new String[] {"regex1", "regex2"}, 1001l}},
			availableCommands);
		assertEquals("serialization didn't work",
		             "command='TestCommandProcessor', patterns=[regex1, regex2], timeout=1001",
		             nsc.serializeCommandTrigger(null)[0]);
	}
	
	@Test
	public void testSetNavigationStateCommandTriggers() {
		config.baseModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
			{availableCommandName, new String[] {"regex1", "regex2"}, 1001l}},
			availableCommands);
		assertEquals("serialization of regex & timeout didn't work",
		             "command='TestCommandProcessor', patterns=[regex1, regex2], timeout=1001",
		             config.baseModuleNavigationStates.nstNewUser.serializeCommandTrigger(null)[0]);

		config.baseModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
			{availableCommandName, new String[] {"regex1", "regex2"}}},
			availableCommands);
		assertEquals("serialization of regex & without timeout didn't work",
		             "command='TestCommandProcessor', patterns=[regex1, regex2], timeout=-1",
		             config.baseModuleNavigationStates.nstNewUser.serializeCommandTrigger(null)[0]);

		config.baseModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
				{availableCommandName, 101l}}, availableCommands);
		assertEquals("serialization of timeout & without regex didn't work",
		             "command='TestCommandProcessor', patterns=null, timeout=101",
		             config.baseModuleNavigationStates.nstNewUser.serializeCommandTrigger(null)[0]);
	}

}

class TestCommandProcessor extends ICommandProcessor {

	public TestCommandProcessor() {
		super(NavigationStateCommonsTests.availableCommandName);
	}

	@Override
	public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
		return null;
	}
	
}