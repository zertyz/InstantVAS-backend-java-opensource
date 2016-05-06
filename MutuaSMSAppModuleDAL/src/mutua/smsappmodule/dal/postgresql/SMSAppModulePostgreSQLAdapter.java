package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import adapters.AbstractPreparedProcedure;
import adapters.IJDBCAdapterParameterDefinition;
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
	private static final String modelVersionForMetaTable = "2015.08.12";

	// Mutua Configurable Class pattern
	///////////////////////////////////
	
	/** this class' singleton instance */
	private static SMSAppModulePostgreSQLAdapter instance = null;
	
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
		boolean allowDataStructuresAssertion, boolean shouldDebugQueries,
	    String hostname, int port, String database, String user, String password) throws SQLException {
				
		ALLOW_DATA_STRUCTURES_ASSERTION = allowDataStructuresAssertion;
		SHOULD_DEBUG_QUERIES            = shouldDebugQueries;
		HOSTNAME = hostname;
		PORT     = port;
		DATABASE = database;
		USER     = user;
		PASSWORD = password;

		instance = null;
	}
	
	
	private SMSAppModulePostgreSQLAdapter() throws SQLException {
		super(ALLOW_DATA_STRUCTURES_ASSERTION, SHOULD_DEBUG_QUERIES, HOSTNAME, PORT, DATABASE, USER, PASSWORD);
	}

	@Override
	protected String[][] getTableDefinitions() {
		return new String[][] {
			
			{"Meta", // global set_updated_timestamp trigger function
                     // all tables that sets this trigger with "CREATE TRIGGER [TBLNAME]_update_timestamp BEFORE UPDATE ON [TBLNAME] FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp()"
                     // must have a field of type TIMESTAMP named 'uts'
                     "CREATE OR REPLACE FUNCTION set_updated_timestamp() RETURNS TRIGGER LANGUAGE plpgsql AS $$\n" +
                     "BEGIN\n" +
                     "    NEW.uts := now();\n" +
                     "    RETURN NEW;\n" +
                     "END;\n" +
                     "$$",

                     // the table
			         "CREATE TABLE Meta(" +
			         "tableName     TEXT       PRIMARY KEY," +
			         "modelVersion  TEXT       NOT NULL," +
			         "cts           TIMESTAMP  DEFAULT CURRENT_TIMESTAMP," +
			         "uts           TIMESTAMP  DEFAULT NULL)",

			         // trigger for 'uts' -- the updated timestamp
	                 "CREATE TRIGGER Meta_update_timestamp BEFORE UPDATE ON Meta FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp()",
			          
			         // Meta record
			         "INSERT INTO Meta(tableName, modelVersion) VALUES ('Meta', '"+modelVersionForMetaTable+"')"},
			
			{"Users", "CREATE TABLE Users(" +
			          "userId          SERIAL      PRIMARY KEY," +
			          "phoneNumber     TEXT        NOT NULL UNIQUE," +
	                  "cts             TIMESTAMP   DEFAULT CURRENT_TIMESTAMP," +
	                  "uts             TIMESTAMP   DEFAULT NULL)",
	                  
	                  // trigger for 'uts' -- the updated timestamp
	                  "CREATE TRIGGER Users_update_timestamp BEFORE UPDATE ON Users FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp()",
			          
	                  // stored procedure
			          "CREATE OR REPLACE FUNCTION AssertUserIsRegistered(p_phone TEXT) RETURNS SETOF INTEGER AS $$\n" + 
			          "BEGIN\n" + 
			          "    RETURN QUERY SELECT userId FROM Users WHERE phoneNumber=p_phone; \n" + 
			          "    IF NOT FOUND THEN \n" + 
			          "        RETURN QUERY INSERT INTO Users(phoneNumber) VALUES (p_phone) RETURNING userId; \n" + 
			          "    END IF;\n" + 
			          "END;\n" +			          
			          "$$ LANGUAGE plpgsql",
			          
			          // Meta record
			          "INSERT INTO Meta(tableName, modelVersion) VALUES ('Users', '"+modelVersionForMetaTable+"')"},
			          
			{"Sessions", "CREATE TABLE Sessions(" +
			             "userId                INTEGER     REFERENCES Users(userId) ON DELETE CASCADE," +
			             "propertyName          TEXT        NOT NULL," +
			             "propertyValue         TEXT        NOT NULL," +
		                 "cts                   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP," +
		                 "uts                   TIMESTAMP   DEFAULT NULL," +
			             "PRIMARY KEY (userId, propertyName))",
		                 
			             // TODO we may wish to consider using enum data types, as in http://www.postgresql.org/docs/9.2/static/datatype-enum.html
		                  
		                 // trigger for 'uts' -- the updated timestamp
		                 "CREATE TRIGGER Sessions_update_timestamp BEFORE UPDATE ON Sessions FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp()",
				          
				         // Meta record
				         "INSERT INTO Meta(tableName, modelVersion) VALUES ('Sessions', '"+modelVersionForMetaTable+"')"}
		};
	}
	
	/***************
	** PARAMETERS **
	***************/
	
	public enum Parameters implements IJDBCAdapterParameterDefinition {

		// 'Users' parameters
		PHONE,
		// 'Sessions' parameters
		USER_ID,
		PROPERTY_NAME,
		PROPERTY_VALUE,
		
		;
		
		@Override
		public String getParameterName() {
			return name();
		}
	}
	
	/***************
	** STATEMENTS **
	***************/

	public static final class UsersDBStatements {
		/** Zero the table contents -- for testing purposes only */
		public final static AbstractPreparedProcedure ResetTable = new AbstractPreparedProcedure(connectionPool,
			"TRUNCATE Users CASCADE");
		/** Executes the 'AssertUserIsRegistered' stored procedure, which returns an 'USER_ID' for the given 'PHONE', registering it as a new user as needed */
		public final static AbstractPreparedProcedure AssertUserIsRegistered = new AbstractPreparedProcedure(connectionPool,
			"SELECT * FROM AssertUserIsRegistered(",Parameters.PHONE,")");
	}
	
	public static final class SessionsDBStatements {
		/** Zero the table contents -- for testing purposes only */
		public final static AbstractPreparedProcedure ResetTable = new AbstractPreparedProcedure(connectionPool,
			"TRUNCATE Sessions CASCADE");
		/** Remove the property 'PROPERTY_NAME' from the given 'USER_ID' */
		public final static AbstractPreparedProcedure DeleteProperty = new AbstractPreparedProcedure(connectionPool,
			"DELETE FROM Sessions WHERE userId=",Parameters.USER_ID," AND propertyName=",Parameters.PROPERTY_NAME);
		/** Retrieve all pairs {propertyName, propertyValue} from 'USER_ID' */
		public final static AbstractPreparedProcedure FetchProperties = new AbstractPreparedProcedure(connectionPool,
			"SELECT propertyName, propertyValue FROM Sessions WHERE userId=",Parameters.USER_ID);
		/** Assign a non existing 'PROPERTY_NAME' with the given 'PROPERTY_VALUE' to 'USER_ID' */
		public final static AbstractPreparedProcedure InsertProperty = new AbstractPreparedProcedure(connectionPool,
			"INSERT INTO Sessions(userId, propertyName, propertyValue) VALUES (",Parameters.USER_ID,", ",Parameters.PROPERTY_NAME,", ",Parameters.PROPERTY_VALUE,")");
		/** Update the 'USER_ID' existing 'PROPERTY_NAME' to the desired 'PROPERTY_VALUE' */
		public final static AbstractPreparedProcedure UpdateProperty = new AbstractPreparedProcedure(connectionPool,
			"UPDATE Sessions SET propertyValue=",Parameters.PROPERTY_VALUE," WHERE userId=",Parameters.USER_ID," AND propertyName=",Parameters.PROPERTY_NAME);
		/** Updates or inserts 'PROPERTY_NAME' with the given 'PROPERTY_VALUE' and associate them with 'USER_ID' */
		public final static AbstractPreparedProcedure AssureProperty = new AbstractPreparedProcedure(connectionPool,
			"WITH upsert AS (",
			                 "UPDATE Sessions SET propertyValue=",Parameters.PROPERTY_VALUE," WHERE userId=",Parameters.USER_ID,
			                 " AND propertyName=",Parameters.PROPERTY_NAME," RETURNING *) ",
			"INSERT INTO Sessions(userId, propertyName, propertyValue) SELECT ",Parameters.USER_ID,", ",Parameters.PROPERTY_NAME,", ",
			                                                           Parameters.PROPERTY_VALUE," WHERE NOT EXISTS (SELECT * FROM upsert)");
	}

	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getInstance() throws SQLException {
//		if (wasConfigured = false) {
//		throw new RuntimeException("Class '" + SMSAppModulePostgreSQLAdapter.class.getCanonicalName() + "' was not configured according to the " +
//		                           "'Mutua Configurable Class' pattern -- a preliminar call to 'configureDefaultValuesForNewInstances' " +
//		                           "was not made.");
//	}
		if (instance == null) {
			instance = new SMSAppModulePostgreSQLAdapter();
		}
		return instance;
	}
	
	public static JDBCAdapter getUsersDBAdapter() throws SQLException {
		return getInstance();
	}

	public static JDBCAdapter getSessionsDBAdapter() throws SQLException {
		return getInstance();
	}

}
