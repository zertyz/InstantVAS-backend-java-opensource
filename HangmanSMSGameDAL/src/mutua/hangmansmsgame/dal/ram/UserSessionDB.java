package mutua.hangmansmsgame.dal.ram;

import java.util.Hashtable;

import mutua.hangmansmsgame.dal.IUserSessionDB;
import mutua.hangmansmsgame.dal.dto.UserSessionDto;

/** <pre>
 * SessionDB.java
 * ==============
 * (created by luiz, Jan 2, 2015)
 *
 * Implements a RAM version of 'IUserSessionDB'
 *
 * @see IUserSessionDB
 * @version $Id$
 * @author luiz
 */

public class UserSessionDB implements IUserSessionDB {
	
	private static Hashtable<String, UserSessionDto> db = new Hashtable<String, UserSessionDto>();

	@Override
	public void reset() {
		db.clear();
	}

	@Override
	public UserSessionDto getSession(String phone) {
		return db.get(phone);
	}

	@Override
	public void setSession(UserSessionDto userSession) {
		db.put(userSession.getPhone(), userSession);
	}

}
