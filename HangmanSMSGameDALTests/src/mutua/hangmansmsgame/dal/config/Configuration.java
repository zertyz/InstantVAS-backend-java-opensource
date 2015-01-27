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
	public static String  POSTGRESQL_CONNECTION_HOSTNAME      = "zertyz.heliohost.org";
	public static int     POSTGRESQL_CONNECTION_PORT          = 5432;
	public static String  POSTGRESQL_CONNECTION_DATABASE_NAME = "zertyz_spikes";
	public static String  POSTGRESQL_CONNECTION_USER          = "zertyz_user";
	public static String  POSTGRESQL_CONNECTION_PASSWORD      = "spikes";
	public static String  POSTGRES_CONNECTION_PROPERTIES      = "";

	
	static {
		log = new Instrumentation<DefaultInstrumentationProperties, String>("HangmanSMSGameDALTests", DIP_MSG, JDBCAdapterInstrumentationEvents.values());
    	try {
        	InstrumentationProfilingEventsClient instrumentationProfilingEventsClient = new InstrumentationProfilingEventsClient(log, EInstrumentationDataPours.CONSOLE);
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}
    	
    	// set the vars
    	///////////////
    	
    	HangmanSMSGamePostgreSQLAdapters.log = log;
		HangmanSMSGamePostgreSQLAdapters.HOSTNAME = POSTGRESQL_CONNECTION_HOSTNAME;
		HangmanSMSGamePostgreSQLAdapters.PORT     = POSTGRESQL_CONNECTION_PORT;
		HangmanSMSGamePostgreSQLAdapters.DATABASE = POSTGRESQL_CONNECTION_DATABASE_NAME;
		HangmanSMSGamePostgreSQLAdapters.USER     = POSTGRESQL_CONNECTION_USER;
		HangmanSMSGamePostgreSQLAdapters.PASSWORD = POSTGRESQL_CONNECTION_PASSWORD;

    	DALFactory.DEFAULT_DAL = DEFAULT_DAL;
	}
	
}
