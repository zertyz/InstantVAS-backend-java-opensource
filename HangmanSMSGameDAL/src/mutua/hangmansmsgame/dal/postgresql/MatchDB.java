package mutua.hangmansmsgame.dal.postgresql;

import java.sql.SQLException;

import adapters.JDBCAdapter;
import adapters.dto.PreparedProcedureInvocationDto;
import mutua.hangmansmsgame.dal.IMatchDB;
import mutua.hangmansmsgame.dal.IMatchTraversalCallback;
import mutua.hangmansmsgame.dal.dto.MatchDto;
import mutua.hangmansmsgame.dal.dto.MatchDto.EMatchStatus;

/** <pre>
 * MatchDB.java
 * ============
 * (created by luiz, Jan 27, 2015)
 *
 * Implements the persistent, PostgreSQL version of 'IMatchDB'
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class MatchDB implements IMatchDB {
	
	private JDBCAdapter dba;
	
	public MatchDB() throws SQLException {
		dba = HangmanSMSGamePostgreSQLAdapters.getMatchDBAdapter();
	}

	@Override
	public void reset() throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("ResetTable");
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public int storeNewMatch(MatchDto match) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("InsertMatch");
		procedure.addParameter("WORD_PROVIDING_PLAYER_PHONE", match.getWordProvidingPlayerPhone());
		procedure.addParameter("WORD_GUESSING_PLAYER_PHONE",  match.getWordGuessingPlayerPhone());
		procedure.addParameter("SERIALIZED_GAME",             match.getSerializedGame());
		procedure.addParameter("MATCH_START_MILLIS",          match.getMatchStartMillis());
		procedure.addParameter("STATUS",                      match.getStatus().name());
		return (Integer)dba.invokeScalarProcedure(procedure);
	}

	@Override
	public MatchDto retrieveMatch(int matchId) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectMatchById");
		procedure.addParameter("MATCH_ID", matchId);
		Object[] fields = dba.invokeRowProcedure(procedure);
		if (fields == null) {
			return null;
		}
		MatchDto match = new MatchDto(
			(String)fields[0],
			(String)fields[1],
			(String)fields[2],
			(Long)fields[3],
			EMatchStatus.valueOf((String)fields[4]));
		return match;
	}

	@Override
	public void updateMatchStatus(int matchId, EMatchStatus status) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("UpdateStatusById");
		procedure.addParameter("MATCH_ID", matchId);
		procedure.addParameter("STATUS",   status.name());
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public void traverseThroughActiveMatches(IMatchTraversalCallback callback) {
	}

}
