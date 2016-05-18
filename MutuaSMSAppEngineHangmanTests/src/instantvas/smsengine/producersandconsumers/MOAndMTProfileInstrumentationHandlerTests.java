package instantvas.smsengine.producersandconsumers;

import static config.InstantVASLicense.*;
import static instantvas.smsengine.SMSAppEngineInstrumentationMethods.*;
import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import instantvas.smsengine.SMSAppEngineInstrumentationMethods;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.dto.InstrumentationEventDto;
import mutua.icc.instrumentation.handlers.IInstrumentationHandler;
import mutua.icc.instrumentation.handlers.InstrumentationHandlerLogConsole;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto.EBillingType;
import mutua.tests.SplitRun;

import org.junit.Before;
import org.junit.Test;

/** <pre>
 * MOAndMTProfileInstrumentationHandlerTests.java
 * ==============================================
 * (created by luiz, May 13, 2016)
 *
 * Tests both unit and integration of {@link MOAndMTProfileInstrumentationHandler}
 *
 * @version $Id$
 * @author luiz
 */

public class MOAndMTProfileInstrumentationHandlerTests {
	
	private IInstrumentationHandler              nullInstrumentationHandler;
	private TestInstrumentationHandler           testInstrumentationHandler;
	private MOAndMTProfileInstrumentationHandler profileHandler;

	
	@Before
	public void reset() {
		nullInstrumentationHandler  = new NullInstrumentationHandler();
		testInstrumentationHandler  = new TestInstrumentationHandler();
		profileHandler              = new MOAndMTProfileInstrumentationHandler();
		Instrumentation.configureDefaultValuesForNewInstances(testInstrumentationHandler, nullInstrumentationHandler, profileHandler);
	}
	
	private int moId = 1;
	/** this method attempts to call the correct sequence of the instrumentation events
	 *  defined in {@link SMSAppEngineInstrumentationMethods}, when an MO is being processed and the MT being sent 
	 * @param instrumentMOId TODO*/
	private void simulateMOandMTs(
		String phone, int numberOfMTs, int numberOfSMSes, boolean acceptMO, boolean processMO,
		boolean instrumentMOId, int moEnqueuingTime, int moDequeuingTime, int moProcessingTime, int perMtEnqueuingTime, int perMtDequeuingTime, int perSMSDeliveryTime) throws InterruptedException {
		
		IncomingSMSDto mo;
		OutgoingSMSDto mt; 

		synchronized (this) {
			mo = new IncomingSMSDto(moId, phone, "test mo", ESMSInParserCarrier.TEST_CARRIER, "999");
			mt = new OutgoingSMSDto(moId, phone, "test mt", EBillingType.FREE);
			moId++;
		}
		
		// MO arrival
		startAddToMOQueueRequest("--");
		Thread.sleep(moEnqueuingTime);
		if (!acceptMO) {
			reportMOQueueRejection("--");
			finishRequest();
			return;
		}
		if (instrumentMOId) {
			reportMOQueueAddition(mo.getMoId(), mo);
		} else {
			reportMOQueueAddition(mo);	// to test the temporary negative moId
		}
		finishRequest();
		Thread.sleep(moDequeuingTime);
		// MO processing
		startMOProcessingRequest(mo);
		Thread.sleep(moProcessingTime);
		if (!processMO) {
			// simulates an exception, where the request won't be properly finished
			return ;
		}
		for (int i=0; i<numberOfMTs; i++) {
			reportMTIsReady(mo, mt);
			Thread.sleep(perMtEnqueuingTime);
			reportMTEnqueued(mo, mt);
		}
		finishRequest();	// on the real system, MT delivery starts before the MO processing request finishes. This will not be simulated here.
		Thread.sleep(perMtDequeuingTime*numberOfMTs);
		// MT delivery
		startMTDeliveryRequest(mt);
		Thread.sleep(perSMSDeliveryTime*numberOfSMSes);
		finishRequest();
	}
	
