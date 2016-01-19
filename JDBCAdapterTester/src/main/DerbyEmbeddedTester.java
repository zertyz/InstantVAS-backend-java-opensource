package main;

import static main.config.Configuration.log;

import java.sql.SQLException;

import adapters.DerbyEmbeddedAdapter;
import adapters.JDBCAdapter;
import adapters.PostgreSQLAdapter;
import adapters.dto.PreparedProcedureInvocationDto;

/** <pre>
 * EmbeddedDerbyTester.java
 * ========================
 * (created by luiz, Jan 18, 2016)
 *
 * Some spikes about Embedded Derby integration with {@link JDBCAdapter} as well as
 * {@link DerbyEmbeddedAdapter} database administration commands
 *
 * @version $Id$
 * @author luiz
*/

public class DerbyEmbeddedTester {

	public static void embeddedDerbyTesterMain(String[] args) throws SQLException {
		log.reportRequestStart("DerbyEmbeddedTester");
		log.reportDebug("Attempting to get a Derby Embedded connection...");
		DerbyEmbeddedAdapter db = DerbyEmbeddedAdapterConfiguration.getDBAdapter();
		db.resetDatabase();

		// INSERT
		PreparedProcedureInvocationDto invocation = new PreparedProcedureInvocationDto("InsertTestRecord");
		invocation.addParameter("ID",    11);
		invocation.addParameter("PHONE", "2192820997");
		int result = db.invokeUpdateProcedure(invocation);
		System.out.println("Result: " + result);
		
		// QUERY
		invocation = new PreparedProcedureInvocationDto("GetPhoneFromId");
		invocation.addParameter("ID", 11);
		String phone = (String) db.invokeScalarProcedure(invocation);
		System.out.println("Result: " + phone);
		
		// DELETE
		invocation = new PreparedProcedureInvocationDto("DeleteTestRecord");
		invocation.addParameter("ID", 11);
		result = db.invokeUpdateProcedure(invocation);
		System.out.println("Result: " + result);

		log.reportRequestFinish();
	}
	
	

}
