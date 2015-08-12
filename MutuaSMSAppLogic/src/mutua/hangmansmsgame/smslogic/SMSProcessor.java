package mutua.hangmansmsgame.smslogic;

import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;
import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;
import static mutua.icc.instrumentation.SMSProcessorInstrumentationEvents.*;
import static mutua.icc.instrumentation.SMSProcessorInstrumentationProperties.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mutua.events.annotations.EventConsumer;
import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.hangmansmsgame.dispatcher.MessageDispatcher;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.SMSProcessorInstrumentationEvents;
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
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
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

	
	// databases
	////////////
	
	private static ISessionDB sessionDB = SMSAppModuleDALFactory.DEFAULT_DAL.getSessionDB();
	private static IUserDB    userDB    = SMSAppModuleDALFactory.DEFAULT_DAL.getUserDB();
	
	
	// message dispatchers
	//////////////////////
	
	private MessageDispatcher mtDispatcher;
	
	
	private Instrumentation<?, ?> log;
	private INavigationState[]    navigationStates;
	
	
	/***********************
	** PROCESSING METHODS **
	***********************/
	
	/** Gets a processor instance which will deliver Output SMSes (MT's) to the 
	 * provided 'interactionReceiver' MessageReceiver instance */
	public SMSProcessor(Instrumentation<?, ?> log, IResponseReceiver defaultReceiver, INavigationState[]... navigationStatesArrays) {
		log.addInstrumentableEvents(SMSProcessorInstrumentationEvents.values());
		this.log              = log;
		mtDispatcher          = new MessageDispatcher(defaultReceiver);
		// compute the length
		int navigationStatesLength = 0;
		for (INavigationState[] navigationStatesEntry : navigationStatesArrays) {
			navigationStatesLength += navigationStatesEntry.length;
		}
		// build the 1d array
		this.navigationStates = new INavigationState[navigationStatesLength];
		int navigationStatesIndex = 0;
		for (INavigationState[] navigationStatesEntry : navigationStatesArrays) {
			for (INavigationState navigationState : navigationStatesEntry) {
				this.navigationStates[navigationStatesIndex++] = navigationState;
			}
		}
	}
	
	/** from the available options (i.e., the commands that belongs to the current 'userState') determine which one of them
	 *  should process the 'incomingText' and build the object needed to issue the call */
	protected CommandInvocationDto resolveInvocationHandler(INavigationState state, String incomingText) {
		CommandTriggersDto[] commandTriggers = state.getCommandTriggers();
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
			log.reportEvent(IE_ANSWER_FROM_COMMAND, IP_COMMAND_ANSWER, commandAnswer);
		} catch (Throwable t) {
			// in case of error...
			t.printStackTrace();
			log.reportThrowable(t, "Error processing command {"+invocationHandler.toString()+"} for userPhone {"+incomingPhone+"}");
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
	
	public void process(IncomingSMSDto incomingSMS) throws SMSProcessorException {
		
		String incomingPhone = incomingSMS.getPhone();
		String incomingText = incomingSMS.getText();
		
		// get the user state
		UserDto      user;
		SessionModel session;
		try {
			user                  = userDB.assureUserIsRegistered(incomingPhone);
			SessionDto sessionDto = sessionDB.getSession(user);
			// new user
			if (sessionDto == null) {
				session = new SessionModel(user);
				session.setNavigationState(nstNewUser);
				log.reportEvent(IE_REQUEST_FROM_NEW_USER, IP_PHONE, incomingPhone, IP_TEXT, incomingText);
			} else {
				session = new SessionModel(sessionDto);
				log.reportEvent(IE_REQUEST_FROM_EXISTING_USER, IP_PHONE, incomingPhone, IP_STATE, session.getNavigationState(), IP_TEXT, incomingText);
			}
		} catch (Exception e) {
			throw new RuntimeException("Database communication problem: cannot retrieve the session for user '"+incomingPhone+"'", e);
		}
			
		// determine which command (and arguments) to call
		CommandInvocationDto invocationHandler = resolveInvocationHandler(session.getNavigationState(), incomingText);
		if (invocationHandler != null) {
			
			log.reportEvent(IE_PROCESSING_COMMAND, IP_COMMAND_INVOCATION, invocationHandler);
			
			// execute
			CommandAnswerDto commandResponse = invokeCommand(invocationHandler, session, incomingSMS.getPhone(), incomingSMS.getCarrier());
			
			if (commandResponse != null) {
				// route messages
				routeMessages(commandResponse.getResponseMessages(), incomingSMS);
				// set the user state
				SessionDto newUserSession = session.getChangedSessionDto();
				if (newUserSession != null) {
					log.reportEvent(DIE_DEBUG, DIP_MSG, "Setting new user session: " + newUserSession);
					try {
						sessionDB.setSession(newUserSession);
					} catch (SQLException e) {
						throw new RuntimeException("Database communication problem: cannot store the session for user '"+incomingPhone+"'", e);
					}
				} else {
					log.reportEvent(DIE_DEBUG, DIP_MSG, "Not setting a new user session");
				}
			}
		} else {
			throw new RuntimeException("The incoming message '" + incomingText + "', belonging to the state '" +
			                           session.getNavigationState().getNavigationStateName() + "' doesn't match " +
			                           "any of the commands listed on it's commands & triggers list.");
		}

	}
}
