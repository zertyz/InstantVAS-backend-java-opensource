package mutua.smsappmodule.dal.mvstore;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import adapters.MVStoreAdapter;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dto.UserDto;

/** UserDB.java
 * ============
 * (created by luiz, Aug 31, 2018)
 *
 * Implements the MVStore version of {@link IUserDB}.
 * 
 * Note: the data model for MVStore implies we don't need user ids and stuff. The index will be on the phone number.
 * 
 * @see IUserDB
 * @author luiz
*/

public class UserDB implements IUserDB {
	
	// Users := {[phone] = registrationTimeMillis}
	private MVMap<String, Long> users;
	
	public UserDB() {
		MVStore store = MVStoreAdapter.getStore();
		users         = store.openMap("smsappmodule.Users", new MVMap.Builder<String, Long>());
	}

	@Override
	public void reset() {
		users.clear();
	}

	@Override
	public UserDto assureUserIsRegistered(String phoneNumber) {
		long registrationTimeMillis;
		if (!users.containsKey(phoneNumber)) {
			registrationTimeMillis = System.currentTimeMillis();
			users.put(phoneNumber, registrationTimeMillis);
		} else {
			registrationTimeMillis = users.get(phoneNumber);
		}
		return new UserDto(((int)registrationTimeMillis % Integer.MAX_VALUE), phoneNumber);
	}

}
