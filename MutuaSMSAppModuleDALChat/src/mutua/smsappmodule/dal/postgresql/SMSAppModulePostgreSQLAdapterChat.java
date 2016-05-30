package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;

import adapters.AbstractPreparedProcedure;
import adapters.IJDBCAdapterParameterDefinition;
import adapters.JDBCAdapter;
import adapters.JDBCAdapterInstrumentationMethods;
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
	private static final String modelVersionForMetaTable = "2015.09.10";
	
	// Mutua Configurable Class pattern
	///////////////////////////////////
	
	/** this class' singleton instance */
	private static SMSAppModulePostgreSQLAdapterChat instance = null;
	
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
	/** The text field name, within the MO table */
	private static String MO_TEXT_FIELD_NAME;
	
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
	 *  @param moTextFieldName              see {@link #MO_Text_FIELD_NAME} */
	public static void configureDefaultValuesForNewInstances(
		boolean allowDataStructuresAssertion, boolean shouldDebugQueries,
	    String hostname, int port, String database, String user, String password,
	    String moTableName, String moIdFieldName, String moTextFieldName) throws SQLException {

		ALLOW_DATA_STRUCTURES_ASSERTION = allowDataStructuresAssertion;
		SHOULD_DEBUG_QUERIES            = shouldDebugQueries;
		HOSTNAME = hostname;
		PORT     = port;
		DATABASE = database;
		USER     = user;
		PASSWORD = password;
		
		MO_TABLE_NAME      = moTableName;
		MO_ID_FIELD_NAME   = moIdFieldName;
		MO_TEXT_FIELD_NAME = moTextFieldName;

		instance = null;
	}
	
	@Override
	protected String[][] getTableDefinitions() {
		String moIdReference = moTableName+"("+moIdFieldName+")";
		return new String[][] {
			{"PrivateMessages", "CREATE TABLE PrivateMessages(" +
			                    "moId             INTEGER   PRIMARY KEY REFERENCES "+moIdReference+" ON DELETE CASCADE, " +
			                    "senderUserId     INTEGER   NOT NULL REFERENCES Users(userId) ON DELETE CASCADE, " +
			                    "recipientUserId  INTEGER   NOT NULL REFERENCES Users(userId) ON DELETE CASCADE, " +
			                    "moTextStartIndex INTEGER   NOT NULL, " +
			                    "cts              TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
			                    
			                    // custom indexes
			                    "CREATE INDEX SenderUserIds    ON PrivateMessages(senderUserId)",
			                    "CREATE INDEX RecipientUserIds ON PrivateMessages(recipientUserId)",
			                    
				                // Meta record
				                "INSERT INTO Meta(tableName, modelVersion) VALUES ('PrivateMessages', '"+modelVersionForMetaTable+"_"+moIdReference+"')"},
		};
	}
	
	
	/***************
	** PARAMETERS **
	***************/
	
	public enum Parameters implements IJDBCAdapterParameterDefinition {

		MO_ID,
		USER_ID,
		SENDER_USER_ID,
		RECIPIENT_USER_ID,
		MO_TEXT_START_INDEX,
		USER1_ID,
		USER2_ID,
		
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
	public final AbstractPreparedProcedure ResetTables;
	/** Inserts a conversation record on the 'PrivateMessages' table */
	public final AbstractPreparedProcedure InsertPrivateMessage;
	/** Returns all userIds and phoneNumbers for every user who had ever exchanged a message with 'USER_ID' */
	public final AbstractPreparedProcedure SelectPeers;
	/** Retrieves all private messages exchanged between 'USER1_ID' and 'USER2_ID' */
	public final AbstractPreparedProcedure SelectPrivateMessages;
	
	private final String moTableName;
	private final String moIdFieldName;
	private final String moTextFieldName;
	private SMSAppModulePostgreSQLAdapterChat() throws SQLException {
		super(false, SHOULD_DEBUG_QUERIES, HOSTNAME, PORT, DATABASE, USER, PASSWORD);
		this.moTableName     = MO_TABLE_NAME;
		this.moIdFieldName   = MO_ID_FIELD_NAME;
		this.moTextFieldName = MO_TEXT_FIELD_NAME;
		// the execution of the following method was delayed by invoking the super constructor with 'false' in order for the fields
		// needed by 'getTableDefinitions' to be set
		if (ALLOW_DATA_STRUCTURES_ASSERTION) {
			JDBCAdapterInstrumentationMethods.reportAdministrationWarningMessage("WARNING: executing delayed 'assureDataStructures' for '"+getClass().getName()+"'");
			assureDataStructures();
		}
		
		ResetTables           = new AbstractPreparedProcedure(connectionPool,
			"TRUNCATE PrivateMessages CASCADE");
		InsertPrivateMessage  = new AbstractPreparedProcedure(connectionPool,
			"INSERT INTO PrivateMessages(moId, senderUserId, recipientUserId, moTextStartIndex) ",
		                                                      "VALUES(",Parameters.MO_ID,", ",Parameters.SENDER_USER_ID,", ",Parameters.RECIPIENT_USER_ID,", ",Parameters.MO_TEXT_START_INDEX,")");
		SelectPeers           = new AbstractPreparedProcedure(connectionPool,
			"SELECT DISTINCT userId, phoneNumber FROM ",
		    "(SELECT senderUserId    AS userId, phoneNumber, moId FROM PrivateMessages, Users WHERE PrivateMessages.recipientUserId=",
		    Parameters.USER_ID," AND Users.userId=",Parameters.USER_ID," UNION ",
		    " SELECT recipientUserId AS userId, phoneNumber, moId FROM PrivateMessages, Users WHERE PrivateMessages.senderUserId=",Parameters.USER_ID,
		    " AND Users.userId=",Parameters.USER_ID," ORDER BY moId ASC) uq");
		SelectPrivateMessages = new AbstractPreparedProcedure(connectionPool,
			"SELECT pm.moId, pm.senderUserID, su.phoneNumber AS senderPhoneNumber, pm.recipientUserID, ru.phoneNumber AS recipientPhoneNumber, SUBSTRING(",
		    moTableName,".",moTextFieldName," FROM pm.moTextStartIndex+1) AS message FROM PrivateMessages pm, Users su, Users ru, ",moTableName," WHERE ",
		    "((pm.senderUserId=",Parameters.USER1_ID," AND pm.recipientUserId=",Parameters.USER2_ID,") OR ",
		    " (pm.senderUserId=",Parameters.USER2_ID," AND pm.recipientUserId=",Parameters.USER1_ID,")) AND ",
		    "su.userId=pm.senderUserId AND ru.userId=pm.recipientUserId AND pm.moId = ",moTableName,".",moIdFieldName," ORDER BY pm.moId ASC");
	}

	
	// public methods
	/////////////////
	
	public static SMSAppModulePostgreSQLAdapterChat getInstance() throws SQLException {
		if (instance == null) {
			instance = new SMSAppModulePostgreSQLAdapterChat();
//			throw new RuntimeException("Class '" + SMSAppModulePostgreSQLAdapterChat.class.getCanonicalName() + "' was not configured according to the " +
//			                           "'SMSAppModulePostgreSQLAdapterHangman' pattern -- a preliminar call to 'configureDefaultValuesForNewInstances' " +
//			                           "was not made.");
		}
		return instance;
	}
	
	public static SMSAppModulePostgreSQLAdapterChat getChatDBAdapter() throws SQLException {
		return getInstance();
	}

}