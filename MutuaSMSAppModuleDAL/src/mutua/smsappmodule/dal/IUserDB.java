package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * IUserDB.java
 * ============
 * (created by luiz, Jul 15, 2015)
 *
 * Defines access methods for the "Users" database
 *
 * @see mutua.smsappmodule.dal.ram.UserDB
 * @see mutua.smsappmodule.dal.postgresql.UserDB
 * @version $Id$
 * @author luiz
 */

public interface IUserDB {
	
	/** Reset the database, for testing purposes */
	public abstract void reset() throws SQLException;

	/** given a 'phoneNumber', assures that user is ready to operate on the database, returning it's
	 * 'UserDto' whether it was an old or new user */
	public abstract UserDto assureUserIsRegistered(String phoneNumber) throws SQLException;

}
