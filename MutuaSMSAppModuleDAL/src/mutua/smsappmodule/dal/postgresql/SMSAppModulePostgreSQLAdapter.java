package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * SMSAppModulePostgreSQLAdapter.java
 * ==================================
 * (created by luiz, Jul 28, 2015)
 *
 * Provides {@link PostgreSQLAdapter}s to manipulate the Subscription module database & tables
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SMSAppModulePostgreSQLAdapter extends PostgreSQLAdapter {

	
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
	
	
	public static void configureSMSDatabaseModule(Instrumentation<?, ?> log,
	                                                  String hostname, int port, String database, String user, String password) {

		SMSAppModulePostgreSQLAdapter.log = log;
		
		SMSAppModulePostgreSQLAdapter.hostname = hostname;
		SMSAppModulePostgreSQLAdapter.port     = port;
		SMSAppModulePostgreSQLAdapter.database = database;
		SMSAppModulePostgreSQLAdapter.user     = user;
		SMSAppModulePostgreSQLAdapter.password = password;
	}

	
	private SMSAppModulePostgreSQLAdapter(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
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
			
			{"Meta", // global set_updated_timestamp trigger function
                     // all tables that sets this trigger with "CREATE TRIGGER [TBLNAME]_update_timestamp BEFORE UPDATE ON [TBLNAME] FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp()"
                     // must have a field of type TIMESTAMP named 'uts'
                     "CREATE OR REPLACE FUNCTION set_updated_timestamp() RETURNS TRIGGER LANGUAGE plpgsql AS $$\n" +
                     "BEGIN\n" +
                     "    NEW.uts := now();\n" +
                     "    RETURN NEW;\n" +
                     "END;\n" +
                     "$$;" +

                     // the table
			         "CREATE TABLE Meta(" +
			         "tableName     TEXT       PRIMARY KEY," +
			         "modelVersion  TEXT       NOT NULL," +
			         "cts           TIMESTAMP  DEFAULT CURRENT_TIMESTAMP," +
			         "uts           TIMESTAMP  DEFAULT NULL);" +

			         // trigger for 'uts' -- the updated timestamp
	                 "CREATE TRIGGER Meta_update_timestamp BEFORE UPDATE ON Meta FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp();" +
			          
			         // Meta record
			         "INSERT INTO Meta(tableName, modelVersion) VALUES ('Meta', '"+modelVersionForMetaTable+"')"},
			
			{"Users", "CREATE TABLE Users(" +
			          "userId          SERIAL      PRIMARY KEY," +
			          "phoneNumber     TEXT        NOT NULL UNIQUE," +
	                  "cts             TIMESTAMP   DEFAULT CURRENT_TIMESTAMP," +
	                  "uts             TIMESTAMP   DEFAULT NULL);" +
	                  
	                  // trigger for 'uts' -- the updated timestamp
	                  "CREATE TRIGGER Users_update_timestamp BEFORE UPDATE ON Users FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp();" +
			          
	                  // stored procedure
			          "CREATE OR REPLACE FUNCTION AssertUserIsRegistered(p_phone TEXT) RETURNS SETOF INTEGER AS $$\n" + 
			          "BEGIN\n" + 
			          "    RETURN QUERY SELECT userId FROM Users WHERE phoneNumber=p_phone; \n" + 
			          "    IF NOT FOUND THEN \n" + 
			          "        RETURN QUERY INSERT INTO Users(phoneNumber) VALUES (p_phone) RETURNING userId; \n" + 
			          "    END IF;\n" + 
			          "END;\n" +			          
			          "$$ LANGUAGE plpgsql;" +
			          
			          // Meta record
			          "INSERT INTO Meta(tableName, modelVersion) VALUES ('Users', '"+modelVersionForMetaTable+"')"},
			          
			{"Sessions", "CREATE TABLE Sessions(" +
			             "userId                INTEGER     REFERENCES Users(userId) ON DELETE CASCADE," +
			             "propertyName          TEXT        NOT NULL," +
			             "propertyValue         TEXT        NOT NULL," +
		                 "cts                   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP," +
		                 "uts                   TIMESTAMP   DEFAULT NULL," +
			             "PRIMARY KEY (userId, propertyName));" +
		                 
			             // TODO we may wish to consider using enum data types, as in http://www.postgresql.org/docs/9.2/static/datatype-enum.html
		                  
		                 // trigger for 'uts' -- the updated timestamp
		                 "CREATE TRIGGER Sessions_update_timestamp BEFORE UPDATE ON Sessions FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp();" +
				          
				         // Meta record
				         "INSERT INTO Meta(tableName, modelVersion) VALUES ('Sessions', '"+modelVersionForMetaTable+"')"}
		};
	}
	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getUsersDBAdapter() throws SQLException {
		return new SMSAppModulePostgreSQLAdapter(log, new String[][] {
			{"ResetTable",                "TRUNCATE Users CASCADE"},
			{"AssertUserIsRegistered",    "SELECT * FROM AssertUserIsRegistered(${PHONE})"},
		});
	}

	public static JDBCAdapter getSessionsDBAdapter() throws SQLException {
		return new SMSAppModulePostgreSQLAdapter(log, new String[][] {
			{"ResetTable",          "TRUNCATE Sessions CASCADE"},
			{"DeleteProperty",      "DELETE FROM Sessions WHERE userId=${USER_ID} AND propertyName=${PROPERTY_NAME}"},
			{"FetchProperties",     "SELECT propertyName, propertyValue FROM Sessions WHERE userId=${USER_ID}"},
			{"InsertProperty",      "INSERT INTO Sessions(userId, propertyName, propertyValue) VALUES (${USER_ID}, ${PROPERTY_NAME}, ${PROPERTY_VALUE})"},
			{"UpdateProperty",      "UPDATE Sessions SET propertyValue=${PROPERTY_VALUE} WHERE userId=${USER_ID} AND propertyName=${PROPERTY_NAME}"},
			{"AssureProperty",      "WITH upsert AS (" +
			                                        "UPDATE Sessions SET propertyValue=${PROPERTY_VALUE} WHERE userId=${USER_ID} AND propertyName=${PROPERTY_NAME} " +
			                                        "RETURNING *) " +
			                        "INSERT INTO Sessions(userId, propertyName, propertyValue) SELECT ${USER_ID}, ${PROPERTY_NAME}, ${PROPERTY_VALUE} " + 
			                                                                                         "WHERE NOT EXISTS (SELECT * FROM upsert)"},
		});
	}

}
