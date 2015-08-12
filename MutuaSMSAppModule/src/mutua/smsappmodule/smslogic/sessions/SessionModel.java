package mutua.smsappmodule.smslogic.sessions;

import java.util.Arrays;
import java.util.HashMap;

import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;

/** <pre>
 * SessionModel.java
 * =================
 * (created by luiz, Jul 14, 2015)
 *
 * Represents the set of session properties a specific user has, both at the database and
 * after the command processing, before committing changes.
 * 
 * Note this class is not reentrant, meaning each instance must be accessed by a single thread.
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SessionModel {
	
	private static HashMap<String, INavigationState> navigationStates = new HashMap<String, INavigationState>();
	
	public static INavigationState getNavigationState(String navigationStateName) {
		return navigationStates.get(navigationStateName);
	}

	public static void registerNewNavigationState(INavigationState navigationState) {
		navigationStates.put(navigationState.getNavigationStateName(), navigationState);
	}
	
	private final UserDto user;
	
	private final HashMap<String, String> storedProperties;
	private final HashMap<String, String> pendingProperties = new HashMap<String, String>(16);
	
	private static final ISessionProperty NAVIGATION_STATE_PROPERTY = new ISessionProperty() {
		@Override
		public String getPropertyName() {
			return "NavState";
		}
	};


	/** Creates a session for 'user' where 'storedProperties' := {{(ISessionProperty)prop, propVal}, ...} */
	public SessionModel(SessionDto sessionDto) {
		String[][] sessionDtoStoredProperties = sessionDto.getStoredProperties();
		this.user              = sessionDto.getUser();
		this.storedProperties  = new HashMap<String, String>(sessionDtoStoredProperties.length+1, 1);
		for (String[] propertyData : sessionDtoStoredProperties) {
			String propertyName  = propertyData[0];
			String propertyValue = propertyData[1];
			this.storedProperties.put(propertyName, propertyValue);
		}
	}
	
	/** Creates a session for 'user' with the 'storedProperties' */
	public SessionModel(UserDto user, HashMap<String, String> storedProperties) {
		this.user              = user;
		this.storedProperties  = storedProperties;
	}
	
	/** Creates an empty session (usually, for a new user) */
	public SessionModel(UserDto user) {
		this.user              = user;
		this.storedProperties  = new HashMap<String, String>();
	}
	
	public String getStringProperty(ISessionProperty property) {
		String value = pendingProperties.get(property.getPropertyName());
		if (value != null) {
			return value;
		} else {
			return storedProperties.get(property.getPropertyName());
		}
	}
	
	public int getIntProperty(ISessionProperty property) {
		String value = getStringProperty(property);
		if (value != null) try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {}
		return -1;
	}
	
	public INavigationState getNavigationState() {
		return getNavigationState(getStringProperty(NAVIGATION_STATE_PROPERTY));
	}
	
	public void setProperty(ISessionProperty property, Object value) {
		pendingProperties.put(property.getPropertyName(), value.toString());
	}
	
	public void setNavigationState(INavigationState navigationStateName) {
		setProperty(NAVIGATION_STATE_PROPERTY, navigationStateName.getNavigationStateName());
	}

	public UserDto getUser() {
		return user;
	}
	
	public String[][] getNewProperties() {
		String[][] newProperties = new String[pendingProperties.size()][2];
		int newPropertiesIndex = 0;
		for (String propertyName : pendingProperties.keySet()) {
			if (!storedProperties.containsKey(propertyName)) {
				String propertyValue = pendingProperties.get(propertyName);
				if ((propertyValue != null) && (!"".equals(propertyValue))) {
					newProperties[newPropertiesIndex][0] = propertyName;
					newProperties[newPropertiesIndex][1] = propertyValue;
					newPropertiesIndex++;
				}
			}
		}
		return Arrays.copyOf(newProperties, newPropertiesIndex);
	}
	
	public String[][] getUpdatedProperties() {
		String[][] updatedProperties = new String[storedProperties.size()][2];
		int updatedPropertiesIndex = 0;
		for (String propertyName : storedProperties.keySet()) {
			if (pendingProperties.containsKey(propertyName)) {
				String propertyValue = pendingProperties.get(propertyName);
				if ((propertyValue != null) && (!"".equals(propertyValue))) {
					updatedProperties[updatedPropertiesIndex][0] = propertyName;
					updatedProperties[updatedPropertiesIndex][1] = propertyValue;
					updatedPropertiesIndex++;
				}
			}
		}
		return Arrays.copyOf(updatedProperties, updatedPropertiesIndex);
	}

	public String[] getDeletedProperties() {
		String[] deletedProperties = new String[pendingProperties.size()];
		int deletedPropertiesIndex = 0;
		for (String propertyName : pendingProperties.keySet()) {
			String propertyValue = pendingProperties.get(propertyName);
			if ((propertyValue == null) || ("".equals(propertyValue))) {
				deletedProperties[deletedPropertiesIndex] = propertyName;
				deletedPropertiesIndex++;
			}
		}
		return Arrays.copyOf(deletedProperties, deletedPropertiesIndex);
	}

	/** @see SessionDto#SessionDto(String[][], String[][], String[]) */
	public SessionDto getChangedSessionDto() {
		String[][] newProperties     = getNewProperties();
		String[][] updatedProperties = getUpdatedProperties();
		String[]   deletedProperties = getDeletedProperties();
		return new SessionDto(user, newProperties, updatedProperties, deletedProperties);
	}

}