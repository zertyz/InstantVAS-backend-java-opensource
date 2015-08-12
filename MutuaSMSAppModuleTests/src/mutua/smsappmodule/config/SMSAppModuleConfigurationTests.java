package mutua.smsappmodule.config;

import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * SMSAppModuleConfigurationTests.java
 * ===================================
 * (created by luiz, Jul 28, 2015)
 *
 * Defines the configuration for the base module test application
 *
 * @see SMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationTests {


	// log
	public static Instrumentation<DefaultInstrumentationProperties, String> log = new Instrumentation<DefaultInstrumentationProperties, String>(
		"SMSModuleTests", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null);
	
	// database
	public static SMSAppModuleDALFactory DEFAULT_MODULE_DAL        = SMSAppModuleDALFactory.POSTGRESQL;
	public static Boolean POSTGRESQL_DEBUG_QUERIES                 = false;
	public static Boolean POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION = true;
	public static String  POSTGRESQL_CONNECTION_HOSTNAME      = "venus";
	public static int     POSTGRESQL_CONNECTION_PORT          = 5432;
	public static String  POSTGRESQL_CONNECTION_DATABASE_NAME = "hangman";
	public static String  POSTGRESQL_CONNECTION_USER          = "hangman";
	public static String  POSTGRESQL_CONNECTION_PASSWORD      = "hangman";
	public static String  POSTGRESQL_CONNECTION_PROPERTIES    = "charSet=UTF8&tcpKeepAlive=true&connectTimeout=30&loginTimeout=30&socketTimeout=300";
	
	// performance tests
	public static int PERFORMANCE_TESTS_LOAD_FACTOR = 1;

			
	
	/************
	** METHODS **
	************/
	
	/** Apply on-the-fly configuration changes */
	public static void applyConfiguration() {
		
		SMSAppModulePostgreSQLAdapter.log = log;
		SMSAppModulePostgreSQLAdapter.HOSTNAME              = POSTGRESQL_CONNECTION_HOSTNAME;
		SMSAppModulePostgreSQLAdapter.PORT                  = POSTGRESQL_CONNECTION_PORT;
		SMSAppModulePostgreSQLAdapter.DATABASE              = POSTGRESQL_CONNECTION_DATABASE_NAME;
		SMSAppModulePostgreSQLAdapter.USER                  = POSTGRESQL_CONNECTION_USER;
		SMSAppModulePostgreSQLAdapter.PASSWORD              = POSTGRESQL_CONNECTION_PASSWORD;
		SMSAppModuleDALFactory.DEFAULT_DAL                  = DEFAULT_MODULE_DAL;

		PostgreSQLAdapter.CONNECTION_PROPERTIES         = POSTGRESQL_CONNECTION_PROPERTIES;
		PostgreSQLAdapter.ALLOW_DATABASE_ADMINISTRATION = POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION;
		
		JDBCAdapter.SHOULD_DEBUG_QUERIES = POSTGRESQL_DEBUG_QUERIES;

		SMSAppModuleConfiguration.APPName = "ModuleTest";
		
	}

	
	static {
		applyConfiguration();
	}
}
