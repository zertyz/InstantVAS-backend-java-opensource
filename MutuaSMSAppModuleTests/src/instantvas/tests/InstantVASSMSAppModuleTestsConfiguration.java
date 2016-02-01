package instantvas.tests;

import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;

/** <pre>
 * InstantVASSMSAppModuleTestsConfiguration.java
 * =============================================
 * (created by luiz, Jul 28, 2015)
 *
 * Configures the classes' static options for the base sms applications module.
 * 
 * Follows the "Mutua Configurable Module" pattern.
 *
 * @see SMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class InstantVASSMSAppModuleTestsConfiguration {


	// log
	public static Instrumentation<DefaultInstrumentationProperties, String> LOG = new Instrumentation<DefaultInstrumentationProperties, String>(
		"SMSModuleTests", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null);
	
	// this module's tests behavior
	/** The desired data access handler for the 'SMS Module' base features */
	public static SMSAppModuleDALFactory DEFAULT_SMS_MODULE_DAL = SMSAppModuleDALFactory.POSTGRESQL;
	/** how much to load into the performance tests */
	public static int PERFORMANCE_TESTS_LOAD_FACTOR             = 1;
	/** The name of this SMS Game / Application, for phrasing & logging purposes */
	public static String APPName      = "GenericApp";
	/** The short code of this SMS Game / Application, for phrasing / routing purposes */
	public static String APPShortCode = "991";

	
	// PostgreSQL
	public static Boolean POSTGRESQL_SHOULD_DEBUG_QUERIES            = false;
	public static Boolean POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTION = true;
	public static String  POSTGRESQL_HOSTNAME                        = "venus";
	public static int     POSTGRESQL_PORT                            = 5432;
	public static String  POSTGRESQL_DATABASE                        = "hangman";
	public static String  POSTGRESQL_USER                            = "hangman";
	public static String  POSTGRESQL_PASSWORD                        = "hangman";
	public static int     POSTGRESQL_CONNECTION_POOL_SIZE            = PostgreSQLAdapter.CONNECTION_POOL_SIZE;
	

			
	/** method to be called when attempting to configure the default behavior of 'MutuaEventsAdditionalEventLinksTests' module.
	 *  The following default values won't be touched if:
	 *  @param log is null
	 *  @param performanceTestsLoadFactor   is < 0
	 *  @param defaultModuleDAL             is not null
	 *  @param postgreSQLConnectionPoolSize is <= 0 */
	public static void configureSMSAppModuleTests(Instrumentation<DefaultInstrumentationProperties, String> log, 
		int performanceTestsLoadFactor, SMSAppModuleDALFactory defaultModuleDAL,
		boolean postgreSQLAllowDataStructuresAssertion, boolean postreSQLShouldDebugQueries,
		String postreSQLHostname, int postreSQLPort, String postreSQLDatabase, String postreSQLUser, String postreSQLPassword,
		int postgreSQLConnectionPoolSize) throws SQLException {
		
		LOG = log != null ? log : LOG;
		
		PERFORMANCE_TESTS_LOAD_FACTOR  = performanceTestsLoadFactor >= 0    ? performanceTestsLoadFactor : PERFORMANCE_TESTS_LOAD_FACTOR;
		DEFAULT_SMS_MODULE_DAL             = defaultModuleDAL           != null ? defaultModuleDAL           : DEFAULT_SMS_MODULE_DAL;
		
		POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTION = postgreSQLAllowDataStructuresAssertion;
		POSTGRESQL_SHOULD_DEBUG_QUERIES            = postreSQLShouldDebugQueries;
		
		POSTGRESQL_HOSTNAME = postreSQLHostname;
		POSTGRESQL_PORT     = postreSQLPort;
		POSTGRESQL_DATABASE = postreSQLDatabase;
		POSTGRESQL_USER     = postreSQLUser;
		POSTGRESQL_PASSWORD = postreSQLPassword;
		
		POSTGRESQL_CONNECTION_POOL_SIZE = postgreSQLConnectionPoolSize > 0 ? postgreSQLConnectionPoolSize : POSTGRESQL_CONNECTION_POOL_SIZE;
		
		applyConfiguration();
		
		log.reportDebug(InstantVASSMSAppModuleTestsConfiguration.class.getName() + ": new configuration loaded.");
	}
	
	/** Apply on-the-fly configuration changes */
	public static void applyConfiguration() throws SQLException {
		PostgreSQLAdapter.configureDefaultValuesForNewInstances(null, POSTGRESQL_CONNECTION_POOL_SIZE);
		SMSAppModulePostgreSQLAdapter.configureDefaultValuesForNewInstances(LOG, POSTGRESQL_ALLOW_DATA_STRUCTURES_ASSERTION,
			POSTGRESQL_SHOULD_DEBUG_QUERIES, POSTGRESQL_HOSTNAME, POSTGRESQL_PORT, POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
		
		// TO DO 28/01/2016:
		// v 1) acertas as mudanças de parâmetros para o novo JDBCAdapter
		// 2) Remodelar esta classe para o "Mutua Configurable Module" pattern
		// v 2.1) Copiar de MutuaEventsAdditionalEventLinksConfiguration o comentário da classe
		// 2.2) O molde de declarações de variáveis -- a classe precisa conter tudo que influenciará na criação de instâncias para sua execução
		// v 2.3) a função configureSMSAppModuleTests
		// v 2.4) mover esta classe para o pacote 'instantvas.tests' e dar-lhe o nome de 'SMSAppModuleTestsConfiguration'
		// v 2.5) criar a função 'applyConfiguration' e executá-la do construtor estático
		// v 2.6) constatar a utilização do campo PERFORMANCE_TESTS_LOAD_FACTOR e removê-lo se não estiver sendo utilizado neste projeto
		// v 2.7) 'log' tem que ser maiúsculo
		// v 3) Remodelar a class 'SMSAppModulePostgreSQLAdapter' para o Mutua Configurable Class pattern, copiado de 'QueuesPostgreSQLAdapter':
		// v 3.1) 
		
		APPName = "ModuleTest";
		
	}

	
	static {
		try {
			applyConfiguration();
		} catch (SQLException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
}
