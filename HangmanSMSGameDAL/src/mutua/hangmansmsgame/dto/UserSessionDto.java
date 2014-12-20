package mutua.hangmansmsgame.dto;

/** <pre>
 * UserSessionDto.java
 * ===================
 * (created by luiz, Dec 19, 2014)
 *
 * Represents the (somewhat) persistent user session information
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class UserSessionDto {

	public enum EUserSessionParameters {
		NEXT_LISTED_PLAYER_ID, HANGMAN_GUESSING_WORD, HANGMAN_GUESSING_WRONG_ATTEMPTS_LEFT, HANGMAN_GAME_STATE 
	}
	
	private final String phone;
	private final String navigationState;
	
	public UserSessionDto(String phone, String navigationState) {
		this.phone = phone;
		this.navigationState = navigationState;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public String getNavigationState() {
		return navigationState;
	}

}
