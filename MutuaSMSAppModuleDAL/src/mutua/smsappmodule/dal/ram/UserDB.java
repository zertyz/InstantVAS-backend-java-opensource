package mutua.smsappmodule.dal.ram;

import java.sql.SQLException;
import java.util.Hashtable;

import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * UserDB.java
 * ===========
 * (created by luiz, Jul 15, 2015)
 *
 * Implements the RAM version of {@link IUserDB}
 *
 * @see IUserDB
 * @version $Id$
 * @author luiz
 */

public class UserDB implements IUserDB {

	
	// data structures
	//////////////////
	
	private static Hashtable<String, UserDto> users = new Hashtable<String, UserDto>();
	
	
	// common methods
	/////////////////
	

	// IUserDB implementation
	/////////////////////////

	
	@Override
	public void reset() throws SQLException {
		users.clear();
	}

	@Override
	public synchronized UserDto assureUserIsRegistered(String phoneNumber) throws SQLException {
		UserDto user = users.get(phoneNumber);
		if (user == null) {
			user = new UserDto(users.size(), phoneNumber);
			users.put(phoneNumber, user);
		}
		return user;
	}

}
