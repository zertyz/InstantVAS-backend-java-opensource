package instantvas.tests;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.config.InstantVASSMSAppModuleConfiguration;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;

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

	public static Instrumentation<DefaultInstrumentationProperties, String> log;
	public static SMSAppModuleNavigationStates baseModuleNavigationStates;
	
	// this module's tests behavior
	/** The desired data access handler for the 'SMS Module' base features */
	public static SMSAppModuleDALFactory DEFAULT_SMS_MODULE_DAL;
	/** how much to load into the performance tests */
	public static int PERFORMANCE_TESTS_LOAD_FACTOR;
	
	/** method to be called to configure all the modules needed to get the desired instance of 'InstantVASSMSAppModule' base modules */
	public static void configureSMSAppModuleTests(Instrumentation<DefaultInstrumentationProperties, String> log, 
		int performanceTestsLoadFactor, SMSAppModuleDALFactory defaultModuleDAL,
		int postgreSQLConnectionPoolSize, boolean postgreSQLAllowDataStructuresAssertion,
		boolean postreSQLShouldDebugQueries, String postreSQLHostname, int postreSQLPort, String postreSQLDatabase, String postreSQLUser,
		String postreSQLPassword) throws SQLException {
		
		InstantVASSMSAppModuleTestsConfiguration.log = log;
		
		PERFORMANCE_TESTS_LOAD_FACTOR  = performanceTestsLoadFactor;
		DEFAULT_SMS_MODULE_DAL         = defaultModuleDAL;
		
		// Suggested by 'InstantVASSMSAppModuleConfiguration.configureSMSAppModule' */
		PostgreSQLAdapter.configureDefaultValuesForNewInstances(null, postgreSQLConnectionPoolSize);
		Object[] baseModule = InstantVASSMSAppModuleConfiguration.getBaseModuleInstances(log, defaultModuleDAL, postgreSQLAllowDataStructuresAssertion,
			postreSQLShouldDebugQueries, postreSQLHostname, postreSQLPort, postreSQLDatabase, postreSQLUser, postreSQLPassword,
			new ICommandProcessor[0], new Object[0][], new Object[0][]);
		
		baseModuleNavigationStates = (SMSAppModuleNavigationStates) baseModule[0];
		
		System.err.println(InstantVASSMSAppModuleTestsConfiguration.class.getName() + ": test configuration loaded.");
	}
		// TO DO 1/2/16:
//		1) Deletar default DAL do factory
//		2) Deletar o config de cada módulo (manter os dos testes) -- tudo dos módulos deve ser configurado a partir de constructors
//		3) 
//
//		-- Phrasings:
//		As frases passarão a conter uma interface a uma classe base (que implementa a interface). Com a interface, podemos definir frases a partir de enums. A classe base garante que os enums só serão acessíveis via os métodos de instância.
//
//		-- Commands:
//		Os comandos passão a depender de um construtor para gerar os cmd*, que serão campos da classe
//
//		-- Navigation states:
//		Move-se os trg de comandos para cá. Os métodos dos navigation states precisam agora receber a instância dos comandos para dizer qual é o comando
//		que precisa ser executado; haverá um método setCommandTriggers(icommand, triggers) onde cada icommand pode ter somente 1 array de triggers -- em outras palavras, acaba-se com os enums
//
//		-- Configuration: ainda ver o que fazer, mas manter a linha: configurar para novas instâncias

	
	static {
		try {
			configureSMSAppModuleTests(
				// log
				new Instrumentation<DefaultInstrumentationProperties, String>(
					"SMSModuleTests", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null),
				0, SMSAppModuleDALFactory.POSTGRESQL,
				// PostgreSQL properties
				0,	// don't touch default connection pool size
				true,
				false, "venus", 5432, "hangman", "hangman", "hangman");
		} catch (SQLException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
}
