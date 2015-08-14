package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.icc.configuration.annotations.ConfigurableElement;
import mutua.smsappmodule.dal.ram.MatchDB;
import mutua.smsappmodule.dal.ram.NextBotWordsDB;

/** <pre>
 * SMSAppModuleDALFactoryHangman.java
 * ==================================
 * (created by luiz, Aug 13, 2015)
 *
 * Enum based implementation of the Factory Pattern, to select among
 * data access layers
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleDALFactoryHangman {
	
	RAM {
		protected void instantiateDataAccessLayers() {
			super.matchDB        = new mutua.smsappmodule.dal.ram.MatchDB();
			super.nextBotWordsDB = new mutua.smsappmodule.dal.ram.NextBotWordsDB();
		}
	},
	
	POSTGRESQL {
		protected void instantiateDataAccessLayers() throws SQLException {
			super.matchDB        = new mutua.smsappmodule.dal.postgresql.MatchDB();
			super.nextBotWordsDB = new mutua.smsappmodule.dal.postgresql.NextBotWordsDB();
		}
	},
	
	;
	
	@ConfigurableElement("The desired data access handler for the 'User Profile SMS Module' facilities")
	public static SMSAppModuleDALFactoryHangman DEFAULT_DAL = SMSAppModuleDALFactoryHangman.RAM;
	
	private IMatchDB        matchDB;
	private INextBotWordsDB nextBotWordsDB;
	
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
	
	public IMatchDB getMatchDB() {
		checkDataAccessLayers();
		return matchDB;
	}
	
	public INextBotWordsDB getNextBotWordsDB() {
		checkDataAccessLayers();
		return nextBotWordsDB;
	}
}
