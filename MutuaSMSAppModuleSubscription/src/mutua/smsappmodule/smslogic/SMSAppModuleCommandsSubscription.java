package mutua.smsappmodule.smslogic;

import static mutua.smsappmodule.smslogic.CommandCommons.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription.NavigationStatesNamesSubscription.*;

import java.sql.SQLException;

import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.ISubscriptionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactorySubscription;
import mutua.smsappmodule.dto.SubscriptionDto;
import mutua.smsappmodule.dto.SubscriptionDto.ESubscriptionChannel;
import mutua.smsappmodule.dto.SubscriptionDto.EUnsubscriptionChannel;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsSubscription;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesSubscription;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.subscriptionengine.SubscriptionEngine;
import mutua.subscriptionengine.SubscriptionEngine.ESubscriptionOperationStatus;
import mutua.subscriptionengine.SubscriptionEngine.EUnsubscriptionOperationStatus;

/** <pre>
 * SMSAppModuleCommandsSubscription.java
 * =====================================
 * (created by luiz, Jul 22, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Subscription" SMS Module.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the "Instant VAS SMSApp Command Processors" design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleCommandsSubscription {
	
	/** Class to be statically imported by the Configurators to refer to commands when defining the {@link CommandTriggersDto} */
	public static class CommandNamesSubscription {
		/** @see SMSAppModuleCommandsSubscription#cmdStartDoubleOptinProcess */
		public final static String cmdStartDoubleOptinProcess = "StartDoubleOptinProcess";
		/** @see SMSAppModuleCommandsSubscription#cmdSubscribe */
		public final static String cmdSubscribe               = "Subscribe";
		/** @see SMSAppModuleCommandsSubscription#cmdUnsubscribe */
		public final static String cmdUnsubscribe             = "Unsubscribe";
		/** @see SMSAppModuleCommandsSubscription#cmdDoNotAgreeToSubscribe */
		public final static String cmdDoNotAgreeToSubscribe   = "DoNotAgreeToSubscribe";
	}
	
	/** Class to be used as a reference when customizing the MO commands for this module */
	public static class CommandTriggersSubscription {
		/** Local triggers (available only to {@link SMSAppModuleNavigationStates#nstNewUser} and 'unsubscribed users') to execute the 'start double opt-in' process -- 
		 *  {@link SMSAppModuleNavigationStates#nstNewUser} triggers that activates {@link SMSAppModuleCommandsSubscription#cmdStartDoubleOptinProcess} */
		public final static String[] trgLocalStartDoubleOptin  = {"MyApp"};
		/** Local triggers (available only to the {@link SMSAppModuleNavigationStatesSubscription#nstAnsweringDoubleOptin} navigation state) to execute the 'subscribe' process --
		 *  {@link SMSAppModuleNavigationStatesSubscription#nstAnsweringDoubleOptin} triggers that activates {@link SMSAppModuleCommandsSubscription#cmdSubscribe} */
		public final static String[] trgLocalAcceptDoubleOptin = {"YES"};
		/** Local triggers (available only to the 'answering double opt-in' navigation state) to quit the 'subscription' process --
		 *  {@link SMSAppModuleNavigationStatesSubscription#nstAnsweringDoubleOptin} triggers that activates {@link SMSAppModuleCommandsSubscription#cmdDoNotAgreeToSubscribe} */
		public final static String[] trgLocalRefuseDoubleOptin = {".*"};
		/** Global triggers (to be used on several navigation states) to execute the 'unsubscribe' command -- 
		 *  {@link SMSAppModuleCommandsSubscription#cmdUnsubscribe} */
		public final static String[] trgGlobalUnsubscribe      = {"UNSUBSCRIBE", "QUIT", "EXIT"};
	}
	
	// Instance Fields
	//////////////////

	private final SMSAppModulePhrasingsSubscription subscriptionPhrases;
	private final IUserDB                        userDB;
	private final ISessionDB                     sessionDB;
	private final ISubscriptionDB                subscriptionDB;
	/** The integration module with the service responsible for managing the subscription lifecycle of each user */
	private final SubscriptionEngine             subscriptionEngine;
	/** The identifier of the billing entity to which the subscribers of this service must be assigned to */
	private final String                         subscriptionToken;
	/** The events manager for this module */
	private final SMSAppModuleEventsSubscription events;

	
	/** Constructs an instance of this module's command processors.
	 *  @param subscriptionPhrases an instance of the phrasings to be used
	 *  @param baseModulesDAL      one of the members of {@link SMSAppModuleDALFactory}
	 *  @param subscriptionDAL     one of the members of {@link SMSAppModuleDALFactorySubscription}
	 *  @param subscriptionEngine  see {@link #subscriptionEngine}
	 *  @param subscriptionToken   see {@link #subscriptionToken} */
	public SMSAppModuleCommandsSubscription(SMSAppModulePhrasingsSubscription  subscriptionPhrases,
	                                        SMSAppModuleDALFactory             baseModulesDAL,
	                                        SMSAppModuleDALFactorySubscription subscriptionDAL,
	                                        SubscriptionEngine                 subscriptionEngine,
	                                        String                             subscriptionToken) {
		this.subscriptionPhrases = subscriptionPhrases;
		this.userDB              = baseModulesDAL.getUserDB();
		this.sessionDB           = baseModulesDAL.getSessionDB();
		this.subscriptionDB      = subscriptionDAL.getSubscriptionDB();
		this.subscriptionEngine  = subscriptionEngine;
		this.subscriptionToken   = subscriptionToken;
		this.events              = new SMSAppModuleEventsSubscription();
	}

	// Command Definitions
	//////////////////////	
	
	/** Command to bring the user to an additional verification step before subscribing him/her to the service.
	 *  Receives no parameters. */
	public final ICommandProcessor cmdStartDoubleOptinProcess = new ICommandProcessor(CommandNamesSubscription.cmdStartDoubleOptinProcess) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getNewStateReplyCommandAnswer(session, nstAnsweringDoubleOptin, subscriptionPhrases.getDoubleOptinStart());
		}
	};
	
	/** Command to subscribe the user to the service, promoting him/her to the 'nstExistingUser' state, which should unlock features.
	 *  Receives no parameters. */
	public final ICommandProcessor cmdSubscribe = new ICommandProcessor(CommandNamesSubscription.cmdSubscribe) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			UserDto user = session.getUser();
			ESubscriptionOperationStatus subscriptionStatus = subscriptionEngine.subscribeUser(user.getPhoneNumber(), subscriptionToken);
			if ((subscriptionStatus == ESubscriptionOperationStatus.OK) ||
			    (subscriptionStatus == ESubscriptionOperationStatus.ALREADY_SUBSCRIBED)) {
				SubscriptionDto subscriptionRecord = new SubscriptionDto(user, ESubscriptionChannel.SMS);
				subscriptionDB.setSubscriptionRecord(subscriptionRecord);
				events.dispatchSubscriptionNotification(subscriptionRecord);
				return getNewStateReplyCommandAnswer(session, nstExistingUser, subscriptionPhrases.getSuccessfullySubscribed());
			} else {
				return getSameStateReplyCommandAnswer(subscriptionPhrases.getCouldNotSubscribe());
			}
		}
	};
	
	/** Command to cancel the user subscription from the service, bringing him/her to the 'nstNewUser' state.
	 *  Receives no parameters. */
	public final ICommandProcessor cmdUnsubscribe = new ICommandProcessor(CommandNamesSubscription.cmdUnsubscribe) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			UserDto user = session.getUser();
			EUnsubscriptionOperationStatus unsubscriptionStatus = subscriptionEngine.unsubscribeUser(user.getPhoneNumber(), subscriptionToken);
			if ((unsubscriptionStatus == EUnsubscriptionOperationStatus.OK) ||
			    (unsubscriptionStatus == EUnsubscriptionOperationStatus.NOT_SUBSCRIBED)) {
				SubscriptionDto unsubscriptionRecord = new SubscriptionDto(user, EUnsubscriptionChannel.SMS);
				subscriptionDB.setSubscriptionRecord(unsubscriptionRecord);
				events.dispatchUnsubscriptionNotification(unsubscriptionRecord);
				return getNewStateReplyCommandAnswer(session, nstNewUser, subscriptionPhrases.getUserRequestedUnsubscriptionNotification());
			} else {
				throw new RuntimeException("For some reason, the user could not be unsubscribed");
			}
		}
	};
	
	/** Command to deal with the user responding that he/she do not agree when asked to confirm the subscription attempt.
	 *  Receives no parameters. */
	public final ICommandProcessor cmdDoNotAgreeToSubscribe = new ICommandProcessor(CommandNamesSubscription.cmdDoNotAgreeToSubscribe) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getNewStateReplyCommandAnswer(session, nstNewUser, subscriptionPhrases.getDisagreeToSubscribe());
		}
	};
	
	
	// public methods
	/////////////////
	
	// TODO assert, on the old web interface, that when celltick sends us an unsubscribe request, we do (or do not) call them back
	public boolean unsubscribeUser(String phone) throws SQLException {
		UserDto user = userDB.assureUserIsRegistered(phone);
		EUnsubscriptionOperationStatus unsubscriptionStatus = subscriptionEngine.unsubscribeUser(phone, subscriptionToken);
		if ((unsubscriptionStatus == EUnsubscriptionOperationStatus.OK) ||
		    (unsubscriptionStatus == EUnsubscriptionOperationStatus.NOT_SUBSCRIBED)) {
			SubscriptionDto unsubscriptionRecord = new SubscriptionDto(user, EUnsubscriptionChannel.API);
			subscriptionDB.setSubscriptionRecord(unsubscriptionRecord);
			events.dispatchUnsubscriptionNotification(unsubscriptionRecord);
			// optimally set the navigation state for an unregistered user
			sessionDB.assureProperty(user, SessionModel.NAVIGATION_STATE_PROPERTY.getPropertyName(), nstNewUser);
			return true;
		}
		return false;
	}
	
	// TODO assert, on the old web interface, that when celltick sends us a subscribe request, we do (or do not) call them back
	public boolean subscribeUser(String phone) throws SQLException {
		UserDto user = userDB.assureUserIsRegistered(phone);
		ESubscriptionOperationStatus subscriptionStatus = subscriptionEngine.subscribeUser(phone, subscriptionToken);
		if ((subscriptionStatus == ESubscriptionOperationStatus.OK) ||
		    (subscriptionStatus == ESubscriptionOperationStatus.ALREADY_SUBSCRIBED)) {
			SubscriptionDto subscriptionRecord = new SubscriptionDto(user, ESubscriptionChannel.SMS);
			subscriptionDB.setSubscriptionRecord(subscriptionRecord);
			events.dispatchSubscriptionNotification(subscriptionRecord);
			sessionDB.assureProperty(user, SessionModel.NAVIGATION_STATE_PROPERTY.getPropertyName(), nstExistingUser);
			return true;
		}
		return false;
	}
		
	
	// SMSAppModuleCommandCommons candidates
	////////////////////////////////////////
	
	
	// Command List
	///////////////
	
	/** The list of all commands -- to allow deserialization by {@link CommandTriggersDto} */
	public final ICommandProcessor[] values = {
		cmdStartDoubleOptinProcess,
		cmdSubscribe,
		cmdUnsubscribe,
		cmdDoNotAgreeToSubscribe,
	};	
}
