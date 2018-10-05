package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.smsappmodule.dto.MatchDto;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;

/** <pre>
 * IMatchDB.java
 * =============
 * (created by luiz, Aug 13, 2015)
 *
 * Defines access methods for the "Matches" table
 *
 * @see mutua.smsappmodule.dal.ram.MatchDB
 * @see mutua.smsappmodule.dal.postgresql.MatchDB
 * @version $Id$
 * @author luiz
 */

public interface IMatchDB {
	
	/** Resets the database, for testing purposes */
	void reset() throws SQLException;

	/** Stores & mark a 'MatchDto' as active, return it's 'matchId'. Also, sets the ID on the 'match' parameter. */
	int storeNewMatch(MatchDto match) throws SQLException;
	
	/** Retrieves a 'MatchDto' from the database */
	MatchDto retrieveMatch(int matchId) throws SQLException;
	
	/** Updates the game evolution for a match */
	void updateMatchStatus(MatchDto match, EMatchStatus status, String serializedGame) throws SQLException;
	
}
