package mutua.smsappmodule;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationChatTests.*;

import java.sql.SQLException;

import mutua.events.MO;
import mutua.events.PostgreSQLQueueEventLink;
import mutua.events.SpecializedMOQueueDataBureau;
import mutua.events.TestEventServer;
import mutua.events.TestEventServer.ETestEventServices;
import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterChat;
import mutua.smsappmodule.dto.UserDto;

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

	
	// MOs
	public static PostgreSQLQueueEventLink<ETestEventServices> moQueueLink;
	public static TestEventServer moQueueProducer;

	
	static {
			try {
				// to use or not to use database queues to register MOs
				if (DEFAULT_CHAT_DAL == SMSAppModuleDALFactoryChat.POSTGRESQL) {
					moQueueLink = new PostgreSQLQueueEventLink<ETestEventServices>(ETestEventServices.class, "SpecializedMOQueue", new SpecializedMOQueueDataBureau());
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
	
	public static void resetChatTables(IChatDB chatDB) throws SQLException {
		chatDB.reset();
		SMSAppModuleTestCommons.resetTables();
		if (moQueueLink != null) {
			moQueueLink.resetQueues();
		}
	}
	
	// simulates the recording of an MO message, returning the 'moId'
	public static int addMO(IChatDB chatDB, UserDto user, String moText) throws SQLException {
		if (chatDB instanceof mutua.smsappmodule.dal.postgresql.ChatDB) {
			return moQueueProducer.addToMOQueue(new MO(user.getPhoneNumber(), moText));
		} else if (chatDB instanceof mutua.smsappmodule.dal.ram.ChatDB) {
			return ((mutua.smsappmodule.dal.ram.ChatDB)chatDB).addMO(moText);
		} else {
			throw new RuntimeException("Don't know how to set an MO for Chat DAL type '"+chatDB.getClass().getCanonicalName()+"'");
		}
	}
}
