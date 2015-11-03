package mutua.smsappmodule.dal.ram;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * SessionDB.java
 * ==============
 * (created by luiz, Jul 15, 2015)
 *
 * Implements the RAM version of {@link ISessionDB}
 *
 * @see IUserDB
 * @version $Id$
 * @author luiz
 */

public class SessionDB implements ISessionDB {


	// data structures
	//////////////////
	
	/** sessions := {[user] = {[propName] = propVal, ...}, ...} */
	private static Hashtable<UserDto, Hashtable<String, String>> sessions = new Hashtable<UserDto, Hashtable<String, String>>();

	
	// common methods
	/////////////////
	
	private Hashtable<String, String> getSessionProperties(UserDto user) {
		Hashtable<String, String> properties = sessions.get(user);
		if (properties == null) {
			properties = new Hashtable<String, String>();
			sessions.put(user, properties);
		}
		return properties;
	}

	
	// ISessionDB implementation
	////////////////////////////

	@Override
	public void reset() {
		sessions.clear();
	}

	@Override
	public SessionDto getSession(UserDto user) {
		if (!sessions.containsKey(user)) {
			return null;
		}
		Hashtable<String, String> properties = getSessionProperties(user);
		String[][] storedProperties = new String[properties.size()][2];
		int storedPropertiesIndex = 0;
		for (String propertyName : properties.keySet()) {
			String propertyValue = properties.get(propertyName);
			storedProperties[storedPropertiesIndex][0] = propertyName;
			storedProperties[storedPropertiesIndex][1] = propertyValue;
			storedPropertiesIndex++;
		}
		return new SessionDto(user, storedProperties);
	}

	@Override
	public void setSession(SessionDto session) {
		Hashtable<String, String> properties = getSessionProperties(session.getUser());
		for (String[][] sessionProperties : Arrays.asList(session.getNewProperties(), session.getUpdatedProperties())) {
			for (String[] sessionProperty : sessionProperties) {
				String propertyName  = sessionProperty[0];
				String propertyValue = sessionProperty[1];
				properties.put(propertyName, propertyValue);
			}
		}
	}

	@Override
	public void assureProperty(UserDto user, String propertyName, String propertyValue) {
		Hashtable<String, String> properties = getSessionProperties(user);
		properties.put(propertyName, propertyValue);
	}

}
