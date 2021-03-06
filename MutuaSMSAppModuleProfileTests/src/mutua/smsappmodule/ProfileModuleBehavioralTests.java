package mutua.smsappmodule;

import static instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.NavigationStatesNamesProfile.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Arrays;

import mutua.events.SpecializedMOQueueDataBureau.SpecializedMOParameters;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.navigationstates.NavigationState;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto;

import org.junit.Before;
import org.junit.Test;

import instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration;

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
	
	// configuration
	InstantVASSMSAppModuleProfileTestsConfiguration config = InstantVASSMSAppModuleProfileTestsConfiguration.getInstance();

	private IUserDB    userDB    = BASE_MODULE_DAL.getUserDB();
	private ISessionDB sessionDB = BASE_MODULE_DAL.getSessionDB();
	private IProfileDB profileDB = PROFILE_MODULE_DAL.getProfileDB();
	
	/** Test replacement of SMSProcessor's {@link SessionModel} */
	private class TestSessionModel extends SessionModel {
		public TestSessionModel(UserDto user) {
			super(user, null);
		}
		@Override
		public NavigationState getNavigationStateFromStateName(String navigationStateName) {
			return null;
		}
	}
	
	
	@Before
	public void resetTables() throws SQLException {
		profileDB.reset();
		SMSAppModuleTestCommons.resetBaseTables(BASE_MODULE_DAL);
	}


	@Test
	public void testStartAskForNicknameDialog() throws SQLException {
		UserDto user = userDB.assureUserIsRegistered("21991234899");
		String expectedMessageForNotNicknamedUser = config.profileModulePhrasings.getAskForFirstNickname();
		String nickname = "IAmSet";
		String expectedMessageForNicknamedUser = config.profileModulePhrasings.getAskForNewNickname(nickname);
		
		// no nickname yet
		SessionModel sessionForNotNicknamedUser   = new TestSessionModel(user);
		String observedMessageForNotNicknamedUser = config.profileModuleCommands.cmdStartAskForNicknameDialog.processCommand(sessionForNotNicknamedUser, null, null).getResponseMessages()[0].getText();
		assertEquals("Command didn't answer the correct message for starting the ask for the first nickname process", expectedMessageForNotNicknamedUser, observedMessageForNotNicknamedUser);
		assertSame("Navigation State wasn't correctly set", nstRegisteringNickname, sessionForNotNicknamedUser.getNavigationStateName());

		// attempting to change a nickname
		SessionModel sessionForNicknamedUser   = new TestSessionModel(user);
		profileDB.setProfileRecord(new ProfileDto(user, nickname));
		String observedMessageForNicknamedUser = config.profileModuleCommands.cmdStartAskForNicknameDialog.processCommand(sessionForNicknamedUser, null, null).getResponseMessages()[0].getText();
		assertEquals("Command didn't answer the correct message for starting the ask for a nickname change process", expectedMessageForNicknamedUser, observedMessageForNicknamedUser);
		assertEquals("Navigation State wasn't correctly set", nstRegisteringNickname, sessionForNicknamedUser.getNavigationStateName());

	}
	
	@Test
	public void testRegisterNickname() throws SQLException {
		String firstExpectedNickname  = "IMeMine";
		String secondExpectedNickname = "IMeMine1";
		String firstExpectedMessage  = config.profileModulePhrasings.getNicknameRegistrationNotification(firstExpectedNickname);
		String secondExpectedMessage = config.profileModulePhrasings.getNicknameRegistrationNotification(secondExpectedNickname);
		
		// first nickname
		SessionModel firstSession = new TestSessionModel(userDB.assureUserIsRegistered("21991234899"));
		String firstObservedMessage = config.profileModuleCommands.cmdRegisterNickname.processCommand(firstSession, null, new String[] {firstExpectedNickname}).getResponseMessages()[0].getText();
		assertEquals("Command didn't answer the correct message for setting a nickname", firstExpectedMessage, firstObservedMessage);
		
		// second nickname
		SessionModel secondSession = new TestSessionModel(userDB.assureUserIsRegistered("21998019167"));
		String secondObservedMessage = config.profileModuleCommands.cmdRegisterNickname.processCommand(secondSession, null, new String[] {firstExpectedNickname}).getResponseMessages()[0].getText();
		assertEquals("Command didn't answer the correct message for setting a nickname", secondExpectedMessage, secondObservedMessage);
		
		// test getting out of 'nstRegisteringNickname' state
		SessionModel getOutOfRegisteringNicknameSession = new TestSessionModel(userDB.assureUserIsRegistered("2197559595"));
		config.profileModuleCommands.cmdStartAskForNicknameDialog.processCommand(getOutOfRegisteringNicknameSession, null, null);	// start the register nickname dialog
		config.profileModuleCommands.cmdRegisterNickname.processCommand(getOutOfRegisteringNicknameSession, null, new String[] {"AnyNick"});
		assertEquals("Navigation State wasn't correctly reset after being through 'nstRegisteringNickname'", nstExistingUser, getOutOfRegisteringNicknameSession.getNavigationStateName());
	}
	
	@Test
	public void testInquireUserProfile() throws SQLException {
		UserDto      aUser           = userDB.assureUserIsRegistered("21991234899");
		SessionModel aSession        = new TestSessionModel(aUser);
		String       aUserNickname   = "aUser";
		String       expectedMessage = config.profileModulePhrasings.getUserProfilePresentation(aUserNickname, "21991234899");
		
		profileDB.setProfileRecord(new ProfileDto(aUser, aUserNickname));
		String parameterizedObservedMessage   = config.profileModuleCommands.cmdShowUserProfile.processCommand(aSession, null, new String[] {aUserNickname}).getResponseMessages()[0].getText();
		String unparameterizedObservedMessage = config.profileModuleCommands.cmdShowUserProfile.processCommand(aSession, null, new String[] {}).getResponseMessages()[0].getText();
		
		assertEquals("Wrong message for parameterized user profile inquiry",   expectedMessage, parameterizedObservedMessage);
		assertEquals("Wrong message for unparameterized user profile inquiry", expectedMessage, unparameterizedObservedMessage);
		
	}
	
	@Test
	public void testInquireInexistentProfile() throws SQLException {
		UserDto aUser                = userDB.assureUserIsRegistered("21991234899");
		SessionModel aSession        = new TestSessionModel(aUser);
		String  aUserNickname        = "aUser";
		String  expectedMessage      = config.profileModulePhrasings.getNicknameNotFound(aUserNickname);
		
		String observedMessage   = config.profileModuleCommands.cmdShowUserProfile.processCommand(aSession, null, new String[] {aUserNickname}).getResponseMessages()[0].getText();
		
		assertEquals("Non existing nickname was not correctly reported when referenced in the PROFILE command", expectedMessage, observedMessage);

	}
	
	@Test
	public void testListProfilesInfo() throws SQLException {
		
		// TODO correct this test when we are not using POSTGRESQL DAL -- we should have a common MO for all DALs and, today, we only have it for POSTGRESQL. Tip: see we should use a queue event link (PostgreSQLQueueEventLink) istead of the low level, postgresql only adapter for the queue
		
		// fill in listable profiles (simulates the MO, session & nickname setting)
		///////////////////////////////////////////////////////////////////////////

		// simulate MO inclusion (BUGs when we're not using the POSTGRESQL DAL)
		config.moDB.invokeUpdateBatchProcedure(config.moDB.BatchInsertNewQueueElement, new Object[][] {
			{SpecializedMOParameters.PHONE, "21998919167", SpecializedMOParameters.TEXT, "this is an MO from Paty!"},
			{SpecializedMOParameters.PHONE, "21991234899", SpecializedMOParameters.TEXT, "this is the MO from Dom!"},
		});
		
		UserDto user = userDB.assureUserIsRegistered("21998919167");
		ProfileDto profile = new ProfileDto(user, "Paty");
		profileDB.setProfileRecord(profile);
		sessionDB.assureProperty(user, SessionModel.NAVIGATION_STATE_PROPERTY.getPropertyName(), nstExistingUser);
		user = userDB.assureUserIsRegistered("21991234899");
		profile = new ProfileDto(user, "Dom");
		profileDB.setProfileRecord(profile);
		sessionDB.assureProperty(user, SessionModel.NAVIGATION_STATE_PROPERTY.getPropertyName(), nstExistingUser);

		// check not listing anyone because of a too low 'maxChars'
		try {
			config.profileModuleCommands.getListProfilesInfoWithLimitedPhraseLength(
				config.profileModulePhrasings.getProfileList(new ProfileDto[] {profile}).length()-1, new int[0]);
			fail("An exception should have been raised");
		} catch (RuntimeException e) {}
		
		// check listing just one of the profiles (the limit is via the MT message length
		Object[] listProfilesInfo = config.profileModuleCommands.getListProfilesInfoWithLimitedPhraseLength(
			config.profileModulePhrasings.getProfileList(new ProfileDto[] {profile}).length(), new int[0]);
		ProfileDto[] profiles         = (ProfileDto[]) listProfilesInfo[0];
		String       MTPhrase         = (String)       listProfilesInfo[1];
		int[]        presentedUserIds = (int[])        listProfilesInfo[2];
		assertEquals("Only one profile should be returned at this point", 1, profiles.length);
		assertEquals("Wrong profile listed as the first one", "Dom", profiles[0].getNickname());
		
		// test listing the next profile
		listProfilesInfo = config.profileModuleCommands.getListProfilesInfoWithLimitedPhraseLength(800, presentedUserIds);
		profiles         = (ProfileDto[]) listProfilesInfo[0];
		MTPhrase         = (String)       listProfilesInfo[1];
		presentedUserIds = (int[])        listProfilesInfo[2];
		assertEquals("Only one profile should be returned at this point", 1, profiles.length);
		assertEquals("Wrong profile listed as the first one", "Paty", profiles[0].getNickname());
		assertEquals("Wrong number of 'presentedUserIds'", 2, presentedUserIds.length);
		
		// test listing the two profiles
		listProfilesInfo = config.profileModuleCommands.getListProfilesInfoWithLimitedPhraseLength(800, new int[0]);
		profiles         = (ProfileDto[]) listProfilesInfo[0];
		MTPhrase         = (String)       listProfilesInfo[1];
		presentedUserIds = (int[])        listProfilesInfo[2];
		assertEquals("Two profiles should be returned at this point", 2, profiles.length);
		assertEquals("Wrong profile listed as the first one",  "Dom",  profiles[0].getNickname());
		assertEquals("Wrong profile listed as the second one", "Paty", profiles[1].getNickname());
		assertEquals("Wrong number of 'presentedUserIds'", 2, presentedUserIds.length);
		
		// test listing a next (and inexistent) profile
		listProfilesInfo = config.profileModuleCommands.getListProfilesInfoWithLimitedPhraseLength(800, presentedUserIds);
		assertNull("When there is no more profiles to list, null should be returned", listProfilesInfo);
	}
}