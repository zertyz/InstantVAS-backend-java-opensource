package mutua.hangmansmsgame.dal.dto;

import mutua.hangmansmsgame.dal.dto.MatchDto.EMatchStatus;

/** <pre>
 * MatchDto.java
 * =============
 * (created by luiz, Jan 3, 2015)
 *
 * Represents a Hangman Match
 *
 * @see IMatchDB
 * @version $Id$
 * @author luiz
 */

public class MatchDto {
	
	public enum EMatchStatus {
		ACTIVE,
		CLOSED_WORD_GUESSED,
		CLOSED_ATTEMPTS_EXCEEDED,
		CLOSED_A_PLAYER_GAVE_UP,
		CLOSED_TIMEDOUT,
	}

	private final String       wordProvidingPlayerPhone;
	private final String       wordGuessingPlayerPhone;
	private final String       serializedGame;
	private final long         matchStartMillis;
	private final EMatchStatus status;
	
	
	public MatchDto(String wordProvidingPlayerPhone, String wordGuessingPlayerPhone, String serializedGame, long matchStartMillis, EMatchStatus status) {
		this.wordProvidingPlayerPhone = wordProvidingPlayerPhone;
		this.wordGuessingPlayerPhone  = wordGuessingPlayerPhone;
		this.serializedGame           = serializedGame;
		this.matchStartMillis         = matchStartMillis;
		this.status                   = status;
	}
	
	public String getWordProvidingPlayerPhone() {
		return wordProvidingPlayerPhone;
	}
	
	public String getWordGuessingPlayerPhone() {
		return wordGuessingPlayerPhone;
	}
	
	public EMatchStatus getStatus() {
		return status;
	}

	public MatchDto getNewMatch(EMatchStatus status) {
		return new MatchDto(wordProvidingPlayerPhone, wordGuessingPlayerPhone, serializedGame, matchStartMillis, status);
	}
	
}
