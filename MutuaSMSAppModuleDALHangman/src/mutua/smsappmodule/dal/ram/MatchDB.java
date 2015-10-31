package mutua.smsappmodule.dal.ram;

import java.util.ArrayList;

import mutua.smsappmodule.dal.IMatchDB;
import mutua.smsappmodule.dto.MatchDto;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;

/** <pre>
 * MatchDB.java
 * ============
 * (created by luiz, Jan 3, 2015)
 *
 * Implements the RAM version of {@link IMatchDB}
 *
 * @see IMatchDB
 * @version $Id$
 * @author luiz
 */

public class MatchDB implements IMatchDB {

	
	// data structures
	//////////////////
	
	private static ArrayList<MatchDto> matches = new ArrayList<MatchDto>();
	
	
	// common methods
	/////////////////

	
	
	// IMatchDB implementation
	//////////////////////////

	@Override
	public void reset() {
		matches.clear();
	}

	@Override
	public synchronized int storeNewMatch(MatchDto match) {
		matches.add(match);
		int matchId = matches.lastIndexOf(match);
		match.setMatchId(matchId);
		return matchId;
	}

	@Override
	public MatchDto retrieveMatch(int matchId) {
		return matches.get(matchId);
	}

	@Override
	public void updateMatchStatus(MatchDto match, EMatchStatus status, String serializedGame) {
		int matchId = match.getMatchId();
		MatchDto storedMatch = matches.get(matchId);
		MatchDto newMatch = new MatchDto(storedMatch.getMatchId(), storedMatch.getWordProvidingPlayer(), storedMatch.getWordGuessingPlayer(),
		                                 serializedGame, storedMatch.getMatchStartMillis(), status);
		
		matches.set(matchId, newMatch);
	}

}
