package mutua.smsappmodule.smslogic;

import java.sql.SQLException;

import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandMessageDto;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.sessions.ISessionProperty;
import mutua.smsappmodule.smslogic.sessions.SessionModel;

/** <pre>
 * CommandCommons.java
 * ===================
 * (created by luiz, Jul 16, 2015)
 *
 * Refactored functionalities common to module commands
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class CommandCommons {
	
	
	/************************************
	** CommandAnswerDto HELPER METHODS **
	************************************/
	
	/** refactored method to be used when a command does not change the navigation state and
	 * only generates a reply (an MT to the same sender as the MO) */
	protected static CommandAnswerDto getSameStateReplyCommandAnswer(String replyMTText) {
		CommandMessageDto commandMessage = new CommandMessageDto(replyMTText, null);
		return new CommandAnswerDto(commandMessage, null);
	}
	
	/** refactored method to be used when a command changes the navigation state and
	 * only generates a reply (an MT to the same sender as the MO) */
	protected static CommandAnswerDto getNewStateReplyCommandAnswer(SessionModel session, INavigationState newState, String replyMTText) {
		CommandMessageDto commandMessage = new CommandMessageDto(replyMTText, null);
		session.setNavigationState(newState);
		return new CommandAnswerDto(commandMessage, session);
	}
	
	/** Implements rules like warning users that a match was cancelled by commands who would force the user from quitting the playing state (invite, list, ...),
	 *  possibly issueing messages to both users and taking other actions as well. */
	protected static CommandMessageDto[] applySessionTransitionRules(SessionModel currentSession, INavigationState newNavigationState) throws SQLException {
		return null;
	}
	
	/** Adds two arrays */
	protected static CommandMessageDto[] addCommandMessages(CommandMessageDto[] a1, CommandMessageDto[] a2) {
		if (a1 == null) {
			return a2;
		} else if (a2 == null) {
			return a1;
		} else {
			CommandMessageDto[] combinedMessages = new CommandMessageDto[a1.length + a2.length];
			System.arraycopy(a1, 0, combinedMessages, 0, a1.length);
			System.arraycopy(a2, 0, combinedMessages, a1.length, a2.length);
			return combinedMessages;
		}
	}
	
	/** Get a new command answer, applying session transition rules */
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionModel currentSession, CommandMessageDto[] commandMessages, INavigationState newNavigationState, ISessionProperty parameter, String parameterValue) throws SQLException {
		CommandMessageDto[] transitionMessages = applySessionTransitionRules(currentSession, newNavigationState);
		currentSession.setNavigationState(newNavigationState);
		currentSession.setProperty(parameter, parameterValue);
		return new CommandAnswerDto(addCommandMessages(transitionMessages, commandMessages), currentSession);
	}
	
	/** Get a new command answer, applying session transition rules */
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionModel currentSession, CommandMessageDto[] commandMessages, INavigationState newNavigationState) throws SQLException {
		CommandMessageDto[] transitionMessages = applySessionTransitionRules(currentSession, newNavigationState);
		currentSession.setNavigationState(newNavigationState);
		return new CommandAnswerDto(addCommandMessages(transitionMessages, commandMessages), currentSession);
	}

	protected static CommandAnswerDto getNewCommandAnswerDto(SessionModel currentSession, CommandMessageDto[] commandMessages) throws SQLException {
		return new CommandAnswerDto(commandMessages, null);
	}

	// overloads with a single command message
	//////////////////////////////////////////
	
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionModel currentSession, CommandMessageDto commandMessage) throws SQLException {
		return getNewCommandAnswerDto(currentSession, new CommandMessageDto[] {commandMessage});
	}

	protected static CommandAnswerDto getNewCommandAnswerDto(SessionModel currentSession, CommandMessageDto commandMessage, INavigationState newNavigationState) throws SQLException {
		return getNewCommandAnswerDto(currentSession, new CommandMessageDto[] {commandMessage}, newNavigationState);
	}
	
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionModel currentSession, CommandMessageDto commandMessage, INavigationState newNavigationState, ISessionProperty parameter, String parameterValue) throws SQLException {
		return getNewCommandAnswerDto(currentSession, new CommandMessageDto[] {commandMessage}, newNavigationState, parameter, parameterValue);
	}
	
	
}
