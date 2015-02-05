package mutua.hangmansmsgame.dal;

import java.sql.SQLException;

/** <pre>
 * IUserDB.java
 * ============
 * (created by luiz, Jan 2, 2015)
 *
 * Defines access methods for the "Users" data base
 *
 * @see ram.UserDB, postgresql.UserDB
 * @version $Id$
 * @author luiz
 */

public interface IUserDB {
	
	/** Reset the database, for testing purposes */
	void reset() throws SQLException;

	/** Given a case insensitive nickname, return the correctly cased nickname, as it was registered */
	String getCorrectlyCasedNickname(String nickname) throws SQLException;

	/** Retrieve the nickname for a user, based on it's 'phoneNumber' */
	String getUserNickname(String phoneNumber) throws SQLException;

	/** Retrieve the phone number for a user, based on it's case insensitive 'nickname', or null if
	 *  none was found. */
	String getUserPhoneNumber(String nickname) throws SQLException;

	/** Returns true if the provided case insensitive nickname was not being used by any record on 
	 *  the database and could be, then, associated with the provided 'phoneNumber'. */
	boolean checkAvailabilityAndRecordNickname(String phoneNumber, String nickname) throws SQLException;
	
	/** Returns whether we have a record or not for a specific user through it's phone number.
	 *  If there isn't one, probably it is time to 'createUserRecord' */
	boolean isUserOnRecord(String phoneNumber) throws SQLException;

	/** Returns true if the user is registered as a subscriber */
	boolean isUserSubscribed(String phoneNumber) throws SQLException;
	
	/** Sets the 'subscribed' state for the user */
	void setSubscribed(String phoneNumber, boolean subscribed) throws SQLException;

	/** Get & increment the next bot word pointer for the user specified by 'phoneNumber' */
	int getAndIncrementNextBotWord(String phoneNumber) throws SQLException;

}
