package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.icc.configuration.annotations.ConfigurableElement;

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
	
	RAM {
		protected void instantiateDataAccessLayers() {
			super.chatDB = new mutua.smsappmodule.dal.ram.ChatDB();
		}
	},
	
	POSTGRESQL {
		protected void instantiateDataAccessLayers() throws SQLException {
			super.chatDB = new mutua.smsappmodule.dal.postgresql.ChatDB();
		}
	},
	
	;
	
	@ConfigurableElement("The desired data access handler for the 'Chat SMS Module' facilities")
	public static SMSAppModuleDALFactoryChat DEFAULT_DAL = SMSAppModuleDALFactoryChat.RAM;
	
	private IChatDB chatDB;
	
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
			// TODO instrument it
			t.printStackTrace();
		}
	}
	
	public IChatDB getChatDB() {
		checkDataAccessLayers();
		return chatDB;
	}
}