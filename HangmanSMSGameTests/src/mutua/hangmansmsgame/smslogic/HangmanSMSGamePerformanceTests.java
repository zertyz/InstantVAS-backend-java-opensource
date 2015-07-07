package mutua.hangmansmsgame.smslogic;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.IUserDB;
import mutua.hangmansmsgame.dal.DALFactory.EDataAccessLayers;

import org.junit.Test;

import config.Configuration;

/** <pre>
 * HangmanSMSGamePerformanceTests.java
 * ===================================
 * (created by luiz, Jan 25, 2015)
 *
 * Tests the performance aspect of the HangmanSMSGame
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HangmanSMSGamePerformanceTests {
	
	
	private TestCommons tc = new TestCommons();

	
	// databases
	////////////
    
	private static IUserDB userDB = DALFactory.getUserDB(Configuration.DEFAULT_DAL);


	@Test
	public void testNicknameIndexes() throws InterruptedException, SQLException {

		tc.resetDatabases();
		
		int measurementNicks = 1000;
		int randomNicks = 10000;
		long start = System.currentTimeMillis();
		for (int i=0; i<measurementNicks; i++) {
			CommandDetails.registerUserNickname("t_1_"+i, "nick_1_"+i+"_");
		}
		long referenceElapsed = System.currentTimeMillis() - start;
		
		for (int i=0; i<randomNicks; i++) {
			UUID uuid = UUID.randomUUID();
			CommandDetails.registerUserNickname("rnd_"+i, uuid.toString().substring(0, 10));
		}
		
		start = System.currentTimeMillis();
		for (int i=0; i<measurementNicks; i++) {
			CommandDetails.registerUserNickname("t_2_"+i, "nick_2_"+i+"_");
		}
		long targetElapsed = System.currentTimeMillis() - start;
		System.out.println(referenceElapsed + "\t" + ((double)referenceElapsed)/((double)measurementNicks));
		System.out.println(targetElapsed + "\t" + ((double)targetElapsed)/((double)(measurementNicks)));
		
		// for O(1), these two numbers should be similar
		
	}

	
}
