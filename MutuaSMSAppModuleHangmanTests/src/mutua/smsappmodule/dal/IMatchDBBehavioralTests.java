package mutua.smsappmodule.dal;

import static instantvas.tests.InstantVASSMSAppModuleHangmanTestsConfiguration.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import instantvas.tests.InstantVASSMSAppModuleHangmanTestsConfiguration;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.dto.MatchDto;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * IProfileDBBehavioralTests.java
 * ==============================
 * (created by luiz, Jan 27, 2015)
 *
 * Tests the normal-circumstance usage of {@link IMatchDB} implementations
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class IMatchDBBehavioralTests {
	
	// configuration
	InstantVASSMSAppModuleHangmanTestsConfiguration config = InstantVASSMSAppModuleHangmanTestsConfiguration.getInstance();
	
	private IUserDB  userDB    = BASE_MODULE_DAL.getUserDB();
	private IMatchDB matchDB   = HANGMAN_MODULE_DAL.getMatchDB();
	

	/*******************
	** COMMON METHODS **
	*******************/
	
	@Before
	public void resetTables() throws SQLException {
		matchDB.reset();
		SMSAppModuleTestCommons.resetBaseTables(BASE_MODULE_DAL);
	}
	

	/**********
	** TESTS **
	**********/
	
	public void testNonExistingMatchRecord() throws SQLException {
		MatchDto match = matchDB.retrieveMatch(-1);
		assertNull("Non-existing profiles must be null", match);
	}

	@Test
	public void testSimpleUsage() throws SQLException {
		
		UserDto firstExpectedMatchWordProvidingPlayer  = userDB.assureUserIsRegistered("111");
		UserDto firstExpectedMatchWordGuessingPlayer   = userDB.assureUserIsRegistered("999");
		UserDto secondExpectedMatchWordProvidingPlayer = userDB.assureUserIsRegistered("222");
		UserDto secondExpectedMatchWordGuessingPlayer  = userDB.assureUserIsRegistered("888");
		
		MatchDto firstExpectedMatch  = new MatchDto(firstExpectedMatchWordProvidingPlayer,  firstExpectedMatchWordGuessingPlayer,  "ANYSHIT",     123,    EMatchStatus.ACTIVE);
		MatchDto secondExpectedMatch = new MatchDto(secondExpectedMatchWordProvidingPlayer, secondExpectedMatchWordGuessingPlayer, "ANOTHERSHIT", 123111, EMatchStatus.CLOSED_A_PLAYER_GAVE_UP);
		
		int firstMatchId  = matchDB.storeNewMatch(firstExpectedMatch);
		int secondMatchId = matchDB.storeNewMatch(secondExpectedMatch);
		
		MatchDto firstObservedMatch  = matchDB.retrieveMatch(firstMatchId);
		MatchDto secondObservedMatch = matchDB.retrieveMatch(secondMatchId);
		
		assertEquals("Storing & Retrieving first  match failed", firstExpectedMatch,  firstObservedMatch);
		assertEquals("Storing & Retrieving second match failed", secondExpectedMatch, secondObservedMatch);
		
		String expectedSerializedGame = "abcdefg";
		matchDB.updateMatchStatus(firstExpectedMatch, EMatchStatus.CLOSED_WORD_GUESSED, expectedSerializedGame);
		
		MatchDto expectedTaintedMatch = new MatchDto(firstMatchId, firstExpectedMatchWordProvidingPlayer,
		                                             firstExpectedMatchWordGuessingPlayer,
		                                             expectedSerializedGame,
		                                             123,
		                                             EMatchStatus.CLOSED_WORD_GUESSED);
		MatchDto observedTaintedMatch = matchDB.retrieveMatch(firstMatchId);
		
		assertEquals("'updateMatchStatus' failed", expectedTaintedMatch, observedTaintedMatch);
	}

}
