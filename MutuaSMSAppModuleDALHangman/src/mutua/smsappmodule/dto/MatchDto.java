package mutua.smsappmodule.dto;


/** <pre>
 * MatchDto.java
 * =============
 * (created by luiz, Jan 3, 2015)
 *
 * Represents a retrieved/committable Hangman Match, be it already registered (has a 'matchId') on the database or
 * new ('matchId' equals to -1)
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

	private       int          matchId;
	private final UserDto      wordProvidingPlayer;
	private final UserDto      wordGuessingPlayer;
	private final String       serializedGame;
	private final long         matchStartMillis;
	private final EMatchStatus status;
	
	
	public MatchDto(UserDto wordProvidingPlayer, UserDto wordGuessingPlayer, String serializedGame, long matchStartMillis, EMatchStatus status) {
		this.wordProvidingPlayer = wordProvidingPlayer;
		this.wordGuessingPlayer  = wordGuessingPlayer;
		this.serializedGame      = serializedGame;
		this.matchStartMillis    = matchStartMillis;
		this.status              = status;
		this.matchId             = -1;
	}
	
	public MatchDto(int matchId, UserDto wordProvidingPlayer, UserDto wordGuessingPlayer, String serializedGame, long matchStartMillis, EMatchStatus status) {
		this(wordProvidingPlayer, wordGuessingPlayer, serializedGame, matchStartMillis, status);
		this.matchId = matchId;
	}
	
	/** To be used when a new match is inserted on the database */
	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}
	
	public int getMatchId() {
		return matchId;
	}
	
	public UserDto getWordProvidingPlayer() {
		return wordProvidingPlayer;
	}
	
	public UserDto getWordGuessingPlayer() {
		return wordGuessingPlayer;
	}
	
	public String getSerializedGame() {
		return serializedGame;
	}
	
	public long getMatchStartMillis() {
		return matchStartMillis;
	}

	public EMatchStatus getStatus() {
		return status;
	}

	@Override
	public boolean equals(Object obj) {
		MatchDto anotherMatch = (MatchDto)obj;
		return (matchId == anotherMatch.matchId) &&
		       wordProvidingPlayer.equals(anotherMatch.wordProvidingPlayer) &&
		       wordGuessingPlayer.equals(anotherMatch.wordGuessingPlayer) &&
		       serializedGame.equals(anotherMatch.serializedGame) &&
		       (matchStartMillis == anotherMatch.matchStartMillis) &&
		       (status == anotherMatch.status);
	}

}
