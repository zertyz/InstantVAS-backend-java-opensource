package mutua.smsappmodule.dto;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;

/** <pre>
 * UserDto.java
 * ============
 * (created by luiz, Jul 15, 2015)
 *
 * Represents a user, be it already registered (has an 'userId') on the database or
 * new ('userId' equals to -1)
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class UserDto {
	
	private       int    userId;
	private final String phoneNumber;

	
	/** Constructs an 'UserDto' for an already registered user */
	public UserDto(int userId, String phoneNumber) {
		this.userId      = userId;
		this.phoneNumber = phoneNumber;
	}
	
	/** Constructs an 'UserDto' for a new user */
	public UserDto(String phoneNumber) {
		this.userId      = -1;
		this.phoneNumber = phoneNumber;
	}
	
	/** To be used when a new user is inserted on the database */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

}