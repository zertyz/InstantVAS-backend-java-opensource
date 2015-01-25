package mutua.hangmansmsgame.smslogic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import mutua.hangmansmsgame.config.Configuration;
import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.ISessionDB;
import mutua.hangmansmsgame.dal.IUserDB;
import mutua.hangmansmsgame.dal.dto.SessionDto;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.InstrumentationTestRequestProperty;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 *  TestCommons.java
 * ================
 * (created by luiz, Feb 13, 2011)
 *
 * Result of a refactor of the test classes
 */



public class TestCommons {
	
	
	protected ESMSInParserCarrier TEST_CARRIER = ESMSInParserCarrier.TEST_CARRIER;
	
	private TestResponseReceiver responseReceiver;
	private HangmanSMSGameProcessor processor;


	// databases
	////////////
	
	private static IUserDB    userDB    = DALFactory.getUserDB();
	private static ISessionDB sessionDB = DALFactory.getSessionDB();

	
	public TestCommons() {
		responseReceiver = new TestResponseReceiver();
		processor = new HangmanSMSGameProcessor(responseReceiver);
	}

	
	/**********************************
	** DATABASE MANIPULATION METHODS **
	**********************************/
	
	/** Reset all databases */
	public void resetDatabases() {
		userDB.reset();
		sessionDB.reset();
	}
	
	/** Reset and populate Users database. users := { {phone, nick}, ...} */
	public void setUserDB(String[][] users) {
		userDB.reset();
		for (String[] userRecord : users) {
			userDB.checkAvailabilityAndRecordNickname(userRecord[0], userRecord[1]);
		}
	}
	
	/** Reset and populate Sessions Database. sessions := { {phone, state}, ...} */
	public void setSessionDB(String[][] sessions) {
		sessionDB.reset();
		for (String[] sessionEntry : sessions) {
			sessionDB.setSession(new SessionDto(sessionEntry[0], sessionEntry[1]));
		}
	}
	



	/********************************
	** DIALOG VERIFICATION METHODS **
	********************************/	
	
	public void checkResponse(String phone, String inputText, String expectedResponseText) {
		checkResponse(phone, inputText, new String[] {expectedResponseText});
	}
	
	public void checkResponse(String phone, String inputText, String[] expectedResponsesText) {
		try {
			IncomingSMSDto mo = new IncomingSMSDto(phone, inputText, TEST_CARRIER, "1234", "null");
			Instrumentation<InstrumentationTestRequestProperty, String> log = (Instrumentation<InstrumentationTestRequestProperty, String>)Configuration.log;
			log.reportRequestStart(Thread.currentThread().getStackTrace()[2].getMethodName());
			processor.process(mo);
			log.reportRequestFinish();
			OutgoingSMSDto[] observedResponses = responseReceiver.getLastOutgoingSMSes();
			boolean isNullityCorrect = ((expectedResponsesText == null) && (observedResponses == null)) ||
			                           ((expectedResponsesText != null) && (observedResponses != null));
			assertTrue("The 'expectedResponseText' nullity isn't the same as 'observedResponses's", isNullityCorrect);
System.out.print("#"+phone+";"+inputText+";");
			String[] observedResponsesText = new String[observedResponses.length];
			for (int i=0; i<observedResponses.length; i++) {
System.out.print(observedResponses[i].getPhone()+";"+observedResponses[i].getText().replaceAll("\n", "\\\\n")+";");
				observedResponsesText[i] = observedResponses[i].getText();
			}
System.out.println("\n");
			assertArrayEquals("This command generated the wrong messages", expectedResponsesText, observedResponsesText);
		} catch (SMSProcessorException e) {
			fail("Exception while processing message: "+e.getMessage());
		}
	}
	
}
