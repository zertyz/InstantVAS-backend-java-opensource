package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dal.mvstore.ProfileDB;

/** <pre>
 * SMSAppModuleDALFactoryProfile.java
 * ==================================
 * (created by luiz, Aug 3, 2015)
 *
 * Enum based implementation of the Mutua's DAL Factory Pattern, to select among
 * data access layers.
 *
 * @see SMSAppModuleDALFactory
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleDALFactoryProfile {
	

	// DALs
	///////

	RAM {
		@Override
		protected void instantiateDataAccessLayers() {
			super.profileDB = new mutua.smsappmodule.dal.ram.ProfileDB();
		}
	},
	
	POSTGRESQL {
		@Override
		protected void instantiateDataAccessLayers() throws SQLException {
			super.profileDB = new mutua.smsappmodule.dal.postgresql.ProfileDB();
		}
	},
	
	MVSTORE {
		@Override
		protected void instantiateDataAccessLayers() throws SQLException {
			super.profileDB = new mutua.smsappmodule.dal.mvstore.ProfileDB();
		}
	}
	
	;
	

	// Data Access Objects & methods
	////////////////////////////////
	
	private IProfileDB profileDB;
	
	public IProfileDB getProfileDB() {
		checkDataAccessLayers();
		return profileDB;
	}

	/** Method to construct the DAO instances, which is Overridden by each enum. */
	protected abstract void instantiateDataAccessLayers() throws SQLException;
	

	// Instantiation Parameters
	///////////////////////////
	//
	// Some DALs require specific parameters to be instantiated -- for instance, what is the name of the Queue entry I should refer to?
	// The following fields controls this optional behavior

	// ... the protected parameters list that should be overwritten
	
	/** Method to setup the soon-to-be-instantiated Data Access Objects provided by this DAL.
	 * 
	 *  Not all DALs need it. This one doesn't, for instance.
	 *  
	 *  See {@link SMSAppModuleDALFactoryChat} for a good example.
	 *  
	 *  @param ...     see {@link #... parameter name at 'Instantiation Parameters' session} */
//	public SMSAppModuleDALFactory setInstantiationParameters(...) {
//		this...     = ...;
//		this.wasInstantiationParametersProvided = true;
//		return this;
//	}
	
	private static final boolean requireInstantiationParameters = false;
	private boolean wasInstantiationParametersProvided = false;
	private boolean wasInstantiated                    = false;

	/** This method allows the instantiation of only the desired data access layer
	/* and their set of DAOs, preventing unnecessary drivers to be loaded.
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