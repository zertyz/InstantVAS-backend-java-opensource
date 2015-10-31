package mutua.smsappmodule.config;

import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterProfile;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * SMSAppModuleConfigurationProfileTests.java
 * ==========================================
 * (created by luiz, Aug 3, 2015)
 *
 * Defines the configuration for the "Profile" module test application
 *
 * @see SMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationProfileTests {

	// log
	public static Instrumentation<DefaultInstrumentationProperties, String> log = new Instrumentation<DefaultInstrumentationProperties, String>(
		"ProfileModuleTests", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null);
	
	// database
	public static SMSAppModuleDALFactoryProfile  DEFAULT_PROFILE_DAL = SMSAppModuleDALFactoryProfile.RAM;
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

		SMSAppModulePostgreSQLAdapterProfile.log = log;
		SMSAppModulePostgreSQLAdapterProfile.HOSTNAME              = POSTGRESQL_CONNECTION_HOSTNAME;
		SMSAppModulePostgreSQLAdapterProfile.PORT                  = POSTGRESQL_CONNECTION_PORT;
		SMSAppModulePostgreSQLAdapterProfile.DATABASE              = POSTGRESQL_CONNECTION_DATABASE_NAME;
		SMSAppModulePostgreSQLAdapterProfile.USER                  = POSTGRESQL_CONNECTION_USER;
		SMSAppModulePostgreSQLAdapterProfile.PASSWORD              = POSTGRESQL_CONNECTION_PASSWORD;
		SMSAppModuleDALFactoryProfile.DEFAULT_DAL                  = DEFAULT_PROFILE_DAL;

		PostgreSQLAdapter.CONNECTION_PROPERTIES         = POSTGRESQL_CONNECTION_PROPERTIES;
		PostgreSQLAdapter.ALLOW_DATABASE_ADMINISTRATION = POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION;
		
		JDBCAdapter.SHOULD_DEBUG_QUERIES = POSTGRESQL_DEBUG_QUERIES;

		// set the 'APPName' before 'SUBSCRIPTIONtrgLocalStartDoubleOptin' is defined
		SMSAppModuleConfiguration.APPName = "ProfileTest";
		
	}

	
	static {
		applyConfiguration();
	}
}
