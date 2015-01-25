package mutua.hangmansmsgame.config;

import mutua.icc.instrumentation.Instrumentation;
import mutua.subscriptionengine.SubscriptionEngine;
import mutua.subscriptionengine.TestableSubscriptionAPI;

/** <pre>
 * Configuration.java
 * ==================
 * (created by luiz, Jan 21, 2015)
 *
 * Defines common configuration variables on load
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class Configuration {
	
	/** to be defined by the request receivers (servlet, console, tests, icc app, ...) */
	public static Instrumentation<?, ?> log;
	
	/** also to be defined by the request receivers (servlet, console, tests, icc app, ...) */
	public static SubscriptionEngine SUBSCRIPTION_ENGINE;
	
	public static String DEFAULT_NICKNAME_PREFIX    = "Guest";
	public static String SUBSCRIPTION_CHANNEL_NAME  = "HangMan";
	public static long   INVITATION_TIMEOUT_MILLIS = (1000*60)*20;


}
