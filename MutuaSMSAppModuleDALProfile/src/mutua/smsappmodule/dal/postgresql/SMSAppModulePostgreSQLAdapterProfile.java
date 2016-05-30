package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import adapters.AbstractPreparedProcedure;
import adapters.IJDBCAdapterParameterDefinition;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * SMSAppModulePostgreSQLAdapterProfile.java
 * =========================================
 * (created by luiz, Aug 3, 2015)
 *
 * Provides {@link PostgreSQLAdapter}s to manipulate the "User Profiler" module database & tables
 *
 * @see PostgreSQLAdapter
 * @version $Id$
 * @author luiz
 */

public class SMSAppModulePostgreSQLAdapterProfile extends PostgreSQLAdapter {

	
	// the version information for database tables present on this class, to be stored on the 'Meta' table. Useful for future data conversions.
	private static String modelVersionForMetaTable = "2015.08.12";
	
	// Mutua Configurable Class pattern
	///////////////////////////////////
	
	/** this class' singleton instance */
	private static SMSAppModulePostgreSQLAdapterProfile instance = null;
	
	// JDBCAdapter default values
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
	
	/** The table used to register MOs */
	private static String MO_TABLE_NAME;
	/** The index id field name, within the MO table */
	private static String MO_ID_FIELD_NAME;
	/** The phone field name, within the MO table */
	private static String MO_PHONE_FIELD_NAME;

	/** method to be called when attempting to configure the singleton for new instances of 'PostgreSQLAdapter'.
	 *  @param allowDataStructuresAssertion see {@link #ALLOW_DATA_STRUCTURES_ASSERTION}
	 *  @param shouldDebugQueries           see {@link #SHOULD_DEBUG_QUERIES}
	 *  @param hostname                     see {@link #HOSTNAME}
	 *  @param port                         see {@link #PORT}
	 *  @param database                     see {@link #DATABASE}
	 *  @param user                         see {@link #USER}
	 *  @param password                     see {@link #PASSWORD}
	 *  @param moTableName                  see {@link #MO_TABLE_NAME}
	 *  @param moIdFieldName                see {@link #MO_ID_FIELD_NAME}
	 *  @param moPhoneFieldName             see {@link #MO_PHONE_FIELD_NAME} */
	public static void configureDefaultValuesForNewInstances(
		boolean allowDataStructuresAssertion, boolean shouldDebugQueries,
	    String hostname, int port, String database, String user, String password,
	    String moTableName, String moIdFieldName, String moPhoneFieldName) throws SQLException {

		ALLOW_DATA_STRUCTURES_ASSERTION = allowDataStructuresAssertion;
		SHOULD_DEBUG_QUERIES            = shouldDebugQueries;
		HOSTNAME = hostname;
		PORT     = port;
		DATABASE = database;
		USER     = user;
		PASSWORD = password;
		
		MO_TABLE_NAME       = moTableName;
		MO_ID_FIELD_NAME    = moIdFieldName;
		MO_PHONE_FIELD_NAME = moPhoneFieldName;

		instance = null;
	}
	
	
	private SMSAppModulePostgreSQLAdapterProfile() throws SQLException {
		super(ALLOW_DATA_STRUCTURES_ASSERTION, SHOULD_DEBUG_QUERIES, HOSTNAME, PORT, DATABASE, USER, PASSWORD);
	}

