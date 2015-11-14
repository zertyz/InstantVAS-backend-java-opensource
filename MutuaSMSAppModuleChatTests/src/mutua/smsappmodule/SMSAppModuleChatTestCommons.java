package mutua.smsappmodule;

import static mutua.smsappmodule.SMSAppModuleChatTestCommons.*;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationChatTests.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsChat.*;

import java.sql.SQLException;

import mutua.events.MO;
import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.SpecializedMOQueueDataBureau;
import mutua.events.TestEventServer;
import mutua.events.TestEventServer.ETestEventServices;
import mutua.smsappmodule.config.SMSAppModuleConfiguration;
import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterChat;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.commands.CommandMessageDto;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto;

import org.junit.Assert;
import org.junit.Before;

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

	
	/**************
	** DATABASES ** 
	**************/
	
	public static IUserDB    userDB    = DEFAULT_MODULE_DAL.getUserDB();
	public static ISessionDB sessionDB = DEFAULT_MODULE_DAL.getSessionDB();
	public static IProfileDB profileDB = DEFAULT_PROFILE_DAL.getProfileDB();
	public static IChatDB    chatDB    = DEFAULT_CHAT_DAL.getChatDB();

	// MOs (also a database)
	public static PostgreSQLQueueEventLink<ETestEventServices> moQueueLink;
	public static TestEventServer moQueueProducer;

	
	static {
			try {
				// to use or not to use database queues to register MOs
				if (DEFAULT_CHAT_DAL == SMSAppModuleDALFactoryChat.POSTGRESQL) {
					moQueueLink = new PostgreSQLQueueEventLink<ETestEventServices>(ETestEventServices.class, MO_DATABASE_NAME, new SpecializedMOQueueDataBureau());
					moQueueProducer = new TestEventServer(moQueueLink);
				}
			} catch (Throwable t) {
				t.printStackTrace();
				Assert.fail("Exception detected while initializing the Chat Module Tests: " + t.toString());
			}

	}

	/*******************
	** COMMON METHODS **
	*******************/
	
	/** resets all pertinent databases, including the optional 'moQueue' */
	public static void resetChatTables() throws SQLException {
		chatDB.reset();
		profileDB.reset();
		userDB.reset();
		sessionDB.reset();
		if (moQueueLink != null) {
			moQueueLink.resetQueues();
		}
	}
	
	/** simulates the recording of an MO message, returning the 'moId' -- just like it is done on the webapp */
	public static int addMO(UserDto user, String moText) throws SQLException {
		if (chatDB instanceof mutua.smsappmodule.dal.postgresql.ChatDB) {
			return moQueueProducer.addToMOQueue(new MO(user.getPhoneNumber(), moText));
		} else if (chatDB instanceof mutua.smsappmodule.dal.ram.ChatDB) {
			return ((mutua.smsappmodule.dal.ram.ChatDB)chatDB).addMO(moText);
		} else {
			throw new RuntimeException("Don't know how to set an MO for Chat DAL type '"+chatDB.getClass().getCanonicalName()+"'");
		}
	}
	
	
	/** registers a user and attribute a nickname to it, so it can be later referenced by private messages */
	public static UserDto createUserAndNickname(String phone, String nickname) throws SQLException {
		UserDto user = userDB.assureUserIsRegistered(phone);
		profileDB.setProfileRecord(new ProfileDto(user, nickname));
		return user;
	}

	/** constructs and adds an MO to the queue, issuing the command to send it and returning the resulting MTs */
	public static CommandMessageDto[] sendPrivateMessage(String senderPhone, String targetNickname, String message) throws SQLException {
		UserDto sender   = userDB.assureUserIsRegistered(senderPhone);
		String moText = "P " + targetNickname + " " + message;
		int moId = addMO(sender, moText);
		SessionModel session = new SessionModel(sender, new IncomingSMSDto(moId, sender.getPhoneNumber(), moText, null, SMSAppModuleConfiguration.APPShortCode), null);
		return cmdSendPrivateMessage.processCommand(session, null, new String[] {targetNickname, message}).getResponseMessages();
	}
	
}
