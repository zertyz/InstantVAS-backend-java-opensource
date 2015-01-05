package mutua.hangmansmsgame.dal;

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
	void reset();
	
	/** Retrieve the nickname for a user, based on it's 'phoneNumber' */
	String getUserNickname(String phoneNumber);

	/** Retrieve the phone number for a user, based on it's 'nickname' */
	String getUserPhoneNumber(String nickname);	

	/** Returns true if the provided nickname was not being used by any record on the database and could be, then,
	 *  associated with the provided 'phoneNumber'. */
	boolean checkAvailabilityAndRecordNickname(String phoneNumber, String nickname);
	
	/** Returns whether we have a record or not for a specific user through it's phone number.
	 *  If there isn't one, probably it is time to 'createUserRecord' */
	boolean isUserOnRecord(String phoneNumber);

//	/** Creates a record for a user, so the system may start interacting with it */
//	void createUserRecord(String phoneNumber, String nickName);

}
