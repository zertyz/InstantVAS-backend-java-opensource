package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dal.mvstore.ChatDB;

/** <pre>
 * SMSAppModuleDALFactoryChat.java
 * ===============================
 * (created by luiz, Sep 8, 2015)
 *
 * Enum based implementation of the Mutua's DAL Factory Pattern, to select among
 * data access layers.
 *
 * @see SMSAppModuleDALFactory
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleDALFactoryChat {
	
	
	// DALs
	///////
	
	/** The RAM based DAL instances of the "Chat SMS Module", for modeling & testing purposes */
	RAM(false) {
		@Override
		protected void instantiateDataAccessLayers() {
			super.chatDB = new mutua.smsappmodule.dal.ram.ChatDB();
		}
	},
	
	/** The persistent PostgreSQL DAL instances of the "Chat SMS Module", for production */
	POSTGRESQL(true) {
		@Override
		protected void instantiateDataAccessLayers() throws SQLException {
			super.chatDB = new mutua.smsappmodule.dal.postgresql.ChatDB(moTableName, moIdFieldName, moTextFieldName);
		}
	},
	
	/** H2's MVStore -- NoSQL embedded storage library used in H2 database, for blazing performance */
	MVSTORE(true) {
		@Override
		protected void instantiateDataAccessLayers() {
			super.chatDB = new mutua.smsappmodule.dal.mvstore.ChatDB(moTableName, moIdFieldName, moTextFieldName);
		}
	}
	
	;
	
	// Data Access Objects & methods
	////////////////////////////////
	
	private IChatDB chatDB;
	
	public IChatDB getChatDB() {
		checkDataAccessLayers();
		return chatDB;
	}
	
	/** Method to construct the DAO instances, which is Overridden by each enum. */
	protected abstract void instantiateDataAccessLayers() throws SQLException;
	

	// Instantiation Parameters
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
	
	private boolean requireInstantiationParameters;
	private boolean wasInstantiationParametersProvided = false;
	private boolean wasInstantiated                    = false;
	
	/** Some enums of this class require that {@link #setInstantiationParameters} is called before use. */
	SMSAppModuleDALFactoryChat(boolean requireInstantiationParameters) {
		this.requireInstantiationParameters = requireInstantiationParameters;
	}
	

	/** This method allows the instantiation of only the desired data access layer
	/* (preventing unnecessary drivers to be loaded)
	 * ... and checks if {@link #setInstantiationParameters} was properly called. */
	public void checkDataAccessLayers() {
		if (!wasInstantiated) try {
			// checks if {@link #setInstantiationParameters} was properly called
			if (requireInstantiationParameters && (wasInstantiationParametersProvided == false)) {
				throw new RuntimeException("Mutua's DAL Factory pattern: '"+this.getClass().getName()+"."+this.name()+".setInstantiationParameters(...)' was not properly called at DATA_ACCESS_LAYERs setup.");
			}
			instantiateDataAccessLayers();
			wasInstantiated = true;
		} catch (Throwable t) {
			Instrumentation.reportThrowable(t, "Error instantiating '"+this.getClass().getName()+"'");
		}
	}
	
}