package mutua.hangmansmsgame.smslogic;

import mutua.hangmansmsgame.dal.dto.UserSessionDto;
import mutua.hangmansmsgame.dal.dto.UserSessionDto.EUserSessionParameters;
import mutua.hangmansmsgame.i18n.IPhraseology;
import mutua.hangmansmsgame.smslogic.NavigationMap.ESTATES;
import mutua.hangmansmsgame.smslogic.commands.ICommandProcessor;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandAnswerDto;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandMessageDto;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandMessageDto.EResponseMessageType;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * CommandDetails.java
 * ===================
 * (created by luiz, Dec 19, 2014)
 *
 * Defines how to process each of the commands defined in 'ECOMMANDS'
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class CommandDetails {

	public static final ICommandProcessor SHOW_WELCOME_MESSAGE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(UserSessionDto userSession, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INFOWelcome(),
			                                                          EResponseMessageType.HELP);
			return new CommandAnswerDto(commandResponse, null);
		}
	};
	
	public static final ICommandProcessor SHOW_FULL_HELP_MESSAGE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(UserSessionDto userSession, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String[] smsTexts = phrases.INFOFullHelp();
			CommandMessageDto[] commandResponses = new CommandMessageDto[smsTexts.length];
			for (int i=0; i<commandResponses.length; i++) {
				commandResponses[i] = new CommandMessageDto(smsTexts[i], EResponseMessageType.HELP);
			}
			return new CommandAnswerDto(commandResponses, null);
		}
	};

	public static final ICommandProcessor START_INVITATION_PROCESS = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(UserSessionDto userSession, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String invitingPlayerNickname = "HardCodedNick";
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INVITINGAskOpponentNickOrPhone(invitingPlayerNickname),
			                                                          EResponseMessageType.HELP);
			return new CommandAnswerDto(commandResponse, userSession.getNewUserSession(ESTATES.ENTERING_OPPONENT_CONTACT_INFO.name()));
		}
	};

	public static final ICommandProcessor REGISTER_OPPONENT_PHONE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(UserSessionDto userSession, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String opponentPhoneNumber = parameters[0];			
			System.out.println("Good! We now have a user to invite! " + opponentPhoneNumber);
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(opponentPhoneNumber),
			                                                          EResponseMessageType.ACQUIRE_MATCH_INFORMATION);
			return new CommandAnswerDto(commandResponse, userSession.getNewUserSession(ESTATES.ENTERING_MATCH_WORD_TO_PLAY.name(), EUserSessionParameters.OPPONENT_PHONE_NUMBER, opponentPhoneNumber));
		}
	};

	public static final ICommandProcessor REGISTER_MATCH_WORD =  new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(UserSessionDto userSession, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String opponentPhoneNumber    = userSession.getParameterValue(UserSessionDto.EUserSessionParameters.OPPONENT_PHONE_NUMBER);
			String wordToPlay             = parameters[0];
			String invitingPlayerNickName = "HardCodedNick";
			String invitedPlayerNickName  = "Howlie";
			String invitedPlayerPhoneNumber;
			
			invitedPlayerPhoneNumber = userSession.getParameterValue(EUserSessionParameters.OPPONENT_PHONE_NUMBER);
			
			// MATCH_WORD_TO_PLAY_WITH
			System.out.println("Good! We now have a user to invite, " + opponentPhoneNumber + ", and a word to play: " + wordToPlay);
			CommandMessageDto invitingPlayerMessage = new CommandMessageDto(phrases.INVITINGInvitationNotificationForInvitingPlayer(invitedPlayerNickName),
			                                                                EResponseMessageType.ACQUIRE_MATCH_INFORMATION);
			CommandMessageDto opponentPlayerMessage = new CommandMessageDto(phrases.INVITINGInvitationNotificationForInvitedPlayer(invitingPlayerNickName),
                                                                            EResponseMessageType.INVITATION_MESSAGE);
			return new CommandAnswerDto(new CommandMessageDto[] {invitingPlayerMessage, opponentPlayerMessage}, null);
		}
	};;

}
