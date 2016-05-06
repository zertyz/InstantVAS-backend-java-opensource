package mutua.subscriptionengine;

/** <pre>
 * SubscriptionEngine.java
 * =======================
 * (created by luiz, Jan 25, 2015)
 *
 * Common API for SMS platforms to request carriers to sign up users to services
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public abstract class SubscriptionEngine {

	
	/** Return results for 'subscribeUser' operation */
    public enum ESubscriptionOperationStatus {
		/** Indicates a successful subscription */             OK,
		/** Server reported authentication failure */          AUTHENTICATION_ERROR,
		/** Indicates that the server could not be reached */  COMMUNICATION_ERROR,
		/** Server reported the user was already subscribed */ ALREADY_SUBSCRIBED,
    }

    /** Attempts to subscribe the user specified by 'userPhone' into the service */
    public abstract ESubscriptionOperationStatus subscribeUser(String userPhone);
    
    
    /** Return results for 'unsubscribeUser' operation */
    public enum EUnsubscriptionOperationStatus {
		/** Indicates a successful unsubscription */          OK,
		/** Server reported authentication failure */         AUTHENTICATION_ERROR,
		/** Indicates that the server could not be reached */ COMMUNICATION_ERROR,
		/** Server reported the user was not subscribed */    NOT_SUBSCRIBED,
    };

    /** Attempts to unsubscribe the user specified by 'userPhone' from the service */
    public abstract EUnsubscriptionOperationStatus unsubscribeUser(String userPhone);

}