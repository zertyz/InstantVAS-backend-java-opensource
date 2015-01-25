package mutua.hangmansmsgame.smslogic;

import mutua.hangmansmsgame.smslogic.commands.ICommandProcessor;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandTriggersDto;

/** <pre>
 * NavigationMap.java
 * ==================
 * (created by luiz, Dec 19, 2014)
 *
 * Defines the relations between commands and states
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class NavigationMap {

	
	/************************
	** COMMAND DEFINITIONS **
	************************/
	
	// commands are abstractions used to transform an incoming message into an outgoing one, based
	// on either the content of the message and/or on a context (state).
	
	public enum ECOMMANDS {


		// special commands
		///////////////////
		
		NO_ANSWER(CommandDetails.NO_ANSWER),

		// help commands
		////////////////
		
		SHOW_WELCOME_MESSAGE(CommandDetails.SHOW_WELCOME_MESSAGE),
		SHOW_FULL_HELP_MESSAGE(CommandDetails.SHOW_FULL_HELP_MESSAGE),
		
		
		// invitation commands
		//////////////////////

		START_INVITATION_PROCESS(CommandDetails.START_INVITATION_PROCESS),
		INVITE_NICK_OR_PHONE(CommandDetails.INVITE_NICK_OR_PHONE),
		HOLD_OPPONENT_PHONE(CommandDetails.HOLD_OPPONENT_PHONE),
		HOLD_OPPONENT_NICK(CommandDetails.HOLD_OPPONENT_NICK),
		HOLD_MATCH_WORD(CommandDetails.HOLD_MATCH_WORD),
		ACCEPT_INVITATION(CommandDetails.ACCEPT_INVITATION),
		REFUSE_INVITATION(CommandDetails.REFUSE_INVITATION),
		INVITATION_TIMEOUT(null),
		
		
		// playing commands
		///////////////////
		
		PLAY_WITH_RANDOM_USER_OR_BOT(CommandDetails.PLAY_WITH_RANDOM_USER_OR_BOT),
		SUGGEST_LETTER_OR_WORD_FOR_HUMAN(CommandDetails.SUGGEST_LETTER_OR_WORD_FOR_HUMAN),
		SUGGEST_LETTER_OR_WORD_FOR_BOT(CommandDetails.SUGGEST_LETTER_OR_WORD_FOR_BOT),
		
		
		// provocation commands
		///////////////////////
		
		PROVOKE(CommandDetails.PROVOKE),
		
		
		// profile commands
		///////////////////
		
		SHOW_PROFILE(CommandDetails.SHOW_PROFILE),
		DEFINE_NICK(CommandDetails.DEFINE_NICK),
		
		
		// list users commands
		//////////////////////
		
		LIST_USERS(CommandDetails.LIST_USERS),
		LIST_MORE_USERS(CommandDetails.LIST_MORE_USERS),
		
		
		// unsubscription commands
		//////////////////////////
		
		UNSUBSCRIBE(CommandDetails.UNSUBSCRIBE),
		
		
		;
		
		private ICommandProcessor commandProcessor;
		
		ECOMMANDS(ICommandProcessor commandProcessor) {
			this.commandProcessor = commandProcessor;
		}
		
		public ICommandProcessor getCommandProcessor() {
			return commandProcessor;
		}
	}

	
	/************************
	** COMMAND DEFINITIONS **
	************************/
	
	// Navigation states specify the dialog context between the user and the system. Each state has
	// a set of commands associated to it forming a navigation automata.

	public enum ESTATES {
		
		
		// inactive or first comers states
		//////////////////////////////////
		
		NEW_USER(StateDetails.NEW_USER),
		
		
		// loop state for existing users
		////////////////////////////////
		
		EXISTING_USER(StateDetails.EXISTING_USER),
		
		
		// invitation states
		////////////////////
		
		ENTERING_OPPONENT_CONTACT_INFO(StateDetails.ENTERING_OPPONENT_CONTACT_INFO),
		ENTERING_MATCH_WORD_TO_PLAY(StateDetails.ENTERING_MATCH_WORD),
		ANSWERING_TO_INVITATION(StateDetails.ANSWERING_TO_INVITATION),
		
		
		// playing states
		/////////////////
		
		GUESSING_HUMAN_WORD(StateDetails.GUESSING_HUMAN_WORD),
		GUESSING_BOT_WORD(StateDetails.GUESSING_BOT_WORD),
		
		
		// listing users states
		///////////////////////
		
		LISTING_USERS(StateDetails.LISTING_USERS),
				
		;
		
		private CommandTriggersDto[] commandPatterns;
		
		ESTATES(CommandTriggersDto[] commandPatterns) {
			this.commandPatterns = commandPatterns;
		}
		
		public CommandTriggersDto[] getCommandPatterns() {
			return commandPatterns;
		}		
		
	}	

}
