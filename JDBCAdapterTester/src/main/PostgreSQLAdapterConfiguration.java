package main;

import java.sql.SQLException;

import main.config.Configuration;
import adapters.MySQLAdapter;
import adapters.PostgreSQLAdapter;

/** <pre>
 * PostgreSQLAdapterConfiguration.java
 * ===================================
 * (created by luiz, Jan 26, 2015)
 *
 * Provides the needed 'PostgreSQLAdapter' configuration to access and operate on the database
 *
 * @see PostgreSQLAdapter
 * @version $Id$
 * @author luiz
 */

public class PostgreSQLAdapterConfiguration extends PostgreSQLAdapter {
	 
	
	// PostgreSQLAdapter section
	////////////////////////////
	
	static {
		MySQLAdapter.SHOULD_DEBUG_QUERIES = true;
	}

	private PostgreSQLAdapterConfiguration(String[][] preparedProceduresDefinitions) throws SQLException {
		super(Configuration.log, preparedProceduresDefinitions);
	}

	@Override
	protected String[] getCredentials() {
		String hostname     = "venus";
		String port         = "5432";
		String databaseName = "hangman";
		String user         = "hangman";
		String password     = "hangman";
		return new String[] {hostname, port, databaseName, user, password};
	}

	@Override
	protected String[][] getTableDefinitions() {
		return new String[][] {
			{"SimpleTable", "CREATE TABLE SimpleTable (id int, phone char(20));"},
			{"NotSoSimple", "CREATE TABLE NotSoSimple (id Serial NOT NULL PRIMARY KEY, " +
			                "phone char(20) UNIQUE);"},
		};
	}
	
	
	// public access methods
	////////////////////////
	
	public static PostgreSQLAdapter getDBAdapter() throws SQLException {
		return new PostgreSQLAdapterConfiguration(new String[][] {
			{"InsertSimpleRecord",      "INSERT INTO SimpleTable VALUES (${ID}, ${PHONE})"},
			{"GetSimpleIdFromPhone",    "SELECT phone FROM SimpleTable WHERE id=${ID}"},
			{"DeleteSimpleRecord",      "DELETE FROM SimpleTable WHERE id=${ID}"},
			{"InsertNotSoSimpleRecord", "INSERT INTO NotSoSimple VALUES (${PHONE})"},
		});
	}
}
