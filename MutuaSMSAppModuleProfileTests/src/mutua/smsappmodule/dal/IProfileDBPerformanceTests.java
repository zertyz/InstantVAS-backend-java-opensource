package mutua.smsappmodule.dal;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationProfileTests.DEFAULT_MODULE_DAL;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationProfileTests.DEFAULT_PROFILE_DAL;

import java.sql.SQLException;

import mutua.smsappmodule.DatabaseAlgorithmAnalysis;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;

import org.junit.Before;
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
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class IProfileDBPerformanceTests {

	private IUserDB    userDB    = DEFAULT_MODULE_DAL.getUserDB();
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
	public void testPureNonCollisionalNicknamesAlgorithmAnalysis() throws SQLException, InterruptedException {

		int inserts = totalNumberOfUsers / 2;
		int updates = inserts / 5;
		int selects = inserts / 5;

		// prepare the tables & variables
		final ProfileDto[] profiles = new ProfileDto[inserts*2];
		for (int i=0; i<users.length; i++) {
			profiles[i]  = new ProfileDto(users[i], "nick for " + users[i].getPhoneNumber());
		}

		new DatabaseAlgorithmAnalysis("IProfileDB Non-Collisional Nicknames", numberOfThreads, inserts, updates, selects) {
			public void resetTables() throws SQLException {
				profileDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				profileDB.setProfileRecord(profiles[i]);
			}
			public void updateLoopCode(int i) throws SQLException {
				profileDB.setProfileRecord(profiles[i]);
			}
			public void selectLoopCode(int i) throws SQLException {
				profileDB.getProfileRecord(users[i]);
			}
		};

	}
	
	@Test
	public void testPureCollisionalNicknamesAlgorithmAnalysis() throws SQLException, InterruptedException {

		int inserts = 5000;
		int selects = inserts / 5;

		// prepare the tables & variables
		final ProfileDto[] profiles = new ProfileDto[inserts*2];
		for (int i=0; i<inserts*2; i++) {
			profiles[i]  = new ProfileDto(users[i], "Dom");
		}

		new DatabaseAlgorithmAnalysis("IProfileDB Pure-Collisional Nicknames", numberOfThreads, inserts, selects) {
			public void resetTables() throws SQLException {
				profileDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				profiles[i] = profileDB.setProfileRecord(profiles[i]);
			}
			public void selectLoopCode(int i) throws SQLException {
				profileDB.getProfileRecord(users[i]);
			}
		};
		
	}
	
	@Test
	public void testHibridCollisionalNicknamesAlgorithmAnalysis() throws SQLException, InterruptedException {

		int inserts = totalNumberOfUsers / 2;
		int updates = inserts / 5;
		int selects = inserts / 5;
		int collisionFactor = 3;

		// prepare the tables & variables
		final ProfileDto[] profiles = new ProfileDto[inserts*2];
		for (int i=0; i<inserts*2; i++) {
			profiles[i]  = new ProfileDto(users[i], "nick for " + (i/collisionFactor) + "th collision factor");
		}

		new DatabaseAlgorithmAnalysis("IProfileDB Hibrid-Collisional Nicknames", numberOfThreads, inserts, updates, selects) {
			public void resetTables() throws SQLException {
				profileDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				profileDB.setProfileRecord(profiles[i]);
			}
			public void updateLoopCode(int i) throws SQLException {
				profileDB.setProfileRecord(profiles[i]);
			}
			public void selectLoopCode(int i) throws SQLException {
				profileDB.getProfileRecord(users[i]);
			}
		};

	}

	@Test
	public void testReentrancy() {
		
	}
}
