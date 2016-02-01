package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;
import adapters.JDBCAdapter;

import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterProfile.ProfileDBStatements.*;
import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterProfile.Parameters.*;

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
		dba.invokeUpdateProcedure(ResetTable);
	}

	@Override
	public ProfileDto getProfileRecord(UserDto user) throws SQLException {
		Object[] row = dba.invokeRowProcedure(SelectProfileByUser, USER_ID, user.getUserId());
		if (row == null) {
			return null;
		}
		int    userId   = (Integer) row[0];
		String nickname = (String)  row[1];
		return new ProfileDto(user, nickname);
	}

	@Override
	public ProfileDto getProfileRecord(String nickname) throws SQLException {
		Object[] row = dba.invokeRowProcedure(SelectProfileByNickname, NICKNAME, nickname);
		if (row == null) {
			return null;
		}
		int    userId                 = (Integer) row[0];
		String phoneNumber            = (String)  row[1];
		String correctlyCasedNickname = (String)  row[2];
		return new ProfileDto(new UserDto(userId, phoneNumber), correctlyCasedNickname);
	}

	@Override
	public ProfileDto setProfileRecord(ProfileDto profile) throws SQLException {
		Object[] row = dba.invokeRowProcedure(AssertProfile,
			USER_ID,  profile.getUser().getUserId(),
			NICKNAME, profile.getNickname());
		int    storedUserId   = (Integer) row[0];
		String storedNickname = (String)  row[1];
		if (profile.getNickname().equals(storedNickname)) {
			return profile;
		} else {
			return new ProfileDto(profile.getUser(), storedNickname);
		}
	}

}
