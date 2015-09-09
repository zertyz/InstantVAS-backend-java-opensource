package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.smsappmodule.dto.PrivateMessageDto;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * IChatDB.java
 * ============
 * (created by luiz, Sep 8, 2015)
 *
 * Defines access methods for the "User Profile" table
 *
 * @see mutua.smsappmodule.dal.ram.ChatDB
 * @see mutua.smsappmodule.dal.postgresql.ChatDB
 * @version $Id$
 * @author luiz
 */

public abstract class IChatDB {

	/** Resets the database, for testing purposes */
	public abstract void reset() throws SQLException;

	/** Stores a private chat message based on an 'moId'. 'moTextStartIndex' is such so that "moText.substring(moTextStringIndex)" returns the private message */
	public abstract void logPrivateMessage(UserDto sender, UserDto recipient, int moId, int moTextStartIndex) throws SQLException;
	
	/** High level method for storing a private chat message. The default implementation calculates 'moTextStartIndex' and
	 *  uses {@link #logPrivateMessage(UserDto, UserDto, int, int)} to store on the database */
	public void logPrivateMessage(UserDto sender, UserDto recipient, int moId, String moText, String privateMessage) throws SQLException {
		int moTextStartIndex = moText.indexOf(privateMessage);
		if (moTextStartIndex == -1) {
			throw new RuntimeException("Could not log a private message. Chat message '"+privateMessage+"' is not present on the moText ('"+moText+"')");
		} else if ((moTextStartIndex + privateMessage.length()) != moText.length()) {
			throw new RuntimeException("Could not log a private message. Chat message '"+privateMessage+"' is not at the end of the moText '"+moText+"'");
		}
		logPrivateMessage(sender, recipient, moId, moTextStartIndex);
	}
	
	/** Retrieves all peers who participated on private conversations with 'user' */
	public abstract UserDto[] getPrivatePeers(UserDto user) throws SQLException;

	/** Retrieves all private messages between 'user1' and 'user2', sorted by 'moId' */
	public abstract PrivateMessageDto[] getPrivateMessages(UserDto user1, UserDto user2) throws SQLException;

}
