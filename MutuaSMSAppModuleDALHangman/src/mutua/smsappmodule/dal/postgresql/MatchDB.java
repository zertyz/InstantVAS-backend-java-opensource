package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IMatchDB;
import mutua.smsappmodule.dto.MatchDto;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;
import mutua.smsappmodule.dto.UserDto;
import adapters.JDBCAdapter;
import adapters.dto.PreparedProcedureInvocationDto;

/** <pre>
 * MatchDB.java
 * ============
 * (created by luiz, Jan 27, 2015)
 *
 * Implements the POSTGRESQL version of {@link IMatchDB}
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
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("ResetTable");
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public int storeNewMatch(MatchDto match) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("InsertMatch");
		procedure.addParameter("WORD_PROVIDING_PLAYER_USER_ID", match.getWordProvidingPlayer().getUserId());
		procedure.addParameter("WORD_GUESSING_PLAYER_USER_ID",  match.getWordGuessingPlayer().getUserId());
		procedure.addParameter("SERIALIZED_GAME",               match.getSerializedGame());
		procedure.addParameter("MATCH_START_MILLIS",            match.getMatchStartMillis());
		procedure.addParameter("STATUS",                        match.getStatus().name());
		int matchId = (Integer)dba.invokeScalarProcedure(procedure);
		match.setMatchId(matchId);
		return matchId;
	}

	@Override
	public MatchDto retrieveMatch(int matchId) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectMatchById");
		procedure.addParameter("MATCH_ID", matchId);
		Object[] fields = dba.invokeRowProcedure(procedure);
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
	public void updateMatchStatus(MatchDto match, EMatchStatus status) throws SQLException {
		int matchId = match.getMatchId();
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("UpdateMatchStatusById");
		procedure.addParameter("MATCH_ID", matchId);
		procedure.addParameter("STATUS",   status.name());
		dba.invokeUpdateProcedure(procedure);
	}

}
