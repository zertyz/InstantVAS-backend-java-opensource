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

	public static final Instrumentation<TestInstrumentationRequestProperty, String> log;
	
	public static EDataAccessLayers DEFAULT_DAL = EDataAccessLayers.POSTGRESQL;

	static {
		log = new Instrumentation<TestInstrumentationRequestProperty, String>("HangmanSMSGameProcessorTests", new TestInstrumentationRequestProperty(), HangmanSMSGameInstrumentationEvents.values());
    	try {
        	InstrumentationProfilingEventsClient instrumentationProfilingEventsClient = new InstrumentationProfilingEventsClient(log, EInstrumentationDataPours.CONSOLE);
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}
    	
    	// set the vars
    	mutua.hangmansmsgame.config.Configuration.log = log;
    	mutua.hangmansmsgame.config.Configuration.SUBSCRIPTION_ENGINE = new TestableSubscriptionAPI(log);
    	HangmanSMSGamePostgreSQLAdapters.log = log;
    	DALFactory.DEFAULT_DAL = DEFAULT_DAL;
	}

}