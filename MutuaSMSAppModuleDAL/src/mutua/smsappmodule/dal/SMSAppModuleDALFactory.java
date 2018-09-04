package mutua.smsappmodule.dal;

import java.sql.SQLException;

import mutua.icc.instrumentation.Instrumentation;

/** <pre>
 * SMSAppModuleDALFactory.java
 * ===========================
 * (created by luiz, Jul 15, 2015)
 *
 * Reference implementation of the Mutua's DAL Factory Pattern, to select among
 * data access layers.
 * 
 * This class provides the foundations for the Mutua's Factory Pattern to select
 * among Data Access Layers and is, therefore, an example to be followed.
 * 
 * This one implements the DAL to the "Base SMS Module".
 * 
 * To properly follow the Mutua's DAL Factory Pattern, followers must:
 *  1) Declare themselves as Enum, copying the methods {@link #instantiateDataAccessLayers} and {@link checkDataAccessLayers}
 *  2) Each enum member should be named to the data layer they will represent -- {@link #RAM}, {@link #POSTGRESQL}, {@link #MVSTORE}, MYSQL, ORACLE, ...
 *  3) Each enum member will override the method 'instantiateDataAccessLayers', who will initialize the Data Access Objects
 *  4) If interested into parameterizing the instantiation, followers should define a method like {@link #setInstantiationParameters},
 *     which will receive and set their parameters. This method should have the return type of the follower class and, therefore,
 *     return 'this', so the configuration pattern can be applied 
 *  5) When the time for DATA_ACCESS_LAYERs setup comes, usage classes should make use the following configuration pattern:
 *     MyDAL = MyExtendingClassDAL.MY_DATABASE_DAL_IMPLEMENTATION.setConfigurationParameters(....)
 *     And then proceed to calling the appropriate instantiation methods -- see next.
 *  6) Extensions should provide their own "instantiation methods" -- methods who returns a Data Access Object. See this example {@link #getUserDB()}
 *
 * @author luiz
 */

public enum SMSAppModuleDALFactory {
	

	// DALs
	///////

	/** The RAM based DAL instances of the "Base SMS Module", for modeling & testing purposes */
	RAM {
		@Override
		protected void instantiateDataAccessLayers() {
			super.userDB    = new mutua.smsappmodule.dal.ram.UserDB();
			super.sessionDB = new mutua.smsappmodule.dal.ram.SessionDB();
		}
	},
	
	/** The persistent PostgreSQL DAL instances of the "Base SMS Module", for production */
	POSTGRESQL {
		@Override
		protected void instantiateDataAccessLayers() throws SQLException {
			super.userDB    = new mutua.smsappmodule.dal.postgresql.UserDB();
			super.sessionDB = new mutua.smsappmodule.dal.postgresql.SessionDB();
		}

	},
	
	/** H2's MVStore -- NoSQL embedded storage library used in H2 database, for blazing performance */
	MVSTORE {
		@Override
		protected void instantiateDataAccessLayers() {
			super.userDB    = new mutua.smsappmodule.dal.mvstore.UserDB();
			super.sessionDB = new mutua.smsappmodule.dal.mvstore.SessionDB();
		}
	}
	
	;

	
	// Data Access Objects & methods
	////////////////////////////////
	//
	// Classes implementing this pattern should overwrite this section
	// with their own DAOs and accessor methods.
	
	private IUserDB    userDB;
	private ISessionDB sessionDB;

	public IUserDB getUserDB() {
		checkDataAccessLayers();
		return userDB;
	}
	
	public ISessionDB getSessionDB() {
		checkDataAccessLayers();
		return sessionDB;
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
	
	private boolean requireInstantiationParameters;				// if you are not using parameters, declare it as static final and set it to false
	private boolean wasInstantiationParametersProvided = false;
	private boolean wasInstantiated                    = false;

	/** Enum constructor. If instantiation parameters are required, each enum should state if it requires that
	 * {@link #setInstantiationParameters} is called before use. */
	private SMSAppModuleDALFactory(boolean requireInstantiationParameters) {
		this.requireInstantiationParameters = requireInstantiationParameters;
	}
	
	/** ... if this constructor is used, it means this DAL doesn't need any configuration prior to instantiation */
	private SMSAppModuleDALFactory() {
		this(false);
	}
	
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