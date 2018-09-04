package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dto.PrivateMessageDto;
import mutua.smsappmodule.dto.UserDto;

import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterChat.Parameters.*;

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
	
	private SMSAppModulePostgreSQLAdapterChat dba;
	
	
	public ChatDB(String moTableName, String moIdFieldName, String moTextFieldName) throws SQLException {
		dba = SMSAppModulePostgreSQLAdapterChat.getChatDBAdapter(moTableName, moIdFieldName, moTextFieldName);
	}

	@Override
	public void reset() throws SQLException {
		dba.invokeUpdateProcedure(dba.ResetTables);
	}

	@Override
	public void logPrivateMessage(UserDto sender, UserDto recipient, int moId, int moTextStartIndex) throws SQLException {
		dba.invokeUpdateProcedure(dba.InsertPrivateMessage,
			MO_ID,               moId,
			SENDER_USER_ID,      sender.getUserId(),
			RECIPIENT_USER_ID,   recipient.getUserId(),
			MO_TEXT_START_INDEX, moTextStartIndex);
	}

	@Override
	public UserDto[] getPrivatePeers(UserDto user) throws SQLException {
		Object[][] rows = dba.invokeArrayProcedure(dba.SelectPeers, USER_ID, user.getUserId());
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
		Object[][] rows = dba.invokeArrayProcedure(dba.SelectPrivateMessages,
			USER1_ID, user1.getUserId(),
			USER2_ID, user2.getUserId());
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