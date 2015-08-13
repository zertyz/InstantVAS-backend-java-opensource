package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * ISubscriptionDB.java
 * ====================
 * (created by luiz, Jul 24, 2015)
 *
 * Defines access methods for the "Subscriptions" table
 *
 * @see mutua.smsappmodule.dal.ram.SubscriptionDB
 * @see mutua.smsappmodule.dal.postgresql.SubscriptionDB
 * @version $Id$
 * @author luiz
 */

public interface ISubscriptionDB {
	
	/** Resets the database, for testing purposes */
	void reset() throws SQLException;

	/** Retrieves the 'SubscriptionDto' associated with 'user' from the database, or null if none exists */
	SubscriptionDto getSubscriptionRecord(UserDto user) throws SQLException;
	
	/** Stores the 'subscription' record, returning true if the record existed and false otherwise */
	boolean setSubscriptionRecord(SubscriptionDto subscription) throws SQLException;

}
