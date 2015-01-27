package adapters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;

/** <pre>
 * SQLServerAdapter.java  --  $Id: SQLServerHelper.java,v 1.1 2010/07/01 22:02:14 luiz Exp $
 * =====================
 * (created by luiz, Jun 29, 2010)
 *
 * Specializes 'JDBCHelper' to deal with the particularities of the SQL Server database
 * and JTDS JDBC Driver
 */

public abstract class SQLServerAdapter extends JDBCAdapter {


	protected SQLServerAdapter(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
		super(log, new net.sourceforge.jtds.jdbc.Driver().getClass(), preparedProceduresDefinitions);
	}
	
	@Override
	protected String getShowTablesCommand() {
		return null;
	}
	
	@Override
	protected String getShowDatabasesCommand() {
		return null;
	}
	
	@Override
	protected String getDropDatabaseCommand() {
		return null;
	}

	@Override
	protected Connection createAdministrativeConnection() throws SQLException {
		return null;	// disable administration. The driver seems not to allow us to operate on creating / dropping databases from the sql client
	}

	@Override
	protected Connection createDatabaseConnection() throws SQLException {
		String url = "jdbc:jtds:sqlserver://" + HOSTNAME + ":"+PORT+"/" +
		             DATABASE_NAME; 

		return DriverManager.getConnection(url,
		                                   USER,
		                                   PASSWORD);
	}

}
