package mutua.smsappmodule;

import static instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.NavigationStatesNamesProfile.*;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.NavigationState;

import org.junit.Before;
import org.junit.Test;

import instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration;

/** <pre>
 * ProfileModuleSMSProcessorTests.java
 * ===================================
 * (created by luiz, Oct 31, 2015)
 *
 * Tests the integration of the "Profile" SMS App Module (and the modules it depends on) with the SMS Processor logic
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ProfileModuleSMSProcessorTests {
	
	// configuration
	InstantVASSMSAppModuleProfileTestsConfiguration config = InstantVASSMSAppModuleProfileTestsConfiguration.getInstance();

	// databases
	////////////
	
	private IUserDB    userDB    = BASE_MODULE_DAL.getUserDB();
	private ISessionDB sessionDB = BASE_MODULE_DAL.getSessionDB();
	private IProfileDB profileDB = PROFILE_MODULE_DAL.getProfileDB();
	
	private SMSAppModuleTestCommons tc = new SMSAppModuleTestCommons(BASE_MODULE_DAL,
		new NavigationState[][]   {config.baseModuleNavigationStates.values, config.profileModuleNavigationStates.values},
		new ICommandProcessor[][] {config.profileModuleCommands.values});

	
	@Before
	public void resetTables() throws SQLException {
		profileDB.reset();
		sessionDB.reset();
		userDB.reset();
	}

	
	// tests
	////////
	
	@Test
	public void testNicknamesAndProfiles() throws SQLException {
		
		// change nickname dialog tests
		tc.checkResponse("991234899", "nick",     config.profileModulePhrasings.getAskForFirstNickname());
		tc.checkNavigationState("991234899", nstRegisteringNickname);
		tc.checkResponse("991234899", "aspargus", config.profileModulePhrasings.getNicknameRegistrationNotification("aspargus"));
		tc.checkNavigationState("991234899", nstExistingUser);
		tc.checkResponse("991234899", "nick",     config.profileModulePhrasings.getAskForNewNickname("aspargus"));
		tc.checkResponse("991234899", "tomatoes", config.profileModulePhrasings.getNicknameRegistrationNotification("tomatoes"));
		tc.checkResponse("991234899", "nick",     config.profileModulePhrasings.getAskForNewNickname("tomatoes"));
		tc.checkNavigationState("991234899", nstRegisteringNickname);
		tc.checkResponse("991234899", "nick",     config.profileModulePhrasings.getAskForNewNickname("tomatoes"));
		tc.checkResponse("991234899", "cancel",   config.profileModulePhrasings.getAskForNicknameCancelation("tomatoes"));
		tc.checkNavigationState("991234899", nstExistingUser);
		// for other commands (other than cancel), the nickname changing should also skip nickname changing, for instance:
		// tc.checkResponse("+5521991234899", "nick",     getAskForNewNickname("tomatoes"));
		// tc.checkResponse("+5521991234899", "help",     SMSAppModulePhrasingsHelp.getDefaultHelpMessage...);
		
		// stateless nickname change
		tc.checkResponse("991234899", "nick donadom", config.profileModulePhrasings.getNicknameRegistrationNotification("donadom"));
		
		// profile inquiry
		tc.checkResponse("991234899", "profile donadom", config.profileModulePhrasings.getUserProfilePresentation("donadom", "21991234899"));
		tc.checkResponse("991234899", "profile",         config.profileModulePhrasings.getUserProfilePresentation("donadom", "21991234899"));
	}
	
}
