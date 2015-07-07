package mutua.hangmansmsgame.dal.ram;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import mutua.hangmansmsgame.dal.IUserDB;

/** <pre>
 * UserDB.java
 * ===========
 * (created by luiz, Jan 3, 2015)
 *
 * Implements the RAM version of 'IUserDB'
 *
 * @see IUserDB
 * @version $Id$
 * @author luiz
 */

public class UserDB extends IUserDB {
	
	/** nicknamesByPhone := {[phone] = "nickname", ...} */
	private static Hashtable<String, String> nicknamesByPhone = new Hashtable<String, String>();
	/** phonesByNickname := {[nickname] = "phone", ...} */
	private static Hashtable<String, String> phonesByNickname = new Hashtable<String, String>();
	/** subscriptionsByPhone := {[phone] = "nickname", ...} */
	private static Hashtable<String, Boolean> subscriptionsByPhone = new Hashtable<String, Boolean>();
	/** nextBotWordByPhone := {[phone] = nextBotWord, ...} */
	private static Hashtable<String, Integer> nextBotWordByPhone = new Hashtable<String, Integer>();
	
	
	// AUXILIAR METHODS
	///////////////////
	
	
	
	// IUserDB IMPLEMENTATION
	/////////////////////////

	@Override
	public void reset() {
		nicknamesByPhone.clear();
		phonesByNickname.clear();
		subscriptionsByPhone.clear();
		nextBotWordByPhone.clear();
	}

	@Override
	public String getCorrectlyCasedNickname(String nickname) {
		String lowercaseNickname = nickname.toLowerCase();
		for (String registeredNickname : phonesByNickname.keySet()) {
			String lowercaseRegisteredNickname = registeredNickname.toLowerCase();
			if (lowercaseRegisteredNickname.equals(lowercaseNickname)) {
				return registeredNickname;
			}
		}
		return null;
	}

	@Override
	public String getUserNickname(String phoneNumber) {
		return nicknamesByPhone.get(phoneNumber);
	}

	@Override
	public String getUserPhoneNumber(String nickname) {
		String registeredNickname = getCorrectlyCasedNickname(nickname);
		if (registeredNickname != null) {
			String registeredPhone = phonesByNickname.get(registeredNickname);
			return registeredPhone;
		} else {
			return null;
		}
	}

	@Override
	protected boolean checkAvailabilityAndRecordNickname(String phoneNumber, String nickname) {
		
		String registeredPhoneNumber = getUserPhoneNumber(nickname);
		if ((registeredPhoneNumber != null) && (!registeredPhoneNumber.equals(phoneNumber))) {
			return false;
		}
		
		nicknamesByPhone.put(phoneNumber, nickname);
		phonesByNickname.put(nickname, phoneNumber);
		return true;
	}
	
	@Override
	protected Map<String, Boolean> getNicknameAutonumberedSequenceElements(String nickname) throws SQLException {
		HashMap<String, Boolean> nicks = new HashMap<String, Boolean>();
		for (String nick : phonesByNickname.keySet()) {
			if (nick.startsWith(nickname)) {
				nicks.put(nick, true);
			}
		}
		return nicks;
	}

	@Override
	public boolean isUserOnRecord(String phoneNumber) {
		return nicknamesByPhone.containsKey(phoneNumber);
	}

	@Override
	public boolean isUserSubscribed(String phoneNumber) {
		if (!subscriptionsByPhone.containsKey(phoneNumber)) {
			return false;
		}
		return subscriptionsByPhone.get(phoneNumber);
	}

	@Override
	public void setSubscribed(String phoneNumber, boolean subscribed) {
		subscriptionsByPhone.put(phoneNumber, subscribed);
	}

	@Override
	public int getAndIncrementNextBotWord(String phoneNumber) {
		int nextBotWord = 0;
		int returnValue;
		if (nextBotWordByPhone.containsKey(phoneNumber)) {
			nextBotWord = nextBotWordByPhone.get(phoneNumber);
		}
		returnValue = nextBotWord++;
		nextBotWordByPhone.put(phoneNumber, nextBotWord);
		return returnValue;
	}

}
