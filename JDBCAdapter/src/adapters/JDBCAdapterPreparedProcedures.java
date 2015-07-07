package adapters;

import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;
import static mutua.icc.instrumentation.JDBCAdapterInstrumentationEvents.*;
import static mutua.icc.instrumentation.JDBCAdapterInstrumentationProperties.*;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mutua.icc.instrumentation.Instrumentation;
import adapters.dto.PreparedProcedureInvocationDto;
import adapters.exceptions.PreparedProcedureException;

/** <pre>
 * JDBCAdapterPreparedProcedures.java  --  $Id: JDBCPreparedProceduresHelper.java,v 1.1 2010/07/01 22:02:14 luiz Exp $
 * ==================================
 * (created by luiz, Dec 15, 2008)
 *
 * Deals with prepared procedures -- an imitation of stored procedures that won't let anybody (nor
 * the developer nor the user) to execute arbitrary code on the database 
 */

public class JDBCAdapterPreparedProcedures {
	
	private Hashtable<String, String> preparedProcedures;
	private Instrumentation<?, ?> log;
	
	
	/** retrieves the string to be used in place for the name of the parameter.
	/*  ex: "SELECT * FROM table WHERE ID=${ID}" becomes "SELECT * FROM table WHERE ID=?" and so on */
	private String getParameterSubstitutionForDataType(Object parameterValue) {
		String parameterNameReplacement = "?";		// '?' for almost all types, except for those:
		if (parameterValue instanceof int[]) {		// integer arrays -- for IN lists. Ex: SELECT * FROM table WHERE id IN (?, ?, ?, ...)
			int[] intArray = (int[])parameterValue;
			StringBuffer concatBuffer = new StringBuffer();
			for (int i=0; i<intArray.length; i++) {
				if (i == 0) {
					concatBuffer.append("?");
				} else {
					concatBuffer.append(", ?");
				}
			}
			parameterNameReplacement = concatBuffer.toString();
			
		} else if (parameterValue instanceof String[]) {			// String arrays -- for IN lists. Ex: SELECT * FROM table WHERE name IN (?, ?, ?, ...)
			String[] stringArray = (String[])parameterValue;
			StringBuffer concatBuffer = new StringBuffer();
			for (int i=0; i<stringArray.length; i++) {
				if (i == 0) {
					concatBuffer.append("?");
				} else {
					concatBuffer.append(", ?");
				}
			}
			parameterNameReplacement = concatBuffer.toString();
			
		}
		return parameterNameReplacement;
	}
	
	
	/** loads the prepared procedures defined by this array, where:
	/*  prepared_procedures_definitions := {procedure_name = sql_template, ... }
	/*  'sql_template' is an SQL command lacking parameters -- it should be marked in a bash-like
	/*  syntax, for instance: "SELECT * FROM ${TABLE}" */
	protected JDBCAdapterPreparedProcedures(Instrumentation<?, ?> log, String[][] preparedProceduresDefinitions) throws PreparedProcedureException {
		
		this.log = log;
		preparedProcedures = new Hashtable<String, String>();
		
		for (int procedureId=0; procedureId<preparedProceduresDefinitions.length; procedureId++) {
			String procedureName = preparedProceduresDefinitions[procedureId][0];
			String sqlTemplate   = preparedProceduresDefinitions[procedureId][1];
			if (preparedProcedures.containsKey(procedureName)) {
				throw new PreparedProcedureException("JDBCAdapterPreparedProcedures: Prepared Procedure named '"+procedureName+"' was already defined");
			}
			preparedProcedures.put(procedureName, sqlTemplate);
		}
	}
	
	protected PreparedStatement buildPreparedStatement(PreparedProcedureInvocationDto invocation, Connection connection) throws SQLException {

		String invokedProcedureName = invocation.getCommandName();
		
		// check that the command is a registered one
		if (!preparedProcedures.containsKey(invokedProcedureName)) {
			throw new PreparedProcedureException("buildPreparedStatement: Prepared Procedure Invocation failed for command '"+invokedProcedureName+"': command not found");
		}
		
		String sqlTemplate = preparedProcedures.get(invokedProcedureName);
		Hashtable<String, Object> parametersTable = invocation.getParametersTable();
		ArrayList<String> parameterNames  = new ArrayList<String>();
		ArrayList<Object> parameterValues = new ArrayList<Object>();
		
		// replace variables
		String regularExpression = "\\$\\{([^}]+)\\}";
		Pattern pattern = Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE);
		String preparedSql = sqlTemplate;
		int replaceCount = 0;
		while (true) {
			Matcher m = pattern.matcher(preparedSql);
			if (m.find()) {
				if (m.groupCount() == 1) {
					String varName = m.group(1);
					Object varValue = parametersTable.get(varName);
					if (varValue == null) {
						throw new PreparedProcedureException("buildPreparedStatement: Prepared Procedure Invocation failed for command '"+invokedProcedureName+"': parameter named '"+varName+"' is missing");
					}
					
					String parameterNameReplacement = getParameterSubstitutionForDataType(varValue);
					preparedSql = m.replaceFirst(parameterNameReplacement);
					
					parameterNames.add(varName);
					parameterValues.add(varValue);
					replaceCount++;
					if (replaceCount > 1000) {
						throw new PreparedProcedureException("buildPreparedStatement: This invocation led to an overflow in the number of variable substitutions");
					}
				}
			} else {
				break;
			}
		}

		if (JDBCAdapter.SHOULD_DEBUG_QUERIES) {
			log.reportEvent(IE_DATABASE_QUERY, IP_SQL_TEMPLATE, preparedSql, IP_SQL_TEMPLATE_PARAMETERS, parameterValues);
		}
		
		PreparedStatement ps = connection.prepareStatement(preparedSql);
		int parameterCount = 1;
		for (int i=0; i<parameterValues.size(); i++) {
			Object value = parameterValues.get(i);
			if (value instanceof String) {
				ps.setString(parameterCount++, (String)value);
			} else if (value instanceof Integer) {
				ps.setInt(parameterCount++, ((Integer)value).intValue());
			} else if (value instanceof Long) {
				ps.setLong(parameterCount++, ((Long)value).longValue());
			} else if (value instanceof byte[]) {
				ps.setBytes(parameterCount++, (byte[])value);
			} else if (value instanceof int[]) {
				int[] intArray = (int[])value;
				for (int j=0; j<intArray.length; j++) {
					ps.setInt(parameterCount++, intArray[j]);
				}
			} else if (value instanceof String[]) {
				String[] stringArray = (String[])value;
				for (int j=0; j<stringArray.length; j++) {
					ps.setString(parameterCount++, stringArray[j]);
				}
			} else if (value instanceof Serializable) {
				ps.setObject(parameterCount++, value);
			} else {
				throw new PreparedProcedureException("buildPreparedStatement: Don't know how to handle the type of the parameter named '" + 
				                                     parameterNames.get(i) + "' in query '" + sqlTemplate + "' for prepared procedure named '" +
				                                     invokedProcedureName + "'");
			}
		}
		
		return ps;
	
	}

}