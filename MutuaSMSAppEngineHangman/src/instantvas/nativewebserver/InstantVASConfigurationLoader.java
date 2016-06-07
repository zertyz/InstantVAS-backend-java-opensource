package instantvas.nativewebserver;

import static config.InstantVASLicense.*;
import static config.MutuaHardCodedConfiguration.*;

import java.io.IOException;
import java.util.ArrayList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import config.InstantVASInstanceConfiguration;
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
	
	// INSTANCES DEFINITIONS LOADING METHODS
	////////////////////////////////////////
	
	private static void loadInstanceDefinitionsFromPlainFSFile(String instancesDefinitionFile) {
		throw new NotImplementedException();
	}

	// INSTANCE CONFIGURATION LOADING METHODS
	/////////////////////////////////////////

	public static void applyConfigurationFromString(String configurationContents) {
		
	}
	
	public static void applyConfigurationFromPlainFSFile(String fsFilePath) throws IllegalArgumentException, IOException, IllegalAccessException {
		ConfigurationManager cm = new ConfigurationManager(InstantVASInstanceConfiguration.class);
		cm.loadFromFile(fsFilePath);
		// static values for 'InstantVASInstanceConfiguration' are set, which will eventually be used to generate a new instance of that class
	}
	
	public static void applyConfigurationFromLicenseClass() throws IllegalArgumentException, IOException, IllegalAccessException {
		
		setTemporaryLog();
		
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
		}
		
		if ((INSTANTVAS_INSTANCES_SOURCE_TYPE == ConfigurationSourceType_HARDCODED) && (INSTANTVAS_INSTANCE_CONFIGn_LENGTH > 0)) {
			if (INSTANTVAS_INSTANCE_CONFIG0_TYPE == ConfigurationSourceType_PLAIN_FS_FILE) {
				/* debug */ if (IFDEF_CONFIG_DEBUG) {
					Instrumentation.reportDebug("INSTANTVAS_INSTANCE_CONFIGn_LENGTH := " + INSTANTVAS_INSTANCE_CONFIGn_LENGTH);
					Instrumentation.reportDebug("Loading Hard-Coded Instance 0 configuration from Plain FS File '"+INSTANTVAS_INSTANCE_CONFIG0_ACCESS_INFO+"'");
				}
				applyConfigurationFromPlainFSFile(INSTANTVAS_INSTANCE_CONFIG0_ACCESS_INFO);
			}
		} else if (INSTANTVAS_INSTANCES_SOURCE_TYPE == ConfigurationSourceType_PLAIN_FS_FILE) {
			String instancesDefinitionFile = INSTANTVAS_INSTANCES_SOURCE_ACCESS_INFO;
			/* debug */ if (IFDEF_CONFIG_DEBUG) {
				Instrumentation.reportDebug("INSTANTVAS_INSTANCES_SOURCE_ACCESS_INFO := " + instancesDefinitionFile);
				Instrumentation.reportDebug("Loading Hard-Coded Instance 0 configuration from Plain FS File '"+INSTANTVAS_INSTANCE_CONFIG0_ACCESS_INFO+"'");
			}
			loadInstanceDefinitionsFromPlainFSFile(instancesDefinitionFile);
		} else {
			/* debug */ if (IFDEF_CONFIG_DEBUG) {throw new RuntimeException("InstantVASConfigurationLoader ERROR: No detectable license configuration was found");}
		}
		
	}

	/** temporary log -- meant to record events before the configuration file tells what to do with them (mainly records parsing and application of the configuration file) */
	private static ArrayList<InstrumentationEventDto> temporaryLoggedEvents = null;

	/** called to allow logging events before the configuration is loaded. Don't forget to call {@link #purgeTemporaryLog} */
	public static void setTemporaryLog() {
		if (temporaryLoggedEvents == null) {
			temporaryLoggedEvents = new ArrayList<InstrumentationEventDto>();
			IInstrumentationHandler ramLogger = new InstrumentationHandlerRAM() {
				public void analyzeRequest(ArrayList<InstrumentationEventDto> requestEvents) {
					temporaryLoggedEvents.addAll(requestEvents);
				}
				public void close() {
					onRequestFinish(new InstrumentationEventDto(System.currentTimeMillis(), Thread.currentThread(),
						new InstrumentableEvent("Reconfiguring Instrumentation", ELogSeverity.CRITICAL)));
				}
			};
			Instrumentation.configureDefaultValuesForNewInstances(ramLogger, null, null);
		}
	}

	/** register any eventually recorded RAM events (that happened before we know how/where to log) */
	public static void purgeTemporaryLog(IInstrumentationHandler newLogHandler) {
		if (temporaryLoggedEvents != null) {
			for (int i=1; i<temporaryLoggedEvents.size(); i++) {	// skip the first element, a redundant 'APP_START' event
				InstrumentationEventDto temporaryLoggedEvent = temporaryLoggedEvents.get(i);
				newLogHandler.onInstrumentationEvent(temporaryLoggedEvent);
			}
			temporaryLoggedEvents = null;
		}
	}
}
