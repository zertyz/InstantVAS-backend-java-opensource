package mutua.hangmansmsgame.smslogic;

import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.IUserDB;
import mutua.hangmansmsgame.dal.DALFactory.EDataAccessLayers;
import mutua.hangmansmsgame.i18n.TestPhraseology;

import org.junit.Test;

import config.Configuration;

/** <pre>
 * CommandDetailsTests.java
 * ========================
 * (created by luiz, Jan 7, 2015)
 *
 * Test the decoupled SMS Command helper functions behavior
 *
 * @see CommandDetails
 * @version $Id$
 * @author luiz
 */

public class CommandDetailsTests {
	

	private TestCommons tc = new TestCommons();
	TestPhraseology testPhraseology = new TestPhraseology("XXXX");
	
	
	// databases
	////////////
	
	private IUserDB userDB = DALFactory.getUserDB(Configuration.DEFAULT_DAL);


	/**********
	** TESTS **
	**********/
	
	@Test
	public void getDDDTest() {
		assertEquals("Wrong DDD when using 8 digits phone",                                  "BA", CommandDetails.getBrazillianPhoneState("7178321467"));
		assertEquals("Wrong DDD when using 9 digits phone",                                  "RJ", CommandDetails.getBrazillianPhoneState("21991234899"));
		assertEquals("Wrong DDD when using 8 digits phone with country code",                "BA", CommandDetails.getBrazillianPhoneState("557178321467"));
		assertEquals("Wrong DDD when using 8 digits phone with country code preceeded by +", "BA", CommandDetails.getBrazillianPhoneState("+557178321467"));
		assertEquals("Wrong DDD when using 9 digits phone with country code",                "RJ", CommandDetails.getBrazillianPhoneState("5521991234899"));
		assertEquals("Wrong DDD when using 9 digits phone with country code preceeded by +", "RJ", CommandDetails.getBrazillianPhoneState("+5521991234899"));
		assertEquals("Wrong way to inform an invalid DDD",                                   "--", CommandDetails.getBrazillianPhoneState("094840"));
	}
	
	@Test
	public void stringArraySerializationTest() {
		String[] expectedArray = {"one", "two", "three"};
		String[] observedArray = CommandDetails.desserializeStringArray(CommandDetails.serializeStringArray(expectedArray, ";"), ";");
		assertArrayEquals("Serialization / Desserialization of string arrays did not work as expected", expectedArray, observedArray);
	}
	
	@Test
	public void getUsersListBreakingUnderTheMaximumNumberOfCharactersTest() throws SQLException {
		String[] presentedUsers;
		int maxChars = testPhraseology.LISTINGShowPlayers(new String[][] {}).length() + 15;
		tc.setUserDB(new String[][] {
			{"21991234811", "Patata"},
			{"21991234812", "Patate"},
			{"21991234813", "Patati"},
			{"21991234814", "Patato"},
			{"21991234815", "Patatu"},
		});
		tc.setSessionDB(new String[][] {
			{"21991234811", "NEW_USER"},
			{"21991234812", "NEW_USER"},
			{"21991234813", "NEW_USER"},
			{"21991234814", "NEW_USER"},
			{"21991234815", "NEW_USER"},
			{"21991234816", "NEW_USER"},
		});

		presentedUsers = new String[] {};
		while (true) {
			String[][] playersInfo = CommandDetails.getPlayersInfoToPresentOnTheListCommandRespectingTheMaximumNumberOfCharacters(testPhraseology, maxChars, presentedUsers);
			if (playersInfo == null) {
				break;
			}
			presentedUsers = CommandDetails.getNewPresentedUsers(presentedUsers, playersInfo);
			System.out.println(testPhraseology.LISTINGShowPlayers(playersInfo));
		}
//		CommandDetails.getUsersListBreakingUnderTheMaximumNumberOfCharacters(testPhraseology, maxChars, presentedUsers)
	}
	
	@Test
	public void registerUserNicknameTests() throws SQLException {
		String[][] expectedUsersAndNicknames = {
			{"21991234899", "Dom"},	
			{"21991234891", "Dom1"},	
			{"21991234892", "Dom2"},	
		};
		// feed
		for (String[] userAndNickname : expectedUsersAndNicknames) {
			String phone    = userAndNickname[0];
			CommandDetails.registerUserNickname(phone, "Dom");
		}
		// check
		for (String[] userAndNickname : expectedUsersAndNicknames) {
			String phone            = userAndNickname[0];
			String expectedNickname = userAndNickname[1];
			String observedNickname = userDB.getUserNickname(phone);
			assertEquals("Unexpected nickname renaming occurred for phone "+phone, expectedNickname, observedNickname);
		}
		// rename to the same nick
		CommandDetails.registerUserNickname(expectedUsersAndNicknames[0][0], "Dom");
		assertEquals("Renaming to the same crowded nickname failed", "Dom", userDB.getUserNickname(expectedUsersAndNicknames[0][0]));
	}
	
	@Test
	public void isParameterAPhoneNumberTests() {
		assertTrue("False negative while detecting if string is a phone number", CommandDetails.isParameterAPhoneNumber("21991234899"));
		assertFalse("False positive while detecting if string is a phone number", CommandDetails.isParameterAPhoneNumber("ordinary string"));
	}

}
