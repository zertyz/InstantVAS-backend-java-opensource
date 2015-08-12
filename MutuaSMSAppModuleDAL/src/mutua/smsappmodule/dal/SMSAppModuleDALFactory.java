package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * SMSAppModuleDALFactory.java
 * ===========================
 * (created by luiz, Jul 15, 2015)
 *
 * Enum based implementation of the Factory Pattern, to select among
 * data access layers
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleDALFactory {

	RAM {
		protected void instantiateDataAccessLayers() {
			super.userDB    = new mutua.smsappmodule.dal.ram.UserDB();
			super.sessionDB = new mutua.smsappmodule.dal.ram.SessionDB();
		}
	},
	
	POSTGRESQL {
		protected void instantiateDataAccessLayers() throws SQLException {
			super.userDB    = new mutua.smsappmodule.dal.postgresql.UserDB();
			super.sessionDB = new mutua.smsappmodule.dal.postgresql.SessionDB();
		}

	},
	
	;
	
	@ConfigurableElement("The desired data access handler for the 'SMS Module' base features")
	public static SMSAppModuleDALFactory DEFAULT_DAL = SMSAppModuleDALFactory.RAM;
		
	private IUserDB    userDB;
	private ISessionDB sessionDB;

	private boolean wasInstantiated = false;

	/** method to construct the DAO instances */
	protected abstract void instantiateDataAccessLayers() throws SQLException;
	
	/** this method allows the instantiation of only the desired data access layer
	/* (preventing unecessary drivers to be loaded) */
	private void checkDataAccessLayers() {
		if (!wasInstantiated) try {
			instantiateDataAccessLayers();
			wasInstantiated = true;
		} catch (Throwable t) {
			// TODO instrument it
			t.printStackTrace();
		}
	}
	
	public IUserDB getUserDB() {
		checkDataAccessLayers();
		return userDB;
	}
	
	public ISessionDB getSessionDB() {
		checkDataAccessLayers();
		return sessionDB;
	}

}