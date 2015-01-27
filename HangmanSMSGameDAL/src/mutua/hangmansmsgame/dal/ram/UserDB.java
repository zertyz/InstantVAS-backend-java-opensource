package mutua.hangmansmsgame.dal.ram;

import java.sql.SQLException;
import java.util.Hashtable;

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

public class UserDB implements IUserDB {
	
	/** nicknamesByPhone := {[phone] = "nickname", ...} */
	private static Hashtable<String, String> nicknamesByPhone = new Hashtable<String, String>();
	/** phonesByNickname := {[nickname] = "phone", ...} */
	private static Hashtable<String, String> phonesByNickname = new Hashtable<String, String>();
	/** subscriptionsByPhone := {[phone] = "nickname", ...} */
	private static Hashtable<String, Boolean> subscriptionsByPhone = new Hashtable<String, Boolean>();
	
	
	// AUXILIAR METHODS
	///////////////////
	
	
	
	// IUserDB IMPLEMENTATION
	/////////////////////////

	@Override
	public void reset() {
		nicknamesByPhone.clear();
		phonesByNickname.clear();
		subscriptionsByPhone.clear();
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
	public boolean checkAvailabilityAndRecordNickname(String phoneNumber, String nickname) {
		
		String registeredPhoneNumber = getUserPhoneNumber(nickname);
		if ((registeredPhoneNumber != null) && (!registeredPhoneNumber.equals(phoneNumber))) {
			return false;
		}
		
		nicknamesByPhone.put(phoneNumber, nickname);
		phonesByNickname.put(nickname, phoneNumber);
		return true;
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


}
