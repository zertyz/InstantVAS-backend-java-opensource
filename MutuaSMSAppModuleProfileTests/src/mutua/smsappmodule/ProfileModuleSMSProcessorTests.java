package mutua.smsappmodule;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationProfileTests.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationProfile.*;
import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.*;
import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile;

import org.junit.Before;
import org.junit.Test;

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

	// variables
	////////////
	
	private static IUserDB    userDB    = DEFAULT_MODULE_DAL.getUserDB();
	private static IProfileDB profileDB = DEFAULT_PROFILE_DAL.getProfileDB();
	
	private SMSAppModuleTestCommons tc = new SMSAppModuleTestCommons(log, SMSAppModuleNavigationStates.values());

	
	// setup
	////////
	
	static {
		// set triggers for the "new" and "existing" user navigation states
		SMSAppModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
			{cmdStartAskForNicknameDialog, PROFILEtrgGlobalStartAskForNicknameDialog},
		});
		SMSAppModuleNavigationStates.nstExistingUser.setCommandTriggers(new Object[][] {
			{cmdRegisterNickname,                PROFILEtrgGlobalRegisterNickname},
			{cmdStartAskForNicknameDialog,       PROFILEtrgGlobalStartAskForNicknameDialog},
			{cmdShowUserProfile,                 PROFILEtrgGlobalShowUserProfile},
		});
		SMSAppModuleNavigationStatesProfile.nstRegisteringNickname.setCommandTriggers(new Object[][] {
			{cmdRegisterNickname,                PROFILEtrgGlobalRegisterNickname},
			{cmdStartAskForNicknameDialog,       PROFILEtrgGlobalStartAskForNicknameDialog},
			{cmdAskForNicknameDialogCancelation, PROFILEtrgLocalNicknameDialogCancelation},
			{cmdShowUserProfile,                 PROFILEtrgGlobalShowUserProfile},
			{cmdRegisterNickname,                PROFILEtrgLocalRegisterNickname},
		});
	}
	
	@Before
	public void resetTables() throws SQLException {
		SMSAppModuleTestCommons.resetTables();
		profileDB.reset();
	}

	
	// tests
	////////
	
	@Test
	public void testNicknamesAndProfiles() throws SQLException {
		
		// change nickname dialog tests
		tc.checkResponse("991234899", "nick",     getAskForFirstNickname());
		tc.checkNavigationState("991234899",      nstRegisteringNickname);
		tc.checkResponse("991234899", "aspargus", getNicknameRegistrationNotification("aspargus"));
		tc.checkNavigationState("991234899",      nstExistingUser);
		tc.checkResponse("991234899", "nick",     getAskForNewNickname("aspargus"));
		tc.checkResponse("991234899", "tomatoes", getNicknameRegistrationNotification("tomatoes"));
		tc.checkResponse("991234899", "nick",     getAskForNewNickname("tomatoes"));
		tc.checkNavigationState("991234899",      nstRegisteringNickname);
		tc.checkResponse("991234899", "nick",     getAskForNewNickname("tomatoes"));
		tc.checkResponse("991234899", "cancel",   getAskForNicknameCancelation("tomatoes"));
		tc.checkNavigationState("991234899",      nstExistingUser);
		// for other commands (other than cancel), the nickname changing should also skip nickname changing, for instance:
		// tc.checkResponse("+5521991234899", "nick",     getAskForNewNickname("tomatoes"));
		// tc.checkResponse("+5521991234899", "help",     SMSAppModulePhrasingsHelp.getDefaultHelpMessage...);
		
		// stateless nickname change
		tc.checkResponse("991234899", "nick donadom", getNicknameRegistrationNotification("donadom"));
		
		// profile inquiry
		tc.checkResponse("991234899", "profile donadom", getUserProfilePresentation("donadom"));
		tc.checkResponse("991234899", "profile",         getUserProfilePresentation("donadom"));
	}
	
}
