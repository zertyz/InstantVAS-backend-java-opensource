package mutua.hangmansmsgame.dal.postgresql;

import java.sql.SQLException;

import mutua.hangmansmsgame.dal.IUserDB;
import adapters.JDBCAdapter;
import adapters.dto.PreparedProcedureInvocationDto;

/** <pre>
 * UserDB.java
 * ===========
 * (created by luiz, Jan 26, 2015)
 *
 * Implements the persistent, PostgreSQL version of 'IUserDB'
 *
 * @see IUserDB
 * @version $Id$
 * @author luiz
 */

public class UserDB implements IUserDB {

	private JDBCAdapter dba;
	
	public UserDB() throws SQLException {
		dba = HangmanSMSGamePostgreSQLAdapters.getUserDBAdapter();
	}
	
	@Override
	public void reset() throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("ResetTable");
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public String getCorrectlyCasedNickname(String nickname) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectCorrectlyCasedNick");
		procedure.addParameter("NICK", nickname);
		return (String)dba.invokeScalarProcedure(procedure);
	}

	@Override
	public String getUserNickname(String phoneNumber) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectNickByPhone");
		procedure.addParameter("PHONE", phoneNumber);
		return (String)dba.invokeScalarProcedure(procedure);
	}

	@Override
	public String getUserPhoneNumber(String nickname) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectPhoneByNick");
		procedure.addParameter("NICK", nickname);
		return (String)dba.invokeScalarProcedure(procedure);
	}

	@Override
	public boolean checkAvailabilityAndRecordNickname(String phoneNumber, String nickname) throws SQLException {
		String nicksPhone = getUserPhoneNumber(nickname);
		if (nicksPhone != null) {
			if (!phoneNumber.equals(nicksPhone)) {
				return false;
			}
		}
		PreparedProcedureInvocationDto procedure;
		if (isUserOnRecord(phoneNumber)) {
			procedure = new PreparedProcedureInvocationDto("UpdateUser");
		} else {
			procedure = new PreparedProcedureInvocationDto("InsertUser");
		}
		procedure.addParameter("PHONE", phoneNumber);
		procedure.addParameter("NICK",  nickname);
		dba.invokeUpdateProcedure(procedure);
		return true;
	}

	@Override
	public boolean isUserOnRecord(String phoneNumber) throws SQLException {
		return getUserNickname(phoneNumber) != null;
	}

}