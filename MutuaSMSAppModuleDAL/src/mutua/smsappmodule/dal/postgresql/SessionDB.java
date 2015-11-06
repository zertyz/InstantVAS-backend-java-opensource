package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import adapters.JDBCAdapter;
import adapters.dto.PreparedProcedureInvocationDto;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;

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
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("ResetTable");
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public SessionDto getSession(UserDto user) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("FetchProperties");
		procedure.addParameter("USER_ID", user.getUserId());
		Object[][] rows = dba.invokeArrayProcedure(procedure);
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
			PreparedProcedureInvocationDto deleteProcedure = new PreparedProcedureInvocationDto("DeleteProperty");
			deleteProcedure.addParameter("USER_ID",       user.getUserId());
			deleteProcedure.addParameter("PROPERTY_NAME", toBeDeletedPropertyName);
			dba.invokeUpdateProcedure(deleteProcedure);
		}
		// update
		for (String[] toBeUpdatedProperty : session.getUpdatedProperties()) {
			String propertyName  = toBeUpdatedProperty[0];
			String propertyValue = toBeUpdatedProperty[1];
			PreparedProcedureInvocationDto updateProcedure = new PreparedProcedureInvocationDto("UpdateProperty");
			updateProcedure.addParameter("USER_ID",        user.getUserId());
			updateProcedure.addParameter("PROPERTY_NAME",  propertyName);
			updateProcedure.addParameter("PROPERTY_VALUE", propertyValue);
			dba.invokeUpdateProcedure(updateProcedure);
		}
		// insert
		for (String[] toBeInsertedProperty : session.getNewProperties()) {
			String propertyName  = toBeInsertedProperty[0];
			String propertyValue = toBeInsertedProperty[1];
			PreparedProcedureInvocationDto insertedProcedure = new PreparedProcedureInvocationDto("InsertProperty");
			insertedProcedure.addParameter("USER_ID",        user.getUserId());
			insertedProcedure.addParameter("PROPERTY_NAME",  propertyName);
			insertedProcedure.addParameter("PROPERTY_VALUE", propertyValue);
			dba.invokeUpdateProcedure(insertedProcedure);
		}
	}

	@Override
	public void assureProperty(UserDto user, String propertyName, String propertyValue)	throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("AssureProperty");
		procedure.addParameter("USER_ID", user.getUserId());
		procedure.addParameter("PROPERTY_NAME", propertyName);
		procedure.addParameter("PROPERTY_VALUE", propertyValue);
		dba.invokeUpdateProcedure(procedure);
	}

}
