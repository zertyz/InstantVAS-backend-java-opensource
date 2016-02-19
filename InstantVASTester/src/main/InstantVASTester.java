package main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import adapters.PostgreSQLAdapter;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dal.SMSAppModuleDALFactorySubscription;

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
	
	// works with classpaths and jars as well
	// before populating with the default class (""), populate with a class name for the jar version to work around
	private static URL lastPackageURL = null;
	public static void populateClassNamesFromPackage(ArrayList<String> names, String packageName) throws IOException, URISyntaxException{
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	    packageName = packageName.replace(".", "/");
	    
	    Enumeration<URL> packageURLs = classLoader.getResources(packageName);
	    while (packageURLs.hasMoreElements()) {
	    	URL packageURL = packageURLs.nextElement();
	    
		    // workarround for default packages on jars
		    if (packageName.equals("")) {
		    	if (lastPackageURL == null) {
		    		throw new RuntimeException("in order to access the default package, you must first access a non-default package, since doing this "+
		    		                           "within .jars needs this workarround to work");
		    	} else if (lastPackageURL.getProtocol().equals("jar")){
		    		packageURL = lastPackageURL;
		    	}
		    }
		    lastPackageURL = packageURL;
		    
		    if(packageURL.getProtocol().equals("jar")){
		        String jarFileName;
		        JarFile jf ;
		        Enumeration<JarEntry> jarEntries;
		        String entryName;
	
		        // build jar file name, then loop through zipped entries
		        jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
		        jarFileName = jarFileName.substring(5,jarFileName.indexOf("!"));
		        //System.out.println(">"+jarFileName);
		        jf = new JarFile(jarFileName);
		        jarEntries = jf.entries();
		        while(jarEntries.hasMoreElements()){
		            entryName = jarEntries.nextElement().getName();
		            if(entryName.startsWith(packageName) && entryName.endsWith(".class") && (entryName.replaceAll("^"+packageName+"/(.*)\\.class", "$1").indexOf('/') == -1)){
		            	entryName = entryName.replaceAll("\\.class$", "");
		                names.add(entryName.replaceAll("/", "."));
		            }
		        }
	
		    // loop through files in classpath
		    }else{
		    URI uri = new URI(packageURL.toString());
		    File folder = new File(uri.getPath());
		        // won't work with path which contains blank (%20)
		        // File folder = new File(packageURL.getFile()); 
		        File[] contenuti = folder.listFiles();
		        String entryName;
		        for(File actual: contenuti){
		            entryName = actual.getName();
		            if (entryName.endsWith(".class")) {
		            	entryName = entryName.replaceAll("\\.class$", "");
	            		names.add(packageName.replaceAll("/", ".")+(packageName.length() > 0 ? "." : "")+entryName);
	            	}
		        }
		    }
	    }
	}
	
	public static boolean doesClassForNameHasAnAnnotatedMethod(String className, Class annotation) throws ClassNotFoundException {
		Class clazz = Class.forName(className);
		for (Method method : clazz.getMethods()) {
		    if (method.isAnnotationPresent(annotation)) {
		    	return true;
		    }
		}
		return false;
	}
	
	public static String[] searchJUnitTestClasses() throws ClassNotFoundException, IOException, URISyntaxException {
		String[] packages = {
			"mutua.events",
			"mutua.imi",
			"mutua.serialization",
			"mutua.icc.configuration",
			"mutua.icc.instrumentation",
			"mutua.smsappmodule.smslogic.sessions",
			"mutua.smsappmodule",
			"mutua.smsappengine.logic",
			"instantvas.nativewebserver",
			"mutua.schedule",
			"mutua.smsappmodule.dal",
			""	
		};
		
	    // populate classes
	    ArrayList<String> classNames = new ArrayList<String>();
	    for (String packageName : packages) {
	    	populateClassNamesFromPackage(classNames, packageName);
	    }

	    // filter
	    ArrayList<String> testClasses = new ArrayList<String>();
		for (String className : classNames) {
			if (doesClassForNameHasAnAnnotatedMethod(className, org.junit.Test.class)) {
				testClasses.add(className);
				System.out.println("testClass: " + className);
			}
		}
		
		return testClasses.toArray(new String[0]);
	}


	public static void main(String[] args) throws ClassNotFoundException, IOException, URISyntaxException {
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
			System.out.println("Searching JUnit test classes:");
			searchJUnitTestClasses();

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
	}

}
