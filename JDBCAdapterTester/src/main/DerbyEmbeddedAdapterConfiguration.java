package main;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import adapters.AbstractPreparedProcedure;
import adapters.DerbyEmbeddedAdapter;
import adapters.IJDBCAdapterParameterDefinition;
import adapters.exceptions.PreparedProcedureException;
import main.config.Configuration;

/** <pre>
 * DerbyEmbeddedAdapterConfiguration.java
 * ======================================
 * (created by luiz, Jan 18, 2016)
 *
 * Provides the needed {@link DerbyEmbeddedAdapter} configuration to access and operate on the database
 *
 * @version $Id$
 * @author luiz
*/

public class DerbyEmbeddedAdapterConfiguration extends DerbyEmbeddedAdapter {
	
	// TODO considerar refatorar o JDBCAdapter para rastrear não só a criação de tabelas, mas também de views, indices, procedures, schemas, triggers, etc, e assim permitir um 'clean database' mais facilmente
	
	
	private DerbyEmbeddedAdapterConfiguration() throws SQLException {
		super(Configuration.log, true, true, null, -1, "/temp/tmp/DerbyDBSpikes", null, null);
	}

	@Override
	protected String[][] getTableDefinitions() {
		return new String[][] {
			{"DerbyTestTable", "CREATE TABLE DerbyTestTable (id int, phone varchar(20))"},
		};
	}

	
	/***************
	** PARAMETERS **
	***************/
	
	public enum DerbyParameters implements IJDBCAdapterParameterDefinition {

		ID   (Integer.class),
		PHONE(String.class),
		
		;
		
		private DerbyParameters(Class<? extends Object> a) {}

		@Override
		public String getParameterName() {
			return name();
		}
	}
	
	/***************
	** STATEMENTS **
	***************/
	
	public static final class DerbyStatements {
		/** Inserts an 'ID' and 'PHONE' into the 'DerbyTestTable' */
		public static final AbstractPreparedProcedure InsertTestRecord = new AbstractPreparedProcedure(
			"INSERT INTO DerbyTestTable VALUES (",DerbyParameters.ID,", ",DerbyParameters.PHONE,")");
		/** Returns the 'PHONE' associated with 'ID' */
		public static final AbstractPreparedProcedure GetPhoneFromId = new AbstractPreparedProcedure(
			"SELECT phone FROM DerbyTestTable WHERE id=",DerbyParameters.ID);
		/** Updates the 'PHONE' on the record associated with 'ID' */
		public static final AbstractPreparedProcedure UpdateTestRecord = new AbstractPreparedProcedure(
			"UPDATE DerbyTestTable SET phone=",DerbyParameters.PHONE," WHERE id=",DerbyParameters.ID);
		/** removes the record denoted by 'ID' */
		public static final AbstractPreparedProcedure DeleteTestRecord = new AbstractPreparedProcedure(
			"DELETE FROM DerbyTestTable WHERE id=",DerbyParameters.ID);
	}
	
	public static DerbyEmbeddedAdapter getDBAdapter() throws SQLException {
		return new DerbyEmbeddedAdapterConfiguration();
	}
	
	

}