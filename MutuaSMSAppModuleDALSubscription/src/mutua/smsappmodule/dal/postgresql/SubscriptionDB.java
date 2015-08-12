package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;
import java.sql.Timestamp;

import mutua.smsappmodule.dal.ISubscriptionDB;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.SubscriptionDto.ESubscriptionChannel;
import mutua.smsappmodule.dto.SubscriptionDto.EUnsubscriptionChannel;
import mutua.smsappmodule.dto.UserDto;
import adapters.JDBCAdapter;
import adapters.dto.PreparedProcedureInvocationDto;

/** <pre>
 * SubscriptionDB.java
 * ===================
 * (created by luiz, Jul 24, 2015)
 *
 * Implements the POSTGRESQL version of {@link ISubscriptionDB}
 *
 * @see ISubscriptionDB
 * @version $Id$
 * @author luiz
 */

public class SubscriptionDB implements ISubscriptionDB {
	
	private JDBCAdapter dba;
	
	
	public SubscriptionDB() throws SQLException {
		dba = SMSAppModulePostgreSQLAdapterSubscription.getSubscriptionDBAdapter();
	}

	@Override
	public void reset() throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("ResetTable");
		dba.invokeUpdateProcedure(procedure);
	}

	@Override
	public SubscriptionDto getSubscriptionRecord(UserDto user) throws SQLException {
		PreparedProcedureInvocationDto procedure = new PreparedProcedureInvocationDto("SelectSubscriptionByUser");
		procedure.addParameter("USER_ID", user.getUserId());
		Object[] row = dba.invokeRowProcedure(procedure);
		if (row == null) {
			return null;
		}
		int     userId                = (Integer)   row[0];
		boolean isSubscribed          = (Boolean)   row[1];
		long    lastBilling           = ((Timestamp)row[2]).getTime();
		String  subscriptionChannel   = (String)    row[3];
		String  unsubscriptionChannel = (String)    row[4];
		SubscriptionDto subscription;
		if (isSubscribed) {
			subscription = new SubscriptionDto(user, ESubscriptionChannel.valueOf(subscriptionChannel));
		} else {
			subscription = new SubscriptionDto(user, EUnsubscriptionChannel.valueOf(unsubscriptionChannel));
		}
		return subscription;
	}

	@Override
	public boolean setSubscriptionRecord(SubscriptionDto subscription) throws SQLException {
		PreparedProcedureInvocationDto procedure;
		if (subscription.getIsSubscribed()) {
			procedure = new PreparedProcedureInvocationDto("AssertSubscribed");
			procedure.addParameter("CHANNEL", subscription.getSubscriptionChannel().name());
		} else {
			procedure = new PreparedProcedureInvocationDto("AssertUnsubscribed");
			procedure.addParameter("CHANNEL", subscription.getUnsubscriptionChannel().name());
		}
		procedure.addParameter("USER_ID", subscription.getUser().getUserId());
		dba.invokeRowProcedure(procedure);
		return true;
	}
}