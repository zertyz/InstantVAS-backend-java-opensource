package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dto.PrivateMessageDto;
import mutua.smsappmodule.dto.UserDto;
import adapters.JDBCAdapter;
import adapters.dto.PreparedProcedureInvocationDto;

/** <pre>
 * ChatDB.java
 * ===========
 * (created by luiz, Sep 8, 2015)
 *
 * Implements the POSTGRESQL version of {@link IChatDB}
 *
 * @see IChatDB
 * @version $Id$
 * @author luiz
 */

public class ChatDB extends IChatDB {
	
	private JDBCAdapter dba;
	
	
	public ChatDB() throws SQLException {
		dba = SMSAppModulePostgreSQLAdapterChat.getChatDBAdapter();
	}

	@Override
	public void reset() throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("ResetTable");
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public void logPrivateMessage(UserDto sender, UserDto recipient, int moId, int moTextStartIndex) throws SQLException {
		PreparedProcedureInvocationDto procedure;
		procedure = new PreparedProcedureInvocationDto("InsertPrivateMessage");
		procedure.addParameter("MO_ID", moId);
		procedure.addParameter("SENDER_USER_ID",         sender.getUserId());
		procedure.addParameter("RECIPIENT_USER_ID",   recipient.getUserId());
		procedure.addParameter("MO_TEXT_START_INDEX", moTextStartIndex);
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public UserDto[] getPrivatePeers(UserDto user) throws SQLException {
		PreparedProcedureInvocationDto procedure;
		procedure = new PreparedProcedureInvocationDto("SelectPeers");
		procedure.addParameter("USER_ID", user.getUserId());
		Object[][] rows = dba.invokeArrayProcedure(procedure);
		if (rows.length == 0) {
			return null;
		}
		UserDto[] users = new UserDto[rows.length];
		for (int i=0; i<rows.length; i++) {
			Object[] row = rows[i];
			int    userId      = (Integer)row[0];
			String phoneNumber = (String) row[1];
			users[i] = new UserDto(userId, phoneNumber);
		}
		return users;
	}

	@Override
	public PrivateMessageDto[] getPrivateMessages(UserDto user1, UserDto user2) throws SQLException {
		PreparedProcedureInvocationDto procedure;
		procedure = new PreparedProcedureInvocationDto("SelectPrivateMessages");
		procedure.addParameter("USER1_ID", user1.getUserId());
		procedure.addParameter("USER2_ID", user2.getUserId());
		Object[][] rows = dba.invokeArrayProcedure(procedure);
		if (rows.length == 0) {
			return null;
		}
		PrivateMessageDto[] privateMessages = new PrivateMessageDto[rows.length];
		for (int i=0; i<rows.length; i++) {
			Object[] row = rows[i];
			int    moId                 = (Integer)row[0];
			int    senderUserId         = (Integer)row[1];
			String senderPhoneNumber    = (String) row[2];
			int    recipientUserId      = (Integer)row[3];
			String recipientPhoneNumber = (String) row[4];
			String message              = (String) row[5];
			UserDto sender    = new UserDto(senderUserId,    senderPhoneNumber);
			UserDto recipient = new UserDto(recipientUserId, recipientPhoneNumber);
			privateMessages[i] = new PrivateMessageDto(sender, recipient, moId, message);
		}
		return privateMessages;
	}

}