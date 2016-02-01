package adapters;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import adapters.exceptions.PreparedProcedureException;

/** <pre>
 * JDBCAdapterPreparedProcedure.java  --  $Id: JDBCPreparedProceduresHelper.java,v 1.1 2010/07/01 22:02:14 luiz Exp $
 * =================================
 * (created by luiz, Dec 15, 2008)
 *
 * A member of "JDBC Adapter Configuration" pattern to abstract a 'PreparedProcedure', freeing it from the
 * need to be associated with a 'Connection' -- and responsible for building a 'PreparedProcedure' when requested.
 * Also keeps a cache of 'IJDBDAdapterSQLStatementDefinition' to 'PreparedProcedure(conn)', for optimization purposes
 */

public class AbstractPreparedProcedure {
	
	private String preparedProcedureSQL;
	private IJDBCAdapterParameterDefinition[] params;

	/** Keep the structures needed to transform 'sqlStatementBits' into a 'PreparedProcedure',
	 *  according to the "JDBC Adapter Configuration" pattern. */
	public AbstractPreparedProcedure(Object... sqlStatementBits) {
		preparedProcedureSQL = calculatePreparedProcedureSQL(sqlStatementBits);
		params               = calculateParameters(sqlStatementBits);			
	}

	/** returns the 'sqlStatement' to be used to construct 'PreparedProcedures' associated with this instance */
	public String getPreparedProcedureSQL() {
		return preparedProcedureSQL;
	}
	
	/** Uses the internal cache mechanism to efficiently retrieve a ready to use 'PreparedStatement' */
	public PreparedStatement getPreparedStatement(Connection conn, Object... parametersAndValuesPairs) throws SQLException {
		return buildPreparedStatement(conn, parametersAndValuesPairs);
	}

	private PreparedStatement buildPreparedStatement(Connection conn, Object... parametersAndValuesPairs) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(preparedProcedureSQL);
		int pairsLength = parametersAndValuesPairs.length/2;
		for (int paramsIndex=0; paramsIndex<params.length; paramsIndex++) {
			IJDBCAdapterParameterDefinition parameterFromConstructor = params[paramsIndex];
			for (int pairsIndex=0; pairsIndex<pairsLength; pairsIndex++) {
				IJDBCAdapterParameterDefinition parameterFromPairs = (IJDBCAdapterParameterDefinition)parametersAndValuesPairs[pairsIndex*2];
				if (parameterFromPairs == parameterFromConstructor) {
					Object value = parametersAndValuesPairs[pairsIndex*2+1];
					if (value instanceof String) {
						ps.setString(paramsIndex+1, (String)value);
					} else if (value instanceof Integer) {
						ps.setInt(paramsIndex+1, ((Integer)value).intValue());
					} else if (value instanceof Long) {
						ps.setLong(paramsIndex+1, ((Long)value).longValue());
					} else if (value instanceof byte[]) {
						ps.setBytes(paramsIndex+1, (byte[])value);
					} else if (value instanceof int[]) {
						int[] intArray = (int[])value;
						Object[] genericArray = new Object[intArray.length];
						System.arraycopy(intArray, 0, genericArray, 0, intArray.length);
						ps.setArray(paramsIndex, conn.createArrayOf("int", genericArray));
					} else if (value instanceof String[]) {
						String[] stringArray = (String[])value;
						Object[] genericArray = new Object[stringArray.length];
						System.arraycopy(stringArray, 0, genericArray, 0, stringArray.length);
						ps.setArray(paramsIndex, conn.createArrayOf("text", genericArray));
					} else if (value instanceof Serializable) {
						ps.setObject(paramsIndex, value);
					} else {
						throw new PreparedProcedureException("buildPreparedStatement: Don't know how to handle the type for the parameter named '" + 
						                                     parameterFromPairs.getParameterName() + "' in query '" + preparedProcedureSQL + "'");
					}
				}
			}
		}
		return ps;
	}

	
	/** method to build an SQL Statement that can be used on construct a 'PreparedProcedure'.
	 *  Ex: sqlStatements := {"INSERT INTO MyTable VALUES (",parameters.ID,", ",parameters.PHONE,")"}
	 *  where 'parameters.ID' and 'parameters.PHONE' are instances of {@link IJDBCAdapterParameterDefinition} */
	private static String calculatePreparedProcedureSQL(Object[] sqlStatementBits) {
		StringBuffer preparedProcedureSQL = new StringBuffer();
		for (Object sqlStatementBit : sqlStatementBits) {
			if (sqlStatementBit instanceof String) {
				preparedProcedureSQL.append((String)sqlStatementBit);
			} else if (sqlStatementBit instanceof Integer) {
				preparedProcedureSQL.append((Integer)sqlStatementBit);
			} else if (sqlStatementBit instanceof IJDBCAdapterParameterDefinition) {
				preparedProcedureSQL.append('?');
			} else if (sqlStatementBit instanceof Object[]) {
				preparedProcedureSQL.append(calculatePreparedProcedureSQL((Object[])sqlStatementBit));
			} else {
				throw new NotImplementedException();
			}
		}
		return preparedProcedureSQL.toString();
	}

	/** method to build the parameters array for the 'PreparedStatement' associated with 'sqlStatementBits'.
	 *  Note: parameters are not unique, for they may happen n number of times in the sql statement */
	private static IJDBCAdapterParameterDefinition[] calculateParameters(Object[] sqlStatementBits) {
		ArrayList<IJDBCAdapterParameterDefinition> parameters = new ArrayList<IJDBCAdapterParameterDefinition>(sqlStatementBits.length);
		for (Object sqlStatementBit : sqlStatementBits) {
			if (sqlStatementBit instanceof IJDBCAdapterParameterDefinition) {
				parameters.add((IJDBCAdapterParameterDefinition)sqlStatementBit);
			} else if (sqlStatementBit instanceof Object[]) {
				for (IJDBCAdapterParameterDefinition parameter : calculateParameters((Object[])sqlStatementBit)) {
					parameters.add(parameter);
				}
			}
		}
		return parameters.toArray(new IJDBCAdapterParameterDefinition[parameters.size()]);
	}

}