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
			                "phone char(20) UNIQUE);\n"+
"CREATE OR REPLACE FUNCTION somefunc() RETURNS integer AS $$\n"+
"DECLARE\n"+
"    quantity integer := 30;\n"+
"BEGIN\n"+
"    RAISE NOTICE 'Quantity here is %', quantity;  -- Quantity here is 30\n"+
"    quantity := 50;\n"+
"    --\n"+
"    -- Create a subblock\n"+
"    --\n"+
"    DECLARE\n"+
"        quantity integer := 80;\n"+
"    BEGIN\n"+
"        RAISE NOTICE 'Quantity here is %', quantity;  -- Quantity here is 80\n"+
"    END;\n"+
"    \n"+
"    RAISE NOTICE 'Quantity here is %', quantity;  -- Quantity here is 50\n"+
"\n"+
"    RETURN quantity;\n"+
"END;\n"+
"$$ LANGUAGE plpgsql;\n"+

"CREATE OR REPLACE FUNCTION UpdateOrInsertNotSoSimple(new_phone CHAR(20), new_id int) RETURNS void AS $$\n"+
"BEGIN\n"+
"UPDATE NotSoSimple SET phone=new_phone WHERE id=new_id;\n"+
"IF NOT FOUND THEN \n"+
"INSERT INTO NotSoSimple(phone) VALUES (new_phone);\n"+
"END IF;\n"+
"END;\n"+
"$$ LANGUAGE plpgsql;\n"

			},
		};
	}
	
	
	// public access methods
	////////////////////////
	
	public static PostgreSQLAdapter getDBAdapter() throws SQLException {
		return new PostgreSQLAdapterConfiguration(new String[][] {
			{"InsertSimpleRecord",      "INSERT INTO SimpleTable VALUES (${ID}, ${PHONE})"},
			{"GetSimpleIdFromPhone",    "SELECT phone FROM SimpleTable WHERE id=${ID}"},
			{"DeleteSimpleRecord",      "DELETE FROM SimpleTable WHERE id=${ID}"},
			{"InsertNotSoSimpleRecord", "INSERT INTO NotSoSimple(phone) VALUES (${PHONE})"},
			{"NoParamStoredProcedure",  "SELECT * FROM somefunc()"},
			{"ParamStoredProcedure",    "SELECT FROM UpdateOrInsertNotSoSimple(${PHONE}, ${ID})"},
		});
	}
}
