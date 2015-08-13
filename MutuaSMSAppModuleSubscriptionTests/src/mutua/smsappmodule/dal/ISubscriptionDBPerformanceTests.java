package mutua.smsappmodule.dal;

import java.sql.SQLException;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationSubscriptionTests.*;
import mutua.smsappmodule.DatabaseAlgorithmAnalysis;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.SplitRun;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.SubscriptionDto.EUnsubscriptionChannel;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.dto.SubscriptionDto.ESubscriptionChannel;

import org.junit.BeforeClass;
import org.junit.Test;

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

	private IUserDB         userDB         = DEFAULT_MODULE_DAL.getUserDB();
	private ISubscriptionDB subscriptionDB = DEFAULT_SUBSCRIPTION_DAL.getSubscriptionDB();

	// algorithm settings
	private static int numberOfThreads = 4;

	// users table pre-fill
	private static int       totalNumberOfUsers = SMSAppModuleConfigurationTests.PERFORMANCE_TESTS_LOAD_FACTOR * ((DEFAULT_SUBSCRIPTION_DAL == SMSAppModuleDALFactorySubscription.RAM) ? 1000000 : 40000);	// please, be sure the division between this and 'numberOfThreads' is round
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
	public void testAlgorithmAnalysis() throws SQLException, InterruptedException {
		
		// TODO: análise de features prometidos para a Celltick. Demanda inicial Koby + Prometido por mim + Demanda relatórios Dedé
		// DONE: acrescentar testes aos performance tests e, quando não puder, criar os reentrancy tests
		// DONE: colocar essa galera toda no svn
		// NODO: criar classe FillUsersTableForPerformanceTests no base modules para evitar de todos terem que girarem a mesma roda
		// DONE: criar mais um teste de performance pro sessions, quando existem muitas sessions por usuário
		// DONE: fazer o mesmo para muitas e muitas sessions e um usuário só -- no modelo Pure-Collisional, Non-Collisional e Hibrid-Collisional
		// TODO: testar performance do sessions com o enum data type http://www.postgresql.org/docs/9.2/static/datatype-enum.html
		// TODO: fazer testes de performance para as filas, no mesmo modelo. Criar também os stored procedures
		// TODO: criar os outros bancos de dados: hangman: botwords por usuário (vai para o profile), matches; chat: started conversations, chat history (substring of MO/MT), block?
		// DONE: criar uma tabela MutuaSMSMeta para controlar as versões de cada tabela & stored procedure
		
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
