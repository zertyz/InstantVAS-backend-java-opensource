package mutua.hangmansmsgame.dal;

import mutua.hangmansmsgame.dal.dto.MatchDto;

/** <pre>
 * IMatchTraversalCallback.java
 * ============================
 * (created by luiz, Jan 3, 2015)
 *
 * Callback interface for 'IMatchDB' traversal methods
 *
 * @see IMatchDB
 * @version $Id$
 * @author luiz
 */

public interface IMatchTraversalCallback {
	
	/** Method called whenever another match record is found on the database, when traversing it 
	 * @param matchId TODO*/
	boolean onNextEntry(int matchId, MatchDto match);
	
}
