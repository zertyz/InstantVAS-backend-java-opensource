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
	
	private final SMSAppModuleCommandsHangman hangmanCommands;
	
	private SMSAppModuleListenersHangman(SMSAppModuleCommandsHangman hangmanCommands) {
		this.hangmanCommands = hangmanCommands;
	}
	
	
	/*******************************
	** SUBSCRIPTION MODULE EVENTS **
	*******************************/
	
	/** This event listener is the responsible for making all users have a default nickname */
	private EventClient<ESMSAppModuleEventsSubscription> subscriptionEventListener = new EventClient<ESMSAppModuleEventsSubscription>() {
		
		@EventListener("USER_JUST_SUBSCRIBED_NOTIFICATION")
		public void onSubscription(SubscriptionDto subscriptionRecord) throws SQLException {
			hangmanCommands.assureUserHasANickname(subscriptionRecord.getUser());
		}
		
	};
	
	// TODO pros eventos voltarem a funcionar, o hangman commands deve receber a instancia do subscription commands e o subscription commands tem que declarar uma instancia do SMSAppModuleEventsSubscription e, lógico, todos os métodos estáticos desta última classe têm de ser removidos

	public static SMSAppModuleListenersHangman instantiateAndRegisterEventListeners(SMSAppModuleCommandsHangman hangmanCommands) throws IndirectMethodNotFoundException {
		SMSAppModuleListenersHangman instance = new SMSAppModuleListenersHangman(hangmanCommands);
		SMSAppModuleEventsSubscription.addListener(instance.subscriptionEventListener);
		return instance;
	}

}
