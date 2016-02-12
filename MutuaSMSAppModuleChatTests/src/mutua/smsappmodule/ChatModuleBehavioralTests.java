package mutua.smsappmodule;

import static instantvas.tests.InstantVASSMSAppModuleChatTestsConfiguration.LOG;
import static mutua.smsappmodule.SMSAppModuleChatTestCommons.*;
import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.icc.configuration.ConfigurationManager;
import mutua.smsappmodule.config.SMSAppModuleConfigurationChat;
import mutua.smsappmodule.dto.PrivateMessageDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.smslogic.commands.CommandMessageDto;

import org.junit.Before;
import org.junit.Test;

/** <pre>
 * ChatModuleBehavioralTests.java
 * ==============================
 * (created by luiz, Nov 10, 2015)
 *
 * Tests the normal-circumstance usage of the "Chat" module features
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ChatModuleBehavioralTests {
	
	
	/**********
	** TESTS ** 
	**********/

	@Before
	public void resetTables() throws SQLException {
		resetChatTables();
	}

	@Test
	public void testConfigurationFile() throws IllegalArgumentException, IllegalAccessException {
		ConfigurationManager cm = new ConfigurationManager(LOG, SMSAppModuleConfigurationChat.class);
		String serializedFields = cm.serializeConfigurableClasses();
		System.out.println(serializedFields);
		cm.deserializeConfigurableClasses(serializedFields);
	}
	
	@Test
	public void testPrivateMessage() throws SQLException {
		String expectedPrivateMessage = "this is the message";
		
		UserDto sender   = createUserAndNickname("21991234899", "sourceNick");
		UserDto receiver = createUserAndNickname("21997559595", "destinationNick");
		CommandMessageDto[] messages = sendPrivateMessage("21991234899", "destinationNick", expectedPrivateMessage);
		
		// command response checks
		assertEquals("Response message's target user is wrong",     null, messages[0].getPhone());	// remembering the convention that null target phone means "send back to the same user"
		assertEquals("Notification message's target user is wrong", "21997559595", messages[1].getPhone());
		assertTrue("Notification message does not contain the intended private message", messages[1].getText().indexOf(expectedPrivateMessage) != -1);
		
		// database side-effect checks
		UserDto[] observedReceivers = chatDB.getPrivatePeers(sender);
		assertNotNull("Sending a private message did not generate a relation between the involved private peers", observedReceivers);
		assertEquals("Sending a private message did not generate the correct number of relations between the involved private peers", 1, observedReceivers.length);
		assertEquals("Sending a private message did not correctly generate a relation between the involved private peers", receiver, observedReceivers[0]);
		PrivateMessageDto[] observedPrivateMessages = chatDB.getPrivateMessages(sender, receiver);
		assertEquals("The private message was not correctly recorded", 1, observedPrivateMessages.length);
		assertEquals("The private message contents are wrong", expectedPrivateMessage, observedPrivateMessages[0].getMessage());
		assertEquals("The private message sender   is wrong", sender,   observedPrivateMessages[0].getSender());
		assertEquals("The private message receiver is wrong", receiver, observedPrivateMessages[0].getRecipient());
	}
	
	@Test
	public void testPrivateMessageToUnexistingNickname() throws SQLException {
		createUserAndNickname("21991234899", "dom");
		CommandMessageDto[] messages = sendPrivateMessage("21991234899", "unexistingNick", "This message should never be delivered to no one...");
		assertEquals("Wrong response message to tell the nickname was not found", SMSAppModulePhrasingsProfile.getNicknameNotFound("unexistingNick"), messages[0].getText());
	}

}
