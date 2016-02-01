package mutua.smsappmodule.dal;

import static instantvas.tests.InstantVASSMSAppModuleTestsConfiguration.DEFAULT_SMS_MODULE_DAL;
import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.smsappmodule.dto.UserDto;

import org.junit.Test;

/** <pre>
 * IUserDBBehavioralTests.java
 * ===========================
 * (created by luiz, Jul 28, 2015)
 *
 * Tests the normal-circumstance usage of {@link #ISessionDB} implementations
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class IUserDBBehavioralTests {

	private IUserDB    userDB    = DEFAULT_SMS_MODULE_DAL.getUserDB();

	
	/*******************
	** COMMON METHODS ** 
	*******************/
	

	/**********
	** TESTS **
	**********/
	
	@Test
	public void testNonExistingAndExistingUser() throws SQLException {
		UserDto nonExistingUser = userDB.assureUserIsRegistered("991234899");
		UserDto existingUser    = userDB.assureUserIsRegistered("991234899");
		assertEquals("User ids    are not the same", nonExistingUser.getUserId(),      existingUser.getUserId());
		assertEquals("User phones are not the same", nonExistingUser.getPhoneNumber(), existingUser.getPhoneNumber());
	}

}
