package mutua.hangmansmsgame.smslogic;

import static org.junit.Assert.*;
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
		checkResponse(phone, inputText, new String[] {expectedResponseText});
	}
	
	public void checkResponse(String phone, String inputText, String[] expectedResponsesText) {
		try {
			IncomingSMSDto mo = new IncomingSMSDto(phone, inputText, TEST_CARRIER, "1234", "null");
			processor.process(mo);
			OutgoingSMSDto[] observedResponses = responseReceiver.getLastOutgoingSMSes();
			boolean isNullityCorrect = ((expectedResponsesText == null) && (observedResponses == null)) ||
			                           ((expectedResponsesText != null) && (observedResponses != null));
			assertTrue("The 'expectedResponseText' nullity isn't the same as 'observedResponses's", isNullityCorrect);
			String[] observedResponsesText = new String[observedResponses.length];
			for (int i=0; i<observedResponses.length; i++) {
				observedResponsesText[i] = observedResponses[i].getText();
			}
			assertArrayEquals("This command generated the wrong messages", expectedResponsesText, observedResponsesText);
		} catch (SMSProcessorException e) {
			fail("Exception while processing message: "+e.getMessage());
		}
	}
		
//	public void setFullDB() {
//		SetFullDB.setFullDB();
//	}
	

	
}
