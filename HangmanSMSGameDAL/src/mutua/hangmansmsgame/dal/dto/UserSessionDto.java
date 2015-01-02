package mutua.hangmansmsgame.dal.dto;

import java.util.Arrays;

import mutua.hangmansmsgame.dal.dto.UserSessionDto.EUserSessionParameters;


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

		// listing users session info
		NEXT_LISTED_PLAYER_ID,
		
		// playing session info
		HANGMAN_GUESSING_WORD,
		HANGMAN_GUESSING_WRONG_ATTEMPTS_LEFT,
		HANGMAN_GAME_STATE,
		
		// invitation to play session info
		OPPONENT_PHONE_NUMBER,
	}
	
	private final String phone;
	private final String navigationState;
	private final String[] parameterValues;

	public UserSessionDto(String phone, String navigationState) {
		this(phone, navigationState, new String[EUserSessionParameters.values().length]);
	}
	
	public UserSessionDto(String phone, String navigationState, String[] parameterValues) {
		this.phone = phone;
		this.navigationState = navigationState;
		this.parameterValues = parameterValues;
	}
	
	public UserSessionDto getNewUserSession(String newNavigationState) {
		return new UserSessionDto(phone, newNavigationState);
	}

	public UserSessionDto getNewUserSession(String newNavigationState, EUserSessionParameters parameter, String parameterValue) {
		String[] newParameterValues = Arrays.copyOf(parameterValues, parameterValues.length);
		newParameterValues[parameter.ordinal()] = parameterValue;
		return new UserSessionDto(phone, newNavigationState, newParameterValues);
	}

	public String getPhone() {
		return phone;
	}
	
	public String getNavigationState() {
		return navigationState;
	}

	public String getParameterValue(EUserSessionParameters parameter) {
		return parameterValues[parameter.ordinal()];
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("UserSessionDto [phone='").append(phone).append("', navigationState='");
		sb.append(navigationState).append("', parameterValues={");
		for (int i=1; i<parameterValues.length; i++) {
			sb.append(EUserSessionParameters.values()[i]).append('=');
			sb.append("'").append(parameterValues[i]).append("'");
			sb.append(i<parameterValues.length-1 ? "," : "");
		}
		sb.append("}]");
		return sb.toString();
	}
	
	

}
