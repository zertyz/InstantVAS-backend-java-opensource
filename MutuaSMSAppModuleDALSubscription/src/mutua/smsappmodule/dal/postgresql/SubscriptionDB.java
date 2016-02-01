package mutua.smsappmodule.dal.postgresql;

import java.sql.SQLException;
import java.sql.Timestamp;

import mutua.smsappmodule.dal.ISubscriptionDB;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.SubscriptionDto.ESubscriptionChannel;
import mutua.smsappmodule.dto.SubscriptionDto.EUnsubscriptionChannel;
import mutua.smsappmodule.dto.UserDto;
import adapters.JDBCAdapter;

import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterSubscription.SubscriptionDBStatements.*;
import static mutua.smsappmodule.dal.postgresql.SMSAppModulePostgreSQLAdapterSubscription.Parameters.*;

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
		dba.invokeUpdateProcedure(ResetTable);
	}

	@Override
	public SubscriptionDto getSubscriptionRecord(UserDto user) throws SQLException {
		Object[] row = dba.invokeRowProcedure(SelectSubscriptionByUser, USER_ID, user.getUserId());
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
		if (subscription.getIsSubscribed()) {
			dba.invokeRowProcedure(AssertSubscribed,
				USER_ID, subscription.getUser().getUserId(),
				CHANNEL, subscription.getSubscriptionChannel().name());
		} else {
			dba.invokeRowProcedure(AssertUnsubscribed,
				USER_ID, subscription.getUser().getUserId(),
				CHANNEL, subscription.getSubscriptionChannel().name());
		}
		return true;
	}
}