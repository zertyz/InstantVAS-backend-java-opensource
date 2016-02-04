package mutua.smsappmodule;

import static instantvas.tests.InstantVASSMSAppModuleTestsConfiguration.*;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.hangmansmsgame.smslogic.SMSProcessor;
import mutua.hangmansmsgame.smslogic.SMSProcessorException;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 * SMSAppModuleTestCommons.java
 * ============================
 * (created by luiz, Jul 24, 2015)
 *
 * Contains entries common to SMS App Module test classes
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleTestCommons {
	
	public final SMSProcessor smsP;
	
	private TestResponseReceiver responseReceiver;
	
	private static IUserDB    userDB    = DEFAULT_SMS_MODULE_DAL.getUserDB();
	private static ISessionDB sessionDB = DEFAULT_SMS_MODULE_DAL.getSessionDB();

	
	public SMSAppModuleTestCommons(Instrumentation<?, ?> log, INavigationState[]... navigationStatesArrays) {
		responseReceiver = new TestResponseReceiver();
		SMSProcessor.configureDefaultValuesForNewInstances(log, DEFAULT_SMS_MODULE_DAL);
		smsP = new SMSProcessor(responseReceiver, navigationStatesArrays);
	}
	
	
	/**********************************
	** DATABASE MANIPULATION METHODS **
	**********************************/
	
	/** Reset all the base module databases */
	public static void resetBaseTables() throws SQLException {
		sessionDB.reset();
		userDB.reset();
	}
	
	/** Inserts 'n' users on the database, starting at 'first' and using 'p' concurrent threads.
	 *  'users.length' must be divisible by 'p'. */
	public static void insertUsers(final long first, final UserDto[] users, int p) throws SQLException, InterruptedException {
		final int _n = users.length / p;
		for (int threadNumber=0; threadNumber<p; threadNumber++) {
			SplitRun.add(new SplitRun(threadNumber) {
				@Override
				public void splitRun(int threadNumber) throws SQLException {
					for (int i=(_n*threadNumber); i<(_n*(threadNumber+1)); i++) {
						String phone = Long.toString(first+i);
						users[i]     = userDB.assureUserIsRegistered(phone);
					}
				}
			});
		}
		SplitRun.runAndWaitForAll();
	}

	
	/********************************
	** DIALOG VERIFICATION METHODS **
	********************************/	

	private Random rnd = new Random();
	public void checkResponse(String phone, String inputText, String... expectedResponsesText) {
		int moId = rnd.nextInt();
		checkResponse(moId, phone, inputText, expectedResponsesText);
	}
	
	public void checkResponse(int moId, String phone, String inputText, String... expectedResponsesText) {
		try {
			IncomingSMSDto mo = new IncomingSMSDto(moId, phone, inputText, ESMSInParserCarrier.TEST_CARRIER, "1234");
			smsP.process(mo);
			OutgoingSMSDto[] observedResponses = responseReceiver.getLastOutgoingSMSes();
			boolean isNullityCorrect = ((expectedResponsesText == null) && (observedResponses == null)) ||
			                           ((expectedResponsesText != null) && (observedResponses != null));
			assertTrue("The 'expectedResponseText' nullity isn't the same as 'observedResponses's", isNullityCorrect);
System.err.print("#"+phone+";"+inputText+";");
			String[] observedResponsesText = new String[observedResponses.length];
			for (int i=0; i<observedResponses.length; i++) {
System.err.print(observedResponses[i].getPhone()+";"+observedResponses[i].getText().replaceAll("\n", "\\\\n")+";");
				observedResponsesText[i] = observedResponses[i].getText();
				assertEquals("'moId' inconsistency", moId, observedResponses[i].getMoId());
			}
System.err.println("\n");
			assertArrayEquals("This command generated the wrong messages", expectedResponsesText, observedResponsesText);
		} catch (SMSProcessorException e) {
			fail("Exception while processing message: "+e.getMessage());
		}
	}

	public void checkNavigationState(String phone, INavigationState navigationState) throws SQLException {
		UserDto user          = userDB.assureUserIsRegistered(phone);
		SessionDto sessionDto = sessionDB.getSession(user);
		String navigationStatePropertyName = SessionModel.NAVIGATION_STATE_PROPERTY.getPropertyName();
		String[][] storedProperties = sessionDto.getStoredProperties();
		boolean found = false;
		for (String[] storedPropertyNameAndValuePair : storedProperties) {
			String storedPropertyName  = storedPropertyNameAndValuePair[0];
			String storedPropertyValue = storedPropertyNameAndValuePair[1];
			if (navigationStatePropertyName.equals(storedPropertyName)) {
				assertEquals("Wrong navigation state", navigationState.getNavigationStateName(), storedPropertyValue);
				found = true;
			}
		}
		assertTrue("Navigation state information was not found on the stored session properties", found);
	}

}

/** Allows test methods to receive SMSes */
class TestResponseReceiver implements IResponseReceiver {

	private ArrayList<OutgoingSMSDto> outgoingSMSes = new ArrayList<OutgoingSMSDto>();
	private int outgoingSMSesIndex = 0;

	@Override
	public void onMessage(OutgoingSMSDto outgoingMessage, IncomingSMSDto incomingMessage) {
		outgoingSMSes.add(outgoingMessage);
	}
	
	public OutgoingSMSDto[] getLastOutgoingSMSes() {
		OutgoingSMSDto[] lastOutgoingSMSes = new OutgoingSMSDto[outgoingSMSes.size()-outgoingSMSesIndex];
		int i=0;
		while (outgoingSMSesIndex<outgoingSMSes.size()) {
			lastOutgoingSMSes[i] = outgoingSMSes.get(outgoingSMSesIndex);
			outgoingSMSesIndex++;
			i++;
		}
		return lastOutgoingSMSes;
	}
	
}