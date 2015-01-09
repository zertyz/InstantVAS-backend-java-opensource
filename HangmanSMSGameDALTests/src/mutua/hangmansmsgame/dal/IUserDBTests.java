package mutua.hangmansmsgame.dal;

import static org.junit.Assert.*;

import org.junit.Test;

/** <pre>
 * IUserDBTests.java
 * =================
 * (created by luiz, Jan 7, 2015)
 *
 * Test the data access layers of 'IUserDB' for correct behavior information handling
 *
 * @see IUserDB
 * @version $Id$
 * @author luiz
 */

public class IUserDBTests {
	
	private IUserDB userDB = DALFactory.getUserDB();

	
	@Test
	public void testSimpleUsage() {
		userDB.reset();
		
		// non existing user tests
		assertFalse("An empty database should return false when queried if a user exists", userDB.isUserOnRecord("21991234899"));
		assertNull("An empty database should return null when queried for the phone number of a user", userDB.getUserPhoneNumber("Dom"));
		assertNull("An empty database should return null when queried for the nickname of a user", userDB.getUserNickname("21991234899"));
		
		// registration tests
		assertTrue("Failed to register user", userDB.checkAvailabilityAndRecordNickname("21991234899", "Dom"));
		
		// existing user tests
		assertTrue("Failed to correctly identify that the user is registered", userDB.isUserOnRecord("21991234899"));
		assertEquals("Failed to correctly retrieve the phone number of a registered user, by it's case insensitive nickname ", "21991234899", userDB.getUserPhoneNumber("dom"));
		assertEquals("Failed to correctly retrieve the nickname of a registered user", "Dom", userDB.getUserNickname("21991234899"));
		
		// check availability
		assertFalse("Failed to prevent two users from sharing the same case insensitive nickname", userDB.checkAvailabilityAndRecordNickname("21998019167", "dom"));
		assertTrue("Failed to detect & allow the same user to \"change\" his own nickname to the same case insensitive value", userDB.checkAvailabilityAndRecordNickname("21991234899", "DoM"));
		
	}

}
