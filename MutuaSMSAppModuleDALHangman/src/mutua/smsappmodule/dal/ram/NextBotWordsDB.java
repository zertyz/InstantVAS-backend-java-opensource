package mutua.smsappmodule.dal.ram;

import java.sql.SQLException;
import java.util.Hashtable;
import mutua.smsappmodule.dal.INextBotWordsDB;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * NextBotWordsDB.java
 * ===================
 * (created by luiz, Aug 14, 2015)
 *
 * Implements the RAM version of {@link INextBotWordsDB}
 *
 * @see INextBotWordsDB
 * @version $Id$
 * @author luiz
 */

public class NextBotWordsDB implements INextBotWordsDB {


	// data structures
	//////////////////
	
	private static Hashtable<UserDto, Integer> nextBotWords = new Hashtable<UserDto, Integer>();
	
	
	// common methods
	/////////////////

	
	
	// INextBotWordsDB implementation
	/////////////////////////////////


	@Override
	public void reset() throws SQLException {
		nextBotWords.clear();
	}

	@Override
	public int getAndIncNextBotWord(UserDto user) throws SQLException {
		Integer cursor = nextBotWords.get(user);
		if (cursor == null) {
			cursor = 0;
		}
		nextBotWords.put(user, cursor+1);
		return cursor;
	}

}
