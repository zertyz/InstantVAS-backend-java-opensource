package mutua.smsappmodule.smslogic;

import java.sql.SQLException;

import static mutua.smsappmodule.smslogic.CommandCommons.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.NavigationStatesNames.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.NavigationStatesNamesProfile.*;

import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * SMSAppModuleCommandsProfile.java
 * ================================
 * (created by luiz, Aug 3, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Profile" SMS Module.
 * It is a god idea, when possible, to make command names match phrase names.
 * 
 * This class implements the "Instant VAS SMSApp Command Processors" design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleCommandsProfile {
	
	/** Class to be statically imported by the Configurators to refer to commands when defining the {@link CommandTriggersDto} */
	public static class CommandNamesProfile {
		/** @see SMSAppModuleCommandsProfile#cmdStartAskForNicknameDialog */
		public final static String cmdStartAskForNicknameDialog       = "StartAskForNicknameDialog";
		/** @see SMSAppModuleCommandsProfile#cmdAskForNicknameDialogCancelation */
		public final static String cmdAskForNicknameDialogCancelation = "AskForNicknameDialogCancelation";
		/** @see SMSAppModuleCommandsProfile#cmdRegisterNickname */
		public final static String cmdRegisterNickname                = "RegisterNickname";
		/** @see SMSAppModuleCommandsProfile#cmdShowUserProfile */
		public final static String cmdShowUserProfile                 = "ShowUserProfile";
	}
	
	/** Class to be used as a reference when customizing the MO commands for this module */
	public static class CommandTriggersProfile {
		/** Global triggers (to be used on several navigation states) to execute the 'ask for a nickname' dialog.
		 *  Receives no parameters --
		 *  activates {@link SMSAppModuleCommandsProfile#cmdStartAskForNicknameDialog} */
		public final static String[] trgGlobalStartAskForNicknameDialog   = {"NICK"};
		/** Local triggers (available only to the 'ask for a nickname' navigation state) to cancel the 'ask for a nickname' dialog.
		 *  Receives no parameters --
		 *  {@link SMSAppModuleNavigationStatesProfile#nstRegisteringNickname} triggers that activates {@link SMSAppModuleCommandsProfile#cmdAskForNicknameDialogCancelation} */
		public final static String[] trgLocalNicknameDialogCancelation    = {"CANCEL"};
		/** Local triggers (available only to the 'ask for a nickname' navigation state) to execute the 'register user nickname' command.
		 *  Receives 1 parameter: the new nickname --
		 *  {@link SMSAppModuleNavigationStatesProfile#nstRegisteringNickname} triggers that activates {@link SMSAppModuleCommandsProfile#cmdRegisterNickname} */
		public final static String[] trgLocalRegisterNickname             = {"([A-Za-z0-9]+)"};
		/** Global triggers (to be used on several navigation states) to execute the 'register user nickname' command.
		 *  Receives 1 parameter: the nickname --
		 *  activates {@link #cmdRegisterNickname} */
		public final static String[] trgGlobalRegisterNickname            = {"NICK (.*)"};
		/** Global triggers (to be used on several navigation states) to execute the 'show user profile' command.
		 *  Receives 1 optional parameter: the nickname. If called without a parameter, the user gets his/her own information instead --
		 *  activates {@link SMSAppModuleCommandsProfile#cmdShowUserProfile} */
		public final static String[] trgGlobalShowUserProfile             = {"PROFILE", "PROFILE (.*)"};
	}
	
	// Instance Fields
	//////////////////

	private final SMSAppModulePhrasingsProfile profilePhrases;
	private final IProfileDB profileDB;


	/** Constructs an instance of this module's command processors.<pre>
	 *  @param profilePhrases   an instance of the phrasings to be used
	 *  @param profileModuleDAL one of the members of {@link SMSAppModuleDALFactoryProfile}, from which the {@link SMSAppModuleDALFactory} user data will also be taken */
	public SMSAppModuleCommandsProfile(SMSAppModulePhrasingsProfile  profilePhrases,
	                                   SMSAppModuleDALFactoryProfile profileModuleDAL) {
		this.profilePhrases = profilePhrases;
		this.profileDB      = profileModuleDAL.getProfileDB();
	}

	// Command Definitions
	//////////////////////	
		
	/** Command to initiate the wizard to set/change the user nickname, so he/she can be referenced throughout the system
	 *  Receives no parameters. */
	public final ICommandProcessor cmdStartAskForNicknameDialog = new ICommandProcessor(CommandNamesProfile.cmdStartAskForNicknameDialog) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			ProfileDto registeredProfile = profileDB.getProfileRecord(session.getUser());
			if (registeredProfile == null) {
				return getNewStateReplyCommandAnswer(session, nstRegisteringNickname, profilePhrases.getAskForFirstNickname());
			} else {
				return getNewStateReplyCommandAnswer(session, nstRegisteringNickname, profilePhrases.getAskForNewNickname(registeredProfile.getNickname()));
			}
		}
	};
	
	/** Command to deal with the user request of canceling the ask for a nickname' wizard.
	 *  Receives no parameters */
	public final ICommandProcessor cmdAskForNicknameDialogCancelation = new ICommandProcessor(CommandNamesProfile.cmdAskForNicknameDialogCancelation) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			ProfileDto registeredProfile = profileDB.getProfileRecord(session.getUser());
			if (registeredProfile == null) {
				// TODO possibly this scenario demands for a new configuration "default nickname"
				return getNewStateReplyCommandAnswer(session, nstExistingUser, profilePhrases.getAskForNicknameCancelation("0000"));
			} else {
				return getNewStateReplyCommandAnswer(session, nstExistingUser, profilePhrases.getAskForNicknameCancelation(registeredProfile.getNickname()));
			}
		}
	};
	
	/** Command to set the desired user nickname. Since no two users may share the same nickname, in case of
	 *  nickname collision, a new on will be picked automatically. 
	 *  Receives 1 parameter: the desired nickname */
	public final ICommandProcessor cmdRegisterNickname = new ICommandProcessor(CommandNamesProfile.cmdRegisterNickname) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String desiredNickname = parameters[0];
			ProfileDto registeredProfile = profileDB.setProfileRecord(new ProfileDto(session.getUser(), desiredNickname));
			String registeredNickname = registeredProfile.getNickname();
			// if we are setting the nick from 'nstRegisteringNickname', get out of it
			if (nstRegisteringNickname.equals(session.getNavigationStateName())) {
				return getNewStateReplyCommandAnswer(session, nstExistingUser, profilePhrases.getNicknameRegistrationNotification(registeredNickname));
			} else {
				return getSameStateReplyCommandAnswer(profilePhrases.getNicknameRegistrationNotification(registeredNickname));
			}
		}
	};

	/** Command to present some interesting and public user information, such as its nickname and some extensible information like
	 *  his/her geolocation (GeoReference module), subscription state (Subscription module) and quantity of valid lucky numbers (Draw module).
	 *  Receives 1 optional parameter: the nickname to inquire for. On its abstance, inquire for the own user's information */
	public final ICommandProcessor cmdShowUserProfile = new ICommandProcessor(CommandNamesProfile.cmdShowUserProfile) {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String desiredNickname    = parameters.length == 1  ? parameters[0]                               : null;
			ProfileDto desiredProfile = desiredNickname != null ? profileDB.getProfileRecord(desiredNickname) : profileDB.getProfileRecord(session.getUser());
			
			if (desiredProfile == null) {
				return getSameStateReplyCommandAnswer(profilePhrases.getNicknameNotFound(desiredNickname));
			}
			
			String registeredNickname = desiredProfile.getNickname();
			return getSameStateReplyCommandAnswer(profilePhrases.getUserProfilePresentation(registeredNickname));
		}
	};
	

	// SMSAppModuleCommandCommons candidates
	////////////////////////////////////////

	
	// Command List
	///////////////
	
	/** The list of all commands -- to allow deserialization by {@link CommandTriggersDto} */
	public final ICommandProcessor[] values = {
		cmdStartAskForNicknameDialog,
		cmdAskForNicknameDialogCancelation,
		cmdRegisterNickname,
		cmdShowUserProfile,
	};

}
