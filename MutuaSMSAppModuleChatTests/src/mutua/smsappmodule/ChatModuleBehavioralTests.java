package mutua.smsappmodule;

import static mutua.smsappmodule.SMSAppModuleChatTestCommons.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationChatTests.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.*;
import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.icc.configuration.ConfigurationManager;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;
import mutua.smsappmodule.config.SMSAppModuleConfigurationChat;
import mutua.smsappmodule.dto.PrivateMessageDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.commands.CommandMessageDto;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto;

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
		ConfigurationManager cm = new ConfigurationManager(log, SMSAppModuleConfigurationChat.class);
		String serializedFields = cm.serializeConfigurableClasses();
		System.out.println(serializedFields);
		cm.deserializeConfigurableClasses(serializedFields);
	}
	
	@Test
	public void testPrivateMessage() throws SQLException {
		String expectedPrivateMessage = "this is the message";
		
		createUserAndNickname("21991234899", "sourceNick");
		createUserAndNickname("21997559595", "destinationNick");
		UserDto sender   = userDB.assureUserIsRegistered("21991234899");
		UserDto receiver = userDB.assureUserIsRegistered("21997559595");
		String moText = "P destinationNick " + expectedPrivateMessage;
		int moId = addMO(sender, moText);
		SessionModel session = new SessionModel(sender, new IncomingSMSDto(moId, sender.getPhoneNumber(), moText, null, SMSAppModuleConfiguration.APPShortCode), null);
		
		CommandMessageDto[] messages = cmdSendPrivateMessage.processCommand(session, null, new String[] {"destinationNick", expectedPrivateMessage}).getResponseMessages();
		
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

}
