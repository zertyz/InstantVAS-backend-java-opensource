package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import adapters.JDBCAdapter;
import adapters.dto.PreparedProcedureInvocationDto;
import mutua.smsappmodule.dal.INextBotWordsDB;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * NextBotWordsDB.java
 * ===================
 * (created by luiz, Aug 14, 2015)
 *
 * Implements the POSTGRESQL version of {@link INextBotWordsDB}
 *
 * @see INextBotWordsDB
 * @version $Id$
 * @author luiz
 */

public class NextBotWordsDB implements INextBotWordsDB {

	private JDBCAdapter dba;

	
	public NextBotWordsDB() throws SQLException {
		dba = SMSAppModulePostgreSQLAdapterHangman.getNextBotWordsDBAdapter();
	}
	
	@Override
	public void reset() throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("ResetTable");
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public int getAndIncNextBotWord(UserDto user) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectAndIncrementNextBotWord");
		procedure.addParameter("USER_ID", user.getUserId());
		int cursor = (Integer) dba.invokeScalarProcedure(procedure);
		return cursor;
	}

}
