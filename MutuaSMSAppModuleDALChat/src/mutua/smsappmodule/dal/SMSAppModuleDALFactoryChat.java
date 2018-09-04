package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dal.mvstore.ChatDB;

/** <pre>
 * SMSAppModuleDALFactoryChat.java
 * ===============================
 * (created by luiz, Sep 8, 2015)
 *
 * Enum based implementation of the Factory Pattern, to select among
 * data access layers
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleDALFactoryChat {
	
	RAM(false) {
		@Override
		protected void instantiateDataAccessLayers() {
			super.chatDB = new mutua.smsappmodule.dal.ram.ChatDB();
		}
	},
	
	POSTGRESQL(true) {
		@Override
		protected void instantiateDataAccessLayers() throws SQLException {
			super.chatDB = new mutua.smsappmodule.dal.postgresql.ChatDB(moTableName, moIdFieldName, moTextFieldName);
		}
	},
	
	MVSTORE(true) {
		@Override
		protected void instantiateDataAccessLayers() {
			super.chatDB = new mutua.smsappmodule.dal.mvstore.ChatDB(moTableName, moIdFieldName, moTextFieldName);
		}
	}
	
	;
	
	private IChatDB chatDB;
	
	private boolean requireInstantiationParameters;
	private boolean wasInstantiationParametersProvided = false;
	private boolean wasInstantiated                    = false;
	
	/** Enum constructor. Each enum of this class should state if it requires that {@link #setInstantiationParameters} is called before use. */
	SMSAppModuleDALFactoryChat(boolean requireInstantiationParameters) {
		this.requireInstantiationParameters = requireInstantiationParameters;
	}
	

	// instantiation parameters
	///////////////////////////
	
	/** The table used to register MOs */
	protected String moTableName;
	/** The index id field name, within the MO table */
	protected String moIdFieldName;
	/** The text field name, within the MO table */
	protected String moTextFieldName;
	
	/** Method to setup the soon-to-be-instantiated database modules provided by this DAL.
	 * 
	 *  The ChatDB's behaves a little different from others SMSAppModuleDALFactory(ies): if we know
	 *  where the MO's are stored, we don't need to copy the information again on the 'private messages' log.
	 *  
	 *  Production implementations (like PostgreSQL and MVStore) should not duplicate the information and,
	 *  therefore, do make use of the parameters provided;
	 *  The RAM instance, on the other hand, is known to ignore such parameters -- it indeed copy the information again,
	 *  wasting space, in the hope that this is OK, since it was made to be used only for testing purposes.
	 *  
	 *  @param moTableName     see {@link #moTableName}
	 *  @param moIdFieldName   see {@link #moIdFieldName}
	 *  @param moTextFieldName see {@link #moTextFieldName} */
	public SMSAppModuleDALFactoryChat setInstantiationParameters(String moTableName, String moIdFieldName, String moTextFieldName) {
		this.moTableName     = moTableName;
		this.moIdFieldName   = moIdFieldName;
		this.moTextFieldName = moTextFieldName;
		this.wasInstantiationParametersProvided = true;
		return this;
	}
	
	/** Method to construct the DAO instances, which is Overridden by each enum. */
	protected abstract void instantiateDataAccessLayers() throws SQLException;
	
	/** This method allows the instantiation of only the desired data access layer
	/* (preventing unnecessary drivers to be loaded)
	 * ... and checks if {@link #setInstantiationParameters} was properly called. */
	public void checkDataAccessLayers() {
		if (!wasInstantiated) try {
			// checks if {@link #setInstantiationParameters} was properly called
			if (requireInstantiationParameters && (wasInstantiationParametersProvided == false)) {
				throw new RuntimeException("SMSAppModuleDALFactory pattern: '"+this.getClass().getName()+"."+this.name()+".setInstantiationParameters(...)' was not properly called at DATA_ACCESS_LAYERs setup.");
			}
			instantiateDataAccessLayers();
			wasInstantiated = true;
		} catch (Throwable t) {
			Instrumentation.reportThrowable(t, "Error instantiating '"+this.getClass().getName()+"'");
		}
	}
	
	public IChatDB getChatDB() {
		checkDataAccessLayers();
		return chatDB;
	}
}