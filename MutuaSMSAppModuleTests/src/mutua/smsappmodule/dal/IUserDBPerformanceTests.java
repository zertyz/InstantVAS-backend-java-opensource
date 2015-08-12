package mutua.smsappmodule.dal;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationTests.DEFAULT_MODULE_DAL;

import java.sql.SQLException;

import mutua.smsappmodule.DatabaseAlgorithmAnalysis;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;

import org.junit.Test;

/** <pre>
 * IUserDBPerformanceTests.java
 * ============================
 * (created by luiz, Jul 28, 2015)
 *
 * Measures and tests the f(n) of the O(f(n)) algorithm complexity implementations
 * of {@link IUserDB}
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class IUserDBPerformanceTests {
	
	private IUserDB userDB = DEFAULT_MODULE_DAL.getUserDB();

	
	@Test
	public void testAlgorithmAnalysis() throws InterruptedException, SQLException {
		
		SMSAppModuleTestCommons.resetTables();
		
		int numberOfThreads = 4;
		int inserts = 15000 * SMSAppModuleConfigurationTests.PERFORMANCE_TESTS_LOAD_FACTOR;
		int selects = inserts;
		final long phoneStart = 991230000;
		
		// prepare tables & variables
		final String[] phones = new String[inserts*2];
		for (int i=0; i<inserts*2; i++) {
			phones[i] = Long.toString(phoneStart+i);
		}
		
		new DatabaseAlgorithmAnalysis("IUserDB", numberOfThreads, inserts, selects) {
			public void resetTables() throws SQLException {
				userDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				userDB.assureUserIsRegistered(phones[i]);
			}
			public void selectLoopCode(int i) throws SQLException {
				userDB.assureUserIsRegistered(phones[i]);
			}
		};
		
	}

}
