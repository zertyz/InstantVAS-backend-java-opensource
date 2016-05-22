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
		String[] toBeDeletedPropertyNames = session.getDeletedProperties();
		if (toBeDeletedPropertyNames != null) {
			Object[][] parametersAndValuesPairsSet = new Object[toBeDeletedPropertyNames.length][4];
			for (int i=0; i<toBeDeletedPropertyNames.length; i++) {
				parametersAndValuesPairsSet[i][0] = USER_ID;
				parametersAndValuesPairsSet[i][1] = user.getUserId();
				parametersAndValuesPairsSet[i][2] = PROPERTY_NAME;
				parametersAndValuesPairsSet[i][3] = toBeDeletedPropertyNames[i];
			}
			dba.invokeUpdateBatchProcedure(DeleteProperty, parametersAndValuesPairsSet);
		}
		
		// update
		String[][] toBeUpdatedProperties = session.getUpdatedProperties();
		if (toBeUpdatedProperties != null) {
			Object[][] parametersAndValuesPairsSet = new Object[toBeUpdatedProperties.length][6];
			for (int i=0; i<toBeUpdatedProperties.length; i++) {
				String propertyName = toBeUpdatedProperties[i][0];
				String propertyValue = toBeUpdatedProperties[i][1];
				parametersAndValuesPairsSet[i][0] = USER_ID;
				parametersAndValuesPairsSet[i][1] = user.getUserId();
				parametersAndValuesPairsSet[i][2] = PROPERTY_NAME;
				parametersAndValuesPairsSet[i][3] = propertyName;
				parametersAndValuesPairsSet[i][4] = PROPERTY_VALUE;
				parametersAndValuesPairsSet[i][5] = propertyValue;
			}
			dba.invokeUpdateBatchProcedure(UpdateProperty, parametersAndValuesPairsSet);
		}

		// insert
		String[][] toBeInsertedProperties = session.getNewProperties();
		if (toBeUpdatedProperties != null) {
			Object[][] parametersAndValuesPairsSet = new Object[toBeInsertedProperties.length][6];
			for (int i=0; i<toBeInsertedProperties.length; i++) {
				String propertyName = toBeInsertedProperties[i][0];
				String propertyValue = toBeInsertedProperties[i][1];
				parametersAndValuesPairsSet[i][0] = USER_ID;
				parametersAndValuesPairsSet[i][1] = user.getUserId();
				parametersAndValuesPairsSet[i][2] = PROPERTY_NAME;
				parametersAndValuesPairsSet[i][3] = propertyName;
				parametersAndValuesPairsSet[i][4] = PROPERTY_VALUE;
				parametersAndValuesPairsSet[i][5] = propertyValue;
			}
			dba.invokeUpdateBatchProcedure(InsertProperty, parametersAndValuesPairsSet);
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
