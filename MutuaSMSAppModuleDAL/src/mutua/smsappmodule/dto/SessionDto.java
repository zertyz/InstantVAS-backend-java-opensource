package mutua.smsappmodule.dto;

/** <pre>
 * SessionDto.java
 * ===============
 * (created by luiz, Jul 17, 2015)
 *
 * Represents a retrieved/committable session information, obtained from 'SessionModel'.
 * 
 * Retrieved SessionDto's has a non-null 'storedProperties'.
 *
 * @see SessionModel
 * @version $Id$
 * @author luiz
 */

public class SessionDto {
	
	private UserDto    user;
	private String[][] storedProperties;
	private String[][] newProperties;
	private String[][] updatedProperties;
	private String[]   deletedProperties;
	

	/** Creates a map (a copy of the database contents) 'SessionDto' for 'user' where 'storedProperties' := {{propName, propVal}, ...} */
	public SessionDto(UserDto user, String[][] storedProperties) {
		this.user              = user;
		this.storedProperties  = storedProperties;
		this.newProperties     = new String[0][0];
		this.updatedProperties = new String[0][0];
		this.deletedProperties = new String[0];
	}
	
	/** Creates a changed (relative to the database contents) 'SessionDto', consisting only of the information to be inserted, updated or deleted.
	 * @param user
	 * @param newProperties := {{"prop1", "value1"}, ...}
	 * @param updatedProperties := ...
	 * @param deletedProperties := {"prop1", ...} */
	public SessionDto(UserDto user, String[][] newProperties, String[][] updatedProperties, String[] deletedProperties) {
		this.user              = user;
		this.storedProperties  = new String[0][];
		this.newProperties     = (newProperties     != null) ? newProperties     : new String[0][0];
		this.updatedProperties = (updatedProperties != null) ? updatedProperties : new String[0][0];
		this.deletedProperties = (deletedProperties != null) ? deletedProperties : new String[0];
	}
	
	public UserDto getUser() {
		return user;
	}

	/** Returns the map of the database stored session properties */
	public String[][] getStoredProperties() {
		return storedProperties;
	}

	/** Returns the session properties that should be inserted on the database */
	public String[][] getNewProperties() {
		return newProperties;
	}

	/** Returns the session properties that should be updated on the database */
	public String[][] getUpdatedProperties() {
		return updatedProperties;
	}

	/** Returns the session properties that should be deleted on the database */
	public String[] getDeletedProperties() {
		return deletedProperties;
	}
}
