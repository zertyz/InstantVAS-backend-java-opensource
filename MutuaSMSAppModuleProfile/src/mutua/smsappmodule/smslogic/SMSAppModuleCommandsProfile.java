package mutua.smsappmodule.smslogic;

import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile.*;
import static mutua.smsappmodule.smslogic.CommandCommons.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.*;

import java.sql.SQLException;

import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * SMSAppModuleCommandsProfile.java
 * ================================
 * (created by luiz, Aug 3, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Profile" 'MutuaSMSAppModule' implementation.
 * It is a god idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Command Processor design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleCommandsProfile implements ICommandProcessor {
	
	/** Command to initiate the inquiry for the user desired nickname, so it can be registered on the system
	 *  Receives no parameters. */
	cmdStartAskForNicknameDialog {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getNewStateReplyCommandAnswer(session, nstRegisteringNickname, getAskNickname());
		}
	},
	
	/** Command to set the desired user nickname. Since no two users may share the same nickname, in case of
	 *  nickname collision, a new on will be picked automatically. 
	 *  Receives 1 parameter: the desired nickname */
	cmdRegisterNickname {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			return getNewStateReplyCommandAnswer(session, nstExistingUser, getNicknameRegistrationNotification());
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
	
	/** global triggers that activates {@link #cmdStartAskForNicknameDialog} */
	public static String[] trgGlobalStartAskForNicknameDialog   = {"NICK"};
	/** {@link #nstRegisteringNickname} triggers that activates {@link #cmdRegisterNickname} */
	public static String[] trgLocalRegisterNickname             = {"([A-Za-z0-9]+)"};
	/** global triggers that activates {@link #cmdRegisterNickname} */
	public static String[] trgGlobalRegisterNickname            = {"NICK (.*)"};

}
