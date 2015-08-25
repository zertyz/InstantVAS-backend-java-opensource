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
 * of {@link IMatchDB}
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class IMatchDBPerformanceTests {

	private IUserDB  userDB  = DEFAULT_MODULE_DAL.getUserDB();
	private IMatchDB matchDB = DEFAULT_HANGMAN_DAL.getMatchDB();
	
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
	public void testMatchDBAlgorithmAnalysis() throws Throwable {

		int inserts = (totalNumberOfUsers / 2) / 2;
		int updates = inserts;
		int selects = inserts;

		// prepare the tables & variables
		final MatchDto[] matches  = new MatchDto[inserts*2];
		long millis = System.currentTimeMillis();
		for (int i=0; i<inserts*2; i++) {
			matches[i]  = new MatchDto(users[i], users[i+(inserts*2)], "Serialized game", millis, EMatchStatus.ACTIVE);
		}

		new DatabaseAlgorithmAnalysis("IMatchDB", numberOfThreads, inserts, updates, selects) {
			public void resetTables() throws SQLException {
				matchDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				matchDB.storeNewMatch(matches[i]);
			}
			public void updateLoopCode(int i) throws SQLException {
				matchDB.updateMatchStatus(matches[i], EMatchStatus.CLOSED_WORD_GUESSED);
			}
			public void selectLoopCode(int i) throws SQLException {
				matchDB.retrieveMatch(matches[i].getMatchId());
			}
		};
		
		// attempt to speed up the next 'UserDB.reset()',
		// which is unreasonably slow due to the non-unique relations to userId
		matchDB.reset();

	}
	
}
