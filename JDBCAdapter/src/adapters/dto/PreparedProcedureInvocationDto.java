package adapters.dto;

import java.io.Serializable;
import java.util.Hashtable;

import adapters.exceptions.PreparedProcedureException;

/** <pre>
 * PreparedProcedureInvocationDto.java  --  $Id: PreparedProcedureInvocationDto.java,v 1.1 2010/07/01 22:02:13 luiz Exp $
 * ===================================
 * (created by luiz, Dec 15, 2008)
 *
 * Builds a prepared procedure invocation object that, together with a 'PreparedProcedureDefinition', can be used to
 * generate SQL statements
 */

public class PreparedProcedureInvocationDto {
	
	/** the name of the 'PreparedProcedureDefinitionInfo' to be executed */
	private String commandName;
	/** the parameter table, containing parameter names as keys and their values (as values) */
	private Hashtable<String, Object> parametersTable;
	
	
	// AUXILIAR METHODS
	///////////////////
	
	private void checkParameterName(String parameterName) throws PreparedProcedureException {
		if (parametersTable.containsKey(parameterName)) {
			throw new PreparedProcedureException("Parameter named '"+parameterName+"' was already defined for this PreparedProcedureInvocationDto object");
		}
	}
	

	public PreparedProcedureInvocationDto(String commandName) {
		this.commandName     = commandName;
		this.parametersTable = new Hashtable<String, Object>();
	}
	

	public void addParameter(String parameterName, String value) throws PreparedProcedureException {

		checkParameterName(parameterName);
		
		// escape special characters
		////////////////////////////
		
		// regex
		String special_regex_characters_pattern = "([\\$\\{\\}\\\\])";
		if (value.matches(".*"+special_regex_characters_pattern+".*")) {
			value = value.replaceAll(special_regex_characters_pattern, "\\\\$1");
		}
		
		parametersTable.put(parameterName, value);
	}
	
	public void addParameter(String parameterName, int value) throws PreparedProcedureException {
		checkParameterName(parameterName);
		parametersTable.put(parameterName, new Integer(value));
	}

	public void addParameter(String parameterName, long value) throws PreparedProcedureException {
		checkParameterName(parameterName);
		parametersTable.put(parameterName, new Long(value));
	}

	public void addParameter(String parameterName, byte[] value) throws PreparedProcedureException {
		checkParameterName(parameterName);
		parametersTable.put(parameterName, value);
	}
	
	public void addParameter(String parameterName, Serializable object) throws PreparedProcedureException {
		checkParameterName(parameterName);
		parametersTable.put(parameterName, object);
	}

	
	public String getCommandName() {
		return commandName;
	}

	public Hashtable<String, Object> getParametersTable() {
		return parametersTable;
	}
	
}