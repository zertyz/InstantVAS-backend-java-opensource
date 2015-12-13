package mutua.smsappmodule.smslogic.navigationstates;

import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

import org.junit.Test;

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

	@Test
	public void testSetCommandTriggers() {
		NavigationStateCommons nsc = new NavigationStateCommons(SMSAppModuleNavigationStates.nstNewUser);
		nsc.setCommandTriggers(new Object[][] {
			{new TestCommandProcessor(), new String[] {"regex1", "regex2"}, 1001l}});
		assertEquals("serialization didn't work",
		             "command='TestCommandProcessor', patterns=[regex1, regex2], timeout=1001",
		             nsc.serializeCommandTrigger(null)[0]);
	}
	
	@Test
	public void testSetNavigationStateCommandTriggers() {
		SMSAppModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
			{new TestCommandProcessor(), new String[] {"regex1", "regex2"}, 1001l}});
		assertEquals("serialization of regex & timeout didn't work",
		             "command='TestCommandProcessor', patterns=[regex1, regex2], timeout=1001",
		             SMSAppModuleNavigationStates.nstNewUser.serializeCommandTrigger(null)[0]);

		SMSAppModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
				{new TestCommandProcessor(), new String[] {"regex1", "regex2"}}});
		assertEquals("serialization of regex & without timeout didn't work",
		             "command='TestCommandProcessor', patterns=[regex1, regex2], timeout=-1",
		             SMSAppModuleNavigationStates.nstNewUser.serializeCommandTrigger(null)[0]);

		SMSAppModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
				{new TestCommandProcessor(), 101l}});
		assertEquals("serialization of timeout & without regex didn't work",
		             "command='TestCommandProcessor', patterns=null, timeout=101",
		             SMSAppModuleNavigationStates.nstNewUser.serializeCommandTrigger(null)[0]);
	}

}

class TestCommandProcessor implements ICommandProcessor {

	@Override
	public String getCommandName() {
		return "TestCommandProcessor";
	}

	@Override
	public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
		return null;
	}
	
}