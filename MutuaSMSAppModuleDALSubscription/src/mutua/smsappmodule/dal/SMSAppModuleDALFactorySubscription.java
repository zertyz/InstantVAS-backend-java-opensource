package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;

/** <pre>
 * SMSAppModuleDALFactorySubscription.java
 * =======================================
 * (created by luiz, Jul 24, 2015)
 *
 * Enum based implementation of the Factory Pattern, to select among
 * data access layers for the "Subscription SMS Module"
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleDALFactorySubscription {
	
	/** The RAM based DAL instances of the "Subscription SMS Module", for modeling & testing purposes */
	RAM {
		protected void instantiateDataAccessLayers() {
			super.subscriptionDB = new mutua.smsappmodule.dal.ram.SubscriptionDB();
		}
	},
	
	/** The persistent PostgreSQL DAL instances of the "Subscription SMS Module", for production */
	POSTGRESQL {
		protected void instantiateDataAccessLayers() throws SQLException {
			super.subscriptionDB = new mutua.smsappmodule.dal.postgresql.SubscriptionDB();
		}
	},
	
	;
	
	private ISubscriptionDB subscriptionDB;
	
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
			Instrumentation.reportThrowable(t, "Error instantiating 'SMSAppModuleDALSubscription'");
		}
	}
	
	public ISubscriptionDB getSubscriptionDB() {
		checkDataAccessLayers();
		return subscriptionDB;
	}
}
