package mutua.smsappmodule.smslogic;

import java.sql.SQLException;

import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp.*;
import static mutua.smsappmodule.smslogic.sessions.SMSAppModuleSessionsHelp.*;
import static mutua.smsappmodule.smslogic.CommandCommons.*;

/** <pre>
 * SMSAppModuleCommandsHelp.java
 * =============================
 * (created by luiz, Jul 16, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Help" 'MutuaSMSAppModule' implementation.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Command Processors design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleCommandsHelp implements ICommandProcessor {
	
	/** Command used to provide an instant help message, without changing the navigation flow.
	 *  Receives no parameters. */
	cmdShowStatelessHelp {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getSameStateReplyCommandAnswer(getStatelessHelpMessage());
		}
	},
	
	/** Command used to provide a contextualized help message, based on the current navigation state.
	 *  Receives no parameters. */
	cmdShowStatefulHelp {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getSameStateReplyCommandAnswer(getStatefulHelpMessage(session.getNavigationState()));
		}
	},
	
	/** Command used to show the first message of the composite help, setting the navigation state
	 *  to show additional composite help messages.
	 *  Receives no parameters. */
	cmdStartCompositeHelpDialog {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			session.setProperty(sprLastCompositeHelpMsgNumberShown, 0);
			INavigationState newNavigationState = nstPresentingCompositeHelp;
			return getNewStateReplyCommandAnswer(session, newNavigationState, getCompositeHelpMessage(0));
		}
	},
	
	/** The continuation of {@link #cmdStartCompositeHelpDialog} */
	cmdShowNextCompositeHelpMessage {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			int nextHelpMsgNumber = session.getIntProperty(sprLastCompositeHelpMsgNumberShown) + 1;
			String compositeHelpMessage = getCompositeHelpMessage(nextHelpMsgNumber);
			if (compositeHelpMessage != null) {
				session.setProperty(sprLastCompositeHelpMsgNumberShown, nextHelpMsgNumber);
				return getSameStateReplyCommandAnswer(compositeHelpMessage);
			} else {
				return cmdStartCompositeHelpDialog.processCommand(session, carrier, parameters);
			}
		}
	},
	
	
	/** Presents the help message for new users when they send a command not recognized by the application */
	cmdShowNewUsersFallbackHelp {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getSameStateReplyCommandAnswer(getNewUsersFallbackHelp());
		}
	},
	
	/** Presents the help message for existing users when they send a command not recognized by the application */
	cmdShowExistingUsersFallbackHelp {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getSameStateReplyCommandAnswer(getExistingUsersFallbackHelp());
		}
	},

	;
	
	@Override
	// this.name is the enumeration property name
	public String getCommandName() {
		return this.name();
	}
	
	
	// SMSAppModuleCommandCommons candidates
	////////////////////////////////////////
	
	
	
	/***********************************************************************
	** GLOBAL COMMAND TRIGGERS -- to be used in several navigation states **
	***********************************************************************/
	
	/** global triggers that activates {@link #cmdStartCompositeHelpDialog} */
	public static String[] trgGlobalStartCompositeHelpDialog    = {"M?O?R?E? *INFOR?M?A?T?I?O?N?", "RULE.*"};
	/** {@link #nstPresentingCompositeHelp} triggers that activates {@link #cmdShowNextCompositeHelpMessage} */
	public static String[] trgLocalShowNextCompositeHelpMessage = {"MO?R?E?.*", "\\s*+\\s*", "NE?X?T?.*"};
	/** global triggers that activates {@link #cmdShowStatelessHelp} */
	public static String[] trgGlobalShowStatelessHelpMessage    = {"HELP.*"};

}
