package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IMatchDB;
import mutua.smsappmodule.dto.MatchDto;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;
import mutua.smsappmodule.dto.UserDto;
import adapters.JDBCAdapter;

import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterHangman.MatchesDBStatements.*;
import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterHangman.Parameters.*;

/** <pre>
 * MatchDB.java
 * ============
 * (created by luiz, Jan 27, 2015)
 *
 * Implements the PostgreSQL version of {@link IMatchDB}
 *
 * @see IMatchDB
 * @version $Id$
 * @author luiz
 */

public class MatchDB implements IMatchDB {
	
	private JDBCAdapter dba;

	
	public MatchDB() throws SQLException {
		dba = SMSAppModulePostgreSQLAdapterHangman.getMatchDBAdapter();
	}
	
	@Override
	public void reset() throws SQLException {
		dba.invokeUpdateProcedure(ResetTable);
	}

	@Override
	public int storeNewMatch(MatchDto match) throws SQLException {
		int matchId = (Integer)dba.invokeScalarProcedure(InsertMatch,
			WORD_PROVIDING_PLAYER_USER_ID, match.getWordProvidingPlayer().getUserId(),
			WORD_GUESSING_PLAYER_USER_ID,  match.getWordGuessingPlayer().getUserId(),
			SERIALIZED_GAME,               match.getSerializedGame(),
			MATCH_START_MILLIS,            match.getMatchStartMillis(),
			STATUS,                        match.getStatus().name());
		match.setMatchId(matchId);
		return matchId;
	}

	@Override
	public MatchDto retrieveMatch(int matchId) throws SQLException {
		Object[] fields = dba.invokeRowProcedure(SelectMatchById, MATCH_ID, matchId);
		if (fields == null) {
			return null;
		}
		int          wordProvidingPlayerUserId = (Integer)fields[0];
		String       wordProvidingPlayerPhone  = (String)fields[1];
		int          wordGuessingPlayerUserId  = (Integer)fields[2];
		String       wordGuessingPlayerPhone   = (String)fields[3];
		String       serializedGame            = (String)fields[4];
		long         matchStartMillis          = (Long)fields[5];
		EMatchStatus status                    = EMatchStatus.valueOf((String)fields[6]);

		MatchDto match = new MatchDto(
			matchId,
			new UserDto(wordProvidingPlayerUserId, wordProvidingPlayerPhone),
			new UserDto(wordGuessingPlayerUserId, wordGuessingPlayerPhone),
			serializedGame,
			matchStartMillis,
			status);
		return match;
	}

	@Override
	public void updateMatchStatus(MatchDto match, EMatchStatus status, String serializedGame) throws SQLException {
		int matchId = match.getMatchId();
		dba.invokeUpdateProcedure(UpdateMatchStatusById,
			MATCH_ID,        matchId,
			STATUS,          status.name(),
			SERIALIZED_GAME, serializedGame);
	}

}
