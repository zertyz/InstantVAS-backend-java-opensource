package config;

import static org.junit.Assert.*;
import instantvas.nativewebserver.InstantVASConfigurationLoader;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.handlers.IInstrumentationHandler;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerLogConsole;

import org.junit.Test;

/** <pre>
 * InstantVASInstanceConfigurationTests.java
 * =========================================
 * (created by luiz, May 18, 2016)
 *
 * Tests the configuration for instant vas application instances
 *
 * @see InstantVASInstanceConfiguration
 * @version $Id$
 * @author luiz
 */

public class InstantVASInstanceConfigurationTests {

	@Test
	public void testTemporaryLoggingMechanism() {
		InstantVASConfigurationLoader.setTemporaryLog();
		Instrumentation.reportDebug("This should appear at the console, as soon as I set the definitive logs");
		IInstrumentationHandler logHandler = new InstrumentationHandlerLogConsole("TestApp", ELogSeverity.DEBUG);
		Instrumentation.configureDefaultValuesForNewInstances(logHandler, null, null);
		InstantVASConfigurationLoader.purgeTemporaryLog(logHandler);
		Instrumentation.reportDebug("This one shuld appear just after");
	}

}
