package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * INextBotWordsDB.java
 * ====================
 * (created by luiz, Aug 14, 2015)
 *
 * Defines access methods for the "NextBotWords" table
 *
 * @see mutua.smsappmodule.dal.ram.NextBotWordsDB
 * @see mutua.smsappmodule.dal.postgresql.NextBotWordsDB
 * @version $Id$
 * @author luiz
 */

public interface INextBotWordsDB {

	/** Resets the database, for testing purposes */
	void reset() throws SQLException;

	/** returns the next bot word pointer for the given 'userId', and increment the cursor */
	int getAndIncNextBotWord(UserDto user) throws SQLException;
	
}
