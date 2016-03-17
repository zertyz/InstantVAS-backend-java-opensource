package mutua.smsappmodule.smslogic;

import java.sql.SQLException;

import mutua.events.EventClient;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.smslogic.SMSAppModuleEventsSubscription.ESMSAppModuleEventsSubscription;
import mutua.smsappmodule.smslogic.SMSAppModuleEventsSubscription.SubscriptionEvent;

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
	
	private final SMSAppModuleCommandsHangman hangmanCommands;
	
	protected SMSAppModuleListenersHangman(SMSAppModuleCommandsHangman hangmanCommands, SMSAppModuleEventsSubscription subscriptionEventsServer) throws IndirectMethodNotFoundException {
		this.hangmanCommands = hangmanCommands;
		subscriptionEventsServer.addListener(subscriptionEventListener);
	}
	
	
	/*******************************
	** SUBSCRIPTION MODULE EVENTS **
	*******************************/
	
	/** This event listener is the responsible for making all users have a default nickname */
	private EventClient<ESMSAppModuleEventsSubscription> subscriptionEventListener = new EventClient<ESMSAppModuleEventsSubscription>() {
		
		@SubscriptionEvent(ESMSAppModuleEventsSubscription.USER_JUST_SUBSCRIBED_NOTIFICATION)
		public void onSubscription(SubscriptionDto subscriptionRecord) throws SQLException {
			hangmanCommands.assureUserHasANickname(subscriptionRecord.getUser());
		}
		
	};
	
}
