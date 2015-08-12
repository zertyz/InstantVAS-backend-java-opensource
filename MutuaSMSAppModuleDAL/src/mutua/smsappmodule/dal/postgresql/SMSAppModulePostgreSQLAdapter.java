package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.icc.configuration.annotations.ConfigurableElement;
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

	// configuration
	////////////////
	
	@ConfigurableElement("The application's instrumentation instance to be used to log PostgreSQL database events for the base functionality of all SMS Modules")
	public static Instrumentation<?, ?> log;

	@ConfigurableElement("Hostname (or IP) of the PostgreSQL server")
	public static String HOSTNAME;
	@ConfigurableElement("Connection port for the PostgreSQL server")
	public static int    PORT;
	@ConfigurableElement("The PostgreSQL database with the application's data scope")
	public static String DATABASE;
	@ConfigurableElement("The PostgreSQL user name to access 'DATABASE' -- note: administrative rights, such as the creation of tables, may be necessary")
	public static String USER;
	@ConfigurableElement("The PostgreSQL plain text password for 'USER'")
	public static String PASSWORD;

	
	private SMSAppModulePostgreSQLAdapter(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
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
				
			{"Users", // global set_updated_timestamp trigger function
                      // all tables that sets this trigger with "CREATE TRIGGER [TBLNAME]_update_timestamp BEFORE UPDATE ON [TBLNAME] FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp()"
                      // must have a field of type TIMESTAMP named 'uts'
                      "CREATE FUNCTION set_updated_timestamp() RETURNS TRIGGER LANGUAGE plpgsql AS $$\n" +
                      "BEGIN\n" +
                      "    NEW.uts := now();\n" +
                      "    RETURN NEW;\n" +
                      "END;\n" +
                      "$$;" +

                      // the table
			          "CREATE TABLE Users(" +
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
			          "$$ LANGUAGE plpgsql;"},
			          
			{"Sessions", "CREATE TABLE Sessions(" +
			             "userId                INTEGER     REFERENCES Users(userId) ON DELETE CASCADE," +
			             "propertyName          TEXT        NOT NULL," +
			             "propertyValue         TEXT        NOT NULL," +
		                 "cts                   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP," +
		                 "uts                   TIMESTAMP   DEFAULT NULL," +
			             "PRIMARY KEY (userId, propertyName));" +
		                  
		                 // trigger for 'uts' -- the updated timestamp
		                 "CREATE TRIGGER Sessions_update_timestamp BEFORE UPDATE ON Sessions FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp();"}
		};
	}
	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getUsersDBAdapter() throws SQLException {
		return new SMSAppModulePostgreSQLAdapter(log, new String[][] {
			{"ResetTable",                "DELETE FROM Users"},
			{"AssertUserIsRegistered",    "SELECT * FROM AssertUserIsRegistered(${PHONE})"},
		});
	}

	public static JDBCAdapter getSessionsDBAdapter() throws SQLException {
		return new SMSAppModulePostgreSQLAdapter(log, new String[][] {
			{"ResetTable",          "DELETE FROM Sessions"},
			{"DeleteProperty",      "DELETE FROM Sessions WHERE userId=${USER_ID} AND propertyName=${PROPERTY_NAME}"},
			{"FetchProperties",     "SELECT propertyName, propertyValue FROM Sessions WHERE userId=${USER_ID}"},
			{"InsertProperty",      "INSERT INTO Sessions(userId, propertyName, propertyValue) VALUES (${USER_ID}, ${PROPERTY_NAME}, ${PROPERTY_VALUE})"},
			{"UpdateProperty",      "UPDATE Sessions SET propertyValue = ${PROPERTY_VALUE} WHERE userId=${USER_ID} AND propertyName=${PROPERTY_NAME}"},
		});
	}

}
