package mutua.smsappmodule.dto;

/** <pre>
 * ProfileDto.java
 * ===============
 * (created by luiz, Aug 3, 2015)
 *
 * Represents a retrieved/committable user profile information
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class ProfileDto {
	
	private UserDto user;
	private String  nickname;
	
	/** Creates a user profile record */
	public ProfileDto(UserDto user, String nickname) {
		this.user     = user;
		this.nickname = nickname;
	}
	
	public UserDto getUser() {
		return user;
	}
	
	public String getNickname() {
		return nickname;
	}

}
