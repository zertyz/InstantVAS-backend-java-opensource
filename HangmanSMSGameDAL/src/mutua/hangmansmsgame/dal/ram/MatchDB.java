package mutua.hangmansmsgame.dal.ram;

import java.util.ArrayList;

import mutua.hangmansmsgame.dal.IMatchDB;
import mutua.hangmansmsgame.dal.IMatchTraversalCallback;
import mutua.hangmansmsgame.dal.dto.MatchDto;
import mutua.hangmansmsgame.dal.dto.MatchDto.EMatchStatus;

/** <pre>
 * MatchDB.java
 * ============
 * (created by luiz, Jan 3, 2015)
 *
 * Implements a RAM version of 'IMatchDB'
 *
 * @see IMatchDB
 * @version $Id$
 * @author luiz
 */

public class MatchDB implements IMatchDB {


	// data structures
	//////////////////
	
	private static ArrayList<MatchDto> matches = new ArrayList<MatchDto>();
	
	
	// IMatchDB implementation
	//////////////////////////
	
	@Override
	public void reset() {
		matches.clear();
	}

	@Override
	public int storeNewMatch(MatchDto match) {
		matches.add(match);
		return matches.lastIndexOf(match);
	}

	@Override
	public MatchDto retrieveMatch(int matchId) {
		return matches.get(matchId);
	}

	@Override
	public void updateMatchStatus(int matchId, EMatchStatus status) {
		MatchDto storedMatch = matches.get(matchId);
		MatchDto newMatch = storedMatch.getNewMatch(status);
		matches.set(matchId, newMatch);
	}

	@Override
	public void traverseThroughActiveMatches(IMatchTraversalCallback callback) {
		for (int i=0; i<matches.size(); i++) {
			callback.onNextEntry(i, matches.get(i));
		}
	}

}
