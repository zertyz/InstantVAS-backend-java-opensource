package main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import adapters.PostgreSQLAdapter;
import instantvas.tests.InstantVASSMSAppModuleChatTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleHangmanTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleSubscriptionTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleTestsConfiguration;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryHangman;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dal.SMSAppModuleDALFactorySubscription;
import mutua.tests.MutuaEventsAdditionalEventLinksTestsConfiguration;

/** <pre>
 * InstantVASTester.java
 * =====================
 * (created by luiz, Feb 19, 2016)
 *
 * Discovers and runs all JUnit tests available on the known packages.
 *
 * @version $Id$
 * @author luiz
*/

public class InstantVASTester {
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, URISyntaxException, SQLException {
		System.out.println("InstantVASTester.jar: Runs all known JUnit tests available for modules used by the InstantVAS.com service.");
		System.out.println("                      Use this tool regularly to validate refactorings, performance enhancements and the");
		System.out.println("                      ProGuard obfuscation parameters.");
		System.out.println("                      For a correct algorithm analysis, run these tests on a idle system, with an empty database.");
		if ((args.length < 5) || (args.length > 10)) {
			System.out.println("Usage: sudo bash -c 'echo 0 >/proc/sys/vm/swappiness'");
			System.out.println("       java -Xms1408M -Xmx1408M -Xmn50M -Xss228k -XX:ReservedCodeCacheSize=12M -XX:+AlwaysPreTouch -XX:MaxMetaspaceSize=32M");
			System.out.println("            -Xbatch -Xcomp -XX:+AggressiveOpts -Xshare:auto -Xverify:none -XX:-UseHugeTLBFS -XX:+RelaxAccessControlCheck");
			System.out.println("            -XX:+UseAES -XX:+UseAESIntrinsics -XX:+UseCondCardMark -XX:-UseRTMLocking -XX:OnError='echo REPLACE WITH A COMMAND'");
			System.out.println("            -XX:OnOutOfMemoryError='echo REPLACE WITH A COMMAND' -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly");
			System.out.println("            -XX:CMSInitiatingOccupancyFraction=95 -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark -XX:+UseNUMA");
			System.out.println("            -XX:+UseSerialGC -XX:-UseSHM -XX:+UseStringDeduplication -jar");
			System.out.println("            InstantVASTester.jar <postgresql host/ip> <port> <database name> <user> <password>");
			System.out.println("                                 [number of concurrent database connections]");
			System.out.println("                                 [data access layer -- one of RAM, POSTGRESQL, MYSQL, EMBEDDED_DERBY]");
			System.out.println("                                 [performance tests load factor -- increase '-Xmx' accordingly]");
			System.out.println("                                 ['true' or 'false' for creating the database model, if necessary (caution!)]");
			System.out.println("                                 ['true' or 'false' for logging queries]");

			System.out.println();
			
			System.exit(1);
		}
		
		// from InstantVASDALTester:
		////////////////////////////
		
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
		Instrumentation<DefaultInstrumentationProperties, String> log = new Instrumentation<DefaultInstrumentationProperties, String>(InstantVASTester.class.getCanonicalName(), DefaultInstrumentationProperties.DIP_MSG, EInstrumentationDataPours.CONSOLE, null);
				
		System.out.println("\n### Applying configuration:");
		MutuaEventsAdditionalEventLinksTestsConfiguration   .configureDefaultValuesForNewInstances(log, loadFactor, -1, -1,           connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleTestsConfiguration            .configureDefaultValuesForNewInstances(log, loadFactor, baseModuleDAL,    connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleSubscriptionTestsConfiguration.configureDefaultValuesForNewInstances(log, loadFactor, subscriptionDAL,  connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleProfileTestsConfiguration     .configureDefaultValuesForNewInstances(log, loadFactor, profileModuleDAL, connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleChatTestsConfiguration        .configureDefaultValuesForNewInstances(log, loadFactor, chatModuleDAL,    connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleHangmanTestsConfiguration     .configureDefaultValuesForNewInstances(log, loadFactor, hangmanModuleDAL, connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		
		System.out.println("\n### Instantiating database engines:");
		// MutuaEventsAdditionalEventLinksTestsConfiguration does not have instances
		InstantVASSMSAppModuleTestsConfiguration            .getInstance();
		InstantVASSMSAppModuleSubscriptionTestsConfiguration.getInstance();
		InstantVASSMSAppModuleProfileTestsConfiguration     .getInstance();
		InstantVASSMSAppModuleChatTestsConfiguration        .getInstance();
		InstantVASSMSAppModuleHangmanTestsConfiguration     .getInstance();
		
		// please, periodically update the classes listed here with the following command, keeping the order implied by the comments:
		// w=~/workspace/celltick/SMSGames/; find "$w" -name "*.java" -exec grep -l 'org.junit.Test' "{}" \; | sed "s|$w[^/]*/src/||" | grep -v main/InstantVASTester.java | sed 's|.java$|.class.getName(),|' | sed 's|/|.|g'
		String[] jUnitTestClasses = new String[] {
			// Mutua libs tests
			adapters.HTTPClientAdapterTest.class.getName(),
			mutua.serialization.SerializationTests.class.getName(),
			mutua.imi.IndirectMethodInvocationTests.class.getName(),
			mutua.events.TestEvents.class.getName(),
			mutua.events.QueueEventLinkTests.class.getName(),
			mutua.events.DirectEventTests.class.getName(),
			mutua.icc.instrumentation.InstrumentationTests.class.getName(),
			// these seems, currently, not to make sense while obfuscated. Please, verify
/*			mutua.p2pcommunications.P2PServicesManagerTest.class.getName(),
			mutua.icc.configuration.ConfigurationManagerTests.class.getName(),
			mutua.icc.configuration.ConfigurationParserTests.class.getName(),*/
			// Instant VAS application tests
			instantvas.nativewebserver.NativeHTTPServerBehavioralTests.class.getName(),
			mutua.schedule.ScheduleControlBehavioralTests.class.getName(),
//			mutua.smsappengine.logic.HangmanAppEngineBehavioralTests.class.getName(),
			// SMS Module tests
			mutua.smsappmodule.smslogic.navigationstates.NavigationStateCommonsTests.class.getName(),
			mutua.smsappmodule.smslogic.sessions.SessionModelTests.class.getName(),
			mutua.smsappmodule.HelpModuleBehavioralTests.class.getName(),
			mutua.smsappmodule.HelpModuleSMSProcessorTests.class.getName(),
			mutua.smsappmodule.SubscriptionModuleSMSProcessorTests.class.getName(),
			mutua.smsappmodule.SubscriptionModuleBehavioralTests.class.getName(),
			mutua.smsappmodule.dal.ISubscriptionDBBehavioralTests.class.getName(),
			mutua.smsappmodule.ChatModuleBehavioralTests.class.getName(),
			//mutua.smsappmodule.ChatModuleSMSProcessorTests.class.getName(),
			mutua.smsappmodule.dal.IChatDBBehavioralTests.class.getName(),
			mutua.smsappmodule.dal.ISessionDBBehavioralTests.class.getName(),
			mutua.smsappmodule.dal.IUserDBBehavioralTests.class.getName(),
			mutua.smsappmodule.hangmangame.HangmanGameTests.class.getName(),
			//mutua.smsappmodule.HangmanModuleSMSProcessorTests.class.getName(),
			mutua.smsappmodule.dal.INextBotWordsDBBehavioralTests.class.getName(),
			mutua.smsappmodule.dal.IMatchDBBehavioralTests.class.getName(),
			mutua.smsappmodule.ProfileModuleBehavioralTests.class.getName(),
			mutua.smsappmodule.ProfileModuleSMSProcessorTests.class.getName(),
			mutua.smsappmodule.dal.IProfileDBBehavioralTests.class.getName(),
			// SMS Modules DAL performance tests
			mutua.events.PostgreSQLQueueEventLinkTests.class.getName(),
			mutua.events.PostgreSQLQueueEventLinkPerformanceTests.class.getName(),
			// Additional Events DAL performance tests
			mutua.smsappmodule.dal.IUserDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.ISessionDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.ISubscriptionDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.IProfileDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.IChatDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.IMatchDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.INextBotWordsDBPerformanceTests.class.getName(),
		};

		System.out.println("Running the tests:");
		org.junit.runner.JUnitCore.main(jUnitTestClasses);
	}
}
