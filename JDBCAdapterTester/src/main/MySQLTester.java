package main;

import java.sql.SQLException;

import adapters.MySQLAdapter;
import adapters.dto.PreparedProcedureInvocationDto;
import adapters.exceptions.PreparedProcedureException;

/**
 * MySQLTester.java  --  $Id: MySQLTester.java,v 1.1 2010/07/01 22:03:06 luiz Exp $
 * ================
 * (created by luiz, Dec 15, 2008)
 *
 * Some 'JDBCAdapter' / 'MySQLAdapter' spikes
 */

public class MySQLTester {
	
	public static void mysqltesterMain(String[] args) throws SQLException, PreparedProcedureException {
		System.out.println("MySQLAdapterTester is running...");
		MySQLAdapter db = MySQLAdapterConfiguration.getDBAdapter();
		
		// INSERT
		PreparedProcedureInvocationDto invocation = new PreparedProcedureInvocationDto("InsertSimpleRecord");
		invocation.addParameter("ID",    "11");
		invocation.addParameter("PHONE", "2192820997");
		int result = db.invokeUpdateProcedure(invocation);
		System.out.println("Result: " + result);
		
		// QUERY
		invocation = new PreparedProcedureInvocationDto("GetSimpleIdFromPhone");
		invocation.addParameter("ID", "11");
		String phone = (String) db.invokeScalarProcedure(invocation);
		System.out.println("Result: " + phone);
		
		// DELETE
		invocation = new PreparedProcedureInvocationDto("DeleteSimpleRecord");
		invocation.addParameter("ID", "11");
		result = db.invokeUpdateProcedure(invocation);
		System.out.println("Result: " + result);
	}

}


