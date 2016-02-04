package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;
import adapters.AbstractPreparedProcedure;
import adapters.IJDBCAdapterParameterDefinition;
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
	
	// Mutua Configurable Class pattern
	///////////////////////////////////

	/** this class' singleton instance */
	private static SMSAppModulePostgreSQLAdapterSubscription instance = null;

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

		instance = new SMSAppModulePostgreSQLAdapterSubscription();	// start/restart the singleton with the new settings
	}

	
	private SMSAppModulePostgreSQLAdapterSubscription() throws SQLException {
		super(LOG, ALLOW_DATA_STRUCTURES_ASSERTION, SHOULD_DEBUG_QUERIES, HOSTNAME, PORT, DATABASE, USER, PASSWORD);
	}

	@Override
	protected String[][] getTableDefinitions() {
		return new String[][] {
			{"Subscriptions", "CREATE TABLE Subscriptions(" +
			                  "userId                INTEGER     PRIMARY KEY REFERENCES Users(userId) ON DELETE CASCADE," +
			                  "isSubscribed          BOOLEAN     NOT NULL," +
			                  "lastBilling           TIMESTAMP   DEFAULT NULL," +
			                  "subscriptionChannel   TEXT        DEFAULT NULL," +
			                  "unsubscriptionChannel TEXT        DEFAULT NULL," +
			                  "cts                   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP," +
			                  "uts                   TIMESTAMP   DEFAULT NULL)",
			                  
			                  // trigger for 'uts' -- the updated timestamp
			                  "CREATE TRIGGER Subscriptions_update_timestamp BEFORE UPDATE ON Subscriptions FOR EACH ROW EXECUTE PROCEDURE set_updated_timestamp()",
			            
			                  // stored procedure
			                  "CREATE OR REPLACE FUNCTION AssertSubscribed(p_userId INTEGER, p_channel TEXT) RETURNS VOID AS $$\n" + 
			                  "BEGIN\n" + 
			                  "    UPDATE Subscriptions SET (isSubscribed, lastBilling, subscriptionChannel, unsubscriptionChannel) = (TRUE, NOW(), p_channel, NULL) WHERE userId=p_userId; \n" + 
			                  "    IF NOT FOUND THEN \n" + 
			                  "        INSERT INTO Subscriptions(userId, isSubscribed, lastBilling, subscriptionChannel, unsubscriptionChannel) VALUES (p_userId, TRUE,  NOW(), p_channel, NULL); \n" + 
			                  "    END IF;\n" + 
			                  "END;\n" + 
			                  "$$ LANGUAGE plpgsql",
			                  
			                  // stored procedure
			                  "CREATE OR REPLACE FUNCTION AssertUnsubscribed(p_userId INTEGER, p_channel TEXT) RETURNS VOID AS $$\n" + 
			                  "BEGIN\n" + 
			                  "    UPDATE Subscriptions SET (isSubscribed, subscriptionChannel, unsubscriptionChannel) = (FALSE, NULL, p_channel) WHERE userId=p_userId; \n" + 
			                  "    IF NOT FOUND THEN \n" + 
			                  "        INSERT INTO Subscriptions(userId, isSubscribed, subscriptionChannel, unsubscriptionChannel) VALUES (p_userId, FALSE, p_channel, NULL); \n" + 
			                  "    END IF;\n" + 
			                  "END;\n" + 
			                  "$$ LANGUAGE plpgsql",
					          
					          // Meta record
					          "INSERT INTO Meta(tableName, modelVersion) VALUES ('Subscriptions', '"+modelVersionForMetaTable+"')"},
		};
	}
	
	/***************
	** PARAMETERS **
	***************/
	
	public enum Parameters implements IJDBCAdapterParameterDefinition {

		USER_ID,
		CHANNEL,
		
		;
		
		@Override
		public String getParameterName() {
			return name();
		}
	}
	
	/***************
	** STATEMENTS **
	***************/

	public static final class SubscriptionDBStatements {
		/** Zero the table contents -- for testing purposes only */
		public final static AbstractPreparedProcedure ResetTable = new AbstractPreparedProcedure(
			"TRUNCATE Subscriptions CASCADE");
		/** Returns if the 'USER_ID' is or isn't subscribed -- and all channels, billings & etc available */
		public final static AbstractPreparedProcedure SelectSubscriptionByUser = new AbstractPreparedProcedure(
			"SELECT userId, isSubscribed, lastBilling, subscriptionChannel, unsubscriptionChannel FROM Subscriptions WHERE userId=",Parameters.USER_ID);
		/** Executes the 'AssertSubscribed' stored procedure, which assures 'USER_ID' is marked as subscribed via 'CHANNEL' */
		public final static AbstractPreparedProcedure AssertSubscribed = new AbstractPreparedProcedure(
			"SELECT * FROM AssertSubscribed(",Parameters.USER_ID,", ",Parameters.CHANNEL,")");
		/** Executes the 'AssertUnsubscribed' stored procedure, which assures 'USER_ID's subscription has been cancelled via 'CHANNEL' */
		public final static AbstractPreparedProcedure AssertUnsubscribed = new AbstractPreparedProcedure(
			"SELECT * FROM AssertUnsubscribed(",Parameters.USER_ID,", ",Parameters.CHANNEL,")");
	}
	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getInstance() {
		if (instance == null) {
			throw new RuntimeException("Class '" + SMSAppModulePostgreSQLAdapterSubscription.class.getCanonicalName() + "' was not configured according to the " +
			                           "'Mutua Configurable Class' pattern -- a preliminar call to 'configureDefaultValuesForNewInstances' " +
			                           "was not made.");
		}
		return instance;
	}
	
	public static JDBCAdapter getSubscriptionDBAdapter() throws SQLException {
		return getInstance();
	}
}
