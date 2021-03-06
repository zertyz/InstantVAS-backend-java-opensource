package mutua.smsappmodule.smslogic;

import java.sql.SQLException;

import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandMessageDto;
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
	protected static CommandAnswerDto getNewStateReplyCommandAnswer(SessionModel session, String newState, String replyMTText) {
		CommandMessageDto commandMessage = new CommandMessageDto(replyMTText, null);
		session.setNavigationState(newState);
		return new CommandAnswerDto(commandMessage, session);
	}

	/** refactored method to be used when a command generates not only a "same state" reply, but also a message to
	 *  another user */
	protected static CommandAnswerDto getSameStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(String replyMTText, UserDto anotherUser, String anotherMTText) {
		CommandMessageDto reply          = new CommandMessageDto(replyMTText, null);
		CommandMessageDto anotherMessage = new CommandMessageDto(anotherUser.getPhoneNumber(), anotherMTText, null);
		return new CommandAnswerDto(new CommandMessageDto[] {reply, anotherMessage}, null);
	}
	
	/** refactored method to be used when a command generates a reply with a change in the navigation state which requires another user
	 *  to be notified */
	protected static CommandAnswerDto getNewStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(SessionModel session, String newState,
	                                                                                                    String replyMTText, UserDto anotherUser, String anotherMTText) {
		session.setNavigationState(newState);
		CommandMessageDto reply          = new CommandMessageDto(replyMTText, null);
		CommandMessageDto anotherMessage = new CommandMessageDto(anotherUser.getPhoneNumber(), anotherMTText, null);
		return new CommandAnswerDto(new CommandMessageDto[] {reply, anotherMessage}, null);
	}

	
	/** Implements rules like warning users that a match was cancelled by commands who would force the user from quitting the playing state (invite, list, ...),
	 *  possibly issueing messages to both users and taking other actions as well. */
	protected static CommandMessageDto[] applySessionTransitionRules(SessionModel currentSession, String newNavigationState) throws SQLException {
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
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionModel currentSession, CommandMessageDto[] commandMessages, String newNavigationState, ISessionProperty parameter, String parameterValue) throws SQLException {
		CommandMessageDto[] transitionMessages = applySessionTransitionRules(currentSession, newNavigationState);
		currentSession.setNavigationState(newNavigationState);
		currentSession.setProperty(parameter, parameterValue);
		return new CommandAnswerDto(addCommandMessages(transitionMessages, commandMessages), currentSession);
	}
	
	/** Get a new command answer, applying session transition rules */
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionModel currentSession, CommandMessageDto[] commandMessages, String newNavigationState) throws SQLException {
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

	protected static CommandAnswerDto getNewCommandAnswerDto(SessionModel currentSession, CommandMessageDto commandMessage, String newNavigationState) throws SQLException {
		return getNewCommandAnswerDto(currentSession, new CommandMessageDto[] {commandMessage}, newNavigationState);
	}
	
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionModel currentSession, CommandMessageDto commandMessage, String newNavigationState, ISessionProperty parameter, String parameterValue) throws SQLException {
		return getNewCommandAnswerDto(currentSession, new CommandMessageDto[] {commandMessage}, newNavigationState, parameter, parameterValue);
	}
	
	
}
