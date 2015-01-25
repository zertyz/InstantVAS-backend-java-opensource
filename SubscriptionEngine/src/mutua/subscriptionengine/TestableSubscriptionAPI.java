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

	
	/** registeredUsers := { [userPhone] = "|channelName1||channelName2|...", ...} */
	private static Hashtable<String, String> registeredUsers = new Hashtable<String, String>();;
	
	
	public static void reset() {
		registeredUsers.clear();
	}
	
	
	public TestableSubscriptionAPI(Instrumentation<?, ?> log) {
		super(log);
	}
	
	private boolean _isUserSubscribed(String userPhone, String channelName) {
		if (!registeredUsers.containsKey(userPhone)) {
			return false;
		}
		String subscribedChannels = registeredUsers.get(userPhone);
		if (subscribedChannels.indexOf("|"+channelName+"|") < 0) {
			return false;
		} else {
			return true;
		}
	}

	private void _subscribeUser(String userPhone, String channelName) {
		String subscribedChannels;
		if (registeredUsers.containsKey(userPhone)) {
			subscribedChannels = registeredUsers.get(userPhone);
		} else {
			subscribedChannels = "";
		}
		subscribedChannels += "|"+channelName+"|";
		registeredUsers.put(userPhone, subscribedChannels);
	}
	
	private void _unsubscribeUser(String userPhone, String channelName) {
		String subscribedChannels;
		if (registeredUsers.containsKey(userPhone)) {
			subscribedChannels = registeredUsers.get(userPhone);
		} else {
			return;
		}
		subscribedChannels.replaceAll("|"+channelName+"|", "");
		registeredUsers.put(userPhone, subscribedChannels);
	}

	@Override
	public ESubscriptionOperationStatus subscribeUser(String userPhone, String channelName) {
		
		String request = "subscribe '"+userPhone+"' to '"+channelName+"'";
		
		if (_isUserSubscribed(userPhone, channelName)) {
			log.reportEvent(SUBSCRIPTION_OK, REQUEST, request, RESPONSE, "_isUserSubscribed == true");
			return ESubscriptionOperationStatus.ALREADY_SUBSCRIBED;
		} else {
			_subscribeUser(userPhone, channelName);
			log.reportEvent(SUBSCRIPTION_OK, REQUEST, request, RESPONSE, "_isUserSubscribed == false");
			return ESubscriptionOperationStatus.OK;
		}
	}

	@Override
	public EUnsubscriptionOperationStatus unsubscribeUser(String userPhone,	String channelName) {
		
		String request = "unsubscribe '"+userPhone+"' from '"+channelName+"'";

		if (!_isUserSubscribed(userPhone, channelName)) {
			log.reportEvent(UNSUBSCRIPTION_NOT_SUBSCRIBED, REQUEST, request, RESPONSE, "_isUserSubscribed == false");
			return EUnsubscriptionOperationStatus.NOT_SUBSCRIBED;
		} else {
			_unsubscribeUser(userPhone, channelName);
			log.reportEvent(UNSUBSCRIPTION_OK, REQUEST, request, RESPONSE, "_isUserSubscribed == true");
			return EUnsubscriptionOperationStatus.OK;
		}
	}

}
