package mutua.smsappmodule.smslogic;

import static mutua.smsappmodule.config.SMSAppModuleConfigurationHangman.*;
import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman.*;
import static mutua.smsappmodule.smslogic.CommandCommons.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman.*;
import static mutua.smsappmodule.smslogic.sessions.SMSAppModuleSessionsHangman.*;

import java.sql.SQLException;

import mutua.smsappmodule.dal.IMatchDB;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryHangman;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dto.MatchDto;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;
import mutua.smsappmodule.hangmangame.HangmanGame;
import mutua.smsappmodule.hangmangame.HangmanGame.EHangmanGameStates;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandMessageDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman;
import mutua.smsappmodule.smslogic.sessions.SMSAppModuleSessionsHangman;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * SMSAppModuleCommandsHangman.java
 * ================================
 * (created by luiz, Sep 18, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Hangman" 'MutuaSMSAppModule' implementation.
 * It is a god idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Command Processor design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleCommandsHangman implements ICommandProcessor {
	
	/** Triggered by messages matched by {@link #trgGlobalInviteNicknameOrPhoneNumber}, starts the invitation process for a human opponent to play a hangman match providing his/her nickname or phone number.
	 *  Receives 1 parameter: the opponent identifier (a nickname or phone number) */
	cmdInviteNicknameOrPhoneNumber {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String opponentPhoneNumberOrNickName = parameters[0];
			if (isParameterAPhoneNumber(opponentPhoneNumberOrNickName)) {
				return cmdHoldOpponentPhoneNumber.processCommand(session, carrier, parameters);
			} else {
				return cmdHoldOpponentNickname.processCommand(session, carrier, parameters);
			}
		}
	},
	
	/** Parameterless start of the invitation process for a human opponent to play a hangman match.
	 *  Receives no parameters */
	cmdStartHangmanMatchInvitationProcess {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
//			String message  = parameters[0];
//			String nickname = session.getStringProperty(sprLastPrivateMessageSender);
//			if (nickname == null) {
//				getSameStateReplyCommandAnswer(getDoNotKnowWhoYouAreChattingTo());
//			}
//			return cmdSendPrivateMessage.processCommand(session, carrier, new String[] {nickname, message});
			return null;
		}
	},
	
	/** Command to take note of the opponent phone number when inviting someone for a match.
	 *  Receives 1 parameter: the opponent phone number */
	cmdHoldOpponentPhoneNumber {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
//			String opponentPhoneNumber = parameters[0];
//			getNewStateReplyCommandAnswer(session, nstEnteringMatchWord, getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation(opponentNickname));
//			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(opponentPhoneNumber),
//			                                                          EResponseMessageType.ACQUIRE_MATCH_INFORMATION);
//			return getNewCommandAnswerDto(session, commandResponse, ESTATES.ENTERING_MATCH_WORD_TO_PLAY,
//			                              ESessionParameters.OPPONENT_PHONE_NUMBER, opponentPhoneNumber);
			return null;
		}
	},
	
	/** Command to take note of the opponent nickname when inviting someone for a match.
	 *  Receives 1 parameter: the opponent nickname */
	cmdHoldOpponentNickname {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String providedOpponentNickname = parameters[0];
			ProfileDto opponentProfile = profileDB.getProfileRecord(providedOpponentNickname);
			if (opponentProfile == null) {
				// nickname really not found
				return getNickNotFoundMessage(providedOpponentNickname);
			}
			String opponentNickname    = opponentProfile.getNickname();
			String opponentPhoneNumber = opponentProfile.getUser().getPhoneNumber();
			if (opponentPhoneNumber.equals(session.getUser().getPhoneNumber())) {
				// that opponent nickname belongs to the inviting player
				return getNickNotFoundMessage(opponentNickname);
			}
			session.setProperty(sprOpponentPhoneNumber, opponentPhoneNumber);
			return getNewStateReplyCommandAnswer(session, nstEnteringMatchWord, getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation(opponentNickname));
		}
	},
	
	/** Command triggered by messages matched by {@link #trgLocalHoldMatchWord} to take note of the word to play, when inviting someone.
	 *  Assumes the session contains {@link SMSAppModuleSessionsHangman#sprOpponentPhoneNumber}.
	 *  Receives 1 parameter: the desired word */
	cmdHoldMatchWord {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String opponentPlayerPhoneNumber = session.getStringProperty(sprOpponentPhoneNumber);
			String invitingPlayerNickname    = assureUserHasANickname(session.getUser()).getNickname();
			String wordToPlay                = parameters[0];
			
			ProfileDto opponentProfile = assurePhoneNumberHasAnUserAndNickname(opponentPlayerPhoneNumber);
			String opponentPlayerNickName = opponentProfile.getNickname();

			HangmanGame game = new HangmanGame(wordToPlay, 6);

			// not a good word if it is not entirely made by letters and if putting in the first and last letters the word is revealed
			if ((!wordToPlay.matches("[A-Za-z]+")) || (game.getGameState() == EHangmanGameStates.WON)) {
				return getSameStateReplyCommandAnswer(getNotAGoodWord(wordToPlay.toUpperCase()));
			}
			
			// set inviting & invited player sessions
			SessionModel opponentSession = new SessionModel(sessionDB.getSession(opponentProfile.getUser()), null);
			MatchDto match = new MatchDto(session.getUser(), opponentSession.getUser(), game.serializeGameState(), System.currentTimeMillis(), EMatchStatus.ACTIVE);
			matchDB.storeNewMatch(match);
			opponentSession.setNavigationState(nstAnsweringToHangmanMatchInvitation);
			opponentSession.setProperty(sprHangmanMatchId, match.getMatchId());
			sessionDB.setSession(opponentSession.getChangedSessionDto());
			//session.setProperty(sprHangmanMatchId, match.getMatchId());
			session.deleteProperty(sprOpponentPhoneNumber);
			session.setNavigationState(nstExistingUser);
			// TODO a mensagem enviada ao convidado pode conter o telefone do proponente, caso o convite seja baseado em número de telefone, para facilitar a identificação do amigo proponente.
			return getSameStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(getInvitationResponseForInvitingPlayer(opponentPlayerNickName),
			                                                                          userDB.assureUserIsRegistered(opponentPlayerPhoneNumber), getInvitationNotificationForInvitedPlayer(invitingPlayerNickname));
		}
	},
	
	/** Command triggered by messages matched by {@link #trgLocalAcceptMatchInvitation}, invoked by the invited user who wants to accept
	 *  a hangman match invitation.
	 *  Receives no parameters */
	cmdAcceptMatchInvitation {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			MatchDto match = matchDB.retrieveMatch(session.getIntProperty(sprHangmanMatchId));
			HangmanGame game = new HangmanGame(match.getSerializedGame());
			UserDto wordProvidingPlayer = match.getWordProvidingPlayer();
			String wordGuessingPlayerNickname = profileDB.getProfileRecord(session.getUser()).getNickname();
			return getNewStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(session, nstGuessingWordFromHangmanHumanOpponent,
			                                                                         getWordGuessingPlayerMatchStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()),
			                                                                         wordProvidingPlayer,
			                                                                         getWordProvidingPlayerMatchStart(game.getGuessedWordSoFar(), wordGuessingPlayerNickname));
		}
	},
	
	/** Command triggered by messages matched by {@link #trgLocalAcceptMatchInvitation}, invoked by the invited user who wants to refuse
	 *  a hangman match invitation.
	 *  Receives no parameters */
	cmdRefuseMatchInvitation {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			MatchDto match = matchDB.retrieveMatch(session.getIntProperty(sprHangmanMatchId));
			UserDto wordProvidingPlayer = match.getWordProvidingPlayer();
			String wordGuessingPlayerNickname = assureUserHasANickname(session.getUser()).getNickname();
			String wordProvidingPlayerNickname = assureUserHasANickname(wordProvidingPlayer).getNickname();
			session.deleteProperty(sprHangmanMatchId);
			return getNewStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(session, nstExistingUser,
			                                                                         getInvitationRefusalResponseForInvitedPlayer(wordProvidingPlayerNickname),
			                                                                         wordProvidingPlayer,
			                                                                         getInvitationRefusalNotificationForInvitingPlayer(wordGuessingPlayerNickname));
		}
	},
	
	/** Command triggered by messages matched by {@link #trgLocalNewLetterOrWordSuggestion}, issued by the user who is playing a hangman match against a human and is trying to guess the word.
	 *  Receives 1 parameter: the letter, set of characters or word attempted */
	cmdSuggestLetterOrWordForHuman {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String suggestedLetterOrWord = parameters[0];
			int    matchId               = session.getIntProperty(sprHangmanMatchId);
			MatchDto match               = matchDB.retrieveMatch(matchId);
			String serializedGameState   = match.getSerializedGame();
			
			UserDto wordProvidingUser     = match.getWordProvidingPlayer();
			String  wordProvidingPhone    = wordProvidingUser.getPhoneNumber();
			String  wordProvidingNickname = assureUserHasANickname(wordProvidingUser).getNickname();
			String  wordGuessingPhone     = session.getUser().getPhoneNumber();
			String  wordGuessingNickname  = assureUserHasANickname(match.getWordGuessingPlayer()).getNickname();
			
			//MatchPlayersInfo matchPlayersInfo = getMatchPlayersInfo(matchData);
			HangmanGame game  = new HangmanGame(serializedGameState);
			char[] suggestLetters = suggestedLetterOrWord.toCharArray();
			for (char c : suggestLetters) {
				if (game.getGameState() != EHangmanGameStates.PLAYING) {
					break;
				}
				game.suggestLetter(c);
			}
			int attemptsLeft = game.getNumberOfWrongTriesLeft();
			
			EHangmanGameStates gameState = game.getGameState();
			String replyMT                 = null;
			String notificationMT          = null;
			EMatchStatus matchStatus       = null;
			CommandAnswerDto commandAnswer = null;
			switch (gameState) {
				case PLAYING:
					replyMT        = getWordGuessingPlayerStatus(attemptsLeft<6, attemptsLeft<5, attemptsLeft<4, attemptsLeft<3, attemptsLeft<2, attemptsLeft<1,
					                                             game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar());
					notificationMT = getWordProvidingPlayerStatus(attemptsLeft<6, attemptsLeft<5, attemptsLeft<4, attemptsLeft<3, attemptsLeft<2, attemptsLeft<1,
                                                                  game.getGuessedWordSoFar(), suggestedLetterOrWord, game.getAttemptedLettersSoFar(), wordGuessingNickname);
					matchStatus    = EMatchStatus.ACTIVE;
					commandAnswer  = getSameStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(replyMT, wordProvidingUser, notificationMT);
					break;
				case WON:
					replyMT        = getWinningMessageForWordGuessingPlayer(game.getWord(), wordProvidingNickname);
					notificationMT = getWinningMessageForWordProvidingPlayer(wordGuessingNickname);
					matchStatus    = EMatchStatus.CLOSED_WORD_GUESSED;
					commandAnswer  = getNewStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(session, nstExistingUser, replyMT, wordProvidingUser, notificationMT);
					break;
				case LOST:
					replyMT        = getLosingMessageForWordGuessingPlayer(game.getWord(), wordProvidingNickname);
					notificationMT = getLosingMessageForWordProvidingPlayer(wordGuessingNickname);
					matchStatus    = EMatchStatus.CLOSED_ATTEMPTS_EXCEEDED;
					commandAnswer  = getNewStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(session, nstExistingUser, replyMT, wordProvidingUser, notificationMT);
					break;
				default:
					throw new RuntimeException("Don't know nothing about EHangmanGameState." + gameState);
			}
			matchDB.updateMatchStatus(match, matchStatus, game.serializeGameState());
			return commandAnswer;
		}
	},
	
	;
	
	// databases
	////////////
	
	private static IUserDB    userDB    = SMSAppModuleDALFactory.DEFAULT_DAL.getUserDB();
	private static ISessionDB sessionDB = SMSAppModuleDALFactory.DEFAULT_DAL.getSessionDB();
	private static IProfileDB profileDB = SMSAppModuleDALFactoryProfile.DEFAULT_DAL.getProfileDB();
	private static IMatchDB   matchDB   = SMSAppModuleDALFactoryHangman.DEFAULT_DAL.getMatchDB();
	
	
	static {
		try {
			SMSAppModuleListenersHangman.registerEventListeners();
		} catch (Throwable t) {
			throw new RuntimeException("Unable to register Hangman event listeners", t);
		}
	}

	
	@Override
	// this.name is the enumeration property name
	public String getCommandName() {
		return this.name();
	}
	
	
	// SMSAppModuleCommandCommons candidates
	////////////////////////////////////////
	
	/** Returns true if 'parameter' can represent a phone number, false if not */
	private static boolean isParameterAPhoneNumber(String parameter) {
		return parameter.matches("[0-9]+");
	}

	/** Command response when a nickname is not found, to be used by several commands and, possibly, to be refactored to the Profile module */
	private static CommandAnswerDto getNickNotFoundMessage(String nickname) {
		return getSameStateReplyCommandAnswer("Gritei -- alto, até -- porém ninguém respondeu a alcunha '"+nickname+"'");
	}

	/** formalizes the necessity for both the user and profile records being registered for every user on the game and
	 *  formalizes the default nickname registration rule */
	public static ProfileDto assurePhoneNumberHasAnUserAndNickname(String phoneNumber) throws SQLException {
		UserDto user = userDB.assureUserIsRegistered(phoneNumber);
		return assureUserHasANickname(user);
	}
	public static ProfileDto assureUserHasANickname(UserDto user) throws SQLException {
		ProfileDto profile = profileDB.getProfileRecord(user);
		if (profile == null) {
			profile = profileDB.setProfileRecord(new ProfileDto(user, DEFAULT_NICKNAME_PREFIX + user.getPhoneNumber().substring(Math.max(user.getPhoneNumber().length()-4, 0))));
		}
		return profile;
	}

	

		
	/***********************************************************************
	** GLOBAL COMMAND TRIGGERS -- to be used in several navigation states **
	***********************************************************************/
	
	/** global triggers that executes {@link #cmdInviteNicknameOrPhoneNumber} */
	public static String[] trgGlobalInviteNicknameOrPhoneNumber   = {"INVITE +(.*)"};
	/** {@link SMSAppModuleNavigationStatesHangman#nstEnteringMatchWord} triggers that activates {@link #cmdHoldMatchWord} */
	public static String[] trgLocalHoldMatchWord                  = {"([^ ]+)"};
	/** {@link SMSAppModuleNavigationStatesHangman#nstAnsweringToHangmanMatchInvitation} triggers that activates {@link #cmdAcceptMatchInvitation} */
	public static String[] trgLocalAcceptMatchInvitation          = {"YES"};
	/** {@link SMSAppModuleNavigationStatesHangman#nstAnsweringToHangmanMatchInvitation} triggers that activates {@link #cmdRefuseMatchInvitation} */
	public static String[] trgLocalRefuseMatchInvitation          = {"NO"};
	/** {@link SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanBotOpponent} and {@link SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanHumanOpponent} triggers
	 *  that activates {@link #cmdSuggestLetterOrWordForHuman} */
	public static String[] trgLocalNewLetterOrWordSuggestion      = {"([A-Z]+)"};

}