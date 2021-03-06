package mutua.hangmansmsgame.dal.postgresql;
import java.sql.SQLException;

import mutua.icc.configuration.annotations.ConfigurableElement;
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
	
	
	// configuration
	////////////////
	
	@ConfigurableElement("The application's instrumentation instance to be used to log PostgreSQL database events")
	public static Instrumentation<?, ?> log;

	@ConfigurableElement("Hostname (or IP) of the PostgreSQL server")
	public static String HOSTNAME;
	@ConfigurableElement("Connection port for the PostgreSQL server")
	public static int    PORT;
	@ConfigurableElement("The PostgreSQL database with the application's data scope")
	public static String DATABASE;
	@ConfigurableElement("The PostgreSQL user name to access 'DATABASE' -- note: administrative rights, such as the creation of tables, might be necessary")
	public static String USER;
	@ConfigurableElement("The PostgreSQL plain text password for 'USER'")
	public static String PASSWORD;

	
	private HangmanSMSGamePostgreSQLAdapters(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
		super(log, preparedProceduresDefinitions);
	}

	@Override
	protected String[] getCredentials() {
		return new String[] {HOSTNAME, Integer.toString(PORT), DATABASE, USER, PASSWORD};
	}

	@Override
	protected String[][] getTableDefinitions() {
		if (!ALLOW_DATABASE_ADMINISTRATION) {
			return null;
		}
		return new String[][] {
			{"Users", "CREATE TABLE Users(" +
			          "userId      SERIAL   NOT NULL PRIMARY KEY, " +
			          "phone       VARCHAR(15) NOT NULL UNIQUE,   " +
			          "nick        VARCHAR(15) NOT NULL,          " +
			          "subscribed  BOOLEAN,                       " +
			          "nextBotWord INTEGER     DEFAULT -1,        " +
			          "ts          TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
			          "CREATE UNIQUE INDEX unique_caseinsensitive_nick ON Users (lower(nick));"},
			{"Match", "CREATE TABLE Match(" +
			          "matchId                  SERIAL      NOT NULL PRIMARY KEY, " +
			          "wordProvidingPlayerPhone VARCHAR(15) NOT NULL, " +
			          "wordGuessingPlayerPhone  VARCHAR(15) NOT NULL, " +
			          "serializedGame           VARCHAR(64) NOT NULL, " +
			          "matchStartMillis         BIGINT      NOT NULL, " +
			          "status                   VARCHAR(32) NOT NULL, " +
			          "ts                       TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"},
		};
	}
	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getUserDBAdapter() throws SQLException {
		return new HangmanSMSGamePostgreSQLAdapters(log, new String[][] {
			{"ResetTable",                  "DELETE FROM Users"},
			{"SelectPhoneByNick",           "SELECT phone FROM Users WHERE LOWER(nick)=LOWER(${NICK})"},
			{"SelectCorrectlyCasedNick",    "SELECT nick  FROM Users WHERE LOWER(nick)=LOWER(${NICK})"},
			{"SelectNickByPhone",           "SELECT nick  FROM Users WHERE phone=${PHONE}"},
			{"InsertUser",                  "INSERT INTO Users(phone, nick) VALUES(${PHONE}, ${NICK})"},
			//{"UpsertUser",          		"INSERT INTO Users(phone, nick) VALUES(${PHONE}, ${NICK}) ON CONFLICT (phone) UPDATE SET nick=${NICK} WHERE phone=${PHONE}"},	// will only be available on postgresql 9.5 -- http://dba.stackexchange.com/questions/13468/idiomatic-way-to-implement-upsert-in-postgresql
			//{"UpsertUser",          		"WITH update_outcome AS (UPDATE Users SET nick=${NICK} WHERE phone=${PHONE} RETURNING 'update'::text AS action, phone), insert_outcome AS (INSERT INTO Users(phone, nick) SELECT ${PHONE} AS phone, ${NICK} AS nick WHERE NOT EXISTS (SELECT phone FROM update_outcome LIMIT 1) RETURNING 'insert'::text AS action, phone) SELECT * FROM update_outcome UNION ALL SELECT * FROM insert_outcome"},	// works on any postgresql. Same link above
			{"SelectAutonumberedNicks",     "SELECT nick from Users WHERE nick ~ ${NICK}"},
			{"UpdateNick",                  "UPDATE Users SET nick=${NICK} WHERE phone=${PHONE}"},
			{"UpdateSubscriptionByPhone",   "UPDATE Users SET subscribed=${SUBSCRIBED} WHERE phone=${PHONE}"},
			{"SelectSubscriptionByPhone",   "SELECT subscribed FROM Users WHERE phone=${PHONE}"},
			{"IncrementNextBotWordByPhone", "UPDATE Users SET nextBotWord=nextBotWord+1 WHERE phone=${PHONE} RETURNING nextBotWord"},
		});
	}

	public static JDBCAdapter getMatchDBAdapter() throws SQLException {
		return new HangmanSMSGamePostgreSQLAdapters(log, new String[][] {
			{"ResetTable",        "DELETE FROM Match"},
			{"InsertMatch",       "INSERT INTO Match(wordProvidingPlayerPhone, wordGuessingPlayerPhone, serializedGame, matchStartMillis, status) " +
			                      "VALUES(${WORD_PROVIDING_PLAYER_PHONE}, ${WORD_GUESSING_PLAYER_PHONE}, ${SERIALIZED_GAME}, ${MATCH_START_MILLIS}, ${STATUS}) " +
			                      "RETURNING matchId"},
			{"SelectMatchById",   "SELECT wordProvidingPlayerPhone, wordGuessingPlayerPhone, serializedGame, matchStartMillis, status FROM Match " + 
			                      "WHERE matchId=${MATCH_ID}"},
			{"UpdateStatusById",  "UPDATE Match SET status=${STATUS} WHERE matchId=${MATCH_ID}"},
		});
	}
}
