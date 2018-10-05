package main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import adapters.PostgreSQLAdapter;
import instantvas.tests.InstantVASSMSAppModuleChatTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleHangmanTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleHelpTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleProfileTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleSubscriptionTestsConfiguration;
import instantvas.tests.InstantVASSMSAppModuleTestsConfiguration;
import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.SpecializedMOQueueDataBureau;
import mutua.events.TestAdditionalEventServer.ETestAdditionalEventServices;
import mutua.hangmansmsgame.smslogic.SMSProcessor;
import mutua.icc.configuration.ConfigurationManagerTests;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.handlers.IInstrumentationHandler;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerLogConsole;
import mutua.smsappengine.web.AddToMOQueueTests;
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
		System.out.println("                      Use this tool regularly to validate refactorings, performance enhancements and");
		System.out.println("                      ProGuard settings.");
		System.out.println("                      For a correct algorithm analysis, run these tests on a idle system, with an empty database.");
		if ((args.length < 6) || (args.length > 11)) {
			System.out.println("Usage: sudo bash -c 'echo 0 >/proc/sys/vm/swappiness'");
			System.out.println("       java -Xms1408M -Xmx1408M -Xmn50M -Xss228k -XX:ReservedCodeCacheSize=12M -XX:+AlwaysPreTouch -XX:MaxMetaspaceSize=32M");
			System.out.println("            -Xbatch -Xcomp -XX:+AggressiveOpts -Xshare:auto -Xverify:none -XX:-UseHugeTLBFS -XX:+RelaxAccessControlCheck");
			System.out.println("            -XX:+UseAES -XX:+UseAESIntrinsics -XX:+UseCondCardMark -XX:-UseRTMLocking -XX:OnError='echo REPLACE WITH A COMMAND'");
			System.out.println("            -XX:OnOutOfMemoryError='echo REPLACE WITH A COMMAND' -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly");
			System.out.println("            -XX:CMSInitiatingOccupancyFraction=95 -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark -XX:+UseNUMA");
			System.out.println("            -XX:+UseSerialGC -XX:-UseSHM -XX:+UseStringDeduplication -jar");
			System.out.println("            InstantVASTester.jar <postgresql host/ip> <port> <database name> <user> <password>");
			System.out.println("                                 <path and name of the MVStore database file>");
			System.out.println("                                 [number of concurrent database connections]");
			System.out.println("                                 [data access layer -- one of RAM, POSTGRESQL, MVSTORE, MYSQL, EMBEDDED_DERBY]");
			System.out.println("                                 [performance tests load factor -- increase '-Xmx' accordingly]");
			System.out.println("                                 ['true' or 'false' for creating the database model, if necessary (caution!)]");
			System.out.println("                                 ['true' or 'false' for logging queries]");

			System.out.println();
			
			System.exit(1);
		}
		
		// from InstantVASDALTester:
		////////////////////////////
		
		String       hostname                     = args[0];
		int          port                         = Integer.parseInt(args[1]);
		String       database                     = args[2];
		String       user                         = args[3];
		String       password                     = args[4];
		String       mvStoreDatabaseFile          = args[5];
		int          concurrentConnectionsNumber  = args.length >= 7  ? Integer.parseInt(args[6])      : 8;
		String       dal                          = args.length >= 8  ? args[7]                        : "POSTGRESQL";
		int          loadFactor                   = args.length >= 9  ? Integer.parseInt(args[8])      : 42;
		boolean      allowDataStructuresAssertion = args.length >= 10 ? Boolean.parseBoolean(args[9])  : true;
		boolean      shouldDebugQueries           = args.length >= 11 ? Boolean.parseBoolean(args[10]) : false;
		ELogSeverity logSeverity                  = shouldDebugQueries ? ELogSeverity.DEBUG : ELogSeverity.ERROR;
		String       connectionProperties         = PostgreSQLAdapter.CONNECTION_PROPERTIES;

		System.out.println("Configuration:");
		System.out.println("\thostname                    : "+hostname);
		System.out.println("\tport                        : "+port);
		System.out.println("\tdatabase                    : "+database);
		System.out.println("\tuser                        : "+user);
		System.out.println("\tpassword                    : "+password);
		System.out.println("\tMVStore file                : "+mvStoreDatabaseFile);
		System.out.println("\tconcurrentConnectionsNumber : "+concurrentConnectionsNumber);
		System.out.println("\tdal                         : "+dal);
		System.out.println("\tloadFactor                  : "+loadFactor);
		System.out.println("\tallowDataStructuresAssertion: "+allowDataStructuresAssertion);
		System.out.println("\tshouldDebugQueries          : "+shouldDebugQueries);
		System.out.println("\tlogSeverity                 : "+logSeverity);
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
		} else if ("MVSTORE".equals(dal)) {
			baseModuleDAL    = SMSAppModuleDALFactory            .MVSTORE;
			subscriptionDAL  = SMSAppModuleDALFactorySubscription.MVSTORE;
			profileModuleDAL = SMSAppModuleDALFactoryProfile     .MVSTORE;
			chatModuleDAL    = SMSAppModuleDALFactoryChat        .MVSTORE;
			hangmanModuleDAL = SMSAppModuleDALFactoryHangman     .MVSTORE;
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
		if ("POSTGRESQL".equals(dal)) {
			// configure postgreSQL queues
			MutuaEventsAdditionalEventLinksTestsConfiguration   .configureDefaultValuesForNewInstances(loadFactor, -1, -1,           connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		}
		InstantVASSMSAppModuleTestsConfiguration            .configureDefaultValuesForNewInstances(loadFactor, baseModuleDAL,    mvStoreDatabaseFile, connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleHelpTestsConfiguration        .configureDefaultValuesForNewInstances(            baseModuleDAL,                         connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleSubscriptionTestsConfiguration.configureDefaultValuesForNewInstances(loadFactor, subscriptionDAL,  mvStoreDatabaseFile, connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleProfileTestsConfiguration     .configureDefaultValuesForNewInstances(loadFactor, profileModuleDAL, mvStoreDatabaseFile, connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);	
		InstantVASSMSAppModuleChatTestsConfiguration        .configureDefaultValuesForNewInstances(loadFactor, chatModuleDAL,    mvStoreDatabaseFile, connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		InstantVASSMSAppModuleHangmanTestsConfiguration     .configureDefaultValuesForNewInstances(loadFactor, hangmanModuleDAL, mvStoreDatabaseFile, connectionProperties, concurrentConnectionsNumber, allowDataStructuresAssertion, shouldDebugQueries, hostname, port, database, user, password);
		
		
				
		System.out.println("\n### Configuring Instrumentation:");
		new ConfigurationManagerTests();
		new AddToMOQueueTests();
		IInstrumentationHandler log = new InstrumentationHandlerLogConsole(InstantVASTester.class.getCanonicalName(), logSeverity);
		Instrumentation.configureDefaultValuesForNewInstances(log, log, log);

		System.out.println("\n### Instantiating database engines:");
		// MutuaEventsAdditionalEventLinksTestsConfiguration does not have instances
		InstantVASSMSAppModuleTestsConfiguration            .getInstance();
		InstantVASSMSAppModuleHelpTestsConfiguration        .getInstance();
		InstantVASSMSAppModuleSubscriptionTestsConfiguration.getInstance();
		InstantVASSMSAppModuleProfileTestsConfiguration     .getInstance();
		
		// chat module instantiation
		if ("POSTGRESQL".equals(dal)) {
			// assure MO queues table exists -- they are needed for the chat module
			new PostgreSQLQueueEventLink<ETestAdditionalEventServices>(ETestAdditionalEventServices.class, MutuaEventsAdditionalEventLinksTestsConfiguration.ANNOTATION_CLASSES, "MOSMSes", new SpecializedMOQueueDataBureau());
		}
		InstantVASSMSAppModuleChatTestsConfiguration        .getInstance();

		InstantVASSMSAppModuleHangmanTestsConfiguration     .getInstance();
		
		// please, periodically update the classes listed here with the following command, keeping the order implied by the comments:
		// w=~/workspace/celltick/SMSGames/; find "$w" -name "*.java" -exec grep -l 'org.junit.Test' "{}" \; | sed "s|$w[^/]*/src/||" | grep -v main/InstantVASTester.java | sed 's|.java$|.class.getName(),|' | sed 's|/|.|g'
		ArrayList<String> jUnitTestClasses = new ArrayList<String>();
		jUnitTestClasses.addAll(Arrays.asList(new String[] {
			// Mutua libs tests
			adapters.HTTPClientAdapterTest.class.getName(),
			mutua.serialization.SerializationTests.class.getName(),
			mutua.imi.IndirectMethodInvocationTests.class.getName(),
			mutua.events.TestEvents.class.getName(),
			mutua.events.QueueEventLinkTests.class.getName(),
			mutua.events.DirectEventTests.class.getName(),
			//mutua.icc.instrumentation.InstrumentationTests.class.getName(),
			// these seems, currently, not to make sense while obfuscated. Please, verify
/*			mutua.p2pcommunications.P2PServicesManagerTest.class.getName(),*/
			mutua.icc.configuration.ConfigurationManagerTests.class.getName(),
			mutua.icc.configuration.ConfigurationParserTests.class.getName(),
			// Instant VAS application tests
			instantvas.nativewebserver.NativeHTTPServerBehavioralTests.class.getName(),
			mutua.schedule.ScheduleControlBehavioralTests.class.getName(),
			mutua.smsappengine.logic.HangmanAppEngineBehavioralTests.class.getName(),
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
		}));
		
		// only include PostgreSQLQueue tests if we are using the PostgreSQL DAL
		if ("POSTGRESQL".equals(dal)) {
			jUnitTestClasses.addAll(Arrays.asList(new String[] {
				// SMS Modules DAL performance tests
				mutua.events.PostgreSQLQueueEventLinkTests.class.getName(),
				mutua.events.PostgreSQLQueueEventLinkPerformanceTests.class.getName(),
			}));
		}
		
		jUnitTestClasses.addAll(Arrays.asList(new String[] {
			// Additional Events DAL performance tests
			mutua.smsappmodule.dal.IUserDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.ISessionDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.ISubscriptionDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.IProfileDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.IChatDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.IMatchDBPerformanceTests.class.getName(),
			mutua.smsappmodule.dal.INextBotWordsDBPerformanceTests.class.getName(),
		}));

		System.out.println("Running the tests:");
		org.junit.runner.JUnitCore.main(jUnitTestClasses.toArray(new String[0]));
	}
}
