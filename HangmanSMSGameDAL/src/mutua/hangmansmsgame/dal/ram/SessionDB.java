package mutua.hangmansmsgame.dal.ram;

import java.util.Hashtable;

import mutua.hangmansmsgame.dal.ISessionDB;
import mutua.hangmansmsgame.dal.dto.SessionDto;

/** <pre>
 * SessionDB.java
 * ==============
 * (created by luiz, Jan 2, 2015)
 *
 * Implements a RAM version of 'ISessionDB'
 *
 * @see ISessionDB
 * @version $Id$
 * @author luiz
 */

public class SessionDB implements ISessionDB {
	
	
	// data structures
	//////////////////
	
	/** sessions := {[phone] = SessionDto, ...} */
	private static Hashtable<String, SessionDto> sessions = new Hashtable<String, SessionDto>();
	
	
	// ISessionDB implementation
	////////////////////////////

	@Override
	public void reset() {
		sessions.clear();
	}

	@Override
	public SessionDto getSession(String phone) {
		return sessions.get(phone);
	}

	@Override
	public void setSession(SessionDto session) {
		sessions.put(session.getPhone(), session);
	}

}
