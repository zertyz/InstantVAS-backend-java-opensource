package instantvas.nativewebserver;

import static config.InstantVASLicense.*;
import static config.MutuaHardCodedConfiguration.*;
import instantvas.smsengine.producersandconsumers.EInstantVASEvents;
import instantvas.smsengine.producersandconsumers.MOConsumer;
import instantvas.smsengine.producersandconsumers.MOProducer;
import instantvas.smsengine.producersandconsumers.MTConsumer;
import instantvas.smsengine.producersandconsumers.MTProducer;
import instantvas.smsengine.producersandconsumers.SCConsumer;
import instantvas.smsengine.producersandconsumers.SCProducer;
import instantvas.smsengine.producersandconsumers.SRConsumer;
import instantvas.smsengine.producersandconsumers.SRProducer;
import instantvas.smsengine.web.AddToMOQueue;
import mutua.events.EventClient;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsin.parsers.SMSInParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.*;

import adapters.HTTPClientAdapter;
import config.InstantVASInstanceConfiguration;

/** <pre>
 * NativeHTTPServer.java
 * =====================
 * (created by luiz, Dec 21, 2015)
 *
 * This class' purpose is to test the average speed of the native http server implementation.
 * 
 * For a process started like this on an interserver VPS 1 slot server (1GiB RAM, 1/4 of a CPU, 25GiB disk with 30MiB/s):
 * 	/tmp/jdk1.8.0_66/bin/java -Xms884M -Xmx884M -Xmn50M -Xss228k -XX:ReservedCodeCacheSize=6M -XX:+AlwaysPreTouch -XX:MaxMetaspaceSize=6M
 *                            -Xbatch -Xcomp -XX:+AggressiveOpts -Xshare:auto -Xverify:none -XX:-UseHugeTLBFS -XX:+RelaxAccessControlCheck
 *                            -XX:+UseAES -XX:+UseAESIntrinsics -XX:+UseCondCardMark -XX:-UseRTMLocking -XX:OnError="shutdown -r now"
 *                            -XX:OnOutOfMemoryError="shutdown -r now" -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly
 *                            -XX:CMSInitiatingOccupancyFraction=95 -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark -XX:+UseNUMA
 *                            -XX:+UseSerialGC -XX:-UseSHM -XX:+UseStringDeduplication -jar /tmp/InterserverMemoryManagement.jar 645 300000000
 * Note: it could run even faster on a WEB hosting machine, but this machines don't accept external connections
 * 
 * The following test achieve the following results on a jmeter session conducted on my laptop over a wi-fi connection:
 *  -- 20 server threads, 80 client threads (my wified lap -- min 317ms), 203,2/sec, ~.85 5min load --> 3 sec max for an answer
 *  -- 20 server threads, 100 client threads (idem), 245/sec, ~1.08 5min load --> 5,1 sec max for an answer
 *  -- same thing but without an SSH connection (java started in rc.local) --> 256/sec, ~0.83 5min load --> 4,2sec max for an answer
 *  -- 20 server threads, 120 client threads (min 281ms), 320/sec (with 360 peaks), ~0.81 5min load --> 4,6sec max for an answer
 *  -- with 150 client threads it appears I reached my BW limit (400/sec peaks and 250/sec valleys)
 *  
 *  The future of a embedded web server seems to be a home made implementation in 'MutuaGPP2PCommunications', specially
 *  designed for short textual SMS exchange with predictable answers, to avoid the big garbage generated by the available implementations.
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class NativeHTTPServer {
	
	public static InstantVASInstanceConfiguration ivac;
	public static AddToMOQueue addToMOQueue;
	
	// AddToMOQueue
	public static SMSInParser<Map<String, String>, byte[]> moParser;
	public static MTProducer                               mtProducer;
	public static EventClient<EInstantVASEvents>           moConsumer;
	public static MOProducer                               moProducer;
	public static SRProducer                               srProducer;
	public static SRConsumer                               srConsumer;
	public static SCProducer                               scProducer;
	public static SCConsumer                               scConsumer;
	
	public static void instantiate() throws IllegalArgumentException, SecurityException, SQLException, IllegalAccessException, NoSuchFieldException, UnsupportedEncodingException, FileNotFoundException {
		ivac       = new InstantVASInstanceConfiguration();
		
		// TODO 18/5/2016 -- corrigir a arquitetura de Instancias: o código abaixo é válido para somente 1 instância.
		//      As instâncias devem ser carregadas (configuradas) via definição em Licenses, onde um campo
		//      chamado "InstantiationStyle" poderia ter os valores "preload", "lazyload", ou "cacheable"
		//      onde o preload incorporaria somente código otimizado (com IFDEFs) e os outros, tanto faz.
		
		moParser   = ivac.moParser;
		mtProducer = new MTProducer(ivac, new MTConsumer(ivac));
		moConsumer = new MOConsumer(ivac, mtProducer);
		moProducer = new MOProducer(ivac, moConsumer);
		srConsumer = new SRConsumer(ivac);
		srProducer = new SRProducer(ivac, srConsumer);
		scConsumer = new SCConsumer(ivac);
		scProducer = new SCProducer(ivac, scConsumer);

		addToMOQueue = new AddToMOQueue(moProducer, moParser,
		                                ALLOWABLE_MSISDN_MIN_LENGTH,
		                                ALLOWABLE_MSISDN_MAX_LENGTH,
		                                ALLOWABLE_MSISDN_PREFIXES,
		                                ALLOWABLE_CARRIERS,
		                                ALLOWABLE_SHORT_CODES);
	}
	
	public static void main(String[] args) {
		try {
			InstantVASConfigurationLoader.applyConfigurationFromLicenseClass();
			instantiate();
		} catch (Throwable t) {
			t.printStackTrace();
			return;
		}
		
		// use the active MO fecthing mechanism?
		if (MO_ACQUISITION_METHOD == MOAcquisitionMethods_ACTIVE_HTTP_QUEUE_CLIENT) {
			// debug
			if (IFDEF_WEB_DEBUG) {
				Instrumentation.reportDebug("Starting the Active HTTP Queue Client for the service '"+MO_ACTIVE_HTTP_QUEUE_BASE_URL+
				                            "', with a maximum fetch of "+MO_ACTIVE_HTTP_QUEUE_BATCH_SIZE+" elements at a time and "+
						                    "with no more than 1 request every "+MO_ACTIVE_HTTP_QUEUE_POOLING_DELAY+" milliseconds");
			}
			new Thread() {
				
				@Override
				public void run() {
					try {
						HTTPClientAdapter httpQueueClient = new HTTPClientAdapter(MO_ACTIVE_HTTP_QUEUE_BASE_URL);
						
						// check file existence & permissions
						File f = new File(MO_ACTIVE_HTTP_QUEUE_LOCAL_OFFSET_FILE);
						if (f.exists() == false) {
							/* debug */ if (IFDEF_WEB_DEBUG) {Instrumentation.reportDebug("ActiveMOFetcher: File '"+f.getAbsolutePath()+"' does not exist. Creating...");}
							f.createNewFile();
						}
						if (!f.canWrite()) {
							throw new RuntimeException("ActiveMOFetcher cannot write to the local offset file '"+f.getAbsolutePath()+"'. Aborting, since we cannot keep track of the last MO processed after this application is restarted.");
						}
						
						// open the file & check it's consistency
						RandomAccessFile raf = new RandomAccessFile(f, "rw");
						if (raf.length() != 8)  {
							/* debug */ if (IFDEF_WEB_DEBUG) {Instrumentation.reportDebug("ActiveMOFetcher: Invalid offset pointer found on file '"+f.getAbsolutePath()+"'. Recreating...");}
							raf.writeLong(0);
							raf.seek(0);
						}
						long localOffset = raf.readLong();
						long remoteFileInitialLength = Long.parseLong(httpQueueClient.requestGetWithAlreadyEncodedValues("offset", "-1"));
						/* debug */ if (IFDEF_WEB_DEBUG) {Instrumentation.reportDebug("ActiveMOFetcher: Starting active fetcher from offset "+localOffset+" to a known number of "+remoteFileInitialLength);}
						while (true) {
							// fetch the MO queryString parameters (1 one each line), with the last line telling the next offset to pass along to keep fetching the sequence
							String contents = httpQueueClient.requestGetWithAlreadyEncodedValues("offset", Long.toString(localOffset), "lines", MO_ACTIVE_HTTP_QUEUE_BATCH_SIZE);
							String[] lines = contents.split("\n");
							if (lines.length > 1) {
								String[] queryStrings = new String[lines.length-1];	// the last line is the new offset to be used on the subsequente http get
								// enqueue the MOs
								for (int i=0; i<queryStrings.length; i++) {
									String[] httpQueueData = lines[i].split("§");
									String remoteAddr      = httpQueueData[0];
									String queryString     = httpQueueData[2];
									queryStrings[i] = queryString;
								}
								// the new 'localOffset'
								localOffset = Long.parseLong(lines[lines.length-1]);
								raf.seek(0);
								raf.writeLong(localOffset);
								// batch process
								addToMOQueue.processRequests(queryStrings);
							}
							sleep(MO_ACTIVE_HTTP_QUEUE_POOLING_DELAY);
						}
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}.start();
		}
		
		try {
			startServer(NATIVE_HTTP_SERVER_PORT, NATIVE_HTTP_SOCKET_BACKLOG_QUEUE_SLOTS, InstantVASSMSWebHandlers.values());
		} catch (Throwable t) {
			t.printStackTrace();
			return;
		}
		Instrumentation.reportDebug("InstantVAS Internal :"+NATIVE_HTTP_SERVER_PORT+" server started. Requests may now commence.");
		/* debug */ if (IFDEF_WEB_DEBUG) {Instrumentation.reportDebug("Registered services: "+Arrays.deepToString(InstantVASSMSWebHandlers.values()));}
	}
	
	public static void startServer(int port, int socketBacklogQueueSlots, INativeHTTPServerHandler[]... handlerArrays) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), socketBacklogQueueSlots);
		for (INativeHTTPServerHandler[] handlers : handlerArrays) {
			for (INativeHTTPServerHandler handler : handlers) {
				server.createContext(handler.getContextPath(), handler);
			}
		}
		server.setExecutor(Executors.newFixedThreadPool(NATIVE_HTTP_NUMBER_OF_THREADS));
		server.start();
	}
	
	private static WeakHashMap<Thread, HashMap<String, String>> parametersMaps = new WeakHashMap<Thread, HashMap<String, String>>(NATIVE_HTTP_NUMBER_OF_THREADS);
	
	public static synchronized HashMap<String, String> getParametersHash() {
		HashMap<String, String> parameters = parametersMaps.get(Thread.currentThread());
		if (parameters == null) {
			parameters = new HashMap<String, String>(NATIVE_HTTP_PARAMETERS_HASH_CAPACITY);
			parametersMaps.put(Thread.currentThread(), parameters);
		}
		return parameters;
	}
	
	public static HashMap<String, String> retrieveGetParameters(String queryString) throws UnsupportedEncodingException {
		
		HashMap<String, String> parameters = getParametersHash();
		parameters.clear();
		
		int nextTokenStart = 0;
		int nextTokenEnd   = -1;
		
		String parameterName  = "";
		String parameterValue = "";
		
		while (true) try {
			// gathering parameter name state
			nextTokenEnd = queryString.indexOf('=', nextTokenStart);
			if (nextTokenEnd == -1) {
				parameterName = queryString.substring(nextTokenStart).intern();
				break;
			}
			// note: for performance reasons, we: 1) do not 'URLDecoder' parameter names; 2) use .intern() for parameter names minimize the heap strings
			parameterName = queryString.substring(nextTokenStart, nextTokenEnd).intern();
			nextTokenStart = nextTokenEnd+1;		// skip the '=' character

			// gathering parameter value state
			nextTokenEnd = queryString.indexOf('&', nextTokenStart);
			if (nextTokenEnd == -1) {
				parameterValue = URLDecoder.decode(queryString.substring(nextTokenStart), "UTF-8");
				break;
			}
			parameterValue = URLDecoder.decode(queryString.substring(nextTokenStart, nextTokenEnd), "UTF-8");
			nextTokenStart = nextTokenEnd+1;		// skip the '&' character
		} finally {
			parameters.put(parameterName, parameterValue);
		}

		return parameters;
	}
	
	/** By "strict", we mean:
	 *  1. Parameters are only matched if their order in the 'queryString' corresponds to the order in 'parameterNames'
	 *  2. Names are case sensitive
	 *  3. 'queryString' parameters before the first element in 'parameterNames' are ignored
	 *  4. the same happens to 'queryString' parameters after the last element in 'parameterNames'.
	 *  Returns an array of string containing parameter values extracted from 'queryString' in the order defined in 'parameterNames'. */
	public static String[] retrieveStrictGetParameters(String[] parameterNames, String queryString) throws UnsupportedEncodingException {
		
		String[] parameterValues = new String[parameterNames.length];
		
		int nextTokenStart = 0;
		int nextTokenEnd   = -1;
		
		int i=0;
		while (i<parameterValues.length) {
			String expectedParameterName = parameterNames[i];
			String parameterValue;
			String parameterName;
			// gathering parameter name state
			nextTokenEnd = queryString.indexOf('=', nextTokenStart);
			if (nextTokenEnd == -1) {
				break;
			}
			// note: for performance reasons, we do not 'URLDecoder' parameter names
			parameterName = queryString.substring(nextTokenStart, nextTokenEnd);
			nextTokenStart = nextTokenEnd+1;		// skip the '=' character

			// gathering parameter value state
			nextTokenEnd = queryString.indexOf('&', nextTokenStart);
			if (nextTokenEnd == -1) {
				if (expectedParameterName.equals(parameterName)) {
					parameterValue = URLDecoder.decode(queryString.substring(nextTokenStart), "UTF-8");
					parameterValues[i++] = parameterValue;
				}
				break;
			} else {
				if (expectedParameterName.equals(parameterName)) {
					parameterValue = URLDecoder.decode(queryString.substring(nextTokenStart, nextTokenEnd), "UTF-8");
					parameterValues[i++] = parameterValue;
				}
				nextTokenStart = nextTokenEnd+1;		// skip the '&' character
			}
		}

		return parameterValues;
	}

	
	public enum InstantVASSMSWebHandlers implements INativeHTTPServerHandler {
				
		ADD_TO_MO_QUEUE("/AddToMOQueue") {
			
			@Override
			public void handle(HttpExchange he) {
				try {
					byte[] response;
					String queryString = he.getRequestURI().getRawQuery();
					
					// TODO 18/5/2016 -- corrigir a arquitetura de Instancias: a instancia determina a criação dos pares AddToMOQueue / Process MT & MO mas
					// a verificação do token só é feita dentro de AddToMOQueue#attemptToAuthenticateFromStrictGetParameters. Deve haver um método
					// intermediário (que funciona aqui e por tomcat... talvez chamado MOReceiverRouter) que desempenhe tal função.

					response = addToMOQueue.processRequest(queryString);

					he.sendResponseHeaders(200, response.length);
					OutputStream os = he.getResponseBody();
					os.write(response);
					os.close();		// keep-alive connections may be tested with curl -v 'http://localhost:8080/AddToMOQueue' 'http://localhost:8080/AddToMOQueue'
				} catch (Throwable t) {
					Instrumentation.reportThrowable(t, "Error processing request /AddToMOQueue: "+he.getRequestURI().getRawQuery());
				}
			}
		},
		
		RELOAD_CONFIGURATION("/ReloadConfiguration") {
			
			@Override
			public void handle(HttpExchange he) throws IOException {
				byte[] response;
				try {
					InstantVASConfigurationLoader.applyConfigurationFromLicenseClass();
					instantiate();
					response = "RELOADED".intern().getBytes();
				} catch (Throwable t) {
					System.err.println("Error reloading configuration");
					t.printStackTrace();
					Instrumentation.reportThrowable(t, "Error reloading configuration");
					response = "FAILED".intern().getBytes();
				}
				he.sendResponseHeaders(200, response.length);
				he.getResponseBody().write(response);
		        he.close();
			}
		}
		
		// TODO Subscribe, Unsubscribe, ...
		
		;
		
		private String contextPath;
		
		private InstantVASSMSWebHandlers(String contextPath) {
			this.contextPath = contextPath;
		}
		
		public String getContextPath() {
			return contextPath;
		}
		
//		private static WeakHashMap<Thread, byte[]> inputBuffers = new WeakHashMap<Thread, byte[]>(NUMBER_OF_THREADS);
//		
//		public static synchronized byte[] getInputBuffer() {
//			byte[] inputBuffer = inputBuffers.get(Thread.currentThread());
//			if (inputBuffer == null) {
//				inputBuffer = new byte[INPUT_BUFFER_SIZE];
//				inputBuffers.put(Thread.currentThread(), inputBuffer);
//			}
//			return inputBuffer;
//		}
//		
//		public static int readNextChunk(InputStream is, byte[] inputBuffer, int currentLength) throws Exception {
//			int available = is.available();
//			if ((available == 0) && (READ_TIMEOUT > 0)) {
//				Thread.sleep(READ_TIMEOUT);
//			}
//			if ((available + currentLength) > INPUT_BUFFER_SIZE) {
//				return -2;
//			}
//			is.read(inputBuffer, currentLength, available);
//			return 0;
//		}
		
	}
		
}
