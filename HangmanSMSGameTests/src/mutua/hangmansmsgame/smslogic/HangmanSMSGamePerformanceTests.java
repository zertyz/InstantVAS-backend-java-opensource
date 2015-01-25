package mutua.hangmansmsgame.smslogic;

import static org.junit.Assert.*;
import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.IUserDB;

import org.junit.Test;

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
    
	private static IUserDB userDB = DALFactory.getUserDB();


	@Test
	public void registerNicknamePerformanceTests() throws InterruptedException {
		int attempts = 1000;
		long referenceStart = System.currentTimeMillis();
		for (int i=0; i<attempts; i++) {
			CommandDetails.registerUserNickname("t"+i, "nick"+i+"_");
		}
		long targetStart = System.currentTimeMillis();
		long referenceElapsed = targetStart - referenceStart;
		for (int i=0; i<attempts; i++) {
			CommandDetails.registerUserNickname("t2"+i, "nick"+i+"_");
		}
		long targetElapsed = System.currentTimeMillis() - targetStart;
		System.out.println(referenceElapsed + "\t" + ((double)referenceElapsed)/((double)attempts));
		System.out.println(targetElapsed + "\t" + ((double)targetElapsed)/((double)(2*attempts)));
		
		tc.resetDatabases();
		
		referenceStart = System.currentTimeMillis();
		for (int i=0; i<attempts; i++) {
			CommandDetails.registerUserNickname("t"+i, "nick");
		}
		targetStart = System.currentTimeMillis();
		referenceElapsed = targetStart - referenceStart;
		for (int i=0; i<attempts; i++) {
			CommandDetails.registerUserNickname("t2"+i, "nick");
		}
		targetElapsed = System.currentTimeMillis() - targetStart;
		System.out.println(referenceElapsed + "\t" + ((double)referenceElapsed)/((double)attempts));
		System.out.println(targetElapsed + "\t" + ((double)targetElapsed)/((double)(2*attempts)));
		
		// O(1) -- same time
		// O(n) -- elapsed time devided by total n are equal
		// O(n^x) -- 
		
		// this is O(n), right?
//		referenceStart = 0;
//		targetStart = 0;
//		for (int i=0; i<attempts; i++) {
//			for (int j=0; j<i; j++) {
//				targetStart++;
//			}
//		}
//		referenceElapsed = targetStart - referenceStart;
//		targetElapsed = 0;
//		for (int i=0; i<attempts; i++) {
//			for (int j=0; j<(i+attempts); j++) {
//				targetElapsed++;
//			}
//		}
//		System.out.println(referenceElapsed + "\t" + ((double)referenceElapsed)/((double)attempts));
//		System.out.println(targetElapsed + "\t" + ((double)targetElapsed)/((double)(1*attempts)));
		
	}

}