	private void checkLastProfileData(int moEnqueuingTime, int moDequeuingTime, int moProcessingTime, int mtEnqueuingTime, int mtDequeuingTime, int mtDeliveryTime) {
		RequestProfilingTimes[] expectedRPTs = new RequestProfilingTimes[1];
		expectedRPTs[0] = new RequestProfilingTimes();
		expectedRPTs[0].moEnqueuingTime  = moEnqueuingTime;
		expectedRPTs[0].moDequeuingTime  = moDequeuingTime;
		expectedRPTs[0].moProcessingTime = moProcessingTime;
		expectedRPTs[0].mtEnqueuingTime  = mtEnqueuingTime;
		expectedRPTs[0].mtDequeuingTime  = mtDequeuingTime;
		expectedRPTs[0].mtDeliveryTime   = mtDeliveryTime;
		checkLastProfileData(expectedRPTs);
	}
	
	private void checkLastProfileData(RequestProfilingTimes[] expectedRPTs) {
		InstrumentationEventDto[] lastProfiledRequests = testInstrumentationHandler.consumeLastProfiledRequests();
		assertEquals("Wrong number of profiled requests", expectedRPTs.length, lastProfiledRequests.length);
		for (int i=0; i<expectedRPTs.length; i++) {
			RequestProfilingTimes observedRPT = (RequestProfilingTimes) (lastProfiledRequests[i].propertiesAndValues[1]);
			observedRPT.computeTimes();
			assertNotNull("Undefined 'phone' detected at event #"+i, observedRPT.phone);
			assertTrue   ("Undefined 'moId' detected at event #"+i, observedRPT.moId != -1);
			// compares with 15ms precision
			assertEquals("wrong 'moEnqueuingTime' measured at event #"+i,  (long) expectedRPTs[i].moEnqueuingTime  / 10 * 10, observedRPT.moEnqueuingTime  / 10 * 10);
			assertEquals("wrong 'moDequeuingTime' measured at event #"+i,  (long) expectedRPTs[i].moDequeuingTime  / 10 * 10, observedRPT.moDequeuingTime  / 10 * 10);
			assertEquals("wrong 'moProcessingTime' measured at event #"+i, (long) expectedRPTs[i].moProcessingTime / 10 * 10, observedRPT.moProcessingTime / 10 * 10);
			assertEquals("wrong 'mtEnqueuingTime' measured at event #"+i,  (long) expectedRPTs[i].mtEnqueuingTime  / 10 * 10, observedRPT.mtEnqueuingTime  / 10 * 10);
			assertEquals("wrong 'mtDequeuingTime' measured at event #"+i,  (long) expectedRPTs[i].mtDequeuingTime  / 10 * 10, observedRPT.mtDequeuingTime  / 10 * 10);
			assertEquals("wrong 'mtDeliveryTime' measured at event #"+i,   (long) expectedRPTs[i].mtDeliveryTime   / 10 * 10, observedRPT.mtDeliveryTime   / 10 * 10);
		}
	}
	
