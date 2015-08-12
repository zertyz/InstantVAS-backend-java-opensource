package mutua.smsappmodule.dal;

import java.sql.SQLException;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationSubscriptionTests.*;
import mutua.smsappmodule.DatabaseAlgorithmAnalysis;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.SplitRun;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.dto.SubscriptionDto.ESubscriptionChannel;

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


	@Test
	public void testAlgorithmAnalysis() throws SQLException, InterruptedException {
		
		// TODO: criar classe FillUsersTableForPerformanceTests no base modules para evitar de todos terem que girarem a mesma roda
		// TODO: análise de features prometidos para a Celltick. Demanda inicial Koby + Prometido por mim + Demanda relatórios Dedé
		// TODO: acrescentar testes aos performance tests e, quando não puder, criar os reentrancy tests
		// TODO: colocar essa galera toda no svn
		// TODO: criar mais um teste de performance pro sessions, quando existem muitas sessions por usuário
		// TODO: fazer o mesmo para muitas e muitas sessions e um usuário só -- no modelo Pure-Collisional, Non-Collisional e Hibrid-Collisional
		// TODO: fazer testes de performance para as filas, no mesmo modelo. Criar também os stored procedures
		// TODO: criar os outros bancos de dados: hangman: botwords por usuário, matches; chat: started conversations, chat history (substring of MO/MT), block?
		// TODO: testar performance do sessions com o enum data type http://www.postgresql.org/docs/9.2/static/datatype-enum.html
		
		SMSAppModuleTestCommons.resetTables();

		int  numberOfThreads = 4;
		int inserts =  SMSAppModuleConfigurationTests.PERFORMANCE_TESTS_LOAD_FACTOR * ((DEFAULT_SUBSCRIPTION_DAL == SMSAppModuleDALFactorySubscription.RAM) ? 500000 : 20000);
		int updates = inserts / 5;
		int selects = inserts / 5;
		final long phoneStart = 991230000;

		// prepare the tables & variables
		final UserDto[]         users         = new UserDto[inserts*2];
		final SubscriptionDto[] subscriptions = new SubscriptionDto[inserts*2];
		for (int i=0; i<inserts*2; i++) {
			users[i]         = userDB.assureUserIsRegistered(Long.toString(phoneStart+i));
			subscriptions[i] = new SubscriptionDto(users[i], ESubscriptionChannel.SMS);
		}

		new DatabaseAlgorithmAnalysis("ISubscriptionDB", numberOfThreads, inserts, updates, selects) {
			public void resetTables() throws SQLException {
				subscriptionDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				subscriptionDB.setSubscriptionRecord(subscriptions[i]);
			}
			public void updateLoopCode(int i) throws SQLException {
				subscriptionDB.setSubscriptionRecord(subscriptions[i]);
			}
			public void selectLoopCode(int i) throws SQLException {
				subscriptionDB.getSubscriptionRecord(users[i]);
			}
		};

	}
}
