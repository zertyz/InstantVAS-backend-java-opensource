package mutua.hangmansmsgame.smslogic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor;
import mutua.hangmansmsgame.smslogic.SMSProcessorException;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 *  TestCommons.java
 * ================
 * (created by luiz, Feb 13, 2011)
 *
 * Result of a refactoring of the test classes
 */



public class TestCommons {
	
	
	protected ESMSInParserCarrier TEST_CARRIER = ESMSInParserCarrier.TEST_CARRIER;

	
	private TestResponseReceiver responseReceiver;
	private HangmanSMSGameProcessor processor;

	
	public TestCommons() {
		responseReceiver = new TestResponseReceiver();
		processor = new HangmanSMSGameProcessor(responseReceiver);
	}
		
//	public void setDB(Object[][] blocosDBData) {
//		IBlocoDAO blocoDB = DALFactory.getInstance().getBlocoDAO();
//		blocoDB.setData(blocosDBData);
//	}
	
	public void checkResponse(String phone, String inputText, String expectedResponseText) {
		try {
			IncomingSMSDto mo = new IncomingSMSDto(phone, inputText, TEST_CARRIER, "1234", "null");
			processor.process(mo);
			OutgoingSMSDto[] observedResponses = responseReceiver.getLastOutgoingSMSes();
			boolean isNullityCorrect = ((expectedResponseText == null) && (observedResponses == null)) ||
			                           ((expectedResponseText != null) && (observedResponses != null));
			assertTrue("The 'expectedResponseText' nullity isn't the same as 'observedResponses's", isNullityCorrect);
			if (observedResponses != null) {
				assertEquals("This command should have generated 1 (and only 1) response message", 1, observedResponses.length);
				//System.out.println(observedResponses[0].getText());
				assertEquals("The input message '"+inputText+"' issued a wrong response", expectedResponseText, observedResponses[0].getText());
			}
		} catch (SMSProcessorException e) {
			fail("Exception while processing message: "+e.getMessage());
		}
	}
	
	// TODO: refactor this method
	public void checkResponse(String phone, String inputText, String[] expectedResponsesText) {
		try {
			IncomingSMSDto mo = new IncomingSMSDto(phone, inputText, TEST_CARRIER, "1234", "null");
			processor.process(mo);
			OutgoingSMSDto[] observedResponses = responseReceiver.getLastOutgoingSMSes();
			boolean isNullityCorrect = ((expectedResponsesText == null) && (observedResponses == null)) ||
			                           ((expectedResponsesText != null) && (observedResponses != null));
			assertTrue("The 'expectedResponseText' nullity isn't the same as 'observedResponses's", isNullityCorrect);
			if (observedResponses != null) {
				assertEquals("This command generated the wrong number of messages", expectedResponsesText.length, observedResponses.length);
				//System.out.println(observedResponses[0].getText());
				for (int i=0; i<expectedResponsesText.length; i++) {
					String expectedResponseText = expectedResponsesText[i];
					assertEquals("The input message '"+inputText+"' issued a wrong response at message #"+i, expectedResponseText, observedResponses[i].getText());
				}
			}
		} catch (SMSProcessorException e) {
			fail("Exception while processing message: "+e.getMessage());
		}
	}
		
//	public void setFullDB() {
//		SetFullDB.setFullDB();
//	}
	

	
}