	private void checkLastTimedOutProfileData(int moEnqueuingTime, long totalDuration) {
		InstrumentationEventDto[] lastTimedOutRequests = testInstrumentationHandler.consumeLastTimedOutRequests();
		if (lastTimedOutRequests.length > 1) {
			System.err.println("Wrong number of timed out requests. Dumping them:");
			for (int i=0; i<lastTimedOutRequests.length; i++) {
				System.err.println(i+"#: " + ((RequestProfilingTimes)lastTimedOutRequests[i].propertiesAndValues[1]).toString());
			}
		}
		assertEquals("Wrong number of timed out requests", 1, lastTimedOutRequests.length);
		RequestProfilingTimes rpt = (RequestProfilingTimes) (lastTimedOutRequests[0].propertiesAndValues[1]);
		rpt.computeTimes();
System.out.println("### totalDuration="+totalDuration+"ms; rpt.totalDuration="+rpt.totalDuration+"ms.");
System.out.println("### totalDuration  / 1000 * 1000 ="+(totalDuration / 1000 * 1000)+"ms; rpt.totalDuration  / 1000 * 1000 ="+(rpt.totalDuration / 1000 * 1000)+"ms.");
		// compares with 1s precision
		assertEquals("wrong 'totalDuration' measured",    totalDuration / 1000 * 1000,       rpt.totalDuration / 1000 * 1000);
		// compares with 10ms precision
		assertEquals("wrong 'moEnqueuingTime' measured",  (long) moEnqueuingTime  / 10 * 10, rpt.moEnqueuingTime  / 10 * 10);
		assertEquals("wrong 'moDequeuingTime' measured",  (long) -1,                         rpt.moDequeuingTime);
		assertEquals("wrong 'moProcessingTime' measured", (long) -1,                         rpt.moProcessingTime);
		assertEquals("wrong 'mtEnqueuingTime' measured",  (long) -1,                         rpt.mtEnqueuingTime);
		assertEquals("wrong 'mtDequeuingTime' measured",  (long) -1,                         rpt.mtDequeuingTime);
		assertEquals("wrong 'mtDeliveryTime' measured",   (long) -1,                         rpt.mtDeliveryTime);
	}

	@Test
	public void testOrdinaryRequestProfiling() throws InterruptedException {
		simulateMOandMTs("21991234899", 2, 3, true, true, true, 10, 20, 30, 40, 50, 60);
		checkLastProfileData(10, 20, 30, 40*2, 50*2, 60*3);
	}
	
	@Test
	public void testBatchRequestProfiling() throws InterruptedException {
		simulateMOandMTs("5521991234899", 2, 3, true, true, false, 10, 20, 30, 40, 50, 60);
		checkLastProfileData(10, 20, 30, 40*2, 50*2, 60*3);
		simulateMOandMTs("+5521991234898", 2, 3, true, true, false, 10, 20, 30, 40, 50, 60);
		checkLastProfileData(10, 20, 30, 40*2, 50*2, 60*3);
	}
	
	@Test
	public void testTimedOutRequestsAndReentrancy() throws InterruptedException {
		simulateMOandMTs("21991234899", 2, 3, true, false, true, 10, 20, 30, 40, 50, 60);		// no requests will be generated
		assertEquals("The last MO shouldn't've produced any requests", 0, testInstrumentationHandler.consumeLastProfiledRequests().length);
		simulateMOandMTs("21991234899", 2, 3, false, false, true, 10, 20, 30, 40, 50, 60);		// a request, which will timeout, will be generated
		assertEquals("The last MO shouldn't've produced any requests", 0, testInstrumentationHandler.consumeLastProfiledRequests().length);
		
		// reentrancy tests -- should take no more than 'INSTRUMENT_MO_AND_MT_TIMEOUT' + 1 second
		/////////////////////////////////////////////////////////////////////////////////////////
		
		long start = System.currentTimeMillis();
		
		final int t = 800;
		final int n = 138;
		final RequestProfilingTimes[] expectedRPTs = new RequestProfilingTimes[t * n];
		for (int i=0; i<expectedRPTs.length; i++) {
			expectedRPTs[i] = new RequestProfilingTimes();
			expectedRPTs[i].moEnqueuingTime  = 10;
			expectedRPTs[i].moDequeuingTime  = 20;
			expectedRPTs[i].moProcessingTime = 30;
			expectedRPTs[i].mtEnqueuingTime  = 40*2;
			expectedRPTs[i].mtDequeuingTime  = 50*2;
			expectedRPTs[i].mtDeliveryTime   = 60*3;
		}
		for (int threadNumber=0; threadNumber<t; threadNumber++) {
			SplitRun.add(new SplitRun(threadNumber) {
				public void splitRun(int threadNumber) throws Throwable {
					sleep((long) (Math.random()*400));
					for (int i=0; i<n; i++) {
						simulateMOandMTs("21991234899", 2, 3, true, true, true, 10, 20, 30, 40, 50, 60);
					}
				}
			});
		}
		
		// run the reentrncy test
		SplitRun.runAndWaitForAll();
		
		// check
		InstrumentationEventDto[] lastProfiledRequests = testInstrumentationHandler.consumeLastProfiledRequests();
		assertEquals("Wrong number of profiled requests", expectedRPTs.length, lastProfiledRequests.length);
		for (int i=0; i<expectedRPTs.length; i++) {
			RequestProfilingTimes observedRPT = (RequestProfilingTimes) (lastProfiledRequests[i].propertiesAndValues[1]);
			observedRPT.computeTimes();
			assertNotNull("Undefined 'phone' detected at event #"+i, observedRPT.phone);
			assertTrue   ("Undefined 'moId' detected at event #"+i, observedRPT.moId != -1);
		}		
		
		// sleep the remaining time until reaching 'INSTRUMENT_MO_AND_MT_TIMEOUT'ms
		long remainingMillis = (INSTRUMENT_MO_AND_MT_TIMEOUT)-(System.currentTimeMillis()-start);
		assertTrue("'n' was too great and requred extra "+(-remainingMillis)+"ms to run. Please, decrease it", remainingMillis >= 0);
		System.out.println("Sleeping for additional "+remainingMillis+"ms");
		Thread.sleep(remainingMillis);

		// timeout check
		testOrdinaryRequestProfiling();		// provokes a computation of the timed out events
		checkLastTimedOutProfileData(10, INSTRUMENT_MO_AND_MT_TIMEOUT);
	}

}

