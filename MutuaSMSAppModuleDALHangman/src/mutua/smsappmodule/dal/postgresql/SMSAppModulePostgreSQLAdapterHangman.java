package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;
import adapters.AbstractPreparedProcedure;
import adapters.IJDBCAdapterParameterDefinition;
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
	private static final String modelVersionForMetaTable = "2015.08.13";
	
	// Mutua Configurable Class pattern
	///////////////////////////////////
	
	/** this class' singleton instance */
	private static SMSAppModulePostgreSQLAdapterHangman instance = null;
	
	// JDBCAdapter default values
	private static Instrumentation<?, ?> LOG;
	/** @see JDBCAdapter#hostname */
	private static String HOSTNAME;
	/** @see JDBCAdapter#port */
	private static int    PORT;
	/** @see JDBCAdapter#database */
	private static String DATABASE;
	/** @see JDBCAdapter#user */
	private static String USER;
	/** @see JDBCAdapter#password */
	private static String PASSWORD;
	/** @see JDBCAdapter#allowDataStructuresAssertion */
	private static boolean ALLOW_DATA_STRUCTURES_ASSERTION;
	/** @see JDBCAdapter#shouldDebugQueries */
	private static boolean SHOULD_DEBUG_QUERIES;	
	
	/** method to be called when attempting to configure the singleton for new instances of 'PostgreSQLAdapter'.
	 *  @param log
	 *  @param allowDataStructuresAssertion see {@link #ALLOW_DATA_STRUCTURES_ASSERTION}
	 *  @param shouldDebugQueries           see {@link #SHOULD_DEBUG_QUERIES}
	 *  @param hostname                     see {@link #HOSTNAME}
	 *  @param port                         see {@link #PORT}
	 *  @param database                     see {@link #DATABASE}
	 *  @param user                         see {@link #USER}
	 *  @param password                     see {@link #PASSWORD} */
	public static void configureDefaultValuesForNewInstances(
		Instrumentation<?, ?> log, boolean allowDataStructuresAssertion, boolean shouldDebugQueries,
	    String hostname, int port, String database, String user, String password) throws SQLException {
				
		LOG      = log;
		ALLOW_DATA_STRUCTURES_ASSERTION = allowDataStructuresAssertion;
		SHOULD_DEBUG_QUERIES            = shouldDebugQueries;
		HOSTNAME = hostname;
		PORT     = port;
		DATABASE = database;
		USER     = user;
		PASSWORD = password;

		instance = null;
	}
	
	
	private SMSAppModulePostgreSQLAdapterHangman() throws SQLException {
		super(LOG, ALLOW_DATA_STRUCTURES_ASSERTION, SHOULD_DEBUG_QUERIES, HOSTNAME, PORT, DATABASE, USER, PASSWORD);
	}

	@Override
	protected String[][] getTableDefinitions() {
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
	
	
	/***************
	** PARAMETERS **
	***************/
	
	public enum Parameters implements IJDBCAdapterParameterDefinition {

		// Matches
		MATCH_ID,
		WORD_PROVIDING_PLAYER_USER_ID,
		WORD_GUESSING_PLAYER_USER_ID,
		SERIALIZED_GAME,
		MATCH_START_MILLIS,
		STATUS,
		
		// NextBotWords
		USER_ID,
		
		;
		
		@Override
		public String getParameterName() {
			return name();
		}
	}
	
	/***************
	** STATEMENTS **
	***************/

	public static final class MatchesDBStatements {
		/** Zero the table contents -- for testing purposes only */
		public final static AbstractPreparedProcedure ResetTable = new AbstractPreparedProcedure(
			"TRUNCATE Matches CASCADE");
		/**  */
		public final static AbstractPreparedProcedure InsertMatch = new AbstractPreparedProcedure(
			"INSERT INTO Matches(wordProvidingPlayerUserId, wordGuessingPlayerUserId, serializedGame, matchStartMillis, status) ",
			"VALUES(",Parameters.WORD_PROVIDING_PLAYER_USER_ID,", ",Parameters.WORD_GUESSING_PLAYER_USER_ID,", ",Parameters.SERIALIZED_GAME,", ",
			Parameters.MATCH_START_MILLIS,", ",Parameters.STATUS,"::MatchStatuses) RETURNING matchId");
		/**  */
		public final static AbstractPreparedProcedure SelectMatchById = new AbstractPreparedProcedure(
			"SELECT m.wordProvidingPlayerUserId AS wordProvidingPlayerUserId, wp.phoneNumber AS wordProvidingPlayerPhone,",
			       "m.wordGuessingPlayerUserId AS wordGuessingPlayerUserId,   wg.phoneNumber AS wordGuessingPlayerPhone,",
			       "m.serializedGame AS serializedGame, m.matchStartMillis AS matchStartMillis, ",
			       "m.status AS status FROM Matches m, Users wp, Users wg ",
			"WHERE m.matchId=",Parameters.MATCH_ID," AND m.wordProvidingPlayerUserId=wp.userId AND m.wordGuessingPlayerUserId=wg.userId");
		/**  */
		public final static AbstractPreparedProcedure UpdateMatchStatusById = new AbstractPreparedProcedure(
			"UPDATE Matches SET (status, serializedGame) = (",Parameters.STATUS,"::MatchStatuses, ",Parameters.SERIALIZED_GAME,") WHERE matchId=",Parameters.MATCH_ID);
	}

	public static final class NextBotWordsDBStatements {
		/** Zero the table contents -- for testing purposes only */
		public final static AbstractPreparedProcedure ResetTable = new AbstractPreparedProcedure(
			"TRUNCATE NextBotWords CASCADE");
		/**  */
		public final static AbstractPreparedProcedure SelectAndIncrementNextBotWord = new AbstractPreparedProcedure(
			"SELECT * FROM SelectAndIncrementNextBotWord(",Parameters.USER_ID,")");
	}
	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getInstance() throws SQLException {
		if (instance == null) {
			instance = new SMSAppModulePostgreSQLAdapterHangman();
//			throw new RuntimeException("Class '" + SMSAppModulePostgreSQLAdapterHangman.class.getCanonicalName() + "' was not configured according to the " +
//			                           "'SMSAppModulePostgreSQLAdapterSubscription' pattern -- a preliminar call to 'configureDefaultValuesForNewInstances' " +
//			                           "was not made.");
		}
		return instance;
	}

	public static JDBCAdapter getMatchDBAdapter() throws SQLException {
		return getInstance();
	}

	public static JDBCAdapter getNextBotWordsDBAdapter() throws SQLException {
		return getInstance();
	}
}
