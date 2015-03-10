package mutua.hangmansmsgame.dal.postgresql;

import java.sql.SQLException;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.DALFactory.EDataAccessLayers;
import mutua.hangmansmsgame.dal.ISessionDB;
import mutua.hangmansmsgame.dal.IUserDB;
import mutua.hangmansmsgame.dal.dto.SessionDto;

/** <pre>
 * SessionDB.java
 * ==============
 * (created by luiz, Mar 10, 2015)
 *
 * Implements the hibrid PostgreSQL/RAM version of 'ISessionDB'
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SessionDB implements ISessionDB {
	
	private final ISessionDB ramDB;
	private final IUserDB   userDB;
	
	public SessionDB() {
		try {
			userDB = new mutua.hangmansmsgame.dal.postgresql.UserDB();
			ramDB  = new mutua.hangmansmsgame.dal.ram.SessionDB();
		} catch (SQLException e) {
			throw new RuntimeException("Error instantiating userDB", e);
		}
	}

	@Override
	public void reset() {
		ramDB.reset();
	}

	@Override
	public SessionDto getSession(String phone) throws SQLException {
		SessionDto session = ramDB.getSession(phone);
		if (session == null) {
			if (userDB.isUserOnRecord(phone)) {
				return new SessionDto(phone, "EXISTING_USER");
			}
		}
		return session;
	}

	@Override
	public void setSession(SessionDto session) {
		ramDB.setSession(session);
	}

	@Override
	public String[] getRecentlyUpdatedSessionPhoneNumbers(int limit) {
		return ramDB.getRecentlyUpdatedSessionPhoneNumbers(limit);
	}

}
