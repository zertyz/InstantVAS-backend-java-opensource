package mutua.hangmansmsgame.dal;

import java.sql.SQLException;
import java.util.Map;

/** <pre>
 * IUserDB.java
 * ============
 * (created by luiz, Jan 2, 2015)
 *
 * Defines access methods for the "Users" data base, with some common logic attatched to it
 *
 * @see ram.UserDB, postgresql.UserDB
 * @version $Id$
 * @author luiz
 */

public abstract class IUserDB {
	
	/** Reset the database, for testing purposes */
	public abstract void reset() throws SQLException;

	/** Given a case insensitive nickname, return the correctly cased nickname, as it was registered */
	public abstract String getCorrectlyCasedNickname(String nickname) throws SQLException;

	/** Retrieve the nickname for a user, based on it's 'phoneNumber' */
	public abstract String getUserNickname(String phoneNumber) throws SQLException;

	/** Retrieve the phone number for a user, based on it's case insensitive 'nickname', or null if
	 *  none was found. */
	public abstract String getUserPhoneNumber(String nickname) throws SQLException;

	/** Returns true if the provided case insensitive nickname was not being used by any record on 
	 *  the database and could be, then, associated with the provided 'phoneNumber'. */
	protected abstract boolean checkAvailabilityAndRecordNickname(String phoneNumber, String nickname) throws SQLException;
	
	/** When a 'nickname' is part of a sequence (that is: there are nicks that match '^nickname\%d+$'), returns the elements
	 *  of that sequence, or an empty list if that nick is not part of a sequence; */
	protected abstract Map<String, Boolean> getNicknameAutonumberedSequenceElements(String nickname) throws SQLException;
	
	/** High level user registration: if 'nickname' exists on the database, try to register 'nickname{i+1}' and so on.
	 *  Returns the final assigned nickname to 'phoneNumber' */
	public String assignSequencedNicknameToPhone(String phoneNumber, String nickname) throws SQLException {
		// first step: is the nickname available or is it part of an auto generated sequence?
		boolean isPartOfAnAutonumberedNickSequence = ! checkAvailabilityAndRecordNickname(phoneNumber, nickname);
		if (!isPartOfAnAutonumberedNickSequence) {
			return nickname;
		}
		// So it is part of a sequence. Lets fetch the whole sequence:
		Map<String, Boolean> nicks = getNicknameAutonumberedSequenceElements(nickname);
		// now lets find a whole on the sequence (which starts on 1) to place the new element or at the end of it (up to 10 tries, in case of concurrent accesses)
		for (int i=1; i<nicks.size()+10; i++) {
			String newNicknameSequenceCandidate = nickname + i;
			if (!nicks.containsKey(newNicknameSequenceCandidate)) {
				if (checkAvailabilityAndRecordNickname(phoneNumber, newNicknameSequenceCandidate)) {
					return newNicknameSequenceCandidate;
				}
			}
		}
		return null;
	}
	
	/** Returns whether we have a record or not for a specific user through it's phone number.
	 *  If there isn't one, probably it is time to 'createUserRecord' */
	public abstract boolean isUserOnRecord(String phoneNumber) throws SQLException;

	/** Returns true if the user is registered as a subscriber */
	public abstract boolean isUserSubscribed(String phoneNumber) throws SQLException;
	
	/** Sets the 'subscribed' state for the user */
	public abstract void setSubscribed(String phoneNumber, boolean subscribed) throws SQLException;

	/** Get & increment the next bot word pointer for the user specified by 'phoneNumber' */
	public abstract int getAndIncrementNextBotWord(String phoneNumber) throws SQLException;

}
