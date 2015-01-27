package adapters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.security.auth.login.Configuration;

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

	// driver configuration
	protected static String  CONNECTION_PROPERTIES = "characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true&connectTimeout=10000&socketTimeout=10000";


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
		if (true) return createDatabaseConnection();	// disable administrative connections
		String url = "jdbc:postgresql://" + HOSTNAME + ":"+PORT+"/?" +
		             CONNECTION_PROPERTIES; 

		System.out.println("PostgreSQLAdapter: Attempting to connect: "+url+" with USER='"+USER+"' and PASSWORD='"+PASSWORD+"'");
		return DriverManager.getConnection(url,
		                                   USER,
		                                   PASSWORD);
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