package mutua.hangmansmsgame.smslogic;

import static mutua.hangmansmsgame.smslogic.SMSProcessorInstrumentationMethods.*;

import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.hangmansmsgame.dispatcher.MessageDispatcher;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandInvocationDto;
import mutua.smsappmodule.smslogic.commands.CommandMessageDto;
import mutua.smsappmodule.smslogic.commands.CommandMessageDto.EResponseMessageType;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.NavigationState;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * SMSProcessor.java
 * =================
 * (created by luiz, Dec 19, 2014)
 *
 * Class responsible for receiving input SMSes and producing output SMSes
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SMSProcessor {

	
	private static SMSAppModuleDALFactory BASE_MODULE_DAL;
	
	// TODO put on the configurable class pattern format
	// na verdade, todos os valores do construtor podem vir pra cá. De outro modo, não fica complicado
	// criar os workers? Os parâmetros terão que dar a volta ao mundo... não?
	public static void configureDefaultValuesForNewInstances(SMSAppModuleDALFactory baseModuleDAL) {
		BASE_MODULE_DAL = baseModuleDAL;
		Instrumentation.reportDebug(SMSProcessor.class.getCanonicalName() + ": new configuration loaded.");
	}
	
	private final IUserDB    userDB         = BASE_MODULE_DAL.getUserDB();
	private final ISessionDB sessionDB      = BASE_MODULE_DAL.getSessionDB();
	
	// message dispatchers
	//////////////////////
	
	private final MessageDispatcher   mtDispatcher;
	private final NavigationState[]   navigationStates;
	private final ICommandProcessor[] commandProcessors;
		
	
	/***********************
	** PROCESSING METHODS **
	***********************/
	
	/** Gets a processor instance which will deliver Output SMSes (MT's) to the 
	 * provided 'interactionReceiver' MessageReceiver instance */
	public SMSProcessor(IResponseReceiver defaultReceiver, NavigationState[][] navigationStatesArrays, ICommandProcessor[][] commandProcessorsArrays) {
		mtDispatcher          = new MessageDispatcher(defaultReceiver);
		Instrumentation.reportDebug("SMSProcessor started with navigation states '"+Arrays.deepToString(navigationStatesArrays)+"' and commands '"+Arrays.deepToString(commandProcessorsArrays)+"'");
				
		// rearrange for 'commandProcessors'
		ArrayList<ICommandProcessor> tempCmdProcessors = new ArrayList<ICommandProcessor>();
		for (ICommandProcessor[] commandProcessorsEntry : commandProcessorsArrays) {
			if (commandProcessorsEntry != null) {
				tempCmdProcessors.addAll(Arrays.asList(commandProcessorsEntry));
			}
		}
		this.commandProcessors = tempCmdProcessors.toArray(new ICommandProcessor[tempCmdProcessors.size()]);
		
		// rearrange for 'navigationStates', resolving the temporary Object[][] command triggers data
		ArrayList<NavigationState> tempNavStates = new ArrayList<NavigationState>();
		for (NavigationState[] navigationStatesEntry : navigationStatesArrays) {
			if (navigationStatesEntry != null) {
				for (NavigationState navigationState : navigationStatesEntry) {
					navigationState.applyCommandTriggersData(commandProcessors);
					tempNavStates.add(navigationState);
				}
			}
		}
		this.navigationStates = tempNavStates.toArray(new NavigationState[tempNavStates.size()]);
		
	}
	
	/** from the available options (i.e., the commands that belongs to the current 'userState') determine which one of them
	 *  should process the 'incomingText' and build the object needed to issue the call */
	protected CommandInvocationDto resolveInvocationHandler(NavigationState state, String incomingText) {
		CommandTriggersDto[] commandTriggers = state.getCommandTriggers();
		if (commandTriggers == null) {
			throw new RuntimeException("CommandTriggers for state '"+state.getNavigationStateName()+"' is null");
		}
		// traverse all commands
		for (int comandIndex=0; comandIndex<commandTriggers.length; comandIndex++) {
			ICommandProcessor command = commandTriggers[comandIndex].getCommand();
			String[] patterns = commandTriggers[comandIndex].getPatterns();
			// try to match 'incomingText' against each pattern
			for (int patternIndex=0; patternIndex<patterns.length; patternIndex++) {
				String regularExpression = patterns[patternIndex];
				Pattern pattern = Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher m = pattern.matcher(incomingText);
				// build the invocation object
				if (m.matches()) {
					// the parameters
					ArrayList<String> parameters = new ArrayList<String>(m.groupCount());
					// rules:
					// discard item 0 -- the matched string
					// do not insert null items
					for (int i=0; i<m.groupCount(); i++) {
						String parameter = m.group(i+1);
						if (parameter != null) {
							parameters.add(parameter.trim());
						}
					}
					return new CommandInvocationDto(command, parameters.toArray(new String[parameters.size()]));
				}
			}
		}
		return null;
	}
	
	/** invoke the command determined as the one to process the 'incomingSMS' */
	protected CommandAnswerDto invokeCommand(CommandInvocationDto invocationHandler, SessionModel session, String incomingPhone, ESMSInParserCarrier carrier) {
		ICommandProcessor commandProcessor = invocationHandler.getCommand();
		String[] parameters = invocationHandler.getParameters();
		CommandAnswerDto commandAnswer;
		try {
			commandAnswer = commandProcessor.processCommand(session, carrier, parameters);
			reportAnswerFromCommand(incomingPhone, invocationHandler, commandAnswer);
		} catch (Throwable t) {
			// in case of error...
			Instrumentation.reportThrowable(t, "Error processing command {"+invocationHandler.toString()+"} for userPhone {"+incomingPhone+"}");
			CommandMessageDto message = new CommandMessageDto(incomingPhone,
				// turn this into a phrase of 'MutuaSMSAppModule'
				"Desculpe o transtorno mas sua mensagem nao pode ser processada agora. Por favor, tente novamente mais tarde",
				EResponseMessageType.ERROR);
			commandAnswer = new CommandAnswerDto(message, null);
		}
		return commandAnswer;
	}
	
	/** route the SMS response of a command to the appropriate dispatcher */
	protected void routeMessages(CommandMessageDto[] responseMessages, IncomingSMSDto incomingSMS) {
		try {
//			if ((response_messages != null) && (response_messages[0].getType() == EResponseMessageType.NEWS_INCENTIVE)) {
//				// broadcast
//				broadcast_dispatcher.dispatchMessage(command_response, incoming_sms);
//			}
//			else {
				// normal interaction
				mtDispatcher.dispatchMessage(responseMessages, incomingSMS);
//			}
		} catch (Throwable t) {
			throw new RuntimeException("Cannot dispatch a message in response to {phone='"+incomingSMS.getPhone()+"', text='"+incomingSMS.getText()+"'}", t);
		}
	}
	
	private class NavigationStateAwareSessionModel extends SessionModel {
		public NavigationStateAwareSessionModel(UserDto user, IncomingSMSDto MO) {
			super(user, MO);
		}
		public NavigationStateAwareSessionModel(SessionDto sessionDto, IncomingSMSDto MO) {
			super(sessionDto, MO);
		}

		@Override
		public NavigationState getNavigationStateFromStateName(String navigationStateName) {
			for (NavigationState navigationState : navigationStates) {
				if (navigationState.getNavigationStateName().equals(navigationStateName)) {
					return navigationState;
				}
			}
			throw new RuntimeException(
				"NavigationState named '" + navigationStateName +
				"' (referenced in the 'Sessions' table) is not present on the provided navigation states list '" +
				Arrays.toString(navigationStates) + "'");
		}
		
	}
	private SessionModel resolveUserSession(IncomingSMSDto MO) {
		String phone = MO.getPhone();
		String text  = MO.getText();
		UserDto      user;
		SessionModel session;
		try {
			user                  = userDB.assureUserIsRegistered(phone);
			SessionDto sessionDto = sessionDB.getSession(user);
			// new user
			if (sessionDto == null) {
				session = new NavigationStateAwareSessionModel(user, MO);
				session.setNavigationState(nstNewUser);
				reportRequestFromNewUser(phone, text);
			} else {
				session = new NavigationStateAwareSessionModel(sessionDto, MO);
				reportRequestFromExistingUser(phone, text, session.getNavigationState());
			}
		} catch (Exception e) {
			throw new RuntimeException("Database communication problem: cannot retrieve the session for user '"+phone+"'", e);
		}
		return session;
	}
	
	public void process(IncomingSMSDto MO) throws SMSProcessorException {
		
		String incomingPhone = MO.getPhone();
		String incomingText = MO.getText();
		
		// get the user state
		SessionModel session = resolveUserSession(MO);
			
		// determine which command (and arguments) to call
		CommandInvocationDto invocationHandler = resolveInvocationHandler(session.getNavigationState(), incomingText);
		if (invocationHandler != null) {
			
			// execute
			CommandAnswerDto commandResponse = invokeCommand(invocationHandler, session, MO.getPhone(), MO.getCarrier());
			
			if (commandResponse != null) {
				// route messages
				routeMessages(commandResponse.getResponseMessages(), MO);
				// set the user state
				SessionDto newUserSession = session.getChangedSessionDto();
				if (newUserSession != null) {
					Instrumentation.reportDebug("Setting new user session: " + newUserSession);
					try {
						sessionDB.setSession(newUserSession);
					} catch (SQLException e) {
						throw new RuntimeException("Database communication problem: cannot store the session for user '"+incomingPhone+"'", e);
					}
				} else {
					Instrumentation.reportDebug("Not setting a new user session");
				}
			}
		} else {
			throw new RuntimeException("The incoming message '" + incomingText + "', belonging to the state '" +
			                           session.getNavigationState().getNavigationStateName() + "' doesn't match " +
			                           "any of the commands listed on it's commands & triggers list.");
		}

	}
}