package mutua.smsappmodule.dal;

import static org.junit.Assert.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationSubscriptionTests.*;

import java.sql.SQLException;

import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.SubscriptionDto.ESubscriptionChannel;
import mutua.smsappmodule.dto.SubscriptionDto.EUnsubscriptionChannel;
import mutua.smsappmodule.dto.UserDto;

import org.junit.Test;

/** <pre>
 * ISubscriptionDBTests.java
 * =========================
 * (created by luiz, Jul 25, 2015)
 *
 * Tests the normal-circumstance usage of {@link ISubscriptionDB} implementations
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ISubscriptionDBBehavioralTests {
	
	
	private IUserDB         userDB         = DEFAULT_MODULE_DAL.getUserDB();
	private ISubscriptionDB subscriptionDB = DEFAULT_SUBSCRIPTION_DAL.getSubscriptionDB();

	
	/*******************
	** COMMON METHODS ** 
	*******************/
	

	/**********
	** TESTS **
	**********/
	
	@Test
	public void testNonExistingSubscriptionRecord() throws SQLException {
		
		SMSAppModuleTestCommons.resetTables();

		UserDto         user         = userDB.assureUserIsRegistered("21991234899");
		SubscriptionDto subscription = subscriptionDB.getSubscriptionRecord(user);
		assertNull("Non-existing subscriptions must be null", subscription);
	}
	
	@Test
	public void testSimpleUsage() throws SQLException {
		
		SMSAppModuleTestCommons.resetTables();

		UserDto         user                  = userDB.assureUserIsRegistered("21991234899");
		SubscriptionDto storedSubscription    = null;
		SubscriptionDto retrievedSubscription = null;
				
		// test subscription
		storedSubscription = new SubscriptionDto(user, ESubscriptionChannel.SMS);
		subscriptionDB.setSubscriptionRecord(storedSubscription);
		retrievedSubscription = subscriptionDB.getSubscriptionRecord(user);
		assertEquals("Stored and retrieved 'user' doesn't match",              storedSubscription.getUser().getPhoneNumber(),                retrievedSubscription.getUser().getPhoneNumber());
		assertEquals("Stored and retrieved 'isSubscribed' don't match",        storedSubscription.getIsSubscribed(),                         retrievedSubscription.getIsSubscribed());
		assertEquals("Stored and retrieved 'subscriptionChanneç' don't match", storedSubscription.getSubscriptionChannel(),                  retrievedSubscription.getSubscriptionChannel());
		
		// test unsubscription
		storedSubscription = new SubscriptionDto(user, EUnsubscriptionChannel.API);
		subscriptionDB.setSubscriptionRecord(storedSubscription);
		retrievedSubscription = subscriptionDB.getSubscriptionRecord(user);
		assertEquals("Stored and retrieved 'user' doesn't match",                storedSubscription.getUser().getPhoneNumber(),                retrievedSubscription.getUser().getPhoneNumber());
		assertEquals("Stored and retrieved 'isSubscribed' doesn't match",        storedSubscription.getIsSubscribed(),                         retrievedSubscription.getIsSubscribed());
		assertEquals("Stored and retrieved 'subscriptionChannel' doesn't match", storedSubscription.getSubscriptionChannel(),                  retrievedSubscription.getSubscriptionChannel());
		
	}
}
