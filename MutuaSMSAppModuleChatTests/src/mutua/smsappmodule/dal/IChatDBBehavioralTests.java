package mutua.smsappmodule.dal;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationChatTests.*;
import static mutua.smsappmodule.SMSAppModuleChatTestCommons.*;
import static org.junit.Assert.*;

import java.sql.SQLException;

import mutua.events.MO;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.dto.PrivateMessageDto;
import mutua.smsappmodule.dto.UserDto;

import org.junit.Before;
import org.junit.Test;

/** <pre>
 * IChatDBBehavioralTests.java
 * ===========================
 * (created by luiz, Sep 8, 2015)
 *
 * Tests the normal-circumstance usage of {@link IChatDB} implementations
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class IChatDBBehavioralTests {

	private IUserDB userDB = DEFAULT_MODULE_DAL.getUserDB();
	private IChatDB chatDB = DEFAULT_CHAT_DAL.getChatDB();
	

	/*******************
	** COMMON METHODS **
	*******************/
	
	@Before
	public void resetTables() throws SQLException {
		resetChatTables(chatDB);
	}
	

	/**********
	** TESTS **
	**********/
	
	@Test
	public void testNonExistingPeers() throws SQLException {
		UserDto   user  = userDB.assureUserIsRegistered("21991234899");
		UserDto[] peers = chatDB.getPrivatePeers(user);
		assertNull("Non-existing chat sessions must return no peers", peers);
	}
	
	@Test
	public void testNonExistingPrivateMessages() throws SQLException {
		UserDto user1 = userDB.assureUserIsRegistered("21991234899");
		UserDto user2 = userDB.assureUserIsRegistered("21998019167");
		PrivateMessageDto[] messages = chatDB.getPrivateMessages(user1, user2);
		assertNull("Non-existing chat sessions must return no messages", messages);
	}
	
	@Test
	public void testSimpleUsage() throws SQLException {
		UserDto dom  = userDB.assureUserIsRegistered("21991234899");
		UserDto paty = userDB.assureUserIsRegistered("21998019167");
		
		String mo1Text           = "M paty did you get that?";
		String mo1PrivateMessage = "did you get that?";
		String mo2Text           = "M dom yes, I did!";
		String mo2PrivateMessage = "yes, I did!";
		
		// test ping-pong
		int mo1Id = addMO(chatDB, dom, mo1Text);
		int mo2Id = addMO(chatDB, paty, mo2Text);
		chatDB.logPrivateMessage(dom, paty, mo1Id, mo1Text, mo1PrivateMessage);
		chatDB.logPrivateMessage(paty, dom, mo2Id, mo2Text, mo2PrivateMessage);
		UserDto[] domPeers  = chatDB.getPrivatePeers(dom);
		UserDto[] patyPeers = chatDB.getPrivatePeers(paty);
		assertEquals("Dom's peers count is wrong",  1, domPeers.length);
		assertEquals("Paty's peers count is wrong", 1, patyPeers.length);
		assertEquals("Dom's peer doesn't match", paty, domPeers[0]);
		assertEquals("Paty's peer doesn't match", dom, patyPeers[0]);
		PrivateMessageDto[] domPatyMessages = chatDB.getPrivateMessages(dom, paty);
		PrivateMessageDto[] patyDomMessages = chatDB.getPrivateMessages(paty, dom);
		assertEquals("Dom's private messages count is wrong",  2, domPatyMessages.length);
		assertEquals("Paty's private messages count is wrong", 2, patyDomMessages.length);
		assertEquals("Private messages sorting is wrong", domPatyMessages[0], patyDomMessages[0]);
		assertEquals("Private messages sorting is wrong", domPatyMessages[1], patyDomMessages[1]);
		assertEquals("First messages' sender is wrong",    dom,               patyDomMessages[0].getSender());
		assertEquals("First messages' recipient is wrong", paty,              patyDomMessages[0].getRecipient());
		assertEquals("First messages' content is wrong",   mo1PrivateMessage, patyDomMessages[0].getMessage());
		assertEquals("Second messages' sender is wrong",    paty,              patyDomMessages[1].getSender());
		assertEquals("Second messages' recipient is wrong", dom,               patyDomMessages[1].getRecipient());
		assertEquals("Second messages' content is wrong",   mo2PrivateMessage, patyDomMessages[1].getMessage());
	}
	
	
	/*********************
	** ERROR CONDITIONS **
	*********************/
	
	@Test(expected=RuntimeException.class)
	public void testMOTextAndPrivateMessageDoNotMatch() throws SQLException {
		UserDto dom  = userDB.assureUserIsRegistered("21991234899");
		UserDto paty = userDB.assureUserIsRegistered("21998019167");
		String moText = "M paty this is what I sent to you";
		int moId = addMO(chatDB, dom, moText);
		chatDB.logPrivateMessage(dom, paty, moId, moText, "but this is what the chat system reports to the db I sent instead");
	}

	@Test(expected=RuntimeException.class)
	public void testMOTextAndPrivateMessageDoNotMatchEntirely() throws SQLException {
		UserDto dom  = userDB.assureUserIsRegistered("21991234899");
		UserDto paty = userDB.assureUserIsRegistered("21998019167");
		String moText = "M paty this is what I sent to you";
		int moId = addMO(chatDB, dom, moText);
		chatDB.logPrivateMessage(dom, paty, moId, moText, "this is what I sent");
	}

}
