package adapters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.Instrumentation;

/** <pre>
 * PostgresSQLAdapter.java
 * =======================
 * (created by luiz, Jan 26, 2015)
 *
 * Specializes 'JDBCHelper' to deal with the particularities of the PostgreSQL database
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public abstract class PostgreSQLAdapter extends JDBCAdapter {

	// configuration
	////////////////
	
	@ConfigurableElement("Additional URL parameters for PostgreSQL JDBC driver connection properties")
	public static String  CONNECTION_PROPERTIES = "characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true&connectTimeout=10000&socketTimeout=10000";


	public PostgreSQLAdapter(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
		super(log, new org.postgresql.Driver().getClass(), preparedProceduresDefinitions);
	}
	
	@Override
	protected String getShowTablesCommand() {
		return "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';";
	};

	@Override
	protected String getShowDatabasesCommand() {
		return "SELECT datname FROM pg_database;";
	}
	
	@Override
	/** For postgreSQL, a different strategy is used to "drop" the database -- drop all tables instead */
	protected String getDropDatabaseCommand() {
		String statements = "";		
		String[][] tableDefinitions = getTableDefinitions();
		
		for (String[] tableDefinition : tableDefinitions) {
			String databaseName = tableDefinition[0];
			statements += "DROP TABLE " + databaseName + ";";
		}
		return statements;
	}

	@Override
	protected Connection createAdministrativeConnection() throws SQLException {
		return createDatabaseConnection();	// it seems it is impossible to connect to PostgreSQL without a database
	}

	@Override
	protected Connection createDatabaseConnection() throws SQLException {
		String url = "jdbc:postgresql://" + HOSTNAME + ":"+PORT+"/" +
		             DATABASE_NAME + "?" + CONNECTION_PROPERTIES; 

		return DriverManager.getConnection(url,
		                                   USER,
		                                   PASSWORD);
	}
}