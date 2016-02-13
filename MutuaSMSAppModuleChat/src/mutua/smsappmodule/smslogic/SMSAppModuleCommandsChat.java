package mutua.smsappmodule.smslogic;

import static mutua.smsappmodule.smslogic.CommandCommons.*;
import static mutua.smsappmodule.smslogic.sessions.SMSAppModuleSessionsChat.sprLastPrivateMessageSender;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryChat;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsChat;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesChat;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * SMSAppModuleCommandsChat.java
 * =============================
 * (created by luiz, Aug 26, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Chat" SMS Module.
 * It is a god idea, when possible, to make command names match phrase names.
 * 
 * This class implements the "Instant VAS SMSApp Command Processors" design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleCommandsChat {
	
	/** Class to be statically imported by the Configurators to refer to commands when defining the {@link CommandTriggersDto} */
	public static class CommandNamesChat {
		/** @see SMSAppModuleCommandsChat#cmdSendPrivateReply */
		public final static String cmdSendPrivateReply   = "SendPrivateReply";
		/** @see SMSAppModuleCommandsChat#cmdSendPrivateMessage */
		public final static String cmdSendPrivateMessage = "SendPrivateMessage";
	}
	
	/** Class to be used as a reference when customizing the MO commands for this module */
	public static class CommandTriggersChat {
		/** Global triggers (to be used on several navigation states) to send a private message to a chosen user.
		 *  Receives 2 parameter: the destination nickname and the message
		 *  -- activates {@link SMSAppModuleCommandsChat#cmdSendPrivateMessage} */
		public final static String[] trgGlobalSendPrivateMessage   = {"[MP] ([^ ]+) (.*)"};
		/** Local triggers (available only to the 'answering to a private message' navigation state) to send the reply. Receives 1 parameter: the message --
		 *  {@link SMSAppModuleNavigationStatesChat#nstChattingWithSomeone} triggers that activates {@link SMSAppModuleCommandsChat#cmdSendPrivateReply} */
		public final static String[] trgLocalSendPrivateReply      = {"(.+)"};

	}
	
	// Instance Fields
	//////////////////

	private final SMSAppModulePhrasingsProfile profilePhrases;
	private final SMSAppModulePhrasingsChat    chatPhrases;
	private final IProfileDB profileDB;
	private final IChatDB    chatDB;


	/** Constructs an instance of this module's command processors.
	 *  @param profilePhrases   an instance of the "Profile" SMSApp Module Phrasing to be used
	 *  @param chatPhrases      an instance of the this module's phrasings to be used
	 *  @param profileModuleDAL one of the members of {@link SMSAppModuleDALFactoryProfile}
	 *  @param chatModuleDAL    one of the members of {@link SMSAppModuleDALFactoryChat} */
	public SMSAppModuleCommandsChat(SMSAppModulePhrasingsProfile  profilePhrases,
	                                SMSAppModulePhrasingsChat     chatPhrases,
	                                SMSAppModuleDALFactoryProfile profileModuleDAL,
	                                SMSAppModuleDALFactoryChat    chatModuleDAL) {
		this.profilePhrases = profilePhrases;
		this.chatPhrases    = chatPhrases;
		this.profileDB      = profileModuleDAL.getProfileDB();
		this.chatDB         = chatModuleDAL.getChatDB();
	}

	// Command Definitions
	//////////////////////	
	
	/** Just a parameter overload (without the nickname) implementation for {@link #cmdSendPrivateMessage} -- to be
	 *  used when in {@link SMSAppModuleNavigationStatesChat#nstChattingWithSomeone}.
	 *  Receives 1 parameter: the message to be sent */
	public final ICommandProcessor cmdSendPrivateReply = new ICommandProcessor(CommandNamesChat.cmdSendPrivateReply) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String message  = parameters[0];
			String nickname = session.getStringProperty(sprLastPrivateMessageSender);
			if (nickname == null) {
				getSameStateReplyCommandAnswer(chatPhrases.getDoNotKnowWhoYouAreChattingTo());
			}
			return cmdSendPrivateMessage.processCommand(session, carrier, new String[] {nickname, message});
		}
	};
	
	/** Command to send a private message to a user. 
	 *  Receives 2 parameters: the destination nickname and the message */
	public final ICommandProcessor cmdSendPrivateMessage = new ICommandProcessor(CommandNamesChat.cmdSendPrivateMessage) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String targetNickname        = parameters[0];
			String privateMessage        = parameters[1];
			
			UserDto senderUser           = session.getUser();
			ProfileDto targetUserProfile = profileDB.getProfileRecord(targetNickname);
			
			if (targetUserProfile == null) {
				// TODO log tick: nickname not found
				return getSameStateReplyCommandAnswer(profilePhrases.getNicknameNotFound(targetNickname));
			}
			
			UserDto targetUser           = targetUserProfile.getUser();
			String  targetPhoneNumber    = targetUser.getPhoneNumber();
			String  senderNickname       = profileDB.getProfileRecord(senderUser).getNickname();
			
			// TODO: in order for 'cmdSendPrivateReply', 'sprLastPrivateMessageSender' and 'nstChattingWithSomeone' to work, we need to set the state of the target user to 'nstChattingWithSomeone'. This may be undesired and, until the dead-lock is solved, that feature won't be further implemented
			
			chatDB.logPrivateMessage(senderUser, targetUser, session.getMO().getMoId(), session.getMO().getText(), privateMessage);
			
			return getSameStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(
				chatPhrases.getPrivateMessageDeliveryNotification(targetUserProfile.getNickname()),
				targetUserProfile.getUser(), chatPhrases.getPrivateMessage(senderNickname, privateMessage));
		}
	};

	
	// SMSAppModuleCommandCommons candidates
	////////////////////////////////////////
	
	
	// Command List
	///////////////
	
	/** The list of all commands -- to allow deserialization by {@link CommandTriggersDto} */
	public final ICommandProcessor[] values = {
		cmdSendPrivateReply,
		cmdSendPrivateMessage,
	};
}