	@Override
	protected String[][] getTableDefinitions() {
		return new String[][] {
			{"Profiles", "CREATE TABLE Profiles(" +
			             "userId        INTEGER     PRIMARY KEY REFERENCES Users(userId) ON DELETE CASCADE," +
			             "nickname      TEXT        NOT NULL," +
			             "cts           TIMESTAMP   DEFAULT CURRENT_TIMESTAMP," +
			             "uts           TIMESTAMP   DEFAULT NULL)",
			            
			             // custom indexes
			             "CREATE UNIQUE INDEX unique_caseinsensitive_nickname ON Profiles (lower(nickname) text_pattern_ops)",
			            
			             // trigger for 'uts' -- the updated timestamp
			             "CREATE TRIGGER Profiles_update_timestamp BEFORE UPDATE ON Profiles FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp()",

			             // stored procedure
			             "CREATE OR REPLACE FUNCTION AssertProfile(p_userId INTEGER, p_nickname TEXT, OUT userId INTEGER, OUT nickname TEXT) RETURNS RECORD AS $$\n" +
			             "DECLARE\n" +
			             "    sequence    INTEGER := 1;\n" +
			             "    attempt     INTEGER := 1;\n" +
			             "    sequenceRow RECORD;\n" +
			             "BEGIN\n" + 
			             "    UPDATE Profiles SET (nickname) = (p_nickname) WHERE Profiles.userId=p_userId; \n" + 
			             "    IF NOT FOUND THEN \n" + 
			             "        INSERT INTO Profiles(userId, nickname) VALUES (p_userId, p_nickname); \n" + 
			             "    END IF;\n" +
			             "    userId   := p_userId;\n" +
			             "    nickname := p_nickname;\n" +
			             "    RETURN;\n" +
			             "EXCEPTION WHEN unique_violation THEN\n" +
			                  // a unique 'nickname' violation: determine the first role on the sequence and insert (or update) the sequenced nick
			                  // even with the explicit lock (valid until the function ends), a retry loop is still necessary. Possibly because the
			                  // transaction starts with the query that calls the stored procedure
			             "    <<retry>>\n" +
			             "    LOOP\n" +
			             "        BEGIN\n" +
			             "            PERFORM pg_advisory_xact_lock(hashtext(lower(p_nickname)));\n" +
			             "            FOR sequenceRow IN SELECT CAST(SUBSTRING(lower(Profiles.nickname) FROM lower('^'||p_nickname||'(\\d+)$')) AS INTEGER) as nicknameSequenceElement FROM Profiles WHERE lower(Profiles.nickname) LIKE lower(p_nickname||'%') AND lower(Profiles.nickname) ~ lower('^'||p_nickname||'\\d+$') AND Profiles.userId != p_userId ORDER BY nicknameSequenceElement ASC\n" +
			             "            LOOP\n" +
			             "                EXIT WHEN (sequenceRow.nicknameSequenceElement - sequence) > 0;\n" +
			             "                sequence := sequence + 1;\n" +
			             "            END LOOP;\n" +
			             "            UPDATE Profiles SET (nickname) = (p_nickname||CAST(sequence AS TEXT)) WHERE Profiles.userId=p_userId; \n" + 
			             "            IF NOT FOUND THEN \n" + 
			             "                INSERT INTO Profiles(userId, nickname) VALUES (p_userId, p_nickname||CAST(sequence AS TEXT)); \n" + 
			             "            END IF;\n" +
			             "            userId   := p_userId;\n" +
			             "            nickname := p_nickname||CAST(sequence AS TEXT);\n" +
			             "            RETURN;\n" +
			             "        EXCEPTION WHEN unique_violation THEN\n" +
			             "            IF attempt >= 3 THEN\n" +
			             "                RAISE EXCEPTION 'Duplicate entry (userId and/or nickname): (%, %), even with pg_advisory_xact_lock(%), after % attempt(s)', p_userId, p_nickname||CAST(sequence AS TEXT), hashtext(lower(p_nickname)), attempt USING ERRCODE = 'unique_violation';\n" +
			             "            END IF;\n" +
			             "            attempt := attempt + 1;\n" +
			             "        END;\n" +
			             "    END LOOP retry;\n" +
			             "END;\n" + 
			             "$$ LANGUAGE plpgsql",
				          
				         // Meta record
				         "INSERT INTO Meta(tableName, modelVersion) VALUES ('Profiles', '"+modelVersionForMetaTable+"')"},
		};
	}
	
	/***************
	** PARAMETERS **
	***************/
	
	public enum Parameters implements IJDBCAdapterParameterDefinition {

		USER_ID,
		NICKNAME,
		MAX_PROFILES,
		SESSION_PROPERTY_NAME,
		SESSION_PROPERTY_VALUES,
		
		;
		
		@Override
		public String getParameterName() {
			return name();
		}
	}
	
	/***************
	** STATEMENTS **
	***************/

	/** Zero the table contents -- for testing purposes only */
	public final AbstractPreparedProcedure ResetTable = new AbstractPreparedProcedure(connectionPool,
		"TRUNCATE Profiles CASCADE");
	/** Returns the nickname associated with 'USER_ID' */
	public final AbstractPreparedProcedure SelectProfileByUser = new AbstractPreparedProcedure(connectionPool,
		"SELECT userId, nickname FROM Profiles WHERE userId=",Parameters.USER_ID);
	/** Assures 'USER_ID' has the 'NICKNAME' or a derivative of it, in case it is already taken by another user. The attributed nickname is returned. */
	public final AbstractPreparedProcedure AssertProfile = new AbstractPreparedProcedure(connectionPool,
		"SELECT userId, nickname FROM AssertProfile(",Parameters.USER_ID,", ",Parameters.NICKNAME,")");
	/** Returns the full user information (id, phone, correctly cased nickname) associated with the given case insensitive 'NICKNAME' */
	public final AbstractPreparedProcedure SelectProfileByNickname = new AbstractPreparedProcedure(connectionPool,
		"SELECT Users.userId, Users.phoneNumber, Profiles.nickname FROM Users, Profiles WHERE lower(nickname)=lower(",Parameters.NICKNAME,") AND Users.userId = Profiles.userId");
	/** List Users who sent MOs whose provided session variable isn't one of the provided values */
	public final AbstractPreparedProcedure SelectRecentProfilesByLastMOTimeNotInSessionValues = new AbstractPreparedProcedure(connectionPool,
		"SELECT Users.userId, Users.phoneNumber, Profiles.nickname, Sessions.propertyValue FROM Users, Sessions, Profiles WHERE phoneNumber IN ",
		"(SELECT DISTINCT ON (phone) phone FROM (SELECT ",MO_PHONE_FIELD_NAME," FROM ",MO_TABLE_NAME," ORDER BY ",MO_ID_FIELD_NAME,
		" DESC LIMIT ",Parameters.MAX_PROFILES,"*2) as bruteMO ORDER BY phone LIMIT ",Parameters.MAX_PROFILES,
		") AND (Users.userId = Sessions.userId) AND (Users.userId = Profiles.userId) AND ((Sessions.propertyName = ",
		Parameters.SESSION_PROPERTY_NAME,") AND (NOT Sessions.propertyValue = ANY (",Parameters.SESSION_PROPERTY_VALUES,")))");
	
	// public methods
	/////////////////
	
	public static SMSAppModulePostgreSQLAdapterProfile getInstance() throws SQLException {
		if (instance == null) {
			instance = new SMSAppModulePostgreSQLAdapterProfile();
		}
		return instance;
	}
	
	public static SMSAppModulePostgreSQLAdapterProfile getProfileDBAdapter() throws SQLException {
		return getInstance();
	}
}
