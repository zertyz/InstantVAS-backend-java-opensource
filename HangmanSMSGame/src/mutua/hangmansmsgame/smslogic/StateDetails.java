package mutua.hangmansmsgame.smslogic;

import mutua.hangmansmsgame.smslogic.NavigationMap.ECOMMANDS;
import mutua.hangmansmsgame.smslogic.commands.dto.CommandPatternsDto;

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
	
	private static final CommandPatternsDto SHOW_FULL_HELP_COMMAND_PATTERNS =
		new CommandPatternsDto(ECOMMANDS.SHOW_FULL_HELP_MESSAGE, new String[] {"AJUDA", "HELP"});
	private static final CommandPatternsDto SHOW_PROFILE_COMMAND_PATTERNS =
		new CommandPatternsDto(ECOMMANDS.SHOW_PROFILE, new String[] {"PROFILE (.*)"});
	private static final CommandPatternsDto DEFINE_NICK_COMMAND_PATTERNS =
		new CommandPatternsDto(ECOMMANDS.DEFINE_NICK, new String[] {"NICK (.*)"});
	private static final CommandPatternsDto LIST_USERS_COMMAND_PATTERNS =
		new CommandPatternsDto(ECOMMANDS.LIST_USERS, new String[] {"LIST", "RANKING", "R"});
	private static final CommandPatternsDto PROVOKE_COMMAND_PATTERNS =
		new CommandPatternsDto(ECOMMANDS.PROVOKE, new String[] {"P ([^ ]+) (.*)"});
	private static final CommandPatternsDto START_INVITATION_PROCESS_COMMAND_PATTERNS = 
		new CommandPatternsDto(ECOMMANDS.START_INVITATION_PROCESS, new String[] {"CO?N?V?I?[DT]?[AEO]?R?", "INVITE"});
	private static final CommandPatternsDto INVITE_NICK_OR_PHONE_COMMAND_PATTERNS = 
		new CommandPatternsDto(ECOMMANDS.INVITE_NICK_OR_PHONE, new String[] {"CO?N?V?I?[DT]?[AEO]?R? +(.*)", "INVITE +(.*)", "JO?G?[AO]?R? +(.*)", "PARTIDA +(.*)", "NOVO +(.*)", "DE ?NOVO +(.*)", "OUTR[OA] +(.*)", "FORCA +(.*)"});
	private static final CommandPatternsDto PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS = 
		new CommandPatternsDto(ECOMMANDS.PLAY_WITH_RANDOM_USER_OR_BOT, new String[] {"JO?G?[AO]?R?", "PARTIDA", "NOVO", "DE ?NOVO", "OUTR[OA]", "FORCA", "PLAY"});
	private static final CommandPatternsDto UNSUBSCRIBE_COMMAND_PATTERNS = 
		new CommandPatternsDto(ECOMMANDS.UNSUBSCRIBE, new String[] {"UNSUBSCRIBE"});
	private static final CommandPatternsDto FALLBACK_HELP_COMMAND_PATTERNS =
		new CommandPatternsDto(ECOMMANDS.SHOW_FULL_HELP_MESSAGE, new String[] {".*"});
	
	
	// STATE DETAILS
	////////////////

	public static final CommandPatternsDto[] NEW_USER = {
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		new CommandPatternsDto(ECOMMANDS.SHOW_WELCOME_MESSAGE, new String[] {
			"FORCA", "JOGO", "HANGMAN", "PLAY"}),
		new CommandPatternsDto(ECOMMANDS.NO_ANSWER, new String[] {
			".*"}),
	};
	
	public static final CommandPatternsDto[] EXISTING_USER = {
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
	
	public static final CommandPatternsDto[] ENTERING_OPPONENT_CONTACT_INFO = {
		new CommandPatternsDto(ECOMMANDS.REGISTER_OPPONENT_PHONE, new String[] {
			"(\\d+)"}),
		new CommandPatternsDto(ECOMMANDS.REGISTER_OPPONENT_NICK, new String[] {
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

	public static final CommandPatternsDto[] ENTERING_MATCH_WORD = {
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
		PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		new CommandPatternsDto(ECOMMANDS.REGISTER_MATCH_WORD, new String[] {"([^ ]+)"}),
		FALLBACK_HELP_COMMAND_PATTERNS
	};

	public static final CommandPatternsDto[] ANSWERING_TO_INVITATION = {
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
		PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		new CommandPatternsDto(ECOMMANDS.ACCEPT_INVITATION, new String[] {
			"YES"}),
		new CommandPatternsDto(ECOMMANDS.REFUSE_INVITATION, new String[] {
			"NO"}),
	};

	public static final CommandPatternsDto[] GUESSING_WORD = {
		SHOW_FULL_HELP_COMMAND_PATTERNS,
		SHOW_PROFILE_COMMAND_PATTERNS,
		DEFINE_NICK_COMMAND_PATTERNS,
		LIST_USERS_COMMAND_PATTERNS,
		PROVOKE_COMMAND_PATTERNS,
		new CommandPatternsDto(ECOMMANDS.SUGGEST_LETTER_OR_WORD, new String[] {
			"(C)", "(J)"}),	// TODO fix the C conflict with 'START_INVITATION_PROCESS_COMMAND_PATTERNS'
		START_INVITATION_PROCESS_COMMAND_PATTERNS,
		INVITE_NICK_OR_PHONE_COMMAND_PATTERNS,
		PLAY_WITH_RANDOM_USER_OR_BOT_COMMAND_PATTERNS,
		UNSUBSCRIBE_COMMAND_PATTERNS,
		new CommandPatternsDto(ECOMMANDS.SUGGEST_LETTER_OR_WORD, new String[] {
			"(.*)"})
	};
	
	public static final CommandPatternsDto[] LISTING_USERS = {
		new CommandPatternsDto(ECOMMANDS.LIST_MORE_USERS, new String[] {
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
