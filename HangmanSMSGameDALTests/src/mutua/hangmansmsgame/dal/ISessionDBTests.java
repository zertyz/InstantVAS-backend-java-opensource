package mutua.hangmansmsgame.dal;

import mutua.hangmansmsgame.dal.dto.SessionDto;

import org.junit.Test;

import static org.junit.Assert.*;

/** <pre>
 * ISessionDBTests.java
 * ====================
 * (created by luiz, Jan 7, 2015)
 *
 * Test the data access layers of 'ISessionDB' for correct behavior information handling
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ISessionDBTests {
	
	private ISessionDB sessionDB = DALFactory.getSessionDB();
	
	
	/*******************
	** COMMON METHODS ** 
	*******************/
	
	private void fillTimedSessions(String[][] timedSessions) throws InterruptedException {
		for (String[] sessionEntry : timedSessions) {
			Thread.sleep(100);
			sessionDB.setSession(new SessionDto(sessionEntry[0], sessionEntry[1]));
		}
	}
	
	/**********
	** TESTS **
	**********/
	
	@Test
	public void testSimpleUsage() throws InterruptedException {
		String expectedPhone           = "21991234899";
		String expectedNavigationState = "state";
		
		sessionDB.reset();
		
		sessionDB.setSession(new SessionDto(expectedPhone, expectedNavigationState));
		SessionDto retrievedSession = sessionDB.getSession(expectedPhone);
		assertEquals("failed to retrieve session", expectedNavigationState, retrievedSession.getNavigationState());
		
		String[][] timedSessions = {
			{"211", "e1"},
			{"212", "e2"},
			{"213", "e3"},
			{"214", "e4"},
			{"215", "e5"},
		};
		fillTimedSessions(timedSessions);
		// check
		String[] latestPhones = sessionDB.getRecentlyUpdatedSessionPhoneNumbers(timedSessions.length);
		int latestPhonesIndex = 0;
		int timedSessionsIndex = timedSessions.length-1;
		while ((latestPhonesIndex < latestPhones.length) && (timedSessionsIndex >= 0)) {
			SessionDto latestSession = sessionDB.getSession(latestPhones[latestPhonesIndex]);
			assertEquals("Failed to get latest sessions on the correct order", timedSessions[timedSessionsIndex][0], latestSession.getPhone());
			latestPhonesIndex++;
			timedSessionsIndex--;
		}
		assertEquals("Failed to retrieve latest sessions", latestPhones.length, latestPhonesIndex);
		assertEquals("Failed to retrieve latest sessions", -1, timedSessionsIndex);
	}

	@Test
	public void testAttemptToFetchMoreThanWeHave() throws InterruptedException {
		sessionDB.reset();
		fillTimedSessions(new String[][] {
			{"219911", "ee1"},
			{"219912", "ee2"},
			{"219913", "ee3"},
			{"219914", "ee4"},
			{"219915", "ee5"},
		});
		String[] latestPhones = sessionDB.getRecentlyUpdatedSessionPhoneNumbers(100);
		assertEquals("Wrong number of elements while retrieving latest users", 5, latestPhones.length);
	}
	
	@Test
	public void testUniqueSessions() throws InterruptedException {
		sessionDB.reset();
		fillTimedSessions(new String[][] {
			{"21991234899", "state 1"},
			{"21991234899", "state 2"},
			{"21991234899", "state 1"},
			{"21991234899", "state 4"},
			{"21991234899", "state 3"},
		});
		String[] latestPhones = sessionDB.getRecentlyUpdatedSessionPhoneNumbers(5);
		assertEquals("Unique elements test during session enumeration didn't pass", 1, latestPhones.length);
	}
	
	@Test
	public void testUntimedSessionsFill() {
		sessionDB.reset();
		String[][] sessions = {
			{"219911", "ee1"},
			{"219912", "ee2"},
			{"219913", "ee3"},
			{"219914", "ee4"},
			{"219915", "ee5"},
		};
		for (String[] sessionEntry : sessions) {
			sessionDB.setSession(new SessionDto(sessionEntry[0], sessionEntry[1]));
		}
		String[] latestPhones = sessionDB.getRecentlyUpdatedSessionPhoneNumbers(100);
		assertEquals("Incorrect number of elements retrieved when we add them atomically", sessions.length, latestPhones.length);
	}

}
