package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;
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
	
	
	public static void configureProfileDatabaseModule(Instrumentation<?, ?> log,
	                                                  String hostname, int port, String database, String user, String password) {

		SMSAppModulePostgreSQLAdapterProfile.log = log;
		
		SMSAppModulePostgreSQLAdapterProfile.hostname = hostname;
		SMSAppModulePostgreSQLAdapterProfile.port     = port;
		SMSAppModulePostgreSQLAdapterProfile.database = database;
		SMSAppModulePostgreSQLAdapterProfile.user     = user;
		SMSAppModulePostgreSQLAdapterProfile.password = password;
	}

	
	private SMSAppModulePostgreSQLAdapterProfile(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
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
			{"Profiles", "CREATE TABLE Profiles(" +
			             "userId        INTEGER     PRIMARY KEY REFERENCES Users(userId) ON DELETE CASCADE," +
			             "nickname      TEXT        NOT NULL," +
			             "cts           TIMESTAMP   DEFAULT CURRENT_TIMESTAMP," +
			             "uts           TIMESTAMP   DEFAULT NULL);" +
			            
			             // custom indexes
			             "CREATE UNIQUE INDEX unique_caseinsensitive_nickname ON Profiles (lower(nickname) text_pattern_ops);" +
			            
			             // trigger for 'uts' -- the updated timestamp
			             "CREATE TRIGGER Profiles_update_timestamp BEFORE UPDATE ON Profiles FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp();" +

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
			             "$$ LANGUAGE plpgsql;" +
				          
				         // Meta record
				         "INSERT INTO Meta(tableName, modelVersion) VALUES ('Profiles', '"+modelVersionForMetaTable+"')"},
		};
	}
	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getProfileDBAdapter() throws SQLException {
		return new SMSAppModulePostgreSQLAdapterProfile(log, new String[][] {
			{"ResetTable",              "TRUNCATE Profiles CASCADE"},
			{"SelectProfileByUser",     "SELECT userId, nickname FROM Profiles WHERE userId=${USER_ID}"},
			{"AssertProfile",           "SELECT userId, nickname FROM AssertProfile(${USER_ID}, ${NICKNAME})"},
			{"SelectProfileByNickname", "SELECT Users.userId, Users.phoneNumber, Profiles.nickname FROM Users, Profiles WHERE lower(nickname)=lower(${NICKNAME}) AND Users.userId = Profiles.userId"},
		});
	}
}
