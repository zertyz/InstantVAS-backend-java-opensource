package mutua.smsappmodule.dal;

import static instantvas.tests.InstantVASSMSAppModuleSubscriptionTestsConfiguration.*;

import java.sql.SQLException;

import mutua.smsappmodule.DatabaseAlgorithmAnalysis;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.SubscriptionDto.EUnsubscriptionChannel;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.dto.SubscriptionDto.ESubscriptionChannel;

import org.junit.AfterClass;
import org.junit.Test;

import instantvas.tests.InstantVASSMSAppModuleTestsConfiguration;

/** <pre>
 * ISubscriptionDBPerformanceTests.java
 * ====================================
 * (created by luiz, Jul 25, 2015)
 *
 * Measures and tests the f(n) of the O(f(n)) algorithm complexity implementations
 * of {@link ISubscriptionDB}
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ISubscriptionDBPerformanceTests {

	private IUserDB         userDB         = BASE_MODULE_DAL.getUserDB();
	private ISubscriptionDB subscriptionDB = SUBSCRIPTION_DAL.getSubscriptionDB();

	// algorithm settings
	private static int numberOfThreads = 4;

	// users table pre-fill
	private static int       totalNumberOfUsers = InstantVASSMSAppModuleTestsConfiguration.PERFORMANCE_TESTS_LOAD_FACTOR * ((SUBSCRIPTION_DAL == SMSAppModuleDALFactorySubscription.RAM) ? 1000000 : 40000);	// please, be sure the division between this and 'numberOfThreads' is round
	private static long      phoneStart         = 991230000;
	private static UserDto[] users              = new UserDto[totalNumberOfUsers];

	
	/*******************
	** COMMON METHODS **
	*******************/
	
	//@BeforeClass
	public ISubscriptionDBPerformanceTests() {
		// fill users table
		try {
			subscriptionDB.reset();
			userDB.reset();
			SMSAppModuleTestCommons.insertUsers(userDB, phoneStart, users, numberOfThreads);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Could not fulfill users table", t);
		}
	}

	@AfterClass
	public static void clearRAM() {
		users = null;
		// clear the databases...
	}

	
	/**********
	** TESTS **
	**********/
	
	@Test
	public void testAlgorithmAnalysis() throws Throwable {
		
		int inserts =  totalNumberOfUsers / 2;
		int updates = inserts;
		int selects = inserts;

		// prepare the tables & variables
		final SubscriptionDto[] subscriptions   = new SubscriptionDto[inserts*2];
		final SubscriptionDto[] unsubscriptions = new SubscriptionDto[inserts*2];
		for (int i=0; i<inserts*2; i++) {
			subscriptions[i]   = new SubscriptionDto(users[i], ESubscriptionChannel.SMS);
			unsubscriptions[i] = new SubscriptionDto(users[i], EUnsubscriptionChannel.SMS);
		}

		new DatabaseAlgorithmAnalysis("ISubscriptionDB", numberOfThreads, inserts, updates, selects) {
			public void resetTables() throws SQLException {
				subscriptionDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				subscriptionDB.setSubscriptionRecord(subscriptions[i]);
			}
			public void updateLoopCode(int i) throws SQLException {
				subscriptionDB.setSubscriptionRecord(unsubscriptions[i]);
			}
			public void selectLoopCode(int i) throws SQLException {
				subscriptionDB.getSubscriptionRecord(users[i]);
			}
		};

	}
}
