package mutua.smsappmodule.smslogic;

import java.sql.SQLException;

import mutua.events.EventClient;
import mutua.events.annotations.EventListener;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.smslogic.SMSAppModuleEventsSubscription.ESMSAppModuleEventsSubscription;

/** <pre>
 * SMSAppModuleListenersHangman.java
 * =================================
 * (created by luiz, Nov 17, 2015)
 *
 * This class is responsible for listening to interesting events produced by other SMSApp Modules. 
 * 
 * This class defines the Mutua SMS Module Event Listeners Pattern
 *
 * @see SMSAppModuleEventsSubscription
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleListenersHangman {
	
	
	/*******************************
	** SUBSCRIPTION MODULE EVENTS **
	*******************************/
	
	private static EventClient<ESMSAppModuleEventsSubscription> subscriptionEventListener = new EventClient<ESMSAppModuleEventsSubscription>() {
		
		@EventListener("USER_JUST_SUBSCRIBED_NOTIFICATION")
		/** This event listener is the responsible for making all users have a default nickname */
		public void onSubscription(SubscriptionDto subscriptionRecord) throws SQLException {
			SMSAppModuleCommandsHangman.assureUserHasANickname(subscriptionRecord.getUser());
		}
		
	};
	

	public static void registerEventListeners() throws IndirectMethodNotFoundException {
		SMSAppModuleEventsSubscription.addListener(subscriptionEventListener);
	}

}
