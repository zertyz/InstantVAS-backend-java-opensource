package mutua.hangmansmsgame.dal;

import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.hangmansmsgame.dal.config.Configuration;
import mutua.hangmansmsgame.dal.dto.MatchDto;
import mutua.hangmansmsgame.dal.dto.MatchDto.EMatchStatus;

import org.junit.Test;

/** <pre>
 * IMatchDBTests.java
 * ==================
 * (created by luiz, Jan 27, 2015)
 *
 * Test the data access layers of 'IMatchDB' for correct behavior information handling
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class IMatchDBTests {

	
	private IMatchDB matchDB = DALFactory.getMatchDB(Configuration.DEFAULT_DAL);

	@Test
	public void testSimpleUsage() throws SQLException {
		
		matchDB.reset();
		
		MatchDto firstExpectedMatch  = new MatchDto("111", "999", "ANYSHIT",     123,    EMatchStatus.ACTIVE);
		MatchDto secondExpectedMatch = new MatchDto("222", "888", "ANOTHERSHIT", 123111, EMatchStatus.CLOSED_A_PLAYER_GAVE_UP);
		
		int firstMatchId  = matchDB.storeNewMatch(firstExpectedMatch);
		int secondMatchId = matchDB.storeNewMatch(secondExpectedMatch);
		
		MatchDto firstObservedMatch  = matchDB.retrieveMatch(firstMatchId);
		MatchDto secondObservedMatch = matchDB.retrieveMatch(secondMatchId);
		
		assertEquals("Storing & Retrieving first  match failed", firstExpectedMatch,  firstObservedMatch);
		assertEquals("Storing & Retrieving second match failed", secondExpectedMatch, secondObservedMatch);
		
		matchDB.updateMatchStatus(firstMatchId, EMatchStatus.CLOSED_WORD_GUESSED);
		
		MatchDto expectedTaintedMatch = new MatchDto("111", "999", "ANYSHIT", 123, EMatchStatus.CLOSED_WORD_GUESSED);
		MatchDto observedTaintedMatch = matchDB.retrieveMatch(firstMatchId);
		
		assertEquals("'updateMatchStatus' failed", expectedTaintedMatch, observedTaintedMatch);
	}

}
