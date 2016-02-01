package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import adapters.JDBCAdapter;
import mutua.smsappmodule.dal.INextBotWordsDB;
import mutua.smsappmodule.dto.UserDto;

import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterHangman.NextBotWordsDBStatements.*;
import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterHangman.Parameters.*;

/** <pre>
 * NextBotWordsDB.java
 * ===================
 * (created by luiz, Aug 14, 2015)
 *
 * Implements the PostgreSQL version of {@link INextBotWordsDB}
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
		dba.invokeUpdateProcedure(ResetTable);
	}

	@Override
	public int getAndIncNextBotWord(UserDto user) throws SQLException {
		int cursor = (Integer) dba.invokeScalarProcedure(SelectAndIncrementNextBotWord, USER_ID, user.getUserId());
		return cursor;
	}

}
