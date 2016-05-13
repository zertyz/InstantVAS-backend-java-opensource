package instantvas.smsengine.producersandconsumers;

import static config.InstantVASLicense.*;
import static instantvas.smsengine.SMSAppEngineInstrumentationMethods.*;
import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import mutua.icc.instrumentation.InstrumentableProperty;
import mutua.icc.instrumentation.dto.InstrumentationEventDto;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerRAM;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 * MOAndMTProfileInstrumentationHandler.java
 * =========================================
 * (created by luiz, Mar 31, 2016)
 *
 * Instrumentation Handler class to measure the times involved in the MOs and MTs processing
 *
 * @see RequestProfilingTimes
 * @version $Id$
 * @author luiz
*/

public class MOAndMTProfileInstrumentationHandler extends InstrumentationHandlerRAM {
	
	private static ConcurrentHashMap<Integer, RequestProfilingTimes> MOsInProcess = new ConcurrentHashMap<Integer, RequestProfilingTimes>(100);

	private static void purgeTimedOutRequests() {
		synchronized (MOsInProcess) {
			long currentTimeMillis = System.currentTimeMillis();
			Iterator<RequestProfilingTimes> iterator = MOsInProcess.values().iterator();
			while (iterator.hasNext()) {
				RequestProfilingTimes rpt = iterator.next();
				if ((currentTimeMillis - rpt.moReceivedMillis) > INSTRUMENT_MO_AND_MT_TIMEOUT) {
					iterator.remove();
					logTimedOutProfiledRequest(rpt);
				}
			}
		}
	}
	
	/** Returns the {@link RequestProfilingTimes} instance for the given 'moId' or create a new one */
	private RequestProfilingTimes getProcessingTimes(int moId) {
		synchronized (MOsInProcess) {
			RequestProfilingTimes processingTimes = MOsInProcess.get(moId);
			if (processingTimes == null) {
				processingTimes = new RequestProfilingTimes();
				MOsInProcess.put(moId, processingTimes);
			}
			return processingTimes;
		}
	}
	
	// InstrumentationHandlerRAM IMPLEMENTATION
	///////////////////////////////////////////

	@Override
	public void close() {}

	private static final InstrumentationEventDto[] zeroInstrumentationEventArray = {};
	@Override
	public void analyzeRequest(ArrayList<InstrumentationEventDto> requestEvents) {
		InstrumentationEventDto[] events = requestEvents.toArray(zeroInstrumentationEventArray);
		
		// sanity check -- only requests properly started & with an associated property are analyzed
		if ((events.length == 0) || (events[0].instrumentableEvent != REQUEST_START_EVENT) || (events[0].propertiesAndValues.length == 0)) {
			return;
		}
		
		long                  requestMillis = events[0].currentTimeMillis;	// time the current request started
		int                   moId          = -1;
		RequestProfilingTimes processingTimes;
		
		String requestIdentity;
		InstrumentableProperty requestStartProperty = (InstrumentableProperty)(events[0].propertiesAndValues[0]);
		if (requestStartProperty == addToMOQueueRequestProperty) {
			// MO Arrival request -- started with 'startAddToMOQueueRequest'
			Object[] instrumentedValues = retrieveFirstMOQueueAdditionData(events);
			moId = (Integer) instrumentedValues[0];
			processingTimes = getProcessingTimes(moId);
			processingTimes.moId                  = moId;
			processingTimes.moReceivedMillis      = requestMillis;
			processingTimes.moQueueAdditionMillis = (Long) instrumentedValues[2];
//requestIdentity = "MO Arrival";
		} else if (requestStartProperty == processMORequestProperty) {
			// MO Processing request -- started with 'startMOProcessingRequest'
			Object[] instrumentedValues = retrieveFirstMTIsReadyData(events);
			IncomingSMSDto mo = (IncomingSMSDto) instrumentedValues[0];
			moId = mo.getMoId();
			processingTimes = getProcessingTimes(moId);
			processingTimes.phone = mo.getPhone();
			processingTimes.moProcessingCompleteMillis = (Long) instrumentedValues[2];	// we only want the times for the first occurrence of the 'MT is Ready' event, which we, then, may call 'MO Processing Complete time'
			processingTimes.moProcessingStart          = requestMillis;
			instrumentedValues = retrieveLastMTEnqueuedData(events);
			processingTimes.mtQueueAdditionMillis      = (Long) instrumentedValues[2];
//requestIdentity = "MO Processing";
		} else if (requestStartProperty == deliverMTRequestProperty) {
			// MT Delivery request -- started with 'startMTDeliveryRequest'
			OutgoingSMSDto mt = (OutgoingSMSDto) events[0].propertiesAndValues[1];	// value set at 'REQUEST_START_EVENT'
			moId = mt.getMoId();
			processingTimes = getProcessingTimes(moId);
			processingTimes.mtDeliveryStartMillis = requestMillis;
			processingTimes.mtDeliveredMillis = events[events.length-1].currentTimeMillis;	// the time of the 'REQUEST_FINISH_EVENT'
			
			logProfiledRequest(processingTimes);
			MOsInProcess.remove(moId);
			purgeTimedOutRequests();
//requestIdentity = "MT Delivery";
		} else {
//requestIdentity = "Unknown";
			return;
		}
//		System.out.print("Just received a request to analyze, identifyed as '"+requestIdentity+"', with moId="+moId+": ");
//		for (InstrumentationEventDto event : requestEvents) {
//			System.out.print(event.instrumentableEvent.eventName+", ");
//		}
//		System.out.println();
	}
}