package mutua.hangmansmsgame.i18n;

import static org.junit.Assert.*;
import mutua.hangmansmsgame.config.Configuration;
import mutua.hangmansmsgame.i18n.IPhraseology.EPhraseNames;
import mutua.hangmansmsgame.smslogic.TestCommons;

import org.junit.Test;

/** <pre>
 * ConfigurationChangeTests.java
 * =============================
 * (created by luiz, Jan 12, 2015)
 *
 * Verifies the behavior of 'i18n' and 'StateDetail' classes after configuration reloading
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */


public class ConfigurationChangeTests {
	
	private TestCommons tc = new TestCommons();
	private TestPhraseology phrases = new TestPhraseology(Configuration.SHORT_CODE);

	@Test
	public void testReadingMessagesFromConfiguration() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		
		// set
		String[] oldValue = Configuration.playersList;
		Configuration.playersList = new String[] {"{{nick}} ({{state}}/{{numberOfLuckyNumbers}})"};		
		Configuration.applyConfiguration();
		
		String message = phrases.getPhrase(EPhraseNames.playersList, new String[][] {
			{"nick",                 "Dom"},
			{"state",                "RJ"},
			{"numberOfLuckyNumbers", "97"},
		});
		assertEquals("Parameter substitution failed", "Dom (RJ/97)", message);
		
		
		message = phrases.getPhrase(EPhraseNames.playersList, new String[][] {
			{"nick",                 "\\"},
			{"state",                "$"},
			{"numberOfLuckyNumbers", "{}"},
		});
		assertEquals("Spacial symbols substitution failed", "\\ ($/{})", message);
		
		
		message = phrases.getPhrase(EPhraseNames.playersList, new String[][] {
			{"nick",                 null},
			{"state",                null},
			{"numberOfLuckyNumbers", null},
		});
		assertEquals("Null parameter values substitution failed", "{{nick}} ({{state}}/{{numberOfLuckyNumbers}})", message);
		
		// reset to avoid side effects
		Configuration.playersList = oldValue;
		Configuration.applyConfiguration();
	}
	
	@Test
	public void testChangingCommandsByReloadingConfiguration() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		
		// set
		String[] oldValue = Configuration.SHOW_WELCOME_MESSAGE;
		Configuration.SHOW_WELCOME_MESSAGE = new String[] {"DUNNOWHATTOSEND", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
		Configuration.applyConfiguration();
		
		tc.checkResponse("phone1", "dunnowhattosend", phrases.INFOWelcome());
		
		// reset to avoid side effects
		Configuration.SHOW_WELCOME_MESSAGE = oldValue;
		Configuration.applyConfiguration();
	}

}
