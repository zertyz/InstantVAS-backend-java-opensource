package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.Instrumentation;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * SMSAppModulePostgreSQLAdapterSubscription.java
 * ==============================================
 * (created by luiz, Jul 27, 2015)
 *
 * Provides {@link PostgreSQLAdapter}s to manipulate the "Subscription" module database & tables
 *
 * @see PostgreSQLAdapter
 * @version $Id$
 * @author luiz
 */

public class SMSAppModulePostgreSQLAdapterSubscription extends PostgreSQLAdapter {

	
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
	
	
	public static void configureSubscriptionDatabaseModule(Instrumentation<?, ?> log,
	                                                       String hostname, int port, String database, String user, String password) {

		SMSAppModulePostgreSQLAdapterSubscription.log = log;
		
		SMSAppModulePostgreSQLAdapterSubscription.hostname = hostname;
		SMSAppModulePostgreSQLAdapterSubscription.port     = port;
		SMSAppModulePostgreSQLAdapterSubscription.database = database;
		SMSAppModulePostgreSQLAdapterSubscription.user     = user;
		SMSAppModulePostgreSQLAdapterSubscription.password = password;
	}

	
	private SMSAppModulePostgreSQLAdapterSubscription(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
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
			{"Subscriptions", "CREATE TABLE Subscriptions(" +
			                  "userId                INTEGER     PRIMARY KEY REFERENCES Users(userId) ON DELETE CASCADE," +
			                  "isSubscribed          BOOLEAN     NOT NULL," +
			                  "lastBilling           TIMESTAMP   DEFAULT NULL," +
			                  "subscriptionChannel   TEXT        DEFAULT NULL," +
			                  "unsubscriptionChannel TEXT        DEFAULT NULL," +
			                  "cts                   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP," +
			                  "uts                   TIMESTAMP   DEFAULT NULL);" +
			                  
			                  // trigger for 'uts' -- the updated timestamp
			                  "CREATE TRIGGER Subscriptions_update_timestamp BEFORE UPDATE ON Subscriptions FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp();" +
			            
			                  // stored procedure
			                  "CREATE OR REPLACE FUNCTION AssertSubscribed(p_userId INTEGER, p_channel TEXT) RETURNS VOID AS $$\n" + 
			                  "BEGIN\n" + 
			                  "    UPDATE Subscriptions SET (isSubscribed, lastBilling, subscriptionChannel, unsubscriptionChannel) = (TRUE, NOW(), p_channel, NULL) WHERE userId=p_userId; \n" + 
			                  "    IF NOT FOUND THEN \n" + 
			                  "        INSERT INTO Subscriptions(userId, isSubscribed, lastBilling, subscriptionChannel, unsubscriptionChannel) VALUES (p_userId, TRUE,  NOW(), p_channel, NULL); \n" + 
			                  "    END IF;\n" + 
			                  "END;\n" + 
			                  "$$ LANGUAGE plpgsql;\n" +
			                  
			                  // stored procedure
			                  "CREATE OR REPLACE FUNCTION AssertUnsubscribed(p_userId INTEGER, p_channel TEXT) RETURNS VOID AS $$\n" + 
			                  "BEGIN\n" + 
			                  "    UPDATE Subscriptions SET (isSubscribed, subscriptionChannel, unsubscriptionChannel) = (FALSE, NULL, p_channel) WHERE userId=p_userId; \n" + 
			                  "    IF NOT FOUND THEN \n" + 
			                  "        INSERT INTO Subscriptions(userId, isSubscribed, subscriptionChannel, unsubscriptionChannel) VALUES (p_userId, FALSE, p_channel, NULL); \n" + 
			                  "    END IF;\n" + 
			                  "END;\n" + 
			                  "$$ LANGUAGE plpgsql;" +
					          
					          // Meta record
					          "INSERT INTO Meta(tableName, modelVersion) VALUES ('Subscriptions', '"+modelVersionForMetaTable+"')"},
		};
	}
	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getSubscriptionDBAdapter() throws SQLException {
		return new SMSAppModulePostgreSQLAdapterSubscription(log, new String[][] {
			{"ResetTable",                  "TRUNCATE Subscriptions CASCADE"},
			{"SelectSubscriptionByUser",    "SELECT userId, isSubscribed, lastBilling, subscriptionChannel, unsubscriptionChannel FROM Subscriptions WHERE userId=${USER_ID}"},
			{"AssertSubscribed",            "SELECT * FROM AssertSubscribed(${USER_ID}, ${CHANNEL})"},
			{"AssertUnsubscribed",          "SELECT * FROM AssertUnsubscribed(${USER_ID}, ${CHANNEL})"},
		});
	}
}
