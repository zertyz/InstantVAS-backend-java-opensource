package mutua.hangmansmsgame.dal.postgresql;
import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;


/** <pre>
 * HangmanSMSGamePostgreSQLAdapters.java
 * =====================================
 * (created by luiz, Jan 26, 2015)
 *
 * Provides 'PostgreSQLAdapter's to manipulate HangmanSMSGame databases
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HangmanSMSGamePostgreSQLAdapters extends PostgreSQLAdapter {
	
	public static Instrumentation<?, ?> log;
	
	public static String HOSTNAME;
	public static int    PORT;
	public static String DATABASE;
	public static String USER;
	public static String PASSWORD;

	public HangmanSMSGamePostgreSQLAdapters(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
		super(log, preparedProceduresDefinitions);
	}

	@Override
	protected String[] getCredentials() {
		return new String[] {HOSTNAME, Integer.toString(PORT), DATABASE, USER, PASSWORD};
	}

	@Override
	protected String[][] getTableDefinitions() {
		return new String[][] {
			{"Users", "CREATE TABLE Users(" +
			          "userId SERIAL   NOT NULL PRIMARY KEY, " +
			          "phone  VARCHAR(15) NOT NULL UNIQUE, " +
			          "nick   VARCHAR(15) NOT NULL UNIQUE, " +
			          "ts     timestamp DEFAULT current_timestamp)"},
		};
	}
	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getUserDBAdapter() throws SQLException {
		return new HangmanSMSGamePostgreSQLAdapters(log, new String[][] {
			{"ResetTable",               "DELETE FROM Users"},
			{"SelectPhoneByNick",        "SELECT phone FROM Users WHERE LOWER(nick)=LOWER(${NICK})"},
			{"SelectCorrectlyCasedNick", "SELECT nick  FROM Users WHERE LOWER(nick)=LOWER(${NICK})"},
			{"SelectNickByPhone",        "SELECT nick  FROM Users WHERE phone=${PHONE}"},
			{"InsertUser",               "INSERT INTO Users(phone, nick) VALUES(${PHONE}, ${NICK})"},
			{"UpdateUser",               "UPDATE Users SET nick=${NICK} WHERE phone=${PHONE}"},
		});
	}

}
