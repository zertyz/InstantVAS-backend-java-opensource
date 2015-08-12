package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dto.UserDto;
import adapters.JDBCAdapter;
import adapters.dto.PreparedProcedureInvocationDto;

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
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("ResetTable");
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public UserDto assureUserIsRegistered(String phoneNumber) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("AssertUserIsRegistered");
		procedure.addParameter("PHONE", phoneNumber);
		int userId = (Integer) dba.invokeScalarProcedure(procedure);
		return new UserDto(userId, phoneNumber);
	}

}
