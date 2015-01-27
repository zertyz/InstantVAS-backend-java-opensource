package mutua.hangmansmsgame.dal;

import java.sql.SQLException;

import mutua.hangmansmsgame.dal.dto.MatchDto;
import mutua.hangmansmsgame.dal.dto.MatchDto.EMatchStatus;

/** <pre>
 * IMatchDB.java
 * =============
 * (created by luiz, Jan 3, 2015)
 *
 * Defines access methods for the "Match" data base
 *
 * @see ram.MatchDB, postgresql.MatchDB
 * @version $Id$
 * @author luiz
 */

public interface IMatchDB {

	/** Resets the database, for testing purposes */
	void reset() throws SQLException;
	
	/** Stores & mark a 'MatchDto' as active, return it's 'matchId' */
	int storeNewMatch(MatchDto match) throws SQLException;
	
	/** Retrieves a 'MatchDto' from the database */
	MatchDto retrieveMatch(int matchId) throws SQLException;
	
	/** Used to mark a match as over, for instance */
	void updateMatchStatus(int matchId, EMatchStatus status) throws SQLException;
	
	/** Traverses through all active matches, notifying the 'callback' function */
	void traverseThroughActiveMatches(IMatchTraversalCallback callback);
	
}
