package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;

/** <pre>
 * SMSAppModuleDALFactory.java
 * ===========================
 * (created by luiz, Jul 15, 2015)
 *
 * Enum based implementation of the Factory Pattern, to select among
 * data access layers for the "Base SMS Module"
 *
 * @see #RAM
 * @see #POSTGRESQL
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleDALFactory {

	/** The RAM based DAL instances of the "Base SMS Module", for modeling & testing purposes */
	RAM {
		protected void instantiateDataAccessLayers() {
			super.userDB    = new mutua.smsappmodule.dal.ram.UserDB();
			super.sessionDB = new mutua.smsappmodule.dal.ram.SessionDB();
		}
	},
	
	/** The persistent PostgreSQL DAL instances of the "Base SMS Module", for production */
	POSTGRESQL {
		protected void instantiateDataAccessLayers() throws SQLException {
			super.userDB    = new mutua.smsappmodule.dal.postgresql.UserDB();
			super.sessionDB = new mutua.smsappmodule.dal.postgresql.SessionDB();
		}

	},
	
	;
	
	private IUserDB    userDB;
	private ISessionDB sessionDB;

	private boolean wasInstantiated = false;

	/** method to construct the DAO instances */
	protected abstract void instantiateDataAccessLayers() throws SQLException;
	
	/** this method allows the instantiation of only the desired data access layer
	/* (preventing unnecessary drivers to be loaded) */
	public void checkDataAccessLayers() {
		if (!wasInstantiated) try {
			instantiateDataAccessLayers();
			wasInstantiated = true;
		} catch (Throwable t) {
			Instrumentation.reportThrowable(t, "Error instantiating 'SMSAppModuleDAL'");
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