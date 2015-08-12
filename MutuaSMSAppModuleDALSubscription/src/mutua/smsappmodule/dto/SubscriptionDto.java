package mutua.smsappmodule.dto;

/** <pre>
 * SubscriptionDto.java
 * ====================
 * (created by luiz, Jul 24, 2015)
 *
 * Represents a retrieved/committable subscription information.
 * 
 * The subscription status may be either "subscribed" or "unsubscribed",
 * differentiated by the use of the two constructors.
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SubscriptionDto {
	
	public enum ESubscriptionChannel   {SMS, API, LIFECYCLE};
	public enum EUnsubscriptionChannel {SMS, API, LIFECYCLE};
	
	private UserDto                user;
	private boolean                isSubscribed;
	private long                   lastBilling;
	private ESubscriptionChannel   subscriptionChannel;
	private EUnsubscriptionChannel unsubscriptionChannel;
	
	
	/** Creates a new instance signaling the user is subscribed */
	public SubscriptionDto(UserDto user, ESubscriptionChannel channel) {
		this.user                  = user;
		this.isSubscribed          = true;
		this.lastBilling           = System.currentTimeMillis();	// presumed as "just billed"
		this.subscriptionChannel   = channel;
		this.unsubscriptionChannel = null;
	}
	
	/** Creates a new instance signaling the user is unsubscribed */
	public SubscriptionDto(UserDto user, EUnsubscriptionChannel channel) {
		this.user                  = user;
		this.isSubscribed          = isSubscribed;
		this.lastBilling           = -1l;
		this.subscriptionChannel   = null;
		this.unsubscriptionChannel = channel;
	}

	public UserDto getUser() {
		return user;
	}

	public boolean getIsSubscribed() {
		return isSubscribed;
	}

	public ESubscriptionChannel getSubscriptionChannel() {
		return subscriptionChannel;
	}

	public EUnsubscriptionChannel getUnsubscriptionChannel() {
		return unsubscriptionChannel;
	}
	

}
