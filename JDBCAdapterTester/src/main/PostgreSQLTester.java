package main;

import java.sql.SQLException;
import java.util.Arrays;

import static main.config.Configuration.log;
import adapters.PostgreSQLAdapter;
import adapters.exceptions.PreparedProcedureException;

import static main.PostgreSQLAdapterConfiguration.PostgreSQLStatements.*;
import static main.PostgreSQLAdapterConfiguration.PostgreSQLParameters.*;


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
		int result = db.invokeUpdateProcedure(InsertSimpleRecord,
		                                      ID,    11,
		                                      PHONE, "2192820997");
		System.out.println("Result: " + result);
		
		// QUERY
		String phone = (String) db.invokeScalarProcedure(GetSimpleIdFromPhone,
		                                                 ID, 11);
		System.out.println("Result: " + phone);
		
		// DELETE
		result = db.invokeUpdateProcedure(DeleteSimpleRecord,
		                                  ID, 11);
		System.out.println("Result: " + result);

		// NO PARAM STORED PROCEDURE
		int sp = (Integer) db.invokeScalarProcedure(NoParamStoredProcedure);
		System.out.println("Result: " + sp);

		// PARAM STORED PROCEDURE
		db.invokeRowProcedure(ParamStoredProcedure,
                              ID,    11,
                              PHONE, "2192820997");
		System.out.println("Result: NULL");
		
		// Batch INSERT (40 elements at a time)
		int c = 0;
		for (int batchCount=0; batchCount<100; batchCount++) {
			int[] results = db.invokeUpdateBatchProcedure(InsertSimpleRecord, new Object[][] {
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
				{ID, 12+c, PHONE, Long.toString(21991234900L+(c++))},
			});
			System.out.println("Batch Results: " + Arrays.toString(results));
		}

		log.reportRequestFinish();
	}

}
