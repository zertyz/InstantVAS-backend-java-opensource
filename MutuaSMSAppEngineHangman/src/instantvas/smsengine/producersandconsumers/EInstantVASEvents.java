package instantvas.smsengine.producersandconsumers;

import mutua.events.IEventLink;

/** <pre>
 * EInstantVASEvents.java
 * ======================
 * (created by luiz, Mar 16, 2016)
 *
 * Enumerates the events an Instant VAS game or application is able to generate and/or process.
 *
 * @see MOProducer
 * @see MOConsumer
 * @see MTProducer
 * @see MTConsumer
 * @see SubscriptionRenewalProducer
 * @see SubscriptionRenewalConsumer
 * @see SubscriptionCancellationProducer
 * @see SubscriptionCancellationConsumer
 * @see IEventLink
 * @version $Id$
 * @author luiz
*/

public enum EInstantVASEvents {
	/** Happens when the application receives an MO */
	MO_ARRIVED,
	/** Happens when an MT, generated in response to an MO, is ready for delivery */
	INTERACTIVE_MT,
	/** Happens when a lifecycle rule decides a given user had it's subscription renewed */
	SUBSCRIPTION_RENEWED,
	/** Happens when a lifecycle rule decides a given user had it's subscription cancelled */
	SUBSCRIPTION_CANCELLED,
}