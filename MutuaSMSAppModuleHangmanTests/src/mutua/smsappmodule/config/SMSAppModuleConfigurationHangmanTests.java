package mutua.smsappmodule.config;

import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryHangman;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterHangman;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * SMSAppModuleConfigurationHangmanTests.java
 * ==========================================
 * (created by luiz, Aug 13, 2015)
 *
 * Defines the configuration for the "Hangman" module test application
 *
 * @see SMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationHangmanTests {

	// log
	public static Instrumentation<DefaultInstrumentationProperties, String> log = new Instrumentation<DefaultInstrumentationProperties, String>(
		"HangmanModuleTests", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null);
	
	// database
	public static SMSAppModuleDALFactoryHangman  DEFAULT_HANGMAN_DAL = SMSAppModuleDALFactoryHangman.RAM;
	public static SMSAppModuleDALFactory         DEFAULT_MODULE_DAL  = SMSAppModuleDALFactory       .RAM;
	public static Boolean POSTGRESQL_DEBUG_QUERIES                 = false;
	public static Boolean POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION = true;
	public static String  POSTGRESQL_CONNECTION_HOSTNAME      = "venus";
	public static int     POSTGRESQL_CONNECTION_PORT          = 5432;
	public static String  POSTGRESQL_CONNECTION_DATABASE_NAME = "hangman";
	public static String  POSTGRESQL_CONNECTION_USER          = "hangman";
	public static String  POSTGRESQL_CONNECTION_PASSWORD      = "hangman";
	public static String  POSTGRESQL_CONNECTION_PROPERTIES    = "charSet=UTF8&tcpKeepAlive=true&connectTimeout=30&loginTimeout=30&socketTimeout=300";

			
	
	/************
	** METHODS **
	************/
	
	/** Apply on-the-fly configuration changes */
	public static void applyConfiguration() {

		// SMSAppModule configuration
		SMSAppModuleConfigurationTests.log = log;
		SMSAppModuleConfigurationTests.DEFAULT_MODULE_DAL = DEFAULT_MODULE_DAL;
		SMSAppModuleConfigurationTests.POSTGRESQL_CONNECTION_HOSTNAME      = POSTGRESQL_CONNECTION_HOSTNAME;
		SMSAppModuleConfigurationTests.POSTGRESQL_CONNECTION_PORT          = POSTGRESQL_CONNECTION_PORT;
		SMSAppModuleConfigurationTests.POSTGRESQL_CONNECTION_DATABASE_NAME = POSTGRESQL_CONNECTION_DATABASE_NAME;
		SMSAppModuleConfigurationTests.POSTGRESQL_CONNECTION_USER          = POSTGRESQL_CONNECTION_USER;
		SMSAppModuleConfigurationTests.POSTGRESQL_CONNECTION_PASSWORD      = POSTGRESQL_CONNECTION_PASSWORD;
		SMSAppModuleConfigurationTests.applyConfiguration();

		SMSAppModuleConfigurationTests.applyConfiguration();

		SMSAppModulePostgreSQLAdapterHangman.log = log;
		SMSAppModulePostgreSQLAdapterHangman.HOSTNAME              = POSTGRESQL_CONNECTION_HOSTNAME;
		SMSAppModulePostgreSQLAdapterHangman.PORT                  = POSTGRESQL_CONNECTION_PORT;
		SMSAppModulePostgreSQLAdapterHangman.DATABASE              = POSTGRESQL_CONNECTION_DATABASE_NAME;
		SMSAppModulePostgreSQLAdapterHangman.USER                  = POSTGRESQL_CONNECTION_USER;
		SMSAppModulePostgreSQLAdapterHangman.PASSWORD              = POSTGRESQL_CONNECTION_PASSWORD;
		SMSAppModuleDALFactoryHangman.DEFAULT_DAL                  = DEFAULT_HANGMAN_DAL;

		PostgreSQLAdapter.CONNECTION_PROPERTIES         = POSTGRESQL_CONNECTION_PROPERTIES;
		PostgreSQLAdapter.ALLOW_DATABASE_ADMINISTRATION = POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION;
		
		JDBCAdapter.SHOULD_DEBUG_QUERIES = POSTGRESQL_DEBUG_QUERIES;

		// set the 'APPName'
		SMSAppModuleConfiguration.APPName = "HangmanTest";
		
	}

	
	static {
		applyConfiguration();
	}
}
