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

	public enum EDataAccessLayers {RAM, POSTGRESQL}
	
	// configurable values
	public static EDataAccessLayers DEFAULT_DAL = EDataAccessLayers.RAM;

	private static final DALFactory[] instances;
	
	static {
		// build the multiton instances
		instances = new DALFactory[EDataAccessLayers.values().length];
		instances[EDataAccessLayers.RAM.ordinal()]        = new DALFactory(EDataAccessLayers.RAM);
		instances[EDataAccessLayers.POSTGRESQL.ordinal()] = new DALFactory(EDataAccessLayers.POSTGRESQL);
	}
	
	private ISessionDB sessionDB;
	private IUserDB userDB;
	
	
	private DALFactory(EDataAccessLayers dal) {
		
		switch (dal) {
			case RAM:
				sessionDB = new mutua.hangmansmsgame.dal.ram.SessionDB();
				userDB    = new mutua.hangmansmsgame.dal.ram.UserDB();
				break;
			case POSTGRESQL:
				break;
			default:
				throw new RuntimeException("Don't know how to build a '"+dal.name()+"' DAL instance");
		}
		
	}
	
	
	// SessionDB
	////////////
	
	public static ISessionDB getSessionDB(EDataAccessLayers dal) {
		return instances[dal.ordinal()].sessionDB;
	}
	
	public static ISessionDB getSessionDB() {
		return getSessionDB(DEFAULT_DAL);
	}
	
	
	// UserDB
	/////////
	
	public static IUserDB getUserDB(EDataAccessLayers dal) {
		return instances[dal.ordinal()].userDB;
	}
	
	public static IUserDB getUserDB() {
		return getUserDB(DEFAULT_DAL);
	}
	
}
