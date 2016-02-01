package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dto.UserDto;
import adapters.JDBCAdapter;

import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter.Parameters.*;
import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter.UsersDBStatements.*;

/** <pre>
 * UserDB.java
 * ===========
 * (created by luiz, Jul 15, 2015)
 *
 * Implement the persistent, PostgreSQL version of {@link IUserDB}
 *
 * @see IUserDB
 * @version $Id$
 * @author luiz
 */

public class UserDB implements IUserDB {

	private JDBCAdapter dba;
	
	public UserDB() throws SQLException {
		dba = SMSAppModulePostgreSQLAdapter.getUsersDBAdapter();
	}

	@Override
	public void reset() throws SQLException {
		dba.invokeUpdateProcedure(ResetTable);
	}

	@Override
	public UserDto assureUserIsRegistered(String phoneNumber) throws SQLException {
		int userId = (Integer) dba.invokeScalarProcedure(AssertUserIsRegistered, PHONE, phoneNumber);
		return new UserDto(userId, phoneNumber);
	}

}
