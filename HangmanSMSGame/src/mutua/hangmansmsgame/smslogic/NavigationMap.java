package mutua.hangmansmsgame.smslogic;

import mutua.hangmansmsgame.smslogic.commands.ICommandProcessor;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandPatternsDto;

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


		// help commands
		////////////////

		SHOW_WELCOME_MESSAGE(CommandDetails.SHOW_WELCOME_MESSAGE),
		
		
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
				
		;
		
		private CommandPatternsDto[] commandPatterns;
		
		ESTATES(CommandPatternsDto[] commandPatterns) {
			this.commandPatterns = commandPatterns;
		}
		
		public CommandPatternsDto[] getCommandPatterns() {
			return commandPatterns;
		}		
		
	}	

}
