package mutua.smsappmodule.smslogic;

import static mutua.smsappmodule.smslogic.CommandCommons.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman.NavigationStatesNamesHangman.*;
import static mutua.smsappmodule.smslogic.sessions.SMSAppModuleSessionsHangman.*;

import java.sql.SQLException;
import java.util.Random;

import mutua.smsappmodule.config.SMSAppModuleConfigurationSubscription;
import mutua.smsappmodule.dal.IMatchDB;
import mutua.smsappmodule.dal.INextBotWordsDB;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.ISessionDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryHangman;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dto.MatchDto;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.dto.MatchDto.EMatchStatus;
import mutua.smsappmodule.hangmangame.HangmanGame;
import mutua.smsappmodule.hangmangame.HangmanGame.EHangmanGameStates;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.NavigationState;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman;
import mutua.smsappmodule.smslogic.sessions.SMSAppModuleSessionsHangman;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * SMSAppModuleCommandsHangman.java
 * ================================
 * (created by luiz, Sep 18, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Hangman" SMS Module.
 * It is a god idea, when possible, to make command names match phrase names.
 * 
 * This class implements the "Instant VAS SMSApp Command Processors" design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleCommandsHangman {
	
	/** Class to be statically imported by the Configurators to refer to commands when defining the {@link CommandTriggersDto} */
	public static class CommandNamesHangman {
		/** @see SMSAppModuleCommandsHangman#cmdPlayWithRandomUserOrBot */
		public final static String cmdPlayWithRandomUserOrBot            = "PlayWithRandomUserOrBot";
		/** @see SMSAppModuleCommandsHangman#cmdInviteNicknameOrPhoneNumber */
		public final static String cmdInviteNicknameOrPhoneNumber        = "InviteNicknameOrPhoneNumber";
		/** @see SMSAppModuleCommandsHangman#cmdStartHangmanMatchInvitationProcess */
		public final static String cmdStartHangmanMatchInvitationProcess = "StartHangmanMatchInvitationProcess";
		/** @see SMSAppModuleCommandsHangman#cmdHoldOpponentPhoneNumber */
		public final static String cmdHoldOpponentPhoneNumber            = "HoldOpponentPhoneNumber";
		/** @see SMSAppModuleCommandsHangman#cmdHoldOpponentNickname */
		public final static String cmdHoldOpponentNickname               = "HoldOpponentNickname";
		/** @see SMSAppModuleCommandsHangman#cmdHoldMatchWord */
		public final static String cmdHoldMatchWord                      = "HoldMatchWord";
		/** @see SMSAppModuleCommandsHangman#cmdAcceptMatchInvitation */
		public final static String cmdAcceptMatchInvitation              = "AcceptMatchInvitation";
		/** @see SMSAppModuleCommandsHangman#cmdRefuseMatchInvitation */
		public final static String cmdRefuseMatchInvitation              = "RefuseMatchInvitation";
		/** @see SMSAppModuleCommandsHangman#cmdSuggestLetterOrWordForHuman */
		public final static String cmdSuggestLetterOrWordForHuman        = "SuggestLetterOrWordForHuman";
		/** @see SMSAppModuleCommandsHangman#cmdSuggestLetterOrWordForBot */
		public final static String cmdSuggestLetterOrWordForBot          = "SuggestLetterOrWordForBot";
		/** @see SMSAppModuleCommandsHangman#cmdGiveUpCurrentHumanMatch */
		public final static String cmdGiveUpCurrentHumanMatch            = "GiveUpCurrentHumanMatch";
		/** @see SMSAppModuleCommandsHangman#cmdGiveUpCurrentBotMatch */
		public final static String cmdGiveUpCurrentBotMatch              = "GiveUpCurrentBotMatch";
	}
	
	/** Class to be used as a reference when customizing the MO commands for this module */
	public static class CommandTriggersHangman {
		/** Global triggers (to be used on several navigation states) to start guess word match with a bot or with a random user.
		 *  Receives no parameters --
		 *  activates {@link SMSAppModuleCommandsHangman#cmdPlayWithRandomUserOrBot} */
		public final static String[] trgPlayWithRandomUserOrBot             = {"PLAY"};
		/** Global triggers (to be used on several navigation states) to invite a human player for a hangman match.
		 *  Receives 1 parameters: the user identification string (phone number or nickname) --
		 *  activates {@link SMSAppModuleCommandsHangman#cmdInviteNicknameOrPhoneNumber} */
		public final static String[] trgGlobalInviteNicknameOrPhoneNumber   = {"INVITE +(.*)"};
		/** Local triggers (available only to the 'inviting user for a match' navigation state) to compute the word to be used on the hangman match.
		 *  Receives 1 parameter: the match word --
		 *  {@link SMSAppModuleNavigationStatesHangman#nstEnteringMatchWord} triggers that activates {@link SMSAppModuleCommandsHangman#cmdHoldMatchWord} */
		public final static String[] trgLocalHoldMatchWord                  = {"([^ ]+)"};
		/** Local triggers (available only to the 'answering invitation' navigation state) to accept to play a hangman match for which the user was previously invited.
		 *  Receives no parameters --
		 *  {@link SMSAppModuleNavigationStatesHangman#nstAnsweringToHangmanMatchInvitation} triggers that activates {@link SMSAppModuleCommandsHangman#cmdAcceptMatchInvitation} */
		public final static String[] trgLocalAcceptMatchInvitation          = {"YES"};
		/** Local triggers (available only to the 'answering invitation' navigation state) to refuse to play a hangman match for which the user was previously invited.
		 *  Receives no parameters --
		 *  {@link SMSAppModuleNavigationStatesHangman#nstAnsweringToHangmanMatchInvitation} triggers that activates {@link SMSAppModuleCommandsHangman#cmdRefuseMatchInvitation} */
		public final static String[] trgLocalRefuseMatchInvitation          = {"NO"};
		/** Local triggers (available only to the 'playing a hangman match to a bot or human' navigation states) that will recognize patterns to be interpreted as a letter for a hangman match turn.
		 *  Designed to be used together with {@link #trgLocalWordSuggestionFallback}. Receives 1 parameter: the suggested letter or word
		 *  {@link SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanBotOpponent} and
		 *  {@link SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanHumanOpponent} triggers that activates {@link SMSAppModuleCommandsHangman#cmdSuggestLetterOrWordForHuman} */
		public final static String[] trgLocalSingleLetterSuggestion         = {"([A-Z])"};
		/** Local triggers (available only to the 'playing a hangman match to a bot or human' navigation states) that will recognize patterns to be interpreted as a word for a hangman match turn.
		 *  This trigger should be used as a fallback, after no other commands have been recognized. Receives 1 parameter: the suggested letter or word
		 *  {@link SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanBotOpponent} and
		 *  {@link SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanHumanOpponent} triggers that activates {@link SMSAppModuleCommandsHangman#cmdSuggestLetterOrWordForHuman} */
		public final static String[] trgLocalWordSuggestionFallback         = {"([A-Z]+)"};
		/** Local triggers (available only to the 'playing a hangman match to a bot or human' navigation states) that will recognize "cancel this match" intend by the user.
		 *  {@link SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanBotOpponent} and
		 *  {@link SMSAppModuleNavigationStatesHangman#nstGuessingWordFromHangmanHumanOpponent} triggers that activates {@link SMSAppModuleCommandsHangman#cmdGiveUpCurrentHumanMatch} and {@link SMSAppModuleCommandsHangman#cmdGiveUpCurrentBotMatch}*/
		public final static String[] trgLocalEndCurrentMatch                = {"END"};
	}
	
	// Instance Fields
	//////////////////

	private final SMSAppModulePhrasingsHangman hangmanPhrases;
	private final IUserDB         userDB;
	private final ISessionDB      sessionDB;
	private final IProfileDB      profileDB;
	private final IMatchDB        matchDB;
	private final INextBotWordsDB nextBotWordsDB;
	
	private final SMSAppModuleListenersHangman eventListeners;
	
	/** Default nickname prefix for invited & new users, before they have a change to send NICK <their_chosen_nick>. The game makes all users available for a match
	 *  since the beginning, therefore, each user must be addressable via a nickname. The default nickname for each one will be this prefix followed by the
	 *  last 4 digits of their phone number -- a sequence number will further added whenever their is a nickname collision */
	private final String defaultNicknamePrefix;
	
	/** the list of real or fake phone numbers that should be assigned automated playing functionalities */
	private final String[] botPhoneNumbers;
	
	/** the list of words to use for matches were bots are responsible for providing the words */
	private final String[] botWords;
	
	
	/** Constructs an instance of this module's command processors.<pre>
	 *  @param hangmanPhrases           an instance of the phrasings to be used
	 *  @param subscriptionEventsServer one of the values returned by {@link SMSAppModuleConfigurationSubscription#getSubscriptionModuleInstances}
	 *  @param baseModuleDAL            one of the members of {@link SMSAppModuleDALFactory}
	 *  @param profileModuleDAL         equivalent as the above
	 *  @param hangmanModuleDAL         equivalent as the above
	 *  @param defaultNicknamePrefix    @see {@link #defaultNicknamePrefix}
	 *  @param botPhoneNumbers          @see {@link #botPhoneNumbers}
	 *  @param botWords					@see {@link #botWords} */
	public SMSAppModuleCommandsHangman(SMSAppModulePhrasingsHangman   hangmanPhrases,
	                                   SMSAppModuleEventsSubscription subscriptionEventsServer,
	                                   SMSAppModuleDALFactory         baseModuleDAL,
	                                   SMSAppModuleDALFactoryProfile  profileModuleDAL,
	                                   SMSAppModuleDALFactoryHangman  hangmanModuleDAL,
	                                   String defaultNicknamePrefix,
	                                   String[] botPhoneNumbers,
	                                   String[] botWords) {
		this.hangmanPhrases        = hangmanPhrases;
		this.userDB                = baseModuleDAL.getUserDB();
		this.sessionDB             = baseModuleDAL.getSessionDB();
		this.profileDB             = profileModuleDAL.getProfileDB();
		this.matchDB               = hangmanModuleDAL.getMatchDB();
		this.nextBotWordsDB        = hangmanModuleDAL.getNextBotWordsDB();
		this.defaultNicknamePrefix = defaultNicknamePrefix;
		this.botPhoneNumbers       = botPhoneNumbers;
		this.botWords              = botWords;

		// register event listeners
		if (subscriptionEventsServer != null) try {
			eventListeners = new SMSAppModuleListenersHangman(this, subscriptionEventsServer);
		} catch (Throwable t) {
			throw new RuntimeException("Unable to register Hangman event listeners", t);
		} else {
			eventListeners = null;
		}
	}
	
	/** Triggered by messages matched by {@link CommandTriggersHangman#trgPlayWithRandomUserOrBot}, starts a guess the word match against a bot
	 *  or a "timed out waiting for invited user answer" word providing player. 
	 *  Receives no parameters */
	public final ICommandProcessor cmdPlayWithRandomUserOrBot = new ICommandProcessor(CommandNamesHangman.cmdPlayWithRandomUserOrBot) {
		private int nextBotUserId = 0;
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			
			// get the word providing user or bot (by now, just the bot)
			String     wordProvidingMSISDN          = botPhoneNumbers[(nextBotUserId++) % botPhoneNumbers.length];
			ProfileDto wordProvidingUserProfile     = assurePhoneNumberHasAnUserAndNickname(wordProvidingMSISDN);
			UserDto    wordProvidingUser            = wordProvidingUserProfile.getUser();
			String     wordProvidingPlayerNickname  = wordProvidingUserProfile.getNickname();
			
			// start a new match
			int botWordId  = nextBotWordsDB.getAndIncNextBotWord(wordProvidingUser) % botWords.length;
			String botWord = botWords[botWordId];
			HangmanGame game = new HangmanGame(botWord, 6);
			MatchDto match = new MatchDto(wordProvidingUser, session.getUser(), game.serializeGameState(), System.currentTimeMillis(), EMatchStatus.ACTIVE);
			matchDB.storeNewMatch(match);
			session.setProperty(sprHangmanMatchId, match.getMatchId());
			return getNewStateReplyCommandAnswer(session, nstGuessingWordFromHangmanBotOpponent,
				hangmanPhrases.getWordGuessingPlayerMatchStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar(), wordProvidingPlayerNickname));

		}
	};

	/** Triggered by messages matched by {@link CommandTriggersHangman#trgGlobalInviteNicknameOrPhoneNumber}, starts the invitation process for a human opponent to play a hangman match providing his/her nickname or phone number.
	 *  Receives 1 parameter: the opponent identifier (a nickname or phone number) */
	public final ICommandProcessor cmdInviteNicknameOrPhoneNumber = new ICommandProcessor(CommandNamesHangman.cmdInviteNicknameOrPhoneNumber) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String opponentPhoneNumberOrNickName = parameters[0];
			if (isParameterAPhoneNumber(opponentPhoneNumberOrNickName)) {
				return cmdHoldOpponentPhoneNumber.processCommand(session, carrier, parameters);
			} else {
				return cmdHoldOpponentNickname.processCommand(session, carrier, parameters);
			}
		}
	};
	
	/** Parameterless start of the invitation process for a human opponent to play a hangman match -- matched by {@link CommandTriggersHangman#trgGlobalStartInvitationProcess}
	 *  Receives no parameters */
	public final ICommandProcessor cmdStartHangmanMatchInvitationProcess = new ICommandProcessor(CommandNamesHangman.cmdStartHangmanMatchInvitationProcess) {
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
	};
	
	/** Command to take note of the opponent phone number when inviting someone for a match -- matched by {@link CommandTriggersHangman#trgLocalHoldOpponentPhoneNumber}
	 *  Receives 1 parameter: the opponent phone number */
	public final ICommandProcessor cmdHoldOpponentPhoneNumber = new ICommandProcessor(CommandNamesHangman.cmdHoldOpponentPhoneNumber) {
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
	};
	
	/** Command to take note of the opponent nickname when inviting someone for a match -- matched by {@link CommandTriggersHangman#trgHoldOpponentNickname}
	 *  Receives 1 parameter: the opponent nickname */
	public final ICommandProcessor cmdHoldOpponentNickname = new ICommandProcessor(CommandNamesHangman.cmdHoldOpponentNickname) {
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
			return getNewStateReplyCommandAnswer(session, nstEnteringMatchWord, hangmanPhrases.getAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation(opponentNickname));
		}
	};
	
	/** Command triggered by messages matched by {@link CommandTriggersHangman#trgLocalHoldMatchWord} to take note of the word to play, when inviting someone.
	 *  Assumes the session contains {@link SMSAppModuleSessionsHangman#sprOpponentPhoneNumber}.
	 *  Receives 1 parameter: the desired word */
	public final ICommandProcessor cmdHoldMatchWord = new ICommandProcessor(CommandNamesHangman.cmdHoldMatchWord) {
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
				return getSameStateReplyCommandAnswer(hangmanPhrases.getNotAGoodWord(wordToPlay.toUpperCase()));
			}
			
			// set inviting & invited player sessions
			SessionModel opponentSession = new SessionModel(sessionDB.getSession(opponentProfile.getUser()), null) {@Override public NavigationState getNavigationStateFromStateName(String navigationStateName) {throw new RuntimeException("Innertype sessionModel should not have been asked to provide a navigationState");}};
			MatchDto match = new MatchDto(session.getUser(), opponentSession.getUser(), game.serializeGameState(), System.currentTimeMillis(), EMatchStatus.ACTIVE);
			matchDB.storeNewMatch(match);
			opponentSession.setNavigationState(nstAnsweringToHangmanMatchInvitation);
			opponentSession.setProperty(sprHangmanMatchId, match.getMatchId());
			sessionDB.setSession(opponentSession.getChangedSessionDto());
			//session.setProperty(sprHangmanMatchId, match.getMatchId());
			session.deleteProperty(sprOpponentPhoneNumber);
			session.setNavigationState(nstExistingUser);
			// TODO a mensagem enviada ao convidado pode conter o telefone do proponente, caso o convite seja baseado em número de telefone, para facilitar a identificação do amigo proponente.
			return getSameStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(hangmanPhrases.getInvitationResponseForInvitingPlayer(opponentPlayerNickName),
			                                                                          userDB.assureUserIsRegistered(opponentPlayerPhoneNumber),
			                                                                          hangmanPhrases.getInvitationNotificationForInvitedPlayer(invitingPlayerNickname));
		}
	};
	
	/** Command triggered by messages matched by {@link CommandTriggersHangman#trgLocalAcceptMatchInvitation}, invoked by the invited user who wants to accept
	 *  a hangman match invitation.
	 *  Receives no parameters */
	public final ICommandProcessor cmdAcceptMatchInvitation = new ICommandProcessor(CommandNamesHangman.cmdAcceptMatchInvitation) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			MatchDto match = matchDB.retrieveMatch(session.getIntProperty(sprHangmanMatchId));
			HangmanGame game = new HangmanGame(match.getSerializedGame());
			UserDto wordProvidingPlayer = match.getWordProvidingPlayer();
			String wordGuessingPlayerNickname  = assureUserHasANickname(session.getUser()).getNickname();
			String wordProvidingPlayerNickname = assureUserHasANickname(wordProvidingPlayer).getNickname();
			return getNewStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(session, nstGuessingWordFromHangmanHumanOpponent,
			                                                                         hangmanPhrases.getWordGuessingPlayerMatchStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar(), wordProvidingPlayerNickname),
			                                                                         wordProvidingPlayer,
			                                                                         hangmanPhrases.getWordProvidingPlayerMatchStart(game.getGuessedWordSoFar(), wordGuessingPlayerNickname));
		}
	};
	
	/** Command triggered by messages matched by {@link CommandTriggersHangman#trgLocalAcceptMatchInvitation}, invoked by the invited user who wants to refuse
	 *  a hangman match invitation.
	 *  Receives no parameters */
	public final ICommandProcessor cmdRefuseMatchInvitation = new ICommandProcessor(CommandNamesHangman.cmdRefuseMatchInvitation) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			MatchDto match = matchDB.retrieveMatch(session.getIntProperty(sprHangmanMatchId));
			UserDto wordProvidingPlayer = match.getWordProvidingPlayer();
			String wordGuessingPlayerNickname = assureUserHasANickname(session.getUser()).getNickname();
			String wordProvidingPlayerNickname = assureUserHasANickname(wordProvidingPlayer).getNickname();
			session.deleteProperty(sprHangmanMatchId);
			return getNewStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(session, nstExistingUser,
			                                                                         hangmanPhrases.getInvitationRefusalResponseForInvitedPlayer(wordProvidingPlayerNickname),
			                                                                         wordProvidingPlayer,
			                                                                         hangmanPhrases.getInvitationRefusalNotificationForInvitingPlayer(wordGuessingPlayerNickname));
		}
	};
	
	/** Command triggered by messages matched by {@link CommandTriggersHangman#trgLocalSingleLetterSuggestion} and {@link CommandTriggersHangman#trgLocalWordSuggestionFallback}, 
	 *  issued by the user who is playing a hangman match against a human and is trying to guess the word.
	 *  Receives 1 parameter: the letter, set of characters or word attempted */
	public final ICommandProcessor cmdSuggestLetterOrWordForHuman = new ICommandProcessor(CommandNamesHangman.cmdSuggestLetterOrWordForHuman) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String suggestedLetterOrWord = parameters[0].toUpperCase();
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
					replyMT        = hangmanPhrases.getWordGuessingPlayerStatus(attemptsLeft<6, attemptsLeft<5, attemptsLeft<4, attemptsLeft<3, attemptsLeft<2, attemptsLeft<1,
					                                                            game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar(), wordProvidingNickname);
					notificationMT = hangmanPhrases.getWordProvidingPlayerStatus(attemptsLeft<6, attemptsLeft<5, attemptsLeft<4, attemptsLeft<3, attemptsLeft<2, attemptsLeft<1,
                                                                                 game.getGuessedWordSoFar(), suggestedLetterOrWord, game.getAttemptedLettersSoFar(), wordGuessingNickname);
					matchStatus    = EMatchStatus.ACTIVE;
					commandAnswer  = getSameStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(replyMT, wordProvidingUser, notificationMT);
					break;
				case WON:
					replyMT        = hangmanPhrases.getWinningMessageForWordGuessingPlayer(game.getWord(), wordProvidingNickname);
					notificationMT = hangmanPhrases.getWinningMessageForWordProvidingPlayer(wordGuessingNickname);
					matchStatus    = EMatchStatus.CLOSED_WORD_GUESSED;
					commandAnswer  = getNewStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(session, nstExistingUser, replyMT, wordProvidingUser, notificationMT);
					session.deleteProperty(sprHangmanMatchId);
					break;
				case LOST:
					replyMT        = hangmanPhrases.getLosingMessageForWordGuessingPlayer(game.getWord(), wordProvidingNickname);
					notificationMT = hangmanPhrases.getLosingMessageForWordProvidingPlayer(wordGuessingNickname);
					matchStatus    = EMatchStatus.CLOSED_ATTEMPTS_EXCEEDED;
					commandAnswer  = getNewStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(session, nstExistingUser, replyMT, wordProvidingUser, notificationMT);
					session.deleteProperty(sprHangmanMatchId);
					break;
				default:
					throw new RuntimeException("Don't know nothing about EHangmanGameState." + gameState);
			}
			matchDB.updateMatchStatus(match, matchStatus, game.serializeGameState());
			return commandAnswer;
		}
	};
	
	/** Command triggered by messages matched by {@link CommandTriggersHangman#trgLocalSingleLetterSuggestion} and {@link CommandTriggersHangman#trgLocalWordSuggestionFallback},
	 *  issued by the user who is playing a hangman match against a bot and is trying to guess the word.
	 *  Receives 1 parameter: the letter, set of characters or word attempted */
	public final ICommandProcessor cmdSuggestLetterOrWordForBot = new ICommandProcessor(CommandNamesHangman.cmdSuggestLetterOrWordForBot) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String suggestedLetterOrWord = parameters[0].toUpperCase();
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
			EMatchStatus matchStatus       = null;
			CommandAnswerDto commandAnswer = null;
			switch (gameState) {
				case PLAYING:
					replyMT        = hangmanPhrases.getWordGuessingPlayerStatus(attemptsLeft<6, attemptsLeft<5, attemptsLeft<4, attemptsLeft<3, attemptsLeft<2, attemptsLeft<1,
					                                                            game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar(), wordProvidingNickname);
					matchStatus    = EMatchStatus.ACTIVE;
					commandAnswer  = getSameStateReplyCommandAnswer(replyMT);
					break;
				case WON:
					replyMT        = hangmanPhrases.getWinningMessageForWordGuessingPlayer(game.getWord(), wordProvidingNickname);
					matchStatus    = EMatchStatus.CLOSED_WORD_GUESSED;
					commandAnswer  = getNewStateReplyCommandAnswer(session, nstExistingUser, replyMT);
					session.deleteProperty(sprHangmanMatchId);
					break;
				case LOST:
					replyMT        = hangmanPhrases.getLosingMessageForWordGuessingPlayer(game.getWord(), wordProvidingNickname);
					matchStatus    = EMatchStatus.CLOSED_ATTEMPTS_EXCEEDED;
					commandAnswer  = getNewStateReplyCommandAnswer(session, nstExistingUser, replyMT);
					session.deleteProperty(sprHangmanMatchId);
					break;
				default:
					throw new RuntimeException("Don't know nothing about EHangmanGameState." + gameState);
			}
			matchDB.updateMatchStatus(match, matchStatus, game.serializeGameState());
			return commandAnswer;
		}
	};
	
	public final ICommandProcessor cmdGiveUpCurrentHumanMatch = new ICommandProcessor(CommandNamesHangman.cmdGiveUpCurrentHumanMatch) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			
			int    matchId               = session.getIntProperty(sprHangmanMatchId);
			MatchDto match               = matchDB.retrieveMatch(matchId);
			
			if (match == null) {
				return null;
			}

			EMatchStatus status = match.getStatus();
			if (status != EMatchStatus.ACTIVE) {
				return null;
			}
			matchDB.updateMatchStatus(match, EMatchStatus.CLOSED_A_PLAYER_GAVE_UP, match.getSerializedGame());
			
			String  currentUserNickname         = assureUserHasANickname(session.getUser()).getNickname();
			UserDto wordProvidingPlayer         = match.getWordProvidingPlayer();
			String  wordProvidingPlayerNickname = assureUserHasANickname(wordProvidingPlayer).getNickname();

			// remove the session variable
			session.deleteProperty(sprHangmanMatchId);

			// since this method may also be called from the 'nstGuessingWordFromHangmanHumanOpponent.onLeavingState' event, we don't want to change the
			// navigation state in that case (since this method would just be called because the navigation state already changed)
			String navigationState = session.getNavigationStateName();
			if (nstGuessingWordFromHangmanHumanOpponent.equals(navigationState)) {
				return getNewStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(session, nstExistingUser,
					hangmanPhrases.getMatchGiveupNotificationForWordGuessingPlayer(wordProvidingPlayerNickname),
					match.getWordProvidingPlayer(), hangmanPhrases.getMatchGiveupNotificationForWordProvidingPlayer(currentUserNickname));
			} else {
				return getSameStateReplyWithAnAdditionalMessageToAnotherUserCommandAnswer(
					hangmanPhrases.getMatchGiveupNotificationForWordGuessingPlayer(wordProvidingPlayerNickname),
					match.getWordProvidingPlayer(), hangmanPhrases.getMatchGiveupNotificationForWordProvidingPlayer(currentUserNickname));
			}
		}
	};

	public final ICommandProcessor cmdGiveUpCurrentBotMatch = new ICommandProcessor(CommandNamesHangman.cmdGiveUpCurrentBotMatch) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			
			int    matchId               = session.getIntProperty(sprHangmanMatchId);
			MatchDto match               = matchDB.retrieveMatch(matchId);
			
			if (match == null) {
				return null;
			}

			EMatchStatus status = match.getStatus();
			if (status != EMatchStatus.ACTIVE) {
				return null;
			}
			matchDB.updateMatchStatus(match, EMatchStatus.CLOSED_A_PLAYER_GAVE_UP, match.getSerializedGame());
			
			String  currentUserNickname         = assureUserHasANickname(session.getUser()).getNickname();
			UserDto wordProvidingPlayer         = match.getWordProvidingPlayer();
			String  wordProvidingPlayerNickname = assureUserHasANickname(wordProvidingPlayer).getNickname();

			// remove the session variable
			session.deleteProperty(sprHangmanMatchId);

			// since this method may also be called from the 'nstGuessingWordFromHangmanBotOpponent.onLeavingState' event, we don't want to change the
			// navigation state in that case (since this method would just be called because the navigation state already changed)
			String navigationState = session.getNavigationStateName();
			if (nstGuessingWordFromHangmanBotOpponent.equals(navigationState)) {
				return getNewStateReplyCommandAnswer(session, nstExistingUser,
					hangmanPhrases.getMatchGiveupNotificationForWordGuessingPlayer(wordProvidingPlayerNickname));
			} else {
				return getSameStateReplyCommandAnswer(hangmanPhrases.getMatchGiveupNotificationForWordGuessingPlayer(wordProvidingPlayerNickname));
			}
		}
	};
	
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
	public ProfileDto assurePhoneNumberHasAnUserAndNickname(String phoneNumber) throws SQLException {
		UserDto user = userDB.assureUserIsRegistered(phoneNumber);
		return assureUserHasANickname(user);
	}
	public ProfileDto assureUserHasANickname(UserDto user) throws SQLException {
		ProfileDto profile = profileDB.getProfileRecord(user);
		if (profile == null) {
			profile = profileDB.setProfileRecord(new ProfileDto(user, defaultNicknamePrefix + user.getPhoneNumber().substring(Math.max(user.getPhoneNumber().length()-4, 0))));
		}
		return profile;
	}

	
	// Command List
	///////////////
	
	/** The list of all commands -- to allow deserialization by {@link CommandTriggersDto} */
	public final ICommandProcessor[] values = {
		cmdPlayWithRandomUserOrBot,
		cmdInviteNicknameOrPhoneNumber,
		cmdStartHangmanMatchInvitationProcess,
		cmdHoldOpponentPhoneNumber,
		cmdHoldOpponentNickname,
		cmdHoldMatchWord,
		cmdAcceptMatchInvitation,
		cmdRefuseMatchInvitation,
		cmdSuggestLetterOrWordForHuman,
		cmdSuggestLetterOrWordForBot,
		cmdGiveUpCurrentHumanMatch,
		cmdGiveUpCurrentBotMatch,
	};

		
}