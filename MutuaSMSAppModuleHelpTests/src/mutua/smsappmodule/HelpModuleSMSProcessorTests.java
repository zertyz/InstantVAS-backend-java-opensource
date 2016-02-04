package mutua.smsappmodule;

import static instantvas.tests.InstantVASSMSAppModuleHelpTestsConfiguration.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.CommandNamesHelp.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHelp.CommandTriggersHelp.*;

import java.sql.SQLException;

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
		baseModuleNavigationStates.values,
		helpModuleNavigationStates.values);

	
	// tests
	////////
	
	@Test
	public void testStatelessHelps() throws SQLException {
		tc.resetBaseTables();
		baseModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
			{cmdShowStatelessHelp, trgGlobalShowStatelessHelpMessage},
		}, helpModuleCommands.values);
		tc.checkResponse("+5521991234899", "help", helpModulePhrasings.getStatelessHelpMessage());
	}
	
	@Test
	public void testFallbackHelps() throws SQLException {
		tc.resetBaseTables();
		baseModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
			{cmdShowStatelessHelp,        trgGlobalShowStatelessHelpMessage},
			{cmdStartCompositeHelpDialog, trgGlobalStartCompositeHelpDialog},
			{cmdShowNewUsersFallbackHelp, ".*"},
		}, helpModuleCommands.values);
		tc.checkResponse("+5521991234899", "unknown keyword", helpModulePhrasings.getNewUsersFallbackHelp());
		tc.checkResponse("+5521991234899", "rules",           helpModulePhrasings.getCompositeHelpMessage(0));
		tc.checkResponse("+5521991234899", "unknown keyword", helpModulePhrasings.getExistingUsersFallbackHelp());
	}
	
}