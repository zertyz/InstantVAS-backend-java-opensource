package main;

import java.sql.SQLException;

import adapters.DerbyEmbeddedAdapter;
import main.config.Configuration;

/** <pre>
 * DerbyEmbeddedAdapterConfiguration.java
 * ======================================
 * (created by luiz, Jan 18, 2016)
 *
 * Provides the needed {@link DerbyEmbeddedAdapter} configuration to access and operate on the database
 *
 * @version $Id$
 * @author luiz
*/

public class DerbyEmbeddedAdapterConfiguration extends DerbyEmbeddedAdapter {
	
	// TODO considerar refatorar o JDBCAdapter para rastrear não só a criação de tabelas, mas também de views, indices, procedures, schemas, triggers, etc, e assim permitir um 'clean database' mais facilmente
	
	
	private DerbyEmbeddedAdapterConfiguration(String[][] preparedProceduresDefinitions) throws SQLException {
		super(Configuration.log, preparedProceduresDefinitions);
	}

	@Override
	protected String[] getCredentials() {
		String hostname     = "--";
		String port         = "--";
		String databaseName = "/temp/tmp/DerbyDBSpikes";
		String user         = "--";
		String password     = "--";
		return new String[] {hostname, port, databaseName, user, password};
	}

	@Override
	protected String[][] getTableDefinitions() {
		return new String[][] {
			{"DerbyTestTable", "CREATE TABLE DerbyTestTable (id int, phone varchar(20))"},
		};
	}

	public static DerbyEmbeddedAdapter getDBAdapter() throws SQLException {
		return new DerbyEmbeddedAdapterConfiguration(new String[][] {
			{"InsertTestRecord",   "INSERT INTO DerbyTestTable VALUES (${ID}, ${PHONE})"},
			{"GetPhoneFromId",     "SELECT phone FROM DerbyTestTable WHERE id=${ID}"},
			{"UpdateTestRecord",   "UPDATE DerbyTestTable SET phone=${PHONE} WHERE id=${ID}"},
			{"DeleteTestRecord",   "DELETE FROM DerbyTestTable WHERE id=${ID}"},
		});
	}
	
	

}
