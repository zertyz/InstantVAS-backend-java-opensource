package mutua.smsappmodule.dal.mvstore;

import java.sql.SQLException;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import adapters.MVStoreAdapter;
import mutua.smsappmodule.dal.INextBotWordsDB;
import mutua.smsappmodule.dto.UserDto;

/** NextBotWordsDB.java
 * ====================
 * (created by luiz, Sep 11, 2018)
 *
 * Implements the MVStore version of {@link INextBotWordsDB}.
 * 
 * @see INextBotWordsDB
 * @author luiz
*/

public class NextBotWordsDB implements INextBotWordsDB {
	
	// NextBotWord := {[phone] = nextBotWordId, ...}
	private MVMap<String, Integer> nextBotWord;
	
	
	public NextBotWordsDB() {
		MVStore store = MVStoreAdapter.getStore();
		nextBotWord = store.openMap("smsappmodulehangman.NextBotWord", new MVMap.Builder<String, Integer>());
	}

	@Override
	public void reset() {
		nextBotWord.clear();
	}

	@Override
	public synchronized int getAndIncNextBotWord(UserDto user) {
		String phoneNumber = user.getPhoneNumber();
		int nextBotWordId;
		if (nextBotWord.containsKey(phoneNumber)) {
			nextBotWordId = nextBotWord.get(phoneNumber);
			nextBotWord.replace(phoneNumber, nextBotWordId+1);
		} else {
			nextBotWordId = 0;
			nextBotWord.put(phoneNumber, nextBotWordId+1);
		}
		return nextBotWordId;
	}

}
