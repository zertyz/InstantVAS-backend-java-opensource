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
 * Enum based implementation of the Factory Pattern, to select among
 * data access layers
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleDALFactoryProfile {
	
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
	
//	@ConfigurableElement("The desired data access handler for the 'User Profile SMS Module' facilities")
//	public static SMSAppModuleDALFactoryProfile DEFAULT_DAL = SMSAppModuleDALFactoryProfile.RAM;
	
	private IProfileDB profileDB;
	
	private boolean wasInstantiated = false;
	
	/** method to construct the DAO instances */
	protected abstract void instantiateDataAccessLayers() throws SQLException;
	
	/** this method allows the instantiation of only the desired data access layer
	/* (preventing unecessary drivers to be loaded) */
	public void checkDataAccessLayers() {
		if (!wasInstantiated) try {
			instantiateDataAccessLayers();
			wasInstantiated = true;
		} catch (Throwable t) {
			Instrumentation.reportThrowable(t, "Error instantiating 'SMSAppModuleDALProfile'");
		}
	}
	
	public IProfileDB getProfileDB() {
		checkDataAccessLayers();
		return profileDB;
	}
}
