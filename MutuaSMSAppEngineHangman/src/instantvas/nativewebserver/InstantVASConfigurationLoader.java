package instantvas.nativewebserver;

import static config.InstantVASLicense.*;
import static config.MutuaHardCodedConfiguration.*;

import java.io.IOException;
import java.util.ArrayList;

import config.InstantVASInstanceConfiguration;
import instantvas.smsengine.InstantVASHTTPInstrumentationRequestProperty;
import mutua.icc.configuration.ConfigurationManager;
import mutua.icc.instrumentation.InstrumentableEvent;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.dto.InstrumentationEventDto;
import mutua.icc.instrumentation.handlers.IInstrumentationHandler;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerRAM;

/** <pre>
 * InstantVASConfigurationLoader.java
 * ==================================
 * (created by luiz, Apr 7, 2016)
 *
 * Uses 'MutuaICCConfiguration' module to apply new values for the
 * static fields of 'InstantVASInstanceConfiguration' class
 *
 * @version $Id$
 * @author luiz
*/

public class InstantVASConfigurationLoader {

	private static InstrumentationEventDto[]   temporaryLoggedEvents;
	//private static final Instrumentation<?, ?> log = new Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String>("InstantVAS Configuration Loader", new InstantVASHTTPInstrumentationRequestProperty(), EInstrumentationDataPours.CONSOLE, "");
	
	private static void setTemporaryLog() {
		IInstrumentationHandler ramLogger = new InstrumentationHandlerRAM() {
			public void analyzeRequest(ArrayList<InstrumentationEventDto> requestEvents) {
				temporaryLoggedEvents = requestEvents.toArray(new InstrumentationEventDto[0]);
			}
			public void close() {
				onRequestFinish(new InstrumentationEventDto(System.currentTimeMillis(), Thread.currentThread(),
					new InstrumentableEvent("Reconfiguring Instrumentation", ELogSeverity.CRITICAL)));
			}
		};
		Instrumentation.configureDefaultValuesForNewInstances(ramLogger, ramLogger, ramLogger);
	}
	
	public static void applyConfigurationFromString(String configurationContents) {
		
	}
	
	public static void applyConfigurationFromPlainFSFile(String fsFilePath) throws IllegalArgumentException, IOException, IllegalAccessException {
		setTemporaryLog();
		ConfigurationManager cm = new ConfigurationManager(InstantVASInstanceConfiguration.class);
		cm.loadFromFile(fsFilePath);
		InstantVASInstanceConfiguration.applyConfiguration(temporaryLoggedEvents);
	}
	
	public static void applyConfigurationFromLicenseClass() throws IllegalArgumentException, IOException, IllegalAccessException {
		
		/* debug */ if (IFDEF_CONFIG_DEBUG) {
			Instrumentation.reportDebug("Applying Configuration from the License Class:");
			Instrumentation.reportDebug("INSTANTVAS_INSTANCES_SOURCE_TYPE := " + (
				INSTANTVAS_INSTANCES_SOURCE_TYPE == ConfigurationSourceType_HARDCODED     ? "ConfigurationSourceType_HARDCODED"     : (
				INSTANTVAS_INSTANCES_SOURCE_TYPE == ConfigurationSourceType_RESOURCE      ? "ConfigurationSourceType_RESOURCE"      : (
				INSTANTVAS_INSTANCES_SOURCE_TYPE == ConfigurationSourceType_ENC_FS_FILE   ? "ConfigurationSourceType_ENC_FS_FILE"   : (
				INSTANTVAS_INSTANCES_SOURCE_TYPE == ConfigurationSourceType_PLAIN_FS_FILE ? "ConfigurationSourceType_PLAIN_FS_FILE" : (
				INSTANTVAS_INSTANCES_SOURCE_TYPE == ConfigurationSourceType_HTTP          ? "ConfigurationSourceType_HTTP"          : (
				INSTANTVAS_INSTANCES_SOURCE_TYPE == ConfigurationSourceType_POSTGRESQL    ? "ConfigurationSourceType_POSTGRESQL"    : (
				"Unknown value '"+INSTANTVAS_INSTANCES_SOURCE_TYPE+"'"))))))));
			Instrumentation.reportDebug("INSTANTVAS_INSTANCE_CONFIGn_LENGTH := " + INSTANTVAS_INSTANCE_CONFIGn_LENGTH);
		}
		
		if ((INSTANTVAS_INSTANCES_SOURCE_TYPE == ConfigurationSourceType_HARDCODED) && (INSTANTVAS_INSTANCE_CONFIGn_LENGTH > 0)) {
			if (INSTANTVAS_INSTANCE_CONFIG0_TYPE == ConfigurationSourceType_PLAIN_FS_FILE) {
				/* debug */ if (IFDEF_CONFIG_DEBUG) {Instrumentation.reportDebug("Loading Hard-Coded Instance 0 configuration from Plain FS File '"+INSTANTVAS_INSTANCE_CONFIG0_ACCESS_INFO+"'");}
				applyConfigurationFromPlainFSFile(INSTANTVAS_INSTANCE_CONFIG0_ACCESS_INFO);
			}
		} else {
			/* debug */ if (IFDEF_CONFIG_DEBUG) {throw new RuntimeException("InstantVASConfigurationLoader ERROR: No detectable license configuration was found");}
		}
		
	}

}
