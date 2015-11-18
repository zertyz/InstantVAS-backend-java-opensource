package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.Instrumentation;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * SMSAppModulePostgreSQLAdapterChat.java
 * ======================================
 * (created by luiz, Sep 8, 2015)
 *
 * Provides {@link PostgreSQLAdapter}s to manipulate the "Hangman" module database & tables
 *
 * @see PostgreSQLAdapter
 * @version $Id$
 * @author luiz
 */

public class SMSAppModulePostgreSQLAdapterChat extends PostgreSQLAdapter {

	
	// the version information for database tables present on this class, to be stored on the 'Meta' table. Useful for future data conversions.
	private static String modelVersionForMetaTable = "2015.09.10";
	
	// configuration
	////////////////
	
	/** The application's instrumentation instance to be used to log PostgreSQL database events for the Profile Module */
	public static Instrumentation<?, ?> log;
	
	/** The table name that keeps track of 'moTexts' and 'moId's */
	private static String moTableName      = null;
	
	/** The field name, within 'moQueueTableName' that keeps track of 'moId's */
	private static String moIdFieldName   = null;
	
	/** The field name, within 'MoQueueTableName' that keeps track of 'moText's */
	private static String moTextFieldName = null;

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
	
	
	
	public static void configureChatDatabaseModule(String moTableName, String moIdFieldName, String moTextFieldName) {
		SMSAppModulePostgreSQLAdapterChat.moTableName     = moTableName;
		SMSAppModulePostgreSQLAdapterChat.moIdFieldName   = moIdFieldName;
		SMSAppModulePostgreSQLAdapterChat.moTextFieldName = moTextFieldName;
	}

	
	private SMSAppModulePostgreSQLAdapterChat(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
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
		String moIdReference = moTableName+"("+moIdFieldName+")";
		return new String[][] {
			{"PrivateMessages", "CREATE TABLE PrivateMessages(" +
			                    "moId             INTEGER   PRIMARY KEY REFERENCES "+moIdReference+" ON DELETE CASCADE, " +
			                    "senderUserId     INTEGER   NOT NULL REFERENCES Users(userId) ON DELETE CASCADE, " +
			                    "recipientUserId  INTEGER   NOT NULL REFERENCES Users(userId) ON DELETE CASCADE, " +
			                    "moTextStartIndex INTEGER   NOT NULL, " +
			                    "cts              TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" +
			                    
			                    // custom indexes
			                    "CREATE INDEX SenderUserIds    ON PrivateMessages(senderUserId);" +
			                    "CREATE INDEX RecipientUserIds ON PrivateMessages(recipientUserId);" +
			                    
				                // Meta record
				                "INSERT INTO Meta(tableName, modelVersion) VALUES ('PrivateMessages', '"+modelVersionForMetaTable+"_"+moIdReference+"')"},
		};
	}
	
	
	// public methods
	/////////////////
	
	public static JDBCAdapter getChatDBAdapter() throws SQLException {
		return new SMSAppModulePostgreSQLAdapterChat(log, new String[][] {
			{"ResetTable",             "TRUNCATE PrivateMessages CASCADE"},
			{"InsertPrivateMessage",   "INSERT INTO PrivateMessages(moId, senderUserId, recipientUserId, moTextStartIndex) " +
			                           "VALUES(${MO_ID}, ${SENDER_USER_ID}, ${RECIPIENT_USER_ID}, ${MO_TEXT_START_INDEX})"},
			{"SelectPeers",            "SELECT DISTINCT userId, phoneNumber FROM " +
			                           "(SELECT senderUserId    AS userId, phoneNumber, moId FROM PrivateMessages, Users WHERE PrivateMessages.recipientUserId=${USER_ID} AND Users.userId=${USER_ID} UNION " +
			                           " SELECT recipientUserId AS userId, phoneNumber, moId FROM PrivateMessages, Users WHERE PrivateMessages.senderUserId=${USER_ID}    AND Users.userId=${USER_ID} ORDER BY moId ASC) uq"},
			{"SelectPrivateMessages",  "SELECT pm.moId, pm.senderUserID, su.phoneNumber AS senderPhoneNumber, pm.recipientUserID, ru.phoneNumber AS recipientPhoneNumber, SUBSTRING("+moTableName+"."+moTextFieldName+" FROM pm.moTextStartIndex+1) AS message FROM PrivateMessages pm, Users su, Users ru, "+moTableName+" WHERE " +
			                           "((pm.senderUserId=${USER1_ID} AND pm.recipientUserId=${USER2_ID}) OR " +
			                           " (pm.senderUserId=${USER2_ID} AND pm.recipientUserId=${USER1_ID})) AND " +
			                           "su.userId=pm.senderUserId AND ru.userId=pm.recipientUserId AND pm.moId = "+moTableName+"."+moIdFieldName+" ORDER BY pm.moId ASC"},
		});
	}

}
