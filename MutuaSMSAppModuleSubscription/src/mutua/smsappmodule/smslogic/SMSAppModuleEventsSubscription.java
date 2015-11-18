package mutua.smsappmodule.smslogic;

import mutua.events.DirectEventLink;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.smslogic.SMSAppModuleEventsSubscription.ESMSAppModuleEventsSubscription;

/** <pre>
 * SMSAppModuleEventsSubscription.java
 * ===================================
 * (created by luiz, Nov 17, 2015)
 *
 * This class is responsible for dispatching subscription events to every
 * interested entity, as well as managing such entities, which should report this
 * class about their interest.
 * 
 * This class defines the Mutua SMS Module Event Producers Pattern
 *
 * @see SMSAppModuleListenersHangman
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleEventsSubscription extends EventServer<ESMSAppModuleEventsSubscription> {
	
	/** naming convention: *_NOTIFICATION are listenable events */
	public enum ESMSAppModuleEventsSubscription {
		/** this event is fired upon a successful user subscription to the platform.
		 *  Reference implementation:
		 *  @EventListener("USER_JUST_SUBSCRIBED_NOTIFICATION")
		 *  public void onSubscription(SubscriptionDto subscriptionRecord) {... */
		USER_JUST_SUBSCRIBED_NOTIFICATION,
		USER_JUST_UNSUBSCRIBED_NOTIFICATION,
	}

	
	/**************
	** SINGLETON **
	**************/
	
	private static IEventLink<ESMSAppModuleEventsSubscription> eventLink = new DirectEventLink<ESMSAppModuleEventsSubscription>(ESMSAppModuleEventsSubscription.class);
	private static SMSAppModuleEventsSubscription singleton = new SMSAppModuleEventsSubscription(eventLink);
	
	protected SMSAppModuleEventsSubscription(IEventLink<ESMSAppModuleEventsSubscription> link) {
		super(link);
	}

	
	/*******************
	** IMPLEMENTATION **
	*******************/
	
	public static void addListener(EventClient<ESMSAppModuleEventsSubscription> eventClient) throws IndirectMethodNotFoundException {
		eventLink.addClient(eventClient);
	}
	
	public static void removeListener(EventClient<ESMSAppModuleEventsSubscription> eventClient) {
		eventLink.deleteClient(eventClient);
	}
	
	protected static void dispatchSubscriptionNotification(SubscriptionDto subscriptionRecord) {
		singleton.dispatchListenableEvent(ESMSAppModuleEventsSubscription.USER_JUST_SUBSCRIBED_NOTIFICATION, subscriptionRecord);
	}
	
	protected static void dispatchUnsubscriptionNotification(SubscriptionDto unsubscriptionRecord) {
		singleton.dispatchListenableEvent(ESMSAppModuleEventsSubscription.USER_JUST_UNSUBSCRIBED_NOTIFICATION, unsubscriptionRecord);
	}

}
