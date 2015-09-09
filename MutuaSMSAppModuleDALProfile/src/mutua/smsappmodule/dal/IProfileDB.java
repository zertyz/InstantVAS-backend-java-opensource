package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * IProfileDB.java
 * ===============
 * (created by luiz, Aug 3, 2015)
 *
 * Defines access methods for the "User Profile" table
 *
 * @see mutua.smsappmodule.dal.ram.ProfileDB
 * @see mutua.smsappmodule.dal.postgresql.ProfileDB
 * @version $Id$
 * @author luiz
 */

public interface IProfileDB {
	
	/** Resets the database, for testing purposes */
	void reset() throws SQLException;

	/** Retrieves the 'ProfileDto' associated with 'user' from the database, or null if none exists */
	ProfileDto getProfileRecord(UserDto user) throws SQLException;
	
	/** Retrieves the 'ProfileDto' associated with the case insensitive 'nickname' */
	ProfileDto getProfileRecord(String nickname) throws SQLException;

	/** Makes the best effort to store the 'profile' record, returning the same object if successful, or a new object
	 *  if any values needed to be changed -- e.g. a new nickname is assigned in case of nickname collision */
	ProfileDto setProfileRecord(ProfileDto profile) throws SQLException;

}
