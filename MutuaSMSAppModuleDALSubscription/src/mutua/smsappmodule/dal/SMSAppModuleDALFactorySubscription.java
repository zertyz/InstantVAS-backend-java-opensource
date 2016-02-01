package mutua.smsappmodule.dal;

import java.sql.SQLException;

/** <pre>
 * SMSAppModuleDALFactorySubscription.java
 * =======================================
 * (created by luiz, Jul 24, 2015)
 *
 * Enum based implementation of the Factory Pattern, to select among
 * data access layers
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleDALFactorySubscription {
	
	RAM {
		protected void instantiateDataAccessLayers() {
			super.subscriptionDB = new mutua.smsappmodule.dal.ram.SubscriptionDB();
		}
	},
	
	POSTGRESQL {
		protected void instantiateDataAccessLayers() throws SQLException {
			super.subscriptionDB = new mutua.smsappmodule.dal.postgresql.SubscriptionDB();
		}
	},
	
	;
	
//	@ConfigurableElement("The desired data access handler for the 'Subscription SMS Module' facilities")
//	public static SMSAppModuleDALFactorySubscription DEFAULT_DAL = SMSAppModuleDALFactorySubscription.RAM;
//	
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
			// TODO instrument it
			t.printStackTrace();
		}
	}
	
	public ISubscriptionDB getSubscriptionDB() {
		checkDataAccessLayers();
		return subscriptionDB;
	}
}
