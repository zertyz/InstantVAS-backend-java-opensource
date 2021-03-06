package mutua.smsappmodule.smslogic.navigationstates;

import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.CommandNamesProfile.*;
import static mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile.CommandTriggersProfile.*;

import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;

/** <pre>
 * SMSAppModuleNavigationStatesProfile.java
 * ========================================
 * (created by luiz, Aug 3, 2015)
 *
 * Declares the navigation states and the reference {@link CommandTriggersDto} required by
 * the "Profile" SMSApp Module
 * 
 * Implements the "Instant VAS SMSApp Navigation States" design pattern, as described by
 * {@link NavigationState}.
 *
 * @see NavigationState
 * @see CommandTriggersDto
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleNavigationStatesProfile {
	
	/** Class to be statically imported by the Commands Implementation to represent the navigation states */
	public static class NavigationStatesNamesProfile {
		/** @see SMSAppModuleNavigationStatesProfile#nstRegisteringNickname */
		public final static String nstRegisteringNickname = "RegisteringNickname";
		/** @see SMSAppModuleNavigationStatesProfile#nstListingProfiles */
		public final static String nstListingProfiles     = "ListingProfiles";
	}
	
	// Navigation States Definitions
	////////////////////////////////
	
	/** The list of all navigation states -- for 'SMSProcessor' to be able to deserialize state names */
	public final NavigationState[] values;
	
	/** Navigation state used to interact with the user when asking for a nickname */
	public final NavigationState nstRegisteringNickname;
	
	/** Navigation state used to interact with the user when listing existing and active User Profiles */
	public final NavigationState nstListingProfiles;
	
	/** Provides the navigation states instance with the default test values */
	public SMSAppModuleNavigationStatesProfile() { 
		this(
			new Object[][] {
				{cmdRegisterNickname,                trgGlobalRegisterNickname},
				{cmdStartAskForNicknameDialog,       trgGlobalStartAskForNicknameDialog},
				{cmdAskForNicknameDialogCancelation, trgLocalNicknameDialogCancelation},
				{cmdShowUserProfile,                 trgGlobalShowUserProfile},
				{cmdRegisterNickname,                trgLocalRegisterNickname},
				{cmdListProfiles,                    trgGlobalListProfiles}},
			new Object[][] {
				{cmdRegisterNickname,                trgGlobalRegisterNickname},
				{cmdStartAskForNicknameDialog,       trgGlobalStartAskForNicknameDialog},
				{cmdAskForNicknameDialogCancelation, trgLocalNicknameDialogCancelation},
				{cmdShowUserProfile,                 trgGlobalShowUserProfile},
				{cmdRegisterNickname,                trgLocalRegisterNickname},
				{cmdListProfiles,                    trgLocalListMoreProfiles}});
	}

	/** Provides the navigation states instance with custom triggers.
	 *  @param nstRegisteringNicknameTriggers  The list of regular expression triggers and commands to execute when the user is on the {@link #nstRegisteringNickname} navigation state. See {@link NavigationState#applyCommandTriggersData(Object[][], mutua.smsappmodule.smslogic.commands.ICommandProcessor[])}
	 *  @param nstListingProfilesTriggers      idem for when the user is on the {@link #nstListingProfiles} navigation state */
	public SMSAppModuleNavigationStatesProfile(Object[][] nstRegisteringNicknameTriggers,
	                                           Object[][] nstListingProfilesTriggers) {		
		nstRegisteringNickname = new NavigationState(NavigationStatesNamesProfile.nstRegisteringNickname, nstRegisteringNicknameTriggers);
		nstListingProfiles     = new NavigationState(NavigationStatesNamesProfile.nstListingProfiles,     nstListingProfilesTriggers);
		
		// the list of values
		values = new NavigationState[] {
			nstRegisteringNickname,
			nstListingProfiles,
		};
	}
}