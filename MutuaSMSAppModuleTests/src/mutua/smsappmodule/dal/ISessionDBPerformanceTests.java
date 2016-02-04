package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.smsappmodule.DatabaseAlgorithmAnalysis;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import instantvas.tests.InstantVASSMSAppModuleTestsConfiguration;

import static instantvas.tests.InstantVASSMSAppModuleTestsConfiguration.*;
import static org.junit.Assert.*;

/** <pre>
 * ISessionDBPerformanceTests.java
 * ===============================
 * (created by luiz, Jul 28, 2015)
 *
 * Measures and tests the f(n) of the O(f(n)) algorithm complexity implementations
 * of {@link ISessionDB}
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ISessionDBPerformanceTests {

	private IUserDB    userDB    = DEFAULT_SMS_MODULE_DAL.getUserDB();
	private ISessionDB sessionDB = DEFAULT_SMS_MODULE_DAL.getSessionDB();

	// algorithm settings
	private static int numberOfThreads = 4;

	// users table pre-fill
	private static int       totalNumberOfUsers = InstantVASSMSAppModuleTestsConfiguration.PERFORMANCE_TESTS_LOAD_FACTOR * ((DEFAULT_SMS_MODULE_DAL == SMSAppModuleDALFactory.RAM) ? 600000 : 30000);	// please, be sure the division between this and 'numberOfThreads' is round
	private static long      phoneStart         = 991230000;
	private static UserDto[] users              = new UserDto[totalNumberOfUsers];

	/*******************
	** COMMON METHODS **
	*******************/
	
	@BeforeClass
	public static void fulfillUsersTable() {
		try {
			SMSAppModuleTestCommons.resetBaseTables();
			SMSAppModuleTestCommons.insertUsers(phoneStart, users, numberOfThreads);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Could not fulfill users table", t);
		}
	}
	
	@AfterClass
	public static void clearRAM() {
		users = null;
	}

	
	/**********
	** TESTS **
	**********/

	@Test
	public void testMultipleUsersSessionAlgorithmAnalysis() throws Throwable {
		int inserts = totalNumberOfUsers / 2;
		int updates = inserts;
		int selects = inserts;
		
		String[][] newProperties = {
			{"BobSong",     "Don't rock my boat"},
			{"BeatlesSong", "Yes it is"},
		};
		String[][] updateProperties = {
			{"BobSong",     "Easy Skanking"},
		};
		
		// prepare tables & variables
		final SessionDto[] insertSessions = new SessionDto[inserts*2];
		final SessionDto[] updateSessions = new SessionDto[inserts*2];
		for (int i=0; i<inserts*2; i++) {
			insertSessions[i] = new SessionDto(users[i], newProperties, null,                   null);
			updateSessions[i] = new SessionDto(users[i],                null, updateProperties, null);
		}
		
		new DatabaseAlgorithmAnalysis("ISessionDB Multiple Users", numberOfThreads, inserts, updates, selects) {
			public void resetTables() throws SQLException {
				sessionDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				sessionDB.setSession(insertSessions[i]);
			}
			public void updateLoopCode(int i) throws SQLException {
				sessionDB.setSession(updateSessions[i]);
			}
			public void selectLoopCode(int i) throws SQLException {
				sessionDB.getSession(users[i]);
			}
		};
		
	}

	@Test
	public void testMultiplePropertiesSessionAlgorithmAnalysis() throws Throwable {
		int totalNumberOfSessions = InstantVASSMSAppModuleTestsConfiguration.PERFORMANCE_TESTS_LOAD_FACTOR * ((DEFAULT_SMS_MODULE_DAL == SMSAppModuleDALFactory.RAM) ? 600000 : 30000);	// please, be sure the division between this and 'numberOfThreads' is round
		int inserts = totalNumberOfSessions / 2;
		int updates = inserts;
				
		new DatabaseAlgorithmAnalysis("ISessionDB Multiple Properties", numberOfThreads, inserts, updates, -1) {
			public void resetTables() throws SQLException {
				sessionDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				String si = Integer.toString(i);
				sessionDB.setSession(new SessionDto(users[0], new String[][] {
					{"Song"+si, "This is song #"+si+": another bob song"},
				}, null, null));
			}
			public void updateLoopCode(int i) throws SQLException {
				String si = Integer.toString(i);
				sessionDB.setSession(new SessionDto(users[0], null, new String[][] {
					{"Song"+si, "This is still song #"+si+": but now, a beatles song"},
				}, null));
			}
		};
		
//		SessionDto retrievedSession = sessionDB.getSession(users[0]);
//		String[][] storedProperties = retrievedSession.getStoredProperties();
//		assertEquals("Performance test didn't insert the elements correctly", inserts*2, storedProperties.length);
//		for (int i=0; i<storedProperties.length; i++) {
//			assertEquals("Performance test didn't insert the elements correctly", "Song"+Integer.toString(i),                                             storedProperties[i][0]);
//			assertEquals("Performance test didn't update the elements correctly", "This is still song #"+Integer.toString(i)+": but now, a beatles song", storedProperties[i][1]);
//		}
		
	}
}
