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
import java.util.Iterator;
import java.util.Random;

import mutua.hangmansmsgame.config.Configuration;
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
import mutua.icc.configuration.annotations.ConfigurableElement;
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
	
	public static void registerUserNickname(String phone, String nickname) throws SQLException {
		userDB.assignSequencedNicknameToPhone(phone, nickname);
	}

	/** formalizes the default nickname registration rule */
	public static void assureUserHasANickname(String phone) throws SQLException {
		if (!userDB.isUserSubscribed(phone)) {
			registerUserNickname(phone, DEFAULT_NICKNAME_PREFIX + phone.substring(Math.max(phone.length()-4, 0)));
		}
	}
	
	/** This is the point we may call Celltick APIs for registration */
	public static boolean assureUserIsRegistered(String phone) throws SQLException {

		if (userDB.isUserSubscribed(phone)) {
			return true;
		}
		
		ESubscriptionOperationStatus subscriptionStatus = SUBSCRIPTION_ENGINE.subscribeUser(phone, SUBSCRIPTION_CHANNEL_NAME);
		
		if ((subscriptionStatus == ESubscriptionOperationStatus.OK) ||
			(subscriptionStatus == ESubscriptionOperationStatus.ALREADY_SUBSCRIBED)) {
			if (!userDB.isUserOnRecord(phone)) {
				log.reportEvent(DIE_DEBUG, DIP_MSG, "Hangman: registering user "+phone+" succeeded");
				assureUserHasANickname(phone);
			}
			userDB.setSubscribed(phone, true);
			return true;
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
			if (unpresentedPhonesIndex == unpresentedPhones.length) {
				break NEXT_PLAYER;
			}
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
		return new CommandAnswerDto(commandMessages, null);
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


	@ConfigurableElement("Generates a 'CommandAnswerDto' with 0 messages to be sent. Receives no parameters. Activated when an unrecognized message is sent from a 'NEW_USER'")
	public static final ICommandProcessor NO_ANSWER = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) {
			return null;
		}
	};

	@ConfigurableElement("Activated for 'NEW_USER's, when we receive words like HANGMAN or FORCA")
	public static final ICommandProcessor SHOW_WELCOME_MESSAGE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			
			CommandMessageDto commandResponse;
			
			if (assureUserIsRegistered(session.getPhone())) {
				commandResponse = new CommandMessageDto(phrases.INFOWelcome(),
				                                        EResponseMessageType.HELP);
				return getNewCommandAnswerDto(session, commandResponse, ESTATES.EXISTING_USER);
			} else {
				commandResponse = new CommandMessageDto(phrases.INFOCouldNotRegister(), EResponseMessageType.HELP);
				return getNewCommandAnswerDto(session, commandResponse);
			}

		}
	};
	
	@ConfigurableElement("Command to be executed if an unrecognized message is received while in 'NEW_USER' state")
	public static final ICommandProcessor SHOW_FALLBACK_NEW_USERS_HELP = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			CommandMessageDto commandResponses = new CommandMessageDto(phrases.INFOFallbackNewUsersHelp(), EResponseMessageType.HELP);
			return getNewCommandAnswerDto(session, commandResponses);
		}
	};
	
	@ConfigurableElement("Command to be executed if an unrecognized message is received while in 'EXISTING_USER' state")
	public static final ICommandProcessor SHOW_FALLBACK_EXISTING_USERS_HELP = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			CommandMessageDto commandResponses = new CommandMessageDto(phrases.INFOFallbackExistingUsersHelp(), EResponseMessageType.HELP);
			return getNewCommandAnswerDto(session, commandResponses);
		}
	};
	
	@ConfigurableElement("The help command. No args.")
	public static final ICommandProcessor SHOW_FULL_HELP1_MESSAGE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String[] smsTexts = phrases.INFOFullHelp1();
			CommandMessageDto[] commandResponses = new CommandMessageDto[smsTexts.length];
			for (int i=0; i<commandResponses.length; i++) {
				commandResponses[i] = new CommandMessageDto(smsTexts[i], EResponseMessageType.HELP);
			}
			return getNewCommandAnswerDto(session, commandResponses, ESTATES.SHOWING_HELP);
		}
	};

	@ConfigurableElement("The continuation help command. No args.")
	public static final ICommandProcessor SHOW_FULL_HELP2_MESSAGE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String[] smsTexts = phrases.INFOFullHelp2();
			CommandMessageDto[] commandResponses = new CommandMessageDto[smsTexts.length];
			for (int i=0; i<commandResponses.length; i++) {
				commandResponses[i] = new CommandMessageDto(smsTexts[i], EResponseMessageType.HELP);
			}
			return getNewCommandAnswerDto(session, commandResponses, ESTATES.EXISTING_USER);
		}
	};

	@ConfigurableElement("Called when the user just wants to play -- with any opponent. No args")
	public static final ICommandProcessor PLAY_WITH_RANDOM_USER_OR_BOT = new ICommandProcessor() {
		private Random rnd = new Random(System.currentTimeMillis());
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String wordProvidingPlayerNick  = Configuration.BOT_USERS[rnd.nextInt(Configuration.BOT_USERS.length)];
			
			// start a new Match
			assureUserIsRegistered(session.getPhone());
			int botWordIndex = userDB.getAndIncrementNextBotWord(session.getPhone());
			botWordIndex = botWordIndex % Configuration.BOT_WORDS.length;
			HangmanGame game = new HangmanGame(Configuration.BOT_WORDS[botWordIndex], 6);
			String serializedGameState = game.serializeGameState();
			
			CommandMessageDto wordGuessingPlayerMessage = new CommandMessageDto(phrases.PLAYINGWordGuessingPlayerStart(game.getGuessedWordSoFar(), game.getAttemptedLettersSoFar()),
                                                                                EResponseMessageType.PLAYING);

			sessionDB.setSession(session.getNewSessionDto(ESTATES.GUESSING_BOT_WORD.name(),
			                                              ESessionParameters.MATCH_BOT_NAME, wordProvidingPlayerNick,
			                                              ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE, serializedGameState));
			return getNewCommandAnswerDto(session, wordGuessingPlayerMessage);
		}
	};

	@ConfigurableElement("Starts the invitation process with an argument. parameter 1: the phone number or nick name of the user")
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

	@ConfigurableElement("Starts the invitation process without any arguments")
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

	@ConfigurableElement("Command to take note of the opponent phone number when inviting someone. Parameter 1: a phone number")
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
	
	@ConfigurableElement("Command to take note of the opponent nick when inviting someone. Parameter 1: a nick name")
	public static final ICommandProcessor HOLD_OPPONENT_NICK = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String opponentNickname    = parameters[0];
			String opponentPhoneNumber = userDB.getUserPhoneNumber(opponentNickname);
			if ((opponentPhoneNumber == null) || (session.getPhone().equals(opponentPhoneNumber))) {
				return getNickNotFoundMessage(session, phrases, opponentNickname);
			}
			CommandMessageDto commandResponse = new CommandMessageDto(phrases.INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation(opponentNickname),
			                                                          EResponseMessageType.ACQUIRE_MATCH_INFORMATION);
			return getNewCommandAnswerDto(session, commandResponse, ESTATES.ENTERING_MATCH_WORD_TO_PLAY,
			                              ESessionParameters.OPPONENT_PHONE_NUMBER, opponentPhoneNumber);
		}
	};

	@ConfigurableElement("Command to take note of the word to play, when inviting someone. Parameter 1: a word")
	public static final ICommandProcessor HOLD_MATCH_WORD = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String opponentPlayerPhoneNumber = session.getParameterValue(ESessionParameters.OPPONENT_PHONE_NUMBER);
			String invitingPlayerNickName    = userDB.getUserNickname(session.getPhone());
			String wordToPlay                = parameters[0];
			
			assureUserHasANickname(opponentPlayerPhoneNumber);			
			String opponentPlayerNickName = userDB.getUserNickname(opponentPlayerPhoneNumber);

			HangmanGame game = new HangmanGame(wordToPlay, 6);
			
			if ((!wordToPlay.matches("[A-Za-z]+")) || (game.getGameState() == EHangmanGameStates.WON)) {
				CommandMessageDto commandResponse = new CommandMessageDto(phrases.INVITINGNotAGoodWord(game.getWord()), EResponseMessageType.HELP);
				return getNewCommandAnswerDto(session, commandResponse);
			}
			
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

	@ConfigurableElement("Command for when the word guessing player answers YES to the invitation message he/she received to attend to a hangman match")
	public static final ICommandProcessor ACCEPT_INVITATION = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String serializedGameState      = session.getParameterValue(ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE);
			
			MatchPlayersInfo matchPlayersInfo = getMatchPlayersInfo(session);

			// if the invited player could not be registered
			if (!assureUserIsRegistered(session.getPhone())) {
				CommandMessageDto wordProvidingPlayerMessage = new CommandMessageDto(
						matchPlayersInfo.wordProvidingPlayerPhone,
						phrases.INVITINGInvitationRefusalNotificationForInvitingPlayer(matchPlayersInfo.wordGuessingPlayerNick),
				        EResponseMessageType.HELP);
				CommandMessageDto wordGuessingPlayerMessage = new CommandMessageDto(
						phrases.INFOCouldNotRegister(),
	                    EResponseMessageType.HELP);

				return getNewCommandAnswerDto(session, new CommandMessageDto[] {wordProvidingPlayerMessage, wordGuessingPlayerMessage},
				                              ESTATES.NEW_USER);
			}
			
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

	@ConfigurableElement("Command for when the word guessing player answers NO to the invitation message he/she received to attend to a hangman match")
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


	@ConfigurableElement("Command executed when the user is attempting to guess a word provided by a human. Parameter 1: the suggested letter or word")
	public static final ICommandProcessor SUGGEST_LETTER_OR_WORD_FOR_HUMAN = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			int    matchId                  = Integer.parseInt(session.getParameterValue(ESessionParameters.MATCH_ID));
			String suggestedLetter          = parameters[0];
			String serializedGameState      = session.getParameterValue(ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE);
			
			MatchDto matchData = matchDB.retrieveMatch(matchId);
			MatchPlayersInfo matchPlayersInfo = getMatchPlayersInfo(matchData);
			HangmanGame game  = new HangmanGame(serializedGameState);
			char[] suggestLetters = suggestedLetter.toCharArray();
			for (char c : suggestLetters) {
				if (game.getGameState() != EHangmanGameStates.PLAYING) {
					break;
				}
				game.suggestLetter(c);
			}
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
	
	@ConfigurableElement("Command called when the user is attempting to guess a word provided by a bot. Parameter 1: the letter or word")
	public static final ICommandProcessor SUGGEST_LETTER_OR_WORD_FOR_BOT = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String suggestedLetter          = parameters[0];
			String serializedGameState      = session.getParameterValue(ESessionParameters.HANGMAN_SERIALIZED_GAME_STATE);
			String wordProvidingPlayerNick  = session.getParameterValue(ESessionParameters.MATCH_BOT_NAME);

			HangmanGame game = new HangmanGame(serializedGameState);
			char[] suggestLetters = suggestedLetter.toCharArray();
			for (char c : suggestLetters) {
				if (game.getGameState() != EHangmanGameStates.PLAYING) {
					break;
				}
				game.suggestLetter(c);
			}
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

	@ConfigurableElement("Responds to the 'END' command issued by the word guessing player when playing with a human")
	public static final ICommandProcessor CANCEL_HUMAN_GAME = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			return getNewCommandAnswerDto(session, (CommandMessageDto[])null, ESTATES.EXISTING_USER);
		}
	};

	@ConfigurableElement("Responds to the 'END' command issued by the word guessing player when playing with a bot")
	public static final ICommandProcessor CANCEL_BOT_GAME = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String botNick    = session.getParameterValue(ESessionParameters.MATCH_BOT_NAME);
			
			return getNewCommandAnswerDto(session,
				new CommandMessageDto(phrases.PLAYINGMatchGiveupNotificationForWordGuessingPlayer(botNick), EResponseMessageType.HELP),
				ESTATES.EXISTING_USER);
		}
	};

	@ConfigurableElement("Called when the user wants to send a chat message to another player. Parameters: 1: the target nickname, 2: the message")
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
	
	@ConfigurableElement("Command called when the user wants to see the profile of another user -- receives the optional parameter: user nickname")
	public static final ICommandProcessor SHOW_PROFILE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String desiredUserNickname = null;
			String desiredUserPhone    = null;
			// responds to "PROFILE [NICK]" command
			if (parameters.length == 1) {
				desiredUserNickname = userDB.getCorrectlyCasedNickname(parameters[0]);
				if (desiredUserNickname != null) {
					desiredUserPhone    = userDB.getUserPhoneNumber(desiredUserNickname);
				}
			} else {
				// responds to "PROFILE" command without arguments. Try to get the opponent nick from the session variables
				String matchBotName;
				String sMatchId;
				if ((matchBotName = session.getParameterValue(ESessionParameters.MATCH_BOT_NAME)) != null) {
					return null;	// do nothing for bots, by now
				} else if ((sMatchId = session.getParameterValue(ESessionParameters.MATCH_ID)) != null) {
					MatchDto match = matchDB.retrieveMatch(Integer.parseInt(sMatchId));
					desiredUserPhone = match.getWordProvidingPlayerPhone();
				} else if ((desiredUserPhone = session.getParameterValue(ESessionParameters.OPPONENT_PHONE_NUMBER)) != null) {
					// just setting opponentPhoneNumber is ok by now
				} else {
					return null;
				}
				desiredUserNickname = userDB.getUserNickname(desiredUserPhone);
			}
			// return the results
			CommandMessageDto message;
			if ((desiredUserNickname != null) && (desiredUserPhone != null)) { 
				message = new CommandMessageDto(phrases.PROFILEView(desiredUserNickname, getBrazillianPhoneState(desiredUserPhone), 0), EResponseMessageType.PROFILE);
			} else {
				message = new CommandMessageDto(phrases.PROVOKINGNickNotFound(parameters[0]), EResponseMessageType.PROFILE);
			}
			return getNewCommandAnswerDto(session, message);
		}
	};
	
	@ConfigurableElement("Command called when the user wants to reset his/her profile nickname. Parameter 1: the new nick")
	public static final ICommandProcessor DEFINE_NICK = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String newNickname = parameters[0];
			assureUserIsRegistered(session.getPhone());
			registerUserNickname(session.getPhone(), newNickname);
			String registeredNickname = userDB.getUserNickname(session.getPhone());
			CommandMessageDto message = new CommandMessageDto(phrases.PROFILENickRegisteredNotification(registeredNickname), EResponseMessageType.PROFILE);
			return getNewCommandAnswerDto(session, message);
		}
	};
	
	
	private static CommandAnswerDto performListCommand(SessionDto session,	ESMSInParserCarrier carrier, IPhraseology phrases, String[] presentedUsers) throws SQLException {
		String[][] playersInfo = CommandDetails.getPlayersInfoToPresentOnTheListCommandRespectingTheMaximumNumberOfCharacters(phrases, carrier.getMaxMTChars()*3, presentedUsers);
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


	@ConfigurableElement("Command called when the user wants to see a list of online users")
	public static final ICommandProcessor LIST_USERS = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String[] presentedUsers = {session.getPhone()};
			return performListCommand(session, carrier, phrases, presentedUsers);
		}
	};
	
	@ConfigurableElement("Command called when the user is listing online users and wants to see more of them")
	public static final ICommandProcessor LIST_MORE_USERS = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			String[] presentedUsers = desserializeStringArray(session.getParameterValue(ESessionParameters.PRESENTED_USERS), ";");
			return performListCommand(session, carrier, phrases, presentedUsers);
		}
	};
	
	@ConfigurableElement("Command called when the user wants to have his subscription cancelled from the game")
	public static final ICommandProcessor UNSUBSCRIBE = new ICommandProcessor() {
		@Override
		public CommandAnswerDto processCommand(SessionDto session, ESMSInParserCarrier carrier, String[] parameters, IPhraseology phrases) throws SQLException {
			SUBSCRIPTION_ENGINE.unsubscribeUser(session.getPhone(), SUBSCRIPTION_CHANNEL_NAME);
			userDB.setSubscribed(session.getPhone(), false);
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