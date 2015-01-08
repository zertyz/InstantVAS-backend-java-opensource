package mutua.hangmansmsgame.dal;

import mutua.hangmansmsgame.dal.dto.SessionDto;

/** <pre>
 * ISessionDB.java
 * ===============
 * (created by luiz, Jan 2, 2015)
 *
 * Defines access methods for the "User Session" data base
 *
 * @see ram.SessionDB, postgresql.SessionDB
 * @version $Id$
 * @author luiz
 */

public interface ISessionDB {

	/** Resets the database, for testing purposes */
	void reset();
	
	/** Retrieves a 'UserSessionDto' from the database */
	SessionDto getSession(String phone);
	
	/** Stores a 'UserSessionDto' on the database */
	void setSession(SessionDto session);
	
	/** Retrieve the most recently used session phone numbers, up to 'limit' entries */
	String[] getRecentlyUpdatedSessionPhoneNumbers(int limit);
}
