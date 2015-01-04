package mutua.hangmansmsgame.smslogic;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.IMatchDB;
import mutua.hangmansmsgame.dal.ISessionDB;
import mutua.hangmansmsgame.dal.IUserDB;
import mutua.hangmansmsgame.dal.dto.MatchDto;
import mutua.hangmansmsgame.dal.dto.SessionDto;
import mutua.hangmansmsgame.dal.dto.MatchDto.EMatchStatus;
import mutua.hangmansmsgame.dal.dto.SessionDto.ESessionParameters;
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
	
	
	// databases
	////////////
	
	private static IUserDB    userDB    = DALFactory.getUserDB();
	private static ISessionDB sessionDB = DALFactory.getSessionDB();
	private static IMatchDB   matchDB   = DALFactory.getMatchDB();
	

	public static final ICommandProcessor SHOW_WELCOME_MESSAGE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INFOWelcome(),
			                                                          EResponseMessageType.HELP);
			return new CommandAnswerDto(commandResponse, null);
		}
	};
	
	public static final ICommandProcessor SHOW_FULL_HELP_MESSAGE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
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
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String invitingPlayerNickname = "HardCodedNick";
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INVITINGAskOpponentNickOrPhone(invitingPlayerNickname),
			                                                          EResponseMessageType.HELP);
			return new CommandAnswerDto(commandResponse, session.getNewSessionDto(ESTATES.ENTERING_OPPONENT_CONTACT_INFO.name()));
		}
	};

	public static final ICommandProcessor REGISTER_OPPONENT_PHONE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String opponentPhoneNumber = parameters[0];			
			System.out.println("Good! We now have a user to invite! " + opponentPhoneNumber);
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(opponentPhoneNumber),
			                                                          EResponseMessageType.ACQUIRE_MATCH_INFORMATION);
			return new CommandAnswerDto(commandResponse, session.getNewSessionDto(ESTATES.ENTERING_MATCH_WORD_TO_PLAY.name(), ESessionParameters.OPPONENT_PHONE_NUMBER, opponentPhoneNumber));
		}
	};

	public static final ICommandProcessor REGISTER_MATCH_WORD = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String opponentPlayerPhoneNumber = session.getParameterValue(ESessionParameters.OPPONENT_PHONE_NUMBER);
			String opponentPlayerNickName    = userDB.getUserNickname(opponentPlayerPhoneNumber);
			String invitingPlayerNickName    = userDB.getUserNickname(session.getPhone());
			String wordToPlay                = parameters[0];
			
			// MATCH_WORD_TO_PLAY_WITH
			// TODO a mensagem enviada ao convidado pode conter o telefone do proponente, caso o convite seja baseado em número de telefone, para facilitar a identificação do amigo proponente.
			System.out.println("Good! We now have a user to invite, " + opponentPlayerPhoneNumber + ", and a word to play: " + wordToPlay);
			CommandMessageDto invitingPlayerMessage = new CommandMessageDto(phrases.INVITINGInvitationNotificationForInvitingPlayer(opponentPlayerNickName),
			                                                                EResponseMessageType.ACQUIRE_MATCH_INFORMATION);
			CommandMessageDto opponentPlayerMessage = new CommandMessageDto(phrases.INVITINGInvitationNotificationForInvitedPlayer(invitingPlayerNickName),
                                                                            EResponseMessageType.INVITATION_MESSAGE);
			SessionDto opponentSession = new SessionDto(opponentPlayerPhoneNumber, ESTATES.ANSWERING_TO_INVITATION.name(),
			                                            ESessionParameters.OPPONENT_PHONE_NUMBER, session.getPhone(),
			                                            ESessionParameters.HANGMAN_GUESSING_WORD, wordToPlay);
			sessionDB.setSession(opponentSession);
			return new CommandAnswerDto(new CommandMessageDto[] {invitingPlayerMessage, opponentPlayerMessage},
			                            /*session.getNewSessionDto(newNavigationState, parameter, parameterValue)*/null);
		}
	};
	
	/** Called when the word guessing player answered YES to the invitation message he/she received to attend to a hangman match */
	public static final ICommandProcessor ACCEPT_INVITATION = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String wordProvidingPlayerPhone = session.getParameterValue(ESessionParameters.OPPONENT_PHONE_NUMBER);
			String wordGuessingPlayerPhone  = session.getPhone();
			String wordProvidingPlayerNick  = userDB.getUserNickname(wordProvidingPlayerPhone);
			String wordGuessingPlayerNick   = userDB.getUserNickname(wordGuessingPlayerPhone);
			String wordToPlay               = session.getParameterValue(ESessionParameters.HANGMAN_GUESSING_WORD);

			// start a new Match
			System.out.println("Now we are good to start a match! Users " + wordProvidingPlayerPhone + " and " + wordGuessingPlayerPhone + " are playing with word '" + wordToPlay + "'");
			MatchDto match = new MatchDto(wordProvidingPlayerPhone, wordGuessingPlayerPhone, wordToPlay, System.currentTimeMillis(), EMatchStatus.ACTIVE);
			matchDB.storeNewMatch(match);
			
			CommandMessageDto wordProvidingPlayerMessage = new CommandMessageDto(phrases.PLAYINGWordProvidingPlayerStart("coco word", wordGuessingPlayerNick),
			                                                                     EResponseMessageType.PLAYING);
			CommandMessageDto wordGuessingPlayerMessage = new CommandMessageDto(phrases.PLAYINGWordGuessingPlayerStart("coco word", "cd"),
                                                                                EResponseMessageType.PLAYING);
			SessionDto opponentSession = new SessionDto(wordProvidingPlayerPhone, ESTATES.PLAYING.name());
			sessionDB.setSession(opponentSession);
			return new CommandAnswerDto(new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage}, session.getNewSessionDto(ESTATES.PLAYING.name()));
		}
	};

}
