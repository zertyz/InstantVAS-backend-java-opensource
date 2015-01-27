package mutua.hangmansmsgame.dal;

import java.sql.SQLException;

import mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters;
import adapters.PostgreSQLAdapter;

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
	public static EDataAccessLayers DEFAULT_DAL = EDataAccessLayers.POSTGRESQL;

	// the multiton instances
	private static final DALFactory[] instances = new DALFactory[EDataAccessLayers.values().length];

	// the databases
	private ISessionDB sessionDB;
	private IUserDB    userDB;
	private IMatchDB   matchDB;
	
	
	private DALFactory(EDataAccessLayers dal) throws SQLException {
		
		switch (dal) {
			case RAM:
				sessionDB = new mutua.hangmansmsgame.dal.ram.SessionDB();
				userDB    = new mutua.hangmansmsgame.dal.ram.UserDB();
				matchDB   = new mutua.hangmansmsgame.dal.ram.MatchDB();
				break;
			case POSTGRESQL:
				userDB    = new mutua.hangmansmsgame.dal.postgresql.UserDB();
				break;
			default:
				throw new RuntimeException("Don't know how to build a '"+dal.name()+"' DAL instance");
		}
		
	}
	
	private static DALFactory getInstance(EDataAccessLayers dal) {
		try {
			if (instances[dal.ordinal()] == null) {
				instances[dal.ordinal()] = new DALFactory(dal);
			}
			return instances[dal.ordinal()];
		} catch (SQLException e) {
			HangmanSMSGamePostgreSQLAdapters.log.reportThrowable(e, "Error while instantiating DALFactory '"+dal.name()+"'");
			return null;
		}
	}
	
	
	// SessionDB
	////////////
	
	public static ISessionDB getSessionDB(EDataAccessLayers dal) {
		return getInstance(dal).sessionDB;
	}
	
	public static ISessionDB getSessionDB() {
		return getSessionDB(DEFAULT_DAL);
	}
	
	
	// UserDB
	/////////
	
	public static IUserDB getUserDB(EDataAccessLayers dal) {
		return getInstance(dal).userDB;
	}
	
	public static IUserDB getUserDB() {
		return getUserDB(DEFAULT_DAL);
	}
	
	
	// MatchDB
	//////////
	
	public static IMatchDB getMatchDB(EDataAccessLayers dal) {
		return getInstance(dal).matchDB;
	}
	
	public static IMatchDB getMatchDB() {
		return getMatchDB(DEFAULT_DAL);
	}
	
}
