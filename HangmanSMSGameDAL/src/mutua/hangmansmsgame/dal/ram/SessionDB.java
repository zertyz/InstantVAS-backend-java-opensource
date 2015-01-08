package mutua.hangmansmsgame.dal.ram;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.TreeMap;

import mutua.hangmansmsgame.dal.ISessionDB;
import mutua.hangmansmsgame.dal.dto.SessionDto;

/** <pre>
 * SessionDB.java
 * ==============
 * (created by luiz, Jan 2, 2015)
 *
 * Implements a RAM version of 'ISessionDB'
 *
 * @see ISessionDB
 * @version $Id$
 * @author luiz
 */

public class SessionDB implements ISessionDB {
	
	
	// data structures
	//////////////////
	
	/** sessions := {[phone] = SessionDto, ...} */
	private static Hashtable<String, SessionDto> sessions     = new Hashtable<String, SessionDto>();
	private static TreeMap<String, SessionDto> sortedSessions = new TreeMap<String, SessionDto>(new Comparator<String>() {
		@Override
		// TODO possibly use other than TreeMap structure for sorting. It is odd behaviored, allowing or disallowing elements whether we return 0 or not
		public int compare(String o1, String o2) {
			if (o1.equals(o2)) {
				return 0;
			}
			SessionDto s1 = sessions.get(o1);
			SessionDto s2 = sessions.get(o2);
			int comparission = (int)(s2.getTimestamp()-s1.getTimestamp());
			if (comparission == 0) {
				return 1;
			} else {
				return comparission;
			}
		}
	});
	
	
	// ISessionDB implementation
	////////////////////////////

	@Override
	public void reset() {
		sortedSessions.clear();
		sessions.clear();
	}

	@Override
	public SessionDto getSession(String phone) {
		return sessions.get(phone);
	}

	@Override
	public void setSession(SessionDto session) {
		sessions.put(session.getPhone(), session);
		sortedSessions.put(session.getPhone(), session);
	}

	@Override
	public String[] getRecentlyUpdatedSessionPhoneNumbers(int limit) {
		String[] phoneNumbers = new String[limit];
		int index = 0;
		for (String phone : sortedSessions.keySet()) {
			phoneNumbers[index++] = phone;
			if (index == limit) {
				break;
			}
		}
		return Arrays.copyOf(phoneNumbers, index);
	}

}
