package mutua.smsappmodule.dal;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationHangmanTests.*;
import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.dto.MatchDto;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;
import mutua.smsappmodule.dto.UserDto;

import org.junit.Before;
import org.junit.Test;

/** <pre>
 * INextBotWordsDBBehavioralTests.java
 * ===================================
 * (created by luiz, Aug 14, 2015)
 *
 * Tests the normal-circumstance usage of {@link INextBotWordsDB} implementations
 *
 * @see INextBotWordsDB
 * @version $Id$
 * @author luiz
 */

public class INextBotWordsDBBehavioralTests {
	
	private IUserDB         userDB         = DEFAULT_MODULE_DAL.getUserDB();
	private INextBotWordsDB nextBotWordsDB = DEFAULT_HANGMAN_DAL.getNextBotWordsDB();
	

	/*******************
	** COMMON METHODS **
	*******************/
	
	@Before
	public void resetTables() throws SQLException {
		nextBotWordsDB.reset();
		SMSAppModuleTestCommons.resetTables();
	}
	

	/**********
	** TESTS **
	**********/
	
	@Test
	public void testSimpleUsage() throws SQLException {
		
		UserDto user  = userDB.assureUserIsRegistered("111");
		
		int nextBotWordCursor = nextBotWordsDB.getAndIncNextBotWord(user);
		assertTrue("On the first time, the next bot words cursos must be 0", nextBotWordCursor == 0);
		
		nextBotWordCursor = nextBotWordsDB.getAndIncNextBotWord(user);
		assertTrue("On the second time, the next bot words cursos must be 1", nextBotWordCursor == 1);

	}

}
