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
			String invitingPlayerNickname    = profileDB.getProfileRecord(session.getUser()).getNickname();
			String wordToPlay                = parameters[0];
			
			ProfileDto opponentProfile = assurePhoneNumberHasAnUserAndNickname(opponentPlayerPhoneNumber);
			String opponentPlayerNickName = opponentProfile.getNickname();

			HangmanGame game = new HangmanGame(wordToPlay, 6);
			
			if ((!wordToPlay.matches("[A-Za-z]+")) || (game.getGameState() == EHangmanGameStates.WON)) {
				throw new RuntimeException("not a good word");
				//CommandMessageDto commandResponse = new CommandMessageDto(phrases.INVITINGNotAGoodWord(game.getWord()), EResponseMessageType.HELP);
				//return getNewCommandAnswerDto(session, commandResponse);
			}
			
			// set inviting & invited player sessions
			SessionModel opponentSession = new SessionModel(sessionDB.getSession(opponentProfile.getUser()));
			MatchDto match = new MatchDto(session.getUser(), opponentSession.getUser(), game.serializeGameState(), System.currentTimeMillis(), EMatchStatus.ACTIVE);
			matchDB.storeNewMatch(match);
			opponentSession.setNavigationState(nstAnsweringToHangmanMatchInvitation);
			opponentSession.setProperty(sprHangmanMatchId, match.getMatchId());
			sessionDB.setSession(opponentSession.getChangedSessionDto());
			//session.setProperty(sprHangmanMatchId, match.getMatchId());
			session.setProperty(sprOpponentPhoneNumber, "");
			session.setNavigationState(nstExistingUser);
			// TODO a mensagem enviada ao convidado pode conter o telefone do proponente, caso o convite seja baseado em número de telefone, para facilitar a identificação do amigo proponente.
			return getSameStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(getInvitationNotificationForInvitingPlayer(opponentPlayerNickName),
			                                                                          userDB.assureUserIsRegistered(opponentPlayerPhoneNumber), getInvitationNotificationForInvitedPlayer(invitingPlayerNickname));
		}
	},
	
	/** Command triggered by messages matched by {@link #trgLocalAcceptInvitation}, invoked by the invited user who wants to accept
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
	
	;
	
	// databases
	////////////
	
	private static IUserDB    userDB    = SMSAppModuleDALFactory.DEFAULT_DAL.getUserDB();
	private static ISessionDB sessionDB = SMSAppModuleDALFactory.DEFAULT_DAL.getSessionDB();
	private static IProfileDB profileDB = SMSAppModuleDALFactoryProfile.DEFAULT_DAL.getProfileDB();
	private static IMatchDB   matchDB   = SMSAppModuleDALFactoryHangman.DEFAULT_DAL.getMatchDB();

	
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
		ProfileDto profile = profileDB.getProfileRecord(user);
		if (profile == null) {
			profile = profileDB.setProfileRecord(new ProfileDto(user, DEFAULT_NICKNAME_PREFIX + phoneNumber.substring(Math.max(phoneNumber.length()-4, 0))));
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

}