package mutua.subscriptionengine;

import static mutua.subscriptionengine.ESubscriptionEngineInstrumentationEvents.*;
import static mutua.subscriptionengine.ESubscriptionEngineInstrumentationProperties.*;

import java.util.Hashtable;

import mutua.icc.instrumentation.Instrumentation;

/** <pre>
 * TestableSubscriptionAPI.java
 * ============================
 * (created by luiz, Jan 25, 2015)
 *
 * Testable reference implementation for subscription engines
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class TestableSubscriptionAPI extends SubscriptionEngine {

	
	/** registeredUsers := { [userPhone] = "#channelName1##channelName2#...", ...} */
	private static final Hashtable<String, String> registeredUsers = new Hashtable<String, String>();
	
	private final String channelName;
	
	
	public void reset() {
		registeredUsers.clear();
	}
	
	
	public TestableSubscriptionAPI(Instrumentation<?, ?> log, String channelName) {
		super(log);
		this.channelName = channelName;
	}
	
	public boolean _isUserSubscribed(String userPhone) {
		if (!registeredUsers.containsKey(userPhone)) {
			return false;
		}
		String subscribedChannels = registeredUsers.get(userPhone);
		if (subscribedChannels.indexOf("#"+channelName+"#") < 0) {
			return false;
		} else {
			return true;
		}
	}

	private void _subscribeUser(String userPhone) {
		String subscribedChannels;
		if (registeredUsers.containsKey(userPhone)) {
			subscribedChannels = registeredUsers.get(userPhone);
		} else {
			subscribedChannels = "";
		}
		subscribedChannels += "#"+channelName+"#";
		registeredUsers.put(userPhone, subscribedChannels);
	}
	
	private void _unsubscribeUser(String userPhone) {
		String subscribedChannels;
		if (registeredUsers.containsKey(userPhone)) {
			subscribedChannels = registeredUsers.get(userPhone);
		} else {
			return;
		}
		subscribedChannels = subscribedChannels.replaceAll("#"+channelName+"#", "");
		registeredUsers.put(userPhone, subscribedChannels);
	}

	@Override
	public ESubscriptionOperationStatus subscribeUser(String userPhone) {
		
		String request = "subscribe '"+userPhone+"' to '"+channelName+"'";
		
		if (_isUserSubscribed(userPhone)) {
			log.reportEvent(SUBSCRIPTION_OK, REQUEST, request, RESPONSE, "_wasUserSubscribed == true");
			return ESubscriptionOperationStatus.ALREADY_SUBSCRIBED;
		} else {
			_subscribeUser(userPhone);
			log.reportEvent(SUBSCRIPTION_OK, REQUEST, request, RESPONSE, "_wasUserSubscribed == false");
			return ESubscriptionOperationStatus.OK;
		}
	}

	@Override
	public EUnsubscriptionOperationStatus unsubscribeUser(String userPhone) {
		
		String request = "unsubscribe '"+userPhone+"' from '"+channelName+"'";

		if (!_isUserSubscribed(userPhone)) {
			log.reportEvent(UNSUBSCRIPTION_NOT_SUBSCRIBED, REQUEST, request, RESPONSE, "_wasUserSubscribed == false");
			return EUnsubscriptionOperationStatus.NOT_SUBSCRIBED;
		} else {
			_unsubscribeUser(userPhone);
			log.reportEvent(UNSUBSCRIPTION_OK, REQUEST, request, RESPONSE, "_wasUserSubscribed == true");
			return EUnsubscriptionOperationStatus.OK;
		}
	}

}
