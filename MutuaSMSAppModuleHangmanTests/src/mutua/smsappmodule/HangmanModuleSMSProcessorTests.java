package mutua.smsappmodule;

import java.sql.SQLException;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationHangmanTests.*;

import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;

import org.junit.Test;

/** <pre>
 * HangmanModuleSMSProcessorTests.java
 * ===================================
 * (created by luiz, Sep 15, 2015)
 *
 * Tests the integration of the "Hangman" SMS App Module (and the modules it depends on) with the SMS Processor logic
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HangmanModuleSMSProcessorTests {
/*
	// variables
	////////////
	
	private SMSAppModuleTestCommons tc = new SMSAppModuleTestCommons(log,
		SMSAppModuleNavigationStates.values(),
		SMSAppModuleNavigationStatesHangman.values());

	
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
*/}
