package mutua.smsappmodule.dal.mvstore;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import adapters.MVStoreAdapter;
import mutua.smsappmodule.dal.ISubscriptionDB;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.SubscriptionDto.ESubscriptionChannel;
import mutua.smsappmodule.dto.SubscriptionDto.EUnsubscriptionChannel;
import mutua.smsappmodule.dto.UserDto;

/** SubscriptionDB.java
 * ====================
 * (created by luiz, Sep 11, 2018)
 *
 * Implements the MVStore version of {@link ISubscriptionDB}.
 * 
 * @see ISubscriptionDB
 * @author luiz
*/

public class SubscriptionDB implements ISubscriptionDB {
	
	// subscriptions := {[phone]=(SubscriptionDto){(ESubscriptionChannel)subscriptionChannel, (EUnsubscriptionChannel)unsubscriptionChannel}, ...}
	private MVMap<String, Object[]> subscriptions;
	
	
	public SubscriptionDB() {
		MVStore store = MVStoreAdapter.getStore();
		subscriptions = store.openMap("smsappmodulesubscription.Subscriptions", new MVMap.Builder<String, Object[]>());
	}

	@Override
	public void reset() {
		subscriptions.clear();
	}

	@Override
	public SubscriptionDto getSubscriptionRecord(UserDto user) {
		Object[] objectSubscription = subscriptions.get(user.getPhoneNumber());
		if (objectSubscription == null) {
			return null;
		}
		ESubscriptionChannel   subscriptionChannel   = (ESubscriptionChannel)   objectSubscription[0];
		EUnsubscriptionChannel unsubscriptionChannel = (EUnsubscriptionChannel) objectSubscription[1];
		if (subscriptionChannel != null) {
			return new SubscriptionDto(user, subscriptionChannel);
		} else {
			return new SubscriptionDto(user, unsubscriptionChannel);
		}
	}

	@Override
	public boolean setSubscriptionRecord(SubscriptionDto subscription) {
		String phone = subscription.getUser().getPhoneNumber();
		Object[] objectSubscription = {subscription.getSubscriptionChannel(), subscription.getUnsubscriptionChannel()};
		if (subscriptions.containsKey(phone)) {
			subscriptions.replace(phone, objectSubscription);
			return true;
		} else {
			subscriptions.put(phone, objectSubscription);
			return false;
		}
	}

}
