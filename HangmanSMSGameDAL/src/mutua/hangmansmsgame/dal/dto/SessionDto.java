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
 * @see ISessionDB
 * @version $Id$
 * @author luiz
 */

public class SessionDto {

	public enum ESessionParameters {

		// playing states info
		HANGMAN_SERIALIZED_GAME_STATE,
		MATCH_BOT_NAME,
		
		// invitation to play info
		OPPONENT_PHONE_NUMBER,
		MATCH_ID,
		
		// listing users info
		PRESENTED_USERS,
	}
	
	private final String phone;
	private final String navigationState;
	private final String[] parameterValues;
	private long timestamp;

	public SessionDto(String phone, String navigationState) {
		this(phone, navigationState, new String[ESessionParameters.values().length]);
	}
	
	private SessionDto(String phone, String navigationState, String[] parameterValues) {
		this.phone = phone;
		this.navigationState = navigationState;
		this.parameterValues = parameterValues;
		this.timestamp = System.currentTimeMillis();
	}
	
	public SessionDto(String phone, String navigationState, ESessionParameters parameter1, String parameter1Value) {
		this(phone, navigationState);
		parameterValues[parameter1.ordinal()] = parameter1Value;
	}
	
	public SessionDto(String phone, String navigationState,
	                  ESessionParameters parameter1, String parameter1Value,
	                  ESessionParameters parameter2, String parameter2Value) {
		this(phone, navigationState, parameter1, parameter1Value);
		parameterValues[parameter2.ordinal()] = parameter2Value;
	}
	
	public SessionDto getNewSessionDto(String newNavigationState) {
		return new SessionDto(phone, newNavigationState);
	}

	public SessionDto getNewSessionDto(String newNavigationState,
	                                   ESessionParameters parameter1, String parameter1Value,
	                                   ESessionParameters parameter2, String parameter2Value) {
		String[] newParameterValues = Arrays.copyOf(parameterValues, parameterValues.length);
		newParameterValues[parameter1.ordinal()] = parameter1Value;
		newParameterValues[parameter2.ordinal()] = parameter2Value;
		return new SessionDto(phone, newNavigationState, newParameterValues);
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
	
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("phone='").append(phone).append("', navigationState='").
		append(navigationState).append("', timestamp=").
		append(timestamp).append(", parameterValues={");
		for (int i=1; i<parameterValues.length; i++) {
			sb.append(ESessionParameters.values()[i]).append('=');
			sb.append("'").append(parameterValues[i]).append("'");
			sb.append(i<parameterValues.length-1 ? "," : "");
		}
		return sb.append("}").toString();
	}
	
	

}
