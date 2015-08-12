package mutua.smsappmodule.dal;

import static org.junit.Assert.*;

import java.sql.SQLException;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationTests.*;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;

import org.junit.Test;

/** <pre>
 * ISessionDBTests.java
 * ====================
 * (created by luiz, Jul 24, 2015)
 *
 * Tests the normal-circumstance usage of {@link #ISessionDB} implementations
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ISessionDBBehavioralTests {
	
	private IUserDB    userDB    = DEFAULT_MODULE_DAL.getUserDB();
	private ISessionDB sessionDB = DEFAULT_MODULE_DAL.getSessionDB();

	
	/*******************
	** COMMON METHODS ** 
	*******************/
	

	/**********
	** TESTS **
	**********/
	
	@Test
	public void testNonExistingSession() throws SQLException {
		UserDto user = new UserDto("991234899");
		SessionDto session = sessionDB.getSession(user);
		assertNull("Non-existing sessions must be null", session);
	}
	
	@Test
	public void testSimpleUsage() throws InterruptedException, SQLException {
		String expectedPhone                   = "21991234899";
		String expectedNavigationStateProperty = "NavState";
		String expectedNavigationState         = "state";
		
		sessionDB.reset();
		
		UserDto user = userDB.assureUserIsRegistered(expectedPhone);
		SessionDto expectedSession = new SessionDto(user,
		                                            new String[][] {
		                                                {expectedNavigationStateProperty, expectedNavigationState}},
		                                            new String[][] {},
		                                            new String[] {}); 
		sessionDB.setSession(expectedSession);
		SessionDto observedSession = sessionDB.getSession(user);
		assertEquals("failed to retrieve session property name",  expectedNavigationStateProperty, observedSession.getStoredProperties()[0][0]);
		assertEquals("failed to retrieve session property value", expectedNavigationState,         observedSession.getStoredProperties()[0][1]);
		
//		String[][] timedSessions = {
//			{"211", "e1"},
//			{"212", "e2"},
//			{"213", "e3"},
//			{"214", "e4"},
//			{"215", "e5"},
//		};
//		fillTimedSessions(timedSessions);
//		// check
//		String[] latestPhones = sessionDB.getRecentlyUpdatedSessionPhoneNumbers(timedSessions.length);
//		int latestPhonesIndex = 0;
//		int timedSessionsIndex = timedSessions.length-1;
//		while ((latestPhonesIndex < latestPhones.length) && (timedSessionsIndex >= 0)) {
//			SessionDto latestSession = sessionDB.getSession(latestPhones[latestPhonesIndex]);
//			assertEquals("Failed to get latest sessions on the correct order", timedSessions[timedSessionsIndex][0], latestSession.getPhone());
//			latestPhonesIndex++;
//			timedSessionsIndex--;
//		}
//		assertEquals("Failed to retrieve latest sessions", latestPhones.length, latestPhonesIndex);
//		assertEquals("Failed to retrieve latest sessions", -1, timedSessionsIndex);
	}

//	@Test
//	public void testAttemptToFetchMoreThanWeHave() throws InterruptedException {
//		sessionDB.reset();
//		fillTimedSessions(new String[][] {
//			{"219911", "ee1"},
//			{"219912", "ee2"},
//			{"219913", "ee3"},
//			{"219914", "ee4"},
//			{"219915", "ee5"},
//		});
//		String[] latestPhones = sessionDB.getRecentlyUpdatedSessionPhoneNumbers(100);
//		assertEquals("Wrong number of elements while retrieving latest users", 5, latestPhones.length);
//	}
//	
//	@Test
//	public void testUniqueSessions() throws InterruptedException {
//		sessionDB.reset();
//		fillTimedSessions(new String[][] {
//			{"21991234899", "state 1"},
//			{"21991234899", "state 2"},
//			{"21991234899", "state 1"},
//			{"21991234899", "state 4"},
//			{"21991234899", "state 3"},
//		});
//		String[] latestPhones = sessionDB.getRecentlyUpdatedSessionPhoneNumbers(5);
//		assertEquals("Unique elements test during session enumeration didn't pass", 1, latestPhones.length);
//	}
//	
//	@Test
//	public void testUntimedSessionsFill() {
//		sessionDB.reset();
//		String[][] sessions = {
//			{"219911", "ee1"},
//			{"219912", "ee2"},
//			{"219913", "ee3"},
//			{"219914", "ee4"},
//			{"219915", "ee5"},
//		};
//		for (String[] sessionEntry : sessions) {
//			sessionDB.setSession(new SessionDto(sessionEntry[0], sessionEntry[1]));
//		}
//		String[] latestPhones = sessionDB.getRecentlyUpdatedSessionPhoneNumbers(100);
//		assertEquals("Incorrect number of elements retrieved when we add them atomically", sessions.length, latestPhones.length);
//	}

}
