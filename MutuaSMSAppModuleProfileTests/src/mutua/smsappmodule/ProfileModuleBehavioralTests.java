package mutua.smsappmodule;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationProfileTests.*;
import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.*;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import mutua.icc.configuration.ConfigurationManager;
import mutua.smsappmodule.config.SMSAppModuleConfigurationProfile;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.sessions.SessionModel;

import org.junit.Before;
import org.junit.Test;

/** <pre>
 * ProfileModuleBehavioralTests.java
 * =================================
 * (created by luiz, Oct 31, 2015)
 *
 * Tests the normal-circumstance usage of the "Profile" module features
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ProfileModuleBehavioralTests {
	
	
	private static IUserDB    userDB    = DEFAULT_MODULE_DAL.getUserDB();
	private static IProfileDB profileDB = DEFAULT_PROFILE_DAL.getProfileDB();
	
	
	@Before
	public void resetTables() throws SQLException {
		SMSAppModuleTestCommons.resetTables();
		profileDB.reset();
	}


	@Test
	public void testConfigurationFile() throws IllegalArgumentException, IllegalAccessException {
		ConfigurationManager cm = new ConfigurationManager(log, SMSAppModuleConfigurationProfile.class);
		String serializedFields = cm.serializeConfigurableClasses();
		System.out.println(serializedFields);
		cm.deserializeConfigurableClasses(serializedFields);
	}
	
	@Test
	public void testStartAskForNicknameDialog() throws SQLException {
		UserDto user = userDB.assureUserIsRegistered("21991234899");
		String expectedMessageForNotNicknamedUser = getAskForFirstNickname();
		String nickname = "IAmSet";
		String expectedMessageForNicknamedUser = getAskForNewNickname(nickname);
		
		// no nickname yet
		SessionModel sessionForNotNicknamedUser   = new SessionModel(user, null, null);
		String observedMessageForNotNicknamedUser = cmdStartAskForNicknameDialog.processCommand(sessionForNotNicknamedUser, null, null).getResponseMessages()[0].getText();
		assertEquals("Command didn't answer the correct message for starting the ask for the first nickname process", expectedMessageForNotNicknamedUser, observedMessageForNotNicknamedUser);
		assertEquals("Navigation State wasn't correctly set", nstRegisteringNickname, sessionForNotNicknamedUser.getNavigationState());

		// attempting to change a nickname
		SessionModel sessionForNicknamedUser   = new SessionModel(user, null, null);
		profileDB.setProfileRecord(new ProfileDto(user, nickname));
		String observedMessageForNicknamedUser = cmdStartAskForNicknameDialog.processCommand(sessionForNicknamedUser, null, null).getResponseMessages()[0].getText();
		assertEquals("Command didn't answer the correct message for starting the ask for a nickname change process", expectedMessageForNicknamedUser, observedMessageForNicknamedUser);
		assertEquals("Navigation State wasn't correctly set", nstRegisteringNickname, sessionForNicknamedUser.getNavigationState());

	}
	
	@Test
	public void testRegisterNickname() throws SQLException {
		String firstExpectedNickname  = "IMeMine";
		String secondExpectedNickname = "IMeMine1";
		String firstExpectedMessage  = getNicknameRegistrationNotification(firstExpectedNickname);
		String secondExpectedMessage = getNicknameRegistrationNotification(secondExpectedNickname);
		
		// first nickname
		SessionModel firstSession = new SessionModel(userDB.assureUserIsRegistered("21991234899"), null, null);
		String firstObservedMessage = cmdRegisterNickname.processCommand(firstSession, null, new String[] {firstExpectedNickname}).getResponseMessages()[0].getText();
		assertEquals("Command didn't answer the correct message for setting a nickname", firstExpectedMessage, firstObservedMessage);
		
		// second nickname
		SessionModel secondSession = new SessionModel(userDB.assureUserIsRegistered("21998019167"), null, null);
		String secondObservedMessage = cmdRegisterNickname.processCommand(secondSession, null, new String[] {firstExpectedNickname}).getResponseMessages()[0].getText();
		assertEquals("Command didn't answer the correct message for setting a nickname", secondExpectedMessage, secondObservedMessage);
		
	}
	
	@Test
	public void testInquireUserProfile() throws SQLException {
		UserDto aUser          = userDB.assureUserIsRegistered("21991234899");
		String  aUserNickname  = "aUser";
		String expectedMessage = getUserProfilePresentation(aUserNickname);
		
		profileDB.setProfileRecord(new ProfileDto(aUser, aUserNickname));
		String parameterizedObservedMessage   = cmdShowUserProfile.processCommand(new SessionModel(aUser, null, null), null, new String[] {aUserNickname}).getResponseMessages()[0].getText();
		String unparameterizedObservedMessage = cmdShowUserProfile.processCommand(new SessionModel(aUser, null, null), null, new String[] {}).getResponseMessages()[0].getText();
		
		assertEquals("Wrong message for parameterized user profile inquiry",   expectedMessage, parameterizedObservedMessage);
		assertEquals("Wrong message for unparameterized user profile inquiry", expectedMessage, unparameterizedObservedMessage);
		
	}
}