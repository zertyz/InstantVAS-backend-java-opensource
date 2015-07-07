package mutua.hangmansmsgame.dal.postgresql;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import mutua.hangmansmsgame.dal.IUserDB;
import adapters.JDBCAdapter;
import adapters.dto.PreparedProcedureInvocationDto;

/** <pre>
 * UserDB.java
 * ===========
 * (created by luiz, Jan 26, 2015)
 *
 * Implements the persistent, PostgreSQL version of 'IUserDB'
 *
 * @see IUserDB
 * @version $Id$
 * @author luiz
 */

public class UserDB extends IUserDB {

	private JDBCAdapter dba;
	
	public UserDB() throws SQLException {
		dba = HangmanSMSGamePostgreSQLAdapters.getUserDBAdapter();
	}
	
	@Override
	public void reset() throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("ResetTable");
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public String getCorrectlyCasedNickname(String nickname) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectCorrectlyCasedNick");
		procedure.addParameter("NICK", nickname);
		return (String)dba.invokeScalarProcedure(procedure);
	}

	@Override
	public String getUserNickname(String phoneNumber) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectNickByPhone");
		procedure.addParameter("PHONE", phoneNumber);
		return (String)dba.invokeScalarProcedure(procedure);
	}

	@Override
	public String getUserPhoneNumber(String nickname) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectPhoneByNick");
		procedure.addParameter("NICK", nickname);
		return (String)dba.invokeScalarProcedure(procedure);
	}

	@Override
	protected synchronized boolean checkAvailabilityAndRecordNickname(String phoneNumber, String nickname) throws SQLException {
		String nicksPhone = getUserPhoneNumber(nickname);
		if (nicksPhone != null) {
			if (!phoneNumber.equals(nicksPhone)) {
				return false;
			}
		}
		PreparedProcedureInvocationDto procedure;
		boolean useUpSert = false;
		if (useUpSert) {
			procedure = new PreparedProcedureInvocationDto("UpsertUser");
		} else {
			if (isUserOnRecord(phoneNumber)) {
				procedure = new PreparedProcedureInvocationDto("UpdateNick");
			} else {
				procedure = new PreparedProcedureInvocationDto("InsertUser");
			}
		}
		procedure.addParameter("PHONE", phoneNumber);
		procedure.addParameter("NICK",  nickname);
		if (useUpSert) {
			dba.invokeVirtualTableProcedure(procedure).close();
		} else {
			dba.invokeUpdateProcedure(procedure);
		}
		return true;
	}
	
	@Override
	protected Map<String, Boolean> getNicknameAutonumberedSequenceElements(String nickname) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectAutonumberedNicks");
		procedure.addParameter("NICK", "^"+nickname+"\\d+$");	// pattern for the postgresql ~ operator
		Object[][] elements = dba.invokeArrayProcedure(procedure);
		HashMap<String, Boolean> nicks = new HashMap<String, Boolean>(elements.length+1, 1);
		for (int i=0; i < elements.length; i++) {
			String nick = (String)elements[i][0];
			nicks.put(nick, true);
		}
		return nicks;
	}

	@Override
	public boolean isUserOnRecord(String phoneNumber) throws SQLException {
		return getUserNickname(phoneNumber) != null;
	}

	@Override
	public boolean isUserSubscribed(String phoneNumber) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectSubscriptionByPhone");
		procedure.addParameter("PHONE", phoneNumber);
		Boolean rawResult = (Boolean)dba.invokeScalarProcedure(procedure);
		if (rawResult == null) {
			return false;
		} else {
			return (boolean)rawResult;
		}
	}

	@Override
	public void setSubscribed(String phoneNumber, boolean subscribed) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("UpdateSubscriptionByPhone");
		procedure.addParameter("SUBSCRIBED", subscribed);
		procedure.addParameter("PHONE", phoneNumber);
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public int getAndIncrementNextBotWord(String phoneNumber) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("IncrementNextBotWordByPhone");
		procedure.addParameter("PHONE", phoneNumber);
		int nextBotWord = (Integer)dba.invokeScalarProcedure(procedure);
		return nextBotWord;
	}

}