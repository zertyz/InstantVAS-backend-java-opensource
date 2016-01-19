package main;

import java.sql.SQLException;

import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;
import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;
import static main.config.Configuration.log;
import adapters.PostgreSQLAdapter;
import adapters.dto.PreparedProcedureInvocationDto;
import adapters.exceptions.PreparedProcedureException;

/** <pre>
 * PostgreSQLTester.java
 * =====================
 * (created by luiz, Jan 26, 2015)
 *
 * Some 'JDBCAdapter' / 'PostgreSQLAdapter' spikes
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class PostgreSQLTester {

	public static void postgreSQLTesterMain(String[] args) throws SQLException, PreparedProcedureException {
		log.reportRequestStart("PostgreSQLTester");
		log.reportDebug("Attempting to get a PostgreSQL connection...");
		PostgreSQLAdapter db = PostgreSQLAdapterConfiguration.getDBAdapter();
		db.resetDatabase();

		// INSERT
		PreparedProcedureInvocationDto invocation = new PreparedProcedureInvocationDto("InsertSimpleRecord");
		invocation.addParameter("ID",    11);
		invocation.addParameter("PHONE", "2192820997");
		int result = db.invokeUpdateProcedure(invocation);
		System.out.println("Result: " + result);
		
		// QUERY
		invocation = new PreparedProcedureInvocationDto("GetSimpleIdFromPhone");
		invocation.addParameter("ID", 11);
		String phone = (String) db.invokeScalarProcedure(invocation);
		System.out.println("Result: " + phone);
		
		// DELETE
		invocation = new PreparedProcedureInvocationDto("DeleteSimpleRecord");
		invocation.addParameter("ID", 11);
		result = db.invokeUpdateProcedure(invocation);
		System.out.println("Result: " + result);

		// NO PARAM STORED PROCEDURE
		invocation = new PreparedProcedureInvocationDto("NoParamStoredProcedure");
		int sp = (Integer) db.invokeScalarProcedure(invocation);
		System.out.println("Result: " + sp);

		// PARAM STORED PROCEDURE
		invocation = new PreparedProcedureInvocationDto("ParamStoredProcedure");
		invocation.addParameter("ID",    11);
		invocation.addParameter("PHONE", "2192820997");
		db.invokeRowProcedure(invocation);
		System.out.println("Result: NULL");

		log.reportRequestFinish();
	}

}
