package mutua.smsappmodule.smslogic;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationSubscription.*;
import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription.*;
import static mutua.smsappmodule.smslogic.CommandCommons.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription.*;

import java.sql.SQLException;

import mutua.smsappmodule.config.SMSAppModuleConfiguration;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.ISubscriptionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactorySubscription;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.SubscriptionDto.ESubscriptionChannel;
import mutua.smsappmodule.dto.SubscriptionDto.EUnsubscriptionChannel;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.subscriptionengine.SubscriptionEngine.ESubscriptionOperationStatus;
import mutua.subscriptionengine.SubscriptionEngine.EUnsubscriptionOperationStatus;

/** <pre>
 * SMSAppModuleCommandsSubscription.java
 * =====================================
 * (created by luiz, Jul 22, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Subscription" 'MutuaSMSAppModule' implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Command Processors design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleCommandsSubscription implements ICommandProcessor {
	
	cmdStartDoubleOptinProcess {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getNewStateReplyCommandAnswer(session, nstAnsweringDoubleOptin, getDoubleOptinStart());
		}
	},
	
	cmdSubscribe {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			UserDto user = session.getUser();
			ESubscriptionOperationStatus subscriptionStatus = subscriptionEngine.subscribeUser(user.getPhoneNumber(), subscriptionToken);
			if ((subscriptionStatus == ESubscriptionOperationStatus.OK) ||
			    (subscriptionStatus == ESubscriptionOperationStatus.ALREADY_SUBSCRIBED)) {
				subscriptionDB.setSubscriptionRecord(new SubscriptionDto(user, ESubscriptionChannel.SMS));
				return getNewStateReplyCommandAnswer(session, nstExistingUser, getSuccessfullySubscribed());
			} else {
				return getSameStateReplyCommandAnswer(getCouldNotSubscribe());
			}
		}
	},
	
	cmdUnsubscribe {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			UserDto user = session.getUser();
			EUnsubscriptionOperationStatus unsubscriptionStatus = subscriptionEngine.unsubscribeUser(user.getPhoneNumber(), subscriptionToken);
			if ((unsubscriptionStatus == EUnsubscriptionOperationStatus.OK) ||
			    (unsubscriptionStatus == EUnsubscriptionOperationStatus.NOT_SUBSCRIBED)) {
				subscriptionDB.setSubscriptionRecord(new SubscriptionDto(user, EUnsubscriptionChannel.SMS));
				return getNewStateReplyCommandAnswer(session, nstNewUser, getUserRequestedUnsubscriptionNotification());
			} else {
				throw new RuntimeException("For some reason, the user could not be unsubscribed");
			}
		}
	},
	
	cmdDoNotAgreeToSubscribe {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getNewStateReplyCommandAnswer(session, nstNewUser, getDisagreeToSubscribe());
		}
	},
	
	;
	
	@Override
	// this.name is the enumeration property name
	public String getCommandName() {
		return this.name();
	}
	

	// database access
	//////////////////
	
	private static IUserDB         userDB         = SMSAppModuleDALFactory.DEFAULT_DAL.getUserDB();
	private static ISessionDB      sessionDB      = SMSAppModuleDALFactory.DEFAULT_DAL.getSessionDB();
	private static ISubscriptionDB subscriptionDB = SMSAppModuleDALFactorySubscription.DEFAULT_DAL.getSubscriptionDB();
	
	
	// public methods
	/////////////////
	
	// TODO assert that when celltick sends us an unsubscribe request, we do (or do not) call them back
	public static boolean unsubscribeUser(String phone) throws SQLException {
		UserDto user = userDB.assureUserIsRegistered(phone);
		EUnsubscriptionOperationStatus unsubscriptionStatus = subscriptionEngine.unsubscribeUser(phone, subscriptionToken);
		if ((unsubscriptionStatus == EUnsubscriptionOperationStatus.OK) ||
		    (unsubscriptionStatus == EUnsubscriptionOperationStatus.NOT_SUBSCRIBED)) {
			subscriptionDB.setSubscriptionRecord(new SubscriptionDto(user, EUnsubscriptionChannel.API));
			// optimally set the navigation state for an unregistered user
			sessionDB.assureProperty(user, SessionModel.NAVIGATION_STATE_PROPERTY.getPropertyName(), nstNewUser.getNavigationStateName());
			return true;
		}
		return false;
	}
	
	// TODO assert that when celltick sends us a subscribe request, we do (or do not) call them back
	public static boolean subscribeUser(String phone) throws SQLException {
		UserDto user = userDB.assureUserIsRegistered(phone);
		ESubscriptionOperationStatus subscriptionStatus = subscriptionEngine.subscribeUser(phone, subscriptionToken);
		if ((subscriptionStatus == ESubscriptionOperationStatus.OK) ||
		    (subscriptionStatus == ESubscriptionOperationStatus.ALREADY_SUBSCRIBED)) {
			subscriptionDB.setSubscriptionRecord(new SubscriptionDto(user, ESubscriptionChannel.SMS));
			sessionDB.assureProperty(user, SessionModel.NAVIGATION_STATE_PROPERTY.getPropertyName(), nstExistingUser.getNavigationStateName());
			return true;
		}
		return false;
	}
		
	
	// SMSAppModuleCommandCommons candidates
	////////////////////////////////////////
	
	

	
	/***********************************************************************
	** GLOBAL COMMAND TRIGGERS -- to be used in several navigation states **
	***********************************************************************/
	
	/** {@link #nstNotYetSubscribed} triggers that activates {@link #cmdStartDoubleOptinProcess} */
	public static String[] trgLocalStartDoubleOptin  = {SMSAppModuleConfiguration.APPName};
	/** {@link #nstAnsweringDoubleOptin} triggers that activates {@link #cmdSubscribe} */
	public static String[] trgLocalAcceptDoubleOptin = {"YES"};
	/** {@link #nstAnsweringDoubleOptin} triggers that activates {@link #cmdDoNotAgreeToSubscribe} */
	public static String[] trgLocalRefuseDoubleOptin = {"NO"};
	/** global triggers that activates {@link #cmdUnsubscribe} */
	public static String[] trgGlobalUnsubscribe      = {"UNSUBSCRIBE", "QUIT", "EXIT"};
	
}
