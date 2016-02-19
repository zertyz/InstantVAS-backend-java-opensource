package mutua.smsappmodule.smslogic.sessions;

import java.util.Arrays;
import java.util.HashMap;

import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsin.dto.IncomingSMSDto;

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

public abstract class SessionModel {
	
	private final UserDto        user;
	private final IncomingSMSDto MO;
	
	private final HashMap<String, String> storedProperties;
	private final HashMap<String, String> pendingProperties = new HashMap<String, String>(16);
	
	public static final ISessionProperty NAVIGATION_STATE_PROPERTY = new ISessionProperty() {
		@Override
		public String getPropertyName() {
			return "NavState";
		}
	};

	/** Should return the {@link INavigationState} associated with the provided 'navigationStateName' */
	public abstract INavigationState getNavigationStateFromStateName(String navigationStateName);

	/** Creates a session for 'user' where 'storedProperties' := {{(ISessionProperty)prop, propVal}, ...} */
	public SessionModel(SessionDto sessionDto, IncomingSMSDto MO) {
		String[][] sessionDtoStoredProperties = sessionDto.getStoredProperties();
		this.user                             = sessionDto.getUser();
		this.MO                               = MO;
		this.storedProperties                 = new HashMap<String, String>(sessionDtoStoredProperties.length+1, 1);
		for (String[] propertyData : sessionDtoStoredProperties) {
			String propertyName  = propertyData[0];
			String propertyValue = propertyData[1];
			this.storedProperties.put(propertyName, propertyValue);
		}
	}
	
	/** Creates a session for 'user' with the 'storedProperties' */
	public SessionModel(UserDto user, IncomingSMSDto MO, HashMap<String, String> storedProperties) {
		this.user              = user;
		this.MO                = MO;
		this.storedProperties  = storedProperties;
	}
	
	/** Creates an empty session (usually, for a new user) */
	public SessionModel(UserDto user, IncomingSMSDto MO) {
		this.user              = user;
		this.MO                = MO;
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
	
	public String getNavigationStateName() {
		return getStringProperty(NAVIGATION_STATE_PROPERTY);
	}
	
	public INavigationState getNavigationState() {
		String navigationStateName = getStringProperty(NAVIGATION_STATE_PROPERTY);
		if (navigationStateName == null) {
			return null;
		}
		return getNavigationStateFromStateName(navigationStateName);
	}
	
	public void setProperty(ISessionProperty property, Object value) {
		pendingProperties.put(property.getPropertyName(), value.toString());
	}
	
	public void deleteProperty(ISessionProperty property) {
		setProperty(property, "");
	}
	
	public void setNavigationState(String navigationStateName) {
		setProperty(NAVIGATION_STATE_PROPERTY, navigationStateName);
	}

	public UserDto getUser() {
		return user;
	}
	
	public IncomingSMSDto getMO() {
		return MO;
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