package mutua.smsappmodule.smslogic.navigationstates;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationHangman.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman.*;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;

/** <pre>
 * SMSAppModuleNavigationStatesHangman.java
 * ========================================
 * (created by luiz, Sep 18, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Hangman" SMS Application Module
 * 
 * Implements the Mutua SMSApp Navigation States design pattern, as described by
 * {@link INavigationState}
 *
 * @see INavigationState
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleNavigationStatesHangman implements INavigationState {
	
	/** Navigation state part of the invitation process of a human to play a hangman match -- on this state, the user must enter the desired word to be guessed,
	 *  which will be processed by {@link SMSAppModuleCommandsHangman#cmdHoldMatchWord}.
	 *  SMS Applications must extend it, adding their own command triggers. */
	nstEnteringMatchWord {
		@Override
		public void setCommandTriggersFromConfigurationValues() {
			setCommandTriggers(new Object[][] {
//				{cmdHoldMatchWord,  HANGMANtrgLocalEnterMatchWord},
//				{SMSAppModuleCommandsHelp.cmdShowStatelessHelp, new String[] {"HELP"}},
//				{SMSAppModuleCommandsHelp.cmdShowStatefulHelp,  new String[] {".*"}},
			});
		}
	},
	
	/** Navigation state that indicates the user is playing a hangman match with a human (as the invited opponent), which hole is to guess the word.
	 *  SMS Applications must extend it, adding their own command triggers. */
	nstGuessingWordFromHangmanHumanOpponent {
		@Override
		public void setCommandTriggersFromConfigurationValues() {
			setCommandTriggers(new Object[][] {
//				{cmdSuggestLetterOrWordForHuman,  HANGMANtrgLocalSuggestLetterOrWordForHumanOrBot},
//				{SMSAppModuleCommandsHelp.cmdShowStatelessHelp, new String[] {"HELP"}},
//				{SMSAppModuleCommandsHelp.cmdShowStatefulHelp,  new String[] {".*"}},
			});
		}
	},
	
	/** Navigation state that indicates the user is playing a hangman match with the robot, which hole is to guess the word.
	 *  SMS Applications must extend it, adding their own command triggers. */
	nstGuessingWordFromHangmanBotOpponent {
		@Override
		public void setCommandTriggersFromConfigurationValues() {
			setCommandTriggers(new Object[][] {
//				{cmdSuggestLetterOrWordForBot,  HANGMANtrgLocalSuggestLetterOrWordForHumanOrBot},
//				{SMSAppModuleCommandsHelp.cmdShowStatelessHelp, new String[] {"HELP"}},
//				{SMSAppModuleCommandsHelp.cmdShowStatefulHelp,  new String[] {".*"}},
			});
		}
	},
	
	;
	
	static {
		// defines stateful help messages for the here defined navigation states
//		SMSAppModuleNavigationStates.nstNewUser.setCommandTriggers(new Object[][] {
//			{cmdStartDoubleOptinProcess, SUBSCRIPTIONtrgLocalStartDoubleOptin},
//			//{/*fall back help*/,         ".*"},
//		});
//		SMSAppModuleNavigationStates.nstExistingUser.setCommandTriggers(new Object[][] {
//			{cmdUnsubscribe, SUBSCRIPTIONtrgGlobalUnsubscribe},
//			//{/*fall back help*/,         ".*"},
//		});
	}
	
	private NavigationStateCommons nsc;
	
	private SMSAppModuleNavigationStatesHangman() {
		nsc = new NavigationStateCommons(this);
	}

	@Override
	public String getNavigationStateName() {
		return this.name();
	}

	@Override
	public void setCommandTriggers(Object[][] commandsTriggersData) {
		nsc.setCommandTriggers(commandsTriggersData);
	}

	@Override
	public CommandTriggersDto[] getCommandTriggers() {
		return nsc.getCommandTriggers();
	}

	@Override
	public String[] serializeCommandTrigger(ICommandProcessor command) {
		return nsc.serializeCommandTrigger(command);
	}

	@Override
	public void deserializeCommandTrigger(String[] serializedData) {
		nsc.deserializeCommandTrigger(serializedData);
	}
}