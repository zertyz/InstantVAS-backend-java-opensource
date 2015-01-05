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

	public static final CommandPatternsDto[] NEW_USER = {
		new CommandPatternsDto(ECOMMANDS.START_INVITATION_PROCESS, new String[] {
			"C"}),
		new CommandPatternsDto(ECOMMANDS.SHOW_FULL_HELP_MESSAGE, new String[] {
			"(.*)AJUDA(.*)"}),
		new CommandPatternsDto(ECOMMANDS.SHOW_PROFILE, new String[] {
			"PROFILE (.*)"}),
		new CommandPatternsDto(ECOMMANDS.DEFINE_NICK, new String[] {
			"NICK (.*)"}),
		new CommandPatternsDto(ECOMMANDS.LIST_USERS, new String[] {
			"LIST"}),
		new CommandPatternsDto(ECOMMANDS.SHOW_WELCOME_MESSAGE, new String[] {
			"(.*)"}),
	};
	
	public static final CommandPatternsDto[] ENTERING_OPPONENT_CONTACT_INFO = {
		new CommandPatternsDto(ECOMMANDS.REGISTER_OPPONENT_PHONE, new String[] {
			"(\\d+)"}),
	};

	public static final CommandPatternsDto[] ENTERING_MATCH_WORD = {
		new CommandPatternsDto(ECOMMANDS.REGISTER_MATCH_WORD, new String[] {
			"(.*)"}),
	};

	public static final CommandPatternsDto[] ANSWERING_TO_INVITATION = {
		new CommandPatternsDto(ECOMMANDS.ACCEPT_INVITATION, new String[] {
			"YES"}),
//		new CommandPatternsDto(ECOMMANDS.REFUSE_INVITATION, new String[] {
//			"NO"}),
	};

	public static final CommandPatternsDto[] PLAYING = {
		new CommandPatternsDto(ECOMMANDS.PROVOKE, new String[] {
			"P ([^ ]+) (.*)"}),
		new CommandPatternsDto(ECOMMANDS.SUGGEST_LETTER_OR_WORD, new String[] {
			"(.*)"}),
	};

}
