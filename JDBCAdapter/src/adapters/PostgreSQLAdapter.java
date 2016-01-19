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
 * Specializes {@link JDBCAdapter} to deal with the peculiarities of the PostgreSQL database and it's JDBC driver
 *
 * @version $Id$
 * @author luiz
 */

public abstract class PostgreSQLAdapter extends JDBCAdapter {

	
	// configuration
	////////////////
	
	@ConfigurableElement("Additional URL parameters for PostgreSQL JDBC driver connection properties")
	public static String  CONNECTION_PROPERTIES = "charSet=UTF8&tcpKeepAlive=true&connectTimeout=30&loginTimeout=30&socketTimeout=300";
	@ConfigurableElement("Indicates whether or not to perform needed administrative tasks, such as database creation")
	public static boolean ALLOW_DATABASE_ADMINISTRATION = true;


	public PostgreSQLAdapter(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
		super(log, new org.postgresql.Driver().getClass(), preparedProceduresDefinitions);
	}
	
	@Override
	protected String getShowTablesCommand() {
		return "SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname != 'pg_catalog' AND schemaname != 'information_schema' AND tableowner='"+USER+"';";
	};

	@Override
	protected String getShowDatabasesCommand() {
		return "SELECT datname FROM pg_database;";
	}
	
	@Override
	/** For postgreSQL, a different strategy is used to "drop" the database -- drop all tables instead */
	protected String[] getDropDatabaseCommand() {
		String[][] tableDefinitions = getTableDefinitions();
		
		String[] statements = new String[tableDefinitions.length];
		int i = 0;
		for (String[] tableDefinition : tableDefinitions) {
			String databaseName = tableDefinition[0];
			statements[i++] = "DROP TABLE " + databaseName + " CASCADE";
		}
		return statements;
	}

	@Override
	protected Connection createAdministrativeConnection() throws SQLException {
		if (ALLOW_DATABASE_ADMINISTRATION) {
			return createDatabaseConnection();	// it seems it is impossible to connect to PostgreSQL without a database
		} else {
			return null;
		}
	}

	@Override
	protected Connection createDatabaseConnection() throws SQLException {
		String url = "jdbc:postgresql://" + HOSTNAME + ":"+PORT+"/" +
		             DATABASE_NAME + "?" + CONNECTION_PROPERTIES; 

		return DriverManager.getConnection(url,
		                                   USER,
		                                   PASSWORD);
	}
	
	
	// helper methods
	/////////////////
	
	protected static String list(Object[] stringArray, String quote, String separator) {
		StringBuffer sb = new StringBuffer();
		for (Object element : stringArray) {
			sb.append(quote).append(element.toString()).append(quote).append(separator);
		}
		// remove the last 'separator'
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

}