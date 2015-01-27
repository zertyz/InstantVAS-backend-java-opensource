package main;

import java.sql.SQLException;

import main.config.Configuration;
import adapters.MySQLAdapter;

/**
 * MysqlAdapterConfiguration.java  --  $Id: MySQLHelperConfiguration.java,v 1.1 2010/07/01 22:03:06 luiz Exp $
 * ==============================
 * (created by luiz, Dec 22, 2008)
 *
 * Provides the needed 'MySQLAdapter' configuration to access and operate on the database
 */

public class MySQLAdapterConfiguration extends MySQLAdapter {
	 
	
	// MySQLAdapter section
	///////////////////////
	
	static {
		MySQLAdapter.DEBUG_QUERIES = true;
	}

	private MySQLAdapterConfiguration(String[][] preparedProceduresDefinitions) throws SQLException {
		super(Configuration.log, preparedProceduresDefinitions);
	}

	@Override
	protected String[] getCredentials() {
		String hostname     = "192.168.0.3";
		String port         = "3306";
		String databaseName = "MysqlHelperTester";
		String user         = "root";
		String password     = "";
		return new String[] {hostname, port, databaseName, user, password};
	}

	@Override
	protected String[][] getTableDefinitions() {
		return new String[][] {
			{"SimpleTable", "CREATE TABLE SimpleTable (id int, phone char(20));"},
			{"NotSoSimple", "CREATE TABLE NotSoSimple (id int NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
			                "phone char(20) UNIQUE);"},
		};
	}
	
	
	// public access methods
	////////////////////////
	
	public static MySQLAdapter getDBAdapter() throws SQLException {
		return new MySQLAdapterConfiguration(new String[][] {
				{"InsertSimpleRecord",      "INSERT INTO SimpleTable VALUES (${ID}, ${PHONE})"},
				{"GetSimpleIdFromPhone",    "SELECT phone FROM SimpleTable WHERE id=${ID}"},
				{"DeleteSimpleRecord",      "DELETE FROM SimpleTable WHERE id=${ID}"},
				{"InsertNotSoSimpleRecord", "INSERT INTO NotSoSimple VALUES (${PHONE})"},
			});
	}

}