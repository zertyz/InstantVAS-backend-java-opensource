package mutua.smsappmodule.dal;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationProfileTests.*;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Random;

import mutua.smsappmodule.DatabaseAlgorithmAnalysis;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;

import org.junit.BeforeClass;
import org.junit.Test;

/** <pre>
 * IProfileDBPerformanceTests.java
 * ===============================
 * (created by luiz, Aug 4, 2015)
 *
 * Measures and tests the f(n) of the O(f(n)) algorithm complexity implementations
 * of {@link IProfileDB}
 *
 * @see IProfileDB
 * @version $Id$
 * @author luiz
 */

public class IProfileDBPerformanceTests {

	private IProfileDB profileDB = DEFAULT_PROFILE_DAL.getProfileDB();
	
	// algorithm settings
	private static int numberOfThreads = 4;

	// users table pre-fill
	private static int       totalNumberOfUsers = SMSAppModuleConfigurationTests.PERFORMANCE_TESTS_LOAD_FACTOR * ((DEFAULT_PROFILE_DAL == SMSAppModuleDALFactoryProfile.RAM) ? 1000000 : 40000);	// please, be sure the division between this and 'numberOfThreads' is round
	private static long      phoneStart         = 991230000;
	private static UserDto[] users              = new UserDto[totalNumberOfUsers];


	/*******************
	** COMMON METHODS **
	*******************/
	
	@BeforeClass
	public static void fulfillUsersTable() {
		try {
			SMSAppModuleTestCommons.resetTables();
			SMSAppModuleTestCommons.insertUsers(phoneStart, users, numberOfThreads);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Could not fulfill users table", t);
		}
	}

	
	/**********
	** TESTS **
	**********/
	
	@Test
	public void testPureNonCollisionalNicknamesAlgorithmAnalysis() throws Throwable {

		int inserts = totalNumberOfUsers / 2;
		int updates = inserts;
		int selects = inserts;

		// prepare the tables & variables
		final ProfileDto[] profiles = new ProfileDto[inserts*2];	// populated on the inserts

		new DatabaseAlgorithmAnalysis("IProfileDB Non-Collisional Nicknames", numberOfThreads, inserts, updates, selects) {
			public void resetTables() throws SQLException {
				profileDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				profiles[i] = new ProfileDto(users[i], "nick for " + users[i].getPhoneNumber());
				ProfileDto storedProfile = profileDB.setProfileRecord(profiles[i]);
				assertSame("Profiles are not the same", profiles[i], storedProfile);
			}
			public void updateLoopCode(int i) throws SQLException {
				profileDB.setProfileRecord(profiles[i]);
			}
			public void selectLoopCode(int i) throws SQLException {
				ProfileDto retrievedProfile = profileDB.getProfileRecord(users[i]);
				if (!profiles[i].equals(retrievedProfile)) {
					assertEquals("Profile #"+i+" doesn't match", profiles[i], retrievedProfile);
				}
			}
		};

	}
	
	@Test
	public void testPureCollisionalNicknamesAlgorithmAnalysis() throws Throwable {

		int inserts = 5000;
		int selects = inserts;

		// prepare the tables & variables
		final ProfileDto[]               profiles = new ProfileDto[inserts*2];	// populated at the inserts
		final Hashtable<String, Integer> nicknames = new Hashtable<String, Integer>();

		new DatabaseAlgorithmAnalysis("IProfileDB Pure-Collisional Nicknames", numberOfThreads, inserts, selects) {
			public void resetTables() throws SQLException {
				profileDB.reset();
				nicknames.clear();	// needed because of the warm up
			}
			public void insertLoopCode(int i) throws SQLException {
				profiles[i] = profileDB.setProfileRecord(new ProfileDto(users[i], "Dom"));
				String nickname = profiles[i].getNickname();
				if (nicknames.containsKey(nickname)) {
					fail("Nickname '"+nickname+"', previously set at #"+nicknames.get(nickname)+" is already present, when trying to (re)insert it at #"+i);
				} else {
					nicknames.put(nickname, i);
				}

				//assertEquals("Error handling nickname collision", baseNickname+(i == 0 ? "" : i), profiles[i].getNickname());
			}
			public void selectLoopCode(int i) throws SQLException {
				ProfileDto retrievedProfile = profileDB.getProfileRecord(users[i]);
				if (!profiles[i].equals(retrievedProfile)) {
					assertEquals("Profile #"+i+" doesn't match", profiles[i], retrievedProfile);
				}
				String nickname = retrievedProfile.getNickname();
				if (!nicknames.containsKey(nickname)) {
					fail("Nickname '"+nickname+"' wasn't set");
				} else {
					nicknames.remove(nickname);
				}
			}
		};
		
		assertEquals("Wrong count of 'nicknames' after symetrical put/remove operations", 0, nicknames.size());
		
	}
	
