package mutua.hangmansmsgame.smslogic;

import static mutua.hangmansmsgame.config.Configuration.DEFAULT_NICKNAME_PREFIX;
import static mutua.hangmansmsgame.config.Configuration.SUBSCRIPTION_CHANNEL_NAME;
import static mutua.hangmansmsgame.config.Configuration.SUBSCRIPTION_ENGINE;
import static mutua.hangmansmsgame.config.Configuration.log;
import static mutua.icc.instrumentation.DefaultInstrumentationEvents.DIE_DEBUG;
import static mutua.icc.instrumentation.DefaultInstrumentationProperties.DIP_MSG;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Hashtable;

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
import mutua.subscriptionengine.SubscriptionEngine.ESubscriptionOperationStatus;

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
	
	
	// DDD to states map
	////////////////////
	
	private static String[][] dddAndStates = {
		{"11", "SP"}, {"12", "SP"}, {"13", "SP"}, {"14", "SP"}, {"15", "SP"}, {"16", "SP"}, {"17", "SP"}, {"18", "SP"}, {"19", "SP"},
        {"21", "RJ"}, {"22", "RJ"}, {"24", "RJ"}, {"27", "ES"}, {"28", "ES"}, {"31", "MG"}, {"32", "MG"}, {"33", "MG"}, {"34", "MG"},
        {"35", "MG"}, {"37", "MG"}, {"38", "MG"}, {"41", "PR"}, {"42", "PR"}, {"43", "PR"}, {"44", "PR"}, {"45", "PR"}, {"46", "PR"},
        {"47", "SC"}, {"48", "SC"}, {"49", "SC"}, {"51", "RS"}, {"53", "RS"}, {"54", "RS"}, {"55", "RS"}, {"61", "DF"}, {"62", "GO"},
        {"63", "TO"}, {"64", "GO"}, {"65", "MT"}, {"66", "MT"}, {"67", "MS"}, {"68", "AC"}, {"69", "RO"}, {"71", "BA"}, {"73", "BA"},
        {"74", "BA"}, {"75", "BA"}, {"77", "BA"}, {"79", "SE"}, {"81", "PE"}, {"82", "AL"}, {"83", "PB"}, {"84", "RN"}, {"85", "CE"},
        {"86", "PI"}, {"87", "PE"}, {"88", "CE"}, {"89", "PI"}, {"91", "PA"}, {"92", "AM"}, {"93", "PA"}, {"94", "PA"}, {"95", "RR"},
        {"96", "AP"}, {"97", "AM"}, {"98", "MA"}, {"99", "MA"},
	};
	private static Hashtable<String, String> dddToStatesMap;
	
	static {
		dddToStatesMap = new Hashtable<String, String>();
		for (String[] dddAndState : dddAndStates) {
			String ddd   = dddAndState[0];
			String state = dddAndState[1];
			dddToStatesMap.put(ddd, state);
		}
	}
	
	// common & decoupled (testable) methods
	////////////////////////////////////////
	
	protected static String getBrazillianPhoneState(String phone) {
		if (phone == null) {
            return null;
        }
		String ddd;
		if (phone.length() > 11) {
			if (phone.startsWith("+55")) {
				ddd = phone.substring(3, 5);
			} else if (phone.startsWith("55")) {
				ddd = phone.substring(2, 4);
			} else {
				return "--";
			}
		} else if (phone.length() < 8) {
			return "--";
		} else {
			ddd = phone.substring(0, 2);
		}
        String state = dddToStatesMap.get(ddd);
        if (state == null) {
            return "--";
        } else {
            return state;
        }
	}
	
	protected static boolean registerUserNickname(String phone, String nickname) throws SQLException {
		int count = 1;
		String alternativeNick = nickname;
		while (!userDB.checkAvailabilityAndRecordNickname(phone, alternativeNick)) {
			alternativeNick = nickname + count;
			count++;
		}
		return true;
	}
	
	/** This is the point we may call Celltick APIs for registration */
	protected static boolean assureUserIsRegistered(String phone) throws SQLException {

		if (userDB.isUserOnRecord(phone)) {
			return true;
		}
		
		ESubscriptionOperationStatus subscriptionStatus = SUBSCRIPTION_ENGINE.subscribeUser(phone, SUBSCRIPTION_CHANNEL_NAME);
		
		if (subscriptionStatus == ESubscriptionOperationStatus.OK) {
			log.reportEvent(DIE_DEBUG, DIP_MSG, "Hangman: registering user "+phone+" succeeded");
			return registerUserNickname(phone, DEFAULT_NICKNAME_PREFIX + phone.substring(Math.max(phone.length()-4, 0)));
		} else {
			log.reportEvent(DIE_DEBUG, DIP_MSG, "Hangman: registering user "+phone+" failed");
			return false;
		}
	}

	/** Called to store a new match and assure both users exists on the database */
	protected static int startNewMatch(MatchDto match) throws SQLException {
		assureUserIsRegistered(match.getWordProvidingPlayerPhone());
		assureUserIsRegistered(match.getWordGuessingPlayerPhone());
		return matchDB.storeNewMatch(match);
	}

	/** Returns true if 'parameter' can represent a phone number, false if not */
	public static boolean isParameterAPhoneNumber(String parameter) {
		return parameter.matches("[0-9]+");
	}
	
	/** 'playersInfo' is defined by IPhraseology.LISTINGShowPlayers */
	protected static String[] getNewPresentedUsers(String[] presentedUsers, String[][] playersInfo) throws SQLException {
		String[] newPresentedUsers = new String[presentedUsers.length + playersInfo.length];
		System.arraycopy(presentedUsers, 0, newPresentedUsers, 0, presentedUsers.length);
		int newPresentedUsersIndex = presentedUsers.length;
		for (String[] playerInfo : playersInfo) {
			String nickname = playerInfo[0];
			newPresentedUsers[newPresentedUsersIndex++] = userDB.getUserPhoneNumber(nickname);
		}
		return newPresentedUsers;
	}

	/** Returns a 'playersInfo' structure to be used by IPhraseology.LISTINGShowPlayers */
	protected static String[][] getPlayersInfoToPresentOnTheListCommandRespectingTheMaximumNumberOfCharacters(IPhraseology phrases, int maxChars, String[] presentedUsers) throws SQLException {
		int lookAhead = 10;
		String[] latestPlayerPhones = sessionDB.getRecentlyUpdatedSessionPhoneNumbers(presentedUsers.length+lookAhead);
		String[] unpresentedPhones = new String[lookAhead];
		int unpresentedPhonesIndex = 0;
		NEXT_PLAYER: for (int i=0; i<latestPlayerPhones.length; i++) {
			if (!userDB.isUserOnRecord(latestPlayerPhones[i])) {
				continue NEXT_PLAYER;
			}
			for (int j=0; j<presentedUsers.length; j++) {
				if (latestPlayerPhones[i].equals(presentedUsers[j])) {
					continue NEXT_PLAYER;
				}
			}
			unpresentedPhones[unpresentedPhonesIndex++] = latestPlayerPhones[i];
		}
		String[][] playersInfo = new String[unpresentedPhonesIndex][3];
		for (int i=0; i<playersInfo.length; i++) {
			playersInfo[i][0] = userDB.getUserNickname(unpresentedPhones[i]);
			playersInfo[i][1] = getBrazillianPhoneState(unpresentedPhones[i]);
			playersInfo[i][2] = Integer.toString(10+i);
		}
		int numberOfPlayersInfo = 1;
		String[][] playersInfoCandidate = null;
		while (true) {
			if (numberOfPlayersInfo > playersInfo.length) {
				break;
			}
			playersInfoCandidate = Arrays.copyOf(playersInfo, numberOfPlayersInfo);
			String phrase = phrases.LISTINGShowPlayers(playersInfoCandidate);
			if (phrase.length() <= maxChars) {
				numberOfPlayersInfo++;
			} else {
				break;
			}
		}
		return playersInfoCandidate;
	}
	
	protected static String serializeStringArray(String[] stringArray, String separator) {
		StringBuffer sb = new StringBuffer();
		for (String element : stringArray) {
			sb.append(element).append(separator);
		}
		return sb.toString();
	}
	
	protected static String[] desserializeStringArray(String serializedStringArray, String separator) {
		return serializedStringArray.split(separator);
	}
	
	/** Adds two arrays */
	protected static CommandMessageDto[] addCommandMessages(CommandMessageDto[] a1, CommandMessageDto[] a2) {
		if (a1 == null) {
			return a2;
		} else if (a2 == null) {
			return a1;
		} else {
			CommandMessageDto[] combinedMessages = new CommandMessageDto[a1.length + a2.length];
			System.arraycopy(a1, 0, combinedMessages, 0, a1.length);
			System.arraycopy(a2, 0, combinedMessages, a1.length, a2.length);
			return combinedMessages;
		}
	}
	
	/** to be called by the word guessing player, after the match has formed */
	protected static MatchPlayersInfo getMatchPlayersInfo(MatchDto match) throws SQLException {
		String wordProvidingPlayerPhone = match.getWordProvidingPlayerPhone();
		String wordGuessingPlayerPhone  = match.getWordGuessingPlayerPhone();
		String wordProvidingPlayerNick  = userDB.getUserNickname(wordProvidingPlayerPhone);
		String wordGuessingPlayerNick   = userDB.getUserNickname(wordGuessingPlayerPhone);
		return new MatchPlayersInfo(wordProvidingPlayerPhone, wordGuessingPlayerPhone,
		                            wordProvidingPlayerNick,  wordGuessingPlayerNick);
	}

	/** to be called by the word guessing player, before the match has formed */
	protected static MatchPlayersInfo getMatchPlayersInfo(SessionDto currentSession) throws SQLException {
		String wordProvidingPlayerPhone = currentSession.getParameterValue(ESessionParameters.OPPONENT_PHONE_NUMBER);
		String wordGuessingPlayerPhone  = currentSession.getPhone();
		String wordProvidingPlayerNick  = userDB.getUserNickname(wordProvidingPlayerPhone);
		String wordGuessingPlayerNick   = userDB.getUserNickname(wordGuessingPlayerPhone);
		return new MatchPlayersInfo(wordProvidingPlayerPhone, wordGuessingPlayerPhone,
		                            wordProvidingPlayerNick,  wordGuessingPlayerNick);
	}
		
	/** Implements rules like warning users that a match was cancelled by commands who would force the user from quitting the playing state (invite, list, ...),
	 *  possibly issueing messages to both users and taking other actions as well. */
	protected static CommandMessageDto[] applySessionTransitionRules(SessionDto currentSession, ESTATES newNavigationState) throws SQLException {
		if (ESTATES.GUESSING_HUMAN_WORD.name().equals(currentSession.getNavigationState()) &&
			(newNavigationState != ESTATES.GUESSING_HUMAN_WORD)) {
			int matchId = Integer.parseInt(currentSession.getParameterValue(ESessionParameters.MATCH_ID));
			MatchDto match = matchDB.retrieveMatch(matchId);
			EMatchStatus status = match.getStatus();
			if (status == EMatchStatus.ACTIVE) {
				IPhraseology phrases = IPhraseology.getCarrierSpecificPhraseology(ESMSInParserCarrier.TEST_CARRIER);
				MatchPlayersInfo matchPlayersInfo = getMatchPlayersInfo(match);
				matchDB.storeNewMatch(match.getNewMatch(EMatchStatus.CLOSED_A_PLAYER_GAVE_UP));
				return new CommandMessageDto[] {
					new CommandMessageDto(matchPlayersInfo.wordProvidingPlayerPhone, phrases.PLAYINGMatchGiveupNotificationForWordProvidingPlayer(matchPlayersInfo.wordGuessingPlayerNick), EResponseMessageType.HELP),
					new CommandMessageDto(matchPlayersInfo.wordGuessingPlayerPhone,  phrases.PLAYINGMatchGiveupNotificationForWordGuessingPlayer(matchPlayersInfo.wordProvidingPlayerNick), EResponseMessageType.HELP),
				};
			}
		}
		return null;
	}
	
	/** Get a new command answer, applying session transition rules */
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionDto currentSession, CommandMessageDto[] commandMessages, ESTATES newNavigationState, ESessionParameters parameter, String parameterValue) throws SQLException {
		CommandMessageDto[] transitionMessages = applySessionTransitionRules(currentSession, newNavigationState);
		SessionDto newSession = currentSession.getNewSessionDto(newNavigationState.name(), parameter, parameterValue);
		return new CommandAnswerDto(addCommandMessages(transitionMessages, commandMessages), newSession);
	}
	
	/** Get a new command answer, applying session transition rules */
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionDto currentSession, CommandMessageDto[] commandMessages, ESTATES newNavigationState) throws SQLException {
		CommandMessageDto[] transitionMessages = applySessionTransitionRules(currentSession, newNavigationState);
		SessionDto newSession = currentSession.getNewSessionDto(newNavigationState.name());
		return new CommandAnswerDto(addCommandMessages(transitionMessages, commandMessages), newSession);
	}

	protected static CommandAnswerDto getNewCommandAnswerDto(SessionDto currentSession, CommandMessageDto[] commandMessages) throws SQLException {
		CommandMessageDto[] transitionMessages = applySessionTransitionRules(currentSession, null);
		return new CommandAnswerDto(addCommandMessages(transitionMessages, commandMessages), null);
	}

	
	// overloads with a single command message
	//////////////////////////////////////////
	
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionDto currentSession, CommandMessageDto commandMessage) throws SQLException {
		return getNewCommandAnswerDto(currentSession, new CommandMessageDto[] {commandMessage});
	}

	protected static CommandAnswerDto getNewCommandAnswerDto(SessionDto currentSession, CommandMessageDto commandMessage, ESTATES newNavigationState) throws SQLException {
		return getNewCommandAnswerDto(currentSession, new CommandMessageDto[] {commandMessage}, newNavigationState);
	}
	
	protected static CommandAnswerDto getNewCommandAnswerDto(SessionDto currentSession, CommandMessageDto commandMessage, ESTATES newNavigationState, ESessionParameters parameter, String parameterValue) throws SQLException {
		return getNewCommandAnswerDto(currentSession, new CommandMessageDto[] {commandMessage}, newNavigationState, parameter, parameterValue);
	}
	
	
	// common response messages
	///////////////////////////
	
	private static CommandAnswerDto getNickNotFoundMessage(SessionDto session, IPhraseology phrases, String nickname) throws SQLException {
		return getNewCommandAnswerDto(session, new CommandMessageDto(phrases.PROVOKINGNickNotFound(nickname), EResponseMessageType.HELP));
	}


	// commands
	///////////


	/** Generates a 'CommandAnswerDto' with 0 messages to be sent */
	public static final ICommandProcessor NO_ANSWER = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			return null;
		}
	};

	public static final ICommandProcessor SHOW_WELCOME_MESSAGE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INFOWelcome(),
			                                                          EResponseMessageType.HELP);
			return getNewCommandAnswerDto(session, commandResponse, ESTATES.EXISTING_USER);
		}
	};
	
	public static final ICommandProcessor SHOW_FULL_HELP_MESSAGE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String[] smsTexts = phrases.INFOFullHelp();
			CommandMessageDto[] commandResponses = new CommandMessageDto[smsTexts.length];
			for (int i=0; i<commandResponses.length; i++) {
				commandResponses[i] = new CommandMessageDto(smsTexts[i], EResponseMessageType.HELP);
			}
			return getNewCommandAnswerDto(session, commandResponses);
		}
	};

	/** Called when the user just wants to play, with any opponent */
	public static final ICommandProcessor PLAY_WITH_RANDOM_USER_OR_BOT = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String wordProvidingPlayerNick  = "DomBot";
			
			// start a new Match
			HangmanGame game = new HangmanGame("chimpanzee", 6);
			String serializedGameState = game.serializeGameState();
			
			CommandMessageDto wordGuessingPlayerMessage = new CommandMessageDto(phrases.PLAYINGWordGuessingPlayerStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()),
                                                                                EResponseMessageType.PLAYING);

			sessionDB.setSession(session.getNewSessionDto(ESTATES.GUESSING_BOT_WORD.name(),
			                                              ESessionParameters.MATCH_BOT_NAME, wordProvidingPlayerNick,
			                                              ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, serializedGameState));
			return getNewCommandAnswerDto(session, wordGuessingPlayerMessage);
		}
	};

	/** Called when the user provides the intention to invite someone and provide the parameter to reference that one.
	 *  parameter 1: the phone number or nick name of the user */
	public static final ICommandProcessor INVITE_NICK_OR_PHONE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String opponentPhoneNumberOrNickName = parameters[0];
			if (isParameterAPhoneNumber(opponentPhoneNumberOrNickName)) {
				return HOLD_OPPONENT_PHONE.processCommand(session, carrier, parameters, phrases);
			} else {
				return HOLD_OPPONENT_NICK.processCommand(session, carrier, parameters, phrases);
			}
		}
	};

	public static final ICommandProcessor START_INVITATION_PROCESS = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			
			CommandMessageDto commandResponse;
			
			if (assureUserIsRegistered(session.getPhone())) {
				String invitingPlayerNickname = userDB.getUserNickname(session.getPhone());
				commandResponse = new CommandMessageDto(phrases.INVITINGAskOpponentNickOrPhone(invitingPlayerNickname),
				                                        EResponseMessageType.HELP);
				return getNewCommandAnswerDto(session, commandResponse, ESTATES.ENTERING_OPPONENT_CONTACT_INFO);
			} else {
				commandResponse = new CommandMessageDto(phrases.INFOCouldNotRegister(), EResponseMessageType.HELP);
				return getNewCommandAnswerDto(session, commandResponse);
			}
			
		}
	};

	public static final ICommandProcessor HOLD_OPPONENT_PHONE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String opponentPhoneNumber = parameters[0];
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation(opponentPhoneNumber),
			                                                          EResponseMessageType.ACQUIRE_MATCH_INFORMATION);
			return getNewCommandAnswerDto(session, commandResponse, ESTATES.ENTERING_MATCH_WORD_TO_PLAY,
			                              ESessionParameters.OPPONENT_PHONE_NUMBER, opponentPhoneNumber);
		}
	};
	
	public static final ICommandProcessor HOLD_OPPONENT_NICK = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String opponentNickname    = parameters[0];
			String opponentPhoneNumber = userDB.getUserPhoneNumber(opponentNickname);
			if (opponentPhoneNumber == null) {
				return getNickNotFoundMessage(session, phrases, opponentNickname);
			}
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation(opponentNickname),
			                                                          EResponseMessageType.ACQUIRE_MATCH_INFORMATION);
			return getNewCommandAnswerDto(session, commandResponse, ESTATES.ENTERING_MATCH_WORD_TO_PLAY,
			                              ESessionParameters.OPPONENT_PHONE_NUMBER, opponentPhoneNumber);
		}
	};

	public static final ICommandProcessor HOLD_MATCH_WORD = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String opponentPlayerPhoneNumber = session.getParameterValue(ESessionParameters.OPPONENT_PHONE_NUMBER);
			String invitingPlayerNickName    = userDB.getUserNickname(session.getPhone());
			String wordToPlay                = parameters[0];
			
			// if the invited player could not be registered
			if (!assureUserIsRegistered(opponentPlayerPhoneNumber)) {
				return getNewCommandAnswerDto(session,
					new CommandMessageDto(phrases.INFOCouldNotRegister(), EResponseMessageType.HELP));
			}
			
			String opponentPlayerNickName = userDB.getUserNickname(opponentPlayerPhoneNumber);

			HangmanGame game = new HangmanGame(wordToPlay, 6);
			
			// TODO a mensagem enviada ao convidado pode conter o telefone do proponente, caso o convite seja baseado em número de telefone, para facilitar a identificação do amigo proponente.
			CommandMessageDto invitingPlayerMessage = new CommandMessageDto(phrases.INVITINGInvitationNotificationForInvitingPlayer(opponentPlayerNickName),
			                                                                EResponseMessageType.ACQUIRE_MATCH_INFORMATION);
			CommandMessageDto opponentPlayerMessage = new CommandMessageDto(opponentPlayerPhoneNumber, phrases.INVITINGInvitationNotificationForInvitedPlayer(invitingPlayerNickName),
                                                                            EResponseMessageType.INVITATION_MESSAGE);
			SessionDto opponentSession = new SessionDto(opponentPlayerPhoneNumber, ESTATES.ANSWERING_TO_INVITATION.name(),
			                                            ESessionParameters.OPPONENT_PHONE_NUMBER, session.getPhone(),
			                                            ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, game.serializeGameState());
			sessionDB.setSession(opponentSession);
			return getNewCommandAnswerDto(session, new CommandMessageDto[] {invitingPlayerMessage, opponentPlayerMessage});
		}
	};

	/** Called when the word guessing player answered YES to the invitation message he/she received to attend to a hangman match */
	public static final ICommandProcessor ACCEPT_INVITATION = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String serializedGameState      = session.getParameterValue(ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE);
			
			MatchPlayersInfo matchPlayersInfo = getMatchPlayersInfo(session);

			// start a new Match
			HangmanGame game  = new HangmanGame(serializedGameState);
			MatchDto match = new MatchDto(matchPlayersInfo.wordProvidingPlayerPhone, matchPlayersInfo.wordGuessingPlayerPhone,
			                              serializedGameState, System.currentTimeMillis(), EMatchStatus.ACTIVE);
			int matchId = startNewMatch(match);
			
			CommandMessageDto wordProvidingPlayerMessage = new CommandMessageDto(
					matchPlayersInfo.wordProvidingPlayerPhone,
					phrases.PLAYINGWordProvidingPlayerStart(game.getGuessedWordSoFar(), matchPlayersInfo.wordGuessingPlayerNick),
			        EResponseMessageType.PLAYING);
			CommandMessageDto wordGuessingPlayerMessage = new CommandMessageDto(
					phrases.PLAYINGWordGuessingPlayerStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()),
                    EResponseMessageType.PLAYING);

			return getNewCommandAnswerDto(session, new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage},
			                              ESTATES.GUESSING_HUMAN_WORD, ESessionParameters.MATCH_ID, Integer.toString(matchId));
		}
	};

	/** Called when the word guessing player answered NO to the invitation message he/she received to attend to a hangman match */
	public static final ICommandProcessor REFUSE_INVITATION = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {

			MatchPlayersInfo matchPlayersInfo = getMatchPlayersInfo(session);
			
			// refuse to match
			CommandMessageDto wordProvidingPlayerMessage = new CommandMessageDto(
					matchPlayersInfo.wordProvidingPlayerPhone,
					phrases.INVITINGInvitationRefusalNotificationForInvitingPlayer(matchPlayersInfo.wordGuessingPlayerNick),
					EResponseMessageType.HELP);
			CommandMessageDto wordGuessingPlayerMessage = new CommandMessageDto(
					phrases.INVITINGInvitationRefusalNotificationForInvitedPlayer(matchPlayersInfo.wordProvidingPlayerNick),
					EResponseMessageType.HELP);

			return getNewCommandAnswerDto(session, new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage},
			                              ESTATES.EXISTING_USER);
		}
	};


	/** Called when the user is attempting to guess a word provided by a human */
	public static final ICommandProcessor SUGGEST_LETTER_OR_WORD_FOR_HUMAN = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			int    matchId                  = Integer.parseInt(session.getParameterValue(ESessionParameters.MATCH_ID));
			String suggestedLetter          = parameters[0];
			String serializedGameState      = session.getParameterValue(ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE);
			
			MatchDto matchData = matchDB.retrieveMatch(matchId);
			MatchPlayersInfo matchPlayersInfo = getMatchPlayersInfo(matchData);
			HangmanGame game  = new HangmanGame(serializedGameState);
			game.suggestLetter(suggestedLetter.charAt(0));
			int attemptsLeft = game.getNumberOfWrongTriesLeft();
			
			CommandMessageDto wordProvidingPlayerMessage;
			CommandMessageDto wordGuessingPlayerMessage;
			CommandAnswerDto answer;
			MatchDto newMatchData;
			EHangmanGameStates gameState = game.getGameState();
			switch (gameState) {
				case PLAYING:
					wordProvidingPlayerMessage = new CommandMessageDto(matchPlayersInfo.wordProvidingPlayerPhone,
						phrases.PLAYINGWordProvidingPlayerStatus(
							attemptsLeft<6, attemptsLeft<5, attemptsLeft<4, attemptsLeft<3, attemptsLeft<2, attemptsLeft<1,
							game.getGuessedWordSoFar(), suggestedLetter, game.getAttemptedLettersSoFar(), matchPlayersInfo.wordGuessingPlayerNick),
						EResponseMessageType.PLAYING);
					wordGuessingPlayerMessage = new CommandMessageDto(
						phrases.PLAYINGWordGuessingPlayerStatus(
							attemptsLeft<6, attemptsLeft<5, attemptsLeft<4, attemptsLeft<3, attemptsLeft<2, attemptsLeft<1,
							game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()),
						EResponseMessageType.PLAYING);
					newMatchData = matchData;
					answer = getNewCommandAnswerDto(session, new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage},
                                                    ESTATES.GUESSING_HUMAN_WORD, ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, game.serializeGameState());
					break;
				case WON:
					matchDB.updateMatchStatus(matchId, EMatchStatus.CLOSED_WORD_GUESSED);
					wordProvidingPlayerMessage = new CommandMessageDto(matchPlayersInfo.wordProvidingPlayerPhone,
						phrases.PLAYINGWinningMessageForWordProvidingPlayer(matchPlayersInfo.wordGuessingPlayerNick),
						EResponseMessageType.PLAYING);
					wordGuessingPlayerMessage = new CommandMessageDto(
						phrases.PLAYINGWinningMessageForWordGuessingPlayer(game.getWord(), "xx.xx.xx.xx"),
						EResponseMessageType.PLAYING);
					newMatchData = matchData.getNewMatch(EMatchStatus.CLOSED_WORD_GUESSED);
					answer = getNewCommandAnswerDto(session, new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage},
                                                    ESTATES.EXISTING_USER, ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, game.serializeGameState());
					break;
				case LOST:
					matchDB.updateMatchStatus(matchId, EMatchStatus.CLOSED_ATTEMPTS_EXCEEDED);
					wordProvidingPlayerMessage = new CommandMessageDto(matchPlayersInfo.wordProvidingPlayerPhone,
						phrases.PLAYINGLosingMessageForWordProvidingPlayer(matchPlayersInfo.wordGuessingPlayerNick),
						EResponseMessageType.PLAYING);
					wordGuessingPlayerMessage = new CommandMessageDto(
						phrases.PLAYINGLosingMessageForWordGuessingPlayer(game.getWord(), matchPlayersInfo.wordProvidingPlayerNick),
						EResponseMessageType.PLAYING);
					newMatchData = matchData.getNewMatch(EMatchStatus.CLOSED_ATTEMPTS_EXCEEDED);
					answer = getNewCommandAnswerDto(session, new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage},
                                                    ESTATES.EXISTING_USER, ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, game.serializeGameState());
					break;
				default:
					throw new RuntimeException("Don't know nothing about EHangmanGameState." + gameState);
			}
			
			matchDB.storeNewMatch(newMatchData);
			return answer;
		}
	};
	
	/** Called when the user is attempting to guess a word provided by a bot */
	public static final ICommandProcessor SUGGEST_LETTER_OR_WORD_FOR_BOT = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String suggestedLetter          = parameters[0];
			String serializedGameState      = session.getParameterValue(ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE);
			String wordProvidingPlayerNick  = session.getParameterValue(ESessionParameters.MATCH_BOT_NAME);

			HangmanGame game = new HangmanGame(serializedGameState);
			game.suggestLetter(suggestedLetter.charAt(0));
			int attemptsLeft = game.getNumberOfWrongTriesLeft();
			
			CommandAnswerDto answer;
			CommandMessageDto wordGuessingPlayerMessage;
			EHangmanGameStates gameState = game.getGameState();
			switch (gameState) {
				case PLAYING:
					wordGuessingPlayerMessage = new CommandMessageDto(
						phrases.PLAYINGWordGuessingPlayerStatus(
							attemptsLeft<6, attemptsLeft<5, attemptsLeft<4, attemptsLeft<3, attemptsLeft<2, attemptsLeft<1,
							game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()),
						EResponseMessageType.PLAYING);
					answer = getNewCommandAnswerDto(session, wordGuessingPlayerMessage,
                                                    ESTATES.GUESSING_BOT_WORD, ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, game.serializeGameState());
					break;
				case WON:
					wordGuessingPlayerMessage = new CommandMessageDto(
						phrases.PLAYINGWinningMessageForWordGuessingPlayer(game.getWord(), "xx.xx.xx.xx"),
						EResponseMessageType.PLAYING);
					answer = getNewCommandAnswerDto(session, wordGuessingPlayerMessage,
                                                    ESTATES.EXISTING_USER, ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, game.serializeGameState());
					break;
				case LOST:
					wordGuessingPlayerMessage = new CommandMessageDto(
						phrases.PLAYINGLosingMessageForWordGuessingPlayer(game.getWord(), wordProvidingPlayerNick),
						EResponseMessageType.PLAYING);
					answer = getNewCommandAnswerDto(session, wordGuessingPlayerMessage,
                                                    ESTATES.EXISTING_USER, ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, game.serializeGameState());
					break;
				default:
					throw new RuntimeException("Don't know nothing about EHangmanGameState." + gameState);
			}
			
			return answer;
		}
	};


	/** Called when the user wants to send a chat message to another player */
	public static final ICommandProcessor PROVOKE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String sourceNickname    = userDB.getUserNickname(session.getPhone());
			String targetNickname    = parameters[0];
			String targetPhoneNumber = userDB.getUserPhoneNumber(targetNickname);
			String chatText          = parameters[1];
			if (targetPhoneNumber != null) {
				CommandMessageDto notificationMessage = new CommandMessageDto(phrases.PROVOKINGDeliveryNotification(targetNickname), EResponseMessageType.CHAT);
				CommandMessageDto chatMessage = new CommandMessageDto(targetPhoneNumber, phrases.PROVOKINGSendMessage(sourceNickname, chatText), EResponseMessageType.CHAT);
				return getNewCommandAnswerDto(session, new CommandMessageDto[] {notificationMessage, chatMessage});
			} else {
				return getNickNotFoundMessage(session, phrases, targetNickname);
			}
		}
	};
	
	/** Called when the user wants to see the profile of another user */
	public static final ICommandProcessor SHOW_PROFILE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String nickname = userDB.getCorrectlyCasedNickname(parameters[0]);
			CommandMessageDto message = new CommandMessageDto(phrases.PROFILEView(nickname, getBrazillianPhoneState(session.getPhone()), 0), EResponseMessageType.PROFILE);
			return getNewCommandAnswerDto(session, message);
		}
	};
	
	/** Called when the user wants to reset his/her profile nickname */
	public static final ICommandProcessor DEFINE_NICK = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String newNickname = parameters[0];
			assureUserIsRegistered(session.getPhone());
			userDB.checkAvailabilityAndRecordNickname(session.getPhone(), newNickname);
			String registeredNickname = userDB.getUserNickname(session.getPhone());
			CommandMessageDto message = new CommandMessageDto(phrases.PROFILENickRegisteredNotification(registeredNickname), EResponseMessageType.PROFILE);
			return getNewCommandAnswerDto(session, message);
		}
	};
	
	
	private static CommandAnswerDto performListCommand(SessionDto session,	ESMSInParserCarrier carrier, IPhraseology phrases, String[] presentedUsers) throws SQLException {
		String[][] playersInfo = CommandDetails.getPlayersInfoToPresentOnTheListCommandRespectingTheMaximumNumberOfCharacters(phrases, carrier.getMaxMTChars(), presentedUsers);
		CommandMessageDto message;
		if (playersInfo == null) {
			message = new CommandMessageDto(phrases.LISTINGNoMorePlayers(), EResponseMessageType.PROFILE);
			return getNewCommandAnswerDto(session, message);
		} else {
			message = new CommandMessageDto(phrases.LISTINGShowPlayers(playersInfo), EResponseMessageType.PROFILE);
			String[] newPresentedUsers = getNewPresentedUsers(presentedUsers, playersInfo);
			return getNewCommandAnswerDto(session, message, ESTATES.LISTING_USERS, ESessionParameters.PRESENTED_USERS, serializeStringArray(newPresentedUsers, ";"));
		}
	}


	/** Called when the user wants to a list of users */
	public static final ICommandProcessor LIST_USERS = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String[] presentedUsers = {session.getPhone()};
			return performListCommand(session, carrier, phrases, presentedUsers);
		}
	};
	
	/** Called when the user is listing other users and wants to see more of them */
	public static final ICommandProcessor LIST_MORE_USERS = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String[] presentedUsers = desserializeStringArray(session.getParameterValue(ESessionParameters.PRESENTED_USERS), ";");
			return performListCommand(session, carrier, phrases, presentedUsers);
		}
	};
	
	/** Called when the user issues the "unsubscribe" command from an sms message */
	public static final ICommandProcessor UNSUBSCRIBE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			SUBSCRIPTION_ENGINE.unsubscribeUser(session.getPhone(), SUBSCRIPTION_CHANNEL_NAME);
			CommandMessageDto message = new CommandMessageDto(phrases.UNSUBSCRIBINGUnsubscriptionNotification(), EResponseMessageType.HELP);
			return getNewCommandAnswerDto(session, message);
		}
	};

}

class MatchPlayersInfo {

	public final String wordProvidingPlayerPhone;
	public final String wordGuessingPlayerPhone;
	public final String wordProvidingPlayerNick;
	public final String wordGuessingPlayerNick;

	public MatchPlayersInfo(String wordProvidingPlayerPhone, String wordGuessingPlayerPhone,
	                        String wordProvidingPlayerNick,  String wordGuessingPlayerNick) {
		this.wordProvidingPlayerPhone = wordProvidingPlayerPhone;
		this.wordGuessingPlayerPhone  = wordGuessingPlayerPhone;
		this.wordProvidingPlayerNick  = wordProvidingPlayerNick;
		this.wordGuessingPlayerNick   = wordGuessingPlayerNick;
	}
	
}