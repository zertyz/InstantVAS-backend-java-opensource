package adapters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;

/** <pre>
 * MySQLAdapter.java  --  $Id: MySQLHelper.java,v 1.1 2010/07/01 22:02:14 luiz Exp $
 * =================
 * (created by luiz, Jun 29, 2010)
 *
 * Specializes 'JDBCHelper' to deal with the peculiarities of the MySQL database
 * and MySQL JDBC Driver
 */

public abstract class MySQLAdapter extends JDBCAdapter {

	// driver configuration
	protected static String  CONNECTION_PROPERTIES = "characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true&connectTimeout=10000&socketTimeout=10000";


	protected MySQLAdapter(Instrumentation<?, ?> log, String[][] prepared_procedures_definitions) throws SQLException {
		super(log, com.mysql.jdbc.Driver.class, prepared_procedures_definitions);
	}
	
	@Override
	protected String getShowTablesCommand() {
		return "show tables;";
	}
	
	@Override
	protected String getShowDatabasesCommand() {
		return "show databases;";
	}

	@Override
	protected String[] getDropDatabaseCommand() {
		return new String[] {"DROP DATABASE " + DATABASE_NAME};
	}

	@Override
	protected Connection createAdministrativeConnection() throws SQLException {
		String url = "jdbc:mysql://" + HOSTNAME + ":"+PORT+"/?" +
		             CONNECTION_PROPERTIES; 

		return DriverManager.getConnection(url,
		                                   USER,
		                                   PASSWORD);
	}

	@Override
	protected Connection createDatabaseConnection() throws SQLException {
		String url = "jdbc:mysql://" + HOSTNAME + ":"+PORT+"/" +
		             DATABASE_NAME + "?" + CONNECTION_PROPERTIES; 

		return DriverManager.getConnection(url,
		                                   USER,
		                                   PASSWORD);
	}


}
