package mutua.smsappmodule.dal;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationHangmanTests.DEFAULT_HANGMAN_DAL;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationHangmanTests.DEFAULT_MODULE_DAL;

import java.sql.SQLException;

import mutua.smsappmodule.DatabaseAlgorithmAnalysis;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;
import mutua.smsappmodule.dto.MatchDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;

import org.junit.BeforeClass;
import org.junit.Test;

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

	private IUserDB         userDB         = DEFAULT_MODULE_DAL.getUserDB();
	private INextBotWordsDB nextBotWordsDB = DEFAULT_HANGMAN_DAL.getNextBotWordsDB();
	
	// algorithm settings
	private static int numberOfThreads = 4;

	// users table pre-fill
	private static int       totalNumberOfUsers = SMSAppModuleConfigurationTests.PERFORMANCE_TESTS_LOAD_FACTOR * ((DEFAULT_HANGMAN_DAL == SMSAppModuleDALFactoryHangman.RAM) ? 1000000 : 40000);	// please, be sure the division between this and 'numberOfThreads' is round
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
	public void testSeveralUsersAlgorithmAnalysis() throws SQLException, InterruptedException {

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
