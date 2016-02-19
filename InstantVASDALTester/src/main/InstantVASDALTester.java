package main;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;
import mutua.events.PostgreSQLQueueEventLinkPerformanceTests;
import mutua.events.PostgreSQLQueueEventLinkTests;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsappmodule.dal.IChatDBBehavioralTests;
import mutua.smsappmodule.dal.IChatDBPerformanceTests;
import mutua.smsappmodule.dal.IProfileDBBehavioralTests;
import mutua.smsappmodule.dal.IProfileDBPerformanceTests;
import mutua.smsappmodule.dal.ISessionDBBehavioralTests;
import mutua.smsappmodule.dal.ISessionDBPerformanceTests;
import mutua.smsappmodule.dal.IUserDBBehavioralTests;
import mutua.smsappmodule.dal.IUserDBPerformanceTests;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dal.SMSAppModuleDALFactorySubscription;
import mutua.tests.MutuaEventsAdditionalEventLinksTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleChatTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleSubscriptionTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleTestsConfiguration;

/** <pre>
 * InstantVASDALTester.java
 * ========================
 * (created by luiz, Jul 28, 2015)
 *
 * Runs JUnit test cases
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class InstantVASDALTester {

	public static void main(String[] args) throws SQLException, IndirectMethodNotFoundException, InterruptedException {

		System.out.println("InstantVASDALTester.jar: tests the DAL implementations needed by version 2.0 of the HangmanSMS game.");
		System.out.println("                         Use this tool to validate any changes on the standard Hangman stored procedures as well");
		System.out.println("                         as changes to the model, indexes, etc -- the algorithm complexities should, at least, be");
		System.out.println("                         the same as stated on the 'Hangman 2.0 Data Model' documentation.");
		System.out.println("                         For a correct algorithm analysis, run these tests on a idle system, with an empty database.");
		if ((args.length < 5) || (args.length > 10)) {
			System.out.println("Usage: sudo bash -c 'echo 0 >/proc/sys/vm/swappiness'");
			System.out.println("       java -Xms1408M -Xmx1408M -Xmn50M -Xss228k -XX:ReservedCodeCacheSize=12M -XX:+AlwaysPreTouch -XX:MaxMetaspaceSize=32M");
			System.out.println("            -Xbatch -Xcomp -XX:+AggressiveOpts -Xshare:auto -Xverify:none -XX:-UseHugeTLBFS -XX:+RelaxAccessControlCheck");
			System.out.println("            -XX:+UseAES -XX:+UseAESIntrinsics -XX:+UseCondCardMark -XX:-UseRTMLocking -XX:OnError='echo REPLACE WITH A COMMAND'");
			System.out.println("            -XX:OnOutOfMemoryError='echo REPLACE WITH A COMMAND' -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly");
			System.out.println("            -XX:CMSInitiatingOccupancyFraction=95 -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark -XX:+UseNUMA");
			System.out.println("            -XX:+UseSerialGC -XX:-UseSHM -XX:+UseStringDeduplication -jar");
			System.out.println("            InstantVASDALTester.jar <postgresql host/ip> <port> <database name> <user> <password>");
			System.out.println("                                    [number of concurrent database connections]");
			System.out.println("                                    [data access layer -- one of RAM, POSTGRESQL, MYSQL, EMBEDDED_DERBY]");
			System.out.println("                                    [performance tests load factor -- increase '-Xmx' accordingly]");
			System.out.println("                                    ['true' or 'false' for creating the database model, if necessary (caution!)]");
			System.out.println("                                    ['true' or 'false' for logging queries]");
			return;
		}
		
		String  hostname                     = args[0];
		int     port                         = Integer.parseInt(args[1]);
		String  database                     = args[2];
		String  user                         = args[3];
		String  password                     = args[4];
		int     concurrentConnectionsNumber  = args.length >= 6  ? Integer.parseInt(args[5])     : 8;
		String  dal                          = args.length >= 7  ? args[6]                       : "POSTGRESQL";
		int     loadFactor                   = args.length >= 8  ? Integer.parseInt(args[7])     : 42;
		boolean allowDataStructuresAssertion = args.length >= 9  ? Boolean.parseBoolean(args[8]) : true;
		boolean shouldDebugQueries           = args.length >= 10 ? Boolean.parseBoolean(args[9]) : false;
		String  connectionProperties         = PostgreSQLAdapter.CONNECTION_PROPERTIES;

		System.out.println("Configuration:");
		System.out.println("\thostname                    : "+hostname);
		System.out.println("\tport                        : "+port);
		System.out.println("\tdatabase                    : "+database);
		System.out.println("\tuser                        : "+user);
		System.out.println("\tpassword                    : "+password);
		System.out.println("\tconcurrentConnectionsNumber : "+concurrentConnectionsNumber);
		System.out.println("\tdal                         : "+dal);
		System.out.println("\tloadFactor                  : "+loadFactor);
		System.out.println("\tallowDataStructuresAssertion: "+allowDataStructuresAssertion);
		System.out.println("\tshouldDebugQueries          : "+shouldDebugQueries);
		System.out.println("\tconnectionProperties        : "+connectionProperties);

		// DALs
		SMSAppModuleDALFactory             baseModuleDAL;
		SMSAppModuleDALFactorySubscription subscriptionDAL;
		SMSAppModuleDALFactoryProfile      profileModuleDAL;
		SMSAppModuleDALFactoryChat         chatModuleDAL;
		
		if ("POSTGRESQL".equals(dal)) {
			baseModuleDAL    = SMSAppModuleDALFactory            .POSTGRESQL;
			subscriptionDAL  = SMSAppModuleDALFactorySubscription.POSTGRESQL;
			profileModuleDAL = SMSAppModuleDALFactoryProfile     .POSTGRESQL;
			chatModuleDAL    = SMSAppModuleDALFactoryChat        .POSTGRESQL;
		} else if ("RAM".equals(dal)) {
			baseModuleDAL    = SMSAppModuleDALFactory            .RAM;
			subscriptionDAL  = SMSAppModuleDALFactorySubscription.RAM;
			profileModuleDAL = SMSAppModuleDALFactoryProfile     .RAM;
			chatModuleDAL    = SMSAppModuleDALFactoryChat        .RAM;
		} else {
			System.out.println("Incorrect 'dal' provided. Please, consult usage.");
			return;
		}
		
		System.out.println("\n### Starting. Please copy & paste it to luiz@InstantVAS.com:");
		Instrumentation<DefaultInstrumentationProperties, String> log = new Instrumentation<DefaultInstrumentationProperties, String>(InstantVASDALTester.class.getCanonicalName(), DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null);
				
		System.out.println("\n### Applying configuration:");
		InstantVASSMSAppModuleTestsConfiguration            .configureDefaultValuesForNewInstances(log, loadFactor, baseModuleDAL,    connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleSubscriptionTestsConfiguration.configureDefaultValuesForNewInstances(log, loadFactor, subscriptionDAL,  connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleProfileTestsConfiguration     .configureDefaultValuesForNewInstances(log, loadFactor, profileModuleDAL, connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleChatTestsConfiguration        .configureDefaultValuesForNewInstances(log, loadFactor, chatModuleDAL,    connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password, "ChatTestMOQueue", "eventId", "text");
		
		System.out.println("\n### Instantiating database engines:");
		InstantVASSMSAppModuleTestsConfiguration            .getInstance();
		InstantVASSMSAppModuleSubscriptionTestsConfiguration.getInstance();
		InstantVASSMSAppModuleProfileTestsConfiguration     .getInstance();
		InstantVASSMSAppModuleChatTestsConfiguration        .getInstance();
		
		if ("RAM".equals(dal)) {
			System.out.println("\n### Now running the RAM tests:");
			org.junit.runner.JUnitCore.main(
				//PostgreSQLQueueEventLinkPerformanceTests.class.getName(),
				//PostgreSQLQueueEventLinkTests.class.getName(),
				IUserDBPerformanceTests.class.getName(),
				IUserDBBehavioralTests.class.getName(),
				ISessionDBPerformanceTests.class.getName(),
				ISessionDBBehavioralTests.class.getName(),
				IProfileDBPerformanceTests.class.getName(),
				IProfileDBBehavioralTests.class.getName(),
				IChatDBPerformanceTests.class.getName(),
				IChatDBBehavioralTests.class.getName());
		} else {
			// configure postgreSQL queues
			MutuaEventsAdditionalEventLinksTestsConfiguration.configureMutuaEventsAdditionalEventLinksTests(log, loadFactor, 0, 10, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password, concurrentConnectionsNumber);

			System.out.println("\n### Now running the "+dal+" tests:");
			org.junit.runner.JUnitCore.main(
				PostgreSQLQueueEventLinkPerformanceTests.class.getName(),
				PostgreSQLQueueEventLinkTests.class.getName(),
				IUserDBPerformanceTests.class.getName(),
				IUserDBBehavioralTests.class.getName(),
				ISessionDBPerformanceTests.class.getName(),
				ISessionDBBehavioralTests.class.getName(),
				IProfileDBPerformanceTests.class.getName(),
				IProfileDBBehavioralTests.class.getName(),
				IChatDBPerformanceTests.class.getName(),
				IChatDBBehavioralTests.class.getName());
		}


//		// base module configuration
//		JDBCAdapter.CONNECTION_POOL_SIZE = 8;
//		SMSAppModuleDALFactory.        DEFAULT_DAL        = SMSAppModuleDALFactory.POSTGRESQL;
//		InstantVASSMSAppModuleTestsConfiguration.BASE_MODULE_DAL = SMSAppModuleDALFactory.POSTGRESQL;
//		InstantVASSMSAppModuleTestsConfiguration.PERFORMANCE_TESTS_LOAD_FACTOR = 42;
//		InstantVASSMSAppModuleTestsConfiguration.POSTGRESQL_DEBUG_QUERIES = true;
//		InstantVASSMSAppModuleTestsConfiguration.POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION = true;
//		InstantVASSMSAppModuleTestsConfiguration.POSTGRESQL_CONNECTION_HOSTNAME       = args[0];
//		InstantVASSMSAppModuleTestsConfiguration.POSTGRESQL_CONNECTION_PORT           = Integer.parseInt(args[1]);
//		InstantVASSMSAppModuleTestsConfiguration.POSTGRESQL_CONNECTION_DATABASE_NAME  = args[2];
//		InstantVASSMSAppModuleTestsConfiguration.POSTGRESQL_CONNECTION_USER           = args[3];
//		InstantVASSMSAppModuleTestsConfiguration.POSTGRESQL_CONNECTION_PASSWORD       = args[4];
//		InstantVASSMSAppModuleTestsConfiguration.applyConfiguration();
//		// validate db model
//		SMSAppModuleDALFactory.POSTGRESQL.checkDataAccessLayers();
//		
//		// queues
//		// force the creation of 'SpecializedMOQueue', needed to create the model for the Chat module
//		PostgreSQLQueueEventLinkTests queueDBSetupTests = new PostgreSQLQueueEventLinkTests();
//		queueDBSetupTests.toString();	// force the static method to run
//		QueuesPostgreSQLAdapter.configureDefaultValuesForNewInstances(log, args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);
//		queueDBSetupTests.testAddSeveralItemsAndConsumeAllAtOnce();
//
//		// subscription configuration
//		SMSAppModuleDALFactorySubscription.DEFAULT_DAL = SMSAppModuleDALFactorySubscription.POSTGRESQL;
//		SMSAppModulePostgreSQLAdapterSubscription.configureSubscriptionDatabaseModule(log, args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);
//		SMSAppModuleConfigurationSubscription.subscriptionToken  = "postgresqltests";	// force the internal subscriptions db references to load using the above credentials before the test module does so
//		InstantVASSMSAppModuleSubscriptionTestsConfiguration.DEFAULT_SUBSCRIPTION_DAL = SMSAppModuleDALFactorySubscription.POSTGRESQL;
//		InstantVASSMSAppModuleSubscriptionTestsConfiguration.DEFAULT_MODULE_DAL       = SMSAppModuleDALFactory.            POSTGRESQL;
//		InstantVASSMSAppModuleSubscriptionTestsConfiguration.POSTGRESQL_DEBUG_QUERIES = false;
//		InstantVASSMSAppModuleSubscriptionTestsConfiguration.POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION = true;
//		InstantVASSMSAppModuleSubscriptionTestsConfiguration.POSTGRESQL_CONNECTION_HOSTNAME       = args[0];
//		InstantVASSMSAppModuleSubscriptionTestsConfiguration.POSTGRESQL_CONNECTION_PORT           = Integer.parseInt(args[1]);
//		InstantVASSMSAppModuleSubscriptionTestsConfiguration.POSTGRESQL_CONNECTION_DATABASE_NAME  = args[2];
//		InstantVASSMSAppModuleSubscriptionTestsConfiguration.POSTGRESQL_CONNECTION_USER           = args[3];
//		InstantVASSMSAppModuleSubscriptionTestsConfiguration.POSTGRESQL_CONNECTION_PASSWORD       = args[4];
//		InstantVASSMSAppModuleSubscriptionTestsConfiguration.applyConfiguration();
//		// validate db model
//		SMSAppModuleDALFactorySubscription.POSTGRESQL.checkDataAccessLayers();
//		
//		// profile configuration
//		SMSAppModuleDALFactoryProfile.DEFAULT_DAL = SMSAppModuleDALFactoryProfile.POSTGRESQL;
//		SMSAppModulePostgreSQLAdapterProfile.configureProfileDatabaseModule(log, args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);
//		InstantVASSMSAppModuleProfileTestsConfiguration.DEFAULT_PROFILE_DAL      = SMSAppModuleDALFactoryProfile.POSTGRESQL;
//		InstantVASSMSAppModuleProfileTestsConfiguration.DEFAULT_MODULE_DAL       = SMSAppModuleDALFactory.       POSTGRESQL;
//		InstantVASSMSAppModuleProfileTestsConfiguration.POSTGRESQL_DEBUG_QUERIES = false;
//		InstantVASSMSAppModuleProfileTestsConfiguration.POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION = true;
//		InstantVASSMSAppModuleProfileTestsConfiguration.POSTGRESQL_CONNECTION_HOSTNAME       = args[0];
//		InstantVASSMSAppModuleProfileTestsConfiguration.POSTGRESQL_CONNECTION_PORT           = Integer.parseInt(args[1]);
//		InstantVASSMSAppModuleProfileTestsConfiguration.POSTGRESQL_CONNECTION_DATABASE_NAME  = args[2];
//		InstantVASSMSAppModuleProfileTestsConfiguration.POSTGRESQL_CONNECTION_USER           = args[3];
//		InstantVASSMSAppModuleProfileTestsConfiguration.POSTGRESQL_CONNECTION_PASSWORD       = args[4];
//		InstantVASSMSAppModuleProfileTestsConfiguration.applyConfiguration();
//		// validate db model
//		SMSAppModuleDALFactoryProfile.POSTGRESQL.checkDataAccessLayers();
//		
//		// hangman configuration
//		SMSAppModuleDALFactoryHangman.DEFAULT_DAL = SMSAppModuleDALFactoryHangman.POSTGRESQL;
//		SMSAppModulePostgreSQLAdapterHangman.configureHangmanDatabaseModule(log, args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);
//		SMSAppModuleConfigurationHangmanTests.DEFAULT_HANGMAN_DAL      = SMSAppModuleDALFactoryHangman.POSTGRESQL;
//		SMSAppModuleConfigurationHangmanTests.DEFAULT_MODULE_DAL       = SMSAppModuleDALFactory.       POSTGRESQL;
//		SMSAppModuleConfigurationHangmanTests.POSTGRESQL_DEBUG_QUERIES = false;
//		SMSAppModuleConfigurationHangmanTests.POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION = true;
//		SMSAppModuleConfigurationHangmanTests.POSTGRESQL_CONNECTION_HOSTNAME       = args[0];
//		SMSAppModuleConfigurationHangmanTests.POSTGRESQL_CONNECTION_PORT           = Integer.parseInt(args[1]);
//		SMSAppModuleConfigurationHangmanTests.POSTGRESQL_CONNECTION_DATABASE_NAME  = args[2];
//		SMSAppModuleConfigurationHangmanTests.POSTGRESQL_CONNECTION_USER           = args[3];
//		SMSAppModuleConfigurationHangmanTests.POSTGRESQL_CONNECTION_PASSWORD       = args[4];
//		SMSAppModuleConfigurationHangmanTests.applyConfiguration();
//		// validate db model
//		SMSAppModuleDALFactoryHangman.POSTGRESQL.checkDataAccessLayers();
//
//		// chat configuration
//		SMSAppModuleDALFactoryChat.        DEFAULT_DAL           = SMSAppModuleDALFactoryChat.POSTGRESQL;
//		InstantVASSMSAppModuleChatTestsConfiguration.DEFAULT_CHAT_DAL      = SMSAppModuleDALFactoryChat.POSTGRESQL;
//		InstantVASSMSAppModuleChatTestsConfiguration.DEFAULT_MODULE_DAL    = SMSAppModuleDALFactory.    POSTGRESQL;
//		InstantVASSMSAppModuleChatTestsConfiguration.POSTGRESQL_DEBUG_QUERIES = false;
//		InstantVASSMSAppModuleChatTestsConfiguration.POSTGRESQL_ALLOW_DATABASE_ADMINISTRATION = true;
//		InstantVASSMSAppModuleChatTestsConfiguration.POSTGRESQL_CONNECTION_HOSTNAME       = args[0];
//		InstantVASSMSAppModuleChatTestsConfiguration.POSTGRESQL_CONNECTION_PORT           = Integer.parseInt(args[1]);
//		InstantVASSMSAppModuleChatTestsConfiguration.POSTGRESQL_CONNECTION_DATABASE_NAME  = args[2];
//		InstantVASSMSAppModuleChatTestsConfiguration.POSTGRESQL_CONNECTION_USER           = args[3];
//		InstantVASSMSAppModuleChatTestsConfiguration.POSTGRESQL_CONNECTION_PASSWORD       = args[4];
//		InstantVASSMSAppModuleChatTestsConfiguration.applyConfiguration();
//		// validate db model
//		SMSAppModuleDALFactoryChat.POSTGRESQL.checkDataAccessLayers();	// can't create the tables before some queue tests run
//
//		// app name
//		InstantVASSMSAppModuleConfiguration.APPName = "PostgreSQLTests";
//		InstantVASSMSAppModuleConfiguration.applyConfiguration();
//
//		System.out.println("Now running the tests:");
//		org.junit.runner.JUnitCore.main(
//			PostgreSQLQueueEventLinkPerformanceTests.class.getName(),
//			PostgreSQLQueueEventLinkTests.class.getName(),
//			IUserDBPerformanceTests.class.getName(),
//			IUserDBBehavioralTests.class.getName(),
//			ISessionDBPerformanceTests.class.getName(),
//			ISessionDBBehavioralTests.class.getName(),
//			ISubscriptionDBPerformanceTests.class.getName(),
//			ISubscriptionDBBehavioralTests.class.getName(),
//			IProfileDBPerformanceTests.class.getName(),
//			IProfileDBBehavioralTests.class.getName(),
//			IChatDBPerformanceTests.class.getName(),
//			IChatDBBehavioralTests.class.getName(),
//			INextBotWordsDBPerformanceTests.class.getName(),
//			INextBotWordsDBBehavioralTests.class.getName(),
//			IMatchDBPerformanceTests.class.getName(),
//			IMatchDBBehavioralTests.class.getName()
//		);
	}

}
