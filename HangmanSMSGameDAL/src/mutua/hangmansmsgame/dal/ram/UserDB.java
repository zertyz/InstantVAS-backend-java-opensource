package mutua.hangmansmsgame.dal.ram;

import java.util.Hashtable;

import mutua.hangmansmsgame.dal.IUserDB;

/** <pre>
 * UserDB.java
 * ===========
 * (created by luiz, Jan 3, 2015)
 *
 * Implements a RAM version of 'IUserDB'
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

	@Override
	public void reset() {
		nicknamesByPhone.clear();
		phonesByNickname.clear();
	}

	@Override
	public String getUserNickname(String phoneNumber) {
		return nicknamesByPhone.get(phoneNumber);
	}

	@Override
	public boolean checkAvailabilityAndRecordNickname(String phoneNumber, String nickname) {
		if (phonesByNickname.containsKey(nickname)) {
			return false;
		} else {
			// TODO test updating the nickname and leaving no tracks
			nicknamesByPhone.put(phoneNumber, nickname);
			phonesByNickname.put(nickname, phoneNumber);
			return true;
		}
	}

	@Override
	public boolean isUserOnRecord(String phoneNumber) {
		return nicknamesByPhone.containsKey(phoneNumber);
	}

}
