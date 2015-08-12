package main;

import java.sql.SQLException;

import static main.config.Configuration.log;

/* Main.java  --  $Id: Main.java,v 1.1 2010/07/01 22:03:06 luiz Exp $
 * =========
 * (created by luiz, Dec 15, 2008)
 *
 * Some 'MysqlHelper' spikes
 */

public class Main {

	public static void main(String[] args) {

		try {
			
			// test MySQL conectivity
			//MySQLTester.mysqltesterMain(args);
			
			// test SQL Server conectivity
			//SQLServerTester.sqlserverMain(args);
			
			PostgreSQLTester.postgreSQLTesterMain(args);
			
		} catch (Throwable e) {
			e.printStackTrace();
			log.reportThrowable(e, "Error running 'JDBCAdapterTester'");
		}
	}

}