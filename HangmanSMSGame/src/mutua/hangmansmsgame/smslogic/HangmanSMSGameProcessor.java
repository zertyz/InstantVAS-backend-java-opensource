package mutua.hangmansmsgame.smslogic;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mutua.hangmansmsgame.config.Configuration.log;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents.*;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationProperties.*;
import static mutua.icc.instrumentation.DefaultInstrumentationEvents.*;
import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.ISessionDB;
import mutua.hangmansmsgame.dal.dto.SessionDto;
import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.hangmansmsgame.dispatcher.MessageDispatcher;
import mutua.hangmansmsgame.i18n.IPhraseology;
import mutua.hangmansmsgame.smslogic.NavigationMap.ECOMMANDS;
import mutua.hangmansmsgame.smslogic.NavigationMap.ESTATES;
import mutua.hangmansmsgame.smslogic.commands.ICommandProcessor;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandAnswerDto;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandInvocationDto;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandMessageDto;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandMessageDto.EResponseMessageType;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandTriggersDto;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * HangmanSMSGameProcessor.java
 * ============================
 * (created by luiz, Dec 19, 2014)
 *
 * Class responsible for receiving input SMSes and producing output SMSes for the
 * Hangman SMS Game
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HangmanSMSGameProcessor {

	// databases
	////////////
	
	private static ISessionDB userSessionDB = DALFactory.getSessionDB();
	
	// phraseology
	//////////////
	
	private static IPhraseology phrases;
	
	// message dispatchers
	//////////////////////
	
	private MessageDispatcher mtDispatcher;
	
	
	/***********************
	** PROCESSING METHODS **
	***********************/
	
	/*
	 * Gets a processor instance which will deliver Output SMSes (MT's) to the 
	 * provided 'interactionReceiver' MessageReceiver instance
	 */
	public HangmanSMSGameProcessor(IResponseReceiver defaultReceiver) {
		mtDispatcher = new MessageDispatcher(defaultReceiver);
	}
	
	/** from the available options (i.e., the commands that belongs to the current 'userState') determine which one of them
	 *  should process the 'incomingText' and build the object needed to issue the call
	 */
	protected CommandInvocationDto resolveInvocationHandler(ESTATES state, String incomingText) {
		
		CommandTriggersDto[] commandPatterns = state.getCommandPatterns();
		// traverse all commands
		for (int comandIndex=0; comandIndex<commandPatterns.length; comandIndex++) {
			ECOMMANDS command = commandPatterns[comandIndex].getCommand();
			String[] patterns = commandPatterns[comandIndex].getPatterns();
			// try to match 'incomingText' against each pattern
			for (int patternIndex=0; patternIndex<patterns.length; patternIndex++) {
				String regularExpression = patterns[patternIndex];
				Pattern pattern = Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE);
				Matcher m = pattern.matcher(incomingText);
				// build the invocation object
				if (m.matches()) {
					ICommandProcessor commandProcessor = command.getCommandProcessor();
					// the parameters
					ArrayList<String> parameters = new ArrayList<String>();
					// rules:
					// discard item 0 -- the matched string
					// do not insert null items
					for (int i=0; i<m.groupCount(); i++) {
						String parameter = m.group(i+1);
						if (parameter != null) {
							parameters.add(parameter.trim());
						}
					}
					return new CommandInvocationDto(command, commandProcessor, parameters.toArray(new String[] {}));
				}
			}
		}
		return null;
	}
	
	/*
	 *  invoke the command determined as the one to process the 'incomingSMS'
	 */
	protected CommandAnswerDto invokeCommand(CommandInvocationDto invocationHandler, SessionDto userSession, IncomingSMSDto incomingSMS) {
		ICommandProcessor commandProcessor = invocationHandler.getCommandProcessor();
		String[] parameters = invocationHandler.getParameters();
		String incomingPhone = incomingSMS.getPhone();
		String incomingText = incomingSMS.getText();
		CommandAnswerDto commandAnswer;
		try {
			ESMSInParserCarrier carrier = incomingSMS.getCarrier();
			commandAnswer = commandProcessor.processCommand(userSession, carrier, parameters, IPhraseology.getCarrierSpecificPhraseology(carrier));
			log.reportEvent(IE_ANSWER_FROM_COMMAND, IP_COMMAND_ANSWER, commandAnswer);
		} catch (Throwable t) {
			// in case of error...
			//Instrumentation.reportSevereError("BlocosDeCarnaval: Error processing message '"+incoming_text+"' from phone '"+incoming_phone+"'", t);
			t.printStackTrace();
			CommandMessageDto message = new CommandMessageDto(incomingPhone,
				"Desculpe o transtorno mas sua mensagem nao pode ser processada agora. Por favor, tente novamente mais tarde",
				EResponseMessageType.ERROR);
			commandAnswer = new CommandAnswerDto(message, null);
		}
		return commandAnswer;
	}
	
	/*
	 *  route the SMS response of a command to the appropriate dispatcher 
	 */
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
		SessionDto userSession;
		try {
			userSession = userSessionDB.getSession(incomingPhone);
			// new user
			if (userSession == null) {
				log.reportEvent(IE_REQUEST_FROM_NEW_USER, IP_PHONE, incomingPhone);
				userSession = new SessionDto(incomingPhone, "NEW_USER");
			} else {
				log.reportEvent(IE_REQUEST_FROM_EXISTING_USER, IP_PHONE, incomingPhone);
			}
		} catch (Exception e) {
			throw new RuntimeException("Database comunication problem: cannot retrieve state for user '"+incomingPhone+"'", e);
		}
			
		// determine which command (and arguments) to call
		CommandInvocationDto invocationHandler = resolveInvocationHandler(ESTATES.valueOf(userSession.getNavigationState()), incomingText);
		if (invocationHandler != null) {
			
			log.reportEvent(IE_PROCESSING_COMMAND, IP_COMMAND_INVOCATION, invocationHandler);
			
			// execute
			CommandAnswerDto commandResponse = invokeCommand(invocationHandler, userSession, incomingSMS);
			
			if (commandResponse != null) {
				// set the user state
				SessionDto newUserSession = commandResponse.getUserSession();
				if (newUserSession != null) {
					log.reportEvent(DIE_DEBUG, DIP_MSG, "Setting new user session: " + newUserSession);
					userSessionDB.setSession(newUserSession);
				} else {
					log.reportEvent(DIE_DEBUG, DIP_MSG, "Not setting a new user session");
				}
				// route messages
				routeMessages(commandResponse.getResponseMessages(), incomingSMS);
			}
		} else {
			throw new RuntimeException("The incoming message '" + incomingText +
			                           "', belonging to " +
			                           " the state '" + userSession.getNavigationState() + "' doesn't match " +
			                           "with none of the commands listed in the 'NavigationMap'.");
		}

	}
}
