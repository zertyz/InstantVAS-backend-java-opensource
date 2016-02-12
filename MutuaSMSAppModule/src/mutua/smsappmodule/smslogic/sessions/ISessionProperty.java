package mutua.smsappmodule.smslogic.sessions;

/** <pre>
 * ISessionProperty.java
 * =====================
 * (created by luiz, Jul 14, 2015)
 *
 * Represents a session entry, used by SMS Applications and Games to store context data. For instance,
 * the current navigation state, the last composite help message show, etc.
 * 
 * Implementing classes must use the "Instant VAS SMSApp Session Properties" design pattern, described
 * bellow:
 * 
 * {@code
 * 	get it from the help module by now
 * }
 *
 * @version $Id$
 * @author luiz
 */

public interface ISessionProperty {

	/** Returns the human readable / database indexable and storable session property name.
	* May be implemented as 'return this.name().substring(3);' in order to skip the prefix "spr" */
	String getPropertyName();
	
}
