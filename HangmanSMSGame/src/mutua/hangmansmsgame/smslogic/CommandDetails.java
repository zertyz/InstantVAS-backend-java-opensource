package mutua.hangmansmsgame.smslogic;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.IMatchDB;
import mutua.hangmansmsgame.dal.ISessionDB;
import mutua.hangmansmsgame.dal.IUserDB;
import mutua.hangmansmsgame.dal.dto.MatchDto;
import mutua.hangmansmsgame.dal.dto.MatchDto.EMatchStatus;
import mutua.hangmansmsgame.dal.dto.SessionDto;
import mutua.hangmansmsgame.dal.dto.SessionDto.ESessionParameters;
import mutua.hangmansmsgame.hangmangamelogic.HangmanGame;
import mutua.hangmansmsgame.hangmangamelogic.HangmanGame.EHangmanGameStates;
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
			String invitingPlayerNickname = userDB.getUserNickname(session.getPhone());
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

			HangmanGame game = new HangmanGame(wordToPlay, 6);
			
			// TODO a mensagem enviada ao convidado pode conter o telefone do proponente, caso o convite seja baseado em número de telefone, para facilitar a identificação do amigo proponente.
			System.out.println("Good! We now have a user to invite, " + opponentPlayerPhoneNumber + ", and a word to play: " + wordToPlay);
			CommandMessageDto invitingPlayerMessage = new CommandMessageDto(phrases.INVITINGInvitationNotificationForInvitingPlayer(opponentPlayerNickName),
			                                                                EResponseMessageType.ACQUIRE_MATCH_INFORMATION);
			CommandMessageDto opponentPlayerMessage = new CommandMessageDto(opponentPlayerPhoneNumber, phrases.INVITINGInvitationNotificationForInvitedPlayer(invitingPlayerNickName),
                                                                            EResponseMessageType.INVITATION_MESSAGE);
			SessionDto opponentSession = new SessionDto(opponentPlayerPhoneNumber, ESTATES.ANSWERING_TO_INVITATION.name(),
			                                            ESessionParameters.OPPONENT_PHONE_NUMBER, session.getPhone(),
			                                            ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, game.serializeGameState());
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
			String serializedGameState      = session.getParameterValue(ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE);

			// start a new Match
			HangmanGame game  = new HangmanGame(serializedGameState);
			System.out.println("Now we are good to start a match! Users " + wordProvidingPlayerPhone + " and " + wordGuessingPlayerPhone + " are playing");
			MatchDto match = new MatchDto(wordProvidingPlayerPhone, wordGuessingPlayerPhone, serializedGameState, System.currentTimeMillis(), EMatchStatus.ACTIVE);
			int matchId = matchDB.storeNewMatch(match);
			
			CommandMessageDto wordProvidingPlayerMessage = new CommandMessageDto(phrases.PLAYINGWordProvidingPlayerStart(game.getGuessedWordSoFar(), wordGuessingPlayerNick),
			                                                                     EResponseMessageType.PLAYING);
			CommandMessageDto wordGuessingPlayerMessage = new CommandMessageDto(phrases.PLAYINGWordGuessingPlayerStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()),
                                                                                EResponseMessageType.PLAYING);
			SessionDto opponentSession = new SessionDto(wordProvidingPlayerPhone, ESTATES.PLAYING.name());
			sessionDB.setSession(opponentSession);
			
			return new CommandAnswerDto(new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage},
			                            session.getNewSessionDto(ESTATES.PLAYING.name(), ESessionParameters.MATCH_ID, Integer.toString(matchId)));
		}
	};

	/** Called when the user is attempting to guess the word */
	public static final ICommandProcessor SUGGEST_LETTER_OR_WORD = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String wordProvidingPlayerPhone = session.getParameterValue(ESessionParameters.OPPONENT_PHONE_NUMBER);
			String wordGuessingPlayerPhone  = session.getPhone();
			String wordProvidingPlayerNick  = userDB.getUserNickname(wordProvidingPlayerPhone);
			String wordGuessingPlayerNick   = userDB.getUserNickname(wordGuessingPlayerPhone);
			int    matchId                  = Integer.parseInt(session.getParameterValue(ESessionParameters.MATCH_ID));
			String suggestedLetter          = parameters[0];
			String serializedGameState      = session.getParameterValue(ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE);


			System.out.println("Now are continueing a match between users " + wordProvidingPlayerPhone + " and " + wordGuessingPlayerPhone + " with user state '" + serializedGameState + "'");
			HangmanGame game  = new HangmanGame(serializedGameState);
			MatchDto match = matchDB.retrieveMatch(matchId);
			
			game.suggestLetter(suggestedLetter.charAt(0));
			int attemptsLeft = game.getNumberOfWrongTriesLeft();
			
			CommandMessageDto wordProvidingPlayerMessage;
			CommandMessageDto wordGuessingPlayerMessage;
			EHangmanGameStates gameState = game.getGameState();
			switch (gameState) {
				case PLAYING:
					wordProvidingPlayerMessage = new CommandMessageDto(
						phrases.PLAYINGWordProvidingPlayerStatus(
							attemptsLeft<6, attemptsLeft<5, attemptsLeft<4, attemptsLeft<3, attemptsLeft<2, attemptsLeft<1,
							game.getGuessedWordSoFar(), suggestedLetter, game.getAttemptedLettersSoFar(), wordGuessingPlayerNick),
						EResponseMessageType.PLAYING);
					wordGuessingPlayerMessage = new CommandMessageDto(
						phrases.PLAYINGWordGuessingPlayerStatus(
							attemptsLeft<6, attemptsLeft<5, attemptsLeft<4, attemptsLeft<3, attemptsLeft<2, attemptsLeft<1,
							game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()),
						EResponseMessageType.PLAYING);
					return new CommandAnswerDto(new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage},
                                                session.getNewSessionDto(ESTATES.PLAYING.name(), ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, game.serializeGameState()));
				case WON:
					matchDB.updateMatchStatus(matchId, EMatchStatus.CLOSED_WORD_GUESSED);
					wordProvidingPlayerMessage = new CommandMessageDto(wordProvidingPlayerPhone,
						phrases.PLAYINGWinningMessageForWordProvidingPlayer(wordGuessingPlayerNick),
						EResponseMessageType.PLAYING);
					wordGuessingPlayerMessage = new CommandMessageDto(
						phrases.PLAYINGWinningMessageForWordGuessingPlayer(game.getWord(), "xx.xx.xx.xx"),
						EResponseMessageType.PLAYING);
					return new CommandAnswerDto(new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage},
                                                session.getNewSessionDto(ESTATES.PLAYING.name(), ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, game.serializeGameState()));
				case LOST:
					matchDB.updateMatchStatus(matchId, EMatchStatus.CLOSED_ATTEMPTS_EXCEEDED);
					wordProvidingPlayerMessage = new CommandMessageDto(wordProvidingPlayerPhone,
						phrases.PLAYINGLosingMessageForWordProvidingPlayer(wordGuessingPlayerNick),
						EResponseMessageType.PLAYING);
					wordGuessingPlayerMessage = new CommandMessageDto(
						phrases.PLAYINGLosingMessageForWordGuessingPlayer(game.getWord(), wordProvidingPlayerNick),
						EResponseMessageType.PLAYING);
					return new CommandAnswerDto(new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage},
                                                session.getNewSessionDto(ESTATES.NEW_USER.name()));
				default:
					throw new RuntimeException("Don't know nothing about EHangmanGameState." + gameState);
			}
		}
	};

	/** Called when the user wants to send a chat message to another player */
	public static final ICommandProcessor PROVOKE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String sourceNickname    = userDB.getUserNickname(session.getPhone());
			String targetNickname    = parameters[0];
			String targetPhoneNumber = userDB.getUserPhoneNumber(targetNickname);
			String chatText          = parameters[1];
			CommandMessageDto notificationMessage = new CommandMessageDto(phrases.PROVOKINGDeliveryNotification(targetNickname), EResponseMessageType.CHAT);
			CommandMessageDto chatMessage = new CommandMessageDto(targetPhoneNumber, phrases.PROVOKINGSendMessage(sourceNickname, chatText), EResponseMessageType.CHAT);
			return new CommandAnswerDto(new CommandMessageDto[] {notificationMessage, chatMessage}, null);
		}
	};
	
	/** Called when the user wants to see the profile of another user */
	public static final ICommandProcessor SHOW_PROFILE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String nickname = parameters[0];
			CommandMessageDto message = new CommandMessageDto(phrases.PROFILEView(nickname, "RJ", 0), EResponseMessageType.PROFILE);
			return new CommandAnswerDto(message, null);
		}
	};
	
	/** Called when the user wants to reset his/her profile nickname */
	public static final ICommandProcessor DEFINE_NICK = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String newNickname = parameters[0];
			userDB.checkAvailabilityAndRecordNickname(session.getPhone(), newNickname);
			String registeredNickname = userDB.getUserNickname(session.getPhone());
			CommandMessageDto message = new CommandMessageDto(phrases.PROFILENickRegisteredNotification(registeredNickname), EResponseMessageType.PROFILE);
			return new CommandAnswerDto(message, null);
		}
	};
	
	/** Called when the user wants to a list of users */
	public static final ICommandProcessor LIST_USERS = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			String[][] playersInfo = {
				{userDB.getUserNickname("21991234899"), "RJ", "1"},
				{userDB.getUserNickname("21998019167"), "RJ", "2"},
			};
			CommandMessageDto message = new CommandMessageDto(phrases.LISTINGShowPlayers(playersInfo), EResponseMessageType.PROFILE);
			return new CommandAnswerDto(message, null);
		}
	};

}
