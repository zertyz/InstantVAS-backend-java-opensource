package mutua.hangmansmsgame.smslogic;

import mutua.hangmansmsgame.config.Configuration;
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
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class StateDetails {
	
	
	// COMMON COMMAND PATTERNS
	//////////////////////////
	
	private static final CommandTriggersDto SHOW_FULL_HELP_COMMAND_PATTERNS =
		new CommandTriggersDto(ECOMMANDS.SHOW_FULL_HELP_MESSAGE, new String[] {"AJUDA", "HELP"});
	private static final CommandTriggersDto SHOW_PROFILE_COMMAND_PATTERNS =
		new CommandTriggersDto(ECOMMANDS.SHOW_PROFILE, new String[] {"PROFILE (.*)"});
	private static final CommandTriggersDto DEFINE_NICK_COMMAND_PATTERNS =
		new CommandTriggersDto(ECOMMANDS.DEFINE_NICK, new String[] {"NICK (.*)"});
	private static final CommandTriggersDto LIST_USERS_COMMAND_PATTERNS =
		new CommandTriggersDto(ECOMMANDS.LIST_USERS, new String[] {"LIST", "RANKING", "R"});
	private static final CommandTriggersDto PROVOKE_COMMAND_PATTERNS =
		new CommandTriggersDto(ECOMMANDS.PROVOKE, new String[] {"P ([^ ]+) (.*)"});
	private static final CommandTriggersDto START_INVITATION_PROCESS_COMMAND_PATTERNS = 
		new CommandTriggersDto(ECOMMANDS.START_INVITATION_PROCESS, new String[] {"CO?N?V?I?[DT]?[AEO]?R?", "INVITE"});
	private static final CommandTriggersDto INVITE_NICK_OR_PHONE_COMMAND_PATTERNS = 
		new CommandTriggersDto(ECOMMANDS.INVITE_NICK_OR_PHONE, new String[] {"CO?N?V?I?[DT]?[AEO]?R? +(.*)", "INVITE +(.*)", "JO?G?[AO]?R? +(.*)", "PARTIDA +(.*)", "NOVO +(.*)", "DE ?NOVO +(.*)", "OUTR[OA] +(.*)", "FORCA +(.*)"});
	private static final CommandTriggersDto PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS = 
		new CommandTriggersDto(ECOMMANDS.PLAY_WITH_RANDOM_USER_OR_BOT, new String[] {"JO?G?[AO]?R?", "PARTIDA", "NOVO", "DE ?NOVO", "OUTR[OA]", "FORCA", "PLAY"});
	private static final CommandTriggersDto UNSUBSCRIBE_COMMAND_PATTERNS = 
		new CommandTriggersDto(ECOMMANDS.UNSUBSCRIBE, new String[] {"UNSUBSCRIBE"});
	private static final CommandTriggersDto FALLBACK_HELP_COMMAND_PATTERNS =
		new CommandTriggersDto(ECOMMANDS.SHOW_FULL_HELP_MESSAGE, new String[] {".*"});
	
	
	// STATE DETAILS
	////////////////

	public static final CommandTriggersDto[] NEW_USER = {
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		new CommandTriggersDto(ECOMMANDS.SHOW_WELCOME_MESSAGE, new String[] {
			"FORCA", "JOGO", "HANGMAN", "PLAY"}),
		new CommandTriggersDto(ECOMMANDS.NO_ANSWER, new String[] {
			".*"}),
	};
	
	public static final CommandTriggersDto[] EXISTING_USER = {
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
		PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		FALLBACK_HELP_COMMAND_PATTERNS,
	};
	
	public static final CommandTriggersDto[] ENTERING_OPPONENT_CONTACT_INFO = {
		new CommandTriggersDto(ECOMMANDS.REGISTER_OPPONENT_PHONE, new String[] {
			"(\\d+)"}),
		new CommandTriggersDto(ECOMMANDS.REGISTER_OPPONENT_NICK, new String[] {
			"(^\\d.+)"}),
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
		PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		FALLBACK_HELP_COMMAND_PATTERNS
	};

	public static final CommandTriggersDto[] ENTERING_MATCH_WORD = {
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
		PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		new CommandTriggersDto(ECOMMANDS.REGISTER_MATCH_WORD, new String[] {"([^ ]+)"}),
		FALLBACK_HELP_COMMAND_PATTERNS
	};

	public static final CommandTriggersDto[] ANSWERING_TO_INVITATION = {
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
		PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		new CommandTriggersDto(ECOMMANDS.ACCEPT_INVITATION, new String[] {
			"YES"}),
		new CommandTriggersDto(ECOMMANDS.REFUSE_INVITATION, new String[] {
			"NO"}),
		new CommandTriggersDto(ECOMMANDS.INVITATION_TIMEOUT, Configuration.INVITATION_TIMEOUT_MILLIS),
	};

	public static final CommandTriggersDto[] GUESSING_WORD = {
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		new CommandTriggersDto(ECOMMANDS.SUGGEST_LETTER_OR_WORD, new String[] {
			"(C)", "(J)"}),	// TODO fix the C conflict with 'START_INVITATION_PROCESS_COMMAND_PATTERNS'
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
		PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		new CommandTriggersDto(ECOMMANDS.SUGGEST_LETTER_OR_WORD, new String[] {
			"(.*)"})
	};
	
	public static final CommandTriggersDto[] LISTING_USERS = {
		new CommandTriggersDto(ECOMMANDS.LIST_MORE_USERS, new String[] {
			"MAIS", "MORE", "\\+"}),
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
		PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		FALLBACK_HELP_COMMAND_PATTERNS
	};

}