class TestInstrumentationHandler implements IInstrumentationHandler {
	
	private ArrayList<InstrumentationEventDto> lastProfiledRequests = new ArrayList<InstrumentationEventDto>(150000);
	private ArrayList<InstrumentationEventDto> lastTimedOutRequests = new ArrayList<InstrumentationEventDto>();
	
//private IInstrumentationHandler log = new InstrumentationHandlerLogConsole("TestApp", ELogSeverity.DEBUG);
	
	public InstrumentationEventDto[] consumeLastProfiledRequests() {
		InstrumentationEventDto[] requests = lastProfiledRequests.toArray(new InstrumentationEventDto[0]);
		lastProfiledRequests.clear();
		return requests;
	}

	public InstrumentationEventDto[] consumeLastTimedOutRequests() {
		InstrumentationEventDto[] requests = lastTimedOutRequests.toArray(new InstrumentationEventDto[0]);
		lastTimedOutRequests.clear();
		return requests;
	}

	@Override
	public void onRequestStart(InstrumentationEventDto requestStartInstrumentationEvent) {}

	@Override
	public synchronized void onInstrumentationEvent(InstrumentationEventDto instrumentationEvent) {
		if (instrumentationEvent.instrumentableEvent == PROFILED_REQUEST_EVENT) {
			lastProfiledRequests.add(instrumentationEvent);
		}
		if (instrumentationEvent.instrumentableEvent == TIMEDOUT_REQUEST_EVENT) {
			lastTimedOutRequests.add(instrumentationEvent);
		}
//if ((instrumentationEvent.instrumentableEvent == PROFILED_REQUEST_EVENT) || (instrumentationEvent.instrumentableEvent == TIMEDOUT_REQUEST_EVENT)) {
//log.onInstrumentationEvent(instrumentationEvent);
//}
	}

	@Override
	public void onRequestFinish(InstrumentationEventDto requestFinishInstrumentationEvent) {}

	@Override
	public void close() {}
	
}

class NullInstrumentationHandler implements IInstrumentationHandler {
	public void onRequestStart(InstrumentationEventDto requestStartInstrumentationEvent) {}
	public void onRequestFinish(InstrumentationEventDto requestFinishInstrumentationEvent) {}
	public void onInstrumentationEvent(InstrumentationEventDto instrumentationEvent) {}
	public void close() {}
}