	@Test
	public void testHibridCollisionalNicknamesAlgorithmAnalysis() throws Throwable {

		int inserts = totalNumberOfUsers / 2;
		int updates = inserts;
		int selects = inserts;
		final int collisionFactor = 4;	// must be a divisor for 'inserts'

		// prepare the tables & variables
		final ProfileDto[] profiles = new ProfileDto[inserts*2];	// populated at the inserts

		new DatabaseAlgorithmAnalysis("IProfileDB Hibrid-Collisional Nicknames", numberOfThreads, inserts, updates, selects) {
			public void resetTables() throws SQLException {
				profileDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				int cf = i/collisionFactor;
				int sequence = i % collisionFactor;
				String baseNickname = "nick for " + cf + "th collision factor";
				profiles[i] = profileDB.setProfileRecord(new ProfileDto(users[i], baseNickname));
				assertEquals("Error handling nickname collision", baseNickname+(sequence == 0 ? "" : sequence), profiles[i].getNickname());
			}
			public void updateLoopCode(int i) throws SQLException {
				profileDB.setProfileRecord(profiles[i]);
			}
			public void selectLoopCode(int i) throws SQLException {
				ProfileDto retrievedProfile = profileDB.getProfileRecord(users[i]);
				if (!profiles[i].equals(retrievedProfile)) {
					assertEquals("Profile #"+i+" doesn't match", profiles[i], retrievedProfile);
				}
			}
		};

	}

	@Test
	public void testRetrieveProfilesByNickname() throws Throwable {
		int inserts = totalNumberOfUsers / 2;
		int updates = inserts;
		int selects = inserts;

		// prepare the tables & variables
		char[] nicknameChars = "Ab0Cd1Ef2Gh3Ij4Kl5Mn6Op7Qr8St9Uv_XwYz".toCharArray();
		final String[]     nicknames = new String[inserts*2];
		final ProfileDto[] profiles  = new ProfileDto[inserts*2];
		Random rnd = new Random();
		for (int i=0; i<users.length; i++) {
			StringBuffer nickname = new StringBuffer(10);
			for (int j=0; j<8; j++) {
				nickname.append(nicknameChars[rnd.nextInt(nicknameChars.length)]);
			}
			nicknames[i] = nickname.toString();
			nickname.delete(0, nickname.length());
			profiles[i]  = new ProfileDto(users[i], nicknames[i]);
		}

		new DatabaseAlgorithmAnalysis("IProfileDB Retrieve by nicknames", numberOfThreads, inserts, selects) {
			public void resetTables() throws SQLException {
				profileDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				ProfileDto storedProfile = profileDB.setProfileRecord(profiles[i]);
				assertSame("Profiles are not the same", profiles[i], storedProfile);
			}
			public void selectLoopCode(int i) throws SQLException {
				ProfileDto profile       = profileDB.getProfileRecord(nicknames[i]);
				String retrievedNickname = profile.getNickname();
				if (!nicknames[i].equals(retrievedNickname)) {
					assertEquals("Nickname #"+i+" doesn't match", nicknames[i], retrievedNickname);
				}
			}
		};
	}
}
