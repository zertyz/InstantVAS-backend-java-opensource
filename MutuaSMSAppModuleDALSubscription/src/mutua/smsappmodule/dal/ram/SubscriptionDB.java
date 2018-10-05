package mutua.smsappmodule.dal.ram;

import java.util.Hashtable;

import mutua.smsappmodule.dal.ISubscriptionDB;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * SubscriptionDB.java
 * ===================
 * (created by luiz, Jul 24, 2015)
 *
 * Implements the RAM version of {@link ISubscriptionDB}.
 *
 * @see ISubscriptionDB
 * @version $Id$
 * @author luiz
 */

public class SubscriptionDB implements ISubscriptionDB {

	
	// data structures
	//////////////////
	
	private static Hashtable<UserDto, SubscriptionDto> subscriptions = new Hashtable<UserDto, SubscriptionDto>();
	

	// common methods
	/////////////////
	

	// ISubscriptionDB implementation
	/////////////////////////////////

	@Override
	public void reset() {
		subscriptions.clear();
	}

	@Override
	public SubscriptionDto getSubscriptionRecord(UserDto user) {
		return subscriptions.get(user);
	}

	@Override
	public boolean setSubscriptionRecord(SubscriptionDto subscription) {
		UserDto user = subscription.getUser();
		try {
			if (subscriptions.containsKey(user)) {
				return false;
			} else {
				return true;
			}
		} finally {
			subscriptions.put(user, subscription);
		}
	}
}
