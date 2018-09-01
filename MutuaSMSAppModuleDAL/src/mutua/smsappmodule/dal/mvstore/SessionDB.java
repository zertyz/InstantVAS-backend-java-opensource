package mutua.smsappmodule.dal.mvstore;

import java.sql.SQLException;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import adapters.MVStoreAdapter;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;

/** SessionDB.java
 * ===============
 * (created by luiz, Aug 31, 2018)
 *
 * Implements the MVStore version of {@link ISessionDB}.
 * 
 * @see ISessionDB
 * @author luiz
*/

public class SessionDB implements ISessionDB {
	
	// SessionProperties := {[phone] = {{propName1,propValId}, {propName2,propValId}, ...}, ... }
	// SessionValues     := {[propValId] = propVal, ... }
	private MVMap<String, Object[][]> sessionProperties;
	private MVMap<Integer, String>    sessionValues;
	
	public SessionDB() {
		MVStore store     = MVStoreAdapter.getStore();
		sessionProperties = store.openMap("smsappmodule.Session.Properties", new MVMap.Builder<String, Object[][]>());
		sessionValues     = store.openMap("smsappmodule.Session.Values",     new MVMap.Builder<Integer, String>());
	}

	@Override
	public void reset() {
		sessionProperties.clear();
		sessionValues.clear();
	}

	@Override
	public SessionDto getSession(UserDto user) {
		Object[][] properties = sessionProperties.get(user.getPhoneNumber());
		if (properties == null) {
			return null;
		}
		String[][] storedProperties = new String[properties.length][2];
		for (int i=0; i<properties.length; i++) {
			String storedPropertyName  = (String)(properties[i][0]);
			int    propertyValueId     = (Integer)(properties[i][1]);
			String storedPropertyValue = sessionValues.get(propertyValueId);
			storedProperties[i][0] = storedPropertyName;
			storedProperties[i][1] = storedPropertyValue;
		}
		
		return new SessionDto(user, storedProperties);
	}

	@Override
	public void setSession(SessionDto session) {
		
		String phoneNumber                = session.getUser().getPhoneNumber();
		Object[][] properties             = null;
		boolean didPropertiesChange       = false;
		String[] toBeDeletedPropertyNames = session.getDeletedProperties();
		String[][] toBeUpdatedProperties  = session.getUpdatedProperties();
		String[][] toBeInsertedProperties = session.getNewProperties();
		
		// will we need to load properties?
		if ((toBeDeletedPropertyNames.length > 0) || (toBeUpdatedProperties.length > 0) || (toBeInsertedProperties.length > 0)) {
			properties = sessionProperties.get(phoneNumber);
		}

		
		// delete
		for (int i=0; i<toBeDeletedPropertyNames.length; i++) {
			String toBeDeletedPropertyName = toBeDeletedPropertyNames[i];
			int foundPropertyIndex         = indexOfStoredPropertyName(properties, toBeDeletedPropertyName);
			if (foundPropertyIndex != -1) {
				// delete the value
				int propertyValueId = (Integer)(properties[foundPropertyIndex][1]);
				sessionValues.remove(propertyValueId);
				// delete the name
				// new properties array
				Object[][] newProperties = new Object[properties.length-1][2];
				System.arraycopy(properties, 0, newProperties, 0, foundPropertyIndex);
				if (foundPropertyIndex < properties.length-1) {
					System.arraycopy(properties, foundPropertyIndex+1, newProperties, foundPropertyIndex, properties.length-foundPropertyIndex);
				}
				properties = newProperties;
				didPropertiesChange = true;
			}
		}

		
		// update
		for (int i=0; i<toBeUpdatedProperties.length; i++) {
			String toBeUpdatedPropertyName  = toBeUpdatedProperties[i][0];
			String toBeUpdatedPropertyValue = toBeUpdatedProperties[i][1];
			Object[][] newProperties = assureProperty(phoneNumber, properties, toBeUpdatedPropertyName, toBeUpdatedPropertyValue);
			if (newProperties != null) {	// note: 'newProperties' should always be null when really updating a property (one that already exists)
				properties = newProperties;
				didPropertiesChange = true;
			}
		}
		
		
		// insert
		for (int i=0; i<toBeInsertedProperties.length; i++) {
			String toBeInsertedPropertyName  = toBeInsertedProperties[i][0];
			String toBeInsertedPropertyValue = toBeInsertedProperties[i][1];
			Object[][] newProperties = assureProperty(phoneNumber, properties, toBeInsertedPropertyName, toBeInsertedPropertyValue);
			if (newProperties != null) {
				properties = newProperties;
				didPropertiesChange = true;
			}
		}
		
		// store new properties
		if (didPropertiesChange) {
			sessionProperties.put(phoneNumber, properties);
		}
	}

	@Override
	public void assureProperty(UserDto user, String propertyName, String propertyValue) {
		Object[][] properties    = sessionProperties.get(user.getPhoneNumber());
		Object[][] newProperties = assureProperty(user.getPhoneNumber(), properties, propertyName, propertyValue);
		if (newProperties != null) {
			sessionProperties.put(user.getPhoneNumber(), newProperties);
		}
	}
	
	// factored method with common code between interface's 'assureProperty' and 'setSession'.
	// returns the new array of properties if it was changed (and needs to be persisted) or null otherwise.
	// note: a changed array of properties means a property with that 'propertyName' didn't exist for that 'phoneNumber'
	private Object[][] assureProperty(String phoneNumber, Object[][] properties, String propertyName, String propertyValue) {
		// sanity check -- for the case we are setting the first property if that 'phoneNumber'
		if (properties == null) {
			properties = new Object[0][0];
		}
		// search for the property name and its value id -- creating a new value id, if a property with that name isn't already present
		int propertyValueId; 
		int foundPropertyIndex = indexOfStoredPropertyName(properties, propertyName);

		// create ?
		if (foundPropertyIndex == -1) {
			// new property value and it's id
			synchronized (sessionValues) {
				propertyValueId = sessionValues.size();
				sessionValues.put(propertyValueId, propertyValue);
			}
			// new properties array
			Object[][] newProperties = new Object[properties.length+1][2];
			System.arraycopy(properties, 0, newProperties, 0, properties.length);
			newProperties[properties.length] = new Object[] {propertyName, propertyValueId};
			return newProperties;
		}
		
		// edit ?
		propertyValueId = (Integer)(properties[foundPropertyIndex][1]); 
		sessionValues.put(propertyValueId, propertyValue);
		return null;
	}
	
	// returns the matching index or -1
	// idiom:
	//   int foundPropertyIndex = indexOfStoredPropertyName(properties, propertyName);
	//   int propertyValueId    = (foundPropertyIndex != -1) ? (Integer)(properties[foundPropertyIndex][1]) : -1;
	private int indexOfStoredPropertyName(Object[][] properties, String propertyName) {
		for (int i=0; i<properties.length; i++) {
			String storedPropertyName = (String)(properties[i][0]);
			if (propertyName.equals(storedPropertyName)) {
				return i;
			}
		}
		return -1;
	}

}
