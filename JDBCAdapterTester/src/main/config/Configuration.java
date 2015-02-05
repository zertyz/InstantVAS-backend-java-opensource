package main.config;

import static mutua.icc.instrumentation.DefaultInstrumentationProperties.DIP_MSG;
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
 * Defines common configuration variables for the application
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class Configuration {

	public static final Instrumentation<DefaultInstrumentationProperties, String> log;
	
	static {
		log = new Instrumentation<DefaultInstrumentationProperties, String>("JDBCAdapterTester", DIP_MSG,
				EInstrumentationDataPours.CONSOLE, null, JDBCAdapterInstrumentationEvents.values());
    	try {
        	InstrumentationProfilingEventsClient instrumentationProfilingEventsClient = new InstrumentationProfilingEventsClient(log, EInstrumentationDataPours.CONSOLE, null);
			log.addInstrumentationPropagableEventsClient(instrumentationProfilingEventsClient);
		} catch (IndirectMethodNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
