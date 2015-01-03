package mutua.hangmansmsgame.dal;

import mutua.hangmansmsgame.dal.dto.SessionDto;

/** <pre>
 * ISessionDB.java
 * ===============
 * (created by luiz, Jan 2, 2015)
 *
 * Defines access methods for the "User Session" data base
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public interface ISessionDB {

	/** Reset the database, for testing purposes */
	void reset();
	
	/** Retrieve a 'UserSessionDto' from the database */
	SessionDto getSession(String phone);
	
	/** Stores a 'UserSessionDto' on the database */
	void setSession(SessionDto userSession);
}
