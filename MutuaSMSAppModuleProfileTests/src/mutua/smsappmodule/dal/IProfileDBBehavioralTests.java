package mutua.smsappmodule.dal;

import static instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration.*;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Arrays;

import mutua.events.SpecializedMOQueueDataBureau;
import mutua.events.SpecializedMOQueueDataBureau.SpecializedMOParameters;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;

import org.junit.Before;
import org.junit.Test;

import instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration;

/** <pre>
 * IProfileDBBehavioralTests.java
 * ==============================
 * (created by luiz, Aug 4, 2015)
 *
 * Tests the normal-circumstance usage of {@link IProfileDB} implementations
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class IProfileDBBehavioralTests {
	
	/******************
	** CONFIGURATION **
	******************/
	
	private InstantVASSMSAppModuleProfileTestsConfiguration config = InstantVASSMSAppModuleProfileTestsConfiguration.getInstance();
	
	private IUserDB    userDB    = config.BASE_MODULE_DAL.getUserDB();
	private ISessionDB sessionDB = config.BASE_MODULE_DAL.getSessionDB();
	private IProfileDB profileDB = config.PROFILE_MODULE_DAL.getProfileDB();	

	/*******************
	** COMMON METHODS **
	*******************/
	
	@Before
	public void resetTables() throws SQLException {
		config.moDB.resetQueues();
		profileDB.reset();
		SMSAppModuleTestCommons.resetBaseTables(BASE_MODULE_DAL);
	}
	

	/**********
	** TESTS **
	**********/
	
	@Test
	public void testNonExistingProfileRecord() throws SQLException {
		UserDto    user    = userDB.assureUserIsRegistered("21991234899");
		ProfileDto profile = profileDB.getProfileRecord(user);
		assertNull("Non-existing profiles must be null", profile);
	}

	@Test
	public void testSimpleUsage() throws SQLException {
		UserDto    dom              = userDB.assureUserIsRegistered("21991234899");
		UserDto    paty             = userDB.assureUserIsRegistered("21998019167");
		ProfileDto originalProfile  = null;
		ProfileDto storedProfile    = null;
		ProfileDto retrievedProfile = null;
		
		// test new profile
		originalProfile  = new ProfileDto(dom, "anickname");
		storedProfile    = profileDB.setProfileRecord(originalProfile);
		retrievedProfile = profileDB.getProfileRecord(dom);
		assertEquals("Failed to store a new user profile -- Original and retrieved 'user' doesn't match",      originalProfile.getUser().getPhoneNumber(),     retrievedProfile.getUser().getPhoneNumber());
		assertEquals("Failed to store a new user profile -- Original and retrieved 'nickname' doesn't match",  originalProfile.getNickname(),                  retrievedProfile.getNickname());
		assertSame  ("Failed to store a new user profile -- Original and stored objects are not the same",     originalProfile, storedProfile);
		assertEquals("Failed to retrieve profile by nickname",                                                 retrievedProfile, profileDB.getProfileRecord("anickname"));
		
		// test existing profile
		originalProfile  = new ProfileDto(dom, "dOm");
		storedProfile    = profileDB.setProfileRecord(originalProfile);
		retrievedProfile = profileDB.getProfileRecord(dom);
		assertEquals("Failed to store an existing user profile -- Original and retrieved 'user' doesn't match",      originalProfile.getUser().getPhoneNumber(),     retrievedProfile.getUser().getPhoneNumber());
		assertEquals("Failed to store an existing user profile -- Original and retrieved 'nickname' doesn't match",  originalProfile.getNickname(),                  retrievedProfile.getNickname());
		assertSame  ("Failed to store an existing user profile -- Original and stored objects are not the same",     originalProfile, storedProfile);
		assertEquals("Failed to retrieve profile by nickname",                                                       retrievedProfile, profileDB.getProfileRecord("dom"));
		
		// test nickname collision
		originalProfile  = new ProfileDto(paty, "Dom");
		storedProfile    = profileDB.setProfileRecord(originalProfile);
		retrievedProfile = profileDB.getProfileRecord(paty);
		assertEquals   ("Failed to treat nickname collision -- Stored and retrieved 'user' doesn't match",         storedProfile.getUser().getPhoneNumber(),     retrievedProfile.getUser().getPhoneNumber());
		assertEquals   ("Failed to treat nickname collision -- Returned and retrieved 'nickname' doesn't match",   storedProfile.getNickname(),                  retrievedProfile.getNickname());
		assertNotEquals("Failed to treat nickname collision -- Original and stored 'nickname' cannot be the same", originalProfile.getNickname(),                storedProfile.getNickname());
		
		// test nickname case change
		originalProfile  = new ProfileDto(dom, "dOm");
		storedProfile    = profileDB.setProfileRecord(originalProfile);
		retrievedProfile = profileDB.getProfileRecord(dom);
		assertEquals("Failed allow the same user to change his own nickname to the same case insensitive value -- Original and retrieved 'nickname's differ",    originalProfile.getNickname(), retrievedProfile.getNickname());
		assertSame  ("Failed allow the same user to change his own nickname to the same case insensitive value -- Original and stored objects are not the same", originalProfile, storedProfile);
		assertEquals("Failed to retrieve profile by nickname",                                                     retrievedProfile, profileDB.getProfileRecord("DoM"));
		
	}
	
	@Test
	public void testNicknameCollision() throws SQLException {
		String[] baseNicknames = {"Dom", "dOm", "doM", "DOm", "DoM", "dOM", "DOM", "dom"};
		UserDto[] users = new UserDto[100];
		for (int i=0; i<users.length; i++) {
			users[i] = userDB.assureUserIsRegistered("21999"+((users.length*10)+i));
			String baseNickname = baseNicknames[i%baseNicknames.length];
			profileDB.setProfileRecord(new ProfileDto(users[i], baseNickname));
			ProfileDto retrievedProfile = profileDB.getProfileRecord(users[i]);
			String expectedNickname = baseNickname + (i == 0 ? "" : Integer.toString(i));
			assertEquals("Nicknames collision preventing sequence algorithm is not working right", expectedNickname, retrievedProfile.getNickname());
			baseNickname = baseNicknames[(i+1)%baseNicknames.length];
			profileDB.setProfileRecord(new ProfileDto(users[i], baseNickname));
			retrievedProfile = profileDB.getProfileRecord(users[i]);
			expectedNickname = baseNickname + (i == 0 ? "" : Integer.toString(i));
			assertEquals("Nicknames collision prevention sequence algorithm is not allowing the user to try to get a better numbering / change case of his/her nickname", expectedNickname, retrievedProfile.getNickname());
			profileDB.setProfileRecord(retrievedProfile);
			retrievedProfile = profileDB.getProfileRecord(users[i]);
			assertEquals("Nicknames collision prevention sequence algorithm is not allowing the user to change to exactly the same nickname (sequence numbers included)", expectedNickname, retrievedProfile.getNickname());
			assertEquals("Failed to retrieve profile by nickname", retrievedProfile, profileDB.getProfileRecord(expectedNickname));

		}
		
		// test the rock-solidity of the sequence algorithm
		UserDto    craftedSequenceBreakingNickUser    = userDB.assureUserIsRegistered("21999998");
		ProfileDto craftedSequenceBreakingNickProfile = profileDB.setProfileRecord(new ProfileDto(craftedSequenceBreakingNickUser, "DomA"));
		UserDto    oneMoreToTheSequenceUser    = userDB.assureUserIsRegistered("21999999");
		ProfileDto oneMoreToTheSequenceProfile = profileDB.setProfileRecord(new ProfileDto(oneMoreToTheSequenceUser, "Dom"));
		assertEquals("Nickname sequence algorithm could be break by a specially crafted nickname", "Dom100", oneMoreToTheSequenceProfile.getNickname());
	}
	
	@Test
	public void testListingSheets() throws SQLException {
		ProfileDto[] profiles;

		profiles = profileDB.getRecentProfilesByLastMOTimeNotInSessionValues(10, "withinnity", "out");
		assertEquals("There must be nothing to be listed on an empty database", 0, profiles.length);

		// simulate MO inclusion (BUGs when we're usimg the RAM DAL)
		config.moDB.invokeUpdateBatchProcedure(config.moDB.BatchInsertNewQueueElement, new Object[][] {
			{SpecializedMOParameters.PHONE, "21998919167", SpecializedMOParameters.TEXT, "this is an MO from Paty!"},
			{SpecializedMOParameters.PHONE, "21991234899", SpecializedMOParameters.TEXT, "this is the MO from Dom!"},
		});
		
		UserDto outUser = userDB.assureUserIsRegistered("21998919167");
		profileDB.setProfileRecord(new ProfileDto(outUser, "Paty"));
		sessionDB.assureProperty(outUser, "withinnity", "out");
		profiles = profileDB.getRecentProfilesByLastMOTimeNotInSessionValues(10, "withinnity", "out");
		assertEquals("I have an user, true; but she is not THE ONE", 0, profiles.length);

		UserDto inUser = userDB.assureUserIsRegistered("21991234899");
		profileDB.setProfileRecord(new ProfileDto(inUser, "Dom"));
		
		profiles = profileDB.getRecentProfilesByLastMOTimeNotInSessionValues(10, "withinnity", "out");
		assertEquals("Still nothing... I didn't set the session yet!", 0, profiles.length);

		sessionDB.assureProperty(inUser, "withinnity", "in");

		profiles = profileDB.getRecentProfilesByLastMOTimeNotInSessionValues(10, "withinnity", "out");
		assertEquals("There must be only one!", 1, profiles.length);
		assertEquals("... and it must be The Dom", "Dom", profiles[0].getNickname());
	}

}
