package mutua.hangmansmsgame.dal;

/** <pre>
 * DALFactory.java
 * ================
 * (created by luiz, Jan 2, 2015)
 *
 * Select among data access layers of 'EDataAccessLayers'
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class DALFactory {

	public enum EDataAccessLayers {MEMORY, POSTGRESQL}
	
	// configurable values
	public static EDataAccessLayers DEFAULT_DAL = EDataAccessLayers.MEMORY;
	
	private static final DALFactory[] instances;
	
	static {
		// build the multiton instances
		instances = new DALFactory[EDataAccessLayers.values().length];
		instances[EDataAccessLayers.MEMORY.ordinal()]     = new DALFactory(EDataAccessLayers.MEMORY);
		instances[EDataAccessLayers.POSTGRESQL.ordinal()] = new DALFactory(EDataAccessLayers.POSTGRESQL);
	}
	
	private IUserSessionDB userSessionDB;
	
	private DALFactory(EDataAccessLayers dal) {
		
		switch (dal) {
			case MEMORY:
				userSessionDB = new mutua.hangmansmsgame.dal.ram.UserSessionDB();
				break;
			case POSTGRESQL:
				break;
			default:
				throw new RuntimeException("Don't know how to build a '"+dal.name()+"' DAL instance");
		}
		
	}
	
	public static IUserSessionDB getSessionDB(EDataAccessLayers dal) {
		return instances[dal.ordinal()].userSessionDB;
	}
	
	public static IUserSessionDB getSessionDB() {
		return getSessionDB(DEFAULT_DAL);
	}
	
}
