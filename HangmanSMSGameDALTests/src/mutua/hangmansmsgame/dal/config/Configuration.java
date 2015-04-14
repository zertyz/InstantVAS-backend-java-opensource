package mutua.hangmansmsgame.dal.config;

import static mutua.icc.instrumentation.DefaultInstrumentationProperties.DIP_MSG;
import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.DALFactory.EDataAccessLayers;
import mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters;
import mutua.icc.instrumentation.DefaultInstrumentationProperties;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.JDBCAdapterInstrumentationEvents;
import mutua.icc.instrumentation.eventclients.InstrumentationProfilingEventsClient;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;

/** <pre>
 * Configuration.java
 * ==================
 * (created by luiz, Jan 26, 2015)
 *
 * Defines common variables for the application
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class Configuration {
	
	public static final Instrumentation<DefaultInstrumentationProperties, String> log;
	
	public static EDataAccessLayers DEFAULT_DAL = EDataAccessLayers.POSTGRESQL;
	public static Boolean POSTGRES_DEBUG_QUERIES              = true;
	public static String  POSTGRESQL_CONNECTION_HOSTNAME      = "venus";
	public static int     POSTGRESQL_CONNECTION_PORT          = 5432;
	public static String  POSTGRESQL_CONNECTION_DATABASE_NAME = "hangman";
	public static String  POSTGRESQL_CONNECTION_USER          = "hangman";
	public static String  POSTGRESQL_CONNECTION_PASSWORD      = "hangman";
	public static String  POSTGRESQL_CONNECTION_PROPERTIES    = "characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true&connectTimeout=10000&socketTimeout=10000";

	
	static {
		log = new Instrumentation<DefaultInstrumentationProperties, String>("HangmanSMSGameDALTests", DIP_MSG,
				EInstrumentationDataPours.CONSOLE, "/tmp/coco", JDBCAdapterInstrumentationEvents.values());
    	try {
        	InstrumentationProfilingEventsClient instrumentationProfilingEventsClient = new InstrumentationProfilingEventsClient(log, EInstrumentationDataPours.CONSOLE);
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}
    	
    	// set the vars
    	///////////////
    	
    	HangmanSMSGamePostgreSQLAdapters.log = log;
		HangmanSMSGamePostgreSQLAdapters.HOSTNAME              = POSTGRESQL_CONNECTION_HOSTNAME;
		HangmanSMSGamePostgreSQLAdapters.PORT                  = POSTGRESQL_CONNECTION_PORT;
		HangmanSMSGamePostgreSQLAdapters.DATABASE              = POSTGRESQL_CONNECTION_DATABASE_NAME;
		HangmanSMSGamePostgreSQLAdapters.USER                  = POSTGRESQL_CONNECTION_USER;
		HangmanSMSGamePostgreSQLAdapters.PASSWORD              = POSTGRESQL_CONNECTION_PASSWORD;
		HangmanSMSGamePostgreSQLAdapters.CONNECTION_PROPERTIES = POSTGRESQL_CONNECTION_PROPERTIES;

    	DALFactory.DEFAULT_DAL          = DEFAULT_DAL;
    	DALFactory.DEFAULT_SESSIONS_DAL = DEFAULT_DAL;
	}
	
}
