package mutua.hangmansmsgame.config;

import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents.IE_CONFIGURING_NUMBER_PROPERTY;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents.IE_CONFIGURING_STRING_ARRAY_PROPERTY;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationEvents.IE_CONFIGURING_STRING_PROPERTY;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationProperties.IP_CONFIGURATION_FIELD_NAME;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationProperties.IP_CONFIGURATION_NUMBER_FIELD_VALUE;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationProperties.IP_CONFIGURATION_STRING_ARRAY_FIELD_VALUE;
import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationProperties.IP_CONFIGURATION_STRING_FIELD_VALUE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

import mutua.hangmansmsgame.dal.DALFactory;
import mutua.hangmansmsgame.dal.DALFactory.EDataAccessLayers;
import mutua.hangmansmsgame.dal.postgresql.HangmanSMSGamePostgreSQLAdapters;
import mutua.icc.instrumentation.Instrumentation;
import mutua.subscriptionengine.SubscriptionEngine;
import adapters.HTTPClientAdapter;
import adapters.SQLServerAdapter;

/** <pre>
 * Configuration.java
 * ==================
 * (created by luiz, Jan 21, 2015)
 *
 * Defines common configuration variables for the framework
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class Configuration {
	
	/** to be defined by the request receivers (servlet, console, tests, icc app, ...) */
	public static Instrumentation<?, ?> log;
	
	/** empty string means console. All the rest, the path */
	public static String LOG_FILE_PATH = "";
	
	/** also to be defined by the request receivers (servlet, console, tests, icc app, ...) */
	public static SubscriptionEngine SUBSCRIPTION_ENGINE;

	// Integration services
	///////////////////////
	
	public static String SUBSCRIBE_SERVICE_URL               = "http://localhost:8082/celltick/wapAPI?action=subpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
	public static String UNSUBSCRIBE_SERVICE_URL             = "http://localhost:8082/celltick/wapAPI?action=unsubpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
	public static String SUBSCRIPTION_CHANNEL_NAME           = "HangMan";
	public static String MT_SERVICE_URL                      = "http://localhost:15001/cgi-bin/sendsms";
	public static int    MT_SERVICE_NUMBER_OF_RETRY_ATTEMPTS = 5;
	public static long   MT_SERVICE_DELAY_BETWEEN_ATTEMPTS   = 5000;

	
	// HTTPClientAdapter
	////////////////////
	
	public static int HTTP_CONNECTION_TIMEOUT_MILLIS = 30000;
	public static int HTTP_READ_TIMEOUT_MILLIS       = 30000;
	
	
	// JDBCAdapter
	//////////////
	
	public static String  DATA_ACCESS_LAYER                 = "RAM";
	public static Boolean POSTGRES_DEBUG_QUERIES            = true;
	public static String  POSTGRESQL_CONNECTION_HOSTNAME      = "zertyz.heliohost.org";
	public static int     POSTGRESQL_CONNECTION_PORT          = 5432;
	public static String  POSTGRESQL_CONNECTION_DATABASE_NAME = "zertyz_spikes";
	public static String  POSTGRESQL_CONNECTION_USER          = "zertyz_user";
	public static String  POSTGRESQL_CONNECTION_PASSWORD      = "spikes";
	public static String  POSTGRES_CONNECTION_PROPERTIES    = "";

	
	// SMS Framework parameters
	///////////////////////////
	
	public static String APPID                          = "HANGMAN";
	public static String SHORT_CODE                     = "9714";
	public static int    QUEUE_CAPACITY                 = 1000;
	public static int    QUEUE_NUMBER_OF_WORKER_THREADS = 10;

	// SMS Application parameters
	/////////////////////////////
	
	public static String DEFAULT_NICKNAME_PREFIX    = "Guest";
	public static long   INVITATION_TIMEOUT_MILLIS  = (1000*60)*20;
	
	public static String[] BOT_WORDS = {
		"CHIMPANZEE", "AGREGATE", "TWEEZERS",
	};


	// phrasing
	public static String[] shortHelp                                                      = {"(J) Play online; (C) Invite a friend or user; (R)anking; (A)Help"};
	public static String[] gallowsArt                                                     = {"+-+\n| {{head}}\n|{{leftArm}}{{chest}}{{rightArm}}\n|{{leftLeg}} {{rightLeg}}\n|\n====\n"};
	public static String[] winningArt                                                     = {"\\0/\n |\n/ \\\n"};
	public static String[] losingArt                                                      = {"+-+\n| x\n|/|\\\n|/ \\\n====\n"};
	public static String[] playersList                                                    = {"{{nick}} ({{state}}/{{numberOfLuckyNumbers}})"};
	public static String[] INFOWelcome                                                    = {"Welcome to the HANGMAN game. Join and compete for prizes. Send HELP for free to {{shortCode}} to know the rules."};
	public static String[] INFOFullHelp                                                   = {"1/3: You can play the HANGMAN game in 2 ways: guessing someone's word or inviting someone to play with your word",
	                                                                                         "2/3: You'll get 1 lucky number each word you guess. Whenever you invite a friend or user to play, you win another lucky number",
	                                                                                         "3/3: Every week, 1 lucky number is selected to win the prize. Send an option to {{shortCode}}: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help"};
	public static String[] INFOWelcomeMenu                                                = {"Pick an option. Send to {{shortCode}}: {{shortHelp}}"};
	public static String[] INFOCouldNotRegister                                           = {"HANGMAN: You could not be registered at this time. Please try again later."};
	public static String[] PROFILEView                                                    = {"HANGMAN: {{nick}}: Subscribed, {{state}}, {{numberOfLuckyNumbers}} lucky numbers. Send SIGNUP to provoke for free or INVITE {{nick}} for a match."};
	public static String[] PROFILEFullfillingAskNick                                      = {"HANGMAN: To play with a friend, u need first to sign your name. Now send your name (8 letters or numbers max.) to {{shortCode}}"};
	public static String[] PROFILENickRegisteredNotification                              = {"HANGMAN: Name registered: {{newNickname}}. Send LIST to {{shortCode}} to see online players. NICK [NEW NICK] to change your name."};
	public static String[] PLAYINGWordProvidingPlayerStart                                = {"Game started with {{wordGuessingPlayerNick}}.\n{{gallowsArt}}Send P {{wordGuessingPlayerNick}} MSG to give him/her clues"};
	public static String[] PLAYINGWordGuessingPlayerStart                                 = {"{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"};
	public static String[] PLAYINGWordGuessingPlayerStatus                                = {"{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend a letter, the complete word or END to cancel the game"};
	public static String[] PLAYINGWordProvidingPlayerStatus                               = {"{{nick}} guessed letter {{guessedLetter}}\n{{gallowsArt}}Word: {{guessedWordSoFar}}\nUsed: {{usedLetters}}\nSend P {{nick}} MSG to provoke him/her"};
	public static String[] PLAYINGWinningMessageForWordGuessingPlayer                     = {"{{winningArt}}{{word}}! You got it! Here is your lucky number: {{luckyNumber}}. Send: J to play or A for help"};
	public static String[] PLAYINGWinningMessageForWordProvidingPlayer                    = {"{{wordGuessingPlayerNick}} guessed your word! P {{wordGuessingPlayerNick}} MSG to provoke him/her or INVITE {{wordGuessingPlayerNick}} for a new match"};
	public static String[] PLAYINGLosingMessageForWordGuessingPlayer                      = {"{{losingArt}}The word was {{word}}. Now challenge {{wordProvidingPlayerNick}}: send INVITE {{wordProvidingPlayerNick}} to {{shortCode}}"};
	public static String[] PLAYINGLosingMessageForWordProvidingPlayer                     = {"Good one! {{wordGuessingPlayerNick}} wasn't able to guessed your word! P {{wordGuessingPlayerNick}} MSG to provoke him/her or INVITE {{wordGuessingPlayerNick}} for a new match"};
	public static String[] PLAYINGMatchGiveupNotificationForWordProvidingPlayer           = {"{{wordGuessingPlayerNick}} cancelled the match. To find other users to play with, sent LIST to {{shortCode}}"};
	public static String[] PLAYINGMatchGiveupNotificationForWordGuessingPlayer            = {"Your match with {{wordProvidingPlayerNick}} has been canceled. Send P {{wordProvidingPlayerNick}} MSG to talk to him/her or LIST to play with someone else"};
	public static String[] INVITINGAskOpponentNickOrPhone                                 = {"HANGMAN: Name registered: {{invitingPlayerNickname}}. Send your friend's phone to {{shortCode}} or LIST to see online players. NICK [NEW NICK] to change your name."};
	public static String[] INVITINGAskForAWordToStartAMatchBasedOnOpponentNickInvitation  = {"HANGMAN: Inviting {{opponentNickname}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"};
	public static String[] INVITINGAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation = {"HANGMAN: Your friend's phone: {{opponentPhoneNumber}}. Think of a word without special digits and send it now to {{shortCode}}. After the invitation, you'll get a lucky number"};
	public static String[] INVITINGInvitationNotificationForInvitingPlayer                = {"{{invitedPlayerNickname}} was invited to play with you. while you wait, you can provoke {{invitedPlayerNickname}} by sending a message to {{shortCode}} (0.31+tax) or send SIGNUP to provoke for free how many times you want"};
	public static String[] INVITINGTimeoutNotificationForInvitingPlayer                   = {"{{invitedPlayerNickname}} is taking too long to answer. However, a new player, {{suggestedNewPlayersNickname}}, is available. Play with {{suggestedNewPlayersNickname}}? Send YES to {{shortCode}}"};
	public static String[] INVITINGInvitationNotificationForInvitedPlayer                 = {"HANGMAN: {{invitingPlayerNickname}} is inviting you for a hangman match. Do you accept? Send YES to {{shortCode}} or PROFILE to see {{invitingPlayerNickname}} information"};
	public static String[] INVITINGInvitationRefusalNotificationForInvitingPlayer         = {"{{invitedPlayerNickname}} refused your invitation to play. Send LIST to 9714 and pick someone else"};
	public static String[] INVITINGInvitationRefusalNotificationForInvitedPlayer          = {"The invitation to play the Hangman Game made by {{invitingPlayerNickname}} was refused. Send LIST to {{shortCode}} to see online users"};
	public static String[] LISTINGShowPlayers                                             = {"{{playersList}}. To play, send INVITE [NICK] to {{shortCode}}; MORE for more players or PROFILE [NICK]"};
	public static String[] LISTINGNoMorePlayers                                           = {"There is no more online players to show. Send P [NICK] [MSG] to provoke or INVITE [PHONE] to invite a friend of yours to play the Hangman Game."};
	public static String[] PROVOKINGDeliveryNotification                                  = {"Your message was sent to {{destinationNick}}. Wait for the answer or provoke other players sending P [NICK] [MSG] to {{shortCode}}. Send SIGNUP to provoke for free."};
	public static String[] PROVOKINGSendMessage                                           = {"{{sourceNick}}: {{message}} - Answer by sending P {{sourceNick}} [MSG] to {{shortCode}}"};
	public static String[] PROVOKINGNickNotFound                                          = {"No player with nickname '{{nickname}}' was found. Maybe he/she changed it? Send LIST to {{shortCode}} to see online players"};
	public static String[] UNSUBSCRIBINGUnsubscriptionNotification                        = {"You are now unsubscribed from the HANGMAN GAME and will no longer receive invitations to play nor lucky numbers. To join again, send HANGMAN to {{shortCode}}"};

	
	///////
	
	static {
		applyConfiguration();
	}
	
	private static void applyConfiguration() {
		HTTPClientAdapter.CONNECTION_TIMEOUT = HTTP_CONNECTION_TIMEOUT_MILLIS;
		HTTPClientAdapter.READ_TIMEOUT       = HTTP_READ_TIMEOUT_MILLIS;

		SQLServerAdapter.DEBUG_QUERIES = POSTGRES_DEBUG_QUERIES;
		DALFactory.DEFAULT_DAL = EDataAccessLayers.valueOf(DATA_ACCESS_LAYER);
		HangmanSMSGamePostgreSQLAdapters.log = log;
		HangmanSMSGamePostgreSQLAdapters.HOSTNAME = POSTGRESQL_CONNECTION_HOSTNAME;
		HangmanSMSGamePostgreSQLAdapters.PORT     = POSTGRESQL_CONNECTION_PORT;
		HangmanSMSGamePostgreSQLAdapters.DATABASE = POSTGRESQL_CONNECTION_DATABASE_NAME;
		HangmanSMSGamePostgreSQLAdapters.USER     = POSTGRESQL_CONNECTION_USER;
		HangmanSMSGamePostgreSQLAdapters.PASSWORD = POSTGRESQL_CONNECTION_PASSWORD;
	}

	
	
	public static String serializeStaticFields(Class<?> cc) throws IllegalArgumentException, IllegalAccessException {
		StringBuffer buffer = new StringBuffer();
		Field[] fields = cc.getDeclaredFields();
		for (Field f : fields) {
			Class<?> fType = f.getType();
			if (fType == String.class) {
				String s = (String)f.get(null);
				s = s.replaceAll("\\\\", "\\\\\\\\").
				      replaceAll("\n",   "\\\\n").
				      replaceAll("\r",   "\\\\t").
				      replaceAll("\t",   "\\\\t");
				buffer.append(f.getName()).append("=").append(s).append("\n");
			} else if (fType == int.class) {
				int i = (Integer)f.get(null);
				buffer.append(f.getName()).append("=").append(i).append("\n");
			} else if (fType == long.class) {
				long l = (Long)f.get(null);
				buffer.append(f.getName()).append("=").append(l).append("\n");
			} else if (fType == String[].class) {
				String[] ss = (String[])f.get(null);
				for (String s : ss) {
					s = s.replaceAll("\\\\", "\\\\\\\\").
					      replaceAll("\n",   "\\\\n").
					      replaceAll("\r",   "\\\\t").
					      replaceAll("\t",   "\\\\t");
					buffer.append(f.getName()).append("+=").append(s).append("\n");
				}
			}
		}
		return buffer.toString();
	}

	public static void desserializeStaticFields(Class<?> cc, String serializedFields) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = cc.getDeclaredFields();
		for (Field f : fields) try {
			Class<?> fType = f.getType();
			String fName = f.getName();
			if (fType == String.class) {
				String s = serializedFields.replaceAll("(?s).*?\n?"+fName+"=([^\n]*).*", "$1");
				s = s.replaceAll("\\\\n",   "\n").
				      replaceAll("\\\\r",   "\t").
				      replaceAll("\\\\t",   "\t").
				      replaceAll("\\\\\\\\", "\\\\");
				f.set(null, s);
				log.reportEvent(IE_CONFIGURING_STRING_PROPERTY, IP_CONFIGURATION_FIELD_NAME, fName, IP_CONFIGURATION_STRING_FIELD_VALUE, s);
			} else if (fType == long.class) {
				String s = serializedFields.replaceAll("(?s).*?\n?"+fName+"=([^\n]*).*", "$1");
				long l = Long.parseLong(s);
				f.set(null, l);
				log.reportEvent(IE_CONFIGURING_NUMBER_PROPERTY, IP_CONFIGURATION_FIELD_NAME, fName, IP_CONFIGURATION_NUMBER_FIELD_VALUE, l);
			} else if (fType == int.class) {
				String s = serializedFields.replaceAll("(?s).*?\n?"+fName+"=([^\n]*).*", "$1");
				int i = Integer.parseInt(s);
				f.set(null, i);
				log.reportEvent(IE_CONFIGURING_NUMBER_PROPERTY, IP_CONFIGURATION_FIELD_NAME, fName, IP_CONFIGURATION_NUMBER_FIELD_VALUE, i);
			} else if (fType == String[].class) {
				String a = serializedFields.replaceAll("(?s)(\n?)"+fName+"\\+=([^\n]*)", "$1;@<!$2:#_%").
				                            replaceAll("(?s).*?(;@<!.*:#_%).*", "$1").
				                            replaceAll("(?s);@<!", "").
				                            replaceAll("(?s):#_%", "");
				String[] ss = a.split("\n");
				for (int i=0; i<ss.length; i++) {
					String s = ss[i].replaceAll("\\\\n",   "\n").
					                 replaceAll("\\\\r",   "\t").
					                 replaceAll("\\\\t",   "\t").
					                 replaceAll("\\\\\\\\", "\\\\");
					ss[i] = s;
				}
				f.set(null, ss);
				log.reportEvent(IE_CONFIGURING_STRING_ARRAY_PROPERTY, IP_CONFIGURATION_FIELD_NAME, fName, IP_CONFIGURATION_STRING_ARRAY_FIELD_VALUE, ss);
			}
		} catch (NumberFormatException e) {
			log.reportThrowable(e, "Error while attempt to configure field '"+f.getName()+"'");
		}
	}

	public static void loadFromFile(String filePath) throws IOException, IllegalArgumentException, IllegalAccessException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		StringBuffer fileContents = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			fileContents.append(line).append('\n');
		}
		br.close();
		desserializeStaticFields(Configuration.class, fileContents.toString());
		applyConfiguration();
	}

}
