package mutua.smsappmodule.dal;

import static instantvas.tests.InstantVASSMSAppModuleHangmanTestsConfiguration.*;

import java.sql.SQLException;

import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.dto.UserDto;
import mutua.tests.DatabaseAlgorithmAnalysis;

import org.junit.Test;

import instantvas.tests.InstantVASSMSAppModuleHangmanTestsConfiguration;

/** <pre>
 * IMatchDBPerformanceTests.java
 * =============================
 * (created by luiz, Aug 13, 2015)
 *
 * Measures and tests the f(n) of the O(f(n)) algorithm complexity implementations
 * of {@link INextBotWordsDB}
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class INextBotWordsDBPerformanceTests {

	// configuration
	InstantVASSMSAppModuleHangmanTestsConfiguration config = InstantVASSMSAppModuleHangmanTestsConfiguration.getInstance();

	private IUserDB         userDB         = BASE_MODULE_DAL.getUserDB();
	private INextBotWordsDB nextBotWordsDB = HANGMAN_MODULE_DAL.getNextBotWordsDB();
	
	// algorithm settings
	private int numberOfThreads = 4;

	// users table pre-fill
	private int       totalNumberOfUsers = PERFORMANCE_TESTS_LOAD_FACTOR * ((HANGMAN_MODULE_DAL == SMSAppModuleDALFactoryHangman.RAM) ? 1000000 : 40000);	// please, be sure the division between this and 'numberOfThreads' is round
	private long      phoneStart         = 991230000;
	private UserDto[] users              = new UserDto[totalNumberOfUsers];


	/*******************
	** COMMON METHODS **
	*******************/
	
	//@BeforeClass, fulfillUsersTable
	public INextBotWordsDBPerformanceTests() {
		try {
			SMSAppModuleTestCommons.resetBaseTables(BASE_MODULE_DAL);
			SMSAppModuleTestCommons.insertUsers(userDB, phoneStart, users, numberOfThreads);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Could not fulfill users table", t);
		}
	}
	
	
	/**********
	** TESTS **
	**********/
	
	@Test
	public void testSeveralUsersAlgorithmAnalysis() throws Throwable {

		int inserts = totalNumberOfUsers / 2;
		int updates = inserts;

		new DatabaseAlgorithmAnalysis("INextBotWordsDB Several Users", numberOfThreads, inserts, updates, -1) {
			public void resetTables() throws SQLException {
				nextBotWordsDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				nextBotWordsDB.getAndIncNextBotWord(users[i]);
			}
			public void updateLoopCode(int i) throws SQLException {
				nextBotWordsDB.getAndIncNextBotWord(users[i]);
			}
		};

	}
	
}
