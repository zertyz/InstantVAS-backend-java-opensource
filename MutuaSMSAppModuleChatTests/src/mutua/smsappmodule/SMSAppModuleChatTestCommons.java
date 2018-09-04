package mutua.smsappmodule;

import static instantvas.tests.InstantVASSMSAppModuleChatTestsConfiguration.*;

import java.sql.SQLException;

import instantvas.tests.InstantVASSMSAppModuleChatTestsConfiguration;
import mutua.events.MO;
import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.commands.CommandMessageDto;
import mutua.smsappmodule.smslogic.navigationstates.NavigationState;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto;

/** <pre>
 * SMSAppModuleChatTestCommons.java
 * ================================
 * (created by luiz, Sep 9, 2015)
 *
 * A refactoring for the common methods for the Chat module tests
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleChatTestCommons {

	// configuration
	InstantVASSMSAppModuleChatTestsConfiguration config = InstantVASSMSAppModuleChatTestsConfiguration.getInstance();
	
	/**************
	** DATABASES ** 
	**************/
	
	public IUserDB    userDB    = BASE_MODULE_DAL.getUserDB();
	public ISessionDB sessionDB = BASE_MODULE_DAL.getSessionDB();
	public IProfileDB profileDB = PROFILE_MODULE_DAL.getProfileDB();
	public IChatDB    chatDB    = CHAT_MODULE_DAL.getChatDB();
	
	/*******************
	** COMMON METHODS **
	*******************/
	
	/** resets all pertinent databases, including the optional 'moQueue' */
	public void resetChatTables() throws SQLException {
		chatDB.reset();
		profileDB.reset();
		userDB.reset();
		sessionDB.reset();
		if (config.MO_QUEUE_LINK != null) {
			config.MO_QUEUE_LINK.resetQueues();
		}
	}
	
	/** simulates the recording of an MO message, returning the 'moId' -- just like it is done on the webapp */
	public int addMO(UserDto user, String moText) throws SQLException {
		if (chatDB instanceof mutua.smsappmodule.dal.postgresql.ChatDB) {
			return config.MO_QUEUE_PRODUCER.addToMOQueue(new MO(user.getPhoneNumber(), moText));
		} else if (chatDB instanceof mutua.smsappmodule.dal.mvstore.ChatDB) {
			return ((mutua.smsappmodule.dal.mvstore.ChatDB)chatDB).addMO(moText);
		} else if (chatDB instanceof mutua.smsappmodule.dal.ram.ChatDB) {
			return ((mutua.smsappmodule.dal.ram.ChatDB)chatDB).addMO(moText);
		} else {
			throw new RuntimeException("Don't know how to set an MO for Chat DAL type '"+chatDB.getClass().getCanonicalName()+"'");
		}
	}
	
	
	/** registers a user and attribute a nickname to it, so it can be later referenced by private messages */
	public UserDto createUserAndNickname(String phone, String nickname) throws SQLException {
		UserDto user = userDB.assureUserIsRegistered(phone);
		profileDB.setProfileRecord(new ProfileDto(user, nickname));
		return user;
	}

	/** constructs and adds an MO to the queue, issuing the command to send it and returning the resulting MTs */
	public CommandMessageDto[] sendPrivateMessage(String senderPhone, String targetNickname, String message) throws SQLException {
		UserDto sender   = userDB.assureUserIsRegistered(senderPhone);
		String moText = "P " + targetNickname + " " + message;
		int moId = addMO(sender, moText);
		SessionModel session = new SessionModel(sender, new IncomingSMSDto(moId, sender.getPhoneNumber(), moText, null, shortCode), null) {
			public NavigationState getNavigationStateFromStateName(String navigationStateName) {return null;}
		};
		return config.chatModuleCommands.cmdSendPrivateMessage.processCommand(session, null, new String[] {targetNickname, message}).getResponseMessages();
	}
	
}