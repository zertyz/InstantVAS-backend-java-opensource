package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * ISessionDB.java
 * ===============
 * (created by luiz, Jul 15, 2015)
 *
 * Defines access methods for the "Sessions" database
 *
 * @see mutua.smsappmodule.dal.ram.SessionDB
 * @see mutua.smsappmodule.dal.postgresql.SessionDB
 * @version $Id$
 * @author luiz
 */

public interface ISessionDB {
	
	/** Resets the database, for testing purposes */
	void reset() throws SQLException;
	
	/** Retrieves a 'SessionDto' from the database, or null if none exists */
	SessionDto getSession(UserDto user) throws SQLException;
	
	/** Stores a 'SessionDto' on the database */
	void setSession(SessionDto session) throws SQLException;

}
