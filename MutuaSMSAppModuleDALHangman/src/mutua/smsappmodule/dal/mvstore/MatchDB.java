package mutua.smsappmodule.dal.mvstore;

import java.sql.SQLException;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import adapters.MVStoreAdapter;
import mutua.smsappmodule.dal.IMatchDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dto.MatchDto;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;

/** MatchDB.java
 * =============
 * (created by luiz, Sep 11, 2018)
 *
 * Implements the MVStore version of {@link IMatchDB}.
 * 
 * @see IMatchDB
 * @author luiz
*/

public class MatchDB implements IMatchDB {
	
	// Matches := {[matchId] = (MatchDTO) {(int)matchId, (String)wordProvidingPlayerPhone, (String)wordGuessingPlayerPhone, (String)serializedGame, (long)matchStartMillis}, (EMatchStatus)status}, ...}
	private MVMap<Integer, Object[]> matches;
	
	// databases
	private IUserDB userDB;

	
	public MatchDB() {
		MVStore store            = MVStoreAdapter.getStore();
		matches                  = store.openMap("smsappmodulehangman.Match", new MVMap.Builder<Integer, Object[]>());
		userDB                   = SMSAppModuleDALFactory.MVSTORE.getUserDB();
	}

	@Override
	public void reset() {
		matches.clear();
	}

	@Override
	public synchronized int storeNewMatch(MatchDto match) {
		int nextMatchId = matches.size();
		Object[] objectMatch = {
			nextMatchId,
			match.getWordProvidingPlayer().getPhoneNumber(),
			match.getWordGuessingPlayer().getPhoneNumber(),
			match.getSerializedGame(),
			match.getMatchStartMillis(),
			match.getStatus()
		};
		matches.put(nextMatchId, objectMatch);
		match.setMatchId(nextMatchId);
		return nextMatchId;
	}

	@Override
	public MatchDto retrieveMatch(int matchId) throws SQLException {
		Object[] objectMatch = matches.get(matchId);
		if (objectMatch == null) {
			return null;
		}
		int          retrievedMatchId         = (Integer)     objectMatch[0];
		String       wordProvidingPlayerPhone = (String)      objectMatch[1];
		String       wordGuessingPlayerPhone  = (String)      objectMatch[2];
		String       serializedGame           = (String)      objectMatch[3];
		long         matchStartMillis         = (Long)        objectMatch[4];
		EMatchStatus status                   = (EMatchStatus)objectMatch[5];
		MatchDto match = new MatchDto(retrievedMatchId,
		                              userDB.assureUserIsRegistered(wordProvidingPlayerPhone),
		                              userDB.assureUserIsRegistered(wordGuessingPlayerPhone),
		                              serializedGame,
		                              matchStartMillis,
		                              status);
		return match;
	}

	@Override
	public synchronized void updateMatchStatus(MatchDto match, EMatchStatus status, String serializedGame) {
		Object[] objectMatch = {
			match.getMatchId(),
			match.getWordProvidingPlayer().getPhoneNumber(),
			match.getWordGuessingPlayer().getPhoneNumber(),
			serializedGame,
			match.getMatchStartMillis(),
			status
		};
		matches.replace(match.getMatchId(), objectMatch);
	}

}
