package mutua.smsappmodule.config;

import java.sql.SQLException;

import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.SpecializedMOQueueDataBureau;
import mutua.events.TestEventServer;
import mutua.events.TestEventServer.ETestEventServices;
import mutua.events.postgresql.QueuesPostgreSQLAdapter;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterChat;

import org.junit.Assert;

import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * SMSAppModuleConfigurationChatTests.java
 * =======================================
 * (created by luiz, Sep 8, 2015)
 *
 * Defines the configuration for the "Profile" module test application
 *
 * @see SMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationChatTests {
	// log
	public static Instrumentation<DefaultInstrumentationProperties, String> log = new Instrumentation<DefaultInstrumentationProperties, String>(
		"ChatModuleTests", DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null);
	
	// database
	public static SMSAppModuleDALFactoryChat  DEFAULT_CHAT_DAL   = SMSAppModuleDALFactoryChat.POSTGRESQL;
	public static SMSAppModuleDALFactory      DEFAULT_MODULE_DAL = SMSAppModuleDALFactory    .POSTGRESQL;
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

		// MutuaEventsAdditionalEventLinks configuration
		QueuesPostgreSQLAdapter.log = log; 
		QueuesPostgreSQLAdapter.HOSTNAME = POSTGRESQL_CONNECTION_HOSTNAME;
		QueuesPostgreSQLAdapter.PORT     = POSTGRESQL_CONNECTION_PORT;
		QueuesPostgreSQLAdapter.DATABASE = POSTGRESQL_CONNECTION_DATABASE_NAME;
		QueuesPostgreSQLAdapter.USER     = POSTGRESQL_CONNECTION_USER;
		QueuesPostgreSQLAdapter.PASSWORD = POSTGRESQL_CONNECTION_PASSWORD;

		SMSAppModulePostgreSQLAdapterChat.log = log;
		SMSAppModulePostgreSQLAdapterChat.HOSTNAME              = POSTGRESQL_CONNECTION_HOSTNAME;
		SMSAppModulePostgreSQLAdapterChat.PORT                  = POSTGRESQL_CONNECTION_PORT;
		SMSAppModulePostgreSQLAdapterChat.DATABASE              = POSTGRESQL_CONNECTION_DATABASE_NAME;
		SMSAppModulePostgreSQLAdapterChat.USER                  = POSTGRESQL_CONNECTION_USER;
		SMSAppModulePostgreSQLAdapterChat.PASSWORD              = POSTGRESQL_CONNECTION_PASSWORD;
		SMSAppModulePostgreSQLAdapterChat.configureChatDatabaseModule("SpecializedMOQueue", "eventId", "text");	// from 'moQueueLink', 'PostgreSQLQueueEventLink' and 'SpecializedMOQueueDataBureau'
		SMSAppModuleDALFactoryChat.DEFAULT_DAL                  = DEFAULT_CHAT_DAL;

		PostgreSQLAdapter.CONNECTION_PROPERTIES         = POSTGRESQL_CONNECTION_PROPERTIES;
		PostgreSQLAdapter.ALLOW_DATABASE_ADMINISTRATION = POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION;
		
		JDBCAdapter.SHOULD_DEBUG_QUERIES = POSTGRESQL_DEBUG_QUERIES;

		// set the 'APPName' before 'SUBSCRIPTIONtrgLocalStartDoubleOptin' is defined
		SMSAppModuleConfiguration.APPName = "ChatTest";
		
	}

	
	static {
		applyConfiguration();
	}
}
