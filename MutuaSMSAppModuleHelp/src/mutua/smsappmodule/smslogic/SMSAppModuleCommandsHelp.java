package mutua.smsappmodule.smslogic;

import java.sql.SQLException;

import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHelp;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHelp.NavigationStatesNamesHelp.*;
import static mutua.smsappmodule.smslogic.sessions.SMSAppModuleSessionsHelp.*;
import static mutua.smsappmodule.smslogic.CommandCommons.*;

/** <pre>
 * SMSAppModuleCommandsHelp.java
 * =============================
 * (created by luiz, Jul 16, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Help" SMS Module.
 * It is a good idea, when possible, to make command names match phrase names.
 * 
 * This class implements the "Instant VAS SMSApp Command Processors" design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleCommandsHelp {
	
	
	/** Class to be statically imported by the Configurators to refer to commands when defining the {@link CommandTriggersDto} */
	public static class CommandNamesHelp {
		/** @see SMSAppModuleCommandsHelp#cmdShowStatelessHelp */
		public final static String cmdShowStatelessHelp             = "ShowStatelessHelp";
		/** @see SMSAppModuleCommandsHelp#cmdShowStatefulHelp */
		public final static String cmdShowStatefulHelp              = "ShowStatefulHelp";
		/** @see SMSAppModuleCommandsHelp#cmdStartCompositeHelpDialog */
		public final static String cmdStartCompositeHelpDialog      = "StartCompositeHelpDialog";
		/** @see SMSAppModuleCommandsHelp#cmdShowNextCompositeHelpMessage */
		public final static String cmdShowNextCompositeHelpMessage  = "ShowNextCompositeHelpMessage";
		/** @see SMSAppModuleCommandsHelp#cmdShowNewUsersFallbackHelp */
		public final static String cmdShowNewUsersFallbackHelp      = "ShowNewUsersFallbackHelp";
		/** @see SMSAppModuleCommandsHelp#cmdShowExistingUsersFallbackHelp */
		public final static String cmdShowExistingUsersFallbackHelp = "ShowExistingUsersFallbackHelp";
	}
	
	/** Class to be used as a reference when customizing the MO commands for this module */
	public static class CommandTriggersHelp {
		/** Global triggers (to be used on several navigation states) to execute the 'composite help dialog', the multiple help/rules/terms
		 *  of service messages which accepts the MORE keyword (described at 'trgLocalShowNextCompositeHelpMessage') to present the next one --
		 *  activates {@link SMSAppModuleCommandsHelp#cmdStartCompositeHelpDialog} */
		public final static String[] trgGlobalStartCompositeHelpDialog      = {"M?O?R?E? *INFOR?M?A?T?I?O?N?", "RULE.*"};
		/** Local triggers (available only to the 'composite help dialog' navigation state) to execute the 'show next composite help message' --
		 *  {@link SMSAppModuleNavigationStatesHelp#nstPresentingCompositeHelp} triggers that activates {@link SMSAppModuleCommandsHelp#cmdShowNextCompositeHelpMessage} */
		public final static String[] trgLocalShowNextCompositeHelpMessage   = {"MO?R?E?.*", "\\s*+\\s*", "NE?X?T?.*"};
		/** THIS SHOULD NOT BE CONFIGURABLE SINCE IT IS A FALLBACK AND MUST ALWAYS BE .* ALSO, A SIMILAR TRIGGER IS MISSING FOR THE NEW USERS FALLBACK -- anyway, it activates 
		 *  {@link SMSAppModuleCommandsHelp#cmdShowExistingUsersFallbackHelp} */
		public final static String   trgGlobalShowExistingUsersFallbackHelp = ".*";
		/** Global triggers (to be used on several navigation states) to execute the general help message --
		 *  {@link SMSAppModuleCommandsHelp#cmdShowStatelessHelp} */
		public final static String   trgGlobalShowStatelessHelpMessage      = "HELP.*";
	}

	// Instance Fields
	//////////////////

	private final SMSAppModulePhrasingsHelp helpPhrases;
	
	/** Constructs an instance of this module's command processors.
	 *  @param helpPhrases an instance of the phrasings to be used */
	public SMSAppModuleCommandsHelp(SMSAppModulePhrasingsHelp helpPhrases) {
		this.helpPhrases = helpPhrases;
	}

	// Command Definitions
	//////////////////////	
	
	/** Command used to provide an instant help message, without changing the navigation flow.
	 *  Receives no parameters. */
	public final ICommandProcessor cmdShowStatelessHelp = new ICommandProcessor(CommandNamesHelp.cmdShowStatelessHelp) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getSameStateReplyCommandAnswer(helpPhrases.getStatelessHelpMessage());
		}
	};
	
	/** Command used to provide a contextualized help message, based on the current navigation state.
	 *  Receives no parameters. */
	public final ICommandProcessor cmdShowStatefulHelp = new ICommandProcessor(CommandNamesHelp.cmdShowStatefulHelp) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String MOText = parameters[0] != null ? parameters[0] : "NULL";
			return getSameStateReplyCommandAnswer(helpPhrases.getStatefulHelpMessage(session.getNavigationStateName(), parameters[0]));
		}
	};
	
	/** Command used to show the first message of the composite help, setting the navigation state
	 *  to show additional composite help messages.
	 *  Receives no parameters. */
	public final ICommandProcessor cmdStartCompositeHelpDialog = new ICommandProcessor(CommandNamesHelp.cmdStartCompositeHelpDialog) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			session.setProperty(sprLastCompositeHelpMsgNumberShown, 0);
			return getNewStateReplyCommandAnswer(session, nstPresentingCompositeHelp, helpPhrases.getCompositeHelpMessage(0));
		}
	};
	
	/** The continuation of {@link #cmdStartCompositeHelpDialog} */
	public final ICommandProcessor cmdShowNextCompositeHelpMessage = new ICommandProcessor(CommandNamesHelp.cmdShowNextCompositeHelpMessage) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			int nextHelpMsgNumber = session.getIntProperty(sprLastCompositeHelpMsgNumberShown) + 1;
			String compositeHelpMessage = helpPhrases.getCompositeHelpMessage(nextHelpMsgNumber);
			if (compositeHelpMessage != null) {
				session.setProperty(sprLastCompositeHelpMsgNumberShown, nextHelpMsgNumber);
				return getSameStateReplyCommandAnswer(compositeHelpMessage);
			} else {
				return cmdStartCompositeHelpDialog.processCommand(session, carrier, parameters);
			}
		}
	};
	
	
	/** Presents the help message for new users when they send a command not recognized by the application */
	public final ICommandProcessor cmdShowNewUsersFallbackHelp = new ICommandProcessor(CommandNamesHelp.cmdShowNewUsersFallbackHelp) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getSameStateReplyCommandAnswer(helpPhrases.getNewUsersFallbackHelp());
		}
	};
	
	/** Presents the help message for existing users when they send a command not recognized by the application */
	public final ICommandProcessor cmdShowExistingUsersFallbackHelp = new ICommandProcessor(CommandNamesHelp.cmdShowExistingUsersFallbackHelp) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getSameStateReplyCommandAnswer(helpPhrases.getExistingUsersFallbackHelp());
		}
	};

	// SMSAppModuleCommandCommons candidates
	////////////////////////////////////////
	
	
	// Command List
	///////////////
	
	/** The list of all commands -- to allow deserialization by {@link CommandTriggersDto} */
	public final ICommandProcessor[] values = {
		cmdShowStatelessHelp,
		cmdShowStatefulHelp,
		cmdStartCompositeHelpDialog,
		cmdShowNextCompositeHelpMessage,
		cmdShowNewUsersFallbackHelp,
		cmdShowExistingUsersFallbackHelp,
	};
}
