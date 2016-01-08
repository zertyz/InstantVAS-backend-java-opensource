package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * SMSAppModulePostgreSQLAdapterHangman.java
 * =========================================
 * (created by luiz, Aug 13, 2015)
 *
 * Provides {@link PostgreSQLAdapter}s to manipulate the "Hangman" module database & tables
 *
 * @see PostgreSQLAdapter
 * @version $Id$
 * @author luiz
 */

public class SMSAppModulePostgreSQLAdapterHangman extends PostgreSQLAdapter {

	
	// the version information for database tables present on this class, to be stored on the 'Meta' table. Useful for future data conversions.
	private static String modelVersionForMetaTable = "2015.08.13";
	
	// configuration
	////////////////
	
	/** The application's instrumentation instance to be used to log PostgreSQL database events */
	private static Instrumentation<?, ?> log;

	/** Hostname (or IP) of the PostgreSQL server */
	private static String hostname;
	/** Connection port for the PostgreSQL server */
	private static int port;
	/** The PostgreSQL database with the application's data scope */
	private static String database;
	/** The PostgreSQL user name to access 'DATABASE' -- note: administrative rights, such as the creation of tables, might be necessary */
	private static String user;
	/** The PostgreSQL plain text password for 'USER' */
	private static String password;
	
	
	public static void configureHangmanDatabaseModule(Instrumentation<?, ?> log,
	                                                  String hostname, int port, String database, String user, String password) {

		SMSAppModulePostgreSQLAdapterHangman.log = log;
		
		SMSAppModulePostgreSQLAdapterHangman.hostname = hostname;
		SMSAppModulePostgreSQLAdapterHangman.port     = port;
		SMSAppModulePostgreSQLAdapterHangman.database = database;
		SMSAppModulePostgreSQLAdapterHangman.user     = user;
		SMSAppModulePostgreSQLAdapterHangman.password = password;
	}

	
	private SMSAppModulePostgreSQLAdapterHangman(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
		super(log, preparedProceduresDefinitions);
	}

	@Override
	protected String[] getCredentials() {
		return new String[] {hostname, Integer.toString(port), database, user, password};
	}

	@Override
	protected String[][] getTableDefinitions() {
		if (!ALLOW_DATABASE_ADMINISTRATION) {
			return null;
		}
		return new String[][] {
			{"Matches", "DROP TYPE IF EXISTS MatchStatuses;" + 
			            "CREATE TYPE MatchStatuses AS ENUM (" + list(EMatchStatus.values(), "'", ",") + ");" + 	// SELECT enum_range(NULL::MatchStatuses)
			            "CREATE TABLE Matches(" +
			            "matchId                   SERIAL        PRIMARY KEY, " +
			            "wordProvidingPlayerUserId INTEGER       NOT NULL REFERENCES Users(userId) ON DELETE CASCADE, " +
			            "wordGuessingPlayerUserId  INTEGER       NOT NULL REFERENCES Users(userId) ON DELETE CASCADE, " +
			            "serializedGame            TEXT          NOT NULL, " +
			            "matchStartMillis          BIGINT        NOT NULL, " +
			            "status                    MatchStatuses NOT NULL, " +
			            "cts                       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP," +
			            "uts                       TIMESTAMP     DEFAULT NULL);" +
			            
			            // trigger for 'uts' -- the updated timestamp
			            "CREATE TRIGGER Matches_update_timestamp BEFORE UPDATE ON Matches FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp();" +

				        // Meta record
				        "INSERT INTO Meta(tableName, modelVersion) VALUES ('Matches', '"+modelVersionForMetaTable+"')"},

			{"NextBotWords", "CREATE TABLE NextBotWords(" +
			                 "userId      INTEGER   PRIMARY KEY REFERENCES Users(userId) ON DELETE CASCADE, " +
			                 "nextBotWord INTEGER   NOT NULL, " +
			                 "cts         TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
			                 "uts         TIMESTAMP DEFAULT NULL);" +
			                 
			                 // trigger for 'uts' -- the updated timestamp
			                 "CREATE TRIGGER NextBotWords_update_timestamp BEFORE UPDATE ON NextBotWords FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp();" +
			                 
			                 // stored procedure
			                 "CREATE OR REPLACE FUNCTION SelectAndIncrementNextBotWord(p_userId INTEGER) RETURNS NextBotWords.nextBotWord%TYPE AS $$\n" +
			                 "DECLARE cursor NextBotWords.nextBotWord%TYPE;\n" +
			                 "BEGIN\n" + 
			                 "    UPDATE NextBotWords SET nextBotWord = nextBotWord + 1 WHERE userId=p_userId RETURNING nextBotWord INTO cursor;\n" + 
			                 "    IF NOT FOUND THEN \n" + 
			                 "        cursor := 0;\n" +
			                 "        INSERT INTO NextBotWords(userId, nextBotWord) VALUES (p_userId, cursor);\n" + 
			                 "    END IF;\n" +
			                 "    RETURN cursor;\n" +
			                 "END;\n" +			          
			                 "$$ LANGUAGE plpgsql;" +
  
			                 // Meta record
			                 "INSERT INTO Meta(tableName, modelVersion) VALUES ('NextBotWords', '"+modelVersionForMetaTable+"')"},
		};
	}
	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getMatchDBAdapter() throws SQLException {
		return new SMSAppModulePostgreSQLAdapterHangman(log, new String[][] {
			{"ResetTable",             "TRUNCATE Matches CASCADE"},
			{"InsertMatch",            "INSERT INTO Matches(wordProvidingPlayerUserId, wordGuessingPlayerUserId, serializedGame, matchStartMillis, status) " +
			                           "VALUES(${WORD_PROVIDING_PLAYER_USER_ID}, ${WORD_GUESSING_PLAYER_USER_ID}, ${SERIALIZED_GAME}, ${MATCH_START_MILLIS}, ${STATUS}::MatchStatuses) " +
			                           "RETURNING matchId"},
			{"SelectMatchById",        "SELECT m.wordProvidingPlayerUserId AS wordProvidingPlayerUserId, wp.phoneNumber AS wordProvidingPlayerPhone," +
			                           "       m.wordGuessingPlayerUserId AS wordGuessingPlayerUserId,   wg.phoneNumber AS wordGuessingPlayerPhone," +
			                           "       m.serializedGame AS serializedGame, m.matchStartMillis AS matchStartMillis, " +
			                           "       m.status AS status FROM Matches m, Users wp, Users wg " + 
			                           "WHERE m.matchId=${MATCH_ID} AND m.wordProvidingPlayerUserId=wp.userId AND m.wordGuessingPlayerUserId=wg.userId"},
			{"UpdateMatchStatusById",  "UPDATE Matches SET (status, serializedGame) = (${STATUS}::MatchStatuses, ${SERIALIZED_GAME}) WHERE matchId=${MATCH_ID}"},
		});
	}

	public static JDBCAdapter getNextBotWordsDBAdapter() throws SQLException {
		return new SMSAppModulePostgreSQLAdapterHangman(log, new String[][] {
			{"ResetTable",                    "TRUNCATE NextBotWords CASCADE"},
			{"SelectAndIncrementNextBotWord", "SELECT * FROM SelectAndIncrementNextBotWord(${USER_ID})"},
		});
	}
}
