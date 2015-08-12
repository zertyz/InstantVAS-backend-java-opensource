package mutua.smsappmodule.dal;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationTests.DEFAULT_MODULE_DAL;

import java.sql.SQLException;

import mutua.smsappmodule.DatabaseAlgorithmAnalysis;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;

import org.junit.BeforeClass;
import org.junit.Test;

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

	private IUserDB    userDB    = DEFAULT_MODULE_DAL.getUserDB();
	private ISessionDB sessionDB = DEFAULT_MODULE_DAL.getSessionDB();

	// algorithm settings
	private static int numberOfThreads = 4;

	// users table pre-fill
	private static int       totalNumberOfUsers = SMSAppModuleConfigurationTests.PERFORMANCE_TESTS_LOAD_FACTOR * ((DEFAULT_MODULE_DAL == SMSAppModuleDALFactory.RAM) ? 600000 : 30000);	// please, be sure the division between this and 'numberOfThreads' is round
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
	public void testAlgorithmAnalysis() throws InterruptedException, SQLException {
		int inserts = totalNumberOfUsers / 2;
		int updates = inserts / 5;
		int selects = inserts / 5;
		
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
		
		new DatabaseAlgorithmAnalysis("ISessionDB", numberOfThreads, inserts, updates, selects) {
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
}
