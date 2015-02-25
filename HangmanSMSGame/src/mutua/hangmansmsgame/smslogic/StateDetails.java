package mutua.hangmansmsgame.smslogic;

import java.lang.reflect.Field;

import mutua.hangmansmsgame.smslogic.NavigationMap.ECOMMANDS;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandTriggersDto;

/** <pre>
 * StateDetails.java
 * =================
 * (created by luiz, Dec 19, 2014)
 *
 * Defines the list of commands that belongs to each one of the states.
 * Also defines the textual pattern used to identify which command should
 * process which message.
 * 
 * A nice tool to get regular expressions from is the following applet:
 * 		http://www.fileformat.info/tool/regex.htm
 * 
 * Note: this class has some odd field setting to allow configuration reloading
 * while, at the same time, allowing 'NavigationMap' to keep the original array
 * instances set at enum initialization. A refactoring may be fine here to correct
 * this mess -- that is, the need for the 'setInstance' method should fall.
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class StateDetails {
	
	
	public enum ECommandPatterns {
		
		
		// commands used on several states
		//////////////////////////////////
		
		SHOW_FULL_HELP_MESSAGE            ("AJUDA", "HELP", "A"),
		SHOW_PROFILE                      ("PROFILE (.*)", "PROFILE"),
		DEFINE_NICK                       ("NICK (.*)", "N (.*)"),
		LIST_USERS                        ("LIST", "RANKING", "R", "L"),
		PROVOKE                           ("P ([^ ]+) (.*)"),
		START_INVITATION_PROCESS          ("CO?N?V?I?[DT]?[AEO]?R?", "INVITE"),
		INVITE_NICK_OR_PHONE              ("CO?N?V?I?[DT]?[AEO]?R? +(.*)", "INVITE +(.*)", "JO?G?[AO]?R? +(.*)",
		                                   "PARTIDA +(.*)", "NOVO +(.*)", "DE ?NOVO +(.*)", "OUTR[OA] +(.*)",
		                                   "FORCA +(.*)", "PLAY +(.*)", "HANGMAN +(.*)", "F +(.*)", "C +(.*)"),
		PLAY_WITH_RANDOM_USER_OR_BOT      ("JO?G?[AO]?R?", "PARTIDA", "NOVO", "DE ?NOVO", "OUTR[OA]",
		                                   "FORCA", "PLAY", "HANGMAN", "F"),
		UNSUBSCRIBE                       ("UNSUBSCRIBE", "S"),
		SHOW_FALLBACK_EXISTING_USERS_HELP (".*"),
		NO_ANSWER                         (".*"),
		
		
		// state specific commands
		//////////////////////////
		
		SHOW_WELCOME_MESSAGE             ("FORCA", "JOGO", "HANGMAN", "PLAY", "F"),
		SHOW_FALLBACK_NEW_USERS_HELP     (".*"),
		
		HOLD_OPPONENT_PHONE              ("(\\d+)"),
		HOLD_OPPONENT_NICK               ("(^\\d.+)"),
		
		HOLD_MATCH_WORD                  ("([^ ]+)"),
		
		ACCEPT_INVITATION                ("YES"),
		REFUSE_INVITATION                ("NO"),
		INVITATION_TIMEOUT               ((1000*60)*20),
		
		SUGGEST_LETTER_OR_WORD_FOR_HUMAN ("([A-Z])"),
		CANCEL_HUMAN_GAME                ("END"),
		
		SUGGEST_LETTER_OR_WORD_FOR_BOT   ("([A-Z])"),
		CANCEL_BOT_GAME                  ("END"),
		
		LIST_MORE_USERS                  ("MAIS", "MORE", "\\+"),
		
		;
		
		private String[] regularExpressions;
		private long     timeout;
		
		private ECommandPatterns(String... regularExpressions) {
			this.regularExpressions = regularExpressions;
			this.timeout            = -1;
		}
		
		private ECommandPatterns(long timeout) {
			this.regularExpressions = null;
			this.timeout            = timeout;
		}
		
		public void setRegularExpressions(String[] regularExpressions) {
			this.regularExpressions = regularExpressions;
		}
		
		public void setTimeout(long timeout) {
			this.timeout = timeout;
		}
		
		public String[] getRegularExpressions() {
			return regularExpressions;
		}
		
		public long getTimeout() {
			return timeout;
		}

		public CommandTriggersDto getCommandTriggers(ECOMMANDS command) {
			if (regularExpressions != null) {
				return new CommandTriggersDto(command, regularExpressions);
			} else {
				return new CommandTriggersDto(command, timeout);
			}
		}
		
	}
	
	
	// STATE DETAILS
	////////////////

	public static CommandTriggersDto[] NEW_USER;	
	public static CommandTriggersDto[] EXISTING_USER;	
	public static CommandTriggersDto[] ENTERING_OPPONENT_CONTACT_INFO;
	public static CommandTriggersDto[] ENTERING_MATCH_WORD;
	public static CommandTriggersDto[] ANSWERING_TO_INVITATION;
	public static CommandTriggersDto[] GUESSING_HUMAN_WORD;	
	public static CommandTriggersDto[] GUESSING_BOT_WORD;
	public static CommandTriggersDto[] LISTING_USERS;

	
	/** Method to give value to fields, to be called by the static block initializer and by the ConfigurationManager,
	 * whenever a reloaded configuration should be applyed */
	public static void defineFields() throws Throwable {

		// COMMON COMMAND PATTERNS
		//////////////////////////
		
		CommandTriggersDto SHOW_FULL_HELP_COMMAND_PATTERNS               = ECommandPatterns.SHOW_FULL_HELP_MESSAGE.getCommandTriggers(ECOMMANDS.SHOW_FULL_HELP_MESSAGE);
		CommandTriggersDto SHOW_PROFILE_COMMAND_PATTERNS                 = ECommandPatterns.SHOW_PROFILE.getCommandTriggers(ECOMMANDS.SHOW_PROFILE);
		CommandTriggersDto DEFINE_NICK_COMMAND_PATTERNS                  = ECommandPatterns.DEFINE_NICK.getCommandTriggers(ECOMMANDS.DEFINE_NICK);
		CommandTriggersDto LIST_USERS_COMMAND_PATTERNS                   = ECommandPatterns.LIST_USERS.getCommandTriggers(ECOMMANDS.LIST_USERS);
		CommandTriggersDto PROVOKE_COMMAND_PATTERNS                      = ECommandPatterns.PROVOKE.getCommandTriggers(ECOMMANDS.PROVOKE);
		CommandTriggersDto START_INVITATION_PROCESS_COMMAND_PATTERNS     = ECommandPatterns.START_INVITATION_PROCESS.getCommandTriggers(ECOMMANDS.START_INVITATION_PROCESS);
		CommandTriggersDto INVITE_NICK_OR_PHONE_COMMAND_PATTERNS         = ECommandPatterns.INVITE_NICK_OR_PHONE.getCommandTriggers(ECOMMANDS.INVITE_NICK_OR_PHONE);
		CommandTriggersDto PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS = ECommandPatterns.PLAY_WITH_RANDOM_USER_OR_BOT.getCommandTriggers(ECOMMANDS.PLAY_WITH_RANDOM_USER_OR_BOT);
		CommandTriggersDto UNSUBSCRIBE                                   = ECommandPatterns.UNSUBSCRIBE.getCommandTriggers(ECOMMANDS.UNSUBSCRIBE);
		CommandTriggersDto FALLBACK_HELP_COMMAND_PATTERNS                = ECommandPatterns.SHOW_FALLBACK_EXISTING_USERS_HELP.getCommandTriggers(ECOMMANDS.SHOW_FALLBACK_EXISTING_USERS_HELP);
		CommandTriggersDto NO_ANSWER_COMMAND_PATTERNS                    = ECommandPatterns.NO_ANSWER.getCommandTriggers(ECOMMANDS.NO_ANSWER);
		
		
		setInstance("NEW_USER", 
			ECommandPatterns.SHOW_WELCOME_MESSAGE.getCommandTriggers(ECOMMANDS.SHOW_WELCOME_MESSAGE),
			UNSUBSCRIBE,
			ECommandPatterns.SHOW_FALLBACK_NEW_USERS_HELP.getCommandTriggers(ECOMMANDS.SHOW_FALLBACK_NEW_USERS_HELP),
			NO_ANSWER_COMMAND_PATTERNS
		);
		
		setInstance("EXISTING_USER",
			SHOW_FULL_HELP_COMMAND_PATTERNS,
			SHOW_PROFILE_COMMAND_PATTERNS,
			DEFINE_NICK_COMMAND_PATTERNS,
			LIST_USERS_COMMAND_PATTERNS,
			PROVOKE_COMMAND_PATTERNS,
			START_INVITATION_PROCESS_COMMAND_PATTERNS,
			INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
			PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
			UNSUBSCRIBE,
			FALLBACK_HELP_COMMAND_PATTERNS,
			NO_ANSWER_COMMAND_PATTERNS
		);
		
		setInstance("ENTERING_OPPONENT_CONTACT_INFO",
			ECommandPatterns.HOLD_OPPONENT_PHONE.getCommandTriggers(ECOMMANDS.HOLD_OPPONENT_PHONE),
			ECommandPatterns.HOLD_OPPONENT_NICK.getCommandTriggers(ECOMMANDS.HOLD_OPPONENT_NICK),
			SHOW_FULL_HELP_COMMAND_PATTERNS,
			SHOW_PROFILE_COMMAND_PATTERNS,
			DEFINE_NICK_COMMAND_PATTERNS,
			LIST_USERS_COMMAND_PATTERNS,
			PROVOKE_COMMAND_PATTERNS,
			START_INVITATION_PROCESS_COMMAND_PATTERNS,
			INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
			PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
			UNSUBSCRIBE,
			FALLBACK_HELP_COMMAND_PATTERNS
		);

		setInstance("ENTERING_MATCH_WORD",
			SHOW_FULL_HELP_COMMAND_PATTERNS,
			SHOW_PROFILE_COMMAND_PATTERNS,
			DEFINE_NICK_COMMAND_PATTERNS,
			LIST_USERS_COMMAND_PATTERNS,
			PROVOKE_COMMAND_PATTERNS,
			START_INVITATION_PROCESS_COMMAND_PATTERNS,
			INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
			PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
			UNSUBSCRIBE,
			ECommandPatterns.HOLD_MATCH_WORD.getCommandTriggers(ECOMMANDS.HOLD_MATCH_WORD),
			FALLBACK_HELP_COMMAND_PATTERNS
		);

		setInstance("ANSWERING_TO_INVITATION",
			SHOW_FULL_HELP_COMMAND_PATTERNS,
			SHOW_PROFILE_COMMAND_PATTERNS,
			DEFINE_NICK_COMMAND_PATTERNS,
			LIST_USERS_COMMAND_PATTERNS,
			PROVOKE_COMMAND_PATTERNS,
			START_INVITATION_PROCESS_COMMAND_PATTERNS,
			INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
			PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
			UNSUBSCRIBE,
			ECommandPatterns.ACCEPT_INVITATION.getCommandTriggers(ECOMMANDS.ACCEPT_INVITATION),
			ECommandPatterns.REFUSE_INVITATION.getCommandTriggers(ECOMMANDS.REFUSE_INVITATION),
			ECommandPatterns.INVITATION_TIMEOUT.getCommandTriggers(ECOMMANDS.INVITATION_TIMEOUT),
			FALLBACK_HELP_COMMAND_PATTERNS
		);

		setInstance("GUESSING_HUMAN_WORD",
			ECommandPatterns.SUGGEST_LETTER_OR_WORD_FOR_HUMAN.getCommandTriggers(ECOMMANDS.SUGGEST_LETTER_OR_WORD_FOR_HUMAN),
			SHOW_FULL_HELP_COMMAND_PATTERNS,
			SHOW_PROFILE_COMMAND_PATTERNS,
			DEFINE_NICK_COMMAND_PATTERNS,
			LIST_USERS_COMMAND_PATTERNS,
			PROVOKE_COMMAND_PATTERNS,
			START_INVITATION_PROCESS_COMMAND_PATTERNS,
			INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
			PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
			UNSUBSCRIBE,
			ECommandPatterns.CANCEL_HUMAN_GAME.getCommandTriggers(ECOMMANDS.CANCEL_HUMAN_GAME),
			new CommandTriggersDto(ECOMMANDS.SUGGEST_LETTER_OR_WORD_FOR_HUMAN, new String[] {"(.*)"})	// fallback
		);
		
		setInstance("GUESSING_BOT_WORD",
			ECommandPatterns.SUGGEST_LETTER_OR_WORD_FOR_BOT.getCommandTriggers(ECOMMANDS.SUGGEST_LETTER_OR_WORD_FOR_BOT),
			SHOW_FULL_HELP_COMMAND_PATTERNS,
			SHOW_PROFILE_COMMAND_PATTERNS,
			DEFINE_NICK_COMMAND_PATTERNS,
			LIST_USERS_COMMAND_PATTERNS,
			PROVOKE_COMMAND_PATTERNS,
			START_INVITATION_PROCESS_COMMAND_PATTERNS,
			INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
			PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
			UNSUBSCRIBE,
			ECommandPatterns.CANCEL_BOT_GAME.getCommandTriggers(ECOMMANDS.CANCEL_BOT_GAME),
			new CommandTriggersDto(ECOMMANDS.SUGGEST_LETTER_OR_WORD_FOR_BOT, new String[] {"(.*)"})		// fallback
		);

		setInstance("LISTING_USERS",
			ECommandPatterns.LIST_MORE_USERS.getCommandTriggers(ECOMMANDS.LIST_MORE_USERS),
			SHOW_FULL_HELP_COMMAND_PATTERNS,
			SHOW_PROFILE_COMMAND_PATTERNS,
			DEFINE_NICK_COMMAND_PATTERNS,
			LIST_USERS_COMMAND_PATTERNS,
			PROVOKE_COMMAND_PATTERNS,
			START_INVITATION_PROCESS_COMMAND_PATTERNS,
			INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
			PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
			UNSUBSCRIBE,
			FALLBACK_HELP_COMMAND_PATTERNS
		);
	}
	
	/** Define local array fields, creating the array just once -- needed for configuration reloading
	 * while also allowing consumers who got the first instance to follow the new element values */
	private static void setInstance(String fieldName, CommandTriggersDto... triggers) throws Throwable {
		Field field = StateDetails.class.getField(fieldName);
		CommandTriggersDto[] fieldArray = (CommandTriggersDto[])field.get(null);
		if (fieldArray == null) {	// first time setting the values -- means that we are on the class initialization. Ok to use any instance...			
			field.set(null, triggers);
		} else {					// subsequent setting of values -- we should use the same array instance
			for (int i=0; i<triggers.length; i++) {
				fieldArray[i] = triggers[i];
			}
		}
	}

	static {
		try {
			defineFields();
		} catch (Throwable t) {
			System.out.println("ERROR -- could not run 'StateDetails.defineFields'. Class loading aborted!");
			t.printStackTrace();
		}
	}
}
