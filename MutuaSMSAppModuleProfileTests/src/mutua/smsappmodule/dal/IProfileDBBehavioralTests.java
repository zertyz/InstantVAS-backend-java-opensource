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

import com.sun.corba.se.spi.activation._LocatorImplBase;

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
		
		// now test a huge number of listing profiles
		/////////////////////////////////////////////
		
		resetTables();
		
		// insert the MOs -- one by one this time, since the order now counts
		Object[][] hugeMOset = new Object[1000][4];
		for (int i=0; i<hugeMOset.length; i++) {
			hugeMOset[i][0] = SpecializedMOParameters.PHONE;
			hugeMOset[i][1] = Long.toString(21991234900L+i);
			hugeMOset[i][2] = SpecializedMOParameters.TEXT;
			hugeMOset[i][3] = "this is an MO from Lady or Gentlemen #"+i;
			config.moDB.invokeScalarProcedure(config.moDB.InsertNewQueueElement, hugeMOset[i]);
		}
		//config.moDB.invokeUpdateBatchProcedure(config.moDB.BatchInsertNewQueueElement, hugeMOset);
		
		// set the desired state for even and odd phones -- evens are in, odds are out
		for (int i=0; i<hugeMOset.length; i++) {
			String phone = (String)hugeMOset[i][1];
			UserDto user = userDB.assureUserIsRegistered(phone);
			profileDB.setProfileRecord(new ProfileDto(user, "LadyOrGentlemen#"+i));
			sessionDB.assureProperty(user, "withinnity", i % 2 == 0 ? "in" : "out");
		}
		
		// checks
		/////////
		
		// do we have the integer half of them? The even ones...
		profiles = profileDB.getRecentProfilesByLastMOTimeNotInSessionValues(hugeMOset.length, "withinnity", "out");
		assertEquals("There must be the integer half of all of them", hugeMOset.length/2, profiles.length);
		
		// warning: complicated loop ahead -- meaning the following loop must return the exact same 'profiles' array
		// if we were really building the 'profiles' array, we'd execute now: profiles = new ProfileDto[profiles.length];
		for (int i=0; i<profiles.length; i++) {
			ProfileDto[] limitedProfiles = profileDB.getRecentProfilesByLastMOTimeNotInSessionValues(i+1, "withinnity", "out");
			assertEquals("For this query, there is an exact number of returned rows that should be returned", i+1, limitedProfiles.length);
			// add to 'profiles'
			int notFoundJIndex = (limitedProfiles.length == 1) ? 0 : -1;
			for (int j=0; j<limitedProfiles.length; j++) {
				// search for the limited profile 'j' in all profiles up to the 'i'th element
				boolean found = false;
				for (int k=0; k<i; k++) {
					if (profiles[k].getUser().equals(limitedProfiles[j].getUser())) {
						found = true;
					}
				}
				if (!found) {
					notFoundJIndex = j;
				}
			}
			if (notFoundJIndex < 0) {
				fail("Every new 'limitedProfiles' query should return one (and only) new element to put in the 'profiles' 'i'th position. Failed for i="+i);
			}
			// "add the new (and unique) 'limitedProfiles' element" --
			// if we were really going to add, the code would be: profiles[i] = limitedProfiles[notFoundJIndex];
			// but we'll only check, since this complicated code's 'profiles' array should be the same as
			// the one returned by the one-liner before
			
			assertEquals("'profiles' array (the one returned by the one-liner and the complicated loop) didn't match, meaning the query sorting is not stable", profiles[i], limitedProfiles[notFoundJIndex]);
		}
		
		// check the 'profiles' array, on the correct order of elements (most recent first)
		for (int i=0; i<hugeMOset.length; i+=2) {
			String expectedPhone = (String)hugeMOset[i][1];
			String observedPhone = profiles[profiles.length-1-(i/2)].getUser().getPhoneNumber();
//			System.err.println("#"+(profiles.length-1-(i/2))+": phone='"+observedPhone+"'; nick='"+profiles[profiles.length-1-(i/2)].getNickname()+"'");
			assertEquals("Oops at element #"+(profiles.length-1-(i/2))+": wrong phones", expectedPhone, observedPhone);
		}
		
	}

}
