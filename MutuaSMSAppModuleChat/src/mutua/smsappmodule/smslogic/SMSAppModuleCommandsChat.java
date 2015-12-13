package mutua.smsappmodule.smslogic;

import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsChat.*;
import static mutua.smsappmodule.smslogic.CommandCommons.*;
import static mutua.smsappmodule.smslogic.sessions.SMSAppModuleSessionsChat.sprLastPrivateMessageSender;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * SMSAppModuleCommandsChat.java
 * =============================
 * (created by luiz, Aug 26, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Chat" 'MutuaSMSAppModule' implementation.
 * It is a god idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Command Processor design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleCommandsChat implements ICommandProcessor {
	
	/** Just a parameter overload (without the nickname) implementation for {@link #cmdSendPrivateMessage} -- to be
	 *  used when in {@link SMSAppModuleNavigationStatesChat#nstChattingWithSomeone}.
	 *  Receives 1 parameter: the message to be sent */
	cmdSendPrivateReply {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String message  = parameters[0];
			String nickname = session.getStringProperty(sprLastPrivateMessageSender);
			if (nickname == null) {
				getSameStateReplyCommandAnswer(getDoNotKnowWhoYouAreChattingTo());
			}
			return cmdSendPrivateMessage.processCommand(session, carrier, new String[] {nickname, message});
		}
	},
	
	/** Command to send a private message to a user. 
	 *  Receives 2 parameters: the destination nickname and the message */
	cmdSendPrivateMessage {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String targetNickname        = parameters[0];
			String privateMessage        = parameters[1];
			
			UserDto senderUser           = session.getUser();
			ProfileDto targetUserProfile = profileDB.getProfileRecord(targetNickname);
			
			if (targetUserProfile == null) {
				// TODO log tick: nickname not found
				return getSameStateReplyCommandAnswer(SMSAppModulePhrasingsProfile.getNicknameNotFound(targetNickname));
			}
			
			UserDto targetUser           = targetUserProfile.getUser();
			String  targetPhoneNumber    = targetUser.getPhoneNumber();
			String  senderNickname       = profileDB.getProfileRecord(senderUser).getNickname();
			
			// TODO: in order for 'cmdSendPrivateReply', 'sprLastPrivateMessageSender' and 'nstChattingWithSomeone' to work, we need to set the state of the target user to 'nstChattingWithSomeone'. This may be undesired and, until the dead-lock is solved, that feature won't be further implemented
			
			chatDB.logPrivateMessage(senderUser, targetUser, session.getMO().getMoId(), session.getMO().getText(), privateMessage);
			
			return getSameStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(
				getPrivateMessageDeliveryNotification(targetUserProfile.getNickname()),
				targetUserProfile.getUser(), getPrivateMessage(senderNickname, privateMessage));
		}
	},
	
	
	;
	
	// databases
	////////////
	
	private static IChatDB    chatDB    = SMSAppModuleDALFactoryChat.DEFAULT_DAL.getChatDB();
	private static IProfileDB profileDB = SMSAppModuleDALFactoryProfile.DEFAULT_DAL.getProfileDB();

	
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
	
	/** global triggers that executes {@link #cmdSendPrivateMessage} */
	public static String[] trgGlobalSendPrivateMessage   = {"[MP] ([^ ]+) (.*)"};
	/** {@link SMSAppModuleNavigationStatesChat#nstChattingWithSomeone} triggers that activates {@link #cmdSendPrivateReply} */
	public static String[] trgLocalSendPrivateReply      = {"(.+)"};

}
