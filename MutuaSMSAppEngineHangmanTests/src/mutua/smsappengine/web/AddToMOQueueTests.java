package mutua.smsappengine.web;

import static org.junit.Assert.*;

import org.junit.Test;

import static config.InstantVASLicense.*;

import instantvas.smsengine.HangmanSMSGameServicesInstrumentationEvents;
import instantvas.smsengine.InstantVASHTTPInstrumentationRequestProperty;
import instantvas.smsengine.producersandconsumers.IMOProducer;
import instantvas.smsengine.web.AddToMOQueue;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsin.parsers.SMSInParser;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** <pre>
 * AddToMOQueueTests.java
 * ======================
 * (created by luiz, Mar 29, 2016)
 *
 * Tests the helper class AddToMOQueue, used by servlets and netive web server services
 *
 * @version $Id$
 * @author luiz
*/

public class AddToMOQueueTests {

	@Test
	public void testLicenseEnforcement() {

		final IncomingSMSDto[] lastMO = {null}; 
		
		Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String> log =
			new Instrumentation<InstantVASHTTPInstrumentationRequestProperty, String>(
				"MyTests", new InstantVASHTTPInstrumentationRequestProperty(), EInstrumentationDataPours.CONSOLE, null);
		log.addInstrumentableEvents(HangmanSMSGameServicesInstrumentationEvents.values());
		
		IMOProducer moProducer = new IMOProducer() {
			@Override
			public int dispatchMOForProcessing(IncomingSMSDto mo) {
				lastMO[0] = mo;
				return 1;
			};
		};
		
		SMSInParser<String[], byte[]> moParser = new SMSInParser<String[], byte[]>("MyParser", "MyApp") {
			@Override
			public String[] getRequestParameterNames(String... precedingParameterNames) {return new String[] {"msisdn", "text"};}
			@Override
			public IncomingSMSDto parseIncomingSMS(String... parameterValues) {
				String msisdn = parameterValues[0];
				String text   = parameterValues[1];
				if (text.indexOf("brazil") != -1) {
					return new IncomingSMSDto(-1, msisdn, text, ALLOWABLE_CARRIER0, ALLOWABLE_SHORT_CODE0);
				} else {
					return new IncomingSMSDto(-1, msisdn, text, ESMSInParserCarrier.UNKNOWN, "111");
				}
			}
			@Override
			public byte[] getReply(ESMSInParserSMSAcceptionStatus status) {
				switch (status) {
					case ACCEPTED : return "ACCEPTED" .getBytes();
					case POSTPONED: return "POSTPONED".getBytes();
					case REJECTED:  return "REJECTED" .getBytes();
					default: throw new NotImplementedException();
				}
			}
			
		};
		
		AddToMOQueue amoq = new AddToMOQueue(log, moProducer, moParser,
			ALLOWABLE_MSISDN_MIN_LENGTH, ALLOWABLE_MSISDN_MAX_LENGTH,
			ALLOWABLE_MSISDN_PREFIXES,
			ALLOWABLE_CARRIERS,
			ALLOWABLE_SHORT_CODES);

		// tests
		IncomingSMSDto mo = moParser.parseIncomingSMS("121991234899", "message from the united states");
		byte[] response = amoq.processRequest(mo, null);
		assertEquals("The message wasn't rejected", "REJECTED", new String(response));
		
		mo = moParser.parseIncomingSMS("5521991234899", "message from brazil");
		response = amoq.processRequest(mo, null);
		assertEquals("The message wasn't accepted", "ACCEPTED", new String(response));
	}

}
