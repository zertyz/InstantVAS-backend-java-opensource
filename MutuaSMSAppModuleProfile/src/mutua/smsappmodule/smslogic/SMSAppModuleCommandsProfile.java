package mutua.smsappmodule.smslogic;

import java.sql.SQLException;

import static mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile.*;
import static mutua.smsappmodule.smslogic.CommandCommons.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStates.*;
import static mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile.*;

import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.sessions.SessionModel;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;

/** <pre>
 * SMSAppModuleCommandsProfile.java
 * ================================
 * (created by luiz, Aug 3, 2015)
 *
 * Enumerates and specifies how to execute each of the commands from the "Profile" 'MutuaSMSAppModule' implementation.
 * It is a god idea, when possible, to make command names match phrase names.
 * 
 * This class implements the Mutua SMSApp Command Processor design pattern,
 * as described in {@link ICommandProcessor}
 *
 * @see ICommandProcessor
 * @version $Id$
 * @author luiz
 */

public enum SMSAppModuleCommandsProfile implements ICommandProcessor {
	
	/** Command to initiate the wizard to set/change the user nickname, so he/she can be referenced throughout the system
	 *  Receives no parameters. */
	cmdStartAskForNicknameDialog {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			ProfileDto registeredProfile = profileDB.getProfileRecord(session.getUser());
			if (registeredProfile == null) {
				return getNewStateReplyCommandAnswer(session, nstRegisteringNickname, getAskForFirstNickname());
			} else {
				return getNewStateReplyCommandAnswer(session, nstRegisteringNickname, getAskForNewNickname(registeredProfile.getNickname()));
			}
		}
	},
	
	/** Command to deal with the user request of canceling the ask for a nickname' wizard.
	 *  Receives no parameters */
	cmdAskForNicknameDialogCancelation {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			ProfileDto registeredProfile = profileDB.getProfileRecord(session.getUser());
			if (registeredProfile == null) {
				// TODO possibly this scenario demands for a new configuration "default nickname"
				return getNewStateReplyCommandAnswer(session, nstExistingUser, getAskForNicknameCancelation("0000"));
			} else {
				return getNewStateReplyCommandAnswer(session, nstExistingUser, getAskForNicknameCancelation(registeredProfile.getNickname()));
			}
		}
	},
	
	/** Command to set the desired user nickname. Since no two users may share the same nickname, in case of
	 *  nickname collision, a new on will be picked automatically. 
	 *  Receives 1 parameter: the desired nickname */
	cmdRegisterNickname {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String desiredNickname = parameters[0];
			ProfileDto registeredProfile = profileDB.setProfileRecord(new ProfileDto(session.getUser(), desiredNickname));
			String registeredNickname = registeredProfile.getNickname();
			return getNewStateReplyCommandAnswer(session, nstExistingUser, getNicknameRegistrationNotification(registeredNickname));
		}
	},

	/** Command to present some interesting and public user information, such as its nickname and some extensible information like
	 *  his/her geolocation (GeoReference module), subscription state (Subscription module) and quantity of valid lucky numbers (Draw module).
	 *  Receives 1 optional parameter: the nickname to inquire for. On its abstance, inquire for the own user's information */
	cmdShowUserProfile {
		@Override
		public CommandAnswerDto processCommand(SessionModel session, ESMSInParserCarrier carrier, String[] parameters) throws SQLException {
			String desiredNickname    = parameters.length == 1  ? parameters[0]                               : null;
			ProfileDto desiredProfile = desiredNickname != null ? profileDB.getProfileRecord(desiredNickname) : profileDB.getProfileRecord(session.getUser());
			
			String registeredNickname = desiredProfile.getNickname();
			return getSameStateReplyCommandAnswer(getUserProfilePresentation(registeredNickname));
		}
	},
	
	
	;
	
	@Override
	// this.name is the enumeration property name
	public String getCommandName() {
		return this.name();
	}
	
	
	// database access
	//////////////////
	
	private static IUserDB    userDB    = SMSAppModuleDALFactory.DEFAULT_DAL.getUserDB();
	private static IProfileDB profileDB = SMSAppModuleDALFactoryProfile.DEFAULT_DAL.getProfileDB();
		
	
	// SMSAppModuleCommandCommons candidates
	////////////////////////////////////////
	
		
		
	/***********************************************************************
	** GLOBAL COMMAND TRIGGERS -- to be used in several navigation states **
	***********************************************************************/
	
	/** global triggers that activates {@link #cmdStartAskForNicknameDialog} */
	public static String[] trgGlobalStartAskForNicknameDialog   = {"NICK"};
	/** {@link #nstRegisteringNickname} triggers that activates {@link #cmdAskForNicknameDialogCancelation} */
	public static String[] trgLocalNicknameDialogCancelation    = {"CANCEL"};
	/** {@link #nstRegisteringNickname} triggers that activates {@link #cmdRegisterNickname} */
	public static String[] trgLocalRegisterNickname             = {"([A-Za-z0-9]+)"};
	/** global triggers that activates {@link #cmdRegisterNickname} */
	public static String[] trgGlobalRegisterNickname            = {"NICK (.*)"};
	/** global triggers that activates {@link #cmdShowUserProfile} */
	public static String[] trgGlobalShowUserProfile             = {"PROFILE", "PROFILE (.*)"};

}
