package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;
import adapters.JDBCAdapter;
import adapters.dto.PreparedProcedureInvocationDto;

/** <pre>
 * ProfileDB.java
 * ==============
 * (created by luiz, Aug 3, 2015)
 *
 * Implements the POSTGRESQL version of {@link IProfileDB}
 *
 * @see IProfileDB
 * @version $Id$
 * @author luiz
 */

public class ProfileDB implements IProfileDB {
	
	private JDBCAdapter dba;

	
	public ProfileDB() throws SQLException {
		dba = SMSAppModulePostgreSQLAdapterProfile.getProfileDBAdapter();
	}
	
	@Override
	public void reset() throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("ResetTable");
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public ProfileDto getProfileRecord(UserDto user) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectProfileByUser");
		procedure.addParameter("USER_ID", user.getUserId());
		Object[] row = dba.invokeRowProcedure(procedure);
		int    userId   = (Integer) row[0];
		String nickname = (String)  row[1];
		return new ProfileDto(user, nickname);
	}

	@Override
	public ProfileDto setProfileRecord(ProfileDto profile) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("AssertProfile");
		procedure.addParameter("USER_ID",  profile.getUser().getUserId());
		procedure.addParameter("NICKNAME", profile.getNickname());
		Object[] row = dba.invokeRowProcedure(procedure);
		int    storedUserId   = (Integer) row[0];
		String storedNickname = (String)  row[1];
		if (profile.getNickname().equals(storedNickname)) {
			return profile;
		} else {
			return new ProfileDto(profile.getUser(), storedNickname);
		}
	}

}
