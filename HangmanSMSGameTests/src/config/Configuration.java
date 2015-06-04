package config;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.DALFactory.EDataAccessLayers;
import mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters;
import mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.TestInstrumentationRequestProperty;
import mutua.icc.instrumentation.eventclients.InstrumentationProfilingEventsClient;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.subscriptionengine.TestableSubscriptionAPI;
import static mutua.hangmansmsgame.config.Configuration.log;

/** <pre>
 * Configuration.java
 * ==================
 * (created by luiz, Jan 27, 2015)
 *
 * Defines common variables for the application
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class Configuration {

	public static EDataAccessLayers DEFAULT_DAL = EDataAccessLayers.RAM;

	static {
    	// set the vars
    	mutua.hangmansmsgame.config.Configuration.DATA_ACCESS_LAYER = DEFAULT_DAL;
    	mutua.hangmansmsgame.config.Configuration.SESSIONS_DATA_ACCESS_LAYER = DEFAULT_DAL;
    	DALFactory.DEFAULT_DAL = DEFAULT_DAL;
    	
    	try {
			mutua.hangmansmsgame.config.Configuration.applyConfiguration();
		} catch (Throwable t) {
			t.printStackTrace();
			log.reportThrowable(t, "Error applying Configuration");
		}

    	mutua.hangmansmsgame.config.Configuration.SUBSCRIPTION_ENGINE = new TestableSubscriptionAPI(log);
    	HangmanSMSGamePostgreSQLAdapters.log = log;
	}

}