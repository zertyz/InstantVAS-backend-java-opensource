package main;

import java.sql.SQLException;

import adapters.PostgreSQLAdapter;
import instantvas.tests.InstantVASSMSAppModuleChatTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleHangmanTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleSubscriptionTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleTestsConfiguration;
import mutua.events.PostgreSQLQueueEventLinkPerformanceTests;
import mutua.events.PostgreSQLQueueEventLinkTests;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.handlers.IInstrumentationHandler;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerLogConsole;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsappmodule.dal.IChatDBBehavioralTests;
import mutua.smsappmodule.dal.IChatDBPerformanceTests;
import mutua.smsappmodule.dal.IMatchDBBehavioralTests;
import mutua.smsappmodule.dal.IMatchDBPerformanceTests;
import mutua.smsappmodule.dal.INextBotWordsDBBehavioralTests;
import mutua.smsappmodule.dal.INextBotWordsDBPerformanceTests;
import mutua.smsappmodule.dal.IProfileDBBehavioralTests;
import mutua.smsappmodule.dal.IProfileDBPerformanceTests;
import mutua.smsappmodule.dal.ISessionDBBehavioralTests;
import mutua.smsappmodule.dal.ISessionDBPerformanceTests;
import mutua.smsappmodule.dal.IUserDBBehavioralTests;
import mutua.smsappmodule.dal.IUserDBPerformanceTests;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryHangman;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dal.SMSAppModuleDALFactorySubscription;
import mutua.tests.MutuaEventsAdditionalEventLinksTestsConfiguration;

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
		SMSAppModuleDALFactoryHangman      hangmanModuleDAL;
		
		if ("POSTGRESQL".equals(dal)) {
			baseModuleDAL    = SMSAppModuleDALFactory            .POSTGRESQL;
			subscriptionDAL  = SMSAppModuleDALFactorySubscription.POSTGRESQL;
			profileModuleDAL = SMSAppModuleDALFactoryProfile     .POSTGRESQL;
			chatModuleDAL    = SMSAppModuleDALFactoryChat        .POSTGRESQL;
			hangmanModuleDAL = SMSAppModuleDALFactoryHangman     .POSTGRESQL;
		} else if ("RAM".equals(dal)) {
			baseModuleDAL    = SMSAppModuleDALFactory            .RAM;
			subscriptionDAL  = SMSAppModuleDALFactorySubscription.RAM;
			profileModuleDAL = SMSAppModuleDALFactoryProfile     .RAM;
			chatModuleDAL    = SMSAppModuleDALFactoryChat        .RAM;
			hangmanModuleDAL = SMSAppModuleDALFactoryHangman     .RAM;
		} else {
			System.out.println("Incorrect 'dal' provided. Please, consult usage.");
			return;
		}
		
		System.out.println("\n### Starting. Please copy & paste it to luiz@InstantVAS.com:");
				
		System.out.println("\n### Applying configuration:");
		InstantVASSMSAppModuleTestsConfiguration            .configureDefaultValuesForNewInstances(loadFactor, baseModuleDAL,    connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleSubscriptionTestsConfiguration.configureDefaultValuesForNewInstances(loadFactor, subscriptionDAL,  connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleProfileTestsConfiguration     .configureDefaultValuesForNewInstances(loadFactor, profileModuleDAL, connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleChatTestsConfiguration        .configureDefaultValuesForNewInstances(loadFactor, chatModuleDAL,    connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleHangmanTestsConfiguration     .configureDefaultValuesForNewInstances(loadFactor, hangmanModuleDAL, connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		if ("POSTGRESQL".equals(dal)) {
			// configure postgreSQL queues
			MutuaEventsAdditionalEventLinksTestsConfiguration.configureDefaultValuesForNewInstances(loadFactor, 0, 10, null, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		}
		
		System.out.println("\n### Configuring Instrumentation:");
		IInstrumentationHandler log = new InstrumentationHandlerLogConsole(InstantVASDALTester.class.getCanonicalName(), shouldDebugQueries == true ? ELogSeverity.DEBUG : ELogSeverity.ERROR);
		Instrumentation.configureDefaultValuesForNewInstances(log, log, log);

		
		System.out.println("\n### Instantiating database engines:");
		InstantVASSMSAppModuleTestsConfiguration            .getInstance();
		InstantVASSMSAppModuleSubscriptionTestsConfiguration.getInstance();
		InstantVASSMSAppModuleProfileTestsConfiguration     .getInstance();
		InstantVASSMSAppModuleChatTestsConfiguration        .getInstance();
		InstantVASSMSAppModuleHangmanTestsConfiguration     .getInstance();
		
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
				IChatDBBehavioralTests.class.getName(),
				INextBotWordsDBPerformanceTests.class.getName(),
				INextBotWordsDBBehavioralTests.class.getName(),
				IMatchDBPerformanceTests.class.getName(),
				IMatchDBBehavioralTests.class.getName());
		} else {

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
				IChatDBBehavioralTests.class.getName(),
				INextBotWordsDBPerformanceTests.class.getName(),
				INextBotWordsDBBehavioralTests.class.getName(),
				IMatchDBPerformanceTests.class.getName(),
				IMatchDBBehavioralTests.class.getName());
		}
	}

}