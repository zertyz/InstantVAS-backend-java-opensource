package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import adapters.JDBCAdapter;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;

import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter.Parameters.*;
import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapter.SessionsDBStatements.*;

/** <pre>
 * SessionDB.java
 * ==============
 * (created by luiz, Jul 15, 2015)
 *
 * Implements the persistent, PostgreSQL version of {@link ISessionDB}
 *
 * @see ISessionDB
 * @version $Id$
 * @author luiz
 */

public class SessionDB implements ISessionDB {

	private JDBCAdapter dba;
	
	public SessionDB() throws SQLException {
		dba = SMSAppModulePostgreSQLAdapter.getSessionsDBAdapter();
	}

	@Override
	public void reset() throws SQLException {
		dba.invokeUpdateProcedure(ResetTable);
	}

	@Override
	public SessionDto getSession(UserDto user) throws SQLException {
		Object[][] rows = dba.invokeArrayProcedure(FetchProperties, USER_ID, user.getUserId());
		if (rows.length == 0) {
			return null;
		}
		String[][] properties = new String[rows.length][2];
		for (int row=0; row<rows.length; row++) {
			String propertyName  = (String) rows[row][0];
			String propertyValue = (String) rows[row][1];
			properties[row][0] = propertyName;
			properties[row][1] = propertyValue;
		}
		return new SessionDto(user, properties);
	}

	@Override
	public void setSession(SessionDto session) throws SQLException {

		UserDto user = session.getUser();
		
		// delete
		for (String toBeDeletedPropertyName : session.getDeletedProperties()) {
			dba.invokeUpdateProcedure(DeleteProperty,
				USER_ID,       user.getUserId(),
				PROPERTY_NAME, toBeDeletedPropertyName);
		}
		// update
		for (String[] toBeUpdatedProperty : session.getUpdatedProperties()) {
			String propertyName  = toBeUpdatedProperty[0];
			String propertyValue = toBeUpdatedProperty[1];
			dba.invokeUpdateProcedure(UpdateProperty,
				USER_ID,        user.getUserId(),
				PROPERTY_NAME,  propertyName,
				PROPERTY_VALUE, propertyValue);
		}
		// insert
		for (String[] toBeInsertedProperty : session.getNewProperties()) {
			String propertyName  = toBeInsertedProperty[0];
			String propertyValue = toBeInsertedProperty[1];
			dba.invokeUpdateProcedure(InsertProperty,
				USER_ID,        user.getUserId(),
				PROPERTY_NAME,  propertyName,
				PROPERTY_VALUE, propertyValue);
		}
	}

	@Override
	public void assureProperty(UserDto user, String propertyName, String propertyValue)	throws SQLException {
		dba.invokeUpdateProcedure(AssureProperty,
			USER_ID,        user.getUserId(),
			PROPERTY_NAME,  propertyName,
			PROPERTY_VALUE, propertyValue);
	}

}
