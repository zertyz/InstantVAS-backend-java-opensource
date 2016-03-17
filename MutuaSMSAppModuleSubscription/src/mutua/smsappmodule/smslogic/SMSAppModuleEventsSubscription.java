package mutua.smsappmodule.smslogic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mutua.events.DirectEventLink;
import mutua.events.EventServer;
import mutua.events.IEventLink;
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
 * This class implements "Mutua SMS Module Event Producers" pattern
 * 
 * TODO these commands should be tied to an instance of the subscription commands -- the way they are now,
 *      every subscriber will receive the events for every sms service running on the machine
 *
 * @see SMSAppModuleListenersHangman
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleEventsSubscription extends EventServer<ESMSAppModuleEventsSubscription> {
	
	// implements the 'EventConsumer' & 'EventListener' Events Enumeration & Annotation pattern
	
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) public @interface SubscriptionEvent {
		ESMSAppModuleEventsSubscription[] value();
	}

	
	/** naming convention: *_NOTIFICATION are listenable events */
	public enum ESMSAppModuleEventsSubscription {
		/** this event is fired upon a successful user subscription to the platform.
		 *  Reference implementation:
		 *  @EventListener("USER_JUST_SUBSCRIBED_NOTIFICATION")
		 *  public void onSubscription(SubscriptionDto subscriptionRecord) {... */
		USER_JUST_SUBSCRIBED_NOTIFICATION,
		USER_JUST_UNSUBSCRIBED_NOTIFICATION,
	}

	
	public SMSAppModuleEventsSubscription() {
		super(new DirectEventLink<ESMSAppModuleEventsSubscription>(ESMSAppModuleEventsSubscription.class, new Class[] {SubscriptionEvent.class}));
	}

	
	/*******************
	** IMPLEMENTATION **
	*******************/
	
	protected void dispatchSubscriptionNotification(SubscriptionDto subscriptionRecord) {
		dispatchListenableEvent(ESMSAppModuleEventsSubscription.USER_JUST_SUBSCRIBED_NOTIFICATION, subscriptionRecord);
	}
	
	protected void dispatchUnsubscriptionNotification(SubscriptionDto unsubscriptionRecord) {
		dispatchListenableEvent(ESMSAppModuleEventsSubscription.USER_JUST_UNSUBSCRIBED_NOTIFICATION, unsubscriptionRecord);
	}

}
