package mutua.smsappmodule;

import static mutua.smsappmodule.HelpModuleTestCommons.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationHelpTests.*;
import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.*;

import java.sql.SQLException;

import mutua.hangmansmsgame.smslogic.SMSProcessor;
import mutua.hangmansmsgame.smslogic.SMSProcessorException;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

import org.junit.Test;

/** <pre>
 * HelpModuleSMSProcessorTests.java
 * ================================
 * (created by luiz, Jul 23, 2015)
 *
 * Tests the integration of the "Help" SMS App Module with the SMS Processor logic
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HelpModuleSMSProcessorTests {
	
	
	// variables
	////////////
	
	private SMSAppModuleTestCommons tc = new SMSAppModuleTestCommons(log,
		SMSAppModuleNavigationStates.values(),
		SMSAppModuleNavigationStatesHelp.values());

	
	// tests
	////////
	
	@Test
	public void testStatelessHelps() throws SQLException {
		tc.resetTables();
		SMSAppModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
			{cmdShowStatelessHelp, new String[] {"help"}},
		});
		tc.checkResponse("+5521991234899", "help", getStatelessHelpMessage());
	}
	
	@Test
	public void testFallbackHelps() throws SQLException {
		tc.resetTables();
		SMSAppModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
			{cmdShowStatelessHelp,        new String[] {"help"}},
			{cmdStartCompositeHelpDialog, new String[] {"rules"}},
			{cmdShowNewUsersFallbackHelp, new String[] {".*"}},
		});
		tc.checkResponse("+5521991234899", "unknown keyword", getNewUsersFallbackHelp());
		tc.checkResponse("+5521991234899", "rules",           getCompositeHelpMessage(0));
		tc.checkResponse("+5521991234899", "unknown keyword", getExistingUsersFallbackHelp());
	}
	
}