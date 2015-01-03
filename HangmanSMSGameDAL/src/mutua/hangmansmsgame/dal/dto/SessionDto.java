package mutua.hangmansmsgame.dal.dto;

import java.util.Arrays;

import mutua.hangmansmsgame.dal.dto.SessionDto.ESessionParameters;


/** <pre>
 * SessionDto.java
 * ===============
 * (created by luiz, Dec 19, 2014)
 *
 * Represents the (somewhat) persistent user session information
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SessionDto {

	public enum ESessionParameters {

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

	public SessionDto(String phone, String navigationState) {
		this(phone, navigationState, new String[ESessionParameters.values().length]);
	}
	
	public SessionDto(String phone, String navigationState, String[] parameterValues) {
		this.phone = phone;
		this.navigationState = navigationState;
		this.parameterValues = parameterValues;
	}
	
	public SessionDto getNewSessionDto(String newNavigationState) {
		return new SessionDto(phone, newNavigationState);
	}

	public SessionDto getNewSessionDto(String newNavigationState, ESessionParameters parameter, String parameterValue) {
		String[] newParameterValues = Arrays.copyOf(parameterValues, parameterValues.length);
		newParameterValues[parameter.ordinal()] = parameterValue;
		return new SessionDto(phone, newNavigationState, newParameterValues);
	}

	public String getPhone() {
		return phone;
	}
	
	public String getNavigationState() {
		return navigationState;
	}

	public String getParameterValue(ESessionParameters parameter) {
		return parameterValues[parameter.ordinal()];
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SessionDto [phone='").append(phone).append("', navigationState='");
		sb.append(navigationState).append("', parameterValues={");
		for (int i=1; i<parameterValues.length; i++) {
			sb.append(ESessionParameters.values()[i]).append('=');
			sb.append("'").append(parameterValues[i]).append("'");
			sb.append(i<parameterValues.length-1 ? "," : "");
		}
		sb.append("}]");
		return sb.toString();
	}
	
	

}
