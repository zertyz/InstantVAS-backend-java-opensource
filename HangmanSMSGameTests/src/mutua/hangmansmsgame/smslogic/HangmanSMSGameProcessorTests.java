package mutua.hangmansmsgame.smslogic;

import org.junit.Test;

/** <pre>
 * HangmanSMSGameProcessorTests.java
 * =================================
 * (created by luiz, Jan 19, 2011)
 *
 * Tests the application as described on the product manual -- with emphasis at the phraseologies,
 * and the usage mechanics
 */


public class HangmanSMSGameProcessorTests {

	
	private TestCommons tc = new TestCommons();
	
	
	/*********************
	** AUXILIAR METHODS **
	*********************/

	
	/**********
	** TESTS **
	**********/
	
	// SCENARIO: the first message being sent to the system
    ///////////////////////////////////////////////////////

	@Test
	public void testUnrecognizedCommand() {
		tc.checkResponse("21991234899", "HJKS", "welcome to ocsco");
	}
	
}