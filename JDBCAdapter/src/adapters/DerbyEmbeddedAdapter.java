package adapters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.Instrumentation;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** <pre>
 * DerbyEmbeddedAdapter.java
 * =========================
 * (created by luiz, Jan 14, 2016)
 *
 * Specializes {@link JDBCAdapter} to deal with the peculiarities of the PostgreSQL database and it's JDBC driver
 *
 * @version $Id$
 * @author luiz
 */

public abstract class DerbyEmbeddedAdapter extends JDBCAdapter {

	
	// configuration
	////////////////
	
	@ConfigurableElement("Additional URL parameters for PostgreSQL JDBC driver connection properties")
	public static String  CONNECTION_PROPERTIES = "";
	@ConfigurableElement("Indicates whether or not to perform needed administrative tasks, such as database creation")
	public static boolean ALLOW_DATABASE_ADMINISTRATION = true;
	
	// for the shutdown hook
	private static DerbyEmbeddedAdapter firstInstance = null;
	
	
	public DerbyEmbeddedAdapter(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws SQLException {
		super(log, new org.apache.derby.jdbc.EmbeddedDriver().getClass(), preparedProceduresDefinitions);
		// register the shutdown hook
		if (firstInstance == null) {
			firstInstance = this;
			addDerbyShutdownHook();
		}
	}

	@Override
	protected Connection createAdministrativeConnection() throws SQLException {
		if (ALLOW_DATABASE_ADMINISTRATION) {
			return createDatabaseConnection();
		} else {
			return null;
		}
	}

	@Override
	protected Connection createDatabaseConnection() throws SQLException {
		String url = "jdbc:derby:" + DATABASE_NAME + ";" + (ALLOW_DATABASE_ADMINISTRATION ? "create=true":"") + ";" +
		             CONNECTION_PROPERTIES;
	
		return DriverManager.getConnection(url);
	}

	@Override
	protected String getShowTablesCommand() {
		return "select TABLENAME, SCHEMANAME from SYS.SYSTABLES,SYS.SYSSCHEMAS where TABLETYPE='T' and SYS.SYSTABLES.SCHEMAID=SYS.SYSSCHEMAS.SCHEMAID";
	}

	@Override
	protected String getShowDatabasesCommand() {
		return "SELECT database FROM (values ('"+DATABASE_NAME+"')) as x(database)";
	}

	@Override
	protected String[] getDropDatabaseCommand() {
		// to clean a database, these commands should be executed https://db.apache.org/derby/docs/10.0/manuals/reference/sqlj28.html
		// and their data may be gathered by the following sys tables:
		// SYS.SYSVIEWS
		// SYS.SYSTABLES;
		// SYS.SYSSCHEMAS;
		// SYS.SYSTRIGGERS;
		// select username from sys.sysusers
		// select roleid from sys.sysroles where cast(isdef as char(1)) = 'Y'" --> DROP ROLE ?;
		// ... and so on. continue from http://svn.apache.org/viewvc/db/derby/code/trunk/java/testing/org/apache/derbyTesting/junit/CleanDatabaseTestSetup.java?view=markup
		try {
			ArrayList<String> dropCommands = new ArrayList<String>();
			Connection conn = createAdministrativeConnection();
			
			// drop tables
			for (Object[] tableAndSchema : getArrayFromQueryExecution(conn, "select TABLENAME, SCHEMANAME from SYS.SYSTABLES,SYS.SYSSCHEMAS where TABLETYPE='T' and SYS.SYSTABLES.SCHEMAID=SYS.SYSSCHEMAS.SCHEMAID")) {
				String tableName  = (String)tableAndSchema[0];
				String schemaName = (String)tableAndSchema[1];
				dropCommands.add("DROP TABLE " + schemaName + "." + tableName);
			}
			
			// drop schemas
			// "select SCHEMANAME from SYS.SYSSCHEMAS where SCHEMANAME=AUTHORIZATIONID"
			
			conn.close();
			return dropCommands.toArray(new String[dropCommands.size()]);
		} catch (SQLException e) {
			log.reportThrowable(e, "Error while assembling the drop database command set");
			return null;
		}
		
		
	}
	
	/** Method to safely disconnect from the local database prior to VM shutdown. Possibly this may be included in the JDBCAdapter? */
	private void addDerbyShutdownHook() throws SQLException {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("JVM shutdown request detected. Shuttingdown Derby...");
					DriverManager.getConnection("jdbc:derby:;shutdown=true");
				} catch (SQLException e) {
					System.out.println("Successfully shutdown Derby");
				}
			}
		});
	}
}
